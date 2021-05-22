package com.myway.models;

import java.util.Date;

public class Live {

    private String userId;
    private String title;
    private String preview_jpg;
    private boolean live;
    private String resourceUri;
    private String time;

    public String getResourceUri() {
        return resourceUri;
    }

    public void setResourceUri(String resourceUri) {
        this.resourceUri = resourceUri;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Live(String userId, String title, String preview_jpg, boolean live, String resourceUri, String time) {
        this.userId = userId;
        this.title = title;
        this.preview_jpg = preview_jpg;
        this.live = live;
        this.resourceUri=resourceUri;
        this.time=time;

        this.live=false;
    }

    public Live() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPreview_jpg() {
        return preview_jpg;
    }

    public void setPreview_jpg(String preview_jpg) {
        this.preview_jpg = preview_jpg;
    }

    public boolean isLive() {
        return live;
    }

    public void setLive(boolean live) {
        this.live = live;
    }
}
