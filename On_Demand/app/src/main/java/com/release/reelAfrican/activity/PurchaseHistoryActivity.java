package com.release.reelAfrican.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.release.reelAfrican.R;
import com.release.reelAfrican.adapter.PurchaseHistoryAdapter;
import com.release.reelAfrican.model.PurchaseHistoryModel;
import com.release.reelAfrican.model.RecyclerItemClickListener;
import com.release.reelAfrican.utils.ProgressBarHandler;
import com.release.reelAfrican.utils.Util;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import io.fabric.sdk.android.Fabric;

public class PurchaseHistoryActivity extends AppCompatActivity {
    Toolbar mActionBarToolbar;
    RecyclerView recyclerView;
    ArrayList<PurchaseHistoryModel> purchaseData = new ArrayList<PurchaseHistoryModel>();
    RelativeLayout noInternet;
    RelativeLayout noData;
    LinearLayout primary_layout;
    Button tryAgainButton;
    boolean isNetwork;

    int corePoolSize = 60;
    int maximumPoolSize = 80;
    int keepAliveTime = 10;
    BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<Runnable>(maximumPoolSize);
    Executor threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, workQueue);

    String Invoice,Id,PutrcahseDate,TranactionStatus,Ppvstatus,Amount;
    private String Currency_symbol = "";
    private String currency_code  = "";
    String user_id = "";
    TextView purchaseHistoryTitleTextView,no_internet_text,noDataTextView;
    SharedPreferences pref;
    PurchaseHistoryModel purchaseHistoryModel;
    ArrayList<String> Id_Purchase_History;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());

        setContentView(R.layout.activity_purchase_history);

        noInternet = (RelativeLayout)findViewById(R.id.noInternet);
        noData = (RelativeLayout)findViewById(R.id.noData);
        primary_layout = (LinearLayout)findViewById(R.id.primary_layout);
        tryAgainButton = (Button)  findViewById(R.id.tryAgainButton);
        no_internet_text = (TextView)  findViewById(R.id.no_internet_text);
        noDataTextView = (TextView)  findViewById(R.id.noDataTextView);
        recyclerView = (RecyclerView) findViewById(R.id.purchase_history_recyclerview);
        purchaseHistoryTitleTextView = (TextView)findViewById(R.id.purchaseHistoryTitleTextView);

        no_internet_text.setText(Util.getTextofLanguage(PurchaseHistoryActivity.this,Util.NO_INTERNET_NO_DATA,Util.DEFAULT_NO_INTERNET_NO_DATA));
        tryAgainButton.setText(Util.getTextofLanguage(PurchaseHistoryActivity.this,Util.TRY_AGAIN,Util.DEFAULT_TRY_AGAIN));

        pref = getSharedPreferences(Util.LOGIN_PREF, 0);
        user_id = pref.getString("PREFS_LOGGEDIN_ID_KEY", null);


        Typeface typeface = Typeface.createFromAsset(getAssets(),getResources().getString(R.string.regular_fonts));
        purchaseHistoryTitleTextView.setTypeface(typeface);
        purchaseHistoryTitleTextView.setText(Util.getTextofLanguage(PurchaseHistoryActivity.this,Util.PURCHASE_HISTORY,Util.DEFAULT_PURCHASE_HISTORY
        ));


        mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mActionBarToolbar);
        mActionBarToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back));
        mActionBarToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        tryAgainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Util.checkNetwork(PurchaseHistoryActivity.this))
                    GetPurchaseHistoryDetails();
                else
                {
                    noInternet.setVisibility(View.VISIBLE);
                    primary_layout.setVisibility(View.GONE);
                }
            }
        });

        GetPurchaseHistoryDetails();


     /*   for(int i = 0 ;i<20 ;i++)
        {
            PurchaseHistoryModel purchaseHistoryModel = new PurchaseHistoryModel
                    ("Invoie Data","Order "+i,"12-10-20","Success","$299","Active");
            purchaseData.add(purchaseHistoryModel);
        }

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        PurchaseHistoryAdapter purchaseHistoryAdapter = new PurchaseHistoryAdapter(PurchaseHistoryActivity.this,purchaseData);
        recyclerView.setAdapter(purchaseHistoryAdapter);*/

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(PurchaseHistoryActivity.this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        // TODO Handle item click

                        final Intent detailsIntent = new Intent(PurchaseHistoryActivity.this, TransactionDetailsActivity.class);

                        detailsIntent.putExtra("id",Id_Purchase_History.get(position));
                        detailsIntent.putExtra("user_id",user_id);

                        Log.v("SUBHA","ID = "+Id_Purchase_History.get(position));
                        Log.v("SUBHA","user_id = "+user_id);

                        detailsIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(detailsIntent);
                    }
                })
        );

    }




    public void GetPurchaseHistoryDetails()
    {
        noInternet.setVisibility(View.GONE);
        primary_layout.setVisibility(View.VISIBLE);

        AsynGetPurchaseDetails asynGetPurchaseDetail = new AsynGetPurchaseDetails();
        asynGetPurchaseDetail.executeOnExecutor(threadPoolExecutor);
    }

    //Asyntask for getDetails of the csat and crew members.

    private class AsynGetPurchaseDetails extends AsyncTask<Void, Void, Void> {
        ProgressBarHandler pDialog;
        String responseStr = "";
        int status;

        @Override
        protected Void doInBackground(Void... params) {

            try {
                HttpClient httpclient=new DefaultHttpClient();
                HttpPost httppost = new HttpPost(Util.rootUrl().trim()+Util.PurchaseHistory.trim());
                httppost.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");
                httppost.addHeader("authToken",Util.authTokenStr);
                httppost.addHeader("user_id",user_id);
                httppost.addHeader("lang_code",Util.getTextofLanguage(PurchaseHistoryActivity.this,Util.SELECTED_LANGUAGE_CODE,Util.DEFAULT_SELECTED_LANGUAGE_CODE));


                // Execute HTTP Post Request
                try {
                    HttpResponse response = httpclient.execute(httppost);
                    responseStr = EntityUtils.toString(response.getEntity());

                } catch (Exception e){

                }

                JSONObject myJson =null;
                if(responseStr!=null){
                    myJson = new JSONObject(responseStr);
                    status = Integer.parseInt(myJson.optString("code"));
                }
                if (status > 0) {
                    if (status == 200) {
                        Id_Purchase_History = new ArrayList<>();
                        JSONArray jsonArray = myJson.getJSONArray("section");
                        for(int i=0 ;i<jsonArray.length();i++)
                        {
                            Invoice = jsonArray.getJSONObject(i).optString("invoice_id");
                            if(Invoice.equals("") || Invoice==null || Invoice.equals("null"))
                                Invoice = "";
                            Id = jsonArray.getJSONObject(i).optString("id");
                            if(Id.equals("") || Id==null || Id.equals("null"))
                                Id = "";

                            Id_Purchase_History.add(Id);
                            Log.v("SUBHA","ID =========================== "+Id);

                            PutrcahseDate = jsonArray.getJSONObject(i).optString("transaction_date");
                            if(PutrcahseDate.equals("") || PutrcahseDate==null || PutrcahseDate.equals("null"))
                                PutrcahseDate = "";

                            TranactionStatus = jsonArray.getJSONObject(i).optString("transaction_status");
                            if(TranactionStatus.equals("") || TranactionStatus==null || TranactionStatus.equals("null"))
                                TranactionStatus = "";

                            Ppvstatus = jsonArray.getJSONObject(i).optString("statusppv");
                            if(Ppvstatus.equals("") || Ppvstatus==null || Ppvstatus.equals("null"))
                                Ppvstatus = "";

                            Currency_symbol = (jsonArray.getJSONObject(i).optString("currency_symbol")).trim();
                            if(Currency_symbol.equals("") || Currency_symbol==null || Currency_symbol.equals("null"))
                                Currency_symbol = "";

                            Log.v("SUBHA","currency_symbol = "+Currency_symbol);

                            currency_code = jsonArray.getJSONObject(i).optString("currency_code");
                            if(currency_code.equals("") || currency_code==null || currency_code.equals("null"))
                                currency_code = "";

                            Log.v("SUBHA","currency_code = "+currency_code);


                            Amount = jsonArray.getJSONObject(i).optString("amount");
                            if(Amount.equals("") || Amount==null || Amount.equals("null"))
                                Amount = "";
                            else{

                                if(Currency_symbol.equals("") || Currency_symbol==null || Currency_symbol.trim().equals(null))
                                {
                                    Amount = currency_code+ " "+ Amount;
                                }
                                else
                                {
                                    Amount = Currency_symbol+ " "+ Amount;
                                }
                            }

                            Log.v("SUBHA","amount"+ Amount);


                            purchaseHistoryModel = new PurchaseHistoryModel(Invoice,Id,PutrcahseDate,TranactionStatus,Amount,Ppvstatus);
                            purchaseData.add(purchaseHistoryModel);

                        }



                    }else{  responseStr = "0";}
                }
                else{
                    responseStr = "0";

                }
            } catch (final JSONException e1) {
                responseStr = "0";
            }
            catch (Exception e)
            {
                responseStr = "0";
            }
            return null;

        }

        protected void onPostExecute(Void result) {

            try{
                if(pDialog.isShowing())
                    pDialog.hide();
            }
            catch(IllegalArgumentException ex)
            {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        primary_layout.setVisibility(View.GONE);
                        noInternet.setVisibility(View.VISIBLE);

                    }

                });
                responseStr = "0";
            }
            if(responseStr == null)
                responseStr = "0";

            if((responseStr.trim().equals("0"))){
                primary_layout.setVisibility(View.GONE);
                noInternet.setVisibility(View.VISIBLE);
            }else{
                if(purchaseData.size()>0){

                    // Set the recycler adapter here.
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                    recyclerView.setLayoutManager(layoutManager);
                    PurchaseHistoryAdapter purchaseHistoryAdapter = new PurchaseHistoryAdapter(PurchaseHistoryActivity.this,purchaseData);
                    recyclerView.setAdapter(purchaseHistoryAdapter);

                }
                else{

                    primary_layout.setVisibility(View.GONE);

                    noData.setVisibility(View.VISIBLE);
                    noDataTextView.setText(Util.getTextofLanguage(PurchaseHistoryActivity.this,Util.NO,Util.DEFAULT_NO) + "  "+ Util.getTextofLanguage(PurchaseHistoryActivity.this,Util.PURCHASE_HISTORY,Util.DEFAULT_PURCHASE_HISTORY) );

                }


            }
        }

        @Override
        protected void onPreExecute() {

            pDialog = new ProgressBarHandler(PurchaseHistoryActivity.this);
            pDialog.show();
        }
    }

    @Override
    public void onBackPressed()
    {
        finish();
        overridePendingTransition(0, 0);
        super.onBackPressed();
    }
}
