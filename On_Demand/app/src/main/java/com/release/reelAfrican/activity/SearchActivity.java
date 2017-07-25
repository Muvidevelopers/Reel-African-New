package com.release.reelAfrican.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.release.reelAfrican.R;
import com.release.reelAfrican.adapter.VideoFilterAdapter;
import com.release.reelAfrican.model.GridItem;
import com.release.reelAfrican.utils.ProgressBarHandler;
import com.release.reelAfrican.utils.Util;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import io.fabric.sdk.android.Fabric;

import static android.content.res.Configuration.SCREENLAYOUT_SIZE_LARGE;
import static android.content.res.Configuration.SCREENLAYOUT_SIZE_MASK;
import static android.content.res.Configuration.SCREENLAYOUT_SIZE_NORMAL;
import static android.content.res.Configuration.SCREENLAYOUT_SIZE_SMALL;
import static android.content.res.Configuration.SCREENLAYOUT_SIZE_XLARGE;

/*
import com.twotoasters.jazzylistview.JazzyGridView;
import com.twotoasters.jazzylistview.JazzyHelper;
*/

/**
 * Created by user on 28-06-2015.
 */
public class SearchActivity extends AppCompatActivity {
    ProgressBarHandler videoPDialog;
    private static final String BUNDLE_RECYCLER_LAYOUT = "classname.recycler.layout";
    int previousTotal = 0;
    ProgressBarHandler gDialog;

    private boolean mIsScrollingUp;
    private int mLastFirstVisibleItem;
    int scrolledPosition=0;
    boolean scrolling;

    String videoImageStrToHeight;
    int videoHeight = 185;
    int videoWidth = 256;
    SharedPreferences pref;
    GridItem itemToPlay;

    private static int firstVisibleInListview;

    private final String KEY_RECYCLER_STATE = "recycler_state";
    private static Bundle mBundleRecyclerViewState;

    //for no internet

    private RelativeLayout noInternetConnectionLayout;

    //firsttime load
    boolean firstTime = false;

    //data to load videourl
    private String movieUniqueId;
    private String movieStreamUniqueId;
    // String videoUrlStr;
    String videoResolution = "BEST";


    //search
    String searchTextStr;
    boolean isSearched = false;
    private SearchView.SearchAutoComplete theTextArea;



    /* Handling GridView Scrolling*/


    // private int mCurrentTransitionEffect = JazzyHelper.HELIX;

    //no data
    RelativeLayout noDataLayout;

    /*The Data to be posted*/
    int offset = 1;
    int limit = 10;
    int listSize = 0;
    int itemsInServer = 0;

