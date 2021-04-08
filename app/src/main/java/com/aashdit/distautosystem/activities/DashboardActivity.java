package com.aashdit.distautosystem.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.aashdit.distautosystem.BuildConfig;
import com.aashdit.distautosystem.adapters.ClosurePhotoNotUploadAdapter;
import com.aashdit.distautosystem.adapters.InitiationPhotoNotUploadAdapter;
import com.aashdit.distautosystem.adapters.InitiationPhotoUploadAdapter;
import com.aashdit.distautosystem.adapters.PhotoNotUploadAdapter;
import com.aashdit.distautosystem.adapters.PhotoUploadAdapter;
import com.aashdit.distautosystem.app.App;
import com.aashdit.distautosystem.databinding.ActivityDashboardBinding;
import com.aashdit.distautosystem.model.ClosureData;
import com.aashdit.distautosystem.model.InitiationData;
import com.aashdit.distautosystem.model.NotUploaded;
import com.aashdit.distautosystem.model.Uploaded;
import com.aashdit.distautosystem.util.Constants;
import com.aashdit.distautosystem.util.SharedPrefManager;
import com.aashdit.distautosystem.util.Utility;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.rm.rmswitch.RMSwitch;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class DashboardActivity extends AppCompatActivity implements PhotoNotUploadAdapter.NotUploadedListener ,
        LocationListener, InitiationPhotoNotUploadAdapter.NotUploadedListener, ClosurePhotoNotUploadAdapter.ClosureUploadListener {

    private static final String TAG = "DashboardActivity";

    private ActivityDashboardBinding binding;
    private String startdate = "", enddate = "";

    private ArrayList<NotUploaded> notUploadedData = new ArrayList<>();
    private ArrayList<Uploaded> uploadedData = new ArrayList<>();
    private ArrayList<InitiationData> initiationData = new ArrayList<>();
    private ArrayList<InitiationData> initiationUploadedData = new ArrayList<>();
    private ArrayList<ClosureData> closureData = new ArrayList<>();
    private PhotoNotUploadAdapter notUploadedAdapter;
    private PhotoUploadAdapter uploadedAdapter;
    private InitiationPhotoNotUploadAdapter initiationPhotoNotUploadAdapter;
    private ClosurePhotoNotUploadAdapter closurePhotoNotUploadAdapter;
    private InitiationPhotoUploadAdapter initiationPhotoUploadAdapter;

    private boolean isUploaded = false;
    private SharedPrefManager sp;
    private Long userId;

    private String dataType = "INITIATION";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sp = SharedPrefManager.getInstance(this);
        userId = sp.getLongData(Constants.USER_ID);

        binding.tvNotUpload.setTextColor(Color.parseColor("#364F6B"));
        binding.tvUpload.setTextColor(Color.parseColor("#8A364F6B"));

        binding.ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent profileIntent = new Intent(DashboardActivity.this, ProfileActivity.class);
                startActivity(profileIntent);
            }
        });
        setDafaultDateFormat();
        binding.animationView.setVisibility(View.GONE);
        binding.progressbar.setVisibility(View.GONE);

