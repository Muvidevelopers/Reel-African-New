package com.release.reelAfrican.model;

import java.io.Serializable;

/**
 * Created by User on 13-12-2016.
 */
public class PPVModel implements Serializable {
    private String ppvPriceForUnsubscribedStr;
    private String ppvPriceForsubscribedStr;
    private String ppvPlanId;
    private String ppvShowUnsubscribedStr;
    private String ppvShowSubscribedStr;
    private String ppvSeasonUnsubscribedStr;
    private String ppvSeasonSubscribedStr;
    private String ppvEpisodeUnsubscribedStr;
    private String ppvEpisodeSubscribedStr;
    private int isShow;
    private int isSeason;
    private int isEpisode;

    public String getPpvShowUnsubscribedStr() {
        return ppvShowUnsubscribedStr;
    }

    public void setPpvShowUnsubscribedStr(String ppvShowUnsubscribedStr) {
        this.ppvShowUnsubscribedStr = ppvShowUnsubscribedStr;
    }

    public String getPpvShowSubscribedStr() {
        return ppvShowSubscribedStr;
    }

    public void setPpvShowSubscribedStr(String ppvShowSubscribedStr) {
        this.ppvShowSubscribedStr = ppvShowSubscribedStr;
    }

    public String getPpvSeasonUnsubscribedStr() {
        return ppvSeasonUnsubscribedStr;
    }

    public void setPpvSeasonUnsubscribedStr(String ppvSeasonUnsubscribedStr) {
        this.ppvSeasonUnsubscribedStr = ppvSeasonUnsubscribedStr;
    }

    public String getPpvSeasonSubscribedStr() {
        return ppvSeasonSubscribedStr;
    }

    public void setPpvSeasonSubscribedStr(String ppvSeasonSubscribedStr) {
        this.ppvSeasonSubscribedStr = ppvSeasonSubscribedStr;
    }

    public String getPpvEpisodeUnsubscribedStr() {
        return ppvEpisodeUnsubscribedStr;
    }

    public void setPpvEpisodeUnsubscribedStr(String ppvEpisodeUnsubscribedStr) {
        this.ppvEpisodeUnsubscribedStr = ppvEpisodeUnsubscribedStr;
    }

    public String getPpvEpisodeSubscribedStr() {
        return ppvEpisodeSubscribedStr;
    }

    public void setPpvEpisodeSubscribedStr(String ppvEpisodeSubscribedStr) {
        this.ppvEpisodeSubscribedStr = ppvEpisodeSubscribedStr;
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



    public String getPpvPlanId() {
        return ppvPlanId;
    }

    public void setPpvPlanId(String ppvPlanId) {
        this.ppvPlanId = ppvPlanId;
    }

    public String getPPVPriceForUnsubscribedStr() {
        return ppvPriceForUnsubscribedStr;
    }

    public void setPPVPriceForUnsubscribedStr(String ppvPriceForUnsubscribedStr) {
        this.ppvPriceForUnsubscribedStr = ppvPriceForUnsubscribedStr;
    }

    public String getPPVPriceForsubscribedStr() {
        return ppvPriceForsubscribedStr;
    }

    public void setPPVPriceForsubscribedStr(String ppvPriceForsubscribedStr) {
        this.ppvPriceForsubscribedStr = ppvPriceForsubscribedStr;
    }
}
