package com.crabsofts.sharenotes.Models;

/**
 * Created by CodeGod on 6/17/2017.
 */

public class DoubtDetailsModel {
    String user_name, post_time, post_title, post_content, profile_image, post_key, user_uid;
    int colorStrip;
    public DoubtDetailsModel(int colorStrip, String profile_image, String user_name, String post_time, String post_title, String post_content, String post_key, String user_uid){
        this.setColorStrip(colorStrip);
        this.setProfile_image(profile_image);
        this.setUser_name(user_name);
        this.setPost_time(post_time);
        this.setPost_title(post_title);
        this.setPost_content(post_content);
        this.setPost_key(post_key);
        this.setUser_uid(user_uid);
    }

    public String getUser_uid() {
        return user_uid;
    }

    public void setUser_uid(String user_uid) {
        this.user_uid = user_uid;
    }

    public String getPost_key() {
        return post_key;
    }

    public void setPost_key(String post_key) {
        this.post_key = post_key;
    }

    public String getProfile_image() {
        return profile_image;
    }

    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getPost_time() {
        return post_time;
    }

    public void setPost_time(String post_time) {
        this.post_time = post_time;
    }

    public String getPost_title() {
        return post_title;
    }

    public void setPost_title(String post_title) {
        this.post_title = post_title;
    }

    public String getPost_content() {
        return post_content;
    }

    public void setPost_content(String post_content) {
        this.post_content = post_content;
    }

    public int getColorStrip() {
        return colorStrip;
    }

    public void setColorStrip(int colorStrip) {
        this.colorStrip = colorStrip;
    }
}
