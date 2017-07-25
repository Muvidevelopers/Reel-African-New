package com.release.reelAfrican.model;

/**
 * Created by Muvi on 12/15/2016.
 */

public class GetMenuItem {


    private String name;
    private String studioId;
    private String sectionId;
    private String language_id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStudioId() {
        return studioId;
    }

    public void setStudioId(String studioId) {
        this.studioId = studioId;
    }

    public String getSectionId() {
        return sectionId;
    }

    public void setSectionId(String sectionId) {
        this.sectionId = sectionId;
    }

    public String getLanguage_id() {
        return language_id;
    }

    public void setLanguage_id(String language_id) {
        this.language_id = language_id;
    }

    public GetMenuItem(String name, String sectionId, String studioId, String language_id) {
        this.name = name;
        this.sectionId = sectionId;
        this.language_id=language_id;
        this.studioId=studioId;

    }



}
