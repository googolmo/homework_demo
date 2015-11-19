package im.amomo.homework.widget;

import android.content.Context;
import android.util.AttributeSet;

import com.bumptech.glide.DrawableRequestBuilder;

/**
 * Project - HomeWork_Demo
 * Created by Moyw on 11/8/15.
 */
public class DynamicHeightImageView extends android.support.v7.widget.AppCompatImageView {

    private float mHeightRatio = 1.0f;
    DrawableRequestBuilder mRequest;


    public DynamicHeightImageView(Context context) {
        super(context);
    }

    public DynamicHeightImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DynamicHeightImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setImage(DrawableRequestBuilder request, float ratio) {
        this.mRequest = request;
        this.mHeightRatio = ratio;
        requestLayout();
    }

    public void setRatio(float ratio) {
        this.mHeightRatio = ratio;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (widthMode == MeasureSpec.EXACTLY && heightMode != MeasureSpec.EXACTLY) {
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int height = (int) (width / mHeightRatio);
            if (heightMode == MeasureSpec.AT_MOST) {
                height = Math.min(height, MeasureSpec.getSize(heightMeasureSpec));
            }
            setMeasuredDimension(width, height);
            if (mRequest != null && width > 0 && height > 0) {
                mRequest.override(width, height)
                        .into(this);
            }
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }

    }
}
