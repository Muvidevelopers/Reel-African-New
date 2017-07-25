package com.release.reelAfrican.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.crashlytics.android.Crashlytics;
import com.release.reelAfrican.R;
import com.release.reelAfrican.adapter.ResolutionChangeAdapter;
import com.release.reelAfrican.utils.Util;

import java.util.ArrayList;

import io.fabric.sdk.android.Fabric;

public class ResolutionChangeActivity extends Activity {

    ListView listView;
    ArrayList<String> resolutionformst_list = new ArrayList<>();
    ResolutionChangeAdapter resolutionChangeAdapter;
    LinearLayout total_layout;

    ArrayList<String> ResolutionFormat = new ArrayList<>();
    ArrayList<String>ResolutionUrl = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());

        setContentView(R.layout.activity_resolution_change);
        hideSystemUI();
        listView = (ListView) findViewById(R.id.listView);
        total_layout = (LinearLayout) findViewById(R.id.total_layout);

        Util.call_finish_at_onUserLeaveHint = true;

        if (getIntent().getStringArrayListExtra("ResolutionFormat") != null) {
            ResolutionFormat = getIntent().getStringArrayListExtra("ResolutionFormat");
        } else {
            ResolutionFormat.clear();
        }

        if (getIntent().getStringArrayListExtra("ResolutionUrl") != null) {
            ResolutionUrl = getIntent().getStringArrayListExtra("ResolutionUrl");
        } else {
            ResolutionUrl.clear();
        }

        for(int i=0;i<ResolutionFormat.size();i++)
        {
            resolutionformst_list.add(ResolutionFormat.get(i));
        }

        resolutionChangeAdapter = new ResolutionChangeAdapter(ResolutionChangeActivity.this,resolutionformst_list);
        listView.setAdapter(resolutionChangeAdapter);

        Animation topTobottom = AnimationUtils.loadAnimation(this, R.anim.bottom_top);
        listView.startAnimation(topTobottom );


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Util.VideoResolution = ResolutionFormat.get(position).trim();

                Intent playerIntent = new Intent();
                playerIntent.putExtra("position", ""+position);
                playerIntent.putExtra("type", "resolution");
                setResult(RESULT_OK, playerIntent);
                finish();
            }
        });

        total_layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Intent playerIntent = new Intent();
                playerIntent.putExtra("position", "nothing");
                playerIntent.putExtra("type", "resolution");
                setResult(RESULT_OK, playerIntent);
                finish();
                return false;
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent playerIntent = new Intent();
        playerIntent.putExtra("position", "nothing");
        playerIntent.putExtra("type", "resolution");
        setResult(RESULT_OK, playerIntent);
        finish();
    }

    private void hideSystemUI() {

        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }
}
