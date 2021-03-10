package com.aashdit.distautosystem.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.aashdit.distautosystem.R;
import com.aashdit.distautosystem.adapters.PhotoNotUploadAdapter;
import com.aashdit.distautosystem.databinding.ActivityDashboardBinding;
import com.aashdit.distautosystem.model.NotUploaded;
import com.rm.rmswitch.RMSwitch;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class DashboardActivity extends AppCompatActivity {

    private static final String TAG = "DashboardActivity";

    private ActivityDashboardBinding binding;
    private String startdate = "", enddate = "";

    private ArrayList<NotUploaded> notUploadedData = new ArrayList<>();
    private PhotoNotUploadAdapter notUploadedAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent profileIntent = new Intent(DashboardActivity.this, ProfileActivity.class);
                startActivity(profileIntent);
            }
        });
        setDafaultDateFormat();
        notUploadedAdapter = new PhotoNotUploadAdapter(this,notUploadedData);
        binding.rvProjectList.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL,false));
        binding.rvProjectList.setAdapter(notUploadedAdapter);

        binding.ivFromCalender.setOnClickListener(view12 -> setDateTimeField());
        binding.ivToCalender.setOnClickListener(view1 -> setTodayDateTimeField());

        binding.uploadSwitch.addSwitchObserver(new RMSwitch.RMSwitchObserver() {
            @Override
            public void onCheckStateChange(RMSwitch switchView, boolean isChecked) {
                if (isChecked) {

                }else{
                    binding.rvProjectList.setAdapter(notUploadedAdapter);
                }
            }
        });

        binding.swiperefreshlayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                binding.swiperefreshlayout.setRefreshing(false);
            }
        });

    }
    private void setTodayDateTimeField() {
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(Objects.requireNonNull(this),
                (view, year, monthOfYear, dayOfMonth) -> {

                    binding.tvToDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
//                    checkForApiCall();
                }, mYear, mMonth, mDay);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }
    private int mYear, mMonth, mDay;
    private void setDateTimeField() {
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(Objects.requireNonNull(this),
                (view, year, monthOfYear, dayOfMonth) -> {

                    binding.tvFromDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
//                    checkForApiCall();
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


}