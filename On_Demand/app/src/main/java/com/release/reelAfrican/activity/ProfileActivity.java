package com.release.reelAfrican.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.release.reelAfrican.R;
import com.release.reelAfrican.utils.ProgressBarHandler;
import com.release.reelAfrican.utils.Util;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import io.fabric.sdk.android.Fabric;

public class ProfileActivity extends AppCompatActivity {
    SharedPreferences loginPref;

    ImageView bannerImageView;
    EditText editConfirmPassword, editNewPassword, editProfileNameEditText;
    EditText emailAddressEditText;
    Button changePassword, update_profile,manage_devices;

    String Name, Password;
    boolean password_visibility = false;

    String User_Id = "";
    String Email_Id = "";
    TextView name_of_user;


    // load asynctask
    int corePoolSize = 60;
    int maximumPoolSize = 80;
    int keepAliveTime = 10;
    BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<Runnable>(maximumPoolSize);
    Executor threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, workQueue);

    // Added for country and language spinner
    Spinner country_spinner,language_spinner;
    ArrayAdapter<String> Language_arrayAdapter,Country_arrayAdapter;

    String Selected_Language,Selected_Country="0",Selected_Language_Id,Selected_Country_Id;
    SharedPreferences countryPref;
    List<String> Country_List,Country_Code_List,Language_List,Language_Code_List;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());

        setContentView(R.layout.activity_profile);
        loginPref = getSharedPreferences(Util.LOGIN_PREF, 0);
        manage_devices = (Button) findViewById(R.id.manage_devices);
        bannerImageView = (ImageView) findViewById(R.id.bannerImageView);
        editNewPassword = (EditText) findViewById(R.id.editNewPassword);
        editConfirmPassword = (EditText) findViewById(R.id.editConfirmPassword);
        editProfileNameEditText = (EditText) findViewById(R.id.editProfileNameEditText);
        emailAddressEditText = (EditText) findViewById(R.id.emailAddressEditText);
        changePassword = (Button) findViewById(R.id.changePasswordButton);
        update_profile = (Button) findViewById(R.id.update_profile);
        editConfirmPassword.setVisibility(View.GONE);
        editNewPassword.setVisibility(View.GONE);
        name_of_user = (TextView)findViewById(R.id.name_of_user);

        country_spinner = (Spinner) findViewById(R.id.countrySpinner);
        language_spinner = (Spinner) findViewById(R.id.languageSpinner);
        language_spinner.setVisibility(View.GONE);
        country_spinner.setVisibility(View.GONE);

        Typeface editProfileNameEditTextTypeface = Typeface.createFromAsset(getAssets(),getResources().getString(R.string.light_fonts));
        editProfileNameEditText.setTypeface(editProfileNameEditTextTypeface);


        Typeface editConfirmPasswordTypeface = Typeface.createFromAsset(getAssets(),getResources().getString(R.string.light_fonts));
        editConfirmPassword.setTypeface(editConfirmPasswordTypeface);


        Typeface editNewPasswordTypeface = Typeface.createFromAsset(getAssets(),getResources().getString(R.string.light_fonts));
        editNewPassword.setTypeface(editNewPasswordTypeface);


        InputFilter filter = new InputFilter() {
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

                for (int i = start; i < end; i++) {

                    if (source.charAt(i) == '\n') {
                        return " ";
                    }
                }
                return null;
            }
        };

        editNewPassword.setFilters(new InputFilter[]{filter});
        editConfirmPassword.setFilters(new InputFilter[]{filter});
        editProfileNameEditText.setFilters(new InputFilter[]{filter});

        Typeface changePasswordTypeface = Typeface.createFromAsset(getAssets(),getResources().getString(R.string.regular_fonts));
        changePassword.setTypeface(changePasswordTypeface);

        Typeface update_profileTypeface = Typeface.createFromAsset(getAssets(),getResources().getString(R.string.regular_fonts));
        update_profile.setTypeface(update_profileTypeface);

        editProfileNameEditText.setHint(Util.getTextofLanguage(ProfileActivity.this,Util.NAME_HINT,Util.DEFAULT_NAME_HINT));
        editConfirmPassword.setHint(Util.getTextofLanguage(ProfileActivity.this,Util.CONFIRM_PASSWORD,Util.DEFAULT_CONFIRM_PASSWORD));
        editNewPassword.setHint(Util.getTextofLanguage(ProfileActivity.this,Util.NEW_PASSWORD,Util.DEFAULT_NEW_PASSWORD));
        changePassword.setText(Util.getTextofLanguage(ProfileActivity.this,Util.CHANGE_PASSWORD,Util.DEFAULT_CHANGE_PASSWORD));
        update_profile.setText(Util.getTextofLanguage(ProfileActivity.this,Util.UPDATE_PROFILE,Util.DEFAULT_UPDATE_PROFILE));


        Toolbar mActionBarToolbar= (Toolbar) findViewById(R.id.toolbar);
        mActionBarToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back));
        mActionBarToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


       /* User_Id = getIntent().getStringExtra("LOGID");
        Email_Id = getIntent().getStringExtra("EMAIL");
*/
        if (loginPref != null) {
            User_Id = loginPref.getString("PREFS_LOGGEDIN_ID_KEY", null);
            Email_Id = loginPref.getString("PREFS_LOGIN_EMAIL_ID_KEY", null);
        }

        if(!Util.getTextofLanguage(ProfileActivity.this,Util.IS_RESTRICT_DEVICE,Util.DEFAULT_IS_RESTRICT_DEVICE).trim().equals("1"))
        {
            manage_devices.setVisibility(View.GONE);
        }

        manage_devices.setText(Util.getTextofLanguage(ProfileActivity.this,Util.MANAGE_DEVICE,Util.DEFAULT_MANAGE_DEVICE));
        manage_devices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isNetwork = Util.checkNetwork(ProfileActivity.this);
                if(isNetwork)
                {
                    Intent intent = new Intent(ProfileActivity.this,ManageDevices.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(ProfileActivity.this,Util.getTextofLanguage(ProfileActivity.this,Util.NO_INTERNET_CONNECTION,Util.DEFAULT_NO_INTERNET_CONNECTION),Toast.LENGTH_LONG).show();
                }

            }
        });

        Typeface manage_typeface = Typeface.createFromAsset(getAssets(),getResources().getString(R.string.regular_fonts));
        manage_devices.setTypeface(manage_typeface);

        // This is used for language and country spunner

        countryPref = getSharedPreferences(Util.COUNTRY_PREF, 0);

        Country_List = Arrays.asList(getResources().getStringArray(R.array.country));
        Country_Code_List = Arrays.asList(getResources().getStringArray(R.array.countrycode));
        Language_List = Arrays.asList(getResources().getStringArray(R.array.languages));
        Language_Code_List = Arrays.asList(getResources().getStringArray(R.array.languagesCode));



        Language_arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.country_language_spinner, Language_List) {
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                Typeface externalFont = Typeface.createFromAsset(getAssets(),getResources().getString(R.string.light_fonts));
                ((TextView) v).setTypeface(externalFont);
                return v;
            }
            public View getDropDownView(int position,  View convertView,  ViewGroup parent) {
                View v =super.getDropDownView(position, convertView, parent);

                Typeface externalFont1 = Typeface.createFromAsset(getAssets(),getResources().getString(R.string.light_fonts));
                ((TextView) v).setTypeface(externalFont1);

                return v;
            }

        };

        language_spinner.setAdapter(Language_arrayAdapter);

        Country_arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.country_language_spinner, Country_List) {
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                Typeface externalFont = Typeface.createFromAsset(getAssets(),getResources().getString(R.string.light_fonts));
                ((TextView) v).setTypeface(externalFont);
                return v;
            }
            public View getDropDownView(int position,  View convertView,  ViewGroup parent) {
                View v =super.getDropDownView(position, convertView, parent);

                Typeface externalFont1 = Typeface.createFromAsset(getAssets(),getResources().getString(R.string.light_fonts));
                ((TextView) v).setTypeface(externalFont1);

                return v;
            }

        };

        country_spinner.setAdapter(Country_arrayAdapter);

        country_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                Selected_Country_Id = Country_Code_List.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        language_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Selected_Language_Id = Language_Code_List.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

