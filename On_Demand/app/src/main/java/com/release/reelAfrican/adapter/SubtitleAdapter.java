package com.release.reelAfrican.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.release.reelAfrican.R;

import java.util.ArrayList;

/**
 * Created by MUVI on 3/10/2017.
 */


public class SubtitleAdapter extends BaseAdapter {
    private Context context; //context
    private ArrayList<String> items; //data source of the list adapter

    //public constructor
    public SubtitleAdapter(Context context, ArrayList<String> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size(); //returns total of items in the list
    }

    @Override
    public Object getItem(int position) {
        return items.get(position); //returns list item at the specified position
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // inflate the layout for each list row
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.subtitile_list_layout, parent, false);
        }

        // get the TextView for item name and item description
        TextView title = (TextView)convertView.findViewById(R.id.title);

        //sets the text for item name and item description from the current item object
        title.setText(items.get(position));

        Typeface typeface = Typeface.createFromAsset(context.getAssets(),context.getResources().getString(R.string.regular_fonts));
        title.setTypeface(typeface);


        // returns the view for the current row
        return convertView;
    }
}
