package com.aashdit.distautosystem.activities;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.aashdit.distautosystem.BuildConfig;
import com.aashdit.distautosystem.app.App;
import com.aashdit.distautosystem.databinding.ActivityImageUploadBinding;
import com.aashdit.distautosystem.util.Constants;
import com.aashdit.distautosystem.util.FileUtils;
import com.aashdit.distautosystem.util.SharedPrefManager;
import com.aashdit.distautosystem.util.Utility;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.androidnetworking.interfaces.UploadProgressListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.List;

public class ImageUploadActivity extends AppCompatActivity {

    private static final String TAG = "ImageUploadActivity";

    private ActivityImageUploadBinding binding;
    int REQUEST_CAMERA = 2;

    private SharedPrefManager sp;

    String userId;
    String remarks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityImageUploadBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sp = SharedPrefManager.getInstance(this);
        userId = sp.getStringData(Constants.USER_ID);
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA,
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


        binding.ivUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCamera();
            }
        });

        binding.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        binding.btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                remarks = binding.remark.getText().toString().trim();
                if (!TextUtils.isEmpty(remarks)) {
                    uploadImage();
                } else {
                    Toast.makeText(ImageUploadActivity.this, "Please Enter Remarks", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ImageUploadActivity.this);
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

    double latitude, longitude;
    String address = "";

    @Override
    protected void onResume() {
        super.onResume();

        latitude = App.latitude;
        longitude = App.longitude;
        address = App.capturedAddress;

    }

    private void uploadImage() {

        AndroidNetworking.upload(BuildConfig.BASE_URL + "api/captureFundReleaseGeoTagDetails")
                .addMultipartFile("imagePath", finalFile)
                .addMultipartParameter("fundReleaseId", remarks)
                .addMultipartParameter("remarks", "value")
                .addMultipartParameter("latitude", String.valueOf(latitude))
                .addMultipartParameter("longitude", String.valueOf(longitude))
                .addMultipartParameter("address", address)
                .addMultipartParameter("userId", userId)
                .setTag("uploadTest")
                .setPriority(Priority.HIGH)
                .build()
                .setUploadProgressListener(new UploadProgressListener() {
                    @Override
                    public void onProgress(long bytesUploaded, long totalBytes) {
                        // do anything with progress
                    }
                })
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        if (Utility.isStringValid(response)) {
                            try {
                                JSONObject resObj = new JSONObject(response);
                                if (resObj.optString("flag").equals("Success")) {
                                    Intent intent = getIntent();
                                    intent.putExtra("data",true);
                                    setResult(RESULT_OK,intent);
                                    finish();
                                }else{
                                    Intent intent = getIntent();
                                    intent.putExtra("data",false);
                                    setResult(RESULT_OK,intent);
                                    finish();
                                }
                                Toast.makeText(ImageUploadActivity.this, resObj.optString("Message"), Toast.LENGTH_SHORT).show();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onError(ANError anError) {

                    }
                });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    String imageFilePath = "";
    File finalFile;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CAMERA) {
            if (data != null) {
                if (data.hasExtra("data")) {
                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                    binding.ivUpload.setImageBitmap(bitmap);
//                    onCaptureImageResult(bitmap);
                    Uri furi = getImageUri(ImageUploadActivity.this, bitmap);
                    //File finalFile = new File(getRealPathFromUri(uri));
                    finalFile = FileUtils.getFile(ImageUploadActivity.this, furi);
                    imageFilePath = finalFile.toString();
                    Log.v("imagepath", imageFilePath);
                }
            } else {
                Toast.makeText(this, "null return", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private Uri getImageUri(ImageUploadActivity youractivity, Bitmap bitmap) {
        String path = "";
        if (!bitmap.equals("")) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            path = MediaStore.Images.Media.insertImage(youractivity.getContentResolver(), bitmap, "IMG_" + System.currentTimeMillis() /*Calendar.getInstance().getTime()*/, null);

        }
        return Uri.parse(path);
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }
}