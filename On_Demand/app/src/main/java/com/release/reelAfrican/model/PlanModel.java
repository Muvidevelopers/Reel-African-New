package com.release.reelAfrican.model;

/**
 * Created by Muvi on 9/6/2016.
 */
public class PlanModel {
    private String planIdStr;
    private String planNameStr;
    private String purchaseValueStr;
    private String planRecurrenceStr;
    private String studioIdStr;
    private String planFrequencuStr;
    private int planStatusStr;
    private String planLanguage_idStr;
    private String currencyCountryCodeStr;
    private String planCurrencyIdStr;
    private String planCurrencySymbolstr;

    public PlanModel(String planNameStr, String purchaseValueStr, String planRecurrenceStr, String planFrequencuStr, boolean isSelected, String studioIdStr, int planStatusStr, String planLanguage_idStr, String planIdStr, String planCurrencyIdStr, String planCurrencySymbolstr, String planTrialPeriodStr, String planTrialRecurrenceStr, String currencyCountryCodeStr) {
        this.planNameStr = planNameStr;
        this.purchaseValueStr = purchaseValueStr;
        this.planRecurrenceStr = planRecurrenceStr;
        this.planFrequencuStr = planFrequencuStr;
        this.isSelected = isSelected;
        this.studioIdStr = studioIdStr;
        this.planStatusStr = planStatusStr;
        this.planLanguage_idStr = planLanguage_idStr;
        this.planIdStr = planIdStr;
        this.planCurrencyIdStr = planCurrencyIdStr;
        this.planCurrencySymbolstr = planCurrencySymbolstr;
        this.planTrialPeriodStr = planTrialPeriodStr;
        this.planTrialRecurrenceStr = planTrialRecurrenceStr;
        this.currencyCountryCodeStr = currencyCountryCodeStr;

    }

    public String getCurrencyCountryCodeStr() {
        return currencyCountryCodeStr;
    }

    public void setCurrencyCountryCodeStr(String currencyCountryCodeStr) {
        this.currencyCountryCodeStr = currencyCountryCodeStr;
    }

    public String getPlanTrialPeriodStr() {
        return planTrialPeriodStr;
    }

    public void setPlanTrialPeriodStr(String planTrialPeriodStr) {
        this.planTrialPeriodStr = planTrialPeriodStr;
    }

    public String getPlanTrialRecurrenceStr() {
        return planTrialRecurrenceStr;
    }

    public void setPlanTrialRecurrenceStr(String planTrialRecurrenceStr) {
        this.planTrialRecurrenceStr = planTrialRecurrenceStr;
    }

    private String planTrialPeriodStr;
    private String planTrialRecurrenceStr;

    public String getPlanCurrencyIdStr() {
        return planCurrencyIdStr;
    }

    public void setPlanCurrencyIdStr(String planCurrencyIdStr) {
        this.planCurrencyIdStr = planCurrencyIdStr;
    }

    public String getPlanCurrencySymbolstr() {
        return planCurrencySymbolstr;
    }

    public void setPlanCurrencySymbolstr(String planCurrencySymbolstr) {
        this.planCurrencySymbolstr = planCurrencySymbolstr;
    }

    public String getPlanLanguage_idStr() {
        return planLanguage_idStr;
    }

    public void setPlanLanguage_idStr(String planLanguage_idStr) {
        this.planLanguage_idStr = planLanguage_idStr;
    }


    public int getPlanStatusStr() {
        return planStatusStr;
    }

    public void setPlanStatusStr(int planStatusStr) {
        this.planStatusStr = planStatusStr;
    }


    public String getStudioIdStr() {
        return studioIdStr;
    }

    public void setStudioIdStr(String studioIdStr) {
        this.studioIdStr = studioIdStr;
    }

    public String getPlanIdStr() {
        return planIdStr;
    }

    public void setPlanIdStr(String planIdStr) {
        this.planIdStr = planIdStr;
    }

    private boolean isSelected;

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getPlanFrequencuStr() {
        return planFrequencuStr;
    }

    public void setPlanFrequencuStr(String planFrequencuStr) {
        this.planFrequencuStr = planFrequencuStr;
    }


    public String getPlanNameStr() {
        return planNameStr;
    }

    public void setPlanNameStr(String planNameStr) {
        this.planNameStr = planNameStr;
    }

    public String getPurchaseValueStr() {
        return purchaseValueStr;
    }

    public void setPurchaseValueStr(String purchaseValueStr) {
        this.purchaseValueStr = purchaseValueStr;
    }

    public String getPlanRecurrenceStr() {
        return planRecurrenceStr;
    }

    public void setPlanRecurrenceStr(String planRecurrenceStr) {
        this.planRecurrenceStr = planRecurrenceStr;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }
}
