package com.aashdit.distautosystem.model;

import org.json.JSONObject;

/**
 * Created by Manabendu on 06/07/21
 */
public class UploadedPhotoDetail {
    public String latitude;
    public String longitude;
    public String remarks;
    public String uploadedImage;

    public static UploadedPhotoDetail parseInitiationUploadedPhotoDetail(JSONObject object){
        UploadedPhotoDetail uploadedPhotoDetail = new UploadedPhotoDetail();
        uploadedPhotoDetail.latitude = object.optString("latitude");
        uploadedPhotoDetail.longitude = object.optString("longitude");
        uploadedPhotoDetail.remarks = object.optString("remarks");
        uploadedPhotoDetail.uploadedImage = object.optString("uploadedImage");

        return uploadedPhotoDetail;
    }
}
