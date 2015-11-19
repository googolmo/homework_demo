package im.amomo.homework.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.StringRes;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import im.amomo.homework.R;

/**
 * Project - HomeWork_Demo
 * Created by Moyw on 11/19/15.
 */
public class EmptyView extends FrameLayout{

    private ProgressBar mProgressBar;
    private View mLayoutEmpty;
    private AppCompatTextView mText;
    private AppCompatButton mButton;

    public EmptyView(Context context) {
        super(context);
        init(context);
    }

    public EmptyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public EmptyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        @SuppressLint("InflateParams") View view = LayoutInflater.from(context).inflate(R.layout.empty_view_empty, null);

        mProgressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        mLayoutEmpty = view.findViewById(R.id.empty_layout_empty);
        mText = (AppCompatTextView) view.findViewById(R.id.empty_text_label);
        mButton = (AppCompatButton) view.findViewById(R.id.empty_btn_empty);

        addView(view);
    }

    public void showProgress() {
        mProgressBar.setVisibility(View.VISIBLE);
        mLayoutEmpty.setVisibility(View.GONE);
    }

    public void showEmpty(CharSequence text, CharSequence buttonText, OnClickListener listener) {
        if (listener == null) {
            mButton.setVisibility(View.GONE);
        } else {
            mButton.setVisibility(View.VISIBLE);
            mButton.setText(buttonText);
            mButton.setOnClickListener(listener);
        }
        mText.setText(text);
        mLayoutEmpty.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
    }

    public void showEmpty(@StringRes int res, @StringRes int btnRes, View.OnClickListener listener) {
        showEmpty(getContext().getString(res), getContext().getString(btnRes), listener);
    }

    public void showEmpty(CharSequence text) {
        showEmpty(text, null, null);
    }

    public void showEmpty(@StringRes int res) {
        showEmpty(res, R.string.ultimate_default_button_text, null);
    }

    public TextView getTextView() {
        return mText;
    }

    public Button getButton() {
        return mButton;
    }
}
