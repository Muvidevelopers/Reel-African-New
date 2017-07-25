package com.release.reelAfrican.activity;


import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.release.reelAfrican.R;
import com.release.reelAfrican.utils.ProgressBarHandler;
import com.release.reelAfrican.utils.Util;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * A simple {@link Fragment} subclass.
 */
public class AboutUs extends Fragment {
    String about;
    // TextView textView;
    Context context;
    ProgressBar progresBar;
    WebView webView;

    AsyncAboutUS asyncAboutUS;


    public AboutUs() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       /* getActionBar().setTitle(getArguments().getString(""));
        setHasOptionsMenu(true);*/

        View view = inflater.inflate(R.layout.fragment_about_us, container, false);
        context = getActivity();
        progresBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        webView = (WebView) view.findViewById(R.id.aboutUsWebView);
        asyncAboutUS = new AsyncAboutUS();
        asyncAboutUS.execute();
        TextView categoryTitle = (TextView) view.findViewById(R.id.categoryTitle);
        Typeface castDescriptionTypeface = Typeface.createFromAsset(context.getAssets(),context.getResources().getString(R.string.regular_fonts));
        categoryTitle.setTypeface(castDescriptionTypeface);
        categoryTitle.setText(Html.fromHtml(getArguments().getString("title")));

        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {

                        final Intent startIntent = new Intent(getActivity(), MainActivity.class);

                        getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                startIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                getActivity().startActivity(startIntent);

                                getActivity().finish();

                            }
                        });
                    }
                }
                return false;
            }
        });

        return view;

    }

    private ActionBar getActionBar() {
        return ((ActionBarActivity) getActivity()).getSupportActionBar();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
    }

    public class AsyncAboutUS extends AsyncTask<Void, Void, Void> {
        String responseStr;

        private ProgressBarHandler progressBarHandler = null;

        @Override
        protected void onPostExecute(Void aVoid) {
            progresBar.setVisibility(View.GONE);
            String bodyData = about;

          /*  textView.setMovementMethod(LinkMovementMethod.getInstance());
            textView.setText(getStyledTextFromHtml(bodyData));*/

            String text = "<html><head>"
                    + "<style type=\"text/css\" >body{color:#0f1113; background-color: #fff;}"
                    + "</style></head>"
                    + "<body style >"
                    + about
                    + "</body></html>";

            webView.loadData(text, "text/html", "utf-8");
            webView.getSettings().setJavaScriptEnabled(true);
        }

        @Override
        protected Void doInBackground(Void... voids) {
//            String urlRouteList = "https://sonydadc.muvi.com/rest" + "/getStaticPagedetails";
            String urlRouteList = Util.rootUrl().trim() + Util.AboutUs.trim();

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    progresBar.setVisibility(View.VISIBLE);
                }

            });

            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(urlRouteList);
                httppost.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");
                httppost.addHeader("authToken", Util.authTokenStr);
                String strtext = getArguments().getString("item");
                httppost.addHeader("permalink",strtext.trim());
                httppost.addHeader("lang_code",Util.getTextofLanguage(getActivity(),Util.SELECTED_LANGUAGE_CODE,Util.DEFAULT_SELECTED_LANGUAGE_CODE));
                try {
                    HttpResponse response = httpclient.execute(httppost);
                    responseStr = EntityUtils.toString(response.getEntity());

                } catch (org.apache.http.conn.ConnectTimeoutException e) {

                }

            } catch (IOException e) {

                e.printStackTrace();
            }

            JSONObject myJson = null;
            if (responseStr != null) {
                try {
                    myJson = new JSONObject(responseStr);
                } catch (JSONException e) {

                    e.printStackTrace();
                }
            }
            try {
                JSONObject jsonMainNode = myJson.getJSONObject("page_details");
                about = jsonMainNode.optString("content").trim();

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
   /* public static CharSequence getStyledTextFromHtml(String source) {
        return android.text.Html.fromHtml(replaceNewlinesWithBreaks(source));
    }
    public static String replaceNewlinesWithBreaks(String source) {
        return source != null ? source.replaceAll("(?:\n|\r\n)","<br>") : "";
    }*/
}