//        getInitialUploadedTenders();

        binding.tvInitiation.setTextColor(Color.parseColor("#DD084B"));
        binding.tvInitiation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dataType = "INITIATION";
                binding.tvInitiation.setTextColor(Color.parseColor("#DD084B"));
                binding.tvFundRelease.setTextColor(Color.parseColor("#000000"));
                binding.tvClosure.setTextColor(Color.parseColor("#000000"));
                getResponse();
            }
        });

        binding.tvFundRelease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dataType = "FUND";
                binding.tvInitiation.setTextColor(Color.parseColor("#000000"));
                binding.tvFundRelease.setTextColor(Color.parseColor("#DD084B"));
                binding.tvClosure.setTextColor(Color.parseColor("#000000"));
                getResponse();
            }
        });
        binding.tvClosure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dataType = "CLOSURE";
                binding.tvInitiation.setTextColor(Color.parseColor("#000000"));
                binding.tvFundRelease.setTextColor(Color.parseColor("#000000"));
                binding.tvClosure.setTextColor(Color.parseColor("#DD084B"));
                getResponse();
            }
        });

        initiationPhotoUploadAdapter =  new InitiationPhotoUploadAdapter(this,initiationUploadedData);

        initiationPhotoNotUploadAdapter = new InitiationPhotoNotUploadAdapter(this,initiationData);
        initiationPhotoNotUploadAdapter.setNotUploadedListener(this);
        closurePhotoNotUploadAdapter = new ClosurePhotoNotUploadAdapter(this,closureData);
        closurePhotoNotUploadAdapter.setClosureUploadListener(this);

        notUploadedAdapter = new PhotoNotUploadAdapter(this, notUploadedData);
        notUploadedAdapter.setNotUploadedListener(this);
        uploadedAdapter = new PhotoUploadAdapter(this, uploadedData);
        binding.rvProjectList.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        binding.rvProjectList.setAdapter(notUploadedAdapter);

        binding.ivFromCalender.setOnClickListener(view1 -> setFromDateField());
        binding.ivToCalender.setOnClickListener(view1 -> setToDateField());

        getResponse();
        binding.uploadSwitch.addSwitchObserver(new RMSwitch.RMSwitchObserver() {
            @Override
            public void onCheckStateChange(RMSwitch switchView, boolean isChecked) {
                isUploaded = isChecked;
                if (isChecked) {
                    binding.tvUpload.setTextColor(Color.parseColor("#364F6B"));
                    binding.tvNotUpload.setTextColor(Color.parseColor("#8A364F6B"));
//                    getGeoTaggedFundReleaseList();
//                    binding.rvProjectList.setAdapter(uploadedAdapter);
                    getResponse();
                }else {
                    binding.tvNotUpload.setTextColor(Color.parseColor("#364F6B"));
                    binding.tvUpload.setTextColor(Color.parseColor("#8A364F6B"));
//                    getFundReleaseListForGeoTagging();
//                    binding.rvProjectList.setAdapter(notUploadedAdapter);
                    getResponse();
                }
            }
        });

        binding.swiperefreshlayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                binding.swiperefreshlayout.setRefreshing(false);
                if (isUploaded) {
                    getResponse();
//                    getGeoTaggedFundReleaseList();
                } else {
                    getResponse();
//                    getFundReleaseListForGeoTagging();
                }
            }
        });

    }

    private void getResponse() {
        if (dataType.equals("INITIATION") && isUploaded){
            getInitialUploadedTenders();
        }else if (dataType.equals("INITIATION") && !isUploaded){
            getTendersForInspection();
            binding.rvProjectList.setAdapter(initiationPhotoNotUploadAdapter);
        }

        if (dataType.equals("FUND") && isUploaded){

        }else if (dataType.equals("FUND") && !isUploaded){
            getFundReleaseListForGeoTagging();
            binding.rvProjectList.setAdapter(notUploadedAdapter);
        }

        if (dataType.equals("CLOSURE") && isUploaded){

        }else if (dataType.equals("CLOSURE") && !isUploaded){
            getTendersForClosure();
            binding.rvProjectList.setAdapter(closurePhotoNotUploadAdapter);
        }
    }

    private void getInitialUploadedTenders() {
        binding.animationView.setVisibility(View.VISIBLE);
        AndroidNetworking.post(BuildConfig.BASE_URL.concat("api/getInitialUploadedTenders?userId=" + userId +
                "&startDate=" + startdate + "&endDate=" + enddate))
                .setTag("Login")
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        binding.animationView.setVisibility(View.GONE);
                        if (Utility.isStringValid(response)) {

                            JSONObject resObj = null;
                            try {
                                resObj = new JSONObject(response);
                                String status = resObj.optString("status");
                                if (status.equals("SUCCESS")) {
                                    JSONArray resArray = resObj.optJSONArray("result");
                                    if (resArray != null && resArray.length() > 0) {
                                        initiationUploadedData.clear();
                                        for (int i = 0; i < resArray.length(); i++) {
                                            InitiationData uploaded = InitiationData.parseInitiationData(resArray.optJSONObject(i));
                                            initiationUploadedData.add(uploaded);
                                        }
//                                        uploadedAdapter.notifyDataSetChanged();
                                        binding.rvProjectList.setAdapter(initiationPhotoUploadAdapter);
                                        initiationPhotoUploadAdapter.notifyDataSetChanged();
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        binding.animationView.setVisibility(View.GONE);
                        Log.e(TAG, "onError: " + anError.getErrorDetail());
                    }
                });
    }

    private void getGeoTaggedFundReleaseList() {
        binding.animationView.setVisibility(View.VISIBLE);
        AndroidNetworking.get(BuildConfig.BASE_URL.concat("api/getGeoTaggedFundReleaseList?userId=" + userId +
                "&startDate=" + startdate + "&endDate=" + enddate))
                .setTag("Login")
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        binding.animationView.setVisibility(View.GONE);
                        if (Utility.isStringValid(response)) {

                            JSONObject resObj = null;
                            try {
                                resObj = new JSONObject(response);
                                String status = resObj.optString("flag");
                                if (status.equals("Success")) {
                                    JSONArray resArray = resObj.optJSONArray("fundReleaseList");
                                    if (resArray != null && resArray.length() > 0) {
                                        uploadedData.clear();
                                        for (int i = 0; i < resArray.length(); i++) {
                                            Uploaded uploaded = Uploaded.parseUploaded(resArray.optJSONObject(i));
                                            uploadedData.add(uploaded);
                                        }
                                        uploadedAdapter.notifyDataSetChanged();
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        binding.animationView.setVisibility(View.GONE);
                        Log.e(TAG, "onError: " + anError.getErrorDetail());
                    }
                });
    }

    private void getTendersForInspection(){
        binding.animationView.setVisibility(View.VISIBLE);
        AndroidNetworking.post(BuildConfig.BASE_URL.concat("api/getTendersForInspection?userId=" + userId +
                "&startDate=" + startdate + "&endDate=" + enddate))
                .setTag("Login")
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        binding.animationView.setVisibility(View.GONE);
                        if (Utility.isStringValid(response)) {
                            JSONObject resObj = null;
                            try {
                                resObj = new JSONObject(response);
                                String status = resObj.optString("status");
                                if (status.equals("SUCCESS")) {
                                    JSONArray resArray = resObj.optJSONArray("result");
                                    if (resArray != null && resArray.length() > 0) {
                                        initiationData.clear();
                                        for (int i = 0; i < resArray.length(); i++) {
                                            InitiationData notUploaded = InitiationData.parseInitiationData(resArray.optJSONObject(i));
                                            initiationData.add(notUploaded);
                                        }
                                        initiationPhotoNotUploadAdapter.notifyDataSetChanged();
                                    }
                                }else{
                                    Toast.makeText(DashboardActivity.this, resObj.optString("message"), Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        binding.animationView.setVisibility(View.GONE);
                        Log.e(TAG, "onError: " + anError.getErrorDetail());
                    }
                });
    }

    private void getTendersForClosure(){
        binding.animationView.setVisibility(View.VISIBLE);
        AndroidNetworking.post(BuildConfig.BASE_URL.concat("api/getTendersForClosure?userId=" + userId +
                "&startDate=" + startdate + "&endDate=" + enddate))
                .setTag("Login")
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        binding.animationView.setVisibility(View.GONE);
                        if (Utility.isStringValid(response)) {
                            JSONObject resObj = null;
                            try {
                                resObj = new JSONObject(response);
                                String status = resObj.optString("status");
                                if (status.equals("SUCCESS")) {
                                    JSONArray resArray = resObj.optJSONArray("result");
                                    if (resArray != null && resArray.length() > 0) {
                                        closureData.clear();
                                        for (int i = 0; i < resArray.length(); i++) {
                                            ClosureData notUploaded = ClosureData.parseInitiationData(resArray.optJSONObject(i));
                                            closureData.add(notUploaded);
                                        }
                                        initiationPhotoNotUploadAdapter.notifyDataSetChanged();
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        binding.animationView.setVisibility(View.GONE);
                        Log.e(TAG, "onError: " + anError.getErrorDetail());
                    }
                });
    }


    private void getFundReleaseListForGeoTagging() {
        binding.animationView.setVisibility(View.VISIBLE);
        AndroidNetworking.get(BuildConfig.BASE_URL.concat("api/getFundReleaseListForGeoTagging?userId=" + userId +
                "&startDate=" + startdate + "&endDate=" + enddate))
                .setTag("Login")
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        binding.animationView.setVisibility(View.GONE);
                        if (Utility.isStringValid(response)) {
                            JSONObject resObj = null;
                            try {
                                resObj = new JSONObject(response);
                                String status = resObj.optString("flag");
                                if (status.equals("Success")) {
                                    JSONArray resArray = resObj.optJSONArray("fundReleaseList");
                                    if (resArray != null && resArray.length() > 0) {
                                        notUploadedData.clear();
                                        for (int i = 0; i < resArray.length(); i++) {
                                            NotUploaded notUploaded = NotUploaded.parseNotUploaded(resArray.optJSONObject(i));
                                            notUploadedData.add(notUploaded);
                                        }
                                        notUploadedAdapter.notifyDataSetChanged();
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        binding.animationView.setVisibility(View.GONE);
//                        binding.progressbar.setVisibility(View.GONE);
                        Log.e(TAG, "onError: " + anError.getErrorDetail());
                    }
                });
    }

    private int mYear, mMonth, mDay;

    private void setToDateField() {
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(Objects.requireNonNull(this),
                (view, year, monthOfYear, dayOfMonth) -> {

                    binding.tvToDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
//                    checkForApiCall();
                    enddate = binding.tvToDate.getText().toString();
                    if (isUploaded) {
                        getGeoTaggedFundReleaseList();
                    } else {
                        getFundReleaseListForGeoTagging();
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void setFromDateField() {
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(Objects.requireNonNull(this),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        binding.tvFromDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                        startdate = binding.tvFromDate.getText().toString().trim();
                        if (isUploaded) {
                            getGeoTaggedFundReleaseList();
                        } else {
                            getFundReleaseListForGeoTagging();
                        }
//                    checkForApiCall();
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }
    private void setDafaultDateFormat() {
        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String formattedDate = df.format(c);
        binding.tvFromDate.setText(formattedDate);

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -2);  // two month back
        SimpleDateFormat df1 = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String formattedDate1 = df1.format(cal.getTime());

        binding.tvFromDate.setText(formattedDate1);
        startdate = binding.tvFromDate.getText().toString().trim();


        Calendar currentcal = Calendar.getInstance();
        SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String formattedDate2 = df2.format(currentcal.getTime());
        binding.tvToDate.setText(formattedDate2);
        enddate = binding.tvToDate.getText().toString();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            boolean isUploaded = data.getBooleanExtra("data",false);
            if (isUploaded){
                getFundReleaseListForGeoTagging();
            }else{
                Toast.makeText(this, isUploaded+"", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void notUploaded(NotUploaded item) {
        Intent uploadIntent = new Intent(DashboardActivity.this, ImageUploadActivity.class);
        uploadIntent.putExtra("ID",item.fundReleaseId);
        uploadIntent.putExtra("TYPE","FUND");
        startActivity(uploadIntent);
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        Log.d("Tag", "LatLng===>" + location.getLatitude() + " " + location.getLongitude());

        if (location.getLatitude() != 0.0 && location.getLongitude() != 0.0) {
            binding.animationView.setVisibility(View.GONE);
//            binding.progress.setVisibility(View.GONE);
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

        } else {
            Toast.makeText(this, "Fetching Location, Please wait.", Toast.LENGTH_LONG).show();
            binding.animationView.setVisibility(View.VISIBLE);
//            binding.progress.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }

    @Override
    public void notUploaded(InitiationData resultResponse) {
        Intent uploadIntent = new Intent(DashboardActivity.this, ImageUploadActivity.class);
        uploadIntent.putExtra("ID",resultResponse.tenderId);
        uploadIntent.putExtra("TYPE","INITITATION");
        startActivity(uploadIntent);
    }

    @Override
    public void onClosureUpload(int position) {
        Intent uploadIntent = new Intent(DashboardActivity.this, ImageUploadActivity.class);
        uploadIntent.putExtra("ID",closureData.get(position).tenderId);
        uploadIntent.putExtra("TYPE","CLOSURE");
        startActivity(uploadIntent);
    }
}