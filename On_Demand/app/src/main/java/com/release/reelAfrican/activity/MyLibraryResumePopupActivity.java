 package com.release.reelAfrican.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.release.reelAfrican.R;
import com.release.reelAfrican.utils.SensorOrientationChangeNotifier;
import com.release.reelAfrican.utils.Util;

public class MyLibraryResumePopupActivity extends Activity implements SensorOrientationChangeNotifier.Listener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resume_playing);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
        LinearLayout mainLayout = (LinearLayout) findViewById(R.id.mainlayout);

        LinearLayout popupLayout = (LinearLayout) findViewById(R.id.popupLayout);
        Button yesButton = (Button) findViewById(R.id.yesButton);
        Button cancelButton = (Button) findViewById(R.id.cancelButton);
        TextView resumeTitleTextView = (TextView) findViewById(R.id.resumeTitleTextView);
        resumeTitleTextView.setText(Util.getTextofLanguage(MyLibraryResumePopupActivity.this, Util.RESUME_MESSAGE, Util.DEFAULT_RESUME_MESSAGE));
        yesButton.setText(Util.getTextofLanguage(MyLibraryResumePopupActivity.this, Util.CONTINUE_BUTTON, Util.DEAFULT_CONTINUE_BUTTON));
        cancelButton.setText(Util.getTextofLanguage(MyLibraryResumePopupActivity.this, Util.CANCEL_BUTTON, Util.DEAFULT_CANCEL_BUTTON));

        Animation topTobottom = AnimationUtils.loadAnimation(this, R.anim.top_bottom);
        popupLayout.startAnimation(topTobottom);
        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent playerIntent = new Intent();
                playerIntent.putExtra("yes", "1002");
                setResult(RESULT_OK, playerIntent);
                finish();
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent playerIntent = new Intent();
                playerIntent.putExtra("yes", "1003");
                setResult(RESULT_OK, playerIntent);
                finish();
            }
        });
        mainLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
               /* Intent playerIntent = new Intent();
                playerIntent.putExtra("yes", "1003");
                setResult(RESULT_OK, playerIntent);
                finish();*/
                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        SensorOrientationChangeNotifier.getInstance(MyLibraryResumePopupActivity.this).addListener(this);
    }

    @Override
    public void onOrientationChange(int orientation) {

        if (orientation == 90) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
        } else if (orientation == 270) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }
}
