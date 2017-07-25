package com.release.reelAfrican.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.androidquery.AQuery;
import com.google.android.gms.cast.MediaInfo;
import com.google.android.gms.cast.MediaMetadata;
import com.google.android.gms.cast.MediaTrack;
import com.google.android.gms.cast.framework.CastButtonFactory;
import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.cast.framework.CastSession;
import com.google.android.gms.cast.framework.CastStateListener;
import com.google.android.gms.cast.framework.IntroductoryOverlay;
import com.google.android.gms.cast.framework.SessionManagerListener;
import com.google.android.gms.cast.framework.media.RemoteMediaClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.images.WebImage;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.release.reelAfrican.R;
import com.release.reelAfrican.adapter.EpisodesListAdapter;
import com.release.reelAfrican.adapter.LanguageCustomAdapter;
import com.release.reelAfrican.expandedcontrols.ExpandedControlsActivity;
import com.release.reelAfrican.model.APVModel;
import com.release.reelAfrican.model.CurrencyModel;
import com.release.reelAfrican.model.DataModel;
import com.release.reelAfrican.model.EpisodesListModel;
import com.release.reelAfrican.model.LanguageModel;
import com.release.reelAfrican.model.PPVModel;
import com.release.reelAfrican.utils.ProgressBarHandler;
import com.release.reelAfrican.utils.ResizableCustomView;
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
import org.json.JSONTokener;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HttpsURLConnection;



public class ShowWithEpisodesActivity extends AppCompatActivity {
    String movieDetailsStr = "";
    String priceForUnsubscribedStr,priceFosubscribedStr;
    PPVModel ppvmodel ;
    APVModel advmodel ;
    CurrencyModel currencymodel ;
    String PlanId = "";
    private static final int MAX_LINES = 2;
    ProgressBarHandler loadMovieDetailspDialog;
    AsynGetReviewDetails asynGetReviewDetails;
    int ratingAddedByUser = 1;


    ///****rating****///

    TextView viewRatingTextView;
    String movieIdStr;

    /*** rating***///
    String rating = "0";
    String reviews = "0";
    int isReviewThere = 1;
    int isRatingThere = 1;


    int selectedPurchaseType = 0 ; // selectedPurchaseType = 1(for show),selectedPurchaseType = 2(for season),selectedPurchaseType = 3(for episode)
    /*subtitle-------------------------------------*/

    String filename = "";
    static File mediaStorageDir;

    ArrayList<String> SubTitleName = new ArrayList<>();
    ArrayList<String> SubTitlePath = new ArrayList<>();
    ArrayList<String> FakeSubTitlePath = new ArrayList<>();
    ArrayList<String> ResolutionFormat = new ArrayList<>();
    ArrayList<String>ResolutionUrl = new ArrayList<>();
    ArrayList<String> SubTitleLanguage = new ArrayList<>();

        /*subtitle-------------------------------------*/


    public enum PlaybackLocation {
        LOCAL,
        REMOTE
    }

    /**
     * List of various states that we can be in
     */
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


    // Added For The Voucher

    int isVoucher = 0;
    String PurchageType = "";
    String VoucherCode = "";

    TextView content_label,content_name,voucher_success;
    EditText voucher_code;
    Button apply,watch_now;
    boolean watch_status = false;
    String ContentName="";
    AlertDialog voucher_alert;
    String sucessMsg;

    // voucher ends here //

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


    MediaInfo mediaInfo;
    String ipAddressStr="";



    public static ProgressBarHandler progressBarHandler;
    String email,id;
    LanguageCustomAdapter languageCustomAdapter;
    String Default_Language = "";
    String Previous_Selected_Language="";
    int  prevPosition = 0;

    RelativeLayout viewStoryLayout;

    //Add By Bibhu Later.
    TextView videoStoryTextView;
    Button storyViewMoreButton;

    boolean isExpanded = false;
    AsynLoadVideoUrls asynLoadVideoUrls;
    AsynValidateUserDetails asynValidateUserDetails;
    AsynLoadMovieDetails asynLoadMovieDetails;
    AsynFavoriteAdd asynFavoriteAdd;
    Toolbar mActionBarToolbar;
   // ProgressBarHandler pDialog;
    int isLogin = 0;
    String movieUniqueId = "";

    String censorRatingStr = "";
    private boolean isThirdPartyTrailer = false;
    AsynEpisodeDetails asynEpisodeDetails;
    int spinnerPosition = 0;
    ImageView moviePoster,favorite_view_episode;
    ImageView playButton;
    ImageButton offlineImageButton;
    Button viewTrailerButton, btnmore,watchTrailerButton;
    TextView videoTitle, videoGenreTextView, videoDurationTextView, videoCensorRatingTextView,videoCensorRatingTextView1, videoReleaseDateTextView, videoCastCrewTitleTextView;
    RatingBar ratingBar;
    SharedPreferences pref;
    Spinner season_spinner;
    ArrayList<String>season;
    EpisodesListModel itemToPlay;
    String videoResolution = "BEST";
    boolean castStr = false;
    int isFavorite ;
    String Season_Value = "";
    int loginresultcode =0;

    AlertDialog alert;


    RecyclerView.LayoutManager mLayoutManager;
    String movieStreamUniqueId,bannerImageId,posterImageId
            ,permalinkStr;
    String movieTrailerUrlStr,isEpisode= "";
    String movieReleaseDateStr = "";
    String name,loggedInStr,planid;
    static String _permalink;
    RelativeLayout noInternetConnectionLayout,noDataLayout,iconImageRelativeLayout,bannerImageRelativeLayout;
    LinearLayout story_layout;
    int isFreeContent,isPPV,isConverted,contentTypesId,isAPV;
    RecyclerView seasontiveLayout;
    int status = 0;
    int corePoolSize = 60;
    int maximumPoolSize = 80;
    int keepAliveTime = 10;
    int isSubscribedDataStr = 0;
    BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<Runnable>(maximumPoolSize);
    Executor threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, workQueue);
    ArrayList<EpisodesListModel> itemData = new ArrayList<EpisodesListModel>();
    String movieNameStr;
   // TextView seasonTitleTextView;
    String episodeVideoUrlStr;
    TextView noDataTextView;
    TextView noInternetTextView;
    String isMemberSubscribed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_show_with_episodes);
        SharedPreferences isLoginPref = getSharedPreferences(Util.IS_LOGIN_SHARED_PRE, 0); // 0 - for private mode
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(DELETE_ACTION, new IntentFilter("ITEM_STATUS"));

        Util.goToLibraryplayer = false;

        isLogin = isLoginPref.getInt(Util.IS_LOGIN_PREF_KEY, 0);
        mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mActionBarToolbar);
        mActionBarToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back));
        mActionBarToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        moviePoster = (ImageView) findViewById(R.id.bannerImageView);
        btnmore= (Button) findViewById(R.id.btnMore);
        favorite_view_episode = (ImageView) findViewById(R.id.favorite_view_episode);

        Typeface videoGenreTextViewTypeface = Typeface.createFromAsset(getAssets(),getResources().getString(R.string.regular_fonts));
        btnmore.setTypeface(videoGenreTextViewTypeface);

        btnmore.setText(Util.getTextofLanguage(ShowWithEpisodesActivity.this, Util.VIEW_MORE, Util.DEFAULT_VIEW_MORE));

        btnmore.setVisibility(View.GONE);
        playButton = (ImageView) findViewById(R.id.playButton);
        watchTrailerButton = (Button) findViewById(R.id.viewTrailerButton);
        Typeface submitButtonTypeface = Typeface.createFromAsset(getAssets(),getResources().getString(R.string.regular_fonts));
        watchTrailerButton.setTypeface(submitButtonTypeface);
        watchTrailerButton.setText(Util.getTextofLanguage(ShowWithEpisodesActivity.this,Util.VIEW_TRAILER,Util.DEFAULT_VIEW_TRAILER));

        playButton.setVisibility(View.GONE);

        offlineImageButton = (ImageButton) findViewById(R.id.offlineImageButton);
        videoTitle = (TextView) findViewById(R.id.videoTitle);
        videoGenreTextView = (TextView) findViewById(R.id.videoGenreTextView);
        videoDurationTextView = (TextView) findViewById(R.id.videoDurationTextView);
        videoCensorRatingTextView = (TextView) findViewById(R.id.videoCensorRatingTextView);
        videoReleaseDateTextView = (TextView) findViewById(R.id.videoReleaseDateTextView);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        videoStoryTextView = (TextView) findViewById(R.id.videoStoryTextView);
        videoCastCrewTitleTextView = (TextView) findViewById(R.id.videoCastCrewTitleTextView);
        playButton.setVisibility(View.GONE);

        videoCastCrewTitleTextView.setVisibility(View.GONE);
        ppvmodel = new PPVModel();
        advmodel = new APVModel();
        currencymodel = new CurrencyModel();
        PlanId = (Util.getTextofLanguage(ShowWithEpisodesActivity.this, Util.PLAN_ID, Util.DEFAULT_PLAN_ID)).trim();
        season_spinner= (Spinner) findViewById(R.id.seasonSpinner);

        if (Build.VERSION.SDK_INT < 23){
            season_spinner.setBackgroundResource(R.drawable.lollipop_spinner_theme);
        }

        season_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                spinnerPosition = position;
                if (asynEpisodeDetails!=null){
                    asynEpisodeDetails.cancel(true);
                }
                btnmore.setVisibility(View.VISIBLE);
                asynEpisodeDetails=new AsynEpisodeDetails();
                asynEpisodeDetails.executeOnExecutor(threadPoolExecutor);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        videoCensorRatingTextView1 = (TextView) findViewById(R.id.videoCensorRatingTextView1);
      /*  seasonTitleTextView = (TextView) findViewById(R.id.seasonTitleTextView);
        seasonTitleTextView.setText(Util.getTextofLanguage(ShowWithEpisodesActivity.this, Util.SEASON, Util.DEFAULT_SEASON));
*/
        seasontiveLayout= (RecyclerView) findViewById(R.id.featureContent);
        noInternetConnectionLayout = (RelativeLayout)findViewById(R.id.noInternet);
        noDataLayout = (RelativeLayout)findViewById(R.id.noData);
        noInternetTextView =(TextView)findViewById(R.id.noInternetTextView);
        noDataTextView =(TextView)findViewById(R.id.noDataTextView);
        noInternetTextView.setText(Util.getTextofLanguage(ShowWithEpisodesActivity.this,Util.NO_INTERNET_CONNECTION,Util.DEFAULT_NO_INTERNET_CONNECTION));
        noDataTextView.setText(Util.getTextofLanguage(ShowWithEpisodesActivity.this,Util.NO_CONTENT,Util.DEFAULT_NO_CONTENT));

        mLayoutManager = new LinearLayoutManager(ShowWithEpisodesActivity.this, LinearLayoutManager.HORIZONTAL, false);
        iconImageRelativeLayout = (RelativeLayout) findViewById(R.id.iconImageRelativeLayout);
        bannerImageRelativeLayout = (RelativeLayout) findViewById(R.id.bannerImageRelativeLayout);
        story_layout = (LinearLayout) findViewById(R.id.story_layout);
        seasontiveLayout.setVisibility(View.GONE);
        episodeVideoUrlStr = Util.getTextofLanguage(ShowWithEpisodesActivity.this,Util.NO_DATA,Util.DEFAULT_NO_DATA);
        seasontiveLayout.addItemDecoration(new HorizotalSpaceItemDecoration(20));
        videoStoryTextView = (TextView) findViewById(R.id.videoStoryTextView);
        storyViewMoreButton = (Button) findViewById(R.id.storyViewMoreButton);
        // *** rating***////
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        ratingBar.setFocusable(false);
        ratingBar.setVisibility(View.GONE);

        ratingBar.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        viewRatingTextView = (TextView) findViewById(R.id.viewRatingTextView);



        // *****rating********///
        viewRatingTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent reviewIntent = new Intent(ShowWithEpisodesActivity.this,ReviewActivity.class);
                reviewIntent.putExtra("muviId",movieIdStr.trim());
                startActivityForResult(reviewIntent, 30060);
            }
        });

        /***favorite *****/
        favorite_view_episode.setVisibility(View.GONE);

        favorite_view_episode.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {

                if (pref != null) {
                    loggedInStr = pref.getString("PREFS_LOGGEDIN_ID_KEY", null);
                }

                if (loggedInStr != null){
                    if (isFavorite==1){
                        AsynFavoriteDelete asynFavoriteDelete=new AsynFavoriteDelete();
                        asynFavoriteDelete.execute();
                    }else{
                        Log.v("SUBHA","favorite");
                        AsynFavoriteAdd asynFavoriteAdd =new AsynFavoriteAdd();
                        asynFavoriteAdd.execute();

                    }
                }
                else {
                    Util.favorite_clicked=true;
                    final Intent registerActivity = new Intent(ShowWithEpisodesActivity.this, RegisterActivity.class);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            registerActivity.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            registerActivity.putExtra("from", this.getClass().getName());
                            startActivity(registerActivity);

                        }
                    });

                }

            }
        });

        /***favorite *****/


/*

        */
