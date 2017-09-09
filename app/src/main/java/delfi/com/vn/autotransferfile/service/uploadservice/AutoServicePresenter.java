package delfi.com.vn.autotransferfile.service.uploadservice;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.google.gson.Gson;
import com.snatik.storage.Storage;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.ListUtils;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import delfi.com.vn.autotransferfile.BuildConfig;
import delfi.com.vn.autotransferfile.Constant;
import delfi.com.vn.autotransferfile.R;
import delfi.com.vn.autotransferfile.common.api.ServerAPI;
import delfi.com.vn.autotransferfile.common.utils.NetworkUtil;
import delfi.com.vn.autotransferfile.model.CAutoFileOffice;
import delfi.com.vn.autotransferfile.model.CFileDocument;
import delfi.com.vn.autotransferfile.model.CFolder;
import delfi.com.vn.autotransferfile.model.CUser;
import delfi.com.vn.autotransferfile.service.AutoService;
import delfi.com.vn.autotransferfile.ui.autodetail.AutoDetailPresenter;
import delfi.com.vn.autotransferfile.ui.autoupload.AutoUploadView;
import delfi.com.vn.autotransferfile.ui.user.UserView;
import dk.delfi.core.Dependencies;
import dk.delfi.core.common.controller.RealmController;
import dk.delfi.core.common.presenter.Presenter;
import okhttp3.ResponseBody;
import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by PC on 9/6/2017.
 */

public class AutoServicePresenter extends Presenter<AutoServiceView> implements RealmController.RealmControllerListener,Dependencies.DependenciesListener{

    private Context context ;
    private ServerAPI serverAPI ;
    private List<CFileDocument>fileDocumentList;
    private List<CFolder> listFolder ;
    private CUser cUser;
    private RealmController realmController ;
    private String folder_name;
    private String author = null ;
    private Storage storage ;
    private boolean isUploading = false;
    private int countUploading = 0 ;
    private String file_name ;


    private String path_file_name ;
    private List<FileObserverService> listObserver;
    public static final String TAG = AutoServicePresenter.class.getSimpleName();

    public AutoServicePresenter(Context context){
        this.context = context;
        Dependencies dependencies = Dependencies.getsInstance(context.getApplicationContext(), BuildConfig.BASE_URL_API);
        dependencies.dependenciesListener(this);
        dependencies.init();
        fileDocumentList = new ArrayList<>();
        listFolder = new ArrayList<>();
        listObserver = new ArrayList<>();
        serverAPI = (ServerAPI) Dependencies.serverAPI;
        realmController = RealmController.with(this);

        cUser = (CUser) realmController.getFirstItem(CUser.class);
        if (cUser!=null){
            this.author = cUser.apiKey;
        }
        listFolder = realmController.getALLObject(CFolder.class);
        if (listFolder!=null){
            for (CFolder index : listFolder){
                listObserver.add(new FileObserverService(index.path_folder_name,index.folder_name));
            }
        }
        storage = new Storage(context);
    }

