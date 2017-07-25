package com.release.reelAfrican.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.cast.MediaInfo;
import com.google.android.gms.cast.MediaMetadata;
import com.google.android.gms.cast.MediaTrack;
import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.cast.framework.CastSession;
import com.google.android.gms.cast.framework.SessionManagerListener;
import com.google.android.gms.cast.framework.media.RemoteMediaClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.images.WebImage;
import com.release.reelAfrican.R;
import com.release.reelAfrican.adapter.CardSpinnerAdapter;
import com.release.reelAfrican.expandedcontrols.ExpandedControlsActivity;
import com.release.reelAfrican.model.CardModel;
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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import io.fabric.sdk.android.Fabric;
/*
import io.card.payment.CardIOActivity;
import io.card.payment.CreditCard;*/

public class PPvPaymentInfoActivity extends ActionBarActivity {

    /*chromecast-------------------------------------*/
    View view;


    public enum PlaybackLocation {
        LOCAL,
        REMOTE
    }

    /**
     * List of various states that we can be in
     */
    public enum PlaybackState {
        PLAYING, PAUSED, BUFFERING, IDLE
    }

    private MovieDetailsActivity.PlaybackLocation mLocation;
    private MovieDetailsActivity.PlaybackState mPlaybackState;
    private final float mAspectRatio = 72f / 128;
    private AQuery mAquery;
    private MediaInfo mSelectedMedia;


    private CastContext mCastContext;
    private SessionManagerListener<CastSession> mSessionManagerListener =
            new MySessionManagerListener();
    private CastSession mCastSession;

    private class MySessionManagerListener implements SessionManagerListener<CastSession> {

        @Override
        public void onSessionEnded(CastSession session, int error) {
            if (session == mCastSession) {
                mCastSession = null;
            }
            invalidateOptionsMenu();
        }

        @Override
        public void onSessionResumed(CastSession session, boolean wasSuspended) {
            mCastSession = session;
            invalidateOptionsMenu();
        }

        @Override
        public void onSessionStarted(CastSession session, String sessionId) {
            mCastSession = session;
            invalidateOptionsMenu();
        }

        @Override
        public void onSessionStarting(CastSession session) {
        }

        @Override
        public void onSessionStartFailed(CastSession session, int error) {
        }

        @Override
        public void onSessionEnding(CastSession session) {
        }

        @Override
        public void onSessionResuming(CastSession session, String sessionId) {
        }

        @Override
        public void onSessionResumeFailed(CastSession session, int error) {
        }

        @Override
        public void onSessionSuspended(CastSession session, int reason) {
        }
    }


    MediaInfo mediaInfo;

    /*chromecast-------------------------------------*/
    CardModel[] cardSavedArray;
    ProgressBarHandler pDialog;
    String existing_card_id = "";
    String isCheckedToSavetheCard = "1";
    private boolean isCastConnected = false;
    public static ProgressBarHandler progressBarHandler;
    private static final int MAX_LINES = 2;
    int isVoucher = 0;
    EditText voucher_code;
    Button apply,watch_now;
    TextView voucher_success;
    boolean watch_status = false;
    int selectedPurchaseType = 0;
    String VoucherCode = "";
    String filename = "";
    static File mediaStorageDir;
    SharedPreferences loginPref, countryPref;
    ArrayList<String> SubTitleName = new ArrayList<>();
    ArrayList<String> SubTitlePath = new ArrayList<>();
    ArrayList<String> FakeSubTitlePath = new ArrayList<>();
    ArrayList<String> ResolutionFormat = new ArrayList<>();
    ArrayList<String>ResolutionUrl = new ArrayList<>();

    ArrayList<String> SubTitleLanguage = new ArrayList<>();
    Toolbar mActionBarToolbar;
    boolean isCouponCodeAdded = false;
    String validCouponCode;

    final String TAG = getClass().getName();
    private int MY_SCAN_REQUEST_CODE = 100; // arbitrary int

    Spinner cardExpiryYearSpinner;
    Spinner cardExpiryMonthSpinner;
    Spinner creditCardSaveSpinner;
    Spinner spinnerCardTextView;
    private RelativeLayout creditCardLayout;
    private RelativeLayout withoutCreditCardLayout;
    private LinearLayout cardExpiryDetailsLayout;
    private LinearLayout voucherLinearLayout;
    private CheckBox saveCardCheckbox;

    private TextView withoutCreditCardChargedPriceTextView;
    private TextView withoutPaymentTitleTextView;
    private Button nextButton;
    //private Button paywithCreditCardButton;
    //private Button paywithPaypalButton;

    private EditText nameOnCardEditText;
    private EditText cardNumberEditText;
    private EditText securityCodeEditText;
    private Button scanButton;
    private Button payNowButton;

    private Button applyButton;
    private EditText couponCodeEditText;
    private RadioGroup paymentOptionsRadioGroup;
    private RadioButton payWithCreditCardRadioButton;
    private RadioButton voucherRadioButton;

    private LinearLayout paymentOptionLinearLayout;

    private TextView paymentOptionsTitle;
    private TextView selectShowRadioButton;

    private TextView chargedPriceTextView;

    String cardLastFourDigitStr;
    String tokenStr;
    String cardTypeStr;
    String responseText;
    String statusStr;

    String movieStreamUniqueIdStr;
    String muviUniqueIdStr;
    String planPriceStr;
    String videoUrlStr;
    String currencyCountryCodeStr;
    String currencyIdStr;
    String currencySymbolStr;
    String videoPreview;
    String videoName = "No Name";
    int isPPV = 0;
    int isAPV = 0;
    int isConverted = 0;
    int contentTypesId = 0;

    String profileIdStr;
    int expiryMonthStr = 0;
    int planIdForPaypal = 0;
    ProgressBarHandler videoPDialog;

    int expiryYearStr = 0;
    float planPrice = 0.0f;
    float chargedPrice = 0.0f;
    float previousChargedPrice = 0.0f;
    ArrayAdapter<Integer> cardExpiryYearSpinnerAdapter;
    ArrayAdapter<Integer> cardExpiryMonthSpinnerAdapter;
    CardSpinnerAdapter creditCardSaveSpinnerAdapter;
    List<Integer> yearArray = new ArrayList<Integer>(21);
    List<Integer> monthsIdArray = new ArrayList<Integer>(12);
    /*Asynctask on background thread*/
    int corePoolSize = 60;
    int maximumPoolSize = 80;
    int keepAliveTime = 10;
    BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<Runnable>(maximumPoolSize);
    Executor threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, workQueue);

    TextView purchaseTextView,creditCardDetailsTitleTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());

        setContentView(R.layout.activity_ppv_payment_info);

        Log.v("SUBHA","ppvpayment Activity Season Id ="+Util.selected_season_id);
        Log.v("SUBHA","ppvpatment Activity episode Id ="+Util.selected_episode_id);

        videoPreview = Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.NO_DATA, Util.DEFAULT_NO_DATA);
        countryPref = getSharedPreferences(Util.COUNTRY_PREF, 0);

        //Set toolbar
        mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mActionBarToolbar);
        mActionBarToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back));
        mActionBarToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(PPvPaymentInfoActivity.this);
                onBackPressed();
            }
        });
       /* if (pDialog == null) {
            pDialog = new ProgressBarHandler(PPvPaymentInfoActivity.this);
           // pDialog.show();

        }
*/

        purchaseTextView = (TextView) findViewById(R.id.purchaseTextView);




        loginPref = getSharedPreferences(Util.LOGIN_PREF, 0); // 0 - for private mode


        if (getIntent().getStringExtra("muviuniqueid") != null) {
            muviUniqueIdStr = getIntent().getStringExtra("muviuniqueid");
        } else {
            muviUniqueIdStr = "";
        }


        if (getIntent().getStringExtra("episodeStreamId") != null) {
            movieStreamUniqueIdStr = getIntent().getStringExtra("episodeStreamId");
        } else {
            movieStreamUniqueIdStr = "";
        }

        if (getIntent().getStringExtra("videoPreview") != null) {
            videoPreview = getIntent().getStringExtra("videoPreview");
        }

        if (getIntent().getStringExtra("showName") != null) {
            videoName = getIntent().getStringExtra("showName");
        }
        if (getIntent().getStringExtra("planIdForPaypal") != null) {
            planIdForPaypal = Integer.parseInt(getIntent().getStringExtra("planIdForPaypal"));
        }
        if (getIntent().getIntExtra("isPPV", 0) != 0) {
            isPPV = getIntent().getIntExtra("isPPV", 0);
        }
        if (getIntent().getIntExtra("isAPV", 0) != 0) {
            isAPV = getIntent().getIntExtra("isPPV", 0);
        }
        if (getIntent().getIntExtra("isConverted", 0) != 0) {
            isConverted = getIntent().getIntExtra("isConverted", 0);
        }

        if (getIntent().getStringExtra("selectedPurchaseType") != null) {
            selectedPurchaseType = Integer.parseInt(getIntent().getStringExtra("selectedPurchaseType"));
        }
        if (getIntent().getStringExtra("contentTypesId") != null) {
            contentTypesId = Integer.parseInt(getIntent().getStringExtra("contentTypesId"));
        }

        if (loginPref.getString("PREFS_LOGIN_ISSUBSCRIBED_KEY", null) != null) {
            String isSubscribedStr = "0";
            isSubscribedStr = loginPref.getString("PREFS_LOGIN_ISSUBSCRIBED_KEY", null);
            if (isSubscribedStr.equalsIgnoreCase("1")) {
                if (getIntent().getStringExtra("planSubscribedPrice") != null) {
                    chargedPrice = Float.parseFloat(getIntent().getStringExtra("planSubscribedPrice"));
                    previousChargedPrice = Float.parseFloat(getIntent().getStringExtra("planSubscribedPrice"));
                    planPrice = Float.parseFloat(getIntent().getStringExtra("planSubscribedPrice"));
                } else {
                    chargedPrice = 0.0f;
                    previousChargedPrice = 0.0f;
                    planPrice = 0.0f;
                }
            } else {
                if (getIntent().getStringExtra("planUnSubscribedPrice") != null) {
                    chargedPrice = Float.parseFloat(getIntent().getStringExtra("planUnSubscribedPrice"));
                    previousChargedPrice = Float.parseFloat(getIntent().getStringExtra("planUnSubscribedPrice"));
                    planPrice = Float.parseFloat(getIntent().getStringExtra("planUnSubscribedPrice"));

                } else {
                    chargedPrice = 0.0f;
                    previousChargedPrice = 0.0f;
                    planPrice = 0.0f;

                }
            }


        } else {
            chargedPrice = 0.0f;
            previousChargedPrice = 0.0f;
            planPrice = 0.0f;

        }


        if (getIntent().getStringExtra("currencyId") != null) {
            currencyIdStr = getIntent().getStringExtra("currencyId");
        } else {
            currencyIdStr = "";
        }

        if (getIntent().getStringExtra("currencyCountryCode") != null) {
            currencyCountryCodeStr = getIntent().getStringExtra("currencyCountryCode");
        } else {
            currencyCountryCodeStr = "";
        }

        if (getIntent().getStringExtra("currencySymbol") != null) {
            currencySymbolStr = getIntent().getStringExtra("currencySymbol");
        } else {
            currencySymbolStr = "";
        }

        chargedPriceTextView = (TextView) findViewById(R.id.chargeDetailsTextView);
        creditCardLayout = (RelativeLayout) findViewById(R.id.creditCardLayout);
        cardExpiryDetailsLayout = (LinearLayout) findViewById(R.id.cardExpiryDetailsLayout);
        saveCardCheckbox = (CheckBox) findViewById(R.id.saveCardCheckbox);
        withoutCreditCardLayout = (RelativeLayout) findViewById(R.id.withoutPaymentLayout);
        withoutCreditCardLayout.setVisibility(View.GONE);
        withoutCreditCardChargedPriceTextView = (TextView) findViewById(R.id.withoutPaymentChargeDetailsTextView);
        withoutPaymentTitleTextView = (TextView) findViewById(R.id.withoutPaymentTitleTextView);
        nextButton = (Button) findViewById(R.id.nextButton);
        //paywithCreditCardButton = (Button) findViewById(R.id.payWithCreditCardButton);
        //paywithPaypalButton = (Button) findViewById(R.id.payWithPaypalCardButton);

        voucherLinearLayout = (LinearLayout) findViewById(R.id.voucherLinearLayout);

        paymentOptionsRadioGroup = (RadioGroup) findViewById(R.id.paymentOptionsRadioGroup);
        payWithCreditCardRadioButton = (RadioButton) findViewById(R.id.payWithCreditCardRadioButton);
        voucherRadioButton = (RadioButton) findViewById(R.id.voucherRadioButton);
        paymentOptionsTitle = (TextView) findViewById(R.id.paymentOptionsTitle);
        paymentOptionLinearLayout = (LinearLayout) findViewById(R.id.paymentOptionLinearLayout);


        cardExpiryMonthSpinner = (Spinner) findViewById(R.id.cardExpiryMonthEditText);
        cardExpiryYearSpinner = (Spinner) findViewById(R.id.cardExpiryYearEditText);
        creditCardSaveSpinner = (Spinner) findViewById(R.id.creditCardSaveEditText);


        apply = (Button) findViewById(R.id.apply);
        watch_now = (Button) findViewById(R.id.watch_now);
        scanButton = (Button) findViewById(R.id.scanButton);
        scanButton.setVisibility(View.GONE);
        payNowButton = (Button) findViewById(R.id.payNowButton);
        applyButton = (Button) findViewById(R.id.addCouponButton);
        selectShowRadioButton = (TextView) findViewById(R.id.showNameWithPrice);
        creditCardDetailsTitleTextView = (TextView) findViewById(R.id.creditCardDetailsTitleTextView);
        voucher_success = (TextView) findViewById(R.id.voucher_success);
        nameOnCardEditText = (EditText) findViewById(R.id.nameOnCardEditText);
        cardNumberEditText = (EditText) findViewById(R.id.cardNumberEditText);
        securityCodeEditText = (EditText) findViewById(R.id.securityCodeEditText);
        couponCodeEditText = (EditText) findViewById(R.id.couponCodeEditText);
        voucher_code = (EditText) findViewById(R.id.voucher_code);
        couponCodeEditText.addTextChangedListener(filterTextWatcher);


        Typeface typeface6 = Typeface.createFromAsset(getAssets(),getResources().getString(R.string.light_fonts));
        nameOnCardEditText.setTypeface(typeface6);
        cardNumberEditText.setTypeface(typeface6);
        securityCodeEditText.setTypeface(typeface6);
        voucher_code.setTypeface(typeface6);
        couponCodeEditText.setTypeface(typeface6);


        nameOnCardEditText.setHint(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.CREDIT_CARD_NAME_HINT, Util.DEFAULT_CREDIT_CARD_NAME_HINT));
        cardNumberEditText.setHint(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.CREDIT_CARD_NUMBER_HINT, Util.DEFAULT_CREDIT_CARD_NUMBER_HINT));
        securityCodeEditText.setHint(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.CREDIT_CARD_CVV_HINT, Util.DEFAULT_CREDIT_CARD_CVV_HINT));
        couponCodeEditText.setHint(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.COUPON_CODE_HINT, Util.DEFAULT_COUPON_CODE_HINT));
        voucher_code.setHint(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.VOUCHER_CODE, Util.DEFAULT_VOUCHER_CODE));


        Typeface typeface7 = Typeface.createFromAsset(getAssets(), getResources().getString(R.string.regular_fonts));
        saveCardCheckbox.setTypeface(typeface7);
        Log.v("SUBHA","Check box"+Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.SAVE_THIS_CARD,Util.DEFAULT_SAVE_THIS_CARD));
        saveCardCheckbox.setText(" " + Util.getTextofLanguage(PPvPaymentInfoActivity.this,Util.SAVE_THIS_CARD,Util.DEFAULT_SAVE_THIS_CARD));

        Typeface typeface = Typeface.createFromAsset(getAssets(),getResources().getString(R.string.regular_fonts));
        applyButton.setTypeface(typeface);
        applyButton.setText(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.BUTTON_APPLY, Util.DEFAULT_BUTTON_APPLY));
        apply.setTypeface(typeface);
        apply.setText(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.BUTTON_APPLY, Util.DEFAULT_BUTTON_APPLY));
        watch_now.setTypeface(typeface);
        watch_now.setText(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.WATCH_NOW, Util.DEFAULT_WATCH_NOW));
        nextButton.setTypeface(typeface);
        nextButton.setText(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.BTN_NEXT, Util.DEFAULT_BTN_NEXT));

        Typeface typeface1 = Typeface.createFromAsset(getAssets(), getResources().getString(R.string.regular_fonts));
        purchaseTextView.setTypeface(typeface1);
        paymentOptionsTitle.setTypeface(typeface1);
        purchaseTextView.setText(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.PURCHASE, Util.DEFAULT_PURCHASE));
        paymentOptionsTitle.setText(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.PAYMENT_OPTIONS_TITLE, Util.DEFAULT_PAYMENT_OPTIONS_TITLE));

        Typeface typeface2 = Typeface.createFromAsset(getAssets(), getResources().getString(R.string.regular_fonts));
        creditCardDetailsTitleTextView.setTypeface(typeface2);
        creditCardDetailsTitleTextView.setText(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.CREDIT_CARD_DETAILS, Util.DEFAULT_CREDIT_CARD_DETAILS));

        Typeface typeface3 = Typeface.createFromAsset(getAssets(), getResources().getString(R.string.regular_fonts));
        chargedPriceTextView.setTypeface(typeface3);
        selectShowRadioButton.setTypeface(typeface3);



        Typeface typeface4 = Typeface.createFromAsset(getAssets(), getResources().getString(R.string.regular_fonts));
        payNowButton.setTypeface(typeface4);
        payNowButton.setText(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.BUTTON_PAY_NOW, Util.DEFAULT_BUTTON_PAY_NOW));



        payWithCreditCardRadioButton.setTypeface(typeface7);
        voucherRadioButton.setTypeface(typeface7);

        payWithCreditCardRadioButton.setText(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.CREDIT_CARD_DETAILS, Util.DEFAULT_CREDIT_CARD_DETAILS));
        voucherRadioButton.setText(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.VOUCHER_CODE, Util.DEFAULT_VOUCHER_CODE));

        AsynGetMoniTization asynGetMoniTization = new AsynGetMoniTization();
        asynGetMoniTization.executeOnExecutor(threadPoolExecutor);



        voucherRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                creditCardLayout.setVisibility(View.GONE);
                paymentOptionLinearLayout.setVisibility(View.VISIBLE);
                voucherLinearLayout.setVisibility(View.VISIBLE);
            }

        });

        payWithCreditCardRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                creditCardLayout.setVisibility(View.VISIBLE);
                paymentOptionLinearLayout.setVisibility(View.VISIBLE);
                voucherLinearLayout.setVisibility(View.GONE);
            }
        });


        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                VoucherCode = voucher_code.getText().toString().trim();
                if(!VoucherCode.equals(""))
                {
                    ValidateVoucher_And_VoucherSubscription();
                }
                else
                {
                    Toast.makeText(getApplicationContext(),Util.getTextofLanguage(PPvPaymentInfoActivity.this,Util.VOUCHER_BLANK_MESSAGE,Util.DEFAULT_VOUCHER_BLANK_MESSAGE), Toast.LENGTH_SHORT).show();

                }
            }
        });

        watch_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(watch_status)
                {

                    // Calling Voucher Subscription Api

                    AsynVoucherSubscription asynVoucherSubscription = new AsynVoucherSubscription();
                    asynVoucherSubscription.executeOnExecutor(threadPoolExecutor);



                }
            }
        });

       /* paymentOptionsRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                // find which radio button is selected
                if(checkedId == R.id.payWithCreditCardRadioButton) {


                    creditCardLayout.setVisibility(View.VISIBLE);
                    voucherRadioButton.setVisibility(View.GONE);


                } else if(checkedId == R.id.voucherRadioButton) {
                    creditCardLayout.setVisibility(View.GONE);
                    voucherRadioButton.setVisibility(View.VISIBLE);
                }

            }
        });*/





        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        for (int i = 0; i < 21; i++) {
            yearArray.add(year + i);

        }
        for (int i = 1; i < 13; i++) {
            monthsIdArray.add(i);
        }


        cardExpiryMonthSpinnerAdapter = new ArrayAdapter<Integer>(this,R.layout.spinner_new, monthsIdArray);
        cardExpiryMonthSpinner.setAdapter(cardExpiryMonthSpinnerAdapter);


        int mn = c.get(Calendar.MONTH);
        if (Util.containsIgnoreCase(monthsIdArray, mn + 1)) {
            // true
            int mnIndex = monthsIdArray.indexOf(mn + 1);

            cardExpiryMonthSpinner.setSelection(mnIndex);
            expiryMonthStr = monthsIdArray.get(mnIndex);
        } else {
            cardExpiryMonthSpinner.setSelection(0);
            expiryMonthStr = monthsIdArray.get(0);
        }

       /* cardExpiryMonthSpinner.setSelection(0);
        expiryMonthStr = monthsIdArray.get(0);*/

        cardExpiryYearSpinnerAdapter = new ArrayAdapter<Integer>(this, R.layout.spinner_new, yearArray);
        cardExpiryYearSpinner.setAdapter(cardExpiryYearSpinnerAdapter);
        cardExpiryYearSpinner.setSelection(0);
        expiryYearStr = yearArray.get(0);

      /*  creditCardSaveSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                creditCardSaveSpinner.setSelection(position);
                if(position == 0)
                {
                    creditCardLayout.setVisibility(View.VISIBLE);
                }else
                {
                    creditCardLayout.setVisibility(View.INVISIBLE);
                }

            }

        });
*/

        saveCardCheckbox.setChecked(true);

        creditCardSaveSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                creditCardSaveSpinner.setSelection(position);
                if (position == 0) {

                    nameOnCardEditText.setVisibility(View.VISIBLE);
                    cardNumberEditText.setVisibility(View.VISIBLE);
                    cardNumberEditText.setVisibility(View.VISIBLE);
                    cardExpiryDetailsLayout.setVisibility(View.VISIBLE);
                    saveCardCheckbox.setVisibility(View.VISIBLE);
                    isCheckedToSavetheCard = "1";
                    saveCardCheckbox.setChecked(true);
                } else {
                    //withoutCreditCardLayout.setVisibility(View.GONE);
                    nameOnCardEditText.setVisibility(View.GONE);
                    cardNumberEditText.setVisibility(View.GONE);
                    cardNumberEditText.setVisibility(View.GONE);
                    cardExpiryDetailsLayout.setVisibility(View.GONE);
                    saveCardCheckbox.setVisibility(View.GONE);
                    isCheckedToSavetheCard = "0";
                    saveCardCheckbox.setChecked(false);


                }
            }

            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
                creditCardSaveSpinner.setSelection(0);

                if (creditCardSaveSpinner.getSelectedItemPosition() == 0) {

                    nameOnCardEditText.setVisibility(View.VISIBLE);
                    cardNumberEditText.setVisibility(View.VISIBLE);
                    cardNumberEditText.setVisibility(View.VISIBLE);
                    cardExpiryDetailsLayout.setVisibility(View.VISIBLE);
                    saveCardCheckbox.setVisibility(View.VISIBLE);
                    isCheckedToSavetheCard = "1";
                    saveCardCheckbox.setChecked(true);

                    //withoutCreditCardLayout.setVisibility(View.VISIBLE);
                    //creditCardLayout.setVisibility(View.GONE);
                    //chargedPriceTextView.setText(Util.getTextofLanguage(PPvPaymentInfoActivity.this,Util.CARD_WILL_CHARGE,Util.DEFAULT_CARD_WILL_CHARGE)+" " +currencySymbolStr+chargedPrice);
                } else {
                    //withoutCreditCardLayout.setVisibility(View.GONE);
                    nameOnCardEditText.setVisibility(View.GONE);
                    cardNumberEditText.setVisibility(View.GONE);
                    cardNumberEditText.setVisibility(View.GONE);
                    cardExpiryDetailsLayout.setVisibility(View.GONE);
                    saveCardCheckbox.setVisibility(View.GONE);
                    isCheckedToSavetheCard = "0";
                    saveCardCheckbox.setChecked(false);
                }

            }
        });


        cardExpiryMonthSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                cardExpiryMonthSpinner.setSelection(position);
                expiryMonthStr = monthsIdArray.get(position);

            }

            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
                Calendar c = Calendar.getInstance();
                int mn = c.get(Calendar.MONTH);
                if (Util.containsIgnoreCase(monthsIdArray, mn + 1)) {
                    // true
                    int mnIndex = monthsIdArray.indexOf(mn + 1);

                    cardExpiryMonthSpinner.setSelection(mnIndex);
                    expiryMonthStr = monthsIdArray.get(mnIndex);
                } else {
                    cardExpiryMonthSpinner.setSelection(0);
                    expiryMonthStr = monthsIdArray.get(0);

                }
            }
        });
        cardExpiryYearSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {


            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                cardExpiryYearSpinner.setSelection(position);
                expiryYearStr = yearArray.get(position);
            }

            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
                cardExpiryYearSpinner.setSelection(0);
                expiryYearStr = yearArray.get(0);


            }
        });
        selectShowRadioButton.setText(videoName + " : " + currencySymbolStr + planPrice);
        chargedPriceTextView.setText(Util.getTextofLanguage(PPvPaymentInfoActivity.this,Util.CARD_WILL_CHARGE,Util.DEFAULT_CARD_WILL_CHARGE)+" " + currencySymbolStr + chargedPrice);
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                nameOnCardEditText.setText("");
                securityCodeEditText.setText("");
                cardNumberEditText.setText("");



            }
        });


        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(couponCodeEditText.getWindowToken(), 0);
                String couponCodeStr = couponCodeEditText.getText().toString().trim();

                if (couponCodeStr.matches("")) {
                    Util.showToast(PPvPaymentInfoActivity.this, Util.getTextofLanguage(PPvPaymentInfoActivity.this,Util.COUPON_CODE_HINT,Util.DEFAULT_COUPON_CODE_HINT));

                   // Toast.makeText(PPvPaymentInfoActivity.this, Util.getTextofLanguage(PPvPaymentInfoActivity.this,Util.COUPON_CODE_HINT,Util.DEFAULT_COUPON_CODE_HINT), Toast.LENGTH_LONG).show();

                } else {
                    boolean isNetwork = Util.checkNetwork(PPvPaymentInfoActivity.this);
                    if (isNetwork == false) {
                        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(PPvPaymentInfoActivity.this,R.style.MyAlertDialogStyle);
                        dlgAlert.setMessage(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.NO_INTERNET_CONNECTION, Util.DEFAULT_NO_INTERNET_CONNECTION));
                        dlgAlert.setTitle(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.SORRY, Util.DEFAULT_SORRY));
                        dlgAlert.setPositiveButton(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.BUTTON_OK, Util.DEFAULT_BUTTON_OK), null);
                        dlgAlert.setCancelable(false);
                        dlgAlert.setPositiveButton(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.BUTTON_OK, Util.DEFAULT_BUTTON_OK),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                        dlgAlert.create().show();

                    } else {
                        Log.v("SUBHA","coupon details");

                        AsynCouponInfoDetails asyncReg = new AsynCouponInfoDetails();
                        asyncReg.executeOnExecutor(threadPoolExecutor);
                    }

                }
            }
        });

       /* payByPaypalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AsynPayByPaypalDetails asynPayByPaypalDetails = new AsynPayByPaypalDetails();
                asynPayByPaypalDetails.executeOnExecutor(threadPoolExecutor);

            }
        });*/
        payNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (creditCardSaveSpinner != null && cardSavedArray != null && cardSavedArray.length > 0 && creditCardSaveSpinner.getSelectedItemPosition() > 0) {

                    boolean isNetwork = Util.checkNetwork(PPvPaymentInfoActivity.this);
                    if (isNetwork == false) {
                        Util.showToast(PPvPaymentInfoActivity.this, Util.getTextofLanguage(PPvPaymentInfoActivity.this,Util.NO_INTERNET_CONNECTION,Util.DEFAULT_NO_INTERNET_CONNECTION));

                       // Toast.makeText(PPvPaymentInfoActivity.this, Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.NO_INTERNET_CONNECTION, Util.DEFAULT_NO_INTERNET_CONNECTION), Toast.LENGTH_LONG).show();
                    } else {
                        if (creditCardSaveSpinner != null && cardSavedArray != null && cardSavedArray.length > 0 && creditCardSaveSpinner.getSelectedItemPosition() > 0) {
                            existing_card_id = cardSavedArray[creditCardSaveSpinner.getSelectedItemPosition()].getCardId();

                        } else {
                            existing_card_id = "";
                        }

                        AsynWithouPaymentSubscriptionRegDetails asynWithouPaymentSubscriptionRegDetails = new AsynWithouPaymentSubscriptionRegDetails();
                        asynWithouPaymentSubscriptionRegDetails.executeOnExecutor(threadPoolExecutor);
                    }

                } else {
                    String nameOnCardStr = nameOnCardEditText.getText().toString().trim();
                    String cardNumberStr = cardNumberEditText.getText().toString().trim();
                    String securityCodeStr = securityCodeEditText.getText().toString().trim();


                    if (nameOnCardStr.matches("")) {
                        Util.showToast(PPvPaymentInfoActivity.this, Util.getTextofLanguage(PPvPaymentInfoActivity.this,Util.CREDIT_CARD_NAME_HINT,Util.DEFAULT_CREDIT_CARD_NAME_HINT));

                      //  Toast.makeText(PPvPaymentInfoActivity.this,Util.getTextofLanguage(PPvPaymentInfoActivity.this,Util.CREDIT_CARD_NAME_HINT,Util.DEFAULT_CREDIT_CARD_NAME_HINT), Toast.LENGTH_LONG).show();

                    } else if (cardNumberStr.matches("")) {
                        Util.showToast(PPvPaymentInfoActivity.this, Util.getTextofLanguage(PPvPaymentInfoActivity.this,Util.CREDIT_CARD_NUMBER_HINT,Util.DEFAULT_CREDIT_CARD_NUMBER_HINT));

                      //  Toast.makeText(PPvPaymentInfoActivity.this,Util.getTextofLanguage(PPvPaymentInfoActivity.this,Util.CREDIT_CARD_NUMBER_HINT,Util.DEFAULT_CREDIT_CARD_NUMBER_HINT), Toast.LENGTH_LONG).show();

                    } else if (securityCodeStr.matches("")) {
                        Util.showToast(PPvPaymentInfoActivity.this, Util.getTextofLanguage(PPvPaymentInfoActivity.this,Util.CVV_ALERT,Util.DEFAULT_CVV_ALERT));

                    //    Toast.makeText(PPvPaymentInfoActivity.this,Util.getTextofLanguage(PPvPaymentInfoActivity.this,Util.CVV_ALERT,Util.DEFAULT_CVV_ALERT), Toast.LENGTH_LONG).show();


                    } else if (expiryMonthStr <= 0) {
//                        Toast.makeText(PPvPaymentInfoActivity.this, "Please enter expiry month", Toast.LENGTH_LONG).show();

                    } else if (expiryYearStr <= 0) {
//                        Toast.makeText(PPvPaymentInfoActivity.this, "Please enter expiry year", Toast.LENGTH_LONG).show();

                    } else {
                        boolean isNetwork = Util.checkNetwork(PPvPaymentInfoActivity.this);
                        if (isNetwork == false) {
                            AlertDialog.Builder dlgAlert = new AlertDialog.Builder(PPvPaymentInfoActivity.this,R.style.MyAlertDialogStyle);
                            dlgAlert.setMessage(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.NO_INTERNET_CONNECTION, Util.DEFAULT_NO_INTERNET_CONNECTION));
                            dlgAlert.setTitle(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.SORRY, Util.DEFAULT_SORRY));
                            dlgAlert.setPositiveButton(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.BUTTON_OK, Util.DEFAULT_BUTTON_OK), null);
                            dlgAlert.setCancelable(false);
                            dlgAlert.setPositiveButton(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.BUTTON_OK, Util.DEFAULT_BUTTON_OK),
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });
                            dlgAlert.create().show();

                        } else {

                            AsynPaymentInfoDetails asyncReg = new AsynPaymentInfoDetails();
                            asyncReg.executeOnExecutor(threadPoolExecutor);


                        }

                    }
                }

            }
        });
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isNetwork = Util.checkNetwork(PPvPaymentInfoActivity.this);
                if (isNetwork == false) {
                    AlertDialog.Builder dlgAlert = new AlertDialog.Builder(PPvPaymentInfoActivity.this,R.style.MyAlertDialogStyle);
                    dlgAlert.setMessage(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.NO_INTERNET_CONNECTION, Util.DEFAULT_NO_INTERNET_CONNECTION));
                    dlgAlert.setTitle(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.SORRY, Util.DEFAULT_SORRY));
                    dlgAlert.setPositiveButton(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.BUTTON_OK, Util.DEFAULT_BUTTON_OK), null);
                    dlgAlert.setCancelable(false);
                    dlgAlert.setPositiveButton(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.BUTTON_OK, Util.DEFAULT_BUTTON_OK),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    dlgAlert.create().show();

                } else {

                    AsynWithouPaymentSubscriptionRegDetails asyncSubWithoutPayment = new AsynWithouPaymentSubscriptionRegDetails();
                    asyncSubWithoutPayment.executeOnExecutor(threadPoolExecutor);
                }
            }

        });


        boolean isNetwork = Util.checkNetwork(PPvPaymentInfoActivity.this);
        if (isNetwork == false) {
            creditCardSaveSpinner.setVisibility(View.GONE);
            Util.showToast(PPvPaymentInfoActivity.this, Util.getTextofLanguage(PPvPaymentInfoActivity.this,Util.NO_INTERNET_CONNECTION,Util.DEFAULT_NO_INTERNET_CONNECTION));

           // Toast.makeText(PPvPaymentInfoActivity.this, Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.NO_INTERNET_CONNECTION, Util.DEFAULT_NO_INTERNET_CONNECTION), Toast.LENGTH_LONG).show();

        } else {
            AsynLoadCardList asynLoadCardList = new AsynLoadCardList();
            asynLoadCardList.executeOnExecutor(threadPoolExecutor);
        }
        saveCardCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                                                        @Override
                                                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                                            if (isChecked == true) {
                                                                isCheckedToSavetheCard = "1";
                                                            } else {
                                                                isCheckedToSavetheCard = "0";

                                                            }

                                                        }
                                                    }
        );


         /*chromecast-------------------------------------*/

        mAquery = new AQuery(this);

        // setupControlsCallbacks();
        setupCastListener();
        mCastContext = CastContext.getSharedInstance(this);
        mCastContext.registerLifecycleCallbacksBeforeIceCreamSandwich(this, savedInstanceState);
        mCastSession = mCastContext.getSessionManager().getCurrentCastSession();

        boolean shouldStartPlayback = false;
        int startPosition = 0;

         /*   MediaMetadata movieMetadata = new MediaMetadata(MediaMetadata.MEDIA_TYPE_MOVIE);

            movieMetadata.putString(MediaMetadata.KEY_SUBTITLE, movieName.getText().toString());
            movieMetadata.putString(MediaMetadata.KEY_TITLE,  movieName.getText().toString());
            movieMetadata.addImage(new WebImage(Uri.parse(posterImageId.trim())));
            movieMetadata.addImage(new WebImage(Uri.parse(posterImageId.trim())));
            JSONObject jsonObj = null;
            try {
                jsonObj = new JSONObject();
                jsonObj.put("description", movieName.getText().toString());
            } catch (JSONException e) {
                Log.e(TAG, "Failed to add description to the json object", e);
            }

            mediaInfo = new MediaInfo.Builder(castVideoUrl.trim())
                    .setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
                    .setContentType("videos/mp4")
                    .setMetadata(movieMetadata)
                    .setStreamDuration(15 * 1000)
                    .setCustomData(jsonObj)
                    .build();
            mSelectedMedia = mediaInfo;*/

        // see what we need to play and where
           /* Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                mSelectedMedia = getIntent().getParcelableExtra("media");
                //setupActionBar();
                boolean shouldStartPlayback = bundle.getBoolean("shouldStart");
                int startPosition = bundle.getInt("startPosition", 0);
                // mVideoView.setVideoURI(Uri.parse(mSelectedMedia.getContentId()));
               // Log.d(TAG, "Setting url of the VideoView to: " + mSelectedMedia.getContentId());
                if (shouldStartPlayback) {
                    // this will be the case only if we are coming from the
                    // CastControllerActivity by disconnecting from a device
                    mPlaybackState = PlaybackState.PLAYING;
                    updatePlaybackLocation(PlaybackLocation.LOCAL);
                    updatePlayButton(mPlaybackState);
                    if (startPosition > 0) {
                        // mVideoView.seekTo(startPosition);
                    }
                    // mVideoView.start();
                    //startControllersTimer();
                } else {
                    // we should load the video but pause it
                    // and show the album art.
                    if (mCastSession != null && mCastSession.isConnected()) {
                        updatePlaybackLocation(PlaybackLocation.REMOTE);
                    } else {
                        updatePlaybackLocation(PlaybackLocation.LOCAL);
                    }
                    mPlaybackState = PlaybackState.IDLE;
                    updatePlayButton(mPlaybackState);
                }
            }*/


        if (shouldStartPlayback) {
            // this will be the case only if we are coming from the
            // CastControllerActivity by disconnecting from a device
            mPlaybackState = MovieDetailsActivity.PlaybackState.PLAYING;
            updatePlaybackLocation(MovieDetailsActivity.PlaybackLocation.LOCAL);
            updatePlayButton(mPlaybackState);
            if (startPosition > 0) {
                // mVideoView.seekTo(startPosition);
            }
            // mVideoView.start();
            //startControllersTimer();
        } else {
            // we should load the video but pause it
            // and show the album art.
            if (mCastSession != null && mCastSession.isConnected()) {
                //watchMovieButton.setText(getResources().getString(R.string.movie_details_cast_now_button_title));
                updatePlaybackLocation(MovieDetailsActivity.PlaybackLocation.REMOTE);
            } else {
                //watchMovieButton.setText(getResources().getString(R.string.movie_details_watch_video_button_title));

                updatePlaybackLocation(MovieDetailsActivity.PlaybackLocation.LOCAL);
            }
            mPlaybackState = MovieDetailsActivity.PlaybackState.IDLE;
            updatePlayButton(mPlaybackState);
        }
