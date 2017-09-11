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
import delfi.com.vn.autotransferfile.common.api.request.FileDocumentRequest;
import delfi.com.vn.autotransferfile.common.api.request.UserRequest;
import delfi.com.vn.autotransferfile.common.application.BaseApplication;
import delfi.com.vn.autotransferfile.common.utils.FileUtil;
import delfi.com.vn.autotransferfile.common.utils.Navigator;
import delfi.com.vn.autotransferfile.common.utils.NetworkUtil;
import delfi.com.vn.autotransferfile.model.CAuToUpload;
import delfi.com.vn.autotransferfile.model.CFileDocument;
import delfi.com.vn.autotransferfile.model.CFolder;
import delfi.com.vn.autotransferfile.model.CUser;
import delfi.com.vn.autotransferfile.service.AutoApplication;
import delfi.com.vn.autotransferfile.service.uploadservice.AutoServiceView;
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

public class AutoUploadPresenter extends Presenter<AutoUploadRemote> {

    private List<CFolder> listFolder ;
    private Activity activity ;
    private CUser cUser ;
    private Storage storage;
    private String author = null ;
    private CFileDocument fileDocument ;
    private List<CFileDocument>fileDocumentList;
    private RealmController realmController ;
    public static final String TAG = AutoUploadPresenter.class.getSimpleName();

    public AutoUploadPresenter(Activity activity){
        this.activity = activity;
        listFolder = new ArrayList<>();
        fileDocumentList = new ArrayList<>();
        realmController = RealmController.with();
        cUser = (CUser) realmController.getFirstItem(CUser.class);
        listFolder = realmController.getALLObject(CFolder.class);
        if (listFolder!=null){
            setListFolder(listFolder);
        }
        storage = new Storage(activity);
    }

    public void onShowData(){
        AutoUploadView view = view();
        view.onShowList(listFolder);
    }

    public void onLogout(CUser userRequest){
        AutoUploadRemote view = view();
        if (view==null){
            return;
        }
        if (NetworkUtil.pingIpAddress(view.getContext())){
            view.onShowError(view.getContext().getString(R.string.tvCheckNetwork));
            return;
        }

        subscriptions.add(BaseApplication.serverAPI.logOut(userRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(() -> {
                    view.onShowLoading();
                })
                .subscribe(onResponse -> {
                    Log.d(TAG,"Logout : " + new Gson().toJson(onResponse));
                    realmController.clearAll(CUser.class);
                    realmController.clearAll(CFolder.class);
                    view.onLogoutSuccessfully();
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


    public void onGetListFolder(){
        AutoUploadRemote view = view();
        if (view == null) {
            return;
        }
        if (NetworkUtil.pingIpAddress(view.getContext())) {
            view.onShowError(view.getContext().getString(R.string.tvCheckNetwork));
            return;
        }
        subscriptions.add(BaseApplication.serverAPI.getListFolder()
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


    public boolean isCheckingSynData(){
        CFileDocument cFileDocument = (CFileDocument) realmController.getLatestObject(CFileDocument.class,Constant.TAG_FILE_DOCUMENT_ID);
        if (cFileDocument!=null){
            setFileDocument(cFileDocument);
            return true;
        }
        else{
            setFileDocument(null);
            return false;
        }
    }

    public void getAllFile(){
        AutoUploadView view = view();
        if (view == null) {
            return;
        }
        if (NetworkUtil.pingIpAddress(view.getContext())) {
            return;
        }
        subscriptions.add(BaseApplication.serverAPI.getALLFile()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(() -> {
                    fileDocumentList.clear();
                    Log.d(TAG,"action 0 : " + new Gson().toJson(fileDocumentList) );
                })
                .subscribe(onResponse -> {
                    Log.d(TAG,"list from internet : "+ new Gson().toJson(onResponse.files));
                    fileDocumentList = onResponse.files;
                    Log.d(TAG,"You need to add more : "+ new Gson().toJson(fileDocumentList));
                    if (fileDocumentList.size()>0){
                        view.onDownLoadNow();
                    }
                }, throwable -> {
                    if (throwable instanceof HttpException) {
                        ResponseBody body = ((HttpException) throwable).response().errorBody();
                        try {
                            JSONObject object = new JSONObject(body.string());
                            Log.d(TAG,new Gson().toJson(object));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }else {
                        Log.d(TAG,view.getContext().getString(R.string.tv_issue));
                    }
                }));
    }

    public void getLatestFile(FileDocumentRequest request){
        AutoUploadView view = view();
        if (view == null) {
            return;
        }
        if (NetworkUtil.pingIpAddress(view.getContext())) {
            return;
        }

        subscriptions.add(BaseApplication.serverAPI.getLatestFile(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(() -> {
                    Log.d(TAG,"file document : " + request.file_document_id);
                    fileDocumentList.clear();
                    Log.d(TAG,"action 0 filter : " + new Gson().toJson(fileDocumentList));
                })
                .subscribe(onResponse -> {
                    Log.d(TAG,"list from internet filter : "+ new Gson().toJson(onResponse.files));
                    if (onResponse.files!=null){
                        fileDocumentList = onResponse.files;
                        Log.d(TAG,"You need to add more : "+ new Gson().toJson(fileDocumentList));
                        if (onResponse.files.size()>0){
                            view.onDownLoadNow();
                        }
                    }
                }, throwable -> {
                    if (throwable instanceof HttpException) {
                        ResponseBody body = ((HttpException) throwable).response().errorBody();
                        try {
                            JSONObject object = new JSONObject(body.string());
                            Log.d(TAG,new Gson().toJson(object));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }else {
                        Log.d(TAG,view.getContext().getString(R.string.tv_issue));
                    }
                }));
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

    public CFileDocument getFileDocument() {
        return fileDocument;
    }

    public void setFileDocument(CFileDocument fileDocument) {
        this.fileDocument = fileDocument;
    }

    public List<CFileDocument> getFileDocumentList() {
        return fileDocumentList;
    }

    public void setFileDocumentList(List<CFileDocument> fileDocumentList) {
        this.fileDocumentList = fileDocumentList;
    }

    public CUser getcUser() {
        return cUser;
    }

    public void setcUser(CUser cUser) {
        this.cUser = cUser;
    }


}
