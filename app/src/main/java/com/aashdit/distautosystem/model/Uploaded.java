package com.aashdit.distautosystem.model;

import org.json.JSONObject;

import java.util.ArrayList;

public class Uploaded {
    public Long tenderId;
    public String projectName;
    public String agencyName;
    public String photoURL;
    public String latitude;
    public String longitude;
    public String remark;
    public String aggrementValue;
    public String aggrementDate;
    public ArrayList<String> images;


    public static Uploaded parseUploaded(JSONObject object) {
        Uploaded notUploaded = new Uploaded();
        notUploaded.agencyName = object.optString("agencyName");
        notUploaded.projectName = object.optString("projectName");
        notUploaded.photoURL = object.optString("photoURL");
        notUploaded.latitude = object.optString("latitude");
        notUploaded.longitude = object.optString("longitude");
        notUploaded.remark = object.optString("remark");
        notUploaded.aggrementValue = object.optString("aggrementValue");
        notUploaded.aggrementDate = object.optString("aggrementDate");
        notUploaded.tenderId = object.optLong("tenderId");
        return notUploaded;
    }
}
