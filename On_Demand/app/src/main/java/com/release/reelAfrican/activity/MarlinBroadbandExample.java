
package com.release.reelAfrican.activity;

import android.app.DownloadManager;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.text.Html;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.devbrackets.android.exomedia.listener.OnPreparedListener;
import com.devbrackets.android.exomedia.ui.widget.EMVideoView;
import com.release.reelAfrican.R;
import com.release.reelAfrican.subtitle_support.Caption;
import com.release.reelAfrican.subtitle_support.FormatSRT;
import com.release.reelAfrican.subtitle_support.FormatSRT_WithoutCaption;
import com.release.reelAfrican.subtitle_support.TimedTextObject;
import com.release.reelAfrican.utils.SensorOrientationChangeNotifier;
import com.release.reelAfrican.utils.Util;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HttpsURLConnection;

import static android.content.res.Configuration.SCREENLAYOUT_SIZE_LARGE;
import static android.content.res.Configuration.SCREENLAYOUT_SIZE_MASK;
import static android.content.res.Configuration.SCREENLAYOUT_SIZE_XLARGE;

/*
enum ContentTypes {
	DASH("application/dash+xml"), HLS("application/vnd.apple.mpegurl"), PDCF(
			"video/mp4"), M4F("video/mp4"), DCF("application/vnd.oma.drm.dcf"), BBTS(
			"video/mp2t");
	String mediaSourceParamsContentType = null;

	private ContentTypes(String mediaSourceParamsContentType) {
		this.mediaSourceParamsContentType = mediaSourceParamsContentType;
	}

	public String getMediaSourceParamsContentType() {
		return mediaSourceParamsContentType;
	}
}*/

public class MarlinBroadbandExample extends AppCompatActivity implements SensorOrientationChangeNotifier.Listener {
	private static long tot=0;
	int played_length = 0;
	int playerStartPosition = 0;
	boolean censor_layout = true;
	private static final int REQUEST_STORAGE = 1;
	Timer timer;
	static final String TAG = "SampleBBPlayer";
	private Handler threadHandler = new Handler();
	String videoLogId = "0";
	String watchStatus = "start";
	int playerPosition = 0;
	public boolean isFastForward = false;
	public int playerPreviousPosition = 0;
	TimerTask timerTask;
	String fname;
	int lenghtOfFile;
	int lengthfile;
	int seekBarProgress = 0;
	int id = 188;
	AsyncVideoLogDetails asyncVideoLogDetails;
	AsyncFFVideoLogDetails asyncFFVideoLogDetails;
	ImageView download;
	private static ProgressBar Progress;
	TextView percentg;
	Timer timerr;
	AsynGetIpAddress asynGetIpAddress;

	ImageButton back, center_play_pause;
	ImageView compress_expand;
	SeekBar seekBar;
	private Handler mHandler = new Handler();
	Timer center_pause_paly_timer;
	String Current_Time, TotalTime;
	TextView current_time, total_time;
	ProgressBar progressView;
	LinearLayout primary_ll, last_ll;
	boolean video_completed = false;
	// TextView detais_text;
	TextView ipAddressTextView;
	TextView emailAddressTextView;
	TextView dateTextView;
	long previous_matching_time = 0, current_matching_time = 0;
	boolean center_pause_paly_timer_is_running = false;
	RelativeLayout player_layout;
	RelativeLayout download_layout;
	NotificationManager mNotifyManager;
	NotificationCompat.Builder mBuilder;
	boolean compressed = true;
	int player_layout_height, player_layout_width;
	int screenWidth, screenHeight;
	ImageButton latest_center_play_pause;
	static String encrypt_file,poster,genre,release_date,watchtime,movieUniqueId,Movieurl,vedio_duration;
	String fileExtenstion;
	String resolution = "BEST";
	String genreTextView;
	String ipAddressStr = "";

	String  files, filename, path, movieid, duration, rdate;
	// load asynctask
	int corePoolSize = 60;
	int maximumPoolSize = 80;
	int keepAliveTime = 10;
	BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<Runnable>(maximumPoolSize);
	Executor threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, workQueue);
	SharedPreferences pref;
	//Toolbar mActionBarToolbar;
	LinearLayout linearLayout1;

	TextView videoTitle, GenreTextView, videoDurationTextView, videoCensorRatingTextView, videoCensorRatingTextView1, videoReleaseDateTextView,
			videoCastCrewTitleTextView;
	TextView videoStoryTextView;

	private EMVideoView emVideoView;
	int seek_label_pos = 0;
	int content_types_id = 0;

	String emailIdStr = "";
	String title = "";
	String userIdStr = "";
	String movieId = "";
	String episodeId = "0";
	String mlvfile="";
	String token="";
	String streamid;
	File mediaStorageDir,mediaStorageDir1;
	// Adder Later // By Bibhu


	private long enqueue;
	private DownloadManager downloadManager;
	BroadcastReceiver receiver;
	private boolean downloading;

    String contid,muviid,gen,vidduration;

	private SubtitleProcessingTask subsFetchTask;
	public TimedTextObject srt;
	TextView subtitleText;
	public Handler subtitleDisplayHandler;
	ImageView subtitle_change_btn;

	ArrayList<String> SubTitleName = new ArrayList<>();
	ArrayList<String>SubTitlePath = new ArrayList<>();
	boolean callWithoutCaption = true;

	@Override
	protected void onResume() {
		super.onResume();
		SensorOrientationChangeNotifier.getInstance(MarlinBroadbandExample.this).addListener(this);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_video_player);


		contid = getIntent().getStringExtra("contid").trim();
		muviid = getIntent().getStringExtra("muvid").trim();
		//movieid = getIntent().getStringExtra("url").trim();
		gen = getIntent().getStringExtra("gen").trim();
		title=getIntent().getStringExtra("Title").trim();
		vidduration = getIntent().getStringExtra("vid").trim();



		content_types_id = Integer.parseInt(contid);
//		played_length = Util.dataModel.getPlayPos() * 1000;

//		streamid=Util.dataModel.getStreamUniqueId().trim();
		genreTextView=gen;