// =======End ===========================//



        AsynLoadProfileDetails asynLoadProfileDetails = new AsynLoadProfileDetails();
        asynLoadProfileDetails.executeOnExecutor(threadPoolExecutor);


        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (changePassword.isClickable() && editConfirmPassword.isShown() && editNewPassword.isShown()) {


                    if (editConfirmPassword.getText().toString().trim() != null && !(editConfirmPassword.getText().toString().trim().equalsIgnoreCase(""))) {
                        if (Util.isConfirmPassword(editConfirmPassword.getText().toString(), editNewPassword.getText().toString()) == false) {
                            Util.showToast(ProfileActivity.this,Util.getTextofLanguage(ProfileActivity.this,Util.PASSWORDS_DO_NOT_MATCH,Util.DEFAULT_PASSWORDS_DO_NOT_MATCH));

                         //   Toast.makeText(ProfileActivity.this, Util.getTextofLanguage(ProfileActivity.this, Util.PASSWORDS_DO_NOT_MATCH, Util.DEFAULT_PASSWORDS_DO_NOT_MATCH), Toast.LENGTH_LONG).show();

                            editConfirmPassword.setText("");
                            editNewPassword.setText("");

                            return;

                        }
                        else{
                            boolean isNetwork = Util.checkNetwork(ProfileActivity.this);
                            if (isNetwork ) {
                                UpdateProfile();
                                editConfirmPassword.setText("");
                                editNewPassword.setText("");
                                editConfirmPassword.setVisibility(View.GONE);
                                editNewPassword.setVisibility(View.GONE);
                            }


                        }
                    }
                    if (editConfirmPassword.getText().toString().trim().equalsIgnoreCase("") || (editNewPassword.getText().toString().trim().equalsIgnoreCase(""))) {
                        editConfirmPassword.setText("");
                        editNewPassword.setText("");
                        editConfirmPassword.setVisibility(View.GONE);
                        editNewPassword.setVisibility(View.GONE);
                        editProfileNameEditText.requestFocus();
                    }

                } else {

                    editConfirmPassword.setVisibility(View.VISIBLE);
                    editNewPassword.setVisibility(View.VISIBLE);
                    editConfirmPassword.requestFocus();

                }



            }
        });

        update_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(editProfileNameEditText.getText().toString().matches("")){
                    ShowDialog(Util.getTextofLanguage(ProfileActivity.this,Util.FAILURE,Util.DEFAULT_FAILURE), Util.getTextofLanguage(ProfileActivity.this,Util.NAME_HINT,Util.DEFAULT_NAME_HINT));

                }
                else if (!editConfirmPassword.getText().toString().matches(editNewPassword.getText().toString().trim())){
                    ShowDialog(Util.getTextofLanguage(ProfileActivity.this,Util.FAILURE,Util.DEFAULT_FAILURE), Util.getTextofLanguage(ProfileActivity.this,Util.PASSWORDS_DO_NOT_MATCH,Util.DEFAULT_PASSWORDS_DO_NOT_MATCH));

                }else{
                    boolean isNetwork = Util.checkNetwork(ProfileActivity.this);
                    if (isNetwork) {
                        UpdateProfile();
                    }
                }
              /*  boolean isNetwork = Util.checkNetwork(ProfileActivity.this);

                if (isNetwork) {

                    Name = editProfileNameEditText.getText().toString().trim();
                    Password = editNewPassword.getText().toString().trim();

                    if (editOldPassword.getVisibility() == View.VISIBLE) {
                        if (!editProfileNameEditText.getText().toString().trim().equals("")
                                && !emailAddressEditText.getText().toString().trim().equals("")
                                && !editNewPassword.getText().toString().trim().equals("")
                                && !editOldPassword.getText().toString().trim().equals("")) {
                            if (editNewPassword.equals(editOldPassword)) {
                                password_visibility = true;
                                UpdateProfile();
                            } else {
                                ShowDialog(Util.getTextofLanguage(ProfileActivity.this,Util.FAILURE,Util.DEFAULT_FAILURE), Util.getTextofLanguage(ProfileActivity.this,Util.PASSWORDS_DO_NOT_MATCH,Util.DEFAULT_PASSWORDS_DO_NOT_MATCH));

                            }
                        } else {
                            ShowDialog(Util.getTextofLanguage(ProfileActivity.this,Util.FAILURE,Util.DEFAULT_FAILURE), Util.getTextofLanguage(ProfileActivity.this,Util.NO_RECORD,Util.DEFAULT_NO_RECORD));


                        }
                    } else {
                        if (!editProfileNameEditText.getText().toString().trim().equals("") && !emailAddressEditText.getText().toString().trim().equals("")) {
                            password_visibility = false;
                            UpdateProfile();
                        } else {
                            ShowDialog(Util.getTextofLanguage(ProfileActivity.this,Util.FAILURE,Util.DEFAULT_FAILURE), Util.getTextofLanguage(ProfileActivity.this,Util.NO_RECORD,Util.DEFAULT_NO_RECORD));

                        }
                    }

                } else {

                    ShowDialog(Util.getTextofLanguage(ProfileActivity.this,Util.FAILURE,Util.DEFAULT_FAILURE), Util.getTextofLanguage(ProfileActivity.this,Util.NO_INTERNET_CONNECTION,Util.DEFAULT_NO_INTERNET_CONNECTION));
                }*/
            }
        });

    }

    public void ShowDialog(String Title, String msg) {
        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(ProfileActivity.this, R.style.MyAlertDialogStyle);
        dlgAlert.setMessage(msg);
        dlgAlert.setTitle(Title);
        dlgAlert.setPositiveButton(Util.getTextofLanguage(ProfileActivity.this,Util.BUTTON_OK,Util.DEFAULT_BUTTON_OK), null);
        dlgAlert.setCancelable(false);
        dlgAlert.setPositiveButton(Util.getTextofLanguage(ProfileActivity.this,Util.BUTTON_OK,Util.DEFAULT_BUTTON_OK),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        dlgAlert.create().show();
    }

    public void UpdateProfile() {
        AsynUpdateProfile asyncLoadVideos = new AsynUpdateProfile();
        asyncLoadVideos.executeOnExecutor(threadPoolExecutor);
    }

    private class AsynUpdateProfile extends AsyncTask<Void, Void, Void> {
        ProgressBarHandler pDialog;

        int statusCode;
        String loggedInIdStr;
        String confirmPasswordStr = editNewPassword.getText().toString().trim();
        String nameStr = editProfileNameEditText.getText().toString().trim();

        String responseStr;
        JSONObject myJson = null;

        @Override
        protected Void doInBackground(Void... params) {
            if (loginPref != null) {
                loggedInIdStr = loginPref.getString("PREFS_LOGGEDIN_ID_KEY", null);
            }
// start
            String urlRouteList = Util.rootUrl().trim()+Util.updateProfileUrl.trim();
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(urlRouteList);
                httppost.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");
                httppost.addHeader("user_id", User_Id.trim());
                httppost.addHeader("authToken", Util.authTokenStr.trim());
                httppost.addHeader("name", nameStr.trim());
                if (!confirmPasswordStr.trim().equalsIgnoreCase("") && !confirmPasswordStr.isEmpty() && !confirmPasswordStr.equalsIgnoreCase("null") && !confirmPasswordStr.equalsIgnoreCase(null) && !confirmPasswordStr.equals(null) && !confirmPasswordStr.matches("")){
                    httppost.addHeader("password", confirmPasswordStr.trim());
                }
                httppost.addHeader("lang_code",Util.getTextofLanguage(ProfileActivity.this,Util.SELECTED_LANGUAGE_CODE,Util.DEFAULT_SELECTED_LANGUAGE_CODE));

                httppost.addHeader("custom_country", Selected_Country_Id);
                httppost.addHeader("custom_languages",Selected_Language_Id);

                // Execute HTTP Post Request
                try {
                    HttpResponse response = httpclient.execute(httppost);
                    responseStr = EntityUtils.toString(response.getEntity());

                } catch (org.apache.http.conn.ConnectTimeoutException e){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            statusCode = 0;
                            editConfirmPassword.setText("");
                            editNewPassword.setText("");
                            Util.showToast(ProfileActivity.this,Util.getTextofLanguage(ProfileActivity.this,Util.SLOW_INTERNET_CONNECTION,Util.DEFAULT_SLOW_INTERNET_CONNECTION));

                           // Toast.makeText(ProfileActivity.this, Util.getTextofLanguage(ProfileActivity.this, Util.SLOW_INTERNET_CONNECTION, Util.DEFAULT_SLOW_INTERNET_CONNECTION), Toast.LENGTH_LONG).show();

                        }

                    });

                } catch (IOException e) {
                    statusCode = 0;

                    e.printStackTrace();
                }
                if(responseStr!=null) {
                    myJson = new JSONObject(responseStr);
                    statusCode = Integer.parseInt(myJson.optString("code"));

                }

            }
            catch (Exception e) {
                statusCode = 0;

            }

            return null;
        }


        protected void onPostExecute(Void result) {

            if(responseStr == null){
                try {
                    if (pDialog != null && pDialog.isShowing()) {
                        pDialog.hide();
                        pDialog = null;
                    }
                } catch (IllegalArgumentException ex) {
                    statusCode = 0;

                }
                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(ProfileActivity.this, R.style.MyAlertDialogStyle);
                dlgAlert.setMessage(Util.getTextofLanguage(ProfileActivity.this, Util.UPDATE_PROFILE_ALERT, Util.DEFAULT_UPDATE_PROFILE_ALERT));
                dlgAlert.setTitle(Util.getTextofLanguage(ProfileActivity.this, Util.SORRY, Util.DEFAULT_SORRY));
                dlgAlert.setPositiveButton(Util.getTextofLanguage(ProfileActivity.this,Util.BUTTON_OK,Util.DEFAULT_BUTTON_OK), null);
                dlgAlert.setCancelable(false);
                dlgAlert.setPositiveButton(Util.getTextofLanguage(ProfileActivity.this,Util.BUTTON_OK,Util.DEFAULT_BUTTON_OK),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                editConfirmPassword.setText("");
                                editNewPassword.setText("");
                            }
                        });
                dlgAlert.create().show();
            }

            if (statusCode > 0) {

                if (statusCode == 200){
                    try {
                        if (pDialog != null && pDialog.isShowing()) {
                            pDialog.hide();
                            pDialog = null;
                        }
                    } catch (IllegalArgumentException ex) {
                        SharedPreferences.Editor editor = loginPref.edit();
                        if (myJson.has("name")) {
                            String displayNameStr = myJson.optString("name");
                            editor.putString("PREFS_LOGIN_DISPLAY_NAME_KEY", displayNameStr);
                        }
                        if (!confirmPasswordStr.trim().equalsIgnoreCase("") && !confirmPasswordStr.isEmpty() && !confirmPasswordStr.equalsIgnoreCase("null") && !confirmPasswordStr.equalsIgnoreCase(null) && !confirmPasswordStr.equals(null) && !confirmPasswordStr.matches("")){
                            editor.putString("PREFS_LOGGEDIN_PASSWORD_KEY", confirmPasswordStr.trim());

                        }
                        editor.commit();
                    }
                    SharedPreferences.Editor editor = loginPref.edit();
                    if (myJson.has("name")) {
                        String displayNameStr = myJson.optString("display_name");
                        editor.putString("name", displayNameStr);
                    }
                    if (!confirmPasswordStr.trim().equalsIgnoreCase("") && !confirmPasswordStr.isEmpty() && !confirmPasswordStr.equalsIgnoreCase("null") && !confirmPasswordStr.equalsIgnoreCase(null) && !confirmPasswordStr.equals(null) && !confirmPasswordStr.matches("")){
                        editor.putString("PREFS_LOGGEDIN_PASSWORD_KEY", confirmPasswordStr.trim());

                    }

                    editor.commit();
                    name_of_user.setText(editProfileNameEditText.getText().toString().trim());

                    Util.showToast(ProfileActivity.this,Util.getTextofLanguage(ProfileActivity.this,Util.PROFILE_UPDATED,Util.DEFAULT_PROFILE_UPDATED));

                 //   Toast.makeText(ProfileActivity.this, Util.getTextofLanguage(ProfileActivity.this, Util.PROFILE_UPDATED, Util.DEFAULT_PROFILE_UPDATED), Toast.LENGTH_SHORT).show();
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

                    if (name_of_user!=null) {
                        name_of_user.clearFocus();
                        name_of_user.setCursorVisible(false);
                    }
                    if (editConfirmPassword!=null) {
                        editConfirmPassword.clearFocus();

                    }
                    if (editNewPassword!=null) {
                        editNewPassword.clearFocus();
                    }
                   /* if (fullNameEditText != null) fullNameEditText.clearFocus();
                    if (passwordEditText != null) passwordEditText.clearFocus();
                    if (confirmPasswordEditText != null) confirmPasswordEditText.clearFocus();*/

                }else{

                    try {
                        if (pDialog != null && pDialog.isShowing()) {
                            pDialog.hide();
                            pDialog = null;
                        }
                    } catch (IllegalArgumentException ex) {
                        statusCode = 0;

                    }
                    AlertDialog.Builder dlgAlert = new AlertDialog.Builder(ProfileActivity.this, R.style.MyAlertDialogStyle);
                    dlgAlert.setMessage(Util.getTextofLanguage(ProfileActivity.this, Util.UPDATE_PROFILE_ALERT, Util.DEFAULT_UPDATE_PROFILE_ALERT));
                    dlgAlert.setTitle(Util.getTextofLanguage(ProfileActivity.this, Util.SORRY, Util.DEFAULT_SORRY));
                    dlgAlert.setPositiveButton(Util.getTextofLanguage(ProfileActivity.this,Util.BUTTON_OK,Util.DEFAULT_BUTTON_OK), null);
                    dlgAlert.setCancelable(false);
                    dlgAlert.setPositiveButton(Util.getTextofLanguage(ProfileActivity.this,Util.BUTTON_OK,Util.DEFAULT_BUTTON_OK),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    editConfirmPassword.setText("");
                                    editNewPassword.setText("");


                                }
                            });
                    dlgAlert.create().show();
                }
            }else{
                try {
                    if (pDialog != null && pDialog.isShowing()) {
                        pDialog.hide();
                        pDialog = null;
                    }
                } catch (IllegalArgumentException ex) {

                }
                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(ProfileActivity.this, R.style.MyAlertDialogStyle);
                dlgAlert.setMessage(Util.getTextofLanguage(ProfileActivity.this, Util.UPDATE_PROFILE_ALERT, Util.DEFAULT_UPDATE_PROFILE_ALERT));
                dlgAlert.setTitle(Util.getTextofLanguage(ProfileActivity.this, Util.SORRY, Util.DEFAULT_SORRY));
                dlgAlert.setPositiveButton(Util.getTextofLanguage(ProfileActivity.this,Util.BUTTON_OK,Util.DEFAULT_BUTTON_OK), null);
                dlgAlert.setCancelable(false);
                dlgAlert.setPositiveButton(Util.getTextofLanguage(ProfileActivity.this,Util.BUTTON_OK,Util.DEFAULT_BUTTON_OK),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                editConfirmPassword.setText("");
                                editNewPassword.setText("");
                            }
                        });
                dlgAlert.create().show();
            }

        }

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressBarHandler(ProfileActivity.this);
            pDialog.show();
        }


    }
    //Getting Profile Details from The Api

    private class AsynLoadProfileDetails extends AsyncTask<Void, Void, Void> {
        ProgressBarHandler pDialog;
        String responseStr;
        int statusCode;
        String name;
        String profileEmail;
        String profileImage;
        String langStr = "";
        String countryStr = "";

        @Override
        protected Void doInBackground(Void... params) {

            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(Util.rootUrl().trim() + Util.loadProfileUrl.trim());
                httppost.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");
                httppost.addHeader("authToken", Util.authTokenStr.trim());
                httppost.addHeader("user_id",User_Id);
                httppost.addHeader("email",Email_Id);
                httppost.addHeader("lang_code",Util.getTextofLanguage(ProfileActivity.this,Util.SELECTED_LANGUAGE_CODE,Util.DEFAULT_SELECTED_LANGUAGE_CODE));

                // Execute HTTP Post Request
                try {

                    HttpResponse response = httpclient.execute(httppost);
                    responseStr = EntityUtils.toString(response.getEntity());



                } catch (org.apache.http.conn.ConnectTimeoutException e) {
                    ProfileActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            responseStr = "0";
                            Util.showToast(ProfileActivity.this,Util.getTextofLanguage(ProfileActivity.this,Util.SLOW_INTERNET_CONNECTION,Util.DEFAULT_SLOW_INTERNET_CONNECTION));

                           // Toast.makeText(ProfileActivity.this, Util.getTextofLanguage(ProfileActivity.this, Util.SLOW_INTERNET_CONNECTION, Util.DEFAULT_SLOW_INTERNET_CONNECTION), Toast.LENGTH_LONG).show();

                        }

                    });

                } catch (IOException e) {
                    responseStr = "0";
                    e.printStackTrace();
                }

                Log.v("BIBHU","responseStr ="+responseStr);

                JSONObject myJson = null;
                if (responseStr != null) {
                    myJson = new JSONObject(responseStr);
                    statusCode = Integer.parseInt(myJson.optString("code"));

                }
                if (statusCode > 0) {
                    if (statusCode == 200) {

                        if ((myJson.has("display_name")) && myJson.optString("display_name").trim() != null && !myJson.optString("display_name").trim().isEmpty() && !myJson.optString("display_name").trim().equals("null") && !myJson.optString("display_name").trim().matches("")) {
                            name = myJson.getString("display_name");
                        } else {
                            name = "";

                        }

                        if ((myJson.has("email")) && myJson.optString("email").trim() != null && !myJson.optString("email").trim().isEmpty() && !myJson.optString("email").trim().equals("null") && !myJson.optString("email").trim().matches("")) {
                            profileEmail = myJson.optString("email");

                        } else {
                            profileEmail = "";

                        }
                        if ((myJson.has("profile_image")) && myJson.optString("profile_image").trim() != null && !myJson.optString("profile_image").trim().isEmpty() && !myJson.optString("profile_image").trim().equals("null") && !myJson.optString("profile_image").trim().matches("")) {
                            profileImage = myJson.optString("profile_image");


                        } else {
                            profileImage = Util.getTextofLanguage(ProfileActivity.this, Util.NO_DATA, Util.DEFAULT_NO_DATA);

                        }

                        /*JSONArray languageJson = null;
                        if ((myJson.has("custom_languages"))) {

                            languageJson = myJson.getJSONArray("custom_languages");

                            if (languageJson.length() > 0) {
                                Selected_Language_Id = languageJson.optString(0);
                                Log.v("BIBHU","Selected_Language_Id st jon parsing ="+Selected_Language_Id);
                            } else {
                                langStr = "";
                            }
                        } else {
                            langStr = "";
                        }




                        if ((myJson.has("custom_country")) && myJson.optString("custom_country").trim() != null && !myJson.optString("custom_country").trim().isEmpty() && !myJson.optString("custom_country").trim().equals("null") && !myJson.optString("custom_country").trim().matches("")) {
                            //countryPosition = Integer.parseInt(myJson.optString("custom_country"));
                            countryStr = myJson.getString("custom_country");
                            Selected_Country_Id = countryStr;

                        } else {
                            countryStr = "";

                        }*/


                    } else {
                        name = "";
                        profileEmail = "";
                        profileImage = Util.NO_DATA;

                    }
                } else {
                    responseStr = "0";
                }
            } catch (JSONException e1) {

                responseStr = "0";
                e1.printStackTrace();
            } catch (Exception e) {
                responseStr = "0";
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
                responseStr = "0";
            }
            if (responseStr == null) {
                responseStr = "0";
            }

            if ((responseStr.trim().equalsIgnoreCase("0"))) {

                editProfileNameEditText.setText("");
                emailAddressEditText.setText("");
                name_of_user.setText("");
//                bannerImageView.setImageResource(R.drawable.no_user_bg);
                bannerImageView.setAlpha(0.8f);

                // imagebg.setBackgroundColor(Color.parseColor("#969393"));
                bannerImageView.setImageResource(R.drawable.no_image);
            } else {


               /* if(Selected_Country_Id.equals("0"))
                {
                    country_spinner.setSelection(224);
                    Selected_Country_Id = Country_Code_List.get(224);
                    Log.v("BIBHU","country not  matched ="+Selected_Country+"=="+Selected_Country_Id);
                }
                else
                {
                    for(int i=0;i<Country_Code_List.size();i++)
                    {
                        if(Selected_Country_Id.trim().equals(Country_Code_List.get(i)))
                        {
                            country_spinner.setSelection(i);
                            Selected_Country_Id = Country_Code_List.get(i);

                            Log.v("BIBHU","country  matched ="+Selected_Country_Id+"=="+Selected_Country_Id);
                        }
                    }
                }
                Country_arrayAdapter.notifyDataSetChanged();



                    for(int i=0;i<Language_Code_List.size();i++)
                    {
                        if(Selected_Language_Id.trim().equals(Language_Code_List.get(i)))
                        {
                            language_spinner.setSelection(i);
                            Selected_Language_Id = Language_Code_List.get(i);

                            Log.v("BIBHU","Selected_Language_Id ="+Selected_Language_Id);
                        }
                    }
                    Language_arrayAdapter.notifyDataSetChanged();


*/



                editProfileNameEditText.setText(name);
                name_of_user.setText(name);
                emailAddressEditText.setText(profileEmail);
                if (profileImage.matches(Util.NO_DATA)) {
                    bannerImageView.setAlpha(0.8f);
                    bannerImageView.setImageResource(R.drawable.logo);
                } else {
                    Picasso.with(ProfileActivity.this)
                            .load(profileImage)
                            .placeholder(R.drawable.logo).error(R.drawable.logo).noFade().resize(200, 200).into(bannerImageView, new Callback() {

                        @Override
                        public void onSuccess() {

                            Bitmap bitmapFromPalette = ((BitmapDrawable) bannerImageView.getDrawable()).getBitmap();
                            Palette palette = Palette.generate(bitmapFromPalette);
                        }

                        @Override
                        public void onError() {
                            // reset your views to default colors, etc.
                            bannerImageView.setAlpha(0.8f);
                            bannerImageView.setImageResource(R.drawable.no_image);
                        }

                    });
                    if (profileImage != null && profileImage.length() > 0) {
                        int pos = profileImage.lastIndexOf("/");
                        String x = profileImage.substring(pos + 1, profileImage.length());

                        if (x.equalsIgnoreCase("no-user.png")) {
                            bannerImageView.setImageResource(R.drawable.no_image);
                            bannerImageView.setAlpha(0.8f);
                            //imagebg.setBackgroundColor(Color.parseColor("#969393"));

                        } else {
                            Picasso.with(ProfileActivity.this)
                                    .load(profileImage)
                                    .placeholder(R.drawable.logo).error(R.drawable.logo).noFade().resize(200, 200).into(bannerImageView, new Callback() {

                                @Override
                                public void onSuccess() {
                                    bannerImageView.setAlpha(0.3f);

                                }

                                @Override
                                public void onError() {
                                    bannerImageView.setImageResource(R.drawable.no_image);
                                    bannerImageView.setAlpha(0.8f);
                                    //imagebg.setBackgroundColor(Color.parseColor("#969393"));
                                }

                            });
                        }
                    }
                }

            }
        }

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressBarHandler(ProfileActivity.this);
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
    public void removeFocusFromViews(){
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        removeFocusFromViews();

    }

    @Override
    public void onPause() {
        super.onPause();

        removeFocusFromViews();

    }
}
