package com.release.reelAfrican.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.release.reelAfrican.R;
import com.release.reelAfrican.adapter.RecyclerViewDataAdapter;
import com.release.reelAfrican.model.GetMenuItem;
import com.release.reelAfrican.model.SectionDataModel;
import com.release.reelAfrican.model.SingleItemModel;
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
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static android.content.res.Configuration.SCREENLAYOUT_SIZE_LARGE;
import static android.content.res.Configuration.SCREENLAYOUT_SIZE_MASK;
import static android.content.res.Configuration.SCREENLAYOUT_SIZE_XLARGE;

/**
 * Created by Muvi on 11/24/2016.
 */
public class HomeFragment extends Fragment {

//    int banner[] = {R.drawable.slider,R.drawable.slider1,R.drawable.slider2,R.drawable.slider3,R.drawable.slider4,R.drawable.slider5};
//    int bannerL[] = {R.drawable.slider,R.drawable.slider1,R.drawable.slider2,R.drawable.slider3,R.drawable.slider4,R.drawable.slider5};


    /***************chromecast*********************
    public enum PlaybackLocation {
        LOCAL,
        REMOTE
    }

    public enum PlaybackState {
        PLAYING, PAUSED, BUFFERING, IDLE
    }



    private VideoView mVideoView;
    private TextView mTitleView;
    private TextView mDescriptionView;
    private TextView mStartText;
    private TextView mEndText;
    private SeekBar mSeekbar;
    private ImageView mPlayPause;
    private ProgressBar mLoading;
    private View mControllers;
    private View mContainer;
    private ImageView mCoverArt;
    private Timer mSeekbarTimer;
    private Timer mControllersTimer;
    private PlaybackLocation mLocation;
    private PlaybackState mPlaybackState;
    private final Handler mHandler = new Handler();
    private final float mAspectRatio = 72f / 128;
    private AQuery mAquery;
    private MediaInfo mSelectedMedia;
    private boolean mControllersVisible;
    private int mDuration;
    private TextView mAuthorView;
    private ImageButton mPlayCircle;


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
            //invalidateOptionsMenu();
        }

        @Override
        public void onSessionResumed(CastSession session, boolean wasSuspended) {
            mCastSession = session;
            //invalidateOptionsMenu();
        }

        @Override
        public void onSessionStarted(CastSession session, String sessionId) {
            mCastSession = session;
            //invalidateOptionsMenu();
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





    MediaInfo mediaInfo;
    **************chromecast*********************

*/

    int  videoHeight = 185;
    int  videoWidth = 256;

    AsynLoadVideos asynLoadVideos;
    AsynLOADUI loadui;
    View rootView;
    int item_CountOfSections = 0;
    boolean isFirstTime = false;
    int counter = 0;
    ArrayList<GetMenuItem> menuList;
    ArrayList<String> url_maps;

    private ProgressBarHandler mProgressBarHandler = null;

    //private ProgressDialog videoPDialog = null;
    private RelativeLayout noInternetLayout;
    RelativeLayout noDataLayout;
    TextView noDataTextView;
    TextView noInternetTextView;


    RecyclerView my_recycler_view;
    Context context;

    private final String KEY_RECYCLER_STATE = "recycler_state";
    private static Bundle mBundleRecyclerViewState;


    ArrayList<SectionDataModel> allSampleData;
    ArrayList<SingleItemModel> singleItem;

