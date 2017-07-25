package com.release.reelAfrican.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.content.res.ResourcesCompat;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.release.reelAfrican.R;

/**
 * Created by User on 15-03-2017.
 */
public class ExpandableTextView extends TextView {
    private static final int DEFAULT_TRIM_LENGTH = 200;
    private  String ELLIPSIS = " View More";
    private static final String VIEW_LESS_ELLIPSIS = " View Less";

    private CharSequence originalText;
    private CharSequence trimmedText;
    private BufferType bufferType;
    private boolean trim = true;
    private int trimLength;

    public ExpandableTextView(Context context) {
        this(context, null);

    }

    public ExpandableTextView(final Context context, AttributeSet attrs) {
        super(context, attrs);

        ELLIPSIS = "  " +Util.getTextofLanguage(context, Util.VIEW_MORE, Util.DEFAULT_VIEW_MORE);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ExpandableTextView);
        this.trimLength = typedArray.getInt(R.styleable.ExpandableTextView_trimLength, DEFAULT_TRIM_LENGTH);
        typedArray.recycle();

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                trim = !trim;
                setText();
                requestFocusFromTouch();
            }
        });
    }

    private void setText() {
        super.setText(getDisplayableText(), bufferType);
    }

    private CharSequence getDisplayableText() {
        return trim ? trimmedText : originalText;
    }

    @Override
    public void setText(CharSequence text, TextView.BufferType type) {
        originalText = text;
        trimmedText = getTrimmedText(text);
        bufferType = type;
        setText();
    }

    private CharSequence getTrimmedText(CharSequence text) {
         if (originalText != null && originalText.length() > trimLength) {
             SpannableStringBuilder spanTxt = new SpannableStringBuilder(
                     originalText, 0, trimLength + 1).append(ELLIPSIS);
             spanTxt.setSpan(new ForegroundColorSpan(ResourcesCompat.getColor(getResources(), R.color.button_background, null)), spanTxt.length() - ELLIPSIS.length(), spanTxt.length(), 0);
             spanTxt.setSpan(new UnderlineSpan(), spanTxt.length() - ELLIPSIS.length() + 2, spanTxt.length(), 0);

             return spanTxt;

         } else {
            /* SpannableStringBuilder spanTxt = new SpannableStringBuilder(
                     originalText, 0, trimLength + 1).append(VIEW_LESS_ELLIPSIS);
             spanTxt.setSpan(new ForegroundColorSpan(ResourcesCompat.getColor(getResources(), R.color.buttonbg, null)), spanTxt.length() - VIEW_LESS_ELLIPSIS.length(), spanTxt.length(), 0);
             return spanTxt;*/
            return originalText;
        }
    }

    public CharSequence getOriginalText() {
        return originalText;
    }

    public void setTrimLength(int trimLength) {
        this.trimLength = trimLength;
        trimmedText = getTrimmedText(originalText);
        setText();
    }

    public int getTrimLength() {
        return trimLength;
    }
}
