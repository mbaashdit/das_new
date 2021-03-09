package com.aashdit.distautosystem.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.aashdit.distautosystem.BuildConfig;
import com.aashdit.distautosystem.R;
import com.aashdit.distautosystem.databinding.ActivityChangePasswordBinding;
import com.aashdit.distautosystem.util.Constants;
import com.aashdit.distautosystem.util.SharedPrefManager;
import com.aashdit.distautosystem.util.Utility;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;

import org.json.JSONException;
import org.json.JSONObject;

public class ChangePasswordActivity extends AppCompatActivity {

    private static final String TAG = "ChangePasswordActivity";

    private ActivityChangePasswordBinding binding;
    ProgressDialog progressDialog;
    SharedPrefManager sp;
    private String userid,oldPassword,newPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding  = ActivityChangePasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        sp = SharedPrefManager.getInstance(this);
        progressDialog = new ProgressDialog(ChangePasswordActivity.this);

        userid = String.valueOf(sp.getLongData(Constants.USER_ID));//RegPrefManager.getInstance(ChangePasswordActivity.this).getLoginUserid();


        binding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newPassword = binding.etNewPassword.getText().toString().trim();
                oldPassword = binding.etOldPassword.getText().toString().trim();

                if (oldPassword.equals("")) {
                    toastMessage("Please Enter Old Password");
                } else if (newPassword.equals("")) {
                    toastMessage("Please Enter New Password");
                } else {
                    if (isNetworkAvailable()) {
                        changePassword();
                    } else {
                        toastMessage("Please Check Internet Connection");
                    }
                }

            }
        });

    }

    private void changePassword() {
        progressDialog.show();
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);

        AndroidNetworking.post(BuildConfig.BASE_URL.concat("api/changePassword?oldPassword="+oldPassword+"&newPassword="+newPassword))
                .setTag("ChangePassword")
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        if (Utility.isStringValid(response)) {
                            try {
                                JSONObject loginObj = new JSONObject(response);
                                String status = loginObj.optString("status");
                                if (status.equals("SUCCESS")) {

                                    toastMessage(loginObj.optString("message"));

                                }else{
                                    toastMessage(loginObj.optString("message"));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        progressDialog.dismiss();
                        Log.e(TAG, "onError: "+anError.getErrorDetail() );
                    }
                });

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



    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }


}