/**** show or hide storyViewMoreButton button ***//*

        ViewTreeObserver vto = videoStoryTextView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Layout l = videoStoryTextView.getLayout();
                if (l != null) {
                    int lines = l.getLineCount();
                    if (lines > 0)
                        if (l.getEllipsisCount(lines - 1) > 0){
                            storyViewMoreButton.setVisibility(View.VISIBLE);
                        }
                }
            }
        });


        storyViewMoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("SUBHA","isExpanded"+isExpanded);

                if (isExpanded == false){
                    videoStoryTextView.setMaxLines(100);
                    isExpanded = true;
                    storyViewMoreButton.setText(Util.getTextofLanguage(ShowWithEpisodesActivity.this, Util.VIEW_LESS, Util.DEFAULT_VIEW_LESS));
                    Log.v("SUBHA","View Less");


                }else{
                    videoStoryTextView.setMaxLines(2);
                    isExpanded = false;
                    storyViewMoreButton.setText(Util.getTextofLanguage(ShowWithEpisodesActivity.this, Util.VIEW_MORE, Util.DEFAULT_VIEW_MORE));
                    Log.v("SUBHA","View More");

                }
                ViewGroup.LayoutParams params = videoStoryTextView.getLayoutParams();
                params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                videoStoryTextView.setLayoutParams(params);
            }
        });
*/

        watchTrailerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataModel dbModel = new DataModel();
                dbModel.setIsFreeContent(isFreeContent);
                dbModel.setIsAPV(isAPV);
                dbModel.setIsPPV(isPPV);
                dbModel.setIsConverted(isConverted);
                dbModel.setMovieUniqueId(movieUniqueId);
                dbModel.setStreamUniqueId(movieStreamUniqueId);
                dbModel.setThirdPartyUrl("");
                dbModel.setVideoTitle(movieNameStr);
                dbModel.setVideoStory(movieDetailsStr);
                dbModel.setVideoGenre(videoGenreTextView.getText().toString());
                dbModel.setVideoDuration(videoDurationTextView.getText().toString());
                dbModel.setVideoReleaseDate(videoReleaseDateTextView.getText().toString());
                dbModel.setCensorRating(censorRatingStr);
                dbModel.setCastCrew(castStr);
                dbModel.setVideoUrl(movieTrailerUrlStr);
                dbModel.setVideoResolution("BEST");
                dbModel.setContentTypesId(contentTypesId);

                Util.dataModel = dbModel;


                if ((movieTrailerUrlStr.matches("")) || (movieTrailerUrlStr.matches(Util.getTextofLanguage(ShowWithEpisodesActivity.this,Util.NO_DATA,Util.DEFAULT_NO_DATA)))) {
                    AlertDialog.Builder dlgAlert = new AlertDialog.Builder(ShowWithEpisodesActivity.this, R.style.MyAlertDialogStyle);
                    dlgAlert.setMessage(Util.getTextofLanguage(ShowWithEpisodesActivity.this,Util.NO_VIDEO_AVAILABLE,Util.DEFAULT_NO_VIDEO_AVAILABLE));
                    dlgAlert.setTitle(Util.getTextofLanguage(ShowWithEpisodesActivity.this,Util.SORRY,Util.DEFAULT_SORRY));
                    dlgAlert.setPositiveButton(Util.getTextofLanguage(ShowWithEpisodesActivity.this,Util.BUTTON_OK,Util.DEFAULT_BUTTON_OK), null);
                    dlgAlert.setCancelable(false);
                    dlgAlert.setPositiveButton(Util.getTextofLanguage(ShowWithEpisodesActivity.this,Util.BUTTON_OK,Util.DEFAULT_BUTTON_OK),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    dlgAlert.create().show();
                    return;
                } else if (isThirdPartyTrailer == false) {
                    if (mCastSession != null && mCastSession.isConnected()) {


                        MediaMetadata movieMetadata = new MediaMetadata(MediaMetadata.MEDIA_TYPE_MOVIE);

                        movieMetadata.putString(MediaMetadata.KEY_SUBTITLE, movieReleaseDateStr);
                        movieMetadata.putString(MediaMetadata.KEY_TITLE, movieNameStr + " - Trailer");
                        movieMetadata.addImage(new WebImage(Uri.parse(posterImageId.trim())));
                        movieMetadata.addImage(new WebImage(Uri.parse(posterImageId.trim())));
                        JSONObject jsonObj = null;
                        try {
                            jsonObj = new JSONObject();
                            jsonObj.put("description", movieNameStr);


                            //  This Code Is Added For Video Log By Bibhu..

                            jsonObj.put("authToken", Util.authTokenStr.trim());
                            jsonObj.put("user_id", pref.getString("PREFS_LOGGEDIN_ID_KEY", null));
                            jsonObj.put("ip_address",ipAddressStr.trim());
                            jsonObj.put("movie_id",movieUniqueId);
                            jsonObj.put("episode_id","");


                            jsonObj.put("played_length","0");


                            jsonObj.put("watch_status", "start");
                            jsonObj.put("device_type", "2");
                            jsonObj.put("log_id", "0");

                            // restrict_stream_id is always set to be "0" , bcoz it's a triler.
                            jsonObj.put("restrict_stream_id", "0");

                            jsonObj.put("domain_name",Util.rootUrl().trim().substring(0, Util.rootUrl().trim().length()-6));
                            jsonObj.put("is_log", "1");

                            //=====================End===================//


                            // This  Code Is Added For Drm BufferLog By Bibhu ...

                            jsonObj.put("resolution", "BEST");
                            jsonObj.put("start_time","0");
                            jsonObj.put("end_time", "0");
                            jsonObj.put("log_unique_id", "0");
                            jsonObj.put("location", "0");
                            jsonObj.put("video_type", "");
                            jsonObj.put("totalBandwidth", "0");
                            // This is added only to identify, the videolog is called for trailer
                            jsonObj.put("content_type", "2");

                            //====================End=====================//

                        } catch (JSONException e) {
                        }

                        mediaInfo = new MediaInfo.Builder(movieTrailerUrlStr.trim())
                                .setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
                                .setContentType("videos/mp4")
                                .setMetadata(movieMetadata)
                                .setStreamDuration(15 * 1000)
                                .setCustomData(jsonObj)
                                .build();
                        mSelectedMedia = mediaInfo;
                        // Util.showQueuePopup(ShowWithEpisodesActivity.this, view, mediaInfo);

                        togglePlayback();

                    } else {
                        final Intent playVideoIntent = new Intent(ShowWithEpisodesActivity.this, TrailerActivity.class);

                        runOnUiThread(new Runnable() {
                            public void run() {
                                playVideoIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                startActivity(playVideoIntent);

                            }
                        });
                    }
                }
                 else {
                    if (movieTrailerUrlStr.contains("://www.youtube") || movieTrailerUrlStr.contains("://www.youtu.be")) {
                        if (movieTrailerUrlStr.contains("live_stream?channel")) {
                            final Intent playVideoIntent = new Intent(ShowWithEpisodesActivity.this, ThirdPartyPlayer.class);

                            runOnUiThread(new Runnable() {
                                public void run() {
                                    playVideoIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                    startActivity(playVideoIntent);

                                }
                            });
                        } else {

                            final Intent playVideoIntent = new Intent(ShowWithEpisodesActivity.this, YouTubeAPIActivity.class);
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    playVideoIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                    startActivity(playVideoIntent);


                                }
                            });

                        }
                    }else{
                        final Intent playVideoIntent = new Intent(ShowWithEpisodesActivity.this, ThirdPartyPlayer.class);
                        runOnUiThread(new Runnable() {
                            public void run() {
                                playVideoIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                startActivity(playVideoIntent);

                            }
                        });
                    }

                }

            }
        });

        pref = getSharedPreferences(Util.LOGIN_PREF, 0);
        loggedInStr = pref.getString("PREFS_LOGGEDIN_KEY", null);
        planid = pref.getString("PREFS_LOGGEDIN_ID_KEY", null);

        if(planid==null)
            planid = "0";

        if(loggedInStr==null)
            loggedInStr = "0";

        try {
            isSubscribedDataStr = Integer.parseInt(pref.getString("PREFS_LOGIN_ISSUBSCRIBED_KEY", null));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        permalinkStr = getIntent().getStringExtra(Util.PERMALINK_INTENT_KEY);

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isLogin == 1) {
                    if (pref != null) {
                        String loggedInStr = pref.getString("PREFS_LOGGEDIN_KEY", null);

                        if (loggedInStr == null) {
                            final Intent registerActivity = new Intent(ShowWithEpisodesActivity.this, RegisterActivity.class);
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    registerActivity.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                    Util.check_for_subscription = 1;

                                    startActivity(registerActivity);


                                }
                            });

                            //showLoginDialog();
                        } else {
                            String loggedinDateStr = pref.getString("date", null);
                            if (loggedinDateStr != null) {
                                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                                Date loggedInDate = null;
                                try {
                                    loggedInDate = formatter.parse(loggedinDateStr);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                Date today = new Date();
                                long differenceInDays = (int) Util.calculateDays(loggedInDate, today) + 1;
                                if (differenceInDays >= 7) {
                                    SharedPreferences.Editor editor = pref.edit();
                                    editor.clear();
                                    editor.commit();

                                    final Intent registerActivity = new Intent(ShowWithEpisodesActivity.this, RegisterActivity.class);
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            registerActivity.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                            Util.check_for_subscription = 1;

                                            startActivity(registerActivity);


                                        }
                                    });
                                } else {

                                    if (Util.checkNetwork(ShowWithEpisodesActivity.this) == true) {

                                        /*if (isFreeContent == 1) {
                                            asynLoadVideoUrls = new AsynLoadVideoUrls();
                                            asynLoadVideoUrls.executeOnExecutor(threadPoolExecutor);
                                        } else {*/
                                            asynValidateUserDetails = new AsynValidateUserDetails();
                                            asynValidateUserDetails.executeOnExecutor(threadPoolExecutor);
                                      //  }
                                    } else {
                                        Util.showToast(ShowWithEpisodesActivity.this,Util.getTextofLanguage(ShowWithEpisodesActivity.this,Util.NO_INTERNET_CONNECTION,Util.DEFAULT_NO_INTERNET_CONNECTION));

                                       // Toast.makeText(ShowWithEpisodesActivity.this, Util.getTextofLanguage(ShowWithEpisodesActivity.this,Util.NO_INTERNET_CONNECTION,Util.DEFAULT_NO_INTERNET_CONNECTION), Toast.LENGTH_LONG).show();
                                    }


                                }
                            }
                        }
                    } else {

                        final Intent registerActivity = new Intent(ShowWithEpisodesActivity.this, RegisterActivity.class);
                        runOnUiThread(new Runnable() {
                            public void run() {
                                registerActivity.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                Util.check_for_subscription = 1;

                                startActivity(registerActivity);


                            }
                        });                 }
                }else{
                    if (Util.checkNetwork(ShowWithEpisodesActivity.this) == true) {
                        // subhalaxmi

                        asynLoadVideoUrls = new AsynLoadVideoUrls();
                        asynLoadVideoUrls.executeOnExecutor(threadPoolExecutor);

                    } else {
                        Util.showToast(ShowWithEpisodesActivity.this,Util.getTextofLanguage(ShowWithEpisodesActivity.this,Util.NO_INTERNET_CONNECTION,Util.DEFAULT_NO_INTERNET_CONNECTION));

                       // Toast.makeText(ShowWithEpisodesActivity.this, Util.getTextofLanguage(ShowWithEpisodesActivity.this,Util.NO_INTERNET_CONNECTION,Util.DEFAULT_NO_INTERNET_CONNECTION), Toast.LENGTH_LONG).show();
                    }
                }


            }
        });
        btnmore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                final Intent episode = new Intent(ShowWithEpisodesActivity.this, Episode_list_Activity.class);
                episode.putExtra(Util.PERMALINK_INTENT_KEY,_permalink);
                episode.putExtra(Util.VIDEO_TITLE_INTENT_KEY,movieNameStr.trim());

                episode.putExtra(Util.GENRE_INTENT_KEY,videoGenreTextView.getText().toString());
                episode.putExtra(Util.STORY_INTENT_KEY,videoStoryTextView.getText().toString());
                episode.putExtra(Util.CENSOR_RATING_INTENT_KEY,censorRatingStr);
                episode.putExtra(Util.CAST_INTENT_KEY,castStr);
                episode.putExtra(Util.SEASON_INTENT_KEY,Season_Value);
                episode.putExtra("content_types_id",""+contentTypesId);

                Log.v("SUBHA","season intent = "+Season_Value);

                runOnUiThread(new Runnable() {
                    public void run() {
                        episode.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

                        startActivity(episode);


                    }
                });

            }
        });
        videoCastCrewTitleTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent episode = new Intent(ShowWithEpisodesActivity.this, CastAndCrewActivity.class);
                episode.putExtra("cast_movie_id",movieUniqueId.trim());
                runOnUiThread(new Runnable() {
                    public void run() {
                        episode.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(episode);


                    }
                });

            }
        });

           /*subtitle-------------------------------------*/


        if (ContextCompat.checkSelfPermission(ShowWithEpisodesActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(ShowWithEpisodesActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(ShowWithEpisodesActivity.this,
                        new String[]{Manifest.permission
                                .WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_CONTACTS},
                        111);
            } else {
                ActivityCompat.requestPermissions(ShowWithEpisodesActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        111);

            }
        } else {
            //Call whatever you want
            if (Util.checkNetwork(ShowWithEpisodesActivity.this)) {

                asynLoadMovieDetails = new AsynLoadMovieDetails();
                asynLoadMovieDetails.executeOnExecutor(threadPoolExecutor);

            } else {
                Util.showToast(ShowWithEpisodesActivity.this,Util.getTextofLanguage(ShowWithEpisodesActivity.this,Util.NO_INTERNET_CONNECTION,Util.DEFAULT_NO_INTERNET_CONNECTION));

               // Toast.makeText(getApplicationContext(), Util.getTextofLanguage(ShowWithEpisodesActivity.this, Util.NO_INTERNET_CONNECTION, Util.DEFAULT_NO_INTERNET_CONNECTION), Toast.LENGTH_LONG).show();
                finish();
            }
        }
      /*  AsynLoadMovieDetails asyncLoadVideos = new AsynLoadMovieDetails();
        asyncLoadVideos.executeOnExecutor(threadPoolExecutor);*/
           /*subtitle-------------------------------------*/

 /*chromecast-------------------------------------*/

        mAquery = new AQuery(this);

        // setupControlsCallbacks();
        setupCastListener();
        mCastContext = CastContext.getSharedInstance(this);
        mCastContext.registerLifecycleCallbacksBeforeIceCreamSandwich(this, savedInstanceState);
        mCastSession = mCastContext.getSessionManager().getCurrentCastSession();

        boolean shouldStartPlayback = false;
        int startPosition = 0;

         /*   MediaMetadata movieMetadata = new MediaMetadata(MediaMetadata.MEDIA_TYPE_MOVIE);

            movieMetadata.putString(MediaMetadata.KEY_SUBTITLE, movieName.getText().toString());
            movieMetadata.putString(MediaMetadata.KEY_TITLE,  movieName.getText().toString());
            movieMetadata.addImage(new WebImage(Uri.parse(posterImageId.trim())));
            movieMetadata.addImage(new WebImage(Uri.parse(posterImageId.trim())));
            JSONObject jsonObj = null;
            try {
                jsonObj = new JSONObject();
                jsonObj.put("description", movieName.getText().toString());
            } catch (JSONException e) {
                Log.e(TAG, "Failed to add description to the json object", e);
            }

            mediaInfo = new MediaInfo.Builder(castVideoUrl.trim())
                    .setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
                    .setContentType("videos/mp4")
                    .setMetadata(movieMetadata)
                    .setStreamDuration(15 * 1000)
                    .setCustomData(jsonObj)
                    .build();
            mSelectedMedia = mediaInfo;*/

        // see what we need to play and where
           /* Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                mSelectedMedia = getIntent().getParcelableExtra("media");
                //setupActionBar();
                boolean shouldStartPlayback = bundle.getBoolean("shouldStart");
                int startPosition = bundle.getInt("startPosition", 0);
                // mVideoView.setVideoURI(Uri.parse(mSelectedMedia.getContentId()));
               // Log.d(TAG, "Setting url of the VideoView to: " + mSelectedMedia.getContentId());
                if (shouldStartPlayback) {
                    // this will be the case only if we are coming from the
                    // CastControllerActivity by disconnecting from a device
                    mPlaybackState = PlaybackState.PLAYING;
                    updatePlaybackLocation(PlaybackLocation.LOCAL);
                    updatePlayButton(mPlaybackState);
                    if (startPosition > 0) {
                        // mVideoView.seekTo(startPosition);
                    }
                    // mVideoView.start();
                    //startControllersTimer();
                } else {
                    // we should load the video but pause it
                    // and show the album art.
                    if (mCastSession != null && mCastSession.isConnected()) {
                        updatePlaybackLocation(PlaybackLocation.REMOTE);
                    } else {
                        updatePlaybackLocation(PlaybackLocation.LOCAL);
                    }
                    mPlaybackState = PlaybackState.IDLE;
                    updatePlayButton(mPlaybackState);
                }
            }*/


        if (shouldStartPlayback) {
            // this will be the case only if we are coming from the
            // CastControllerActivity by disconnecting from a device
            mPlaybackState = PlaybackState.PLAYING;
            updatePlaybackLocation(PlaybackLocation.LOCAL);
            updatePlayButton(mPlaybackState);
            if (startPosition > 0) {
                // mVideoView.seekTo(startPosition);
            }
            // mVideoView.start();
            //startControllersTimer();
        } else {
            // we should load the video but pause it
            // and show the album art.
            if (mCastSession != null && mCastSession.isConnected()) {
                //watchMovieButton.setText(getResources().getString(R.string.movie_details_cast_now_button_title));
                updatePlaybackLocation(PlaybackLocation.REMOTE);
            } else {
                //watchMovieButton.setText(getResources().getString(R.string.movie_details_watch_video_button_title));

                updatePlaybackLocation(PlaybackLocation.LOCAL);
            }
            mPlaybackState = PlaybackState.IDLE;
            updatePlayButton(mPlaybackState);
        }
/***************chromecast**********************/
    }


    //Load Video Details Like VideoUrl,Release Date,Details,BannerUrl,rating,popularity etc.

    private class AsynLoadMovieDetails extends AsyncTask<Void, Void, Void> {
        String responseStr;
        int status;


        String movieTypeStr = "";
        String videoduration = "";
        String loggedInIdStr;

        @Override
        protected Void doInBackground(Void... params) {
            if (pref != null) {
                loggedInIdStr = pref.getString("PREFS_LOGGEDIN_ID_KEY", null);
            }
            try {
                HttpClient httpclient=new DefaultHttpClient();
                HttpPost httppost = new HttpPost(Util.rootUrl().trim()+Util.detailsUrl.trim());
                httppost.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");
                httppost.addHeader("authToken", Util.authTokenStr.trim());
                httppost.addHeader("permalink",permalinkStr);
                httppost.addHeader("user_id",loggedInIdStr);
                //  httppost.addHeader("country",((Global)getApplicationContext()).getCountryCode());
                SharedPreferences countryPref = getSharedPreferences(Util.COUNTRY_PREF, 0); // 0 - for private mode
                if (countryPref != null) {
                    String countryCodeStr = countryPref.getString("countryCode", null);
                    httppost.addHeader("country", countryCodeStr);
                }else{
                    httppost.addHeader("country", "IN");

                }
                httppost.addHeader("lang_code",Util.getTextofLanguage(ShowWithEpisodesActivity.this,Util.SELECTED_LANGUAGE_CODE,Util.DEFAULT_SELECTED_LANGUAGE_CODE));



                // Execute HTTP Post Request
                try {
                    HttpResponse response = httpclient.execute(httppost);
                    responseStr = EntityUtils.toString(response.getEntity());



                } catch (org.apache.http.conn.ConnectTimeoutException e){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (loadMovieDetailspDialog != null && loadMovieDetailspDialog.isShowing()) {
                                loadMovieDetailspDialog.hide();
                                loadMovieDetailspDialog = null;
                            }
                            noInternetConnectionLayout.setVisibility(View.VISIBLE);
                            noDataLayout.setVisibility(View.GONE);

                            story_layout.setVisibility(View.GONE);
                            bannerImageRelativeLayout.setVisibility(View.GONE);
                            iconImageRelativeLayout.setVisibility(View.GONE);

                            //Commented By Me

                          /*  movieDescription.setVisibility(View.GONE);
                            //movieThumbnailImageView.setVisibility(View.GONE);
                            moviePoster.setVisibility(View.GONE);
                            movieName.setVisibility(View.GONE);
                            movieType.setVisibility(View.GONE);
                            movieDescriptionTitle.setVisibility(View.GONE);
                            castTitleTextView.setVisibility(View.GONE);
                            crewTitleTextView.setVisibility(View.GONE);
                            castTextView.setVisibility(View.GONE);
                            crewTextView.setVisibility(View.GONE);
                            movieCensorRatingTitleTextView.setVisibility(View.GONE);
                            movieCensorRatingTextView.setVisibility(View.GONE);
                            movieGenreTitleTextView.setVisibility(View.GONE);
                            movieReleaseDateTitleTextView.setVisibility(View.GONE);
                            movieReleaseDate.setVisibility(View.GONE);
                            watchTrailerButton.setVisibility(View.GONE);*/

                            Util.showToast(ShowWithEpisodesActivity.this,Util.getTextofLanguage(ShowWithEpisodesActivity.this,Util.SLOW_INTERNET_CONNECTION,Util.DEFAULT_SLOW_INTERNET_CONNECTION));

                           // Toast.makeText(ShowWithEpisodesActivity.this, Util.getTextofLanguage(ShowWithEpisodesActivity.this,Util.SLOW_INTERNET_CONNECTION,Util.DEFAULT_SLOW_INTERNET_CONNECTION), Toast.LENGTH_LONG).show();


                        }

                    });

                }catch (IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (loadMovieDetailspDialog != null && loadMovieDetailspDialog.isShowing()) {
                                loadMovieDetailspDialog.hide();
                                loadMovieDetailspDialog = null;
                            }
                            noInternetConnectionLayout.setVisibility(View.GONE);
                            noDataLayout.setVisibility(View.VISIBLE);

                            story_layout.setVisibility(View.GONE);
                            bannerImageRelativeLayout.setVisibility(View.GONE);
                            iconImageRelativeLayout.setVisibility(View.GONE);

                            //Commented By Me
                          /*  movieDescription.setVisibility(View.GONE);
                            //movieThumbnailImageView.setVisibility(View.GONE);
                            moviePoster.setVisibility(View.GONE);
                            movieName.setVisibility(View.GONE);
                            movieType.setVisibility(View.GONE);
                            movieDescriptionTitle.setVisibility(View.GONE);
                            castTitleTextView.setVisibility(View.GONE);
                            crewTitleTextView.setVisibility(View.GONE);
                            castTextView.setVisibility(View.GONE);
                            crewTextView.setVisibility(View.GONE);
                            movieCensorRatingTitleTextView.setVisibility(View.GONE);
                            movieCensorRatingTextView.setVisibility(View.GONE);
                            movieGenreTitleTextView.setVisibility(View.GONE);
                            movieReleaseDateTitleTextView.setVisibility(View.GONE);
                            movieReleaseDate.setVisibility(View.GONE);
                            watchTrailerButton.setVisibility(View.GONE);*/

                        }

                    });
                    e.printStackTrace();
                }

                JSONObject myJson =null;
                if(responseStr!=null){
                    myJson = new JSONObject(responseStr);
                    status = Integer.parseInt(myJson.optString("code"));
                    season=new ArrayList<>();

                    Log.v("XXX","responseStr======"+responseStr);
                    /** rating*///
                    if (myJson.has("rating") && myJson.has("rating") != false && myJson.getString("rating").trim() != null && !myJson.getString("rating").trim().isEmpty() && !myJson.getString("rating").trim().equals("null") && !myJson.getString("rating").trim().equals("false")) {
                        rating = myJson.getString("rating");

                    }else{
                        isRatingThere = 0;
                    }

                    if (myJson.has("review") && myJson.has("review") != false && myJson.getString("review").trim() != null && !myJson.getString("review").trim().isEmpty() && !myJson.getString("review").trim().equals("null") && !myJson.getString("review").trim().equals("false")) {
                        reviews = myJson.getString("review");
                    }else{
                        isReviewThere = 0;
                    }

                    if ((myJson.has("epDetails")) && myJson.getString("epDetails").trim() != null && !myJson.getString("epDetails").trim().isEmpty() && !myJson.getString("epDetails").trim().equals("null") && !myJson.getString("epDetails").trim().matches("")) {
                        JSONObject epDetailsJson =myJson.getJSONObject("epDetails");
                        if ((epDetailsJson.has("series_number")) && epDetailsJson.getString("series_number").trim() != null && !epDetailsJson.getString("series_number").trim().isEmpty() && !epDetailsJson.getString("series_number").trim().equals("null") && !epDetailsJson.getString("series_number").trim().matches("") && !epDetailsJson.getString("series_number").trim().matches("0")) {
                            String s[] = epDetailsJson.getString("series_number").split(",");
                            Arrays.sort(s);
                            if (season!=null && season.size() > 0){
                                season.clear();
                            }

                            for(int j=0;j<s.length;j++){

                                season.add(Util.getTextofLanguage(ShowWithEpisodesActivity.this, Util.SEASON, Util.DEFAULT_SEASON)+" "+s[j]);


                            }
                          /*  for(int j=0;j<s.length;j++){

                                season.add(Util.getTextofLanguage(ShowWithEpisodesActivity.this, Util.SEASON, Util.DEFAULT_SEASON)+" "+s.get);


                            }*/
                        }

                    }


                }

                if (status > 0) {

                    if (status == 200) {

                        JSONObject mainJson = myJson.getJSONObject("movie");
                        if ((mainJson.has("name")) && mainJson.getString("name").trim() != null && !mainJson.getString("name").trim().isEmpty() && !mainJson.getString("name").trim().equals("null") && !mainJson.getString("name").trim().matches("")) {
                            movieNameStr = mainJson.getString("name");
                        }else{
                            movieNameStr = Util.getTextofLanguage(ShowWithEpisodesActivity.this,Util.NO_DATA,Util.DEFAULT_NO_DATA);

                        }

                        if ((mainJson.has("id")) && mainJson.getString("id").trim() != null && !mainJson.getString("id").trim().isEmpty() && !mainJson.getString("id").trim().equals("null") && !mainJson.getString("id").trim().matches("")) {
                            movieIdStr = mainJson.getString("id");
                        }


                        if ((mainJson.has("video_duration")) && mainJson.getString("video_duration").trim() != null && !mainJson.getString("video_duration").trim().isEmpty() && !mainJson.getString("video_duration").trim().equals("null") && !mainJson.getString("video_duration").trim().matches("")) {
                            videoduration = mainJson.getString("video_duration");


                        }else{
                            videoduration = Util.getTextofLanguage(ShowWithEpisodesActivity.this,Util.NO_DATA,Util.DEFAULT_NO_DATA);

                        }
                        if ((mainJson.has("trailerThirdpartyUrl")) && mainJson.getString("trailerThirdpartyUrl").trim() != null && !mainJson.getString("trailerThirdpartyUrl").trim().isEmpty() && !mainJson.getString("trailerThirdpartyUrl").trim().equals("null") && !mainJson.getString("trailerThirdpartyUrl").trim().matches("")) {
                            movieTrailerUrlStr = mainJson.getString("trailerThirdpartyUrl");
                            isThirdPartyTrailer = true;
                        }
                        else{

                            if ((mainJson.has("trailerUrl")) && mainJson.getString("trailerUrl").trim() != null && !mainJson.getString("trailerUrl").trim().isEmpty() && !mainJson.getString("trailerUrl").trim().equals("null") && !mainJson.getString("trailerUrl").trim().matches("")) {
                                movieTrailerUrlStr = mainJson.getString("trailerUrl");

                            }
                            else{
                                movieTrailerUrlStr = Util.getTextofLanguage(ShowWithEpisodesActivity.this,Util.NO_DATA,Util.DEFAULT_NO_DATA);
                            }
                            isThirdPartyTrailer = false;
                        }
                        if ((mainJson.has("genre")) && mainJson.getString("genre").trim() != null && !mainJson.getString("genre").trim().isEmpty() && !mainJson.getString("genre").trim().equals("null") && !mainJson.getString("genre").trim().matches("")) {
                            movieTypeStr = mainJson.getString("genre");
                            movieTypeStr = movieTypeStr.replaceAll("\\[", "");
                            movieTypeStr = movieTypeStr.replaceAll("\\]","");
                            movieTypeStr = movieTypeStr.replaceAll(","," , ");
                            movieTypeStr = movieTypeStr.replaceAll("\"", "");


                        }else{
                            movieTypeStr = Util.getTextofLanguage(ShowWithEpisodesActivity.this,Util.NO_DATA,Util.DEFAULT_NO_DATA);

                        }
                        if ((mainJson.has("censor_rating")) && mainJson.getString("censor_rating").trim() != null && !mainJson.getString("censor_rating").trim().isEmpty() && !mainJson.getString("censor_rating").trim().equals("null") && !mainJson.getString("censor_rating").trim().matches("")) {
                            censorRatingStr = mainJson.getString("censor_rating");
                            censorRatingStr = censorRatingStr.replaceAll("\\[", "");
                            censorRatingStr = censorRatingStr.replaceAll("\\]","");
                            censorRatingStr = censorRatingStr.replaceAll(","," ");
                            censorRatingStr = censorRatingStr.replaceAll("\"", "");


                        }else{
                            censorRatingStr = Util.getTextofLanguage(ShowWithEpisodesActivity.this,Util.NO_DATA,Util.DEFAULT_NO_DATA);

                        }
                        if ((mainJson.has("story")) && mainJson.getString("story").trim() != null && !mainJson.getString("story").trim().isEmpty() && !mainJson.getString("story").trim().equals("null") && !mainJson.getString("story").trim().matches("")) {
                            movieDetailsStr = mainJson.getString("story");
                        }else{
                            movieDetailsStr = "";

                        }
                       /* if ((mainJson.has("trailerUrl")) && mainJson.getString("trailerUrl").trim() != null && !mainJson.getString("trailerUrl").trim().isEmpty() && !mainJson.getString("trailerUrl").trim().equals("null") && !mainJson.getString("trailerUrl").trim().matches("")) {
                            movieTrailerUrlStr = mainJson.getString("trailerUrl");

                        }else{
                            movieTrailerUrlStr = Util.getTextofLanguage(ShowWithEpisodesActivity.this,Util.NO_DATA,Util.DEFAULT_NO_DATA);

                        }*/
                        if ((mainJson.has("movie_stream_uniq_id")) && mainJson.getString("movie_stream_uniq_id").trim() != null && !mainJson.getString("movie_stream_uniq_id").trim().isEmpty() && !mainJson.getString("movie_stream_uniq_id").trim().equals("null") && !mainJson.getString("movie_stream_uniq_id").trim().matches("")) {
                            movieStreamUniqueId = mainJson.getString("movie_stream_uniq_id");
                        }else{
                            movieStreamUniqueId = Util.getTextofLanguage(ShowWithEpisodesActivity.this,Util.NO_DATA,Util.DEFAULT_NO_DATA);

                        }

                        if ((mainJson.has("muvi_uniq_id")) && mainJson.getString("muvi_uniq_id").trim() != null && !mainJson.getString("muvi_uniq_id").trim().isEmpty() && !mainJson.getString("muvi_uniq_id").trim().equals("null") && !mainJson.getString("muvi_uniq_id").trim().matches("")) {
                            movieUniqueId = mainJson.getString("muvi_uniq_id");
                        }else{
                            movieUniqueId = Util.getTextofLanguage(ShowWithEpisodesActivity.this,Util.NO_DATA,Util.DEFAULT_NO_DATA);

                        }

                       /* if ((mainJson.has("movieUrl")) && mainJson.getString("movieUrl").trim() != null && !mainJson.getString("movieUrl").trim().isEmpty() && !mainJson.getString("movieUrl").trim().equals("null") && !mainJson.getString("movieUrl").trim().matches("")) {
                            movieVideoUrlStr = mainJson.getString("movieUrl");

                        }
                        else{
                            movieVideoUrlStr = getResources().getString(R.string.no_data_str);

                        }*/

                        if ((mainJson.has("is_episode")) && mainJson.getString("is_episode").trim() != null && !mainJson.getString("is_episode").trim().isEmpty() && !mainJson.getString("is_episode").trim().equals("null") && !mainJson.getString("is_episode").trim().matches("")) {
                            isEpisode = mainJson.getString("is_episode");

                        }
                        else{
                            isEpisode = Util.getTextofLanguage(ShowWithEpisodesActivity.this, Util.NO_DATA, Util.DEFAULT_NO_DATA);

                        }
                        if ((mainJson.has("is_favorite")) && mainJson.getString("is_favorite").trim() != null && !mainJson.getString("is_favorite").trim().isEmpty() && !mainJson.getString("is_favorite").trim().equals("null") && !mainJson.getString("is_episode").trim().matches("")) {
                            isFavorite = Integer.parseInt(mainJson.getString("is_favorite"));
                            Log.v("nihar",""+isFavorite);

                        }

                        if ((mainJson.has("banner")) && mainJson.getString("banner").trim() != null && !mainJson.getString("banner").trim().isEmpty() && !mainJson.getString("banner").trim().equals("null") && !mainJson.getString("banner").trim().matches("")){
                            bannerImageId = mainJson.getString("banner");
                            bannerImageId = bannerImageId.replace("episode", "original");
                        }else{
                            bannerImageId = Util.getTextofLanguage(ShowWithEpisodesActivity.this,Util.NO_DATA,Util.DEFAULT_NO_DATA);

                        }

                        if ((mainJson.has("poster")) && mainJson.getString("poster").trim() != null && !mainJson.getString("poster").trim().isEmpty() && !mainJson.getString("poster").trim().equals("null") && !mainJson.getString("poster").trim().matches("")){
                            posterImageId = mainJson.getString("poster");
                            posterImageId = posterImageId.replace("episode", "original");

                        }else{
                            posterImageId = Util.getTextofLanguage(ShowWithEpisodesActivity.this,Util.NO_DATA,Util.DEFAULT_NO_DATA);

                        }


                        if ((mainJson.has("isFreeContent")) && mainJson.getString("isFreeContent").trim() != null && !mainJson.getString("isFreeContent").trim().isEmpty() && !mainJson.getString("isFreeContent").trim().equals("null") && !mainJson.getString("isFreeContent").trim().matches("")){
                            isFreeContent = Integer.parseInt(mainJson.getString("isFreeContent"));
                        }else{
                            isFreeContent = 0;

                        }
                        if ((mainJson.has("release_date")) && mainJson.getString("release_date").trim() != null && !mainJson.getString("release_date").trim().isEmpty() && !mainJson.getString("release_date").trim().equals("null") && !mainJson.getString("release_date").trim().matches("")){
                            movieReleaseDateStr = mainJson.getString("release_date");
                        }else{
                            movieReleaseDateStr = "";

                        }
                        if ((mainJson.has("is_ppv")) && mainJson.getString("is_ppv").trim() != null && !mainJson.getString("is_ppv").trim().isEmpty() && !mainJson.getString("is_ppv").trim().equals("null") && !mainJson.getString("is_ppv").trim().matches("")){
                            isPPV = Integer.parseInt(mainJson.getString("is_ppv"));
                        }else{
                            isPPV = 0;

                        }
                        if ((mainJson.has("is_converted")) && mainJson.getString("is_converted").trim() != null && !mainJson.getString("is_converted").trim().isEmpty() && !mainJson.getString("is_converted").trim().equals("null") && !mainJson.getString("is_converted").trim().matches("")){
                            isConverted = Integer.parseInt(mainJson.getString("is_converted"));
                        }else{
                            isConverted = 0;

                        }
                        if ((mainJson.has("content_types_id")) && mainJson.getString("content_types_id").trim() != null && !mainJson.getString("content_types_id").trim().isEmpty() && !mainJson.getString("content_types_id").trim().equals("null") && !mainJson.getString("content_types_id").trim().matches("")){
                            contentTypesId = Integer.parseInt(mainJson.getString("content_types_id"));
                        }else{
                            contentTypesId = 0;

                        }
                        if ((mainJson.has("is_advance")) && mainJson.getString("is_advance").trim() != null && !mainJson.getString("is_advance").trim().isEmpty() && !mainJson.getString("is_advance").trim().equals("null") && !mainJson.getString("is_advance").trim().matches("")){
                            isAPV = Integer.parseInt(mainJson.getString("is_advance"));
                        }else{
                            isAPV = 0;

                        }


                        if (mainJson.has("cast_detail") && mainJson.has("cast_detail")!= false && mainJson.getString("cast_detail").trim() != null && !mainJson.getString("cast_detail").trim().isEmpty() && !mainJson.getString("cast_detail").trim().equals("null") && !mainJson.getString("cast_detail").trim().equals("false")){
                            castStr = true;

                        }

                    } else{

                        responseStr = "0";

                    }
                }

                else{

                    responseStr = "0";

                }
            } catch (final JSONException e1) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (loadMovieDetailspDialog != null && loadMovieDetailspDialog.isShowing()) {
                            loadMovieDetailspDialog.hide();
                            loadMovieDetailspDialog = null;
                        }
                        noInternetConnectionLayout.setVisibility(View.GONE);
                        noDataLayout.setVisibility(View.VISIBLE);

                        story_layout.setVisibility(View.GONE);
                        bannerImageRelativeLayout.setVisibility(View.GONE);
                        iconImageRelativeLayout.setVisibility(View.GONE);

                        //Commented By Me

                      /*  movieDescription.setVisibility(View.GONE);
                        //movieThumbnailImageView.setVisibility(View.GONE);
                        moviePoster.setVisibility(View.GONE);
                        movieName.setVisibility(View.GONE);
                        movieType.setVisibility(View.GONE);
                        movieDescriptionTitle.setVisibility(View.GONE);
                        castTitleTextView.setVisibility(View.GONE);
                        crewTitleTextView.setVisibility(View.GONE);
                        castTextView.setVisibility(View.GONE);
                        crewTextView.setVisibility(View.GONE);
                        movieCensorRatingTitleTextView.setVisibility(View.GONE);
                        movieCensorRatingTextView.setVisibility(View.GONE);
                        movieGenreTitleTextView.setVisibility(View.GONE);
                        movieReleaseDateTitleTextView.setVisibility(View.GONE);
                        movieReleaseDate.setVisibility(View.GONE);
                        watchTrailerButton.setVisibility(View.GONE);*/

                    }

                });
                responseStr = "0";
                e1.printStackTrace();
            }

            catch (Exception e)
            {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (loadMovieDetailspDialog != null && loadMovieDetailspDialog.isShowing()) {
                            loadMovieDetailspDialog.hide();
                            loadMovieDetailspDialog = null;
                        }
                        noInternetConnectionLayout.setVisibility(View.GONE);
                        noDataLayout.setVisibility(View.VISIBLE);

                        story_layout.setVisibility(View.GONE);
                        bannerImageRelativeLayout.setVisibility(View.GONE);
                        iconImageRelativeLayout.setVisibility(View.GONE);

                        //Commented By Me

                       /* movieDescription.setVisibility(View.GONE);
                        //movieThumbnailImageView.setVisibility(View.GONE);
                        moviePoster.setVisibility(View.GONE);
                        movieName.setVisibility(View.GONE);
                        movieType.setVisibility(View.GONE);
                        movieDescriptionTitle.setVisibility(View.GONE);
                        castTitleTextView.setVisibility(View.GONE);
                        crewTitleTextView.setVisibility(View.GONE);
                        castTextView.setVisibility(View.GONE);
                        crewTextView.setVisibility(View.GONE);
                        movieCensorRatingTitleTextView.setVisibility(View.GONE);
                        movieCensorRatingTextView.setVisibility(View.GONE);
                        movieGenreTitleTextView.setVisibility(View.GONE);
                        movieReleaseDateTitleTextView.setVisibility(View.GONE);
                        movieReleaseDate.setVisibility(View.GONE);
                        watchTrailerButton.setVisibility(View.GONE);*/

                    }

                });
                responseStr = "0";
                e.printStackTrace();

            }
            return null;

        }

        protected void onPostExecute(Void result) {

         /*   try{
                if(pDialog.isShowing())
                    pDialog.dismiss();
            }
            catch(IllegalArgumentException ex)
            {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        noInternetConnectionLayout.setVisibility(View.GONE);
                        noDataLayout.setVisibility(View.VISIBLE);

                        story_layout.setVisibility(View.GONE);
                        bannerImageRelativeLayout.setVisibility(View.GONE);
                        iconImageRelativeLayout.setVisibility(View.GONE);

                        //Commented By Me

                       *//* movieDescription.setVisibility(View.GONE);
                        //movieThumbnailImageView.setVisibility(View.GONE);
                        moviePoster.setVisibility(View.GONE);
                        movieName.setVisibility(View.GONE);
                        movieType.setVisibility(View.GONE);
                        movieDescriptionTitle.setVisibility(View.GONE);
                        castTitleTextView.setVisibility(View.GONE);
                        crewTitleTextView.setVisibility(View.GONE);
                        castTextView.setVisibility(View.GONE);
                        crewTextView.setVisibility(View.GONE);
                        movieCensorRatingTitleTextView.setVisibility(View.GONE);
                        movieCensorRatingTextView.setVisibility(View.GONE);
                        movieGenreTitleTextView.setVisibility(View.GONE);
                        movieReleaseDateTitleTextView.setVisibility(View.GONE);
                        movieReleaseDate.setVisibility(View.GONE);
                        watchTrailerButton.setVisibility(View.GONE);*//*


                    }

                });
                responseStr = "0";
            }*/
            if(responseStr == null) {

                responseStr = "0";
            }
            if((responseStr.trim().equals("0"))){
                if (loadMovieDetailspDialog != null && loadMovieDetailspDialog.isShowing()) {
                    loadMovieDetailspDialog.hide();
                    loadMovieDetailspDialog = null;
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if ( status == 414){
                            noDataTextView.setText(Util.getTextofLanguage(ShowWithEpisodesActivity.this,Util.CONTENT_NOT_AVAILABLE_IN_YOUR_COUNTRY,Util.DEFAULT_CONTENT_NOT_AVAILABLE_IN_YOUR_COUNTRY));
                            noInternetConnectionLayout.setVisibility(View.GONE);
                            noDataLayout.setVisibility(View.VISIBLE);

                            story_layout.setVisibility(View.GONE);
                            bannerImageRelativeLayout.setVisibility(View.GONE);
                            iconImageRelativeLayout.setVisibility(View.GONE);
                            return;
                        }
                        //Commented By Me

                       /* movieDescription.setVisibility(View.GONE);
                        //movieThumbnailImageView.setVisibility(View.GONE);
                        moviePoster.setVisibility(View.GONE);
                        movieName.setVisibility(View.GONE);
                        movieType.setVisibility(View.GONE);
                        movieDescriptionTitle.setVisibility(View.GONE);
                        castTitleTextView.setVisibility(View.GONE);
                        crewTitleTextView.setVisibility(View.GONE);
                        castTextView.setVisibility(View.GONE);
                        crewTextView.setVisibility(View.GONE);
                        movieCensorRatingTitleTextView.setVisibility(View.GONE);
                        movieCensorRatingTextView.setVisibility(View.GONE);
                        movieGenreTitleTextView.setVisibility(View.GONE);
                        movieReleaseDateTitleTextView.setVisibility(View.GONE);
                        movieReleaseDate.setVisibility(View.GONE);
                        watchTrailerButton.setVisibility(View.GONE);*/


                    }

                });

