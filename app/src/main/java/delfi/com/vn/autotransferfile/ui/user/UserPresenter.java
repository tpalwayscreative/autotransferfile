package delfi.com.vn.autotransferfile.ui.user;
import android.app.Activity;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import delfi.com.vn.autotransferfile.BuildConfig;
import delfi.com.vn.autotransferfile.R;
import delfi.com.vn.autotransferfile.common.api.ServerAPI;
import delfi.com.vn.autotransferfile.common.api.request.UserRequest;
import delfi.com.vn.autotransferfile.common.api.request.UserRequestOption;
import delfi.com.vn.autotransferfile.common.utils.NetworkUtil;
import delfi.com.vn.autotransferfile.model.CUser;
import dk.delfi.core.Dependencies;
import dk.delfi.core.common.controller.RealmController;
import dk.delfi.core.common.presenter.Presenter;
import okhttp3.ResponseBody;
import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by PC on 9/1/2017.
 */

public class UserPresenter extends Presenter<UserView> implements Dependencies.DependenciesListener,RealmController.RealmControllerListener<CUser>{

    private Activity activity;
    private ServerAPI serverAPI ;
    public static final String TAG= UserPresenter.class.getSimpleName();
    private String author = null ;
    private RealmController instance ;

    public UserPresenter(Activity activity){
        this.activity = activity;
        Dependencies dependencies = Dependencies.getsInstance(activity.getApplicationContext(), BuildConfig.BASE_URL_API);
        dependencies.dependenciesListener(this);
        dependencies.init();
        serverAPI = (ServerAPI) Dependencies.serverAPI;
        instance = RealmController.with(this);
    }



    @Override
    public void onShowRealmObject(CUser cUser) {
        Log.d(TAG,"ShowRealmObject : "+new Gson().toJson(cUser));
        if (cUser != null){
            this.author = cUser.apiKey;
        }
    }

    public void userRegister(UserRequestOption userRequest) {
        UserView view = view();
        if (view == null) {
            return;
        }
        if (NetworkUtil.pingIpAddress(view.getContext())) {
            view.onShowError(view.getContext().getString(R.string.tvCheckNetwork));
            return;
        }
        subscriptions.add(serverAPI.signUp(userRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(() -> {
                    Log.d(TAG,"action 0");
                    view.onShowLoading();
                })
                .subscribe(onResponseUser -> {
                    Log.d(TAG,"Login" + new Gson().toJson(onResponseUser));
                    view.onHideLoading();
                }, throwable -> {
                    view.onHideLoading();
                    if (throwable instanceof HttpException) {
                        ResponseBody body = ((HttpException) throwable).response().errorBody();
                        try {
                            JSONObject object = new JSONObject(body.string());
                            view.onShowError(object.getString("error_message"));
                        } catch (JSONException e) {
                            view.onShowError(throwable.getMessage());
                            e.printStackTrace();
                        } catch (IOException e) {
                            view.onShowError(throwable.getMessage());
                            e.printStackTrace();
                        }
                    }else {
                        Log.d(TAG,"action 2");
                        view.onShowError(throwable.getMessage());
                    }
                }));
    }


    @Override
    public void onShowRealmList(List<CUser> list) {

    }

    @Override
    public void onShowRealmCheck(boolean b) {

    }

    @Override
    public void onShowRealmQueryItem(CUser cUser) {

    }

    @Override
    public void onRealmUpdated(CUser cUser) {

    }

    @Override
    public void onRealmDeleted(boolean b) {

    }

    @Override
    public void onRealmInserted(CUser cUser) {

    }

    @Override
    public String onAuthorToken() {
        return this.author;
    }

    @Override
    public Class onObject() {
        return ServerAPI.class;
    }

}
