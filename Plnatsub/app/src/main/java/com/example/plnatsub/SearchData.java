package com.example.plnatsub;

import android.widget.ImageView;

public class SearchData {
    private int iv_thumbnail;
    private String tv_title;
    private String tv_percent;

    public SearchData() {}

    public int getIv_thumbnail() {
        return iv_thumbnail;
    }

    public void setIv_thumbnail(int iv_thumbnail) {
        this.iv_thumbnail = iv_thumbnail;
    }

    public String getTv_title() {
        return tv_title;
    }

    public void setTv_title(String tv_title) {
        this.tv_title = tv_title;
    }

    public String getTv_percent() {
        return tv_percent;
    }

    public void setTv_percent(String tv_percent) {
        this.tv_percent = tv_percent;
    }

}