//		if (Util.dataModel.getVideoUrl().matches("")) {
//			backCalled();
//			//onBackPressed();
//		}
		movieId = muviid;
		//episodeId = Util.dataModel.getEpisode_id();

		pref = getSharedPreferences(Util.LOGIN_PREF, 0);
		if (pref != null) {
			emailIdStr = pref.getString("PREFS_LOGIN_EMAIL_ID_KEY", null);
			userIdStr = pref.getString("PREFS_LOGGEDIN_ID_KEY", null);

		} else {
			emailIdStr = "";
			userIdStr = "";
		}

		path = getIntent().getStringExtra("FILE").trim();
		title = getIntent().getStringExtra("Title").trim();


		emVideoView = (EMVideoView) findViewById(R.id.emVideoView);
		subtitleText = (TextView) findViewById(R.id.offLine_subtitleText);
		subtitle_change_btn = (ImageView) findViewById(R.id.subtitle_change_btn);

		latest_center_play_pause = (ImageButton) findViewById(R.id.latest_center_play_pause);
		videoTitle = (TextView) findViewById(R.id.videoTitle);
		Typeface videoTitleface = Typeface.createFromAsset(getAssets(), getResources().getString(R.string.regular_fonts));
		videoTitle.setTypeface(videoTitleface);
		GenreTextView = (TextView) findViewById(R.id.GenreTextView);
		Typeface GenreTextViewface = Typeface.createFromAsset(getAssets(), getResources().getString(R.string.light_fonts));
		GenreTextView.setTypeface(GenreTextViewface);
		videoDurationTextView = (TextView) findViewById(R.id.videoDurationTextView);
		Typeface videoDurationTextViewface = Typeface.createFromAsset(getAssets(), getResources().getString(R.string.light_fonts));
		videoDurationTextView.setTypeface(videoDurationTextViewface);
		videoCensorRatingTextView = (TextView) findViewById(R.id.videoCensorRatingTextView);
		Typeface videoCensorRatingTextViewface = Typeface.createFromAsset(getAssets(), getResources().getString(R.string.light_fonts));
		videoCensorRatingTextView.setTypeface(videoCensorRatingTextViewface);
		videoCensorRatingTextView1 = (TextView) findViewById(R.id.videoCensorRatingTextView1);
		Typeface videoCensorRatingTextView1face = Typeface.createFromAsset(getAssets(), getResources().getString(R.string.light_fonts));
		videoCensorRatingTextView1.setTypeface(videoCensorRatingTextView1face);
		videoReleaseDateTextView = (TextView) findViewById(R.id.videoReleaseDateTextView);
		Typeface videoReleaseDateTextViewface = Typeface.createFromAsset(getAssets(), getResources().getString(R.string.light_fonts));
		videoReleaseDateTextView.setTypeface(videoReleaseDateTextViewface);
		videoStoryTextView = (TextView) findViewById(R.id.videoStoryTextView);
		Typeface videoStoryTextViewTypeface = Typeface.createFromAsset(getAssets(), getResources().getString(R.string.light_fonts));
		videoStoryTextView.setTypeface(videoStoryTextViewTypeface);
		videoCastCrewTitleTextView = (TextView) findViewById(R.id.videoCastCrewTitleTextView);

		Typeface watchTrailerButtonTypeface = Typeface.createFromAsset(getAssets(), getResources().getString(R.string.light_fonts));
		videoCastCrewTitleTextView.setTypeface(watchTrailerButtonTypeface);
		//videoCastCrewTitleTextView.setText(Util.getTextofLanguage(MarlinBroadbandExample.this, Util.CAST_CREW_BUTTON_TITLE, Util.DEFAULT_CAST_CREW_BUTTON_TITLE));
		videoCensorRatingTextView.setVisibility(View.GONE);
		videoCensorRatingTextView1.setVisibility(View.GONE);


		download= (ImageView) findViewById(R.id.downloadImageView);
		Progress= (ProgressBar) findViewById(R.id.progressBar);
		percentg= (TextView) findViewById(R.id.percentage);


		//Check for offline content // Added By sanjay

//		download_layout = (RelativeLayout) findViewById(R.id.downloadRelativeLayout);
//		if(Util.dataModel.getIsoffline().equals("1")){
//
//			download_layout.setVisibility(View.VISIBLE);
//
//		}

//        if(!Util.download_completed)
//        {
//            StartTimer();
//            mNotifyManager =
//                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//            mBuilder = new NotificationCompat.Builder(MarlinBroadbandExample.this);
//            mBuilder.setContentTitle("title")
//                    .setContentText("Download in progress")
//                    .setSmallIcon(R.mipmap.ic_launcher);
//        }
//
//
//
//        if (Util.downloadprogress >0){
//            Log.v("SUBHA","hahahaha");
//            percentg.setVisibility(View.VISIBLE);
//            download.setVisibility(View.GONE);
//            Progress.setVisibility(View.VISIBLE);
//            Progress.setProgress(Util.downloadprogress);
//            percentg.setText(Util.downloadprogress + "%");
//        }else if(Util.downloadprogress ==100){
//            download.setVisibility(View.VISIBLE);
//            percentg.setVisibility(View.GONE);
//            Progress.setVisibility(View.VISIBLE);
//
//        }



		//Call For Subtitle Loading // Added By Bibhu

		if (getIntent().getStringArrayListExtra("SubTitleName") != null) {
			SubTitleName = getIntent().getStringArrayListExtra("SubTitleName");
		} else {
			SubTitleName.clear();
		}

		if (getIntent().getStringArrayListExtra("SubTitlePath") != null) {
			SubTitlePath = getIntent().getStringArrayListExtra("SubTitlePath");
		} else {
			SubTitlePath.clear();
		}

		if(SubTitlePath.size()<1)
		{
			subtitle_change_btn.setVisibility(View.INVISIBLE);
		}

		subtitle_change_btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				try{
					Util.call_finish_at_onUserLeaveHint = false;
					Intent intent = new Intent(MarlinBroadbandExample.this,SubtitleList.class);
					intent.putExtra("SubTitleName",SubTitleName);
					intent.putExtra("SubTitlePath",SubTitlePath);
					startActivityForResult(intent,222);
				}catch (Exception e){}

			}
		});

		if (title != null && !title.matches(""))

		{
			videoTitle.setText(title);
			videoTitle.setVisibility(View.VISIBLE);
		} else {
			videoTitle.setVisibility(View.GONE);
		}


		if (gen != null && !gen.matches(""))

		{
			GenreTextView.setText(gen);
			GenreTextView.setVisibility(View.VISIBLE);
		} else {
			GenreTextView.setVisibility(View.GONE);
		}


		if (vidduration != null && !vidduration.matches(""))

		{
			videoDurationTextView.setText(vidduration);
			videoDurationTextView.setVisibility(View.VISIBLE);
			censor_layout = false;
		} else {
			videoDurationTextView.setVisibility(View.GONE);
		}
//		if (Util.dataModel.getCensorRating().trim() != null && !Util.dataModel.getCensorRating().trim().matches("")) {
//			if ((Util.dataModel.getCensorRating().trim()).contains("_")) {
//				String Data[] = (Util.dataModel.getCensorRating().trim()).split("-");
//				videoCensorRatingTextView.setVisibility(View.VISIBLE);
//				videoCensorRatingTextView1.setVisibility(View.VISIBLE);
//				videoCensorRatingTextView.setText(Data[0]);
//				videoCensorRatingTextView1.setText(Data[1]);
//				censor_layout = false;
//			} else {
//				censor_layout = false;
//				videoCensorRatingTextView.setVisibility(View.VISIBLE);
//				videoCensorRatingTextView1.setVisibility(View.GONE);
//				videoCensorRatingTextView.setText(Util.dataModel.getCensorRating().trim());
//			}
//		} else {
//
//			videoCensorRatingTextView.setVisibility(View.GONE);
//			videoCensorRatingTextView1.setVisibility(View.GONE);
//		}
//		if (Util.dataModel.getCensorRating().trim() != null && Util.dataModel.getCensorRating().trim().equalsIgnoreCase(Util.getTextofLanguage(MarlinBroadbandExample.this, Util.NO_DATA, Util.DEFAULT_NO_DATA))) {
//			videoCensorRatingTextView.setVisibility(View.GONE);
//			videoCensorRatingTextView1.setVisibility(View.GONE);
//		}
//
//		if (Util.dataModel.getVideoReleaseDate().trim() != null && !Util.dataModel.getVideoReleaseDate().trim().matches(""))
//
//		{
//			videoReleaseDateTextView.setText(Util.dataModel.getVideoReleaseDate().trim());
//			videoReleaseDateTextView.setVisibility(View.VISIBLE);
//			censor_layout = false;
//		} else {
//			videoReleaseDateTextView.setVisibility(View.GONE);
//		}

