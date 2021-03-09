package com.aashdit.distautosystem.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.aashdit.distautosystem.BuildConfig;
import com.aashdit.distautosystem.R;
import com.aashdit.distautosystem.util.Constants;
import com.aashdit.distautosystem.util.SharedPrefManager;
import com.aashdit.distautosystem.util.Utility;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;

import org.json.JSONException;
import org.json.JSONObject;

public class SplashActivity extends AppCompatActivity {

    private static final String TAG = "SplashActivity";

    private int SPLASH_TIME = 1000;

    private SharedPrefManager sp;
    private boolean isLogin ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sp = SharedPrefManager.getInstance(this);

        isLogin = sp.getBoolData(Constants.APP_LOGIN);

        Thread timer = new Thread() {
            public void run() {
                try {
                    sleep(SPLASH_TIME);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {

                    if (isLogin){
                        String password = sp.getStringData(Constants.USER_PASSWORD);
                        String userName = sp.getStringData(Constants.USER_NAME);
                        doLogin(userName,password);
                    }
                    else {
                        startActivity(new Intent(SplashActivity.this,LoginActivity.class));
                        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                        finish();
                    }

//                 String  userid = sp.getStringData(Constants.USER_ID);
//                    Log.d("DAS",userid+"");
//                    if(userid!=null){
//                        String password = sp.getStringData(Constants.USER_PASSWORD);
//                        String userName = sp.getStringData(Constants.USER_NAME);
//                        getLoginResponse(userName,password);
//                    }

                }
            }
        };
        timer.start();
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

                                    sp.setLongData(Constants.USER_ID, userId);
                                    sp.setStringData(Constants.USER_NAME, userName);
                                    sp.setStringData(Constants.USER_PASSWORD, password);
                                    sp.setStringData(Constants.NAME, name);
                                    sp.setStringData(Constants.EMAIL, emailId);
                                    sp.setStringData(Constants.MOBILE, mobileNumber);
                                    sp.setStringData(Constants.IMAGE, image);
                                    sp.setStringData(Constants.ROLE_NAME, roleName);
                                    sp.setLongData(Constants.GP_ID, panchayatId);

                                    if (userName.startsWith("io_")) {
                                        Intent intent = new Intent(SplashActivity.this, DashboardActivity.class);
                                        startActivity(intent);
                                        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

                                        finish();
                                    } else {
                                        Intent intent = new Intent(SplashActivity.this, ProjectListActivity.class);
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
}