package im.amomo.homework.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import im.amomo.homework.BuildConfig;
import im.amomo.homework.R;
import im.amomo.homework.model.Item;
import im.amomo.homework.model.Items;
import im.amomo.homework.util.GsonHelper;
import im.amomo.homework.widget.DividerItemDecoration;
import im.amomo.homework.widget.DynamicHeightImageView;
import im.amomo.homework.widget.LoadMoreRecyclerView;
import im.amomo.homework.widget.UltimateRecyclerView;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class HotFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = HotFragment.class.getSimpleName();

    UltimateRecyclerView mRecyclerView;

    private VerticalAdapter mAdapter;

    private OnFragmentInteractionListener mListener;


    public HotFragment() {
    }

    public static HotFragment newInstance() {
        return new HotFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_hot, container, false);
        mRecyclerView = (UltimateRecyclerView) view.findViewById(R.id.recycler_view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(
                    context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(view.getContext(),
                LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.enableLoadMore();
        mRecyclerView.showProgress();

        mAdapter = new VerticalAdapter();
        mRecyclerView.setAdapter(mAdapter);


        mRecyclerView.addItemDecoration(
                new DividerItemDecoration(view.getContext().getApplicationContext()));

        mRecyclerView.setOnRefreshListener(this);

        mRecyclerView.setOnLoadMoreListener(new LoadMoreRecyclerView.OnLoadMoreListener() {
            @Override
            public void loadMore(LoadMoreRecyclerView recyclerView, int lastPosition) {
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "onLoadMoreListener->loadMore(" + lastPosition + ")");
                }
                fetchData(lastPosition);
            }
        });

        mRecyclerView.showEmptyView();

    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        fetchData(0);
    }

    @Override
    public void onRefresh() {
        fetchData(0);
    }

    /**
     * fetch hot list data
     *
     * @param sinceId item id let backend knows which one should as the first
     */
    private void fetchData(final long sinceId) {

        Subscription subscription = rx.Observable.create(new Observable.OnSubscribe<Items>() {
            @Override
            public void call(Subscriber<? super Items> subscriber) {
                if (subscriber.isUnsubscribed()) {
                    subscriber.onCompleted();
                    return;
                }
                try {
                    InputStreamReader reader = new InputStreamReader(getActivity().
                            getApplicationContext().getAssets().open("data/1.json"));
                    Items items = GsonHelper.getGson().fromJson(reader, Items.class);
                    subscriber.onNext(items);
                    reader.close();
                    subscriber.onCompleted();
                } catch (Exception e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                }
            }
        }).delay(2500, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Items>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, e.getMessage(), e);
                        if (mAdapter.getNormalItemCount() == 0) {
                            mRecyclerView.showEmptyText(R.string.no_data);
                        }
                        mRecyclerView.setRefreshing(false);
                    }

                    @Override
                    public void onNext(Items items) {
                        if (sinceId == 0) {
                            mAdapter.clear();
                        }
                        mAdapter.addAll(items.data);
                        if (mAdapter.getNormalItemCount() == 0) {
                            mRecyclerView.showEmptyText(R.string.no_data);
                            mRecyclerView.showEmptyView();
                        } else if (sinceId == 0){
                            fetchTop(0);
                        }
                        mRecyclerView.setRefreshing(false);
                    }
                });

        addSubscription(subscription);

    }


    private void fetchTop(final long sinceId) {
        addSubscription(rx.Observable.create(new Observable.OnSubscribe<Items>() {
            @Override
            public void call(Subscriber<? super Items> subscriber) {
                if (subscriber.isUnsubscribed()) {
                    subscriber.onCompleted();
                    return;
                }
                try {
                    InputStreamReader reader = new InputStreamReader(getActivity().
                            getApplicationContext().getAssets().open("data/2.json"));
                    Items items = GsonHelper.getGson().fromJson(reader, Items.class);
                    reader.close();
                    subscriber.onNext(items);
                    subscriber.onCompleted();
                } catch (IOException e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                }
            }
        }).delay(1300, TimeUnit.MILLISECONDS).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<Items>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, e.getMessage(), e);

                    }

                    @Override
                    public void onNext(Items items) {
                        if (sinceId == 0) {
                            mAdapter.getHorizontalAdapter().clear();
                            if (mAdapter.getItemTopViewHolder() != null) {
                                mAdapter.getItemTopViewHolder().recyclerView.scrollToPosition(0);
                            }
                        }
                        mAdapter.addTopData(items.data);
                    }
                }));

    }


    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    class VerticalAdapter extends LoadMoreRecyclerView.Adapter<RecyclerView.ViewHolder> {

        static final int VIEW_TYPE_TOP = 0x0001;
        static final int VIEW_TYPE_NORMAL = 0x0010;
        List<Item> dataList;
        private HorizontalAdapter mHorizontalAdapter;
        private View mTopView;
        private ItemTopViewHolder mItemTopViewHolder;

        public VerticalAdapter() {
            super();
            dataList = new ArrayList<>();
        }

        public void addAll(List<Item> list) {
            synchronized (this) {
                dataList.addAll(list);
                notifyItemRangeInserted(getNormalItemCount() - list.size(), list.size());
            }
        }

        public void clear() {
            synchronized (this) {
                dataList.clear();
                notifyDataSetChanged();
            }
        }

        public void addTopData(List<Item> list) {
            getHorizontalAdapter().addAll(list);
            if (getHorizontalAdapter().getNormalItemCount() == list.size()) {
                notifyItemInserted(0);
            }
        }

        public View getTopView() {
            return mTopView;
        }

        public ItemTopViewHolder getItemTopViewHolder() {
            return mItemTopViewHolder;
        }

        private HorizontalAdapter getHorizontalAdapter() {
            if (mHorizontalAdapter == null) {
                mHorizontalAdapter = new HorizontalAdapter();
            }
            return mHorizontalAdapter;
        }

        @Override
        public RecyclerView.ViewHolder onCreateNormalViewHolder(LayoutInflater inflater,
                ViewGroup parent, int viewType) {
            if (viewType == VIEW_TYPE_TOP) {
                mTopView = inflater.inflate(R.layout.item_vertical_top, parent, false);
                mItemTopViewHolder = new ItemTopViewHolder(mTopView);
                RecyclerView.LayoutManager manager = new LinearLayoutManager(parent.getContext(),
                        LinearLayoutManager.HORIZONTAL, false);
                mItemTopViewHolder.recyclerView.setLayoutManager(manager);
                mItemTopViewHolder.recyclerView.enableLoadMore();
                mItemTopViewHolder.recyclerView.setOnLoadMoreListener(new LoadMoreRecyclerView.OnLoadMoreListener() {
                    @Override
                    public void loadMore(LoadMoreRecyclerView recyclerView, int lastPosition) {
                        fetchTop(lastPosition);
                    }
                });

                mItemTopViewHolder.recyclerView.setAdapter(getHorizontalAdapter());
                return mItemTopViewHolder;
            } else if (viewType == VIEW_TYPE_NORMAL) {
                View view = inflater.inflate(R.layout.item_vertical, parent, false);
                return new ItemViewHolder(view);
            }
            return null;
        }

        @Override
        public void onBindNormalViewHolder(RecyclerView.ViewHolder viewHolder,
                int position) {
            if (viewHolder instanceof ItemViewHolder) {
                ItemViewHolder holder = (ItemViewHolder) viewHolder;
                Item item = getDataItem(position);

                holder.textTitle.setText(item.title);

                holder.image.setImage(Glide.with(holder.image.getContext()).load(item.image.url),
                        (float) item.image.width / (float) item.image.height);
            }
        }

        @Override
        public int getNormalItemCount() {
            return (getHorizontalAdapter().getItemCount() > 0 ? 1 : 0) + dataList.size();
        }

        @Override
        public int getNormalItemViewType(int position) {
            if (getHorizontalAdapter().getItemCount() > 0 && position == 0) {
                return VIEW_TYPE_TOP;
            }
            return VIEW_TYPE_NORMAL;
        }

        public Item getDataItem(int position) {
            return dataList.get(position - (getHorizontalAdapter().getItemCount() > 0 ? 1 : 0));
        }

    }

    class HorizontalAdapter extends LoadMoreRecyclerView.Adapter<RecyclerView.ViewHolder> {
        private List<Item> dataList;

        public HorizontalAdapter() {
            dataList = new ArrayList<>();
        }

        @Override
        public RecyclerView.ViewHolder onCreateNormalViewHolder(LayoutInflater inflater,
                ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.item_horizontal, parent, false);
            return new HorizontalItemViewHolder(view);
        }

        @Override
        public void onBindNormalViewHolder(RecyclerView.ViewHolder viewHolder,
                int position) {
            Item item = getItem(position);
            HorizontalItemViewHolder holder = (HorizontalItemViewHolder) viewHolder;
            holder.textTitle.setText(item.title);
            Glide.with(holder.image.getContext()).load(item.image.url).into(holder.image);
        }

        @Override
        public int getNormalItemCount() {
            return dataList.size();
        }

        public void addAll(List<Item> items) {
            synchronized (this) {
                this.dataList.addAll(items);
                notifyItemRangeInserted(getNormalItemCount() - items.size(), items.size());
            }

        }

        public void clear() {
            synchronized (this) {
                this.dataList.clear();
                notifyDataSetChanged();
            }
        }

        public Item getItem(int position) {
            synchronized (this) {
                return dataList.get(position);
            }
        }

        @Override
        public boolean displayEmpty() {
            return dataList.size() == 0;
        }
    }

    class ItemTopViewHolder extends RecyclerView.ViewHolder {

        LoadMoreRecyclerView recyclerView;

        public ItemTopViewHolder(View itemView) {
            super(itemView);
            recyclerView = (LoadMoreRecyclerView) itemView.findViewById(R.id.recycler_view);
        }
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {

        AppCompatTextView textTitle;
        DynamicHeightImageView image;

        public ItemViewHolder(View itemView) {
            super(itemView);
            textTitle = (AppCompatTextView) itemView.findViewById(R.id.text_title);
            image = (DynamicHeightImageView) itemView.findViewById(R.id.image);

            textTitle.setGravity(GravityCompat.START | Gravity.TOP);
        }
    }

    class HorizontalItemViewHolder extends RecyclerView.ViewHolder {

        AppCompatTextView textTitle;
        AppCompatImageView image;

        public HorizontalItemViewHolder(View itemView) {
            super(itemView);
            textTitle = (AppCompatTextView) itemView.findViewById(R.id.text_title);
            image = (AppCompatImageView) itemView.findViewById(R.id.image);

            textTitle.setGravity(GravityCompat.START | Gravity.TOP);
        }
    }
}
