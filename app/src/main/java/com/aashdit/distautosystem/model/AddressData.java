package com.aashdit.distautosystem.model;

import org.json.JSONObject;

/**
 * Created by Manabendu on 08/06/21
 */
public class AddressData {
    public String address;
    public Long fundReleaseGeoTagId;
    public String uploadedDateWithTime;
    public String latitude;
    public String longitude;
    public String uploadedImage;
    public String remarks;

    public static AddressData parseAddressData(JSONObject object){
        AddressData addressData = new AddressData();
        addressData.address = object.optString("address");
        addressData.fundReleaseGeoTagId = object.optLong("fundReleaseGeoTagId");
        addressData.uploadedDateWithTime = object.optString("uploadedDateWithTime");
        addressData.latitude = object.optString("latitude");
        addressData.longitude = object.optString("longitude");
        addressData.uploadedImage = object.optString("uploadedImage");
        addressData.remarks = object.optString("remarks");
        return addressData;
    }

}
