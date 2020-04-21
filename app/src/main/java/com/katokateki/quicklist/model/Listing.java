package com.katokateki.quicklist.model;

import java.util.ArrayList;

public class Listing {
    private String user;
    private String name;
    private String description;
    private String image_url;
    private String price;
    private String time;
    private ArrayList<Comments> comments;
    private String epoch;

    public Listing()
    {

    }

    public Listing(String user, String name, String description, String image_url, String price, ArrayList<Comments> comments, String time, String epoch)
    {
        this.comments = comments;
        this.name = name;
        this.description = description;
        this.image_url = image_url;
        this.price = price;
        this.time = time;
        this.user = user;
        this.epoch = epoch;
    }

    public ArrayList<Comments> getComments() {
        return comments;
    }

    public String getDescription() {
        return description;
    }

    public String getImage_url() {
        return image_url;
    }

    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }

    public String getTime() { return time; }

    public String getUser() {
        return user;
    }

    public String getEpoch() {
        return epoch;
    }
}

