//package delfi.com.vn.autotransferfile.common.presenter;
//
//import android.support.annotation.CallSuper;
//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
//import android.util.Log;
//
//import rx.subscriptions.CompositeSubscription;
//
///**
// * Created by PC on 7/4/2017.
// */
//
//public class Presenter<V> {
//
//    @Nullable
//    private volatile V view;
//
//    protected CompositeSubscription subscriptions;
//
//    @CallSuper
//    public void bindView(@NonNull V view) {
//        this.view = view;
//        this.subscriptions = new CompositeSubscription();
//    }
//
//    @Nullable
//    protected V view() {
//        return view;
//    }
//
//    @Nullable
//    protected void showLog(String tag,String name){
//        Log.d(tag,name);
//    }
//    @CallSuper
//    private void unbindView(@NonNull V view) {
//        if (subscriptions != null) {
//            if (subscriptions.isUnsubscribed()) {
//                subscriptions.unsubscribe();
//            }
//            if (subscriptions.hasSubscriptions()) {
//                subscriptions.clear();
//            }
//            subscriptions = null;
//        }
//
//        this.view = null;
//    }
//
//    @CallSuper
//    public void unbindView() {
//        unbindView(view);
//    }
//}
