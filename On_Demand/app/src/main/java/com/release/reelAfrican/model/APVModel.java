package com.release.reelAfrican.model;

import java.io.Serializable;

/**
 * Created by User on 13-12-2016.
 */
public class APVModel implements Serializable {

    private String apvPriceForUnsubscribedStr;
    private String apvPriceForsubscribedStr;
    private String apvPlanId;
    private String apvShowUnsubscribedStr;
    private String apvShowSubscribedStr;
    private String apvSeasonUnsubscribedStr;
    private String apvSeasonSubscribedStr;
    private String apvEpisodeUnsubscribedStr;
    private String apvEpisodeSubscribedStr;
    private int isShow;
    private int isSeason;
    private int isEpisode;

    public String getApvShowUnsubscribedStr() {
        return apvShowUnsubscribedStr;
    }

    public void setApvShowUnsubscribedStr(String apvShowUnsubscribedStr) {
        this.apvShowUnsubscribedStr = apvShowUnsubscribedStr;
    }

    public String getApvShowSubscribedStr() {
        return apvShowSubscribedStr;
    }

    public void setApvShowSubscribedStr(String apvShowSubscribedStr) {
        this.apvShowSubscribedStr = apvShowSubscribedStr;
    }

    public String getApvSeasonUnsubscribedStr() {
        return apvSeasonUnsubscribedStr;
    }

    public void setApvSeasonUnsubscribedStr(String apvSeasonUnsubscribedStr) {
        this.apvSeasonUnsubscribedStr = apvSeasonUnsubscribedStr;
    }

    public String getApvSeasonSubscribedStr() {
        return apvSeasonSubscribedStr;
    }

    public void setApvSeasonSubscribedStr(String apvSeasonSubscribedStr) {
        this.apvSeasonSubscribedStr = apvSeasonSubscribedStr;
    }

    public String getApvEpisodeUnsubscribedStr() {
        return apvEpisodeUnsubscribedStr;
    }

    public void setApvEpisodeUnsubscribedStr(String apvEpisodeUnsubscribedStr) {
        this.apvEpisodeUnsubscribedStr = apvEpisodeUnsubscribedStr;
    }

    public String getApvEpisodeSubscribedStr() {
        return apvEpisodeSubscribedStr;
    }

    public void setApvEpisodeSubscribedStr(String apvEpisodeSubscribedStr) {
        this.apvEpisodeSubscribedStr = apvEpisodeSubscribedStr;
    }

    public int getIsShow() {
        return isShow;
    }

    public void setIsShow(int isShow) {
        this.isShow = isShow;
    }

    public int getIsSeason() {
        return isSeason;
    }

    public void setIsSeason(int isSeason) {
        this.isSeason = isSeason;
    }

    public int getIsEpisode() {
        return isEpisode;
    }

    public void setIsEpisode(int isEpisode) {
        this.isEpisode = isEpisode;
    }




    public String getApvPlanId() {
        return apvPlanId;
    }

    public void setApvPlanId(String apvPlanId) {
        this.apvPlanId = apvPlanId;
    }



    public String getAPVPriceForUnsubscribedStr() {
        return apvPriceForUnsubscribedStr;
    }

    public void setAPVPriceForUnsubscribedStr(String apvPriceForUnsubscribedStr) {
        this.apvPriceForUnsubscribedStr = apvPriceForUnsubscribedStr;
    }

    public String getAPVPriceForsubscribedStr() {
        return apvPriceForsubscribedStr;
    }

    public void setAPVPriceForsubscribedStr(String apvPriceForsubscribedStr) {
        this.apvPriceForsubscribedStr = apvPriceForsubscribedStr;
    }
}
