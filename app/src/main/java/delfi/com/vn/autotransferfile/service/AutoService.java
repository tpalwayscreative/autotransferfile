package delfi.com.vn.autotransferfile.service;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Camera;
import android.os.Environment;
import android.os.FileObserver;
import android.os.IBinder;
import android.util.Log;

import com.google.gson.Gson;
import com.snatik.storage.Storage;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.ServerResponse;
import net.gotev.uploadservice.UploadInfo;
import net.gotev.uploadservice.UploadNotificationConfig;
import net.gotev.uploadservice.UploadStatusDelegate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import delfi.com.vn.autotransferfile.Constant;
import delfi.com.vn.autotransferfile.common.utils.FileUtil;
import delfi.com.vn.autotransferfile.model.CAuToUpload;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class AutoService extends Service  implements SingleUploadBroadcastReceiver.Delegate{

    public static final String TAG = AutoService.class.getSimpleName();
    private Observer cameraObserver;
    private Observer pictureObserver ;
    private Observer downloadsObserver;
    private Storage storage ;

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
        storage = new Storage(getApplicationContext());
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
            if (storage.isDirectoryExists(storage.getExternalStorageDirectory(Environment.DIRECTORY_DCIM+"/Camera/")))
            {
                Log.d(TAG,"is existing");
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
            super(path, FileObserver.CLOSE_WRITE);
            this.path = path;
            Log.d(TAG,"Full path : "+ path);
        }

        @Override
        public void onEvent(int event, String file) {
            Log.d(TAG,"Event is :"+getEventString(event));
            if (event ==FileObserver.CLOSE_WRITE) {
                List<CAuToUpload> list = FileUtil.mReadJsonDataSettingButton(getApplicationContext(), Constant.LIST_FILE);
                for (CAuToUpload index : list){
                    String nameFile = index.full_path+"/"+file;
                    if (index.isEnable && storage.isFileExist(nameFile)){
                        uploadMultipart(getApplicationContext(),nameFile);
                    }
                }
                Log.d(TAG, "event: " + getEventString((Integer) event) + " file: [" + file + "]");
            }
        }
    }

    public void uploadMultipart(final Context context,String filePath) {
        try {
            ///storage/emulated/0/Pictures/Android File Upload/IMG_20170829_171720.jpg
            String uploadId = UUID.randomUUID().toString();
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


    @Override
    public void onProgress(int progress) {

    }

    @Override
    public void onError(Exception exception) {
        Log.d(TAG,"Show Failed");
    }

    @Override
    public void onCompleted(UploadInfo uploadInfo, ServerResponse serverResponse) {

        Log.d(TAG,"Show comepleted");
    }

    @Override
    public void onCancelled() {

    }
}
