package com.aashdit.distautosystem.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.aashdit.distautosystem.R;
import com.aashdit.distautosystem.databinding.ActivityProfileBinding;
import com.aashdit.distautosystem.util.Constants;
import com.aashdit.distautosystem.util.SharedPrefManager;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";

    private ActivityProfileBinding binding;
    private SharedPrefManager sp;


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sp = SharedPrefManager.getInstance(this);

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }


        builder = new AlertDialog.Builder(this);

        binding.etEmail.setText(sp.getStringData(Constants.EMAIL));
        binding.etMobile.setText(sp.getStringData(Constants.MOBILE));
        binding.etUserName.setText(sp.getStringData(Constants.USER_NAME));
        binding.etName.setText(sp.getStringData(Constants.NAME));

        binding.tvChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cpIntent = new Intent(ProfileActivity.this, ChangePasswordActivity.class);
                startActivity(cpIntent);
            }
        });

        binding.rlLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLogoutDialog();
            }
        });


        binding.cvPwdChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cpIntent = new Intent(ProfileActivity.this, ChangePasswordActivity.class);
                startActivity(cpIntent);
            }
        });



    }

    AlertDialog.Builder builder;
    private void showLogoutDialog() {
        builder.setMessage("Do you want to close this application ?")
                .setCancelable(false)
                .setIcon(R.drawable.ic_power)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        sp.clear();
                        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
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
}