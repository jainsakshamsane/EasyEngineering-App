package com.easyengineering.Models;

public class PurchaseModel {

    String courseid, coursename, details, price, purchaseid, timestamp, userid, videourl, videoduration, lasttime;

    public PurchaseModel(String coursename, String price, String details, String courseid, String videourl, String videoduration, String timestamp, String purchaseid, String lasttime) {
        this.coursename = coursename;
        this.price = price;
        this.details = details;
        this.courseid = courseid;
        this.videourl = videourl;
        this.videoduration = videoduration;
        this.timestamp = timestamp;
        this.purchaseid = purchaseid;
        this.lasttime = lasttime;
    }

    public String getLasttime() {
        return lasttime;
    }

    public void setLasttime(String lasttime) {
        this.lasttime = lasttime;
    }

    public String getVideoduration() {
        return videoduration;
    }

    public void setVideoduration(String videoduration) {
        this.videoduration = videoduration;
    }

    public String getCourseid() {
        return courseid;
    }

    public void setCourseid(String courseid) {
        this.courseid = courseid;
    }

    public String getCoursename() {
        return coursename;
    }

    public void setCoursename(String coursename) {
        this.coursename = coursename;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPurchaseid() {
        return purchaseid;
    }

    public void setPurchaseid(String purchaseid) {
        this.purchaseid = purchaseid;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getVideourl() {
        return videourl;
    }

    public void setVideourl(String videourl) {
        this.videourl = videourl;
    }
}
