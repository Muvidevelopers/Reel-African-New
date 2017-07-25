package com.release.reelAfrican.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.release.reelAfrican.R;
import com.release.reelAfrican.utils.Util;

import java.util.ArrayList;

public class Subtitle_Resolution extends Activity {


    LinearLayout main_layout, slidelayout, resolution_view, subtitle_view;
    TextView resolution_text, subtitle_text;

    ArrayList<String> SubTitleName = new ArrayList<>();
    ArrayList<String> SubTitlePath = new ArrayList<>();
    ArrayList<String> ResolutionFormat = new ArrayList<>();
    ArrayList<String> ResolutionUrl = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subtitle__resolution);
        hideSystemUI();
        main_layout = (LinearLayout) findViewById(R.id.main_layout);
        slidelayout = (LinearLayout) findViewById(R.id.slidelayout);
        resolution_view = (LinearLayout) findViewById(R.id.resolution_view);
        subtitle_view = (LinearLayout) findViewById(R.id.subtitle_view);

        resolution_text = (TextView) findViewById(R.id.resolution_text);
        subtitle_text = (TextView) findViewById(R.id.subtitle_text);

        resolution_text.setText("Quality: " + Util.VideoResolution);
        subtitle_text.setText("Subtitle: " + Util.DefaultSubtitle);

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

        if (SubTitleName.size() < 1) {
            subtitle_view.setVisibility(View.GONE);
        }
        if (ResolutionFormat.size() < 1) {
            resolution_view.setVisibility(View.GONE);
        }

        Animation topTobottom = AnimationUtils.loadAnimation(this, R.anim.bottom_top);
        slidelayout.startAnimation(topTobottom);


        resolution_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Subtitle_Resolution.this, ResolutionChangeActivity.class);
                intent.putExtra("ResolutionFormat", ResolutionFormat);
                intent.putExtra("ResolutionUrl", ResolutionUrl);
                intent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
                startActivity(intent);
                finish();
            }
        });


        subtitle_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Subtitle_Resolution.this, SubtitleList.class);
                intent.putExtra("SubTitleName", SubTitleName);
                intent.putExtra("SubTitlePath", SubTitlePath);
                intent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
                startActivity(intent);
                finish();
            }
        });

        main_layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Intent playerIntent = new Intent();
                playerIntent.putExtra("position", "nothing");
                playerIntent.putExtra("type", "subtitle_resolution");
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
        playerIntent.putExtra("type", "subtitle_resolution");
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
