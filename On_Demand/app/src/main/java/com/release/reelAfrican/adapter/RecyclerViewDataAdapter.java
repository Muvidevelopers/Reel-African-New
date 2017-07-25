package com.release.reelAfrican.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.release.reelAfrican.R;
import com.release.reelAfrican.activity.ViewMoreActivity;
import com.release.reelAfrican.model.SectionDataModel;
import com.release.reelAfrican.model.SingleItemModel;
import com.release.reelAfrican.utils.Util;

import java.util.ArrayList;

import static android.content.res.Configuration.SCREENLAYOUT_SIZE_LARGE;
import static android.content.res.Configuration.SCREENLAYOUT_SIZE_MASK;
import static android.content.res.Configuration.SCREENLAYOUT_SIZE_XLARGE;

public  class RecyclerViewDataAdapter extends RecyclerView.Adapter<RecyclerViewDataAdapter.ItemRowHolder>{
    ArrayList<SingleItemModel> singleSectionItems;
    private ArrayList<SectionDataModel> dataList;
    private Context mContext;
    private  boolean firstTime;
    private ArrayList<String> bannerUrls = new ArrayList<String>();
    String pemalink;
    String image;
    int vertical = 0;
    boolean loaded = false;
    int counter=0;

    /*int banner[] = {R.drawable.slider,R.drawable.slider1,R.drawable.slider2,R.drawable.slider3,R.drawable.slider4,R.drawable.slider5};
    int bannerL[] = {R.drawable.slider,R.drawable.slider1,R.drawable.slider2,R.drawable.slider3,R.drawable.slider4,R.drawable.slider5};

*/
    //Forgot Password Dialog


