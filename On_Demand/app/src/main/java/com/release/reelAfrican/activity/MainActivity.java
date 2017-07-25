package com.release.reelAfrican.activity;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.cast.MediaInfo;
import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.cast.framework.CastSession;
import com.google.android.gms.cast.framework.CastState;
import com.google.android.gms.cast.framework.CastStateListener;
import com.google.android.gms.cast.framework.IntroductoryOverlay;
import com.google.android.gms.cast.framework.SessionManagerListener;
import com.google.android.gms.cast.framework.media.RemoteMediaClient;
import com.release.reelAfrican.R;
import com.release.reelAfrican.adapter.LanguageCustomAdapter;
import com.release.reelAfrican.expandedcontrols.ExpandedControlsActivity;
import com.release.reelAfrican.model.LanguageModel;
import com.release.reelAfrican.model.NavDrawerItem;
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

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import io.fabric.sdk.android.Fabric;

import static com.release.reelAfrican.R.id.action_mydownload;


public class MainActivity extends ActionBarActivity implements FragmentDrawer.FragmentDrawerListener {


    public MainActivity() {
    }

   /*** chromecast**************/

    public enum PlaybackLocation {
        LOCAL,
        REMOTE
    }


    public enum PlaybackState {
        PLAYING, PAUSED, BUFFERING, IDLE
    }





    private Timer mControllersTimer;
    private PlaybackLocation mLocation;
    private PlaybackState mPlaybackState;
    private final float mAspectRatio = 72f / 128;
    private AQuery mAquery;
    private MediaInfo mSelectedMedia;


    private CastContext mCastContext;
    private SessionManagerListener<CastSession> mSessionManagerListener =
            new MySessionManagerListener();
    private CastSession mCastSession;
    private MenuItem mediaRouteMenuItem;
    private IntroductoryOverlay mIntroductoryOverlay;
    private CastStateListener mCastStateListener;

    private class MySessionManagerListener implements SessionManagerListener<CastSession> {

        @Override
        public void onSessionEnded(CastSession session, int error) {
            if (session == mCastSession) {
                mCastSession = null;
            }
            invalidateOptionsMenu();
        }

        @Override
        public void onSessionResumed(CastSession session, boolean wasSuspended) {
            mCastSession = session;
            invalidateOptionsMenu();
        }

        @Override
        public void onSessionStarted(CastSession session, String sessionId) {
            mCastSession = session;
            invalidateOptionsMenu();
        }

        @Override
        public void onSessionStarting(CastSession session) {
        }

        @Override
        public void onSessionStartFailed(CastSession session, int error) {
        }

        @Override
        public void onSessionEnding(CastSession session) {
        }

        @Override
        public void onSessionResuming(CastSession session, String sessionId) {
        }

        @Override
        public void onSessionResumeFailed(CastSession session, int error) {
        }

        @Override
        public void onSessionSuspended(CastSession session, int reason) {
        }
    }


    /*** chromecast**************/



