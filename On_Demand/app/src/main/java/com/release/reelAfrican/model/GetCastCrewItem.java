package com.release.reelAfrican.model;

/**
 * Created by Muvi on 23-Jan-17.
 */

public class GetCastCrewItem {

    String title;
    String desc;
    String castImage;

    public String getCelebritySummary() {
        return celebritySummary;
    }

    public void setCelebritySummary(String celebritySummary) {
        this.celebritySummary = celebritySummary;
    }

    String celebritySummary;
    public String getCastPermalink() {
        return castPermalink;
    }

    public void setCastPermalink(String castPermalink) {
        this.castPermalink = castPermalink;
    }

    String castPermalink;

    public GetCastCrewItem(String title, String desc, String castImage,String castPermalink,String celebritySummary) {
        this.title = title;
        this.desc = desc;
        this.castImage = castImage;
        this.castPermalink = castPermalink;
        this.celebritySummary = celebritySummary;

    }

    public String getCastImage() {
        return castImage;
    }

    public void setCastImage(String castImage) {
        this.castImage = castImage;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }



}
