package im.amomo.homework.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.ColorInt;
import android.support.annotation.StringRes;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import im.amomo.homework.R;

/**
 * Project - HomeWork_Demo
 * Created by Moyw on 11/18/15.
 */
public class UltimateRecyclerView extends FrameLayout {

    protected LoadMoreRecyclerView mRecyclerView;
    protected SwipeRefreshLayout mSwipeRefreshLayout;
    protected EmptyView mEmptyView;

    protected boolean mEnableSwipeRefresh;
    protected int mRecyclerViewPadding;
    protected int mRecyclerViewPaddingTop;
    protected int mRecyclerViewPaddingBottom;
    protected int mRecyclerViewPaddingLeft;
    protected int mRecyclerViewPaddingRight;
    protected boolean mClipToPadding;

    public UltimateRecyclerView(Context context) {
        super(context);
        init(null);
    }

    public UltimateRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public UltimateRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {

        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.UltimateRecyclerView);
            try {
                mEnableSwipeRefresh = a.getBoolean(R.styleable.UltimateRecyclerView_ur_swipeRefresh, false);
                mRecyclerViewPadding = (int) a.getDimension(
                        R.styleable.UltimateRecyclerView_ur_recyclerViewPadding, 0);
                mRecyclerViewPaddingTop = (int) a.getDimension(
                        R.styleable.UltimateRecyclerView_ur_recyclerViewPaddingTop, 0);
                mRecyclerViewPaddingBottom = (int) a.getDimension(
                        R.styleable.UltimateRecyclerView_ur_recyclerViewPaddingBottom, 0);
                mRecyclerViewPaddingLeft = (int) a.getDimension(
                        R.styleable.UltimateRecyclerView_ur_recyclerViewPaddingLeft, 0);
                mRecyclerViewPaddingRight = (int) a.getDimension(
                        R.styleable.UltimateRecyclerView_ur_recyclerViewPaddingRight, 0);
                mClipToPadding = a.getBoolean(R.styleable.UltimateRecyclerView_ur_recyclerViewClipToPadding, false);
            } finally {
                a.recycle();
            }
        } else {
            mEnableSwipeRefresh = false;
            mRecyclerViewPadding = 0;
            mRecyclerViewPaddingTop = 0;
            mRecyclerViewPaddingLeft = 0;
            mRecyclerViewPaddingRight = 0;
            mRecyclerViewPaddingBottom = 0;
            mClipToPadding = false;
        }

        View view = LayoutInflater.from(getContext()).inflate(R.layout.ultimate_recycler_view, this);
        mRecyclerView = (LoadMoreRecyclerView) view.findViewById(R.id.ultimate_recycler_view);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.ultimate_swipe_refresh_layout);
        mEmptyView = (EmptyView) view.findViewById(R.id.ultimate_empty_view);

        mEmptyView.setVisibility(View.GONE);

        mSwipeRefreshLayout.setEnabled(mEnableSwipeRefresh);

        if (mRecyclerViewPadding != 0) {
            mRecyclerView.setPadding(mRecyclerViewPadding, mRecyclerViewPadding, mRecyclerViewPadding, mRecyclerViewPadding);
        } else {
            mRecyclerView.setPadding(mRecyclerViewPaddingLeft, mRecyclerViewPaddingTop, mRecyclerViewPaddingRight, mRecyclerViewPaddingBottom);
        }
        mRecyclerView.setClipToPadding(mClipToPadding);
    }

    /**
     * set SwipeRefreshLayout enabled
     * @param value true or false
     */
    public void setEnableSwipeRefresh(boolean value) {
        mEnableSwipeRefresh = value;
        mSwipeRefreshLayout.setEnabled(mEnableSwipeRefresh);
    }

    /**
     * the instance of SwipeRefreshLayout
     * @return instance of SwipeRefreshLayout
     */
    public SwipeRefreshLayout getSwipeRefreshLayout() {
        return mSwipeRefreshLayout;
    }

    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }


    public void setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener listener) {
        mSwipeRefreshLayout.setOnRefreshListener(listener);
    }

    public void setRefreshing(boolean refreshing) {
        mSwipeRefreshLayout.setRefreshing(refreshing);
    }

    public boolean isRefreshing() {
        return mSwipeRefreshLayout.isRefreshing();
    }

    /**
     * Show the custom or default empty view.
     * You can customize it as loading view.
     */
    public void showEmptyView() {
        mEmptyView.setVisibility(View.VISIBLE);
    }

    /**
     * Hide the custom or default empty view.
     */
    public void hideEmptyView() {
        mEmptyView.setVisibility(View.GONE);
    }

    public void showProgress() {
        mEmptyView.showProgress();
    }

    public void showEmptyText(CharSequence text, CharSequence buttonText, View.OnClickListener listener) {
        mEmptyView.showEmpty(text, buttonText, listener);
    }

    public void showEmptyText(@StringRes int text, @StringRes int buttonText, View.OnClickListener listener) {
        mEmptyView.showEmpty(text, buttonText, listener);
    }

    public void showEmptyText(@StringRes int text) {
        mEmptyView.showEmpty(text);
    }

    public void showEmptyText(CharSequence text) {
        mEmptyView.showEmpty(text);
    }

    private int findMax(int[] lastPositions) {
        int max = Integer.MIN_VALUE;
        for (int value : lastPositions) {
            if (value > max)
                max = value;
        }
        return max;
    }

    private int findMin(int[] lastPositions) {
        int min = Integer.MAX_VALUE;
        for (int value : lastPositions) {
            if (value != RecyclerView.NO_POSITION && value < min)
                min = value;
        }
        return min;
    }


    /**
     * Enable loading more of the recyclerView
     */
    public void enableLoadMore() {
        mRecyclerView.enableLoadMore();
    }

    /**
     * Remove loading more scroll listener
     */
    public void disableLoadMore() {
        mRecyclerView.disableLoadMore();
    }

    /**
     * Set the layout manager to the recycler
     *
     * @param manager lm
     */
    public void setLayoutManager(RecyclerView.LayoutManager manager) {
        mRecyclerView.setLayoutManager(manager);
    }

    /**
     * Get the adapter of UltimateRecyclerview
     *
     * @return ad
     */
    public RecyclerView.Adapter getAdapter() {
        return mRecyclerView.getAdapter();
    }

    /**
     * Set a UltimateViewAdapter or the subclass of UltimateViewAdapter to the recyclerview
     *
     * @param adapter the adapter in normal
     */
    public void setAdapter(LoadMoreRecyclerView.Adapter adapter) {
        mRecyclerView.setAdapter(adapter);
        if (adapter != null)

            mRecyclerView.getAdapter().registerAdapterDataObserver(
                    new RecyclerView.AdapterDataObserver() {
                        @Override
                        public void onItemRangeChanged(int positionStart, int itemCount) {
                            super.onItemRangeChanged(positionStart, itemCount);
                            updateHelperDisplays();
                        }

                        @Override
                        public void onItemRangeInserted(int positionStart, int itemCount) {
                            super.onItemRangeInserted(positionStart, itemCount);
                            updateHelperDisplays();
                        }

                        @Override
                        public void onItemRangeRemoved(int positionStart, int itemCount) {
                            super.onItemRangeRemoved(positionStart, itemCount);
                            updateHelperDisplays();
                        }

                        @Override
                        public void onItemRangeMoved(int fromPosition, int toPosition,
                                int itemCount) {
                            super.onItemRangeMoved(fromPosition, toPosition, itemCount);
                            updateHelperDisplays();
                        }

                        @Override
                        public void onChanged() {
                            super.onChanged();
                            updateHelperDisplays();
                        }
                    });
        if (adapter == null || adapter.getNormalItemCount() == 0) {
            mEmptyView.setVisibility(VISIBLE);
        }
    }

    public void setHasFixedSize(boolean hasFixedSize) {
        mRecyclerView.setHasFixedSize(hasFixedSize);
    }


    public void removeItemDecoration(RecyclerView.ItemDecoration decoration) {
        mRecyclerView.removeItemDecoration(decoration);
    }

    public void addOnItemTouchListener(RecyclerView.OnItemTouchListener listener) {
        mRecyclerView.addOnItemTouchListener(listener);
    }

    public void removeOnItemTouchListener(RecyclerView.OnItemTouchListener listener) {
        mRecyclerView.removeOnItemTouchListener(listener);
    }

    public RecyclerView.LayoutManager getLayoutManager() {
        return mRecyclerView.getLayoutManager();
    }

    public void setRecylerViewBackgroundColor(@ColorInt int color) {
        mRecyclerView.setBackgroundColor(color);
    }

    public void addOnScrollListener(RecyclerView.OnScrollListener customOnScrollListener) {
        mRecyclerView.addOnScrollListener(customOnScrollListener);
    }

    public void removeOnScrollListener(RecyclerView.OnScrollListener customOnScrollListener) {
        mRecyclerView.removeOnScrollListener(customOnScrollListener);
    }

    /**
     * Add an {@link RecyclerView.ItemDecoration} to this RecyclerView. Item decorations can affect both measurement and drawing of individual item views. Item decorations are ordered. Decorations placed earlier in the list will be run/queried/drawn first for their effects on item views. Padding added to views will be nested; a padding added by an earlier decoration will mean further item decorations in the list will be asked to draw/pad within the previous decoration's given area.
     *
     * @param itemDecoration Decoration to add
     */
    public void addItemDecoration(RecyclerView.ItemDecoration itemDecoration) {
        mRecyclerView.addItemDecoration(itemDecoration);
    }

    /**
     * Add an {@link RecyclerView.ItemDecoration} to this RecyclerView. Item decorations can affect both measurement and drawing of individual item views.
     * <p>Item decorations are ordered. Decorations placed earlier in the list will be run/queried/drawn first for their effects on item views. Padding added to views will be nested; a padding added by an earlier decoration will mean further item decorations in the list will be asked to draw/pad within the previous decoration's given area.</p>
     *
     * @param itemDecoration Decoration to add
     * @param index          Position in the decoration chain to insert this decoration at. If this value is negative the decoration will be added at the end.
     */
    public void addItemDecoration(RecyclerView.ItemDecoration itemDecoration, int index) {
        mRecyclerView.addItemDecoration(itemDecoration, index);
    }

    /**
     * Sets the {@link RecyclerView.ItemAnimator} that will handle animations involving changes
     * to the items in this RecyclerView. By default, RecyclerView instantiates and
     * uses an instance of {@link android.support.v7.widget.DefaultItemAnimator}. Whether item animations are enabled for the RecyclerView depends on the ItemAnimator and whether
     * the LayoutManager {@link android.support.v7.widget.RecyclerView.LayoutManager#supportsPredictiveItemAnimations()
     * supports item animations}.
     *
     * @param animator The ItemAnimator being set. If null, no animations will occur
     *                 when changes occur to the items in this RecyclerView.
     */
    public void setItemAnimator(RecyclerView.ItemAnimator animator) {
        mRecyclerView.setItemAnimator(animator);
    }

    /**
     * Set the load more listener of recyclerview
     *
     * @param onLoadMoreListener load listen
     */
    public void setOnLoadMoreListener(LoadMoreRecyclerView.OnLoadMoreListener onLoadMoreListener) {
        mRecyclerView.setOnLoadMoreListener(onLoadMoreListener);
    }

    /**
     * Gets the current ItemAnimator for this RecyclerView. A null return value
     * indicates that there is no animator and that item changes will happen without
     * any animations. By default, RecyclerView instantiates and
     * uses an instance of {@link android.support.v7.widget.DefaultItemAnimator}.
     *
     * @return ItemAnimator The current ItemAnimator. If null, no animations will occur
     * when changes occur to the items in this RecyclerView.
     */
    public RecyclerView.ItemAnimator getItemAnimator() {
        return mRecyclerView.getItemAnimator();
    }

    private void updateHelperDisplays() {
        if (mRecyclerView.getAdapter() == null) {
            return;
        }

        if (mRecyclerView.getAdapter().displayEmpty()) {
            mEmptyView.setVisibility(View.VISIBLE);
        } else {
            mEmptyView.setVisibility(View.GONE);
        }

    }

}