    public static int vertical = 0;
    private String lang_code = "";
    int check = 0;
    public static int isNavigated = 0;
    String Default_Language = "";
    public static ArrayList<NavDrawerItem> menuList;
    private String imageUrlStr;
   // public static SharedPreferences dataPref;
    int state=0;
    LanguageCustomAdapter languageCustomAdapter;
    public static ProgressBarHandler internetSpeedDialog;
    SharedPreferences pref;
    //Load on background thread
    /*Asynctask on background thread*/
    int corePoolSize = 60;
    int maximumPoolSize = 80;
    int keepAliveTime = 10;
    BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<Runnable>(maximumPoolSize);
    Executor threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, workQueue);

    //Toolbar
    private Toolbar mToolbar;
    private FragmentDrawer drawerFragment;
    private RelativeLayout noInternetLayout;
    public static String internetSpeed ;
    Fragment fragment = null;
    private ProgressBarHandler pDialog = null;
    String loggedInStr,loginHistoryIdStr,email,id;

    AsynLoadImageUrls as = null;
    AsynLoadMenuItems asynLoadMenuItems = null;
    int isLogin = 0;

    public static int planIdOfStudios = 3;
    int  prevPosition = 0;


    AlertDialog alert;
    String Previous_Selected_Language="";
    TextView noInternetTextView;
    SharedPreferences isLoginPref;
    public static ProgressBarHandler progressBarHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());

        setContentView(R.layout.activity_main);
        if (menuList != null && menuList.size() > 0){
            menuList.clear();
        }


        /*Set Toolbar*/
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

      /**** chromecast*************/

        mCastStateListener = new CastStateListener() {
            @Override
            public void onCastStateChanged(int newState) {
                if (newState != CastState.NO_DEVICES_AVAILABLE) {

                    showIntroductoryOverlay();
                }
            }
        };

        mCastContext = CastContext.getSharedInstance(this);
        mCastContext.registerLifecycleCallbacksBeforeIceCreamSandwich(this, savedInstanceState);



        // int startPosition = getInt("startPosition", 0);
        // mVideoView.setVideoURI(Uri.parse(item.getContentId()));

        setupCastListener();
        mCastSession = mCastContext.getSessionManager().getCurrentCastSession();


        /**** chromecast*************/


        isLoginPref = getSharedPreferences(Util.IS_LOGIN_SHARED_PRE, 0); // 0 - for private mode

        isLogin = isLoginPref.getInt(Util.IS_LOGIN_PREF_KEY, 0);
        // dataPref = getApplicationContext().getSharedPreferences("DrawerState", 0);


    /*    SharedPreferences countryPref = getSharedPreferences(Util.COUNTRY_PREF, 0); // 0 - for private mode
        if (countryPref != null) {
            String countryCodeStr = countryPref.getString("countryCode", null);
            if (countryCodeStr != null) {
                Global globalVariable = (Global) getApplicationContext();
                globalVariable.setCountryCode(countryCodeStr);
            }
        }*/

        noInternetLayout = (RelativeLayout) findViewById(R.id.noInternet);
        noInternetTextView = (TextView) findViewById(R.id.noInternetTextView);
        noInternetTextView.setText(Util.getTextofLanguage(MainActivity.this,Util.NO_INTERNET_NO_DATA,Util.DEFAULT_NO_INTERNET_NO_DATA));
        noInternetLayout.setVisibility(View.GONE);

        boolean isNetwork = Util.checkNetwork(MainActivity.this);
        if (isNetwork == true ) {
            if (asynLoadMenuItems != null){
                asynLoadMenuItems = null;
            }
            asynLoadMenuItems = new AsynLoadMenuItems();
            asynLoadMenuItems.executeOnExecutor(threadPoolExecutor);

        }else{
            noInternetLayout.setVisibility(View.VISIBLE);
            DrawerLayout dl =  (DrawerLayout) findViewById(R.id.drawer_layout);
            dl.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }

       /* if (getIntent().getIntExtra("isLogin",0) != 0){

            Global globalVariable = (Global) getApplicationContext();
            globalVariable.setIsLogin(getIntent().getIntExtra("isLogin",0));

        }*/


        //subhashree genre
       /* Bundle b=this.getIntent().getExtras();

        String[] genreArray = new String[0];
        String[] genreValuesArray = new String[0];
        try {
            genreArray = b.getStringArray("genreArray");
            genreValuesArray = b.getStringArray("genreValueArray");


        Global globalVariable = (Global) getApplicationContext();
        // isLogin = ((Global) getApplicationContext()).getIsLogin();

        if (genreArray.length > 0){
            globalVariable.setGenreArray(genreArray);

        }
        if (genreValuesArray.length > 0){
            globalVariable.setGenreValueArray(genreValuesArray);

        }
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        Log.v("SUBHA","Default_Language"+Default_Language);
        Log.v("SUBHA","Default_Language"+Default_Language);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_main, menu);
     /************chromecast***********/
       /* mediaRouteMenuItem = CastButtonFactory.setUpMediaRouteButton(getApplicationContext(), menu,
                R.id.media_route_menu_item);
        showIntroductoryOverlay();
*/
       /************chromecast***********/


        MenuItem item,item1,item2,item3,item4,item5,item6,item7;
        item= menu.findItem(R.id.action_filter);
        item.setVisible(false);
        pref = getSharedPreferences(Util.LOGIN_PREF, 0);
        loggedInStr = pref.getString("PREFS_LOGGEDIN_KEY", null);
        id = pref.getString("PREFS_LOGGEDIN_ID_KEY", null);

        email=pref.getString("PREFS_LOGIN_EMAIL_ID_KEY", null);
        SharedPreferences language_list_pref = getSharedPreferences(Util.LANGUAGE_LIST_PREF, 0);
        (menu.findItem(R.id.menu_item_language)).setTitle(Util.getTextofLanguage(MainActivity.this, Util.LANGUAGE_POPUP_LANGUAGE, Util.DEFAULT_LANGUAGE_POPUP_LANGUAGE));

        if(language_list_pref.getString("total_language","0").equals("1"))
            (menu.findItem(R.id.menu_item_language)).setVisible(false);

        if(loggedInStr!=null){
            item4= menu.findItem(R.id.action_login);
            item4.setTitle(Util.getTextofLanguage(MainActivity.this,Util.LANGUAGE_POPUP_LOGIN,Util.DEFAULT_LANGUAGE_POPUP_LOGIN));
            item4.setVisible(false);
            item5= menu.findItem(R.id.action_register);
            item5.setTitle(Util.getTextofLanguage(MainActivity.this,Util.BTN_REGISTER,Util.DEFAULT_BTN_REGISTER));
            item5.setVisible(false);
          /*  item6= menu.findItem(R.id.menu_item_language);
            item6.setTitle(Util.getTextofLanguage(MainActivity.this,Util.LANGUAGE_POPUP_LANGUAGE,Util.DEFAULT_LANGUAGE_POPUP_LANGUAGE));
            item6.setVisible(true);*/
            item1 = menu.findItem(R.id.menu_item_profile);
            item1.setTitle(Util.getTextofLanguage(MainActivity.this,Util.PROFILE,Util.DEFAULT_PROFILE));
            item1.setVisible(true);

            item2 = menu.findItem(R.id.action_purchage);
            item2.setTitle(Util.getTextofLanguage(MainActivity.this,Util.PURCHASE_HISTORY,Util.DEFAULT_PURCHASE_HISTORY));
            item2.setVisible(true);

            item3 = menu.findItem(R.id.action_logout);
            item3.setTitle(Util.getTextofLanguage(MainActivity.this,Util.LOGOUT,Util.DEFAULT_LOGOUT));
            item3.setVisible(true);

            item6 = menu.findItem(action_mydownload);
            item6.setTitle(Util.getTextofLanguage(MainActivity.this,Util.MY_DOWNLOAD,Util.DEFAULT_MY_DOWNLOAD));
            item6.setVisible(true);

            item7 = menu.findItem(R.id.menu_item_favorite);
            item7.setTitle(Util.getTextofLanguage(this,Util.MY_FAVOURITE,Util.DEFAULT_MY_FAVOURITE));
            item7.setVisible(true);


        }else if(loggedInStr==null){
            item4= menu.findItem(R.id.action_login);
            item4.setTitle(Util.getTextofLanguage(MainActivity.this,Util.LANGUAGE_POPUP_LOGIN,Util.DEFAULT_LANGUAGE_POPUP_LOGIN));


            item5= menu.findItem(R.id.action_register);
            item5.setTitle(Util.getTextofLanguage(MainActivity.this,Util.BTN_REGISTER,Util.DEFAULT_BTN_REGISTER));
            if(isLogin == 1)
            {
                item4.setVisible(true);
                item5.setVisible(true);

            }else{
                item4.setVisible(false);
                item5.setVisible(false);

            }
            item6 = menu.findItem(action_mydownload);
            item6.setTitle(Util.getTextofLanguage(MainActivity.this,Util.MY_DOWNLOAD,Util.DEFAULT_MY_DOWNLOAD));
            item6.setVisible(false);
           /* item6= menu.findItem(R.id.menu_item_language);
            item6.setTitle(Util.getTextofLanguage(MainActivity.this,Util.LANGUAGE_POPUP_LANGUAGE,Util.DEFAULT_LANGUAGE_POPUP_LANGUAGE));
            item6.setVisible(true);*/
            item1 = menu.findItem(R.id.menu_item_profile);
            item1.setTitle(Util.getTextofLanguage(MainActivity.this,Util.PROFILE,Util.DEFAULT_PROFILE));
            item1.setVisible(false);
            item2= menu.findItem(R.id.action_purchage);
            item2.setTitle(Util.getTextofLanguage(MainActivity.this,Util.PURCHASE_HISTORY,Util.DEFAULT_PURCHASE_HISTORY));
            item2.setVisible(false);
            item3= menu.findItem(R.id.action_logout);
            item3.setTitle(Util.getTextofLanguage(MainActivity.this,Util.LOGOUT,Util.DEFAULT_LOGOUT));
            item3.setVisible(false);
            item7 = menu.findItem(R.id.menu_item_favorite);
            item7.setTitle(Util.getTextofLanguage(this,Util.MY_FAVOURITE,Util.DEFAULT_MY_FAVOURITE));
            item7.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                final Intent searchIntent = new Intent(MainActivity.this, SearchActivity.class);
                searchIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(searchIntent);
                // Not implemented here
                return false;
            case R.id.action_filter:

                // Not implemented here
                return false;
            case R.id.action_login:

                Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                Util.check_for_subscription = 0;
                startActivity(loginIntent);
                // Not implemented here
                return false;
            case R.id.action_register:

                Intent registerIntent = new Intent(MainActivity.this, RegisterActivity.class);
                Util.check_for_subscription = 0;
                startActivity(registerIntent);
                // Not implemented here
                return false;
            case R.id.menu_item_language:

                // Not implemented here
                Default_Language = Util.getTextofLanguage(MainActivity.this,Util.SELECTED_LANGUAGE_CODE,Util.DEFAULT_SELECTED_LANGUAGE_CODE);
                Previous_Selected_Language =Util.getTextofLanguage(MainActivity.this,Util.SELECTED_LANGUAGE_CODE,Util.DEFAULT_SELECTED_LANGUAGE_CODE);

                if (Util.languageModel!=null && Util.languageModel.size() > 0){


                    ShowLanguagePopup();

                }else {
                    AsynGetLanguageList asynGetLanguageList = new AsynGetLanguageList();
                    asynGetLanguageList.executeOnExecutor(threadPoolExecutor);
                }
                return false;
            case R.id.menu_item_profile:

                Intent profileIntent = new Intent(MainActivity.this, ProfileActivity.class);
                profileIntent.putExtra("EMAIL",email);
                profileIntent.putExtra("LOGID",id);
                startActivity(profileIntent);
                // Not implemented here
                return false;
            case R.id.action_purchage:

               Intent purchaseintent = new Intent(MainActivity.this, PurchaseHistoryActivity.class);
                startActivity(purchaseintent);
                // Not implemented here
                return false;
            case R.id.menu_item_favorite:

                Intent favoriteIntent = new Intent(this, FavoriteActivity.class);
                Log.v("ANUU","going to favorite activity");
//                favoriteIntent.putExtra("EMAIL",email);
//                favoriteIntent.putExtra("LOGID",id);
                favoriteIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(favoriteIntent);
                // Not implemented here
                return false;
            case action_mydownload:

                Intent mydownload = new Intent(MainActivity.this, MyDownloads.class);
                startActivity(mydownload);
                // Not implemented here
                return false;
            case R.id.action_logout:

                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(MainActivity.this, R.style.MyAlertDialogStyle);
                dlgAlert.setMessage(Util.getTextofLanguage(MainActivity.this,Util.SIGN_OUT_WARNING,Util.DEFAULT_SIGN_OUT_WARNING));
                dlgAlert.setTitle("");

                dlgAlert.setPositiveButton(Util.getTextofLanguage(MainActivity.this,Util.YES,Util.DEFAULT_YES) ,new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing but close the dialog

                        // dialog.cancel();
                        AsynLogoutDetails asynLogoutDetails=new AsynLogoutDetails();
                        asynLogoutDetails.executeOnExecutor(threadPoolExecutor);



                        dialog.dismiss();
                    }
                });

                dlgAlert.setNegativeButton(Util.getTextofLanguage(MainActivity.this,Util.NO,Util.DEFAULT_NO), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // Do nothing
                        dialog.dismiss();
                    }
                });
                // dlgAlert.setPositiveButton(getResources().getString(R.string.yes_str), null);
                dlgAlert.setCancelable(false);
           /* dlgAlert.setNegativeButton(getResources().getString(R.string.no_str),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {


                        }
                    })
                    .setNegativeButton(getResources().getString(R.string.no_str),
                            new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    });*/
                dlgAlert.create().show();

               /* Intent lanuageIntent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(registerIntent);*/
                // Not implemented here
                return false;
            default:
                break;
        }

        return false;
    }
    /*************chromecast*****************/
    @Override
    public void onResume() {
        super.onResume();
        mCastContext.addCastStateListener(mCastStateListener);
        mCastContext.getSessionManager().addSessionManagerListener(
                mSessionManagerListener, CastSession.class);
        if (mCastSession == null) {
            mCastSession = CastContext.getSharedInstance(this).getSessionManager()
                    .getCurrentCastSession();
        }
     /*   if (mCastSession == null) {
            mCastSession = CastContext.getSharedInstance(this).getSessionManager()
                    .getCurrentCastSession();
        }



        mCastContext.getSessionManager().addSessionManagerListener(
                mSessionManagerListener, CastSession.class);*/
        //removeFocusFromViews();
        invalidateOptionsMenu();
    }


    /*************chromecast*****************/


  /*  @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }
*/
    //Selection of menu Item
    @Override
    public void onDrawerItemSelected(View view, int position) {
        check = position;
            displayView(position);
    }

    //Display View based on selection of menu item

    private void displayView(int position) {

        isNavigated = 1;

        String title = getString(R.string.app_name);
     /*   SharedPreferences.Editor dataEditor = dataPref.edit();*/
        Bundle bundle = new Bundle();
        String str = menuList.get(position).getPermalink();
        String titleStr = menuList.get(position).getTitle();
       // state = position;

        if (internetSpeedDialog!=null && internetSpeedDialog.isShowing()){
            internetSpeedDialog.hide();
            internetSpeedDialog = null;

        }
        if (pDialog!=null && pDialog.isShowing()){
            pDialog.hide();
            pDialog = null;

        }
        if (str !=null && !str.equalsIgnoreCase("") && !str.isEmpty() && menuList.get(position).getLinkType().equalsIgnoreCase("-101")){

            Log.v("SUBHA","home fragment");
            fragment = new HomeFragment();
            bundle.putString("item", str);


        }
        else if (menuList.get(position).getLinkType().equalsIgnoreCase("102")){

            fragment = new MyLibraryFragment();
            bundle.putString("title",titleStr);

        }
        else if (menuList.get(position).getIsEnabled() == false){

            if(str.equals("contactus"))
            {

                fragment = new ContactUs();
                bundle.putString("title",titleStr);


            }
            else{


                fragment = new AboutUs();
                bundle.putString("item",str);
                bundle.putString("title",titleStr);

            }


        }
        else if (menuList.get(position).getIsEnabled() == true) {
            Log.v("SUBHA","video fragment");
            fragment = new VideosListFragment();
            bundle.putString("item", str);
            bundle.putString("title",titleStr);


        }
       /* else if (menuList.get(position).getIsEnabled() == false) {

            fragment = new WebViewFragment();
            bundle.putString("item", getResources().getString(R.string.studio_site)+str);

        }*/
        fragment.setArguments(bundle);

      /*  dataEditor.putString("state", String.valueOf(state));
                dataEditor.commit();*/



        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body, fragment);
            View view = this.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
            fragmentTransaction.commitAllowingStateLoss();


            // set the toolbar title
            getSupportActionBar().setTitle(title);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // TODO Auto-generated method stub
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed()
    {

        if (asynLoadMenuItems != null){
            asynLoadMenuItems.cancel(true);
        }


        if (as!=null){
            as.cancel(true);
        }

       // Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.container_body);


            super.onBackPressed();


    }
    private class AsynLoadImageUrls extends AsyncTask<Void, Void, Void> {
        String responseStr;
        int statusCode;

        @Override
        protected Void doInBackground(Void... params) {

            try {
                HttpClient httpclient=new DefaultHttpClient();
                HttpPost httppost = new HttpPost(Util.rootUrl()+Util.downloadImageUrl.trim());
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
                            try {
                                if (internetSpeedDialog != null && internetSpeedDialog.isShowing()) {
                                    internetSpeedDialog.hide();
                                    internetSpeedDialog = null;

                                }

                            } catch (IllegalArgumentException ex) {
                                responseStr = "0";
                                noInternetLayout.setVisibility(View.VISIBLE);
                                DrawerLayout dl =  (DrawerLayout) findViewById(R.id.drawer_layout);
                                dl.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                            }

                        }

                    });

                }catch (IOException e) {
                    try {
                        if (internetSpeedDialog != null && internetSpeedDialog.isShowing()) {
                            internetSpeedDialog.hide();
                            internetSpeedDialog = null;

                        }

                    }
                    catch(IllegalArgumentException ex)
                    {
                        responseStr = "0";
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                noInternetLayout.setVisibility(View.VISIBLE);
                                DrawerLayout dl =  (DrawerLayout) findViewById(R.id.drawer_layout);
                                dl.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

                            }
                        });
                        e.printStackTrace();
                    }
                }

                JSONObject myJson =null;
                if(responseStr!=null){
                    myJson = new JSONObject(responseStr);
                    statusCode = Integer.parseInt(myJson.optString("code"));

                }

                if (statusCode > 0) {
                    if (statusCode == 200) {
                        if ((myJson.has("image_url")) && myJson.getString("image_url").trim() != null && !myJson.getString("image_url").trim().isEmpty() && !myJson.getString("image_url").trim().equals("null") && !myJson.getString("image_url").trim().matches("")) {
                            imageUrlStr = myJson.getString("image_url");
                        }
                        else{

                            responseStr = "0";

                        }
                    }
                }
                else {
                    responseStr = "0";
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            noInternetLayout.setVisibility(View.VISIBLE);
                            DrawerLayout dl =  (DrawerLayout) findViewById(R.id.drawer_layout);
                            dl.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

                        }
                    });
                }
            } catch (JSONException e1) {
                try {
                    if (internetSpeedDialog != null && internetSpeedDialog.isShowing()) {
                        internetSpeedDialog.hide();
                        internetSpeedDialog = null;

                    }

                }
                catch(IllegalArgumentException ex)
                {
                    responseStr = "0";
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            noInternetLayout.setVisibility(View.VISIBLE);
                            DrawerLayout dl =  (DrawerLayout) findViewById(R.id.drawer_layout);
                            dl.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

                        }
                    });
                    e1.printStackTrace();
                }
            }

            catch (Exception e)
            {
                try {
                    if (internetSpeedDialog != null && internetSpeedDialog.isShowing()) {
                        internetSpeedDialog.hide();
                        internetSpeedDialog = null;

                    }

                }
                catch(IllegalArgumentException ex)
                {
                    responseStr = "0";
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            noInternetLayout.setVisibility(View.VISIBLE);
                            DrawerLayout dl =  (DrawerLayout) findViewById(R.id.drawer_layout);
                            dl.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

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

                        noInternetLayout.setVisibility(View.VISIBLE);
                        DrawerLayout dl =  (DrawerLayout) findViewById(R.id.drawer_layout);
                        dl.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);


                    }
                });
            }

            if(responseStr == null) {
                boolean isNetwork = Util.checkNetwork(MainActivity.this);
                if (isNetwork == true) {
                    new Thread(mWorker).start();
                }else{
                    internetSpeed = "0";

                }
                drawerFragment = (FragmentDrawer)
                        getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
                drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
                drawerFragment.setDrawerListener(MainActivity.this);

               /* if (dataPref != null) {

                    String stateStr = dataPref.getString("state", null);

                    if (stateStr !=null) {
                        int stateInt = Integer.parseInt(stateStr);
                        displayView(stateInt);
                    }else{
                        displayView(0);

                    }
                }else{
                    displayView(0);
                }*/
                displayView(0);


            }
            if ((responseStr.trim().equalsIgnoreCase("0"))){
                boolean isNetwork = Util.checkNetwork(MainActivity.this);
                if (isNetwork == true) {
                    new Thread(mWorker).start();
                }else{
                    internetSpeed = "0";
                }
                drawerFragment = (FragmentDrawer)
                        getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
                drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
                drawerFragment.setDrawerListener(MainActivity.this);
               /* if (dataPref != null) {

                    String stateStr = dataPref.getString("state", null);

                    if (stateStr !=null) {
                        int stateInt = Integer.parseInt(stateStr);
                        displayView(stateInt);
                    }else{
                        displayView(0);

                    }
                }else{
                    displayView(0);
                }*/
                displayView(0);


            }else{

                boolean isNetwork = Util.checkNetwork(MainActivity.this);
                if (isNetwork == true) {

                    new Thread(mWorker).start();
                }else{
                    internetSpeed = "0";
                }
                drawerFragment = (FragmentDrawer)
                        getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
                drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
                drawerFragment.setDrawerListener(MainActivity.this);
               /* if (dataPref != null) {

                    String stateStr = dataPref.getString("state", null);

                    if (stateStr !=null) {
                        int stateInt = Integer.parseInt(stateStr);
                        displayView(stateInt);
                    }else{
                        displayView(0);

                    }
                }else{
                    displayView(0);
                }*/
                displayView(0);


            }
