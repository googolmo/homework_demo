package im.amomo.homework.widget;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import im.amomo.homework.R;

/**
 * Project - HomeWork_Demo
 * Created by Moyw on 11/8/15.
 */
public class LoadMoreRecyclerView extends RecyclerView {

    private static final int DEFAULT_OFFSET = 3;
    private OnLoadMoreListener mOnLoadMoreListener;
    private int mOffset = DEFAULT_OFFSET;
    private boolean isLoading = false;
    private OnScrollListener mLoadMoreScrollListener;
    private Adapter mAdapter;

    public LoadMoreRecyclerView(Context context) {
        super(context);
    }

    public LoadMoreRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LoadMoreRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setOnLoadMoreListener(OnLoadMoreListener listener, int offset) {
        this.mOnLoadMoreListener = listener;
        this.mOffset = offset;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener listener) {
        setOnLoadMoreListener(listener, DEFAULT_OFFSET);
    }

    public interface OnLoadMoreListener {
        void loadMore(LoadMoreRecyclerView recyclerView, int lastPosition);
    }

    public void enableLoadMore() {
        mLoadMoreScrollListener = new OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!isLoading) {

                    int itemCount = getLayoutManager().getItemCount();
                    if (recyclerView.getLayoutManager() instanceof StaggeredGridLayoutManager) {
                        int[] positions = ((StaggeredGridLayoutManager) recyclerView.getLayoutManager())
                                .findLastVisibleItemPositions(null);
                        if (itemCount - positions[0] <= mOffset * ((StaggeredGridLayoutManager) recyclerView
                                .getLayoutManager()).getSpanCount()) {
                            isLoading = true;
                            if (mAdapter != null) {
                                mAdapter.showLoadMore();
                            }
                            mOnLoadMoreListener.loadMore(LoadMoreRecyclerView.this, itemCount - 1);
                        }
                    } else if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
                        int position = ((GridLayoutManager) recyclerView.getLayoutManager())
                                .findLastVisibleItemPosition();
                        if (itemCount - position <= mOffset) {
                            isLoading = true;
                            if (mAdapter != null) {
                                mAdapter.showLoadMore();
                            }
                            mOnLoadMoreListener.loadMore(LoadMoreRecyclerView.this, itemCount - 1);
                        }
                    } else if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
                        int position = ((LinearLayoutManager) recyclerView.getLayoutManager())
                                .findLastVisibleItemPosition();
                        if (itemCount - position <= mOffset) {
                            isLoading = true;
                            if (mAdapter != null) {
                                mAdapter.showLoadMore();
                            }
                            mOnLoadMoreListener.loadMore(LoadMoreRecyclerView.this, itemCount - 1);
                        }
                    }

                }
            }
        };
        addOnScrollListener(mLoadMoreScrollListener);
    }

    public void disableLoadMore() {
        if (mLoadMoreScrollListener != null) {
            removeOnScrollListener(mLoadMoreScrollListener);
        }
        mLoadMoreScrollListener = null;
        if (mAdapter != null) {
            mAdapter.hideLoadMore();
        }
    }

    public Adapter getAdapter() {
        return mAdapter;
    }

    public void setAdapter(Adapter adapter) {
        super.setAdapter(adapter);
        mAdapter = adapter;

        adapter.registerAdapterDataObserver(new AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                updateDisplayHelper();
            }

            @Override
            public void onItemRangeChanged(int positionStart, int itemCount) {
                super.onItemRangeChanged(positionStart, itemCount);
                updateDisplayHelper();
            }

            @Override
            public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
                super.onItemRangeChanged(positionStart, itemCount, payload);
                updateDisplayHelper();
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                updateDisplayHelper();
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                super.onItemRangeRemoved(positionStart, itemCount);
                updateDisplayHelper();
            }

            @Override
            public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
                super.onItemRangeMoved(fromPosition, toPosition, itemCount);
                updateDisplayHelper();
            }
        });
    }

    private void updateDisplayHelper() {
        if (mAdapter == null) {
            return;
        }

        if (isLoading) {
            isLoading = false;
        }

        if (mAdapter.getNormalItemCount() >= mOffset) {
            mAdapter.showLoadMore();
//            mAdapter.loadMoreView.setVisibility(View.VISIBLE);
        } else {
            mAdapter.hideLoadMore();
//            mAdapter.loadMoreView.setVisibility(View.GONE);
        }
    }

    public static abstract class Adapter<VH extends RecyclerView.ViewHolder>
            extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private View loadMoreView;
        private LoadMoreViewHolder loadMoreViewHolder;
        private boolean enableLoadMore = false;

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            if (viewType == ViewTypes.LOAD_MORE) {
                loadMoreView = inflater.inflate(R.layout.bottom_progress_bar, parent, false);
                loadMoreViewHolder = new LoadMoreViewHolder(loadMoreView);
                return loadMoreViewHolder;
            }
            return onCreateNormalViewHolder(inflater, parent, viewType);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            if (!(holder instanceof LoadMoreViewHolder)) {
                onBindNormalViewHolder(holder, position);
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (enableLoadMore && position == getItemCount() - 1) {
                return ViewTypes.LOAD_MORE;
            }
            return getNormalItemViewType(position);
        }

        @Override
        public int getItemCount() {
            int count = 0;
            if (enableLoadMore) {
                count ++;
            }
            return count + getNormalItemCount();
        }

        public View getLoadMoreView() {
            return loadMoreView;
        }

        public boolean displayEmpty() {
            return getNormalItemCount() == 0;
        }

        public int getNormalItemViewType(int position) {
            return ViewTypes.NORMAL;
        }

        public void showLoadMore() {
            if (!enableLoadMore) {
                enableLoadMore = true;
                notifyItemInserted(getItemCount() - 1);
            }

        }

        public void hideLoadMore() {
            if (enableLoadMore) {
                enableLoadMore = false;
                notifyItemRemoved(getItemCount() - 1);
            }

        }

        public abstract VH onCreateNormalViewHolder(LayoutInflater inflater, ViewGroup parent, int viewType);
        public abstract void onBindNormalViewHolder(ViewHolder viewHolder, int position);
        public abstract int getNormalItemCount();

    }

    public static class LoadMoreViewHolder extends RecyclerView.ViewHolder {

        ProgressBar progressBar;

        public LoadMoreViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progress_bar_bottom);
        }
    }

    public interface ViewTypes {
        int NORMAL = 0x11000000;
        int LOAD_MORE = 0x11000001;
    }
}
