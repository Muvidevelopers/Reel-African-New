package com.release.reelAfrican.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.release.reelAfrican.R;
import com.release.reelAfrican.model.PurchaseHistoryModel;
import com.release.reelAfrican.utils.Util;

import java.util.ArrayList;


public class PurchaseHistoryAdapter extends RecyclerView.Adapter<PurchaseHistoryAdapter.ViewHolder>{
    Context context;
    ArrayList<PurchaseHistoryModel> purchaseData;


    public PurchaseHistoryAdapter(Context context, ArrayList<PurchaseHistoryModel> purchaseData) {
        this.context = context;
        this.purchaseData = purchaseData;
    }

    @Override
    public PurchaseHistoryAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_purchase_history, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PurchaseHistoryAdapter.ViewHolder holder, int position){

        int POSITION = position;

        Typeface typeface = Typeface.createFromAsset(context.getAssets(),context.getResources().getString(R.string.regular_fonts));
        holder.transactionTitleTextView.setTypeface(typeface);
        holder.transactionTitleTextView.setText(Util.getTextofLanguage(context,Util.TRANSACTION_TITLE,Util.DEFAULT_TRANSACTION_TITLE));

        Typeface typeface1 = Typeface.createFromAsset(context.getAssets(),context.getResources().getString(R.string.regular_fonts));
        holder.transactionInvoiceTitleTextView.setTypeface(typeface1);
        holder.transactionInvoiceTitleTextView.setText(Util.getTextofLanguage(context,Util.INVOICE,Util.DEFAULT_INVOICE)+" :");

        Typeface typeface2 = Typeface.createFromAsset(context.getAssets(),context.getResources().getString(R.string.regular_fonts));
        holder.transactionOrderTitleTextView.setTypeface(typeface2);
        holder.transactionOrderTitleTextView.setText(Util.getTextofLanguage(context,Util.TRANSACTION_ORDER_ID,Util.DEFAULT_TRANSACTION_ORDER_ID)+" :");

        Typeface typeface3 = Typeface.createFromAsset(context.getAssets(),context.getResources().getString(R.string.regular_fonts));
        holder.transactionPurchaseDateTitleTextView.setTypeface(typeface3);
        holder.transactionPurchaseDateTitleTextView.setText(Util.getTextofLanguage(context,Util.TRANSACTION_DETAIL_PURCHASE_DATE,Util.DEFAULT_TRANSACTION_DETAIL_PURCHASE_DATE)+" :");

        if((purchaseData.get(position).getTransctionActiveInactive().contains("Active")) ||(purchaseData.get(position).getTransctionActiveInactive().contains("active")))
        {
            holder.activeAlertTextView.setTextColor(Color.parseColor("#197b30"));
            holder.activeAlertTextView.setText(purchaseData.get(position).getTransctionActiveInactive());
        }
        else
        {
            if(purchaseData.get(position).getTransctionActiveInactive().contains("N/A"))
            {
                holder.activeAlertTextView.setText("Expired");
            }
            else
            {
                holder.activeAlertTextView.setText(purchaseData.get(position).getTransctionActiveInactive());
            }

            holder.activeAlertTextView.setTextColor(Color.parseColor("#737373"));
        }


        holder.transactionInvoicetextView.setTypeface(typeface3);
        holder.transactionOrderTextView.setTypeface(typeface3);
        holder.transactionPurchaseDateTextView.setTypeface(typeface3);
        holder.showPriceTextView.setTypeface(typeface3);
        holder.successTextView.setTypeface(typeface3);

        holder.transactionInvoicetextView.setText(purchaseData.get(position).getInvoice());
        holder.transactionOrderTextView.setText(purchaseData.get(position).getOrderId());
        holder.transactionPurchaseDateTextView.setText(purchaseData.get(position).getPurchaseDate());
        holder.showPriceTextView.setText(purchaseData.get(position).getAmount());
        holder.successTextView.setText(purchaseData.get(position).getTransactionStatus());

    }

    @Override
    public int getItemCount() {
        return purchaseData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView activeAlertTextView,transactionInvoicetextView,transactionOrderTextView,transactionPurchaseDateTextView,
                showPriceTextView,successTextView;

        public TextView transactionTitleTextView,transactionInvoiceTitleTextView,transactionOrderTitleTextView,transactionPurchaseDateTitleTextView;
        public ViewHolder(View v){

            super(v);
            transactionTitleTextView = (TextView)v. findViewById(R.id.transactionTitleTextView);
            transactionInvoiceTitleTextView = (TextView)v. findViewById(R.id.transactionInvoiceTitleTextView);
            transactionOrderTitleTextView = (TextView)v. findViewById(R.id.transactionOrderTitleTextView);
            transactionPurchaseDateTitleTextView = (TextView)v. findViewById(R.id.transactionPurchaseDateTitleTextView);


            activeAlertTextView = (TextView)v. findViewById(R.id.activeAlertTextView);
            transactionInvoicetextView = (TextView)v. findViewById(R.id.transactionInvoice);
            transactionOrderTextView = (TextView)v. findViewById(R.id.transactionOrderTextView);
            transactionPurchaseDateTextView = (TextView)v. findViewById(R.id.transactionPurchaseDateTextView);
            showPriceTextView = (TextView)v. findViewById(R.id.showPriceTextView);
            successTextView = (TextView)v. findViewById(R.id.successTextView);
        }
    }

}