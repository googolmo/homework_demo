package im.amomo.homework.widget;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.Glide;

import im.amomo.homework.model.Item;

/**
 * Project - HomeWork_Demo
 * Created by Moyw on 11/8/15.
 */
public class DynamicImageView extends AppCompatImageView implements DynamicView {
//    private static final float RATIO_MIN = 6f / 9f;

    private float mHeightRatio = 1.0f;
    private DrawableTypeRequest<String> mRequest;

    public DynamicImageView(Context context, AttributeSet attr) {
        super(context, attr);
    }

    public DynamicImageView(Context context) {
        super(context);
    }

    public DynamicImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setImage(Item.Image image) {
        setRatio((float) image.width / (float) image.height);
        mRequest = Glide.with(getContext()).load(image.url);
        requestLayout();
    }

    public void setRatio(float ratio) {
//        if (ratio < RATIO_MIN) {
//            ratio = RATIO_MIN;
//        }
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
            if (mRequest != null)  {
                mRequest.override(width, height)
                        .into(this);
            }
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }

    }

}