//                Util.showToast(ShowWithEpisodesActivity.this,Util.getTextofLanguage(ShowWithEpisodesActivity.this,Util.NO_DETAILS_AVAILABLE,Util.DEFAULT_NO_DETAILS_AVAILABLE));

               // Toast.makeText(ShowWithEpisodesActivity.this, Util.getTextofLanguage(ShowWithEpisodesActivity.this,Util.NO_DETAILS_AVAILABLE,Util.DEFAULT_NO_DETAILS_AVAILABLE), Toast.LENGTH_LONG).show();


            }else{
                /***favorite *****/

                if ((Util.getTextofLanguage(ShowWithEpisodesActivity.this, Util.HAS_FAVORITE, Util.DEFAULT_HAS_FAVORITE)
                        .trim()).equals("1")) {
                    favorite_view_episode.setVisibility(View.VISIBLE);
                }
                /***favorite *****/

                noInternetConnectionLayout.setVisibility(View.GONE);
                noDataLayout.setVisibility(View.GONE);
                if (isAPV == 1){
                    playButton.setVisibility(View.GONE);
                }else if (isAPV == 0 && isPPV == 0 && isConverted == 0){
                    if (contentTypesId == 4){
                        playButton.setVisibility(View.GONE);
                    }else{
                        playButton.setVisibility(View.GONE);
                    }

                }else if (isAPV == 0 && isPPV == 0 && isConverted == 1){
                    playButton.setVisibility(View.GONE);

                }
                videoTitle.setVisibility(View.VISIBLE);
                Typeface videoGenreTextViewTypeface = Typeface.createFromAsset(getAssets(),getResources().getString(R.string.regular_fonts));
                videoTitle.setTypeface(videoGenreTextViewTypeface);
                videoTitle.setText(movieNameStr);

                if(movieTrailerUrlStr.matches("") || movieTrailerUrlStr.matches(Util.getTextofLanguage(ShowWithEpisodesActivity.this,Util.NO_DATA,Util.DEFAULT_NO_DATA))){
                    watchTrailerButton.setVisibility(View.INVISIBLE);
                }else{

                    watchTrailerButton.setVisibility(View.VISIBLE);
                }

                if(movieTypeStr!=null && movieTypeStr.matches("") || movieTypeStr.matches(Util.getTextofLanguage(ShowWithEpisodesActivity.this,Util.NO_DATA,Util.DEFAULT_NO_DATA))){
                    videoGenreTextView.setVisibility(View.GONE);

                }else{
                    videoGenreTextView.setVisibility(View.VISIBLE);
                    Typeface watchTrailerButtonTypeface = Typeface.createFromAsset(getAssets(),getResources().getString(R.string.light_fonts));
                    videoGenreTextView.setTypeface(watchTrailerButtonTypeface);
                    videoGenreTextView.setText(movieTypeStr);

                }
                if(videoduration.matches("") || videoduration.matches(Util.getTextofLanguage(ShowWithEpisodesActivity.this,Util.NO_DATA,Util.DEFAULT_NO_DATA))){
                    videoDurationTextView.setVisibility(View.GONE);

                }else{
                    videoDurationTextView.setVisibility(View.VISIBLE);
                    Typeface watchTrailerButtonTypeface = Typeface.createFromAsset(getAssets(),getResources().getString(R.string.light_fonts));
                    videoDurationTextView.setTypeface(watchTrailerButtonTypeface);
                    videoDurationTextView.setText(videoduration);
                    iconImageRelativeLayout.setVisibility(View.VISIBLE);
                }


                if(movieReleaseDateStr.matches("") || movieReleaseDateStr.matches(Util.getTextofLanguage(ShowWithEpisodesActivity.this,Util.NO_DATA,Util.DEFAULT_NO_DATA))){
                    videoReleaseDateTextView.setVisibility(View.GONE);
                }else{
                    videoReleaseDateTextView.setVisibility(View.VISIBLE);
                    Typeface watchTrailerButtonTypeface = Typeface.createFromAsset(getAssets(),getResources().getString(R.string.light_fonts));
                    videoReleaseDateTextView.setTypeface(watchTrailerButtonTypeface);
                    movieReleaseDateStr = Util.formateDateFromstring("yyyy-mm-dd", "mm-dd-yyyy", movieReleaseDateStr);
                    videoReleaseDateTextView.setText(movieReleaseDateStr);

                }

                if(movieDetailsStr.matches("") || movieDetailsStr.matches(Util.getTextofLanguage(ShowWithEpisodesActivity.this,Util.NO_DATA,Util.DEFAULT_NO_DATA))){
                    videoStoryTextView.setVisibility(View.GONE);

                }else{
                  //  videoStoryTextView.setMaxLines(3);
                    videoStoryTextView.setVisibility(View.VISIBLE);
                    Typeface watchTrailerButtonTypeface = Typeface.createFromAsset(getAssets(),getResources().getString(R.string.light_fonts));
                    videoStoryTextView.setTypeface(watchTrailerButtonTypeface);
                   // makeTextViewResizable(videoStoryTextView,3,movieDetailsStr,true);
                    videoStoryTextView.setText(movieDetailsStr.trim());
                    ResizableCustomView.doResizeTextView(ShowWithEpisodesActivity.this,videoStoryTextView, MAX_LINES, Util.getTextofLanguage(ShowWithEpisodesActivity.this, Util.VIEW_MORE, Util.DEFAULT_VIEW_MORE), true);
                    //customTextView(movieDetailsStr);
                    //makeTextViewResizable(videoStoryTextView, 3, "See More", true);
                   // customTextView(movieDetailsStr);
                   // videoStoryTextView.setText(movieDetailsStr);
                  /*  String tempStr = movieDetailsStr+"View More";
                    String word = "View More";

                    SpannableString ss = new SpannableString(tempStr);
                    ClickableSpan span1 = new ClickableSpan() {
                        @Override
                        public void onClick(View textView) {
                            // do some thing
                        }
                    };


                    ss.setSpan(span1, 0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);*/

                    //videoStoryTextView.setText(movieDetailsStr);
                    //videoStoryTextView.setMovementMethod(LinkMovementMethod.getInstance());


                }

             /*   *//****rating ******//*
                if (isReviewThere == 0) {
                    viewRatingTextView.setVisibility(View.GONE);

                } else {
                    ratingBar.setVisibility(View.VISIBLE);
                    ratingBar.setRating(Float.parseFloat(rating));
                }
                if (isRatingThere == 0) {
                    ratingBar.setVisibility(View.GONE);
                } else {
                    viewRatingTextView.setVisibility(View.VISIBLE);

                    if (pref != null) {
                        String loggedInStr = pref.getString("PREFS_LOGGEDIN_KEY", null);
                        if (loggedInStr == null) {
                            viewRatingTextView.setText(Util.getTextofLanguage(ShowWithEpisodesActivity.this, Util.ADD_A_REVIEW, Util.DEFAULT_ADD_A_REVIEW));

                        } else {
                            viewRatingTextView.setText("reviews (" + reviews + ")");
                            viewRatingTextView.setText(Util.getTextofLanguage(ShowWithEpisodesActivity.this, Util.REVIEWS, Util.DEFAULT_REVIEWS) + " (" + reviews + ") ");

                        }
                    }
                }
*/

                if (castStr == true){
                    Typeface watchTrailerButtonTypeface = Typeface.createFromAsset(getAssets(),getResources().getString(R.string.light_fonts));
                    videoCastCrewTitleTextView.setTypeface(watchTrailerButtonTypeface);
                    videoCastCrewTitleTextView.setText(Util.getTextofLanguage(ShowWithEpisodesActivity.this,Util.CAST_CREW_BUTTON_TITLE,Util.DEFAULT_CAST_CREW_BUTTON_TITLE));

                    videoCastCrewTitleTextView.setVisibility(View.VISIBLE);
                }
               /* if(censorRatingStr.matches("") || censorRatingStr.matches(getResources().getString(R.string.no_data_str))){
                    videoCensorRatingTextView.setVisibility(View.GONE);
                }else{
                    videoCensorRatingTextView.setVisibility(View.VISIBLE);
                    videoCensorRatingTextView.setText(censorRatingStr);
                }
*/
                if(censorRatingStr.matches("") || censorRatingStr.matches(Util.getTextofLanguage(ShowWithEpisodesActivity.this,Util.NO_DATA,Util.DEFAULT_NO_DATA))){
                    videoCensorRatingTextView.setVisibility(View.GONE);
                    videoCensorRatingTextView1.setVisibility(View.GONE);

                }else{

                    if(censorRatingStr.contains("-"))
                    {
                        String Data[] = censorRatingStr.split("-");
                        videoCensorRatingTextView.setVisibility(View.VISIBLE);
                        videoCensorRatingTextView1.setVisibility(View.VISIBLE);
                        Typeface watchTrailerButtonTypeface = Typeface.createFromAsset(getAssets(),getResources().getString(R.string.light_fonts));
                        videoCensorRatingTextView.setTypeface(watchTrailerButtonTypeface);
                        videoCensorRatingTextView1.setTypeface(watchTrailerButtonTypeface);
                        videoCensorRatingTextView.setText(Data[0]);
                        videoCensorRatingTextView1.setText(Data[1]);

                    }
                    else
                    {
                        videoCensorRatingTextView.setVisibility(View.VISIBLE);
                        videoCensorRatingTextView1.setVisibility(View.GONE);
                        Typeface watchTrailerButtonTypeface = Typeface.createFromAsset(getAssets(),getResources().getString(R.string.light_fonts));
                        videoCensorRatingTextView.setTypeface(watchTrailerButtonTypeface);
                        videoCensorRatingTextView.setText(censorRatingStr);
                    }


                }
                season_spinner.setVisibility(View.VISIBLE);
                ArrayAdapter adapter=new ArrayAdapter(ShowWithEpisodesActivity.this,R.layout.dropdownlist,season);
                season_spinner.setAdapter(adapter);

//                season_spinner.setBackgroundResource(R.drawable.spinner_theme);

                if (bannerImageId.trim().matches(Util.getTextofLanguage(ShowWithEpisodesActivity.this,Util.NO_DATA,Util.DEFAULT_NO_DATA))) {

                    if(posterImageId.trim().matches(Util.getTextofLanguage(ShowWithEpisodesActivity.this,Util.NO_DATA,Util.DEFAULT_NO_DATA))){

                        moviePoster.setImageResource(R.drawable.logo);
                    }else{


                        ImageLoader imageLoader = ImageLoader.getInstance();
                        imageLoader.init(ImageLoaderConfiguration.createDefault(ShowWithEpisodesActivity.this));

                        DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
                                .cacheOnDisc(true).resetViewBeforeLoading(true)
                                .showImageForEmptyUri(R.drawable.logo)
                                .showImageOnFail(R.drawable.logo)
                                .showImageOnLoading(R.drawable.logo).build();
                        imageLoader.displayImage(posterImageId, moviePoster, options);

                    }

                }else {


                    ImageLoader imageLoader = ImageLoader.getInstance();
                    imageLoader.init(ImageLoaderConfiguration.createDefault(ShowWithEpisodesActivity.this));

                    DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
                            .cacheOnDisc(true).resetViewBeforeLoading(true)
                            .showImageForEmptyUri(R.drawable.logo)
                            .showImageOnFail(R.drawable.logo)
                            .showImageOnLoading(R.drawable.logo).build();
                    imageLoader.displayImage(bannerImageId.trim(), moviePoster, options);


                }
                if(contentTypesId==3){
                    if (loadMovieDetailspDialog != null && loadMovieDetailspDialog.isShowing()) {
                        loadMovieDetailspDialog.hide();
                        loadMovieDetailspDialog = null;
                    }
//                    AsynEpisodeDetails asynEpisodeDetails=new AsynEpisodeDetails();
//                    asynEpisodeDetails.executeOnExecutor(threadPoolExecutor);
                }else{
                    if (loadMovieDetailspDialog != null && loadMovieDetailspDialog.isShowing()) {
                        loadMovieDetailspDialog.hide();
                        loadMovieDetailspDialog = null;
                    }
                }
            }
         /*   *//***favorite *****//*

            if(loggedInStr != null && isFavorite== 0 && Util.favorite_clicked == true){

                Util.favorite_clicked = false;
//            call feb Api

                asynFavoriteAdd = new AsynFavoriteAdd();
                asynFavoriteAdd.executeOnExecutor(threadPoolExecutor);
            }
            else if (loggedInStr != null && isFavorite==1){
                Log.v("ANU","isFavorite======"+isFavorite);



                favorite_view_episode.setImageResource(R.drawable.favorite_red);

            }*/

            GetReviewDetails();

            /***favorite *****/


        }

        @Override
        protected void onPreExecute() {

            loadMovieDetailspDialog = new ProgressBarHandler(ShowWithEpisodesActivity.this);
            loadMovieDetailspDialog.show();

        }


    }

    private class AsynEpisodeDetails extends AsyncTask<Void, Void, Void> {
        // ProgressDialog pDialog;
        String responseStr;
        String movieThirdPartyUrl = "";
        int episodeContenTTypesId = 0;

        int status;
        @Override
        protected Void doInBackground(Void... params) {
            String urlRouteList=Util.rootUrl().trim()+Util.episodesUrl.trim();
            try {
                HttpClient httpclient=new DefaultHttpClient();
                HttpPost httppost = new HttpPost(urlRouteList);
                httppost.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");
                httppost.addHeader("authToken",Util.authTokenStr.trim());
                httppost.addHeader("permalink", permalinkStr.trim());
                httppost.addHeader("limit", "4");
                httppost.addHeader("offset", "1");
                SharedPreferences countryPref = getSharedPreferences(Util.COUNTRY_PREF, 0); // 0 - for private mode
                if (countryPref != null) {
                    String countryCodeStr = countryPref.getString("countryCode", null);
                    httppost.addHeader("country", countryCodeStr);
                }else{
                    httppost.addHeader("country", "IN");

                }
                httppost.addHeader("lang_code",Util.getTextofLanguage(ShowWithEpisodesActivity.this,Util.SELECTED_LANGUAGE_CODE,Util.DEFAULT_SELECTED_LANGUAGE_CODE));

                String data = season.get(spinnerPosition);
                String[] data1 = data.split(" ");

                if(data1.length>0){

                    httppost.addHeader("series_number",data1[1].trim());
                    Season_Value = data1[1].trim();

                }else {

                }
                //httppost.addHeader("deviceType", "roku");
                // Execute HTTP Post Request
                try {
                    HttpResponse response = httpclient.execute(httppost);
                    responseStr = EntityUtils.toString(response.getEntity());


                } catch (org.apache.http.conn.ConnectTimeoutException e){


                }catch (IOException e) {

                    e.printStackTrace();
                }

                JSONObject myJson =null;
                if(responseStr!=null){
                    myJson = new JSONObject(responseStr);
                    status = Integer.parseInt(myJson.optString("code"));
                    String items = myJson.optString("item_count");
                    movieUniqueId = myJson.optString("muvi_uniq_id");
                    _permalink = myJson.optString("permalink");

                }


                if (status > 0) {

                    if (status == 200) {

                        itemData.clear();

                        if ((myJson.has("is_ppv")) && myJson.getString("is_ppv").trim() != null && !myJson.getString("is_ppv").trim().isEmpty() && !myJson.getString("is_ppv").trim().equals("null") && !myJson.getString("is_ppv").trim().matches("")){

                            isPPV = Integer.parseInt(myJson.getString("is_ppv"));
                        }else{
                            isPPV = 0;


                        }
                        if ((myJson.has("is_advance")) && myJson.getString("is_advance").trim() != null && !myJson.getString("is_advance").trim().isEmpty() && !myJson.getString("is_advance").trim().equals("null") && !myJson.getString("is_advance").trim().matches("")){

                            isAPV = Integer.parseInt(myJson.getString("is_advance"));
                        }else{
                            isAPV = 0;


                        }

                        if (isPPV == 1){
                            JSONObject ppvJson = null;
                            if ((myJson.has("ppv_pricing"))) {



                                ppvJson = myJson.getJSONObject("ppv_pricing");
                                if ((ppvJson.has("price_for_unsubscribed")) && ppvJson.getString("price_for_unsubscribed").trim() != null && !ppvJson.getString("price_for_unsubscribed").trim().isEmpty() && !ppvJson.getString("price_for_unsubscribed").trim().equals("null") && !ppvJson.getString("price_for_unsubscribed").trim().matches("")){
                                    // priceForUnsubscribedStr = ppvJson.getString("price_for_unsubscribed");
                                    ppvmodel.setPPVPriceForUnsubscribedStr(ppvJson.getString("price_for_unsubscribed"));
                                }else{
                                    // priceForUnsubscribedStr = "0.0";
                                    ppvmodel.setPPVPriceForUnsubscribedStr("0.0");


                                }
                                if ((ppvJson.has("price_for_subscribed")) && ppvJson.getString("price_for_subscribed").trim() != null && !ppvJson.getString("price_for_subscribed").trim().isEmpty() && !ppvJson.getString("price_for_subscribed").trim().equals("null") && !ppvJson.getString("price_for_subscribed").trim().matches("")){
                                    //priceFosubscribedStr = ppvJson.getString("price_for_subscribed");
                                    ppvmodel.setPPVPriceForUnsubscribedStr(ppvJson.getString("price_for_subscribed"));

                                }else{
                                    // priceFosubscribedStr = "0.0";
                                    ppvmodel.setPPVPriceForUnsubscribedStr("0.0");

                                }
                                if ((ppvJson.has("id")) && ppvJson.getString("id").trim() != null && !ppvJson.getString("id").trim().isEmpty() && !ppvJson.getString("id").trim().equals("null") && !ppvJson.getString("id").trim().matches("")){
                                    //  planIdStr = ppvJson.getString("id");
                                    ppvmodel.setPpvPlanId(ppvJson.getString("id"));

                                }else{
                                    //  planIdStr = "0";
                                    ppvmodel.setPpvPlanId("0");

                                }
                                //SubhaShree
                                if ((ppvJson.has("show_unsubscribed")) && ppvJson.getString("show_unsubscribed").trim() != null && !ppvJson.getString("show_unsubscribed").trim().isEmpty() && !ppvJson.getString("show_unsubscribed").trim().equals("null") && !ppvJson.getString("show_unsubscribed").trim().matches("")){
                                    //  planIdStr = ppvJson.getString("id");
                                    ppvmodel.setPpvShowUnsubscribedStr(ppvJson.getString("show_unsubscribed"));

                                }else{
                                    //  planIdStr = "0";
                                    ppvmodel.setPpvShowUnsubscribedStr("0.0");


                                }
                                if ((ppvJson.has("show_subscribed")) && ppvJson.getString("show_subscribed").trim() != null && !ppvJson.getString("show_subscribed").trim().isEmpty() && !ppvJson.getString("show_subscribed").trim().equals("null") && !ppvJson.getString("show_subscribed").trim().matches("")){
                                    //  planIdStr = ppvJson.getString("id");
                                    ppvmodel.setPpvShowSubscribedStr(ppvJson.getString("show_subscribed"));

                                }else{
                                    //  planIdStr = "0";
                                    ppvmodel.setPpvShowSubscribedStr("0.0");


                                }

                                if ((ppvJson.has("season_unsubscribed")) && ppvJson.getString("season_unsubscribed").trim() != null && !ppvJson.getString("season_unsubscribed").trim().isEmpty() && !ppvJson.getString("season_unsubscribed").trim().equals("null") && !ppvJson.getString("season_unsubscribed").trim().matches("")){
                                    //  planIdStr = ppvJson.getString("id");
                                    ppvmodel.setPpvSeasonUnsubscribedStr(ppvJson.getString("season_unsubscribed"));

                                }else{
                                    //  planIdStr = "0";
                                    ppvmodel.setPpvSeasonUnsubscribedStr("0.0");


                                }
                                if ((ppvJson.has("season_subscribed")) && ppvJson.getString("season_subscribed").trim() != null && !ppvJson.getString("season_subscribed").trim().isEmpty() && !ppvJson.getString("season_subscribed").trim().equals("null") && !ppvJson.getString("season_subscribed").trim().matches("")){
                                    //  planIdStr = ppvJson.getString("id");
                                    ppvmodel.setPpvSeasonSubscribedStr(ppvJson.getString("season_subscribed"));

                                }else{
                                    //  planIdStr = "0";
                                    ppvmodel.setPpvSeasonSubscribedStr("0.0");


                                }
                                if ((ppvJson.has("episode_unsubscribed")) && ppvJson.getString("episode_unsubscribed").trim() != null && !ppvJson.getString("episode_unsubscribed").trim().isEmpty() && !ppvJson.getString("episode_unsubscribed").trim().equals("null") && !ppvJson.getString("episode_unsubscribed").trim().matches("")){
                                    //  planIdStr = ppvJson.getString("id");
                                    ppvmodel.setPpvEpisodeUnsubscribedStr(ppvJson.getString("episode_unsubscribed"));

                                }else{
                                    //  planIdStr = "0";
                                    ppvmodel.setPpvEpisodeUnsubscribedStr("0.0");


                                }
                                if ((ppvJson.has("episode_subscribed")) && ppvJson.getString("episode_subscribed").trim() != null && !ppvJson.getString("episode_subscribed").trim().isEmpty() && !ppvJson.getString("episode_subscribed").trim().equals("null") && !ppvJson.getString("episode_subscribed").trim().matches("")){
                                    //  planIdStr = ppvJson.getString("id");
                                    ppvmodel.setPpvEpisodeSubscribedStr(ppvJson.getString("episode_subscribed"));

                                }else{
                                    //  planIdStr = "0";
                                    ppvmodel.setPpvEpisodeSubscribedStr("0.0");


                                }
                                if ((ppvJson.has("is_show")) && ppvJson.getString("is_show").trim() != null && !ppvJson.getString("is_show").trim().isEmpty() && !ppvJson.getString("is_show").trim().equals("null") && !ppvJson.getString("is_show").trim().matches("")){
                                    //  planIdStr = ppvJson.getString("id");
                                    ppvmodel.setIsShow(Integer.parseInt(ppvJson.getString("is_show")));

                                }else{
                                    //  planIdStr = "0";
                                    ppvmodel.setIsShow(0);


                                }
                                if ((ppvJson.has("is_season")) && ppvJson.getString("is_season").trim() != null && !ppvJson.getString("is_season").trim().isEmpty() && !ppvJson.getString("is_season").trim().equals("null") && !ppvJson.getString("is_season").trim().matches("")){
                                    //  planIdStr = ppvJson.getString("id");
                                    ppvmodel.setIsSeason(Integer.parseInt(ppvJson.getString("is_season")));

                                }else{
                                    //  planIdStr = "0";
                                    ppvmodel.setIsSeason(0);


                                }
                                if ((ppvJson.has("is_episode")) && ppvJson.getString("is_episode").trim() != null && !ppvJson.getString("is_episode").trim().isEmpty() && !ppvJson.getString("is_episode").trim().equals("null") && !ppvJson.getString("is_episode").trim().matches("")){
                                    //  planIdStr = ppvJson.getString("id");
                                    ppvmodel.setIsEpisode(Integer.parseInt(ppvJson.getString("is_episode")));

                                }else{
                                    //  planIdStr = "0";
                                    ppvmodel.setIsEpisode(0);
                                }

                                Util.ppvModel = ppvmodel;
                            }
                        }
                        if (isAPV == 1){
                            JSONObject advJson = null;
                            if ((myJson.has("adv_pricing"))) {


                                advJson = myJson.getJSONObject("adv_pricing");
                                if ((advJson.has("price_for_unsubscribed")) && advJson.getString("price_for_unsubscribed").trim() != null && !advJson.getString("price_for_unsubscribed").trim().isEmpty() && !advJson.getString("price_for_unsubscribed").trim().equals("null") && !advJson.getString("price_for_unsubscribed").trim().matches("")){
                                    //priceForUnsubscribedStr = advJson.getString("price_for_unsubscribed");
                                    advmodel.setAPVPriceForUnsubscribedStr(advJson.getString("price_for_unsubscribed"));
                                }else{
                                    //priceForUnsubscribedStr = "0.0";
                                    advmodel.setAPVPriceForUnsubscribedStr("0.0");


                                }
                                if ((advJson.has("price_for_subscribed")) && advJson.getString("price_for_subscribed").trim() != null && !advJson.getString("price_for_subscribed").trim().isEmpty() && !advJson.getString("price_for_subscribed").trim().equals("null") && !advJson.getString("price_for_subscribed").trim().matches("")){
                                    //priceFosubscribedStr = advJson.getString("price_for_subscribed");
                                    advmodel.setAPVPriceForsubscribedStr(advJson.getString("price_for_subscribed"));
                                }else{
                                    //priceFosubscribedStr = "0.0";
                                    advmodel.setAPVPriceForsubscribedStr("0.0");

                                }
                                if ((advJson.has("id")) && advJson.getString("id").trim() != null && !advJson.getString("id").trim().isEmpty() && !advJson.getString("id").trim().equals("null") && !advJson.getString("id").trim().matches("")){
                                    //planIdStr = advJson.getString("id");
                                    advmodel.setApvPlanId(advJson.getString("id"));
                                }else{
                                    // planIdStr = "0";
                                    advmodel.setApvPlanId("0");

                                }

                                Util.apvModel = advmodel;
                            }

                        }

                        if(isPPV == 1 || isAPV == 1){
                            Log.v("SUBHA","currency");

                            JSONObject currencyJson = null;
                            if (myJson.has("currency") && myJson.getString("currency") != null && !myJson.getString("currency").equals("null")){
                                currencyJson = myJson.getJSONObject("currency");


                                if (currencyJson.has("id") && currencyJson.getString("id").trim() != null && !currencyJson.getString("id").trim().isEmpty() && !currencyJson.getString("id").trim().equals("null") && !currencyJson.getString("id").trim().matches("")){
                                    // currencyIdStr = currencyJson.getString("id");
                                    currencymodel.setCurrencyId(currencyJson.getString("id"));
                                    Log.v("SUBHA","currency id"+currencymodel.getCurrencyId());

                                }else{
                                    // currencyIdStr = "";
                                    currencymodel.setCurrencyId("");
                                }

                                if (currencyJson.has("country_code") && currencyJson.getString("country_code").trim() != null && !currencyJson.getString("country_code").trim().isEmpty() && !currencyJson.getString("country_code").trim().equals("null") && !currencyJson.getString("country_code").trim().matches("")){
                                    //currencyCountryCodeStr = currencyJson.getString("country_code");
                                    currencymodel.setCurrencyCode(currencyJson.getString("country_code"));
                                }else{
                                    //currencyCountryCodeStr = "";
                                    currencymodel.setCurrencyCode("153");
                                }
                                if (currencyJson.has("symbol") && currencyJson.getString("symbol").trim() != null && !currencyJson.getString("symbol").trim().isEmpty() && !currencyJson.getString("symbol").trim().equals("null") && !currencyJson.getString("symbol").trim().matches("")){
                                    //currencySymbolStr = currencyJson.getString("symbol");
                                    currencymodel.setCurrencySymbol(currencyJson.getString("symbol"));
                                }else{
                                    //currencySymbolStr = "";
                                    currencymodel.setCurrencySymbol("$");
                                }

                                Util.currencyModel = currencymodel;
                            }
                        }


                        JSONArray jsonMainNode = myJson.getJSONArray("episode");
                        int lengthJsonArr = jsonMainNode.length();
                        for(int i=0; i < lengthJsonArr; i++) {
                            JSONObject jsonChildNode;

                            try {
                                jsonChildNode = jsonMainNode.getJSONObject(i);
                                if ((jsonChildNode.has("thirdparty_url")) && jsonChildNode.getString("thirdparty_url").trim() != null && !jsonChildNode.getString("thirdparty_url").trim().isEmpty() && !jsonChildNode.getString("thirdparty_url").trim().equals("null") && !jsonChildNode.getString("thirdparty_url").trim().matches("")) {
                                    movieThirdPartyUrl = jsonChildNode.getString("thirdparty_url");

                                }
                                if ((jsonChildNode.has("content_types_id")) && jsonChildNode.getString("content_types_id").trim() != null && !jsonChildNode.getString("content_types_id").trim().isEmpty() && !jsonChildNode.getString("content_types_id").trim().equals("null") && !jsonChildNode.getString("content_types_id").trim().matches("")) {
                                    episodeContenTTypesId = Integer.parseInt(jsonChildNode.getString("content_types_id"));

                                }

                                String episodeTitleStr = "";
                                if ((jsonChildNode.has("episode_title")) && jsonChildNode.getString("episode_title").trim() != null && !jsonChildNode.getString("episode_title").trim().isEmpty() && !jsonChildNode.getString("episode_title").trim().equals("null") && !jsonChildNode.getString("episode_title").trim().matches("")) {
                                    episodeTitleStr = jsonChildNode.getString("episode_title");

                                }
                                String episodeNoStr = Util.getTextofLanguage(ShowWithEpisodesActivity.this,Util.NO_DATA,Util.DEFAULT_NO_DATA);

                                if ((jsonChildNode.has("episode_number")) && jsonChildNode.getString("episode_number").trim() != null && !jsonChildNode.getString("episode_number").trim().isEmpty() && !jsonChildNode.getString("episode_number").trim().equals("null") && !jsonChildNode.getString("episode_number").trim().matches("")) {
                                    episodeNoStr = jsonChildNode.getString("episode_number");

                                }
                                String episodeThirdParty = Util.getTextofLanguage(ShowWithEpisodesActivity.this,Util.NO_DATA,Util.DEFAULT_NO_DATA);

                                if ((jsonChildNode.has("thirdparty_url")) && jsonChildNode.getString("thirdparty_url").trim() != null && !jsonChildNode.getString("thirdparty_url").trim().isEmpty() && !jsonChildNode.getString("thirdparty_url").trim().equals("null") && !jsonChildNode.getString("thirdparty_url").trim().matches("")) {
                                    episodeThirdParty = jsonChildNode.getString("thirdparty_url");

                                }
                                String videodurationStr = "";

                                if ((jsonChildNode.has("video_duration")) && jsonChildNode.getString("video_duration").trim() != null && !jsonChildNode.getString("video_duration").trim().isEmpty() && !jsonChildNode.getString("video_duration").trim().equals("null") && !jsonChildNode.getString("video_duration").trim().matches("")) {
                                    videodurationStr = jsonChildNode.getString("video_duration");

                                }
                            /*    String episodeVideoUrlStr = getResources().getString(R.string.no_data_str);

                                if ((jsonChildNode.has("embeddedUrl")) && jsonChildNode.getString("embeddedUrl").trim() != null && !jsonChildNode.getString("embeddedUrl").trim().isEmpty() && !jsonChildNode.getString("embeddedUrl").trim().equals("null") && !jsonChildNode.getString("embeddedUrl").trim().matches("")) {
                                    episodeVideoUrlStr = jsonChildNode.getString("embeddedUrl");

                                }
*/

                                String episodeImageStr = "";

                                if ((jsonChildNode.has("poster_url")) && jsonChildNode.getString("poster_url").trim() != null && !jsonChildNode.getString("poster_url").trim().isEmpty() && !jsonChildNode.getString("poster_url").trim().equals("null") && !jsonChildNode.getString("poster_url").trim().matches("")) {
                                    episodeImageStr = jsonChildNode.getString("poster_url");

                                }
                                String episodeDateStr = "";

                                if ((jsonChildNode.has("episode_date")) && jsonChildNode.getString("episode_date").trim() != null && !jsonChildNode.getString("episode_date").trim().isEmpty() && !jsonChildNode.getString("episode_date").trim().equals("null") && !jsonChildNode.getString("episode_date").trim().matches("")) {
                                    episodeDateStr = jsonChildNode.getString("episode_date");
                                    episodeDateStr = Util.formateDateFromstring("yyyy-mm-dd", "mm-dd-yyyy", episodeDateStr);
                                }
                                String episodeStoryStr = "";

                                if ((jsonChildNode.has("episode_story")) && jsonChildNode.getString("episode_story").trim() != null && !jsonChildNode.getString("episode_story").trim().isEmpty() && !jsonChildNode.getString("episode_story").trim().equals("null") && !jsonChildNode.getString("episode_story").trim().matches("")) {
                                    episodeStoryStr = jsonChildNode.getString("episode_story");

                                }
                                String episodeSeriesNoStr = "";

                                if ((jsonChildNode.has("series_number")) && jsonChildNode.getString("series_number").trim() != null && !jsonChildNode.getString("series_number").trim().isEmpty() && !jsonChildNode.getString("series_number").trim().equals("null") && !jsonChildNode.getString("series_number").trim().matches("")) {
                                    episodeSeriesNoStr = jsonChildNode.getString("series_number");

                                }


                                String episodeMovieStreamUniqueIdStr = "";

                                if ((jsonChildNode.has("movie_stream_uniq_id")) && jsonChildNode.getString("movie_stream_uniq_id").trim() != null && !jsonChildNode.getString("movie_stream_uniq_id").trim().isEmpty() && !jsonChildNode.getString("movie_stream_uniq_id").trim().equals("null") && !jsonChildNode.getString("movie_stream_uniq_id").trim().matches("")) {
                                    episodeMovieStreamUniqueIdStr = jsonChildNode.getString("movie_stream_uniq_id");

                                }



                                itemData.add(new EpisodesListModel(episodeNoStr, episodeStoryStr, episodeDateStr, episodeImageStr, episodeTitleStr,episodeVideoUrlStr,episodeSeriesNoStr,movieUniqueId,episodeMovieStreamUniqueIdStr,episodeThirdParty,episodeContenTTypesId,videodurationStr));
                            } catch (Exception e) {

                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                    }
                    else {
                        responseStr = "0";
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {


                            }

                        });
                    }
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();

            }
            return null;

        }

        protected void onPostExecute(Void result) {

            if(responseStr == null) {
                if (loadMovieDetailspDialog != null && loadMovieDetailspDialog.isShowing()) {
                    loadMovieDetailspDialog.hide();
                    loadMovieDetailspDialog = null;
                }

            }
            if((responseStr.trim().equals("0"))){

                     }else{
                if(itemData.size() <= 0){

                    if (loadMovieDetailspDialog != null && loadMovieDetailspDialog.isShowing()) {
                        loadMovieDetailspDialog.hide();
                        loadMovieDetailspDialog = null;
                    }

                    //Toast.makeText(ShowWithEpisodesListActivity.this, getResources().getString(R.string.there_no_data_str), Toast.LENGTH_LONG).show();
                }else{
                    if (loadMovieDetailspDialog != null && loadMovieDetailspDialog.isShowing()) {
                        loadMovieDetailspDialog.hide();
                        loadMovieDetailspDialog = null;
                    }
                    seasontiveLayout.setVisibility(View.VISIBLE);
                    seasontiveLayout.setLayoutManager(mLayoutManager);
                    seasontiveLayout.setItemAnimator(new DefaultItemAnimator());
                    EpisodesListAdapter mAdapter = new EpisodesListAdapter(ShowWithEpisodesActivity.this, R.layout.list_card_multipart,itemData,new EpisodesListAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(EpisodesListModel item) {
                            clickItem(item);

                        }
                    });
                    seasontiveLayout.setAdapter(mAdapter);

                }
            }

            if (loggedInStr != null && isFavorite==1){

                favorite_view_episode.setImageResource(R.drawable.favorite_red);
            }
        }

        @Override
        protected void onPreExecute() {
            loadMovieDetailspDialog = new ProgressBarHandler(ShowWithEpisodesActivity.this);
            loadMovieDetailspDialog.show();



        }



    }
    @Override
    public void onBackPressed()
    {
        if (asynValidateUserDetails!=null){
            asynValidateUserDetails.cancel(true);
        }
        if (asynLoadVideoUrls!=null){
            asynLoadVideoUrls.cancel(true);
        }
        if (asynLoadMovieDetails!=null){
            asynLoadMovieDetails.cancel(true);
        }
        if (asynGetReviewDetails!=null){
            asynGetReviewDetails.cancel(true);
        }
        if(loadMovieDetailspDialog!=null && loadMovieDetailspDialog.isShowing())
        {
            loadMovieDetailspDialog.hide();
            loadMovieDetailspDialog = null;

        }
        finish();
        overridePendingTransition(0, 0);
        super.onBackPressed();
    }

    public void clickItem(EpisodesListModel item){

        itemToPlay = item;

        DataModel dbModel = new DataModel();
        dbModel.setIsFreeContent(isFreeContent);
        dbModel.setIsAPV(isAPV);
        dbModel.setIsPPV(isPPV);
        dbModel.setIsConverted(1);
        dbModel.setMovieUniqueId(movieUniqueId);
        dbModel.setStreamUniqueId(item.getEpisodeStreamUniqueId());
        dbModel.setThirdPartyUrl(item.getEpisodeThirdPartyUrl());
        dbModel.setVideoTitle(movieNameStr);
        dbModel.setVideoStory(item.getEpisodeDescription());
        dbModel.setVideoGenre(videoGenreTextView.getText().toString());
        dbModel.setVideoDuration(item.getEpisodeDuration());
       // dbModel.setVideoReleaseDate(item.getEpisodeTelecastOn());
        dbModel.setVideoReleaseDate("");

        dbModel.setCensorRating(censorRatingStr);
        dbModel.setCastCrew(castStr);
        dbModel.setEpisode_id(item.getEpisodeStreamUniqueId());
        dbModel.setSeason_id(Season_Value);
        dbModel.setPurchase_type("episode");
        dbModel.setPosterImageId(item.getEpisodeThumbnailImageView());
        dbModel.setContentTypesId(contentTypesId);

        dbModel.setEpisode_series_no(item.getEpisodeSeriesNo());
        dbModel.setEpisode_no(item.getEpisodeNumber());
        dbModel.setEpisode_title(item.getEpisodeTitle());

        dbModel.setParentTitle(movieNameStr);
        dbModel.setContentTypesId(3);


        Util.dataModel = dbModel;
        SubTitleName.clear();
        SubTitlePath.clear();
        ResolutionUrl.clear();
        ResolutionFormat.clear();
        SubTitleLanguage.clear();
        Util.offline_url.clear();
        Util.offline_language.clear();

        Log.v("SUBHA","content typesid = "+ contentTypesId);

        if(isLogin == 1) {
            if (pref != null) {
                String loggedInStr = pref.getString("PREFS_LOGGEDIN_KEY", null);

                if (loggedInStr == null) {
                    final Intent register = new Intent(ShowWithEpisodesActivity.this, RegisterActivity.class);

                    runOnUiThread(new Runnable() {
                        public void run() {
                            register.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            Util.check_for_subscription = 1;
                            startActivity(register);


                        }
                    });
                    //showLoginDialog();
                } else {
                    String loggedinDateStr = pref.getString("date", null);
                    if (loggedinDateStr != null) {
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                        Date loggedInDate = null;
                        try {
                            loggedInDate = formatter.parse(loggedinDateStr);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        Date today = new Date();
                        long differenceInDays = (int) Util.calculateDays(loggedInDate, today) + 1;
                        if (differenceInDays >= 7) {
                            SharedPreferences.Editor editor = pref.edit();
                            editor.clear();
                            editor.commit();

                            final Intent register = new Intent(ShowWithEpisodesActivity.this, RegisterActivity.class);

                            runOnUiThread(new Runnable() {
                                public void run() {
                                    register.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                    Util.check_for_subscription = 1;
                                    startActivity(register);


                                }
                            });
                        } else {

                            if (Util.checkNetwork(ShowWithEpisodesActivity.this) == true) {

                             /*   if (isFreeContent == 1) {
                                    asynLoadVideoUrls = new AsynLoadVideoUrls();
                                    asynLoadVideoUrls.executeOnExecutor(threadPoolExecutor);
                                } else {*/
                                    asynValidateUserDetails = new AsynValidateUserDetails();
                                    asynValidateUserDetails.executeOnExecutor(threadPoolExecutor);
                              //  }
                            } else {
                                Util.showToast(ShowWithEpisodesActivity.this,Util.getTextofLanguage(ShowWithEpisodesActivity.this,Util.NO_INTERNET_CONNECTION,Util.DEFAULT_NO_INTERNET_CONNECTION));

                              //  Toast.makeText(ShowWithEpisodesActivity.this,Util.getTextofLanguage(ShowWithEpisodesActivity.this,Util.NO_INTERNET_CONNECTION,Util.DEFAULT_NO_INTERNET_CONNECTION),Toast.LENGTH_LONG).show();
                            }


                        }
                    }
                }
            } else {

                final Intent register = new Intent(ShowWithEpisodesActivity.this, RegisterActivity.class);

                runOnUiThread(new Runnable() {
                    public void run() {
                        register.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        Util.check_for_subscription = 1;

                        startActivity(register);


                    }
                });
            }
        }else{
            if (Util.checkNetwork(ShowWithEpisodesActivity.this) == true) {
                // subhalaxmi

                AsynLoadVideoUrls asynLoadVideoUrls = new AsynLoadVideoUrls();
                asynLoadVideoUrls.executeOnExecutor(threadPoolExecutor);

            } else {
                Util.showToast(ShowWithEpisodesActivity.this,Util.getTextofLanguage(ShowWithEpisodesActivity.this,Util.NO_INTERNET_CONNECTION,Util.DEFAULT_NO_INTERNET_CONNECTION));

                //Toast.makeText(ShowWithEpisodesActivity.this,Util.getTextofLanguage(ShowWithEpisodesActivity.this,Util.NO_INTERNET_CONNECTION,Util.DEFAULT_NO_INTERNET_CONNECTION),Toast.LENGTH_LONG).show();
            }
        }


    }

    private class AsynLoadVideoUrls extends AsyncTask<Void, Void, Void> {
        ProgressBarHandler pDialog;
        String responseStr;
        int statusCode;
        // This is added because of change in simultaneous login feature
        String message;
        boolean play_video = true;
        @Override
        protected Void doInBackground(Void... params) {
            try {
                HttpClient httpclient=new DefaultHttpClient();
                HttpPost httppost = new HttpPost(Util.rootUrl().trim()+Util.loadVideoUrl.trim());
                httppost.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");
                httppost.addHeader("authToken", Util.authTokenStr.trim());
                httppost.addHeader("content_uniq_id", Util.dataModel.getMovieUniqueId().trim());
                httppost.addHeader("stream_uniq_id", Util.dataModel.getStreamUniqueId().trim());
                httppost.addHeader("internet_speed",MainActivity.internetSpeed.trim());
                httppost.addHeader("user_id",pref.getString("PREFS_LOGGEDIN_ID_KEY", null));
                httppost.addHeader("lang_code",Util.getTextofLanguage(ShowWithEpisodesActivity.this,Util.SELECTED_LANGUAGE_CODE,Util.DEFAULT_SELECTED_LANGUAGE_CODE));


                // Execute HTTP Post Request
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
                            responseStr = "0";
                            Util.dataModel.setVideoUrl(Util.getTextofLanguage(ShowWithEpisodesActivity.this,Util.NO_DATA,Util.DEFAULT_NO_DATA));
                            Util.showToast(ShowWithEpisodesActivity.this,Util.getTextofLanguage(ShowWithEpisodesActivity.this,Util.SLOW_INTERNET_CONNECTION,Util.DEFAULT_SLOW_INTERNET_CONNECTION));

                          //  Toast.makeText(ShowWithEpisodesActivity.this, Util.getTextofLanguage(ShowWithEpisodesActivity.this,Util.SLOW_INTERNET_CONNECTION,Util.DEFAULT_SLOW_INTERNET_CONNECTION), Toast.LENGTH_LONG).show();

                        }

                    });

                }catch (IOException e) {
                    if (pDialog != null && pDialog.isShowing()) {
                        pDialog.hide();
                        pDialog = null;
                    }
                    responseStr = "0";
                    Util.dataModel.setVideoUrl(Util.getTextofLanguage(ShowWithEpisodesActivity.this,Util.NO_DATA,Util.DEFAULT_NO_DATA));
                    e.printStackTrace();
                }

              /*  JSONObject myJson =null;

                if(responseStr!=null){
                    myJson = new JSONObject(responseStr);
                    statusCode = Integer.parseInt(myJson.optString("code"));
                }
*/

                /**** subtitles************/

                JSONObject myJson =null;
                JSONArray SubtitleJosnArray = null;
                JSONArray ResolutionJosnArray = null;
                if(responseStr!=null){
                    myJson = new JSONObject(responseStr);
                    SubtitleJosnArray = myJson.optJSONArray("subTitle");
                    ResolutionJosnArray = myJson.optJSONArray("videoDetails");
                    statusCode = Integer.parseInt(myJson.optString("code"));
                    // This is added because of change in simultaneous login feature
                    message = myJson.optString("msg");

                    Log.v("BIBHU","video stream msg"+message);
                    // ================================== End ====================================//
                }
                /**** subtitles************/

                if (statusCode >= 0) {
                    if (statusCode == 200) {
                        if (Util.dataModel.getThirdPartyUrl().matches("") || Util.dataModel.getThirdPartyUrl().equalsIgnoreCase(Util.getTextofLanguage(ShowWithEpisodesActivity.this,Util.NO_DATA,Util.DEFAULT_NO_DATA))) {
                            if ((myJson.has("studio_approved_url")) && myJson.getString("studio_approved_url").trim() != null && !myJson.getString("studio_approved_url").trim().isEmpty() && !myJson.getString("studio_approved_url").trim().equals("null") && !myJson.getString("studio_approved_url").trim().matches("")) {
                                Util.dataModel.setVideoUrl(myJson.getString("studio_approved_url"));
                                if ((myJson.has("licenseUrl")) && myJson.getString("licenseUrl").trim() != null && !myJson.getString("licenseUrl").trim().isEmpty() && !myJson.getString("licenseUrl").trim().equals("null") && !myJson.getString("licenseUrl").trim().matches("")) {
                                    Util.dataModel.setLicenseUrl(myJson.getString("licenseUrl"));
                                }
                                if ((myJson.has("videoUrl")) && myJson.getString("videoUrl").trim() != null && !myJson.getString("videoUrl").trim().isEmpty() && !myJson.getString("videoUrl").trim().equals("null") && !myJson.getString("videoUrl").trim().matches("")) {
                                    Util.dataModel.setMpdVideoUrl(myJson.getString("videoUrl"));

                                }else {
                                    Util.dataModel.setMpdVideoUrl(Util.getTextofLanguage(ShowWithEpisodesActivity.this, Util.NO_DATA, Util.DEFAULT_NO_DATA));
                                }
                            }

                           /* if ((myJson.has("videoUrl")) && myJson.getString("videoUrl").trim() != null && !myJson.getString("videoUrl").trim().isEmpty() && !myJson.getString("videoUrl").trim().equals("null") && !myJson.getString("videoUrl").trim().matches("")) {
                                Util.dataModel.setVideoUrl(myJson.getString("videoUrl"));
                            }*/

                            else{
                                if ((myJson.has("videoUrl")) && myJson.getString("videoUrl").trim() != null && !myJson.getString("videoUrl").trim().isEmpty() && !myJson.getString("videoUrl").trim().equals("null") && !myJson.getString("videoUrl").trim().matches("")) {
                                    Util.dataModel.setVideoUrl(myJson.getString("videoUrl"));

                                }else {
                                    Util.dataModel.setVideoUrl(Util.getTextofLanguage(ShowWithEpisodesActivity.this, Util.NO_DATA, Util.DEFAULT_NO_DATA));
                                }
                            }
                        }else{
                            if ((myJson.has("thirdparty_url")) && myJson.getString("thirdparty_url").trim() != null && !myJson.getString("thirdparty_url").trim().isEmpty() && !myJson.getString("thirdparty_url").trim().equals("null") && !myJson.getString("thirdparty_url").trim().matches("")) {
                                Util.dataModel.setVideoUrl(myJson.getString("thirdparty_url"));

                            }
                            else{
                                Util.dataModel.setVideoUrl(Util.getTextofLanguage(ShowWithEpisodesActivity.this,Util.NO_DATA,Util.DEFAULT_NO_DATA));

                            }
                        }
                        if ((myJson.has("videoResolution")) && myJson.getString("videoResolution").trim() != null && !myJson.getString("videoResolution").trim().isEmpty() && !myJson.getString("videoResolution").trim().equals("null") && !myJson.getString("videoResolution").trim().matches("")) {
                            Util.dataModel.setVideoResolution(myJson.getString("videoResolution"));

                        }
                        if ((myJson.has("played_length")) && myJson.getString("played_length").trim() != null && !myJson.getString("played_length").trim().isEmpty() && !myJson.getString("played_length").trim().equals("null") && !myJson.getString("played_length").trim().matches("")) {
                            Util.dataModel.setPlayPos(Util.isDouble(myJson.getString("played_length")));
                        }

                        if((myJson.has("is_offline")) && myJson.getString("is_offline").trim() != null && !myJson.getString("is_offline").trim().isEmpty() && !myJson.getString("is_offline").trim().equals("null") && !myJson.getString("is_offline").trim().matches("")){

                            //offline = myJson.getString("is_offline");
                            Util.dataModel.setIsOffline(Util.isOffline=myJson.getString("is_offline"));

                        }else {


                        }

                        if(SubtitleJosnArray!=null)
                        {
                            if(SubtitleJosnArray.length()>0)
                            {
                                for(int i=0;i<SubtitleJosnArray.length();i++)
                                {

                                    Log.v("BIBHU","Sutitle name = "+SubtitleJosnArray.getJSONObject(i).optString("language").trim());
                                    Log.v("BIBHU","Sutitle path = "+SubtitleJosnArray.getJSONObject(i).optString("url").trim());

                                    SubTitleName.add(SubtitleJosnArray.getJSONObject(i).optString("language").trim());
                                    FakeSubTitlePath.add(SubtitleJosnArray.getJSONObject(i).optString("url").trim());
                                    SubTitleLanguage.add(SubtitleJosnArray.getJSONObject(i).optString("code").trim());
                                    Util.offline_url.add(SubtitleJosnArray.getJSONObject(i).optString("url").trim());
                                    Util.offline_language.add(SubtitleJosnArray.getJSONObject(i).optString("language").trim());

                                }
                            }
                        }
                        // This is added because of change in simultaneous login feature
                        if(Util.getTextofLanguage(ShowWithEpisodesActivity.this,Util.IS_STREAMING_RESTRICTION,Util.DEFAULT_IS_IS_STREAMING_RESTRICTION).equals("1"))
                        {

                            Log.v("BIBHU","streaming_restriction============"+myJson.optString("streaming_restriction").toString().trim());

                            if(myJson.optString("streaming_restriction").toString().trim().equals("0"))
                            {
                                play_video = false;
                            }
                            else
                            {
                                play_video = true;
                            }
                        }
                        else
                        {
                            play_video = true;
                        }

                        // ================================== End ====================================//
                    }

                }
                else {

                    responseStr = "0";
                    Util.dataModel.setVideoUrl(Util.getTextofLanguage(ShowWithEpisodesActivity.this,Util.NO_DATA,Util.DEFAULT_NO_DATA));
                }
            } catch (JSONException e1) {
                if (pDialog != null && pDialog.isShowing()) {
                    pDialog.hide();
                    pDialog = null;
                }
                responseStr = "0";
                Util.dataModel.setVideoUrl(Util.getTextofLanguage(ShowWithEpisodesActivity.this,Util.NO_DATA,Util.DEFAULT_NO_DATA));
                e1.printStackTrace();
            }

            catch (Exception e)
            {
                if (pDialog != null && pDialog.isShowing()) {
                    pDialog.hide();
                    pDialog = null;
                }
                responseStr = "0";
                Util.dataModel.setVideoUrl(Util.getTextofLanguage(ShowWithEpisodesActivity.this,Util.NO_DATA,Util.DEFAULT_NO_DATA));

                e.printStackTrace();

            }
            return null;

        }

        protected void onPostExecute(Void result) {

            // This is added because of change in simultaneous login feature //

            if (!play_video) {

                try {
                    if (pDialog.isShowing())
                        pDialog.hide();
                } catch (IllegalArgumentException ex) {
                }

                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(ShowWithEpisodesActivity.this, R.style.MyAlertDialogStyle);
                dlgAlert.setMessage(message);
                dlgAlert.setTitle(Util.getTextofLanguage(ShowWithEpisodesActivity.this, Util.SORRY, Util.DEFAULT_SORRY));
                dlgAlert.setPositiveButton(Util.getTextofLanguage(ShowWithEpisodesActivity.this, Util.BUTTON_OK, Util.DEFAULT_BUTTON_OK), null);
                dlgAlert.setCancelable(false);
                dlgAlert.setPositiveButton(Util.getTextofLanguage(ShowWithEpisodesActivity.this, Util.BUTTON_OK, Util.DEFAULT_BUTTON_OK),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();

                            }
                        });
                dlgAlert.create().show();

                return;
            }

            //=====================End========================================//
            if (responseStr == null) {
                responseStr = "0";
                Util.dataModel.setVideoUrl(Util.getTextofLanguage(ShowWithEpisodesActivity.this,Util.NO_DATA,Util.DEFAULT_NO_DATA));
                //movieThirdPartyUrl = getResources().getString(R.string.no_data_str);
            }

            if ((responseStr.trim().equalsIgnoreCase("0"))) {
                try {
                    if (pDialog != null && pDialog.isShowing()) {
                        pDialog.hide();
                        pDialog = null;
                    }
                } catch (IllegalArgumentException ex) {
                    Util.dataModel.setVideoUrl(Util.getTextofLanguage(ShowWithEpisodesActivity.this,Util.NO_DATA,Util.DEFAULT_NO_DATA));
                    // movieThirdPartyUrl = getResources().getString(R.string.no_data_str);
                }
                Util.dataModel.setVideoUrl(Util.getTextofLanguage(ShowWithEpisodesActivity.this,Util.NO_DATA,Util.DEFAULT_NO_DATA));
                //movieThirdPartyUrl = getResources().getString(R.string.no_data_str);
                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(ShowWithEpisodesActivity.this, R.style.MyAlertDialogStyle);
                dlgAlert.setMessage(Util.getTextofLanguage(ShowWithEpisodesActivity.this,Util.NO_VIDEO_AVAILABLE,Util.DEFAULT_NO_VIDEO_AVAILABLE));
                dlgAlert.setTitle(Util.getTextofLanguage(ShowWithEpisodesActivity.this,Util.SORRY,Util.DEFAULT_SORRY));
                dlgAlert.setPositiveButton(Util.getTextofLanguage(ShowWithEpisodesActivity.this,Util.BUTTON_OK,Util.DEFAULT_BUTTON_OK), null);
                dlgAlert.setCancelable(false);
                dlgAlert.setPositiveButton(Util.getTextofLanguage(ShowWithEpisodesActivity.this,Util.BUTTON_OK,Util.DEFAULT_BUTTON_OK),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                dlgAlert.create().show();
            } else {

                if (Util.dataModel.getVideoUrl() == null) {
                    try {
                        if (pDialog != null && pDialog.isShowing()) {
                            pDialog.hide();
                            pDialog = null;
                        }
                    } catch (IllegalArgumentException ex) {
                        Util.dataModel.setVideoUrl(Util.getTextofLanguage(ShowWithEpisodesActivity.this,Util.NO_DATA,Util.DEFAULT_NO_DATA));
                    }
                    AlertDialog.Builder dlgAlert = new AlertDialog.Builder(ShowWithEpisodesActivity.this, R.style.MyAlertDialogStyle);
                    dlgAlert.setMessage(Util.getTextofLanguage(ShowWithEpisodesActivity.this,Util.NO_VIDEO_AVAILABLE,Util.DEFAULT_NO_VIDEO_AVAILABLE));
                    dlgAlert.setTitle(Util.getTextofLanguage(ShowWithEpisodesActivity.this,Util.SORRY,Util.DEFAULT_SORRY));
                    dlgAlert.setPositiveButton(Util.getTextofLanguage(ShowWithEpisodesActivity.this,Util.BUTTON_OK,Util.DEFAULT_BUTTON_OK), null);
                    dlgAlert.setCancelable(false);
                    dlgAlert.setPositiveButton(Util.getTextofLanguage(ShowWithEpisodesActivity.this,Util.BUTTON_OK,Util.DEFAULT_BUTTON_OK),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    dlgAlert.create().show();
                } else if (Util.dataModel.getVideoUrl().matches("") || Util.dataModel.getVideoUrl().equalsIgnoreCase(Util.getTextofLanguage(ShowWithEpisodesActivity.this,Util.NO_DATA,Util.DEFAULT_NO_DATA))) {
                    try {
                        if (pDialog != null && pDialog.isShowing()) {
                            pDialog.hide();
                            pDialog = null;
                        }
                    } catch (IllegalArgumentException ex) {
                        Util.dataModel.setVideoUrl(Util.getTextofLanguage(ShowWithEpisodesActivity.this,Util.NO_DATA,Util.DEFAULT_NO_DATA));
                    }
                    AlertDialog.Builder dlgAlert = new AlertDialog.Builder(ShowWithEpisodesActivity.this, R.style.MyAlertDialogStyle);
                    dlgAlert.setMessage(Util.getTextofLanguage(ShowWithEpisodesActivity.this,Util.NO_VIDEO_AVAILABLE,Util.DEFAULT_NO_VIDEO_AVAILABLE));
                    dlgAlert.setTitle(Util.getTextofLanguage(ShowWithEpisodesActivity.this,Util.SORRY,Util.DEFAULT_SORRY));
                    dlgAlert.setPositiveButton(Util.getTextofLanguage(ShowWithEpisodesActivity.this,Util.BUTTON_OK,Util.DEFAULT_BUTTON_OK), null);
                    dlgAlert.setCancelable(false);
                    dlgAlert.setPositiveButton(Util.getTextofLanguage(ShowWithEpisodesActivity.this,Util.BUTTON_OK,Util.DEFAULT_BUTTON_OK),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    dlgAlert.create().show();
                } else {
                    try {
                        if (pDialog != null && pDialog.isShowing()) {
                            pDialog.hide();
                            pDialog = null;
                        }
                    } catch (IllegalArgumentException ex) {
                        Util.dataModel.setVideoUrl(Util.getTextofLanguage(ShowWithEpisodesActivity.this,Util.NO_DATA,Util.DEFAULT_NO_DATA));
                    }
                    if (Util.dataModel.getThirdPartyUrl().matches("") || Util.dataModel.getThirdPartyUrl().equalsIgnoreCase(Util.getTextofLanguage(ShowWithEpisodesActivity.this,Util.NO_DATA,Util.DEFAULT_NO_DATA))) {

                        if (mCastSession != null && mCastSession.isConnected()) {


                            MediaMetadata movieMetadata = new MediaMetadata(MediaMetadata.MEDIA_TYPE_MOVIE);

                            movieMetadata.putString(MediaMetadata.KEY_SUBTITLE, Util.dataModel.getVideoStory());
                            movieMetadata.putString(MediaMetadata.KEY_TITLE, Util.dataModel.getEpisode_title());
                            movieMetadata.addImage(new WebImage(Uri.parse(Util.dataModel.getPosterImageId())));
                            movieMetadata.addImage(new WebImage(Uri.parse(Util.dataModel.getPosterImageId())));

                            String mediaContentType = "videos/mp4";
                            if (Util.dataModel.getVideoUrl().contains(".mpd")) {
                                mediaContentType = "application/dash+xml";
                                JSONObject jsonObj = null;
                                try {
                                    jsonObj = new JSONObject();
                                    jsonObj.put("description", Util.dataModel.getVideoTitle());
                                    jsonObj.put("licenseUrl", Util.dataModel.getLicenseUrl());

                                    //  This Code Is Added For Video Log By Bibhu..

                                    jsonObj.put("authToken", Util.authTokenStr.trim());
                                    jsonObj.put("user_id", pref.getString("PREFS_LOGGEDIN_ID_KEY", null));
                                    jsonObj.put("ip_address", ipAddressStr.trim());
                                    jsonObj.put("movie_id",Util.dataModel.getMovieUniqueId());
                                    jsonObj.put("episode_id", Util.dataModel.getEpisode_id());

                                    Log.v("BIBHU4","played legth=="+Util.dataModel.getPlayPos());

                                    jsonObj.put("played_length",Util.dataModel.getPlayPos());


                                    jsonObj.put("watch_status", "start");
                                    jsonObj.put("device_type", "2");
                                    jsonObj.put("log_id", "0");

                                    if (Util.getTextofLanguage(ShowWithEpisodesActivity.this, Util.IS_STREAMING_RESTRICTION, Util.DEFAULT_IS_IS_STREAMING_RESTRICTION).equals("1")) {
                                        jsonObj.put("restrict_stream_id", "1");
                                        Log.v("BIBHU4","restrict_stream_id============1");
                                    }else{
                                        jsonObj.put("restrict_stream_id", "0");
                                        Log.v("BIBHU4","restrict_stream_id============0");
                                    }

                                    jsonObj.put("domain_name",Util.rootUrl().trim().substring(0, Util.rootUrl().trim().length()-6));
                                    jsonObj.put("is_log", "1");

                                    //=====================End===================//


                                    // This  Code Is Added For Drm BufferLog By Bibhu ...

                                    jsonObj.put("resolution", "BEST");
                                    jsonObj.put("start_time","0");
                                    jsonObj.put("end_time", "0");
                                    jsonObj.put("log_unique_id", "0");
                                    jsonObj.put("location", "0");
                                    jsonObj.put("video_type", "");
                                    jsonObj.put("totalBandwidth", "0");

                                    //====================End=====================//

                                } catch (JSONException e) {
                                }
                                List tracks = new ArrayList();
                                for (int i = 0; i < FakeSubTitlePath.size(); i++) {
                                    MediaTrack englishSubtitle = new MediaTrack.Builder(i,
                                            MediaTrack.TYPE_TEXT)
                                            .setName(SubTitleName.get(0))
                                            .setSubtype(MediaTrack.SUBTYPE_SUBTITLES)
                                            .setContentId(FakeSubTitlePath.get(0))
                                            .setLanguage(SubTitleLanguage.get(0))
                                            .setContentType("text/vtt")
                                            .build();
                                    tracks.add(englishSubtitle);
                                }

                                mediaInfo = new MediaInfo.Builder(Util.dataModel.getMpdVideoUrl().trim())
                                        .setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
                                        .setContentType(mediaContentType)
                                        .setMetadata(movieMetadata)
                                        .setStreamDuration(15 * 1000)
                                        .setCustomData(jsonObj)
                                        .setMediaTracks(tracks)
                                        .build();
                                mSelectedMedia = mediaInfo;


                                togglePlayback();
                            } else {
                                JSONObject jsonObj = null;
                                try {
                                    jsonObj = new JSONObject();
                                    jsonObj.put("description", Util.dataModel.getVideoTitle()

                                    );

                                } catch (JSONException e) {
                                }

                                List tracks = new ArrayList();
                                for (int i = 0; i < FakeSubTitlePath.size(); i++) {
                                    MediaTrack englishSubtitle = new MediaTrack.Builder(i,
                                            MediaTrack.TYPE_TEXT)
                                            .setName(SubTitleName.get(0))
                                            .setSubtype(MediaTrack.SUBTYPE_SUBTITLES)
                                            .setContentId(FakeSubTitlePath.get(0))
                                            .setLanguage(SubTitleLanguage.get(0))
                                            .setContentType("text/vtt")
                                            .build();
                                    tracks.add(englishSubtitle);
                                }


                                mediaInfo = new MediaInfo.Builder(Util.dataModel.getVideoUrl().trim())
                                        .setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
                                        .setContentType(mediaContentType)
                                        .setMetadata(movieMetadata)
                                        .setStreamDuration(15 * 1000)
                                        .setCustomData(jsonObj)
                                        .setMediaTracks(tracks)
                                        .build();
                                mSelectedMedia = mediaInfo;


                                togglePlayback();
                            }
                        } else {

                            if (Util.dataModel.getVideoUrl().contains("rtmp://") || Util.dataModel.getVideoUrl().contains("rtmp://")) {
                                Toast.makeText(ShowWithEpisodesActivity.this, Util.getTextofLanguage(ShowWithEpisodesActivity.this, Util.VIDEO_ISSUE, Util.DEFAULT_VIDEO_ISSUE), Toast.LENGTH_SHORT).show();
                            } else {
                                final Intent playVideoIntent;

                                    playVideoIntent = new Intent(ShowWithEpisodesActivity.this, ExoPlayerActivity.class);


                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        if (FakeSubTitlePath.size() > 0) {
                                            // This Portion Will Be changed Later.

                                            File dir = new File(Environment.getExternalStorageDirectory() + "/Android/data/" + getApplicationContext().getPackageName().trim() + "/SubTitleList/");
                                            if (dir.isDirectory()) {
                                                String[] children = dir.list();
                                                for (int i = 0; i < children.length; i++) {
                                                    new File(dir, children[i]).delete();
                                                }
                                            }

                                            progressBarHandler = new ProgressBarHandler(ShowWithEpisodesActivity.this);
                                            progressBarHandler.show();
                                            Download_SubTitle(FakeSubTitlePath.get(0).trim());
                                        } else {
                                            playVideoIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                            playVideoIntent.putExtra("SubTitleName", SubTitleName);
                                            playVideoIntent.putExtra("SubTitlePath", SubTitlePath);
                                            playVideoIntent.putExtra("ResolutionFormat",ResolutionFormat);
                                            playVideoIntent.putExtra("ResolutionUrl",ResolutionUrl);
                                            startActivity(playVideoIntent);
                                        }


                                    }
                                });
                            }
                        }
                    } else {
                        if (Util.dataModel.getVideoUrl().contains("://www.youtube") || Util.dataModel.getVideoUrl().contains("://www.youtu.be")) {
                            if (Util.dataModel.getVideoUrl().contains("live_stream?channel")) {
                                final Intent playVideoIntent = new Intent(ShowWithEpisodesActivity.this, ThirdPartyPlayer.class);
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        playVideoIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                        startActivity(playVideoIntent);

                                    }
                                });
                            } else {

                                final Intent playVideoIntent = new Intent(ShowWithEpisodesActivity.this, YouTubeAPIActivity.class);
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        playVideoIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                        startActivity(playVideoIntent);


                                    }
                                });

                            }
                        } else {
                            final Intent playVideoIntent = new Intent(ShowWithEpisodesActivity.this, ThirdPartyPlayer.class);
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    playVideoIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                    startActivity(playVideoIntent);

                                }
                            });
                        }
                    }
                }


            }
        }

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressBarHandler(ShowWithEpisodesActivity.this);
            pDialog.show();


        }


    }

    private class AsynValidateUserDetails extends AsyncTask<Void, Void, Void> {
        ProgressBarHandler pDialog;

        int status;
        String validUserStr;
        String userMessage;
        String responseStr;
        String loggedInIdStr;

        @Override
        protected Void doInBackground(Void... params) {

            if (pref != null) {
                loggedInIdStr = pref.getString("PREFS_LOGGEDIN_ID_KEY", null);
            }



            String urlRouteList = Util.rootUrl().trim()+Util.userValidationUrl.trim();
            try {
                HttpClient httpclient=new DefaultHttpClient();
                HttpPost httppost = new HttpPost(urlRouteList);
                httppost.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");
                httppost.addHeader("user_id", loggedInIdStr.trim());
                httppost.addHeader("authToken", Util.authTokenStr.trim());
                httppost.addHeader("movie_id", movieUniqueId.trim());
                httppost.addHeader("purchase_type", Util.dataModel.getPurchase_type());
                httppost.addHeader("season_id", Util.dataModel.getSeason_id());
                httppost.addHeader("episode_id", Util.dataModel.getEpisode_id());
             /*   SharedPreferences countryPref = getSharedPreferences(Util.COUNTRY_PREF, 0); // 0 - for private mode
                if (countryPref != null) {
                    String countryCodeStr = countryPref.getString("countryCode", null);
                    httppost.addHeader("country", countryCodeStr);
                }else{
                    httppost.addHeader("country", "IN");

                }*/
                httppost.addHeader("lang_code",Util.getTextofLanguage(ShowWithEpisodesActivity.this,Util.SELECTED_LANGUAGE_CODE,Util.DEFAULT_SELECTED_LANGUAGE_CODE));

                // Execute HTTP Post Request
                try {
                    HttpResponse response = httpclient.execute(httppost);
                    StringBuilder sb = new StringBuilder();

                    BufferedReader reader =
                            new BufferedReader(new InputStreamReader(response.getEntity().getContent()), 65728);
                    String line = null;

                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }

                    responseStr = sb.toString();


                } catch (final org.apache.http.conn.ConnectTimeoutException e){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (pDialog != null && pDialog.isShowing()) {
                                pDialog.hide();
                                pDialog = null;
                            }
                            status = 0;
                            Util.showToast(ShowWithEpisodesActivity.this,Util.getTextofLanguage(ShowWithEpisodesActivity.this,Util.SLOW_INTERNET_CONNECTION,Util.DEFAULT_SLOW_INTERNET_CONNECTION));

                         //   Toast.makeText(ShowWithEpisodesActivity.this, Util.getTextofLanguage(ShowWithEpisodesActivity.this,Util.SLOW_INTERNET_CONNECTION,Util.DEFAULT_SLOW_INTERNET_CONNECTION), Toast.LENGTH_LONG).show();

                        }

                    });

                } catch (IOException e) {
                    if (pDialog != null && pDialog.isShowing()) {
                        pDialog.hide();
                        pDialog = null;
                    }
                    status = 0;

                    e.printStackTrace();
                }
                if(responseStr!=null){
                    JSONObject myJson = new JSONObject(responseStr);
                    status = Integer.parseInt(myJson.optString("code"));
                    validUserStr = myJson.optString("status");
                    userMessage = myJson.optString("msg");
                    if ((myJson.has("member_subscribed")) && myJson.getString("member_subscribed").trim() != null && !myJson.getString("member_subscribed").trim().isEmpty() && !myJson.getString("member_subscribed").trim().equals("null") && !myJson.getString("member_subscribed").trim().matches("")) {

                        isMemberSubscribed = myJson.optString("member_subscribed");
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString("PREFS_LOGIN_ISSUBSCRIBED_KEY",isMemberSubscribed);
                        editor.commit();
                    }

                }

            }
            catch (Exception e) {
                if (pDialog != null && pDialog.isShowing()) {
                    pDialog.hide();
                    pDialog = null;
                }
                status = 0;

            }

            return null;
        }


        protected void onPostExecute(Void result) {
            String Subscription_Str = pref.getString("PREFS_LOGIN_ISSUBSCRIBED_KEY", "0");


            try {
                if (pDialog != null && pDialog.isShowing()) {
                    pDialog.hide();
                    pDialog = null;
                }
            } catch (IllegalArgumentException ex) {
                status = 0;
            }

            if (responseStr == null) {
                try {
                    if (pDialog != null && pDialog.isShowing()) {
                        pDialog.hide();
                        pDialog = null;
                    }
                } catch (IllegalArgumentException ex) {
                    status = 0;
                }
                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(ShowWithEpisodesActivity.this, R.style.MyAlertDialogStyle);
                dlgAlert.setMessage(Util.getTextofLanguage(ShowWithEpisodesActivity.this, Util.NO_DETAILS_AVAILABLE, Util.DEFAULT_NO_DETAILS_AVAILABLE));
                dlgAlert.setTitle(Util.getTextofLanguage(ShowWithEpisodesActivity.this, Util.SORRY, Util.DEFAULT_SORRY));
                dlgAlert.setPositiveButton(Util.getTextofLanguage(ShowWithEpisodesActivity.this, Util.BUTTON_OK, Util.DEFAULT_BUTTON_OK), null);
                dlgAlert.setCancelable(false);
                dlgAlert.setPositiveButton(Util.getTextofLanguage(ShowWithEpisodesActivity.this, Util.BUTTON_OK, Util.DEFAULT_BUTTON_OK),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                Intent in = new Intent(ShowWithEpisodesActivity.this, MainActivity.class);
                                startActivity(in);

                            }
                        });
                dlgAlert.create().show();
            } else if (status <= 0) {
                try {
                    if (pDialog != null && pDialog.isShowing()) {
                        pDialog.hide();
                        pDialog = null;
                    }
                } catch (IllegalArgumentException ex) {
                    status = 0;
                }
                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(ShowWithEpisodesActivity.this, R.style.MyAlertDialogStyle);
                dlgAlert.setMessage(Util.getTextofLanguage(ShowWithEpisodesActivity.this, Util.NO_DETAILS_AVAILABLE, Util.DEFAULT_NO_DETAILS_AVAILABLE));
                dlgAlert.setTitle(Util.getTextofLanguage(ShowWithEpisodesActivity.this, Util.SORRY, Util.DEFAULT_SORRY));
                dlgAlert.setPositiveButton(Util.getTextofLanguage(ShowWithEpisodesActivity.this, Util.BUTTON_OK, Util.DEFAULT_BUTTON_OK), null);
                dlgAlert.setCancelable(false);
                dlgAlert.setPositiveButton(Util.getTextofLanguage(ShowWithEpisodesActivity.this, Util.BUTTON_OK, Util.DEFAULT_BUTTON_OK),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                Intent in = new Intent(ShowWithEpisodesActivity.this, MainActivity.class);
                                startActivity(in);

                            }
                        });
                dlgAlert.create().show();
            }

            if (status > 0) {

                if (status == 427) {

                    try {
                        if (pDialog != null && pDialog.isShowing()) {
                            pDialog.hide();
                            pDialog = null;
                        }
                    } catch (IllegalArgumentException ex) {
                        status = 0;
                    }
                    AlertDialog.Builder dlgAlert = new AlertDialog.Builder(ShowWithEpisodesActivity.this);
                    if (userMessage != null && userMessage.equalsIgnoreCase("")) {
                        dlgAlert.setMessage(userMessage);
                    } else {
                        dlgAlert.setMessage(Util.getTextofLanguage(ShowWithEpisodesActivity.this, Util.CONTENT_NOT_AVAILABLE_IN_YOUR_COUNTRY, Util.DEFAULT_CONTENT_NOT_AVAILABLE_IN_YOUR_COUNTRY));

                    }
                    dlgAlert.setTitle(Util.getTextofLanguage(ShowWithEpisodesActivity.this, Util.SORRY, Util.DEFAULT_SORRY));
                    dlgAlert.setPositiveButton(Util.getTextofLanguage(ShowWithEpisodesActivity.this, Util.BUTTON_OK, Util.DEFAULT_BUTTON_OK), null);
                    dlgAlert.setCancelable(false);
                    dlgAlert.setPositiveButton(Util.getTextofLanguage(ShowWithEpisodesActivity.this, Util.BUTTON_OK, Util.DEFAULT_BUTTON_OK),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();

                                }
                            });
                    dlgAlert.create().show();
                } else if (status == 426 || status == 425) {
                    try {
                        if (pDialog != null && pDialog.isShowing()) {
                            pDialog.hide();
                            pDialog = null;
                        }
                    } catch (IllegalArgumentException ex) {
                        status = 0;
                    }

                    if (PlanId.equals("1") && Subscription_Str.equals("0")) {
                        Intent intent = new Intent(ShowWithEpisodesActivity.this, SubscriptionActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);
                    } else {
                        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(ShowWithEpisodesActivity.this, R.style.MyAlertDialogStyle);
                        if (userMessage != null && !userMessage.equalsIgnoreCase("")) {
                            dlgAlert.setMessage(userMessage);
                        } else {
                            Log.v("SUBHA", "data 2");
                            dlgAlert.setMessage(Util.getTextofLanguage(ShowWithEpisodesActivity.this, Util.CONTENT_NOT_AVAILABLE_IN_YOUR_COUNTRY, Util.DEFAULT_CONTENT_NOT_AVAILABLE_IN_YOUR_COUNTRY));

                        }
                        dlgAlert.setTitle(Util.getTextofLanguage(ShowWithEpisodesActivity.this, Util.SORRY, Util.DEFAULT_SORRY));
                        dlgAlert.setPositiveButton(Util.getTextofLanguage(ShowWithEpisodesActivity.this, Util.BUTTON_OK, Util.DEFAULT_BUTTON_OK), null);
                        dlgAlert.setCancelable(false);
                        dlgAlert.setPositiveButton(Util.getTextofLanguage(ShowWithEpisodesActivity.this, Util.BUTTON_OK, Util.DEFAULT_BUTTON_OK),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();

                                    }
                                });
                        dlgAlert.create().show();
                    }
                } else if (status == 429 || status == 430 || status == 428) {

                    if (validUserStr != null) {
                        try {
                            if (pDialog != null && pDialog.isShowing()) {
                                pDialog.hide();
                                pDialog = null;
                            }
                        } catch (IllegalArgumentException ex) {
                            status = 0;
                        }

                        if ((validUserStr.trim().equalsIgnoreCase("OK")) || (validUserStr.trim().matches("OK")) || (validUserStr.trim().equals("OK"))) {
                            if (Util.checkNetwork(ShowWithEpisodesActivity.this) == true) {
                                asynLoadVideoUrls = new AsynLoadVideoUrls();
                                asynLoadVideoUrls.executeOnExecutor(threadPoolExecutor);
                            } else {

                                Util.showToast(ShowWithEpisodesActivity.this, Util.getTextofLanguage(ShowWithEpisodesActivity.this, Util.NO_INTERNET_CONNECTION, Util.DEFAULT_NO_INTERNET_CONNECTION));
                                //Toast.makeText(ShowWithEpisodesActivity.this, Util.getTextofLanguage(ShowWithEpisodesActivity.this, Util.NO_INTERNET_CONNECTION, Util.DEFAULT_NO_INTERNET_CONNECTION), Toast.LENGTH_LONG).show();

                            }
                        } else {

                            if ((userMessage.trim().equalsIgnoreCase("Unpaid")) || (userMessage.trim().matches("Unpaid")) || (userMessage.trim().equals("Unpaid"))) {
                                if (Util.dataModel.getIsAPV() == 1 || Util.dataModel.getIsPPV() == 1) {
                                    ShowPpvPopUp();
                                } else if (PlanId.equals("1") && Subscription_Str.equals("0")) {
                                    Intent intent = new Intent(ShowWithEpisodesActivity.this, SubscriptionActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                    startActivity(intent);
                                } else {
                                    GetVoucherPlan();
                                }
                            }

                        }
                    }

                } else if (Util.dataModel.getIsAPV() == 1 || Util.dataModel.getIsPPV() == 1) {
                    ShowPpvPopUp();
                } else if (PlanId.equals("1") && Subscription_Str.equals("0")) {
                    Intent intent = new Intent(ShowWithEpisodesActivity.this, SubscriptionActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                } else if (Util.dataModel.getIsConverted() == 0) {
                    AlertDialog.Builder dlgAlert = new AlertDialog.Builder(ShowWithEpisodesActivity.this);
                    dlgAlert.setMessage(Util.getTextofLanguage(ShowWithEpisodesActivity.this, Util.NO_VIDEO_AVAILABLE, Util.DEFAULT_NO_VIDEO_AVAILABLE));
                    dlgAlert.setTitle(Util.getTextofLanguage(ShowWithEpisodesActivity.this, Util.SORRY, Util.DEFAULT_SORRY));
                    dlgAlert.setPositiveButton(Util.getTextofLanguage(ShowWithEpisodesActivity.this, Util.BUTTON_OK, Util.DEFAULT_BUTTON_OK), null);
                    dlgAlert.setCancelable(false);
                    dlgAlert.setPositiveButton(Util.getTextofLanguage(ShowWithEpisodesActivity.this, Util.BUTTON_OK, Util.DEFAULT_BUTTON_OK),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();

                                }
                            });
                    dlgAlert.create().show();
                } else {
                    if (Util.checkNetwork(ShowWithEpisodesActivity.this) == true) {
                        asynLoadVideoUrls = new AsynLoadVideoUrls();
                        asynLoadVideoUrls.executeOnExecutor(threadPoolExecutor);
                    } else {
                        Util.showToast(ShowWithEpisodesActivity.this, Util.getTextofLanguage(ShowWithEpisodesActivity.this, Util.NO_INTERNET_CONNECTION, Util.DEFAULT_NO_INTERNET_CONNECTION));
                        //  Toast.makeText(ShowWithEpisodesActivity.this, Util.getTextofLanguage(ShowWithEpisodesActivity.this, Util.NO_INTERNET_CONNECTION, Util.DEFAULT_NO_INTERNET_CONNECTION), Toast.LENGTH_LONG).show();

                    }
                }

            }
        }


        @Override
        protected void onPreExecute() {
            pDialog = new ProgressBarHandler(ShowWithEpisodesActivity.this);
            pDialog.show();


        }


    }

    private void ShowPpvPopUp() {
        {


            try {
                if (Util.currencyModel.getCurrencySymbol() == null) {
                    Util.showToast(ShowWithEpisodesActivity.this,Util.getTextofLanguage(ShowWithEpisodesActivity.this,Util.NO_DETAILS_AVAILABLE,Util.DEFAULT_NO_DETAILS_AVAILABLE));

                   // Toast.makeText(ShowWithEpisodesActivity.this, Util.getTextofLanguage(ShowWithEpisodesActivity.this, Util.NO_DETAILS_AVAILABLE, Util.DEFAULT_NO_DETAILS_AVAILABLE), Toast.LENGTH_LONG).show();

                }
            } catch (Exception e) {
                Util.showToast(ShowWithEpisodesActivity.this,Util.getTextofLanguage(ShowWithEpisodesActivity.this,Util.NO_DETAILS_AVAILABLE,Util.DEFAULT_NO_DETAILS_AVAILABLE));

               // Toast.makeText(ShowWithEpisodesActivity.this, Util.getTextofLanguage(ShowWithEpisodesActivity.this, Util.NO_DETAILS_AVAILABLE, Util.DEFAULT_NO_DETAILS_AVAILABLE), Toast.LENGTH_LONG).show();
                return;
            }

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(ShowWithEpisodesActivity.this, R.style.MyAlertDialogStyle);
            LayoutInflater inflater = (LayoutInflater) ShowWithEpisodesActivity.this.getSystemService(ShowWithEpisodesActivity.this.LAYOUT_INFLATER_SERVICE);

            View convertView = (View) inflater.inflate(R.layout.activity_ppv_popup, null);
            alertDialog.setView(convertView);
            alertDialog.setTitle("");

            final RadioButton completeRadioButton = (RadioButton) convertView.findViewById(R.id.completeRadioButton);
            final RadioButton seasonRadioButton = (RadioButton) convertView.findViewById(R.id.seasonRadioButton);
            final RadioButton episodeRadioButton = (RadioButton) convertView.findViewById(R.id.episodeRadioButton);
            TextView episodePriceTextView = (TextView) convertView.findViewById(R.id.episodePriceTextView);
            TextView seasonPriceTextView = (TextView) convertView.findViewById(R.id.seasonPriceTextView);
            TextView completePriceTextView = (TextView) convertView.findViewById(R.id.completePriceTextView);
            Button payNowButton = (Button) convertView.findViewById(R.id.payNowButton);


            if (Util.dataModel.getIsAPV() == 1)
            {
                if(Util.apvModel.getIsEpisode() == 1)
                {
                    episodeRadioButton.setVisibility(View.VISIBLE);
                    episodePriceTextView.setVisibility(View.VISIBLE);
                }
                else
                {
                    episodeRadioButton.setVisibility(View.GONE);
                    episodePriceTextView.setVisibility(View.GONE);
                }
                if(Util.apvModel.getIsSeason() == 1)
                {
                    seasonRadioButton.setVisibility(View.VISIBLE);
                    seasonPriceTextView.setVisibility(View.VISIBLE);
                }
                else
                {
                    seasonRadioButton.setVisibility(View.GONE);
                    seasonPriceTextView.setVisibility(View.GONE);
                }
                if(Util.apvModel.getIsShow() == 1)
                {
                    completeRadioButton.setVisibility(View.VISIBLE);
                    completePriceTextView.setVisibility(View.VISIBLE);
                }
                else
                {
                    completeRadioButton.setVisibility(View.GONE);
                    completePriceTextView.setVisibility(View.GONE);
                }
            }
            else
            {
                if(Util.ppvModel.getIsEpisode() == 1)
                {
                    episodeRadioButton.setVisibility(View.VISIBLE);
                    episodePriceTextView.setVisibility(View.VISIBLE);
                }
                else
                {
                    episodeRadioButton.setVisibility(View.GONE);
                    episodePriceTextView.setVisibility(View.GONE);
                }
                if(Util.ppvModel.getIsSeason() == 1)
                {
                    seasonRadioButton.setVisibility(View.VISIBLE);
                    seasonPriceTextView.setVisibility(View.VISIBLE);
                }
                else
                {
                    seasonRadioButton.setVisibility(View.GONE);
                    seasonPriceTextView.setVisibility(View.GONE);
                }
                if(Util.ppvModel.getIsShow() == 1)
                {
                    completeRadioButton.setVisibility(View.VISIBLE);
                    completePriceTextView.setVisibility(View.VISIBLE);
                }
                else
                {
                    completeRadioButton.setVisibility(View.GONE);
                    completePriceTextView.setVisibility(View.GONE);
                }
            }


            completeRadioButton.setText("  " +Util.dataModel.getVideoTitle().trim() + " Complete Season ");
            seasonRadioButton.setText("  "+Util.dataModel.getVideoTitle().trim() + " Season " + Util.dataModel.getEpisode_series_no().trim() + " ");
            episodeRadioButton.setText("  "+Util.dataModel.getVideoTitle().trim() + " S" + Util.dataModel.getEpisode_series_no().trim() + " E " + Util.dataModel.getEpisode_no().trim() + " ");

            String subscriptionStr = pref.getString("PREFS_LOGIN_ISSUBSCRIBED_KEY", "0");

            if(subscriptionStr.trim().equals("1")){
                if (Util.dataModel.getIsAPV() == 1) {

                    episodePriceTextView.setText(Util.currencyModel.getCurrencySymbol() + " " + Util.apvModel.getApvEpisodeSubscribedStr());
                    seasonPriceTextView.setText(Util.currencyModel.getCurrencySymbol() + " " +Util.apvModel.getApvSeasonSubscribedStr());
                    completePriceTextView.setText(Util.currencyModel.getCurrencySymbol() + " " + Util.apvModel.getApvShowSubscribedStr());
                } else {
                    episodePriceTextView.setText(Util.currencyModel.getCurrencySymbol() + " " + Util.ppvModel.getPpvEpisodeSubscribedStr());
                    seasonPriceTextView.setText(Util.currencyModel.getCurrencySymbol() + " " + Util.ppvModel.getPpvSeasonSubscribedStr());
                    completePriceTextView.setText(Util.currencyModel.getCurrencySymbol() + " " +Util.ppvModel.getPpvShowSubscribedStr());
                }
            }
            else{
                if (Util.dataModel.getIsAPV() == 1) {

                    episodePriceTextView.setText(Util.currencyModel.getCurrencySymbol() + " " + Util.apvModel.getApvEpisodeUnsubscribedStr());
                    seasonPriceTextView.setText(Util.currencyModel.getCurrencySymbol() + " " +Util.apvModel.getApvSeasonUnsubscribedStr());
                    completePriceTextView.setText(Util.currencyModel.getCurrencySymbol() + " " + Util.apvModel.getApvShowUnsubscribedStr());
                } else {
                    episodePriceTextView.setText(Util.currencyModel.getCurrencySymbol() + " " + Util.ppvModel.getPpvEpisodeUnsubscribedStr());
                    seasonPriceTextView.setText(Util.currencyModel.getCurrencySymbol() + " " + Util.ppvModel.getPpvSeasonUnsubscribedStr());
                    completePriceTextView.setText(Util.currencyModel.getCurrencySymbol() + " " +Util.ppvModel.getPpvShowUnsubscribedStr());
                }
            }



            alert = alertDialog.show();
            completeRadioButton.setChecked(true);


        /*if (completeRadioButton.isChecked() == true) {
            if (Util.dataModel.getIsAPV() == 1) {
                priceForUnsubscribedStr = Util.apvModel.getApvShowUnsubscribedStr();
                priceFosubscribedStr = Util.apvModel.getApvShowSubscribedStr();

            } else {
                priceForUnsubscribedStr = Util.ppvModel.getPpvShowUnsubscribedStr();
                priceFosubscribedStr = Util.ppvModel.getPpvShowSubscribedStr();
            }
        }*/


            // Changed later

            if (completeRadioButton.getVisibility() == View.VISIBLE) {

                completeRadioButton.setChecked(true);
                episodeRadioButton.setChecked(false);
                seasonRadioButton.setChecked(false);

                if (Util.dataModel.getIsAPV() == 1) {
                    priceForUnsubscribedStr = Util.apvModel.getApvShowUnsubscribedStr();
                    priceFosubscribedStr = Util.apvModel.getApvShowSubscribedStr();

                } else {
                    priceForUnsubscribedStr = Util.ppvModel.getPpvShowUnsubscribedStr();
                    priceFosubscribedStr = Util.ppvModel.getPpvShowSubscribedStr();
                }



            }
            else
            {
                if (seasonRadioButton.getVisibility() == View.VISIBLE) {

                    completeRadioButton.setChecked(false);
                    seasonRadioButton.setChecked(true);
                    episodeRadioButton.setChecked(false);

                    if (Util.dataModel.getIsAPV()== 1) {
                        priceForUnsubscribedStr = Util.apvModel.getApvSeasonUnsubscribedStr();
                        priceFosubscribedStr = Util.apvModel.getApvSeasonSubscribedStr();

                    } else {
                        priceForUnsubscribedStr = Util.ppvModel.getPpvSeasonUnsubscribedStr();
                        priceFosubscribedStr = Util.ppvModel.getPpvSeasonSubscribedStr();
                    }


                }
                else
                {
                    completeRadioButton.setChecked(false);
                    seasonRadioButton.setChecked(false);
                    episodeRadioButton.setChecked(true);

                    if (Util.dataModel.getIsAPV()== 1) {
                        priceForUnsubscribedStr = Util.apvModel.getApvEpisodeUnsubscribedStr();
                        priceFosubscribedStr = Util.apvModel.getApvEpisodeSubscribedStr();

                    } else {
                        priceForUnsubscribedStr = Util.ppvModel.getPpvEpisodeUnsubscribedStr();
                        priceFosubscribedStr = Util.ppvModel.getPpvEpisodeSubscribedStr();
                    }


                }
            }

            ///////////////////////=====================////////////////////////

            completeRadioButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    episodeRadioButton.setChecked(false);
                    seasonRadioButton.setChecked(false);
                    if (Util.dataModel.getIsAPV() == 1) {
                        priceForUnsubscribedStr = Util.apvModel.getApvShowUnsubscribedStr();
                        priceFosubscribedStr = Util.apvModel.getApvShowSubscribedStr();

                    } else {
                        priceForUnsubscribedStr = Util.ppvModel.getPpvShowUnsubscribedStr();
                        priceFosubscribedStr = Util.ppvModel.getPpvShowSubscribedStr();
                    }


                }

            });


            episodeRadioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    completeRadioButton.setChecked(false);
                    seasonRadioButton.setChecked(false);

                    if (Util.dataModel.getIsAPV()== 1) {
                        priceForUnsubscribedStr = Util.apvModel.getApvEpisodeUnsubscribedStr();
                        priceFosubscribedStr = Util.apvModel.getApvEpisodeSubscribedStr();

                    } else {
                        priceForUnsubscribedStr = Util.ppvModel.getPpvEpisodeUnsubscribedStr();
                        priceFosubscribedStr = Util.ppvModel.getPpvEpisodeSubscribedStr();
                    }

                }
            });
            seasonRadioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    episodeRadioButton.setChecked(false);
                    completeRadioButton.setChecked(false);
                    if (Util.dataModel.getIsAPV()== 1) {
                        priceForUnsubscribedStr = Util.apvModel.getApvSeasonUnsubscribedStr();
                        priceFosubscribedStr = Util.apvModel.getApvSeasonSubscribedStr();

                    } else {
                        priceForUnsubscribedStr = Util.ppvModel.getPpvSeasonUnsubscribedStr();
                        priceFosubscribedStr = Util.ppvModel.getPpvSeasonSubscribedStr();
                    }

                }
            });

            payNowButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    if(completeRadioButton.isChecked())
                    {
                        selectedPurchaseType = 1;
                        Util.selected_episode_id = "0";
                        Util.selected_season_id = "0";
                        Log.v("SUBHA","called 1");
                    }
                    else if(seasonRadioButton.isChecked())
                    {
                        selectedPurchaseType = 2;
                        Util.selected_episode_id = "0";
                        Util.selected_season_id = ""+spinnerPosition;
                        Log.v("SUBHA","called 2");
                    }
                    else
                    {
                        selectedPurchaseType = 3;
                        Util.selected_episode_id = Util.dataModel.getStreamUniqueId();
                        Util.selected_season_id = Util.dataModel.getEpisode_series_no();
                        Log.v("SUBHA","called 3");
                    }


                    Log.v("SUBHA","Show withepisode  Activity Season Id ="+Util.selected_season_id);
                    Log.v("SUBHA","Show withepisode Activity episode Id ="+Util.selected_episode_id);

                    alert.dismiss();
                    final Intent showPaymentIntent = new Intent(ShowWithEpisodesActivity.this, PPvPaymentInfoActivity.class);
                    showPaymentIntent.putExtra("muviuniqueid", Util.dataModel.getMovieUniqueId().trim());
                    showPaymentIntent.putExtra("episodeStreamId", Util.dataModel.getStreamUniqueId().trim());
                    showPaymentIntent.putExtra("contentTypesId", Util.dataModel.getContentTypesId());
                    showPaymentIntent.putExtra("movieThirdPartyUrl", Util.dataModel.getThirdPartyUrl());
                    showPaymentIntent.putExtra("planUnSubscribedPrice", priceForUnsubscribedStr);
                    showPaymentIntent.putExtra("planSubscribedPrice", priceFosubscribedStr);
                    showPaymentIntent.putExtra("currencyId", Util.currencyModel.getCurrencyId());
                    showPaymentIntent.putExtra("currencyCountryCode",Util.currencyModel.getCurrencyCode());
                    showPaymentIntent.putExtra("currencySymbol", Util.currencyModel.getCurrencySymbol());
                   // showPaymentIntent.putExtra("showName", Util.dataModel.getEpisode_title());


                    if(selectedPurchaseType==1)
                        showPaymentIntent.putExtra("showName", Util.dataModel.getVideoTitle().trim() + " Complete Season ");
                    if(selectedPurchaseType==2)
                        showPaymentIntent.putExtra("showName", Util.dataModel.getVideoTitle().trim() + " Season " + Util.dataModel.getEpisode_series_no().trim());
                    if(selectedPurchaseType==3)
                        showPaymentIntent.putExtra("showName", Util.dataModel.getVideoTitle().trim() + " S" + Util.dataModel.getEpisode_series_no().trim() + " E " + Util.dataModel.getEpisode_no().trim());
                    showPaymentIntent.putExtra("seriesNumber", Util.dataModel.getEpisode_series_no());
                    showPaymentIntent.putExtra("isPPV", Util.dataModel.getIsPPV());
                    showPaymentIntent.putExtra("isAPV", Util.dataModel.getIsAPV());
                    showPaymentIntent.putExtra("selectedPurchaseType", selectedPurchaseType);
                    if (Util.dataModel.getIsAPV() == 1) {
                        showPaymentIntent.putExtra("isConverted", 0);
                    } else {
                        showPaymentIntent.putExtra("isConverted", 1);

                    }

                    showPaymentIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(showPaymentIntent);

                }
            });
        }
    }




    public class HorizotalSpaceItemDecoration extends RecyclerView.ItemDecoration {

        private final int horizontalSpaceWidth;

        public HorizotalSpaceItemDecoration(int horizontalSpaceWidth) {
            this.horizontalSpaceWidth = horizontalSpaceWidth;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                   RecyclerView.State state) {
            outRect.right = horizontalSpaceWidth;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

       /***************chromecast**********************/

//        CastButtonFactory.setUpMediaRouteButton(getApplicationContext(), menu, R.id.media_route_menu_item);
        /***************chromecast**********************/


        MenuItem item,item1,item2,item3,item4,item5,item6,item7;
        item= menu.findItem(R.id.action_filter);
        item.setVisible(false);
        pref = getSharedPreferences(Util.LOGIN_PREF, 0);
        String loggedInStr = pref.getString("PREFS_LOGGEDIN_KEY", null);
        id = pref.getString("PREFS_LOGGEDIN_ID_KEY", null);
        email=pref.getString("PREFS_LOGIN_EMAIL_ID_KEY", null);
        SharedPreferences language_list_pref = getSharedPreferences(Util.LANGUAGE_LIST_PREF, 0);
        (menu.findItem(R.id.menu_item_language)).setTitle(Util.getTextofLanguage(ShowWithEpisodesActivity.this, Util.LANGUAGE_POPUP_LANGUAGE, Util.DEFAULT_LANGUAGE_POPUP_LANGUAGE));

        if(language_list_pref.getString("total_language","0").equals("1"))
            (menu.findItem(R.id.menu_item_language)).setVisible(false);

        if(loggedInStr!=null){
            item4= menu.findItem(R.id.action_login);
            item4.setTitle(Util.getTextofLanguage(ShowWithEpisodesActivity.this,Util.LANGUAGE_POPUP_LOGIN,Util.DEFAULT_LANGUAGE_POPUP_LOGIN));
            item4.setVisible(false);
            item5= menu.findItem(R.id.action_register);
            item5.setTitle(Util.getTextofLanguage(ShowWithEpisodesActivity.this,Util.BTN_REGISTER,Util.DEFAULT_BTN_REGISTER));
            item5.setVisible(false);

            item1 = menu.findItem(R.id.menu_item_profile);
            item1.setTitle(Util.getTextofLanguage(ShowWithEpisodesActivity.this,Util.PROFILE,Util.DEFAULT_PROFILE));
            item1.setVisible(true);

            item2 = menu.findItem(R.id.action_purchage);
            item2.setTitle(Util.getTextofLanguage(ShowWithEpisodesActivity.this,Util.PURCHASE_HISTORY,Util.DEFAULT_PURCHASE_HISTORY));
            item2.setVisible(true);

            item3 = menu.findItem(R.id.action_logout);
            item3.setTitle(Util.getTextofLanguage(ShowWithEpisodesActivity.this,Util.LOGOUT,Util.DEFAULT_LOGOUT));
            item3.setVisible(true);

            item6 = menu.findItem(R.id.action_mydownload);
            item6.setTitle(Util.getTextofLanguage(ShowWithEpisodesActivity.this,Util.MY_DOWNLOAD,Util.DEFAULT_MY_DOWNLOAD));
            item6.setVisible(true);

            item7 = menu.findItem(R.id.menu_item_favorite);
            item7.setTitle(Util.getTextofLanguage(this,Util.MY_FAVOURITE,Util.DEFAULT_MY_FAVOURITE));
            item7.setVisible(true);


        }else if(loggedInStr==null){
            item4= menu.findItem(R.id.action_login);
            item4.setTitle(Util.getTextofLanguage(ShowWithEpisodesActivity.this,Util.LANGUAGE_POPUP_LOGIN,Util.DEFAULT_LANGUAGE_POPUP_LOGIN));


            item5= menu.findItem(R.id.action_register);
            item5.setTitle(Util.getTextofLanguage(ShowWithEpisodesActivity.this,Util.BTN_REGISTER,Util.DEFAULT_BTN_REGISTER));
            if(isLogin == 1)
            {
                item4.setVisible(true);
                item5.setVisible(true);

            }else{
                item4.setVisible(false);
                item5.setVisible(false);

            }

            item1 = menu.findItem(R.id.menu_item_profile);
            item1.setTitle(Util.getTextofLanguage(ShowWithEpisodesActivity.this,Util.PROFILE,Util.DEFAULT_PROFILE));
            item1.setVisible(false);
            item2= menu.findItem(R.id.action_purchage);
            item2.setTitle(Util.getTextofLanguage(ShowWithEpisodesActivity.this,Util.PURCHASE_HISTORY,Util.DEFAULT_PURCHASE_HISTORY));
            item2.setVisible(false);
            item3= menu.findItem(R.id.action_logout);
            item3.setTitle(Util.getTextofLanguage(ShowWithEpisodesActivity.this,Util.LOGOUT,Util.DEFAULT_LOGOUT));
            item3.setVisible(false);
            item6 = menu.findItem(R.id.action_mydownload);
            item6.setTitle(Util.getTextofLanguage(ShowWithEpisodesActivity.this,Util.MY_DOWNLOAD,Util.DEFAULT_MY_DOWNLOAD));
            item6.setVisible(false);

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
                final Intent searchIntent = new Intent(ShowWithEpisodesActivity.this, SearchActivity.class);
                searchIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(searchIntent);
                // Not implemented here
                return false;
            case R.id.action_filter:

                // Not implemented here
                return false;
            case R.id.action_login:

                Intent loginIntent = new Intent(ShowWithEpisodesActivity.this, LoginActivity.class);
                Util.check_for_subscription = 0;
                startActivity(loginIntent);
                // Not implemented here
                return false;
            case R.id.action_register:

                Intent registerIntent = new Intent(ShowWithEpisodesActivity.this, RegisterActivity.class);
                Util.check_for_subscription = 0;
                startActivity(registerIntent);
                // Not implemented here
                return false;
            case R.id.menu_item_language:

                // Not implemented here
                Default_Language = Util.getTextofLanguage(ShowWithEpisodesActivity.this,Util.SELECTED_LANGUAGE_CODE,Util.DEFAULT_SELECTED_LANGUAGE_CODE);
                Previous_Selected_Language =Util.getTextofLanguage(ShowWithEpisodesActivity.this,Util.SELECTED_LANGUAGE_CODE,Util.DEFAULT_SELECTED_LANGUAGE_CODE);

                if (Util.languageModel!=null && Util.languageModel.size() > 0){


                    ShowLanguagePopup();

                }else {
                    AsynGetLanguageList asynGetLanguageList = new AsynGetLanguageList();
                    asynGetLanguageList.executeOnExecutor(threadPoolExecutor);
                }
                return false;

            case R.id.menu_item_favorite:

                Intent favoriteIntent = new Intent(this, FavoriteActivity.class);
//                favoriteIntent.putExtra("EMAIL",email);
//                favoriteIntent.putExtra("LOGID",id);
                favoriteIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(favoriteIntent);
                // Not implemented here
                return false;
            case R.id.menu_item_profile:

                Intent profileIntent = new Intent(ShowWithEpisodesActivity.this, ProfileActivity.class);
                profileIntent.putExtra("EMAIL",email);
                profileIntent.putExtra("LOGID",id);
                startActivity(profileIntent);
                // Not implemented here
                return false;
            case R.id.action_purchage:

               Intent purchaseintent = new Intent(ShowWithEpisodesActivity.this, PurchaseHistoryActivity.class);
                startActivity(purchaseintent);
                // Not implemented here
                return false;
            case R.id.action_mydownload:

                Intent mydownload = new Intent(ShowWithEpisodesActivity.this, MyDownloads.class);
                startActivity(mydownload);
                // Not implemented here
                return false;
            case R.id.action_logout:

                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(ShowWithEpisodesActivity.this, R.style.MyAlertDialogStyle);
                dlgAlert.setMessage(Util.getTextofLanguage(ShowWithEpisodesActivity.this,Util.SIGN_OUT_WARNING,Util.DEFAULT_SIGN_OUT_WARNING));
                dlgAlert.setTitle("");

                dlgAlert.setPositiveButton(Util.getTextofLanguage(ShowWithEpisodesActivity.this,Util.YES,Util.DEFAULT_YES) ,new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing but close the dialog

                        // dialog.cancel();
                        AsynLogoutDetails asynLogoutDetails=new AsynLogoutDetails();
                        asynLogoutDetails.executeOnExecutor(threadPoolExecutor);



                        dialog.dismiss();
                    }
                });

                dlgAlert.setNegativeButton(Util.getTextofLanguage(ShowWithEpisodesActivity.this,Util.NO,Util.DEFAULT_NO), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // Do nothing
                        dialog.dismiss();
                    }
                });
                // dlgAlert.setPositiveButton(getResources().getString(R.string.yes_str), null);
                dlgAlert.setCancelable(false);

                dlgAlert.create().show();

                return false;
            default:
                break;
        }

        return false;
    }

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
                httppost.addHeader("lang_code",Util.getTextofLanguage(ShowWithEpisodesActivity.this,Util.SELECTED_LANGUAGE_CODE,Util.DEFAULT_SELECTED_LANGUAGE_CODE));


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
                            Util.showToast(ShowWithEpisodesActivity.this,Util.getTextofLanguage(ShowWithEpisodesActivity.this,Util.SLOW_INTERNET_CONNECTION,Util.DEFAULT_SLOW_INTERNET_CONNECTION));

