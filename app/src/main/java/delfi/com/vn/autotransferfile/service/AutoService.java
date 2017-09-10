package delfi.com.vn.autotransferfile.service;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.FileObserver;
import android.os.IBinder;
import android.util.Log;
import com.google.gson.Gson;

import net.gotev.uploadservice.BuildConfig;
import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.ServerResponse;
import net.gotev.uploadservice.UploadInfo;
import net.gotev.uploadservice.UploadNotificationConfig;
import net.gotev.uploadservice.UploadService;
import net.gotev.uploadservice.UploadServiceBroadcastReceiver;
import net.gotev.uploadservice.UploadServiceSingleBroadcastReceiver;
import net.gotev.uploadservice.UploadStatusDelegate;
import net.gotev.uploadservice.okhttp.OkHttpStack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import delfi.com.vn.autotransferfile.Constant;
import delfi.com.vn.autotransferfile.common.api.request.FileDocumentRequest;
import delfi.com.vn.autotransferfile.common.utils.FileUtil;
import delfi.com.vn.autotransferfile.common.utils.NetworkUtil;
import delfi.com.vn.autotransferfile.model.CAuToUpload;
import delfi.com.vn.autotransferfile.model.CAutoFileOffice;
import delfi.com.vn.autotransferfile.model.CFileDocument;
import delfi.com.vn.autotransferfile.model.CFolder;
import delfi.com.vn.autotransferfile.service.broadcastreceiver.ConnectivityReceiver;
import delfi.com.vn.autotransferfile.service.downloadservice.DownloadService;
import delfi.com.vn.autotransferfile.service.downloadservice.Sequence;
import delfi.com.vn.autotransferfile.service.fileobserver.RecursiveFileObserver;
import delfi.com.vn.autotransferfile.service.uploadservice.AutoServicePresenter;
import delfi.com.vn.autotransferfile.service.uploadservice.AutoServiceView;
import delfi.com.vn.autotransferfile.service.uploadservice.FileObserverService;
import delfi.com.vn.autotransferfile.service.uploadservice.SingleUploadBroadcastReceiver;
import dk.delfi.core.common.controller.RealmController;
import io.realm.Realm;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class AutoService extends Service implements ConnectivityReceiver.ConnectivityReceiverListener,AutoServiceView<CFileDocument>,DownloadService.DownLoadServiceListener,FileObserverService.FileObserverServiceListener{

    public static final String TAG = AutoService.class.getSimpleName();
    private List<CAutoFileOffice> listOffice;
    private DownloadService downloadService ;
    private AutoServicePresenter presenter;

    public AutoService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Let it continue running until it is stopped.
       Log.d(TAG,"on Start Command");
        return START_STICKY ;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"Destroy service");
        unregisterReceiver(broadcastReceiver);
    }

    private static String getEventString(int event) {
        switch (event) {
            case  android.os.FileObserver.ACCESS:
                return "ACCESS";
            case android.os.FileObserver.MODIFY:
                return "MODIFY";
            case android.os.FileObserver.ATTRIB:
                return "ATTRIB";
            case android.os.FileObserver.CLOSE_WRITE:
                return "CLOSE_WRITE";
            case android.os.FileObserver.CLOSE_NOWRITE:
                return "CLOSE_NOWRITE";
            case android.os.FileObserver.OPEN:
                return "OPEN";
            case android.os.FileObserver.MOVED_FROM:
                return "MOVED_FROM";
            case android.os.FileObserver.MOVED_TO:
                return "MOVED_TO";
            case FileObserver.CREATE:
                return "CREATE";
            case android.os.FileObserver.DELETE:
                return "DELETE";
            case android.os.FileObserver.DELETE_SELF:
                return "DELETE_SELF";
            case android.os.FileObserver.MOVE_SELF:
                return "MOVE_SELF";
            default:
                return "UNKNOWN - " + event;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        AutoApplication.getInstance().setConnectivityListener(this);
        downloadService = new DownloadService(this);
        presenter = new AutoServicePresenter(this);
        presenter.bindView(this);
        broadcastReceiver.register(this);
        listOffice = new ArrayList<>();
        Log.d(TAG,"Start service here");
        for (int i = 0 ;i < presenter.getListFolder().size();i++) {
            if (presenter.getListFolder().get(i).isEnable){
                presenter.getListObserver().get(i).setListener(this,true);
            }
            else{
                presenter.getListObserver().get(i).setListener(this,false);
            }
        }

    }

    @Override
    public void onEvent(int event, String file, String path,String folder_name) {

        if (event == FileObserver.CLOSE_WRITE || event == FileObserver.MOVED_TO) {
            Log.d(TAG,"show event : " +getEventString(event));
            Log.d(TAG,"Show path of path : "+ path);
            Log.d(TAG,"Show name : " + file);
            if (ConnectivityReceiver.isConnected()){
                presenter.setDevice_id(FileUtil.getMacAddress());
                presenter.setFile_name(file);
                presenter.setFolder_name(folder_name);
                presenter.setPath_file_name(path);
                CFileDocument fileDocument = (CFileDocument) presenter.getRealmController().getSearchObject(getApplicationContext(),"file_name",file,CFileDocument.class);
                if (fileDocument==null){
                    presenter.onUpload();
                }
            }
            else {
                listOffice = FileUtil.mReadJsonDataFileOffice(getContext(),Constant.LIST_FILE_OFFICE);
                if (listOffice==null){
                    listOffice = new ArrayList<>();
                }
                String nameFile = path+"/"+file;
                if (presenter.getStorage().isFileExist(nameFile) && FileUtil.fileAccept(new File(nameFile))){
                    listOffice.add(new CAutoFileOffice(nameFile,folder_name));
                }
                FileUtil.mDeleteFile(getContext(),Constant.LIST_FILE_OFFICE);
                FileUtil.mCreateAndSaveFile(getApplicationContext(),Constant.LIST_FILE_OFFICE,new Gson().toJson(listOffice));
                presenter.onShowDataOffice(listOffice);

            }
        }
    }

    @Override
    public void onDataOffice(List<CAutoFileOffice> list) {
        Log.d(TAG,"List office : "+ new Gson().toJson(list));
    }

    @Override
    public void onDownLoadNow() {
        if (NetworkUtil.pingIpAddress(getContext())) {
            return;
        }
        for (int i = 0 ;i<presenter.getFileDocumentList().size();i++){
                downloadService.onProgressingDownload(this);
                downloadService.intDownLoad(i,presenter.getFileDocumentList().get(i).file_name,presenter.getFileDocumentList().get(i).parent_folder_name,presenter.getFileDocumentList().get(i).path_folder_name);
        }
    }

    @Override
    public void onUploadNow(Context context,HashMap<String,String>hashMap) {
        String nameFile = presenter.getPath_file_name()+"/"+presenter.getFile_name();
        Log.d(TAG," Call 1 name file : "+ nameFile);
        if (presenter.getStorage().isFileExist(nameFile) && FileUtil.fileAccept(new File(nameFile))){
            Log.d(TAG,"Call 2");
            if (ConnectivityReceiver.isConnected()){
                Log.d(TAG,"Call 3");
                Log.d(TAG,"Event is :"+nameFile);
                uploadMultipart(nameFile);
            }
        }
    }

    public void uploadMultipart(String filePath) {
        try {
            Log.d(TAG,"file upload : "+ filePath);
            Log.d(TAG,"Folder : " + presenter.getFolder_name() + " Device_id : "+ presenter.getDevice_id());

            new MultipartUploadRequest(getApplicationContext(), Constant.File_UPLOAD_AUTO)
                    // starting from 3.1+, you can also use content:// URI string instead of absolute file
                    .addFileToUpload(filePath,Constant.TAG_FILE_UPLOAD)
                    .addParameter(Constant.TAG_FOLDER_NAME,presenter.getFolder_name())
                    .addParameter(Constant.TAG_DEVICE_ID,presenter.getDevice_id())
                    .setNotificationConfig(new UploadNotificationConfig())
                    .setMaxRetries(2)
                    .startUpload();
        } catch (Exception exc) {
            Log.e("AndroidUploadService", exc.getMessage(), exc);
        }
    }

    private UploadServiceBroadcastReceiver broadcastReceiver = new UploadServiceBroadcastReceiver() {
        @Override
        public void onProgress(Context context, UploadInfo uploadInfo) {
            // your implementation
            Log.d(TAG,"show progress : " + new Gson().toJson(uploadInfo.getFilesLeft()));
        }

        @Override
        public void onError(Context context, UploadInfo uploadInfo, ServerResponse serverResponse, Exception exception) {
            // your implementation
        }

        @Override
        public void onCompleted(Context context, UploadInfo uploadInfo, ServerResponse serverResponse) {
            Log.d(TAG,"Show onCompleted" + new Gson().toJson(uploadInfo.getFilesLeft()));
            CFileDocument fileDocument =  new Gson().fromJson(serverResponse.getBodyAsString(),CFileDocument.class);
            Log.d(TAG,"Upload successful" + new Gson().toJson(fileDocument));
            presenter.getRealmController().mInsertObject(fileDocument);
            if (listOffice.size()>0){
                int count = presenter.getCountUploading();
                count +=1;
                presenter.setCountUploading(count);
                if (count==listOffice.size()){
                    Log.d(TAG,"Uploading finished");
                    if (presenter.isCheckLoading()){
                        FileDocumentRequest fileDocumentRequest = new FileDocumentRequest(presenter.getFileDocument().file_document_id);
                        presenter.getLatestFile(fileDocumentRequest);
                    }else{
                        presenter.getAllFile();
                    }
                    FileUtil.mDeleteFile(getApplicationContext(),Constant.LIST_FILE_OFFICE);
                    FileUtil.mCreateAndSaveFile(getApplicationContext(),Constant.LIST_FILE_OFFICE,new Gson().toJson(new ArrayList<>()));
                    listOffice = new ArrayList<>();
                }
            }
            else{
                if (presenter.isCheckLoading()){
                    FileDocumentRequest fileDocumentRequest = new FileDocumentRequest(presenter.getFileDocument().file_document_id);
                    presenter.getLatestFile(fileDocumentRequest);
                }else{
                    presenter.getAllFile();
                }
            }
        }

        @Override
        public void onCancelled(Context context, UploadInfo uploadInfo) {
            // your implementation
        }
    };




    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {

        Observable.create(subscriber -> {
            boolean isActive = false;
            try {
                listOffice = FileUtil.mReadJsonDataFileOffice(getApplicationContext(),Constant.LIST_FILE_OFFICE);
                if (listOffice == null){
                    listOffice = new ArrayList<>();
                }

                for (CAutoFileOffice index : listOffice){
                    if (presenter.getStorage().isFileExist(index.pathFile) && FileUtil.fileAccept(new File(index.pathFile))){
                      if (isConnected){
                         presenter.setFolder_name(index.folder_name);
                         presenter.setDevice_id(FileUtil.getMacAddress());
                         uploadMultipart(index.pathFile);
                         isActive = true;
                     }
                    }
                    else{
                        Log.d(TAG,"File is not existing");
                    }
                }

                Log.d(TAG,"Internet changed");

            }
            catch (NullPointerException e){
                e.printStackTrace();
            }
            subscriber.onNext(isActive);
            subscriber.onCompleted();
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.computation())
                .subscribe(response -> {
                    boolean isResponse = (boolean)response;
                    if (isResponse){

                    }
                });
    }


    @Override
    public void onDownLoadCompleted(int count) {
        if (presenter.getFileDocumentList().size()==0){
            Log.d(TAG,"Array is null");
            return;
        }

        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
               CFileDocument fileDocument =  realm.copyToRealmOrUpdate(presenter.getFileDocumentList().get(count));
                fileDocument = realm.copyFromRealm(fileDocument);
                Log.d(TAG,"Insert successful" + new Gson().toJson(fileDocument));
            }
        });
        if (count == presenter.getFileDocumentList().size()-1){

            Log.d(TAG,"onDownload Finish : " + presenter.getFileDocumentList().size());
            realm.close();
        }
        else{
            Log.d(TAG,"onDownload Completed");
        }
       // presenter.getRealmController().mInsertObject(presenter.getFileDocumentList().get(count),1);
    }

    @Override
    public void onUpload(List<CFileDocument> list) {

    }

    @Override
    public void onShowLoading() {

    }

    @Override
    public void onHideLoading() {

    }

    @Override
    public Context getContext() {
        return getApplicationContext();
    }

    @Override
    public void onShowListObjects(List<CFileDocument> list) {

    }

    @Override
    public void onShowObjects(CFileDocument cFileDocument) {

    }

    @Override
    public List<CFileDocument> onGetListObjects() {
        return null;
    }

    @Override
    public CFileDocument onGetObjects() {
        return null;
    }
}
