package delfi.com.vn.autotransferfile.service;
import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.FileObserver;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.util.Log;

public class AutoService extends Service {

    public static final String TAG = AutoService.class.getSimpleName();
    private Observer cameraObserver;
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
        return START_STICKY;
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
            case android.os.FileObserver.CREATE:
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
        cameraObserver = new Observer(Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM/Camera");
        cameraObserver.startWatching();
        Log.d(TAG,"Start service here");
    }

    private class Observer extends FileObserver {
        private String path;

        public Observer(String path) {
            //super(path, FileObserver.CLOSE_WRITE);
            super(path, FileObserver.ALL_EVENTS);
            this.path = path;
            Log.d(TAG,"Full path : "+ path);
        }

        @Override
        public void onEvent(int event, String file) {
            if (file != null && path != null && (event == FileObserver.CLOSE_WRITE || event == FileObserver.MOVED_TO)) {
                if (!file.equals("temp_video")) {
                    Log.d(TAG, "event: " + getEventString((Integer) event) + " file: [" + file + "]");
                }
            }
        }
    }

}
