package com.example.appmessengerfirebase.Model;

public class User {
    private String id;
    private String userName;
    private String image;
    private String status;
    private String search;

    public User(String id, String userName, String image, String status, String search) {
        this.id = id;
        this.userName = userName;
        this.image = image;
        this.status = status;
        this.search = search;
    }

    public User() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

}
