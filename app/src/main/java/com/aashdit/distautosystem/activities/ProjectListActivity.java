package com.aashdit.distautosystem.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.aashdit.distautosystem.BuildConfig;
import com.aashdit.distautosystem.R;
import com.aashdit.distautosystem.adapters.AutoSuggestAdapter;
import com.aashdit.distautosystem.adapters.ProjectListAdapter;
import com.aashdit.distautosystem.app.App;
import com.aashdit.distautosystem.databinding.ActivityProjectListBinding;
import com.aashdit.distautosystem.model.Project;
import com.aashdit.distautosystem.util.Constants;
import com.aashdit.distautosystem.util.SharedPrefManager;
import com.aashdit.distautosystem.util.Utility;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProjectListActivity extends AppCompatActivity implements ProjectListAdapter.OnProjectClickListener,
        LocationListener {

    private static final String TAG = "ProjectListActivity";
    private ActivityProjectListBinding binding;
    private SharedPrefManager prefs;

    private ArrayList<Project> projectList = new ArrayList<>();

    private ProjectListAdapter adapter;

    private AutoSuggestAdapter autoSuggestAdapter;
    private static final int TRIGGER_AUTO_COMPLETE = 100;
    private static final long AUTO_COMPLETE_DELAY = 300;
    private Handler handler;
    private String projectName= "";

    Long gpId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProjectListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        prefs = SharedPrefManager.getInstance(this);
        gpId = prefs.getLongData(Constants.GP_ID);

        binding.rvProjectList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ProjectListAdapter(this,projectList);
        adapter.setOnProjectClickListener(this);
        binding.rvProjectList.setAdapter(adapter);

        binding.progress.setVisibility(View.VISIBLE);

        binding.swiperefreshlayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                binding.swiperefreshlayout.setRefreshing(false);
                projectName = "";
                getProjects();
            }
        });

        binding.ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profileIntent = new Intent (ProjectListActivity.this,ProfileActivity.class);
                startActivity(profileIntent);
            }
        });

        autoSuggestAdapter = new AutoSuggestAdapter(this,
                android.R.layout.simple_dropdown_item_1line);
        binding.etSearch.setThreshold(3);
        binding.etSearch.setAdapter(autoSuggestAdapter);
        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.i(TAG, "beforeTextChanged::::: " + charSequence.toString());
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.i(TAG, "onTextChanged::::: " + charSequence.toString());
//
                handler.removeMessages(TRIGGER_AUTO_COMPLETE);
                handler.sendEmptyMessageDelayed(TRIGGER_AUTO_COMPLETE,
                        AUTO_COMPLETE_DELAY);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (binding.etSearch.getText().toString().contains(" ")) {
                    Log.i(TAG, "afterTextChanged: " + "Space found");
                }
                Log.i(TAG, "afterTextChanged:::: " + binding.etSearch.getText().toString());
            }
        });

        binding.etSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(CreateShgProfileActivity.this,
//                        autoSuggestAdapter.getItem(position).toString(),
//                        Toast.LENGTH_LONG).show();
                projectName = autoSuggestAdapter.getItem(position);
                getProjects();
            }
        });

        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == TRIGGER_AUTO_COMPLETE) {
//                    if (!TextUtils.isEmpty(binding.etSearch.getText())) {
                    projectName = binding.etSearch.getText().toString();
                    getProjects();
//                    }
                }
                return false;
            }
        });


        getLocation();
        builder = new AlertDialog.Builder(this);
        getProjects();
    }

    private LocationManager locationManager;

    private void getLocation() {
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            assert locationManager != null;
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, this);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }
    private BottomSheetDialog dialog;

    AlertDialog.Builder builder;
    private void showLogoutDialog() {
        builder.setMessage("Do you want to close this application ?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
//                        RegPrefManager.getInstance(DashboardActivity.this).clearData();
//                        Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
//                        startActivity(intent);
//                        finish();
                        prefs.clear();
                        Intent intent = new Intent(ProjectListActivity.this, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //  Action for 'NO' Button
                        dialog.cancel();
                    }
                });
        //Creating dialog box
        AlertDialog alert = builder.create();
        //Setting the title manually
        alert.setTitle("Exit ?");
        alert.show();
    }
    private void getProjects() {
        AndroidNetworking.get(BuildConfig.BASE_URL.concat("api/awc/anganwadiConstruction/getProjectListByGpId?gpId="
                +gpId+"&projectName="+projectName))
                .setTag("stopWorkPlan")
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        if(Utility.isStringValid(response)) {
                            binding.progress.setVisibility(View.GONE);
                            try {
                                JSONObject resObj = new JSONObject(response);
                                if(resObj.optString("flag").equals("Success")){
                                    JSONArray resArray = resObj.optJSONArray("awcProjectList");
                                    if(resArray != null && resArray.length() > 0){
                                        projectList.clear();
                                        for (int i = 0; i < resArray.length(); i++) {
                                            Project project = Project.parseProject(resArray.optJSONObject(i));
                                            projectList.add(project);
                                        }
                                        adapter.notifyDataSetChanged();
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        binding.progress.setVisibility(View.GONE);
                        Log.e(TAG, "onError: "+anError.getErrorDetail() );
                    }
                });
    }

    @Override
    public void onProjectClick(int position) {
        Intent detailsIntent = new Intent(this, ProjectDetailsActivity.class);
        detailsIntent.putExtra("PROJ_ID",projectList.get(position).projectId);
        startActivity(detailsIntent);
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("Tag", "LatLng===>" + location.getLatitude() + " " + location.getLongitude());

        if (location.getLatitude() != 0.0 && location.getLongitude() != 0.0) {
            binding.progress.setVisibility(View.GONE);
            App.latitude = location.getLatitude();
            App.longitude = location.getLongitude();

            Geocoder gc = new Geocoder(this, Locale.getDefault());
            try {
                List<Address> addresses = gc.getFromLocation(App.latitude, App.longitude, 1);
                StringBuilder sb = new StringBuilder();
                if (addresses.size() > 0) {
                    Address address = addresses.get(0);
                    for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                        sb.append(address.getAddressLine(i)).append("\n");
                    }
                    if (address.getAddressLine(0) != null)
                        App.capturedAddress = address.getAddressLine(0);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            // do your work now
//                                Toast.makeText(getApplicationContext(), "All permissions are granted!", Toast.LENGTH_SHORT).show();
//                            handler.postDelayed(runnable, 2000);

                            getLocation();
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // permission is denied permenantly, navigate user to app settings
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .onSameThread()
                .check();
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ProjectListActivity.this);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", (dialog, which) -> {
            dialog.cancel();
            openSettings();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();

    }

    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        int SETTINGS_REQ_CODE = 101;
        startActivityForResult(intent, SETTINGS_REQ_CODE);
    }
}