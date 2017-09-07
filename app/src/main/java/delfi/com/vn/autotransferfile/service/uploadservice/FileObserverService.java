package delfi.com.vn.autotransferfile.service.uploadservice;

import android.os.FileObserver;

/**
 * Created by mac10 on 9/7/17.
 */

public class FileObserverService extends FileObserver {

   String path;
   FileObserverServiceListener listener;
   public FileObserverService(String path){
       super(path, FileObserver.CLOSE_WRITE);
       this.path = path;
   }

   public void setListener(FileObserverServiceListener fileObserverServiceListener,boolean isStart){
       if (isStart){
           startWatching();
       }
       else {
           stopWatching();
       }
       this.listener = fileObserverServiceListener;
   }

    @Override
    public void onEvent(int i, String s) {
        listener.onEvent(i,s);
    }

    public interface FileObserverServiceListener{
        void onEvent(int i,String file);
    }

    @Override
    public void startWatching() {
        super.startWatching();
    }
}
