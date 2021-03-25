package com.aashdit.distautosystem.model;

import org.json.JSONObject;

import java.util.ArrayList;

public class Uploaded {
    public Long fundReleaseGeoTagId;
    public String projectName;
    public String agencyName;
    public String uploadedImage;
    public String latitude;
    public String longitude;
    public String remark;
    public String uploadedDateWithTime;
    public String releaseDate;
    public String releaseAmount;
    public String address;
    public ArrayList<String> images;


    public static Uploaded parseUploaded(JSONObject object) {
        Uploaded notUploaded = new Uploaded();
        notUploaded.agencyName = object.optString("agencyName");
        notUploaded.projectName = object.optString("projectName");
        notUploaded.uploadedImage = object.optString("uploadedImage");
        notUploaded.latitude = object.optString("latitude");
        notUploaded.longitude = object.optString("longitude");
        notUploaded.remark = object.optString("remarks");
        notUploaded.uploadedDateWithTime = object.optString("uploadedDateWithTime");
        notUploaded.releaseDate = object.optString("releaseDate");
        notUploaded.releaseAmount = object.optString("releaseAmount");
        notUploaded.address = object.optString("address");
        notUploaded.fundReleaseGeoTagId = object.optLong("fundReleaseGeoTagId");
        return notUploaded;
    }
}