    /*Asynctask on background thread*/
    int corePoolSize = 60;
    int maximumPoolSize = 80;
    int keepAliveTime = 10;
    BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<Runnable>(maximumPoolSize);
    Executor threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, workQueue);

    //Set Context

    //Adapter for GridView
    private VideoFilterAdapter customGridAdapter;
    Toolbar mActionBarToolbar;

    //Model for GridView
    ArrayList<GridItem> itemData = new ArrayList<GridItem>();
    GridLayoutManager mLayoutManager;
    String posterUrl;

    // UI
    private GridView gridView;
    RelativeLayout footerView;
    private String movieVideoUrlStr = "";
    //private String movieThirdPartyUrl = "";
    TextView noDataTextView;
    TextView noInternetTextView;

    public SearchActivity() {
        // Required empty public constructor

    }

    View header;
    private boolean isLoading = false;
    private int lastVisibleItem, totalItemCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());

        setContentView(R.layout.activity_search);
        mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mActionBarToolbar);
        mActionBarToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back));
        mActionBarToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        pref = getSharedPreferences(Util.LOGIN_PREF, 0); // 0 - for private mode

        posterUrl = Util.getTextofLanguage(SearchActivity.this, Util.NO_DATA, Util.DEFAULT_NO_DATA);

        gridView = (GridView) findViewById(R.id.imagesGridView);
       /* gridView.setHasFixedSize(true);
        mLayoutManager = new GridLayoutManager(SearchActivity.this,2);
        gridView.setLayoutManager(mLayoutManager);
        gridView.setItemAnimator(new DefaultItemAnimator());*/

        footerView = (RelativeLayout) findViewById(R.id.loadingPanel);

        noInternetConnectionLayout = (RelativeLayout) findViewById(R.id.noInternet);
        noDataLayout = (RelativeLayout) findViewById(R.id.noData);
        noInternetTextView = (TextView) findViewById(R.id.noInternetTextView);
        noDataTextView = (TextView) findViewById(R.id.noDataTextView);
        noInternetTextView.setText(Util.getTextofLanguage(SearchActivity.this, Util.NO_INTERNET_CONNECTION, Util.DEFAULT_NO_INTERNET_CONNECTION));
        noDataTextView.setText(Util.getTextofLanguage(SearchActivity.this, Util.NO_CONTENT, Util.DEFAULT_NO_CONTENT));

        noInternetConnectionLayout.setVisibility(View.GONE);
        noDataLayout.setVisibility(View.GONE);
        footerView.setVisibility(View.GONE);

        //subhalaxmi

        //Detect Network Connection

        boolean isNetwork = Util.checkNetwork(SearchActivity.this);
        if (isNetwork == false) {
            noInternetConnectionLayout.setVisibility(View.VISIBLE);
            noDataLayout.setVisibility(View.GONE);
            gridView.setVisibility(View.GONE);
            footerView.setVisibility(View.GONE);
        }

        ViewGroup.LayoutParams layoutParams = gridView.getLayoutParams();
        layoutParams.width = RelativeLayout.LayoutParams.MATCH_PARENT; //this is in pixels
        gridView.setLayoutParams(layoutParams);
       /* gridView.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
        gridView.setGravity(Gravity.CENTER_HORIZONTAL);*/
        resetData();



        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                itemToPlay = itemData.get(position);
                String moviePermalink = itemToPlay.getPermalink();
                String movieTypeId = itemToPlay.getVideoTypeId();
                // if searched

                // for tv shows navigate to episodes
                if ((movieTypeId.equalsIgnoreCase("3"))) {
                    if (moviePermalink.matches(Util.getTextofLanguage(SearchActivity.this, Util.NO_DATA, Util.DEFAULT_NO_DATA))) {
                        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(SearchActivity.this, R.style.MyAlertDialogStyle);
                        dlgAlert.setMessage(Util.getTextofLanguage(SearchActivity.this, Util.NO_DETAILS_AVAILABLE, Util.DEFAULT_NO_DETAILS_AVAILABLE));
                        dlgAlert.setTitle(Util.getTextofLanguage(SearchActivity.this, Util.SORRY, Util.DEFAULT_SORRY));
                        dlgAlert.setPositiveButton(Util.getTextofLanguage(SearchActivity.this, Util.BUTTON_OK, Util.DEFAULT_BUTTON_OK), null);
                        dlgAlert.setCancelable(false);
                        dlgAlert.setPositiveButton(Util.getTextofLanguage(SearchActivity.this, Util.BUTTON_OK, Util.DEFAULT_BUTTON_OK),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                        dlgAlert.create().show();
                    } else {

                        final Intent detailsIntent = new Intent(SearchActivity.this, ShowWithEpisodesActivity.class);
                        detailsIntent.putExtra(Util.PERMALINK_INTENT_KEY, moviePermalink);
                        runOnUiThread(new Runnable() {
                            public void run() {
                                detailsIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                startActivity(detailsIntent);
                            }
                        });
                    }

                }

                // for single clips and movies
                else if ((movieTypeId.trim().equalsIgnoreCase("1")) || (movieTypeId.trim().equalsIgnoreCase("2")) || (movieTypeId.trim().equalsIgnoreCase("4"))) {
                    final Intent detailsIntent = new Intent(SearchActivity.this, MovieDetailsActivity.class);

                    if (moviePermalink.matches(Util.getTextofLanguage(SearchActivity.this, Util.NO_DATA, Util.DEFAULT_NO_DATA))) {
                        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(SearchActivity.this, R.style.MyAlertDialogStyle);
                        dlgAlert.setMessage(Util.getTextofLanguage(SearchActivity.this, Util.NO_DETAILS_AVAILABLE, Util.DEFAULT_NO_DETAILS_AVAILABLE));
                        dlgAlert.setTitle(Util.getTextofLanguage(SearchActivity.this, Util.SORRY, Util.DEFAULT_SORRY));
                        dlgAlert.setPositiveButton(Util.getTextofLanguage(SearchActivity.this, Util.BUTTON_OK, Util.DEFAULT_BUTTON_OK), null);
                        dlgAlert.setCancelable(false);
                        dlgAlert.setPositiveButton(Util.getTextofLanguage(SearchActivity.this, Util.BUTTON_OK, Util.DEFAULT_BUTTON_OK),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                        dlgAlert.create().show();
                    } else {
                        detailsIntent.putExtra(Util.PERMALINK_INTENT_KEY, moviePermalink);
                        runOnUiThread(new Runnable() {
                            public void run() {
                                detailsIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                startActivity(detailsIntent);
                            }
                        });
                    }
                }

            }
        });
        gridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

                if (gridView.getLastVisiblePosition() >= itemsInServer - 1) {
                    footerView.setVisibility(View.GONE);
                    return;

                }

                if (view.getId() == gridView.getId()) {
                    final int currentFirstVisibleItem = gridView.getFirstVisiblePosition();

                    if (currentFirstVisibleItem > mLastFirstVisibleItem) {
                        mIsScrollingUp = false;

                    } else if (currentFirstVisibleItem < mLastFirstVisibleItem) {
                        mIsScrollingUp = true;

                    }

                    mLastFirstVisibleItem = currentFirstVisibleItem;
                }
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    scrolling = false;

                } else if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {

                    scrolling = true;

                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {


                if (scrolling == true && mIsScrollingUp == false) {

                    if (firstVisibleItem + visibleItemCount >= totalItemCount) {

                        listSize = itemData.size();
                        if (gridView.getLastVisiblePosition() >= itemsInServer - 1) {
                            return;

                        }
                        offset += 1;
                        boolean isNetwork = Util.checkNetwork(SearchActivity.this);
                        if (isNetwork == true) {

                            // default data
                            AsynLoadSearchVideos asyncSearchLoadVideos = new AsynLoadSearchVideos();
                            asyncSearchLoadVideos.executeOnExecutor(threadPoolExecutor);


                            scrolling = false;

                        }

                    }

                }

            }
        });




       /* AsynLoadVideos asyncLoadVideos = new AsynLoadVideos();
        asyncLoadVideos.executeOnExecutor(threadPoolExecutor);*/
    /*    gridView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }


            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {


                int visibleItemCount = recyclerView.getChildCount();
                int totalItemCount = mLayoutManager.getItemCount();
                int firstVisibleItem = mLayoutManager.findFirstVisibleItemPosition();

                if (isLoading) {
                    if (totalItemCount > previousTotal) {
                        isLoading = false;
                        previousTotal = totalItemCount;
                    }
                }
                if (!isLoading && (totalItemCount - visibleItemCount)
                        <= (firstVisibleItem)) {
                    // End has been reached
                    listSize = itemData.size();
                    if (mLayoutManager.findLastVisibleItemPosition() >= itemsInServer - 1) {
                        footerView.setVisibility(View.GONE);
                        return;

                    }
                    offset += 1;
                    boolean isNetwork = Util.checkNetwork(SearchActivity.this);
                    if (isNetwork == true) {


                        // searched data
                        AsynLoadSearchVideos asyncLoadVideos = new AsynLoadSearchVideos();
                        asyncLoadVideos.executeOnExecutor(threadPoolExecutor);

                    }
                    //isLoading = true;
                }
            }
        });
*/

    }

    @Override
    public void onBackPressed() {
     /*   if (asyncLoadVideos!=null){
            asyncLoadVideos.cancel(true);
        }
        if (asynLoadVideoUrls!=null){
            asynLoadVideoUrls.cancel(true);
        }*/
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        finish();
        overridePendingTransition(0, 0);
        super.onBackPressed();
    }

    //load searched videos
    private class AsynLoadSearchVideos extends AsyncTask<Void, Void, Void> {
        ProgressBarHandler pDialog;
        String responseStr;
        int status;
        String videoGenreStr = Util.getTextofLanguage(SearchActivity.this, Util.NO_DATA, Util.DEFAULT_NO_DATA);
        String videoName = "";
        String videoImageStr = Util.getTextofLanguage(SearchActivity.this, Util.NO_DATA, Util.DEFAULT_NO_DATA);
        String videoPermalinkStr = Util.getTextofLanguage(SearchActivity.this, Util.NO_DATA, Util.DEFAULT_NO_DATA);
        String videoTypeStr = Util.getTextofLanguage(SearchActivity.this, Util.NO_DATA, Util.DEFAULT_NO_DATA);
        String videoTypeIdStr = Util.getTextofLanguage(SearchActivity.this, Util.NO_DATA, Util.DEFAULT_NO_DATA);
        String videoUrlStr = Util.getTextofLanguage(SearchActivity.this, Util.NO_DATA, Util.DEFAULT_NO_DATA);
        String isEpisodeStr = "";
        String movieUniqueIdStr = "";
        String movieStreamUniqueIdStr = "";
        int isConverted = 0;
        int isAPV = 0;
        int isPPV = 0;
        String movieThirdPartyUrl = "";

        @Override
        protected Void doInBackground(Void... params) {

            String urlRouteList = Util.rootUrl().trim() + Util.searchUrl.trim();
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(urlRouteList);
                httppost.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");
                httppost.addHeader("authToken", Util.authTokenStr.trim());
                httppost.addHeader("limit", String.valueOf(limit));
                httppost.addHeader("offset", String.valueOf(offset));
                httppost.addHeader("q", searchTextStr.trim());
                //httppost.addHeader("deviceType", "roku");

                SharedPreferences countryPref = getSharedPreferences(Util.COUNTRY_PREF, 0); // 0 - for private mode
                if (countryPref != null) {
                    String countryCodeStr = countryPref.getString("countryCode", null);
                    httppost.addHeader("country", countryCodeStr);
                }else{
                    httppost.addHeader("country", "IN");

                }                httppost.addHeader("lang_code", Util.getTextofLanguage(SearchActivity.this, Util.SELECTED_LANGUAGE_CODE, Util.DEFAULT_SELECTED_LANGUAGE_CODE));


                // Execute HTTP Post Request
                try {
                    HttpResponse response = httpclient.execute(httppost);
                    responseStr = EntityUtils.toString(response.getEntity());

                } catch (org.apache.http.conn.ConnectTimeoutException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (itemData != null) {
                                noDataLayout.setVisibility(View.GONE);
                                noInternetConnectionLayout.setVisibility(View.GONE);
                                gridView.setVisibility(View.VISIBLE);
                                footerView.setVisibility(View.GONE);

                            } else {
                                noDataLayout.setVisibility(View.GONE);
                                noInternetConnectionLayout.setVisibility(View.VISIBLE);
                                gridView.setVisibility(View.VISIBLE);
                                footerView.setVisibility(View.GONE);
                            }

                            Util.showToast(SearchActivity.this,Util.getTextofLanguage(SearchActivity.this,Util.SLOW_INTERNET_CONNECTION,Util.DEFAULT_SLOW_INTERNET_CONNECTION));

                           // Toast.makeText(SearchActivity.this, Util.getTextofLanguage(SearchActivity.this, Util.SLOW_INTERNET_CONNECTION, Util.DEFAULT_SLOW_INTERNET_CONNECTION), Toast.LENGTH_LONG).show();
                        }

                    });

                } catch (IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            noDataLayout.setVisibility(View.VISIBLE);
                            noInternetConnectionLayout.setVisibility(View.GONE);
                            gridView.setVisibility(View.GONE);
                            footerView.setVisibility(View.GONE);
                        }
                    });
                    e.printStackTrace();
                }

                JSONObject myJson = null;
                if (responseStr != null) {
                    myJson = new JSONObject(responseStr);
                    status = Integer.parseInt(myJson.optString("code"));
                    String items = myJson.optString("item_count");
                    itemsInServer = Integer.parseInt(items);
                }

                if (status > 0) {
                    if (status == 200) {
                        JSONArray jsonMainNode = myJson.getJSONArray("search");
                        int lengthJsonArr = jsonMainNode.length();
                        for (int i = 0; i < lengthJsonArr; i++) {
                            JSONObject jsonChildNode;
                            try {
                                jsonChildNode = jsonMainNode.getJSONObject(i);
                                if ((jsonChildNode.has("thirdparty_url")) && jsonChildNode.getString("thirdparty_url").trim() != null && !jsonChildNode.getString("thirdparty_url").trim().isEmpty() && !jsonChildNode.getString("thirdparty_url").trim().equals("null") && !jsonChildNode.getString("thirdparty_url").trim().matches("")) {
                                    movieThirdPartyUrl = jsonChildNode.getString("thirdparty_url");

                                }
                                if ((jsonChildNode.has("genre")) && jsonChildNode.getString("genre").trim() != null && !jsonChildNode.getString("genre").trim().isEmpty() && !jsonChildNode.getString("genre").trim().equals("null") && !jsonChildNode.getString("genre").trim().matches("")) {
                                    videoGenreStr = jsonChildNode.getString("genre");

                                }
                                if ((jsonChildNode.has("episode_title")) && jsonChildNode.getString("episode_title").trim() != null && !jsonChildNode.getString("episode_title").trim().isEmpty() && !jsonChildNode.getString("episode_title").trim().equals("null") && !jsonChildNode.getString("episode_title").trim().matches("")) {
                                    videoName = jsonChildNode.getString("episode_title");

                                } else {
                                    if ((jsonChildNode.has("name")) && jsonChildNode.getString("name").trim() != null && !jsonChildNode.getString("name").trim().isEmpty() && !jsonChildNode.getString("name").trim().equals("null") && !jsonChildNode.getString("name").trim().matches("")) {
                                        videoName = jsonChildNode.getString("name");

                                    }
                                }
                                if ((jsonChildNode.has("poster_url")) && jsonChildNode.getString("poster_url").trim() != null && !jsonChildNode.getString("poster_url").trim().isEmpty() && !jsonChildNode.getString("poster_url").trim().equals("null") && !jsonChildNode.getString("poster_url").trim().matches("")) {
                                    videoImageStr = jsonChildNode.getString("poster_url");
                                    //videoImageStr = videoImageStr.replace("episode", "original");

                                }
                                if ((jsonChildNode.has("permalink")) && jsonChildNode.getString("permalink").trim() != null && !jsonChildNode.getString("permalink").trim().isEmpty() && !jsonChildNode.getString("permalink").trim().equals("null") && !jsonChildNode.getString("permalink").trim().matches("")) {
                                    videoPermalinkStr = jsonChildNode.getString("permalink");

                                }
                                if ((jsonChildNode.has("display_name")) && jsonChildNode.getString("display_name").trim() != null && !jsonChildNode.getString("display_name").trim().isEmpty() && !jsonChildNode.getString("display_name").trim().equals("null") && !jsonChildNode.getString("display_name").trim().matches("")) {
                                    videoTypeStr = jsonChildNode.getString("display_name");

                                }
                                if ((jsonChildNode.has("content_types_id")) && jsonChildNode.getString("content_types_id").trim() != null && !jsonChildNode.getString("content_types_id").trim().isEmpty() && !jsonChildNode.getString("content_types_id").trim().equals("null") && !jsonChildNode.getString("content_types_id").trim().matches("")) {
                                    videoTypeIdStr = jsonChildNode.getString("content_types_id");

                                }
                                //videoTypeIdStr = "1";

                                if ((jsonChildNode.has("embeddedUrl")) && jsonChildNode.getString("embeddedUrl").trim() != null && !jsonChildNode.getString("embeddedUrl").trim().isEmpty() && !jsonChildNode.getString("embeddedUrl").trim().equals("null") && !jsonChildNode.getString("embeddedUrl").trim().matches("")) {
                                    videoUrlStr = jsonChildNode.getString("embeddedUrl");

                                }
                                if ((jsonChildNode.has("is_episode")) && jsonChildNode.getString("is_episode").trim() != null && !jsonChildNode.getString("is_episode").trim().isEmpty() && !jsonChildNode.getString("is_episode").trim().equals("null") && !jsonChildNode.getString("is_episode").trim().matches("")) {
                                    isEpisodeStr = jsonChildNode.getString("is_episode");

                                }
                                if ((jsonChildNode.has("muvi_uniq_id")) && jsonChildNode.getString("muvi_uniq_id").trim() != null && !jsonChildNode.getString("muvi_uniq_id").trim().isEmpty() && !jsonChildNode.getString("muvi_uniq_id").trim().equals("null") && !jsonChildNode.getString("muvi_uniq_id").trim().matches("")) {
                                    movieUniqueIdStr = jsonChildNode.getString("muvi_uniq_id");

                                }
                                if ((jsonChildNode.has("movie_stream_uniq_id")) && jsonChildNode.getString("movie_stream_uniq_id").trim() != null && !jsonChildNode.getString("movie_stream_uniq_id").trim().isEmpty() && !jsonChildNode.getString("movie_stream_uniq_id").trim().equals("null") && !jsonChildNode.getString("movie_stream_uniq_id").trim().matches("")) {
                                    movieStreamUniqueIdStr = jsonChildNode.getString("movie_stream_uniq_id");

                                }
                                if ((jsonChildNode.has("is_converted")) && jsonChildNode.getString("is_converted").trim() != null && !jsonChildNode.getString("is_converted").trim().isEmpty() && !jsonChildNode.getString("is_converted").trim().equals("null") && !jsonChildNode.getString("is_converted").trim().matches("")) {
                                    isConverted = Integer.parseInt(jsonChildNode.getString("is_converted"));

                                }
                                if ((jsonChildNode.has("is_advance")) && jsonChildNode.getString("is_advance").trim() != null && !jsonChildNode.getString("is_advance").trim().isEmpty() && !jsonChildNode.getString("is_advance").trim().equals("null") && !jsonChildNode.getString("is_advance").trim().matches("")) {
                                    isAPV = Integer.parseInt(jsonChildNode.getString("is_advance"));

                                }
                                if ((jsonChildNode.has("is_ppv")) && jsonChildNode.getString("is_ppv").trim() != null && !jsonChildNode.getString("is_ppv").trim().isEmpty() && !jsonChildNode.getString("is_ppv").trim().equals("null") && !jsonChildNode.getString("is_ppv").trim().matches("")) {
                                    isPPV = Integer.parseInt(jsonChildNode.getString("is_ppv"));

                                }

                                itemData.add(new GridItem(videoImageStr, videoName, "", videoTypeIdStr, videoGenreStr, "", videoPermalinkStr,isEpisodeStr,"","",isConverted,isPPV,isAPV));

                            } catch (Exception e) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        noDataLayout.setVisibility(View.VISIBLE);
                                        noInternetConnectionLayout.setVisibility(View.GONE);
                                        gridView.setVisibility(View.GONE);
                                        footerView.setVisibility(View.GONE);
                                    }
                                });
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                    } else {
                        responseStr = "0";
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                noDataLayout.setVisibility(View.VISIBLE);
                                noInternetConnectionLayout.setVisibility(View.GONE);
                                gridView.setVisibility(View.GONE);
                                footerView.setVisibility(View.GONE);
                            }
                        });
                    }
                }
            } catch (Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        noDataLayout.setVisibility(View.VISIBLE);
                        noInternetConnectionLayout.setVisibility(View.GONE);
                        gridView.setVisibility(View.GONE);
                        footerView.setVisibility(View.GONE);
                    }
                });
                e.printStackTrace();

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
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        noDataLayout.setVisibility(View.VISIBLE);
                        noInternetConnectionLayout.setVisibility(View.GONE);
                        gridView.setVisibility(View.GONE);
                        footerView.setVisibility(View.GONE);
                    }
                });
            }

            if (responseStr == null) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        noDataLayout.setVisibility(View.VISIBLE);
                        noInternetConnectionLayout.setVisibility(View.GONE);
                        gridView.setVisibility(View.GONE);
                        footerView.setVisibility(View.GONE);
                    }
                });
                responseStr = "0";
            }
            if ((responseStr.trim().equals("0"))) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        noDataLayout.setVisibility(View.VISIBLE);
                        noInternetConnectionLayout.setVisibility(View.GONE);
                        gridView.setVisibility(View.GONE);
                        footerView.setVisibility(View.GONE);
                    }
                });
            } else {
                if (itemData.size() <= 0) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            noDataLayout.setVisibility(View.VISIBLE);
                            noInternetConnectionLayout.setVisibility(View.GONE);
                            gridView.setVisibility(View.GONE);
                            footerView.setVisibility(View.GONE);
                        }
                    });

                } else {

                    gridView.setVisibility(View.VISIBLE);
                    noInternetConnectionLayout.setVisibility(View.GONE);
                    noDataLayout.setVisibility(View.GONE);
                   /* videoWidth = 312;
                    videoHeight = 560;*/

                    videoImageStrToHeight = videoImageStr;
                    if (firstTime == true){
                        Picasso.with(SearchActivity.this).load(videoImageStrToHeight
                        ).error(R.drawable.no_image).into(new Target() {

                            @Override
                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                videoWidth = bitmap.getWidth();
                                videoHeight = bitmap.getHeight();
                                AsynLOADUI loadUI = new AsynLOADUI();
                                loadUI.executeOnExecutor(threadPoolExecutor);
                            }

                            @Override
                            public void onBitmapFailed(final Drawable errorDrawable) {
                                videoImageStrToHeight = "https://d2gx0xinochgze.cloudfront.net/public/no-image-a.png";
                                videoWidth = errorDrawable.getIntrinsicWidth();
                                videoHeight = errorDrawable.getIntrinsicHeight();
                                AsynLOADUI loadUI = new AsynLOADUI();
                                loadUI.executeOnExecutor(threadPoolExecutor);

                            }

                            @Override
                            public void onPrepareLoad(final Drawable placeHolderDrawable) {

                            }
                        });

                    }else {
                        AsynLOADUI loadUI = new AsynLOADUI();
                        loadUI.executeOnExecutor(threadPoolExecutor);
                    }

                }
            }
        }

        @Override
        protected void onPreExecute() {
            if (MainActivity.internetSpeedDialog != null && MainActivity.internetSpeedDialog.isShowing()) {
                pDialog = MainActivity.internetSpeedDialog;
            } else {
                pDialog = new ProgressBarHandler(SearchActivity.this);

                if (listSize == 0) {

                    pDialog.show();
                    footerView.setVisibility(View.GONE);
                } else {
                    pDialog.hide();
                    footerView.setVisibility(View.VISIBLE);
                }
            }


        }


    }


    /* @Override
     public boolean onCreateOptionsMenu(Menu menu) {
         MenuInflater inflater = getMenuInflater();
         inflater.inflate(R.menu.menu_search, menu);

         SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
         SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
         searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

         searchView.setIconifiedByDefault(false);
         searchView.setQueryHint(getString(R.string.search_hint_str));


         return true;
     }*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        // Do something that differs the Activity's menu here
        inflater.inflate(R.menu.menu_search, menu);


        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);
        searchView.setIconifiedByDefault(false);
        searchView.setQueryHint(Util.getTextofLanguage(SearchActivity.this, Util.SEARCH_PLACEHOLDER, Util.DEFAULT_SEARCH_PLACEHOLDER));
        searchView.requestFocus();
        int searchImgId = getResources().getIdentifier(String.valueOf(R.id.search_button), null, null);
        ImageView v = (ImageView) searchView.findViewById(searchImgId);

        searchView.setMaxWidth(10000);

        final SearchView.SearchAutoComplete theTextArea = (SearchView.SearchAutoComplete) searchView.findViewById(R.id.search_src_text);
        theTextArea.setBackgroundResource(R.drawable.edit);

        theTextArea.setHint(Util.getTextofLanguage(SearchActivity.this, Util.TEXT_SEARCH_PLACEHOLDER, Util.DEFAULT_TEXT_SEARCH_PLACEHOLDER));
       /* if ((getResources().getConfiguration().screenLayout & SCREENLAYOUT_SIZE_MASK) == SCREENLAYOUT_SIZE_LARGE) {
            theTextArea.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_search_large, 0, 0, 0);
            v.setImageResource(R.drawable.ic_action_search_xlarge);

        }
        else if ((getResources().getConfiguration().screenLayout & SCREENLAYOUT_SIZE_MASK) == SCREENLAYOUT_SIZE_NORMAL) {
            theTextArea.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_search, 0, 0, 0);
            v.setImageResource(R.drawable.ic_action_search_large);

        }
        else if ((getResources().getConfiguration().screenLayout & SCREENLAYOUT_SIZE_MASK) == SCREENLAYOUT_SIZE_SMALL) {
            theTextArea.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_search, 0, 0, 0);
            v.setImageResource(R.drawable.ic_action_search_large);

        }
        else {
            theTextArea.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_search_xlarge, 0, 0, 0);
            v.setImageResource(R.drawable.ic_action_search_xlarge);

        }*/
        theTextArea.setHintTextColor(Color.parseColor("#dadada"));//or any color that you want
        theTextArea.setTextColor(Color.WHITE);

        theTextArea.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    // Your piece of code on keyboard search click
                    String query = theTextArea.getText().toString().trim();
                    if (query.equalsIgnoreCase("") || query == null) {
                        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(SearchActivity.this, R.style.MyAlertDialogStyle);
                        dlgAlert.setMessage(Util.getTextofLanguage(SearchActivity.this, Util.SEARCH_ALERT, Util.DEFAULT_SEARCH_ALERT));
                        dlgAlert.setTitle(Util.getTextofLanguage(SearchActivity.this, Util.SORRY, Util.DEFAULT_SORRY));
                        dlgAlert.setPositiveButton(Util.getTextofLanguage(SearchActivity.this, Util.BUTTON_OK, Util.DEFAULT_BUTTON_OK), null);
                        dlgAlert.setCancelable(false);
                        dlgAlert.setPositiveButton(Util.getTextofLanguage(SearchActivity.this, Util.BUTTON_OK, Util.DEFAULT_BUTTON_OK),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                        dlgAlert.create().show();
                    } else {
                        resetData();
                        firstTime = true;

                        offset = 1;
                        listSize = 0;
                        if (((getResources().getConfiguration().screenLayout & SCREENLAYOUT_SIZE_MASK) == SCREENLAYOUT_SIZE_LARGE) || ((getResources().getConfiguration().screenLayout & SCREENLAYOUT_SIZE_MASK) == SCREENLAYOUT_SIZE_XLARGE)) {
                            limit = 20;
                        } else {
                            limit = 15;
                        }
                        itemsInServer = 0;
                        isLoading = false;
                        searchTextStr = query;
                        if (itemData != null && itemData.size() > 0) {
                            itemData.clear();
                        }
                        boolean isNetwork = Util.checkNetwork(SearchActivity.this);
                        isSearched = true;
                        if (isNetwork == false) {
                            noInternetConnectionLayout.setVisibility(View.VISIBLE);
                            gridView.setVisibility(View.GONE);

                        } else {
                            AsynLoadSearchVideos asyncLoadVideos = new AsynLoadSearchVideos();
                            asyncLoadVideos.executeOnExecutor(threadPoolExecutor);

                        }
                    }
                    return true;
                }
                return false;
            }
        });

        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // TODO Auto-generated method stub

            }
        });


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // TODO Auto-generated method stub
                return false;
            }
        });
        return true;
    }


    private class AsynLOADUI extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }

        protected void onPostExecute(Void result) {
            float density = getResources().getDisplayMetrics().density;

            if (firstTime == true) {
                try {
                    if (videoPDialog != null && videoPDialog.isShowing()) {
                        videoPDialog.hide();
                        videoPDialog = null;
                    }
                } catch (IllegalArgumentException ex) {

                    noDataLayout.setVisibility(View.VISIBLE);
                    noInternetConnectionLayout.setVisibility(View.GONE);
                    gridView.setVisibility(View.GONE);
                    footerView.setVisibility(View.GONE);
                }

                gridView.smoothScrollToPosition(0);
                firstTime = false;
                ViewGroup.LayoutParams layoutParams = gridView.getLayoutParams();
                layoutParams.width = RelativeLayout.LayoutParams.MATCH_PARENT; //this is in pixels
                gridView.setLayoutParams(layoutParams);
                gridView.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
                gridView.setGravity(Gravity.CENTER_HORIZONTAL);

                if ((getResources().getConfiguration().screenLayout & SCREENLAYOUT_SIZE_MASK) == SCREENLAYOUT_SIZE_LARGE) {
                    if (videoWidth > videoHeight) {
                        gridView.setNumColumns(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ? 3 : 3);
                    } else {
                        gridView.setNumColumns(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ? 4 : 4);
                    }

                } else if ((getResources().getConfiguration().screenLayout & SCREENLAYOUT_SIZE_MASK) == SCREENLAYOUT_SIZE_NORMAL) {
                    if (videoWidth > videoHeight) {
                        gridView.setNumColumns(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ? 2 : 2);
                    } else {
                        gridView.setNumColumns(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ? 3 : 3);
                    }

                } else if ((getResources().getConfiguration().screenLayout & SCREENLAYOUT_SIZE_MASK) == SCREENLAYOUT_SIZE_SMALL) {

                    gridView.setNumColumns(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ? 2 : 2);


                } else {
                    if (videoWidth > videoHeight) {
                        gridView.setNumColumns(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ? 4 : 4);
                    } else {
                        gridView.setNumColumns(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ? 5 : 5);
                    }

                }
                if (videoWidth > videoHeight) {
                    if (density >= 3.5 && density <= 4.0) {
                        customGridAdapter = new VideoFilterAdapter(SearchActivity.this, R.layout.nexus_videos_grid_layout_land, itemData);
                    }else{
                        customGridAdapter = new VideoFilterAdapter(SearchActivity.this, R.layout.videos_280_grid_layout, itemData);

                    }
                    gridView.setAdapter(customGridAdapter);
                } else {
                    if (density >= 3.5 && density <= 4.0) {
                        customGridAdapter = new VideoFilterAdapter(SearchActivity.this, R.layout.nexus_videos_grid_layout, itemData);
                    }else{
                        customGridAdapter = new VideoFilterAdapter(SearchActivity.this, R.layout.videos_grid_layout, itemData);

                    }
                    // customGridAdapter = new VideoFilterAdapter(context, R.layout.videos_grid_layout, itemData);
                    gridView.setAdapter(customGridAdapter);
                }
              /*  if (videoWidth > videoHeight) {
                    customGridAdapter = new VideoFilterAdapter(SearchActivity.this, R.layout.videos_280_grid_layout, itemData);
                    gridView.setAdapter(customGridAdapter);
                } else {
                    customGridAdapter = new VideoFilterAdapter(SearchActivity.this, R.layout.videos_grid_layout, itemData);
                    gridView.setAdapter(customGridAdapter);
                }
*/

            } else {
                // save RecyclerView state
                mBundleRecyclerViewState = new Bundle();
                Parcelable listState = gridView.onSaveInstanceState();
                mBundleRecyclerViewState.putParcelable(KEY_RECYCLER_STATE, listState);


              /*  if (videoWidth > videoHeight) {
                    customGridAdapter = new VideoFilterAdapter(SearchActivity.this, R.layout.videos_280_grid_layout, itemData);
                    gridView.setAdapter(customGridAdapter);
                } else {
                    customGridAdapter = new VideoFilterAdapter(SearchActivity.this, R.layout.videos_grid_layout, itemData);
                    gridView.setAdapter(customGridAdapter);
                }*/
                if (videoWidth > videoHeight) {
                    if (density >= 3.5 && density <= 4.0) {
                        customGridAdapter = new VideoFilterAdapter(SearchActivity.this, R.layout.nexus_videos_grid_layout_land, itemData);
                    }else{
                        customGridAdapter = new VideoFilterAdapter(SearchActivity.this, R.layout.videos_280_grid_layout, itemData);

                    }
                    gridView.setAdapter(customGridAdapter);
                } else {
                    if (density >= 3.5 && density <= 4.0) {
                        customGridAdapter = new VideoFilterAdapter(SearchActivity.this, R.layout.nexus_videos_grid_layout, itemData);
                    }else{
                        customGridAdapter = new VideoFilterAdapter(SearchActivity.this, R.layout.videos_grid_layout, itemData);

                    }
                    gridView.setAdapter(customGridAdapter);
                }
                if (mBundleRecyclerViewState != null) {
                    gridView.onRestoreInstanceState(listState);
                }

            }
        }
    }

        public void onResume() {
            super.onResume();
            //movieThirdPartyUrl = getResources().getString(R.string.no_data_str);


        }

        @Override
        public void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
            // save RecyclerView state
            mBundleRecyclerViewState = new Bundle();
            Parcelable listState = gridView.onSaveInstanceState();
            mBundleRecyclerViewState.putParcelable(KEY_RECYCLER_STATE, listState);
        }
   /* @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if(savedInstanceState != null)
        {
            Parcelable savedRecyclerLayoutState = savedInstanceState.getParcelable(KEY_RECYCLER_STATE);
            gridView.getLayoutManager().onRestoreInstanceState(savedRecyclerLayoutState);
        }
    }*/

   /* @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(BUNDLE_RECYCLER_LAYOUT, recyclerView.getLayoutManager().onSaveInstanceState());
    }*/
  /* @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // save RecyclerView state
        mBundleRecyclerViewState = new Bundle();

        //Parcelable listState = gridView.onSaveInstanceState();
        //mBundleRecyclerViewState.putParcelable(KEY_RECYCLER_STATE, gridView.getLayoutManager().onSaveInstanceState());
    }*/


        //load video urls as per resolution

        public interface ClickListener {
            void onClick(View view, int position);

            void onLongClick(View view, int position);
        }

        public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

            private GestureDetector gestureDetector;
            private ClickListener clickListener;

            public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener) {
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



 /*   void showRegistrationDialog(){
        registerDialogListener = new RegisterDialog.NewUserButtonOnClickListener() {
            @Override
            public void onNewUserButtonClick(EditText registerFullNameStr, EditText registerEmailStr, EditText registerPasswordStr) {
                regEmailIdEditText = registerEmailStr;
                regPasswordEditText = registerPasswordStr;
                regFullNameEditText = registerFullNameStr;
                registerOfRegisteredDialogButtonClicked();
            }

            @Override
            public void onCheckboxTextClick() {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://live.levidia.com/page/terms-privacy-policy"));
                browserIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(browserIntent);
            }
            @Override
            public void onAlreadyLoginButtonClick() {
                if (registerDialog.isShowing()){
                    registerDialog.cancel();
                    registerDialog.cancel();
                }
                showLoginDialog();
            }


        };

        registerDialog = new RegisterDialog(SearchActivity.this, registerDialogListener);
        registerDialog.show();


    }
    public void registerOfRegisteredDialogButtonClicked() {

        String regNameStr =  regFullNameEditText.getText().toString().trim();
        String regEmailStr =  regEmailIdEditText.getText().toString();
        String regPasswordStr = regPasswordEditText.getText().toString();

        if (regNameStr.matches("") || (regEmailStr.matches("")) || (regPasswordStr.matches(""))) {
            Toast.makeText(SearchActivity.this,getResources().getString(R.string.empty_fields_alert), Toast.LENGTH_LONG).show();
        }
        else{
            boolean isValidEmail = Util.isValidMail(regEmailStr);
            if( (regEmailStr!=null && regEmailStr.length() > 0) && (isValidEmail == false)){
                Toast.makeText(SearchActivity.this,getResources().getString(R.string.invalid_email_address_msg), Toast.LENGTH_LONG).show();
            }
            else{
                boolean isNetwork = Util.checkNetwork(SearchActivity.this);
                if (isNetwork==false){
                    Toast.makeText(SearchActivity.this, getResources().getString(R.string.no_internet_connection_str), Toast.LENGTH_LONG).show();

                }
                else {
                   *//* AsynRegistrationDetails asyncReg = new AsynRegistrationDetails();
                    asyncReg.executeOnExecutor(threadPoolExecutor);*//*
                }

            }
        }

    }*/
/*
    //Verify the login
    private class AsynLogInDetails extends AsyncTask<Void, Void, Void> {
        ProgressDialog pDialog;
        int statusCode;
        String loggedInIdStr;
        String responseStr;
        String isSubscribedStr;
        String loginHistoryIdStr;

        JSONObject myJson = null;
        String loginEmailIdStr = loginEmailIdEditText.getText().toString();
        String loginPasswordStr = loginPasswordEditText.getText().toString();

        @Override
        protected Void doInBackground(Void... params) {


            ArrayList<NameValuePair> cred = new ArrayList<NameValuePair>();
            cred.add(new BasicNameValuePair("email", loginEmailIdStr));
            cred.add(new BasicNameValuePair("password", loginPasswordStr));
            cred.add(new BasicNameValuePair("authToken", Util.authTokenStr.trim()));

            String urlRouteList = Util.rootUrl().trim()+Util.loginUrl.trim();
            try {
                HttpClient httpclient=new DefaultHttpClient();
                HttpPost httppost = new HttpPost(urlRouteList);
                httppost.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");
                httppost.addHeader("password", loginPasswordStr);
                httppost.addHeader("email", loginEmailIdStr);
                httppost.addHeader("authToken", Util.authTokenStr.trim());

                try {
                    httppost.setEntity(new UrlEncodedFormEntity(cred, "UTF-8"));
                }

                catch (UnsupportedEncodingException e) {
                    statusCode = 0;
                    e.printStackTrace();
                }

                // Execute HTTP Post Request
                try {
                    HttpResponse response = httpclient.execute(httppost);
                    responseStr = EntityUtils.toString(response.getEntity());
                } catch (org.apache.http.conn.ConnectTimeoutException e){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            statusCode = 0;
                            //Crouton.showText(ShowWithEpisodesListActivity.this, "Slow Internet Connection", Style.INFO);
                            Toast.makeText(SearchActivity.this, getResources().getString(R.string.slow_internet_connection_str), Toast.LENGTH_LONG).show();

                        }

                    });

                } catch (IOException e) {
                    statusCode = 0;

                    e.printStackTrace();
                }
                if(responseStr!=null){
                    myJson = new JSONObject(responseStr);
                    statusCode = Integer.parseInt(myJson.optString("code"));

                    //userIdStr = myJson.optString("status");
                    loggedInIdStr = myJson.optString("id");
                    isSubscribedStr = myJson.optString("isSubscribed");
                    loginHistoryIdStr = myJson.optString("login_history_id");


                }

            }
            catch (Exception e) {
                statusCode = 0;

            }

            return null;
        }


        protected void onPostExecute(Void result) {

            if(responseStr == null){
                try {
                    if (pDialog != null && pDialog.isShowing())
                        pDialog.dismiss();
                } catch (IllegalArgumentException ex) {
                    statusCode = 0;

                }
                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(SearchActivity.this);
                dlgAlert.setMessage(getResources().getString(R.string.empty_records_alert));
                dlgAlert.setTitle(getResources().getString(R.string.sorry_str));
                dlgAlert.setPositiveButton(getResources().getString(R.string.ok_str), null);
                dlgAlert.setCancelable(false);
                dlgAlert.setPositiveButton(getResources().getString(R.string.ok_str),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                dlgAlert.create().show();
            }

            if (statusCode > 0) {

                SharedPreferences.Editor editor = pref.edit();

                if (statusCode == 200){
                    String displayNameStr = myJson.optString("display_name");
                    String emailFromApiStr = myJson.optString("email");
                    String profileImageStr = myJson.optString("profile_image");

                    editor.putString("PREFS_LOGGEDIN_KEY","1");
                    editor.putString("PREFS_LOGGEDIN_ID_KEY",loggedInIdStr);
                    editor.putString("PREFS_LOGGEDIN_PASSWORD_KEY","");
                    editor.putString("PREFS_LOGIN_EMAIL_ID_KEY", emailFromApiStr);
                    editor.putString("PREFS_LOGIN_DISPLAY_NAME_KEY", displayNameStr);
                    editor.putString("PREFS_LOGIN_PROFILE_IMAGE_KEY", profileImageStr);
                    editor.putString("PREFS_LOGGEDIN_PASSWORD_KEY",loginPasswordEditText.getText().toString().trim());
                    editor.putString("PREFS_LOGIN_ISSUBSCRIBED_KEY",isSubscribedStr);
                    editor.putString("PREFS_LOGIN_HISTORYID_KEY",loginHistoryIdStr);


                    Date todayDate = new Date();
                    String todayStr = new SimpleDateFormat("yyyy-MM-dd").format(todayDate);
                    editor.putString("date", todayStr.trim());
                    editor.commit();

                    if (loginDialog!=null && loginDialog.isShowing()) {
                        loginDialog.dismiss();
                        loginDialog.cancel();

                    }

                    if (Util.checkNetwork(SearchActivity.this) == true) {
                        if (videoPDialog!=null && videoPDialog.isShowing())
                            videoPDialog.dismiss();
                            AsynLoadVideoUrls asynLoadVideoUrls = new AsynLoadVideoUrls();
                            asynLoadVideoUrls.executeOnExecutor(threadPoolExecutor);


                    } else {
                        Toast.makeText(SearchActivity.this, getResources().getString(R.string.no_internet_connection_str), Toast.LENGTH_LONG).show();
                    }

                }else{
                    try {
                        if (pDialog != null && pDialog.isShowing())
                            pDialog.dismiss();
                    } catch (IllegalArgumentException ex) {
                        statusCode = 0;

                    }
                    AlertDialog.Builder dlgAlert = new AlertDialog.Builder(SearchActivity.this);
                    dlgAlert.setMessage(getResources().getString(R.string.email_password_invalid_str));
                    dlgAlert.setTitle(getResources().getString(R.string.sorry_str));
                    dlgAlert.setPositiveButton(getResources().getString(R.string.ok_str), null);
                    dlgAlert.setCancelable(false);
                    dlgAlert.setPositiveButton(getResources().getString(R.string.ok_str),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    dlgAlert.create().show();
                }
            }else{
                try {
                    if (pDialog != null && pDialog.isShowing())
                        pDialog.dismiss();
                } catch (IllegalArgumentException ex) {

                }
                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(SearchActivity.this);
                dlgAlert.setMessage(getResources().getString(R.string.empty_records_alert));
                dlgAlert.setTitle(getResources().getString(R.string.sorry_str));
                dlgAlert.setPositiveButton(getResources().getString(R.string.ok_str), null);
                dlgAlert.setCancelable(false);
                dlgAlert.setPositiveButton(getResources().getString(R.string.ok_str),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                dlgAlert.create().show();
            }

        }

        @Override
        protected void onPreExecute() {
         *//*   pDialog = new ProgressDialog(context);
            pDialog.setMessage(getResources().getString(R.string.loading_str));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
*//*
            pDialog = new ProgressDialog(SearchActivity.this,R.style.MyTheme);
            pDialog.setCancelable(false);
            pDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Large_Inverse);
            pDialog.setIndeterminate(false);
            pDialog.setIndeterminateDrawable(getResources().getDrawable(R.drawable.progress_rawable));

            if (pDialog!=null && !pDialog.isShowing()) {
                pDialog.show();
            }
        }


    }*/

        //Registration
   /* private class AsynRegistrationDetails extends AsyncTask<Void, Void, Void> {
        ProgressDialog pDialog;

        int status;
        String responseStr;
        String registrationIdStr;
        String isSubscribedStr;
        String loginHistoryIdStr;
        JSONObject myJson = null;
        String regFullNameStr = regFullNameEditText.getText().toString().trim();
        String regEmailStr = regEmailIdEditText.getText().toString();
        String regPasswordStr = regPasswordEditText.getText().toString();
        @Override
        protected Void doInBackground(Void... params) {

            String urlRouteList = Util.rootUrl().trim()+Util.registrationUrl.trim();
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(urlRouteList);
                httppost.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");
                httppost.addHeader("name", regFullNameStr);
                httppost.addHeader("email", regEmailStr);
                httppost.addHeader("password", regPasswordStr);
                httppost.addHeader("authToken", Util.authTokenStr.trim());

                // Execute HTTP Post Request
                try {
                    HttpResponse response = httpclient.execute(httppost);
                    responseStr = EntityUtils.toString(response.getEntity());

                } catch (org.apache.http.conn.ConnectTimeoutException e){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (pDialog.isShowing())
                                pDialog.dismiss();
                            status = 0;
                            Toast.makeText(SearchActivity.this, getResources().getString(R.string.slow_internet_connection_str), Toast.LENGTH_LONG).show();

                        }

                    });

                } catch (IOException e) {
                    if (pDialog.isShowing())
                        pDialog.dismiss();
                    status = 0;
                    e.printStackTrace();
                }
                if(responseStr!=null){
                    myJson = new JSONObject(responseStr);
                    status = Integer.parseInt(myJson.optString("code"));
                    registrationIdStr = myJson.optString("id");
                    isSubscribedStr = myJson.optString("isSubscribed");
                    loginHistoryIdStr = myJson.optString("login_history_id");

                }

            }
            catch (Exception e) {
                if (pDialog.isShowing())
                    pDialog.dismiss();
                status = 0;

            }

            return null;
        }


        protected void onPostExecute(Void result) {
            try {
                if (pDialog!=null && pDialog.isShowing())
                    pDialog.dismiss();
            } catch (IllegalArgumentException ex) {
                status = 0;

            }
            if(responseStr == null){
                status = 0;

            }
            if (status == 0) {
                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(SearchActivity.this);
                dlgAlert.setMessage(getResources().getString(R.string.error_in_registration_str));
                dlgAlert.setTitle(getResources().getString(R.string.failure_str));
                dlgAlert.setPositiveButton(getResources().getString(R.string.ok_str), null);
                dlgAlert.setCancelable(false);
                dlgAlert.setPositiveButton(getResources().getString(R.string.ok_str),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                dlgAlert.create().show();
            }
            if (status > 0) {

                if (status == 422) {
                    AlertDialog.Builder dlgAlert = new AlertDialog.Builder(SearchActivity.this);
                    dlgAlert.setMessage(getResources().getString(R.string.email_exists_for_studio_str));
                    dlgAlert.setTitle(getResources().getString(R.string.sorry_str));
                    dlgAlert.setPositiveButton(getResources().getString(R.string.ok_str), null);
                    dlgAlert.setCancelable(false);
                    dlgAlert.setPositiveButton(getResources().getString(R.string.ok_str),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    dlgAlert.create().show();

                }

                else if (status == 200){

                    String displayNameStr = myJson.optString("display_name");
                    String emailFromApiStr = myJson.optString("email");
                    String profileImageStr = myJson.optString("profile_image");

                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("PREFS_LOGGEDIN_KEY","1");
                    editor.putString("PREFS_LOGGEDIN_ID_KEY", registrationIdStr);
                    editor.putString("PREFS_LOGGEDIN_PASSWORD_KEY", regPasswordEditText.getText().toString().trim());
                    editor.putString("PREFS_LOGIN_EMAIL_ID_KEY", emailFromApiStr);
                    editor.putString("PREFS_LOGIN_DISPLAY_NAME_KEY", displayNameStr);
                    editor.putString("PREFS_LOGIN_PROFILE_IMAGE_KEY", profileImageStr);
                    editor.putString("PREFS_LOGIN_ISSUBSCRIBED_KEY",isSubscribedStr);
                    editor.putString("PREFS_LOGIN_HISTORYID_KEY",loginHistoryIdStr);

                    Date todayDate = new Date();
                    String todayStr = new SimpleDateFormat("yyyy-MM-dd").format(todayDate);
                    editor.putString("date", todayStr.trim());
                    editor.commit();



                    if (registerDialog.isShowing()) {
                        registerDialog.dismiss();
                        registerDialog.cancel();

                    }

                    if (Util.checkNetwork(SearchActivity.this) == true) {
                        if (pDialog!=null && pDialog.isShowing())
                            pDialog.dismiss();
                        // subhalaxmi
                        if (itemToPlay.ge == null) {
                            AlertDialog.Builder dlgAlert = new AlertDialog.Builder(SearchActivity.this);
                            dlgAlert.setMessage(getResources().getString(R.string.no_video_available_str));
                            dlgAlert.setTitle(getResources().getString(R.string.sorry_str));
                            dlgAlert.setPositiveButton(getResources().getString(R.string.ok_str), null);
                            dlgAlert.setCancelable(false);
                            dlgAlert.setPositiveButton(getResources().getString(R.string.ok_str),
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });
                            dlgAlert.create().show();
                        }
                        else if (videoUrlStr.matches("") || videoUrlStr.equalsIgnoreCase(getResources().getString(R.string.no_data_str))) {
                            AlertDialog.Builder dlgAlert = new AlertDialog.Builder(SearchActivity.this);
                            dlgAlert.setMessage(getResources().getString(R.string.no_video_available_str));
                            dlgAlert.setTitle(getResources().getString(R.string.sorry_str));
                            dlgAlert.setPositiveButton(getResources().getString(R.string.ok_str), null);
                            dlgAlert.setCancelable(false);
                            dlgAlert.setPositiveButton(getResources().getString(R.string.ok_str),
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });
                            dlgAlert.create().show();
                        } else{
                            final Intent playVideoIntent = new Intent(SearchActivity.this, VideoTrailerPlayerActivity.class);
                            playVideoIntent.putExtra("url", videoUrlStr);
                           *//* String videoPreview;
                            if(bannerImageId.trim().matches(getResources().getString(R.string.no_data_str))){
                                if(posterImageId.trim().matches(getResources().getString(R.string.no_data_str))) {
                                    videoPreview = getResources().getString(R.string.no_data_str);
                                }else{
                                    videoPreview = posterImageId;
                                }

                            }else{
                                videoPreview = bannerImageId;
                            }

                            playVideoIntent.putExtra("videoPreview", videoPreview);*//*
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    playVideoIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                    startActivity(playVideoIntent);

                                }
                            });


                        }
                    } else {
                        Toast.makeText(SearchActivity.this, getResources().getString(R.string.no_internet_connection_str), Toast.LENGTH_LONG).show();
                    }




                }
            }

        }

        @Override
        protected void onPreExecute() {
          *//*  pDialog = new ProgressDialog(MovieDetailsActivity.this);
            pDialog.setMessage(getResources().getString(R.string.loading_str));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);*//*

            pDialog = new ProgressDialog(SearchActivity.this,R.style.MyTheme);
            pDialog.setCancelable(false);
            pDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Large_Inverse);
            pDialog.setIndeterminate(false);
            pDialog.setIndeterminateDrawable(getResources().getDrawable(R.drawable.progress_rawable));

         *//*   pDialog = new ProgressDialog(MovieDetailsActivity.this);
            pDialog.setMessage(getResources().getString(R.string.loading_str));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);*//*

            if (pDialog!=null && !pDialog.isShowing()) {
                pDialog.show();
            }
        }


    }
*/
/*
    public void forgotPasswordButtonClicked() {

        String loginEmailStr =  loginEmailIdEditText.getText().toString().trim();
        if (loginEmailStr.matches("")) {
            Toast.makeText(SearchActivity.this, getResources().getString(R.string.blank_email_address_msg), Toast.LENGTH_LONG).show();

        }else{
            boolean isValidEmail = Util.isValidMail(loginEmailStr);
            if( (loginEmailStr!=null && loginEmailStr.length() > 0) && (isValidEmail == false)){
                Toast.makeText(SearchActivity.this, getResources().getString(R.string.invalid_email_address_msg), Toast.LENGTH_LONG).show();

            }
            else{
                boolean isNetwork = Util.checkNetwork(SearchActivity.this);
                if (isNetwork==false){
                    AlertDialog.Builder dlgAlert = new AlertDialog.Builder(SearchActivity.this);
                    dlgAlert.setMessage(getResources().getString(R.string.no_internet_no_data));
                    dlgAlert.setTitle(getResources().getString(R.string.sorry_str));
                    dlgAlert.setPositiveButton(getResources().getString(R.string.ok_str), null);
                    dlgAlert.setCancelable(false);
                    dlgAlert.setPositiveButton(getResources().getString(R.string.ok_str),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    dlgAlert.create().show();

                }
                else {
                    AsynForgotPasswordDetails asyncPasswordForgot=new AsynForgotPasswordDetails();
                    asyncPasswordForgot.executeOnExecutor(threadPoolExecutor);
                }

            }
        }

    }
    private class AsynForgotPasswordDetails extends AsyncTask<Void, Void, Void> {
        ProgressDialog pDialog;
        String loginEmailIdStr = loginEmailIdEditText.getText().toString();
        int responseCode;
        String responseStr;

        @Override
        protected Void doInBackground(Void... params) {

            ArrayList<NameValuePair> cred = new ArrayList<NameValuePair>();
            cred.add(new BasicNameValuePair("email", loginEmailIdStr));
            cred.add(new BasicNameValuePair("authToken", Util.authTokenStr.trim()));

            String urlRouteList = Util.rootUrl().trim()+Util.forgotpasswordUrl.trim();
            try {
                HttpClient httpclient=new DefaultHttpClient();
                HttpPost httppost = new HttpPost(urlRouteList);
                httppost.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");
                httppost.addHeader("email", loginEmailIdStr);
                httppost.addHeader("authToken", Util.authTokenStr.trim());

                try {
                    httppost.setEntity(new UrlEncodedFormEntity(cred, "UTF-8"));
                }

                catch (UnsupportedEncodingException e) {
                    if (pDialog != null && pDialog.isShowing())
                        pDialog.dismiss();
                    responseCode = 0;
                    e.printStackTrace();
                }
                try {
                    HttpResponse response = httpclient.execute(httppost);
                    responseStr = EntityUtils.toString(response.getEntity());
                } catch (org.apache.http.conn.ConnectTimeoutException e){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (pDialog != null && pDialog.isShowing())
                                pDialog.dismiss();
                            responseCode = 0;
                            validationIndicatorTextView.setVisibility(View.INVISIBLE);
                            Toast.makeText(SearchActivity.this, getResources().getString(R.string.slow_internet_connection_str), Toast.LENGTH_LONG).show();

                        }

                    });

                }catch (IOException e) {
                    if (pDialog != null && pDialog.isShowing())
                        pDialog.dismiss();
                    responseCode = 0;
                    e.printStackTrace();
                }
                if(responseStr!=null){
                    JSONObject myJson = new JSONObject(responseStr);
                    responseCode = Integer.parseInt(myJson.optString("code"));
                }

            }
            catch (Exception e) {
                if (pDialog != null && pDialog.isShowing())
                    pDialog.dismiss();
                responseCode = 0;

            }

            return null;
        }


        protected void onPostExecute(Void result) {
            try {
                if (pDialog != null && pDialog.isShowing())
                    pDialog.dismiss();
            } catch (IllegalArgumentException ex) {
                validationIndicatorTextView.setVisibility(View.VISIBLE);
                validationIndicatorTextView.setText(getResources().getString(R.string.email_didnot_exists_str));
            }
            if(responseStr == null){
                validationIndicatorTextView.setVisibility(View.VISIBLE);
                validationIndicatorTextView.setText(getResources().getString(R.string.email_didnot_exists_str));
            }
            if (responseCode == 0) {
                validationIndicatorTextView.setVisibility(View.VISIBLE);
                validationIndicatorTextView.setText(getResources().getString(R.string.email_didnot_exists_str));
            }
            if (responseCode > 0) {
                if (responseCode == 200) {
                    if (forgotPasswordDialog.isShowing()) {
                        forgotPasswordDialog.dismiss();
                        forgotPasswordDialog.cancel();
                    }
                    validationIndicatorTextView.setVisibility(View.INVISIBLE);

                    AlertDialog.Builder dlgAlert = new AlertDialog.Builder(SearchActivity.this);
                    dlgAlert.setMessage(getResources().getString(R.string.check_email_to_reset_password));
                    dlgAlert.setTitle(getResources().getString(R.string.success_str));
                    dlgAlert.setPositiveButton(getResources().getString(R.string.ok_str), null);
                    dlgAlert.setCancelable(false);
                    dlgAlert.setPositiveButton(getResources().getString(R.string.ok_str),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    dlgAlert.create().show();

                }
                else {
                    validationIndicatorTextView.setVisibility(View.VISIBLE);
                    validationIndicatorTextView.setText(getResources().getString(R.string.email_didnot_exists_str));
                }
            }

        }

        @Override
        protected void onPreExecute() {
         *//*   pDialog = new ProgressDialog(context);
            pDialog.setMessage(getResources().getString(R.string.loading_str));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);*//*

            pDialog = new ProgressDialog(SearchActivity.this,R.style.MyTheme);
            pDialog.setCancelable(false);
            pDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Large_Inverse);
            pDialog.setIndeterminate(false);
            pDialog.setIndeterminateDrawable(getResources().getDrawable(R.drawable.progress_rawable));

            if (pDialog!=null && !pDialog.isShowing()) {
                pDialog.show();
            }
        }
    }*/
/*
        public void clickItem(GridItem item) {


            String moviePermalink = item.getPermalink();
            String movieTypeId = item.getVideoTypeId();
            // if searched

            // for tv shows navigate to episodes
            if ((movieTypeId.equalsIgnoreCase("3"))) {
                if (moviePermalink.matches(Util.getTextofLanguage(SearchActivity.this, Util.NO_DATA, Util.DEFAULT_NO_DATA))) {
                    AlertDialog.Builder dlgAlert = new AlertDialog.Builder(SearchActivity.this);
                    dlgAlert.setMessage(Util.getTextofLanguage(SearchActivity.this, Util.NO_DETAILS_AVAILABLE, Util.DEFAULT_NO_DETAILS_AVAILABLE));
                    dlgAlert.setTitle(Util.getTextofLanguage(SearchActivity.this, Util.SORRY, Util.DEFAULT_SORRY));
                    dlgAlert.setPositiveButton(Util.getTextofLanguage(SearchActivity.this, Util.BUTTON_OK, Util.DEFAULT_BUTTON_OK), null);
                    dlgAlert.setCancelable(false);
                    dlgAlert.setPositiveButton(Util.getTextofLanguage(SearchActivity.this, Util.BUTTON_OK, Util.DEFAULT_BUTTON_OK),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    dlgAlert.create().show();
                } else {

                    final Intent detailsIntent = new Intent(SearchActivity.this, ShowWithEpisodesActivity.class);
                    detailsIntent.putExtra(Util.PERMALINK_INTENT_KEY, moviePermalink);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            detailsIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            startActivity(detailsIntent);
                        }
                    });
                }

            }

            // for single clips and movies
            else if ((movieTypeId.trim().equalsIgnoreCase("1")) || (movieTypeId.trim().equalsIgnoreCase("2")) || (movieTypeId.trim().equalsIgnoreCase("4"))) {
                final Intent detailsIntent = new Intent(SearchActivity.this, MovieDetailsActivity.class);

                if (moviePermalink.matches(Util.getTextofLanguage(SearchActivity.this, Util.NO_DATA, Util.DEFAULT_NO_DATA))) {
                    AlertDialog.Builder dlgAlert = new AlertDialog.Builder(SearchActivity.this);
                    dlgAlert.setMessage(Util.getTextofLanguage(SearchActivity.this, Util.NO_DETAILS_AVAILABLE, Util.DEFAULT_NO_DETAILS_AVAILABLE));
                    dlgAlert.setTitle(Util.getTextofLanguage(SearchActivity.this, Util.SORRY, Util.DEFAULT_SORRY));
                    dlgAlert.setPositiveButton(Util.getTextofLanguage(SearchActivity.this, Util.BUTTON_OK, Util.DEFAULT_BUTTON_OK), null);
                    dlgAlert.setCancelable(false);
                    dlgAlert.setPositiveButton(Util.getTextofLanguage(SearchActivity.this, Util.BUTTON_OK, Util.DEFAULT_BUTTON_OK),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    dlgAlert.create().show();
                } else {
                    detailsIntent.putExtra(Util.PERMALINK_INTENT_KEY, moviePermalink);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            detailsIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            startActivity(detailsIntent);
                        }
                    });
                }
            }

        }
*/


        public void resetData() {
            if (itemData != null && itemData.size() > 0) {
                itemData.clear();
            }
            firstTime = true;

            offset = 1;
            isLoading = false;
            listSize = 0;
            if (((getResources().getConfiguration().screenLayout & SCREENLAYOUT_SIZE_MASK) == SCREENLAYOUT_SIZE_LARGE) || ((getResources().getConfiguration().screenLayout & SCREENLAYOUT_SIZE_MASK) == SCREENLAYOUT_SIZE_XLARGE)) {
                limit = 20;
            } else {
                limit = 15;
            }
            itemsInServer = 0;
            isSearched = false;
        }

    }

