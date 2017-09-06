package delfi.com.vn.autotransferfile.service;
import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
//import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.snatik.storage.Storage;

import net.gotev.uploadservice.BuildConfig;
import net.gotev.uploadservice.UploadService;
import net.gotev.uploadservice.okhttp.OkHttpStack;
import java.util.ArrayList;
import java.util.List;
import delfi.com.vn.autotransferfile.Constant;
import delfi.com.vn.autotransferfile.common.PermissionUtils;
import delfi.com.vn.autotransferfile.common.application.BaseApplication;
import delfi.com.vn.autotransferfile.common.utils.FileUtil;
import delfi.com.vn.autotransferfile.common.utils.SharePreferencesFile;
import delfi.com.vn.autotransferfile.model.CAuToUpload;
import delfi.com.vn.autotransferfile.model.CFileDocument;
import delfi.com.vn.autotransferfile.service.broadcastreceiver.ConnectivityReceiver;
import delfi.com.vn.autotransferfile.service.downloadservice.DownloadService;
import dk.delfi.core.common.controller.RealmController;

/**
 * Created by PC on 8/29/2017.
 */

public class AutoApplication extends BaseApplication implements Application.ActivityLifecycleCallbacks,RealmController.RealmControllerListener<CFileDocument> {
    public static final String TAG = AutoApplication.class.getSimpleName();
    private Storage storage ;
    private static AutoApplication mInstance;
    private RealmController realms ;
    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        UploadService.NAMESPACE = BuildConfig.APPLICATION_ID;
        UploadService.HTTP_STACK = new OkHttpStack();
        realms = RealmController.with(this);
        initDownloadData();

        if (!FileUtil.mCheckFileExisting(getApplicationContext(),Constant.LIST_FILE)){
            initData();
            FileUtil.mCreateAndSaveFile(this,Constant.LIST_FILE_OFFICE,new Gson().toJson(new ArrayList<>()));
        }
    }

    public static synchronized AutoApplication getInstance() {
        return mInstance;
    }

    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }

    public void initData(){
        storage = new Storage(getApplicationContext());
        List<CAuToUpload>list = new ArrayList<>();
        if (!storage.isDirectoryExists(storage.getExternalStorageDirectory(Environment.DIRECTORY_DCIM)+"/Camera")){
            storage.createDirectory(storage.getExternalStorageDirectory(Environment.DIRECTORY_DCIM)+"/Camera");
        }
        list.add(new CAuToUpload(storage.getExternalStorageDirectory(Environment.DIRECTORY_DCIM)+"/Camera","Camera",false));

        if (!storage.isDirectoryExists(storage.getExternalStorageDirectory(Environment.DIRECTORY_PICTURES))){
            storage.createDirectory(storage.getExternalStorageDirectory(Environment.DIRECTORY_PICTURES));
        }
        list.add(new CAuToUpload(storage.getExternalStorageDirectory(Environment.DIRECTORY_PICTURES),"Picture",false));

        if (!storage.isDirectoryExists(storage.getExternalStorageDirectory(Environment.DIRECTORY_DOWNLOADS))){
            storage.createDirectory(storage.getExternalStorageDirectory(Environment.DIRECTORY_DOWNLOADS));
        }
        list.add(new CAuToUpload(storage.getExternalStorageDirectory(Environment.DIRECTORY_DOWNLOADS),"Downloads",false));
        FileUtil.mCreateAndSaveFile(getApplicationContext(),Constant.LIST_FILE,new Gson().toJson(list));
    }

    public void initDownloadData(){

        CFileDocument fileDocument = new CFileDocument();
        fileDocument.device_id = FileUtil.getMacAddress();
        fileDocument.folder_id = 1;
        fileDocument.group_id = 001;
        fileDocument.status = 0;
        fileDocument.file_document_id = 002;
        fileDocument.file_name = "20170831_175247.jpg";
        fileDocument.created_date = "05/09/2017";
        fileDocument.updated_date = "05/09/2017";
        realms.mInsertObject(fileDocument);

        fileDocument.device_id = FileUtil.getMacAddress();
        fileDocument.folder_id = 2;
        fileDocument.group_id = 002;
        fileDocument.status = 0;
        fileDocument.file_document_id = 003;
        fileDocument.file_name = "20170831_175332.jpg";
        fileDocument.created_date = "05/09/2017";
        fileDocument.updated_date = "05/09/2017";
        realms.mInsertObject(fileDocument);

    }

    @Override
    public void onShowRealmObject(CFileDocument cFileDocument) {

    }

    @Override
    public void onShowRealmList(List<CFileDocument> list) {

    }

    @Override
    public void onShowRealmCheck(boolean b) {

    }

    @Override
    public void onShowRealmQueryItem(CFileDocument cFileDocument) {

    }

    @Override
    public void onRealmUpdated(CFileDocument cFileDocument) {

    }

    @Override
    public void onRealmDeleted(boolean b) {

    }

    @Override
    public void onRealmInserted(CFileDocument cFileDocument) {
        CFileDocument fileDocument = realms.getRealm().copyFromRealm(cFileDocument);
        Log.d(TAG,"Inserted successfully: " + new Gson().toJson(fileDocument));
    }
}

