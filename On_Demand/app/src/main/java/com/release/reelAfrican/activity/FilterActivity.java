package com.release.reelAfrican.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.crashlytics.android.Crashlytics;
import com.release.reelAfrican.R;
import com.release.reelAfrican.adapter.FilterAdapter;
import com.release.reelAfrican.model.FilterListModel;
import com.release.reelAfrican.utils.Util;

import java.util.ArrayList;

import io.fabric.sdk.android.Fabric;

public class FilterActivity extends AppCompatActivity {


    private Button resetButton;
    private Button applyButton;
    private FilterAdapter genreAdapter;
    RecyclerView genreListData;
    String filterOrderByStr = "lastupload";
    int prevPosition = 5;
    ArrayList<String> genreArray;
    ArrayList<Integer> genreIndexArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());

        setContentView(R.layout.activity_filter);
        if (VideosListFragment.genreArray!=null && VideosListFragment.genreArray.size() > 0) {
            genreArray = VideosListFragment.genreArray;
        }else{
            genreArray = new ArrayList<String>();

        }
        if (VideosListFragment.filterOrderByStr!=null && !VideosListFragment.filterOrderByStr.matches("")) {
            filterOrderByStr = VideosListFragment.filterOrderByStr;
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back));
       // toolbar.setTitle(R.string.app_name);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        resetButton = (Button)findViewById(R.id.resetButton);
        applyButton = (Button)findViewById(R.id.applyButton);
        genreListData = (RecyclerView) findViewById(R.id.demoListView);
        LinearLayoutManager linearLayout = new LinearLayoutManager(FilterActivity.this, LinearLayoutManager.VERTICAL, false);
        genreListData.setLayoutManager(linearLayout);
        genreListData.setItemAnimator(new DefaultItemAnimator());
        final ArrayList<FilterListModel> mdata = new ArrayList<FilterListModel>();


        SharedPreferences isLoginPref = getSharedPreferences(Util.IS_LOGIN_SHARED_PRE, 0); // 0 - for private mode

        String genreString = isLoginPref.getString(Util.GENRE_ARRAY_PREF_KEY, null);
        String genreValuesString = isLoginPref.getString(Util.GENRE_VALUES_ARRAY_PREF_KEY, null);
        final String[] genreTempArr = genreString.split(",");
        String[] genreValuesTempArr = genreValuesString.split(",");

        for (int i = 0; i < genreTempArr.length; i++) {
            mdata.add(new FilterListModel(genreTempArr[i], genreValuesTempArr[i]));
            if (i == 0){
                mdata.set(0, new FilterListModel(Util.getTextofLanguage(FilterActivity.this,Util.FILTER_BY,Util.DEFAULT_FILTER_BY), genreValuesTempArr[i]));
            }
            if (i  == genreTempArr.length - 5) {
                mdata.set(i, new FilterListModel(Util.getTextofLanguage(FilterActivity.this, Util.SORT_BY, Util.DEFAULT_SORT_BY), genreValuesTempArr[i]));
            }
            if (i  == genreTempArr.length - 4) {
                mdata.set(i, new FilterListModel(Util.getTextofLanguage(FilterActivity.this, Util.SORT_LAST_UPLOADED, Util.DEFAULT_SORT_LAST_UPLOADED), genreValuesTempArr[i]));
            }
            if (i  == genreTempArr.length - 3) {
                mdata.set(i, new FilterListModel(Util.getTextofLanguage(FilterActivity.this, Util.SORT_RELEASE_DATE, Util.DEFAULT_SORT_RELEASE_DATE), genreValuesTempArr[i]));
            }
            if (i  == genreTempArr.length - 2) {
                mdata.set(i, new FilterListModel(Util.getTextofLanguage(FilterActivity.this, Util.SORT_ALPHA_A_Z, Util.DEFAULT_SORT_ALPHA_A_Z), genreValuesTempArr[i]));
            }
            if (i  == genreTempArr.length - 1) {
                mdata.set(i, new FilterListModel(Util.getTextofLanguage(FilterActivity.this, Util.SORT_ALPHA_Z_A, Util.DEFAULT_SORT_ALPHA_Z_A), genreValuesTempArr[i]));
            }
        }





        genreAdapter = new FilterAdapter(mdata,FilterActivity.this);
        genreListData.setAdapter(genreAdapter);
        if (mdata.size() > 0) {

            if (VideosListFragment.filterOrderByStr != null && filterOrderByStr.matches("lastupload")) {
                prevPosition = mdata.size() - 4;

            } else if (VideosListFragment.filterOrderByStr != null && filterOrderByStr.matches("releasedate")) {
                prevPosition = mdata.size() - 3;

            } else if (VideosListFragment.filterOrderByStr != null && filterOrderByStr.matches("sortasc")) {
                prevPosition = mdata.size() - 2;

            } else if (VideosListFragment.filterOrderByStr != null && filterOrderByStr.matches("sortdesc")) {
                prevPosition = mdata.size() - 1;

            } else {
                prevPosition = mdata.size() - 4;

            }
        }


        if (genreArray.size() > 0) {
            for (int i = 0; i < mdata.size(); i++) {
                for (int j = 0; j < genreArray.size(); j++) {
                    if (mdata.get(i).getSectionType().equalsIgnoreCase(genreArray.get(j))) {
                        mdata.get(i).setSelected(true);

                    }
                }
            }
        }
       mdata.get(prevPosition).setSelected(true);
        genreListData.addOnItemTouchListener(new RecyclerTouchListener(FilterActivity.this, genreListData, new ClickListener() {
            @Override
            public void onClick(View view, int position) {


                if (position >= 1 && position <= (genreTempArr.length - 6)) {
                    if (mdata.get(position).isSelected() == true) {
                        mdata.get(position).setSelected(false);

                        for (int i = 0; i < genreArray.size(); i++) {
                            if (genreArray.contains(mdata.get(position).getSectionType())) {
                                genreArray.remove(mdata.get(position).getSectionType());
                            }
                        }


                    } else {
                        genreArray.add(mdata.get(position).getSectionType());
                        mdata.get(position).setSelected(true);
                    }
                }

                if (position >= (genreTempArr.length - 4)) {
                    mdata.get(position).setSelected(true);
                    filterOrderByStr = mdata.get(position).getSectionType();
                    if (prevPosition != position) {
                        mdata.get(prevPosition).setSelected(false);
                        prevPosition = position;

                    }

                }

                genreAdapter.notifyDataSetChanged();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

       resetButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               VideosListFragment.genreArray = null;
               VideosListFragment.filterOrderByStr ="";
               VideosListFragment.clearClicked = true;
               finish();

           }
       });
        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("ANURADHA","genrearray length======"+genreArray.size());
                Log.v("ANURADHA"," filterOrderByStr======"+filterOrderByStr);
                if (genreArray!=null && genreArray.size() > 0) {
                    VideosListFragment.genreArray = genreArray;
                    VideosListFragment.filterOrderByStr =filterOrderByStr;
                    VideosListFragment.clearClicked = false;
                    Log.v("ANURADHA"," genreArray======"+filterOrderByStr);

                } /*if (filterOrderByStr!=null) {
                    VideosListFragment.genreArray = null;
                    VideosListFragment.filterOrderByStr =filterOrderByStr;
                    VideosListFragment.clearClicked = false;

                }*/

                else{
                    VideosListFragment.genreArray = null;
                    VideosListFragment.filterOrderByStr =filterOrderByStr;
                    VideosListFragment.clearClicked = false;
                }
                finish();

            }
        });
    }
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

}
