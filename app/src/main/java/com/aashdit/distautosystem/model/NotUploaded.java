package com.aashdit.distautosystem.model;

import org.json.JSONObject;

public class NotUploaded {
    public Long fundReleaseId;
    public String agencyName;
    public String projectName;
    public String releaseAmount;
    public String releaseDate;

    public static NotUploaded parseNotUploaded(JSONObject object) {
        NotUploaded notUploaded = new NotUploaded();
        notUploaded.agencyName = object.optString("agencyName");
        notUploaded.projectName = object.optString("projectName");
        notUploaded.releaseAmount = object.optString("releaseAmount");
        notUploaded.releaseDate = object.optString("releaseDate");
        notUploaded.fundReleaseId = object.optLong("fundReleaseId");
        return notUploaded;
    }
}
