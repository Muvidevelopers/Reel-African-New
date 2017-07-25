package com.release.reelAfrican.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.release.reelAfrican.R;
import com.release.reelAfrican.model.NavDrawerItem;
import com.release.reelAfrican.utils.Util;

import java.util.Collections;
import java.util.List;

/**
 * Created by  Sujit on 2-01-2017.
 */
public class NavigationDrawerAdapter extends RecyclerView.Adapter<NavigationDrawerAdapter.MyViewHolder> {
    List<NavDrawerItem> data = Collections.emptyList();
    private LayoutInflater inflater;
    private Context context;

    public NavigationDrawerAdapter(Context context, List<NavDrawerItem> data) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;
    }

    public void delete(int position) {
        data.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.nav_drawer_row, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        NavDrawerItem current = data.get(position);
        holder.title.setText(Html.fromHtml(current.getTitle().toString().toLowerCase()));

/*

        if(current.getTitle().toString().contains(Util.getTextofLanguage(context,Util.MY_LIBRARY,Util.DEFAULT_MY_LIBRARY)))
        {
            holder.textViewLine.setVisibility(View.VISIBLE);
            Util.my_library_visibility = true;
            Util.drawer_line_visibility = false;
        }
        else
        {
            Util.my_library_visibility = false;
        }
*/

        if (current.getIsEnabled() == false) {

            if (position > 0) {
                if (Util.drawer_line_visibility && (data.get(position - 1)).getIsEnabled()) {
                    holder.textViewLine.setVisibility(View.VISIBLE);

                    Util.drawer_line_visibility = false;

                }
                else
                {
                    holder.textViewLine.setVisibility(View.INVISIBLE);
                    Util.drawer_line_visibility = true;

                }
            }
        } else {

            /*if(!current.getTitle().toString().contains(Util.getTextofLanguage(context,Util.MY_LIBRARY,Util.DEFAULT_MY_LIBRARY)))
            {
                holder.textViewLine.setVisibility(View.INVISIBLE);
                Util.drawer_line_visibility = true;
            }*/

            holder.textViewLine.setVisibility(View.INVISIBLE);
            Util.drawer_line_visibility = true;

        }
/*

        if (data.size() == position + 1) {
            Util.drawer_line_visibility = true;

        }
*/


    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title,textViewLine;
        ImageView iconImageView;
        public MyViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            textViewLine = (TextView) itemView.findViewById(R.id.textViewLine);

            Typeface custom_name = Typeface.createFromAsset(context.getAssets(), context.getResources().getString(R.string.regular_fonts));
            title.setTypeface(custom_name);
            // iconImageView = (ImageView) itemView.findViewById(R.id.navIconImageView);

        }
    }
}
