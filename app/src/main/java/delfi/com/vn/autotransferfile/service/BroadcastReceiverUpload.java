package delfi.com.vn.autotransferfile.service;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.TabHost;

import net.gotev.uploadservice.ServerResponse;
import net.gotev.uploadservice.UploadInfo;
import net.gotev.uploadservice.UploadServiceBroadcastReceiver;

/**
 * Created by mac10 on 8/30/17.
 */

public class BroadcastReceiverUpload extends UploadServiceBroadcastReceiver {
    public static String TAG = BroadcastReceiverUpload.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        Log.d(TAG,"on Receiver here");
    }

    @Override
    public void onCompleted(Context context, UploadInfo uploadInfo, ServerResponse serverResponse) {
        Log.d(TAG,"Success upload");
    }

    @Override
    public void onError(Context context, UploadInfo uploadInfo, ServerResponse serverResponse, Exception exception) {
        Log.d(TAG,"Show Failed");
    }
}
