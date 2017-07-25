package com.release.reelAfrican.model;

/**
 * Created by MUVI on 2/7/2017.
 */

public class PurchaseHistoryModel {

    String Invoice, OrderId, PurchaseDate, TransactionStatus, Amount, TransctionActiveInactive;

    public PurchaseHistoryModel(String invoiec, String orderId, String purchaseDate, String transactionStatus, String amount, String transctionActiveInactive) {
        Invoice = invoiec;
        OrderId = orderId;
        PurchaseDate = purchaseDate;
        TransactionStatus = transactionStatus;
        Amount = amount;
        TransctionActiveInactive = transctionActiveInactive;
    }

    public void setInvoice(String Invoice) {
        this.Invoice = Invoice;
    }

    public String getInvoice() {
        return Invoice;
    }

    public void setOrderId(String OrderId) {
        this.OrderId = OrderId;
    }

    public String getOrderId() {
        return OrderId;
    }

    public void setPurchaseDate(String PurchaseDate) {
        this.PurchaseDate = PurchaseDate;
    }

    public String getPurchaseDate() {
        return PurchaseDate;
    }

    public void setTransactionStatus(String TransactionStatus) {
        this.TransactionStatus = TransactionStatus;
    }

    public String getTransactionStatus() {
        return TransactionStatus;
    }

    public void setAmount(String Amount) {
        this.Amount = Amount;
    }

    public String getAmount() {
        return Amount;
    }

    public void setTransctionActiveInactive(String TransctionActiveInactive) {
        this.TransctionActiveInactive = TransctionActiveInactive;
    }

    public String getTransctionActiveInactive() {
        return TransctionActiveInactive;
    }
}
