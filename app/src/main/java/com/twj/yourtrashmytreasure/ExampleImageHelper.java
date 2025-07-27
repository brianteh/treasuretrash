package com.twj.yourtrashmytreasure;

import java.util.HashMap;
import java.util.Map;

public class ExampleImageHelper {
    private String item_name;
    private String image_url;
    private Map<String,Object> item_data = new HashMap<>();
    public ExampleImageHelper(){

    }

    public ExampleImageHelper(String item_name, String image_url) {
        if(item_name.trim().equals("")){
            item_name = "Unknown";
        }
        this.item_name = item_name;
        this.image_url = image_url;
        item_data.put("item_name",this.item_name);
        item_data.put("image_url",this.image_url);
    }

    public String getItem_name() {
        return item_name;
    }

    public void setItem_name(String item_name) {
        this.item_name = item_name;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public Map<String,Object> getItem_data(){ return item_data; }
}