    //AsynLoadImageUrls as = null;
    AsynLoadMenuItems asynLoadMenuItems = null;
   /* int banner[] = {R.drawable.banner1,R.drawable.banner2,R.drawable.banner3};
    int bannerL[] = {R.drawable.banner1_l,R.drawable.banner2_l,R.drawable.banner3_l};*/
    int corePoolSize = 60;
    int maximumPoolSize = 80;
    int keepAliveTime = 10;
    BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<Runnable>(maximumPoolSize);
    Executor threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, workQueue);
    private String imageUrlStr;
    private int listSize = 0;
    RecyclerView recycler_view_list;
    private boolean firstTime = false;
    RecyclerViewDataAdapter adapter;
    LinearLayoutManager mLayoutManager;
    RelativeLayout footerView;
    int bannerLoaded = 0;
    RelativeLayout sliderRelativeLayout;
    private SliderLayout mDemoSlider;
    String videoImageStrToHeight;
    int ui_completed = 0;
    int loading_completed = 0;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        rootView = v;
        context = getActivity();
        setHasOptionsMenu(true);
        Util.image_orentiation.clear();

        Log.v("BIBHU2","device_id already created ="+ Settings.Secure.getString(getActivity().getContentResolver(),Settings.Secure.ANDROID_ID));
        Log.v("BIBHU2","google_id already created ="+Util.getTextofLanguage(getActivity(),Util.GOOGLE_FCM_TOKEN,Util.DEFAULT_GOOGLE_FCM_TOKEN));



 /*       *//***************chromecast**********************//*

        mCastStateListener = new CastStateListener() {
            @Override
            public void onCastStateChanged(int newState) {
                if (newState != CastState.NO_DEVICES_AVAILABLE) {

                    showIntroductoryOverlay();
                }
            }
        };
        mCastContext = CastContext.getSharedInstance(getActivity());
        mCastContext.registerLifecycleCallbacksBeforeIceCreamSandwich(getActivity(), savedInstanceState);



        // int startPosition = getInt("startPosition", 0);
        // mVideoView.setVideoURI(Uri.parse(item.getContentId()));

        setupCastListener();
        mCastSession = mCastContext.getSessionManager().getCurrentCastSession();

*//***************chromecast**********************//*
*/
        allSampleData = new ArrayList<SectionDataModel>();
        // createDummyData();
        footerView = (RelativeLayout) v.findViewById(R.id.loadingPanel);
        my_recycler_view = (RecyclerView) v.findViewById(R.id.my_recycler_view);
        sliderRelativeLayout = (RelativeLayout) v.findViewById(R.id.sliderRelativeLayout);
        mDemoSlider = (SliderLayout) v.findViewById(R.id.sliderLayout);

        sliderRelativeLayout.setVisibility(View.GONE);
        noInternetLayout = (RelativeLayout)rootView.findViewById(R.id.noInternet);
        noDataLayout = (RelativeLayout)rootView.findViewById(R.id.noData);
        noInternetTextView =(TextView)rootView.findViewById(R.id.noInternetTextView);
        noDataTextView =(TextView)rootView.findViewById(R.id.noDataTextView);
        noInternetTextView.setText(Util.getTextofLanguage(context, Util.NO_INTERNET_CONNECTION, Util.DEFAULT_NO_INTERNET_CONNECTION));
        noDataTextView.setText(Util.getTextofLanguage(context, Util.NO_CONTENT, Util.DEFAULT_NO_CONTENT));

        footerView.setVisibility(View.GONE);

        my_recycler_view.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        boolean isNetwork = Util.checkNetwork(getActivity());
        if (isNetwork == true) {
            // default data
            menuList = new ArrayList<GetMenuItem>();

            url_maps = new ArrayList<String>();

            asynLoadMenuItems = new AsynLoadMenuItems();
            asynLoadMenuItems.executeOnExecutor(threadPoolExecutor);


        }
        return v;


    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Do something that differs the Activity's menu here

        MenuItem item;
        item= menu.findItem(R.id.action_filter);
        item.setVisible(false);
      /*  *//***************chromecast**********************//*


        mediaRouteMenuItem = CastButtonFactory.setUpMediaRouteButton(getActivity(), menu, R.id.media_route_menu_item);
        showIntroductoryOverlay();

        *//***************chromecast**********************/

        super.onCreateOptionsMenu(menu, inflater);

    }


    private void StartAsyncTaskInParallel(AsynLoadMenuItems task) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        else
            task.execute();
    }

    private class AsynLoadVideos extends AsyncTask<String, Void, Void> {

        String responseStr;
        int status;
        String movieGenreStr = "";
        String moviePermalinkStr = Util.getTextofLanguage(context, Util.NO_DATA, Util.DEFAULT_NO_DATA);
        String videoTypeIdStr = Util.getTextofLanguage(context, Util.NO_DATA, Util.DEFAULT_NO_DATA);
        String isEpisodeStr = "";
        int isAPV = 0;
        int isPPV = 0;
        int isConverted = 0;
        String movieName = Util.getTextofLanguage(context, Util.NO_DATA, Util.DEFAULT_NO_DATA);
        String movieImageStr = Util.getTextofLanguage(context, Util.NO_DATA, Util.DEFAULT_NO_DATA);
        String prm,permalink;


        @Override
        protected Void doInBackground(String... params) {

            singleItem = new ArrayList<SingleItemModel>();
            String urlRouteList = Util.rootUrl().trim() + Util.getContent.trim();
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(urlRouteList);
                httppost.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");
                httppost.addHeader("authToken", Util.authTokenStr.trim());
                httppost.addHeader("section_id",String.valueOf(params[0]));
                httppost.addHeader("lang_code", Util.getTextofLanguage(context, Util.SELECTED_LANGUAGE_CODE, Util.DEFAULT_SELECTED_LANGUAGE_CODE));


                // Execute HTTP Post Request
                try {
                    HttpResponse response = httpclient.execute(httppost);
                    responseStr = EntityUtils.toString(response.getEntity());

                } catch (org.apache.http.conn.ConnectTimeoutException e) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            responseStr = "0";

                        }

                    });

                } catch (IOException e) {
                    responseStr = "0";

                }

                JSONObject myJson = null;
                if (responseStr != null) {
                    myJson = new JSONObject(responseStr);
                    status = Integer.parseInt(myJson.optString("code"));
                }


                if (status > 0) {
                    if (status == 200) {

                        JSONArray jsonMainNode = myJson.getJSONArray("section");

                    /*    if(clear_item_data)
                        {
                            singleItem.clear();
                            clear_item_data = false;
                        }
*/
                        int lengthJsonArr = jsonMainNode.length();
                        for(int i=0; i < lengthJsonArr; i++) {
                            JSONObject jsonChildNode;
                            try {
                                jsonChildNode = jsonMainNode.getJSONObject(i);

                                if ((jsonChildNode.has("genre")) && jsonChildNode.getString("genre").trim() != null && !jsonChildNode.getString("genre").trim().isEmpty() && !jsonChildNode.getString("genre").trim().equals("null") && !jsonChildNode.getString("genre").trim().matches("")) {
                                    movieGenreStr = jsonChildNode.getString("genre");

                                }
                                if ((jsonChildNode.has("name")) && jsonChildNode.getString("name").trim() != null && !jsonChildNode.getString("name").trim().isEmpty() && !jsonChildNode.getString("name").trim().equals("null") && !jsonChildNode.getString("name").trim().matches("")) {
                                    movieName = jsonChildNode.getString("name");

                                }
                                if ((jsonChildNode.has("poster_url")) && jsonChildNode.getString("poster_url").trim() != null && !jsonChildNode.getString("poster_url").trim().isEmpty() && !jsonChildNode.getString("poster_url").trim().equals("null") && !jsonChildNode.getString("poster_url").trim().matches("")) {
                                    movieImageStr = jsonChildNode.getString("poster_url");
                                    //movieImageStr = movieImageStr.replace("episode", "original");

                                }
                                if ((jsonChildNode.has("permalink")) && jsonChildNode.getString("permalink").trim() != null && !jsonChildNode.getString("permalink").trim().isEmpty() && !jsonChildNode.getString("permalink").trim().equals("null") && !jsonChildNode.getString("permalink").trim().matches("")) {
                                    moviePermalinkStr = jsonChildNode.getString("permalink");

                                }
                                if ((jsonChildNode.has("content_types_id")) && jsonChildNode.getString("content_types_id").trim() != null && !jsonChildNode.getString("content_types_id").trim().isEmpty() && !jsonChildNode.getString("content_types_id").trim().equals("null") && !jsonChildNode.getString("content_types_id").trim().matches("")) {
                                    videoTypeIdStr = jsonChildNode.getString("content_types_id");

                                }
                                //videoTypeIdStr = "1";

                                if ((jsonChildNode.has("is_converted")) && jsonChildNode.getString("is_converted").trim() != null && !jsonChildNode.getString("is_converted").trim().isEmpty() && !jsonChildNode.getString("is_converted").trim().equals("null") && !jsonChildNode.getString("is_converted").trim().matches("")) {
                                    isConverted = Integer.parseInt(jsonChildNode.getString("is_converted"));

                                }
                                if ((jsonChildNode.has("is_advance")) && jsonChildNode.getString("is_advance").trim() != null && !jsonChildNode.getString("is_advance").trim().isEmpty() && !jsonChildNode.getString("is_advance").trim().equals("null") && !jsonChildNode.getString("is_advance").trim().matches("")) {
                                    isAPV = Integer.parseInt(jsonChildNode.getString("is_advance"));

                                }
                                if ((jsonChildNode.has("is_ppv")) && jsonChildNode.getString("is_ppv").trim() != null && !jsonChildNode.getString("is_ppv").trim().isEmpty() && !jsonChildNode.getString("is_ppv").trim().equals("null") && !jsonChildNode.getString("is_ppv").trim().matches("")) {
                                    isPPV = Integer.parseInt(jsonChildNode.getString("is_ppv"));

                                }
                                if ((jsonChildNode.has("is_episode")) && jsonChildNode.getString("is_episode").trim() != null && !jsonChildNode.getString("is_episode").trim().isEmpty() && !jsonChildNode.getString("is_episode").trim().equals("null") && !jsonChildNode.getString("is_episode").trim().matches("")) {
                                    isEpisodeStr = jsonChildNode.getString("is_episode");

                                }

                                singleItem.add(new SingleItemModel(movieImageStr, movieName, "", videoTypeIdStr, movieGenreStr, "", moviePermalinkStr,isEpisodeStr,"","",isConverted,isPPV,isAPV));
                            } catch (Exception e) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        responseStr = "0";

                                    }
                                });
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                    } else {
                        responseStr = "0";

                    }
                }
            } catch (Exception e) {
                responseStr = "0";

            }
            return null;

        }

        protected void onPostExecute(Void result) {
            if (mProgressBarHandler != null) {
                mProgressBarHandler.hide();
                mProgressBarHandler = null;
            }

            if (responseStr == null) {
                responseStr = "0";
            }
        /*    if ((responseStr.trim().equals("0"))) {
                //allSampleData.add(new SectionDataModel(menuList.get(counter).getName(),menuList.get(counter).getSectionId(), singleItem));

            } else {*/

               /* if(clear_sample_data)
                {
                    allSampleData.clear();
                    clear_sample_data = false;
                }*/

            allSampleData.add(new SectionDataModel(menuList.get(counter).getName(), menuList.get(counter).getSectionId(), singleItem));

           /* if (counter >= 0 && counter < menuList.size() -1 ){
                counter = counter+1;
                boolean isNetwork = Util.checkNetwork(context);
                if (isNetwork == true) {
                    if (getActivity()!=null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                footerView.setVisibility(View.VISIBLE);

                            }
                        });
                    }


                *//*    loading_completed = loading_completed + 1;
                    Log.v("SUBHA","loading_completed"+loading_completed);
                    Log.v("SUBHA","ui_completed"+ui_completed);*//*

                    // default data
                    asynLoadVideos = new AsynLoadVideos();
                    asynLoadVideos.executeOnExecutor(threadPoolExecutor, menuList.get(counter).getSectionId());

                }else{
                    noInternetLayout.setVisibility(View.VISIBLE);
                }
            }
*/


            boolean isNetwork = Util.checkNetwork(context);
            if (isNetwork == true) {

                if (getActivity()!=null) {
                    new RetrieveFeedTask().execute(movieImageStr);
                  /*  Picasso.with(getActivity()).load(movieImageStr
                    ).into(new Target() {

                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            videoWidth = bitmap.getWidth();
                            videoHeight = bitmap.getHeight();
                            // default data

                            loadui = new AsynLOADUI();
                            loadui.executeOnExecutor(threadPoolExecutor);
                        }

                        @Override
                        public void onBitmapFailed(final Drawable errorDrawable) {
                            loadui = new AsynLOADUI();
                            loadui.executeOnExecutor(threadPoolExecutor);
                        }

                        @Override
                        public void onPrepareLoad(final Drawable placeHolderDrawable) {



                        }
                    });*/

                }


            }else{
                noInternetLayout.setVisibility(View.VISIBLE);
            }





           /* videoImageStrToHeight = movieImageStr;
            AsynLOADPicasso asynLOADPicasso= new AsynLOADPicasso();
            asynLOADPicasso.executeOnExecutor(threadPoolExecutor);
            loadui = new AsynLOADUI();
            loadui.executeOnExecutor(threadPoolExecutor);*/

            return;

            // }
        }

        @Override
        protected void onPreExecute() {

            if (firstTime == false){


                if (getActivity()!=null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mProgressBarHandler = new ProgressBarHandler(context);
                            mProgressBarHandler.show();
                        }
                    });

                }

            }else {
               /* if (counter >= 0 && counter >= menuList.size()-1) {
                    Log.v("SUBHA","COUNTER");
                    loading_completed = true;
                }*/
            }
        }

    }
    private class AsynLOADPicasso extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            Picasso.with(context).load(videoImageStrToHeight).into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    videoWidth = bitmap.getWidth();
                    videoHeight = bitmap.getHeight();
                    loadui = new AsynLOADUI();
                    loadui.executeOnExecutor(threadPoolExecutor);
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {
                    loadui = new AsynLOADUI();
                    loadui.executeOnExecutor(threadPoolExecutor);
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            });
            return null;
        }

        protected void onPostExecute(Void result) {

        }
        @Override
        protected void onPreExecute() {

        }

    }

    private class AsynLOADUI extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }

        protected void onPostExecute(Void result) {
            //ui_completed = ui_completed + 1;

          /*  if (videoWidth > videoHeight) {

                Util.image_orentiation.add(0);

            }else{
                Util.image_orentiation.add(1);

            }*/

            if (videoWidth > videoHeight) {

                Util.ori=0;

            }else{
                Util.ori=1;

            }

            Log.v("SUBHA", "HHH"+videoWidth+videoHeight);
            Log.v("SUBHA", "vertical"+MainActivity.vertical);

            if (getView() != null) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
            }

            if (firstTime == false) {

                if (mProgressBarHandler != null) {
                    mProgressBarHandler.hide();
                    mProgressBarHandler = null;
                }
                firstTime = true;

                if (adapter != null){

                    adapter.notifyDataSetChanged();
                }
                else { // it works first time
                    adapter = new RecyclerViewDataAdapter(context, allSampleData, url_maps,firstTime, MainActivity.vertical);
                    //   adapter = new AdapterClass(context,list);
                    my_recycler_view.setAdapter(adapter);
                }

            }else{
                if (mProgressBarHandler != null) {
                    mProgressBarHandler.hide();
                    mProgressBarHandler = null;
                }


                if (counter >= 0 && counter >= menuList.size()-1) {
                    footerView.setVisibility(View.GONE);
                }

                mBundleRecyclerViewState = new Bundle();
                Parcelable listState = my_recycler_view.getLayoutManager().onSaveInstanceState();
                mBundleRecyclerViewState.putParcelable(KEY_RECYCLER_STATE, listState);
                if (mBundleRecyclerViewState != null) {
                    my_recycler_view.getLayoutManager().onRestoreInstanceState(listState);
                }
            }

      /*      my_recycler_view.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    Log.v("SUBHA", "onScrollStateChanged");
                   *//* if (counter >= 0 && counter >= menuList.size()-1) {
                        adapter.swapItems();
                    }*//*

                }


                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                       Log.v("SUBHA","ONSCROLL");
                  *//*  if (counter >= 0 && counter >= menuList.size()-1) {
                        adapter.swapItems();
                    }*//*

                }
            });*/
            if (counter >= 0 && counter < menuList.size() -1 ){
                counter = counter+1;
                boolean isNetwork = Util.checkNetwork(context);
                if (isNetwork == true) {
                    if (getActivity()!=null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                footerView.setVisibility(View.VISIBLE);

                            }
                        });
                    }


                /*    loading_completed = loading_completed + 1;
                    Log.v("SUBHA","loading_completed"+loading_completed);
                    Log.v("SUBHA","ui_completed"+ui_completed);*/

                    // default data
                    asynLoadVideos = new AsynLoadVideos();
                    asynLoadVideos.executeOnExecutor(threadPoolExecutor, menuList.get(counter).getSectionId());

                }else{
                    noInternetLayout.setVisibility(View.VISIBLE);
                }
            }



        }
        @Override
        protected void onPreExecute() {

        }

    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        // save RecyclerView state
        if (my_recycler_view != null)
        {
            mBundleRecyclerViewState = new Bundle();
            Parcelable listState = mLayoutManager.onSaveInstanceState();
            mBundleRecyclerViewState.putParcelable(KEY_RECYCLER_STATE, listState);

        }
        super.onSaveInstanceState(outState);

       /* if (my_recycler_view!=null && my_recycler_view.getLayoutManager().onSaveInstanceState()!=nu) {
            Parcelable listState = my_recycler_view.getLayoutManager().onSaveInstanceState();
            mBundleRecyclerViewState.putParcelable(KEY_RECYCLER_STATE, listState);
        }*/
    }


    private class AsynLoadMenuItems extends AsyncTask<Void, Void, Void> implements  BaseSliderView.OnSliderClickListener,ViewPagerEx.OnPageChangeListener{


        String responseStr;
        int statusCode;
        String title;
        String studio_id;
        String language_id;
        String section_id;
        JSONObject bannerJson;
        JSONObject sectionJson;

        private ProgressBarHandler progressBarHandler = null;

        @Override
        protected Void doInBackground(Void... params) {
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(Util.rootUrl().trim() + Util.getFeaturedContent.trim());
                httppost.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");
                httppost.addHeader("authToken", Util.authTokenStr.trim());
                httppost.addHeader("lang_code", Util.getTextofLanguage(context, Util.SELECTED_LANGUAGE_CODE, Util.DEFAULT_SELECTED_LANGUAGE_CODE));

              /*  httppost.addHeader("limit", "1");
                httppost.addHeader("offset", String.valueOf(counter));*/

                // Execute HTTP Post Request
                try {

                    HttpResponse response = httpclient.execute(httppost);
                    responseStr = EntityUtils.toString(response.getEntity());

                    if (singleItem != null && singleItem.size() > 0) {
                        singleItem.clear();
                    }

                    if (allSampleData != null && allSampleData.size() > 0) {
                        allSampleData.clear();
                    }




                } catch (org.apache.http.conn.ConnectTimeoutException e) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (progressBarHandler != null) {
                                progressBarHandler.hide();
                                progressBarHandler = null;
                            }

                            responseStr = "0";
                            allSampleData = null;


                        }

                    });

                } catch (IOException e) {
                    if (progressBarHandler != null) {
                        progressBarHandler.hide();
                        progressBarHandler = null;
                    }

                    responseStr = "0";
                    allSampleData = null;

                }

                JSONObject myJson = null;
                if (responseStr != null) {
                    myJson = new JSONObject(responseStr);
                    if (myJson.has("BannerSectionList")) {
                        bannerJson = new JSONObject(myJson.optString("BannerSectionList"));
                        if (Integer.parseInt(bannerJson.optString("code")) == 200) {
                            JSONArray jsonBannerImageNode = null;
                            try {
                                jsonBannerImageNode = bannerJson.getJSONArray("banners");
                                int lengthBannerImagesArray = jsonBannerImageNode.length();

                                if (lengthBannerImagesArray > 0) {
                                    for (int i = 0; i < lengthBannerImagesArray; i++) {
                                        url_maps.add(jsonBannerImageNode.getJSONObject(i).getString("mobile_view"));

                                    }
                                } else {
                                    url_maps.add("https://d2gx0xinochgze.cloudfront.net/public/no-image-a.png");

                                }

                            } catch (JSONException e2) {
                                url_maps.add("https://d2gx0xinochgze.cloudfront.net/public/no-image-a.png");
                                e2.printStackTrace();
                            }
                        } else {
                            url_maps.add("https://d2gx0xinochgze.cloudfront.net/public/no-image-a.png");

                        }
                    }
                    if (myJson.has("SectionName")) {
                        sectionJson = new JSONObject(myJson.optString("SectionName"));
                        if (Integer.parseInt(sectionJson.optString("code")) == 200) {

                            JSONArray jsonMainNode = sectionJson.getJSONArray("section");
                            int lengthJsonArr = jsonMainNode.length();
                            for (int i = 0; i < lengthJsonArr; i++) {

                                JSONObject jsonChildNode;
                                try {
                                    jsonChildNode = jsonMainNode.getJSONObject(i);

                                    if ((jsonChildNode.has("studio_id")) && jsonChildNode.getString("studio_id").trim() != null && !jsonChildNode.getString("studio_id").trim().isEmpty() && !jsonChildNode.getString("studio_id").trim().equals("null") && !jsonChildNode.getString("studio_id").trim().matches("")) {
                                        studio_id = jsonChildNode.getString("studio_id");

                                    }
                                    if ((jsonChildNode.has("language_id")) && jsonChildNode.getString("language_id").trim() != null && !jsonChildNode.getString("language_id").trim().isEmpty() && !jsonChildNode.getString("language_id").trim().equals("null") && !jsonChildNode.getString("language_id").trim().matches("")) {
                                        language_id = jsonChildNode.getString("language_id");

                                    }
                                    if ((jsonChildNode.has("title")) && jsonChildNode.getString("title").trim() != null && !jsonChildNode.getString("title").trim().isEmpty() && !jsonChildNode.getString("title").trim().equals("null") && !jsonChildNode.getString("title").trim().matches("")) {
                                        title = jsonChildNode.getString("title");

                                    }
                                    if ((jsonChildNode.has("section_id")) && jsonChildNode.getString("section_id").trim() != null && !jsonChildNode.getString("section_id").trim().isEmpty() && !jsonChildNode.getString("section_id").trim().equals("null") && !jsonChildNode.getString("section_id").trim().matches("")) {
                                        section_id = jsonChildNode.getString("section_id");

                                    }

                                    menuList.add(new GetMenuItem(title, section_id, studio_id, language_id));
                                } catch (Exception e) {
                                    responseStr = "0";
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                            }
                        }else{
                            responseStr = "0";


                        }
                    }
                }
            }catch (Exception e) {


                responseStr = "0";
                allSampleData = null;
                e.printStackTrace();
            }

            return null;

        }

        protected void onPostExecute(Void result) {


            if (progressBarHandler != null) {
                progressBarHandler.hide();
                progressBarHandler = null;
            }

            if (responseStr == null) {
                if (progressBarHandler != null) {
                    progressBarHandler.hide();
                    progressBarHandler = null;
                }
                responseStr = "0";
            }
            else if((responseStr.trim().equals("0"))){


               /* if(url_maps.size()>=0 && firstTime == false){
                    firstTime = true;

                    if (((context.getResources().getConfiguration().screenLayout & SCREENLAYOUT_SIZE_MASK) == SCREENLAYOUT_SIZE_LARGE) || ((context.getResources().getConfiguration().screenLayout & SCREENLAYOUT_SIZE_MASK) == SCREENLAYOUT_SIZE_XLARGE)) {
                        for (int j = 0; j < url_maps.size() ;j ++) {
                            DefaultSliderView textSliderView = new DefaultSliderView(context);
                            textSliderView
                                    .description("")
                                    .image(url_maps.get(j))
                                    .setScaleType(BaseSliderView.ScaleType.CenterInside)
                                    .setOnSliderClickListener(this);
                            textSliderView.bundle(new Bundle());
                            textSliderView.getBundle()
                                    .putString("extra", "");

                            mDemoSlider.addSlider(textSliderView);
                        }
                    }else{
                        for (int j = 0; j < url_maps.size();j ++) {
                            DefaultSliderView textSliderView = new DefaultSliderView(context);
                            textSliderView
                                    .description("")
                                    .image(url_maps.get(j))
                                    .setScaleType(BaseSliderView.ScaleType.CenterInside)
                                    .setOnSliderClickListener(this);
                            textSliderView.bundle(new Bundle());
                            textSliderView.getBundle()
                                    .putString("extra", "");

                            mDemoSlider.addSlider(textSliderView);
                        }
                    }

                *//*    for (int i = 0; i < url_maps.size(); i++) {

                        DefaultSliderView textSliderView = new DefaultSliderView(context);

                        textSliderView
                                .description("")
                                .image(R.drawable.slider1)
                                .setScaleType(BaseSliderView.ScaleType.Fit)
                                .setOnSliderClickListener(this);

                        //add your extra information
                        textSliderView.bundle(new Bundle());
                        textSliderView.getBundle()
                                .putString("extra", "");

                        mDemoSlider.addSlider(textSliderView);*//*

                      *//*  DefaultSliderView textSliderView = new DefaultSliderView(context);

                        textSliderView
                                .description("")
                                .image(url_maps.get(i))
                                .setScaleType(BaseSliderView.ScaleType.Fit)
                                .setOnSliderClickListener(this);

                        //add your extra information
                        textSliderView.bundle(new Bundle());
                        textSliderView.getBundle()
                                .putString("extra", "");

                        mDemoSlider.addSlider(textSliderView);*//*
                    }
                else{
                   // DefaultSliderView textSliderView = new DefaultSliderView(context);
                  *//*  textSliderView
                            .description("")
                            .image(R.drawable.slider1)
                            .setScaleType(BaseSliderView.ScaleType.Fit)
                            .setOnSliderClickListener(this);
                    //add your extra information
                    textSliderView.bundle(new Bundle());
                    textSliderView.getBundle()
                            .putString("extra", "");

                    mDemoSlider.addSlider(textSliderView);*//*

                if (((context.getResources().getConfiguration().screenLayout & SCREENLAYOUT_SIZE_MASK) == SCREENLAYOUT_SIZE_LARGE) || ((context.getResources().getConfiguration().screenLayout & SCREENLAYOUT_SIZE_MASK) == SCREENLAYOUT_SIZE_XLARGE)) {
                    for (int j = 0; j < url_maps.size() ;j ++) {
                        DefaultSliderView textSliderView = new DefaultSliderView(context);
                        textSliderView
                                .description("")
                                .image(url_maps.get(j))
                                .setScaleType(BaseSliderView.ScaleType.CenterInside)
                                .setOnSliderClickListener(this);
                        textSliderView.bundle(new Bundle());
                        textSliderView.getBundle()
                                .putString("extra", "");

                        mDemoSlider.addSlider(textSliderView);
                    }
                }else{
                    for (int j = 0; j < url_maps.size();j ++) {
                        DefaultSliderView textSliderView = new DefaultSliderView(context);
                        textSliderView
                                .description("")
                                .image(url_maps.get(j))
                                .setScaleType(BaseSliderView.ScaleType.CenterInside)
                                .setOnSliderClickListener(this);
                        textSliderView.bundle(new Bundle());
                        textSliderView.getBundle()
                                .putString("extra", "");

                        mDemoSlider.addSlider(textSliderView);
                    }
                }

            }*/


                if(firstTime == false) {
                    firstTime = true;

                    if (((context.getResources().getConfiguration().screenLayout & SCREENLAYOUT_SIZE_MASK) == SCREENLAYOUT_SIZE_LARGE) || ((context.getResources().getConfiguration().screenLayout & SCREENLAYOUT_SIZE_MASK) == SCREENLAYOUT_SIZE_XLARGE)) {
                        for (int j = 0; j < url_maps.size(); j++) {
                            DefaultSliderView textSliderView = new DefaultSliderView(context);
                            textSliderView
                                    .description("")
                                    .image(url_maps.get(j))
                                    .setScaleType(BaseSliderView.ScaleType.CenterInside)
                                    .setOnSliderClickListener(this);
                            textSliderView.bundle(new Bundle());
                            textSliderView.getBundle()
                                    .putString("extra", "");

                            mDemoSlider.addSlider(textSliderView);
                        }
                    } else {
                        for (int j = 0; j < url_maps.size(); j++) {
                            DefaultSliderView textSliderView = new DefaultSliderView(context);
                            textSliderView
                                    .description("")
                                    .image(url_maps.get(j))
                                    .setScaleType(BaseSliderView.ScaleType.CenterInside)
                                    .setOnSliderClickListener(this);
                            textSliderView.bundle(new Bundle());
                            textSliderView.getBundle()
                                    .putString("extra", "");

                            mDemoSlider.addSlider(textSliderView);
                        }
                    }
                }
                mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
                mDemoSlider.setCustomAnimation(new DescriptionAnimation());
                mDemoSlider.setDuration(10000);
                mDemoSlider.addOnPageChangeListener(this);
                sliderRelativeLayout.setVisibility(View.VISIBLE);
            }

                else {
                if (mProgressBarHandler != null) {
                    mProgressBarHandler.hide();
                    mProgressBarHandler = null;
                }
                boolean isNetwork = Util.checkNetwork(context);


                if (isNetwork == true) {

                    my_recycler_view.setLayoutManager(mLayoutManager);
                    adapter = new RecyclerViewDataAdapter(context, allSampleData, url_maps, firstTime, MainActivity.vertical);
                    my_recycler_view.setAdapter(adapter);
                    my_recycler_view.setVisibility(View.VISIBLE);


                    asynLoadVideos = new AsynLoadVideos();
                    asynLoadVideos.executeOnExecutor(threadPoolExecutor, menuList.get(counter).getSectionId());
                    // default data
                    /*asynLoadVideos = new AsynLoadVideos();
                    asynLoadVideos.executeOnExecutor(threadPoolExecutor,menuList.get(counter).getSectionId());*/

                } else {
                    noInternetLayout.setVisibility(View.VISIBLE);
                }
            }
            return;
        }

        @Override
        protected void onPreExecute() {

            progressBarHandler = new ProgressBarHandler(getActivity());
            progressBarHandler.show();

        }

        @Override
        public void onSliderClick(BaseSliderView slider) {

        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }
    /*private class AsynLoadImageUrls extends AsyncTask<Void, Void, Void> {
        String responseStr;
        int statusCode;
        ProgressDialog pDialog;
        public ProgressDialog internetSpeedDialog = null;


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
                    getActivity().runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            try {
                                if (internetSpeedDialog != null && internetSpeedDialog.isShowing()) {
                                    internetSpeedDialog.dismiss();
                                }
                            } catch (IllegalArgumentException ex) {
                                responseStr = "0";
                            }

                        }

                    });

                }catch (IOException e) {
                    try {
                        if (internetSpeedDialog != null && internetSpeedDialog.isShowing()) {
                            internetSpeedDialog.dismiss();
                        }
                    }
                    catch(IllegalArgumentException ex)
                    {
                        responseStr = "0";
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

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
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                        }
                    });
                }
            } catch (JSONException e1) {
                try {
                    if (internetSpeedDialog != null && internetSpeedDialog.isShowing()) {
                        internetSpeedDialog.dismiss();
                    }
                }
                catch(IllegalArgumentException ex)
                {
                    responseStr = "0";
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                        }
                    });
                    e1.printStackTrace();
                }
            }

            catch (Exception e)
            {
                try {
                    if (internetSpeedDialog != null && internetSpeedDialog.isShowing()) {
                        internetSpeedDialog.dismiss();
                    }
                }
                catch(IllegalArgumentException ex)
                {
                    responseStr = "0";
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {


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
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                       // createDummyData();

                    }
                });
            }

        }

        @Override
        protected void onPreExecute() {
            if (internetSpeedDialog!=null && internetSpeedDialog.isShowing()){
                pDialog = internetSpeedDialog;
            }else{
              *//*  pDialog = new ProgressDialog(MainActivity.this);
                pDialog.setMessage(getResources().getString(R.string.loading_str));
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(false);
                pDialog.show();*//*
                pDialog = new ProgressDialog(context,R.style.MyTheme);
                pDialog.setCancelable(false);
                pDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Large_Inverse);
                pDialog.setIndeterminate(false);
                pDialog.setIndeterminateDrawable(getResources().getDrawable(R.drawable.progress_rawable));
                pDialog.show();
            }

        }


    }*/
    public void myOnKeyDown(){
        //do whatever you want here
        if (asynLoadMenuItems != null){
            asynLoadMenuItems.cancel(true);
        }

        if (asynLoadVideos != null){
            asynLoadVideos.cancel(true);
        }
       /* ActivityCompat.finishAffinity(getActivity());
        getActivity().finish();
        System.exit(0);*/

    }
   /* *//***************chromecast**********************//*

    private void showIntroductoryOverlay() {


        if (mIntroductoryOverlay != null) {
            mIntroductoryOverlay.remove();
        }


        if ((mediaRouteMenuItem != null) && mediaRouteMenuItem.isVisible()) {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    *//*mIntroductoryOverlay =
                            new IntroductoryOverlay.Builder(
                            getActivity(), mediaRouteMenuItem)
                            .setTitleText(getActivity().getString(R.string.introducing_cast))
                            .setOverlayColor(R.color.primary)
                            .setSingleTime()
                            .setOnOverlayDismissedListener(
                                    new IntroductoryOverlay.OnOverlayDismissedListener() {
                                        @Override
                                        public void onOverlayDismissed() {
                                            mIntroductoryOverlay = null;
                                        }
                                    })
                            .build();


                    mIntroductoryOverlay.show();*//*
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
                   *//* if (mCastSession != null && mCastSession.isConnected()) {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                watchMovieButton.setText(getResources().getString(R.string.movie_details_cast_now_button_title));
                            }
                        });

                    }*//*
                    if (mPlaybackState == PlaybackState.PLAYING) {
                        mVideoView.pause();
                        loadRemoteMedia(mSeekbar.getProgress(), true);
                        return;
                    } else {

                        mPlaybackState = PlaybackState.IDLE;
                        updatePlaybackLocation(PlaybackLocation.REMOTE);
                    }
                }
                //   updatePlayButton(mPlaybackState);
                //invalidateOptionsMenu();
            }

            private void onApplicationDisconnected() {
               *//* if (mCastSession != null && mCastSession.isConnected()) {
                    watchMovieButton.setText(getResources().getString(R.string.movie_details_watch_video_button_title));
                }*//*
                //watchMovieButton.setText(getResources().getString(R.string.movie_details_watch_video_button_title));
                updatePlaybackLocation(PlaybackLocation.LOCAL);
                mPlaybackState = PlaybackState.IDLE;
                mLocation = PlaybackLocation.LOCAL;

                //invalidateOptionsMenu();
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
                Intent intent = new Intent(context, ExpandedControlsActivity.class);
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

   *//* private void setCoverArtStatus(String url) {
        if (url != null) {
            mAquery.id(mCoverArt).image(url);
            mCoverArt.setVisibility(View.VISIBLE);
            mVideoView.setVisibility(View.INVISIBLE);
        } else {
            mCoverArt.setVisibility(View.GONE);
            mVideoView.setVisibility(View.VISIBLE);
        }
    }*//*

    private void stopTrickplayTimer() {
        //Log.d(TAG, "Stopped TrickPlay Timer");
        if (mSeekbarTimer != null) {
            mSeekbarTimer.cancel();
        }
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
            //getSupportActionBar().show();
            mControllers.setVisibility(View.VISIBLE);
        } else {
            if (!Util.isOrientationPortrait(context)) {
                //getSupportActionBar().hide();
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
                    mControllersVisible = false;
                }
            });

        }
    }



    *//***************chromecast**********************/

    @Override
    public void onResume() {
        super.onResume();

        if (mProgressBarHandler != null) {
            mProgressBarHandler.hide();
            mProgressBarHandler = null;
        }
     /*   *//***************chromecast**********************//*
        mCastContext.addCastStateListener(mCastStateListener);
        mCastContext.getSessionManager().addSessionManagerListener(
                mSessionManagerListener, CastSession.class);
        if (mCastSession == null) {
            mCastSession = CastContext.getSharedInstance(context).getSessionManager()
                    .getCurrentCastSession();
        }
        *//***************chromecast**********************//*
*/
        getActivity().invalidateOptionsMenu();

    }
@Override
public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
        case R.id.media_route_menu_item:
            // Not implemented here
            return false;
        default:
            break;
    }

    return false;
}
    class RetrieveFeedTask extends AsyncTask<String, Void, Void> {

        private Exception exception;
        private ProgressBarHandler phandler;

        protected Void doInBackground(String... urls) {
            try {


                URL url = new URL(urls[0]);
                Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                videoHeight = bmp.getHeight();
                videoWidth = bmp.getWidth();


                Log.v("SUBHA", "videoHeight=============="+videoHeight);
                Log.v("SUBHA", "videoWidth=============="+videoWidth);

                return null;
            } catch (Exception e) {
                this.exception = e;
                return null;
            }
        }

        protected void onPostExecute(Void feed) {
            // TODO: check this.exception
            // TODO: do something with the feed

            if(phandler!=null && phandler.isShowing())
            {
                phandler.hide();
            }

            Log.v("SUBHA", "HHH");
            loadui = new AsynLOADUI();
            loadui.executeOnExecutor(threadPoolExecutor);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            phandler = new ProgressBarHandler(getActivity());
            phandler.show();

        }
    }
}





