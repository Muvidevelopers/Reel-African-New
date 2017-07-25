package com.release.reelAfrican.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.release.reelAfrican.R;
import com.release.reelAfrican.utils.ProgressBarHandler;
import com.release.reelAfrican.utils.Util;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class TransactionDetailsActivity extends AppCompatActivity {
    Toolbar mActionBarToolbar;
    TextView transactionTitleTextView;
    LinearLayout transactionDateLayout, transactionOrderLayout, transactionAmountLayout, transactionInvoiceLayout,
            transactionStatusLayout, transactionPLanNameLayout;
    TextView transactionDateTitleTextView, transactionOrderTitletextView, transactionAmountTitletextView, transactionInvoiceTitletextView,
            transactionStatusTitletextView, transactionPlanNameTitleTextView;
    TextView transactionDateTextView, transactionOrdertextView, transactionAmounttextView, transactionInvoicetextView,
            transactionStatustextView, transactionPlanNameTextView;
    Button transactionDownloadButton;
    boolean network = false;

    int corePoolSize = 60;
    int maximumPoolSize = 80;
    int keepAliveTime = 10;
    BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<Runnable>(maximumPoolSize);
    Executor threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, workQueue);
    ProgressDialog progressDialog;
    ProgressDialog pDialog;
    String filename;
    int progress_bar_type = 0;
    int progressStatus = 0;

    String id, user_id;
    RelativeLayout noInternet;
    LinearLayout primary_layout;
    Button tryAgainButton;

    String TransactionDate, OredrId, Amount, Invoice, TransactionStatus, PlanName;

    String Download_Url;
    boolean deletion_success = false;
    AlertDialog msgAlert;
    private String Currency_symbol;
    private String currency_code;
    static File mediaStorageDir;
    TextView no_internet_text;
    ProgressBarHandler Ph;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_details);
        mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(mActionBarToolbar);
        mActionBarToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back));
        mActionBarToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mActionBarToolbar.setTitle("");



        noInternet = (RelativeLayout) findViewById(R.id.noInternet);
        primary_layout = (LinearLayout) findViewById(R.id.primary_layout);
        tryAgainButton = (Button) findViewById(R.id.tryAgainButton);
        no_internet_text = (TextView) findViewById(R.id.no_internet_text);

        no_internet_text.setText(Util.getTextofLanguage(TransactionDetailsActivity.this,Util.NO_INTERNET_NO_DATA,Util.DEFAULT_NO_INTERNET_NO_DATA));
        tryAgainButton.setText(Util.getTextofLanguage(TransactionDetailsActivity.this,Util.TRY_AGAIN,Util.DEFAULT_TRY_AGAIN));


        transactionDateLayout = (LinearLayout) findViewById(R.id.transactionDateLayout);
        transactionOrderLayout = (LinearLayout) findViewById(R.id.transactionOrderLayout);
        transactionAmountLayout = (LinearLayout) findViewById(R.id.transactionAmountLayout);
        transactionInvoiceLayout = (LinearLayout) findViewById(R.id.transactionInvoiceLayout);
        transactionStatusLayout = (LinearLayout) findViewById(R.id.transactionStatusLayout);
        transactionPLanNameLayout = (LinearLayout) findViewById(R.id.transactionPLanNameLayout);




        transactionDateTitleTextView = (TextView) findViewById(R.id.transactionDateTitleTextView);
        Typeface typeface = Typeface.createFromAsset(getAssets(),getResources().getString(R.string.regular_fonts));
        transactionDateTitleTextView.setTypeface(typeface);
        transactionDateTitleTextView.setText(Util.getTextofLanguage(TransactionDetailsActivity.this,Util.TRANSACTION_DATE,Util.DEFAULT_TRANSACTION_DATE)+" :");

        transactionOrderTitletextView = (TextView) findViewById(R.id.transactionOrderTitletextView);
        Typeface typeface1 = Typeface.createFromAsset(getAssets(),getResources().getString(R.string.regular_fonts));
        transactionOrderTitletextView.setTypeface(typeface1);
        transactionOrderTitletextView.setText(Util.getTextofLanguage(TransactionDetailsActivity.this,Util.ORDER,Util.DEFAULT_ORDER)+" :");

        transactionAmountTitletextView = (TextView) findViewById(R.id.transactionAmountTitletextView);
        Typeface typeface2 = Typeface.createFromAsset(getAssets(),getResources().getString(R.string.regular_fonts));
        transactionAmountTitletextView.setTypeface(typeface2);
        transactionAmountTitletextView.setText(Util.getTextofLanguage(TransactionDetailsActivity.this,Util.AMOUNT,Util.DEFAULT_AMOUNT)+" :");


        transactionInvoiceTitletextView = (TextView) findViewById(R.id.transactionInvoiceTitletextView);
        Typeface typeface3 = Typeface.createFromAsset(getAssets(),getResources().getString(R.string.regular_fonts));
        transactionInvoiceTitletextView.setTypeface(typeface3);
        transactionInvoiceTitletextView.setText(Util.getTextofLanguage(TransactionDetailsActivity.this,Util.INVOICE,Util.DEFAULT_INVOICE)+" :");

        transactionStatusTitletextView = (TextView) findViewById(R.id.transactionStatusTitletextView);
        Typeface typeface4 = Typeface.createFromAsset(getAssets(),getResources().getString(R.string.regular_fonts));
        transactionStatusTitletextView.setTypeface(typeface4);
        transactionStatusTitletextView.setText(Util.getTextofLanguage(TransactionDetailsActivity.this,Util.TRANSACTION_STATUS,Util.DEFAULT_TRANSACTION_STATUS)+" :");

        transactionPlanNameTitleTextView = (TextView) findViewById(R.id.transactionPlanNameTitleTextView);
        Typeface typeface5 = Typeface.createFromAsset(getAssets(),getResources().getString(R.string.regular_fonts));
        transactionPlanNameTitleTextView.setTypeface(typeface5);
        transactionPlanNameTitleTextView.setText(Util.getTextofLanguage(TransactionDetailsActivity.this,Util.PLAN_NAME,Util.DEFAULT_PLAN_NAME)+" :");

        transactionTitleTextView = (TextView) findViewById(R.id.transactionTitleTextView);
        Typeface typeface6 = Typeface.createFromAsset(getAssets(),getResources().getString(R.string.regular_fonts));
        transactionTitleTextView.setTypeface(typeface6);
        transactionTitleTextView.setText(Util.getTextofLanguage(TransactionDetailsActivity.this,Util.TRANASCTION_DETAIL,Util.DEFAULT_TRANASCTION_DETAIL));

        transactionDownloadButton = (Button) findViewById(R.id.transactionDownloadButton);
        Typeface typeface7 = Typeface.createFromAsset(getAssets(),getResources().getString(R.string.regular_fonts));
        transactionDownloadButton.setTypeface(typeface7);
        transactionDownloadButton.setText(Util.getTextofLanguage(TransactionDetailsActivity.this,Util.DOWNLOAD_BUTTON_TITLE,Util.DEFAULT_DOWNLOAD_BUTTON_TITLE));

        Typeface typeface8 = Typeface.createFromAsset(getAssets(),getResources().getString(R.string.light_fonts));

        transactionDateTextView = (TextView) findViewById(R.id.transactionDateTextView);
        transactionOrdertextView = (TextView) findViewById(R.id.transactionOrdertextView);
        transactionAmounttextView = (TextView) findViewById(R.id.transactionAmounttextView);
        transactionInvoicetextView = (TextView) findViewById(R.id.transactionInvoicetextView);
        transactionStatustextView = (TextView) findViewById(R.id.transactionStatustextView);
        transactionPlanNameTextView = (TextView) findViewById(R.id.transactionPlanNameTextView);

        transactionDateTextView.setTypeface(typeface8);
        transactionOrdertextView.setTypeface(typeface8);
        transactionAmounttextView.setTypeface(typeface8);
        transactionInvoicetextView.setTypeface(typeface8);
        transactionStatustextView.setTypeface(typeface8);
        transactionPlanNameTextView.setTypeface(typeface8);

        network = Util.checkNetwork(TransactionDetailsActivity.this);
        id = getIntent().getStringExtra("id");
        user_id = getIntent().getStringExtra("user_id");

        // Calling Api To get Transaction Details.

        if (Util.checkNetwork(TransactionDetailsActivity.this))
            GetPurchaseHistoryDetails();
        else {
            noInternet.setVisibility(View.VISIBLE);
            primary_layout.setVisibility(View.GONE);
        }

        tryAgainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Util.checkNetwork(TransactionDetailsActivity.this))
                    GetPurchaseHistoryDetails();
                else {
                    noInternet.setVisibility(View.VISIBLE);
                    primary_layout.setVisibility(View.GONE);
                }
            }
        });

        transactionDownloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                if (ContextCompat.checkSelfPermission(TransactionDetailsActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(TransactionDetailsActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        ActivityCompat.requestPermissions(TransactionDetailsActivity.this,
                                new String[]{Manifest.permission
                                        .WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_CONTACTS},
                                111);
                    } else {
                        ActivityCompat.requestPermissions(TransactionDetailsActivity.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                111);
                    }
                } else {
                    //Call whatever you want
                    if (Util.checkNetwork(TransactionDetailsActivity.this)) {


                        DownloadDocumentDetails downloadDocumentDetails = new DownloadDocumentDetails();
                        downloadDocumentDetails.executeOnExecutor(threadPoolExecutor);



                       // Toast.makeText(getApplicationContext(),Util.getTextofLanguage(TransactionDetailsActivity.this,Util.NO_PDF,Util.DEFAULT_NO_PDF), Toast.LENGTH_LONG).show();
                    } else {
                        Util.showToast(getApplicationContext(),Util.getTextofLanguage(getApplicationContext(),Util.NO_INTERNET_CONNECTION,Util.DEFAULT_NO_INTERNET_CONNECTION));

//                        Toast.makeText(getApplicationContext(),Util.getTextofLanguage(TransactionDetailsActivity.this,Util.NO_INTERNET_CONNECTION,Util.DEFAULT_NO_INTERNET_CONNECTION), Toast.LENGTH_LONG).show();
                    }
                }




            }
        });

    }

    public void DownloadTransactionDetails() {

        registerReceiver(InternetStatus, new IntentFilter("android.net.wifi.STATE_CHANGE"));
        new DownloadFileFromURL().execute(Util.Dwonload_pdf_rootUrl+Download_Url);

        Log.v("SUBHA","Url="+Util.Dwonload_pdf_rootUrl+Download_Url);

    }

    private BroadcastReceiver InternetStatus = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Extract data included in the Intent
            //  Toast.makeText(getApplicationContext(),""+Util.checkNetwork(TransactionDetailsActivity.this),Toast.LENGTH_SHORT).show();
            if (!Util.checkNetwork(TransactionDetailsActivity.this)) {
                if (pDialog.isShowing() && pDialog != null) {

                    showDialog(Util.getTextofLanguage(TransactionDetailsActivity.this,Util.DOWNLOAD_INTERRUPTED,Util.DEFAULT_DOWNLOAD_INTERRUPTED), 0);
                    unregisterReceiver(InternetStatus);
                    pDialog.setProgress(0);
                    progressStatus = 0;
                    dismissDialog(progress_bar_type);
                }
            }
        }
    };

    class DownloadFileFromURL extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread
         * Show Progress Bar Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog(progress_bar_type);
        }

        /**
         * Downloading file in background thread
         */
        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        protected String doInBackground(String... f_url) {
            int count;

            try {
                URL url = new URL(f_url[0]);
                String str = f_url[0];
                filename = str.substring(str.lastIndexOf("/") + 1);
                URLConnection conection = url.openConnection();
                conection.connect();
                // this will be useful so that you can show a tipical 0-100% progress bar
                int lenghtOfFile = conection.getContentLength();

                // download the file
                InputStream input = new BufferedInputStream(url.openStream(), 8192);
                mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "");

                if (!mediaStorageDir.exists()) {
                    if (!mediaStorageDir.mkdirs()) {
                        Log.d("App", "failed to create directory");
                    }
                }
                // Output stream


                OutputStream output = new FileOutputStream(mediaStorageDir + "/" + filename);
                byte data[] = new byte[1024];
                long total = 0;
                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress("" + (int) ((total * 100) / lenghtOfFile));
                    // Log.v("SUBHA", "Lrngth" + data.length);
                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }

            return null;
        }

        /**
         * Updating progress bar
         */
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            pDialog.setProgress(Integer.parseInt(progress[0]));

            if ((Integer.parseInt(progress[0])) == 100) {

                showDialog(Util.getTextofLanguage(TransactionDetailsActivity.this,Util.DOWNLOAD_COMPLETED,Util.DEFAULT_DOWNLOAD_COMPLETED), 1);

                unregisterReceiver(InternetStatus);
                pDialog.setProgress(0);
                progressStatus = 0;
                dismissDialog(progress_bar_type);

                //Calling Api To Delete Pdf file from the Server.


            }

        }

        /**
         * After completing background task
         * Dismiss the progress dialog
         **/
        @Override
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after the file was downloaded

            Log.v("SUBHA","Download Completed");

        }
    }


    //Asyntask to get Transaction Details.

    private class Deletepdf extends AsyncTask<Void, Void, Void> {

        String responseStr = "";
        int status;

        @Override
        protected Void doInBackground(Void... params) {

            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(Util.rootUrl().trim() + Util.DeleteInvoicePath.trim());//hv to cahnge
                httppost.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");
                httppost.addHeader("authToken", Util.authTokenStr);
                httppost.addHeader("filepath", Download_Url);
                httppost.addHeader("lang_code",Util.getTextofLanguage(TransactionDetailsActivity.this,Util.SELECTED_LANGUAGE_CODE,Util.DEFAULT_SELECTED_LANGUAGE_CODE));

                // Execute HTTP Post Request
                try {
                    HttpResponse response = httpclient.execute(httppost);
                    responseStr = EntityUtils.toString(response.getEntity());

                    Log.v("SUBHA", "responseStr Delete Invoice Path=" + responseStr);
                } catch (Exception e) {

                }

                JSONObject myJson = null;
                if (responseStr != null) {
                    myJson = new JSONObject(responseStr);
                    status = Integer.parseInt(myJson.optString("code"));
                }
                if (status > 0) {
                    if (status == 200) {

                        deletion_success = true;

                    } else {
                        deletion_success = false;
                    }
                } else {
                    deletion_success = false;

                }
            } catch (final JSONException e1) {
                deletion_success = false;
            } catch (Exception e) {
                deletion_success = false;
            }
            return null;
        }

        protected void onPostExecute(Void result) {

            try {
                if (Ph.isShowing())
                    Ph.hide();
            } catch (IllegalArgumentException ex) {

                deletion_success = false;
            }
            if (responseStr == null)
                deletion_success = false;

            if (deletion_success) {
                // Do whatever u want to do
                finish();
            }
        }

        @Override
        protected void onPreExecute() {

            Ph = new ProgressBarHandler(TransactionDetailsActivity.this);
            Ph.show();

        }
    }


    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case 0: // we set this to 0
                pDialog = new ProgressDialog(this);
                pDialog.setMessage("Downloading file. Please wait...");
                pDialog.setIndeterminate(false);
                pDialog.setMax(100);
                pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                pDialog.setCancelable(false);
                pDialog.show();
                progressStatus = 0;
                return pDialog;
            default:
                return null;
        }
    }


    public void GetPurchaseHistoryDetails() {
        noInternet.setVisibility(View.GONE);
        primary_layout.setVisibility(View.VISIBLE);

        AsynGetTransactionDetails asynGetTransactionDetails = new AsynGetTransactionDetails();
        asynGetTransactionDetails.executeOnExecutor(threadPoolExecutor);
    }


    //Asyntask to get Transaction Details.

    private class AsynGetTransactionDetails extends AsyncTask<Void, Void, Void> {

        String responseStr = "";
        int status;

        @Override
        protected Void doInBackground(Void... params) {

            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(Util.rootUrl().trim() + Util.PurchaseHistoryDetails.trim());
                httppost.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");
                httppost.addHeader("authToken", Util.authTokenStr);
                httppost.addHeader("user_id", user_id);
                httppost.addHeader("id", id);
                httppost.addHeader("lang_code",Util.getTextofLanguage(TransactionDetailsActivity.this,Util.SELECTED_LANGUAGE_CODE,Util.DEFAULT_SELECTED_LANGUAGE_CODE));



                // Execute HTTP Post Request
                try {
                    HttpResponse response = httpclient.execute(httppost);
                    responseStr = EntityUtils.toString(response.getEntity());

                    Log.v("SUBHA", "responseStr transcation Details=" + responseStr);
                } catch (Exception e) {

                }

                JSONObject myJson = null;
                JSONObject myJson1 = null;
                if (responseStr != null) {
                    myJson = new JSONObject(responseStr);
                    status = Integer.parseInt(myJson.optString("code"));
                    myJson1 = myJson.getJSONObject("section");

//                    TransactionDate,OredrId,Amount,Invoice,TransactionStatus,PlanName;
                }
                if (status > 0) {
                    if (status == 200) {


                        OredrId = myJson1.optString("order_number");
                        if (OredrId.equals("") || OredrId == null || OredrId.equals("null"))
                            OredrId = "";

                        Invoice = myJson1.optString("invoice_id");
                        if (Invoice.equals("") || Invoice == null || Invoice.equals("null"))
                            Invoice = "";

                        TransactionDate = myJson1.optString("transaction_date");
                        if (TransactionDate.equals("") || TransactionDate == null || TransactionDate.equals("null"))
                            TransactionDate = "";


                        if(!TransactionDate.equals(""))
                        {
                            String date=TransactionDate.trim();
                            SimpleDateFormat spf=new SimpleDateFormat("MMMM dd,yyyy hh:mm:ss aaa");
                            Date newDate=spf.parse(date);
                            spf= new SimpleDateFormat("MMMM dd,yyyy");
                            date = spf.format(newDate);
                            Log.v("SUBHA","Transaction Date = "+date);
                        }



                        TransactionStatus = myJson1.optString("transaction_status");
                        if (TransactionStatus.equals("") || TransactionStatus == null || TransactionStatus.equals("null"))
                            TransactionStatus = "";

                        PlanName = myJson1.optString("plan_name");
                        if (PlanName.equals("") || PlanName == null || PlanName.equals("null"))
                            PlanName = "";

                        Currency_symbol = myJson1.optString("currency_symbol");
                        if (Currency_symbol.equals("") || Currency_symbol == null || Currency_symbol.equals("null"))
                            Currency_symbol = "";

                        Log.v("SUBHA", "currency_symbol = " + Currency_symbol);

                        currency_code = myJson1.optString("currency_code");
                        if (currency_code.equals("") || currency_code == null || currency_code.equals("null"))
                            currency_code = "";

                        Log.v("SUBHA", "currency_code = " + currency_code);


                        Amount = myJson1.optString("amount");
                        if (Amount.equals("") || Amount == null || Amount.equals("null"))
                            Amount = "";
                        else {

                            if (Currency_symbol.equals("") || Currency_symbol == null || Currency_symbol.trim().equals(null)) {
                                Amount = currency_code + " " + Amount;
                            } else {
                                Amount = Currency_symbol + " " + Amount;
                            }
                        }

                        Log.v("SUBHA", "amount" + Amount);


                    } else {
                        responseStr = "0";
                    }
                } else {
                    responseStr = "0";

                }
            } catch (final JSONException e1) {
                responseStr = "0";
            } catch (Exception e) {
                responseStr = "0";
            }
            return null;

        }

        protected void onPostExecute(Void result) {


            if (Ph.isShowing() && Ph!=null)
                Ph.hide();


          /*  try{
                if( progressDialog.isShowing())
                    progressDialog.dismiss();
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
            }*/
            if (responseStr == null)
                responseStr = "0";

            if ((responseStr.trim().equals("0"))) {
                primary_layout.setVisibility(View.GONE);
                noInternet.setVisibility(View.VISIBLE);

                if (Ph.isShowing())
                    Ph.hide();

            } else {
                transactionDateTextView.setText(TransactionDate);
                transactionOrdertextView.setText(OredrId);
                transactionAmounttextView.setText(Amount);
                transactionInvoicetextView.setText(Invoice);
                transactionStatustextView.setText(TransactionStatus);
                transactionPlanNameTextView.setText(PlanName);

               /* DownloadDocumentDetails downloadDocumentDetails = new DownloadDocumentDetails();
                downloadDocumentDetails.executeOnExecutor(threadPoolExecutor);*/

            }
        }

        @Override
        protected void onPreExecute() {

            Ph = new ProgressBarHandler(TransactionDetailsActivity.this);
            Ph.show();
        }
    }


    //Asyntask to get Transaction Details.

    private class DownloadDocumentDetails extends AsyncTask<Void, Void, Void> {

        String responseStr = "";
        int status;

        @Override
        protected Void doInBackground(Void... params) {

            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(Util.rootUrl().trim() + Util.GetInvoicePDF.trim());
                httppost.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");
                httppost.addHeader("authToken", Util.authTokenStr);
                httppost.addHeader("user_id", user_id);
                httppost.addHeader("id", id);
                httppost.addHeader("device_type", "app");
                httppost.addHeader("lang_code",Util.getTextofLanguage(TransactionDetailsActivity.this,Util.SELECTED_LANGUAGE_CODE,Util.DEFAULT_SELECTED_LANGUAGE_CODE));



                // Execute HTTP Post Request
                try {
                    HttpResponse response = httpclient.execute(httppost);
                    responseStr = EntityUtils.toString(response.getEntity());
                    Log.v("SUBHA", "responseStr getpdf Details=" + responseStr);
                } catch (Exception e) {

                }

                JSONObject myJson = null;
                if (responseStr != null) {
                    myJson = new JSONObject(responseStr);
                    status = Integer.parseInt(myJson.optString("code"));
                }
                if (status > 0) {
                    if (status == 200) {


                        Download_Url = myJson.optString("section");
                        if (Download_Url.equals("") || Download_Url == null || Download_Url.equals("null")) {

                            Download_Url = "";
                        }
                    } else {
                        responseStr = "0";
                    }
                } else {
                    responseStr = "0";

                }
            } catch (final JSONException e1) {
                responseStr = "0";
            } catch (Exception e) {
                responseStr = "0";
            }
            return null;

        }

        protected void onPostExecute(Void result) {

            try {
                if (Ph.isShowing())
                    Ph.hide();
            } catch (IllegalArgumentException ex) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        primary_layout.setVisibility(View.GONE);
                        noInternet.setVisibility(View.VISIBLE);

                    }

                });
                responseStr = "0";
            }
            if (responseStr == null)
                responseStr = "0";

            if ((responseStr.trim().equals("0"))) {
                primary_layout.setVisibility(View.GONE);
                noInternet.setVisibility(View.VISIBLE);
            } else {

                if (!Download_Url.equals(""))
                    DownloadTransactionDetails();
                else
                    Util.showToast(getApplicationContext(),Util.getTextofLanguage(getApplicationContext(),Util.NO_PDF,Util.DEFAULT_NO_PDF));

            }
        }

        @Override
        protected void onPreExecute() {

            Ph = new ProgressBarHandler(TransactionDetailsActivity.this);
            Ph.show();

            /*progressDialog = new ProgressDialog(TransactionDetailsActivity.this,R.style.MyTheme);
            progressDialog.setCancelable(false);
            progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Large_Inverse);
            progressDialog.setIndeterminate(false);
            progressDialog.setIndeterminateDrawable(getResources().getDrawable(R.drawable.progress_rawable));
            progressDialog.show()*/
            ;
        }
    }

    public void showDialog(String msg, final int deletevalue) {
        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(TransactionDetailsActivity.this,R.style.MyAlertDialogStyle);
        dlgAlert.setMessage(msg);
        dlgAlert.setPositiveButton(Util.getTextofLanguage(TransactionDetailsActivity.this,Util.BUTTON_OK,Util.DEFAULT_BUTTON_OK), null);
        dlgAlert.setCancelable(false);
        dlgAlert.setPositiveButton(Util.getTextofLanguage(TransactionDetailsActivity.this,Util.BUTTON_OK,Util.DEFAULT_BUTTON_OK),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();

                        if (deletevalue == 1) {
                            Deletepdf deletepdf = new Deletepdf();
                            deletepdf.executeOnExecutor(threadPoolExecutor);
                        }
                    }
                });
        dlgAlert.create();
        msgAlert = dlgAlert.show();
    }

    @Override
    public void onBackPressed() {

        finish();
        overridePendingTransition(0, 0);
        super.onBackPressed();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case 111: {

                if(grantResults.length>0)
                {
                    if ((grantResults.length > 0) && (grantResults[0]) == PackageManager.PERMISSION_GRANTED) {
                        //Call whatever you want
                        if (Util.checkNetwork(TransactionDetailsActivity.this)) {
                            DownloadDocumentDetails downloadDocumentDetails = new DownloadDocumentDetails();
                            downloadDocumentDetails.executeOnExecutor(threadPoolExecutor);
                        } else {
                            Util.showToast(getApplicationContext(),Util.getTextofLanguage(getApplicationContext(),Util.NO_INTERNET_CONNECTION,Util.DEFAULT_NO_INTERNET_CONNECTION));
//                            Toast.makeText(getApplicationContext(),Util.getTextofLanguage(TransactionDetailsActivity.this,Util.NO_INTERNET_CONNECTION,Util.DEFAULT_NO_INTERNET_CONNECTION), Toast.LENGTH_LONG).show();
                        }
                    } else {
                        finish();
                    }
                }
                else
                {
                    finish();
                }

                return;
            }
        }
    }
}
