package com.release.reelAfrican.model;

/**
 * Created by Muvi on 10/5/2016.
 */
public class ListItem {

    private String title;

    public boolean isGenre() {
        return isGenre;
    }

    public void setIsGenre(boolean isGenre) {
        this.isGenre = isGenre;
    }

    private boolean isGenre;

    private boolean isSelected;
    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
    public String getSectionType() {
        return value;
    }

    public void setSectionType(String sectionType) {
        this.value = sectionType;
    }

    private String value;


    public ListItem(String title, String value) {
        this.title = title;
        this.value = value;
    }

    public ListItem() {

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }



}
