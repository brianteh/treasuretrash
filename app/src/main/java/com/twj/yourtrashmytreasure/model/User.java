package com.twj.yourtrashmytreasure.model;

public class User {
    private String id;
    private String username;
    private String email;
    private String photo;

    public User(String id, String username, String email, String photo){
        this.id=id;
        this.username=username;
        this.email=email;
        this.photo=photo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
