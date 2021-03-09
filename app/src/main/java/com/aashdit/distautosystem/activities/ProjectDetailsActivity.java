package com.aashdit.distautosystem.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.aashdit.distautosystem.BuildConfig;
import com.aashdit.distautosystem.R;
import com.aashdit.distautosystem.adapters.StagesAdapter;
import com.aashdit.distautosystem.databinding.ActivityProjectDetailsBinding;
import com.aashdit.distautosystem.model.Stage;
import com.aashdit.distautosystem.util.Utility;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ProjectDetailsActivity extends AppCompatActivity  implements StagesAdapter.OnStageClickListener{

    private static final String TAG = "ProjectDetailsActivity";

    private ActivityProjectDetailsBinding binding;
    private Long proj_id;

    private ArrayList<Stage> stages = new ArrayList<>();

    private StagesAdapter adapter;

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
        binding = ActivityProjectDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        proj_id = getIntent().getLongExtra("PROJ_ID",0L);

        adapter = new StagesAdapter(this, stages);
        adapter.setOnStageClickListener(this);
//        getProjectDetails();
        binding.rlViewPhase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                showApproveBottomSheet();
            }
        });

//        RecyclerView mRvPhase = findViewById(R.id.rv_phase_list);
        binding.rvPhaseList.setLayoutManager(new LinearLayoutManager(this));
        binding.rvPhaseList.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getProjectDetails();
    }

    private String currentStageCode,currentPhaseCode;
    private void getProjectDetails() {
        AndroidNetworking.get(BuildConfig.BASE_URL.concat("api/awc/anganwadiConstruction/getProjectDetailsByProjectId?projectId="+proj_id))
                .setTag("projectDetails")
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        if(Utility.isStringValid(response)) {
                            try {
                                JSONObject resObj = new JSONObject(response);
                                if(resObj.optString("flag").equals("Success")){

                                    JSONArray stageArr = resObj.optJSONArray("stageList");
                                    if (stageArr != null && stageArr.length() > 0){
                                        stages.clear();
                                        for (int i = 0; i < stageArr.length(); i++) {
                                            Stage stage = Stage.parseStageResponse(stageArr.optJSONObject(i));
                                            stages.add(stage);
                                        }
                                        adapter.notifyDataSetChanged();

                                    }

                                    currentStageCode = resObj.optString("currentStageCode");
                                    currentPhaseCode = resObj.optString("currentPhaseCode");
                                    String districtName = resObj.optString("districtName");
                                    binding.tvDistName.setText(districtName);
                                    String blockName = resObj.optString("blockName");
                                    binding.tvBlockName.setText(blockName);
                                    String gpName = resObj.optString("gpName");
                                    binding.tvGpName.setText(gpName);
                                    String projectName = resObj.optString("projectName");
                                    binding.tvProjectTitle.setText(projectName);
                                    String financialYearName = resObj.optString("financialYearName");
                                    binding.tvProjectFy.setText(financialYearName);
                                    String projectCode = resObj.optString("projectCode");
                                    binding.tvProjCode.setText(projectCode);
                                    String approvalLetterNumber = resObj.optString("approvalLetterNumber");
                                    binding.tvLetterNo.setText(approvalLetterNumber);
                                    String schemeName = resObj.optString("schemeName");
                                    binding.tvSchName.setText(schemeName);
                                    String estimatedStartDate = resObj.optString("estimatedStartDate");
                                    binding.tvEsDate.setText(estimatedStartDate);
                                    String creationDate = resObj.optString("creationDate");
                                    binding.tvCrDate.setText(creationDate);
                                    String estimatedEndDate = resObj.optString("estimatedEndDate");
                                    binding.tvEeDate.setText(estimatedEndDate);
                                    String currentStageName = resObj.optString("currentStageName");
                                    String estimatedProjectCost = resObj.optString("estimatedProjectCost");
                                    binding.tvEstProjCost.setText(estimatedProjectCost);
                                    String anganwadiCenterName = resObj.optString("anganwadiCenterName");
                                    binding.tvAwcName.setText(anganwadiCenterName);
                                    String description = resObj.optString("description");
                                    binding.tvProjDesc.setText(description);
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

    @Override
    public void onProjectClick(int position) {
        Intent intent = new Intent(this,GeoTaggingActivity.class);
        intent.putExtra("STAGE_ID",stages.get(position).stageId);
        intent.putExtra("PROJ_ID",proj_id);
        intent.putExtra("STAGECODE",stages.get(position).stageCode);
        intent.putExtra("CURRENT_STAGE_CODE",currentStageCode);
        intent.putExtra("CURRENT_PHASE_CODE",currentPhaseCode);
        startActivity(intent);
//        dialog.dismiss();
    }
}