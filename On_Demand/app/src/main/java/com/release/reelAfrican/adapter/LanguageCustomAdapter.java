package com.release.reelAfrican.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.release.reelAfrican.R;
import com.release.reelAfrican.model.LanguageModel;
import com.release.reelAfrican.utils.Util;

import java.util.ArrayList;

public class LanguageCustomAdapter extends RecyclerView.Adapter<LanguageCustomAdapter.ViewHolder> {
    public Context mContext;
    boolean isLanguageSelected = false;
    public ArrayList<LanguageModel> languageModels;

    public LanguageCustomAdapter(Context mContext, ArrayList<LanguageModel> languageModelArrayList) {
        this.mContext = mContext;
        this.languageModels = languageModelArrayList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.language_recycler_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.language.setText(languageModels.get(position).getLanguageName());
      /*  if (Util.getTextofLanguage(mContext, Util.SELECTED_LANGUAGE_CODE, Util.DEFAULT_SELECTED_LANGUAGE_CODE).equalsIgnoreCase(languageModels.get(position).getLanguageId())){
            holder.imageView.setImageResource(R.drawable.selected);
        }else{
            holder.imageView.setImageResource(R.drawable.unselected);

        }*/


        if(Util.itemclicked)
        {
            Util.itemclicked = false;
           // holder.imageView.setImageResource(R.drawable.selected);
            if (languageModels.get(position).getIsSelected() == true){
                holder.imageView.setImageResource(R.drawable.selected);

            }else{
                holder.imageView.setImageResource(R.drawable.unselected);
            }
        }
        else
        {
            if (Util.getTextofLanguage(mContext,Util.SELECTED_LANGUAGE_CODE,Util.DEFAULT_SELECTED_LANGUAGE_CODE).equals(languageModels.get(position).getLanguageId())){
                holder.imageView.setImageResource(R.drawable.selected);


            }else{
                holder.imageView.setImageResource(R.drawable.unselected);

            }

        }

    }

    @Override
    public int getItemCount() {
        return languageModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView language;
        public ImageView imageView;

        public ViewHolder(View v) {

            super(v);
            imageView = (ImageView) v.findViewById(R.id.image);
            language = (TextView) v.findViewById(R.id.language);

        }
    }

}

