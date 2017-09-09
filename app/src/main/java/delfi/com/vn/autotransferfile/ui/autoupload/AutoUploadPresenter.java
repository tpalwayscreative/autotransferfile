package delfi.com.vn.autotransferfile.ui.autoupload;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.snatik.storage.Storage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import delfi.com.vn.autotransferfile.BuildConfig;
import delfi.com.vn.autotransferfile.Constant;
import delfi.com.vn.autotransferfile.R;
import delfi.com.vn.autotransferfile.common.api.ServerAPI;
import delfi.com.vn.autotransferfile.common.utils.FileUtil;
import delfi.com.vn.autotransferfile.common.utils.NetworkUtil;
import delfi.com.vn.autotransferfile.model.CAuToUpload;
import delfi.com.vn.autotransferfile.model.CFolder;
import delfi.com.vn.autotransferfile.model.CUser;
import delfi.com.vn.autotransferfile.ui.user.UserPresenter;
import delfi.com.vn.autotransferfile.ui.user.UserView;
import dk.delfi.core.Dependencies;
import dk.delfi.core.common.controller.RealmController;
import dk.delfi.core.common.presenter.Presenter;
import okhttp3.ResponseBody;
import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by PC on 8/30/2017.
 */

public class AutoUploadPresenter extends Presenter<AutoUploadRemote> implements RealmController.RealmControllerListener,Dependencies.DependenciesListener {

    private List<CFolder> listFolder ;
    private Activity activity ;
    private Storage storage;
    private ServerAPI serverAPI ;
    private String author = null ;



    private RealmController realmController ;
    public static final String TAG = AutoUploadPresenter.class.getSimpleName();

    public AutoUploadPresenter(Activity activity){
        this.activity = activity;
        listFolder = new ArrayList<>();
        Dependencies dependencies = Dependencies.getsInstance(activity.getApplicationContext(), BuildConfig.BASE_URL_API);
        dependencies.dependenciesListener(this);
        dependencies.init();
        serverAPI = (ServerAPI) Dependencies.serverAPI;
        realmController = RealmController.with(this);
        CUser cUser = (CUser) realmController.getFirstItem(CUser.class);
        if (cUser!=null){
            author = cUser.apiKey;
        }
        storage = new Storage(activity);
    }


    public void onGetListFolder(){
        AutoUploadRemote view = view();
        if (view == null) {
            return;
        }
        if (NetworkUtil.pingIpAddress(view.getContext())) {
            view.onShowError(view.getContext().getString(R.string.tvCheckNetwork));
            return;
        }
        subscriptions.add(serverAPI.getListFolder()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(() -> {
                    view.onShowLoading();
                })
                .subscribe(onResponse -> {
                    realmController.clearAll(CFolder.class,0);
                    listFolder = realmController.mInsertList(CFolder.class,onResponse.folder);
                    setListFolder(listFolder);
                    view.onShowList(listFolder);
                    Log.d(TAG,"show all folder : " + new Gson().toJson(onResponse));
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
    public Class onObject() {
        return ServerAPI.class;
    }

    @Override
    public String onAuthorToken() {
        return this.author;
    }

    @Override
    public void onShowRealmObject(Object o, int i) {
        if (o != null && o instanceof CUser){
            CUser cUser1  = realmController.getRealm().copyFromRealm((CUser) o);
            this.author = cUser1.apiKey;
        }
    }

    @Override
    public void onShowRealmList(List list, int i) {
        AutoUploadView view = view();
        if (list != null && !list.isEmpty()){
            if (list.get(0) instanceof CFolder){
                List<CFolder> list1 = realmController.getRealm().copyFromRealm(list);
                setListFolder(list1);
                view.onShowList(list1);
            }
            Log.d(TAG,"running 1");
        }
        else{
            Log.d(TAG,"running 2");
            onGetListFolder();
        }
    }

    @Override
    public void onShowRealmCheck(boolean b, int i) {

    }

    @Override
    public void onShowRealmQueryItem(Object o, int i) {

    }

    @Override
    public void onRealmUpdated(Object o, int i) {

    }

    @Override
    public void onRealmDeleted(boolean b, int i) {

    }

    @Override
    public void onRealmInserted(Object o, int i) {

    }

    @Override
    public void onRealmInsertedList(List list, int i) {

    }

    @Override
    public void onShowRealmQueryItem(Context context, Object o, HashMap hashMap, int i) {

    }

    public Storage getStorage() {
        return storage;
    }

    public void setStorage(Storage storage) {
        this.storage = storage;
    }

    public List<CFolder> getListFolder() {
        return listFolder;
    }

    public void setListFolder(List<CFolder> listFolder) {
        this.listFolder = listFolder;
    }

    public RealmController getRealmController() {
        return realmController;
    }

    public void setRealmController(RealmController realmController) {
        this.realmController = realmController;
    }

}