    public RecyclerViewDataAdapter(Context context, ArrayList<SectionDataModel> dataList, ArrayList<String> bannerUrls,boolean firstTime,int vertical) {
        this.dataList = dataList;
        this.mContext = context;
        this.bannerUrls = bannerUrls;
        this.firstTime = firstTime;
        this.vertical = vertical;

    }
   /* public void swapItems(){
        loaded = true;
    }*/
    @Override
    public ItemRowHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        float density = mContext.getResources().getDisplayMetrics().density;
        Log.v("SUBHA","density === "+ density);
        if(density >= 1.5 && density <= 3.0){
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_small, null);
            ItemRowHolder mh = new ItemRowHolder(v);
            return mh;

        }else {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item, null);
            ItemRowHolder mh = new ItemRowHolder(v);
            return mh;
        }
    }

    @Override
    public void onBindViewHolder(final ItemRowHolder itemRowHolder, final int i) {

        if(i>=counter)
        {
            counter = i;

            final String sectionName = dataList.get(i).getHeaderTitle();
            final String sectionId = dataList.get(i).getHeaderPermalink();

            singleSectionItems = dataList.get(i).getAllItemsInSection();
            pemalink=dataList.get(i).getHeaderPermalink();
            for (int j = 0; j > bannerUrls.size(); j++) {
                image = bannerUrls.get(j);
            }
            Typeface castDescriptionTypeface = Typeface.createFromAsset(mContext.getAssets(),mContext.getResources().getString(R.string.regular_fonts));

            itemRowHolder.itemTitle.setTypeface(castDescriptionTypeface);
            itemRowHolder.itemTitle.setText(sectionName);
            SectionListDataAdapter itemListDataAdapter = null;
            if (Util.ori == 1) {
                float density = mContext.getResources().getDisplayMetrics().density;
                if (density >= 3.5 && density <= 4.0) {
                    itemListDataAdapter = new SectionListDataAdapter(mContext, singleSectionItems, R.layout.list_single_card);
                }else  if (density <= 1.5) {
                    itemListDataAdapter = new SectionListDataAdapter(mContext, singleSectionItems, R.layout.list_single_card_small);
                }else{
                    itemListDataAdapter = new SectionListDataAdapter(mContext, singleSectionItems, R.layout.list_single_card_nexus);
                }
                //  itemListDataAdapter = new SectionListDataAdapter(mContext, singleSectionItems, R.layout.list_single_card);

            }else{
                itemListDataAdapter = new SectionListDataAdapter(mContext, singleSectionItems, R.layout.home_280_card);

            }
            itemRowHolder.recycler_view_list.setHasFixedSize(true);
            itemRowHolder.recycler_view_list.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
            itemRowHolder.recycler_view_list.setAdapter(itemListDataAdapter);

            itemRowHolder.btnMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    Context context = v.getContext();
                    Intent i = new Intent(context, ViewMoreActivity.class);
                    i.putExtra("SectionId", sectionId);
                    i.putExtra("sectionName", sectionName);
                    context.startActivity(i);
                }
            });

            if (i == 0){
                itemRowHolder.mDemoSliderLayout.setVisibility(View.VISIBLE);
            }else{
                itemRowHolder.mDemoSliderLayout.setVisibility(View.GONE);

            }
            Log.v("SUBHA","hggf"+singleSectionItems.size());
          //  itemRowHolder.btnMore.setVisibility(View.VISIBLE);

            if (singleSectionItems.size() <= 0) {
                itemRowHolder.itemTitle.setVisibility(View.GONE);
                itemRowHolder.btnMore.setVisibility(View.GONE);
               // itemRowHolder.recycler_view_list.setVisibility(View.GONE);
            } else  if (singleSectionItems.size() == 1) {
                itemRowHolder.btnMore.setVisibility(View.GONE);
               // itemRowHolder.recycler_view_list.setVisibility(View.GONE);

            }else{
                itemRowHolder.btnMore.setVisibility(View.VISIBLE);
               // itemRowHolder.recycler_view_list.setVisibility(View.VISIBLE);


            }

       /* if ((mContext.getResources().getConfiguration().screenLayout & SCREENLAYOUT_SIZE_MASK) == SCREENLAYOUT_SIZE_LARGE) {
            if (singleSectionItems.size() >= 5) {
                itemRowHolder.btnMore.setVisibility(View.VISIBLE);
            } else {
                itemRowHolder.btnMore.setVisibility(View.GONE);
            }
        }
        else if ((mContext.getResources().getConfiguration().screenLayout & SCREENLAYOUT_SIZE_MASK) == SCREENLAYOUT_SIZE_NORMAL) {
            if (singleSectionItems.size() >= 2) {
                itemRowHolder.btnMore.setVisibility(View.VISIBLE);
            } else {
                itemRowHolder.btnMore.setVisibility(View.GONE);
            }
        }
        else if ((mContext.getResources().getConfiguration().screenLayout & SCREENLAYOUT_SIZE_MASK) == SCREENLAYOUT_SIZE_SMALL) {
            if (singleSectionItems.size() >= 2) {
                itemRowHolder.btnMore.setVisibility(View.VISIBLE);
            } else {
                itemRowHolder.btnMore.setVisibility(View.GONE);
            }
        }else{
            if (singleSectionItems.size() >= 5) {
                itemRowHolder.btnMore.setVisibility(View.VISIBLE);
            } else {
                itemRowHolder.btnMore.setVisibility(View.GONE);
            }
        }
*/
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
    @Override
    public int getItemViewType(int position) {
        return position;
    }



    public class ItemRowHolder extends RecyclerView.ViewHolder implements  BaseSliderView.OnSliderClickListener,ViewPagerEx.OnPageChangeListener{

        protected TextView itemTitle;

        protected RecyclerView recycler_view_list;

        protected Button btnMore;
        private SliderLayout mDemoSlider;
        private RelativeLayout mDemoSliderLayout;

        public ItemRowHolder(View view) {
            super(view);

            this.itemTitle = (TextView) view.findViewById(R.id.itemTitle);
            this.recycler_view_list = (RecyclerView) view.findViewById(R.id.featureContent);
            this.btnMore= (Button) view.findViewById(R.id.btnMore);
            Typeface watchTrailerButtonTypeface = Typeface.createFromAsset(mContext.getAssets(),mContext.getResources().getString(R.string.regular_fonts));
            this.btnMore.setTypeface(watchTrailerButtonTypeface);
            this.btnMore.setText(Util.getTextofLanguage(mContext, Util.VIEW_MORE, Util.DEFAULT_VIEW_MORE));

            mDemoSlider = (SliderLayout) view.findViewById(R.id.sliderLayout);
            mDemoSliderLayout = (RelativeLayout) view.findViewById(R.id.sliderRelativeLayout);


            /*if(bannerUrls.size()>=0 && firstTime == false){
                firstTime = true;
                  for (int i = 0; i < bannerUrls.size(); i++) {


                      // initialize a SliderLayout
               *//* if (((mContext.getResources().getConfiguration().screenLayout & SCREENLAYOUT_SIZE_MASK) == SCREENLAYOUT_SIZE_LARGE) || ((mContext.getResources().getConfiguration().screenLayout & SCREENLAYOUT_SIZE_MASK) == SCREENLAYOUT_SIZE_XLARGE)) {
                    for (int j = 0; j < bannerL.length ;j ++) {
                        DefaultSliderView textSliderView = new DefaultSliderView(view.getContext());
                        textSliderView
                                .description("")
                                .image(bannerL[j])
                                .setScaleType(BaseSliderView.ScaleType.Fit)
                                .setOnSliderClickListener(this);
                        textSliderView.bundle(new Bundle());
                        textSliderView.getBundle()
                                .putString("extra", "");

                        mDemoSlider.addSlider(textSliderView);
                    }
                }else{
                    for (int j = 0; j < banner.length;j ++) {
                        DefaultSliderView textSliderView = new DefaultSliderView(view.getContext());
                        textSliderView
                                .description("")
                                .image(banner[j])
                                .setScaleType(BaseSliderView.ScaleType.Fit)
                                .setOnSliderClickListener(this);
                        textSliderView.bundle(new Bundle());
                        textSliderView.getBundle()
                                .putString("extra", "");

                        mDemoSlider.addSlider(textSliderView);
                    }
                }
*//*
                      DefaultSliderView textSliderView = new DefaultSliderView(view.getContext());

                      textSliderView
                              .description("")
                              .image(bannerUrls.get(i))
                              .setScaleType(BaseSliderView.ScaleType.CenterInside)
                              .setOnSliderClickListener(this);
                      mDemoSlider.addSlider(textSliderView);

                  }
                //add your extra information
                *//*    textSliderView.bundle(new Bundle());
                    textSliderView.getBundle()
                            .putString("extra", "");
                    mDemoSlider.addSlider(textSliderView);*//*
                // }
            }else{
                DefaultSliderView textSliderView = new DefaultSliderView(view.getContext());
                // initialize a SliderLayout
                //  String image=bannerUrls.get(i)

                textSliderView
                        .description("")
                        .image("https://d2gx0xinochgze.cloudfront.net/public/no-image-a.png")
                        .setScaleType(BaseSliderView.ScaleType.CenterInside)
                        .setOnSliderClickListener(this);


                //add your extra information
                textSliderView.bundle(new Bundle());
                textSliderView.getBundle()
                        .putString("extra", "");

                mDemoSlider.addSlider(textSliderView);
            }*/

            if(firstTime == false) {
                firstTime = true;
           /* if(bannerUrls.size()>=0 && firstTime == false){
                firstTime = true;
                for (int i = 0; i < bannerUrls.size(); i++) {*/


                // initialize a SliderLayout
                if (((mContext.getResources().getConfiguration().screenLayout & SCREENLAYOUT_SIZE_MASK) == SCREENLAYOUT_SIZE_LARGE) || ((mContext.getResources().getConfiguration().screenLayout & SCREENLAYOUT_SIZE_MASK) == SCREENLAYOUT_SIZE_XLARGE)) {
                    for (int j = 0; j < bannerUrls.size(); j++) {
                        DefaultSliderView textSliderView = new DefaultSliderView(view.getContext());
                        textSliderView
                                .description("")
                                .image(bannerUrls.get(j))
                                .setScaleType(BaseSliderView.ScaleType.Fit)
                                .setOnSliderClickListener(this);
                        textSliderView.bundle(new Bundle());
                        textSliderView.getBundle()
                                .putString("extra", "");

                        mDemoSlider.addSlider(textSliderView);
                    }
                } else {
                    for (int j = 0; j < bannerUrls.size(); j++) {
                        DefaultSliderView textSliderView = new DefaultSliderView(view.getContext());
                        textSliderView
                                .description("")
                                .image(bannerUrls.get(j))
                                .setScaleType(BaseSliderView.ScaleType.Fit)
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

}