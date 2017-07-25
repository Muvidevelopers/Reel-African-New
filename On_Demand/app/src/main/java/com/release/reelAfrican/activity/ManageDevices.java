package com.release.reelAfrican.activity;

import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.release.reelAfrican.R;
import com.release.reelAfrican.adapter.DeviceListAdapter;
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

public class ManageDevices extends AppCompatActivity {
    SharedPreferences loginPref;
    String User_Id = "";
    String Email_Id = "";
    TextView name_of_user;


    ArrayList<String>DeviceName = new ArrayList<>();
    ArrayList<String>DeviceFalg = new ArrayList<>();
    ArrayList<String>DeviceInfo = new ArrayList<>();
    ListView device_list;
    TextView manage_device_text;


    // load asynctask
    int corePoolSize = 60;
    int maximumPoolSize = 80;
    int keepAliveTime = 10;
    BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<Runnable>(maximumPoolSize);
    Executor threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, workQueue);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_devices);
        loginPref = getSharedPreferences(Util.LOGIN_PREF, 0);
        device_list = (ListView) findViewById(R.id.device_list);
        manage_device_text = (TextView) findViewById(R.id.manage_device_text);

        manage_device_text.setText("  "+Util.getTextofLanguage(ManageDevices.this,Util.YOUR_DEVICE,Util.DEFAULT_YOUR_DEVICE));
        Typeface typeface = Typeface.createFromAsset(getAssets(), getResources().getString(R.string.regular_fonts));
        manage_device_text.setTypeface(typeface);


        Toolbar mActionBarToolbar= (Toolbar) findViewById(R.id.toolbar);
        mActionBarToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back));
        mActionBarToolbar.setTitle(Util.getTextofLanguage(ManageDevices.this,Util.MANAGE_DEVICE,Util.DEFAULT_MANAGE_DEVICE));
        mActionBarToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        if (loginPref != null) {
            User_Id = loginPref.getString("PREFS_LOGGEDIN_ID_KEY", null);
            Email_Id = loginPref.getString("PREFS_LOGIN_EMAIL_ID_KEY", null);
        }

        AsynLoadRegisteredDevices asynLoadRegisteredDevices = new AsynLoadRegisteredDevices();
        asynLoadRegisteredDevices.executeOnExecutor(threadPoolExecutor);
    }

    private class AsynLoadRegisteredDevices extends AsyncTask<Void, Void, Void> {
        ProgressBarHandler pDialog;
        String responseStr="0";
        int statusCode;
        String message;

        @Override
        protected Void doInBackground(Void... params) {

            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(Util.rootUrl().trim() + Util.ManageDevices.trim());
                httppost.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");
                httppost.addHeader("authToken", Util.authTokenStr.trim());
                httppost.addHeader("user_id",User_Id);
                httppost.addHeader("device", Settings.Secure.getString(getContentResolver(),Settings.Secure.ANDROID_ID));
                httppost.addHeader("lang_code",Util.getTextofLanguage(ManageDevices.this,Util.SELECTED_LANGUAGE_CODE,Util.DEFAULT_SELECTED_LANGUAGE_CODE));

                // Execute HTTP Post Request
                try {

                    HttpResponse response = httpclient.execute(httppost);
                    responseStr = EntityUtils.toString(response.getEntity());
                    Log.v("BIBHU","responseStr of device list ="+responseStr);


                } catch (org.apache.http.conn.ConnectTimeoutException e) {
                    ManageDevices.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ManageDevices.this, Util.getTextofLanguage(ManageDevices.this, Util.SLOW_INTERNET_CONNECTION, Util.DEFAULT_SLOW_INTERNET_CONNECTION), Toast.LENGTH_LONG).show();

                        }

                    });

                } catch (IOException e) {
                    e.printStackTrace();
                }

                JSONObject myJson = null;
                if (responseStr != null) {
                    myJson = new JSONObject(responseStr);
                    statusCode = Integer.parseInt(myJson.optString("code"));
                    message = myJson.optString("msg");
                }

            } catch (JSONException e1) {
                e1.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;

        }

        protected void onPostExecute(Void result) {

            try {
                if (pDialog != null && pDialog.isShowing()) {
                    pDialog.hide();
                    pDialog = null;
                }
            } catch (IllegalArgumentException ex) {
            }
            if (responseStr != null) {
                if (statusCode==200) {
                    // Start parsing Here

                    try {
                        JSONObject myJson = new JSONObject(responseStr);
                        JSONArray jsonArray = myJson.optJSONArray("device_list");

                        for(int i=0 ;i<jsonArray.length();i++)
                        {
                            DeviceName.add(jsonArray.optJSONObject(i).optString("device").trim());
                            DeviceInfo.add(jsonArray.optJSONObject(i).optString("device_info").trim());
                            DeviceFalg.add(jsonArray.optJSONObject(i).optString("flag").trim());
                        }

                        DeviceListAdapter adapter = new DeviceListAdapter(ManageDevices.this,DeviceName,DeviceInfo,DeviceFalg);
                        device_list.setAdapter(adapter);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else
                {
                    // Show The Error Message Here
                    Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
                    finish();
                }
            }
            else{
                // Show Try Again Msg and finish here.
                Toast.makeText(getApplicationContext(),  Util.getTextofLanguage(ManageDevices.this,Util.TRY_AGAIN,Util.DEFAULT_TRY_AGAIN),Toast.LENGTH_LONG).show();
                finish();
            }


        }

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressBarHandler(ManageDevices.this);
            pDialog.show();
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
