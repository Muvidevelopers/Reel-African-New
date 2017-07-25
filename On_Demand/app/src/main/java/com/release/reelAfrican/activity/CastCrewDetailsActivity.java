package com.release.reelAfrican.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.DimenRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.release.reelAfrican.R;
import com.release.reelAfrican.adapter.FilmographyAdapter;
import com.release.reelAfrican.model.GridItem;
import com.release.reelAfrican.utils.ProgressBarHandler;
import com.release.reelAfrican.utils.ResizableCustomView;
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

public class CastCrewDetailsActivity extends AppCompatActivity {
    ProgressBarHandler pDialog;
    Toolbar mActionBarToolbar;
    TextView castNameTextView,castDescriptionTextView;
    ImageView castImageView;
    Button btnmore;
    String castpermalinkStr = "";
    RecyclerView filmographyRecyclerView;
     int itemsInServer = 0;
    int  videoHeight = 185;
    int  videoWidth = 256;
    String videoImageStrToHeight;
    String castNameStr = "";
    String castSummaryStr = "";
    String castImageStr = "";

    ArrayList<GridItem> filmogrpahyItems = new ArrayList<GridItem>();
    FilmographyAdapter filmographyAdapter;
    AsynLOADUI loadUI;

    int corePoolSize = 60;
    int maximumPoolSize = 80;
    int keepAliveTime = 10;
    BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<Runnable>(maximumPoolSize);
    Executor threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, workQueue);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());

        setContentView(R.layout.activity_cast_crew_details);
        mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mActionBarToolbar);
        mActionBarToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back));
        mActionBarToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if (getIntent().getStringExtra("castPermalink")!=null) {
            castpermalinkStr = getIntent().getStringExtra("castPermalink");

        }
        if (getIntent().getStringExtra("castName")!=null) {
            castNameStr = getIntent().getStringExtra("castName");
        } if (getIntent().getStringExtra("castSummary")!=null) {
            castSummaryStr = getIntent().getStringExtra("castSummary");
        } if (getIntent().getStringExtra("castImage")!=null) {
            castImageStr = getIntent().getStringExtra("castImage");
        }

        castNameTextView = (TextView) findViewById(R.id.castNameTextView);
        filmographyRecyclerView = (RecyclerView) findViewById(R.id.filmographyRecyclerView);
        castImageView = (ImageView) findViewById(R.id.castImageView);
        // Typeface videoGenreTextViewTypeface = Typeface.createFromAsset(getAssets(),getResources().getString(R.string.regular_fonts));
       // castNameTextView.setTypeface(videoGenreTextViewTypeface);
        castDescriptionTextView = (TextView) findViewById(R.id.castDescriptionTextView);
        btnmore= (Button) findViewById(R.id.btnMore);

        Typeface videoGenreTextViewTypeface = Typeface.createFromAsset(getAssets(),getResources().getString(R.string.regular_fonts));
        btnmore.setTypeface(videoGenreTextViewTypeface);

        btnmore.setText(Util.getTextofLanguage(CastCrewDetailsActivity.this, Util.VIEW_MORE, Util.DEFAULT_VIEW_MORE));

        btnmore.setVisibility(View.GONE);
        btnmore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                final Intent episode = new Intent(CastCrewDetailsActivity.this, ViewMoreActivity.class);
                episode.putExtra(Util.PERMALINK_INTENT_KEY, castpermalinkStr);
                episode.putExtra("cast", "fromcast");
                episode.putExtra("sectionName", castNameTextView.getText().toString().trim());


                runOnUiThread(new Runnable() {
                    public void run() {
                        episode.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

                        startActivity(episode);


                    }
                });

            }
        });
        filmographyRecyclerView.addOnItemTouchListener(new MovieDetailsActivity.RecyclerTouchListener1(CastCrewDetailsActivity.this, filmographyRecyclerView, new MovieDetailsActivity.ClickListener1() {
            @Override
            public void onClick(View view, int position) {

                GridItem item = filmogrpahyItems.get(position);
                String moviePermalink = item.getPermalink();
                String movieTypeId = item.getVideoTypeId();

                if (moviePermalink.matches(Util.getTextofLanguage(CastCrewDetailsActivity.this,Util.NO_DATA,Util.DEFAULT_NO_DATA))) {
                    AlertDialog.Builder dlgAlert = new AlertDialog.Builder(CastCrewDetailsActivity.this, R.style.MyAlertDialogStyle);
                    dlgAlert.setMessage(Util.getTextofLanguage(CastCrewDetailsActivity.this,Util.NO_DETAILS_AVAILABLE,Util.DEFAULT_NO_DETAILS_AVAILABLE));
                    dlgAlert.setTitle(Util.getTextofLanguage(CastCrewDetailsActivity.this,Util.SORRY,Util.DEFAULT_SORRY));
                    dlgAlert.setPositiveButton(Util.getTextofLanguage(CastCrewDetailsActivity.this,Util.BUTTON_OK,Util.DEFAULT_BUTTON_OK), null);
                    dlgAlert.setCancelable(false);
                    dlgAlert.setPositiveButton(Util.getTextofLanguage(CastCrewDetailsActivity.this,Util.BUTTON_OK,Util.DEFAULT_BUTTON_OK),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    dlgAlert.create().show();

                } else {

                    if ((movieTypeId.trim().equalsIgnoreCase("1")) || (movieTypeId.trim().equalsIgnoreCase("2")) || (movieTypeId.trim().equalsIgnoreCase("4"))) {
                        final Intent movieDetailsIntent = new Intent(CastCrewDetailsActivity.this, MovieDetailsActivity.class);
                        movieDetailsIntent.putExtra(Util.PERMALINK_INTENT_KEY, moviePermalink);
                        movieDetailsIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        runOnUiThread(new Runnable() {
                            public void run() {
                                movieDetailsIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                startActivity(movieDetailsIntent);
                            }
                        });


                    } else if ((movieTypeId.trim().equalsIgnoreCase("3")) ) {
                        final Intent detailsIntent = new Intent(CastCrewDetailsActivity.this, ShowWithEpisodesActivity.class);
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

            @Override
            public void onLongClick(View view, int position) {

            }
        }));


        //Typeface castDescriptionTextViewTypeface = Typeface.createFromAsset(getAssets(),getResources().getString(R.string.light_fonts));
       // castDescriptionTextView.setTypeface(castDescriptionTextViewTypeface);
        // makeTextViewResizable(videoStoryTextView,3,movieDetailsStr,true);
       // castDescriptionTextView.setText("Hello");
       // ResizableCustomView.doResizeTextView(CastCrewDetailsActivity.this, castDescriptionTextView, 2, Util.getTextofLanguage(CastCrewDetailsActivity.this, Util.VIEW_MORE, Util.DEFAULT_VIEW_MORE), true);
        GetCastCrewDetails();
    }

    public void GetCastCrewDetails()
    {

        AsynLoadCastCrewDetails asynLoadCastCrewDetails = new AsynLoadCastCrewDetails();
        asynLoadCastCrewDetails.executeOnExecutor(threadPoolExecutor);

    }
    //Load Films Videos
    private class AsynLoadCastCrewDetails extends AsyncTask<Void, Void, Void> {
        String responseStr;

        int status;
        String movieGenreStr = "";
        String movieName = Util.getTextofLanguage(CastCrewDetailsActivity.this,Util.NO_DATA,Util.DEFAULT_NO_DATA);
        String movieImageStr = Util.getTextofLanguage(CastCrewDetailsActivity.this,Util.NO_DATA,Util.DEFAULT_NO_DATA);
        String moviePermalinkStr = Util.getTextofLanguage(CastCrewDetailsActivity.this,Util.NO_DATA,Util.DEFAULT_NO_DATA);
        String videoTypeIdStr = Util.getTextofLanguage(CastCrewDetailsActivity.this,Util.NO_DATA,Util.DEFAULT_NO_DATA);
        String isEpisodeStr = "";

        int isAPV = 0;
        int isPPV = 0;
        int isConverted = 0;

        @Override
        protected Void doInBackground(Void... params) {

            String urlRouteList= Util.rootUrl().trim()+Util.filmographyUrl.trim();

            try {
                HttpClient httpclient=new DefaultHttpClient();
                HttpPost httppost = new HttpPost(urlRouteList);
                httppost.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");

                httppost.addHeader("authToken", Util.authTokenStr.trim());
                httppost.addHeader("permalink", castpermalinkStr);

                httppost.addHeader("limit", String.valueOf(4));
                httppost.addHeader("offset", String.valueOf(0));
                SharedPreferences countryPref = getSharedPreferences(Util.COUNTRY_PREF, 0); // 0 - for private mode
                if (countryPref != null) {
                    String countryCodeStr = countryPref.getString("countryCode", null);
                    httppost.addHeader("country", countryCodeStr);
                }else{
                    httppost.addHeader("country", "IN");

                }
                httppost.addHeader("lang_code",Util.getTextofLanguage(CastCrewDetailsActivity.this,Util.SELECTED_LANGUAGE_CODE,Util.DEFAULT_SELECTED_LANGUAGE_CODE));

                // Execute HTTP Post Request
                try {
                    HttpResponse response = httpclient.execute(httppost);
                    responseStr = EntityUtils.toString(response.getEntity());

                } catch (org.apache.http.conn.ConnectTimeoutException e){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            responseStr = "0";
                            Util.showToast(CastCrewDetailsActivity.this, Util.getTextofLanguage(CastCrewDetailsActivity.this, Util.SLOW_INTERNET_CONNECTION, Util.DEFAULT_SLOW_INTERNET_CONNECTION));

                        }

                    });

                }catch (IOException e) {
                    responseStr = "0";

                    e.printStackTrace();
                }

                JSONObject myJson =null;
                if(responseStr!=null){
                    myJson = new JSONObject(responseStr);
                    status = Integer.parseInt(myJson.optString("status"));
                    itemsInServer = Integer.parseInt(myJson.optString("item_count"));
                    if ((myJson.has("name")) && myJson.getString("name").trim() != null && !myJson.getString("name").trim().isEmpty() && !myJson.getString("name").trim().equals("null") && !myJson.getString("name").trim().matches("")) {
                        castNameStr = myJson.getString("name");

                    }else{
                        castNameStr = "";

                    }
                    Log.v("SUBHA","JFHF"+castSummaryStr);
                    if ((myJson.has("summary")) && myJson.getString("summary").trim() != null && !myJson.getString("summary").trim().isEmpty() && !myJson.getString("summary").trim().equals("null") && !myJson.getString("summary").trim().matches("")) {
                        castSummaryStr = myJson.getString("summary");
                        Log.v("SUBHA","castSummaryStr"+castSummaryStr);

                    }else{
                        castSummaryStr = "";
                    }
                    if ((myJson.has("cast_image")) && myJson.getString("cast_image").trim() != null && !myJson.getString("cast_image").trim().isEmpty() && !myJson.getString("cast_image").trim().equals("null") && !myJson.getString("cast_image").trim().matches("")) {
                        castImageStr = myJson.getString("cast_image");

                    }else{
                        castImageStr = Util.getTextofLanguage(CastCrewDetailsActivity.this,Util.NO_DATA,Util.DEFAULT_NO_DATA);
                    }


                }

                if (status > 0) {
                    if (status == 200) {

                       if (myJson.has("movieList")) {
                           JSONArray jsonMainNode = myJson.getJSONArray("movieList");

                           int lengthJsonArr = jsonMainNode.length();
                           for (int i = 0; i < lengthJsonArr; i++) {
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

                                   }
                                   if ((jsonChildNode.has("permalink")) && jsonChildNode.getString("permalink").trim() != null && !jsonChildNode.getString("permalink").trim().isEmpty() && !jsonChildNode.getString("permalink").trim().equals("null") && !jsonChildNode.getString("permalink").trim().matches("")) {
                                       moviePermalinkStr = jsonChildNode.getString("permalink");

                                   }
                                   if ((jsonChildNode.has("content_types_id")) && jsonChildNode.getString("content_types_id").trim() != null && !jsonChildNode.getString("content_types_id").trim().isEmpty() && !jsonChildNode.getString("content_types_id").trim().equals("null") && !jsonChildNode.getString("content_types_id").trim().matches("")) {
                                       videoTypeIdStr = jsonChildNode.getString("content_types_id");

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
                                   if ((jsonChildNode.has("is_episode")) && jsonChildNode.getString("is_episode").trim() != null && !jsonChildNode.getString("is_episode").trim().isEmpty() && !jsonChildNode.getString("is_episode").trim().equals("null") && !jsonChildNode.getString("is_episode").trim().matches("")) {
                                       isEpisodeStr = jsonChildNode.getString("is_episode");

                                   }

                                   filmogrpahyItems.add(new GridItem(movieImageStr, movieName, "", videoTypeIdStr, movieGenreStr, "", moviePermalinkStr, isEpisodeStr, "", "", isConverted, isPPV, isAPV));
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
                    else{
                        responseStr = "0";
                    }
                }
            }
            catch (Exception e)
            {
                responseStr = "0";

                e.printStackTrace();

            }
            return null;

        }

        protected void onPostExecute(Void result) {

            if (itemsInServer > 4){
                btnmore.setVisibility(View.VISIBLE);
            }
            if(castImageStr.trim().matches(Util.getTextofLanguage(CastCrewDetailsActivity.this,Util.NO_DATA,Util.DEFAULT_NO_DATA))){
                castImageView.setImageResource(R.drawable.logo);
            }else{

                ImageLoader imageLoader = ImageLoader.getInstance();
                imageLoader.init(ImageLoaderConfiguration.createDefault(CastCrewDetailsActivity.this));

                DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
                        .cacheOnDisc(true).resetViewBeforeLoading(true)
                        .showImageForEmptyUri(R.drawable.logo)
                        .showImageOnFail(R.drawable.logo)
                        .showImageOnLoading(R.drawable.logo).build();
                imageLoader.displayImage(castImageStr, castImageView, options);

            }
            if(responseStr == null)
                responseStr = "0";
            if((responseStr.trim().equals("0"))){

                if (pDialog != null && pDialog.isShowing()) {
                    pDialog.hide();
                    pDialog = null;
                }
                if (castNameStr.matches("")){
                    castNameTextView.setVisibility(View.GONE);
                }else{
                    Typeface videoGenreTextViewTypeface = Typeface.createFromAsset(getAssets(),getResources().getString(R.string.regular_fonts));
                    castNameTextView.setTypeface(videoGenreTextViewTypeface);
                    castNameTextView.setText(castNameStr);
                    castNameTextView.setVisibility(View.VISIBLE);

                }
                if (castSummaryStr.matches("")){
                    castDescriptionTextView.setVisibility(View.GONE);
                }else {
                    Typeface castDescriptionTextViewTypeface = Typeface.createFromAsset(getAssets(),getResources().getString(R.string.light_fonts));
                    castDescriptionTextView.setTypeface(castDescriptionTextViewTypeface);
                    castDescriptionTextView.setText(castSummaryStr);
                    ResizableCustomView.doResizeTextView(CastCrewDetailsActivity.this, castDescriptionTextView, 2, Util.getTextofLanguage(CastCrewDetailsActivity.this, Util.VIEW_MORE, Util.DEFAULT_VIEW_MORE), true);
                }
            }else{
                if(filmogrpahyItems.size() <= 0){

                    if (pDialog != null && pDialog.isShowing()) {
                        pDialog.hide();
                        pDialog = null;
                    }
                    if (castNameStr.matches("")){
                        castNameTextView.setVisibility(View.GONE);
                    }else{
                        Typeface videoGenreTextViewTypeface = Typeface.createFromAsset(getAssets(),getResources().getString(R.string.regular_fonts));
                        castNameTextView.setTypeface(videoGenreTextViewTypeface);
                        castNameTextView.setText(castNameStr);
                        castNameTextView.setVisibility(View.VISIBLE);

                    }
                    if (castSummaryStr.matches("")){
                        castDescriptionTextView.setVisibility(View.GONE);
                    }else {
                        Typeface castDescriptionTextViewTypeface = Typeface.createFromAsset(getAssets(),getResources().getString(R.string.light_fonts));
                        castDescriptionTextView.setTypeface(castDescriptionTextViewTypeface);
                        castDescriptionTextView.setText(castSummaryStr);
                        ResizableCustomView.doResizeTextView(CastCrewDetailsActivity.this, castDescriptionTextView, 2, Util.getTextofLanguage(CastCrewDetailsActivity.this, Util.VIEW_MORE, Util.DEFAULT_VIEW_MORE), true);
                    }
                }else{
                    if (castNameStr.matches("")){
                        castNameTextView.setVisibility(View.GONE);
                    }else{
                        Typeface videoGenreTextViewTypeface = Typeface.createFromAsset(getAssets(),getResources().getString(R.string.regular_fonts));
                        castNameTextView.setTypeface(videoGenreTextViewTypeface);
                        castNameTextView.setText(castNameStr);
                        castNameTextView.setVisibility(View.VISIBLE);

                    }
                    if (castSummaryStr.matches("")){
                        castDescriptionTextView.setVisibility(View.GONE);
                    }else {
                        Typeface castDescriptionTextViewTypeface = Typeface.createFromAsset(getAssets(),getResources().getString(R.string.light_fonts));
                        castDescriptionTextView.setTypeface(castDescriptionTextViewTypeface);
                        castDescriptionTextView.setText(castSummaryStr);
                        ResizableCustomView.doResizeTextView(CastCrewDetailsActivity.this, castDescriptionTextView, 2, Util.getTextofLanguage(CastCrewDetailsActivity.this, Util.VIEW_MORE, Util.DEFAULT_VIEW_MORE), true);
                    }
                    videoImageStrToHeight = movieImageStr;
                    Picasso.with(CastCrewDetailsActivity.this).load(videoImageStrToHeight
                    ).into(new Target() {

                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            videoWidth = bitmap.getWidth();
                            videoHeight = bitmap.getHeight();
                            loadUI = new AsynLOADUI();
                            loadUI.executeOnExecutor(threadPoolExecutor);
                        }

                        @Override
                        public void onBitmapFailed(final Drawable errorDrawable) {

                        }

                        @Override
                        public void onPrepareLoad(final Drawable placeHolderDrawable) {

                        }
                    });

                }
            }
        }

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressBarHandler(CastCrewDetailsActivity.this);
            pDialog.show();

        }


    }

    private class AsynLOADUI extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }

        protected void onPostExecute(Void result) {
            float density = getResources().getDisplayMetrics().density;

            if (videoWidth > videoHeight) {
                if (density >= 3.5 && density <= 4.0) {
                    filmographyAdapter = new FilmographyAdapter(CastCrewDetailsActivity.this, R.layout.nexus_videos_grid_layout_land, filmogrpahyItems);

                }else{
                    filmographyAdapter = new FilmographyAdapter(CastCrewDetailsActivity.this, R.layout.videos_280_grid_layout, filmogrpahyItems);

                }
            } else {
                if (density >= 3.5 && density <= 4.0) {
                    filmographyAdapter = new FilmographyAdapter(CastCrewDetailsActivity.this, R.layout.nexus_videos_grid_layout, filmogrpahyItems);

                }else{
                    filmographyAdapter = new FilmographyAdapter(CastCrewDetailsActivity.this, R.layout.videos_grid_layout, filmogrpahyItems);

                }
            }

            filmographyRecyclerView.setLayoutManager(new LinearLayoutManager(CastCrewDetailsActivity.this, LinearLayoutManager.HORIZONTAL, false));
          //  filmographyRecyclerView.setItemAnimator(new DefaultItemAnimator());
            ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(CastCrewDetailsActivity.this, R.dimen.recy_margin);
            filmographyRecyclerView.addItemDecoration(itemDecoration);
            if (pDialog != null && pDialog.isShowing()) {
                pDialog.hide();
                pDialog = null;
            }
            filmographyRecyclerView.setAdapter(filmographyAdapter);
        }
    }
    public class ItemOffsetDecoration extends RecyclerView.ItemDecoration {

        private int mItemOffset;

        public ItemOffsetDecoration(int itemOffset) {
            mItemOffset = itemOffset;
        }

        public ItemOffsetDecoration(@NonNull Context context, @DimenRes int itemOffsetId) {
            this(context.getResources().getDimensionPixelSize(itemOffsetId));
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                   RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.set(mItemOffset, mItemOffset, mItemOffset, mItemOffset);
        }
    }
}
