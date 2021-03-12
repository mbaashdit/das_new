package com.aashdit.distautosystem.model;

import org.json.JSONObject;

public class NotUploaded {
    public Long tenderId;
    public String agencyName;
    public String projectName;

    public static NotUploaded parseNotUploaded(JSONObject object) {
        NotUploaded notUploaded = new NotUploaded();
        notUploaded.agencyName = object.optString("agencyName");
        notUploaded.projectName = object.optString("projectName");
        notUploaded.tenderId = object.optLong("tenderId");
        return notUploaded;
    }
}