//		if(censor_layout) {
//
//			((LinearLayout) findViewById(R.id.durationratingLiearLayout)).setVisibility(View.GONE);
//		}
//		if (Util.dataModel.getVideoStory().trim() != null && !Util.dataModel.getVideoStory().trim().matches(""))
//
//		{
//			story.setText(Util.dataModel.getVideoStory());
//			story.setVisibility(View.VISIBLE);
//		} else {
//			story.setVisibility(View.GONE);
//		}
//
//		if (Util.dataModel.isCastCrew() == true)
//
//		{
//			videoCastCrewTitleTextView.setVisibility(View.VISIBLE);
//		} else {
//			videoCastCrewTitleTextView.setVisibility(View.GONE);
//		}


		videoCastCrewTitleTextView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (Util.checkNetwork(MarlinBroadbandExample.this)) {
					//Will Add Some Data to send
					Util.call_finish_at_onUserLeaveHint = false;
					Util.hide_pause = true;
					((ProgressBar) findViewById(R.id.progress_view)).setVisibility(View.GONE);
					latest_center_play_pause.setVisibility(View.VISIBLE);

					if (emVideoView.isPlaying()) {
						emVideoView.pause();
						latest_center_play_pause.setImageResource(R.drawable.center_ic_media_play);
						center_play_pause.setImageResource(R.drawable.ic_media_play);
						mHandler.removeCallbacks(updateTimeTask);
					}


					if (center_pause_paly_timer_is_running) {
						center_pause_paly_timer.cancel();
						center_pause_paly_timer_is_running = false;
						Log.v("BIBHU11","CastAndCrewActivity End_Timer cancel called");


						subtitle_change_btn.setVisibility(View.INVISIBLE);
						primary_ll.setVisibility(View.GONE);
						last_ll.setVisibility(View.GONE);
						center_play_pause.setVisibility(View.GONE);
						current_time.setVisibility(View.GONE);
					}


					final Intent detailsIntent = new Intent(MarlinBroadbandExample.this, CastAndCrewActivity.class);
					detailsIntent.putExtra("cast_movie_id", movieId.trim());
					detailsIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
					startActivity(detailsIntent);
				} else {
					Toast.makeText(getApplicationContext(), Util.getTextofLanguage(MarlinBroadbandExample.this, Util.NO_INTERNET_CONNECTION, Util.DEFAULT_NO_INTERNET_CONNECTION), Toast.LENGTH_LONG).show();
				}
			}
		});


		player_layout = (RelativeLayout) findViewById(R.id.player_layout);
		player_layout_height = player_layout.getHeight();
		player_layout_width = player_layout.getWidth();

		primary_ll = (LinearLayout) findViewById(R.id.primary_ll);
		last_ll = (LinearLayout) findViewById(R.id.last_ll);
		last_ll = (LinearLayout) findViewById(R.id.last_ll);
		linearLayout1 = (LinearLayout) findViewById(R.id.linearLayout1);

		ipAddressTextView = (TextView) findViewById(R.id.emailAddressTextView);
		emailAddressTextView = (TextView) findViewById(R.id.ipAddressTextView);
		dateTextView = (TextView) findViewById(R.id.dateTextView);

		ipAddressTextView.setVisibility(View.GONE);
		emailAddressTextView.setVisibility(View.GONE);
		dateTextView.setVisibility(View.GONE);

		compress_expand = (ImageView) findViewById(R.id.compress_expand);
		back = (ImageButton) findViewById(R.id.back);
       /* back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MarlinBroadbandExample.this, "test", Toast.LENGTH_SHORT).show();
            }
        });*/
		//   back.setOnClickListener(MarlinBroadbandExample.this);
      /*  back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("SUBHA","CHFHFH");
               // onBackPressed();
                backCalled();
            }
        });*/
		// pause_play = (ImageButton) findViewById(R.id.pause_play);
		seekBar = (SeekBar) findViewById(R.id.progress);
		center_play_pause = (ImageButton) findViewById(R.id.center_play_pause);

		current_time = (TextView) findViewById(R.id.current_time);
		total_time = (TextView) findViewById(R.id.total_time);
		progressView = (ProgressBar) findViewById(R.id.progress_view);


		Display display = getWindowManager().getDefaultDisplay();
		screenWidth = display.getWidth();
		screenHeight = display.getHeight();


		Util.player_description = true;


		LinearLayout.LayoutParams params1 = null;
		if (((getResources().getConfiguration().screenLayout & SCREENLAYOUT_SIZE_MASK) == SCREENLAYOUT_SIZE_LARGE) || ((getResources().getConfiguration().screenLayout & SCREENLAYOUT_SIZE_MASK) == SCREENLAYOUT_SIZE_XLARGE)){
			if(MarlinBroadbandExample.this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
			{
				params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,(screenHeight*45)/100);
				// showSystemUI();

			}
			else
			{
				//   showSystemUI();
				params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,(screenHeight*45)/100);
			}
		}
		else
		{
			if(MarlinBroadbandExample.this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
			{
				params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,(screenHeight*40)/100);
				// showSystemUI();
			}
			else
			{
				//showSystemUI();
				params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,(screenHeight*40)/100);
			}
		}
		player_layout.setLayoutParams(params1);

		if (content_types_id == 4) {
			seekBar.setEnabled(false);
			seekBar.setProgress(0);
		} else {
			seekBar.setEnabled(true);
			seekBar.setProgress(0);
		}

		seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
				//Toast.makeText(getApplicationContext(),""+seekBar.getProgress(),Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				mHandler.removeCallbacks(updateTimeTask);
				playerStartPosition = emVideoView.getCurrentPosition();

			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				mHandler.removeCallbacks(updateTimeTask);
				emVideoView.seekTo(seekBar.getProgress());
				current_time.setVisibility(View.VISIBLE);
				current_time.setVisibility(View.GONE);
				showCurrentTime();
				current_time.setVisibility(View.VISIBLE);
				updateProgressBar();
				if (playerPreviousPosition == 0) {
					if (playerStartPosition < emVideoView.getCurrentPosition()) {
						isFastForward = true;
						playerPreviousPosition = playerStartPosition;

					} else {
						playerPreviousPosition = playerStartPosition;
						isFastForward = false;

					}
				}
			}
		});

		seekBar.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent motionEvent) {
				if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
					Instant_End_Timer();

				} else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
					Start_Timer();
				}
				return false;
			}
		});

		emVideoView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				if (Util.hide_pause) {
					Util.hide_pause = false;
				}
				if (((ProgressBar) findViewById(R.id.progress_view)).getVisibility() == View.VISIBLE) {
					primary_ll.setVisibility(View.VISIBLE);
					center_play_pause.setVisibility(View.GONE);
					latest_center_play_pause.setVisibility(View.GONE);
					current_time.setVisibility(View.GONE);
					subtitle_change_btn.setVisibility(View.INVISIBLE);


				} else {
					if (primary_ll.getVisibility() == View.VISIBLE) {
						primary_ll.setVisibility(View.GONE);
						last_ll.setVisibility(View.GONE);
						center_play_pause.setVisibility(View.GONE);
						latest_center_play_pause.setVisibility(View.GONE);
						current_time.setVisibility(View.GONE);
						subtitle_change_btn.setVisibility(View.INVISIBLE);

						End_Timer();
					} else {


						primary_ll.setVisibility(View.VISIBLE);

						if (SubTitlePath.size() > 0) {
							subtitle_change_btn.setVisibility(View.VISIBLE);
						}

						last_ll.setVisibility(View.VISIBLE);
						center_play_pause.setVisibility(View.VISIBLE);
						latest_center_play_pause.setVisibility(View.VISIBLE);
						current_time.setVisibility(View.VISIBLE);
						current_time.setVisibility(View.GONE);
						showCurrentTime();
						current_time.setVisibility(View.VISIBLE);
						Start_Timer();
					}

				}


			}
		});

		compress_expand.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				//Write code here

				if (compressed) {
					compressed = false;
					LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
							LinearLayout.LayoutParams.MATCH_PARENT,
							LinearLayout.LayoutParams.MATCH_PARENT);
					player_layout.setLayoutParams(params);
					compress_expand.setImageResource(R.drawable.ic_media_fullscreen_shrink);
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
						}
					});
					Util.player_description = false;
					Util.landscape = true;
					hideSystemUI();
					setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
				} else {

					Util.player_description = true;
					LinearLayout.LayoutParams params1 = null;
					if (((getResources().getConfiguration().screenLayout & SCREENLAYOUT_SIZE_MASK) == SCREENLAYOUT_SIZE_LARGE) || ((getResources().getConfiguration().screenLayout & SCREENLAYOUT_SIZE_MASK) == SCREENLAYOUT_SIZE_XLARGE)){
						if(MarlinBroadbandExample.this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
						{
							params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,(screenHeight*45)/100);

						}
						else
						{
							params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,(screenHeight*45)/100);
						}
					}
					else
					{
						if(MarlinBroadbandExample.this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
						{
							params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,(screenHeight*40)/100);

						}
						else
						{
							params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,(screenHeight*40)/100);
						}
					}
					player_layout.setLayoutParams(params1);
					compressed = true;
					compress_expand.setImageResource(R.drawable.ic_media_fullscreen_stretch);
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
						}
					});
					setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
					showSystemUI();


				}


			}
		});


		center_play_pause.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Execute_Pause_Play();
			}
		});
		latest_center_play_pause.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				if (Util.hide_pause) {
					Util.hide_pause = false;
					latest_center_play_pause.setVisibility(View.GONE);
				}

				Execute_Pause_Play();
			}
		});

       /* back.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    back.setImageResource(R.drawable.ic_back);
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                }
                return false;
            }
        });*/

		emVideoView.setOnPreparedListener(new OnPreparedListener() {
			@Override
			public void onPrepared() {



				video_completed = false;
				if (progressView != null) {
					((ProgressBar) findViewById(R.id.progress_view)).setVisibility(View.VISIBLE);
					center_play_pause.setVisibility(View.GONE);
					latest_center_play_pause.setVisibility(View.GONE);
				}


				try {
                  /*  if (emailIdStr != null && !emailIdStr.equalsIgnoreCase("")) {
                        emailAddressTextView.setVisibility(View.VISIBLE);
                        emailAddressTextView.setText(emailIdStr);
                    } else {
                        emailAddressTextView.setVisibility(View.GONE);
                    }
                    if (ipAddressStr!=null){
                        ipAddressTextView.setVisibility(View.VISIBLE);
                        ipAddressTextView.setText(ipAddressStr);
                    }else{
                        ipAddressTextView.setVisibility(View.GONE);
                    }
                    String date = new SimpleDateFormat("MMMM dd , yyyy").format(new Date());
                    if (date != null && !date.equalsIgnoreCase("")) {
                        dateTextView.setVisibility(View.VISIBLE);
                        dateTextView.setText(date);
                    } else {
                        dateTextView.setVisibility(View.GONE);
                    }*/

					//video log
					if (content_types_id == 4) {

						if(SubTitlePath.size()>0)
						{
							CheckSubTitleParsingType("1");
							subtitleDisplayHandler = new Handler();
							subsFetchTask = new SubtitleProcessingTask("1");
							subsFetchTask.execute();
						}
						else {
							asyncVideoLogDetails = new AsyncVideoLogDetails();
							asyncVideoLogDetails.executeOnExecutor(threadPoolExecutor);
						}

						emVideoView.start();
						updateProgressBar();
					} else {
						startTimer();

						if (played_length > 0) {
							((ProgressBar) findViewById(R.id.progress_view)).setVisibility(View.GONE);
							Util.call_finish_at_onUserLeaveHint = false;

							Intent resumeIntent = new Intent(MarlinBroadbandExample.this, ResumePopupActivity.class);
							startActivityForResult(resumeIntent, 1001);
						} else {

							emVideoView.start();
							seekBar.setProgress(emVideoView.getCurrentPosition());
							updateProgressBar();

							if(SubTitlePath.size()>0)
							{


								Log.v("BIBHU3","parsing called============");

								CheckSubTitleParsingType("1");
								subtitleDisplayHandler = new Handler();
								subsFetchTask = new SubtitleProcessingTask("1");
								subsFetchTask.execute();
							}
							else {
								asyncVideoLogDetails = new AsyncVideoLogDetails();
								asyncVideoLogDetails.executeOnExecutor(threadPoolExecutor);
							}


						}
                      /*  emVideoView.start();
                        seekBar.setProgress(emVideoView.getCurrentPosition());
                        updateProgressBar();*/

                       /* if (played_length > 0) {
                            emVideoView.seekTo(played_length);
                            seekBar.setProgress(played_length);
                        }else {
                            seekBar.setProgress(emVideoView.getCurrentPosition());
                        }
                        updateProgressBar();*/
					}
				} catch (Exception e) {
				}
			}
		});

		back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				backCalled();
               /* Toast.makeText(MarlinBroadbandExample.this, "test", Toast.LENGTH_SHORT).show();
                mHandler.removeCallbacks(updateTimeTask);
                emVideoView.release();
                finish();*/
			}
		});



		  emVideoView.setVideoURI(Uri.parse(path));


