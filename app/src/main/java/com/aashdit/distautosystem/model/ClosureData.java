package com.aashdit.distautosystem.model;

import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

public class ClosureData {
    @SerializedName("tenderId")
    public Long tenderId;
    @SerializedName("tenderCode")
    public String tenderCode;
    @SerializedName("agencyName")
    public String agencyName;
    @SerializedName("aggrementDate")
    public String aggrementDate;
    @SerializedName("aggrementValue")
    public String aggrementValue;
    @SerializedName("timeLine")
    public String timeLine;
    @SerializedName("projectId")
    public String projectId;
    @SerializedName("projectName")
    public String projectName;
    @SerializedName("projectCode")
    public String projectCode;
    public String photoURL;
    public String latitude;
    public String longitude;
    public String remark;

    public static ClosureData parseInitiationData(JSONObject object){
        ClosureData initiationData = new ClosureData();
        initiationData.tenderId = object.optLong("tenderId");
        initiationData.agencyName = object.optString("agencyName");
        initiationData.tenderCode = object.optString("tenderCode");
        initiationData.aggrementDate = object.optString("aggrementDate");
        initiationData.aggrementValue = object.optString("aggrementValue");
        initiationData.timeLine = object.optString("timeLine");
        initiationData.projectName = object.optString("projectName");
        initiationData.projectCode = object.optString("projectCode");
        initiationData.photoURL = object.optString("photoURL");
        initiationData.latitude = object.optString("latitude");
        initiationData.longitude = object.optString("longitude");
        initiationData.remark = object.optString("remark");

        return initiationData;
    }



    public String getTenderCode() {
        return tenderCode;
    }

    public void setTenderCode(String tenderCode) {
        this.tenderCode = tenderCode;
    }

    public String getAgencyName() {
        return agencyName;
    }

    public void setAgencyName(String agencyName) {
        this.agencyName = agencyName;
    }

    public String getAggrementDate() {
        return aggrementDate;
    }

    public void setAggrementDate(String aggrementDate) {
        this.aggrementDate = aggrementDate;
    }

    public String getAggrementValue() {
        return aggrementValue;
    }

    public void setAggrementValue(String aggrementValue) {
        this.aggrementValue = aggrementValue;
    }

    public String getTimeLine() {
        return timeLine;
    }

    public void setTimeLine(String timeLine) {
        this.timeLine = timeLine;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectCode() {
        return projectCode;
    }

    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
    }
}
