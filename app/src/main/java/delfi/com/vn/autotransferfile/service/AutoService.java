package delfi.com.vn.autotransferfile.service;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.FileObserver;
import android.os.IBinder;
import android.util.Log;
import com.google.gson.Gson;
import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import delfi.com.vn.autotransferfile.Constant;
import delfi.com.vn.autotransferfile.common.utils.FileUtil;
import delfi.com.vn.autotransferfile.model.CAuToUpload;
import delfi.com.vn.autotransferfile.model.CAutoFileOffice;
import delfi.com.vn.autotransferfile.model.CFileDocument;
import delfi.com.vn.autotransferfile.service.broadcastreceiver.ConnectivityReceiver;
import delfi.com.vn.autotransferfile.service.downloadservice.DownloadService;
import delfi.com.vn.autotransferfile.service.downloadservice.Sequence;
import delfi.com.vn.autotransferfile.service.fileobserver.RecursiveFileObserver;
import delfi.com.vn.autotransferfile.service.uploadservice.AutoServicePresenter;
import delfi.com.vn.autotransferfile.service.uploadservice.AutoServiceView;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class AutoService extends Service implements ConnectivityReceiver.ConnectivityReceiverListener,AutoServiceView<CFileDocument>{

    public static final String TAG = AutoService.class.getSimpleName();
    private Observer cameraObserver;
    private Observer pictureObserver ;
    private Observer downloadsObserver;
    private RecursiveFileObserver recursiveFileObserver;
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
        presenter.initRealm();
        presenter.getAllFile();
        listOffice = new ArrayList<>();
        Observable.create(subscriber -> {
            List<CAuToUpload> list = FileUtil.mReadJsonDataSettingButton(getApplicationContext(), Constant.LIST_FILE);
            for (CAuToUpload index : list){
                if (index.isEnable){
                    if (index.name.equals("Camera")){
                        cameraObserver = new Observer(index.full_path);
                        cameraObserver.startWatching();
                    }
                    else if(index.name.equals("Picture")){
                        pictureObserver = new Observer(index.full_path);
                        pictureObserver.startWatching();
                    }
                    else if(index.name.equals("Downloads")){
                        downloadsObserver = new Observer(index.full_path);
                        downloadsObserver.startWatching();
                    }
                }
            }
            Log.d(TAG,"Start service here");
            subscriber.onNext(null);
            subscriber.onCompleted();
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.computation())
                .subscribe(response -> {
                });
    }

    private class Observer extends FileObserver {
        private String path;
        public Observer(String path) {
            super(path, FileObserver.CREATE);
            this.path = path;
            Log.d(TAG,"Full path : "+ path);
        }

        @Override
        public void onEvent(int event, String file) {
            if (event == FileObserver.ACCESS || event == FileObserver.CLOSE_NOWRITE || event ==FileObserver.CREATE) {
                List<CAuToUpload> list = FileUtil.mReadJsonDataSettingButton(getApplicationContext(), Constant.LIST_FILE);
                for (CAuToUpload index : list){
                    String nameFile = index.full_path+"/"+file;
                    presenter.setFolder_name(index.name);
                    if (index.isEnable && presenter.getStorage().isFileExist(nameFile) && FileUtil.fileAccept(new File(nameFile))){
                        if (ConnectivityReceiver.isConnected()){
                            Log.d(TAG,"Event is :"+nameFile);
                            uploadMultipart(getApplicationContext(),nameFile);
                        }
                        else{
                            listOffice.add(new CAutoFileOffice(nameFile));
                        }
                    }
                }
                FileUtil.mCreateAndSaveFile(getApplicationContext(),Constant.LIST_FILE_OFFICE,new Gson().toJson(listOffice));
                Log.d(TAG, "event: " + getEventString((Integer) event) + " file: [" + file + "]");
            }
        }
    }

    public void uploadMultipart(final Context context, String filePath) {
        try {
            ///storage/emulated/0/Pictures/Android File Upload/IMG_20170829_171720.jpg
            Log.d(TAG,"file upload : "+ filePath);
            new MultipartUploadRequest(context, Constant.File_UPLOAD_AUTO)
                    // starting from 3.1+, you can also use content:// URI string instead of absolute file
                    .addFileToUpload(filePath,"FileUpload")
                    .addParameter(Constant.TAG_FOLDER_NAME,presenter.getFolder_name())
                    .addParameter(Constant.TAG_DEVICE_ID,presenter.getDevice_id())
                    .setNotificationConfig(new UploadNotificationConfig())
                    .setMaxRetries(2)
                    .startUpload();
        } catch (Exception exc) {
            Log.e("AndroidUploadService", exc.getMessage(), exc);
        }
    }

    /*
    public void uploadMultipart(final Context context, String filePath) {
        try {
            ///storage/emulated/0/Pictures/Android File Upload/IMG_20170829_171720.jpg
            Log.d(TAG,"file upload : "+ filePath);
            Map<String,String> hash = new HashMap<>();
            hash.put("email","delfitest@gmail.com");
            hash.put("website","delfi.com");
            new MultipartUploadRequest(context, Constant.FILE_UPLOAD_URL)
                    // starting from 3.1+, you can also use content:// URI string instead of absolute file
                    .addFileToUpload(filePath,"image")
                    .addParameter("email","delfitest@gmail.com")
                    .addParameter("website","http://delfi.com")
                    .setNotificationConfig(new UploadNotificationConfig())
                    .setMaxRetries(2)
                    .startUpload();
        } catch (Exception exc) {
            Log.e("AndroidUploadService", exc.getMessage(), exc);
        }
    }

    */

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {

        Observable.create(subscriber -> {
            boolean isActive = false;
            try {
                for (CFileDocument index : presenter.getFileDocumentList()){
                    if (isConnected){
                        int nextValue = Sequence.nextValue();
                        downloadService.intDownLoad(nextValue,index.file_name);
                    }
                }

                List<CAutoFileOffice> list = FileUtil.mReadJsonDataFileOffice(this,Constant.LIST_FILE_OFFICE);
                for (CAutoFileOffice index:list){
                    if (isConnected){
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
                        listOffice = new ArrayList<CAutoFileOffice>();
                    }
                });
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
