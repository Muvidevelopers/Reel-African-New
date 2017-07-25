package com.release.reelAfrican.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.release.reelAfrican.R;
import com.release.reelAfrican.model.GetCastCrewItem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CastCrewAdapter extends BaseAdapter{
    private Context mContext;
    private ArrayList<GetCastCrewItem> castCrewItems ;

    public CastCrewAdapter(Context mContext, ArrayList<GetCastCrewItem> castCrewItems) {
        this.mContext = mContext;
        this.castCrewItems = castCrewItems;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return castCrewItems.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View grid;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

//            grid = new View(mContext);
            grid = inflater.inflate(R.layout.cast_crew_layout, null);





        } else {
            grid = (View) convertView;

        }
        ImageView imageView = (ImageView)grid.findViewById(R.id.castImageView);
        TextView castNameTextView = (TextView) grid.findViewById(R.id.castNameTextView);
        TextView castDescriptionTextView = (TextView) grid.findViewById(R.id.castDescriptionTextView);
        Typeface custom_name = Typeface.createFromAsset(mContext.getAssets(),mContext.getResources().getString(R.string.regular_fonts));
        castNameTextView.setTypeface(custom_name);

        Typeface castDescriptionTypeface = Typeface.createFromAsset(mContext.getAssets(),mContext.getResources().getString(R.string.light_fonts));
        castDescriptionTextView.setTypeface(castDescriptionTypeface);

        castNameTextView.setText(castCrewItems.get(position).getTitle());
        castDescriptionTextView.setText(castCrewItems.get(position).getDesc());
//            imageView.setImageResource(castCrewItems.get(position).getCastImage());

        if(!castCrewItems.get(position).getCastImage().equals(""))
        {
            Picasso.with(mContext)
                    .load(castCrewItems.get(position).getCastImage())
                    .placeholder(R.drawable.logo)   // optional
                    .error(R.drawable.logo)      // optional
                    .into(imageView);
        }
        else
        {
            Picasso.with(mContext)
                    .load(R.drawable.logo)
                    .placeholder(R.drawable.logo)   // optional
                    .error(R.drawable.logo)      // optional
                    .into(imageView);
        }
        return grid;
    }
}