package im.amomo.homework.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import im.amomo.homework.R;

/**
 * Project - HomeWork_Demo
 * Created by Moyw on 11/20/15.
 * Mock a AdView just like Facebook ad
 */
public class AdView extends FrameLayout {

    public AdView(Context context) {
        super(context);
        init();
    }

    public AdView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AdView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_ad, this);
    }
}
