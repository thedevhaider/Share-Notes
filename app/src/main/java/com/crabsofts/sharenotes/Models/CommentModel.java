package com.crabsofts.sharenotes.Models;

/**
 * Created by CodeGod on 07-07-2017.
 */

public class CommentModel {
    String comment_user_name, comment_profile_image, comment_user_message, comment_time, comment_key, post_key, comment_owner;
    public CommentModel(String comment_user_name, String comment_profile_image, String comment_user_message, String comment_time, String comment_key, String post_key, String comment_owner){
        this.setComment_user_name(comment_user_name);
        this.setComment_profile_image(comment_profile_image);
        this.setComment_user_message(comment_user_message);
        this.setComment_time(comment_time);
        this.setComment_key(comment_key);
        this.setPost_key(post_key);
        this.setComment_owner(comment_owner);
    }

    public String getComment_owner() {
        return comment_owner;
    }

    public void setComment_owner(String comment_owner) {
        this.comment_owner = comment_owner;
    }

    public String getComment_key() {
        return comment_key;
    }

    public void setComment_key(String comment_key) {
        this.comment_key = comment_key;
    }

    public String getPost_key() {
        return post_key;
    }

    public void setPost_key(String post_key) {
        this.post_key = post_key;
    }

    public String getComment_user_name() {
        return comment_user_name;
    }

    public void setComment_user_name(String comment_user_name) {
        this.comment_user_name = comment_user_name;
    }

    public String getComment_profile_image() {
        return comment_profile_image;
    }

    public void setComment_profile_image(String comment_profile_image) {
        this.comment_profile_image = comment_profile_image;
    }

    public String getComment_user_message() {
        return comment_user_message;
    }

    public void setComment_user_message(String comment_user_message) {
        this.comment_user_message = comment_user_message;
    }

    public String getComment_time() {
        return comment_time;
    }

    public void setComment_time(String comment_time) {
        this.comment_time = comment_time;
    }
}
