package im.amomo.homework.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

/**
 * Project - HomeWork_Demo
 * Created by Moyw on 11/11/15.
 */
public class InterceptViewPager extends ViewPager {

    public InterceptViewPager(Context context) {
        super(context);
    }

    public InterceptViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected boolean canScroll(View v, boolean checkV, int dx, int x, int y) {
        if (v instanceof RecyclerView &&
                ((RecyclerView)v).getLayoutManager() instanceof LinearLayoutManager &&
                ((LinearLayoutManager)((RecyclerView) v).getLayoutManager()).getOrientation() ==
                        LinearLayoutManager.HORIZONTAL) {
            return true;
        }
        return super.canScroll(v, checkV, dx, x, y);
    }
}
