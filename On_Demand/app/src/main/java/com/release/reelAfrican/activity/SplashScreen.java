package com.release.reelAfrican.activity;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.release.reelAfrican.R;
import com.release.reelAfrican.model.ContactModel1;
import com.release.reelAfrican.model.LanguageModel;
import com.release.reelAfrican.utils.AppUpdate;
import com.release.reelAfrican.utils.DBHelper;
import com.release.reelAfrican.utils.Util;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HttpsURLConnection;

import io.fabric.sdk.android.Fabric;

public class SplashScreen extends Activity  implements AppUpdate.clickOnAppUpdate{

    boolean wantsToUpdate = false;
    String User_Id = "";
    String Email_Id = "";
    String isSubscribed = "0";
    String[] genreArrToSend;
    String[] genreValueArrayToSend;

    RelativeLayout noInternetLayout ;
    RelativeLayout geoBlockedLayout ;
    String Default_Language = "";
    ArrayList<LanguageModel> languageModels = new ArrayList<>();
    TextView noInternetTextView;
    TextView geoTextView;
    DBHelper dbHelper;
    //============================Added For FCM===========================//
    Timer GoogleIdGeneraterTimer;
    ArrayList<ContactModel1> databaseupdate;
    /*Asynctask on background thread*/
    int corePoolSize = 60;
    int maximumPoolSize = 80;
    int keepAliveTime = 10;
    String ipAddressStr;
    SharedPreferences countryPref;
    SharedPreferences isLoginPref;
    SharedPreferences language_list_pref;
    BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<Runnable>(maximumPoolSize);
    Executor threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, workQueue);
     SharedPreferences pref;
    String emailIdStr = "";
    DownloadManager downloadManager;
    public boolean downloading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_splash_screen);
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.release.reelAfrican",  // replace with your unique package name
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.v("SUBHA:", Base64.encodeToString(md.digest(), Base64.DEFAULT));

            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }


       // dbHelper=new DBHelper(SplashScreen.this);
        pref = getSharedPreferences(Util.LOGIN_PREF, 0);