if(pDialog!=null && pDialog.isShowing()){
    pDialog.hide();
    pDialog = null;
}
        }

        @Override
        protected void onPreExecute() {
            if (internetSpeedDialog!=null && internetSpeedDialog.isShowing()){
                pDialog = internetSpeedDialog;
            }else{
                pDialog = new ProgressBarHandler(MainActivity.this);
                pDialog.show();
            }

        }


    }

    //logout

    private class AsynLogoutDetails extends AsyncTask<Void, Void, Void> {
        ProgressBarHandler pDialog;
        int responseCode;
        String loginHistoryIdStr = pref.getString("PREFS_LOGIN_HISTORYID_KEY", null);
        String responseStr;

        @Override
        protected Void doInBackground(Void... params) {


            String urlRouteList =Util.rootUrl().trim()+Util.logoutUrl.trim();
            try {
                HttpClient httpclient=new DefaultHttpClient();
                HttpPost httppost = new HttpPost(urlRouteList);
                httppost.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");
                httppost.addHeader("authToken", Util.authTokenStr.trim());
                httppost.addHeader("login_history_id",loginHistoryIdStr);
                httppost.addHeader("lang_code",Util.getTextofLanguage(MainActivity.this,Util.SELECTED_LANGUAGE_CODE,Util.DEFAULT_SELECTED_LANGUAGE_CODE));


                try {
                    HttpResponse response = httpclient.execute(httppost);
                    responseStr = EntityUtils.toString(response.getEntity());
                } catch (org.apache.http.conn.ConnectTimeoutException e){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (pDialog != null && pDialog.isShowing()) {
                                pDialog.hide();
                                pDialog = null;
                            }
                            responseCode = 0;
                            Util.showToast(MainActivity.this, Util.getTextofLanguage(MainActivity.this,Util.SLOW_INTERNET_CONNECTION,Util.DEFAULT_SLOW_INTERNET_CONNECTION));

                          //  Toast.makeText(MainActivity.this,Util.getTextofLanguage(MainActivity.this, Util.SLOW_INTERNET_CONNECTION, Util.DEFAULT_SLOW_INTERNET_CONNECTION),Toast.LENGTH_LONG).show();

                        }

                    });

                }catch (IOException e) {
                    if (pDialog != null && pDialog.isShowing()) {
                        pDialog.hide();
                        pDialog = null;
                    }
                    responseCode = 0;
                    e.printStackTrace();
                }
                if(responseStr!=null){
                    JSONObject myJson = new JSONObject(responseStr);
                    responseCode = Integer.parseInt(myJson.optString("code"));
                }

            }
            catch (Exception e) {
                if (pDialog != null && pDialog.isShowing()) {
                    pDialog.hide();
                    pDialog = null;
                }
                responseCode = 0;

            }

            return null;
        }


        protected void onPostExecute(Void result) {
            try {
                if (pDialog != null && pDialog.isShowing()) {
                    pDialog.hide();
                    pDialog = null;

                }
            } catch (IllegalArgumentException ex) {
                Util.showToast(MainActivity.this, Util.getTextofLanguage(MainActivity.this,Util.SIGN_OUT_ERROR,Util.DEFAULT_SIGN_OUT_ERROR));

                //Toast.makeText(MainActivity.this,Util.getTextofLanguage(MainActivity.this, Util.SIGN_OUT_ERROR, Util.DEFAULT_SIGN_OUT_ERROR),Toast.LENGTH_LONG).show();

            }
            if(responseStr == null){
                Util.showToast(MainActivity.this, Util.getTextofLanguage(MainActivity.this,Util.SIGN_OUT_ERROR,Util.DEFAULT_SIGN_OUT_ERROR));

                //Toast.makeText(MainActivity.this,Util.getTextofLanguage(MainActivity.this, Util.SIGN_OUT_ERROR, Util.DEFAULT_SIGN_OUT_ERROR),Toast.LENGTH_LONG).show();

            }
            if (responseCode == 0) {
                Util.showToast(MainActivity.this, Util.getTextofLanguage(MainActivity.this,Util.SIGN_OUT_ERROR,Util.DEFAULT_SIGN_OUT_ERROR));

//                Toast.makeText(MainActivity.this,Util.getTextofLanguage(MainActivity.this, Util.SIGN_OUT_ERROR, Util.DEFAULT_SIGN_OUT_ERROR),Toast.LENGTH_LONG).show();

            }
            if (responseCode > 0) {
                if (responseCode == 200) {
                    SharedPreferences.Editor editor = pref.edit();
                    editor.clear();
                    editor.commit();
                    SharedPreferences loginPref = getSharedPreferences(Util.LOGIN_PREF, 0); // 0 - for private mode
                    if (loginPref!=null) {
                        SharedPreferences.Editor countryEditor = loginPref.edit();
                        countryEditor.clear();
                        countryEditor.commit();
                    }
                 /*   SharedPreferences countryPref = getSharedPreferences(Util.COUNTRY_PREF, 0); // 0 - for private mode
                    if (countryPref!=null) {
                        SharedPreferences.Editor countryEditor = countryPref.edit();
                        countryEditor.clear();
                        countryEditor.commit();
                    }*/
                    if ((Util.getTextofLanguage(MainActivity.this, Util.IS_ONE_STEP_REGISTRATION, Util.DEFAULT_IS_ONE_STEP_REGISTRATION)
                            .trim()).equals("1")) {
                        final Intent startIntent = new Intent(MainActivity.this, SplashScreen.class);
                        runOnUiThread(new Runnable() {
                            public void run() {
                                startIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                startActivity(startIntent);
                                Util.showToast(MainActivity.this,Util.getTextofLanguage(MainActivity.this, Util.LOGOUT_SUCCESS, Util.DEFAULT_LOGOUT_SUCCESS));
                                finish();

                            }
                        });
                    }
                    else
                    {
                        final Intent startIntent = new Intent(MainActivity.this, MainActivity.class);
                        runOnUiThread(new Runnable() {
                            public void run() {
                                startIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                startActivity(startIntent);
                                Util.showToast(MainActivity.this,Util.getTextofLanguage(MainActivity.this, Util.LOGOUT_SUCCESS, Util.DEFAULT_LOGOUT_SUCCESS));
                                finish();

                            }
                        });
                    }

                }
                else {
                    Util.showToast(MainActivity.this, Util.getTextofLanguage(MainActivity.this,Util.SIGN_OUT_ERROR,Util.DEFAULT_SIGN_OUT_ERROR));

                  //  Toast.makeText(MainActivity.this,Util.getTextofLanguage(MainActivity.this, Util.SIGN_OUT_ERROR, Util.DEFAULT_SIGN_OUT_ERROR),Toast.LENGTH_LONG).show();

                }
            }

        }

        @Override
        protected void onPreExecute() {

            pDialog = new ProgressBarHandler(MainActivity.this);
            pDialog.show();
        }
    }
    //=========
    private class AsynLoadMenuItems extends AsyncTask<Void, Void, Void> {
        String responseStr;
        int statusCode;
        String permalink = null;
        String displayName = null;
        String menuLinkType= null;
        @Override
        protected Void doInBackground(Void... params) {
            try {
                HttpClient httpclient=new DefaultHttpClient();
                HttpPost httppost = new HttpPost(Util.rootUrl().trim()+Util.loadMenuUrl.trim());
                httppost.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");
                httppost.addHeader("authToken", Util.authTokenStr.trim());
                SharedPreferences countryPref = getSharedPreferences(Util.COUNTRY_PREF, 0); // 0 - for private mode
                if (countryPref != null) {
                    String countryCodeStr = countryPref.getString("countryCode", null);
                    httppost.addHeader("country", countryCodeStr);
                }else{
                    httppost.addHeader("country", "IN");

                }
                httppost.addHeader("lang_code",Util.getTextofLanguage(MainActivity.this,Util.SELECTED_LANGUAGE_CODE,Util.DEFAULT_SELECTED_LANGUAGE_CODE));

                // Execute HTTP Post Request
                try {

                    HttpResponse response = httpclient.execute(httppost);
                    responseStr = EntityUtils.toString(response.getEntity());


                } catch (org.apache.http.conn.ConnectTimeoutException e){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (internetSpeedDialog != null && internetSpeedDialog.isShowing()) {
                                internetSpeedDialog.hide();
                                internetSpeedDialog = null;

                            }
                            responseStr = "0";
                            menuList = null;

                            noInternetLayout.setVisibility(View.VISIBLE);
                            DrawerLayout dl = (DrawerLayout) findViewById(R.id.drawer_layout);
                            dl.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                        }

                    });

                }catch (IOException e) {
                    if (internetSpeedDialog != null && internetSpeedDialog.isShowing()) {
                        internetSpeedDialog.hide();
                        internetSpeedDialog = null;
                    }
                    responseStr = "0";
                    menuList = null;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            noInternetLayout.setVisibility(View.VISIBLE);
                            DrawerLayout dl = (DrawerLayout) findViewById(R.id.drawer_layout);
                            dl.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

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
                        menuList = new ArrayList<NavDrawerItem>();
                        JSONArray jsonMainNode = myJson.getJSONArray("menu");
                        JSONArray jsonFooterNode = myJson.getJSONArray("footer_menu");
                        int jsonFooterNodeArr = jsonFooterNode.length();

                        int lengthJsonArr = jsonMainNode.length();
                        for(int i=0; i < lengthJsonArr; i++) {
                            JSONObject jsonChildNode;
                            try {
                                jsonChildNode = jsonMainNode.getJSONObject(i);

                                if ((jsonChildNode.has("display_name")) && jsonChildNode.getString("display_name").trim() != null && !jsonChildNode.getString("display_name").trim().isEmpty() && !jsonChildNode.getString("display_name").trim().equals("null") && !jsonChildNode.getString("display_name").trim().matches("")) {
                                    displayName = jsonChildNode.getString("display_name");

                                }


                                if ((jsonChildNode.has("link_type")) && jsonChildNode.getString("link_type").trim() != null && !jsonChildNode.getString("link_type").trim().isEmpty() && !jsonChildNode.getString("link_type").trim().equals("null") && !jsonChildNode.getString("link_type").trim().matches("")) {
                                    menuLinkType = jsonChildNode.getString("link_type");

                                }else{
                                    menuLinkType = "0";
                                }

                                if (menuLinkType!=null && !menuLinkType.equalsIgnoreCase("")) {
                                    if ((jsonChildNode.has("permalink")) && jsonChildNode.getString("permalink").trim() != null && !jsonChildNode.getString("permalink").trim().isEmpty() && !jsonChildNode.getString("permalink").trim().equals("null") && !jsonChildNode.getString("permalink").trim().matches("")) {
                                        permalink = jsonChildNode.getString("permalink");
                                    }
                                }
                                if (menuLinkType!=null && !menuLinkType.equalsIgnoreCase("") && menuLinkType.equalsIgnoreCase("0")) {
                                    menuList.add(new NavDrawerItem(displayName, permalink,true,menuLinkType));
                                }




                            } catch (Exception e) {
                               runOnUiThread(new Runnable() {
                                   @Override
                                   public void run() {

                                       noInternetLayout.setVisibility(View.VISIBLE);
                                       DrawerLayout dl = (DrawerLayout) findViewById(R.id.drawer_layout);
                                       dl.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

                                   }
                               });
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }

                        menuList.add(new NavDrawerItem(Util.getTextofLanguage(MainActivity.this,Util.MY_LIBRARY,Util.DEFAULT_MY_LIBRARY),"102",true,"102"));


                        /*** footer menu******/
                        for(int i=0; i < jsonFooterNodeArr; i++) {
                            JSONObject jsonChildNode;
                            try {
                                jsonChildNode = jsonFooterNode.getJSONObject(i);

                                if ((jsonChildNode.has("display_name")) && jsonChildNode.getString("display_name").trim() != null && !jsonChildNode.getString("display_name").trim().isEmpty() && !jsonChildNode.getString("display_name").trim().equals("null") && !jsonChildNode.getString("display_name").trim().matches("")) {
                                    displayName = jsonChildNode.getString("display_name");

                                }
                                if ((jsonChildNode.has("url")) && jsonChildNode.getString("url").trim() != null && !jsonChildNode.getString("url").trim().isEmpty() && !jsonChildNode.getString("url").trim().equals("null") && !jsonChildNode.getString("url").trim().matches("")) {
                                    menuLinkType = jsonChildNode.getString("url");

                                }
                                if ((jsonChildNode.has("permalink")) && jsonChildNode.getString("permalink").trim() != null && !jsonChildNode.getString("permalink").trim().isEmpty() && !jsonChildNode.getString("permalink").trim().equals("null") && !jsonChildNode.getString("permalink").trim().matches("")) {
                                    permalink = jsonChildNode.getString("permalink");
                                }
                                if (menuLinkType!=null && !menuLinkType.equalsIgnoreCase("")) {
                                    menuList.add(new NavDrawerItem(displayName, permalink,false,menuLinkType));
                                }
                            }
                            catch (Exception e) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        noInternetLayout.setVisibility(View.VISIBLE);
                                        DrawerLayout dl = (DrawerLayout) findViewById(R.id.drawer_layout);
                                        dl.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

                                    }
                                });
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }

                        }


                        /**********footer**********/


                    }
                }
                else {

                    responseStr = "0";
                    menuList = null;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            noInternetLayout.setVisibility(View.VISIBLE);
                            DrawerLayout dl = (DrawerLayout) findViewById(R.id.drawer_layout);
                            dl.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

                        }
                    });
                }
            } catch (JSONException e1) {

                if (internetSpeedDialog != null && internetSpeedDialog.isShowing()) {
                    internetSpeedDialog.hide();
                    internetSpeedDialog = null;
                }
                responseStr = "0";
                menuList = null;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        noInternetLayout.setVisibility(View.VISIBLE);
                        DrawerLayout dl = (DrawerLayout) findViewById(R.id.drawer_layout);
                        dl.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

                    }
                });
                e1.printStackTrace();
            }

            catch (Exception e)

            {

                if (internetSpeedDialog != null && internetSpeedDialog.isShowing()) {
                    internetSpeedDialog.hide();
                    internetSpeedDialog = null;

                }
                responseStr = "0";
                menuList = null;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        noInternetLayout.setVisibility(View.VISIBLE);
                        DrawerLayout dl = (DrawerLayout) findViewById(R.id.drawer_layout);
                        dl.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

                    }
                });
                e.printStackTrace();

            }
            return null;

        }

        protected void onPostExecute(Void result) {


            try{

            }
            catch(IllegalArgumentException ex)
            {

                responseStr = "0";
                menuList = null;

                noInternetLayout.setVisibility(View.VISIBLE);
                DrawerLayout dl = (DrawerLayout) findViewById(R.id.drawer_layout);
                dl.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

            }

            if(responseStr == null) {

                responseStr = "0";
                menuList = null;

                noInternetLayout.setVisibility(View.VISIBLE);
                DrawerLayout dl = (DrawerLayout) findViewById(R.id.drawer_layout);
                dl.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

            }

            if ((responseStr.trim().equalsIgnoreCase("0"))){
                menuList = null;

                        noInternetLayout.setVisibility(View.VISIBLE);
                        DrawerLayout dl = (DrawerLayout) findViewById(R.id.drawer_layout);
                        dl.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);



            }else{
                if(menuList!=null && menuList.size()>0){
                    if (as != null){
                        as = null;
                    }

                    menuList.add(0,new NavDrawerItem(Util.getTextofLanguage(MainActivity.this, Util.HOME, Util.DEFAULT_HOME),"-101",true,"-101"));
                  //  menuList.add(menuList.size()-2,new NavDrawerItem(Util.getTextofLanguage(MainActivity.this,Util.MY_LIBRARY,Util.DEFAULT_MY_LIBRARY),"102",true,"102"));

                    // menuList.add(new NavDrawerItem("Home", "",true,"0"));
                   /* menuList.add(new NavDrawerItem("Terms", "",false,"-1"));
                    menuList.add(new NavDrawerItem("About", "",false,"-1"));
*/


                    boolean isNetwork = Util.checkNetwork(MainActivity.this);
                    imageUrlStr = "https://dadc-muvi.s3-eu-west-1.amazonaws.com/check-download-speed.jpg";
                    if (isNetwork == true) {

                        new Thread(mWorker).start();
                    }else{
                        internetSpeed = "0";
                    }
                    if (internetSpeedDialog != null && internetSpeedDialog.isShowing()) {
                        internetSpeedDialog.hide();
                        internetSpeedDialog = null;

                    }
                    drawerFragment = (FragmentDrawer)
                            getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
                    drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
                    drawerFragment.setDrawerListener(MainActivity.this);
                    displayView(0);

                  /*  as = new AsynLoadImageUrls();
                    as.executeOnExecutor(threadPoolExecutor);*/
                }else{

                    noInternetLayout.setVisibility(View.VISIBLE);
                    DrawerLayout dl = (DrawerLayout) findViewById(R.id.drawer_layout);
                    dl.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                }
            }
        }

        @Override
        protected void onPreExecute() {
            try {
             /*   internetSpeedDialog = new ProgressDialog(MainActivity.this);
                internetSpeedDialog.setMessage(getResources().getString(R.string.loading_str));
                internetSpeedDialog.setIndeterminate(false);
                internetSpeedDialog.setCancelable(false);
                internetSpeedDialog.show();*/

                internetSpeedDialog = new ProgressBarHandler(MainActivity.this);
                internetSpeedDialog.show();


            }catch (IllegalArgumentException ex){

                noInternetLayout.setVisibility(View.VISIBLE);
                DrawerLayout dl =  (DrawerLayout) findViewById(R.id.drawer_layout);
                dl.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            }
         /*  if (internetSpeedDialog == null || !internetSpeedDialog.isShowing()){
               internetSpeedDialog = new ProgressDialog(MainActivity.this);
               internetSpeedDialog.setMessage(getResources().getString(R.string.loading_str));
               internetSpeedDialog.setIndeterminate(false);
               internetSpeedDialog.setCancelable(false);
               internetSpeedDialog.show();
           }*/
        }


    }
    public void ShowLanguagePopup()
    {

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this,R.style.MyAlertDialogStyle);
        LayoutInflater inflater = (LayoutInflater)getSystemService(MainActivity.this.LAYOUT_INFLATER_SERVICE);

        View convertView = (View) inflater.inflate(R.layout.language_pop_up, null);
        TextView titleTextView = (TextView) convertView.findViewById(R.id.languagePopupTitle);
        titleTextView.setText(Util.getTextofLanguage(MainActivity.this,Util.APP_SELECT_LANGUAGE,Util.DEFAULT_APP_SELECT_LANGUAGE));

        alertDialog.setView(convertView);
        alertDialog.setTitle("");

        RecyclerView recyclerView = (RecyclerView) convertView.findViewById(R.id.language_recycler_view);
        Button apply = (Button) convertView.findViewById(R.id.apply_btn);
        apply.setText(Util.getTextofLanguage(MainActivity.this,Util.BUTTON_APPLY,Util.DEFAULT_BUTTON_APPLY));

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

        languageCustomAdapter = new LanguageCustomAdapter(MainActivity.this, Util.languageModel);
       // Util.languageModel.get(0).setSelected(true);
      /*  if (Util.languageModel.get(i).getLanguageId().equalsIgnoreCase(Util.getTextofLanguage(MainActivity.this, Util.SELECTED_LANGUAGE_CODE, Util.DEFAULT_SELECTED_LANGUAGE_CODE))) {
            prevPosition = i;
            Util.languageModel.get(i).setSelected(true);

        }
        Util.languageModel.get(0).setSelected(true);*/

        recyclerView.setAdapter(languageCustomAdapter);



    /*    for (int i = 0 ; i < Util.languageModel.size() - 1 ; i ++){
                if (Util.languageModel.get(i).getLanguageId().equalsIgnoreCase(Util.getTextofLanguage(MainActivity.this, Util.SELECTED_LANGUAGE_CODE, Util.DEFAULT_SELECTED_LANGUAGE_CODE))) {
                    prevPosition = i;
                    Util.languageModel.get(i).setSelected(true);
                    break;

            }else {
                prevPosition = 0;

                Util.languageModel.get(0).setSelected(true);
                break;

            }
        }
*/
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener1(MainActivity.this, recyclerView, new ClickListener1() {
            @Override
            public void onClick(View view, int position) {
                Util.itemclicked = true;

                Util.languageModel.get(position).setSelected(true);


                if (prevPosition != position) {
                    Util.languageModel.get(prevPosition).setSelected(false);
                    prevPosition = position;

                }

                Default_Language = Util.languageModel.get(position).getLanguageId();



                Util.setLanguageSharedPrefernce(MainActivity.this,Util.SELECTED_LANGUAGE_CODE,Util.languageModel.get(position).getLanguageId());
                languageCustomAdapter.notifyDataSetChanged();

               // Default_Language = Util.languageModel.get(position).getLanguageId();
             /*   AsynGetTransalatedLanguage asynGetTransalatedLanguage = new AsynGetTransalatedLanguage();
                asynGetTransalatedLanguage.executeOnExecutor(threadPoolExecutor);*/



                // new LanguageAsyncTask(new Get).executeOnExecutor(threadPoolExecutor);

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();




                    AsynGetTransalatedLanguage asynGetTransalatedLanguage = new AsynGetTransalatedLanguage();
                    asynGetTransalatedLanguage.executeOnExecutor(threadPoolExecutor);


            }
        });


        alert = alertDialog.show();


        alert.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                Util.setLanguageSharedPrefernce(MainActivity.this,Util.SELECTED_LANGUAGE_CODE,Previous_Selected_Language);
            }
        });

    }
    public static class RecyclerTouchListener1 implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private ClickListener1 clickListener;

        public RecyclerTouchListener1(Context context, final RecyclerView recyclerView, final ClickListener1 clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }
    public interface ClickListener1 {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }


    private class AsynGetLanguageList extends AsyncTask<Void, Void, Void> {
        String responseStr;
        int status;


        @Override
        protected Void doInBackground(Void... params) {

          String urlRouteList =Util.rootUrl().trim()+Util.LanguageList.trim();
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(urlRouteList);
                httppost.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");
                httppost.addHeader("authToken", Util.authTokenStr.trim());


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
                        if(!Util.getTextofLanguage(MainActivity.this,Util.SELECTED_LANGUAGE_CODE,"").equals(""))
                        {
                            Default_Language = Util.getTextofLanguage(MainActivity.this,Util.SELECTED_LANGUAGE_CODE,Util.DEFAULT_SELECTED_LANGUAGE_CODE);
                        }

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

            if(progressBarHandler != null && progressBarHandler.isShowing())
            {
                progressBarHandler.hide();
                progressBarHandler = null;

            }

            if (responseStr == null) {
                noInternetLayout.setVisibility(View.GONE);
            } else {
                if (status > 0 && status == 200) {

                    try {
                        JSONObject json = new JSONObject(responseStr);
                        JSONArray jsonArray = json.getJSONArray("lang_list");
                          ArrayList<LanguageModel> languageModels = new ArrayList<LanguageModel>();

                        for (int i = 0; i < jsonArray.length(); i++) {
                            String language_id = jsonArray.getJSONObject(i).optString("code").trim();
                            String language_name = jsonArray.getJSONObject(i).optString("language").trim();


                            LanguageModel languageModel = new LanguageModel();
                            languageModel.setLanguageId(language_id);
                            languageModel.setLanguageName(language_name);

                            if(Default_Language.equalsIgnoreCase(language_id))
                            {
                                languageModel.setIsSelected(true);
                            }
                            else
                            {
                                languageModel.setIsSelected(false);
                            }
                            languageModels.add(languageModel);
                        }

                        Util.languageModel = languageModels;


                    } catch (JSONException e) {
                        e.printStackTrace();
                        noInternetLayout.setVisibility(View.GONE);
                    }


                 /*   if(!Default_Language.equals("en")) {
                        //                  Call For Language Translation.
                        AsynGetTransalatedLanguage asynGetTransalatedLanguage = new AsynGetTransalatedLanguage();
                        asynGetTransalatedLanguage.executeOnExecutor(threadPoolExecutor);

                    }else{

                    }*/

                } else {
                    noInternetLayout.setVisibility(View.GONE);
                }
            }
            ShowLanguagePopup();


        }

        protected void onPreExecute() {

            progressBarHandler = new ProgressBarHandler(MainActivity.this);
            progressBarHandler.show();

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

            if(progressBarHandler!=null && progressBarHandler.isShowing())
            {
                progressBarHandler.hide();
                progressBarHandler = null;

            }

            if (responseStr == null) {
                noInternetLayout.setVisibility(View.GONE);
            } else {
                if (status > 0 && status == 200) {

                    try {
                        JSONObject parent_json = new JSONObject(responseStr);
                        JSONObject json = parent_json.getJSONObject("translation");



                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.ALREADY_MEMBER,json.optString("already_member").trim());
                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.ACTIAVTE_PLAN_TITLE,json.optString("activate_plan_title").trim());
                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.TRANSACTION_STATUS_ACTIVE,json.optString("transaction_status_active").trim());
                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.ADD_TO_FAV,json.optString("add_to_fav").trim());
                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.ADDED_TO_FAV,json.optString("added_to_fav").trim());

                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.ADVANCE_PURCHASE,json.optString("advance_purchase").trim());
                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.ALERT,json.optString("alert").trim());
                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.EPISODE_TITLE,json.optString("episodes_title").trim());
                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.SORT_ALPHA_A_Z,json.optString("sort_alpha_a_z").trim());
                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.SORT_ALPHA_Z_A,json.optString("sort_alpha_z_a").trim());

                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.AMOUNT,json.optString("amount").trim());
                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.COUPON_CANCELLED,json.optString("coupon_cancelled").trim());
                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.BUTTON_APPLY,json.optString("btn_apply").trim());
                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.SIGN_OUT_WARNING,json.optString("sign_out_warning").trim());
                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.DISCOUNT_ON_COUPON,json.optString("discount_on_coupon").trim());

                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.CREDIT_CARD_CVV_HINT,json.optString("credit_card_cvv_hint").trim());
                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.CAST,json.optString("cast").trim());
                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.CAST_CREW_BUTTON_TITLE,json.optString("cast_crew_button_title").trim());
                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.CENSOR_RATING,json.optString("censor_rating").trim());
                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.ENTER_EMPTY_FIELD,json.optString("enter_register_fields_data").trim());

                        Util.setLanguageSharedPrefernce(MainActivity.this, Util.ENTER_REGISTER_FIELDS_DATA, json.optString("enter_register_fields_data").trim());

                        if(json.optString("change_password").trim()==null || json.optString("change_password").trim().equals("")) {
                            Util.setLanguageSharedPrefernce(MainActivity.this, Util.CHANGE_PASSWORD, Util.DEFAULT_CHANGE_PASSWORD);
                        }
                        else {
                            Util.setLanguageSharedPrefernce(MainActivity.this, Util.CHANGE_PASSWORD, json.optString("change_password").trim());
                        }

                        if(json.optString("add_a_review").trim()==null || json.optString("add_a_review").trim().equals("")) {
                            Util.setLanguageSharedPrefernce(MainActivity.this, Util.ADD_A_REVIEW, Util.DEFAULT_ADD_A_REVIEW);
                        }
                        else {
                            Util.setLanguageSharedPrefernce(MainActivity.this, Util.ADD_A_REVIEW, json.optString("add_a_review").trim());
                        }
                        if(json.optString("submit_your_rating_title").trim()==null || json.optString("submit_rating").trim().equals("")) {
                            Util.setLanguageSharedPrefernce(MainActivity.this, Util.SUBMIT_YOUR_RATING_TITLE, Util.DEFAULT_SUBMIT_YOUR_RATING_TITLE);
                        }
                        else {
                            Util.setLanguageSharedPrefernce(MainActivity.this, Util.SUBMIT_YOUR_RATING_TITLE, json.optString("submit_rating").trim());
                        }
                        if(json.optString("reviews").trim()==null || json.optString("reviews").trim().equals("")) {
                            Util.setLanguageSharedPrefernce(MainActivity.this, Util.REVIEWS, Util.DEFAULT_REVIEWS);
                        }
                        else {
                            Util.setLanguageSharedPrefernce(MainActivity.this, Util.REVIEWS, json.optString("reviews").trim());
                        }
                        if(json.optString("click_here").trim()==null || json.optString("click_here").trim().equals("")) {
                            Util.setLanguageSharedPrefernce(MainActivity.this, Util.CLICK_HERE, Util.DEFAULT_CLICK_HERE);
                        }
                        else {
                            Util.setLanguageSharedPrefernce(MainActivity.this, Util.CLICK_HERE, json.optString("click_here").trim());
                        }
                        if(json.optString("to_login").trim()==null || json.optString("to_login").trim().equals("")) {
                            Util.setLanguageSharedPrefernce(MainActivity.this, Util.TO_LOGIN, Util.DEFAULT_TO_LOGIN);
                        }
                        else {
                            Util.setLanguageSharedPrefernce(MainActivity.this, Util.TO_LOGIN, json.optString("to_login").trim());
                        }
                        if(json.optString("need_login_to_review").trim()==null || json.optString("need_login_to_review").trim().equals("")) {
                            Util.setLanguageSharedPrefernce(MainActivity.this, Util.NEED_LOGIN_TO_REVIEW, Util.DEFAULT_NEED_LOGIN_TO_REVIEW);
                        }
                        else {
                            Util.setLanguageSharedPrefernce(MainActivity.this, Util.NEED_LOGIN_TO_REVIEW, json.optString("need_login_to_review").trim());
                        }
                        if(json.optString("btn_post_review").trim()==null || json.optString("btn_post_review").trim().equals("")) {
                            Util.setLanguageSharedPrefernce(MainActivity.this, Util.BTN_POST_REVIEW, Util.DEFAULT_BTN_POST_REVIEW);
                        }
                        else {
                            Util.setLanguageSharedPrefernce(MainActivity.this, Util.BTN_POST_REVIEW, json.optString("btn_post_review").trim());
                        }
                        if(json.optString("login_facebook").trim()==null || json.optString("login_facebook").trim().equals("")) {
                            Util.setLanguageSharedPrefernce(MainActivity.this, Util.LOGIN_FACEBOOK, Util.DEFAULT_LOGIN_FACEBOOK);
                        }
                        else {
                            Util.setLanguageSharedPrefernce(MainActivity.this, Util.LOGIN_FACEBOOK, json.optString("login_facebook").trim());
                        }
                        if(json.optString("enter_review_here").trim()==null || json.optString("enter_review_here").trim().equals("")) {
                            Util.setLanguageSharedPrefernce(MainActivity.this, Util.ENTER_REVIEW_HERE, Util.DEFAULT_ENTER_REVIEW_HERE);
                        }
                        else {
                            Util.setLanguageSharedPrefernce(MainActivity.this, Util.ENTER_REVIEW_HERE, json.optString("enter_review_here").trim());
                        }

                        if(json.optString("register_facebook").trim()==null || json.optString("register_facebook").trim().equals("")) {
                            Util.setLanguageSharedPrefernce(MainActivity.this, Util.REGISTER_FACEBOOK, Util.DEFAULT_REGISTER_FACEBOOK);
                        }
                        else {
                            Util.setLanguageSharedPrefernce(MainActivity.this, Util.REGISTER_FACEBOOK, json.optString("register_facebook").trim());
                        }


                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.CONFIRM_PASSWORD,json.optString("confirm_password").trim());
                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.CREDIT_CARD_DETAILS,json.optString("credit_card_detail").trim());
                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.DIRECTOR,json.optString("director").trim());
                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.DOWNLOAD_BUTTON_TITLE,json.optString("download_button_title").trim());
                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.DESCRIPTION,json.optString("description").trim());

                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.EMAIL_EXISTS,json.optString("email_exists").trim());
                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.EMAIL_DOESNOT_EXISTS,json.optString("email_does_not_exist").trim());
                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.EMAIL_PASSWORD_INVALID,json.optString("email_password_invalid").trim());
                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.COUPON_CODE_HINT,json.optString("coupon_code_hint").trim());
                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.SEARCH_ALERT,json.optString("search_alert").trim());

                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.CREDIT_CARD_NUMBER_HINT,json.optString("credit_card_number_hint").trim());
                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.TEXT_EMIAL,json.optString("text_email").trim());
                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.NAME_HINT,json.optString("name_hint").trim());
                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.CREDIT_CARD_NAME_HINT,json.optString("credit_card_name_hint").trim());
                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.TEXT_PASSWORD,json.optString("text_password").trim());

                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.ERROR_IN_PAYMENT_VALIDATION,json.optString("error_in_payment_validation").trim());
                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.ERROR_IN_REGISTRATION,json.optString("error_in_registration").trim());
                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.TRANSACTION_STATUS_EXPIRED,json.optString("transaction_status_expired").trim());
                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.DETAILS_NOT_FOUND_ALERT,json.optString("details_not_found_alert").trim());
                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.HOME,json.optString("home").trim());

                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.FAILURE,json.optString("failure").trim());
                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.FILTER_BY,json.optString("filter_by").trim());
                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.FORGOT_PASSWORD,json.optString("forgot_password").trim());
                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.GENRE,json.optString("genre").trim());

                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.AGREE_TERMS,json.optString("agree_terms").trim());
                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.INVALID_COUPON,json.optString("invalid_coupon").trim());
                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.INVOICE,json.optString("invoice").trim());
                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.LANGUAGE_POPUP_LANGUAGE,json.optString("language_popup_language").trim());
                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.SORT_LAST_UPLOADED,json.optString("sort_last_uploaded").trim());

                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.LANGUAGE_POPUP_LOGIN,json.optString("language_popup_login").trim());
                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.LOGIN,json.optString("login").trim());
                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.LOGOUT,json.optString("logout").trim());
                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.LOGOUT_SUCCESS,json.optString("logout_success").trim());
                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.MY_FAVOURITE,json.optString("my_favourite").trim());

                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.NEW_PASSWORD,json.optString("new_password").trim());
                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.NEW_HERE_TITLE,json.optString("new_here_title").trim());
                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.NO,json.optString("no").trim());
                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.NO_DATA,json.optString("no_data").trim());
                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.NO_INTERNET_CONNECTION,json.optString("no_internet_connection").trim());

                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.NO_INTERNET_NO_DATA,json.optString("no_internet_no_data").trim());
                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.NO_DETAILS_AVAILABLE,json.optString("no_details_available").trim());
                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.BUTTON_OK,json.optString("btn_ok").trim());
                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.OLD_PASSWORD,json.optString("old_password").trim());
                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.OOPS_INVALID_EMAIL,json.optString("oops_invalid_email").trim());

                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.ORDER,json.optString("order").trim());
                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.TRANSACTION_DETAILS_ORDER_ID,json.optString("transaction_detail_order_id").trim());
                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.PASSWORD_RESET_LINK,json.optString("password_reset_link").trim());
                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.PASSWORDS_DO_NOT_MATCH,json.optString("password_donot_match").trim());
                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.PAY_BY_PAYPAL,json.optString("pay_by_paypal").trim());

                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.BTN_PAYNOW,json.optString("btn_paynow").trim());
                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.PAY_WITH_CREDIT_CARD,json.optString("pay_with_credit_card").trim());
                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.PAYMENT_OPTIONS_TITLE,json.optString("payment_options_title").trim());
                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.PLAN_NAME,json.optString("plan_name").trim());
                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.ACTIVATE_SUBSCRIPTION_WATCH_VIDEO,json.optString("activate_subscription_watch_video").trim());

                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.COUPON_ALERT,json.optString("coupon_alert").trim());
                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.VALID_CONFIRM_PASSWORD,json.optString("valid_confirm_password").trim());
                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.PROFILE,json.optString("profile").trim());
                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.PROFILE_UPDATED,json.optString("profile_updated").trim());

                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.PURCHASE,json.optString("purchase").trim());
                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.TRANSACTION_DETAIL_PURCHASE_DATE,json.optString("transaction_detail_purchase_date").trim());
                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.PURCHASE_HISTORY,json.optString("purchase_history").trim());
                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.BTN_REGISTER,json.optString("btn_register").trim());
                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.SORT_RELEASE_DATE,json.optString("sort_release_date").trim());

                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.SAVE_THIS_CARD,json.optString("save_this_card").trim());
                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.TEXT_SEARCH_PLACEHOLDER,json.optString("text_search_placeholder").trim());
                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.SEASON,json.optString("season").trim());
                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.SELECT_OPTION_TITLE,json.optString("select_option_title").trim());
                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.SELECT_PLAN,json.optString("select_plan").trim());

                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.SIGN_UP_TITLE,json.optString("signup_title").trim());
                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.SLOW_INTERNET_CONNECTION,json.optString("slow_internet_connection").trim());
                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.SLOW_ISSUE_INTERNET_CONNECTION,json.optString("slow_issue_internet_connection").trim());
                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.SORRY,json.optString("sorry").trim());
                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.GEO_BLOCKED_ALERT,json.optString("geo_blocked_alert").trim());

                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.SIGN_OUT_ERROR,json.optString("sign_out_error").trim());
                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.ALREADY_PURCHASE_THIS_CONTENT,json.optString("already_purchase_this_content").trim());
                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.CROSSED_MAXIMUM_LIMIT,json.optString("crossed_max_limit_of_watching").trim());
                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.SORT_BY,json.optString("sort_by").trim());
                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.STORY_TITLE,json.optString("story_title").trim());

                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.BTN_SUBMIT,json.optString("btn_submit").trim());
                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.TRANSACTION_STATUS,json.optString("transaction_success").trim());
                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.VIDEO_ISSUE,json.optString("video_issue").trim());
                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.NO_CONTENT,json.optString("no_content").trim());
                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.NO_VIDEO_AVAILABLE,json.optString("no_video_available").trim());

                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.CONTENT_NOT_AVAILABLE_IN_YOUR_COUNTRY,json.optString("content_not_available_in_your_country").trim());
                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.TRANSACTION_DATE,json.optString("transaction_date").trim());
                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.TRANASCTION_DETAIL,json.optString("transaction_detail").trim());
                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.TRANSACTION_STATUS,json.optString("transaction_status").trim());
                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.TRANSACTION,json.optString("transaction").trim());

                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.TRY_AGAIN,json.optString("try_again").trim());
                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.UNPAID,json.optString("unpaid").trim());
                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.USE_NEW_CARD,json.optString("use_new_card").trim());
                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.VIEW_MORE,json.optString("view_more").trim());
                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.VIEW_TRAILER,json.optString("view_trailer").trim());

                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.WATCH,json.optString("watch").trim());
                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.WATCH_NOW,json.optString("watch_now").trim());
                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.SIGN_OUT_ALERT,json.optString("sign_out_alert").trim());
                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.UPDATE_PROFILE_ALERT,json.optString("update_profile_alert").trim());
                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.YES,json.optString("yes").trim());

                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.PURCHASE_SUCCESS_ALERT,json.optString("purchase_success_alert").trim());
                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.CARD_WILL_CHARGE,json.optString("card_will_charge").trim());
                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.SEARCH_HINT,json.optString("search_hint").trim());
                        Util.setLanguageSharedPrefernce(MainActivity.this, Util.TERMS, json.optString("terms").trim());
                        Util.setLanguageSharedPrefernce(MainActivity.this, Util.UPDATE_PROFILE, json.optString("btn_update_profile").trim());
                        Util.setLanguageSharedPrefernce(MainActivity.this, Util.APP_ON, json.optString("app_on").trim());
                        Util.setLanguageSharedPrefernce(MainActivity.this, Util.APP_SELECT_LANGUAGE, json.optString("app_select_language").trim());

                        Util.setLanguageSharedPrefernce(MainActivity.this, Util.CANCEL_BUTTON, json.optString("btn_cancel").trim());
                        Util.setLanguageSharedPrefernce(MainActivity.this, Util.RESUME_MESSAGE, json.optString("resume_watching").trim());
                        Util.setLanguageSharedPrefernce(MainActivity.this, Util.CONTINUE_BUTTON, json.optString("continue").trim());

                        Util.setLanguageSharedPrefernce(MainActivity.this, Util.FILL_FORM_BELOW, json.optString("Fill_form_below").trim());
                        Util.setLanguageSharedPrefernce(MainActivity.this, Util.MESSAGE, json.optString("text_message").trim());
                        Util.getTextofLanguage(MainActivity.this, Util.PURCHASE, Util.DEFAULT_PURCHASE);
                        Util.setLanguageSharedPrefernce(MainActivity.this, Util.SELECTED_LANGUAGE_CODE, Default_Language);



                        Util.setLanguageSharedPrefernce(MainActivity.this, Util.FILL_FORM_BELOW, json.optString("fill_form_below").trim());
                        Util.setLanguageSharedPrefernce(MainActivity.this, Util.MESSAGE, json.optString("text_message").trim());
                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.MY_LIBRARY,json.optString("my_library").trim());



                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.SIMULTANEOUS_LOGOUT_SUCCESS_MESSAGE,json.optString("logged_out_from_all_devices").trim());
                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.ANDROID_VERSION,json.optString("android_version").trim());
                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.MANAGE_DEVICE,json.optString("manage_device").trim());
                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.YOUR_DEVICE,json.optString("your_device").trim());
                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.DEREGISTER,json.optString("deregister").trim());
                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.LOGIN_STATUS_MESSAGE,json.optString("oops_you_have_no_access").trim());
                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.UPADTE_TITLE,json.optString("update_title").trim());
                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.UPADTE_MESSAGE,json.optString("update_message").trim());
                        //Call For Language PopUp Dialog


                        //Language for offline viewing

                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.MY_DOWNLOAD,json.optString("my_download").trim());
                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.DELETE_BTN,json.optString("delete_btn").trim());
                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.STOP_SAVING_THIS_VIDEO,json.optString("stop_saving_this_video").trim());
                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.YOUR_VIDEO_WONT_BE_SAVED,json.optString("your_video_can_not_be_saved").trim());
                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.BTN_KEEP,json.optString("btn_keep").trim());
                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.BTN_DISCARD,json.optString("btn_discard").trim());
                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.DOWNLOAD_CANCELLED,json.optString("download_cancelled").trim());
                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.WANT_TO_DOWNLOAD,json.optString("want_to_download").trim());
                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.WANT_TO_DELETE,json.optString("want_to_delete").trim());
                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.VIEW_LESS,json.optString("view_less").trim());



                        //Language for voucher

                        Util.setLanguageSharedPrefernce(MainActivity.this, Util.VOUCHER_CODE, json.optString("voucher_code").trim());
                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.VOUCHER_BLANK_MESSAGE,json.optString("voucher_vaildate_message").trim());
                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.VOUCHER_SUCCESS,json.optString("voucher_applied_success").trim());
                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.BTN_NEXT,json.optString("btn_next").trim());
                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.FREE_FOR_COUPON,json.optString("free_for_coupon").trim());
                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.SELECT_PURCHASE_TYPE,json.optString("select_purchase_type").trim());
                        Util.setLanguageSharedPrefernce(MainActivity.this,Util.COMPLETE_SEASON,json.optString("complete_season").trim());

                        languageCustomAdapter.notifyDataSetChanged();

                        Intent intent = new Intent(MainActivity.this,MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);



                    } catch (JSONException e) {
                        e.printStackTrace();
                        noInternetLayout.setVisibility(View.GONE);
                    }
                    // Call For Other Methods.


                } else {
                    noInternetLayout.setVisibility(View.GONE);
                }
            }



        }
        protected void onPreExecute() {
            progressBarHandler = new ProgressBarHandler(MainActivity.this);
            progressBarHandler.show();
        }
    }

    @Override
    public void onUserLeaveHint() {
        super.onUserLeaveHint();
        /*List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
        if (fragmentList != null) {
            //TODO: Perform your logic to pass back press here
            for(Fragment fragment : fragmentList){
                if(fragment instanceof HomeFragment){
                    ((HomeFragment)fragment).myOnKeyDown();
                    ActivityCompat.finishAffinity(this);
                    finish();
                    System.exit(0);
                }
                else if(fragment instanceof VideosListFragment){
                    ((VideosListFragment)fragment).myOnKeyDown();
                    ActivityCompat.finishAffinity(this);
                    finish();
                    System.exit(0);
                }
            }
        }*/
        if (asynLoadMenuItems!=null){
            asynLoadMenuItems.cancel(true);
        }
        if (as!=null){
            as.cancel(true);
        }
        if (isNavigated == 0){
            if (internetSpeedDialog != null && internetSpeedDialog.isShowing()) {
                internetSpeedDialog.hide();
                internetSpeedDialog = null;

            }

            ActivityCompat.finishAffinity(this);
            finish();
            System.exit(0);
        }




      /*  if (internetSpeedDialog!=null && internetSpeedDialog.isShowing()){
            internetSpeedDialog.dismiss();
        }
        ActivityCompat.finishAffinity(this);
        finish();
        System.exit(0);*/

    }

    /*@Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if ((event.getKeyCode() == KeyEvent.KEYCODE_BACK) || (event.getKeyCode() == KeyEvent.KEYCODE_HOME)) {
            *//*((HomeFragment)fragments.get(0)).myOnKeyDown(keyCode);
            ((EventListFragment)fragments.get(1)).myOnKeyDown(keyCode);*//*
            //and so on...
        }
        return super.dispatchKeyEvent(event);
    }*/

   /* @Override
    public boolean disP(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) || (keyCode == KeyEvent.KEYCODE_HOME)) {

            //and so on...
        }
        return super.onKeyDown(keyCode, event);
    }
*/
    private final Handler mHandler=new Handler(){
        @Override
        public void handleMessage(final Message msg) {
            boolean isNetwork = Util.checkNetwork(MainActivity.this);
            if (isNetwork == true) {
                switch (msg.what) {
                    case MSG_UPDATE_STATUS:
                        break;
                    case MSG_UPDATE_CONNECTION_TIME:

                        break;
                    case MSG_COMPLETE_STATUS:
                        final SpeedInfo info2 = (SpeedInfo) msg.obj;
                        String downloadedSpeed = String.format("%.1f", info2.megabits);
                        internetSpeed = downloadedSpeed;

                        break;
                    default:
                        internetSpeed = "0";

                        super.handleMessage(msg);
                }
            }else{
                internetSpeed = "0";

            }
        }
    };

    /**
     * Our Slave worker that does actually all the work
     */
    private final Runnable mWorker=new Runnable(){
        @Override
        public void run() {
            InputStream stream = null;

            try {
                int bytesIn = 0;
                boolean isNetwork = Util.checkNetwork(MainActivity.this);
                String downloadFileUrl = imageUrlStr;
                long startCon = System.currentTimeMillis();
                URL url = new URL(downloadFileUrl);
                URLConnection con = url.openConnection();
                con.setUseCaches(false);
                long connectionLatency = System.currentTimeMillis() - startCon;
                stream = con.getInputStream();

                Message msgUpdateConnection = Message.obtain(mHandler, MSG_UPDATE_CONNECTION_TIME);
                msgUpdateConnection.arg1 = (int) connectionLatency;
                mHandler.sendMessage(msgUpdateConnection);

                long start = System.currentTimeMillis();
                int currentByte = 0;
                long updateStart = System.currentTimeMillis();
                long updateDelta = 0;
                int bytesInThreshold = 0;

                while ((currentByte = stream.read()) != -1) {
                    bytesIn++;
                    bytesInThreshold++;

                    if (updateDelta >= UPDATE_THRESHOLD) {
                        int progress = (int) ((bytesIn / (double) EXPECTED_SIZE_IN_BYTES) * 100);

                        Message msg = Message.obtain(mHandler, MSG_UPDATE_STATUS, calculate(updateDelta, bytesInThreshold));
                        msg.arg1 = progress;
                        msg.arg2 = bytesIn;
                        mHandler.sendMessage(msg);
                        //Reset
                        updateStart = System.currentTimeMillis();
                        bytesInThreshold = 0;
                    }
                    updateDelta = System.currentTimeMillis() - updateStart;
                }

                long downloadTime = (System.currentTimeMillis() - start);
                //Prevent AritchmeticException
                if (downloadTime == 0) {
                    downloadTime = 1;
                }

                Message msg = Message.obtain(mHandler, MSG_COMPLETE_STATUS, calculate(downloadTime, bytesIn));
                msg.arg1 = bytesIn;
                mHandler.sendMessage(msg);
            }
            catch(MalformedURLException e){

                internetSpeed = "0";
            }catch(IOException e){

                internetSpeed = "0";

            }finally{
                try {
                    if (stream != null) {
                        stream.close();

                    }
                } catch (IOException e) {
                    //Suppressed
                    internetSpeed = "0";
                }
            }



        }

    };


    /**
     * Get Network type from download rate
     * @return 0 for Edge and 1 for 3G
     */
    private int networkType(final double kbps){
        int type=1;//3G
        //Check if its EDGE
        if(kbps<EDGE_THRESHOLD){
            type=0;
        }
        return type;
    }

    /**
     *
     * 1 byte = 0.0078125 kilobits
     * 1 kilobits = 0.0009765625 megabit
     *
     * @param downloadTime in miliseconds
     * @param bytesIn number of bytes downloaded
     * @return SpeedInfo containing current speed
     */
    private SpeedInfo calculate(final long downloadTime, final long bytesIn){
        SpeedInfo info=new SpeedInfo();
        //from mil to sec
        long bytespersecond   =(bytesIn / downloadTime) * 1000;
        double kilobits=bytespersecond * BYTE_TO_KILOBIT;
        double megabits=kilobits  * KILOBIT_TO_MEGABIT;
        info.downspeed=bytespersecond;
        info.kilobits=kilobits;
        info.megabits=megabits;

        return info;
    }

    /**
     * Transfer Object
     * @author devil
     *
     */
    private static class SpeedInfo{
        public double kilobits=0;
        public double megabits=0;
        public double downspeed=0;
    }


    //Private fields
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int EXPECTED_SIZE_IN_BYTES = 1048576;//1MB 1024*1024

    private static final double EDGE_THRESHOLD = 176.0;
    private static final double BYTE_TO_KILOBIT = 0.0078125;
    private static final double KILOBIT_TO_MEGABIT = 0.0009765625;

    private final int MSG_UPDATE_STATUS=0;
    private final int MSG_UPDATE_CONNECTION_TIME=1;
    private final int MSG_COMPLETE_STATUS=2;

    private final static int UPDATE_THRESHOLD=300;


    private DecimalFormat mDecimalFormater;

    /**** chromecast*************/

    @Override
    public boolean dispatchKeyEvent(@NonNull KeyEvent event) {
        return mCastContext.onDispatchVolumeKeyEventBeforeJellyBean(event)
                || super.dispatchKeyEvent(event);
    }



    @Override
    protected void onPause() {
        mCastContext.removeCastStateListener(mCastStateListener);
        mCastContext.getSessionManager().removeSessionManagerListener(
                mSessionManagerListener, CastSession.class);
        super.onPause();
    }


    private void showIntroductoryOverlay() {
        if (mIntroductoryOverlay != null) {
            mIntroductoryOverlay.remove();
        }
        if ((mediaRouteMenuItem != null) && mediaRouteMenuItem.isVisible()) {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    mIntroductoryOverlay = new IntroductoryOverlay.Builder(
                            MainActivity.this, mediaRouteMenuItem)
                            .setTitleText(getString(R.string.introducing_cast))
                            .setOverlayColor(R.color.colorPrimary)
                            .setSingleTime()
                            .setOnOverlayDismissedListener(
                                    new IntroductoryOverlay.OnOverlayDismissedListener() {
                                        @Override
                                        public void onOverlayDismissed() {
                                            mIntroductoryOverlay = null;
                                        }
                                    })
                            .build();
                    mIntroductoryOverlay.show();
                }
            });
        }
    }

    private void setupCastListener() {
        mSessionManagerListener = new SessionManagerListener<CastSession>() {

            @Override
            public void onSessionEnded(CastSession session, int error) {
                onApplicationDisconnected();
            }

            @Override
            public void onSessionResumed(CastSession session, boolean wasSuspended) {
                onApplicationConnected(session);
            }

            @Override
            public void onSessionResumeFailed(CastSession session, int error) {
                onApplicationDisconnected();
            }

            @Override
            public void onSessionStarted(CastSession session, String sessionId) {
                onApplicationConnected(session);
            }

            @Override
            public void onSessionStartFailed(CastSession session, int error) {
                onApplicationDisconnected();
            }

            @Override
            public void onSessionStarting(CastSession session) {

            }

            @Override
            public void onSessionEnding(CastSession session) {

            }

            @Override
            public void onSessionResuming(CastSession session, String sessionId) {

            }

            @Override
            public void onSessionSuspended(CastSession session, int reason) {

            }

            private void onApplicationConnected(CastSession castSession) {
                mCastSession = castSession;

                if (null != mSelectedMedia) {
                   /* if (mCastSession != null && mCastSession.isConnected()) {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                watchMovieButton.setText(getResources().getString(R.string.movie_details_cast_now_button_title));
                            }
                        });

                    }*/
                    if (mPlaybackState == PlaybackState.PLAYING) {
                       /* mVideoView.pause();
                        loadRemoteMedia(mSeekbar.getProgress(), true);*/
                        return;
                    } else {

                        mPlaybackState = PlaybackState.IDLE;
                        updatePlaybackLocation(PlaybackLocation.REMOTE);
                    }
                }
                //   updatePlayButton(mPlaybackState);
                invalidateOptionsMenu();
            }

            private void onApplicationDisconnected() {
               /* if (mCastSession != null && mCastSession.isConnected()) {
                    watchMovieButton.setText(getResources().getString(R.string.movie_details_watch_video_button_title));
                }*/
                //watchMovieButton.setText(getResources().getString(R.string.movie_details_watch_video_button_title));
                updatePlaybackLocation(PlaybackLocation.LOCAL);
                mPlaybackState = PlaybackState.IDLE;
                mLocation = PlaybackLocation.LOCAL;

                invalidateOptionsMenu();
            }
        };
    }

    private void updatePlaybackLocation(PlaybackLocation location) {
        mLocation = location;
        if (location == PlaybackLocation.LOCAL) {
            if (mPlaybackState == PlaybackState.PLAYING
                    || mPlaybackState == PlaybackState.BUFFERING) {
                //setCoverArtStatus(null);
                startControllersTimer();
            } else {
                stopControllersTimer();
                //setCoverArtStatus(MediaUtils.getImageUrl(mSelectedMedia, 0));
            }
        } else {
            stopControllersTimer();
            //setCoverArtStatus(MediaUtils.getImageUrl(mSelectedMedia, 0));
            updateControllersVisibility(false);
        }
    }

    private void loadRemoteMedia(int position, boolean autoPlay) {
        if (mCastSession == null) {
            return;
        }
        final RemoteMediaClient remoteMediaClient = mCastSession.getRemoteMediaClient();
        if (remoteMediaClient == null) {
            return;
        }
        remoteMediaClient.addListener(new RemoteMediaClient.Listener() {
            @Override
            public void onStatusUpdated() {
                Intent intent = new Intent(MainActivity.this, ExpandedControlsActivity.class);
                startActivity(intent);
                remoteMediaClient.removeListener(this);
            }

            @Override
            public void onMetadataUpdated() {
            }

            @Override
            public void onQueueStatusUpdated() {
            }

            @Override
            public void onPreloadStatusUpdated() {
            }

            @Override
            public void onSendingRemoteMediaRequest() {
            }
        });
        remoteMediaClient.load(mSelectedMedia, autoPlay, position);
    }

   /* private void setCoverArtStatus(String url) {
        if (url != null) {
            mAquery.id(mCoverArt).image(url);
            mCoverArt.setVisibility(View.VISIBLE);
            mVideoView.setVisibility(View.INVISIBLE);
        } else {
            mCoverArt.setVisibility(View.GONE);
            mVideoView.setVisibility(View.VISIBLE);
        }
    }*/

    private void stopTrickplayTimer() {
     /*   if (mSeekbarTimer != null) {
            mSeekbarTimer.cancel();
        }*/
    }


    private void stopControllersTimer() {
        if (mControllersTimer != null) {
            mControllersTimer.cancel();
        }
    }

    private void startControllersTimer() {
        if (mControllersTimer != null) {
            mControllersTimer.cancel();
        }
        if (mLocation == PlaybackLocation.REMOTE) {
            return;
        }
        mControllersTimer = new Timer();
        mControllersTimer.schedule(new HideControllersTask(), 5000);
    }

    // should be called from the main thread
    private void updateControllersVisibility(boolean show) {
        if (show) {
            getSupportActionBar().show();
            // mControllers.setVisibility(View.VISIBLE);
        } else {
            if (!Util.isOrientationPortrait(this)) {
                getSupportActionBar().hide();
            }
            //  mControllers.setVisibility(View.INVISIBLE);
        }
    }

    private class HideControllersTask extends TimerTask {

        @Override
        public void run() {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    // updateControllersVisibility(false);
                    //  mControllersVisible = false;
                }
            });

        }
    }

   /* public void removeFocusFromViews(){
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
*/
}


