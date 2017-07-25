package com.release.reelAfrican.utils;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.release.reelAfrican.R;

/**
 * Created by User on 10-02-2017.
 */
public class ProgressBarHandler {
    private ProgressBar mProgressBar;
    private Context mContext;
    RelativeLayout bar;
    ViewGroup layout;
    boolean indeterminate = true;
    public ProgressBarHandler(Context context) {
        mContext = context;

        layout = (ViewGroup) ((Activity) context).findViewById(android.R.id.content).getRootView();

    /*    mProgressBar = new ProgressBar(context, null, android.R.attr.progressBarStyleInverse);
        mProgressBar.setIndeterminate(true);
        mProgressBar.setIndeterminateDrawable(context.getResources().getDrawable(R.drawable.progress_rawable));
        RelativeLayout.LayoutParams params = new
                RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);*/

        //RelativeLayout rl = new RelativeLayout(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        bar = (RelativeLayout) inflater.inflate(R.layout.progress_bar_layout, null);
      //  rl.setGravity(Gravity.CENTER);
//        rl.addView(mProgressBar);


       // hide();
    }

    public void show() {
        layout.addView(bar);

       // mProgressBar.setVisibility(View.VISIBLE);
    }
    public boolean isShowing() {
        if(layout.indexOfChild(bar) == -1){
            return false;
        }else{
            return true;

        }

        // mProgressBar.setVisibility(View.VISIBLE);
    }
    public void hide() {

        layout.removeView(bar);

        //mProgressBar.setVisibility(View.INVISIBLE);
    }
}
