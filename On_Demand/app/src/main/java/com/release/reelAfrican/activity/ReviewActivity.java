package com.release.reelAfrican.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.release.reelAfrican.R;
import com.release.reelAfrican.adapter.ReviewsAdapter;
import com.release.reelAfrican.model.ReviewsItem;
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


public class ReviewActivity extends AppCompatActivity {

    Toolbar mActionBarToolbar;

    ArrayList<ReviewsItem> reviewsItem = new ArrayList<ReviewsItem>();
    ReviewsAdapter reviewsAdapter;
    GridView reviewsGridView;
    SharedPreferences pref;

    /* RelativeLayout noInternetLayout;
     RelativeLayout noDataLayout;
     TextView noDataTextView;
     TextView noInternetTextView;*/
    int isLogin = 0;

    //  LinearLayout primary_layout;
    boolean isNetwork;
    int showRating = 0;
    String  movie_id;
    TextView submitTitleTextView;
    EditText submitReviewTextView;
    RelativeLayout submitRatingLayout;
    Button submitButton;
    RatingBar addRatingBar;
    TextView clickHereToLogin;
    String reviewMessage = "";
    String ratingStr = "";
    int corePoolSize = 60;
    int maximumPoolSize = 80;
    int keepAliveTime = 10;
    BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<Runnable>(maximumPoolSize);
    Executor threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, workQueue);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        Fabric.with(this, new Crashlytics());

        SharedPreferences isLoginPref = getSharedPreferences(Util.IS_LOGIN_SHARED_PRE, 0); // 0 - for private mode

        isLogin = isLoginPref.getInt(Util.IS_LOGIN_PREF_KEY, 0);

        mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mActionBarToolbar);
        mActionBarToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back));
        mActionBarToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        pref = getSharedPreferences(Util.LOGIN_PREF, 0);

        submitRatingLayout = (RelativeLayout)findViewById(R.id.submitRatingLayout);
        clickHereToLogin = (TextView) findViewById(R.id.clickHereToLogin);
        submitTitleTextView = (TextView) findViewById(R.id.sectionTitle);
        submitReviewTextView = (EditText) findViewById(R.id.reviewEditText);
        submitButton = (Button) findViewById(R.id.submitReviewButton);
        addRatingBar = (RatingBar) findViewById(R.id.ratingBar);
        Typeface typeface = Typeface.createFromAsset(getAssets(),getResources().getString(R.string.light_fonts));
        submitButton.setTypeface(typeface);
        submitTitleTextView.setTypeface(typeface);
        submitTitleTextView.setText(Util.getTextofLanguage(ReviewActivity.this, Util.SUBMIT_YOUR_RATING_TITLE, Util.DEFAULT_SUBMIT_YOUR_RATING_TITLE));
        submitButton.setText(Util.getTextofLanguage(ReviewActivity.this, Util.BTN_POST_REVIEW, Util.DEFAULT_BTN_POST_REVIEW));

        submitReviewTextView.setHint(Util.getTextofLanguage(ReviewActivity.this, Util.ENTER_REVIEW_HERE, Util.DEFAULT_ENTER_REVIEW_HERE));
        String clickHereStr = Util.getTextofLanguage(ReviewActivity.this, Util.NEED_LOGIN_TO_REVIEW, Util.DEFAULT_NEED_LOGIN_TO_REVIEW) + " " + Util.getTextofLanguage(ReviewActivity.this, Util.CLICK_HERE, Util.DEFAULT_CLICK_HERE) + " "+ Util.getTextofLanguage(ReviewActivity.this, Util.TO_LOGIN, Util.DEFAULT_TO_LOGIN);

        SpannableString mySpannableString = new SpannableString(clickHereStr);
        mySpannableString.setSpan(new UnderlineSpan(), 0, mySpannableString.length(), 0);
        clickHereToLogin.setText(mySpannableString);
        clickHereToLogin.setVisibility(View.GONE);
        clickHereToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent registerActivity = new Intent(ReviewActivity.this, LoginActivity.class);
                runOnUiThread(new Runnable() {
                    public void run() {
                        registerActivity.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        registerActivity.putExtra("from", this.getClass().getName());
                        startActivity(registerActivity);

                    }
                });

            }
        });


        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reviewMessage = submitReviewTextView.getText().toString().trim();
                ratingStr = Float.toString(addRatingBar.getRating());
                AsynAddReviewDetails asynAddReviewDetails = new AsynAddReviewDetails();
                asynAddReviewDetails.executeOnExecutor(threadPoolExecutor);
            }
        });


        //  primary_layout = (LinearLayout)findViewById(R.id.primary_layout);
        reviewsGridView = (GridView) findViewById(R.id.reviewsList);
        isNetwork = Util.checkNetwork(ReviewActivity.this);
        reviewsGridView.setNumColumns(1);




       /* if (((getResources().getConfiguration().screenLayout & SCREENLAYOUT_SIZE_MASK) == SCREENLAYOUT_SIZE_LARGE) || ((getResources().getConfiguration().screenLayout & SCREENLAYOUT_SIZE_MASK) == SCREENLAYOUT_SIZE_XLARGE)) {
            reviewsGridView.setNumColumns(2);

        }else {
            //"Mobile";
            reviewsGridView.setNumColumns(1);
        }*/

        // GetReviewDetails();

    }

    @Override
    protected void onResume() {
        super.onResume();
        /*if(isLogin == 1) {
            if (pref != null) {
                Log.v("SUBHA","FHFH");
                String loggedInStr = pref.getString("PREFS_LOGGEDIN_KEY", null);
                if (loggedInStr == null) {
                    Log.v("SUBHA","loggedInStr");

                    clickHereToLogin.setVisibility(View.VISIBLE);
                    submitRatingLayout.setVisibility(View.GONE);
                }else{
                    Log.v("SUBHA","loggedInStr1");

                    clickHereToLogin.setVisibility(View.GONE);
                    submitRatingLayout.setVisibility(View.VISIBLE);
                }
            }else{
                Log.v("SUBHA","loggedInStr2");

                clickHereToLogin.setVisibility(View.VISIBLE);
                submitRatingLayout.setVisibility(View.GONE);
            }
        }else{
            Log.v("SUBHA","loggedInStr3");

            clickHereToLogin.setVisibility(View.GONE);
            submitRatingLayout.setVisibility(View.GONE);

        }*/
        GetReviewDetails();
    }

    public void GetReviewDetails()
    {

        AsynGetReviewDetails asynGetReviewDetails = new AsynGetReviewDetails();
        asynGetReviewDetails.executeOnExecutor(threadPoolExecutor);

    }
    //Asyntask for getDetails of the csat and crew members.

    private class AsynGetReviewDetails extends AsyncTask<Void, Void, Void> {
        ProgressBarHandler pDialog;
        String responseStr = "";
        int status;
        String msg;
        int reviewDisabled = 1;
        @Override
        protected Void doInBackground(Void... params) {

            try {


                HttpClient httpclient=new DefaultHttpClient();
                HttpPost httppost = new HttpPost(Util.rootUrl().trim()+Util.ViewContentRating.trim());
                httppost.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");
                httppost.addHeader("authToken", Util.authTokenStr.trim());
                httppost.addHeader("content_id",getIntent().getStringExtra("muviId"));
                if (pref != null) {
                    String loggedInStr = pref.getString("PREFS_LOGGEDIN_KEY", null);
                    if (loggedInStr == null) {

                    }else{
                        httppost.addHeader("user_id",pref.getString("PREFS_LOGGEDIN_ID_KEY", null));

                    }
                }
                httppost.addHeader("lang_code", Util.getTextofLanguage(ReviewActivity.this, Util.SELECTED_LANGUAGE_CODE, Util.DEFAULT_SELECTED_LANGUAGE_CODE));


                // Execute HTTP Post Request
                try {
                    HttpResponse response = httpclient.execute(httppost);
                    responseStr = EntityUtils.toString(response.getEntity());
                    Log.v("SUBHA","RESPO"+responseStr);
                    Log.v("SUBHA","RESPO"+getIntent().getStringExtra("muviId"));


                } catch (Exception e){

                }

                JSONObject myJson =null;
                JSONArray jsonArray =null;
                if(responseStr!=null){
                    myJson = new JSONObject(responseStr);
                    status = Integer.parseInt(myJson.optString("code"));
                    msg = myJson.optString("msg");
                    if ((myJson.has("showrating")) && myJson.optString("showrating").trim() != null && !myJson.optString("showrating").trim().isEmpty() && !myJson.optString("showrating").trim().equals("null") && !myJson.optString("showrating").trim().matches("")) {
                        showRating = Integer.parseInt(myJson.optString("showrating"));
                        Log.v("SUBHA","HFFH"+showRating);
                    }

                }

                if (status == 200) {
                    jsonArray = myJson.getJSONArray("rating");
                    if (reviewsItem!=null && reviewsItem.size() > 0){
                        reviewsItem.clear();
                    }
                    for (int i=0 ;i<jsonArray.length();i++)
                    {
                        String userName = "";
                        String rating = "0";
                        String review = "";
                        if ((jsonArray.getJSONObject(i).has("display_name")) && jsonArray.getJSONObject(i).optString("display_name").trim() != null && !jsonArray.getJSONObject(i).optString("display_name").trim().isEmpty() && !jsonArray.getJSONObject(i).optString("display_name").trim().equals("null") && !jsonArray.getJSONObject(i).optString("display_name").trim().matches("")) {
                            userName =jsonArray.getJSONObject(i).optString("display_name");

                        }
                        if ((jsonArray.getJSONObject(i).has("rating")) && jsonArray.getJSONObject(i).optString("rating").trim() != null && !jsonArray.getJSONObject(i).optString("rating").trim().isEmpty() && !jsonArray.getJSONObject(i).optString("rating").trim().equals("null") && !jsonArray.getJSONObject(i).optString("rating").trim().matches("")) {
                            rating =jsonArray.getJSONObject(i).optString("rating");

                        }
                        if ((jsonArray.getJSONObject(i).has("review")) && jsonArray.getJSONObject(i).optString("review").trim() != null && !jsonArray.getJSONObject(i).optString("display_name").trim().isEmpty() && !jsonArray.getJSONObject(i).optString("review").trim().equals("null") && !jsonArray.getJSONObject(i).optString("review").trim().matches("")) {
                            review =jsonArray.getJSONObject(i).optString("review");

                        }

                        if ((jsonArray.getJSONObject(i).has("status")) && jsonArray.getJSONObject(i).optString("status").trim() != null && !jsonArray.getJSONObject(i).optString("status").trim().isEmpty() && !jsonArray.getJSONObject(i).optString("status").trim().equals("null") && !jsonArray.getJSONObject(i).optString("status").trim().matches("")) {
                            reviewDisabled =Integer.parseInt(jsonArray.getJSONObject(i).optString("status"));

                        }
                        if (reviewDisabled == 1) {
                            ReviewsItem reviewItem = new ReviewsItem(review, userName, rating);
                            reviewsItem.add(reviewItem);
                        }
                    }
                    if ((myJson.has("showrating")) && myJson.optString("showrating").trim() != null && !myJson.optString("showrating").trim().isEmpty() && !myJson.optString("showrating").trim().equals("null") && !myJson.optString("showrating").trim().matches("")) {
                        showRating = Integer.parseInt(myJson.optString("showrating"));
                        Log.v("SUBHA","HFFH"+showRating);
                    }

                }else{
                    responseStr = "0";
                   /* if(status == 448)
                    {

                        // show dialog
                        responseStr = "1";
                    }
                    else
                    {
                        responseStr = "0";
                    }*/
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
                if (pDialog != null && pDialog.isShowing()) {
                    pDialog.hide();
                    pDialog = null;
                }

            }
            catch(IllegalArgumentException ex)
            {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {


                    }

                });
                responseStr = "0";
            }
            if(responseStr == null) {
                responseStr = "0";
            }

            if((responseStr.trim().equals("0"))){

            }else{
                if(isLogin == 1) {
                    if (pref != null) {
                        Log.v("SUBHA","FHFH");
                        String loggedInStr = pref.getString("PREFS_LOGGEDIN_KEY", null);
                        if (loggedInStr == null) {
                            Log.v("SUBHA","loggedInStr");

                            clickHereToLogin.setVisibility(View.VISIBLE);
                            submitRatingLayout.setVisibility(View.GONE);
                        }else{
                            if (showRating == 0){
                                submitRatingLayout.setVisibility(View.GONE);
                            }else{
                                submitRatingLayout.setVisibility(View.VISIBLE);

                            }
                            clickHereToLogin.setVisibility(View.GONE);
                            // submitRatingLayout.setVisibility(View.VISIBLE);
                        }
                    }else{
                        Log.v("SUBHA","loggedInStr2");

                        clickHereToLogin.setVisibility(View.VISIBLE);
                        submitRatingLayout.setVisibility(View.GONE);
                    }
                }else{
                    Log.v("SUBHA","loggedInStr3");

                    clickHereToLogin.setVisibility(View.GONE);
                    submitRatingLayout.setVisibility(View.GONE);

                }

                reviewsAdapter = new ReviewsAdapter(ReviewActivity.this,reviewsItem);
                reviewsGridView.setAdapter(reviewsAdapter);

            }
        }

        @Override
        protected void onPreExecute() {

            pDialog = new ProgressBarHandler(ReviewActivity.this);
            pDialog.show();


        }
    }

    public void ShowDialog(String msg) {
        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(ReviewActivity.this);
        dlgAlert.setTitle(Util.getTextofLanguage(ReviewActivity.this,Util.FAILURE,Util.DEFAULT_FAILURE));
        dlgAlert.setMessage(msg);
        dlgAlert.setPositiveButton(Util.getTextofLanguage(ReviewActivity.this,Util.BUTTON_OK,Util.DEFAULT_BUTTON_OK), null);
        dlgAlert.setCancelable(false);
        dlgAlert.setPositiveButton(Util.getTextofLanguage(ReviewActivity.this,Util.BUTTON_OK,Util.DEFAULT_BUTTON_OK),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        finish();
                    }
                });
        dlgAlert.create().show();
    }

    private class AsynAddReviewDetails extends AsyncTask<Void, Void, Void> {
        ProgressBarHandler pDialog;
        String responseStr = "";
        int status;
        String msg;
        @Override
        protected Void doInBackground(Void... params) {

            try {


                HttpClient httpclient=new DefaultHttpClient();
                HttpPost httppost = new HttpPost(Util.rootUrl().trim()+Util.AddContentRating.trim());
                httppost.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");
                httppost.addHeader("authToken", Util.authTokenStr.trim());
                httppost.addHeader("content_id",getIntent().getStringExtra("muviId"));
                httppost.addHeader("lang_code",Util.getTextofLanguage(ReviewActivity.this, Util.SELECTED_LANGUAGE_CODE, Util.DEFAULT_SELECTED_LANGUAGE_CODE));
                httppost.addHeader("user_id",pref.getString("PREFS_LOGGEDIN_ID_KEY", null));

                httppost.addHeader("rating",ratingStr);
                reviewMessage = reviewMessage.replaceAll("(\r\n|\n\r|\r|\n|<br />)", " ");
                httppost.addHeader("review",reviewMessage);


                // Execute HTTP Post Request
                try {
                    HttpResponse response = httpclient.execute(httppost);
                    responseStr = EntityUtils.toString(response.getEntity());
                    Log.v("SUBHA","RESPO"+responseStr);
                    Log.v("SUBHA","RESPO"+getIntent().getStringExtra("muviId"));


                } catch (Exception e){
                    responseStr = "0";

                }

                JSONObject myJson =null;
                if(responseStr!=null){
                    myJson = new JSONObject(responseStr);
                    status = Integer.parseInt(myJson.optString("code"));
                    msg = myJson.optString("msg");


                }

                if (status == 200) {


                }else{
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
                if (pDialog != null && pDialog.isShowing()) {
                    pDialog.hide();
                    pDialog = null;
                }

            }
            catch(IllegalArgumentException ex)
            {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        responseStr = "0";


                    }

                });
                responseStr = "0";
            }
            if(responseStr == null) {
                responseStr = "0";
            }

            if((responseStr.trim().equals("0"))){
                ShowDialog(msg);
            }else{
                Intent intent=new Intent();
                setResult(30060,intent);
                finish();
                overridePendingTransition(0,0);


            }
        }

        @Override
        protected void onPreExecute() {

            pDialog = new ProgressBarHandler(ReviewActivity.this);
            pDialog.show();


        }
    }




}