//		receiver = new BroadcastReceiver() {
//			@Override
//			public void onReceive(Context context, Intent intent) {
//				String action = intent.getAction();
//				if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
//					enqueue=intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
//					DownloadManager.Query query = new DownloadManager.Query();
//					query.setFilterById(enqueue);
//					Cursor cursor = downloadManager.query(query);
//					if (cursor.moveToFirst()) {
//						int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
//						if (DownloadManager.STATUS_SUCCESSFUL == cursor.getInt(columnIndex)) {
////                            ImageView view = (ImageView) findViewById(R.id.imageView);
////                            String uriString = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
////                            view.setImageURI(Uri.parse(uriString));
//							Toast.makeText(MarlinBroadbandExample.this, "Download completed!", Toast.LENGTH_SHORT).show();
//						}
//					}
//				}else if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
//					//IN THIS SECTION YOU CAN WRITE YOUR LOGIC TO CANCEL DOWNLOAD AS STATED IN ABOVE ANSWER
////                    downloadManager.remove(enqueue);
//					Toast.makeText(getApplicationContext(), "Download competed!", Toast.LENGTH_SHORT).show();
//
//				}
//			}
//		};

//		registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_NOTIFICATION_CLICKED));


	}


	private String getActionTokenFromStorage(String tokenFileName) {
		String token = null;
		byte[] readBuffer = new byte[1024];
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		InputStream is = null;
		int bytesRead = 0;

		try {
			is = new FileInputStream(tokenFileName);
				/*is = getActivity().getAssets()
						.open(tokenFileName, MODE_PRIVATE);*/
			while ((bytesRead = is.read(readBuffer)) != -1) {
				baos.write(readBuffer, 0, bytesRead);
			}
			baos.close();
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		token = new String(baos.toByteArray());
		return token;
	}

	private class AsyncVideoLogDetails extends AsyncTask<Void, Void, Void> {
		//  ProgressDialog pDialog;
		String responseStr;
		int statusCode = 0;

		@Override
		protected Void doInBackground(Void... params) {

			String urlRouteList = Util.rootUrl().trim() + Util.videoLogUrl.trim();
			try {
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(urlRouteList);
				httppost.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");
				httppost.addHeader("authToken", Util.authTokenStr.trim());
				httppost.addHeader("user_id", userIdStr.trim());
				httppost.addHeader("ip_address", ipAddressStr.trim());
				httppost.addHeader("movie_id", movieId.trim());
				httppost.addHeader("episode_id", episodeId.trim());
				httppost.addHeader("played_length", String.valueOf(playerPosition));
				httppost.addHeader("watch_status", watchStatus);
				httppost.addHeader("device_type", "2");
				httppost.addHeader("log_id", videoLogId);


				// Execute HTTP Post Request
				try {
					HttpResponse response = httpclient.execute(httppost);
					responseStr = EntityUtils.toString(response.getEntity());


				} catch (org.apache.http.conn.ConnectTimeoutException e) {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							videoLogId = "0";

						}

					});

				} catch (Exception e) {
					videoLogId = "0";
					e.printStackTrace();

				}
				if (responseStr != null) {
					JSONObject myJson = new JSONObject(responseStr);
					statusCode = Integer.parseInt(myJson.optString("code"));
					if (statusCode == 200) {
						videoLogId = myJson.optString("log_id");
					} else {
						videoLogId = "0";
					}

				}

			} catch (Exception e) {
				videoLogId = "0";

			}

			return null;
		}


		protected void onPostExecute(Void result) {
         /*   try {
                if (pDialog.isShowing())
                    pDialog.dismiss();
            } catch (IllegalArgumentException ex) {
                videoLogId = "0";
            }*/
			if (responseStr == null) {
				videoLogId = "0";

			}
			startTimer();
			return;


		}

		@Override
		protected void onPreExecute() {
			stoptimertask();
		}


	}


	public void startTimer() {
		//set a new Timer
		timer = new Timer();

		initializeTimerTask();
		timer.schedule(timerTask, 1000, 1000); //
	}

	public void stoptimertask() {
		//stop the timer, if it's not already null
		if (timer != null) {
			timer.cancel();
			timer = null;
		}

	}

	public void initializeTimerTask() {

		timerTask = new TimerTask() {
			public void run() {

				//use a handler to run a toast that shows the current timestamp
				threadHandler.post(new Runnable() {
					public void run() {
						if (emVideoView != null) {
							int currentPositionStr = millisecondsToString(emVideoView.getCurrentPosition());
							playerPosition = currentPositionStr;


							if (isFastForward == true) {
								isFastForward = false;


								int duration = emVideoView.getDuration() / 1000;
								if (currentPositionStr > 0 && currentPositionStr == duration) {
									asyncFFVideoLogDetails = new AsyncFFVideoLogDetails();
									watchStatus = "complete";
									asyncFFVideoLogDetails.executeOnExecutor(threadPoolExecutor);
								} else {
									asyncFFVideoLogDetails = new AsyncFFVideoLogDetails();
									watchStatus = "halfplay";
									asyncFFVideoLogDetails.executeOnExecutor(threadPoolExecutor);
								}

							} else if (isFastForward == false && currentPositionStr >= millisecondsToString(playerPreviousPosition)) {

								playerPreviousPosition = 0;

								int duration = emVideoView.getDuration() / 1000;
								if (currentPositionStr > 0 && currentPositionStr == duration) {
									asyncVideoLogDetails = new AsyncVideoLogDetails();
									watchStatus = "complete";
									asyncVideoLogDetails.executeOnExecutor(threadPoolExecutor);
								} else if (currentPositionStr > 0 && currentPositionStr % 60 == 0) {
									asyncVideoLogDetails = new AsyncVideoLogDetails();
									watchStatus = "halfplay";
									asyncVideoLogDetails.executeOnExecutor(threadPoolExecutor);

								}
							}
						}
						//get the current timeStamp
					}
				});
			}
		};
	}

	private class AsyncFFVideoLogDetails extends AsyncTask<Void, Void, Void> {
		String responseStr;
		int statusCode = 0;

		@Override
		protected Void doInBackground(Void... params) {

			String urlRouteList = Util.rootUrl().trim() + Util.videoLogUrl.trim();
			try {
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(urlRouteList);
				httppost.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");
				httppost.addHeader("authToken", Util.authTokenStr.trim());
				httppost.addHeader("user_id", userIdStr);
				httppost.addHeader("ip_address", ipAddressStr.trim());
				httppost.addHeader("movie_id", movieId.trim());
				httppost.addHeader("episode_id", episodeId.trim());

				httppost.addHeader("played_length", String.valueOf(playerPosition));
				httppost.addHeader("watch_status", watchStatus);

				httppost.addHeader("device_type", "2");
				httppost.addHeader("log_id", videoLogId);

				// Execute HTTP Post Request
				try {
					HttpResponse response = httpclient.execute(httppost);
					responseStr = EntityUtils.toString(response.getEntity());
				} catch (org.apache.http.conn.ConnectTimeoutException e) {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							videoLogId = "0";

						}

					});

				} catch (IOException e) {
					videoLogId = "0";

					e.printStackTrace();
				}
				if (responseStr != null) {
					JSONObject myJson = new JSONObject(responseStr);
					statusCode = Integer.parseInt(myJson.optString("code"));
					if (statusCode == 200) {
						videoLogId = myJson.optString("log_id");
					} else {
						videoLogId = "0";
					}

				}

			} catch (Exception e) {
				videoLogId = "0";

			}

			return null;
		}


		protected void onPostExecute(Void result) {
			if (responseStr == null) {
				videoLogId = "0";

			}
			startTimer();
			return;

		}

		@Override
		protected void onPreExecute() {
			// updateSeekBarThread.stop();
			stoptimertask();

		}


	}

	private int millisecondsToString(int milliseconds) {
		// int seconds = (int) (milliseconds / 1000) % 60 ;
		int seconds = (int) (milliseconds / 1000);

		return seconds;
	}

	@Override
	public void onOrientationChange(int orientation) {


		if (orientation == 90) {

			Util.player_description = false;
			Util.landscape = false;

			compressed = false;
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.MATCH_PARENT);
			player_layout.setLayoutParams(params);
			compress_expand.setImageResource(R.drawable.ic_media_fullscreen_shrink);
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
				}
			});
			hideSystemUI();
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
			//current_time.setVisibility(View.GONE);
		} else if (orientation == 270) {
			Util.player_description = false;
			Util.landscape = true;

			compressed = false;
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.MATCH_PARENT);
			player_layout.setLayoutParams(params);
			compress_expand.setImageResource(R.drawable.ic_media_fullscreen_shrink);
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
				}
			});
			hideSystemUI();

			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			//current_time.setVisibility(View.GONE);

			// Do some landscape stuff
		} else if (orientation == 180) {

			Util.player_description = true;

			LinearLayout.LayoutParams params1 = null;
			if (((getResources().getConfiguration().screenLayout & SCREENLAYOUT_SIZE_MASK) == SCREENLAYOUT_SIZE_LARGE) || ((getResources().getConfiguration().screenLayout & SCREENLAYOUT_SIZE_MASK) == SCREENLAYOUT_SIZE_XLARGE)){
				if(MarlinBroadbandExample.this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
				{
					params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,(screenHeight*45)/100);

				}
				else
				{
					params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,(screenHeight*45)/100);
				}
			}
			else
			{
				if(MarlinBroadbandExample.this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
				{
					params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,(screenHeight*40)/100);

				}
				else
				{
					params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,(screenHeight*40)/100);
				}
			}
			player_layout.setLayoutParams(params1);
			compressed = true;
			compress_expand.setImageResource(R.drawable.ic_media_fullscreen_stretch);
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
				}
			});
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			showSystemUI();

			//current_time.setVisibility(View.GONE);

		} else if (orientation == 0) {

			Util.player_description = true;
			LinearLayout.LayoutParams params1 = null;
			if (((getResources().getConfiguration().screenLayout & SCREENLAYOUT_SIZE_MASK) == SCREENLAYOUT_SIZE_LARGE) || ((getResources().getConfiguration().screenLayout & SCREENLAYOUT_SIZE_MASK) == SCREENLAYOUT_SIZE_XLARGE)){
				if(MarlinBroadbandExample.this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
				{
					params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,(screenHeight*45)/100);

				}
				else
				{
					params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,(screenHeight*45)/100);
				}
			}
			else
			{
				if(MarlinBroadbandExample.this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
				{
					params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,(screenHeight*40)/100);

				}
				else
				{
					params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,(screenHeight*40)/100);
				}
			}
			player_layout.setLayoutParams(params1);
			compressed = true;
			compress_expand.setImageResource(R.drawable.ic_media_fullscreen_stretch);
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
				}
			});
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			showSystemUI();

			//current_time.setVisibility(View.GONE);
		}

		current_time_position_timer();

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


	private void updateProgressBar() {
		mHandler.postDelayed(updateTimeTask, 1000);
	}

	private Runnable updateTimeTask = new Runnable() {
		public void run() {
          /*  if (played_length > 0) {
                emVideoView.seekTo(34000);
                seekBar.setProgress(34000);
            }else {*/
			seekBar.setProgress(emVideoView.getCurrentPosition());
			seekBarProgress = emVideoView.getCurrentPosition();
//            }
			seekBar.setMax(emVideoView.getDuration());
			Calcute_Currenttime_With_TotalTime();
			mHandler.postDelayed(this, 1000);

			if (content_types_id != 4) {
				showCurrentTime();
			}

			current_matching_time = emVideoView.getCurrentPosition();


			if ((previous_matching_time == current_matching_time) && (current_matching_time < emVideoView.getDuration())) {
				((ProgressBar) findViewById(R.id.progress_view)).setVisibility(View.VISIBLE);

				primary_ll.setVisibility(View.GONE);
				last_ll.setVisibility(View.GONE);
				center_play_pause.setVisibility(View.GONE);
				latest_center_play_pause.setVisibility(View.GONE);
				current_time.setVisibility(View.GONE);
				subtitle_change_btn.setVisibility(View.INVISIBLE);

				previous_matching_time = current_matching_time;
			} else {

				if (content_types_id == 4) {

				} else {
					if (current_matching_time >= emVideoView.getDuration()) {
						mHandler.removeCallbacks(updateTimeTask);
						//  pause_play.setImageResource(R.drawable.ic_media_play);
//                    emVideoView.release();
//                    emVideoView.reset();
						seekBar.setProgress(0);
//                    emVideoView.seekTo(0);
						current_time.setText("00:00:00");
						total_time.setText("00:00:00");
						previous_matching_time = 0;
						current_matching_time = 0;
						video_completed = true;
						//onBackPressed();
						backCalled();
					}
				}


				previous_matching_time = current_matching_time;
				((ProgressBar) findViewById(R.id.progress_view)).setVisibility(View.GONE);
			}

		}
	};

	public void Calcute_Currenttime_With_TotalTime() {
		TotalTime = String.format("%02d:%02d:%02d",
				TimeUnit.MILLISECONDS.toHours(emVideoView.getDuration()),
				TimeUnit.MILLISECONDS.toMinutes(emVideoView.getDuration()) -
						TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(emVideoView.getDuration())),
				TimeUnit.MILLISECONDS.toSeconds(emVideoView.getDuration()) -
						TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(emVideoView.getDuration())));

		Current_Time = String.format("%02d:%02d:%02d",
				TimeUnit.MILLISECONDS.toHours(emVideoView.getCurrentPosition()),
				TimeUnit.MILLISECONDS.toMinutes(emVideoView.getCurrentPosition()) -
						TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(emVideoView.getCurrentPosition())),
				TimeUnit.MILLISECONDS.toSeconds(emVideoView.getCurrentPosition()) -
						TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(emVideoView.getCurrentPosition())));

		total_time.setText(TotalTime);
		current_time.setText(Current_Time);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		if (asynGetIpAddress != null) {
			asynGetIpAddress.cancel(true);
		}
		if (asyncVideoLogDetails != null) {
			asyncVideoLogDetails.cancel(true);
		}
		if (asyncFFVideoLogDetails != null) {
			asyncFFVideoLogDetails.cancel(true);
		}
		if (progressView != null && progressView.isShown()) {
			progressView = null;
		}
		if (timer != null) {
			stoptimertask();
			timer = null;
		}

		if (video_completed == false) {

			AsyncResumeVideoLogDetails asyncResumeVideoLogDetails = new AsyncResumeVideoLogDetails();
			asyncResumeVideoLogDetails.executeOnExecutor(threadPoolExecutor);
			return;
		}
		mHandler.removeCallbacks(updateTimeTask);
		if (emVideoView != null) {
			emVideoView.release();
		}
		finish();
		overridePendingTransition(0, 0);
	}

	public void backCalled() {

		if (asynGetIpAddress != null) {
			asynGetIpAddress.cancel(true);
		}
		if (asyncVideoLogDetails != null) {
			asyncVideoLogDetails.cancel(true);
		}
		if (asyncFFVideoLogDetails != null) {
			asyncFFVideoLogDetails.cancel(true);
		}
		if (progressView != null && progressView.isShown()) {
			progressView = null;
		}
		if (timer != null) {
			stoptimertask();
			timer = null;
		}
		AsyncResumeVideoLogDetails asyncResumeVideoLogDetails = new AsyncResumeVideoLogDetails();
		asyncResumeVideoLogDetails.executeOnExecutor(threadPoolExecutor);
		return;
      /*  if (video_completed == false){

            AsyncResumeVideoLogDetails  asyncResumeVideoLogDetails = new AsyncResumeVideoLogDetails();
            asyncResumeVideoLogDetails.executeOnExecutor(threadPoolExecutor);
            return;
        }*//*else{
            watchStatus = "com"
            asyncVideoLogDetails = new AsyncVideoLogDetails();
            asyncVideoLogDetails.executeOnExecutor(threadPoolExecutor);
        }*//*
        mHandler.removeCallbacks(updateTimeTask);
        if (emVideoView!=null) {
            emVideoView.release();
        }
        finish();
        overridePendingTransition(0, 0);*/
	}

	/* public void onBackPressed() {
         super.onBackPressed();
         Log.v("SUBHA","HHVID"+videoLogId);
         if (asynGetIpAddress!=null){
             asynGetIpAddress.cancel(true);
         }
         if (asyncVideoLogDetails!=null){
             asyncVideoLogDetails.cancel(true);
         }
         if (asyncFFVideoLogDetails!=null){
             asyncFFVideoLogDetails.cancel(true);
         }
         if (progressView!=null && progressView.isShown()){
             progressView = null;
         }
         if (timer!=null){
             stoptimertask();
             timer = null;
         }
         mHandler.removeCallbacks(updateTimeTask);
         if (emVideoView!=null) {
             emVideoView.release();
         }
         finish();
         overridePendingTransition(0, 0);
     }*/
	@Override
	protected void onUserLeaveHint() {

		//if (played_length <= 0) {
		if (asynGetIpAddress != null) {
			asynGetIpAddress.cancel(true);
		}
		if (asyncVideoLogDetails != null) {
			asyncVideoLogDetails.cancel(true);
		}
		if (asyncFFVideoLogDetails != null) {
			asyncFFVideoLogDetails.cancel(true);
		}
		if (progressView != null && progressView.isShown()) {
			progressView = null;
		}
		if (timer != null) {
			stoptimertask();
			timer = null;
		}




		if(Util.call_finish_at_onUserLeaveHint) {

			Util.call_finish_at_onUserLeaveHint = true;

			mHandler.removeCallbacks(updateTimeTask);
			if (emVideoView != null) {
				emVideoView.release();
			}

			finish();
			overridePendingTransition(0, 0);
			super.onUserLeaveHint();
		}

	}


	public void Execute_Pause_Play() {
		if (emVideoView.isPlaying()) {
			emVideoView.pause();
			latest_center_play_pause.setImageResource(R.drawable.center_ic_media_play);
			center_play_pause.setImageResource(R.drawable.ic_media_play);
			mHandler.removeCallbacks(updateTimeTask);
		} else {
			if (video_completed) {

				if (content_types_id != 4) {
					// onBackPressed();
					backCalled();
				}

			} else {
				emVideoView.start();
				latest_center_play_pause.setImageResource(R.drawable.center_ic_media_pause);
				center_play_pause.setImageResource(R.drawable.ic_media_pause);
				mHandler.removeCallbacks(updateTimeTask);
				updateProgressBar();
			}

		}
	}

	public void Start_Timer() {

		End_Timer();
		center_pause_paly_timer = new Timer();
		center_pause_paly_timer_is_running = true;
		TimerTask timerTaskObj = new TimerTask() {
			public void run() {
				//perform your action here

				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						current_time.setVisibility(View.GONE);
						center_play_pause.setVisibility(View.GONE);
						latest_center_play_pause.setVisibility(View.GONE);
						End_Timer();
					}
				});
			}
		};
		center_pause_paly_timer.schedule(timerTaskObj, 2000, 2000);
	}

	public void End_Timer() {
		if (center_pause_paly_timer_is_running) {
			center_pause_paly_timer.cancel();
			center_pause_paly_timer_is_running = false;

			subtitle_change_btn.setVisibility(View.INVISIBLE);
			primary_ll.setVisibility(View.GONE);
			last_ll.setVisibility(View.GONE);
			center_play_pause.setVisibility(View.GONE);
			latest_center_play_pause.setVisibility(View.GONE);
			current_time.setVisibility(View.GONE);
		}

	}

	public void Instant_End_Timer() {
		if (center_pause_paly_timer_is_running) {
			center_pause_paly_timer.cancel();
			center_pause_paly_timer_is_running = false;
		}

	}

	public void showCurrentTime() {

		current_time.setText(Current_Time);
		current_time_position_timer();

       /* if(seek_label_pos == 0)
        {
            current_time_position_timer();
        }
        else
        {
            seek_label_pos = (((seekBar.getRight() - seekBar.getLeft()) * seekBar.getProgress()) / seekBar.getMax()) + seekBar.getLeft();
            current_time.setX(seek_label_pos - current_time.getWidth() / 2);
        }
       *//* if (progresss <=9)
        {
            current_time.setX(seek_label_pos -6);
        }
        else
        {
            current_time.setX(seek_label_pos - 11);
        }*/


	}

	public void current_time_position_timer() {
		final Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (content_types_id != 4) {

//							seek_label_pos = (((seekBar.getRight() - seekBar.getLeft()) * seekBar.getProgress()) / seekBar.getMax()) + seekBar.getLeft();
//							current_time.setX(seek_label_pos - current_time.getWidth() / 2);
							timer.cancel();

							current_time.setText(Current_Time);
							double pourcent = seekBar.getProgress() / (double) seekBar.getMax();
							int offset = seekBar.getThumbOffset();
							int seekWidth = seekBar.getWidth();
							int val = (int) Math.round(pourcent * (seekWidth - 2 * offset));
							int labelWidth = current_time.getWidth();
							current_time.setX(offset + seekBar.getX() + val
									- Math.round(pourcent * offset)
									- Math.round(pourcent * labelWidth/2));


						}
					}
				});
			}
		}, 0, 100);
	}

   /* @Override
    public boolean onKeyDown(int keyCode, KeyEvent objEvent) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Log.v("SUBHA","FHFHFHCALLED");
            return true;
        }
        return super.onKeyUp(keyCode, objEvent);
    }
*/


	private class AsyncResumeVideoLogDetails extends AsyncTask<Void, Void, Void> {
		//  ProgressDialog pDialog;
		String responseStr;
		int statusCode = 0;
		String watchSt = "halfplay";

		@Override
		protected Void doInBackground(Void... params) {

			String urlRouteList = Util.rootUrl().trim() + Util.videoLogUrl.trim();
			try {
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(urlRouteList);
				httppost.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");
				httppost.addHeader("authToken", Util.authTokenStr.trim());
				httppost.addHeader("user_id", userIdStr.trim());
				httppost.addHeader("ip_address", ipAddressStr.trim());
				httppost.addHeader("movie_id", movieId.trim());
				httppost.addHeader("episode_id", episodeId.trim());
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (current_matching_time >= emVideoView.getDuration()) {
							watchSt = "complete";
						}

					}

				});
				httppost.addHeader("played_length", String.valueOf(playerPosition));
				httppost.addHeader("watch_status", watchSt);
              /*  runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (current_matching_time >= emVideoView.getDuration()) {

                            httppost.addHeader("watch_status", "complete");
                        }else{
                            httppost.addHeader("watch_status", "halfplay");

                        }

                    }

                });*/

				httppost.addHeader("device_type", "2");
				httppost.addHeader("log_id", videoLogId);


				// Execute HTTP Post Request
				try {
					HttpResponse response = httpclient.execute(httppost);
					responseStr = EntityUtils.toString(response.getEntity());


				} catch (org.apache.http.conn.ConnectTimeoutException e) {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							videoLogId = "0";

						}

					});

				} catch (IOException e) {
					videoLogId = "0";

					e.printStackTrace();
				}
				if (responseStr != null) {
					JSONObject myJson = new JSONObject(responseStr);
					statusCode = Integer.parseInt(myJson.optString("code"));
					if (statusCode == 200) {
						videoLogId = myJson.optString("log_id");
					} else {
						videoLogId = "0";
					}

				}

			} catch (Exception e) {
				videoLogId = "0";

			}

			return null;
		}


		protected void onPostExecute(Void result) {
         /*   try {
                if (pDialog.isShowing())
                    pDialog.dismiss();
            } catch (IllegalArgumentException ex) {
                videoLogId = "0";
            }*/
			if (responseStr == null) {
				videoLogId = "0";

			}
			mHandler.removeCallbacks(updateTimeTask);
			if (emVideoView != null) {
				emVideoView.release();
			}
			finish();
			overridePendingTransition(0, 0);
			//startTimer();
			return;


		}

		@Override
		protected void onPreExecute() {
			stoptimertask();

		}


	}

	public void ShowResumeDialog(String Title, String msg) {
		AlertDialog.Builder dlgAlert = new AlertDialog.Builder(MarlinBroadbandExample.this,R.style.MyAlertDialogStyle);

		dlgAlert.setMessage(msg);
		dlgAlert.setTitle(Title);
		dlgAlert.setPositiveButton(Util.getTextofLanguage(MarlinBroadbandExample.this, Util.BUTTON_OK, Util.DEFAULT_BUTTON_OK), null);
		dlgAlert.setCancelable(false);
		dlgAlert.setPositiveButton(Util.getTextofLanguage(MarlinBroadbandExample.this, Util.BUTTON_OK, Util.DEFAULT_BUTTON_OK),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
		dlgAlert.create().show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (requestCode == 1001) {

				Util.call_finish_at_onUserLeaveHint = true;


				if (data.getStringExtra("yes").equals("1002")) {

					watchStatus = "halfplay";
					playerPosition = Util.dataModel.getPlayPos();
					emVideoView.start();
					emVideoView.seekTo(played_length);
					seekBar.setProgress(played_length);
					updateProgressBar();

				} else {
					emVideoView.start();
					seekBar.setProgress(emVideoView.getCurrentPosition());
					updateProgressBar();
				}


				if(SubTitlePath.size()>0)
				{

					CheckSubTitleParsingType("1");

					subtitleDisplayHandler = new Handler();
					subsFetchTask = new SubtitleProcessingTask("1");
					subsFetchTask.execute();
				}
				else {
					asyncVideoLogDetails = new AsyncVideoLogDetails();
					asyncVideoLogDetails.executeOnExecutor(threadPoolExecutor);
				}

			}
			if(requestCode == 222)
			{
//                Toast.makeText(getApplicationContext(),""+data.getStringExtra("position"),Toast.LENGTH_SHORT).show();

				if (!data.getStringExtra("position").equals("nothing")) {

					if(data.getStringExtra("position").equals("0"))
					{
						// Stop Showing Subtitle
						if(subtitleDisplayHandler!=null)
							subtitleDisplayHandler.removeCallbacks(subtitleProcessesor);
						subtitleText.setText("");
					}
					else
					{
						try{

							CheckSubTitleParsingType(data.getStringExtra("position"));

							subtitleDisplayHandler = new Handler();
							subsFetchTask = new SubtitleProcessingTask(data.getStringExtra("position"));
							subsFetchTask.execute();
						}catch (Exception e){
						}

					}

				}
			}

		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Util.hide_pause = false;
//        if(!Util.download_completed)
//        {
//            timerr.cancel();
//        }
	}

	// Added Later By Bibhu For Subtitle Feature.

	public class SubtitleProcessingTask extends AsyncTask<Void, Void, Void> {



		String Subtitle_Path = "";
		public SubtitleProcessingTask(String path) {

			Subtitle_Path = SubTitlePath.get((Integer.parseInt(path)-1));
			Log.v("BIBHU3","parsing called Subtitle_Path============"+Subtitle_Path);
		}

		@Override
		protected void onPreExecute() {
//            subtitleText.setText("Loading subtitles..");
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			// int count;
			try {


				/*
				 * if you want to download file from Internet, use commented
				 * code.
				 */
				// URL url = new URL(
				// "https://dozeu380nojz8.cloudfront.net/uploads/video/subtitle_file/3533/srt_919a069ace_boss.srt");
				// InputStream is = url.openStream();
				// File f = getExternalFile();
				// FileOutputStream fos = new FileOutputStream(f);
				// byte data[] = new byte[1024];
				// while ((count = is.read(data)) != -1) {
				// fos.write(data, 0, count);
				// }
				// is.close();
//                // fos.close();
//                Uri uri = Uri.parse(Environment.getExternalStorageDirectory().getAbsolutePath()+"/sintel.vtt");
////

//                File root = android.os.Environment.getExternalStorageDirectory();
//                mediaStorageDir = new File(root+"/Android/data/"+getApplicationContext().getPackageName().trim()+"/SubTitleList/", "");


//                String path = Environment.getExternalStorageDirectory().toString()+"/sub.vtt";
//                File myFile = new File(path);
				File myFile = new File(Subtitle_Path);
				InputStream fIn = new FileInputStream( String.valueOf( myFile ) );


              /* InputStream stream = getResources().openRawResource(
                        R.raw.subtitle);*/

				if(callWithoutCaption)
				{
					FormatSRT_WithoutCaption formatSRT = new FormatSRT_WithoutCaption();
					srt = formatSRT.parseFile("sample", fIn);
				}
				else
				{
					FormatSRT formatSRT = new FormatSRT();
					srt = formatSRT.parseFile("sample", fIn);
				}



			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (null != srt) {
				subtitleText.setText("");
				subtitleDisplayHandler.post(subtitleProcessesor);
//                Toast.makeText(getApplicationContext(), "subtitles loaded!!",Toast.LENGTH_SHORT).show();
			}

			asyncVideoLogDetails = new AsyncVideoLogDetails();
			asyncVideoLogDetails.executeOnExecutor(threadPoolExecutor);

			super.onPostExecute(result);
		}
	}

	public void onTimedText(Caption text) {
		if (text == null) {
			subtitleText.setVisibility(View.INVISIBLE);
			return;
		}

		Typeface videoGenreTextViewTypeface = Typeface.createFromAsset(getAssets(),getResources().getString(R.string.regular_fonts));
		subtitleText.setTypeface(videoGenreTextViewTypeface);
		subtitleText.setText(Html.fromHtml(text.content));
		subtitleText.setVisibility(View.VISIBLE);

	}

	@Override
	public void finish() {
		cleanUp();
		super.finish();
	}

	private void cleanUp() {
		if (subtitleDisplayHandler != null) {
			subtitleDisplayHandler.removeCallbacks(subtitleProcessesor);
		}

	}

	@Override
	protected void onPause() {
        /*if (subtitleDisplayHandler != null) {
            subtitleDisplayHandler.removeCallbacks(subtitleProcessesor);
            subtitleDisplayHandler = null;
            if (subsFetchTask != null)
                subsFetchTask.cancel(true);
        }*/
		super.onPause();
	}

	private Runnable subtitleProcessesor = new Runnable() {

		@Override
		public void run() {
			if (emVideoView != null && emVideoView.isPlaying()) {
				int currentPos = emVideoView.getCurrentPosition();
				Collection<Caption> subtitles = srt.captions.values();
				for (Caption caption : subtitles) {
					if (currentPos >= caption.start.mseconds
							&& currentPos <= caption.end.mseconds) {
						onTimedText(caption);
						break;
					} else if (currentPos > caption.end.mseconds) {
						onTimedText(null);
					}
				}
			}
			subtitleDisplayHandler.postDelayed(this, 100);
		}
	};

	public void CheckSubTitleParsingType(String path)
	{

		String Subtitle_Path = SubTitlePath.get((Integer.parseInt(path)-1));
		Log.v("BIBHU3","parsing called CheckSubTitleParsingType============"+Subtitle_Path);


		callWithoutCaption = true;

		File myFile = new File(Subtitle_Path);
		BufferedReader test_br = null;
		InputStream stream = null;
		InputStreamReader in = null;
		try {
			stream = new FileInputStream( String.valueOf( myFile ));
			in= new InputStreamReader(stream);
			test_br =  new BufferedReader(in);

		} catch (Exception e) {
			e.printStackTrace();
		}

		int testinglinecounter = 1;
		int captionNumber = 1;


		String TestingLine = null;
		try {
			TestingLine = test_br.readLine();
		} catch (Exception e) {
			e.printStackTrace();
		}
		while(testinglinecounter<6)
		{
			try
			{

				if(Integer.parseInt(TestingLine.toString().trim())==captionNumber)
				{
					callWithoutCaption = false;
					testinglinecounter = 6;
				}
			}
			catch (Exception e){
				try {
					TestingLine = test_br.readLine();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				testinglinecounter++;
			}
		}
	}

	private void hideSystemUI() {
		// Set the IMMERSIVE flag.
		// Set the content to appear under the system bars so that the content
		// doesn't resize when the system bars hide and show.
		View decorView = getWindow().getDecorView();
		decorView.setSystemUiVisibility(
				View.SYSTEM_UI_FLAG_LAYOUT_STABLE
						| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
						| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
						| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
						| View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
						| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
	}

	private void showSystemUI() {
		View decorView = getWindow().getDecorView();
		decorView.setSystemUiVisibility(
				View.SYSTEM_UI_FLAG_LAYOUT_STABLE

		);
	}






}
