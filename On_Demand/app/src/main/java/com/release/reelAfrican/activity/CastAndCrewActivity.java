package com.release.reelAfrican.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.release.reelAfrican.R;
import com.release.reelAfrican.adapter.CastCrewAdapter;
import com.release.reelAfrican.model.GetCastCrewItem;
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

import static android.content.res.Configuration.SCREENLAYOUT_SIZE_LARGE;
import static android.content.res.Configuration.SCREENLAYOUT_SIZE_MASK;
import static android.content.res.Configuration.SCREENLAYOUT_SIZE_XLARGE;

public class CastAndCrewActivity extends AppCompatActivity {



    Toolbar mActionBarToolbar;
    TextView castCrewTitleTextView;
    RecyclerView castCrewListRecyclerView;

    ArrayList<GetCastCrewItem> castCrewItems = new ArrayList<GetCastCrewItem>();
    CastCrewAdapter castCrewAdapter;
    GridView cast_crew_crid;

    RelativeLayout noInternetLayout;
    RelativeLayout noDataLayout;
    TextView noDataTextView;
    TextView noInternetTextView;

    LinearLayout primary_layout;
    boolean isNetwork;

    String  movie_id,movie_uniq_id;

    int corePoolSize = 60;
    int maximumPoolSize = 80;
    int keepAliveTime = 10;
    BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<Runnable>(maximumPoolSize);
    Executor threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, workQueue);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_cast_and_crew);
        mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mActionBarToolbar);
        mActionBarToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back));
        mActionBarToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        noInternetLayout =(RelativeLayout)findViewById(R.id.noInternet);
        noDataLayout =(RelativeLayout)findViewById(R.id.noData);
        noInternetTextView =(TextView)findViewById(R.id.noInternetTextView);
        noDataTextView =(TextView)findViewById(R.id.noDataTextView);
        noInternetTextView.setText(Util.getTextofLanguage(CastAndCrewActivity.this,Util.NO_INTERNET_CONNECTION,Util.DEFAULT_NO_INTERNET_CONNECTION));
        noDataTextView.setText(Util.getTextofLanguage(CastAndCrewActivity.this,Util.NO_CONTENT,Util.DEFAULT_NO_CONTENT));


        primary_layout = (LinearLayout)findViewById(R.id.primary_layout);
        castCrewTitleTextView = (TextView) findViewById(R.id.castCrewTitleTextView);
        Typeface custom_name = Typeface.createFromAsset(getAssets(),getResources().getString(R.string.regular_fonts));
        castCrewTitleTextView.setTypeface(custom_name);
        castCrewTitleTextView.setText(Util.getTextofLanguage(CastAndCrewActivity.this,Util.CAST_CREW_BUTTON_TITLE,Util.DEFAULT_CAST_CREW_BUTTON_TITLE));
        cast_crew_crid = (GridView) findViewById(R.id.cast_crew_crid);
        isNetwork = Util.checkNetwork(CastAndCrewActivity.this);




        if (((getResources().getConfiguration().screenLayout & SCREENLAYOUT_SIZE_MASK) == SCREENLAYOUT_SIZE_LARGE) || ((getResources().getConfiguration().screenLayout & SCREENLAYOUT_SIZE_MASK) == SCREENLAYOUT_SIZE_XLARGE)) {
            cast_crew_crid.setNumColumns(2);

        }else {
            //"Mobile";
            cast_crew_crid.setNumColumns(1);
        }
        castCrewAdapter = new CastCrewAdapter(CastAndCrewActivity.this,castCrewItems);
        cast_crew_crid.setAdapter(castCrewAdapter);
        GetCsatCrewDetails();

        cast_crew_crid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GetCastCrewItem item = castCrewItems.get(position);
                Intent castCrewIntent = new Intent(CastAndCrewActivity.this,CastCrewDetailsActivity.class);
                castCrewIntent.putExtra("castPermalink",item.getCastPermalink());
                Log.v("SUBHA", "PERMALINK_INTENT_KEY" + item.getCastPermalink());

                castCrewIntent.putExtra("castName",item.getCastPermalink());
                castCrewIntent.putExtra("castSummary",item.getCelebritySummary());
                castCrewIntent.putExtra("castImage",item.getCastImage());

                startActivity(castCrewIntent);
            }
        });



    }



 /*   private void prepareMovieData() {
        GetCastCrewItem movie = new GetCastCrewItem("Mad Max: Fury Road", "Action & Adventure", R.mipmap.ic_launcher);
        castCrewItems.add(movie);

        movie = new GetCastCrewItem("Inside Out", "Animation, Kids & Family", R.mipmap.ic_launcher);
        castCrewItems.add(movie);

        movie = new GetCastCrewItem("Star Wars: Episode VII - The Force Awakens, eyfgey yugyef yyugfewf ygew", "Action", R.drawable.logo);
        castCrewItems.add(movie);

        movie = new GetCastCrewItem("Shaun the Sheep", "Animation", R.drawable.logo);
        castCrewItems.add(movie);

        movie = new GetCastCrewItem("The Martian", "Science Fiction & Fantasy", R.mipmap.ic_launcher);
        castCrewItems.add(movie);

        movie = new GetCastCrewItem("Mission: Impossible Rogue Nation", "Action", R.drawable.logo);
        castCrewItems.add(movie);

        movie = new GetCastCrewItem("Up", "Animation", R.drawable.logo);
        castCrewItems.add(movie);

        movie = new GetCastCrewItem("Star Trek", "Science Fiction", R.drawable.logo);
        castCrewItems.add(movie);

        movie = new GetCastCrewItem("The LEGO Movie", "Animation", R.drawable.logo);
        castCrewItems.add(movie);

        movie = new GetCastCrewItem("Iron Man", "Action & Adventure", R.drawable.logo);
        castCrewItems.add(movie);

        movie = new GetCastCrewItem("Aliens", "Science Fiction", R.drawable.logo);
        castCrewItems.add(movie);



        //castCrewAdapter.notifyDataSetChanged();
    }*/

    public void GetCsatCrewDetails()
    {
        noInternetLayout.setVisibility(View.GONE);
        noDataLayout.setVisibility(View.GONE);
        primary_layout.setVisibility(View.VISIBLE);


        AsynGetCsatDetails asynGetCsatDetails = new AsynGetCsatDetails();
        asynGetCsatDetails.executeOnExecutor(threadPoolExecutor);

    }
    //Asyntask for getDetails of the csat and crew members.

    private class AsynGetCsatDetails extends AsyncTask<Void, Void, Void> {
        ProgressBarHandler pDialog;
        String responseStr = "";
        int status;
        String msg;

        @Override
        protected Void doInBackground(Void... params) {

            try {


                HttpClient httpclient=new DefaultHttpClient();
                HttpPost httppost = new HttpPost(Util.rootUrl().trim()+Util.CsatAndCrew.trim());
                httppost.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");
                httppost.addHeader("authToken", Util.authTokenStr.trim());
                httppost.addHeader("movie_id",getIntent().getStringExtra("cast_movie_id"));
                httppost.addHeader("lang_code",Util.getTextofLanguage(CastAndCrewActivity.this,Util.SELECTED_LANGUAGE_CODE,Util.DEFAULT_SELECTED_LANGUAGE_CODE));

               /* httppost.addHeader("authToken", "1836d3f6a8d75407a162bcc5eece68c7");
                httppost.addHeader("movie_id","acb089ace5126fe0e1e6054edd86dc6d");*/


                // Execute HTTP Post Request
                try {
                    HttpResponse response = httpclient.execute(httppost);
                    responseStr = EntityUtils.toString(response.getEntity());

                } catch (Exception e){

                }

                JSONObject myJson =null;
                JSONArray jsonArray =null;
                if(responseStr!=null){
                    myJson = new JSONObject(responseStr);
                    status = Integer.parseInt(myJson.optString("code"));
                    msg = myJson.optString("msg");


                }

                if (status == 200) {
                    jsonArray = myJson.getJSONArray("celibrity");
                    for (int i=0 ;i<jsonArray.length();i++)
                    {
                        String celebrityName = jsonArray.getJSONObject(i).optString("name");
                        String celebrityImage = jsonArray.getJSONObject(i).optString("celebrity_image");
                        String celebrityPermalink = jsonArray.getJSONObject(i).optString("permalink");
                        String celebritySummary = jsonArray.getJSONObject(i).optString("summary");

                        Log.v("SUBHA","PERMALINK"+celebrityPermalink);
                        String celebrityCastName = jsonArray.getJSONObject(i).optString("cast_type");
                        celebrityCastName = celebrityCastName.replaceAll("\\[", "");
                        celebrityCastName = celebrityCastName.replaceAll("\\]","");
                        celebrityCastName = celebrityCastName.replaceAll(","," , ");
                        celebrityCastName = celebrityCastName.replaceAll("\"", "");


                        if(celebrityImage.equals("") || celebrityImage==null)
                        {
                            celebrityImage = "";
                        }
                        else
                        {
                            if(!celebrityImage.contains("http"))
                            {
                                celebrityImage ="";
                            }
                        }

                        GetCastCrewItem   movie = new GetCastCrewItem(celebrityName,celebrityCastName,celebrityImage,celebrityPermalink,celebritySummary);                        castCrewItems.add(movie);
                    }

                }else{

                    if(status == 448)
                    {

                        // show dialog
                        responseStr = "1";
                    }
                    else
                    {
                        responseStr = "0";
                    }
                }

            } catch (final JSONException e1) {
                responseStr = "0";
            }
            catch (Exception e)
            {

                if(status == 448)
                    responseStr = "1";
                else
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
                        primary_layout.setVisibility(View.GONE);
                        noDataLayout.setVisibility(View.VISIBLE);
                        noInternetLayout.setVisibility(View.GONE);


                    }

                });
                responseStr = "0";
            }
            if(responseStr == null) {
                responseStr = "0";
            }

            if((responseStr.trim().equals("0"))){
                primary_layout.setVisibility(View.GONE);
                noDataLayout.setVisibility(View.VISIBLE);
                noInternetLayout.setVisibility(View.GONE);
            }else{
                if( responseStr.equals("1")) {
                    ShowDialog(msg);
                }
                else {
                    noDataLayout.setVisibility(View.GONE);
                    noInternetLayout.setVisibility(View.GONE);
                    primary_layout.setVisibility(View.VISIBLE);
                    // Set the grid adapter here.
                    castCrewAdapter.notifyDataSetChanged();
                }

            }
        }

        @Override
        protected void onPreExecute() {

            pDialog = new ProgressBarHandler(CastAndCrewActivity.this);
            pDialog.show();


        }
    }

    public void ShowDialog(String msg) {
        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(CastAndCrewActivity.this, R.style.MyAlertDialogStyle);
        dlgAlert.setTitle(Util.getTextofLanguage(CastAndCrewActivity.this,Util.FAILURE,Util.DEFAULT_FAILURE));
        dlgAlert.setMessage(msg);
        dlgAlert.setPositiveButton(Util.getTextofLanguage(CastAndCrewActivity.this,Util.BUTTON_OK,Util.DEFAULT_BUTTON_OK), null);
        dlgAlert.setCancelable(false);
        dlgAlert.setPositiveButton(Util.getTextofLanguage(CastAndCrewActivity.this,Util.BUTTON_OK,Util.DEFAULT_BUTTON_OK),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        finish();
                    }
                });
        dlgAlert.create().show();
    }

   /* @Override
    public void onBackPressed()
    {
        finish();
//        overridePendingTransition(0, 0);
        super.onBackPressed();
    }*/





}