//
//        if (pref.contains("PREFS_LOGIN_EMAIL_ID_KEY")){
//
//            if(pref!=null){
//
//
//
//                emailIdStr= pref.getString("PREFS_LOGIN_EMAIL_ID_KEY", null);
//
//                databaseupdate=dbHelper.getContactt(emailIdStr,2);
//
//                if (databaseupdate.size()>0) {
//
//
//                    Toast.makeText(getApplicationContext(),"Running",Toast.LENGTH_LONG).show();
//                    int i = 0;
//
//                    for (i = 0; i < databaseupdate.size(); i++) {
//
//                        checkDownLoadStatusFromDownloadManager1(databaseupdate.get(i));
//
//
//                    }
//                }
//
//
//
//            }
//
//
//
//        }else {
//            emailIdStr = "";
//
//
//        }



        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.release.monetiser",  // replace with your unique package name
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.v("SUBHA:", Base64.encodeToString(md.digest(), Base64.DEFAULT));

            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
        noInternetLayout = (RelativeLayout)findViewById(R.id.noInternet);
        geoBlockedLayout = (RelativeLayout)findViewById(R.id.geoBlocked);

        noInternetTextView =(TextView)findViewById(R.id.noInternetTextView);
        geoTextView =(TextView)findViewById(R.id.geoBlockedTextView);
        noInternetTextView.setText(Util.getTextofLanguage(SplashScreen.this,Util.NO_INTERNET_CONNECTION,Util.DEFAULT_NO_INTERNET_CONNECTION));
        geoTextView.setText(Util.getTextofLanguage(SplashScreen.this,Util.GEO_BLOCKED_ALERT,Util.DEFAULT_GEO_BLOCKED_ALERT));

        noInternetLayout.setVisibility(View.GONE);
        geoBlockedLayout.setVisibility(View.GONE);
        boolean isNetwork = Util.checkNetwork(SplashScreen.this);
        if (isNetwork == false ) {
            noInternetLayout.setVisibility(View.VISIBLE);
            geoBlockedLayout.setVisibility(View.GONE);
        }
        RelativeLayout rl = (RelativeLayout)findViewById(R.id.mainlayout);

        countryPref = getSharedPreferences(Util.COUNTRY_PREF, 0); // 0 - for private mode
        isLoginPref = getSharedPreferences(Util.IS_LOGIN_SHARED_PRE,0);
        language_list_pref = getSharedPreferences(Util.LANGUAGE_LIST_PREF,0);


        if (countryPref != null) {
            String countryCodeStr = countryPref.getString("countryCode", null);

            if (countryCodeStr == null) {
                if (isNetwork == true ) {
                    AsynGetIpAddress asynGetIpAddress = new AsynGetIpAddress();
                    asynGetIpAddress.executeOnExecutor(threadPoolExecutor);
                }else{
                    noInternetLayout.setVisibility(View.VISIBLE);
                    geoBlockedLayout.setVisibility(View.GONE);

                }
            }else{
                AsynGetIpAddress asynGetIpAddress = new AsynGetIpAddress();
                asynGetIpAddress.executeOnExecutor(threadPoolExecutor);

            }
        }else{
            if (isNetwork == true ) {

                AsynGetIpAddress asynGetIpAddress = new AsynGetIpAddress();
                asynGetIpAddress.executeOnExecutor(threadPoolExecutor);

            }else{
                noInternetLayout.setVisibility(View.VISIBLE);
                geoBlockedLayout.setVisibility(View.GONE);
            }
        }

    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        finish();
        overridePendingTransition(0, 0);
    }
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
    @Override
    public void onUserLeaveHint() {
        super.onUserLeaveHint();
        finish();
        System.exit(0);
    }

    //Verify the IP
    private class AsynGetIpAddress extends AsyncTask<Void, Void, Void> {
        String responseStr;


        @Override
        protected Void doInBackground(Void... params) {

            try {

                // Execute HTTP Post Request
                try {
                    URL myurl = new URL(Util.loadIPUrl);
                    HttpsURLConnection con = (HttpsURLConnection)myurl.openConnection();
                    InputStream ins = con.getInputStream();
                    InputStreamReader isr = new InputStreamReader(ins);
                    BufferedReader in = new BufferedReader(isr);

                    String inputLine;

                    while ((inputLine = in.readLine()) != null)
                    {
                        System.out.println(inputLine);
                        responseStr = inputLine;
                    }

                    in.close();


                } catch (org.apache.http.conn.ConnectTimeoutException e){
                    ipAddressStr = "";

                    runOnUiThread(new Runnable() {
                        public void run() {
                            noInternetLayout.setVisibility(View.VISIBLE);
                            geoBlockedLayout.setVisibility(View.GONE);

                        }
                    });
                } catch (UnsupportedEncodingException e) {

                    ipAddressStr = "";
                    runOnUiThread(new Runnable() {
                        public void run() {
                            noInternetLayout.setVisibility(View.VISIBLE);
                            geoBlockedLayout.setVisibility(View.GONE);

                        }
                    });
                }catch (IOException e) {
                    ipAddressStr = "";

                    runOnUiThread(new Runnable() {
                        public void run() {
                            noInternetLayout.setVisibility(View.VISIBLE);
                            geoBlockedLayout.setVisibility(View.GONE);

                        }
                    });
                }
                if(responseStr!=null){
                    Object json = new JSONTokener(responseStr).nextValue();
                    if (json instanceof JSONObject){
                        ipAddressStr = ((JSONObject) json).getString("ip");

                    }

                }

            }
            catch (Exception e) {
                ipAddressStr = "";

                runOnUiThread(new Runnable() {
                    public void run() {
                        noInternetLayout.setVisibility(View.VISIBLE);
                        geoBlockedLayout.setVisibility(View.GONE);

                    }
                });


            }

            return null;
        }


        protected void onPostExecute(Void result) {

            if(responseStr == null){
                ipAddressStr = "";
                noInternetLayout.setVisibility(View.VISIBLE);
                geoBlockedLayout.setVisibility(View.GONE);
            }else{
                AsynGetCountry asynGetCountry = new AsynGetCountry();
                asynGetCountry.executeOnExecutor(threadPoolExecutor);

            }

        }

        protected void onPreExecute() {

        }
    }
    //Verify the IP
    private class AsynGetCountry extends AsyncTask<Void, Void, Void> {
        String responseStr;
        String countryCode;
        int status;

        @Override
        protected Void doInBackground(Void... params) {

            String urlRouteList =Util.rootUrl().trim()+Util.loadCountryUrl.trim();

            try {
                HttpClient httpclient=new DefaultHttpClient();
                HttpGet httppost = new HttpGet(urlRouteList);
                httppost.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");
                httppost.addHeader("authToken", Util.authTokenStr.trim());
                httppost.addHeader("ip", ipAddressStr.trim());

                // Execute HTTP Post Request
                try {
                    HttpResponse response = httpclient.execute(httppost);
                    responseStr = EntityUtils.toString(response.getEntity());

                } catch (org.apache.http.conn.ConnectTimeoutException e){
                    countryCode = "";
                    runOnUiThread(new Runnable() {
                        public void run() {
                            noInternetLayout.setVisibility(View.GONE);
                            geoBlockedLayout.setVisibility(View.VISIBLE);

                        }
                    });

                } catch (UnsupportedEncodingException e) {

                    countryCode = "";
                    runOnUiThread(new Runnable() {
                        public void run() {
                            noInternetLayout.setVisibility(View.GONE);
                            geoBlockedLayout.setVisibility(View.VISIBLE);

                        }
                    });

                }catch (IOException e) {
                    countryCode = "";
                    runOnUiThread(new Runnable() {
                        public void run() {
                            noInternetLayout.setVisibility(View.GONE);
                            geoBlockedLayout.setVisibility(View.VISIBLE);

                        }
                    });

                }
                if(responseStr!=null){
                    Object json = new JSONTokener(responseStr).nextValue();
                    if (json instanceof JSONObject){
                        String statusStr = ((JSONObject) json).getString("code");
                        status = Integer.parseInt(statusStr);
                        if (status == 200){
                            countryCode = ((JSONObject) json).getString("country");
                        }

                    }

                }

            }
            catch (Exception e) {
                countryCode = "";
                runOnUiThread(new Runnable() {
                    public void run() {
                        noInternetLayout.setVisibility(View.GONE);
                        geoBlockedLayout.setVisibility(View.VISIBLE);

                    }
                });



            }

            return null;
        }


        protected void onPostExecute(Void result) {

            if(responseStr == null){
                countryCode = "";
                noInternetLayout.setVisibility(View.GONE);
                geoBlockedLayout.setVisibility(View.VISIBLE);
            }else{
                if (status > 0 && status == 200) {
                    if (countryPref != null) {
                        SharedPreferences.Editor countryEditor = countryPref.edit();
                        countryEditor.putString("countryCode", countryCode.trim());

                        countryEditor.commit();
                        AsynGetPlanId asynGetPlanId = new AsynGetPlanId();
                        asynGetPlanId.executeOnExecutor(threadPoolExecutor);
                    }

                }else{
                    noInternetLayout.setVisibility(View.GONE);
                    geoBlockedLayout.setVisibility(View.VISIBLE);
                }


            }

        }

        protected void onPreExecute() {

        }
    }


    private class AsynGetPlanId extends AsyncTask<Void, Void, Void> {
        String responseStr;
        int status;


        @Override
        protected Void doInBackground(Void... params) {

            String urlRouteList =Util.rootUrl().trim()+Util.getStudioPlanLists.trim();

            try {
                HttpClient httpclient=new DefaultHttpClient();
                HttpGet httppost = new HttpGet(urlRouteList);
                httppost.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");
                httppost.addHeader("authToken",Util.authTokenStr.trim());

                // Execute HTTP Post Request
                try {
                    HttpResponse response = httpclient.execute(httppost);
                    responseStr = EntityUtils.toString(response.getEntity());
                    Log.v("SUBHA","responsestring of plan list = "+responseStr);


                } catch (org.apache.http.conn.ConnectTimeoutException e){
                    runOnUiThread(new Runnable() {
                        public void run() {


                        }
                    });

                } catch (UnsupportedEncodingException e) {

                    runOnUiThread(new Runnable() {
                        public void run() {


                        }
                    });

                }catch (IOException e) {
                    runOnUiThread(new Runnable() {
                        public void run() {


                        }
                    });

                }
                JSONObject myJson =null;
                if(responseStr!=null){
                    myJson = new JSONObject(responseStr);
                    status = Integer.parseInt(myJson.optString("code"));
                }

                if (status > 0) {
                    if (status == 200) {
                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.PLAN_ID,"1");
                        Log.v("SUBHA","responsestring of plan id = 1");
                    }
                    else{
                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.PLAN_ID,"0");
                        Log.v("SUBHA","responsestring of plan id = 0");
                    }
                }

            }
            catch (final Exception e) {
                runOnUiThread(new Runnable() {
                    public void run() {
                    }
                });



            }

            return null;
        }


        protected void onPostExecute(Void result) {

            AsynIsRegistrationEnabled asynIsRegistrationEnabled = new AsynIsRegistrationEnabled();
            asynIsRegistrationEnabled.executeOnExecutor(threadPoolExecutor);
        }

        protected void onPreExecute() {

        }
    }

    //subhashree genre

    private class AsynGetGenreList extends AsyncTask<Void, Void, Void> {
        String responseStr;
        int status;
        ArrayList<String> genreArrayList = new ArrayList<String>();
        ArrayList<String> genreValueArrayList = new ArrayList<String>();

        @Override
        protected Void doInBackground(Void... params) {

            String urlRouteList =Util.rootUrl().trim()+Util.getGenreListUrl.trim();

            try {
                HttpClient httpclient=new DefaultHttpClient();
                HttpGet httppost = new HttpGet(urlRouteList);
                httppost.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");
                httppost.addHeader("authToken", Util.authTokenStr.trim());

                // Execute HTTP Post Request
                try {
                    HttpResponse response = httpclient.execute(httppost);
                    responseStr = EntityUtils.toString(response.getEntity());


                } catch (org.apache.http.conn.ConnectTimeoutException e){
                    runOnUiThread(new Runnable() {
                        public void run() {


                        }
                    });

                } catch (UnsupportedEncodingException e) {

                    runOnUiThread(new Runnable() {
                        public void run() {


                        }
                    });

                }catch (IOException e) {
                    runOnUiThread(new Runnable() {
                        public void run() {


                        }
                    });

                }
                JSONObject myJson =null;
                if(responseStr!=null){
                    myJson = new JSONObject(responseStr);
                    status = Integer.parseInt(myJson.optString("code"));
                }

                if (status > 0) {
                    if (status == 200) {

                        JSONArray jsonMainNode = myJson.getJSONArray("genre_list");

                        int lengthJsonArr = jsonMainNode.length();
                        if (lengthJsonArr > 0){
                            genreArrayList.add(0,Util.getTextofLanguage(SplashScreen.this,Util.FILTER_BY,Util.DEFAULT_FILTER_BY));
                            Log.v("SUBHA","filter by = "+Util.getTextofLanguage(SplashScreen.this,Util.FILTER_BY,Util.DEFAULT_FILTER_BY));

                            genreValueArrayList.add(0,"");

                        }
                        for(int i=0; i < lengthJsonArr; i++) {
                            genreArrayList.add(jsonMainNode.get(i).toString());
                            genreValueArrayList.add(jsonMainNode.get(i).toString());


                        }

                        if (genreArrayList.size() > 1){

                            genreArrayList.add(genreArrayList.size(),Util.getTextofLanguage(SplashScreen.this,Util.SORT_BY,Util.DEFAULT_SORT_BY));
                            genreValueArrayList.add(genreValueArrayList.size(),"");


                            genreArrayList.add(genreArrayList.size(),Util.getTextofLanguage(SplashScreen.this, Util.SORT_LAST_UPLOADED, Util.DEFAULT_SORT_LAST_UPLOADED));
                            genreValueArrayList.add(genreValueArrayList.size(),"lastupload");

                            genreArrayList.add(genreArrayList.size(),Util.getTextofLanguage(SplashScreen.this, Util.SORT_RELEASE_DATE, Util.DEFAULT_SORT_RELEASE_DATE));
                            genreValueArrayList.add(genreValueArrayList.size(),"releasedate");

                            genreArrayList.add(genreArrayList.size(),Util.getTextofLanguage(SplashScreen.this, Util.SORT_ALPHA_A_Z, Util.DEFAULT_SORT_ALPHA_A_Z));
                            genreValueArrayList.add(genreValueArrayList.size(),"sortasc");

                            genreArrayList.add(genreArrayList.size(),Util.getTextofLanguage(SplashScreen.this, Util.SORT_ALPHA_Z_A, Util.DEFAULT_SORT_ALPHA_Z_A));
                            genreValueArrayList.add(genreValueArrayList.size(),"sortdesc");






                        }

                    }
                    else{
                        responseStr = "0";

                    }
                }

            }
            catch (final Exception e) {
                runOnUiThread(new Runnable() {
                    public void run() {
                    }
                });



            }

            return null;
        }


        protected void onPostExecute(Void result) {

            if(responseStr == null){

            }else{
                if (status > 0 && status == 200) {
                    genreArrToSend = new String[genreArrayList.size()];
                    genreArrToSend = genreArrayList.toArray(genreArrToSend);


                    genreValueArrayToSend = new String[genreValueArrayList.size()];
                    genreValueArrayToSend = genreValueArrayList.toArray(genreValueArrayToSend);




                }else{
                   /* genreArrToSend = new String[0];
                    genreArrToSend = genreArrayList.toArray(genreArrToSend);


                    genreValueArrayToSend = new String[0];
                    genreValueArrayToSend = genreValueArrayList.toArray(genreValueArrayToSend);*/


                    genreArrayList.add(genreArrayList.size(),Util.getTextofLanguage(SplashScreen.this, Util.SORT_BY, Util.DEFAULT_SORT_BY));
                    genreValueArrayList.add(genreValueArrayList.size(),"");


                    genreArrayList.add(genreArrayList.size(),Util.getTextofLanguage(SplashScreen.this, Util.SORT_LAST_UPLOADED, Util.DEFAULT_SORT_LAST_UPLOADED));
                    genreValueArrayList.add(genreValueArrayList.size(),"lastupload");

                    genreArrayList.add(genreArrayList.size(),Util.getTextofLanguage(SplashScreen.this, Util.SORT_RELEASE_DATE, Util.DEFAULT_SORT_RELEASE_DATE));
                    genreValueArrayList.add(genreValueArrayList.size(),"releasedate");

                    genreArrayList.add(genreArrayList.size(),Util.getTextofLanguage(SplashScreen.this, Util.SORT_ALPHA_A_Z, Util.DEFAULT_SORT_ALPHA_A_Z));
                    genreValueArrayList.add(genreValueArrayList.size(),"sortasc");

                    genreArrayList.add(genreArrayList.size(),Util.getTextofLanguage(SplashScreen.this, Util.SORT_ALPHA_Z_A, Util.DEFAULT_SORT_ALPHA_Z_A));
                    genreValueArrayList.add(genreValueArrayList.size(),"sortdesc");

                    genreArrToSend = new String[genreArrayList.size()];
                    genreArrToSend = genreArrayList.toArray(genreArrToSend);


                    genreValueArrayToSend = new String[genreValueArrayList.size()];
                    genreValueArrayToSend = genreValueArrayList.toArray(genreValueArrayToSend);


                }

            }

            SharedPreferences.Editor isLoginPrefEditor = isLoginPref.edit();
            if (genreArrToSend!=null && genreArrToSend.length > 0) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < genreArrToSend.length; i++) {
                    sb.append(genreArrToSend[i]).append(",");
                }
                isLoginPrefEditor.putString(Util.GENRE_ARRAY_PREF_KEY, sb.toString());
                StringBuilder sb1 = new StringBuilder();
                for (int i = 0; i < genreValueArrayToSend.length; i++) {
                    sb1.append(genreValueArrayToSend[i]).append(",");
                }
                isLoginPrefEditor.putString(Util.GENRE_VALUES_ARRAY_PREF_KEY, sb1.toString());
                isLoginPrefEditor.commit();
            }

            // This Code Is Done For The One Step Registration.


            if ((Util.getTextofLanguage(SplashScreen.this, Util.IS_ONE_STEP_REGISTRATION, Util.DEFAULT_IS_ONE_STEP_REGISTRATION)
                    .trim()).equals("1")) {


                if (pref != null) {
                    User_Id = pref.getString("PREFS_LOGGEDIN_ID_KEY", null);
                    Email_Id = pref.getString("PREFS_LOGIN_EMAIL_ID_KEY", null);

                    if (User_Id != null && Email_Id != null) {

                        AsynLoadProfileDetails asynLoadProfileDetails = new AsynLoadProfileDetails();
                        asynLoadProfileDetails.executeOnExecutor(threadPoolExecutor);

                    } else {
                        Call_One_Step_Procedure();
                    }
                } else {
                    Call_One_Step_Procedure();
                }
            } else {
                Call_One_Step_Procedure();
            }
        }
        protected void onPreExecute() {

        }
    }


    private class AsynIsRegistrationEnabled extends AsyncTask<Void, Void, Void> {
        String responseStr;
        int statusCode;
        private int isLogin = 0;
        private String IsMyLibrary = "0";
        private String IsOneStepReg = "0";
        private String isRestrictDevice = "0";
        private String isStreamingRestriction = "0";
        private String hasFavorite = "0";

        @Override
        protected Void doInBackground(Void... params) {

            try {
                HttpClient httpclient=new DefaultHttpClient();
                HttpPost httppost = new HttpPost(Util.rootUrl()+Util.isRegistrationEnabledurl.trim());
                httppost.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");
                httppost.addHeader("authToken", Util.authTokenStr.trim());
                // Execute HTTP Post Request
                try {
                    HttpResponse response = httpclient.execute(httppost);
                    responseStr = EntityUtils.toString(response.getEntity());


                } catch (org.apache.http.conn.ConnectTimeoutException e){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                        }

                    });

                }catch (IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {


                        }

                    });
                    e.printStackTrace();
                }


                JSONObject myJson =null;
                if(responseStr!=null){
                    myJson = new JSONObject(responseStr);
                    statusCode = Integer.parseInt(myJson.optString("code"));

                }

                if (statusCode > 0) {
                    if (statusCode == 200) {
                        if ((myJson.has("is_login")) && myJson.getString("is_login").trim() != null && !myJson.getString("is_login").trim().isEmpty() && !myJson.getString("is_login").trim().equals("null") && !myJson.getString("is_login").trim().matches("")) {
                            isLogin = Integer.parseInt(myJson.getString("is_login"));

                            IsOneStepReg = myJson.optString("signup_step");
                            isRestrictDevice = myJson.optString("isRestrictDevice");
                            isStreamingRestriction = myJson.optString("is_streaming_restriction");
                            //hasFavorite = myJson.optString("has_favourite");
                            //Adder Later By Bibhu
                            //This code is used for the 'My Library Feature'

                            if(isLogin == 1)

                            {
                                if((myJson.optString("isMylibrary")).trim().equals("1"))
                                {
                                    IsMyLibrary = "1";
                                }
                            }
                            else
                            {
                                IsMyLibrary = "0";
                            }

                        } else {
                            isLogin = 0;
                        }
                        if ((myJson.has("has_favourite")) && myJson.getString("has_favourite").trim() != null && !myJson.getString("has_favourite").trim().isEmpty() && !myJson.getString("has_favourite").trim().equals("null") && !myJson.getString("has_favourite").trim().matches("")) {
                            hasFavorite = myJson.getString("has_favourite");
                            Log.v("SUBHA","has favorite = "+hasFavorite);

                        }



                    }else{
                        isLogin = 0;
                    }
                }
                else {
                    responseStr = "0";
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            isLogin = 0;


                        }
                    });
                }
            } catch (JSONException e1) {
                try {
                }
                catch(IllegalArgumentException ex)
                {
                    responseStr = "0";
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            isLogin = 0;


                        }
                    });
                    e1.printStackTrace();
                }
            }

            catch (Exception e)
            {
                try {

                }
                catch(IllegalArgumentException ex)
                {
                    responseStr = "0";
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            isLogin = 0;


                        }
                    });
                    e.printStackTrace();
                }

            }
            return null;

        }

        protected void onPostExecute(Void result) {


            try {

            }catch (IllegalArgumentException e){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        isLogin = 0;



                    }
                });
            }

            if(responseStr == null) {
                isLogin = 0;


            }
            if ((responseStr.trim().equalsIgnoreCase("0"))){
                isLogin = 0;

            }

            Util.setLanguageSharedPrefernce(SplashScreen.this, Util.IS_STREAMING_RESTRICTION, isStreamingRestriction);
            Util.setLanguageSharedPrefernce(SplashScreen.this,Util.IS_MYLIBRARY,IsMyLibrary);
            Util.setLanguageSharedPrefernce(SplashScreen.this, Util.IS_RESTRICT_DEVICE, isRestrictDevice);
            Util.setLanguageSharedPrefernce(SplashScreen.this, Util.IS_ONE_STEP_REGISTRATION, IsOneStepReg);
            Util.setLanguageSharedPrefernce(SplashScreen.this, Util.HAS_FAVORITE, hasFavorite);
            SharedPreferences.Editor isLoginPrefEditor = isLoginPref.edit();
            isLoginPrefEditor.putInt(Util.IS_LOGIN_PREF_KEY, isLogin);

            isLoginPrefEditor.commit();
            AsynGetLanguageList asynGetLanguageList = new AsynGetLanguageList();
            asynGetLanguageList.executeOnExecutor(threadPoolExecutor);


            /*AsynGetGenreList asynGetGenreList = new AsynGetGenreList();
            asynGetGenreList.executeOnExecutor(threadPoolExecutor);*/


        }

        @Override
        protected void onPreExecute() {

        }


    }
    private class AsynGetLanguageList extends AsyncTask<Void, Void, Void> {
        String responseStr;
        int status;


        @Override
        protected Void doInBackground(Void... params) {

//          String urlRouteList =Util.rootUrl().trim()+Util.LanguageList.trim();
            String urlRouteList = Util.rootUrl().trim()+Util.LanguageList.trim();
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(urlRouteList);
                httppost.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");
                httppost.addHeader("authToken", Util.authTokenStr);


                // Execute HTTP Post Request
                try {


                    HttpResponse response = httpclient.execute(httppost);
                    responseStr = (EntityUtils.toString(response.getEntity())).trim();
                } catch (Exception e) {
                }
                if (responseStr != null) {
                    JSONObject json = new JSONObject(responseStr);
                    try {
                        status = Integer.parseInt(json.optString("code"));
                        Default_Language = json.optString("default_lang");
                    } catch (Exception e) {
                        status = 0;
                    }
                }

            } catch (Exception e) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        noInternetLayout.setVisibility(View.GONE);
                        geoBlockedLayout.setVisibility(View.VISIBLE);

                    }
                });
            }

            return null;
        }


        protected void onPostExecute(Void result) {

            if (responseStr == null) {
                noInternetLayout.setVisibility(View.GONE);
                geoBlockedLayout.setVisibility(View.VISIBLE);
            } else {
                if (status > 0 && status == 200) {

                    try {
                        JSONObject json = new JSONObject(responseStr);
                        JSONArray jsonArray = json.getJSONArray("lang_list");


                        for (int i = 0; i < jsonArray.length(); i++) {
                            String language_id = jsonArray.getJSONObject(i).optString("code").trim();
                            String language_name = jsonArray.getJSONObject(i).optString("language").trim();


                            LanguageModel languageModel = new LanguageModel();
                            languageModel.setLanguageId(language_id);
                            languageModel.setLanguageName(language_name);
                            if (Default_Language.equalsIgnoreCase(language_id)){
                                languageModel.setIsSelected(true);

                            }else {
                                languageModel.setIsSelected(false);
                            }

                            languageModels.add(languageModel);
                        }

                         Util.languageModel = languageModels;

                    } catch (JSONException e) {
                        e.printStackTrace();
                        noInternetLayout.setVisibility(View.GONE);
                        geoBlockedLayout.setVisibility(View.VISIBLE);
                    }
                    Util.languageModel = languageModels;

                    if(languageModels.size()==1)
                    {
                        SharedPreferences.Editor countryEditor = language_list_pref.edit();
                        countryEditor.putString("total_language","1");
                        countryEditor.commit();
                    }
                    if (Util.getTextofLanguage(SplashScreen.this,Util.SELECTED_LANGUAGE_CODE,"").equalsIgnoreCase("")){
                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.SELECTED_LANGUAGE_CODE,Default_Language);
                    }
                    AsynGetTransalatedLanguage asynGetGenreList = new AsynGetTransalatedLanguage();
                    asynGetGenreList.executeOnExecutor(threadPoolExecutor);
                   /* if(!Default_Language.equals("en")) {
                        //                  Call For Language Translation.
                        AsynGetTransalatedLanguage asynGetTransalatedLanguage = new AsynGetTransalatedLanguage();
                        asynGetTransalatedLanguage.executeOnExecutor(threadPoolExecutor);

                    }else{
                        AsynGetGenreList asynGetGenreList = new AsynGetGenreList();
                        asynGetGenreList.executeOnExecutor(threadPoolExecutor);
                    }*/

                } else {
                    noInternetLayout.setVisibility(View.GONE);
                    geoBlockedLayout.setVisibility(View.VISIBLE);
                }
            }

        }

        protected void onPreExecute() {

        }
    }



    private class AsynGetTransalatedLanguage extends AsyncTask<Void, Void, Void> {
        String responseStr;
        int status;

        @Override
        protected Void doInBackground(Void... params) {

            String urlRouteList =Util.rootUrl().trim()+Util.LanguageTranslation.trim();
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(urlRouteList);
                httppost.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");
                httppost.addHeader("authToken",Util.authTokenStr);
                httppost.addHeader("lang_code",Default_Language);


                // Execute HTTP Post Request
                try {
                    HttpResponse response = httpclient.execute(httppost);
                    responseStr = (EntityUtils.toString(response.getEntity())).trim();
                } catch (Exception e) {
                }
                if (responseStr != null) {
                    JSONObject json = new JSONObject(responseStr);
                    try {
                        status = Integer.parseInt(json.optString("code"));
                    } catch (Exception e) {
                        status = 0;
                    }
                }

            } catch (Exception e) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        noInternetLayout.setVisibility(View.GONE);

                    }
                });
            }

            return null;
        }


        protected void onPostExecute(Void result) {



            if (responseStr == null) {
                noInternetLayout.setVisibility(View.GONE);
            } else {
                if (status > 0 && status == 200) {

                    try {
                        JSONObject parent_json = new JSONObject(responseStr);
                        JSONObject json = parent_json.getJSONObject("translation");


                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.ALREADY_MEMBER,json.optString("already_member").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.ACTIAVTE_PLAN_TITLE,json.optString("activate_plan_title").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.TRANSACTION_STATUS_ACTIVE,json.optString("transaction_status_active").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.ADD_TO_FAV,json.optString("add_to_fav").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.ADDED_TO_FAV,json.optString("added_to_fav").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.ENTER_EMPTY_FIELD,json.optString("enter_register_fields_data").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this, Util.ENTER_REGISTER_FIELDS_DATA, json.optString("enter_register_fields_data").trim());

                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.ADVANCE_PURCHASE,json.optString("advance_purchase").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.ALERT,json.optString("alert").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.EPISODE_TITLE,json.optString("episodes_title").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.SORT_ALPHA_A_Z,json.optString("sort_alpha_a_z").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.SORT_ALPHA_Z_A,json.optString("sort_alpha_z_a").trim());

                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.AMOUNT,json.optString("amount").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.COUPON_CANCELLED,json.optString("coupon_cancelled").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.BUTTON_APPLY,json.optString("btn_apply").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.SIGN_OUT_WARNING,json.optString("sign_out_warning").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.DISCOUNT_ON_COUPON,json.optString("discount_on_coupon").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this, Util.MY_LIBRARY, json.optString("my_library").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.CREDIT_CARD_CVV_HINT,json.optString("credit_card_cvv_hint").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.CAST,json.optString("cast").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.CAST_CREW_BUTTON_TITLE,json.optString("cast_crew_button_title").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.CENSOR_RATING,json.optString("censor_rating").trim());
                        if(json.optString("change_password").trim()==null || json.optString("change_password").trim().equals("")) {
                            Util.setLanguageSharedPrefernce(SplashScreen.this, Util.CHANGE_PASSWORD, Util.DEFAULT_CHANGE_PASSWORD);
                        }
                        else {
                            Util.setLanguageSharedPrefernce(SplashScreen.this, Util.CHANGE_PASSWORD, json.optString("change_password").trim());
                        }

                        if(json.optString("add_a_review").trim()==null || json.optString("add_a_review").trim().equals("")) {
                            Util.setLanguageSharedPrefernce(SplashScreen.this, Util.ADD_A_REVIEW, Util.DEFAULT_ADD_A_REVIEW);
                        }
                        else {
                            Util.setLanguageSharedPrefernce(SplashScreen.this, Util.ADD_A_REVIEW, json.optString("add_a_review").trim());
                        }
                        if(json.optString("submit_your_rating_title").trim()==null || json.optString("submit_rating").trim().equals("")) {
                            Util.setLanguageSharedPrefernce(SplashScreen.this, Util.SUBMIT_YOUR_RATING_TITLE, Util.DEFAULT_SUBMIT_YOUR_RATING_TITLE);
                        }
                        else {
                            Util.setLanguageSharedPrefernce(SplashScreen.this, Util.SUBMIT_YOUR_RATING_TITLE, json.optString("submit_rating").trim());
                        }
                        if(json.optString("reviews").trim()==null || json.optString("reviews").trim().equals("")) {
                            Util.setLanguageSharedPrefernce(SplashScreen.this, Util.REVIEWS, Util.DEFAULT_REVIEWS);
                        }
                        else {
                            Util.setLanguageSharedPrefernce(SplashScreen.this, Util.REVIEWS, json.optString("reviews").trim());
                        }
                        if(json.optString("click_here").trim()==null || json.optString("click_here").trim().equals("")) {
                            Util.setLanguageSharedPrefernce(SplashScreen.this, Util.CLICK_HERE, Util.DEFAULT_CLICK_HERE);
                        }
                        else {
                            Util.setLanguageSharedPrefernce(SplashScreen.this, Util.CLICK_HERE, json.optString("click_here").trim());
                        }
                        if(json.optString("to_login").trim()==null || json.optString("to_login").trim().equals("")) {
                            Util.setLanguageSharedPrefernce(SplashScreen.this, Util.TO_LOGIN, Util.DEFAULT_TO_LOGIN);
                        }
                        else {
                            Util.setLanguageSharedPrefernce(SplashScreen.this, Util.TO_LOGIN, json.optString("to_login").trim());
                        }
                        if(json.optString("need_login_to_review").trim()==null || json.optString("need_login_to_review").trim().equals("")) {
                            Util.setLanguageSharedPrefernce(SplashScreen.this, Util.NEED_LOGIN_TO_REVIEW, Util.DEFAULT_NEED_LOGIN_TO_REVIEW);
                        }
                        else {
                            Util.setLanguageSharedPrefernce(SplashScreen.this, Util.NEED_LOGIN_TO_REVIEW, json.optString("need_login_to_review").trim());
                        }
                        if(json.optString("btn_post_review").trim()==null || json.optString("btn_post_review").trim().equals("")) {
                            Util.setLanguageSharedPrefernce(SplashScreen.this, Util.BTN_POST_REVIEW, Util.DEFAULT_BTN_POST_REVIEW);
                        }
                        else {
                            Util.setLanguageSharedPrefernce(SplashScreen.this, Util.BTN_POST_REVIEW, json.optString("btn_post_review").trim());
                        }
                        if(json.optString("login_facebook").trim()==null || json.optString("login_facebook").trim().equals("")) {
                            Util.setLanguageSharedPrefernce(SplashScreen.this, Util.LOGIN_FACEBOOK, Util.DEFAULT_LOGIN_FACEBOOK);
                        }
                        else {
                            Util.setLanguageSharedPrefernce(SplashScreen.this, Util.LOGIN_FACEBOOK, json.optString("login_facebook").trim());
                        }
                        if(json.optString("enter_review_here").trim()==null || json.optString("enter_review_here").trim().equals("")) {
                            Util.setLanguageSharedPrefernce(SplashScreen.this, Util.ENTER_REVIEW_HERE, Util.DEFAULT_ENTER_REVIEW_HERE);
                        }
                        else {
                            Util.setLanguageSharedPrefernce(SplashScreen.this, Util.ENTER_REVIEW_HERE, json.optString("enter_review_here").trim());
                        }

                        if(json.optString("register_facebook").trim()==null || json.optString("register_facebook").trim().equals("")) {
                            Util.setLanguageSharedPrefernce(SplashScreen.this, Util.REGISTER_FACEBOOK, Util.DEFAULT_REGISTER_FACEBOOK);
                        }
                        else {
                            Util.setLanguageSharedPrefernce(SplashScreen.this, Util.REGISTER_FACEBOOK, json.optString("register_facebook").trim());
                        }

                        Util.setLanguageSharedPrefernce(SplashScreen.this, Util.CANCEL_BUTTON, json.optString("btn_cancel").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this, Util.RESUME_MESSAGE, json.optString("resume_watching").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this, Util.CONTINUE_BUTTON, json.optString("continue").trim());


                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.CONFIRM_PASSWORD,json.optString("confirm_password").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.CREDIT_CARD_DETAILS,json.optString("credit_card_detail").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.DIRECTOR,json.optString("director").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.DOWNLOAD_BUTTON_TITLE,json.optString("download_button_title").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.DESCRIPTION,json.optString("description").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.HOME,json.optString("home").trim());

                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.EMAIL_EXISTS,json.optString("email_exists").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.EMAIL_DOESNOT_EXISTS,json.optString("email_does_not_exist").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.EMAIL_PASSWORD_INVALID,json.optString("email_password_invalid").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.COUPON_CODE_HINT,json.optString("coupon_code_hint").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.SEARCH_ALERT,json.optString("search_alert").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.MY_LIBRARY,json.optString("my_library").trim());

                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.CREDIT_CARD_NUMBER_HINT,json.optString("credit_card_number_hint").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.TEXT_EMIAL,json.optString("text_email").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.NAME_HINT,json.optString("name_hint").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.CREDIT_CARD_NAME_HINT,json.optString("credit_card_name_hint").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.TEXT_PASSWORD,json.optString("text_password").trim());

                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.ERROR_IN_PAYMENT_VALIDATION,json.optString("error_in_payment_validation").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.ERROR_IN_REGISTRATION,json.optString("error_in_registration").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.TRANSACTION_STATUS_EXPIRED,json.optString("transaction_status_expired").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.DETAILS_NOT_FOUND_ALERT,json.optString("details_not_found_alert").trim());

                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.FAILURE,json.optString("failure").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.FILTER_BY,json.optString("filter_by").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.FORGOT_PASSWORD,json.optString("forgot_password").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.GENRE,json.optString("genre").trim());

                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.AGREE_TERMS,json.optString("agree_terms").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.INVALID_COUPON,json.optString("invalid_coupon").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.INVOICE,json.optString("invoice").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.LANGUAGE_POPUP_LANGUAGE,json.optString("language_popup_language").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.SORT_LAST_UPLOADED,json.optString("sort_last_uploaded").trim());

                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.LANGUAGE_POPUP_LOGIN,json.optString("language_popup_login").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.LOGIN,json.optString("login").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.LOGOUT,json.optString("logout").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.LOGOUT_SUCCESS,json.optString("logout_success").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.MY_FAVOURITE,json.optString("my_favourite").trim());

                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.NEW_PASSWORD,json.optString("new_password").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.NEW_HERE_TITLE,json.optString("new_here_title").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.NO,json.optString("no").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.NO_DATA,json.optString("no_data").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.NO_INTERNET_CONNECTION,json.optString("no_internet_connection").trim());

                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.NO_INTERNET_NO_DATA,json.optString("no_internet_no_data").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.NO_DETAILS_AVAILABLE,json.optString("no_details_available").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.BUTTON_OK,json.optString("btn_ok").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.OLD_PASSWORD,json.optString("old_password").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.OOPS_INVALID_EMAIL,json.optString("oops_invalid_email").trim());

                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.ORDER,json.optString("order").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.TRANSACTION_DETAILS_ORDER_ID,json.optString("transaction_detail_order_id").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.PASSWORD_RESET_LINK,json.optString("password_reset_link").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.PASSWORDS_DO_NOT_MATCH,json.optString("password_donot_match").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.PAY_BY_PAYPAL,json.optString("pay_by_paypal").trim());

                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.BTN_PAYNOW,json.optString("btn_paynow").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.PAY_WITH_CREDIT_CARD,json.optString("pay_with_credit_card").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.PAYMENT_OPTIONS_TITLE,json.optString("payment_options_title").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.PLAN_NAME,json.optString("plan_name").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.ACTIVATE_SUBSCRIPTION_WATCH_VIDEO,json.optString("activate_subscription_watch_video").trim());

                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.COUPON_ALERT,json.optString("coupon_alert").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.VALID_CONFIRM_PASSWORD,json.optString("valid_confirm_password").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.PROFILE,json.optString("profile").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.PROFILE_UPDATED,json.optString("profile_updated").trim());

                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.PURCHASE,json.optString("purchase").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.TRANSACTION_DETAIL_PURCHASE_DATE,json.optString("transaction_detail_purchase_date").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.PURCHASE_HISTORY,json.optString("purchase_history").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.BTN_REGISTER,json.optString("btn_register").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.SORT_RELEASE_DATE,json.optString("sort_release_date").trim());

                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.SAVE_THIS_CARD,json.optString("save_this_card").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.TEXT_SEARCH_PLACEHOLDER,json.optString("text_search_placeholder").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.SEASON,json.optString("season").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.SELECT_OPTION_TITLE,json.optString("select_option_title").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.SELECT_PLAN,json.optString("select_plan").trim());

                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.SIGN_UP_TITLE,json.optString("signup_title").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.SLOW_INTERNET_CONNECTION,json.optString("slow_internet_connection").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.SLOW_ISSUE_INTERNET_CONNECTION,json.optString("slow_issue_internet_connection").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.SORRY,json.optString("sorry").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.GEO_BLOCKED_ALERT,json.optString("geo_blocked_alert").trim());

                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.SIGN_OUT_ERROR,json.optString("sign_out_error").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.ALREADY_PURCHASE_THIS_CONTENT,json.optString("already_purchase_this_content").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.CROSSED_MAXIMUM_LIMIT,json.optString("crossed_max_limit_of_watching").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.SORT_BY,json.optString("sort_by").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.STORY_TITLE,json.optString("story_title").trim());

                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.BTN_SUBMIT,json.optString("btn_submit").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.TRANSACTION_STATUS,json.optString("transaction_success").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.VIDEO_ISSUE,json.optString("video_issue").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.NO_CONTENT,json.optString("no_content").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.NO_VIDEO_AVAILABLE,json.optString("no_video_available").trim());

                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.CONTENT_NOT_AVAILABLE_IN_YOUR_COUNTRY,json.optString("content_not_available_in_your_country").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.TRANSACTION_DATE,json.optString("transaction_date").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.TRANASCTION_DETAIL,json.optString("transaction_detail").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.TRANSACTION_STATUS,json.optString("transaction_status").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.TRANSACTION,json.optString("transaction").trim());

                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.TRY_AGAIN,json.optString("try_again").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.UNPAID,json.optString("unpaid").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.USE_NEW_CARD,json.optString("use_new_card").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.VIEW_MORE,json.optString("view_more").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.VIEW_TRAILER,json.optString("view_trailer").trim());

                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.WATCH,json.optString("watch").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.WATCH_NOW,json.optString("watch_now").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.SIGN_OUT_ALERT,json.optString("sign_out_alert").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.UPDATE_PROFILE_ALERT,json.optString("update_profile_alert").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.YES,json.optString("yes").trim());

                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.PURCHASE_SUCCESS_ALERT,json.optString("purchase_success_alert").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.CARD_WILL_CHARGE,json.optString("card_will_charge").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.SEARCH_HINT,json.optString("search_hint").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this, Util.TERMS, json.optString("terms").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this, Util.UPDATE_PROFILE, json.optString("btn_update_profile").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this, Util.APP_ON, json.optString("app_on").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this, Util.APP_SELECT_LANGUAGE, json.optString("app_select_language").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this, Util.FILL_FORM_BELOW, json.optString("fill_form_below").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this, Util.MESSAGE, json.optString("text_message").trim());


                        Util.setLanguageSharedPrefernce(SplashScreen.this, Util.SIMULTANEOUS_LOGOUT_SUCCESS_MESSAGE, json.optString("simultaneous_logout_message").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this, Util.LOGIN_STATUS_MESSAGE, json.optString("login_status_message").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this, Util.FILL_FORM_BELOW, json.optString("fill_form_below").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this, Util.MESSAGE, json.optString("text_message").trim());


                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.SIMULTANEOUS_LOGOUT_SUCCESS_MESSAGE,json.optString("logged_out_from_all_devices").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.ANDROID_VERSION,json.optString("android_version").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.MANAGE_DEVICE,json.optString("manage_device").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.YOUR_DEVICE,json.optString("your_device").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.DEREGISTER,json.optString("deregister").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.LOGIN_STATUS_MESSAGE,json.optString("oops_you_have_no_access").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.UPADTE_TITLE,json.optString("update_title").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.UPADTE_MESSAGE,json.optString("update_message").trim());
                        Util.getTextofLanguage(SplashScreen.this, Util.PURCHASE, Util.DEFAULT_PURCHASE);
                        Util.setLanguageSharedPrefernce(SplashScreen.this, Util.SELECTED_LANGUAGE_CODE, Default_Language);

                        //Language for offline viewing


                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.MY_DOWNLOAD,json.optString("my_download").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.DELETE_BTN,json.optString("delete_btn").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.STOP_SAVING_THIS_VIDEO,json.optString("stop_saving_this_video").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.YOUR_VIDEO_WONT_BE_SAVED,json.optString("your_video_can_not_be_saved").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.BTN_KEEP,json.optString("btn_keep").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.BTN_DISCARD,json.optString("btn_discard").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.DOWNLOAD_CANCELLED,json.optString("download_cancelled").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.WANT_TO_DOWNLOAD,json.optString("want_to_download").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.WANT_TO_DELETE,json.optString("want_to_delete").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.VIEW_LESS,json.optString("view_less").trim());


                        //Language for voucher

                        Util.setLanguageSharedPrefernce(SplashScreen.this, Util.VOUCHER_CODE, json.optString("voucher_code").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.VOUCHER_BLANK_MESSAGE,json.optString("voucher_vaildate_message").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.VOUCHER_SUCCESS,json.optString("voucher_applied_success").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.BTN_NEXT,json.optString("btn_next").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.FREE_FOR_COUPON,json.optString("free_for_coupon").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.SELECT_PURCHASE_TYPE,json.optString("select_purchase_type").trim());
                        Util.setLanguageSharedPrefernce(SplashScreen.this,Util.COMPLETE_SEASON,json.optString("complete_season").trim());

                        //Call For Language PopUp Dialog



                    } catch (JSONException e) {
                        e.printStackTrace();
                        noInternetLayout.setVisibility(View.GONE);
                    }
                    // Call For Other Methods.


                } else {
                    noInternetLayout.setVisibility(View.GONE);
                }
            }
            AsynGetGenreList asynGetGenreList = new AsynGetGenreList();
            asynGetGenreList.executeOnExecutor(threadPoolExecutor);



        }
        protected void onPreExecute() {

        }
    }

    @Override
    public boolean wantsToUpdate(boolean result) {
        Log.v("SUBHA", "DATAnb"+result);
        if(result == false){
            //add later subha
            Util.alert_popup = true;
            noInternetLayout = (RelativeLayout)findViewById(R.id.noInternet);
            geoBlockedLayout = (RelativeLayout)findViewById(R.id.geoBlocked);

            noInternetTextView =(TextView)findViewById(R.id.noInternetTextView);
            geoTextView =(TextView)findViewById(R.id.geoBlockedTextView);
            noInternetTextView.setText(Util.getTextofLanguage(SplashScreen.this,Util.NO_INTERNET_CONNECTION,Util.DEFAULT_NO_INTERNET_CONNECTION));
            geoTextView.setText(Util.getTextofLanguage(SplashScreen.this,Util.GEO_BLOCKED_ALERT,Util.DEFAULT_GEO_BLOCKED_ALERT));

            noInternetLayout.setVisibility(View.GONE);
            geoBlockedLayout.setVisibility(View.GONE);
            language_list_pref = getSharedPreferences(Util.LANGUAGE_LIST_PREF, 0);

            boolean isNetwork = Util.checkNetwork(SplashScreen.this);
            if (isNetwork == false ) {
                noInternetLayout.setVisibility(View.VISIBLE);
                geoBlockedLayout.setVisibility(View.GONE);
            }
            RelativeLayout rl = (RelativeLayout)findViewById(R.id.mainlayout);

            countryPref = getSharedPreferences(Util.COUNTRY_PREF, 0); // 0 - for private mode
            isLoginPref = getSharedPreferences(Util.IS_LOGIN_SHARED_PRE,0);


            if (countryPref != null) {
                String countryCodeStr = countryPref.getString("countryCode", null);

                if (countryCodeStr == null) {
                    if (isNetwork == true ) {
                        AsynGetIpAddress asynGetIpAddress = new AsynGetIpAddress();
                        asynGetIpAddress.executeOnExecutor(threadPoolExecutor);
                    }else{
                        noInternetLayout.setVisibility(View.VISIBLE);
                        geoBlockedLayout.setVisibility(View.GONE);

                    }
                }else{
                    AsynGetIpAddress asynGetIpAddress = new AsynGetIpAddress();
                    asynGetIpAddress.executeOnExecutor(threadPoolExecutor);

                }
            }else{
                if (isNetwork == true ) {

                    AsynGetIpAddress asynGetIpAddress = new AsynGetIpAddress();
                    asynGetIpAddress.executeOnExecutor(threadPoolExecutor);

                }else{
                    noInternetLayout.setVisibility(View.VISIBLE);
                    geoBlockedLayout.setVisibility(View.GONE);
                }
            }

        }
        return false;
    }

    private class AsynLoadProfileDetails extends AsyncTask<Void, Void, Void> {
        String responseStr;
        int statusCode;

        @Override
        protected Void doInBackground(Void... params) {

            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(Util.rootUrl().trim() + Util.loadProfileUrl.trim());
                httppost.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");
                httppost.addHeader("authToken", Util.authTokenStr.trim());
                httppost.addHeader("user_id", User_Id);
                httppost.addHeader("email", Email_Id);
                httppost.addHeader("lang_code", Util.getTextofLanguage(SplashScreen.this, Util.SELECTED_LANGUAGE_CODE, Util.DEFAULT_SELECTED_LANGUAGE_CODE));

                // Execute HTTP Post Request
                try {

                    HttpResponse response = httpclient.execute(httppost);
                    responseStr = EntityUtils.toString(response.getEntity());


                } catch (org.apache.http.conn.ConnectTimeoutException e) {

                } catch (IOException e) {
                    isSubscribed = "0";
                }

                Log.v("BIBHU", "responseStr of get profile update =" + responseStr);

                JSONObject myJson = null;
                if (responseStr != null) {
                    myJson = new JSONObject(responseStr);
                    statusCode = Integer.parseInt(myJson.optString("code"));

                }
                if (statusCode > 0) {
                    if (statusCode == 200) {

                        if ((myJson.has("isSubscribed")) && myJson.optString("isSubscribed").trim() != null && !myJson.optString("isSubscribed").trim().isEmpty() && !myJson.optString("isSubscribed").trim().equals("null") && !myJson.optString("isSubscribed").trim().matches("")) {
                            isSubscribed = myJson.getString("isSubscribed");
                        } else {
                            isSubscribed = "0";
                        }
                    } else {
                        isSubscribed = "0";
                    }
                } else {
                    isSubscribed = "0";
                }
            } catch (Exception e) {
                isSubscribed = "0";
            }
            return null;
        }

        protected void onPostExecute(Void result) {


            if (responseStr == null) {
                isSubscribed = "0";
            }

            Call_One_Step_Procedure();

        }

    }

    public void Call_One_Step_Procedure() {

// Have to change for one step registartion


        //============================Added For FCM===========================//

/*
        if(!Util.getTextofLanguage(SplashScreen.this,Util.GOOGLE_FCM_TOKEN,Util.DEFAULT_GOOGLE_FCM_TOKEN).equals("0"))
        {
            Log.v("BIBHU2","google_id already created ="+Util.getTextofLanguage(SplashScreen.this,Util.GOOGLE_FCM_TOKEN,Util.DEFAULT_GOOGLE_FCM_TOKEN));


            Intent i = new Intent(SplashScreen.this, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(i);
            finish();
            overridePendingTransition(0,0);
        }
        else
        {
            GoogleIdGeneraterTimer = new Timer();
            GoogleIdGeneraterTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if(!Util.getTextofLanguage(SplashScreen.this,Util.GOOGLE_FCM_TOKEN,Util.DEFAULT_GOOGLE_FCM_TOKEN).equals("0"))
                    {
                        GoogleIdGeneraterTimer.cancel();
                        GoogleIdGeneraterTimer.purge();

                        Log.v("BIBHU2","google_id="+Util.getTextofLanguage(SplashScreen.this,Util.GOOGLE_FCM_TOKEN,Util.DEFAULT_GOOGLE_FCM_TOKEN) );

                        Intent i = new Intent(SplashScreen.this, MainActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(i);
                        finish();
                        overridePendingTransition(0,0);
                    }
                }
            },0,1000);
        }*/

        //============================End Added For FCM===========================//





        //============================Added For FCM===========================//

        if (!Util.getTextofLanguage(SplashScreen.this, Util.GOOGLE_FCM_TOKEN, Util.DEFAULT_GOOGLE_FCM_TOKEN).equals("0")) {
            Log.v("BIBHU2", "google_id already created =" + Util.getTextofLanguage(SplashScreen.this, Util.GOOGLE_FCM_TOKEN, Util.DEFAULT_GOOGLE_FCM_TOKEN));

                String loggedInStr = pref.getString("PREFS_LOGGEDIN_KEY", null);

            if ((Util.getTextofLanguage(SplashScreen.this, Util.IS_ONE_STEP_REGISTRATION, Util.DEFAULT_IS_ONE_STEP_REGISTRATION)
                    .trim()).equals("1")) {
                if (loggedInStr != null) {
                    if (isSubscribed.trim().equals("1")) {
                        Intent i = new Intent(SplashScreen.this, MainActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(i);
                        finish();
                        overridePendingTransition(0, 0);
                    } else {
                        Intent intent = new Intent(SplashScreen.this, SubscriptionActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(0, 0);
                    }
                } else {

                    final Intent i = new Intent(SplashScreen.this, RegisterActivity.class);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            overridePendingTransition(0, 0);
                            startActivity(i);
                            finish();

                        }
                    });

                }

            } else {

                Intent i = new Intent(SplashScreen.this, MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(i);
                finish();
                overridePendingTransition(0, 0);
            }

        } else {
            GoogleIdGeneraterTimer = new Timer();
            GoogleIdGeneraterTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (!Util.getTextofLanguage(SplashScreen.this, Util.GOOGLE_FCM_TOKEN, Util.DEFAULT_GOOGLE_FCM_TOKEN).equals("0")) {
                        GoogleIdGeneraterTimer.cancel();
                        GoogleIdGeneraterTimer.purge();

                        Log.v("BIBHU2", "google_id=" + Util.getTextofLanguage(SplashScreen.this, Util.GOOGLE_FCM_TOKEN, Util.DEFAULT_GOOGLE_FCM_TOKEN));

                        Intent i = new Intent(SplashScreen.this, MainActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(i);
                        finish();
                        overridePendingTransition(0,0);

                        String loggedInStr = pref.getString("PREFS_LOGGEDIN_KEY", null);

                        if ((Util.getTextofLanguage(SplashScreen.this, Util.IS_ONE_STEP_REGISTRATION, Util.DEFAULT_IS_ONE_STEP_REGISTRATION)
                                .trim()).equals("1")) {
                            if (loggedInStr != null) {
                                if (isSubscribed.trim().equals("1")) {
                                    Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                    startActivity(intent);
                                    finish();
                                    overridePendingTransition(0, 0);
                                } else {
                                    Intent intent = new Intent(SplashScreen.this, SubscriptionActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                    startActivity(intent);
                                    finish();
                                    overridePendingTransition(0, 0);
                                }
                            } else {
                                final Intent intent = new Intent(SplashScreen.this, RegisterActivity.class);
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                        overridePendingTransition(0,0);
                                        startActivity(intent);
                                        finish();

                                    }
                                });
                            }

                        } else {
                            Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            startActivity(intent);
                            finish();
                            overridePendingTransition(0, 0);
                        }
                    }
                }
            }, 0, 1000);
        }

        //============================End Added For FCM===========================//
    }


    public void checkDownLoadStatusFromDownloadManager1(final ContactModel1 model) {


        if (model.getDOWNLOADID() != 0) {


            new Thread(new Runnable() {

                @Override
                public void run() {

                    downloading = true;
                    //  Util.downloadprogress=0;
                    int bytes_downloaded = 0;
                    int bytes_total = 0;
                    downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                    while (downloading) {


                        DownloadManager.Query q = new DownloadManager.Query();
                        q.setFilterById(model.getDOWNLOADID()); //filter by id which you have receieved when reqesting download from download manager
                        Cursor cursor = downloadManager.query(q);


                        if (cursor != null && cursor.getCount() > 0) {
                            if (cursor.moveToFirst()) {
                                int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
                                int status = cursor.getInt(columnIndex);
                                if (status == DownloadManager.STATUS_SUCCESSFUL) {

                                    model.setDSTATUS(1);
                                    dbHelper.updateRecord(model);

                                } else if (status == DownloadManager.STATUS_FAILED) {
                                    // 1. process for download fail.
                                    model.setDSTATUS(0);

                                } else if ((status == DownloadManager.STATUS_PAUSED) ||
                                        (status == DownloadManager.STATUS_RUNNING)) {
                                    model.setDSTATUS(2);

                                } else if (status == DownloadManager.STATUS_PENDING) {
                                    //Not handling now
                                }
                                int sizeIndex = cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES);
                                int downloadedIndex = cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR);
                                long size = cursor.getInt(sizeIndex);
                                long downloaded = cursor.getInt(downloadedIndex);
                                double progress = 0.0;
                                if (size != -1) progress = downloaded * 100.0 / size;
                                // At this point you have the progress as a percentage.
                                model.setProgress((int) progress);
                                //Util.downloadprogress=(int) progress;

                                if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
                                    //downloading = false;
//                                download_layout.setVisibility(View.GONE);
                                    //writefilepath();
                                    //String path=Environment.getExternalStorageDirectory() + "/WITHDRM/"+fname;

//                                    String path1 = Environment.getExternalStorageDirectory() + "/Android/data/"+getApplicationContext().getPackageName().trim()+"/WITHDRM/" + Util.dataModel.getVideoTitle().trim() + "-1." + "mlv";
//                                    File file = new File(path1);
//                                    if (file != null && file.exists()) {
//
//                                        file.delete();
//
//                                    }

                                }


                            }
                        } else {
                            // model.setDSTATUS(3);
                        }


//


                        // Log.d(Constants.MAIN_VIEW_ACTIVITY, statusMessage(cursor));
                        cursor.close();
                    }


                }
            }).start();


        }

    }
}