package com.katokateki.quicklist.model;

public class Comments {
    private String comment;
    private String fromUser;
    private String time;
    private String epoch;

    public Comments()
    {

    }

    public Comments(String comment, String fromUser, String time, String epoch)
    {
        this.comment = comment;
        this.fromUser = fromUser;
        this.time = time;
        this.epoch = epoch;
    }

    public String getComment() {
        return comment;
    }

    public String getFromUser() {
        return fromUser;
    }

    public String getTime() {
        return time;
    }

    public String getEpoch() {
        return epoch;
    }
}