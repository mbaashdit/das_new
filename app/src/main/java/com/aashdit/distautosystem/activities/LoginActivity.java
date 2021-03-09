package com.aashdit.distautosystem.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.aashdit.distautosystem.BuildConfig;
import com.aashdit.distautosystem.R;
import com.aashdit.distautosystem.databinding.ActivityLoginBinding;
import com.aashdit.distautosystem.util.Constants;
import com.aashdit.distautosystem.util.SharedPrefManager;
import com.aashdit.distautosystem.util.Utility;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private ActivityLoginBinding binding;

    private SharedPrefManager sp;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sp = SharedPrefManager.getInstance(this);
        progressDialog = new ProgressDialog(LoginActivity.this);

        binding.loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.etUsername.getText().toString().trim().equals("")) {
                    Toast.makeText(LoginActivity.this, "Enter user name", Toast.LENGTH_SHORT).show();
                } else if (binding.etPassword.getText().toString().trim().equals("")) {
                    Toast.makeText(LoginActivity.this, "Enter password", Toast.LENGTH_SHORT).show();
                } else {
//                    if (flag == false) {
//                        showalert("Please Allow Location");
//                        //   checkPermission();
//                    } else {
                        if (isNetworkAvailable()) {
                            doLogin(binding.etUsername.getText().toString().trim(),binding.etPassword.getText().toString().trim());
                        } else {
                            toastMessage("Please Check Internet Connection");
                        }

//                    }
                }
            }
        });

        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            // do your work now
//                                Toast.makeText(getApplicationContext(), "All permissions are granted!", Toast.LENGTH_SHORT).show();
//                            handler.postDelayed(runnable, 2000);

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


    private void doLogin(String userName, String password) {
        AndroidNetworking.post(BuildConfig.BASE_URL.concat("api/userLogin?userName="+userName+"&password="+password))
                .setTag("Login")
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        if (Utility.isStringValid(response)) {
                            try {
                                JSONObject loginObj = new JSONObject(response);
                                String status = loginObj.optString("status");
                                if (status.equals("SUCCESS")) {
                                    JSONObject userResult = loginObj.optJSONObject("userResult");
                                    Long userId = userResult.optLong("userId");
                                    String userName = userResult.optString("userName");
                                    String name = userResult.optString("name");
                                    String emailId = userResult.optString("emailId");
                                    String mobileNumber = userResult.optString("mobileNumber");
                                    String image = userResult.optString("image");
                                    Long panchayatId = userResult.optLong("panchayatId");

                                    JSONObject roleResult = loginObj.optJSONObject("roleResult");
                                    String roleName = roleResult.optString("roleName");

                                    sp.setLongData(Constants.USER_ID,userId);
                                    sp.setStringData(Constants.USER_NAME,userName);
                                    sp.setStringData(Constants.USER_PASSWORD,password);
                                    sp.setStringData(Constants.NAME,name);
                                    sp.setStringData(Constants.EMAIL,emailId);
                                    sp.setStringData(Constants.MOBILE,mobileNumber);
                                    sp.setStringData(Constants.IMAGE,image);
                                    sp.setStringData(Constants.ROLE_NAME,roleName);
                                    sp.setLongData(Constants.GP_ID,panchayatId);
                                    sp.setBoolData(Constants.APP_LOGIN,true);


                                    if (userName.startsWith("io_")) {
                                        Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                                        startActivity(intent);
                                        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

                                        finish();
                                    } else {
                                        Intent intent = new Intent(LoginActivity.this, ProjectListActivity.class);
                                        startActivity(intent);
                                        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

                                        finish();
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e(TAG, "onError: "+anError.getErrorDetail() );
                    }
                });
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

    }

    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void toastMessage(String data) {
        Toast.makeText(getApplicationContext(), data, Toast.LENGTH_LONG).show();
    }
}