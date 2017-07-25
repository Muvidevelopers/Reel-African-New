package com.release.reelAfrican.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.release.reelAfrican.R;
import com.release.reelAfrican.adapter.PlanAdapter;
import com.release.reelAfrican.model.PlanModel;
import com.release.reelAfrican.utils.ProgressBarHandler;
import com.release.reelAfrican.utils.Util;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import io.fabric.sdk.android.Fabric;

public class SubscriptionActivity extends AppCompatActivity {
    RecyclerView subcription;
    ArrayList<PlanModel> movieList=new ArrayList<PlanModel>();
    Button activation_plan;
    LinearLayoutManager mLayoutManager;
    PlanAdapter mAdapter;
    int corePoolSize = 60;
    int maximumPoolSize = 80;
    int keepAliveTime = 10;
    String planId;
    TextView subscriptionTitleTextView;
    int selected_subscription_plan = 0 ;
    ProgressBarHandler progressBarHandler;
    BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<Runnable>(maximumPoolSize);
    Executor threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, workQueue);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());

        setContentView(R.layout.activity_subscription);
        subcription= (RecyclerView) findViewById(R.id.recyclerViewSubscription);
        activation_plan= (Button) findViewById(R.id.activationplan);
        Toolbar toolbar= (Toolbar) findViewById(R.id.toolbar);
        subscriptionTitleTextView = (TextView) findViewById(R.id.subscriptionTitleTextView);

        if ((Util.getTextofLanguage(SubscriptionActivity.this, Util.IS_ONE_STEP_REGISTRATION, Util.DEFAULT_IS_ONE_STEP_REGISTRATION)
                .trim()).equals("1")) {
            toolbar.setNavigationIcon(null);
            toolbar.setTitle(getResources().getString(R.string.app_name));
        }
        else
        {
            toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back));
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        Typeface typeface = Typeface.createFromAsset(getAssets(),getResources().getString(R.string.regular_fonts));
        subscriptionTitleTextView.setTypeface(typeface);
        subscriptionTitleTextView.setText(Util.getTextofLanguage(SubscriptionActivity.this,Util.SELECT_PLAN,Util.DEFAULT_SELECT_PLAN)+" :");

        Typeface typeface1 = Typeface.createFromAsset(getAssets(),getResources().getString(R.string.regular_fonts));
        activation_plan.setTypeface(typeface1);
        activation_plan.setText(Util.getTextofLanguage(SubscriptionActivity.this,Util.ACTIAVTE_PLAN_TITLE,Util.DEFAULT_ACTIAVTE_PLAN_TITLE));


        mLayoutManager = new LinearLayoutManager(SubscriptionActivity.this, LinearLayoutManager.VERTICAL, false);
        if(Util.checkNetwork(SubscriptionActivity.this)) {
            AsynLoadPlanDetails asynLoadPlanDetails = new AsynLoadPlanDetails();
            asynLoadPlanDetails.executeOnExecutor(threadPoolExecutor);
        }



        activation_plan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.v("SUBHA","Chek for content click ="+Util.check_for_subscription);

                Intent intentpayment=new Intent(SubscriptionActivity.this,PaymentInfoActivity.class);
                intentpayment.putExtra("currencyId",movieList.get(selected_subscription_plan).getPlanCurrencyIdStr());
                intentpayment.putExtra("currencyCountryCode",movieList.get(selected_subscription_plan).getCurrencyCountryCodeStr());
                intentpayment.putExtra("currencySymbol",movieList.get(selected_subscription_plan).getPlanCurrencySymbolstr());
                intentpayment.putExtra("price",movieList.get(selected_subscription_plan).getPurchaseValueStr());
                intentpayment.putExtra("selected_plan_id",movieList.get(selected_subscription_plan).getPlanIdStr());
                startActivity(intentpayment);
                finish();

            }
        });

    }




    private class AsynLoadPlanDetails extends AsyncTask<Void, Void, Void> {
        ProgressDialog pDialog;
        String responseStr;
        int status;
        int prevPosition = 0;
        String planIdStr;
        String planNamestr;
        String planStudioIdStr;
        String planPriceStr;
        String planRecurrenceStr;
        String planFrequencyStr;
        int planStatusStr = 0;
        String planLanguage_idStr;
        String currencyIdStr;
        String currencyCountryCodeStr;
        String currencySymbolStr;
        String currencyTrialPeriodStr;
        String currencyTrialRecurrenceStr;

        @Override
        protected Void doInBackground(Void... params) {

            try {
                HttpClient httpclient=new DefaultHttpClient();
                HttpPost httppost = new HttpPost(Util.rootUrl().trim()+Util.getStudioPlanLists.trim());
                httppost.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");
                httppost.addHeader("authToken", Util.authTokenStr.trim());
                httppost.addHeader("lang_code", Util.getTextofLanguage(SubscriptionActivity.this, Util.SELECTED_LANGUAGE_CODE, Util.DEFAULT_SELECTED_LANGUAGE_CODE));



                // Execute HTTP Post Request
                try {
                    HttpResponse response = httpclient.execute(httppost);
                    responseStr = EntityUtils.toString(response.getEntity());

                } catch (org.apache.http.conn.ConnectTimeoutException e){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                          /*  noInternetConnectionLayout.setVisibility(View.VISIBLE);
                            noDataLayout.setVisibility(View.GONE);
                            subscriptionPlanRecyclerView.setVisibility(View.GONE);*/

                            Util.showToast(SubscriptionActivity.this,Util.getTextofLanguage(SubscriptionActivity.this,Util.SLOW_INTERNET_CONNECTION,Util.DEFAULT_SLOW_INTERNET_CONNECTION));
                           // Toast.makeText(SubscriptionActivity.this,Util.getTextofLanguage(SubscriptionActivity.this,Util.SLOW_INTERNET_CONNECTION,Util.DEFAULT_SLOW_INTERNET_CONNECTION), Toast.LENGTH_LONG).show();

                        }

                    });

                }catch (IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            /*noInternetConnectionLayout.setVisibility(View.GONE);
                            noDataLayout.setVisibility(View.VISIBLE);
                            subscriptionPlanRecyclerView.setVisibility(View.GONE);
*/
                        }

                    });
                    e.printStackTrace();
                }

                JSONObject myJson =null;
                if(responseStr!=null){
                    myJson = new JSONObject(responseStr);

                    Log.v("SUBHA", "responseStr" + responseStr);
                    status = Integer.parseInt(myJson.optString("code"));
                }
                if (status > 0) {

                    if (status == 200) {

                        JSONArray jsonMainNode = myJson.getJSONArray("plans");

                        int lengthJsonArr = jsonMainNode.length();
                        for(int i=0; i < lengthJsonArr; i++) {
                            JSONObject jsonChildNode;
                            try {
                                jsonChildNode = jsonMainNode.getJSONObject(i);
                                {


                                    if ((jsonChildNode.has("id")) && jsonChildNode.getString("id").trim() != null && !jsonChildNode.getString("id").trim().isEmpty() && !jsonChildNode.getString("id").trim().equals("null") && !jsonChildNode.getString("id").trim().matches("")) {
                                        planIdStr = jsonChildNode.getString("id");
                                    } else {
                                        planIdStr = Util.getTextofLanguage(SubscriptionActivity.this,Util.NO_DATA,Util.DEFAULT_NO_DATA);

                                    }

                                    if ((jsonChildNode.has("name")) && jsonChildNode.getString("name").trim() != null && !jsonChildNode.getString("name").trim().isEmpty() && !jsonChildNode.getString("name").trim().equals("null") && !jsonChildNode.getString("name").trim().matches("")) {
                                        planNamestr = jsonChildNode.getString("name");
                                    } else {
                                        planNamestr = Util.getTextofLanguage(SubscriptionActivity.this,Util.NO_DATA,Util.DEFAULT_NO_DATA);


                                    }

                                    if ((jsonChildNode.has("recurrence")) && jsonChildNode.getString("recurrence").trim() != null && !jsonChildNode.getString("recurrence").trim().isEmpty() && !jsonChildNode.getString("recurrence").trim().equals("null") && !jsonChildNode.getString("recurrence").trim().matches("")) {
                                        planRecurrenceStr = jsonChildNode.getString("recurrence");
                                    } else {
                                        planRecurrenceStr = Util.getTextofLanguage(SubscriptionActivity.this,Util.NO_DATA,Util.DEFAULT_NO_DATA);

                                    }

                                    if ((jsonChildNode.has("frequency")) && jsonChildNode.getString("frequency").trim() != null && !jsonChildNode.getString("frequency").trim().isEmpty() && !jsonChildNode.getString("frequency").trim().equals("null") && !jsonChildNode.getString("frequency").trim().matches("")) {
                                        planFrequencyStr = jsonChildNode.getString("frequency");
                                    } else {
                                        planFrequencyStr = Util.getTextofLanguage(SubscriptionActivity.this,Util.NO_DATA,Util.DEFAULT_NO_DATA);

                                    }

                                    if ((jsonChildNode.has("studio_id")) && jsonChildNode.getString("studio_id").trim() != null && !jsonChildNode.getString("studio_id").trim().isEmpty() && !jsonChildNode.getString("studio_id").trim().equals("null") && !jsonChildNode.getString("studio_id").trim().matches("")) {
                                        planStudioIdStr = jsonChildNode.getString("studio_id");
                                    } else {
                                        planStudioIdStr =Util.getTextofLanguage(SubscriptionActivity.this,Util.NO_DATA,Util.DEFAULT_NO_DATA);

                                    }

                                    if ((jsonChildNode.has("status")) && jsonChildNode.getString("status").trim() != null && !jsonChildNode.getString("status").trim().isEmpty() && !jsonChildNode.getString("status").trim().equals("null") && !jsonChildNode.getString("status").trim().matches("")) {
                                        planStatusStr = Integer.parseInt(jsonChildNode.getString("status"));
                                    } else {
                                        planStatusStr = 0;

                                    }

                                    if ((jsonChildNode.has("language_id")) && jsonChildNode.getString("language_id").trim() != null && !jsonChildNode.getString("language_id").trim().isEmpty() && !jsonChildNode.getString("language_id").trim().equals("null") && !jsonChildNode.getString("language_id").trim().matches("")) {
                                        planLanguage_idStr = jsonChildNode.getString("language_id");
                                    } else {
                                        planLanguage_idStr = Util.getTextofLanguage(SubscriptionActivity.this,Util.NO_DATA,Util.DEFAULT_NO_DATA);

                                    }
                                    if ((jsonChildNode.has("price")) && jsonChildNode.getString("price").trim() != null && !jsonChildNode.getString("price").trim().isEmpty() && !jsonChildNode.getString("price").trim().equals("null") && !jsonChildNode.getString("price").trim().matches("")) {
                                        planPriceStr = jsonChildNode.getString("price");
                                    } else {
                                        planPriceStr = Util.getTextofLanguage(SubscriptionActivity.this,Util.NO_DATA,Util.DEFAULT_NO_DATA);

                                    }
                                    if (jsonChildNode.has("trial_period") && jsonChildNode.getString("trial_period").trim() != null && !jsonChildNode.getString("trial_period").trim().isEmpty() && !jsonChildNode.getString("trial_period").trim().equals("null") && !jsonChildNode.getString("trial_period").trim().matches("")) {
                                        currencyTrialPeriodStr = jsonChildNode.getString("trial_period");
                                    } else {
                                        currencyTrialPeriodStr = "";
                                    }

                                    if (jsonChildNode.has("trial_recurrence") && jsonChildNode.getString("trial_recurrence").trim() != null && !jsonChildNode.getString("trial_recurrence").trim().isEmpty() && !jsonChildNode.getString("trial_recurrence").trim().equals("null") && !jsonChildNode.getString("trial_recurrence").trim().matches("")) {
                                        currencyTrialRecurrenceStr = jsonChildNode.getString("trial_recurrence");
                                    } else {
                                        currencyTrialRecurrenceStr = "";
                                    }

                                    if (jsonChildNode.has("currency")) {
                                        JSONObject currencyJson = jsonChildNode.getJSONObject("currency");
                                        if (currencyJson.has("id") && currencyJson.getString("id").trim() != null && !currencyJson.getString("id").trim().isEmpty() && !currencyJson.getString("id").trim().equals("null") && !currencyJson.getString("id").trim().matches("")) {
                                            currencyIdStr = currencyJson.getString("id");
                                        } else {
                                            currencyIdStr = "";

                                        }
                                        if (currencyJson.has("country_code") && currencyJson.getString("country_code").trim() != null && !currencyJson.getString("country_code").trim().isEmpty() && !currencyJson.getString("country_code").trim().equals("null") && !currencyJson.getString("country_code").trim().matches("")) {
                                            currencyCountryCodeStr = currencyJson.getString("country_code");
                                        } else {
                                            currencyCountryCodeStr = "";
                                        }
                                        if (currencyJson.has("symbol") && currencyJson.getString("symbol").trim() != null && !currencyJson.getString("symbol").trim().isEmpty() && !currencyJson.getString("symbol").trim().equals("null") && !currencyJson.getString("symbol").trim().matches("")) {
                                            currencySymbolStr = currencyJson.getString("symbol");
                                        } else {
                                            currencySymbolStr = "";
                                        }

                                    }
                                    if (planStatusStr == 1) {
                                        if (i == 0) {
                                            planId = planIdStr;
                                            movieList.add(new PlanModel(planNamestr, planPriceStr, planRecurrenceStr, planFrequencyStr, true, planStudioIdStr, planStatusStr, planLanguage_idStr, planIdStr, currencyIdStr, currencySymbolStr, currencyTrialPeriodStr, currencyTrialRecurrenceStr,currencyCountryCodeStr));

                                        } else {
                                            movieList.add(new PlanModel(planNamestr, planPriceStr, planRecurrenceStr, planFrequencyStr, false, planStudioIdStr, planStatusStr, planLanguage_idStr, planIdStr, currencyIdStr, currencySymbolStr,currencyTrialPeriodStr,currencyTrialRecurrenceStr,currencyCountryCodeStr));
                                            Log.v("SUBHA","movieList"+movieList.size());
                                        }
                                    }
                                }

                            } catch (Exception e) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                       /* noDataLayout.setVisibility(View.VISIBLE);
                                        noInternetConnectionLayout.setVisibility(View.GONE);
                                        subscriptionPlanRecyclerView.setVisibility(View.GONE);
*/

                                    }
                                });
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                    }

                }

                else{
                    responseStr = "0";

                }
            } catch (JSONException e1) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                       /* noInternetConnectionLayout.setVisibility(View.GONE);
                        noDataLayout.setVisibility(View.VISIBLE);
                        subscriptionPlanRecyclerView.setVisibility(View.GONE);*/




                    }

                });
                responseStr = "0";
                e1.printStackTrace();
            }

            catch (Exception e)
            {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                      /*  noInternetConnectionLayout.setVisibility(View.GONE);
                        noDataLayout.setVisibility(View.VISIBLE);
                        subscriptionPlanRecyclerView.setVisibility(View.GONE);
*/


                    }

                });
                responseStr = "0";
                e.printStackTrace();

            }
            return null;

        }

        protected void onPostExecute(Void result) {
            try{
                if(progressBarHandler.isShowing())
                    progressBarHandler.hide();
            }
            catch(IllegalArgumentException ex)
            {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                       /* noInternetConnectionLayout.setVisibility(View.GONE);
                        noDataLayout.setVisibility(View.VISIBLE);
                        subscriptionPlanRecyclerView.setVisibility(View.GONE);
*/

                    }

                });
                responseStr = "0";
            }
            if(responseStr == null)
                responseStr = "0";

            if((responseStr.trim().equals("0"))){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                       /* noInternetConnectionLayout.setVisibility(View.GONE);
                        noDataLayout.setVisibility(View.VISIBLE);
                        subscriptionPlanRecyclerView.setVisibility(View.GONE);*/


                    }

                });
                Util.showToast(SubscriptionActivity.this,Util.getTextofLanguage(SubscriptionActivity.this,Util.NO_DETAILS_AVAILABLE,Util.DEFAULT_NO_DETAILS_AVAILABLE));
                //Toast.makeText(SubscriptionActivity.this,Util.getTextofLanguage(SubscriptionActivity.this,Util.NO_DETAILS_AVAILABLE,Util.DEFAULT_NO_DETAILS_AVAILABLE), Toast.LENGTH_LONG).show();
                onBackPressed();

            }else{
              /*  noInternetConnectionLayout.setVisibility(View.GONE);
                noDataLayout.setVisibility(View.GONE);
                creditCardLayout.setVisibility(View.VISIBLE);*/
                subcription.setVisibility(View.VISIBLE);
                subcription.setLayoutManager(mLayoutManager);
                subcription.setItemAnimator(new DefaultItemAnimator());

                mAdapter = new PlanAdapter(SubscriptionActivity.this,movieList);
                subcription.setAdapter(mAdapter);
                activation_plan.setVisibility(View.VISIBLE);
                subcription.addOnItemTouchListener(new VideosListFragment.RecyclerTouchListener(SubscriptionActivity.this, subcription, new VideosListFragment.ClickListener() {
                    @Override
                    public void onClick(View view, int position) {

                        selected_subscription_plan = position;
                        //Toast.makeText(getApplicationContext(),""+selected_subscription_plan,Toast.LENGTH_LONG).show();

                        if (position > 0) {
                            movieList.get(prevPosition).setSelected(false);
                            prevPosition = position;

                        } else if (position == 0 && prevPosition > position) {
                            movieList.get(prevPosition).setSelected(false);
                            prevPosition = position;

                        }
                        planId = movieList.get(position).getPlanIdStr();
                        movieList.get(position).setSelected(true);
                        mAdapter.notifyDataSetChanged();


                    }

                    @Override
                    public void onLongClick(View view, int position) {
                    }
                }));
                  /* planName.setText(planNamestr);

                if(planPriceStr.matches("") || planPriceStr.matches(getResources().getString(R.string.no_data_str))){
                    purchaseValue.setVisibility(View.INVISIBLE);
                }else{
                    purchaseValue.setVisibility(View.VISIBLE);

                    purchaseValue.setText(planPriceStr);

                }
                if(planRecurrenceStr.matches("") || planRecurrenceStr.matches(getResources().getString(R.string.no_data_str))){
                    freeTrial.setVisibility(View.INVISIBLE);
                }else{
                    freeTrial.setVisibility(View.VISIBLE);
                    planStudioIdStr = Util.formateDateFromstring("yyyy-mm-dd", "mm-dd-yyyy", planRecurrenceStr);

                    freeTrial.setText(planRecurrenceStr);

                }*/

            }
        }

        @Override
        protected void onPreExecute() {

            progressBarHandler = new ProgressBarHandler(SubscriptionActivity.this);
            progressBarHandler.show();
        }


    }
    @Override
    public void onBackPressed()
    {
        finish();
        overridePendingTransition(0, 0);
        super.onBackPressed();
    }
}
