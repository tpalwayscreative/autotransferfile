package delfi.com.vn.autotransferfile.service;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.FileObserver;
import android.os.IBinder;
import android.util.Log;
import com.google.gson.Gson;
import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.ServerResponse;
import net.gotev.uploadservice.UploadInfo;
import net.gotev.uploadservice.UploadNotificationConfig;
import net.gotev.uploadservice.UploadStatusDelegate;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import delfi.com.vn.autotransferfile.Constant;
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
import dk.delfi.core.common.controller.RealmController;
import io.realm.Realm;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class AutoService extends Service implements ConnectivityReceiver.ConnectivityReceiverListener,AutoServiceView<CFileDocument>,DownloadService.DownLoadServiceListener,FileObserverService.FileObserverServiceListener{

    public static final String TAG = AutoService.class.getSimpleName();
   // private Observer cameraObserver;
   // private Observer pictureObserver ;
   // private Observer downloadsObserver;
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
        return START_STICKY ;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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

         /*
        Observable.create(subscriber -> {
            for (int i = 0 ;i < presenter.getListFolder().size();i++){
                presenter.getListObserver().get(i).setListener(this);

                if (index.isEnable){
                    if (index.folder_name.equals("Camera")){
                        cameraObserver = new Observer(index.path_folder_name);
                        cameraObserver.startWatching();
                    }
                    else if(index.folder_name.equals("Picture")){
                        pictureObserver = new Observer(index.path_folder_name);
                        pictureObserver.startWatching();
                    }
                    else if(index.folder_name.equals("Downloads")){
                        downloadsObserver = new Observer(index.path_folder_name);
                        downloadsObserver.startWatching();
                    }
                }




            }

            subscriber.onNext(null);
            subscriber.onCompleted();
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.computation())
                .subscribe(response -> {
                });

                 */

    }

    @Override
    public void onEvent(int event, String file, String path,String folder_name) {
        Log.d(TAG,"show event : " +getEventString(event));
        if (event == FileObserver.CLOSE_WRITE || event == FileObserver.MOVED_TO) {
            Log.d(TAG,"Show path of path : "+ path);
            Log.d(TAG,"Show name : " + file);
            if (ConnectivityReceiver.isConnected()){
                HashMap<String,String>hashMap = new HashMap<>();
                hashMap.put(Constant.TAG_PATH_FOLDER_NAME,path);
                hashMap.put(Constant.TAG_FILE_NAME,file);
                hashMap.put(Constant.TAG_FOLDER_NAME,folder_name);
                presenter.getRealmController().getSearchObject(getApplicationContext(),"file_name",file,CFileDocument.class,Constant.TAG_CODE_UPLOAD,hashMap);
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
    /*

    public class Observer extends FileObserver {
        public String path;
        public Observer(String path) {
            super(path, FileObserver.CLOSE_WRITE);
            this.path = path;
            Log.d(TAG,"Full path : "+ path);
        }

        @Override
        public void onEvent(int event, String file) {
            if (event == FileObserver.CLOSE_WRITE) {

                if (ConnectivityReceiver.isConnected()){
                    presenter.getRealmController().getSearchObject("file_name",file,CFileDocument.class,Constant.TAG_CODE_UPLOAD);
                    presenter.setFile_name(file);
                }
                else {
                    listOffice = FileUtil.mReadJsonDataFileOffice(getContext(),Constant.LIST_FILE_OFFICE);
                    if (listOffice==null){
                        listOffice = new ArrayList<>();
                    }
                    for (CFolder index : presenter.getListFolder()){
                        String nameFile = index.path_folder_name+"/"+file;
                        presenter.setFolder_name(index.folder_name);
                        if (index.isEnable && presenter.getStorage().isFileExist(nameFile) && FileUtil.fileAccept(new File(nameFile))){
                            listOffice.add(new CAutoFileOffice(nameFile));
                        }
                    }
                    FileUtil.mDeleteFile(getContext(),Constant.LIST_FILE_OFFICE);
                    FileUtil.mCreateAndSaveFile(getApplicationContext(),Constant.LIST_FILE_OFFICE,new Gson().toJson(listOffice));
                }
            }
        }
    }

    */

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
        Log.d(TAG,"Call 0");
        presenter.setFolder_name(hashMap.get(Constant.TAG_FOLDER_NAME));
        presenter.setFile_name(hashMap.get(Constant.TAG_FILE_NAME));
        presenter.setPath_file_name(hashMap.get(Constant.TAG_PATH_FOLDER_NAME));

        String nameFile = presenter.getPath_file_name()+"/"+presenter.getFile_name();
        Log.d(TAG," Call 1 name file : "+ nameFile);
        if (presenter.getStorage().isFileExist(nameFile) && FileUtil.fileAccept(new File(nameFile))){
            Log.d(TAG,"Call 2");
            if (ConnectivityReceiver.isConnected()){
                Log.d(TAG,"Call 3");
                Log.d(TAG,"Event is :"+nameFile);
                uploadMultipart(context,nameFile);
            }
        }
    }

    public void uploadMultipart(final Context context, String filePath) {
        try {
            presenter.setUploading(true);
            Log.d(TAG,"file upload : "+ filePath);
            new MultipartUploadRequest(context, Constant.File_UPLOAD_AUTO)
                    // starting from 3.1+, you can also use content:// URI string instead of absolute file
                    .addFileToUpload(filePath,Constant.TAG_FILE_UPLOAD)
                    .addParameter(Constant.TAG_FOLDER_NAME,presenter.getFolder_name())
                    .addParameter(Constant.TAG_DEVICE_ID,presenter.getDevice_id())
                    .setNotificationConfig(new UploadNotificationConfig())
                    .setMaxRetries(2)
                    .setDelegate(new UploadStatusDelegate() {
                        @Override
                        public void onProgress(Context context, UploadInfo uploadInfo) {
                            Log.d(TAG,"Show onProgress");
                        }

                        @Override
                        public void onError(Context context, UploadInfo uploadInfo, ServerResponse serverResponse, Exception exception) {
                            Log.d(TAG,"Show onError");
                        }

                        @Override
                        public void onCompleted(Context context, UploadInfo uploadInfo, ServerResponse serverResponse) {
                            try{
                                Log.d(TAG,"Show onCompleted");
                                CFileDocument fileDocument =  new Gson().fromJson(serverResponse.getBodyAsString(),CFileDocument.class);
                                presenter.setUploading(false);
                                Log.d(TAG,"Upload successful" + new Gson().toJson(fileDocument));
                                presenter.getRealmController().mInsertObject(fileDocument,Constant.TAG_CODE_UPLOAD);
                            }
                            catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                        @Override
                        public void onCancelled(Context context, UploadInfo uploadInfo) {
                            Log.d(TAG,"Show onCancelled");
                        }
                    })
                    .startUpload();
        } catch (Exception exc) {
            Log.e("AndroidUploadService", exc.getMessage(), exc);
        }
    }

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
                    if (isConnected){
                        presenter.setFolder_name(index.folder_name);
                        uploadMultipart(this,index.pathFile);
                        isActive = true;
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
                        FileUtil.mDeleteFile(getApplicationContext(),Constant.LIST_FILE_OFFICE);
                        FileUtil.mCreateAndSaveFile(getApplicationContext(),Constant.LIST_FILE_OFFICE,new Gson().toJson(new ArrayList<>()));
                        listOffice = new ArrayList<>();
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