/***************chromecast**********************/
    }

    //Verify the login
  /*  private class AsynPayByPaypalDetails extends AsyncTask<Void, Void, Void> {
        ProgressDialog pDialog;

        int statusCode;
        String userIdStr = "";
        String studioIdStr = "";
        String urlStr = "";

        String responseStr;

        @Override
        protected Void doInBackground(Void... params) {

            if (loginPref != null) {
                userIdStr = loginPref.getString("PREFS_LOGGEDIN_ID_KEY", null);
                studioIdStr = loginPref.getString("PREFS_LOGIN_STUDIO_ID_KEY", null);
            }
            String urlRouteList;
            if (isAPV == 1) {
                urlRouteList = Util.rootUrl().trim() + Util.getPayByPalUrl.trim() + "?authToken=" + Util.authTokenStr.trim() + "&user_id=" + userIdStr.trim() + "&studio_id=" + studioIdStr.trim() + "&plan_id=" + planIdForPaypal + "&movie_id=" + muviUniqueIdStr.trim() + "&is_advance=" + isAPV;
            } else {
                urlRouteList = Util.rootUrl().trim() + Util.getPayByPalUrl.trim() + "?authToken=" + Util.authTokenStr.trim() + "&user_id=" + userIdStr.trim() + "&studio_id=" + studioIdStr.trim() + "&plan_id=" + planIdForPaypal + "&movie_id=" + muviUniqueIdStr.trim();

            }
            if (isCouponCodeAdded == true) {
                urlRouteList = urlRouteList.trim() + "&coupon=" + validCouponCode.trim().toUpperCase();
            }

            try {

                // Execute HTTP Post Request
                try {

                    HttpClient client = new DefaultHttpClient();
                    HttpGet request = new HttpGet(urlRouteList);
                    HttpResponse response = client.execute(request);

// Get the response
                    BufferedReader rd = new BufferedReader
                            (new InputStreamReader(
                                    response.getEntity().getContent()));

                    String line = "";
                    while ((line = rd.readLine()) != null) {
                        responseStr = line;
                    }
                    // NEW CODE
                    //HttpResponse response = httpclient.execute(httppost);
                    // responseStr = EntityUtils.toString(response.getEntity());
                } catch (org.apache.http.conn.ConnectTimeoutException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            statusCode = 0;
                            //Crouton.showText(ShowWithEpisodesListActivity.this, "Slow Internet Connection", Style.INFO);
                            Toast.makeText(PPvPaymentInfoActivity.this, "Slow Internet Connection", Toast.LENGTH_LONG).show();
                            finish();

                        }

                    });

                } catch (IOException e) {
                    statusCode = 0;

                    e.printStackTrace();
                }


                if (responseStr != null) {
                    JSONObject myJson = new JSONObject(responseStr);
                    statusCode = Integer.parseInt(myJson.optString("code"));
                    urlStr = myJson.optString("url");


                }

            } catch (Exception e) {
                statusCode = 0;

            }

            return null;
        }


        protected void onPostExecute(Void result) {
            try {
                if (pDialog.isShowing())
                    pDialog.dismiss();
            } catch (IllegalArgumentException ex) {
                statusCode = 0;

            }
            if (responseStr == null) {
                try {
                    if (pDialog.isShowing())
                        pDialog.dismiss();
                } catch (IllegalArgumentException ex) {
                    statusCode = 0;

                }
                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(PPvPaymentInfoActivity.this);
                dlgAlert.setMessage(getResources().getString(R.string.empty_paybypal_alert));
                dlgAlert.setTitle(getResources().getString(R.string.sorry_str));
                dlgAlert.setPositiveButton(getResources().getString(R.string.ok_str), null);
                dlgAlert.setCancelable(false);
                dlgAlert.setPositiveButton(getResources().getString(R.string.ok_str),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                dlgAlert.create().show();
            }

            if (statusCode > 0) {
                if (statusCode == 200) {
                    final Intent playByPaypalIntent = new Intent(PPvPaymentInfoActivity.this, PayByPaypalActivity.class);
                    playByPaypalIntent.putExtra("url", urlStr.trim());
                    playByPaypalIntent.putExtra("isApv", isAPV);
                    playByPaypalIntent.putExtra("isPpv", isPPV);
                    playByPaypalIntent.putExtra("castConnected", isCastConnected);
                    playByPaypalIntent.putExtra("content_uniq_id", muviUniqueIdStr.trim());
                    playByPaypalIntent.putExtra("stream_uniq_id", movieStreamUniqueIdStr.trim());
                    playByPaypalIntent.putExtra("internet_speed", MainActivity.internetSpeed.trim());

                 *//*   if (isAPV ==1){
                        Toast.makeText(PPvPaymentInfoActivity.this,"You have successfully purchased the content.",Toast.LENGTH_LONG).show();
                        onBackPressed();
                    }else {
                        if(isCastConnected == true) {
                            onBackPressed();

                        }else {
                            AsynLoadVideoUrls asynLoadVideoUrls = new AsynLoadVideoUrls();
                            asynLoadVideoUrls.executeOnExecutor(threadPoolExecutor);
                        }
                    }*//*
                    runOnUiThread(new Runnable() {
                        public void run() {
                            playByPaypalIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            startActivity(playByPaypalIntent);
                            finish();

                        }
                    });
                } else {
                    AlertDialog.Builder dlgAlert = new AlertDialog.Builder(PPvPaymentInfoActivity.this);
                    dlgAlert.setMessage(getResources().getString(R.string.empty_paybypal_alert));
                    dlgAlert.setTitle(getResources().getString(R.string.sorry_str));
                    dlgAlert.setPositiveButton(getResources().getString(R.string.ok_str), null);
                    dlgAlert.setCancelable(false);
                    dlgAlert.setPositiveButton(getResources().getString(R.string.ok_str),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    finish();
                                }
                            });
                    dlgAlert.create().show();
                }
            } else {
                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(PPvPaymentInfoActivity.this);
                dlgAlert.setMessage(getResources().getString(R.string.empty_paybypal_alert));
                dlgAlert.setTitle(getResources().getString(R.string.sorry_str));
                dlgAlert.setPositiveButton(getResources().getString(R.string.ok_str), null);
                dlgAlert.setCancelable(false);
                dlgAlert.setPositiveButton(getResources().getString(R.string.ok_str),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                finish();
                            }
                        });
                dlgAlert.create().show();
            }

        }

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(PPvPaymentInfoActivity.this, R.style.CustomDialogTheme);
            pDialog.setCancelable(false);
            pDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Large_Inverse);
            pDialog.setIndeterminate(false);
            pDialog.setIndeterminateDrawable(getResources().getDrawable(R.drawable.progress_rawable));
            if (pDialog != null && !pDialog.isShowing()) {
                pDialog.show();
            }
        }


    }*/

    private TextWatcher filterTextWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // DO THE CALCULATIONS HERE AND SHOW THE RESULT AS PER YOUR CALCULATIONS
            if (isCouponCodeAdded == true) {
                if (s.length() <= 0) {
                    withoutCreditCardLayout.setVisibility(View.GONE);
                    if(voucherRadioButton.isChecked() == true){
                        voucherLinearLayout.setVisibility(View.VISIBLE);
                    }
                    else{
                        creditCardLayout.setVisibility(View.VISIBLE);
                    }


                    paymentOptionLinearLayout.setVisibility(View.VISIBLE);
                    chargedPriceTextView.setText(Util.getTextofLanguage(PPvPaymentInfoActivity.this,Util.CARD_WILL_CHARGE,Util.DEFAULT_CARD_WILL_CHARGE)+" " + currencySymbolStr + previousChargedPrice);
                    isCouponCodeAdded = false;
                    validCouponCode = "";
                    nameOnCardEditText.setText("");
                    cardNumberEditText.setText("");
                    securityCodeEditText.setText("");
                    cardExpiryMonthSpinner.setSelection(0);
                    cardExpiryYearSpinner.setSelection(0);
                    expiryMonthStr = monthsIdArray.get(0);

                    expiryYearStr = yearArray.get(0);


                    Util.showToast(PPvPaymentInfoActivity.this, Util.getTextofLanguage(PPvPaymentInfoActivity.this,Util.COUPON_CANCELLED,Util.DEFAULT_COUPON_CANCELLED));

                   // Toast.makeText(PPvPaymentInfoActivity.this,Util.getTextofLanguage(PPvPaymentInfoActivity.this,Util.COUPON_CANCELLED,Util.DEFAULT_COUPON_CANCELLED), Toast.LENGTH_LONG).show();

                }
            }

        }


        @Override
        public void afterTextChanged(Editable s) {

        }


        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }


    };


    //Load Films Videos
    private class AsynLoadCardList extends AsyncTask<Void, Void, Void> {
        String responseStr;
        int status;

        ArrayList<CardModel> savedCards = new ArrayList<CardModel>();

        @Override
        protected Void doInBackground(Void... params) {

            String urlRouteList = Util.rootUrl().trim() + Util.getCardDetailsUrl.trim();
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(urlRouteList);
                httppost.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");
                httppost.addHeader("authToken", Util.authTokenStr.trim());
                String userIdStr = loginPref.getString("PREFS_LOGGEDIN_ID_KEY", null);
                httppost.addHeader("user_id", userIdStr.trim());

                // Execute HTTP Post Request
                try {
                    HttpResponse response = httpclient.execute(httppost);
                    responseStr = EntityUtils.toString(response.getEntity());
                } catch (org.apache.http.conn.ConnectTimeoutException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if (cardSavedArray == null || cardSavedArray.length <= 0) {
                                creditCardSaveSpinner.setVisibility(View.GONE);
                            }

                            Util.showToast(PPvPaymentInfoActivity.this, Util.getTextofLanguage(PPvPaymentInfoActivity.this,Util.NO_INTERNET_CONNECTION,Util.DEFAULT_NO_INTERNET_CONNECTION));

                        //    Toast.makeText(PPvPaymentInfoActivity.this, Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.NO_INTERNET_CONNECTION, Util.DEFAULT_NO_INTERNET_CONNECTION), Toast.LENGTH_LONG).show();

                        }

                    });

                } catch (IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (cardSavedArray == null || cardSavedArray.length <= 0) {
                                creditCardSaveSpinner.setVisibility(View.GONE);
                            }
                        }
                    });
                    e.printStackTrace();
                }

                JSONObject myJson = null;
                if (responseStr != null) {
                    myJson = new JSONObject(responseStr);
                    status = Integer.parseInt(myJson.optString("code"));
                }

                if (status > 0) {
                    if (status == 200) {

                        JSONArray jsonMainNode = myJson.getJSONArray("cards");

                        int lengthJsonArr = jsonMainNode.length();
                        for (int i = 0; i < lengthJsonArr; i++) {
                            JSONObject jsonChildNode;
                            String cardIdStr = "";
                            String cardNumberStr = "";

                            try {
                                jsonChildNode = jsonMainNode.getJSONObject(i);

                                if ((jsonChildNode.has("card_id")) && jsonChildNode.getString("card_id").trim() != null && !jsonChildNode.getString("card_id").trim().isEmpty() && !jsonChildNode.getString("card_id").trim().equals("null") && !jsonChildNode.getString("card_id").trim().matches("") && !jsonChildNode.getString("card_id").trim().equalsIgnoreCase("")) {
                                    cardIdStr = jsonChildNode.getString("card_id");


                                }
                                if ((jsonChildNode.has("card_last_fourdigit")) && jsonChildNode.getString("card_last_fourdigit").trim() != null && !jsonChildNode.getString("card_last_fourdigit").trim().isEmpty() && !jsonChildNode.getString("card_last_fourdigit").trim().equals("null") && !jsonChildNode.getString("card_last_fourdigit").trim().matches("") && !jsonChildNode.getString("card_id").trim().equalsIgnoreCase("")) {
                                    cardNumberStr = jsonChildNode.getString("card_last_fourdigit");

                                }
                                if (cardIdStr != null && !cardIdStr.matches("") && !cardIdStr.equalsIgnoreCase("")) {
                                    savedCards.add(new CardModel(cardIdStr, cardNumberStr));
                                }
                            } catch (Exception e) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        creditCardSaveSpinner.setVisibility(View.GONE);

                                    }
                                });
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                    } else {
                        responseStr = "0";
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                creditCardSaveSpinner.setVisibility(View.GONE);

                            }
                        });
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        creditCardSaveSpinner.setVisibility(View.GONE);

                    }
                });
            }
            return null;

        }

        protected void onPostExecute(Void result) {
            try {
                if (videoPDialog.isShowing())
                    videoPDialog.hide();
            } catch (IllegalArgumentException ex) {

                creditCardSaveSpinner.setVisibility(View.GONE);
            }
            if (responseStr == null)
                responseStr = "0";
            if ((responseStr.trim().equals("0"))) {
                try {
                    if (videoPDialog.isShowing())
                        videoPDialog.hide();
                } catch (IllegalArgumentException ex) {

                    creditCardSaveSpinner.setVisibility(View.GONE);

                }
                creditCardSaveSpinner.setVisibility(View.GONE);

            } else {
                if (savedCards.size() <= 0) {
                    try {
                        if (videoPDialog.isShowing())
                            videoPDialog.hide();
                    } catch (IllegalArgumentException ex) {

                        creditCardSaveSpinner.setVisibility(View.GONE);

                    }
                    creditCardSaveSpinner.setVisibility(View.GONE);

                } else {
                    savedCards.add(0, new CardModel("0",Util.getTextofLanguage(PPvPaymentInfoActivity.this,Util.USE_NEW_CARD,Util.DEFAULT_USE_NEW_CARD)));
                    cardSavedArray = savedCards.toArray(new CardModel[savedCards.size()]);
                    creditCardSaveSpinnerAdapter = new CardSpinnerAdapter(PPvPaymentInfoActivity.this, cardSavedArray);
                    //cardExpiryYearSpinnerAdapter = new CardSpinnerAdapter<Integer>(this, R.layout.spinner_new, yearArray);

                    creditCardSaveSpinner.setAdapter(creditCardSaveSpinnerAdapter);
                    creditCardSaveSpinner.setSelection(0);
                }
            }
        }

        @Override
        protected void onPreExecute() {
           /* videoPDialog = new ProgressDialog(PPvPaymentInfoActivity.this, R.style.CustomDialogTheme);
            videoPDialog.setCancelable(false);
            videoPDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Large_Inverse);
            videoPDialog.setIndeterminate(false);
            videoPDialog.setIndeterminateDrawable(getResources().getDrawable(R.drawable.progress_rawable));
            videoPDialog.show();*/

            videoPDialog = new ProgressBarHandler(PPvPaymentInfoActivity.this);
            videoPDialog.show();


        }


    }

    //load video urls as per resolution


    /*private class AsynLoadVideoUrls extends AsyncTask<Void, Void, Void> {
        ProgressBarHandler pDialog;
        String responseStr;
        int statusCode;
        // This is added because of change in simultaneous login feature
        String message;
        boolean play_video = true;
        @Override
        protected Void doInBackground(Void... params) {
            try {
                HttpClient httpclient=new DefaultHttpClient();
                HttpPost httppost = new HttpPost(Util.rootUrl().trim()+Util.loadVideoUrl.trim());
                httppost.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");
                httppost.addHeader("authToken", Util.authTokenStr.trim());
                httppost.addHeader("content_uniq_id", Util.dataModel.getMovieUniqueId().trim());
                httppost.addHeader("stream_uniq_id", Util.dataModel.getStreamUniqueId().trim());
                httppost.addHeader("internet_speed",MainActivity.internetSpeed.trim());
                httppost.addHeader("user_id",loginPref.getString("PREFS_LOGGEDIN_ID_KEY", null));
                httppost.addHeader("lang_code",Util.getTextofLanguage(PPvPaymentInfoActivity.this,Util.SELECTED_LANGUAGE_CODE,Util.DEFAULT_SELECTED_LANGUAGE_CODE));


                // Execute HTTP Post Request
                try {

                    HttpResponse response = httpclient.execute(httppost);
                    responseStr = EntityUtils.toString(response.getEntity());

                } catch (org.apache.http.conn.ConnectTimeoutException e){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (pDialog != null && pDialog.isShowing()) {
                                pDialog.hide();
                                pDialog = null;
                            }
                            responseStr = "0";
                            Util.dataModel.setVideoUrl(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.NO_DATA, Util.DEFAULT_NO_DATA));
                            Util.showToast(PPvPaymentInfoActivity.this, Util.getTextofLanguage(PPvPaymentInfoActivity.this,Util.SLOW_INTERNET_CONNECTION,Util.DEFAULT_SLOW_INTERNET_CONNECTION));

                            //  Toast.makeText(PPvPaymentInfoActivity.this, Util.getTextofLanguage(PPvPaymentInfoActivity.this,Util.SLOW_INTERNET_CONNECTION,Util.DEFAULT_SLOW_INTERNET_CONNECTION), Toast.LENGTH_LONG).show();

                        }

                    });

                }catch (IOException e) {
                    if (pDialog != null && pDialog.isShowing()) {
                        pDialog.hide();
                        pDialog = null;
                    }
                    responseStr = "0";
                    Util.dataModel.setVideoUrl(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.NO_DATA, Util.DEFAULT_NO_DATA));
                    e.printStackTrace();
                }

                JSONObject myJson =null;
                JSONArray SubtitleJosnArray = null;
                JSONArray ResolutionJosnArray = null;
                if(responseStr!=null){
                    myJson = new JSONObject(responseStr);
                    SubtitleJosnArray = myJson.optJSONArray("subTitle");
                    ResolutionJosnArray = myJson.optJSONArray("videoDetails");
                    statusCode = Integer.parseInt(myJson.optString("code"));
                    // This is added because of change in simultaneous login feature
                    message = myJson.optString("msg");

                    Log.v("BIBHU","video stream msg"+message);
                    // ================================== End ====================================//
                }

                if (statusCode >= 0) {
                    if (statusCode == 200) {
                        if (Util.dataModel.getThirdPartyUrl().matches("") || Util.dataModel.getThirdPartyUrl().equalsIgnoreCase(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.NO_DATA, Util.DEFAULT_NO_DATA))) {
                            if ((myJson.has("studio_approved_url")) && myJson.getString("studio_approved_url").trim() != null && !myJson.getString("studio_approved_url").trim().isEmpty() && !myJson.getString("studio_approved_url").trim().equals("null") && !myJson.getString("studio_approved_url").trim().matches("")) {
                                Util.dataModel.setVideoUrl(myJson.getString("studio_approved_url"));
                                if ((myJson.has("licenseUrl")) && myJson.getString("licenseUrl").trim() != null && !myJson.getString("licenseUrl").trim().isEmpty() && !myJson.getString("licenseUrl").trim().equals("null") && !myJson.getString("licenseUrl").trim().matches("")) {
                                    Util.dataModel.setLicenseUrl(myJson.getString("licenseUrl"));
                                }
                                if ((myJson.has("videoUrl")) && myJson.getString("videoUrl").trim() != null && !myJson.getString("videoUrl").trim().isEmpty() && !myJson.getString("videoUrl").trim().equals("null") && !myJson.getString("videoUrl").trim().matches("")) {
                                    Util.dataModel.setMpdVideoUrl(myJson.getString("videoUrl"));

                                }else {
                                    Util.dataModel.setMpdVideoUrl(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.NO_DATA, Util.DEFAULT_NO_DATA));
                                }
                            }

                           *//* if ((myJson.has("videoUrl")) && myJson.getString("videoUrl").trim() != null && !myJson.getString("videoUrl").trim().isEmpty() && !myJson.getString("videoUrl").trim().equals("null") && !myJson.getString("videoUrl").trim().matches("")) {
                                Util.dataModel.setVideoUrl(myJson.getString("videoUrl"));
                            }*//*

                            else{
                                if ((myJson.has("videoUrl")) && myJson.getString("videoUrl").trim() != null && !myJson.getString("videoUrl").trim().isEmpty() && !myJson.getString("videoUrl").trim().equals("null") && !myJson.getString("videoUrl").trim().matches("")) {
                                    Util.dataModel.setVideoUrl(myJson.getString("videoUrl"));

                                }else {
                                    Util.dataModel.setVideoUrl(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.NO_DATA, Util.DEFAULT_NO_DATA));
                                }
                            }

                        } else {
                            if ((myJson.has("thirdparty_url")) && myJson.getString("thirdparty_url").trim() != null && !myJson.getString("thirdparty_url").trim().isEmpty() && !myJson.getString("thirdparty_url").trim().equals("null") && !myJson.getString("thirdparty_url").trim().matches("")) {
                                Util.dataModel.setVideoUrl(myJson.getString("thirdparty_url"));

                            } else {
                                Util.dataModel.setVideoUrl(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.NO_DATA, Util.DEFAULT_NO_DATA));

                            }
                        }
                        if ((myJson.has("videoResolution")) && myJson.getString("videoResolution").trim() != null && !myJson.getString("videoResolution").trim().isEmpty() && !myJson.getString("videoResolution").trim().equals("null") && !myJson.getString("videoResolution").trim().matches("")) {
                            Util.dataModel.setVideoResolution(myJson.getString("videoResolution"));

                        }
                        if ((myJson.has("played_length")) && myJson.getString("played_length").trim() != null && !myJson.getString("played_length").trim().isEmpty() && !myJson.getString("played_length").trim().equals("null") && !myJson.getString("played_length").trim().matches("")) {
                            Util.dataModel.setPlayPos(Util.isDouble(myJson.getString("played_length")));
                        } else {

                        }

                        if((myJson.has("is_offline")) && myJson.getString("is_offline").trim() != null && !myJson.getString("is_offline").trim().isEmpty() && !myJson.getString("is_offline").trim().equals("null") && !myJson.getString("is_offline").trim().matches("")){

                            //offline = myJson.getString("is_offline");
                            Util.dataModel.setIsOffline(Util.isOffline=myJson.getString("is_offline"));

                        }else {


                        }

                        if (SubtitleJosnArray != null) {
                            if (SubtitleJosnArray.length() > 0) {
                                for (int i = 0; i < SubtitleJosnArray.length(); i++) {
                                    SubTitleName.add(SubtitleJosnArray.getJSONObject(i).optString("language").trim());
                                    FakeSubTitlePath.add(SubtitleJosnArray.getJSONObject(i).optString("url").trim());
                                    SubTitleLanguage.add(SubtitleJosnArray.getJSONObject(i).optString("code").trim());

                                }
                            }
                        }


                        // This is added because of change in simultaneous login feature
                        if(Util.getTextofLanguage(PPvPaymentInfoActivity.this,Util.IS_STREAMING_RESTRICTION,Util.DEFAULT_IS_IS_STREAMING_RESTRICTION).equals("1"))
                        {

                            Log.v("BIBHU","streaming_restriction============"+myJson.optString("streaming_restriction").toString().trim());

                            if(myJson.optString("streaming_restriction").toString().trim().equals("0"))
                            {
                                play_video = false;
                            }
                            else
                            {
                                play_video = true;
                            }
                        }
                        else
                        {
                            play_video = true;
                        }

                        // ================================== End ====================================//
                    }

                }
                else {

                    responseStr = "0";
                    Util.dataModel.setVideoUrl(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.NO_DATA, Util.DEFAULT_NO_DATA));
                }
            } catch (JSONException e1) {
                if (pDialog != null && pDialog.isShowing()) {
                    pDialog.hide();
                    pDialog = null;
                }
                responseStr = "0";
                Util.dataModel.setVideoUrl(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.NO_DATA, Util.DEFAULT_NO_DATA));
                e1.printStackTrace();
            }

            catch (Exception e)
            {
                if (pDialog != null && pDialog.isShowing()) {
                    pDialog.hide();
                    pDialog = null;
                }
                responseStr = "0";
                Util.dataModel.setVideoUrl(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.NO_DATA, Util.DEFAULT_NO_DATA));

                e.printStackTrace();

            }
            return null;

        }

        protected void onPostExecute(Void result) {

            // This is added because of change in simultaneous login feature //

            if (!play_video) {

                try {
                    if (pDialog.isShowing())
                        pDialog.hide();
                } catch (IllegalArgumentException ex) {
                }

                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(PPvPaymentInfoActivity.this, R.style.MyAlertDialogStyle);
                dlgAlert.setMessage(message);
                dlgAlert.setTitle(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.SORRY, Util.DEFAULT_SORRY));
                dlgAlert.setPositiveButton(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.BUTTON_OK, Util.DEFAULT_BUTTON_OK), null);
                dlgAlert.setCancelable(false);
                dlgAlert.setPositiveButton(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.BUTTON_OK, Util.DEFAULT_BUTTON_OK),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();

                            }
                        });
                dlgAlert.create().show();

                return;
            }

            //=====================End========================================//
            if (responseStr == null) {
                responseStr = "0";
                Util.dataModel.setVideoUrl(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.NO_DATA, Util.DEFAULT_NO_DATA));
                //movieThirdPartyUrl = getResources().getString(R.string.no_data_str);
            }

            if ((responseStr.trim().equalsIgnoreCase("0"))) {
                try {
                    if (pDialog != null && pDialog.isShowing()) {
                        pDialog.hide();
                        pDialog = null;
                    }
                } catch (IllegalArgumentException ex) {
                    Util.dataModel.setVideoUrl(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.NO_DATA, Util.DEFAULT_NO_DATA));
                    // movieThirdPartyUrl = getResources().getString(R.string.no_data_str);
                }
                Util.dataModel.setVideoUrl(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.NO_DATA, Util.DEFAULT_NO_DATA));
                //movieThirdPartyUrl = getResources().getString(R.string.no_data_str);
                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(PPvPaymentInfoActivity.this, R.style.MyAlertDialogStyle);
                dlgAlert.setMessage(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.NO_VIDEO_AVAILABLE, Util.DEFAULT_NO_VIDEO_AVAILABLE));
                dlgAlert.setTitle(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.SORRY, Util.DEFAULT_SORRY));
                dlgAlert.setPositiveButton(Util.getTextofLanguage(PPvPaymentInfoActivity.this,Util.BUTTON_OK,Util.DEFAULT_BUTTON_OK), null);
                dlgAlert.setCancelable(false);
                dlgAlert.setPositiveButton(Util.getTextofLanguage(PPvPaymentInfoActivity.this,Util.BUTTON_OK,Util.DEFAULT_BUTTON_OK),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                dlgAlert.create().show();
            } else {

                if (Util.dataModel.getVideoUrl() == null) {
                    try {
                        if (pDialog != null && pDialog.isShowing()) {
                            pDialog.hide();
                            pDialog = null;
                        }
                    } catch (IllegalArgumentException ex) {
                        Util.dataModel.setVideoUrl(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.NO_DATA, Util.DEFAULT_NO_DATA));
                    }
                    AlertDialog.Builder dlgAlert = new AlertDialog.Builder(PPvPaymentInfoActivity.this, R.style.MyAlertDialogStyle);
                    dlgAlert.setMessage(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.NO_VIDEO_AVAILABLE, Util.DEFAULT_NO_VIDEO_AVAILABLE));
                    dlgAlert.setTitle(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.SORRY, Util.DEFAULT_SORRY));
                    dlgAlert.setPositiveButton(Util.getTextofLanguage(PPvPaymentInfoActivity.this,Util.BUTTON_OK,Util.DEFAULT_BUTTON_OK), null);
                    dlgAlert.setCancelable(false);
                    dlgAlert.setPositiveButton(Util.getTextofLanguage(PPvPaymentInfoActivity.this,Util.BUTTON_OK,Util.DEFAULT_BUTTON_OK),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    dlgAlert.create().show();
                } else if (Util.dataModel.getVideoUrl().matches("") || Util.dataModel.getVideoUrl().equalsIgnoreCase(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.NO_DATA, Util.DEFAULT_NO_DATA))) {
                    try {
                        if (pDialog != null && pDialog.isShowing()) {
                            pDialog.hide();
                            pDialog = null;
                        }
                    } catch (IllegalArgumentException ex) {
                        Util.dataModel.setVideoUrl(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.NO_DATA, Util.DEFAULT_NO_DATA));
                    }
                    AlertDialog.Builder dlgAlert = new AlertDialog.Builder(PPvPaymentInfoActivity.this, R.style.MyAlertDialogStyle);
                    dlgAlert.setMessage(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.NO_VIDEO_AVAILABLE, Util.DEFAULT_NO_VIDEO_AVAILABLE));
                    dlgAlert.setTitle(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.SORRY, Util.DEFAULT_SORRY));
                    dlgAlert.setPositiveButton(Util.getTextofLanguage(PPvPaymentInfoActivity.this,Util.BUTTON_OK,Util.DEFAULT_BUTTON_OK), null);
                    dlgAlert.setCancelable(false);
                    dlgAlert.setPositiveButton(Util.getTextofLanguage(PPvPaymentInfoActivity.this,Util.BUTTON_OK,Util.DEFAULT_BUTTON_OK),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    dlgAlert.create().show();
                } else {
                    try {
                        if (pDialog != null && pDialog.isShowing()) {
                            pDialog.hide();
                            pDialog = null;
                        }
                    } catch (IllegalArgumentException ex) {
                        Util.dataModel.setVideoUrl(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.NO_DATA, Util.DEFAULT_NO_DATA));
                    }

                    if (Util.dataModel.getThirdPartyUrl().matches("") || Util.dataModel.getThirdPartyUrl().equalsIgnoreCase(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.NO_DATA, Util.DEFAULT_NO_DATA))) {

                        if (isCastConnected == true) {
                            onBackPressed();
                        }else {

                            if (Util.dataModel.getVideoUrl().contains("rtmp://") || Util.dataModel.getVideoUrl().contains("rtmp://")) {
                                Toast.makeText(PPvPaymentInfoActivity.this, Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.VIDEO_ISSUE, Util.DEFAULT_VIDEO_ISSUE), Toast.LENGTH_SHORT).show();
                            } else {
                                final Intent playVideoIntent;

                                playVideoIntent = new Intent(PPvPaymentInfoActivity.this, ExoPlayerActivity.class);


                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        if (FakeSubTitlePath.size() > 0) {
                                            // This Portion Will Be changed Later.

                                            File dir = new File(Environment.getExternalStorageDirectory() + "/Android/data/" + getApplicationContext().getPackageName().trim() + "/SubTitleList/");
                                            if (dir.isDirectory()) {
                                                String[] children = dir.list();
                                                for (int i = 0; i < children.length; i++) {
                                                    new File(dir, children[i]).delete();
                                                }
                                            }

                                            progressBarHandler = new ProgressBarHandler(PPvPaymentInfoActivity.this);
                                            progressBarHandler.show();
                                            Download_SubTitle(FakeSubTitlePath.get(0).trim());
                                        } else {
                                            playVideoIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                            playVideoIntent.putExtra("SubTitleName", SubTitleName);
                                            playVideoIntent.putExtra("SubTitlePath", SubTitlePath);
                                            startActivity(playVideoIntent);

                                            finish();
                                        }


                                    }
                                });
                            }
                        }
                    } else {
                        if (Util.dataModel.getVideoUrl().contains("://www.youtube") || Util.dataModel.getVideoUrl().contains("://www.youtu.be")) {
                            if (Util.dataModel.getVideoUrl().contains("live_stream?channel")) {
                                final Intent playVideoIntent = new Intent(PPvPaymentInfoActivity.this, ThirdPartyPlayer.class);
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        playVideoIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                        startActivity(playVideoIntent);

                                    }
                                });
                            } else {

                                final Intent playVideoIntent = new Intent(PPvPaymentInfoActivity.this, YouTubeAPIActivity.class);
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        playVideoIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                        startActivity(playVideoIntent);


                                    }
                                });

                            }
                        } else {
                            final Intent playVideoIntent = new Intent(PPvPaymentInfoActivity.this, ThirdPartyPlayer.class);
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    playVideoIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                    startActivity(playVideoIntent);

                                }
                            });
                        }
                    }
                }


            }
        }


        @Override
        protected void onPreExecute() {

            SubTitleName.clear();
            SubTitlePath.clear();
            ResolutionUrl.clear();
            ResolutionFormat.clear();
            SubTitleLanguage.clear();
            pDialog = new ProgressBarHandler(PPvPaymentInfoActivity.this);
            pDialog.show();

        }


    }*/

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private class AsynCouponInfoDetails extends AsyncTask<Void, Void, Void> {
        ProgressBarHandler pDialog1;
        String responseStr;
        String statusStr;
        String couponCodeStr = couponCodeEditText.getText().toString().trim();
        int status;

        @Override
        protected Void doInBackground(Void... params) {
            String userIdStr = loginPref.getString("PREFS_LOGGEDIN_ID_KEY", null);

            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(Util.rootUrl().trim() + Util.couponCodeValidationUrl.trim());
                httppost.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");
                httppost.addHeader("authToken", Util.authTokenStr.trim());
                httppost.addHeader("couponCode", couponCodeStr);
                httppost.addHeader("user_id", userIdStr.trim());
                httppost.addHeader("currencyId", currencyIdStr.trim());

                // Execute HTTP Post Request
                try {
                    HttpResponse response = httpclient.execute(httppost);
                    responseStr = EntityUtils.toString(response.getEntity());

                    Log.v("SUBHA","response == "+ responseStr);

                } catch (org.apache.http.conn.ConnectTimeoutException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (pDialog1 != null && pDialog1.isShowing()) {
                                pDialog1.hide();
                                pDialog1 = null;
                            }
                            chargedPriceTextView.setText(Util.getTextofLanguage(PPvPaymentInfoActivity.this,Util.CARD_WILL_CHARGE,Util.DEFAULT_CARD_WILL_CHARGE)+" " + currencySymbolStr + chargedPrice);
                            //selectShowRadioButton.setText("Entire Show: "+currencySymbolStr+planPrice);
                            isCouponCodeAdded = false;
                            validCouponCode = "";
                            Util.showToast(PPvPaymentInfoActivity.this, Util.getTextofLanguage(PPvPaymentInfoActivity.this,Util.SLOW_INTERNET_CONNECTION,Util.DEFAULT_SLOW_INTERNET_CONNECTION));

                           // Toast.makeText(PPvPaymentInfoActivity.this,Util.getTextofLanguage(PPvPaymentInfoActivity.this,Util.SLOW_INTERNET_CONNECTION,Util.DEFAULT_SLOW_INTERNET_CONNECTION)+"."+Util.getTextofLanguage(PPvPaymentInfoActivity.this,Util.COUPON_CANCELLED,Util.DEFAULT_COUPON_CANCELLED), Toast.LENGTH_LONG).show();
                            couponCodeEditText.setText("");


                        }

                    });

                } catch (IOException e) {
                    if (pDialog1 != null && pDialog1.isShowing()) {
                        pDialog1.hide();
                        pDialog1 = null;
                    }
                    responseStr = "0";
                    isCouponCodeAdded = false;
                    validCouponCode = "";
                    e.printStackTrace();
                }


                JSONObject myJson = null;
                if (responseStr != null) {
                    myJson = new JSONObject(responseStr);
                    status = Integer.parseInt(myJson.optString("code"));
                    statusStr = myJson.optString("msg");
                    isCouponCodeAdded = false;
                    validCouponCode = "";

                }

                if (status >= 0) {
                    if (status == 432) {


                        if ((myJson.has("discount_type")) && myJson.getString("discount_type").trim() != null && !myJson.getString("discount_type").trim().isEmpty() && !myJson.getString("discount_type").trim().equals("null") && !myJson.getString("discount_type").trim().matches("")) {
                            String discountTypeStr = myJson.getString("discount_type").trim();

                            if ((myJson.has("discount")) && myJson.getString("discount").trim() != null && !myJson.getString("discount").trim().isEmpty() && !myJson.getString("discount").trim().equals("null") && !myJson.getString("discount").trim().matches("")) {

                                if (discountTypeStr.equalsIgnoreCase("%")) {

                                    chargedPrice = planPrice - planPrice * (Float.parseFloat(myJson.getString("discount")) / 100);

                                    if (chargedPrice < 0.0f) {
                                        chargedPrice = 0.0f;
                                    }
                                } else {

                                    chargedPrice = planPrice - Float.parseFloat(myJson.getString("discount").trim());

                                    if (chargedPrice < 0.0f) {
                                        chargedPrice = 0.0f;
                                    }
                                }

                            }
                        }

                    } else {
                        responseStr = "0";
                        isCouponCodeAdded = false;
                        validCouponCode = "";

                    }
                } else {
                    responseStr = "0";
                    isCouponCodeAdded = false;
                    validCouponCode = "";
                    // couponCodeEditText.setText("");


                }
            } catch (JSONException e1) {
                if (pDialog1 != null && pDialog1.isShowing()) {
                    pDialog1.hide();
                    pDialog1 = null;
                }
                responseStr = "0";
                isCouponCodeAdded = false;
                validCouponCode = "";
                e1.printStackTrace();
            } catch (Exception e) {
                if (pDialog1 != null && pDialog1.isShowing()) {
                    pDialog1.hide();
                    pDialog1 = null;
                }
                responseStr = "0";
                isCouponCodeAdded = false;
                validCouponCode = "";
                e.printStackTrace();

            }
            return null;

        }

        protected void onPostExecute(Void result) {
            try {
                if (pDialog1 != null && pDialog1.isShowing()) {
                    pDialog1.hide();
                    pDialog1 = null;
                }
            } catch (IllegalArgumentException ex) {
                responseStr = "0";
                isCouponCodeAdded = false;
                validCouponCode = "";
            }
            if (responseStr == null) {
                responseStr = "0";
                isCouponCodeAdded = false;
                validCouponCode = "";
                //couponCodeEditText.setText("");
            }
            if ((responseStr.trim().equals("0"))) {
                chargedPriceTextView.setText(Util.getTextofLanguage(PPvPaymentInfoActivity.this,Util.CARD_WILL_CHARGE,Util.DEFAULT_CARD_WILL_CHARGE)+" " + currencySymbolStr + planPrice);
                //selectShowRadioButton.setText("Entire Show: " + currencySymbolStr + planPrice);
                isCouponCodeAdded = false;
                validCouponCode = "";
                couponCodeEditText.setText("");

                if (statusStr.trim() != null && !statusStr.trim().isEmpty() && !statusStr.trim().equals("null") && !statusStr.trim().matches("")) {
                    Util.showToast(PPvPaymentInfoActivity.this,statusStr);

                    //Toast.makeText(PPvPaymentInfoActivity.this, statusStr, Toast.LENGTH_LONG).show();

                } else {

                    Util.showToast(PPvPaymentInfoActivity.this,Util.getTextofLanguage(PPvPaymentInfoActivity.this,Util.INVALID_COUPON,Util.DEFAULT_INVALID_COUPON));
                  //  Toast.makeText(PPvPaymentInfoActivity.this,Util.getTextofLanguage(PPvPaymentInfoActivity.this,Util.INVALID_COUPON,Util.DEFAULT_INVALID_COUPON), Toast.LENGTH_LONG).show();

                }


            } else {
                //selectShowRadioButton.setText("Entire Show: "+currencySymbolStr+planPrice);
                creditCardLayout.setVisibility(View.VISIBLE);
                paymentOptionLinearLayout.setVisibility(View.VISIBLE);



                chargedPriceTextView.setText(Util.getTextofLanguage(PPvPaymentInfoActivity.this,Util.CARD_WILL_CHARGE,Util.DEFAULT_CARD_WILL_CHARGE)+" " + currencySymbolStr + chargedPrice);
                isCouponCodeAdded = true;
                validCouponCode = couponCodeEditText.getText().toString().trim();
                Util.showToast(PPvPaymentInfoActivity.this,Util.getTextofLanguage(PPvPaymentInfoActivity.this,Util.DISCOUNT_ON_COUPON,Util.DEFAULT_DISCOUNT_ON_COUPON));

             //   Toast.makeText(PPvPaymentInfoActivity.this,Util.getTextofLanguage(PPvPaymentInfoActivity.this,Util.DISCOUNT_ON_COUPON,Util.DEFAULT_DISCOUNT_ON_COUPON), Toast.LENGTH_LONG).show();
                if (chargedPrice <= 0.0f && isCouponCodeAdded == true) {
                    creditCardLayout.setVisibility(View.GONE);
                    paymentOptionLinearLayout.setVisibility(View.GONE);
                    //paywithCreditCardButton.setVisibility(View.GONE);
                    voucherLinearLayout.setVisibility(View.GONE);
                    withoutCreditCardLayout.setVisibility(View.VISIBLE);
                    withoutPaymentTitleTextView.setText(Util.getTextofLanguage(PPvPaymentInfoActivity.this,Util.FREE_FOR_COUPON,Util.DEFAULT_FREE_FOR_COUPON));
                    withoutCreditCardChargedPriceTextView.setText(Util.getTextofLanguage(PPvPaymentInfoActivity.this,Util.CARD_WILL_CHARGE,Util.DEFAULT_CARD_WILL_CHARGE)+" : " + currencySymbolStr + chargedPrice);
                }
            }


        }

        @Override
        protected void onPreExecute() {
            pDialog1 = new ProgressBarHandler(PPvPaymentInfoActivity.this);
            pDialog1.show();
        }


    }

    //Verify the login
    private class AsynValidateUserDetails extends AsyncTask<Void, Void, Void> {
        // ProgressDialog pDialog;

        int status;
        String validUserStr;
        String userMessage;
        String responseStr;
        String loggedInIdStr;
        String isSubscribedDataStr;

        @Override
        protected Void doInBackground(Void... params) {
            SharedPreferences pref = getSharedPreferences(Util.LOGIN_PREF, 0); // 0 - for private mode
            if (pref != null) {
                loggedInIdStr = pref.getString("PREFS_LOGGEDIN_ID_KEY", null);
                isSubscribedDataStr = pref.getString("PREFS_LOGIN_ISSUBSCRIBED_KEY", null);
            }


            String urlRouteList = Util.rootUrl().trim() + Util.userValidationUrl.trim();
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(urlRouteList);
                httppost.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");
                httppost.addHeader("user_id", loggedInIdStr.trim());
                httppost.addHeader("authToken", Util.authTokenStr.trim());
                httppost.addHeader("movie_id", muviUniqueIdStr.trim());
                httppost.addHeader("purchase_type", "show");

                httppost.addHeader("season_id", "0");
                httppost.addHeader("country", countryPref.getString("countryCode", null));


                // Execute HTTP Post Request
                try {
                    HttpResponse response = httpclient.execute(httppost);
                    responseStr = EntityUtils.toString(response.getEntity());

                } catch (org.apache.http.conn.ConnectTimeoutException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (pDialog != null && pDialog.isShowing()) {
                                pDialog.hide();
                                pDialog = null;
                            }
                            status = 0;
                            Util.showToast(PPvPaymentInfoActivity.this,Util.getTextofLanguage(PPvPaymentInfoActivity.this,Util.SLOW_INTERNET_CONNECTION,Util.DEFAULT_SLOW_INTERNET_CONNECTION));

                           // Toast.makeText(PPvPaymentInfoActivity.this,Util.getTextofLanguage(PPvPaymentInfoActivity.this,Util.SLOW_INTERNET_CONNECTION,Util.DEFAULT_SLOW_INTERNET_CONNECTION), Toast.LENGTH_LONG).show();

                        }

                    });

                } catch (IOException e) {
                    if (pDialog != null && pDialog.isShowing()) {
                        pDialog.hide();
                        pDialog = null;
                    }
                    status = 0;
                    e.printStackTrace();
                }
                if (responseStr != null) {
                    JSONObject myJson = new JSONObject(responseStr);
                    if (myJson.has("code")) {
                        status = Integer.parseInt(myJson.optString("code"));
                    }
                    if (myJson.has("status")) {
                        validUserStr = myJson.optString("status");
                    }
                    if (myJson.has("msg")) {
                        userMessage = myJson.optString("msg");
                    }
                }

            } catch (Exception e) {
                if (pDialog != null && pDialog.isShowing()) {
                    pDialog.hide();
                    pDialog = null;
                }
                status = 0;

            }

            return null;
        }


        protected void onPostExecute(Void result) {
           /* try {
                if (pDialog.isShowing())
                    pDialog.dismiss();
            } catch (IllegalArgumentException ex) {
                status = 0;
            }*/
            if (responseStr == null) {
                try {
                    if (pDialog != null && pDialog.isShowing()) {
                        pDialog.hide();
                        pDialog = null;
                    }
                } catch (IllegalArgumentException ex) {
                    status = 0;
                }
                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(PPvPaymentInfoActivity.this,R.style.MyAlertDialogStyle);
                dlgAlert.setMessage(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.DETAILS_NOT_FOUND_ALERT, Util.DEFAULT_DETAILS_NOT_FOUND_ALERT));
                dlgAlert.setTitle(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.SORRY, Util.DEFAULT_SORRY));
                dlgAlert.setPositiveButton(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.BUTTON_OK, Util.DEFAULT_BUTTON_OK), null);
                dlgAlert.setCancelable(false);
                dlgAlert.setPositiveButton(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.BUTTON_OK, Util.DEFAULT_BUTTON_OK),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                hideKeyboard(PPvPaymentInfoActivity.this);
                                onBackPressed();

                            }
                        });
                dlgAlert.create().show();
            } else if (status <= 0) {
                try {
                    if (pDialog != null && pDialog.isShowing()) {
                        pDialog.hide();
                        pDialog = null;
                    }
                } catch (IllegalArgumentException ex) {
                    status = 0;
                }
                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(PPvPaymentInfoActivity.this,R.style.MyAlertDialogStyle);
                dlgAlert.setMessage(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.DETAILS_NOT_FOUND_ALERT, Util.DEFAULT_DETAILS_NOT_FOUND_ALERT));
                dlgAlert.setTitle(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.SORRY, Util.DEFAULT_SORRY));
                dlgAlert.setPositiveButton(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.BUTTON_OK, Util.DEFAULT_BUTTON_OK), null);
                dlgAlert.setCancelable(false);
                dlgAlert.setPositiveButton(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.BUTTON_OK, Util.DEFAULT_BUTTON_OK),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                hideKeyboard(PPvPaymentInfoActivity.this);
                                onBackPressed();
                            }
                        });
                dlgAlert.create().show();
            }
            if (status > 0) {
                if (status == 425) {
                    try {
                        if (pDialog != null && pDialog.isShowing()) {
                            pDialog.hide();
                            pDialog = null;
                        }
                    } catch (IllegalArgumentException ex) {
                        status = 0;
                    }
                    AlertDialog.Builder dlgAlert = new AlertDialog.Builder(PPvPaymentInfoActivity.this,R.style.MyAlertDialogStyle);
                    dlgAlert.setMessage(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.ACTIVATE_SUBSCRIPTION_WATCH_VIDEO, Util.DEFAULT_ACTIVATE_SUBSCRIPTION_WATCH_VIDEO));
                    dlgAlert.setTitle(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.SORRY, Util.DEFAULT_SORRY));
                    dlgAlert.setPositiveButton(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.BUTTON_OK, Util.DEFAULT_BUTTON_OK), null);
                    dlgAlert.setCancelable(false);
                    dlgAlert.setPositiveButton(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.BUTTON_OK, Util.DEFAULT_BUTTON_OK),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    hideKeyboard(PPvPaymentInfoActivity.this);
                                    onBackPressed();

                                }
                            });
                    dlgAlert.create().show();
                } else if (status == 426) {
                    try {
                        if (pDialog != null && pDialog.isShowing()) {
                            pDialog.hide();
                            pDialog = null;
                        }
                    } catch (IllegalArgumentException ex) {
                        status = 0;
                    }
                    AlertDialog.Builder dlgAlert = new AlertDialog.Builder(PPvPaymentInfoActivity.this,R.style.MyAlertDialogStyle);
                    dlgAlert.setMessage(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.ACTIVATE_SUBSCRIPTION_WATCH_VIDEO, Util.DEFAULT_ACTIVATE_SUBSCRIPTION_WATCH_VIDEO));
                    dlgAlert.setTitle(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.SORRY, Util.DEFAULT_SORRY));
                    dlgAlert.setPositiveButton(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.BUTTON_OK, Util.DEFAULT_BUTTON_OK), null);
                    dlgAlert.setCancelable(false);
                    dlgAlert.setPositiveButton(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.BUTTON_OK, Util.DEFAULT_BUTTON_OK),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    hideKeyboard(PPvPaymentInfoActivity.this);
                                    onBackPressed();
                                }
                            });
                    dlgAlert.create().show();
                } else if (status == 428) {
                    try {
                        if (pDialog != null && pDialog.isShowing()) {
                            pDialog.hide();
                            pDialog = null;
                        }
                    } catch (IllegalArgumentException ex) {
                        status = 0;
                    }
                    AlertDialog.Builder dlgAlert = new AlertDialog.Builder(PPvPaymentInfoActivity.this,R.style.MyAlertDialogStyle);
                    dlgAlert.setMessage(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.CROSSED_MAXIMUM_LIMIT, Util.DEFAULT_CROSSED_MAXIMUM_LIMIT));
                    dlgAlert.setTitle(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.SORRY, Util.DEFAULT_SORRY));
                    dlgAlert.setPositiveButton(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.BUTTON_OK, Util.DEFAULT_BUTTON_OK), null);
                    dlgAlert.setCancelable(false);
                    dlgAlert.setPositiveButton(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.BUTTON_OK, Util.DEFAULT_BUTTON_OK),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    hideKeyboard(PPvPaymentInfoActivity.this);
                                    onBackPressed();
                                }
                            });
                    dlgAlert.create().show();
                } else if (status == 430) {
                    try {
                        if (pDialog != null && pDialog.isShowing()) {
                            pDialog.hide();
                            pDialog = null;
                        }
                    } catch (IllegalArgumentException ex) {
                        status = 0;
                    }
                    AlertDialog.Builder dlgAlert = new AlertDialog.Builder(PPvPaymentInfoActivity.this,R.style.MyAlertDialogStyle);
                    dlgAlert.setMessage(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.ACTIVATE_SUBSCRIPTION_WATCH_VIDEO, Util.DEFAULT_ACTIVATE_SUBSCRIPTION_WATCH_VIDEO));
                    dlgAlert.setTitle(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.SORRY, Util.DEFAULT_SORRY));
                    dlgAlert.setPositiveButton(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.BUTTON_OK, Util.DEFAULT_BUTTON_OK), null);
                    dlgAlert.setCancelable(false);
                    dlgAlert.setPositiveButton(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.BUTTON_OK, Util.DEFAULT_BUTTON_OK),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    hideKeyboard(PPvPaymentInfoActivity.this);
                                    onBackPressed();
                                }
                            });
                    dlgAlert.create().show();
                } else if (status == 427) {
                    try {
                        if (pDialog != null && pDialog.isShowing()) {
                            pDialog.hide();
                            pDialog = null;
                        }
                    } catch (IllegalArgumentException ex) {
                        status = 0;
                    }
                    AlertDialog.Builder dlgAlert = new AlertDialog.Builder(PPvPaymentInfoActivity.this,R.style.MyAlertDialogStyle);
                    dlgAlert.setMessage(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.CONTENT_NOT_AVAILABLE_IN_YOUR_COUNTRY, Util.DEFAULT_CONTENT_NOT_AVAILABLE_IN_YOUR_COUNTRY));
                    dlgAlert.setTitle(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.SORRY, Util.DEFAULT_SORRY));
                    dlgAlert.setPositiveButton(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.BUTTON_OK, Util.DEFAULT_BUTTON_OK), null);
                    dlgAlert.setCancelable(false);
                    dlgAlert.setPositiveButton(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.BUTTON_OK, Util.DEFAULT_BUTTON_OK),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    hideKeyboard(PPvPaymentInfoActivity.this);
                                    onBackPressed();
                                }
                            });
                    dlgAlert.create().show();
                } else if (status == 429) {
                    if (validUserStr == null) {
                        try {
                            if (pDialog != null && pDialog.isShowing()) {
                                pDialog.hide();
                                pDialog = null;
                            }
                        } catch (IllegalArgumentException ex) {
                            status = 0;
                        }
                        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(PPvPaymentInfoActivity.this,R.style.MyAlertDialogStyle);
                        dlgAlert.setMessage(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.DETAILS_NOT_FOUND_ALERT, Util.DEFAULT_DETAILS_NOT_FOUND_ALERT));
                        dlgAlert.setTitle(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.SORRY, Util.DEFAULT_SORRY));
                        dlgAlert.setPositiveButton(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.BUTTON_OK, Util.DEFAULT_BUTTON_OK), null);
                        dlgAlert.setCancelable(false);
                        dlgAlert.setPositiveButton(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.BUTTON_OK, Util.DEFAULT_BUTTON_OK),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                        hideKeyboard(PPvPaymentInfoActivity.this);
                                        onBackPressed();
                                    }
                                });
                        dlgAlert.create().show();
                    }
                    if (validUserStr != null) {
                        boolean isNetwork = Util.checkNetwork(PPvPaymentInfoActivity.this);
                        if (isNetwork == false) {
                            try {
                                if (pDialog != null && pDialog.isShowing()) {
                                    pDialog.hide();
                                    pDialog = null;
                                }
                            } catch (IllegalArgumentException ex) {
                                status = 0;
                            }
                            AlertDialog.Builder dlgAlert = new AlertDialog.Builder(PPvPaymentInfoActivity.this,R.style.MyAlertDialogStyle);
                            dlgAlert.setMessage(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.NO_INTERNET_CONNECTION, Util.DEFAULT_NO_INTERNET_CONNECTION));
                            dlgAlert.setTitle(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.SORRY, Util.DEFAULT_SORRY));
                            dlgAlert.setPositiveButton(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.BUTTON_OK, Util.DEFAULT_BUTTON_OK), null);
                            dlgAlert.setCancelable(false);
                            dlgAlert.setPositiveButton(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.BUTTON_OK, Util.DEFAULT_BUTTON_OK),
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                            hideKeyboard(PPvPaymentInfoActivity.this);
                                            onBackPressed();
                                        }
                                    });
                            dlgAlert.create().show();

                        } else {
                            if ((validUserStr.trim().equalsIgnoreCase("OK")) || (validUserStr.trim().matches("OK")) || (validUserStr.trim().equals("OK"))) {
                                AsynLoadVideoUrls asynLoadVideoUrls = new AsynLoadVideoUrls();
                                asynLoadVideoUrls.executeOnExecutor(threadPoolExecutor);
                            } else {
                                if ((userMessage.trim().equalsIgnoreCase("Unpaid")) || (userMessage.trim().matches("Unpaid")) || (userMessage.trim().equals("Unpaid"))) {
                                    if (isNetwork == false) {
                                        try {
                                            if (pDialog != null && pDialog.isShowing()) {
                                                pDialog.hide();
                                                pDialog = null;
                                            }
                                        } catch (IllegalArgumentException ex) {
                                            status = 0;
                                        }
                                        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(PPvPaymentInfoActivity.this,R.style.MyAlertDialogStyle);
                                        dlgAlert.setMessage(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.NO_INTERNET_CONNECTION, Util.DEFAULT_NO_INTERNET_CONNECTION));
                                        dlgAlert.setTitle(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.SORRY, Util.DEFAULT_SORRY));
                                        dlgAlert.setPositiveButton(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.BUTTON_OK, Util.DEFAULT_BUTTON_OK), null);
                                        dlgAlert.setCancelable(false);
                                        dlgAlert.setPositiveButton(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.BUTTON_OK, Util.DEFAULT_BUTTON_OK),
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        dialog.cancel();
                                                        hideKeyboard(PPvPaymentInfoActivity.this);
                                                        onBackPressed();
                                                    }
                                                });
                                        dlgAlert.create().show();

                                    } else {
                                        try {
                                            if (pDialog != null && pDialog.isShowing()) {
                                                pDialog.hide();
                                                pDialog = null;
                                            }
                                        } catch (IllegalArgumentException ex) {
                                            status = 0;
                                        }
                                        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(PPvPaymentInfoActivity.this,R.style.MyAlertDialogStyle);
                                        dlgAlert.setMessage(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.ACTIVATE_SUBSCRIPTION_WATCH_VIDEO, Util.DEFAULT_ACTIVATE_SUBSCRIPTION_WATCH_VIDEO));
                                        dlgAlert.setTitle(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.SORRY, Util.DEFAULT_SORRY));
                                        dlgAlert.setPositiveButton(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.BUTTON_OK, Util.DEFAULT_BUTTON_OK), null);
                                        dlgAlert.setCancelable(false);
                                        dlgAlert.setPositiveButton(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.BUTTON_OK, Util.DEFAULT_BUTTON_OK),
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        dialog.cancel();
                                                        hideKeyboard(PPvPaymentInfoActivity.this);
                                                        onBackPressed();
                                                    }
                                                });
                                        dlgAlert.create().show();
                                    }
                                } else {
                                    try {
                                        if (pDialog != null && pDialog.isShowing()) {
                                            pDialog.hide();
                                            pDialog = null;
                                        }
                                    } catch (IllegalArgumentException ex) {
                                        status = 0;
                                    }
                                    AlertDialog.Builder dlgAlert = new AlertDialog.Builder(PPvPaymentInfoActivity.this,R.style.MyAlertDialogStyle);
                                    dlgAlert.setMessage(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.DETAILS_NOT_FOUND_ALERT, Util.DEFAULT_DETAILS_NOT_FOUND_ALERT));
                                    dlgAlert.setTitle(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.SORRY, Util.DEFAULT_SORRY));
                                    dlgAlert.setPositiveButton(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.BUTTON_OK, Util.DEFAULT_BUTTON_OK), null);
                                    dlgAlert.setCancelable(false);
                                    dlgAlert.setPositiveButton(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.BUTTON_OK, Util.DEFAULT_BUTTON_OK),
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    dialog.cancel();
                                                    hideKeyboard(PPvPaymentInfoActivity.this);
                                                    onBackPressed();
                                                }
                                            });
                                    dlgAlert.create().show();
                                }
                            }
                        }
                    }

                }
            }

        }

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressBarHandler(PPvPaymentInfoActivity.this);
            pDialog.show();

        }


    }

    private class AsynPaymentInfoDetails extends AsyncTask<Void, Void, Void> {
        //ProgressDialog pDialog;
        int status;
        String responseStr;
        String responseMessageStr;
        String emailIdStr = loginPref.getString("PREFS_LOGIN_EMAIL_ID_KEY", null);

        String nameOnCardStr = nameOnCardEditText.getText().toString().trim();
        String cardNumberStr = cardNumberEditText.getText().toString().trim();
        String securityCodeStr = securityCodeEditText.getText().toString().trim();

        @Override
        protected Void doInBackground(Void... params) {
            String urlRouteList = Util.rootUrl().trim() + Util.authenticatedCardValidationUrl.trim();
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(urlRouteList);
                httppost.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");
                httppost.addHeader("nameOnCard", nameOnCardStr);
                httppost.addHeader("expiryMonth", String.valueOf(expiryMonthStr).trim());
                httppost.addHeader("expiryYear", String.valueOf(expiryYearStr).trim());
                httppost.addHeader("cardNumber", cardNumberStr);
                httppost.addHeader("cvv", securityCodeStr);
                httppost.addHeader("email", emailIdStr);
                httppost.addHeader("authToken", Util.authTokenStr.trim());

                // Execute HTTP Post Request
                try {
                    HttpResponse response = httpclient.execute(httppost);
                    responseStr = EntityUtils.toString(response.getEntity());

                } catch (org.apache.http.conn.ConnectTimeoutException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (pDialog != null && pDialog.isShowing()) {
                                pDialog.hide();
                                pDialog = null;

                            }
                            status = 0;
                            Util.showToast(PPvPaymentInfoActivity.this,Util.getTextofLanguage(PPvPaymentInfoActivity.this,Util.NO_INTERNET_CONNECTION,Util.DEFAULT_NO_INTERNET_CONNECTION));

                         //   Toast.makeText(PPvPaymentInfoActivity.this, Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.NO_INTERNET_CONNECTION, Util.DEFAULT_NO_INTERNET_CONNECTION), Toast.LENGTH_LONG).show();

                        }

                    });

                } catch (IOException e) {
                    if (pDialog != null && pDialog.isShowing()) {
                        pDialog.hide();
                        pDialog = null;

                    }
                    status = 0;

                    e.printStackTrace();
                }
                JSONObject myJson = null;

                if (responseStr != null && responseStr!= "null" && !responseStr.equalsIgnoreCase("null")) {
                    myJson = new JSONObject(responseStr);
                    status = Integer.parseInt(myJson.optString("isSuccess"));
                    if (status == 1) {JSONObject mainJson = null;

                        if (myJson.has("card")) {
                            mainJson = myJson.getJSONObject("card");
                            if (mainJson.has("status") && mainJson.getString("status").trim() != null && !mainJson.getString("status").trim().isEmpty() && !mainJson.getString("status").trim().equals("null") && !mainJson.getString("status").trim().matches("")) {
                                statusStr = mainJson.getString("status");
                            } else {
                                statusStr = "";

                            }

                            if (mainJson.has("token") && mainJson.getString("token").trim() != null && !mainJson.getString("token").trim().isEmpty() && !mainJson.getString("token").trim().equals("null") && !mainJson.getString("token").trim().matches("")) {
                                tokenStr = mainJson.getString("token");
                            } else {
                                tokenStr = "";

                            }

                            if (mainJson.has("response_text") && mainJson.getString("response_text").trim() != null && !mainJson.getString("response_text").trim().isEmpty() && !mainJson.getString("response_text").trim().equals("null") && !mainJson.getString("response_text").trim().matches("")) {
                                responseText = mainJson.getString("response_text");
                            } else {
                                responseText = "";

                            }

                            if (mainJson.has("profile_id") && mainJson.getString("profile_id").trim() != null && !mainJson.getString("profile_id").trim().isEmpty() && !mainJson.getString("profile_id").trim().equals("null") && !mainJson.getString("profile_id").trim().matches("")) {
                                profileIdStr = mainJson.getString("profile_id");
                            } else {
                                profileIdStr = "";

                            }

                            if (mainJson.has("card_last_fourdigit") && mainJson.getString("card_last_fourdigit").trim() != null && !mainJson.getString("card_last_fourdigit").trim().isEmpty() && !mainJson.getString("card_last_fourdigit").trim().equals("null") && !mainJson.getString("card_last_fourdigit").trim().matches("")) {
                                cardLastFourDigitStr = mainJson.getString("card_last_fourdigit");
                            } else {
                                cardLastFourDigitStr = "";

                            }

                            if (mainJson.has("card_type") && mainJson.getString("card_type").trim() != null && !mainJson.getString("card_type").trim().isEmpty() && !mainJson.getString("card_type").trim().equals("null") && !mainJson.getString("card_type").trim().matches("")) {
                                cardTypeStr = mainJson.getString("card_type");
                            } else {
                                cardTypeStr = "";

                            }
                        }



                    }
                    if (status == 0) {
                        if (myJson.has("Message")) {
                            responseMessageStr = myJson.optString("Message");
                        }
                        if (((responseMessageStr.equalsIgnoreCase("null")) || (responseMessageStr.length() <= 0))) {
                            responseMessageStr = Util.getTextofLanguage(PPvPaymentInfoActivity.this,Util.NO_DETAILS_AVAILABLE,Util.DEFAULT_NO_DETAILS_AVAILABLE);

                        }
                    }
                }else{
                    status = 0;
                }


            } catch (Exception e) {
                if (pDialog != null && pDialog.isShowing()) {
                    pDialog.hide();
                    pDialog = null;

                }
                status = 0;

            }

            return null;
        }


        protected void onPostExecute(Void result) {
           /* try {
                if (pDialog.isShowing())
                    pDialog.dismiss();
            } catch (IllegalArgumentException ex) {
                status = 0;
            }*/
           /* if (responseStr == null) {
                try {
                    if (videoPDialog!=null && videoPDialog.isShowing())
                        videoPDialog.hide();
                } catch (IllegalArgumentException ex) {
                    status = 0;
                }
                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(PPvPaymentInfoActivity.this,R.style.MyAlertDialogStyle);
                dlgAlert.setMessage(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.ERROR_IN_PAYMENT_VALIDATION, Util.DEFAULT_ERROR_IN_PAYMENT_VALIDATION));
                dlgAlert.setTitle(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.SORRY, Util.DEFAULT_SORRY));
                dlgAlert.setPositiveButton(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.BUTTON_OK, Util.DEFAULT_BUTTON_OK), null);
                dlgAlert.setCancelable(false);
                dlgAlert.setPositiveButton(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.BUTTON_OK, Util.DEFAULT_BUTTON_OK),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();

                            }
                        });
                dlgAlert.create().show();
            }*/
            if (status == 0) {
                try {
                    if (videoPDialog.isShowing())
                        videoPDialog.hide();
                } catch (IllegalArgumentException ex) {
                    status = 0;
                }
                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(PPvPaymentInfoActivity.this,R.style.MyAlertDialogStyle);
                if (responseMessageStr!=null && !responseMessageStr.equalsIgnoreCase("")){
                    dlgAlert.setMessage(responseMessageStr);

                }else{
                    dlgAlert.setMessage(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.ERROR_IN_PAYMENT_VALIDATION, Util.DEFAULT_ERROR_IN_PAYMENT_VALIDATION));

                }
                dlgAlert.setTitle(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.FAILURE, Util.DEFAULT_FAILURE));
                dlgAlert.setPositiveButton(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.BUTTON_OK, Util.DEFAULT_BUTTON_OK), null);
                dlgAlert.setCancelable(false);
                dlgAlert.setPositiveButton(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.BUTTON_OK, Util.DEFAULT_BUTTON_OK),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();

                            }
                        });
                dlgAlert.create().show();
            } else if (status == 1) {
                boolean isNetwork = Util.checkNetwork(PPvPaymentInfoActivity.this);
                if (isNetwork == false) {
                    try {
                        if (videoPDialog.isShowing())
                            videoPDialog.hide();
                    } catch (IllegalArgumentException ex) {
                        status = 0;
                    }
                    AlertDialog.Builder dlgAlert = new AlertDialog.Builder(PPvPaymentInfoActivity.this,R.style.MyAlertDialogStyle);
                    dlgAlert.setMessage(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.NO_INTERNET_CONNECTION, Util.DEFAULT_NO_INTERNET_CONNECTION));
                    dlgAlert.setTitle(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.SORRY, Util.DEFAULT_SORRY));
                    dlgAlert.setPositiveButton(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.BUTTON_OK, Util.DEFAULT_BUTTON_OK), null);
                    dlgAlert.setCancelable(false);
                    dlgAlert.setPositiveButton(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.BUTTON_OK, Util.DEFAULT_BUTTON_OK),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();

                                }
                            });
                    dlgAlert.create().show();

                } else {
                    AsynSubscriptionRegDetails asyncSubsrInfo = new AsynSubscriptionRegDetails();
                    asyncSubsrInfo.executeOnExecutor(threadPoolExecutor);
                }
            }
        }

        @Override
        protected void onPreExecute() {
            videoPDialog = new ProgressBarHandler(PPvPaymentInfoActivity.this);
            videoPDialog.show();
           /* pDialog = new ProgressDialog(PPvPaymentInfoActivity.this);
            pDialog.setMessage(getResources().getString(R.string.loading_str));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();*/
        }


    }

    private class AsynWithouPaymentSubscriptionRegDetails extends AsyncTask<Void, Void, Void> {
        ProgressBarHandler pDialog;
        int status;
        String responseStr;

        @Override
        protected Void doInBackground(Void... params) {

            String userIdStr = loginPref.getString("PREFS_LOGGEDIN_ID_KEY", null);
            String emailIdSubStr = loginPref.getString("PREFS_LOGIN_EMAIL_ID_KEY", null);

         /*   runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(saveCardCheckbox.isChecked()){
                        isCheckedToSavetheCard = "1";
                    }else{
                        isCheckedToSavetheCard = "0";

                    }

                }

            });*/


            String urlRouteList = Util.rootUrl().trim() + Util.addSubscriptionUrl.trim();
            try {
                HttpClient httpclient = new DefaultHttpClient();
                final HttpPost httppost = new HttpPost(urlRouteList);
                httppost.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");
                httppost.addHeader("authToken", Util.authTokenStr.trim());
                if (isAPV == 1) {
                    httppost.addHeader("is_advance", "1");
                }

                httppost.addHeader("card_name", "");
                httppost.addHeader("exp_month", "");
                httppost.addHeader("card_number", "");
                httppost.addHeader("exp_year", "");
                httppost.addHeader("email", emailIdSubStr.trim());
                httppost.addHeader("movie_id", muviUniqueIdStr.trim());
                //httppost.addHeader("movie_id","5a07372fd347136975e3dd4c9897cf23");
                httppost.addHeader("user_id", userIdStr.trim());
                if (isCouponCodeAdded == true) {
                    httppost.addHeader("coupon_code", validCouponCode);
                } else {
                    httppost.addHeader("coupon_code", "");
                }
                httppost.addHeader("card_type", "");
                httppost.addHeader("card_last_fourdigit", "");
                httppost.addHeader("profile_id", "");
                httppost.addHeader("token", "");
                httppost.addHeader("cvv", "");
                // httppost.addHeader("country","US");

                httppost.addHeader("country", countryPref.getString("countryCode", null));
                //*********************************//

//                httppost.addHeader("season_id", "0");
//                httppost.addHeader("episode_id", "0");
                httppost.addHeader("season_id",Util.selected_season_id);
                httppost.addHeader("episode_id",Util.selected_episode_id);


                Log.v("SUBHA","season_id====================="+Util.selected_season_id);
                Log.v("SUBHA","episode_id====================="+Util.selected_episode_id);

                httppost.addHeader("currency_id", currencyIdStr.trim());

                httppost.addHeader("is_save_this_card", isCheckedToSavetheCard.trim());
                if (existing_card_id != null && !existing_card_id.matches("") && !existing_card_id.equalsIgnoreCase("")) {
                    httppost.addHeader("existing_card_id", existing_card_id);
                } else {
                    httppost.addHeader("existing_card_id", "");
                }

              /*  runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (creditCardSaveSpinner!=null && cardSavedArray!=null && cardSavedArray.length > 0 && creditCardSaveSpinner.getSelectedItemPosition() > 0){
                            String  existing_card_id = cardSavedArray[creditCardSaveSpinner.getSelectedItemPosition()].getCardId();
                            httppost.addHeader("existing_card_id", existing_card_id);

                        }

                    }

                });

*/

                // Execute HTTP Post Request
                try {
                    HttpResponse response = httpclient.execute(httppost);
                    responseStr = EntityUtils.toString(response.getEntity());

                } catch (org.apache.http.conn.ConnectTimeoutException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if (pDialog != null && pDialog.isShowing()) {
                                pDialog.hide();
                                pDialog = null;
                            }
                            status = 0;
                            Util.showToast(PPvPaymentInfoActivity.this,Util.getTextofLanguage(PPvPaymentInfoActivity.this,Util.NO_INTERNET_CONNECTION,Util.DEFAULT_NO_INTERNET_CONNECTION));

                          //  Toast.makeText(PPvPaymentInfoActivity.this, Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.NO_INTERNET_CONNECTION, Util.DEFAULT_NO_INTERNET_CONNECTION), Toast.LENGTH_LONG).show();

                        }

                    });

                } catch (IOException e) {
                    try {
                        if (pDialog != null && pDialog.isShowing()) {
                            pDialog.hide();
                            pDialog = null;
                        }
                    } catch (IllegalArgumentException ex) {
                        status = 0;

                        e.printStackTrace();
                    }
                }
                if (responseStr != null) {
                    JSONObject myJson = new JSONObject(responseStr);
                    status = Integer.parseInt(myJson.optString("code"));

                }

            } catch (Exception e) {
                try {
                    if (pDialog != null && pDialog.isShowing()) {
                        pDialog.hide();
                        pDialog = null;
                    }
                } catch (IllegalArgumentException ex) {
                    status = 0;
                }

            }

            return null;
        }


        protected void onPostExecute(Void result) {
            try {
                if (videoPDialog != null && videoPDialog.isShowing()) {
                    videoPDialog.hide();
                    videoPDialog = null;
                }
            } catch (IllegalArgumentException ex) {
                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(PPvPaymentInfoActivity.this,R.style.MyAlertDialogStyle);
                dlgAlert.setMessage(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.ERROR_IN_SUBSCRIPTION, Util.DEFAULT_ERROR_IN_SUBSCRIPTION));
                dlgAlert.setTitle(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.SORRY, Util.DEFAULT_SORRY));
                dlgAlert.setPositiveButton(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.BUTTON_OK, Util.DEFAULT_BUTTON_OK), null);
                dlgAlert.setCancelable(false);
                dlgAlert.setPositiveButton(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.BUTTON_OK, Util.DEFAULT_BUTTON_OK),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();

                            }
                        });
                dlgAlert.create().show();
            }
            if (responseStr == null) {
                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(PPvPaymentInfoActivity.this,R.style.MyAlertDialogStyle);
                dlgAlert.setMessage(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.ERROR_IN_SUBSCRIPTION, Util.DEFAULT_ERROR_IN_SUBSCRIPTION));
                dlgAlert.setTitle(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.SORRY, Util.DEFAULT_SORRY));
                dlgAlert.setPositiveButton(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.BUTTON_OK, Util.DEFAULT_BUTTON_OK), null);
                dlgAlert.setCancelable(false);
                dlgAlert.setPositiveButton(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.BUTTON_OK, Util.DEFAULT_BUTTON_OK),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();

                            }
                        });
                dlgAlert.create().show();
            }
            if (status == 0) {
                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(PPvPaymentInfoActivity.this,R.style.MyAlertDialogStyle);
                dlgAlert.setMessage(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.ERROR_IN_SUBSCRIPTION, Util.DEFAULT_ERROR_IN_SUBSCRIPTION));
                dlgAlert.setTitle(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.FAILURE, Util.DEFAULT_FAILURE));
                dlgAlert.setPositiveButton(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.BUTTON_OK, Util.DEFAULT_BUTTON_OK), null);
                dlgAlert.setCancelable(false);
                dlgAlert.setPositiveButton(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.BUTTON_OK, Util.DEFAULT_BUTTON_OK),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();

                            }
                        });
                dlgAlert.create().show();
            }
            if (status > 0) {

                if (status == 200) {

                    runOnUiThread(new Runnable() {
                        public void run() {
                            boolean isNetwork = Util.checkNetwork(PPvPaymentInfoActivity.this);
                            if (isNetwork == false) {
                                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(PPvPaymentInfoActivity.this,R.style.MyAlertDialogStyle);
                                dlgAlert.setMessage(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.NO_INTERNET_CONNECTION, Util.DEFAULT_NO_INTERNET_CONNECTION));
                                dlgAlert.setTitle(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.SORRY, Util.DEFAULT_SORRY));
                                dlgAlert.setPositiveButton(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.BUTTON_OK, Util.DEFAULT_BUTTON_OK), null);
                                dlgAlert.setCancelable(false);
                                dlgAlert.setPositiveButton(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.BUTTON_OK, Util.DEFAULT_BUTTON_OK),
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();

                                            }
                                        });
                                dlgAlert.create().show();

                            } else {
                                if (isAPV == 1) {
                                    Util.showToast(PPvPaymentInfoActivity.this,Util.getTextofLanguage(PPvPaymentInfoActivity.this,Util.PURCHASE_SUCCESS_ALERT,Util.DEFAULT_PURCHASE_SUCCESS_ALERT));

                                   // Toast.makeText(PPvPaymentInfoActivity.this,Util.getTextofLanguage(PPvPaymentInfoActivity.this,Util.PURCHASE_SUCCESS_ALERT,Util.DEFAULT_PURCHASE_SUCCESS_ALERT), Toast.LENGTH_LONG).show();
                                    finish();
                                    overridePendingTransition(0, 0);
                                } else {
                                    if (isCastConnected == true) {
                                        onBackPressed();

                                    } else {
                                        AsynLoadVideoUrls asynLoadVideoUrls = new AsynLoadVideoUrls();
                                        asynLoadVideoUrls.executeOnExecutor(threadPoolExecutor);
                                    }
                                }
                               /* final Intent playVideoIntent = new Intent(PPvPaymentInfoActivity.this, PlayVideoActivity.class);
                                playVideoIntent.putExtra("activity", "generic");
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        playVideoIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                        playVideoIntent.putExtra("url", videoUrlStr.trim());
                                        startActivity(playVideoIntent);
                                        finish();
                                    }
                                });*/
                            }
                        }
                    });


                } else {
                    AlertDialog.Builder dlgAlert = new AlertDialog.Builder(PPvPaymentInfoActivity.this,R.style.MyAlertDialogStyle);
                    dlgAlert.setMessage(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.ERROR_IN_SUBSCRIPTION, Util.DEFAULT_ERROR_IN_SUBSCRIPTION));
                    dlgAlert.setTitle(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.SORRY, Util.DEFAULT_SORRY));
                    dlgAlert.setPositiveButton(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.BUTTON_OK, Util.DEFAULT_BUTTON_OK), null);
                    dlgAlert.setCancelable(false);
                    dlgAlert.setPositiveButton(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.BUTTON_OK, Util.DEFAULT_BUTTON_OK),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();

                                }
                            });
                    dlgAlert.create().show();
                }
            }

        }

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressBarHandler(PPvPaymentInfoActivity.this);
            pDialog.show();

        }


    }

    private class AsynSubscriptionRegDetails extends AsyncTask<Void, Void, Void> {
        // ProgressDialog pDialog;
        int status;
        String responseStr;
        String nameOnCardStr = nameOnCardEditText.getText().toString().trim();
        String cardNumberStr = cardNumberEditText.getText().toString().trim();
        String securityCardStr = securityCodeEditText.getText().toString().trim();

        @Override
        protected Void doInBackground(Void... params) {

            String userIdStr = loginPref.getString("PREFS_LOGGEDIN_ID_KEY", null);
            String emailIdSubStr = loginPref.getString("PREFS_LOGIN_EMAIL_ID_KEY", null);
           /* runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(saveCardCheckbox.isChecked()){
                        isCheckedToSavetheCard = "1";
                        Toast.makeText(PPvPaymentInfoActivity.this,"Data saved",Toast.LENGTH_SHORT).show();

                    }else{
                        isCheckedToSavetheCard = "0";
                        Toast.makeText(PPvPaymentInfoActivity.this,"Data Not saved",Toast.LENGTH_SHORT).show();


                    }

                }

            });*/
            String urlRouteList = Util.rootUrl().trim() + Util.addSubscriptionUrl.trim();
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(urlRouteList);
                httppost.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");
                httppost.addHeader("authToken", Util.authTokenStr.trim());

                httppost.addHeader("card_name", nameOnCardStr);
                httppost.addHeader("exp_month", String.valueOf(expiryMonthStr).trim());
                httppost.addHeader("card_number", cardNumberStr);
                httppost.addHeader("exp_year", String.valueOf(expiryYearStr).trim());
                httppost.addHeader("email", emailIdSubStr.trim());
                httppost.addHeader("movie_id", muviUniqueIdStr.trim());
                httppost.addHeader("user_id", userIdStr.trim());


                if (isCouponCodeAdded == true) {

                    httppost.addHeader("coupon_code", validCouponCode);
                } else {

                    httppost.addHeader("coupon_code", "");
                }

                httppost.addHeader("card_type", cardTypeStr.trim());
                httppost.addHeader("card_last_fourdigit", cardLastFourDigitStr.trim());
                httppost.addHeader("profile_id", profileIdStr.trim());
                httppost.addHeader("token", tokenStr.trim());
                httppost.addHeader("cvv", securityCardStr);
                // httppost.addHeader("country",currencyCountryCodeStr.trim());

                httppost.addHeader("country", countryPref.getString("countryCode", null));
                //*********************************// ((Global) getApplicationContext()).getCountryCode()
//                httppost.addHeader("season_id", "0");
//                httppost.addHeader("episode_id", "0");
                httppost.addHeader("season_id",Util.selected_season_id);
                httppost.addHeader("episode_id",Util.selected_episode_id);





                if (isAPV == 1) {
                    httppost.addHeader("is_advance", "1");
                }
                httppost.addHeader("currency_id", currencyIdStr.trim());

                httppost.addHeader("is_save_this_card", isCheckedToSavetheCard.trim());


                // Execute HTTP Post Request
                try {
                    HttpResponse response = httpclient.execute(httppost);
                    responseStr = EntityUtils.toString(response.getEntity());



                } catch (org.apache.http.conn.ConnectTimeoutException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            status = 0;
                            if (videoPDialog != null && videoPDialog.isShowing()) {
                                videoPDialog.hide();
                                videoPDialog = null;
                            }
                            Util.showToast(PPvPaymentInfoActivity.this,Util.getTextofLanguage(PPvPaymentInfoActivity.this,Util.NO_INTERNET_CONNECTION,Util.DEFAULT_NO_INTERNET_CONNECTION));

                           // Toast.makeText(PPvPaymentInfoActivity.this, Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.NO_INTERNET_CONNECTION, Util.DEFAULT_NO_INTERNET_CONNECTION), Toast.LENGTH_LONG).show();

                        }

                    });

                } catch (IOException e) {

                    status = 0;
                    if (videoPDialog != null && videoPDialog.isShowing()) {
                        videoPDialog.hide();
                        videoPDialog = null;
                    }
                    e.printStackTrace();
                }
                if (responseStr != null) {
                    JSONObject myJson = new JSONObject(responseStr);
                    status = Integer.parseInt(myJson.optString("code"));

                }

            } catch (Exception e) {

                if (videoPDialog != null && videoPDialog.isShowing()) {
                    videoPDialog.hide();
                    videoPDialog = null;
                }
                status = 0;

            }

            return null;
        }


        protected void onPostExecute(Void result) {
           /* try {
                if (pDialog.isShowing())
                    pDialog.dismiss();
            } catch (IllegalArgumentException ex) {
               status = 0;
            }*/
            if (responseStr == null) {
                try {
                    if (videoPDialog != null && videoPDialog.isShowing()) {
                        videoPDialog.hide();
                        videoPDialog = null;
                    }
                } catch (IllegalArgumentException ex) {
                    status = 0;
                }
                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(PPvPaymentInfoActivity.this,R.style.MyAlertDialogStyle);
                dlgAlert.setMessage(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.ERROR_IN_SUBSCRIPTION, Util.DEFAULT_ERROR_IN_SUBSCRIPTION));
                dlgAlert.setTitle(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.SORRY, Util.DEFAULT_SORRY));
                dlgAlert.setPositiveButton(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.BUTTON_OK, Util.DEFAULT_BUTTON_OK), null);
                dlgAlert.setCancelable(false);
                dlgAlert.setPositiveButton(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.BUTTON_OK, Util.DEFAULT_BUTTON_OK),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();

                            }
                        });
                dlgAlert.create().show();
            }
            if (status == 0) {
                try {
                    if (videoPDialog != null && videoPDialog.isShowing()) {
                        videoPDialog.hide();
                        videoPDialog = null;
                    }
                } catch (IllegalArgumentException ex) {
                    status = 0;
                }
                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(PPvPaymentInfoActivity.this,R.style.MyAlertDialogStyle);
                dlgAlert.setMessage(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.ERROR_IN_SUBSCRIPTION, Util.DEFAULT_ERROR_IN_SUBSCRIPTION));
                dlgAlert.setTitle(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.SORRY, Util.DEFAULT_SORRY));
                dlgAlert.setPositiveButton(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.BUTTON_OK, Util.DEFAULT_BUTTON_OK), null);
                dlgAlert.setCancelable(false);
                dlgAlert.setPositiveButton(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.BUTTON_OK, Util.DEFAULT_BUTTON_OK),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();

                            }
                        });
                dlgAlert.create().show();
            }
            if (status > 0) {

                if (status == 200) {

                    runOnUiThread(new Runnable() {
                        public void run() {
                            boolean isNetwork = Util.checkNetwork(PPvPaymentInfoActivity.this);
                            if (isNetwork == false) {
                                try {
                                    if (videoPDialog != null && videoPDialog.isShowing()) {
                                        videoPDialog.hide();
                                        videoPDialog = null;
                                    }
                                } catch (IllegalArgumentException ex) {
                                    status = 0;
                                }
                                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(PPvPaymentInfoActivity.this,R.style.MyAlertDialogStyle);
                                dlgAlert.setMessage(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.NO_INTERNET_CONNECTION, Util.DEFAULT_NO_INTERNET_CONNECTION));
                                dlgAlert.setTitle(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.SORRY, Util.DEFAULT_SORRY));
                                dlgAlert.setPositiveButton(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.BUTTON_OK, Util.DEFAULT_BUTTON_OK), null);
                                dlgAlert.setCancelable(false);
                                dlgAlert.setPositiveButton(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.BUTTON_OK, Util.DEFAULT_BUTTON_OK),
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();

                                            }
                                        });
                                dlgAlert.create().show();

                            } else {
                                try {
                                    if (videoPDialog != null && videoPDialog.isShowing()) {
                                        videoPDialog.hide();
                                        videoPDialog = null;
                                    }
                                } catch (IllegalArgumentException ex) {
                                    status = 0;
                                }
                                if (isAPV == 1) {
                                    Util.showToast(PPvPaymentInfoActivity.this,Util.getTextofLanguage(PPvPaymentInfoActivity.this,Util.PURCHASE_SUCCESS_ALERT,Util.DEFAULT_PURCHASE_SUCCESS_ALERT));

                                   // Toast.makeText(PPvPaymentInfoActivity.this,Util.getTextofLanguage(PPvPaymentInfoActivity.this,Util.PURCHASE_SUCCESS_ALERT,Util.DEFAULT_PURCHASE_SUCCESS_ALERT), Toast.LENGTH_LONG).show();
                                    onBackPressed();
                                } else {
                                    if (isCastConnected == true) {
                                        onBackPressed();

                                    } else {
                                        AsynLoadVideoUrls asynLoadVideoUrls = new AsynLoadVideoUrls();
                                        asynLoadVideoUrls.executeOnExecutor(threadPoolExecutor);
                                    }
                                }
                            }

                        }
                    });


                } else {
                    try {
                        if (videoPDialog != null && videoPDialog.isShowing()) {
                            videoPDialog.hide();
                            videoPDialog = null;
                        }
                    } catch (IllegalArgumentException ex) {
                        status = 0;
                    }
                    AlertDialog.Builder dlgAlert = new AlertDialog.Builder(PPvPaymentInfoActivity.this,R.style.MyAlertDialogStyle);
                    dlgAlert.setMessage(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.ERROR_IN_SUBSCRIPTION, Util.DEFAULT_ERROR_IN_SUBSCRIPTION));
                    dlgAlert.setTitle(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.SORRY, Util.DEFAULT_SORRY));
                    dlgAlert.setPositiveButton(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.BUTTON_OK, Util.DEFAULT_BUTTON_OK), null);
                    dlgAlert.setCancelable(false);
                    dlgAlert.setPositiveButton(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.BUTTON_OK, Util.DEFAULT_BUTTON_OK),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();

                                }
                            });
                    dlgAlert.create().show();
                }
            }

        }

        @Override
        protected void onPreExecute() {
            videoPDialog = new ProgressBarHandler(PPvPaymentInfoActivity.this);
            videoPDialog.show();
           /* pDialog = new ProgressDialog(PPvPaymentInfoActivity.this);
            pDialog.setMessage(getResources().getString(R.string.loading_str));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();*/
        }


    }

    @Override
    protected void onResume() {
        super.onResume();


        // **************chromecast*********************//
        if (mCastSession == null) {
            mCastSession = CastContext.getSharedInstance(this).getSessionManager()
                    .getCurrentCastSession();
        }



        mCastContext.getSessionManager().addSessionManagerListener(
                mSessionManagerListener, CastSession.class);

        //**************chromecast*********************//

    }



    /***********Subtitle********/

    public void Download_SubTitle(String Url) {
        new DownloadFileFromURL().execute(Url);
    }


    class DownloadFileFromURL extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        protected String doInBackground(String... f_url) {
            int count;


            try {
                URL url = new URL(f_url[0]);
                String str = f_url[0];
                filename = str.substring(str.lastIndexOf("/") + 1);
                URLConnection conection = url.openConnection();
                conection.connect();
                int lenghtOfFile = conection.getContentLength();

                // download the file
                InputStream input = new BufferedInputStream(url.openStream(), 8192);
                File root = Environment.getExternalStorageDirectory();
                mediaStorageDir = new File(root + "/Android/data/" + getApplicationContext().getPackageName().trim() + "/SubTitleList/", "");

                if (!mediaStorageDir.exists()) {
                    if (!mediaStorageDir.mkdirs()) {
                        Log.d("App", "failed to create directory");
                    }
                }

                SubTitlePath.add(mediaStorageDir.getAbsolutePath() + "/" + System.currentTimeMillis()+".vtt");
                OutputStream output = new FileOutputStream(mediaStorageDir.getAbsolutePath() + "/" + System.currentTimeMillis()+".vtt");

                byte data[] = new byte[1024];
                long total = 0;
                while ((count = input.read(data)) != -1) {
                    total += count;
                    publishProgress("" + (int) ((total * 100) / lenghtOfFile));
                    output.write(data, 0, count);
                }
                output.flush();
                output.close();
                input.close();

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }

            return null;
        }
        protected void onProgressUpdate(String... progress) {
        }

        @Override
        protected void onPostExecute(String file_url) {
            FakeSubTitlePath.remove(0);
            if(FakeSubTitlePath.size()>0)
            {
                Download_SubTitle(FakeSubTitlePath.get(0).trim());
            }
            else
            {
                if(progressBarHandler!=null && progressBarHandler.isShowing())
                {
                    progressBarHandler.hide();
                }
                Intent playVideoIntent = new Intent(PPvPaymentInfoActivity.this, ExoPlayerActivity.class);
                playVideoIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                playVideoIntent.putExtra("SubTitleName", SubTitleName);
                playVideoIntent.putExtra("SubTitlePath", SubTitlePath);
                playVideoIntent.putExtra("ResolutionFormat",ResolutionFormat);
                playVideoIntent.putExtra("ResolutionUrl",ResolutionUrl);
                startActivity(playVideoIntent);
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                finish();
            }
        }
    }


    private class AsynGetMoniTization extends AsyncTask<Void, Void, Void> {
        ProgressBarHandler pDialog;

        int status;
        String validUserStr;
        String userMessage;
        String responseStr;
        String loggedInIdStr;

        @Override
        protected Void doInBackground(Void... params) {

            if (loginPref != null) {
                loggedInIdStr = loginPref.getString("PREFS_LOGGEDIN_ID_KEY", null);
            }

            try {

                String urlRouteList = Util.rootUrl().trim()+Util.GetMonetizationDetails.trim();
                HttpClient httpclient=new DefaultHttpClient();
                HttpPost httppost = new HttpPost(urlRouteList);
                httppost.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");
                httppost.addHeader("authToken", Util.authTokenStr.trim());
                httppost.addHeader("user_id", loggedInIdStr.trim());
                httppost.addHeader("movie_id", Util.dataModel.getMovieUniqueId().trim());
                httppost.addHeader("stream_id", Util.dataModel.getStreamUniqueId());

                if(Util.dataModel.getContentTypesId() == 3)
                {
                    httppost.addHeader("purchase_type", "episode");
                }
                else
                {
                    httppost.addHeader("purchase_type", "show");
                }





                // Execute HTTP Post Request
                try {
                    HttpResponse response = httpclient.execute(httppost);
                    responseStr = EntityUtils.toString(response.getEntity());

                    Log.v("BIBHU" , "Response Of tGetMonetizationDetails = "+responseStr);


                } catch (final org.apache.http.conn.ConnectTimeoutException e){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (pDialog != null && pDialog.isShowing()) {
                                pDialog.hide();
                                pDialog = null;
                            }
                            status = 0;
                            Toast.makeText(PPvPaymentInfoActivity.this, Util.getTextofLanguage(PPvPaymentInfoActivity.this,Util.SLOW_INTERNET_CONNECTION,Util.DEFAULT_SLOW_INTERNET_CONNECTION), Toast.LENGTH_LONG).show();
                        }
                    });

                } catch (IOException e) {
                    if (pDialog != null && pDialog.isShowing()) {
                        pDialog.hide();
                        pDialog = null;
                    }
                    status = 0;

                    e.printStackTrace();
                }
                if(responseStr!=null){
                    JSONObject myJson = new JSONObject(responseStr);
                    status = Integer.parseInt(myJson.optString("code"));
                }
                else
                {
                    status = 0;
                }
            }
            catch (Exception e) {
                if (pDialog != null && pDialog.isShowing()) {
                    pDialog.hide();
                    pDialog = null;
                }
                status = 0;
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
                status = 0;
            }


            if(status == 200)
            {

                try {
                    JSONObject jsonObject = new JSONObject(responseStr);
                    JSONObject jsonObject1 = jsonObject.optJSONObject("monetization_plans");

                    if(!(jsonObject1.optString("voucher").equals("")) && !(jsonObject1.optString("voucher").equals("null"))
                            && (jsonObject1.optString("voucher").trim().equals("1")))
                    {
                        isVoucher = 1;
                    }
                    else
                    {
                        isVoucher = 0;
                    }

                    // Calling Is isPpvSubscribed.
/*

                    asynValidateUserDetails = new AsynValidateUserDetails();
                    asynValidateUserDetails.executeOnExecutor(threadPoolExecutor);
*/

                    Log.v("SUBHA","monetizations");

                    if(isVoucher == 1){

                        if(contentTypesId == 3) {
                            GetVoucherPlan();
                        } else{
                            creditCardLayout.setVisibility(View.VISIBLE);
                            voucherRadioButton.setChecked(false);
                            paymentOptionLinearLayout.setVisibility(View.VISIBLE);
                            payWithCreditCardRadioButton.setChecked(true);
                            voucherLinearLayout.setVisibility(View.GONE);
                        }
                        Log.v("SUBHA","monetizations voucher 1");

                    }
                    else{
                        Log.v("SUBHA","monetizations voucher 0");
                        creditCardLayout.setVisibility(View.VISIBLE);
                        paymentOptionLinearLayout.setVisibility(View.GONE);
                        voucherLinearLayout.setVisibility(View.GONE);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else
            {
                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(PPvPaymentInfoActivity.this, R.style.MyAlertDialogStyle);
                dlgAlert.setMessage(Util.getTextofLanguage(PPvPaymentInfoActivity.this,Util.NO_DETAILS_AVAILABLE,Util.DEFAULT_NO_DETAILS_AVAILABLE));
                dlgAlert.setTitle(Util.getTextofLanguage(PPvPaymentInfoActivity.this,Util.SORRY,Util.DEFAULT_SORRY));
                dlgAlert.setPositiveButton(Util.getTextofLanguage(PPvPaymentInfoActivity.this,Util.BUTTON_OK,Util.DEFAULT_BUTTON_OK), null);
                dlgAlert.setCancelable(false);
                dlgAlert.setPositiveButton(Util.getTextofLanguage(PPvPaymentInfoActivity.this,Util.BUTTON_OK,Util.DEFAULT_BUTTON_OK),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                finish();
                            }
                        });
                dlgAlert.create().show();
            }
        }

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressBarHandler(PPvPaymentInfoActivity.this);
            pDialog.show();
        }
    }

    public void GetVoucherPlan()
    {
        AsynGetVoucherPlan getVoucherPlan = new AsynGetVoucherPlan();
        getVoucherPlan.executeOnExecutor(threadPoolExecutor);
    }

    private class AsynGetVoucherPlan extends AsyncTask<Void, Void, Void> {
        ProgressBarHandler pDialog;

        int status;
        String validUserStr;
        String userMessage;
        String responseStr;
        String loggedInIdStr;

        @Override
        protected Void doInBackground(Void... params) {

            if (loginPref != null) {
                loggedInIdStr = loginPref.getString("PREFS_LOGGEDIN_ID_KEY", null);
            }

            try {


                String urlRouteList = Util.rootUrl().trim()+Util.GetVoucherPlan.trim();
                HttpClient httpclient=new DefaultHttpClient();
                HttpPost httppost = new HttpPost(urlRouteList);
                httppost.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");
                httppost.addHeader("authToken", Util.authTokenStr.trim());
                httppost.addHeader("user_id", loggedInIdStr.trim());
                httppost.addHeader("movie_id", Util.dataModel.getMovieUniqueId().trim());
                httppost.addHeader("stream_id", Util.dataModel.getStreamUniqueId());
                httppost.addHeader("season", Util.dataModel.getSeason_id());

            /*    String urlRouteList = "http://www.idogic.com/rest/GetVoucherPlan";
                HttpClient httpclient=new DefaultHttpClient();
                HttpPost httppost = new HttpPost(urlRouteList);
                httppost.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");
                httppost.addHeader("authToken","b7fbac553a14c99adcf079be6b48fd9f");
                httppost.addHeader("user_id","5146");
                httppost.addHeader("movie_id","5b24dfaf49a996b04ef92c272bde21f0");
                httppost.addHeader("stream_id","de4fcc9ffcc0b7d3ae1468765290685f");
                httppost.addHeader("season","1");*/



                // Execute HTTP Post Request
                try {
                    HttpResponse response = httpclient.execute(httppost);
                    responseStr = EntityUtils.toString(response.getEntity());

                    Log.v("BIBHU" , "Response Of the get voucher plan = "+responseStr);


                } catch (final org.apache.http.conn.ConnectTimeoutException e){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (pDialog != null && pDialog.isShowing()) {
                                pDialog.hide();
                                pDialog = null;
                            }
                            status = 0;
                            Toast.makeText(PPvPaymentInfoActivity.this, Util.getTextofLanguage(PPvPaymentInfoActivity.this,Util.SLOW_INTERNET_CONNECTION,Util.DEFAULT_SLOW_INTERNET_CONNECTION), Toast.LENGTH_LONG).show();
                        }
                    });

                } catch (IOException e) {
                    if (pDialog != null && pDialog.isShowing()) {
                        pDialog.hide();
                        pDialog = null;
                    }
                    status = 0;

                    e.printStackTrace();
                }
                if(responseStr!=null){
                    JSONObject myJson = new JSONObject(responseStr);
                    status = Integer.parseInt(myJson.optString("code"));
                }
                else
                {
                    status = 0;
                }
            }
            catch (Exception e) {
                if (pDialog != null && pDialog.isShowing()) {
                    pDialog.hide();
                    pDialog = null;
                }
                status = 0;
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
                status = 0;
            }


            if(status == 200)
            {

                String isShow="0",isSeason="0",isEpisode="0";

                try {
                    JSONObject jsonObject = new JSONObject(responseStr);

                    // Checking for Show Purchase Type
                    if(jsonObject.optString("is_show")!=null)
                    {
                        if(!(jsonObject.optString("is_show").equals("")) && !(jsonObject.optString("is_show").equals("null"))
                                && (jsonObject.optString("is_show").trim().equals("1")))
                        {
                            isShow = "1";
                        }
                        else
                        {
                            isShow = "0";
                        }
                    }
                    else
                    {
                        isShow = "0";
                    }

                    // Checking for Season Purchase Type

                    if(jsonObject.optString("is_season")!=null)
                    {
                        if(!(jsonObject.optString("is_season").equals("")) && !(jsonObject.optString("is_season").equals("null"))
                                && (jsonObject.optString("is_season").trim().equals("1")))
                        {
                            isSeason = "1";
                        }
                        else
                        {
                            isSeason = "0";
                        }
                    }
                    else
                    {
                        isSeason = "0";
                    }

                    // Checking for Episode Purchase Type

                    if(jsonObject.optString("is_episode")!=null)
                    {
                        if(!(jsonObject.optString("is_episode").equals("")) && !(jsonObject.optString("is_episode").equals("null"))
                                && (jsonObject.optString("is_episode").trim().equals("1")))
                        {
                            isEpisode = "1";
                        }
                        else
                        {
                            isEpisode = "0";
                        }
                    }
                    else
                    {
                        isEpisode = "0";
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if(selectedPurchaseType == Integer.parseInt(isShow) || selectedPurchaseType == Integer.parseInt(isSeason) || selectedPurchaseType == Integer.parseInt(isEpisode) )
                {
                    creditCardLayout.setVisibility(View.VISIBLE);
                    voucherRadioButton.setChecked(false);
                    paymentOptionLinearLayout.setVisibility(View.VISIBLE);
                    payWithCreditCardRadioButton.setChecked(true);
                    voucherLinearLayout.setVisibility(View.GONE);
                }

                else
                {
                    creditCardLayout.setVisibility(View.VISIBLE);
                    voucherRadioButton.setChecked(false);
                    paymentOptionLinearLayout.setVisibility(View.GONE);
                    payWithCreditCardRadioButton.setChecked(true);
                    voucherLinearLayout.setVisibility(View.GONE);
                }

            }
            else
            {
                creditCardLayout.setVisibility(View.VISIBLE);
                voucherRadioButton.setChecked(false);
                paymentOptionLinearLayout.setVisibility(View.GONE);
                payWithCreditCardRadioButton.setChecked(true);
                voucherLinearLayout.setVisibility(View.GONE);
            }
        }

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressBarHandler(PPvPaymentInfoActivity.this);
            pDialog.show();
        }
    }

    public void ValidateVoucher_And_VoucherSubscription()
    {
        // Calling Validate Voucher Api

        AsynValidateVoucher asynValidateVoucher = new AsynValidateVoucher();
        asynValidateVoucher.executeOnExecutor(threadPoolExecutor);
    }

    private class AsynValidateVoucher extends AsyncTask<Void, Void, Void> {
        ProgressDialog progressDialog1;

        int status;
        String responseStr;
        String loggedInIdStr;
        String message = "Invalid Voucher.";



        @Override
        protected Void doInBackground(Void... params) {

            if (loginPref != null) {
                loggedInIdStr = loginPref.getString("PREFS_LOGGEDIN_ID_KEY", null);
            }

            try {

                String urlRouteList = Util.rootUrl().trim()+Util.ValidateVoucher.trim();
                HttpClient httpclient=new DefaultHttpClient();
                HttpPost httppost = new HttpPost(urlRouteList);
                httppost.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");
                httppost.addHeader("authToken", Util.authTokenStr.trim());
                httppost.addHeader("user_id", loggedInIdStr.trim());
                httppost.addHeader("movie_id", Util.dataModel.getMovieUniqueId().trim());
                httppost.addHeader("stream_id",Util.dataModel.getStreamUniqueId().trim());
                // httppost.addHeader("season", Util.dataModel.getSeason_id());  // This is optional,so don't need to send here
                httppost.addHeader("voucher_code", VoucherCode);
                httppost.addHeader("lang_code",Util.getTextofLanguage(PPvPaymentInfoActivity.this,Util.SELECTED_LANGUAGE_CODE,Util.DEFAULT_SELECTED_LANGUAGE_CODE));


                if(Util.dataModel.getContentTypesId() == 3)
                {
                    httppost.addHeader("season", Util.dataModel.getSeason_id());
                    httppost.addHeader("purchase_type", "episode");

                }
                else
                {
                    httppost.addHeader("purchase_type","show");
                }

                // Execute HTTP Post Request
                try {
                    HttpResponse response = httpclient.execute(httppost);
                    responseStr = EntityUtils.toString(response.getEntity());

                    Log.v("BIBHU" , "Response Of validate voucher  = "+responseStr);


                } catch (final org.apache.http.conn.ConnectTimeoutException e){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (progressDialog1 != null && progressDialog1.isShowing()) {
                                progressDialog1.hide();
                                progressDialog1 = null;
                            }
                            status = 0;
                            Toast.makeText(PPvPaymentInfoActivity.this, Util.getTextofLanguage(PPvPaymentInfoActivity.this,Util.SLOW_INTERNET_CONNECTION,Util.DEFAULT_SLOW_INTERNET_CONNECTION), Toast.LENGTH_LONG).show();
                        }
                    });

                } catch (IOException e) {
                    if (progressDialog1 != null && progressDialog1.isShowing()) {
                        progressDialog1.hide();
                        progressDialog1 = null;
                    }
                    status = 0;

                    e.printStackTrace();
                }
                if(responseStr!=null){
                    JSONObject myJson = new JSONObject(responseStr);
                    status = Integer.parseInt(myJson.optString("code"));
                    message = myJson.optString("msg");
                }
                else
                {
                    status = 0;
                }
            }
            catch (Exception e) {
                if (progressDialog1 != null && progressDialog1.isShowing()) {
                    progressDialog1.hide();
                    progressDialog1 = null;
                }
                status = 0;
            }

            return null;
        }


        protected void onPostExecute(Void result) {
            try {
                if (progressDialog1 != null && progressDialog1.isShowing()) {
                    progressDialog1.hide();
                    progressDialog1 = null;
                }
            } catch (IllegalArgumentException ex) {
                status = 0;
            }

            if(status == 200)
            {


                voucher_success.setVisibility(View.VISIBLE);
                watch_now.setBackgroundResource(R.drawable.button_radious);
                watch_now.setTextColor(Color.parseColor("#ffffff"));
                watch_status = true;

                apply.setEnabled(false);
                apply.setBackgroundResource(R.drawable.voucher_inactive_button);
                apply.setTextColor(Color.parseColor("#7f7f7f"));

            }
            else
            {
                Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onPreExecute() {
            progressDialog1 = new ProgressDialog(PPvPaymentInfoActivity.this,R.style.MyTheme);
            progressDialog1.setCancelable(false);
            progressDialog1.setProgressStyle(android.R.style.Widget_ProgressBar_Large_Inverse);
            progressDialog1.setIndeterminate(false);
            progressDialog1.setIndeterminateDrawable(getResources().getDrawable(R.drawable.dialog_progress_rawable));
            progressDialog1.show();
        }
    }

    private class AsynVoucherSubscription extends AsyncTask<Void, Void, Void> {
        ProgressDialog progressDialog1;

        int status;
        String responseStr;
        String loggedInIdStr;
        String message = "";

        @Override
        protected Void doInBackground(Void... params) {

            if (loginPref != null) {
                loggedInIdStr = loginPref.getString("PREFS_LOGGEDIN_ID_KEY", null);
            }

            try {

                String urlRouteList = Util.rootUrl().trim()+Util.VoucherSubscription.trim();
                HttpClient httpclient=new DefaultHttpClient();
                HttpPost httppost = new HttpPost(urlRouteList);
                httppost.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");
                httppost.addHeader("authToken", Util.authTokenStr.trim());
                httppost.addHeader("user_id", loggedInIdStr.trim());
                httppost.addHeader("movie_id", Util.dataModel.getMovieUniqueId().trim());
                httppost.addHeader("stream_id", Util.dataModel.getStreamUniqueId().trim());
                // httppost.addHeader("season", Util.dataModel.getSeason_id()); // This is optional here
                httppost.addHeader("voucher_code", VoucherCode);

                httppost.addHeader("is_preorder",""+Util.dataModel.getIsAPV());
                httppost.addHeader("lang_code",Util.getTextofLanguage(PPvPaymentInfoActivity.this,Util.SELECTED_LANGUAGE_CODE,Util.DEFAULT_SELECTED_LANGUAGE_CODE));
                if(Util.dataModel.getContentTypesId() == 3)
                {
                    httppost.addHeader("season", Util.dataModel.getSeason_id());

                    if(selectedPurchaseType==1)
                        httppost.addHeader("purchase_type", "show");
                    if(selectedPurchaseType==2)
                        httppost.addHeader("purchase_type", "season");
                    if(selectedPurchaseType==3)
                        httppost.addHeader("purchase_type", "episode");
                }
                else
                {
                    httppost.addHeader("purchase_type","show");
                }


                // Execute HTTP Post Request
                try {
                    HttpResponse response = httpclient.execute(httppost);
                    responseStr = EntityUtils.toString(response.getEntity());

                    Log.v("BIBHU" , "Response Of validate voucher  = "+responseStr);


                } catch (final org.apache.http.conn.ConnectTimeoutException e){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (progressDialog1 != null && progressDialog1.isShowing()) {
                                progressDialog1.hide();
                                progressDialog1 = null;
                            }
                            status = 0;
                            Toast.makeText(PPvPaymentInfoActivity.this, Util.getTextofLanguage(PPvPaymentInfoActivity.this,Util.SLOW_INTERNET_CONNECTION,Util.DEFAULT_SLOW_INTERNET_CONNECTION), Toast.LENGTH_LONG).show();
                        }
                    });

                } catch (IOException e) {
                    if (progressDialog1 != null && progressDialog1.isShowing()) {
                        progressDialog1.hide();
                        progressDialog1 = null;
                    }
                    status = 0;

                    e.printStackTrace();
                }
                if(responseStr!=null){
                    JSONObject myJson = new JSONObject(responseStr);
                    status = Integer.parseInt(myJson.optString("code"));
                    message = myJson.optString("msg");
                }
                else
                {
                    status = 0;
                }
            }
            catch (Exception e) {

                status = 0;
            }

            return null;
        }


        protected void onPostExecute(Void result) {
            try {
                if (progressDialog1 != null && progressDialog1.isShowing()) {
                    progressDialog1.hide();
                }
            } catch (IllegalArgumentException ex) {
                status = 0;
            }

            if(status == 200)
            {


                AsynLoadVideoUrls asynLoadVideoUrls = new AsynLoadVideoUrls();
                asynLoadVideoUrls.executeOnExecutor(threadPoolExecutor);

            }
            else
            {
                Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onPreExecute() {

            progressDialog1 = new ProgressDialog(PPvPaymentInfoActivity.this,R.style.MyTheme);
            progressDialog1.setCancelable(false);
            progressDialog1.setProgressStyle(android.R.style.Widget_ProgressBar_Large_Inverse);
            progressDialog1.setIndeterminate(false);
            progressDialog1.setIndeterminateDrawable(getResources().getDrawable(R.drawable.dialog_progress_rawable));
            progressDialog1.show();
        }
    }

    private class AsynLoadVideoUrls extends AsyncTask<Void, Void, Void> {
        ProgressBarHandler pDialog;
        String responseStr;
        int statusCode;
        // This is added because of change in simultaneous login feature
        String message;
        boolean play_video = true;
        @Override
        protected Void doInBackground(Void... params) {
            try {
                HttpClient httpclient=new DefaultHttpClient();
                HttpPost httppost = new HttpPost(Util.rootUrl().trim()+Util.loadVideoUrl.trim());
                httppost.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");
                httppost.addHeader("authToken", Util.authTokenStr.trim());
                httppost.addHeader("content_uniq_id", Util.dataModel.getMovieUniqueId().trim());
                httppost.addHeader("stream_uniq_id", Util.dataModel.getStreamUniqueId().trim());
                httppost.addHeader("internet_speed",MainActivity.internetSpeed.trim());
                httppost.addHeader("user_id",loginPref.getString("PREFS_LOGGEDIN_ID_KEY", null));
                httppost.addHeader("lang_code",Util.getTextofLanguage(PPvPaymentInfoActivity.this,Util.SELECTED_LANGUAGE_CODE,Util.DEFAULT_SELECTED_LANGUAGE_CODE));


                // Execute HTTP Post Request
                try {

                    HttpResponse response = httpclient.execute(httppost);
                    responseStr = EntityUtils.toString(response.getEntity());

                } catch (org.apache.http.conn.ConnectTimeoutException e){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (pDialog != null && pDialog.isShowing()) {
                                pDialog.hide();
                                pDialog = null;
                            }
                            responseStr = "0";
                            Util.dataModel.setVideoUrl(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.NO_DATA, Util.DEFAULT_NO_DATA));
                            Util.showToast(PPvPaymentInfoActivity.this, Util.getTextofLanguage(PPvPaymentInfoActivity.this,Util.SLOW_INTERNET_CONNECTION,Util.DEFAULT_SLOW_INTERNET_CONNECTION));

                            //  Toast.makeText(MovieDetailsActivity.this, Util.getTextofLanguage(MovieDetailsActivity.this,Util.SLOW_INTERNET_CONNECTION,Util.DEFAULT_SLOW_INTERNET_CONNECTION), Toast.LENGTH_LONG).show();

                        }

                    });

                }catch (IOException e) {
                    if (pDialog != null && pDialog.isShowing()) {
                        pDialog.hide();
                        pDialog = null;
                    }
                    responseStr = "0";
                    Util.dataModel.setVideoUrl(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.NO_DATA, Util.DEFAULT_NO_DATA));
                    e.printStackTrace();
                }

                JSONObject myJson =null;
                JSONArray SubtitleJosnArray = null;
                JSONArray ResolutionJosnArray = null;
                if(responseStr!=null){
                    myJson = new JSONObject(responseStr);
                    SubtitleJosnArray = myJson.optJSONArray("subTitle");
                    ResolutionJosnArray = myJson.optJSONArray("videoDetails");
                    statusCode = Integer.parseInt(myJson.optString("code"));
                    // This is added because of change in simultaneous login feature
                    message = myJson.optString("msg");

                    Log.v("BIBHU","video stream msg"+message);
                    // ================================== End ====================================//
                }

                if (statusCode >= 0) {
                    if (statusCode == 200) {
                        if (Util.dataModel.getThirdPartyUrl().matches("") || Util.dataModel.getThirdPartyUrl().equalsIgnoreCase(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.NO_DATA, Util.DEFAULT_NO_DATA))) {
                            if ((myJson.has("studio_approved_url")) && myJson.getString("studio_approved_url").trim() != null && !myJson.getString("studio_approved_url").trim().isEmpty() && !myJson.getString("studio_approved_url").trim().equals("null") && !myJson.getString("studio_approved_url").trim().matches("")) {
                                Util.dataModel.setVideoUrl(myJson.getString("studio_approved_url"));
                                if ((myJson.has("licenseUrl")) && myJson.getString("licenseUrl").trim() != null && !myJson.getString("licenseUrl").trim().isEmpty() && !myJson.getString("licenseUrl").trim().equals("null") && !myJson.getString("licenseUrl").trim().matches("")) {
                                    Util.dataModel.setLicenseUrl(myJson.getString("licenseUrl"));
                                }
                                if ((myJson.has("videoUrl")) && myJson.getString("videoUrl").trim() != null && !myJson.getString("videoUrl").trim().isEmpty() && !myJson.getString("videoUrl").trim().equals("null") && !myJson.getString("videoUrl").trim().matches("")) {
                                    Util.dataModel.setMpdVideoUrl(myJson.getString("videoUrl"));

                                }else {
                                    Util.dataModel.setMpdVideoUrl(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.NO_DATA, Util.DEFAULT_NO_DATA));
                                }
                            }

                           /* if ((myJson.has("videoUrl")) && myJson.getString("videoUrl").trim() != null && !myJson.getString("videoUrl").trim().isEmpty() && !myJson.getString("videoUrl").trim().equals("null") && !myJson.getString("videoUrl").trim().matches("")) {
                                Util.dataModel.setVideoUrl(myJson.getString("videoUrl"));
                            }*/

                            else{
                                if ((myJson.has("videoUrl")) && myJson.getString("videoUrl").trim() != null && !myJson.getString("videoUrl").trim().isEmpty() && !myJson.getString("videoUrl").trim().equals("null") && !myJson.getString("videoUrl").trim().matches("")) {
                                    Util.dataModel.setVideoUrl(myJson.getString("videoUrl"));

                                }else {
                                    Util.dataModel.setVideoUrl(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.NO_DATA, Util.DEFAULT_NO_DATA));
                                }
                            }

                        } else {
                            if ((myJson.has("thirdparty_url")) && myJson.getString("thirdparty_url").trim() != null && !myJson.getString("thirdparty_url").trim().isEmpty() && !myJson.getString("thirdparty_url").trim().equals("null") && !myJson.getString("thirdparty_url").trim().matches("")) {
                                Util.dataModel.setVideoUrl(myJson.getString("thirdparty_url"));

                            } else {
                                Util.dataModel.setVideoUrl(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.NO_DATA, Util.DEFAULT_NO_DATA));

                            }
                        }
                        if ((myJson.has("videoResolution")) && myJson.getString("videoResolution").trim() != null && !myJson.getString("videoResolution").trim().isEmpty() && !myJson.getString("videoResolution").trim().equals("null") && !myJson.getString("videoResolution").trim().matches("")) {
                            Util.dataModel.setVideoResolution(myJson.getString("videoResolution"));

                        }
                        if ((myJson.has("played_length")) && myJson.getString("played_length").trim() != null && !myJson.getString("played_length").trim().isEmpty() && !myJson.getString("played_length").trim().equals("null") && !myJson.getString("played_length").trim().matches("")) {
                            Util.dataModel.setPlayPos(Util.isDouble(myJson.getString("played_length")));
                        } else {

                        }

                        if((myJson.has("is_offline")) && myJson.getString("is_offline").trim() != null && !myJson.getString("is_offline").trim().isEmpty() && !myJson.getString("is_offline").trim().equals("null") && !myJson.getString("is_offline").trim().matches("")){

                            //offline = myJson.getString("is_offline");
                            Util.dataModel.setIsOffline(Util.isOffline=myJson.getString("is_offline"));

                        }else {


                        }

                        if (SubtitleJosnArray != null) {
                            if (SubtitleJosnArray.length() > 0) {
                                for (int i = 0; i < SubtitleJosnArray.length(); i++) {
                                    SubTitleName.add(SubtitleJosnArray.getJSONObject(i).optString("language").trim());
                                    FakeSubTitlePath.add(SubtitleJosnArray.getJSONObject(i).optString("url").trim());
                                    SubTitleLanguage.add(SubtitleJosnArray.getJSONObject(i).optString("code").trim());
                                    Util.offline_url.add(SubtitleJosnArray.getJSONObject(i).optString("url").trim());
                                    Util.offline_language.add(SubtitleJosnArray.getJSONObject(i).optString("language").trim());

                                }
                            }
                        }


                        // This is added because of change in simultaneous login feature
                        if(Util.getTextofLanguage(PPvPaymentInfoActivity.this,Util.IS_STREAMING_RESTRICTION,Util.DEFAULT_IS_IS_STREAMING_RESTRICTION).equals("1"))
                        {

                            Log.v("BIBHU","streaming_restriction============"+myJson.optString("streaming_restriction").toString().trim());

                            if(myJson.optString("streaming_restriction").toString().trim().equals("0"))
                            {
                                play_video = false;
                            }
                            else
                            {
                                play_video = true;
                            }
                        }
                        else
                        {
                            play_video = true;
                        }

                        // ================================== End ====================================//
                    }

                }
                else {

                    responseStr = "0";
                    Util.dataModel.setVideoUrl(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.NO_DATA, Util.DEFAULT_NO_DATA));
                }
            } catch (JSONException e1) {
                if (pDialog != null && pDialog.isShowing()) {
                    pDialog.hide();
                    pDialog = null;
                }
                responseStr = "0";
                Util.dataModel.setVideoUrl(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.NO_DATA, Util.DEFAULT_NO_DATA));
                e1.printStackTrace();
            }

            catch (Exception e)
            {
                if (pDialog != null && pDialog.isShowing()) {
                    pDialog.hide();
                    pDialog = null;
                }
                responseStr = "0";
                Util.dataModel.setVideoUrl(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.NO_DATA, Util.DEFAULT_NO_DATA));

                e.printStackTrace();

            }
            return null;

        }

        protected void onPostExecute(Void result) {

            // This is added because of change in simultaneous login feature //

            if (!play_video) {

                try {
                    if (pDialog.isShowing())
                        pDialog.hide();
                } catch (IllegalArgumentException ex) {
                }

                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(PPvPaymentInfoActivity.this, R.style.MyAlertDialogStyle);
                dlgAlert.setMessage(message);
                dlgAlert.setTitle(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.SORRY, Util.DEFAULT_SORRY));
                dlgAlert.setPositiveButton(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.BUTTON_OK, Util.DEFAULT_BUTTON_OK), null);
                dlgAlert.setCancelable(false);
                dlgAlert.setPositiveButton(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.BUTTON_OK, Util.DEFAULT_BUTTON_OK),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                                finish();

                            }
                        });
                dlgAlert.create().show();

                return;
            }

            //=====================End========================================//
            if (responseStr == null) {
                responseStr = "0";
                Util.dataModel.setVideoUrl(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.NO_DATA, Util.DEFAULT_NO_DATA));
                //movieThirdPartyUrl = getResources().getString(R.string.no_data_str);
            }

            if ((responseStr.trim().equalsIgnoreCase("0"))) {
                try {
                    if (pDialog != null && pDialog.isShowing()) {
                        pDialog.hide();
                        pDialog = null;
                    }
                } catch (IllegalArgumentException ex) {
                    Util.dataModel.setVideoUrl(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.NO_DATA, Util.DEFAULT_NO_DATA));
                    // movieThirdPartyUrl = getResources().getString(R.string.no_data_str);
                }
                Util.dataModel.setVideoUrl(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.NO_DATA, Util.DEFAULT_NO_DATA));
                //movieThirdPartyUrl = getResources().getString(R.string.no_data_str);
                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(PPvPaymentInfoActivity.this, R.style.MyAlertDialogStyle);
                dlgAlert.setMessage(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.NO_VIDEO_AVAILABLE, Util.DEFAULT_NO_VIDEO_AVAILABLE));
                dlgAlert.setTitle(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.SORRY, Util.DEFAULT_SORRY));
                dlgAlert.setPositiveButton(Util.getTextofLanguage(PPvPaymentInfoActivity.this,Util.BUTTON_OK,Util.DEFAULT_BUTTON_OK), null);
                dlgAlert.setCancelable(false);
                dlgAlert.setPositiveButton(Util.getTextofLanguage(PPvPaymentInfoActivity.this,Util.BUTTON_OK,Util.DEFAULT_BUTTON_OK),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                                finish();
                            }
                        });
                dlgAlert.create().show();
            } else {

                if (Util.dataModel.getVideoUrl() == null) {
                    try {
                        if (pDialog != null && pDialog.isShowing()) {
                            pDialog.hide();
                            pDialog = null;
                        }
                    } catch (IllegalArgumentException ex) {
                        Util.dataModel.setVideoUrl(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.NO_DATA, Util.DEFAULT_NO_DATA));
                    }
                    AlertDialog.Builder dlgAlert = new AlertDialog.Builder(PPvPaymentInfoActivity.this, R.style.MyAlertDialogStyle);
                    dlgAlert.setMessage(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.NO_VIDEO_AVAILABLE, Util.DEFAULT_NO_VIDEO_AVAILABLE));
                    dlgAlert.setTitle(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.SORRY, Util.DEFAULT_SORRY));
                    dlgAlert.setPositiveButton(Util.getTextofLanguage(PPvPaymentInfoActivity.this,Util.BUTTON_OK,Util.DEFAULT_BUTTON_OK), null);
                    dlgAlert.setCancelable(false);
                    dlgAlert.setPositiveButton(Util.getTextofLanguage(PPvPaymentInfoActivity.this,Util.BUTTON_OK,Util.DEFAULT_BUTTON_OK),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                                    finish();
                                }
                            });
                    dlgAlert.create().show();
                } else if (Util.dataModel.getVideoUrl().matches("") || Util.dataModel.getVideoUrl().equalsIgnoreCase(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.NO_DATA, Util.DEFAULT_NO_DATA))) {
                    try {
                        if (pDialog != null && pDialog.isShowing()) {
                            pDialog.hide();
                            pDialog = null;
                        }
                    } catch (IllegalArgumentException ex) {
                        Util.dataModel.setVideoUrl(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.NO_DATA, Util.DEFAULT_NO_DATA));
                    }
                    AlertDialog.Builder dlgAlert = new AlertDialog.Builder(PPvPaymentInfoActivity.this, R.style.MyAlertDialogStyle);
                    dlgAlert.setMessage(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.NO_VIDEO_AVAILABLE, Util.DEFAULT_NO_VIDEO_AVAILABLE));
                    dlgAlert.setTitle(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.SORRY, Util.DEFAULT_SORRY));
                    dlgAlert.setPositiveButton(Util.getTextofLanguage(PPvPaymentInfoActivity.this,Util.BUTTON_OK,Util.DEFAULT_BUTTON_OK), null);
                    dlgAlert.setCancelable(false);
                    dlgAlert.setPositiveButton(Util.getTextofLanguage(PPvPaymentInfoActivity.this,Util.BUTTON_OK,Util.DEFAULT_BUTTON_OK),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                                    finish();
                                }
                            });
                    dlgAlert.create().show();
                } else {
                    try {
                        if (pDialog != null && pDialog.isShowing()) {
                            pDialog.hide();
                            pDialog = null;
                        }
                    } catch (IllegalArgumentException ex) {
                        Util.dataModel.setVideoUrl(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.NO_DATA, Util.DEFAULT_NO_DATA));
                    }

                    if (Util.dataModel.getThirdPartyUrl().matches("") || Util.dataModel.getThirdPartyUrl().equalsIgnoreCase(Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.NO_DATA, Util.DEFAULT_NO_DATA))) {

                        if (mCastSession != null && mCastSession.isConnected()) {


                            MediaMetadata movieMetadata = new MediaMetadata(MediaMetadata.MEDIA_TYPE_MOVIE);

                            movieMetadata.putString(MediaMetadata.KEY_SUBTITLE, Util.dataModel.getVideoStory());
                            movieMetadata.putString(MediaMetadata.KEY_TITLE, Util.dataModel.getVideoTitle());
                            movieMetadata.addImage(new WebImage(Uri.parse(Util.dataModel.getPosterImageId())));
                            movieMetadata.addImage(new WebImage(Uri.parse(Util.dataModel.getPosterImageId())));
                            /*JSONObject jsonObj = null;
                            try {
                                jsonObj = new JSONObject();
                                jsonObj.put("description", Util.dataModel.getVideoTitle());
                            } catch (JSONException e) {
                            }

                            mediaInfo = new MediaInfo.Builder(Util.dataModel.getVideoUrl().trim())
                                    .setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
                                    .setContentType("videos/mp4")
                                    .setMetadata(movieMetadata)
                                    .setStreamDuration(15 * 1000)
                                    .setCustomData(jsonObj)
                                    .build();
                            mSelectedMedia = mediaInfo;


                            togglePlayback();*/
                            String mediaContentType = "videos/mp4";
                            if (Util.dataModel.getVideoUrl().contains(".mpd")) {
                                mediaContentType = "application/dash+xml";
                                JSONObject jsonObj = null;
                                try {
                                    jsonObj = new JSONObject();
                                    jsonObj.put("description", Util.dataModel.getVideoTitle()

                                    );
                                    jsonObj.put("licenseUrl", Util.dataModel.getLicenseUrl());

                                } catch (JSONException e) {
                                }
                                List tracks = new ArrayList();
                                for (int i = 0; i < FakeSubTitlePath.size(); i++) {
                                    MediaTrack englishSubtitle = new MediaTrack.Builder(i,
                                            MediaTrack.TYPE_TEXT)
                                            .setName(SubTitleName.get(0))
                                            .setSubtype(MediaTrack.SUBTYPE_SUBTITLES)
                                            .setContentId(FakeSubTitlePath.get(0))
                                            .setLanguage(SubTitleLanguage.get(0))
                                            .setContentType("text/vtt")
                                            .build();
                                    tracks.add(englishSubtitle);
                                }

                                mediaInfo = new MediaInfo.Builder(Util.dataModel.getMpdVideoUrl().trim())
                                        .setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
                                        .setContentType(mediaContentType)
                                        .setMetadata(movieMetadata)
                                        .setStreamDuration(15 * 1000)
                                        .setCustomData(jsonObj)
                                        .setMediaTracks(tracks)
                                        .build();
                                mSelectedMedia = mediaInfo;


                                togglePlayback();
                            } else {
                                JSONObject jsonObj = null;
                                try {
                                    jsonObj = new JSONObject();
                                    jsonObj.put("description", Util.dataModel.getVideoTitle()

                                    );

                                } catch (JSONException e) {
                                }

                                List tracks = new ArrayList();
                                for (int i = 0; i < FakeSubTitlePath.size(); i++) {
                                    MediaTrack englishSubtitle = new MediaTrack.Builder(i,
                                            MediaTrack.TYPE_TEXT)
                                            .setName(SubTitleName.get(0))
                                            .setSubtype(MediaTrack.SUBTYPE_SUBTITLES)
                                            .setContentId(FakeSubTitlePath.get(0))
                                            .setLanguage(SubTitleLanguage.get(0))
                                            .setContentType("text/vtt")
                                            .build();
                                    tracks.add(englishSubtitle);
                                }


                                mediaInfo = new MediaInfo.Builder(Util.dataModel.getVideoUrl().trim())
                                        .setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
                                        .setContentType(mediaContentType)
                                        .setMetadata(movieMetadata)
                                        .setStreamDuration(15 * 1000)
                                        .setCustomData(jsonObj)
                                        .setMediaTracks(tracks)
                                        .build();
                                mSelectedMedia = mediaInfo;


                                togglePlayback();
                            }
                        } else {

                            if (Util.dataModel.getVideoUrl().contains("rtmp://") || Util.dataModel.getVideoUrl().contains("rtmp://")) {
                                Toast.makeText(PPvPaymentInfoActivity.this, Util.getTextofLanguage(PPvPaymentInfoActivity.this, Util.VIDEO_ISSUE, Util.DEFAULT_VIDEO_ISSUE), Toast.LENGTH_SHORT).show();
                            } else {
                                final Intent playVideoIntent;

                                playVideoIntent = new Intent(PPvPaymentInfoActivity.this, ExoPlayerActivity.class);


                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        if (FakeSubTitlePath.size() > 0) {
                                            // This Portion Will Be changed Later.

                                            File dir = new File(Environment.getExternalStorageDirectory() + "/Android/data/" + getApplicationContext().getPackageName().trim() + "/SubTitleList/");
                                            if (dir.isDirectory()) {
                                                String[] children = dir.list();
                                                for (int i = 0; i < children.length; i++) {
                                                    new File(dir, children[i]).delete();
                                                }
                                            }

                                            progressBarHandler = new ProgressBarHandler(PPvPaymentInfoActivity.this);
                                            progressBarHandler.show();
                                            Download_SubTitle(FakeSubTitlePath.get(0).trim());
                                        } else {
                                            playVideoIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                            playVideoIntent.putExtra("SubTitleName", SubTitleName);
                                            playVideoIntent.putExtra("SubTitlePath", SubTitlePath);
                                            playVideoIntent.putExtra("ResolutionFormat",ResolutionFormat);
                                            playVideoIntent.putExtra("ResolutionUrl",ResolutionUrl);
                                            startActivity(playVideoIntent);
                                            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                                            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                                            finish();
                                        }


                                    }
                                });
                            }
                        }
                    } else {
                        if (Util.dataModel.getVideoUrl().contains("://www.youtube") || Util.dataModel.getVideoUrl().contains("://www.youtu.be")) {
                            if (Util.dataModel.getVideoUrl().contains("live_stream?channel")) {
                                final Intent playVideoIntent = new Intent(PPvPaymentInfoActivity.this, ThirdPartyPlayer.class);
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        playVideoIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                        startActivity(playVideoIntent);
                                        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                                        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                                        finish();

                                    }
                                });
                            } else {

                                final Intent playVideoIntent = new Intent(PPvPaymentInfoActivity.this, YouTubeAPIActivity.class);
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        playVideoIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                        startActivity(playVideoIntent);
                                        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                                        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                                        finish();


                                    }
                                });

                            }
                        } else {
                            final Intent playVideoIntent = new Intent(PPvPaymentInfoActivity.this, ThirdPartyPlayer.class);
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    playVideoIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                    startActivity(playVideoIntent);
                                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                                    finish();

                                }
                            });
                        }
                    }
                }


            }
        }


        @Override
        protected void onPreExecute() {

            SubTitleName.clear();
            SubTitlePath.clear();
            ResolutionUrl.clear();
            ResolutionFormat.clear();
            SubTitleLanguage.clear();
            Util.offline_url.clear();
            Util.offline_language.clear();
            pDialog = new ProgressBarHandler(PPvPaymentInfoActivity.this);
            pDialog.show();

        }


    }



    private void updateMetadata(boolean visible) {
        Point displaySize;
        if (!visible) {
            /*mDescriptionView.setVisibility(View.GONE);
            mTitleView.setVisibility(View.GONE);
            mAuthorView.setVisibility(View.GONE);*/
            displaySize = Util.getDisplaySize(this);
            RelativeLayout.LayoutParams lp = new
                    RelativeLayout.LayoutParams(displaySize.x,
                    displaySize.y + getSupportActionBar().getHeight());
            lp.addRule(RelativeLayout.CENTER_IN_PARENT);
            // mVideoView.setLayoutParams(lp);
            //mVideoView.invalidate();
        } else {
            //MediaMetadata mm = mSelectedMedia.getMetadata();
          /*  mDescriptionView.setText(mSelectedMedia.getCustomData().optString(
                    VideoProvider.KEY_DESCRIPTION));
            //mTitleView.setText(mm.getString(MediaMetadata.KEY_TITLE));
            //mAuthorView.setText(mm.getString(MediaMetadata.KEY_SUBTITLE));
            mDescriptionView.setVisibility(View.VISIBLE);
            mTitleView.setVisibility(View.VISIBLE);
            mAuthorView.setVisibility(View.VISIBLE);*/
            displaySize = Util.getDisplaySize(this);
            RelativeLayout.LayoutParams lp = new
                    RelativeLayout.LayoutParams(displaySize.x,
                    (int) (displaySize.x * mAspectRatio));
            lp.addRule(RelativeLayout.BELOW, R.id.toolbar);
            // mVideoView.setLayoutParams(lp);
            //mVideoView.invalidate();
        }
    }


    private void setupCastListener() {
        mSessionManagerListener = new SessionManagerListener<CastSession>() {

            @Override
            public void onSessionEnded(CastSession session, int error) {
                onApplicationDisconnected();
            }

            @Override
            public void onSessionResumed(CastSession session, boolean wasSuspended) {
                onApplicationConnected(session);
            }

            @Override
            public void onSessionResumeFailed(CastSession session, int error) {
                onApplicationDisconnected();
            }

            @Override
            public void onSessionStarted(CastSession session, String sessionId) {
                onApplicationConnected(session);
            }

            @Override
            public void onSessionStartFailed(CastSession session, int error) {
                onApplicationDisconnected();
            }

            @Override
            public void onSessionStarting(CastSession session) {
            }

            @Override
            public void onSessionEnding(CastSession session) {
            }

            @Override
            public void onSessionResuming(CastSession session, String sessionId) {
            }

            @Override
            public void onSessionSuspended(CastSession session, int reason) {
            }

            private void onApplicationConnected(CastSession castSession) {
                mCastSession = castSession;
                mLocation = MovieDetailsActivity.PlaybackLocation.REMOTE;
                if (null != mSelectedMedia) {

                    if (mPlaybackState == MovieDetailsActivity.PlaybackState.PLAYING) {
                       /* mVideoView.pause();
                        loadRemoteMedia(mSeekbar.getProgress(), true);*/
                        return;
                    } else {
                        mPlaybackState = MovieDetailsActivity.PlaybackState.IDLE;
                        updatePlaybackLocation(MovieDetailsActivity.PlaybackLocation.REMOTE);
                    }
                }
                updatePlayButton(mPlaybackState);
                invalidateOptionsMenu();
            }

            private void onApplicationDisconnected() {
/*
                    mPlayCircle.setVisibility(View.GONE);
*/

                updatePlaybackLocation(MovieDetailsActivity.PlaybackLocation.LOCAL);
                mPlaybackState = MovieDetailsActivity.PlaybackState.IDLE;
                mLocation = MovieDetailsActivity.PlaybackLocation.LOCAL;
                updatePlayButton(mPlaybackState);
                invalidateOptionsMenu();
            }
        };
    }

    private void updatePlayButton(MovieDetailsActivity.PlaybackState state) {
           /* boolean isConnected = (mCastSession != null)
                    && (mCastSession.isConnected() || mCastSession.isConnecting());*/
        //mControllers.setVisibility(isConnected ? View.GONE : View.VISIBLE);

        switch (state) {
            case PLAYING:

                //mLoading.setVisibility(View.INVISIBLE);
                // mPlayPause.setVisibility(View.VISIBLE);
                //mPlayPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_av_pause_dark));

                break;
            case IDLE:
                if (mLocation == MovieDetailsActivity.PlaybackLocation.LOCAL) {
                   /* if (isAPV == 1) {
                        watchMovieButton.setText(getResources().getString(R.string.advance_purchase_str));
                    }else {
                        watchMovieButton.setText(getResources().getString(R.string.movie_details_watch_video_button_title));
                    }*/

                } else {
                   /* if (isAPV == 1) {
                        watchMovieButton.setText(getResources().getString(R.string.advance_purchase_str));
                    }else {
                        watchMovieButton.setText(getResources().getString(R.string.movie_details_cast_now_button_title));
                    }*/
                }
                //mCon
                // trollers.setVisibility(View.GONE);
                // mCoverArt.setVisibility(View.VISIBLE);
                // mVideoView.setVisibility(View.INVISIBLE);
                break;
            case PAUSED:
                //mLoading.setVisibility(View.INVISIBLE);
              /*  mPlayPause.setVisibility(View.VISIBLE);
                mPlayPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_av_play_dark));*/

                break;
            case BUFFERING:
                //mPlayPause.setVisibility(View.INVISIBLE);
                //mLoading.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
    }

    private void updatePlaybackLocation(MovieDetailsActivity.PlaybackLocation location) {
        mLocation = location;
        if (location == MovieDetailsActivity.PlaybackLocation.LOCAL) {
            if (mPlaybackState == MovieDetailsActivity.PlaybackState.PLAYING
                    || mPlaybackState == MovieDetailsActivity.PlaybackState.BUFFERING) {
                //setCoverArtStatus(null);
                //startControllersTimer();
            } else {
                //stopControllersTimer();

                //setCoverArtStatus(MediaUtils.getImageUrl(mSelectedMedia, 0));
            }
        } else {
            //stopControllersTimer();
            // setCoverArtStatus(MediaUtils.getImageUrl(mSelectedMedia, 0));
            //updateControllersVisibility(false);
        }
    }


    private void togglePlayback() {
        //stopControllersTimer();
        switch (mPlaybackState) {
            case PAUSED:
                switch (mLocation) {
                    case LOCAL:



                      /* mVideoView.start();
                        Log.d(TAG, "Playing locally...");
                        mPlaybackState = PlaybackState.PLAYING;
                        startControllersTimer();
                        restartTrickplayTimer();
                        updatePlaybackLocation(PlaybackLocation.LOCAL);*/
                        break;

                    case REMOTE:

                        loadRemoteMedia(0, true);

                        break;
                    default:
                        break;
                }
                break;

            case PLAYING:
                mPlaybackState = MovieDetailsActivity.PlaybackState.PAUSED;

                //  mVideoView.pause();
                break;

            case IDLE:
                switch (mLocation) {
                    case LOCAL:
                        //watchMovieButton.setText(getResources().getString(R.string.movie_details_cast_now_button_title));

                        // mPlayCircle.setVisibility(View.GONE);
                       /* mVideoView.setVideoURI(Uri.parse(mSelectedMedia.getContentId()));
                        mVideoView.seekTo(0);
                        mVideoView.start();
                        mPlaybackState = PlaybackState.PLAYING;
                        restartTrickplayTimer();
                        updatePlaybackLocation(PlaybackLocation.LOCAL);*/
                        break;
                    case REMOTE:
                        // mPlayCircle.setVisibility(View.VISIBLE);
                        if (mCastSession != null && mCastSession.isConnected()) {
                            // watchMovieButton.setText(getResources().getString(R.string.movie_details_cast_now_button_title));
                            loadRemoteMedia(0, true);


                            // Utils.showQueuePopup(this, mPlayCircle, mSelectedMedia);
                        } else {
                        }
                        break;
                    default:
                        break;
                }
                break;
            default:
                break;
        }
        updatePlayButton(mPlaybackState);
    }

    private void loadRemoteMedia(int position, boolean autoPlay) {

        if (mCastSession == null) {
            return;
        }
        final RemoteMediaClient remoteMediaClient = mCastSession.getRemoteMediaClient();
        if (remoteMediaClient == null) {
            return;
        }
        remoteMediaClient.addListener(new RemoteMediaClient.Listener() {

            @Override
            public void onStatusUpdated() {

                Intent intent = new Intent(PPvPaymentInfoActivity.this, ExpandedControlsActivity.class);
                startActivity(intent);
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                remoteMediaClient.removeListener(this);
                finish();

            }

            @Override
            public void onMetadataUpdated() {
            }

            @Override
            public void onQueueStatusUpdated() {
            }

            @Override
            public void onPreloadStatusUpdated() {
            }

            @Override
            public void onSendingRemoteMediaRequest() {
            }
        });
        remoteMediaClient.setActiveMediaTracks(new long[1]).setResultCallback(new ResultCallback<RemoteMediaClient.MediaChannelResult>() {
            @Override
            public void onResult(@NonNull RemoteMediaClient.MediaChannelResult mediaChannelResult) {
                if (!mediaChannelResult.getStatus().isSuccess()) {
                    Log.v("SUBHA", "Failed with status code:" +
                            mediaChannelResult.getStatus().getStatusCode());
                }
            }
        });
        remoteMediaClient.load(mSelectedMedia, autoPlay, position);
    }

    /***************chromecast**********************/

}
