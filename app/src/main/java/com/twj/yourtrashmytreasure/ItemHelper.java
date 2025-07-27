package com.twj.yourtrashmytreasure;

import com.google.firebase.database.Exclude;


public class ItemHelper {
    private String item_id;
    private String item_name;
    private String item_category;
    private String item_description;
    private String user_id;
    private String used_status;
    private String item_redeem_id;
    private String item_image_url;
    private Boolean redeemed_status;
    private double longitude, latitude;

    public ItemHelper() {
        //Empty constructor
    }

    public ItemHelper(String item_image_url, String user_id, String item_name, String item_redeem_id, String item_category, String item_description, Boolean redeemed_status, String used_status, double longitude, double latitude) {
        this.item_image_url = item_image_url;
        this.user_id = user_id;
        this.item_name = item_name;
        this.item_redeem_id = item_redeem_id;
        this.item_category = item_category;
        this.item_description = item_description;
        this.redeemed_status = redeemed_status;
        this.used_status = used_status;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public String getItem_image_url() {
        return item_image_url;
    }

    public void setItem_image_url(String item_image_url) {
        this.item_image_url = item_image_url;
    }

    public String getItem_redeem_id() {
        return item_redeem_id;
    }

    public void setItem_redeem_id(String item_redeem_id) {
        this.item_redeem_id = item_redeem_id;
    }
    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public void setItem_id(String item_id) {
        this.item_id = item_id;
    }

    public void setItem_name(String item_name) {
        this.item_name = item_name;
    }

    public void setItem_category(String item_category) {
        this.item_category = item_category;
    }

    public void setItem_description(String item_description) {
        this.item_description = item_description;
    }

    public void setRedeemed_status(Boolean redeemed_status) {
        this.redeemed_status = redeemed_status;
    }

    public void setUsed_status(String used_status) {
        this.used_status = used_status;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getItem_id() {
        return item_id;
    }

    public String getItem_name() {
        return item_name;
    }

    public String getItem_category() {
        return item_category;
    }

    public String getItem_description() {
        return item_description;
    }

    public Boolean getRedeemed_status() {
        return redeemed_status;
    }

    public String getUsed_status() {
        return used_status;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }
}



