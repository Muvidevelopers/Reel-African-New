package com.release.reelAfrican.model;

/**
 * Created by Muvi on 10/12/2016.
 */
public class CardModel {
    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public CardModel(String cardId, String cardNumber) {
        this.cardId = cardId;
        this.cardNumber = cardNumber;
    }

    String cardId;
    String cardNumber;
}
