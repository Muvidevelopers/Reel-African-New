package com.release.reelAfrican.model;

import java.io.Serializable;

/**
 * Created by Muvi on 03-Feb-17.
 */

public class CurrencyModel implements Serializable {

    String currencyId;
    String currencyCode;
    String currencySymbol;

    public String getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(String currencyId) {
        this.currencyId = currencyId;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getCurrencySymbol() {
        return currencySymbol;
    }

    public void setCurrencySymbol(String currencySymbol) {
        this.currencySymbol = currencySymbol;
    }




}
