package com.twj.yourtrashmytreasure;

public class Bookmark {
    private String item_redeem_id;
    private String user_id;
    public Bookmark(){

    }

    public Bookmark(String item_redeem_id, String user_id) {
        this.item_redeem_id = item_redeem_id;
        this.user_id = user_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getItem_redeem_id() {
        return item_redeem_id;
    }

    public void setItem_redeem_id(String item_redeem_id) {
        this.item_redeem_id = item_redeem_id;
    }
}
