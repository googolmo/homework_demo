package im.amomo.homework.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageButton;
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
import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;

import java.io.IOException;
import java.io.InputStreamReader;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import im.amomo.homework.BuildConfig;
import im.amomo.homework.R;
import im.amomo.homework.database.DatabaseHelper;
import im.amomo.homework.model.Item;
import im.amomo.homework.model.Items;
import im.amomo.homework.util.GsonHelper;
import im.amomo.homework.widget.AdView;
import im.amomo.homework.widget.DividerItemDecoration;
import im.amomo.homework.widget.DynamicView;
import im.amomo.homework.widget.LoadMoreRecyclerView;
import im.amomo.homework.widget.UltimateRecyclerView;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;


public class HotFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = HotFragment.class.getSimpleName();
    private static final int AD_FREQUENCY = 15;

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
                fetchHot(lastPosition);
            }
        });

        mRecyclerView.showEmptyView();

    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        displayHotFromDatabase();
    }

    @Override
    public void onRefresh() {
        fetchHot(0);
    }

    /**
     * fetch hot list data
     *
     * @param sinceId item id let backend knows which one should as the first
     */
    private void fetchHot(final long sinceId) {

        if (BuildConfig.DEBUG) {
            Log.d(TAG, "fetch Hot");
        }

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
                        saveListToDatabase(Items.TABLES.HOT, items.data);
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
                        saveListToDatabase(Items.TABLES.TOP, items.data);
                    }
                }));

    }

    // region database

    private void displayHotFromDatabase() {
        SqlBrite sqlBrite = SqlBrite.create();
        addSubscription(Items.query(sqlBrite.wrapDatabaseHelper(DatabaseHelper.getInstance()), Items.TABLES.HOT)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<Item>>() {
                    @Override
                    public void call(List<Item> items) {
                        if (items.size() == 0) {
                            fetchHot(0);
                        } else {
                            mAdapter.addAll(items);
                            displayTopFromDatabase();
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.e(TAG, "query hot by db error!", throwable);
                        fetchHot(0);
                    }
                }));
    }

    private void displayTopFromDatabase() {
        SqlBrite brite = SqlBrite.create();
        Subscription subscription = Items.query(brite.wrapDatabaseHelper(DatabaseHelper.getInstance()), Items.TABLES.TOP)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<Item>>() {
                    @Override
                    public void call(List<Item> items) {
                        if (items.size() == 0) {
                            fetchTop(0);
                        } else {
                            mAdapter.addTopData(items);
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.e(TAG, "query top by db error", throwable);
                        fetchTop(0);
                    }
                });
        addSubscription(subscription);
    }

    private void saveListToDatabase(@Items.TABLE final String table, final List<Item> list) {
        Subscription subscription = rx.Observable
                .create(new Observable.OnSubscribe<Boolean>() {
                    @Override
                    public void call(Subscriber<? super Boolean> subscriber) {
                        if (subscriber.isUnsubscribed()) {
                            subscriber.onCompleted();
                            return;
                        }

                        BriteDatabase db = SqlBrite.create()
                                .wrapDatabaseHelper(DatabaseHelper.getInstance());

                        try {
                            Items.clear(db, table);
                            Items.save(db, table, list);
                            if (!subscriber.isUnsubscribed()) {
                                subscriber.onNext(true);
                            }
                            db.close();
                        } catch (Exception e) {
                            subscriber.onError(e);
                        }
                        if (!subscriber.isUnsubscribed()) {
                            subscriber.onCompleted();
                        }
                    }
                })
                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        Log.i(TAG, String.format("save %1$s to database successful", table));
                        trimItemTable();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.e(TAG, String.format("save %1$s to database failed", table), throwable);
                    }
                });
    }

    private static void trimItemTable() {
        rx.Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                try {
                    Items.trimDatabase(SqlBrite.create().wrapDatabaseHelper(DatabaseHelper.getInstance()));
                    subscriber.onNext(true);
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        }).observeOn(Schedulers.io())
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        Log.i(TAG, "Table Item trim complete");
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.e(TAG, "Table Item trim failed", throwable);
                    }
                });
    }

    // endregion


    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    interface ViewTypes {
        int TYPE_TOP = 0x0000;
        int TYPE_NORMAL = 0x0001;
        int TYPE_AD = 0x0010;
        int TYPE_LARGE_IMAGE = 0x0011;
    }

    class VerticalAdapter extends LoadMoreRecyclerView.Adapter<RecyclerView.ViewHolder> {

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
                int adCount = getAdCount();
                dataList.addAll(list);
                adCount = getAdCount() - adCount;
                notifyItemRangeInserted(getNormalItemCount() - adCount - list.size(), list.size() + adCount);
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
            if (viewType == ViewTypes.TYPE_TOP) {
                mTopView = inflater.inflate(R.layout.item_vertical_top, parent, false);
                mItemTopViewHolder = new ItemTopViewHolder(mTopView);
                RecyclerView.LayoutManager manager = new LinearLayoutManager(parent.getContext(),
                        LinearLayoutManager.HORIZONTAL, false);
                mItemTopViewHolder.recyclerView.setLayoutManager(manager);
                mItemTopViewHolder.recyclerView.enableLoadMore();
                mItemTopViewHolder.recyclerView.setOnLoadMoreListener(
                        new LoadMoreRecyclerView.OnLoadMoreListener() {
                            @Override
                            public void loadMore(LoadMoreRecyclerView recyclerView,
                                    int lastPosition) {
                                fetchTop(lastPosition);
                            }
                        });

                mItemTopViewHolder.recyclerView.setAdapter(getHorizontalAdapter());
                return mItemTopViewHolder;
            } else if (viewType == ViewTypes.TYPE_NORMAL) {
                View view = inflater.inflate(R.layout.item_vertical, parent, false);
                return new ItemViewHolder(view);
            } else if (viewType == ViewTypes.TYPE_LARGE_IMAGE) {
                View view = inflater.inflate(R.layout.item_vertical_large_image, parent, false);
                return new ItemViewHolder(view);
            } else if (viewType == ViewTypes.TYPE_AD) {
                View view = inflater.inflate(R.layout.item_ad, parent, false);
                return new AdViewHolder(view);
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

                holder.image.setImage(item.image);

                holder.textInfo.setText(getString(R.string.item_info,
                        NumberFormat.getInstance().format(item.points),
                        NumberFormat.getInstance().format(item.comments)));
                holder.actionShare.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //TODO click share
                    }
                });
                holder.actionUp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //TODO action up
                    }
                });
                holder.actionDown.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //TODO action down
                    }
                });
                holder.actionComment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //TODO action comment
                    }
                });
            } else if (viewHolder instanceof AdViewHolder) {
                AdViewHolder holder = (AdViewHolder) viewHolder;
                //Do something for AdView
            }
        }

        @Override
        public int getNormalItemCount() {
            return (getHorizontalAdapter().getItemCount() > 0 ? 1 : 0) + dataList.size() + getAdCount();
        }

        @Override
        public int getNormalItemViewType(int position) {
            if (showTop() && position == 0) {
                return ViewTypes.TYPE_TOP;
            }
            if (isAd(position)) {
                return ViewTypes.TYPE_AD;
            }
            if (getDataItem(position).image.height > 2000) {
                return ViewTypes.TYPE_LARGE_IMAGE;
            }
            return ViewTypes.TYPE_NORMAL;
        }

        public int getAdCount() {
            return dataList.size() / AD_FREQUENCY;
        }

        public boolean isAd(int position) {
            position = position - (showTop() ? 1 : 0);
            return position != 0 && position % AD_FREQUENCY == 0;
        }

        public boolean showTop() {
            return getHorizontalAdapter().getItemCount() > 0;
        }

        public Item getDataItem(int position) {
            position = position - (showTop() ? 1 : 0);
            position = position - position / AD_FREQUENCY;
            return dataList.get(position);
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

    static class ItemTopViewHolder extends RecyclerView.ViewHolder {

        LoadMoreRecyclerView recyclerView;

        public ItemTopViewHolder(View itemView) {
            super(itemView);
            recyclerView = (LoadMoreRecyclerView) itemView.findViewById(R.id.recycler_view);
        }
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {

        AppCompatTextView textTitle;
        DynamicView image;
        AppCompatImageButton actionUp;
        AppCompatImageButton actionDown;
        AppCompatImageButton actionComment;
        AppCompatButton actionShare;
        AppCompatTextView textInfo;

        public ItemViewHolder(View itemView) {
            super(itemView);
            textTitle = (AppCompatTextView) itemView.findViewById(R.id.text_title);
            image = (DynamicView) itemView.findViewById(R.id.image);
            textTitle.setGravity(GravityCompat.START | Gravity.TOP);

            actionUp = (AppCompatImageButton) itemView.findViewById(R.id.action_up);
            actionDown = (AppCompatImageButton) itemView.findViewById(R.id.action_down);
            actionComment = (AppCompatImageButton) itemView.findViewById(R.id.action_comment);
            actionShare = (AppCompatButton) itemView.findViewById(R.id.action_share);
            textInfo = (AppCompatTextView) itemView.findViewById(R.id.text_info);
            textInfo.setGravity(GravityCompat.START | Gravity.CENTER);
        }
    }

    static class AdViewHolder extends RecyclerView.ViewHolder {

        AdView adView;

        public AdViewHolder(View itemView) {
            super(itemView);
            adView = (AdView) itemView.findViewById(R.id.ad_view);
        }
    }

    static class HorizontalItemViewHolder extends RecyclerView.ViewHolder {

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
