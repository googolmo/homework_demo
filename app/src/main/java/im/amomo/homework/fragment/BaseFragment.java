package im.amomo.homework.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import rx.Subscription;

/**
 * Project - HomeWork_Demo
 * Created by Moyw on 11/8/15.
 */
public class BaseFragment extends Fragment {
    private List<WeakReference<Subscription>> mSubscriptionList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSubscriptionList = new ArrayList<>();
    }

    @Override
    public void onStop() {

        super.onStop();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        for (WeakReference<Subscription> subscription : mSubscriptionList) {
            if (subscription != null && subscription.get() != null &&
                    !subscription.get().isUnsubscribed())
                subscription.get().unsubscribe();
        }
        mSubscriptionList.clear();
    }

    public void addSubscription(Subscription subscription) {
        mSubscriptionList.add(new WeakReference<>(subscription));
    }
}
