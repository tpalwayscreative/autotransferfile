package delfi.com.vn.autotransferfile.service.uploadservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by mac10 on 8/30/17.
 */

public class DownLoadBroadCastReceiver extends BroadcastReceiver {
    public static String TAG = DownLoadBroadCastReceiver.class.getSimpleName();
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG,"New download : ");
    }
}
