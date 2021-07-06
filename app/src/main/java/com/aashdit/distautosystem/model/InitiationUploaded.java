package com.aashdit.distautosystem.model;

import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Manabendu on 06/07/21
 */
public class InitiationUploaded {

    @SerializedName("agencyName")
    public String agencyName;
    @SerializedName("projectName")
    public String projectName;
    public ArrayList<UploadedPhotoDetail> uploadedPhotoDetailArrayList;


    public static InitiationUploaded parseInitiationUploaded(JSONObject object) {
        InitiationUploaded initiationData = new InitiationUploaded();
        initiationData.agencyName = object.optString("agencyName");
        initiationData.projectName = object.optString("projectName");
        initiationData.uploadedPhotoDetailArrayList = new ArrayList<>();

        JSONArray resArray = object.optJSONArray("uploadPhotoDetailsList");
        if (resArray != null && resArray.length() > 0) {
            initiationData.uploadedPhotoDetailArrayList.clear();
            for (int i = 0; i < resArray.length(); i++) {
                UploadedPhotoDetail detail = UploadedPhotoDetail.parseInitiationUploadedPhotoDetail(resArray.optJSONObject(i));
                initiationData.uploadedPhotoDetailArrayList.add(detail);
            }
        }

        return initiationData;
    }

}
