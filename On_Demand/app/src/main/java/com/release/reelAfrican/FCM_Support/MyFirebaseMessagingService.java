package com.release.reelAfrican.FCM_Support;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.release.reelAfrican.activity.MainActivity;
import com.release.reelAfrican.utils.Util;

import org.json.JSONException;
import org.json.JSONObject;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "BIBHU2";
    String MESSAGE = "";


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e(TAG, "From: " + remoteMessage.getFrom());

        if (remoteMessage == null)
            return;

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.e(TAG, "Notification Body: " + remoteMessage.getNotification().getBody());
            handleNotification(remoteMessage.getNotification().getBody());
        }

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0){
            Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());

            try {
                JSONObject json = new JSONObject(remoteMessage.getData().toString());
                handleDataMessage(json);
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }
    }

    private void handleNotification(String message) {
        Log.e(TAG, "Notification message : " + message);

        MESSAGE = message;

        Handler handler = new Handler(Looper.getMainLooper());

       /* handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(),"notification="+MESSAGE,Toast.LENGTH_LONG).show();
            }
        });*/
    }


    private void handleDataMessage(JSONObject json) {
        Log.e(TAG, "push json: " + json.toString());
        try {
            JSONObject data = json.getJSONObject("data");

            String user_id = data.optString("user_id");
            String message = data.optString("message");
            MESSAGE = message;

            Log.e(TAG, "user_id: =======" + user_id);
            Log.e(TAG, "message: ==========" + message);

          /*  Handler handler = new Handler(Looper.getMainLooper());

            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(),"data="+MESSAGE,Toast.LENGTH_LONG).show();
                }
            });*/

            SharedPreferences pref = getApplicationContext().getSharedPreferences(Util.LOGIN_PREF, 0);
            String loggedInStr = pref.getString("PREFS_LOGGEDIN_ID_KEY", "0");

            if (pref != null) {
                if(loggedInStr.trim().equals(user_id.trim())){
                    SharedPreferences.Editor editor = pref.edit();
                    editor.clear();
                    editor.commit();

                    Util.Call_API_For_Close_Streming = true;

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }

        } catch (JSONException e) {
            Log.e(TAG, "Json Exception: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

}