//                            Toast.makeText(ShowWithEpisodesActivity.this,Util.getTextofLanguage(ShowWithEpisodesActivity.this, Util.SLOW_INTERNET_CONNECTION, Util.DEFAULT_SLOW_INTERNET_CONNECTION),Toast.LENGTH_LONG).show();

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
                Util.showToast(ShowWithEpisodesActivity.this,Util.getTextofLanguage(ShowWithEpisodesActivity.this,Util.SIGN_OUT_ERROR,Util.DEFAULT_SIGN_OUT_ERROR));

               // Toast.makeText(ShowWithEpisodesActivity.this,Util.getTextofLanguage(ShowWithEpisodesActivity.this, Util.SIGN_OUT_ERROR, Util.DEFAULT_SIGN_OUT_ERROR),Toast.LENGTH_LONG).show();

            }
            if(responseStr == null){
                Util.showToast(ShowWithEpisodesActivity.this,Util.getTextofLanguage(ShowWithEpisodesActivity.this,Util.SIGN_OUT_ERROR,Util.DEFAULT_SIGN_OUT_ERROR));

//                Toast.makeText(ShowWithEpisodesActivity.this,Util.getTextofLanguage(ShowWithEpisodesActivity.this, Util.SIGN_OUT_ERROR, Util.DEFAULT_SIGN_OUT_ERROR),Toast.LENGTH_LONG).show();

            }
            if (responseCode == 0) {
                Util.showToast(ShowWithEpisodesActivity.this,Util.getTextofLanguage(ShowWithEpisodesActivity.this,Util.SIGN_OUT_ERROR,Util.DEFAULT_SIGN_OUT_ERROR));

//                Toast.makeText(ShowWithEpisodesActivity.this,Util.getTextofLanguage(ShowWithEpisodesActivity.this, Util.SIGN_OUT_ERROR, Util.DEFAULT_SIGN_OUT_ERROR),Toast.LENGTH_LONG).show();

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
                    if ((Util.getTextofLanguage(ShowWithEpisodesActivity.this, Util.IS_ONE_STEP_REGISTRATION, Util.DEFAULT_IS_ONE_STEP_REGISTRATION)
                            .trim()).equals("1")) {
                        final Intent startIntent = new Intent(ShowWithEpisodesActivity.this, SplashScreen.class);
                        runOnUiThread(new Runnable() {
                            public void run() {
                                startIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                startActivity(startIntent);
                                Util.showToast(ShowWithEpisodesActivity.this,Util.getTextofLanguage(ShowWithEpisodesActivity.this, Util.LOGOUT_SUCCESS, Util.DEFAULT_LOGOUT_SUCCESS));
                                finish();

                            }
                        });
                    }
                    else
                    {
                        final Intent startIntent = new Intent(ShowWithEpisodesActivity.this, MainActivity.class);
                        runOnUiThread(new Runnable() {
                            public void run() {
                                startIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                startActivity(startIntent);
                                Util.showToast(ShowWithEpisodesActivity.this,Util.getTextofLanguage(ShowWithEpisodesActivity.this, Util.LOGOUT_SUCCESS, Util.DEFAULT_LOGOUT_SUCCESS));
                                finish();

                            }
                        });
                    }

                }
                else {
                    Util.showToast(ShowWithEpisodesActivity.this,Util.getTextofLanguage(ShowWithEpisodesActivity.this,Util.SIGN_OUT_ERROR,Util.DEFAULT_SIGN_OUT_ERROR));

//                    Toast.makeText(ShowWithEpisodesActivity.this,Util.getTextofLanguage(ShowWithEpisodesActivity.this, Util.SIGN_OUT_ERROR, Util.DEFAULT_SIGN_OUT_ERROR),Toast.LENGTH_LONG).show();

                }
            }

        }

        @Override
        protected void onPreExecute() {

            pDialog = new ProgressBarHandler(ShowWithEpisodesActivity.this);
            pDialog.show();
        }
    }

    private BroadcastReceiver DELETE_ACTION = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String movieUniqId = intent.getStringExtra("movie_uniq_id").trim();
            Log.v("UMA","movie_id tto be deleted="+movieUniqId);
            if (movieUniqId.equals(movieUniqueId.trim())){
                isFavorite=0;
                favorite_view_episode.setImageResource(R.drawable.favorite_unselected);
            }


        }
    };

    public void ShowLanguagePopup()
    {

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(ShowWithEpisodesActivity.this,R.style.MyAlertDialogStyle);
        LayoutInflater inflater = (LayoutInflater)getSystemService(ShowWithEpisodesActivity.this.LAYOUT_INFLATER_SERVICE);

        View convertView = (View) inflater.inflate(R.layout.language_pop_up, null);
        TextView titleTextView = (TextView) convertView.findViewById(R.id.languagePopupTitle);
        titleTextView.setText(Util.getTextofLanguage(ShowWithEpisodesActivity.this,Util.APP_SELECT_LANGUAGE,Util.DEFAULT_APP_SELECT_LANGUAGE));

        alertDialog.setView(convertView);
        alertDialog.setTitle("");

        RecyclerView recyclerView = (RecyclerView) convertView.findViewById(R.id.language_recycler_view);
        Button apply = (Button) convertView.findViewById(R.id.apply_btn);
        apply.setText(Util.getTextofLanguage(ShowWithEpisodesActivity.this,Util.BUTTON_APPLY,Util.DEFAULT_BUTTON_APPLY));

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

        languageCustomAdapter = new LanguageCustomAdapter(ShowWithEpisodesActivity.this, Util.languageModel);
        // Util.languageModel.get(0).setSelected(true);
      /*  if (Util.languageModel.get(i).getLanguageId().equalsIgnoreCase(Util.getTextofLanguage(MovieDetailsActivity.this, Util.SELECTED_LANGUAGE_CODE, Util.DEFAULT_SELECTED_LANGUAGE_CODE))) {
            prevPosition = i;
            Util.languageModel.get(i).setSelected(true);

        }
        Util.languageModel.get(0).setSelected(true);*/

        recyclerView.setAdapter(languageCustomAdapter);



    /*    for (int i = 0 ; i < Util.languageModel.size() - 1 ; i ++){
                if (Util.languageModel.get(i).getLanguageId().equalsIgnoreCase(Util.getTextofLanguage(MovieDetailsActivity.this, Util.SELECTED_LANGUAGE_CODE, Util.DEFAULT_SELECTED_LANGUAGE_CODE))) {
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
        recyclerView.addOnItemTouchListener(new MovieDetailsActivity.RecyclerTouchListener1(ShowWithEpisodesActivity.this, recyclerView, new MovieDetailsActivity.ClickListener1() {
            @Override
            public void onClick(View view, int position) {
                Util.itemclicked = true;

                Util.languageModel.get(position).setSelected(true);


                if (prevPosition != position) {
                    Util.languageModel.get(prevPosition).setSelected(false);
                    prevPosition = position;

                }

                Default_Language = Util.languageModel.get(position).getLanguageId();



                Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.SELECTED_LANGUAGE_CODE,Util.languageModel.get(position).getLanguageId());
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
                Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.SELECTED_LANGUAGE_CODE,Previous_Selected_Language);
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

    @Override
    protected void onResume() {
        super.onResume();
        /**FAVORITE*/
        if(Util.favorite_clicked == true){

            asynLoadMovieDetails = new AsynLoadMovieDetails();
            asynLoadMovieDetails.executeOnExecutor(threadPoolExecutor);
        }
        // **************chromecast*********************//
        if (mCastSession == null) {
            mCastSession = CastContext.getSharedInstance(this).getSessionManager()
                    .getCurrentCastSession();
        }

        AsynGetIpAddress asynGetIpAddress = new AsynGetIpAddress();
        asynGetIpAddress.executeOnExecutor(threadPoolExecutor);

        /***************chromecast**********************/
        if (mCastSession == null) {
            mCastSession = CastContext.getSharedInstance(this).getSessionManager()
                    .getCurrentCastSession();
        }



        mCastContext.getSessionManager().addSessionManagerListener(
                mSessionManagerListener, CastSession.class);

        /***************chromecast**********************/
        invalidateOptionsMenu();

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
                        if(!Util.getTextofLanguage(ShowWithEpisodesActivity.this,Util.SELECTED_LANGUAGE_CODE,"").equals(""))
                        {
                            Default_Language = Util.getTextofLanguage(ShowWithEpisodesActivity.this,Util.SELECTED_LANGUAGE_CODE,Util.DEFAULT_SELECTED_LANGUAGE_CODE);
                        }

                    } catch (Exception e) {
                        status = 0;
                    }
                }

            } catch (Exception e) {
                runOnUiThread(new Runnable() {
                    public void run() {

                    }
                });
            }

            return null;
        }


        protected void onPostExecute(Void result) {

            if(progressBarHandler.isShowing())
            {
                progressBarHandler.hide();
                progressBarHandler = null;

            }

            if (responseStr == null) {
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
                    }


                 /*   if(!Default_Language.equals("en")) {
                        //                  Call For Language Translation.
                        AsynGetTransalatedLanguage asynGetTransalatedLanguage = new AsynGetTransalatedLanguage();
                        asynGetTransalatedLanguage.executeOnExecutor(threadPoolExecutor);

                    }else{

                    }*/

                } else {
                }
            }
            ShowLanguagePopup();


        }

        protected void onPreExecute() {

            progressBarHandler = new ProgressBarHandler(ShowWithEpisodesActivity.this);
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
            } else {
                if (status > 0 && status == 200) {

                    try {
                        JSONObject parent_json = new JSONObject(responseStr);
                        JSONObject json = parent_json.getJSONObject("translation");



                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.ALREADY_MEMBER,json.optString("already_member").trim());
                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.ACTIAVTE_PLAN_TITLE,json.optString("activate_plan_title").trim());
                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.TRANSACTION_STATUS_ACTIVE,json.optString("transaction_status_active").trim());
                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.ADD_TO_FAV,json.optString("add_to_fav").trim());
                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.ADDED_TO_FAV,json.optString("added_to_fav").trim());

                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.ADVANCE_PURCHASE,json.optString("advance_purchase").trim());
                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.ALERT,json.optString("alert").trim());
                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.EPISODE_TITLE,json.optString("episodes_title").trim());
                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.SORT_ALPHA_A_Z,json.optString("sort_alpha_a_z").trim());
                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.SORT_ALPHA_Z_A,json.optString("sort_alpha_z_a").trim());

                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.AMOUNT,json.optString("amount").trim());
                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.COUPON_CANCELLED,json.optString("coupon_cancelled").trim());
                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.BUTTON_APPLY,json.optString("btn_apply").trim());
                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.SIGN_OUT_WARNING,json.optString("sign_out_warning").trim());
                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.DISCOUNT_ON_COUPON,json.optString("discount_on_coupon").trim());

                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.CREDIT_CARD_CVV_HINT,json.optString("credit_card_cvv_hint").trim());
                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.CAST,json.optString("cast").trim());
                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.CAST_CREW_BUTTON_TITLE,json.optString("cast_crew_button_title").trim());
                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.CENSOR_RATING,json.optString("censor_rating").trim());
                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.ENTER_EMPTY_FIELD,json.optString("enter_register_fields_data").trim());
                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.HOME,json.optString("home").trim());


                        if(json.optString("change_password").trim()==null || json.optString("change_password").trim().equals("")) {
                            Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this, Util.CHANGE_PASSWORD, Util.DEFAULT_CHANGE_PASSWORD);
                        }
                        else {
                            Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this, Util.CHANGE_PASSWORD, json.optString("change_password").trim());
                        }

                        if(json.optString("add_a_review").trim()==null || json.optString("add_a_review").trim().equals("")) {
                            Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this, Util.ADD_A_REVIEW, Util.DEFAULT_ADD_A_REVIEW);
                        }
                        else {
                            Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this, Util.ADD_A_REVIEW, json.optString("add_a_review").trim());
                        }
                        if(json.optString("submit_your_rating_title").trim()==null || json.optString("submit_rating").trim().equals("")) {
                            Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this, Util.SUBMIT_YOUR_RATING_TITLE, Util.DEFAULT_SUBMIT_YOUR_RATING_TITLE);
                        }
                        else {
                            Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this, Util.SUBMIT_YOUR_RATING_TITLE, json.optString("submit_rating").trim());
                        }
                        if(json.optString("reviews").trim()==null || json.optString("reviews").trim().equals("")) {
                            Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this, Util.REVIEWS, Util.DEFAULT_REVIEWS);
                        }
                        else {
                            Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this, Util.REVIEWS, json.optString("reviews").trim());
                        }
                        if(json.optString("click_here").trim()==null || json.optString("click_here").trim().equals("")) {
                            Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this, Util.CLICK_HERE, Util.DEFAULT_CLICK_HERE);
                        }
                        else {
                            Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this, Util.CLICK_HERE, json.optString("click_here").trim());
                        }
                        if(json.optString("to_login").trim()==null || json.optString("to_login").trim().equals("")) {
                            Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this, Util.TO_LOGIN, Util.DEFAULT_TO_LOGIN);
                        }
                        else {
                            Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this, Util.TO_LOGIN, json.optString("to_login").trim());
                        }
                        if(json.optString("need_login_to_review").trim()==null || json.optString("need_login_to_review").trim().equals("")) {
                            Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this, Util.NEED_LOGIN_TO_REVIEW, Util.DEFAULT_NEED_LOGIN_TO_REVIEW);
                        }
                        else {
                            Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this, Util.NEED_LOGIN_TO_REVIEW, json.optString("need_login_to_review").trim());
                        }
                        if(json.optString("btn_post_review").trim()==null || json.optString("btn_post_review").trim().equals("")) {
                            Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this, Util.BTN_POST_REVIEW, Util.DEFAULT_BTN_POST_REVIEW);
                        }
                        else {
                            Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this, Util.BTN_POST_REVIEW, json.optString("btn_post_review").trim());
                        }
                        if(json.optString("login_facebook").trim()==null || json.optString("login_facebook").trim().equals("")) {
                            Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this, Util.LOGIN_FACEBOOK, Util.DEFAULT_LOGIN_FACEBOOK);
                        }
                        else {
                            Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this, Util.LOGIN_FACEBOOK, json.optString("login_facebook").trim());
                        }
                        if(json.optString("enter_review_here").trim()==null || json.optString("enter_review_here").trim().equals("")) {
                            Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this, Util.ENTER_REVIEW_HERE, Util.DEFAULT_ENTER_REVIEW_HERE);
                        }
                        else {
                            Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this, Util.ENTER_REVIEW_HERE, json.optString("enter_review_here").trim());
                        }

                        if(json.optString("register_facebook").trim()==null || json.optString("register_facebook").trim().equals("")) {
                            Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this, Util.REGISTER_FACEBOOK, Util.DEFAULT_REGISTER_FACEBOOK);
                        }
                        else {
                            Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this, Util.REGISTER_FACEBOOK, json.optString("register_facebook").trim());
                        }

                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this, Util.CANCEL_BUTTON, json.optString("btn_cancel").trim());
                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this, Util.RESUME_MESSAGE, json.optString("resume_watching").trim());
                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this, Util.CONTINUE_BUTTON, json.optString("continue").trim());


                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.CONFIRM_PASSWORD,json.optString("confirm_password").trim());
                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.CREDIT_CARD_DETAILS,json.optString("credit_card_detail").trim());
                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.DIRECTOR,json.optString("director").trim());
                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.DOWNLOAD_BUTTON_TITLE,json.optString("download_button_title").trim());
                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.DESCRIPTION,json.optString("description").trim());

                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.EMAIL_EXISTS,json.optString("email_exists").trim());
                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.EMAIL_DOESNOT_EXISTS,json.optString("email_does_not_exist").trim());
                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.EMAIL_PASSWORD_INVALID,json.optString("email_password_invalid").trim());
                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.COUPON_CODE_HINT,json.optString("coupon_code_hint").trim());
                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.SEARCH_ALERT,json.optString("search_alert").trim());

                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.CREDIT_CARD_NUMBER_HINT,json.optString("credit_card_number_hint").trim());
                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.TEXT_EMIAL,json.optString("text_email").trim());
                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.NAME_HINT,json.optString("name_hint").trim());
                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.CREDIT_CARD_NAME_HINT,json.optString("credit_card_name_hint").trim());
                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.TEXT_PASSWORD,json.optString("text_password").trim());

                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.ERROR_IN_PAYMENT_VALIDATION,json.optString("error_in_payment_validation").trim());
                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.ERROR_IN_REGISTRATION,json.optString("error_in_registration").trim());
                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.TRANSACTION_STATUS_EXPIRED,json.optString("transaction_status_expired").trim());
                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.DETAILS_NOT_FOUND_ALERT,json.optString("details_not_found_alert").trim());

                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.FAILURE,json.optString("failure").trim());
                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.FILTER_BY,json.optString("filter_by").trim());
                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.FORGOT_PASSWORD,json.optString("forgot_password").trim());
                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.GENRE,json.optString("genre").trim());

                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.AGREE_TERMS,json.optString("agree_terms").trim());
                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.INVALID_COUPON,json.optString("invalid_coupon").trim());
                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.INVOICE,json.optString("invoice").trim());
                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.LANGUAGE_POPUP_LANGUAGE,json.optString("language_popup_language").trim());
                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.SORT_LAST_UPLOADED,json.optString("sort_last_uploaded").trim());

                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.LANGUAGE_POPUP_LOGIN,json.optString("language_popup_login").trim());
                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.LOGIN,json.optString("login").trim());
                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.LOGOUT,json.optString("logout").trim());
                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.LOGOUT_SUCCESS,json.optString("logout_success").trim());
                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.MY_FAVOURITE,json.optString("my_favourite").trim());

                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.NEW_PASSWORD,json.optString("new_password").trim());
                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.NEW_HERE_TITLE,json.optString("new_here_title").trim());
                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.NO,json.optString("no").trim());
                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.NO_DATA,json.optString("no_data").trim());
                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.NO_INTERNET_CONNECTION,json.optString("no_internet_connection").trim());

                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.NO_INTERNET_NO_DATA,json.optString("no_internet_no_data").trim());
                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.NO_DETAILS_AVAILABLE,json.optString("no_details_available").trim());
                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.BUTTON_OK,json.optString("btn_ok").trim());
                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.OLD_PASSWORD,json.optString("old_password").trim());
                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.OOPS_INVALID_EMAIL,json.optString("oops_invalid_email").trim());

                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.ORDER,json.optString("order").trim());
                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.TRANSACTION_DETAILS_ORDER_ID,json.optString("transaction_detail_order_id").trim());
                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.PASSWORD_RESET_LINK,json.optString("password_reset_link").trim());
                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.PASSWORDS_DO_NOT_MATCH,json.optString("password_donot_match").trim());
                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.PAY_BY_PAYPAL,json.optString("pay_by_paypal").trim());

                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.BTN_PAYNOW,json.optString("btn_paynow").trim());
                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.PAY_WITH_CREDIT_CARD,json.optString("pay_with_credit_card").trim());
                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.PAYMENT_OPTIONS_TITLE,json.optString("payment_options_title").trim());
                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.PLAN_NAME,json.optString("plan_name").trim());
                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.ACTIVATE_SUBSCRIPTION_WATCH_VIDEO,json.optString("activate_subscription_watch_video").trim());
                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.ENTER_REGISTER_FIELDS_DATA,json.optString("enter_register_fields_data").trim());

                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.COUPON_ALERT,json.optString("coupon_alert").trim());
                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.VALID_CONFIRM_PASSWORD,json.optString("valid_confirm_password").trim());
                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.PROFILE,json.optString("profile").trim());
                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.PROFILE_UPDATED,json.optString("profile_updated").trim());

                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.PURCHASE,json.optString("purchase").trim());
                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.TRANSACTION_DETAIL_PURCHASE_DATE,json.optString("transaction_detail_purchase_date").trim());
                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.PURCHASE_HISTORY,json.optString("purchase_history").trim());
                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.BTN_REGISTER,json.optString("btn_register").trim());
                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.SORT_RELEASE_DATE,json.optString("sort_release_date").trim());

                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.SAVE_THIS_CARD,json.optString("save_this_card").trim());
                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.TEXT_SEARCH_PLACEHOLDER,json.optString("text_search_placeholder").trim());
                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.SEASON,json.optString("season").trim());
                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.SELECT_OPTION_TITLE,json.optString("select_option_title").trim());
                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.SELECT_PLAN,json.optString("select_plan").trim());

                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.SIGN_UP_TITLE,json.optString("signup_title").trim());
                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.SLOW_INTERNET_CONNECTION,json.optString("slow_internet_connection").trim());
                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.SLOW_ISSUE_INTERNET_CONNECTION,json.optString("slow_issue_internet_connection").trim());
                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.SORRY,json.optString("sorry").trim());
                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.GEO_BLOCKED_ALERT,json.optString("geo_blocked_alert").trim());

                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.SIGN_OUT_ERROR,json.optString("sign_out_error").trim());
                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.ALREADY_PURCHASE_THIS_CONTENT,json.optString("already_purchase_this_content").trim());
                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.CROSSED_MAXIMUM_LIMIT,json.optString("crossed_max_limit_of_watching").trim());
                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.SORT_BY,json.optString("sort_by").trim());
                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.STORY_TITLE,json.optString("story_title").trim());

                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.BTN_SUBMIT,json.optString("btn_submit").trim());
                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.TRANSACTION_STATUS,json.optString("transaction_success").trim());
                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.VIDEO_ISSUE,json.optString("video_issue").trim());
                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.NO_CONTENT,json.optString("no_content").trim());
                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.NO_VIDEO_AVAILABLE,json.optString("no_video_available").trim());

                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.CONTENT_NOT_AVAILABLE_IN_YOUR_COUNTRY,json.optString("content_not_available_in_your_country").trim());
                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.TRANSACTION_DATE,json.optString("transaction_date").trim());
                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.TRANASCTION_DETAIL,json.optString("transaction_detail").trim());
                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.TRANSACTION_STATUS,json.optString("transaction_status").trim());
                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.TRANSACTION,json.optString("transaction").trim());

                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.TRY_AGAIN,json.optString("try_again").trim());
                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.UNPAID,json.optString("unpaid").trim());
                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.USE_NEW_CARD,json.optString("use_new_card").trim());
                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.VIEW_MORE,json.optString("view_more").trim());
                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.VIEW_TRAILER,json.optString("view_trailer").trim());

                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.WATCH,json.optString("watch").trim());
                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.WATCH_NOW,json.optString("watch_now").trim());
                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.SIGN_OUT_ALERT,json.optString("sign_out_alert").trim());
                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.UPDATE_PROFILE_ALERT,json.optString("update_profile_alert").trim());
                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.YES,json.optString("yes").trim());

                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.PURCHASE_SUCCESS_ALERT,json.optString("purchase_success_alert").trim());
                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.CARD_WILL_CHARGE,json.optString("card_will_charge").trim());
                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.SEARCH_HINT,json.optString("search_hint").trim());
                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this, Util.TERMS, json.optString("terms").trim());
                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this, Util.UPDATE_PROFILE, json.optString("btn_update_profile").trim());
                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this, Util.APP_ON, json.optString("app_on").trim());
                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this, Util.APP_SELECT_LANGUAGE, json.optString("app_select_language").trim());

                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this, Util.FILL_FORM_BELOW, json.optString("fill_form_below").trim());
                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this, Util.MESSAGE, json.optString("text_message").trim());

                        Util.getTextofLanguage(ShowWithEpisodesActivity.this, Util.PURCHASE, Util.DEFAULT_PURCHASE);
                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this, Util.SELECTED_LANGUAGE_CODE, Default_Language);


                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this, Util.SIMULTANEOUS_LOGOUT_SUCCESS_MESSAGE, json.optString("simultaneous_logout_message").trim());
                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this, Util.LOGIN_STATUS_MESSAGE, json.optString("login_status_message").trim());
                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this, Util.FILL_FORM_BELOW, json.optString("fill_form_below").trim());
                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this, Util.MESSAGE, json.optString("text_message").trim());
                        //Call For Language PopUp Dialog

                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.SIMULTANEOUS_LOGOUT_SUCCESS_MESSAGE,json.optString("logged_out_from_all_devices").trim());
                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.ANDROID_VERSION,json.optString("android_version").trim());
                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.MANAGE_DEVICE,json.optString("manage_device").trim());
                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.YOUR_DEVICE,json.optString("your_device").trim());
                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.DEREGISTER,json.optString("deregister").trim());
                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.LOGIN_STATUS_MESSAGE,json.optString("oops_you_have_no_access").trim());
                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.UPADTE_TITLE,json.optString("update_title").trim());
                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.UPADTE_MESSAGE,json.optString("update_message").trim());

                        //Language for offline viewing

                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.MY_DOWNLOAD,json.optString("my_download").trim());
                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.DELETE_BTN,json.optString("delete_btn").trim());
                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.STOP_SAVING_THIS_VIDEO,json.optString("stop_saving_this_video").trim());
                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.YOUR_VIDEO_WONT_BE_SAVED,json.optString("your_video_can_not_be_saved").trim());
                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.BTN_KEEP,json.optString("btn_keep").trim());
                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.BTN_DISCARD,json.optString("btn_discard").trim());
                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.DOWNLOAD_CANCELLED,json.optString("download_cancelled").trim());
                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.WANT_TO_DOWNLOAD,json.optString("want_to_download").trim());
                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.WANT_TO_DELETE,json.optString("want_to_delete").trim());
                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.VIEW_LESS,json.optString("view_less").trim());

                        //Language for voucher

                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this, Util.VOUCHER_CODE, json.optString("voucher_code").trim());
                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.VOUCHER_BLANK_MESSAGE,json.optString("voucher_vaildate_message").trim());
                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.VOUCHER_SUCCESS,json.optString("voucher_applied_success").trim());
                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.BTN_NEXT,json.optString("btn_next").trim());
                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.FREE_FOR_COUPON,json.optString("free_for_coupon").trim());
                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.SELECT_PURCHASE_TYPE,json.optString("select_purchase_type").trim());
                        Util.setLanguageSharedPrefernce(ShowWithEpisodesActivity.this,Util.COMPLETE_SEASON,json.optString("complete_season").trim());



                        languageCustomAdapter.notifyDataSetChanged();

                        Intent intent = new Intent(ShowWithEpisodesActivity.this,MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);



                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    // Call For Other Methods.


                } else {
                }
            }



        }
        protected void onPreExecute() {
            progressBarHandler = new ProgressBarHandler(ShowWithEpisodesActivity.this);
            progressBarHandler.show();
        }
    }



    /*****************chromecvast*-------------------------------------*/

    private void updateMetadata(boolean visible) {
        Point displaySize;
        if (!visible) {
            /*mDescriptionView.setVisibility(View.GONE);
            mTitleView.setVisibility(View.GONE);
            mAuthorView.setVisibility(View.GONE);*/
            displaySize = Util.getDisplaySize(this);
            RelativeLayout.LayoutParams lp = new
                    RelativeLayout.LayoutParams(displaySize.x,
                    displaySize.y + getSupportActionBar().getHeight());
            lp.addRule(RelativeLayout.CENTER_IN_PARENT);
            // mVideoView.setLayoutParams(lp);
            //mVideoView.invalidate();
        } else {
            //MediaMetadata mm = mSelectedMedia.getMetadata();
          /*  mDescriptionView.setText(mSelectedMedia.getCustomData().optString(
                    VideoProvider.KEY_DESCRIPTION));
            //mTitleView.setText(mm.getString(MediaMetadata.KEY_TITLE));
            //mAuthorView.setText(mm.getString(MediaMetadata.KEY_SUBTITLE));
            mDescriptionView.setVisibility(View.VISIBLE);
            mTitleView.setVisibility(View.VISIBLE);
            mAuthorView.setVisibility(View.VISIBLE);*/
            displaySize = Util.getDisplaySize(this);
            RelativeLayout.LayoutParams lp = new
                    RelativeLayout.LayoutParams(displaySize.x,
                    (int) (displaySize.x * mAspectRatio));
            lp.addRule(RelativeLayout.BELOW, R.id.toolbar);
            // mVideoView.setLayoutParams(lp);
            //mVideoView.invalidate();
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
                mLocation = PlaybackLocation.REMOTE;
                if (null != mSelectedMedia) {

                    if (mPlaybackState ==PlaybackState.PLAYING) {
                       /* mVideoView.pause();
                        loadRemoteMedia(mSeekbar.getProgress(), true);*/
                        return;
                    } else {
                        mPlaybackState = PlaybackState.IDLE;
                        updatePlaybackLocation(PlaybackLocation.REMOTE);
                    }
                }
                updatePlayButton(mPlaybackState);
                invalidateOptionsMenu();
            }

            private void onApplicationDisconnected() {
/*
                    mPlayCircle.setVisibility(View.GONE);
*/

                updatePlaybackLocation(PlaybackLocation.LOCAL);
                mPlaybackState = PlaybackState.IDLE;
                mLocation = PlaybackLocation.LOCAL;
                updatePlayButton(mPlaybackState);
                invalidateOptionsMenu();
            }
        };
    }

    private void updatePlayButton(PlaybackState state) {
           /* boolean isConnected = (mCastSession != null)
                    && (mCastSession.isConnected() || mCastSession.isConnecting());*/
        //mControllers.setVisibility(isConnected ? View.GONE : View.VISIBLE);

        switch (state) {
            case PLAYING:

                //mLoading.setVisibility(View.INVISIBLE);
                // mPlayPause.setVisibility(View.VISIBLE);
                //mPlayPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_av_pause_dark));

                break;
            case IDLE:
                if (mLocation == PlaybackLocation.LOCAL){
                   /* if (isAPV == 1) {
                        watchMovieButton.setText(getResources().getString(R.string.advance_purchase_str));
                    }else {
                        watchMovieButton.setText(getResources().getString(R.string.movie_details_watch_video_button_title));
                    }*/

                }else{
                   /* if (isAPV == 1) {
                        watchMovieButton.setText(getResources().getString(R.string.advance_purchase_str));
                    }else {
                        watchMovieButton.setText(getResources().getString(R.string.movie_details_cast_now_button_title));
                    }*/
                }
                //mCon
                // trollers.setVisibility(View.GONE);
                // mCoverArt.setVisibility(View.VISIBLE);
                // mVideoView.setVisibility(View.INVISIBLE);
                break;
            case PAUSED:
                //mLoading.setVisibility(View.INVISIBLE);
              /*  mPlayPause.setVisibility(View.VISIBLE);
                mPlayPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_av_play_dark));*/

                break;
            case BUFFERING:
                //mPlayPause.setVisibility(View.INVISIBLE);
                //mLoading.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
    }

    private void updatePlaybackLocation(PlaybackLocation location) {
        mLocation = location;
        if (location == PlaybackLocation.LOCAL) {
            if (mPlaybackState == PlaybackState.PLAYING
                    || mPlaybackState == PlaybackState.BUFFERING) {
                //setCoverArtStatus(null);
                //startControllersTimer();
            } else {
                //stopControllersTimer();

                //setCoverArtStatus(MediaUtils.getImageUrl(mSelectedMedia, 0));
            }
        } else {
            //stopControllersTimer();
            // setCoverArtStatus(MediaUtils.getImageUrl(mSelectedMedia, 0));
            //updateControllersVisibility(false);
        }
    }


    private void togglePlayback() {
        //stopControllersTimer();
        switch (mPlaybackState) {
            case PAUSED:
                switch (mLocation) {
                    case LOCAL:



                      /* mVideoView.start();
                        Log.d(TAG, "Playing locally...");
                        mPlaybackState = PlaybackState.PLAYING;
                        startControllersTimer();
                        restartTrickplayTimer();
                        updatePlaybackLocation(PlaybackLocation.LOCAL);*/
                        break;

                    case REMOTE:

                        loadRemoteMedia(0, true);

                        break;
                    default:
                        break;
                }
                break;

            case PLAYING:
                mPlaybackState = PlaybackState.PAUSED;

                // mVideoView.pause();
                break;

            case IDLE:
                switch (mLocation) {
                    case LOCAL:
                        //watchMovieButton.setText(getResources().getString(R.string.movie_details_cast_now_button_title));

                        // mPlayCircle.setVisibility(View.GONE);
                       /* mVideoView.setVideoURI(Uri.parse(mSelectedMedia.getContentId()));
                        mVideoView.seekTo(0);
                        mVideoView.start();
                        mPlaybackState = PlaybackState.PLAYING;
                        restartTrickplayTimer();
                        updatePlaybackLocation(PlaybackLocation.LOCAL);*/
                        break;
                    case REMOTE:
                        // mPlayCircle.setVisibility(View.VISIBLE);
                        if (mCastSession != null && mCastSession.isConnected()) {
                            // watchMovieButton.setText(getResources().getString(R.string.movie_details_cast_now_button_title));
                            loadRemoteMedia(0, true);


                            // Utils.showQueuePopup(this, mPlayCircle, mSelectedMedia);
                        }
                        else
                        {
                        }
                        break;
                    default:
                        break;
                }
                break;
            default:
                break;
        }
        updatePlayButton(mPlaybackState);
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

                Intent intent = new Intent(ShowWithEpisodesActivity.this, ExpandedControlsActivity.class);
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
        remoteMediaClient.setActiveMediaTracks(new long[1]).setResultCallback(new ResultCallback<RemoteMediaClient.MediaChannelResult>() {
            @Override
            public void onResult(@NonNull RemoteMediaClient.MediaChannelResult mediaChannelResult) {
                if (!mediaChannelResult.getStatus().isSuccess()) {
                    Log.v("SUBHA", "Failed with status code:" +
                            mediaChannelResult.getStatus().getStatusCode());
                }
            }
        });
        remoteMediaClient.load(mSelectedMedia, autoPlay, position);
    }

    /***************chromecast**********************/
    /***********Subtitle********/

    public void Download_SubTitle(String Url) {
        new DownloadFileFromURL().execute(Url);
    }


    class DownloadFileFromURL extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

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
                int lenghtOfFile = conection.getContentLength();

                // download the file
                InputStream input = new BufferedInputStream(url.openStream(), 8192);
                File root = Environment.getExternalStorageDirectory();
                mediaStorageDir = new File(root + "/Android/data/" + getApplicationContext().getPackageName().trim() + "/SubTitleList/", "");

                if (!mediaStorageDir.exists()) {
                    if (!mediaStorageDir.mkdirs()) {
                        Log.d("App", "failed to create directory");
                    }
                }

                SubTitlePath.add(mediaStorageDir.getAbsolutePath() + "/" + System.currentTimeMillis()+".vtt");
                OutputStream output = new FileOutputStream(mediaStorageDir.getAbsolutePath() + "/" + System.currentTimeMillis()+".vtt");

                byte data[] = new byte[1024];
                long total = 0;
                while ((count = input.read(data)) != -1) {
                    total += count;
                    publishProgress("" + (int) ((total * 100) / lenghtOfFile));
                    output.write(data, 0, count);
                }
                output.flush();
                output.close();
                input.close();

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }

            return null;
        }
        protected void onProgressUpdate(String... progress) {
        }

        @Override
        protected void onPostExecute(String file_url) {
            FakeSubTitlePath.remove(0);
            if(FakeSubTitlePath.size()>0)
            {
                Download_SubTitle(FakeSubTitlePath.get(0).trim());
            }
            else
            {
                if(progressBarHandler!=null && progressBarHandler.isShowing())
                {
                    progressBarHandler.hide();
                }
                Intent playVideoIntent = new Intent(ShowWithEpisodesActivity.this, ExoPlayerActivity.class);
                playVideoIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                playVideoIntent.putExtra("SubTitleName", SubTitleName);
                playVideoIntent.putExtra("SubTitlePath", SubTitlePath);
                playVideoIntent.putExtra("ResolutionFormat",ResolutionFormat);
                playVideoIntent.putExtra("ResolutionUrl",ResolutionUrl);
                startActivity(playVideoIntent);
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case 111: {

                if (grantResults.length > 0) {
                    if ((grantResults.length > 0) && (grantResults[0]) == PackageManager.PERMISSION_GRANTED) {
                        //Call whatever you want

                        if (Util.checkNetwork(ShowWithEpisodesActivity.this)) {
                             asynLoadMovieDetails = new AsynLoadMovieDetails();
                            asynLoadMovieDetails.executeOnExecutor(threadPoolExecutor);

                        } else {
                            Util.showToast(ShowWithEpisodesActivity.this,Util.getTextofLanguage(ShowWithEpisodesActivity.this,Util.NO_INTERNET_CONNECTION,Util.DEFAULT_NO_INTERNET_CONNECTION));

//                            Toast.makeText(getApplicationContext(), Util.getTextofLanguage(ShowWithEpisodesActivity.this, Util.NO_INTERNET_CONNECTION, Util.DEFAULT_NO_INTERNET_CONNECTION), Toast.LENGTH_LONG).show();
                            finish();
                        }

                    } else {
                        finish();
                    }
                } else {
                    finish();
                }

                return;
            }
        }
    }
    /*subtitles***********/




    //Added Later For Voucher



    public void GetVoucherPlan()
    {
        Log.v("BIBHU","get voucher plan called");

        AsynGetVoucherPlan getVoucherPlan = new AsynGetVoucherPlan();
        getVoucherPlan.executeOnExecutor(threadPoolExecutor);
    }

    private class AsynGetVoucherPlan extends AsyncTask<Void, Void, Void> {
        ProgressBarHandler pDialog;

        int status;
        String validUserStr;
        String userMessage;
        String responseStr;
        String loggedInIdStr;

        @Override
        protected Void doInBackground(Void... params) {

            if (pref != null) {
                loggedInIdStr = pref.getString("PREFS_LOGGEDIN_ID_KEY", null);
            }



            try {


                String urlRouteList = Util.rootUrl().trim()+Util.GetVoucherPlan.trim();
                HttpClient httpclient=new DefaultHttpClient();
                HttpPost httppost = new HttpPost(urlRouteList);
                httppost.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");
                httppost.addHeader("authToken", Util.authTokenStr.trim());
                httppost.addHeader("user_id", loggedInIdStr.trim());
                httppost.addHeader("movie_id", movieUniqueId.trim());
                httppost.addHeader("stream_id", Util.dataModel.getStreamUniqueId());
                httppost.addHeader("season", Util.dataModel.getSeason_id());

               /* String urlRouteList = "http://www.idogic.com/rest/GetVoucherPlan";
                HttpClient httpclient=new DefaultHttpClient();
                HttpPost httppost = new HttpPost(urlRouteList);
                httppost.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");
                httppost.addHeader("authToken","b7fbac553a14c99adcf079be6b48fd9f");
                httppost.addHeader("user_id","5146");
                httppost.addHeader("movie_id","5b24dfaf49a996b04ef92c272bde21f0");
                httppost.addHeader("stream_id","de4fcc9ffcc0b7d3ae1468765290685f");
                httppost.addHeader("season","1");*/



                // Execute HTTP Post Request
                try {
                    HttpResponse response = httpclient.execute(httppost);
                    responseStr = EntityUtils.toString(response.getEntity());

                    Log.v("BIBHU" , "Response Of the get voucher plan = "+responseStr);


                } catch (final org.apache.http.conn.ConnectTimeoutException e){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (pDialog != null && pDialog.isShowing()) {
                                pDialog.hide();
                                pDialog = null;
                            }
                            status = 0;
                            Toast.makeText(ShowWithEpisodesActivity.this, Util.getTextofLanguage(ShowWithEpisodesActivity.this,Util.SLOW_INTERNET_CONNECTION,Util.DEFAULT_SLOW_INTERNET_CONNECTION), Toast.LENGTH_LONG).show();
                        }
                    });

                } catch (IOException e) {
                    if (pDialog != null && pDialog.isShowing()) {
                        pDialog.hide();
                        pDialog = null;
                    }
                    status = 0;

                    e.printStackTrace();
                }
                if(responseStr!=null){
                    JSONObject myJson = new JSONObject(responseStr);
                    status = Integer.parseInt(myJson.optString("code"));
                }
                else
                {
                    status = 0;
                }
            }
            catch (Exception e) {
                if (pDialog != null && pDialog.isShowing()) {
                    pDialog.hide();
                    pDialog = null;
                }
                status = 0;
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
                status = 0;
            }


            if(status == 200)
            {

                String isShow="0",isSeason="0",isEpisode="0";

                try {
                    JSONObject jsonObject = new JSONObject(responseStr);

                    // Checking for Show Purchase Type
                    if(jsonObject.optString("is_show")!=null)
                    {
                        if(!(jsonObject.optString("is_show").equals("")) && !(jsonObject.optString("is_show").equals("null"))
                                && (jsonObject.optString("is_show").trim().equals("1")))
                        {
                            isShow = "1";
                        }
                        else
                        {
                            isShow = "0";
                        }
                    }
                    else
                    {
                        isShow = "0";
                    }

                    // Checking for Season Purchase Type

                    if(jsonObject.optString("is_season")!=null)
                    {
                        if(!(jsonObject.optString("is_season").equals("")) && !(jsonObject.optString("is_season").equals("null"))
                                && (jsonObject.optString("is_season").trim().equals("1")))
                        {
                            isSeason = "1";
                        }
                        else
                        {
                            isSeason = "0";
                        }
                    }
                    else
                    {
                        isSeason = "0";
                    }

                    // Checking for Episode Purchase Type

                    if(jsonObject.optString("is_episode")!=null)
                    {
                        if(!(jsonObject.optString("is_episode").equals("")) && !(jsonObject.optString("is_episode").equals("null"))
                                && (jsonObject.optString("is_episode").trim().equals("1")))
                        {
                            isEpisode = "1";
                        }
                        else
                        {
                            isEpisode = "0";
                        }
                    }
                    else
                    {
                        isEpisode = "0";
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if(isShow.equals("0") && isSeason.equals("0"))
                {
                    PurchageType = "episode";
                    ShowVoucherPopUp("  "+movieNameStr.trim() + " S" + Util.dataModel.getSeason_id().trim() + " E" + Util.dataModel.getEpisode_no() + " ");
                }
                else
                {
                    ShowVoucherPurchaseTypePopUp(isShow,isSeason,isEpisode);
                }

            }
            else
            {
                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(ShowWithEpisodesActivity.this, R.style.MyAlertDialogStyle);
                dlgAlert.setMessage(Util.getTextofLanguage(ShowWithEpisodesActivity.this,Util.NO_DETAILS_AVAILABLE,Util.DEFAULT_NO_DETAILS_AVAILABLE));
                dlgAlert.setTitle(Util.getTextofLanguage(ShowWithEpisodesActivity.this,Util.SORRY,Util.DEFAULT_SORRY));
                dlgAlert.setPositiveButton(Util.getTextofLanguage(ShowWithEpisodesActivity.this,Util.BUTTON_OK,Util.DEFAULT_BUTTON_OK), null);
                dlgAlert.setCancelable(false);
                dlgAlert.setPositiveButton(Util.getTextofLanguage(ShowWithEpisodesActivity.this,Util.BUTTON_OK,Util.DEFAULT_BUTTON_OK),
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
            pDialog = new ProgressBarHandler(ShowWithEpisodesActivity.this);
            pDialog.show();
        }
    }

    public void ShowVoucherPurchaseTypePopUp(String isShow,String isSeason,String isEpisode)
    {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ShowWithEpisodesActivity.this, R.style.MyAlertDialogStyle);
        LayoutInflater inflater = (LayoutInflater) ShowWithEpisodesActivity.this.getSystemService(ShowWithEpisodesActivity.this.LAYOUT_INFLATER_SERVICE);

        View convertView = (View) inflater.inflate(R.layout.voucher_plan_popup, null);
        alertDialog.setView(convertView);
        alertDialog.setTitle("");

        final RadioButton completeRadioButton = (RadioButton) convertView.findViewById(R.id.completeRadioButton);
        final RadioButton seasonRadioButton = (RadioButton) convertView.findViewById(R.id.seasonRadioButton);
        final RadioButton episodeRadioButton = (RadioButton) convertView.findViewById(R.id.episodeRadioButton);
        Button payNowButton = (Button) convertView.findViewById(R.id.payNowButton);
        TextView heading = (TextView) convertView.findViewById(R.id.heading);


        // Font implemented Here//

        Typeface typeface = Typeface.createFromAsset(getAssets(),getResources().getString(R.string.regular_fonts));
        completeRadioButton.setTypeface(typeface);
        seasonRadioButton.setTypeface(typeface);
        episodeRadioButton.setTypeface(typeface);
        payNowButton.setTypeface(typeface);
        heading.setTypeface(typeface);


        // ============end==============//

        // Language Implemented Here //

        completeRadioButton.setText("  " + movieNameStr.trim() +" "+ Util.getTextofLanguage(ShowWithEpisodesActivity.this,Util.COMPLETE_SEASON,Util.DEFAULT_COMPLETE_SEASON).toString().toLowerCase()+" ");
        seasonRadioButton.setText("  "+movieNameStr.trim() +" "+ Util.getTextofLanguage(ShowWithEpisodesActivity.this,Util.SEASON,Util.DEFAULT_SEASON).toString().toLowerCase()+" "+ Util.dataModel.getSeason_id().trim() + " ");
        episodeRadioButton.setText("  "+movieNameStr.trim() + " S" + Util.dataModel.getSeason_id().trim() + " E" + Util.dataModel.getEpisode_no() + " ");
        heading.setText(Util.getTextofLanguage(ShowWithEpisodesActivity.this,Util.SELECT_PURCHASE_TYPE,Util.DEFAULT_SELECT_PURCHASE_TYPE).toString().toLowerCase());
        payNowButton.setText(Util.getTextofLanguage(ShowWithEpisodesActivity.this,Util.BTN_NEXT,Util.DEFAULT_BTN_NEXT).toString().toLowerCase());


        //==============End===============//


        if (isEpisode.equals("1")) {
            episodeRadioButton.setVisibility(View.VISIBLE);
        } else {
            episodeRadioButton.setVisibility(View.GONE);
        }
        if (isSeason.equals("1")) {
            seasonRadioButton.setVisibility(View.VISIBLE);
        } else {
            seasonRadioButton.setVisibility(View.GONE);
        }
        if (isShow.equals("1")) {
            completeRadioButton.setVisibility(View.VISIBLE);
        } else {
            completeRadioButton.setVisibility(View.GONE);
        }

        voucher_alert = alertDialog.show();
        completeRadioButton.setChecked(true);

        // Changed later

        if (completeRadioButton.getVisibility() == View.VISIBLE) {

            completeRadioButton.setChecked(true);
            episodeRadioButton.setChecked(false);
            seasonRadioButton.setChecked(false);

            PurchageType = "show";
            ContentName  = completeRadioButton.getText().toString().trim();

        }
        else
        {
            if (seasonRadioButton.getVisibility() == View.VISIBLE) {

                completeRadioButton.setChecked(false);
                seasonRadioButton.setChecked(true);
                episodeRadioButton.setChecked(false);

                PurchageType = "season";
                ContentName  = seasonRadioButton.getText().toString().trim();

            }
            else
            {
                completeRadioButton.setChecked(false);
                seasonRadioButton.setChecked(false);
                episodeRadioButton.setChecked(true);

                PurchageType = "episode";
                ContentName  = episodeRadioButton.getText().toString().trim();

            }
        }

        ///////////////////////=====================////////////////////////


        episodeRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                completeRadioButton.setChecked(false);
                seasonRadioButton.setChecked(false);

                PurchageType = "episode";
                ContentName  = episodeRadioButton.getText().toString().trim();



            }
        });
        seasonRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                episodeRadioButton.setChecked(false);
                completeRadioButton.setChecked(false);

                PurchageType = "season";
                ContentName  = seasonRadioButton.getText().toString().trim();



            }
        });

        completeRadioButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                episodeRadioButton.setChecked(false);
                seasonRadioButton.setChecked(false);

                PurchageType = "show";
                ContentName  = completeRadioButton.getText().toString().trim();


            }

        });

        payNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                voucher_alert.dismiss();
                ShowVoucherPopUp(ContentName);
            }
        });

    }

    public void ShowVoucherPopUp(String ContentName)
    {

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(ShowWithEpisodesActivity.this, R.style.MyAlertDialogStyle);
        LayoutInflater inflater = (LayoutInflater) ShowWithEpisodesActivity.this.getSystemService(ShowWithEpisodesActivity.this.LAYOUT_INFLATER_SERVICE);

        View convertView = (View) inflater.inflate(R.layout.voucher_popup, null);
        alertDialog.setView(convertView);
        alertDialog.setTitle("");

        content_label = (TextView) convertView.findViewById(R.id.content_label);
        content_name = (TextView) convertView.findViewById(R.id.content_name);
        voucher_success = (TextView) convertView.findViewById(R.id.voucher_success);
        voucher_code = (EditText) convertView.findViewById(R.id.voucher_code);
        apply = (Button) convertView.findViewById(R.id.apply);
        watch_now = (Button) convertView.findViewById(R.id.watch_now);

        apply.setEnabled(true);
        apply.setBackgroundResource(R.drawable.button_radious);
        apply.setTextColor(getResources().getColor(R.color.pageTitleColor));


        // Font implemented Here//

        Typeface typeface = Typeface.createFromAsset(getAssets(),getResources().getString(R.string.regular_fonts));
        content_label.setTypeface(typeface);
        content_name.setTypeface(typeface);
        voucher_success.setTypeface(typeface);
        apply.setTypeface(typeface);
        watch_now.setTypeface(typeface);
        voucher_code.setTypeface(typeface);

        //==============end===============//

        // Language Implemented Here //

        content_label.setText(" "+ Util.getTextofLanguage(ShowWithEpisodesActivity.this,Util.PURCHASE,Util.DEFAULT_PURCHASE).toString().toLowerCase()+" : ");
        voucher_success.setText(" "+ Util.getTextofLanguage(ShowWithEpisodesActivity.this,Util.VOUCHER_SUCCESS,Util.DEFAULT_VOUCHER_SUCCESS).toString().toLowerCase()+" ");
        apply.setText(" "+ Util.getTextofLanguage(ShowWithEpisodesActivity.this,Util.BUTTON_APPLY,Util.DEFAULT_BUTTON_APPLY).toString().toLowerCase()+" ");
        watch_now.setText(" "+ Util.getTextofLanguage(ShowWithEpisodesActivity.this,Util.WATCH_NOW,Util.DEFAULT_WATCH_NOW).toString().toLowerCase()+" ");
        voucher_code.setHint(" "+ Util.getTextofLanguage(ShowWithEpisodesActivity.this,Util.VOUCHER_CODE,Util.DEFAULT_VOUCHER_CODE).toString().toLowerCase()+" ");


        //==============End===============//

        voucher_code.setText("");
        watch_now.setBackgroundResource(R.drawable.voucher_inactive_button);
        watch_now.setTextColor(Color.parseColor("#7f7f7f"));

        voucher_success.setVisibility(View.INVISIBLE);

        content_name.setText(ContentName);

        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                VoucherCode = voucher_code.getText().toString().trim();
                if(!VoucherCode.equals(""))
                {
                    voucher_alert.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                    ValidateVoucher_And_VoucherSubscription();
                }
                else
                {
                    Toast.makeText(getApplicationContext(),Util.getTextofLanguage(ShowWithEpisodesActivity.this,Util.VOUCHER_BLANK_MESSAGE,Util.DEFAULT_VOUCHER_BLANK_MESSAGE), Toast.LENGTH_SHORT).show();
                }
            }
        });

        watch_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(watch_status)
                {
                    voucher_alert.dismiss();

                    // Calling Voucher Subscription Api

                    AsynVoucherSubscription asynVoucherSubscription = new AsynVoucherSubscription();
                    asynVoucherSubscription.executeOnExecutor(threadPoolExecutor);

                }
            }
        });
        voucher_alert = alertDialog.show();
        voucher_alert.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

    }

    public void ValidateVoucher_And_VoucherSubscription()
    {
        // Calling Validate Voucher Api

        AsynValidateVoucher asynValidateVoucher = new AsynValidateVoucher();
        asynValidateVoucher.executeOnExecutor(threadPoolExecutor);
    }


    private class AsynValidateVoucher extends AsyncTask<Void, Void, Void> {
        ProgressDialog progressDialog1;

        int status;
        String responseStr;
        String loggedInIdStr;
        String message = "Invalid Voucher.";



        @Override
        protected Void doInBackground(Void... params) {

            if (pref != null) {
                loggedInIdStr = pref.getString("PREFS_LOGGEDIN_ID_KEY", null);
            }

            try {
                String urlRouteList = Util.rootUrl().trim()+Util.ValidateVoucher.trim();
                HttpClient httpclient=new DefaultHttpClient();
                HttpPost httppost = new HttpPost(urlRouteList);
                httppost.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");
                httppost.addHeader("authToken", Util.authTokenStr.trim());
                httppost.addHeader("user_id", loggedInIdStr.trim());
                httppost.addHeader("movie_id", movieUniqueId.trim());
                httppost.addHeader("stream_id", Util.dataModel.getStreamUniqueId());
                httppost.addHeader("season", Util.dataModel.getSeason_id());
                httppost.addHeader("voucher_code", VoucherCode);
                httppost.addHeader("purchase_type", "episode");

             /*   if(PurchageType.trim().equals("show")){
                    httppost.addHeader("purchase_type","show");
                }
                else {
                    httppost.addHeader("purchase_type","episode");
                }*/

                httppost.addHeader("lang_code",Util.getTextofLanguage(ShowWithEpisodesActivity.this,Util.SELECTED_LANGUAGE_CODE,Util.DEFAULT_SELECTED_LANGUAGE_CODE));

               /* String urlRouteList = "http://www.idogic.com/rest/ValidateVoucher";
                HttpClient httpclient=new DefaultHttpClient();
                HttpPost httppost = new HttpPost(urlRouteList);
                httppost.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");
                httppost.addHeader("authToken","b7fbac553a14c99adcf079be6b48fd9f");
                httppost.addHeader("user_id","5146");
                httppost.addHeader("movie_id","5b24dfaf49a996b04ef92c272bde21f0");
                httppost.addHeader("stream_id","de4fcc9ffcc0b7d3ae1468765290685f");
                httppost.addHeader("season","1");
                httppost.addHeader("voucher_code", VoucherCode);
               if(PurchageType.trim().equals("show")){
                    httppost.addHeader("purchase_type","show");
                }
                else {
                    httppost.addHeader("purchase_type","episode");
                }
*/


                // Execute HTTP Post Request
                try {
                    HttpResponse response = httpclient.execute(httppost);
                    responseStr = EntityUtils.toString(response.getEntity());

                    Log.v("BIBHU" , "Response Of validate voucher  = "+responseStr);


                } catch (final org.apache.http.conn.ConnectTimeoutException e){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (progressDialog1 != null && progressDialog1.isShowing()) {
                                progressDialog1.hide();
                                progressDialog1 = null;
                            }
                            status = 0;
                            Toast.makeText(ShowWithEpisodesActivity.this, Util.getTextofLanguage(ShowWithEpisodesActivity.this,Util.SLOW_INTERNET_CONNECTION,Util.DEFAULT_SLOW_INTERNET_CONNECTION), Toast.LENGTH_LONG).show();
                        }
                    });

                } catch (IOException e) {
                    if (progressDialog1 != null && progressDialog1.isShowing()) {
                        progressDialog1.hide();
                        progressDialog1 = null;
                    }
                    status = 0;

                    e.printStackTrace();
                }
                if(responseStr!=null){
                    JSONObject myJson = new JSONObject(responseStr);
                    status = Integer.parseInt(myJson.optString("code"));
                    message = myJson.optString("msg");
                }
                else
                {
                    status = 0;
                }
            }
            catch (Exception e) {
                if (progressDialog1 != null && progressDialog1.isShowing()) {
                    progressDialog1.hide();
                    progressDialog1 = null;
                }
                status = 0;
            }

            return null;
        }


        protected void onPostExecute(Void result) {
            try {
                if (progressDialog1 != null && progressDialog1.isShowing()) {
                    progressDialog1.hide();
                    progressDialog1 = null;
                }
            } catch (IllegalArgumentException ex) {
                status = 0;
            }

            if(status == 200)
            {

                voucher_success.setVisibility(View.VISIBLE);
                watch_now.setBackgroundResource(R.drawable.button_radious);
                watch_now.setTextColor(Color.parseColor("#ffffff"));
                watch_status = true;

                apply.setEnabled(false);
                apply.setBackgroundResource(R.drawable.voucher_inactive_button);
                apply.setTextColor(Color.parseColor("#7f7f7f"));

            }
            else
            {
                Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onPreExecute() {
            progressDialog1 = new ProgressDialog(ShowWithEpisodesActivity.this,R.style.MyTheme);
            progressDialog1.setCancelable(false);
            progressDialog1.setProgressStyle(android.R.style.Widget_ProgressBar_Large_Inverse);
            progressDialog1.setIndeterminate(false);
            progressDialog1.setIndeterminateDrawable(getResources().getDrawable(R.drawable.dialog_progress_rawable));
            progressDialog1.show();
        }
    }

    private class AsynVoucherSubscription extends AsyncTask<Void, Void, Void> {
        ProgressDialog progressDialog1;

        int status;
        String responseStr;
        String loggedInIdStr;
        String message = "";

        @Override
        protected Void doInBackground(Void... params) {

            if (pref != null) {
                loggedInIdStr = pref.getString("PREFS_LOGGEDIN_ID_KEY", null);
            }

            try {

                String urlRouteList = Util.rootUrl().trim()+Util.VoucherSubscription.trim();
                HttpClient httpclient=new DefaultHttpClient();
                HttpPost httppost = new HttpPost(urlRouteList);
                httppost.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");
                httppost.addHeader("authToken", Util.authTokenStr.trim());
                httppost.addHeader("user_id", loggedInIdStr.trim());
                httppost.addHeader("movie_id", movieUniqueId.trim());
                httppost.addHeader("stream_id", Util.dataModel.getStreamUniqueId());
                httppost.addHeader("season", Util.dataModel.getSeason_id());
                httppost.addHeader("voucher_code", VoucherCode);
                httppost.addHeader("lang_code",Util.getTextofLanguage(ShowWithEpisodesActivity.this,Util.SELECTED_LANGUAGE_CODE,Util.DEFAULT_SELECTED_LANGUAGE_CODE));
                httppost.addHeader("purchase_type", PurchageType);
               /* if(PurchageType.trim().equals("show")){
                    httppost.addHeader("purchase_type","show");
                }
                else {
                    httppost.addHeader("purchase_type","episode");
                }*/
                httppost.addHeader("is_preorder",""+Util.dataModel.getIsAPV());

               /* String urlRouteList = "http://www.idogic.com/rest/VoucherSubscription";
                HttpClient httpclient=new DefaultHttpClient();
                HttpPost httppost = new HttpPost(urlRouteList);
                httppost.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");
                httppost.addHeader("authToken","b7fbac553a14c99adcf079be6b48fd9f");
                httppost.addHeader("user_id","5146");
                httppost.addHeader("movie_id","5b24dfaf49a996b04ef92c272bde21f0");
                httppost.addHeader("stream_id","de4fcc9ffcc0b7d3ae1468765290685f");
                httppost.addHeader("season","de4fcc9ffcc0b7d3ae1468765290685f");
                httppost.addHeader("voucher_code", VoucherCode);
                if(PurchageType.trim().equals("show")){
                    httppost.addHeader("purchase_type","show");
                }
                else {
                    httppost.addHeader("purchase_type","episode");
                }
                httppost.addHeader("is_preorder",""+Util.dataModel.getIsAPV());*/



                // Execute HTTP Post Request
                try {
                    HttpResponse response = httpclient.execute(httppost);
                    responseStr = EntityUtils.toString(response.getEntity());

                    Log.v("BIBHU" , "Response Of VoucherSubscription  = "+responseStr);


                } catch (final org.apache.http.conn.ConnectTimeoutException e){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (progressDialog1 != null && progressDialog1.isShowing()) {
                                progressDialog1.hide();
                                progressDialog1 = null;
                            }
                            status = 0;
                            Toast.makeText(ShowWithEpisodesActivity.this, Util.getTextofLanguage(ShowWithEpisodesActivity.this,Util.SLOW_INTERNET_CONNECTION,Util.DEFAULT_SLOW_INTERNET_CONNECTION), Toast.LENGTH_LONG).show();
                        }
                    });

                } catch (IOException e) {
                    if (progressDialog1 != null && progressDialog1.isShowing()) {
                        progressDialog1.hide();
                        progressDialog1 = null;
                    }
                    status = 0;

                    e.printStackTrace();
                }
                if(responseStr!=null){
                    JSONObject myJson = new JSONObject(responseStr);
                    status = Integer.parseInt(myJson.optString("code"));
                    message = myJson.optString("msg");
                }
                else
                {
                    status = 0;
                }
            }
            catch (Exception e) {
                if (progressDialog1 != null && progressDialog1.isShowing()) {
                    progressDialog1.hide();
                    progressDialog1 = null;
                }
                status = 0;
            }

            return null;
        }


        protected void onPostExecute(Void result) {
            try {
                if (progressDialog1 != null && progressDialog1.isShowing()) {
                    progressDialog1.hide();
                    progressDialog1 = null;
                }
            } catch (IllegalArgumentException ex) {
                status = 0;
            }

            if(status == 200)
            {
               /* voucher_success.setVisibility(View.VISIBLE);
                watch_now.setBackgroundResource(R.drawable.button_radious);
                watch_now.setTextColor(Color.parseColor("#ffffff"));
                watch_status = true;

                apply.setEnabled(false);
                apply.setBackgroundResource(R.drawable.voucher_inactive_button);
                apply.setTextColor(Color.parseColor("#7f7f7f"));*/

                AsynLoadVideoUrls asynLoadVideoUrls = new AsynLoadVideoUrls();
                asynLoadVideoUrls.executeOnExecutor(threadPoolExecutor);
            }
            else
            {
                Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onPreExecute() {
            progressDialog1 = new ProgressDialog(ShowWithEpisodesActivity.this,R.style.MyTheme);
            progressDialog1.setCancelable(false);
            progressDialog1.setProgressStyle(android.R.style.Widget_ProgressBar_Large_Inverse);
            progressDialog1.setIndeterminate(false);
            progressDialog1.setIndeterminateDrawable(getResources().getDrawable(R.drawable.dialog_progress_rawable));
            progressDialog1.show();
        }
    }

    private class AsynFavoriteAdd extends AsyncTask<String, Void, Void> {


        String contName;
        JSONObject myJson = null;
        int status;
        ProgressBarHandler pDialog;
        String contMessage;
        String loggedInIdStr;
        String responseStr;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressBarHandler(ShowWithEpisodesActivity.this);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(String... params) {


            pref = getSharedPreferences(Util.LOGIN_PREF, 0);



            if (pref != null) {
                loggedInIdStr = pref.getString("PREFS_LOGGEDIN_ID_KEY", null);
            }
            String urlRouteList = Util.rootUrl().trim() + Util.AddtoFavlist.trim();

            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(urlRouteList);
                httppost.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");
                httppost.addHeader("authToken", Util.authTokenStr.trim());
                httppost.addHeader("movie_uniq_id", movieUniqueId);
                httppost.addHeader("content_type", isEpisode);
                httppost.addHeader("user_id", loggedInIdStr);
                Log.v("ANU","SHOWTOAST h......isepisode"+isEpisode);
                Log.v("ANU","Value......."+movieUniqueId);
                Log.v("ANU","login data"+loggedInIdStr.trim());


                try {
                    HttpResponse response = httpclient.execute(httppost);
                    responseStr = EntityUtils.toString(response.getEntity());


                } catch (org.apache.http.conn.ConnectTimeoutException e) {
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (responseStr != null) {
                try {
                    myJson = new JSONObject(responseStr);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                status = Integer.parseInt(myJson.optString("code"));
                sucessMsg = myJson.optString("msg");
//                statusmsg = myJson.optString("status");


            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {


            pref = getSharedPreferences(Util.LOGIN_PREF, 0);
            String loggedInStr = pref.getString("PREFS_LOGGEDIN_KEY", null);

            favorite_view_episode.setImageResource(R.drawable.favorite_red);
            isFavorite=1;

            showToast();
            if(pDialog.isShowing()&& pDialog!=null)
            {
                pDialog.hide();
            }

        }

    }

    public void showToast()
    {
        Log.v("ANU","SHOWTOASTisepisode"+isEpisode);
        Log.v("ANU","Value"+movieUniqueId);

        Context context = getApplicationContext();
        // Create layout inflator object to inflate toast.xml file
        LayoutInflater inflater = getLayoutInflater();

        // Call toast.xml file for toast layout
        View toastRoot = inflater.inflate(R.layout.custom_toast, null);
        TextView customToastMsg=(TextView) toastRoot.findViewById(R.id.toastMsg) ;
        customToastMsg.setText(sucessMsg);
        Toast toast = new Toast(context);

        // Set layout to toast
        toast.setView(toastRoot);
//        toast.setText("Added to Favorites");
        toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.show();

    }
    private class AsynFavoriteDelete extends AsyncTask<String, Void, Void> {


        String contName;
        JSONObject myJson = null;
        int status;
        ProgressBarHandler pDialog;
        String loggedInIdStr;

        String contMessage;
        String responseStr;

//    @Override
//    protected void onPreExecute() {
//        pDialog = new ProgressBarHandler(getActivity().getBaseContext());
//        pDialog.show();
//        Log.v("NIhar","onpreExecution");
//    }

        @Override
        protected Void doInBackground(String... params) {

            pref = getSharedPreferences(Util.LOGIN_PREF, 0);

            if (pref != null) {
                loggedInIdStr = pref.getString("PREFS_LOGGEDIN_ID_KEY", null);
            }

            String urlRouteList = Util.rootUrl().trim() + Util.DeleteFavList.trim();

            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(urlRouteList);
                httppost.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");
                httppost.addHeader("authToken", Util.authTokenStr.trim());
                httppost.addHeader("movie_uniq_id", movieUniqueId);
                httppost.addHeader("content_type", isEpisode);
                httppost.addHeader("user_id", loggedInIdStr);

                try {
                    HttpResponse response = httpclient.execute(httppost);
                    responseStr = EntityUtils.toString(response.getEntity());


                } catch (org.apache.http.conn.ConnectTimeoutException e) {

                }
            } catch (IOException e) {

                e.printStackTrace();
            }
            if (responseStr != null) {
                try {
                    myJson = new JSONObject(responseStr);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                status = Integer.parseInt(myJson.optString("code"));
                sucessMsg = myJson.optString("msg");
//                statusmsg = myJson.optString("status");


            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            favorite_view_episode.setImageResource(R.drawable.favorite_unselected);
            showToast();
            isFavorite = 0;
            if(pDialog.isShowing()&& pDialog!=null)
            {
                pDialog.hide();
            }






        }
        @Override
        protected void onPreExecute() {

            pDialog = new ProgressBarHandler(ShowWithEpisodesActivity.this);
            pDialog.show();

        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

       /* loginresultcode = requestCode;
        if (requestCode == 40500) {
            if (resultCode == RESULT_OK) {
                Util.favorite_clicked = false;
                Log.v("BKS", "ifclickedddddd");
//            call feb Api
                loggedInStr = pref.getString("PREFS_LOGGEDIN_ID_KEY", null);

//            AsynFavoriteAdd asynFavoriteAdd =new AsynFavoriteAdd();
//            asynFavoriteAdd.execute();
                AsynLoadMovieDetails asyncLoadVideos = new AsynLoadMovieDetails();
                asyncLoadVideos.executeOnExecutor(threadPoolExecutor);
            } else {
                Log.v("BKS", "elseclickedddddd");
                Util.favorite_clicked = false;
//            AsynFavoriteDelete asynFavoriteDelete=new AsynFavoriteDelete();
//            asynFavoriteDelete.execute();
            }

        }
//*/
        if(requestCode==30060 && requestCode == RESULT_OK)
        {

                if (Util.checkNetwork(ShowWithEpisodesActivity.this)) {

                    Log.v("SUBHA","CODE");
                    asynLoadMovieDetails = new AsynLoadMovieDetails();
                    asynLoadMovieDetails.executeOnExecutor(threadPoolExecutor);

                } else {
                    Toast.makeText(getApplicationContext(), Util.getTextofLanguage(ShowWithEpisodesActivity.this, Util.NO_INTERNET_CONNECTION, Util.DEFAULT_NO_INTERNET_CONNECTION), Toast.LENGTH_LONG).show();
                    finish();
                }
        }

    }


    private class AsynGetIpAddress extends AsyncTask<Void, Void, Void> {
        String responseStr;


        @Override
        protected Void doInBackground(Void... params) {

            try {

                // Execute HTTP Post Request
                try {
                    URL myurl = new URL(Util.loadIPUrl);
                    HttpsURLConnection con = (HttpsURLConnection) myurl.openConnection();
                    InputStream ins = con.getInputStream();
                    InputStreamReader isr = new InputStreamReader(ins);
                    BufferedReader in = new BufferedReader(isr);

                    String inputLine;

                    while ((inputLine = in.readLine()) != null) {
                        System.out.println(inputLine);
                        responseStr = inputLine;
                    }

                    in.close();


                } catch (org.apache.http.conn.ConnectTimeoutException e) {
                    ipAddressStr = "";

                } catch (UnsupportedEncodingException e) {

                    ipAddressStr = "";

                } catch (IOException e) {
                    ipAddressStr = "";

                }
                if (responseStr != null) {
                    Object json = new JSONTokener(responseStr).nextValue();
                    if (json instanceof JSONObject) {
                        ipAddressStr = ((JSONObject) json).getString("ip");

                    }
                }

            } catch (Exception e) {
                ipAddressStr = "";
            }
            return null;
        }


        protected void onPostExecute(Void result) {

            if (responseStr == null) {
                ipAddressStr = "";
            }
            return;
        }

        protected void onPreExecute() {

        }
    }
    public void GetReviewDetails()
    {

        asynGetReviewDetails = new AsynGetReviewDetails();
        asynGetReviewDetails.executeOnExecutor(threadPoolExecutor);

    }
    //Asyntask for getDetails of the csat and crew members.

    private class AsynGetReviewDetails extends AsyncTask<Void, Void, Void> {
        // ProgressBarHandler pDialog;
        String responseStr = "";
        int status;
        String msg;
        @Override
        protected Void doInBackground(Void... params) {

            try {


                HttpClient httpclient=new DefaultHttpClient();
                HttpPost httppost = new HttpPost(Util.rootUrl().trim()+Util.ViewContentRating.trim());
                httppost.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");
                httppost.addHeader("authToken", Util.authTokenStr.trim());
                httppost.addHeader("content_id",movieIdStr.trim());
                httppost.addHeader("user_id",loggedInStr);

                httppost.addHeader("lang_code", Util.getTextofLanguage(ShowWithEpisodesActivity.this, Util.SELECTED_LANGUAGE_CODE, Util.DEFAULT_SELECTED_LANGUAGE_CODE));


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
                    if ((myJson.has("showrating")) && myJson.optString("showrating").trim() != null && !myJson.optString("showrating").trim().isEmpty() && !myJson.optString("showrating").trim().equals("null") && !myJson.optString("showrating").trim().matches("")) {
                        ratingAddedByUser = Integer.parseInt(myJson.optString("showrating"));
                        Log.v("SUBHA","HFFH"+ratingAddedByUser);
                    }

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
                if (loadMovieDetailspDialog != null && loadMovieDetailspDialog.isShowing()) {
                    loadMovieDetailspDialog.hide();
                    loadMovieDetailspDialog = null;
                }

            }
            catch(IllegalArgumentException ex)
            {

                responseStr = "0";
            }
            if(responseStr == null) {
                responseStr = "0";
            }
            /****rating ******/
            if (isReviewThere == 0) {
                viewRatingTextView.setVisibility(View.GONE);

            } else {
                ratingBar.setVisibility(View.VISIBLE);
                ratingBar.setRating(Float.parseFloat(rating));
            }
            if (isRatingThere == 0) {
                ratingBar.setVisibility(View.GONE);
            } else {
                viewRatingTextView.setVisibility(View.VISIBLE);

                if (pref != null) {
                    String loggedInStr = pref.getString("PREFS_LOGGEDIN_KEY", null);
                    if (loggedInStr == null) {
                        viewRatingTextView.setText(Util.getTextofLanguage(ShowWithEpisodesActivity.this, Util.ADD_A_REVIEW, Util.DEFAULT_ADD_A_REVIEW));

                    } else {
                        if (ratingAddedByUser == 1) {
                            viewRatingTextView.setText(Util.getTextofLanguage(ShowWithEpisodesActivity.this, Util.ADD_A_REVIEW, Util.DEFAULT_ADD_A_REVIEW));

                        } else {
                            viewRatingTextView.setText("reviews (" + reviews + ")");
                            viewRatingTextView.setText(Util.getTextofLanguage(ShowWithEpisodesActivity.this, Util.REVIEWS, Util.DEFAULT_REVIEWS) + " (" + reviews + ") ");

                        }

                    }
                }else{
                    viewRatingTextView.setText(Util.getTextofLanguage(ShowWithEpisodesActivity.this, Util.ADD_A_REVIEW, Util.DEFAULT_ADD_A_REVIEW));

                }
            }

            /***favorite *****/

            if(loggedInStr != null && isFavorite== 0 && Util.favorite_clicked == true){

                Util.favorite_clicked = false;

                asynFavoriteAdd = new AsynFavoriteAdd();
                asynFavoriteAdd.executeOnExecutor(threadPoolExecutor);
            }
            else if (loggedInStr != null && isFavorite==1){

                favorite_view_episode.setImageResource(R.drawable.favorite_red);
            }
        }

        @Override
        protected void onPreExecute() {

//            loadMovieDetailspDialog = new ProgressBarHandler(ShowWithEpisodesActivity.this);
//            loadMovieDetailspDialog.show();


        }
    }

}