    public void getAllFile(){

        AutoServiceView view = view();
        if (view == null) {
            return;
        }
        if (NetworkUtil.pingIpAddress(view.getContext())) {
            return;
        }
        subscriptions.add(serverAPI.getALLFile()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(() -> {
                    fileDocumentList = realmController.getALLObject(CFileDocument.class);
                    if (fileDocumentList==null){
                        fileDocumentList = new ArrayList<>();
                    }
                    Log.d(TAG,"action 0");
                })
                .subscribe(onResponse -> {
                    Log.d(TAG,"list from internet : "+ new Gson().toJson(onResponse.files));
                    Log.d(TAG,"list from local : "+ new Gson().toJson(fileDocumentList));
                    fileDocumentList = onCompare(onResponse.files,fileDocumentList);
                    Log.d(TAG,"You need to add more : "+ new Gson().toJson(fileDocumentList));
                    if (fileDocumentList.size()>0){
                        view.onDownLoadNow();
                    }
                }, throwable -> {
                    if (throwable instanceof HttpException) {
                        ResponseBody body = ((HttpException) throwable).response().errorBody();
                        try {
                            JSONObject object = new JSONObject(body.string());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }else {
                        Log.d(TAG,context.getString(R.string.tv_issue));
                    }
                }));
    }

    public List<CFileDocument> onCompare(List<CFileDocument>current, List<CFileDocument>previous){
        List<CFileDocument> list = new ArrayList<>();
        for (CFileDocument index : current) {
            boolean found = false;
            for (CFileDocument person1 : previous) {
                if (index.file_document_id == person1.file_document_id) {
                    found = true;
                }
            }
            if (!found) {
                list.add(index);
            }
        }
        return  list;
    }



    @Override
    public void onShowRealmObject(Object object,int code) {
        if (object instanceof CUser){
            object = realmController.getRealm().copyFromRealm((CUser)object);
            Log.d(TAG,"on Show Realm Object :" + new Gson().toJson(object));
            this.device_id = ((CUser) object).device_id;
            this.author =  ((CUser) object).apiKey;
        }
    }

    @Override
    public void onShowRealmList(List list,int code) {
        if (list!=null && !list.isEmpty()){
            if (list.get(0) instanceof CFileDocument){
                fileDocumentList = realmController.getRealm().copyFromRealm(list);
                Log.d(TAG,"File is existing in local data : "+ new Gson().toJson(this.fileDocumentList));
            }
            else if (list.get(0) instanceof CFolder){
                listFolder = realmController.getRealm().copyFromRealm(list);
                for (CFolder index : listFolder){
                    listObserver.add(new FileObserverService(index.path_folder_name,index.folder_name));
                }
                Log.d(TAG,"List Folder : "+ new Gson().toJson(listFolder));
            }
        }
    }



    @Override
    public String onAuthorToken() {
        return this.author;
    }

    @Override
    public Class onObject() {
        return ServerAPI.class;
    }

    public Context getContext() {
        return context;
    }

    @Override
    public void onShowRealmCheck(boolean b,int code) {

    }

    @Override
    public void onShowRealmQueryItem(Object o,int code) {

    }



    @Override
    public void onShowRealmQueryItem(Context context, Object o, HashMap hashMap, int i) {
        AutoServiceView view = view();
        if (o==null){
            view.onUploadNow(context,hashMap);
            Log.d(TAG,"Query item here work here");
        }
    }

    public void onShowDataHashMap(Context context,HashMap hashMap){
        AutoServiceView view = view();
        view.onUploadNow(context,hashMap);
    }

    public void onUpload(){
        AutoServiceView view = view();
        view.onUploadNow(getContext(),new HashMap<>());
    }

    @Override
    public void onRealmUpdated(Object o,int code) {

    }

    public void onShowDataOffice(List<CAutoFileOffice>list){
        AutoServiceView view = view();
        view.onDataOffice(list);
    }

    @Override
    public void onRealmDeleted(boolean b,int code) {

    }

    @Override
    public void onRealmInserted(Object o,int code) {
        if (code== Constant.TAG_CODE_UPLOAD && !isUploading){
            Log.d(TAG,"show finish here");
            getAllFile();
        }
    }

    @Override
    public void onRealmInsertedList(List list,int code) {
        AutoServiceView view = view();
        if (list != null && !list.isEmpty()){
            if (list.get(0) instanceof CFileDocument){
                List<CFileDocument> list1= realmController.getRealm().copyFromRealm(list);
                if (code==1){
                    this.fileDocumentList = list1;
                   // view.onDownLoadNow();
                }
                Log.d(TAG,"Show inserted list : "+ new Gson().toJson(list1));
            }
        }
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    private String device_id ;

    public String getFolder_name() {
        return folder_name;
    }

    public void setFolder_name(String folder_name) {
        this.folder_name = folder_name;
    }

    public List<CFileDocument> getFileDocumentList() {
        return fileDocumentList;
    }

    public void setFileDocumentList(List<CFileDocument> fileDocumentList) {
        this.fileDocumentList = fileDocumentList;
    }

    public RealmController getRealmController() {
        return realmController;
    }

    public void setRealmController(RealmController realmController) {
        this.realmController = realmController;
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

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public boolean isUploading() {
        return isUploading;
    }

    public void setUploading(boolean uploading) {
        isUploading = uploading;
    }


    public List<FileObserverService> getListObserver() {
        return listObserver;
    }

    public void setListObserver(List<FileObserverService> listObserver) {
        this.listObserver = listObserver;
    }

    public String getPath_file_name() {
        return path_file_name;
    }

    public void setPath_file_name(String path_file_name) {
        this.path_file_name = path_file_name;
    }

    public int getCountUploading() {
        return countUploading;
    }

    public void setCountUploading(int countUploading) {
        this.countUploading = countUploading;
    }






}
