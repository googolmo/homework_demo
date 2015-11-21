package im.amomo.homework.widget;

import android.content.Context;
import android.util.AttributeSet;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.ViewTarget;
import com.davemorrissey.labs.subscaleview.ImageSource;

import java.io.File;

import im.amomo.homework.BuildConfig;
import im.amomo.homework.model.Item;

/**
 * Project - HomeWork_Demo
 * Created by Moyw on 11/8/15.
 */
public class DynamicSubsamplingScaleImageView extends com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView implements
        DynamicView {
//    private static final float RATIO_MIN = 6f / 9f;

    private float mHeightRatio = 1.0f;
    private FileTarget mFileTarget;

    public DynamicSubsamplingScaleImageView(Context context, AttributeSet attr) {
        super(context, attr);
        init();
    }

    public DynamicSubsamplingScaleImageView(Context context) {
        super(context);
        init();
    }

    private void init() {
        setDebug(BuildConfig.DEBUG);
    }

    private synchronized FileTarget getFileTarget() {
        if (mFileTarget == null) {
            mFileTarget = new FileTarget(this);
        }
        return mFileTarget;
    }


    public void setImage(Item.Image image) {
        setRatio((float) image.width / (float) image.height);
        Glide.with(getContext()).load(image.url)
                .downloadOnly(getFileTarget());

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
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }

    }

    private static class FileTarget extends ViewTarget<DynamicSubsamplingScaleImageView, File> {

        public FileTarget(DynamicSubsamplingScaleImageView view) {
            super(view);
        }

        @Override
        public void onResourceReady(File resource, GlideAnimation<? super File> glideAnimation) {
            getView().setImage(ImageSource.uri(resource.getAbsolutePath()));
        }
    }

}

