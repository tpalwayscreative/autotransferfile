package delfi.com.vn.autotransferfile.service;
import android.app.Application;
import android.os.Environment;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.snatik.storage.Storage;

import net.gotev.uploadservice.BuildConfig;
import net.gotev.uploadservice.UploadService;
import net.gotev.uploadservice.okhttp.OkHttpStack;

import java.util.ArrayList;
import java.util.List;

import delfi.com.vn.autotransferfile.Constant;
import delfi.com.vn.autotransferfile.common.PermissionUtils;
import delfi.com.vn.autotransferfile.common.utils.FileUtil;
import delfi.com.vn.autotransferfile.common.utils.SharePreferencesFile;
import delfi.com.vn.autotransferfile.model.CAuToUpload;
import delfi.com.vn.autotransferfile.service.broadcastreceiver.ConnectivityReceiver;

/**
 * Created by PC on 8/29/2017.
 */

public class AutoApplication extends Application {
    public static final String TAG = AutoApplication.class.getSimpleName();
    private Storage storage ;
    private static AutoApplication mInstance;
    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        UploadService.NAMESPACE = BuildConfig.APPLICATION_ID;
        UploadService.HTTP_STACK = new OkHttpStack();
        FirebaseMessaging.getInstance().subscribeToTopic("news");
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
}

