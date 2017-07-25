package com.release.reelAfrican.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.release.reelAfrican.R;
import com.release.reelAfrican.utils.ProgressBarHandler;
import com.release.reelAfrican.utils.Util;


public class WebViewFragment extends Fragment {


    Toolbar mActionBarToolbar;

    TextView noInternetConnectionTextView;
    ProgressBarHandler progressDialog;
    //UI
    private WebView webView;
    private String urlStr;

    int progress = 0;
    Resources res;
    Context context;
    Activity activity;
    private int mRedirectedCount=0;

    public WebViewFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getActivity();
        setHasOptionsMenu(true);
        res = context.getResources();
        View rootView = inflater.inflate(R.layout.fragment_web_view, container, false);

        mActionBarToolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);

        noInternetConnectionTextView = (TextView)rootView.findViewById(R.id.noInternetConnection);
        noInternetConnectionTextView.setText(Util.getTextofLanguage(context,Util.NO_INTERNET_CONNECTION,Util.DEFAULT_NO_INTERNET_CONNECTION));
        noInternetConnectionTextView.setVisibility(View.GONE);

        urlStr =  getArguments().getString("item");

        urlStr = urlStr+"/mobile_view";



        //urlStr = "http://masitv.net/contactus/mobile_view";
        //define UI
        webView = (WebView) rootView.findViewById(R.id.webView);

        //Clear WebView Data,Cache,History
        webView.clearFormData();
        webView.clearCache(true);
        webView.clearHistory();
        CookieManager.getInstance().removeAllCookie();
        boolean isNetwork = Util.checkNetwork(activity);
        if (isNetwork==true){
            startWebView(urlStr);
        }else{
            noInternetConnectionTextView.setVisibility(View.VISIBLE);
        }
        rootView.setFocusableInTouchMode(true);
        rootView.requestFocus();

        rootView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        final Intent startIntent = new Intent(context, MainActivity.class);

                        getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                if (progressDialog != null && progressDialog.isShowing()) {
                                    progressDialog.hide();
                                    progressDialog = null;
                                }
                                startIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                startActivity(startIntent);
                                getActivity().finish();

                            }
                        });

                        return true;
                    }
                }
                return false;
            }
        });
        return rootView;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Do something that differs the Activity's menu here

        MenuItem item;
        item= menu.findItem(R.id.action_filter);
        item.setVisible(false);
        super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_search);
        MenuItem item1 = menu.findItem(R.id.action_filter);
        item.setVisible(false);
        item1.setVisible(false);

    }
   /* @Override
    public void onResume() {
        super.onResume();
        boolean isNetwork = Util.checkNetwork(activity);
        if (isNetwork==false){
            Toast.makeText(activity, "No Internet Comnnection", Toast.LENGTH_LONG).show();
        }
    }*/
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }
    //When Webview is getting loaded
    private void startWebView(String url) {

        webView.setWebViewClient(new WebViewClient() {

            boolean mIsPageFinished = true;

            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                if (mIsPageFinished) {
                    mRedirectedCount = 0; //clear count
                }

                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                mIsPageFinished = false;
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.hide();
                    progressDialog = null;
                }else {
                    progressDialog = new ProgressBarHandler(context);
                    progressDialog.show();
                }

            }

            //Show loader on url load
            public void onLoadResource(WebView view, String url) {

            }

            public void onPageFinished(WebView view, String url) {
                mIsPageFinished = true;
                try {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.hide();
                        progressDialog = null;
                    }
                } catch (Exception exception) {
                    noInternetConnectionTextView.setVisibility(View.VISIBLE);
                    exception.printStackTrace();
                }
            }

            @Override
            public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
                super.doUpdateVisitedHistory(view, url, isReload);

                if(!mIsPageFinished){
                    mRedirectedCount++;
                }
            }
        });

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setSupportMultipleWindows(true);

       /* webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onCreateWindow(WebView view, boolean dialog, boolean userGesture, Message resultMsg) {

                return true;
            }


            @Override
            public void onProgressChanged(WebView view, int newProgress) {


                if (newProgress  == 0) {
                    if (progressDialog == null) {
                        progressDialog = new ProgressDialog(activity);

                    }
                    progressDialog.setMessage(getResources().getString(R.string.loading_str));
                    progressDialog.show();

                }
                if (newProgress == 100) {

                    try {
                        if (progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                    } catch (Exception exception) {
                        noInternetConnectionTextView.setVisibility(View.VISIBLE);
                        exception.printStackTrace();
                    }
                }
                super.onProgressChanged(view, newProgress);

            }

        });*/
        webView.loadUrl(url);

    }
    public void onBackPressed1() {
        if(mRedirectedCount>0){

            while(mRedirectedCount>0){

                webView.goBack();
                mRedirectedCount--;
            }
            mRedirectedCount = 0; //clear
        } else {
            getActivity().finish();
            System.exit(0);

        }


    }
    @Override
    public void onResume() {
        super.onResume();
        boolean isNetwork = Util.checkNetwork(activity);
        if (isNetwork==false) {
            Util.showToast(activity,Util.getTextofLanguage(activity,Util.NO_INTERNET_CONNECTION,Util.DEFAULT_NO_INTERNET_CONNECTION));
          //  Toast.makeText(activity, Util.getTextofLanguage(activity,Util.NO_INTERNET_CONNECTION,Util.DEFAULT_NO_INTERNET_CONNECTION), Toast.LENGTH_LONG).show();

        }

    }
}
