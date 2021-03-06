package com.aashdit.distautosystem.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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
import com.aashdit.distautosystem.R;
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
import com.bumptech.glide.Glide;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ImageUploadActivity extends AppCompatActivity implements LocationListener {

    private static final String TAG = "ImageUploadActivity";
    int REQUEST_CAMERA = 2;
    Long userId;
    String remarks;
    double latitude, longitude;
    String address = "";
    String imageFilePath = "";
    File finalFile;
    private ActivityImageUploadBinding binding;
    private SharedPrefManager sp;
    private List<File> imageFiles = new ArrayList<>();
    private LocationManager locationManager;
    private Long fundReleaseId;
    private String type;
    private int uploadedFileCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityImageUploadBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sp = SharedPrefManager.getInstance(this);
        userId = sp.getLongData(Constants.USER_ID);
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        fundReleaseId = getIntent().getLongExtra("ID", 0L);
        type = getIntent().getStringExtra("TYPE");

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


        binding.ivUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (App.latitude != 0.0 && App.longitude != 0.0) {
                    openCamera();
                } else {
                    Toast.makeText(ImageUploadActivity.this, "Fetching Location, Please wait.", Toast.LENGTH_LONG).show();
                }
            }
        });
        binding.ivUpload2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (App.latitude != 0.0 && App.longitude != 0.0) {
                    openCamera();
                } else {
                    Toast.makeText(ImageUploadActivity.this, "Fetching Location, Please wait.", Toast.LENGTH_LONG).show();
                }
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
                if (imageFiles.size() < 2) {
                    Toast.makeText(ImageUploadActivity.this, "Please select 2 image", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(remarks)) {
                    Toast.makeText(ImageUploadActivity.this, "Please Enter Remarks", Toast.LENGTH_SHORT).show();
                } else if (App.latitude == 0.0 || App.longitude == 0.0) {
                    Toast.makeText(ImageUploadActivity.this, "Fetching Location, Please wait.", Toast.LENGTH_LONG).show();
                } else {
                    switch (type) {
                        case "FUND":
                            uploadImage();
                            break;
                        case "INITITATION":
                            initiationUploadImage();
                            break;
                        case "CLOSURE":
                            closureUploadImage();
                            break;
                        default:
                            break;
                    }
//                    uploadImage();
                }
            }
        });
    }

    private void getLocation() {
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            assert locationManager != null;
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 0, this);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000, 0, this);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
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

    @Override
    protected void onResume() {
        super.onResume();

        latitude = App.latitude;
        longitude = App.longitude;
        address = App.capturedAddress;

    }

    private void uploadImage() {

        AndroidNetworking.upload(BuildConfig.BASE_URL + "api/captureFundReleaseGeoTagDetails")
                .addMultipartFile("imagePath", imageFiles.get(uploadedFileCount))
                .addMultipartParameter("fundReleaseId", String.valueOf(fundReleaseId))
                .addMultipartParameter("remarks", remarks)
                .addMultipartParameter("latitude", String.valueOf(latitude))
                .addMultipartParameter("longitude", String.valueOf(longitude))
                .addMultipartParameter("address", address)
                .addMultipartParameter("userId", String.valueOf(userId))
                .setTag("upload")
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
                                    uploadedFileCount += 1;
                                    if (uploadedFileCount == 2) {
                                        uploadedFileCount = 0;
                                        Intent intent = getIntent();
                                        intent.putExtra("data", true);
                                        setResult(RESULT_OK, intent);
                                        finish();
                                    } else {
                                        uploadImage();
                                    }
                                } else {
                                    Intent intent = getIntent();
                                    intent.putExtra("data", false);
                                    setResult(RESULT_OK, intent);
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
                        Log.e(TAG, "onError: " + anError.getErrorDetail());
                    }
                });

    }

    private void initiationUploadImage() {

        AndroidNetworking.upload(BuildConfig.BASE_URL + "api/tenderInitialInspectionUpload")
                .addMultipartFile("uploadImg", imageFiles.get(uploadedFileCount))
                .addMultipartParameter("tenderId", String.valueOf(fundReleaseId))
                .addMultipartParameter("remark", remarks)
                .addMultipartParameter("latitude", String.valueOf(latitude))
                .addMultipartParameter("longitude", String.valueOf(longitude))
                .addMultipartParameter("uploadLevel", "INITIAL")
                .addMultipartParameter("userId", String.valueOf(userId))
                .setTag("upload")
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
                                if (resObj.optString("outcome").equals("Photo Uploaded Successfully")) {
                                    uploadedFileCount += 1;
                                    if (uploadedFileCount == 2) {
                                        uploadedFileCount = 0;
                                        Intent intent = getIntent();
                                        intent.putExtra("data", true);
                                        setResult(RESULT_OK, intent);
                                        finish();
                                    } else {
                                        initiationUploadImage();
                                    }
                                } else {
                                    Intent intent = getIntent();
                                    intent.putExtra("data", false);
                                    setResult(RESULT_OK, intent);
                                    finish();
                                }
                                Toast.makeText(ImageUploadActivity.this, resObj.optString("outcome"), Toast.LENGTH_SHORT).show();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e(TAG, "onError: " + anError.getErrorDetail());
                    }
                });

    }

    private void closureUploadImage() {

        AndroidNetworking.upload(BuildConfig.BASE_URL + "api/tenderClosureInspectionUpload")
                .addMultipartFile("uploadImg", imageFiles.get(uploadedFileCount))
                .addMultipartParameter("tenderId", String.valueOf(fundReleaseId))
                .addMultipartParameter("remark", remarks)
                .addMultipartParameter("latitude", String.valueOf(latitude))
                .addMultipartParameter("longitude", String.valueOf(longitude))
                .addMultipartParameter("uploadLevel", "CLOSURE")
                .addMultipartParameter("userId", String.valueOf(userId))
                .setTag("upload")
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
                                if (resObj.optString("outcome").equals("Photo Uploaded Successfully")) {
                                    uploadedFileCount += 1;
                                    if (uploadedFileCount == 2) {
                                        uploadedFileCount = 0;
                                        Intent intent = getIntent();
                                        intent.putExtra("data", true);
                                        setResult(RESULT_OK, intent);
                                        finish();
                                    } else {
                                        closureUploadImage();
                                    }
//                                    Intent intent = getIntent();
//                                    intent.putExtra("data", true);
//                                    setResult(RESULT_OK, intent);
//                                    finish();
                                } else {
                                    Intent intent = getIntent();
                                    intent.putExtra("data", false);
                                    setResult(RESULT_OK, intent);
                                    finish();
                                }
                                Toast.makeText(ImageUploadActivity.this, resObj.optString("outcome"), Toast.LENGTH_SHORT).show();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e(TAG, "onError: " + anError.getErrorDetail());
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CAMERA) {
            if (data != null) {
                if (data.hasExtra("data")) {
                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");

//                    onCaptureImageResult(bitmap);
                    Uri furi = getImageUri(ImageUploadActivity.this, bitmap);
                    //File finalFile = new File(getRealPathFromUri(uri));
                    finalFile = FileUtils.getFile(ImageUploadActivity.this, furi);
                    imageFiles.add(finalFile);
                    imageFilePath = finalFile.toString();

                    if (imageFiles.size() == 1 && imageFiles.get(0) != null) {
                        Glide.with(ImageUploadActivity.this).load(imageFiles.get(0))
                                .placeholder(R.drawable.ic_upload).into(binding.ivUpload);
//                        binding.ivUpload.setImageBitmap(imageFiles.get(0));
                    }
                    if (imageFiles.size() == 2 && imageFiles.get(1) != null) {
                        Glide.with(ImageUploadActivity.this).load(imageFiles.get(1))
                                .placeholder(R.drawable.ic_upload).into(binding.ivUpload2);
//                        binding.ivUpload2.setImageBitmap(bitmap);
                    }

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

    @Override
    public void onLocationChanged(@NonNull Location location) {
        Log.d("Tag", "LatLng===>" + location.getLatitude() + " " + location.getLongitude());

        if (location.getLatitude() != 0.0 && location.getLongitude() != 0.0) {
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
        }
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }
}