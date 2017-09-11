package delfi.com.vn.autotransferfile.service.uploadservice;

import android.os.Build;
import android.os.FileObserver;

import dk.delfi.core.common.controller.RealmController;
import io.realm.Realm;

/**
 * Created by mac10 on 9/7/17.
 */

public class FileObserverService extends FileObserver {
    int events = 0 ;
    String path;
    String folder_name ;
    FileObserverServiceListener listener;
   public FileObserverService(String path,String folder_name){
       super(path,FileObserver.ALL_EVENTS);
       this.path = path;
       this.folder_name = folder_name ;

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
        listener.onEvent(i,s,path,folder_name);
    }

    public interface FileObserverServiceListener{
        void onEvent(int i,String file,String path,String folder_name);
    }

    @Override
    public void startWatching() {
        super.startWatching();
    }
}
