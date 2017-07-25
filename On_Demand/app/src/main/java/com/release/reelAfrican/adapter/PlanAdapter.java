package com.release.reelAfrican.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.release.reelAfrican.R;
import com.release.reelAfrican.model.PlanModel;

import java.util.ArrayList;

/**
 * Created by Muvi on 9/6/2016.
 */
public class PlanAdapter extends RecyclerView.Adapter<PlanAdapter.MyViewHolder> {

    public static int prevPosition = 0;
    boolean value=false;
    Context context;
    private ArrayList<PlanModel> moviesList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView planName, purchaseValue, freeTrial,subcriptionmonth,planPurchaseCurrenyTextView;
        private ImageView snap;
        RelativeLayout relativeplannamelayout;
        LinearLayout lineardetails;



        public MyViewHolder(View view) {
            super(view);
            planName = (TextView) view.findViewById(R.id.planNameTextView);
            purchaseValue = (TextView) view.findViewById(R.id.planPurchaseValueTextView);
            subcriptionmonth = (TextView) view.findViewById(R.id.planPurchaseMonthTextView);
            relativeplannamelayout = (RelativeLayout) view.findViewById(R.id.relativeplannamelayout);
            planPurchaseCurrenyTextView=(TextView) view.findViewById(R.id.planPurchaseCurrenyTextView);
            lineardetails = (LinearLayout) view.findViewById(R.id.lineardetails);
            snap = (ImageView) view.findViewById(R.id.select_planMark);
           /* freeTrial = (TextView) view.findViewById(R.id.freeTrialTextView);
            */
        }
    }


    public PlanAdapter(Context context, ArrayList<PlanModel> moviesList) {
        this.moviesList = moviesList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.plan_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder,  final int position) {
         final PlanModel movie = moviesList.get(position);


      if (movie.isSelected() == false){

           holder.relativeplannamelayout.setBackgroundColor(context.getResources().getColor(R.color.textColor));
           holder.lineardetails.setBackgroundColor(context.getResources().getColor(R.color.episodeBackgroundColor));
            holder.snap.setVisibility(View.INVISIBLE);
        }else{
           holder.relativeplannamelayout.setBackgroundColor(context.getResources().getColor(R.color.button_background));
           holder.lineardetails.setBackgroundColor(context.getResources().getColor(R.color.episodeBackgroundColor));
           holder.snap.setVisibility(View.VISIBLE);

        }
        /*if(position==0){

            holder.snap.setVisibility(View.VISIBLE);
        }*/

        holder.subcriptionmonth.setText("(" +movie.getPlanFrequencuStr()+" "+movie.getPlanRecurrenceStr() +")");
        Log.v("SUBHA","name"+movie.getPlanRecurrenceStr());
        holder.planName.setText(movie.getPlanNameStr());
        holder.purchaseValue.setText(movie.getPurchaseValueStr());
        holder.planPurchaseCurrenyTextView.setText(movie.getPlanCurrencySymbolstr());
        /*if (!movie.getPlanTrialPeriodStr().equalsIgnoreCase("0") || !movie.getPlanTrialRecurrenceStr().equalsIgnoreCase("")) {
            holder.freeTrial.setText(movie.getPlanTrialPeriodStr() + " " + movie.getPlanTrialRecurrenceStr() + " (s) "+ Util.getTextofLanguage(context,Util.FREE,Util.DEFAULT_FREE)+" !");
        }else{
           // holder.freeTrial.setVisibility(View.GONE);

        }*/
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


               /* holder.relativeplannamelayout.setBackgroundColor(Color.parseColor("#ff0000"));
                holder.lineardetails.setBackgroundColor(Color.parseColor("#ffffff"));
                holder.snap.setVisibility(View.VISIBLE);*/


               /* Intent in = new Intent(context, Episode_list_Activity.class);
                in.putExtra("PERMALINK",mContentItems.get(position).getPermalink());
                activity.startActivity(in);*/
            }
        });

    }

    @Override
    public int getItemCount() {
        return moviesList.size();
    }

}
