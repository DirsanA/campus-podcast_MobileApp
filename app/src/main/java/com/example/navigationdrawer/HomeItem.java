package com.example.navigationdrawer;

import android.net.Uri;

public class HomeItem {
    private String title;
    private String description;
    private String video;

    public HomeItem(String title, String description, String video) {
        this.title = title;
        this.description = description;
        this.video = video;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public Uri getVideoUri() {
        return Uri.parse(video);
    }
}
