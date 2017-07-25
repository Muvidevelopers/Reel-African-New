package com.release.reelAfrican.model;


/**
 * Created by User on 02-07-2015.
 */
public class EpisodesListModel {

    private String episodeTitle;
    private String episodeNumber;
    private String episodeDescription;
    private String episodeTelecastOn;
    private String episodeThumbnailImageView;
    private String episodeVideoUrl;
    private String episodeStreamUniqueId;
    private String episodeImageButton;
    private String thirdPartyUrl;
    private int episodeContentTypesId;


    public int getEpisodeContentTypesId() {
        return episodeContentTypesId;
    }

    public void setEpisodeContentTypesId(int episodeContentTypesId) {
        this.episodeContentTypesId = episodeContentTypesId;
    }

    public String getEpisodeThirdPartyUrl() {
        return thirdPartyUrl;
    }

    public void setEpisodeThirdPartyUrl(String thirdPartyUrl) {
        this.thirdPartyUrl = thirdPartyUrl;
    }

   /* public EpisodesListModel(String episodeNoStr, String episodeStoryStr, String episodeDateStr, String episodeImageStr, String episodeTitleStr, String episodeVideoUrlStr, String episodeSeriesNoStr, String movieUniqueId, String episodeImageButton) {
        this.episodeImageButton = episodeImageButton;
    }*/



    public String getEpisodeImageButton() {
        return episodeImageButton;
    }

    public void setEpisodeImageButton(String episodeImageButton) {
        this.episodeImageButton = episodeImageButton;
    }



    public String getEpisodeStreamUniqueId() {
        return episodeStreamUniqueId;
    }

    public void setEpisodeStreamUniqueId(String episodeStreamUniqueId) {
        this.episodeStreamUniqueId = episodeStreamUniqueId;
    }

    public String getEpisodeMuviUniqueId() {
        return episodeMuviUniqueId;
    }

    public void setEpisodeMuviUniqueId(String episodeMuviUniqueId) {
        this.episodeMuviUniqueId = episodeMuviUniqueId;
    }

    private String episodeMuviUniqueId;
    public String getEpisodeSeriesNo() {
        return episodeSeriesNo;
    }

    public void setEpisodeSeriesNo(String episodeSeriesNo) {
        this.episodeSeriesNo = episodeSeriesNo;
    }

    private String episodeSeriesNo;
    private String episodeDuration;

    public String getEpisodeDuration() {
        return episodeDuration;
    }

    public void setEpisodeDuration(String episodeDuration) {
        this.episodeDuration = episodeDuration;
    }

    public EpisodesListModel(String episodeNumber, String episodeDescription, String episodeTelecastOn,
                             String episodeThumbnailImageView, String episodeTitle, String episodeVideoUrl,
                             String episodeSeriesNo, String episodeMuviUniqueId, String episodeStreamUniqueId,
                             String thirdPartyUrl, int episodeContentTypesId, String episodeDuration) {
        this.episodeNumber = episodeNumber;
        this.episodeDescription = episodeDescription;
        this.episodeTelecastOn = episodeTelecastOn;
        this.episodeThumbnailImageView = episodeThumbnailImageView;
        this.episodeTitle = episodeTitle;
        this.episodeVideoUrl = episodeVideoUrl;
        this.episodeSeriesNo = episodeSeriesNo;
        this.episodeMuviUniqueId = episodeMuviUniqueId;
        this.episodeStreamUniqueId = episodeStreamUniqueId;
        this.thirdPartyUrl = thirdPartyUrl;
        this.episodeDuration = episodeDuration;


    }

    public String getEpisodeTitle() {
        return episodeTitle;
    }

    public void setEpisodeTitle(String episodeTitle) {
        this.episodeTitle = episodeTitle;
    }

    public String getEpisodeVideoUrl() {
        return episodeVideoUrl;
    }

    public void setEpisodeVideoUrl(String episodeVideoUrl) {
        this.episodeVideoUrl = episodeVideoUrl;
    }

    public String getEpisodeNumber() {
        return episodeNumber;
    }

    public void setEpisodeNumber(String episodeNumber) {
        this.episodeNumber = episodeNumber;
    }

    public String getEpisodeDescription() {
        return episodeDescription;
    }

    public void setEpisodeDescription(String episodeDescription) {
        this.episodeDescription = episodeDescription;
    }

    public String getEpisodeTelecastOn() {
        return episodeTelecastOn;
    }

    public void setEpisodeTelecastOn(String episodeTelecastOn) {
        this.episodeTelecastOn = episodeTelecastOn;
    }

    public String getEpisodeThumbnailImageView() {
        return episodeThumbnailImageView;
    }

    public void setEpisodeThumbnailImageView(String episodeThumbnailImageView) {
        this.episodeThumbnailImageView = episodeThumbnailImageView;
    }
}
