package delfi.com.vn.autotransferfile.service.uploadservice;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import net.gotev.uploadservice.ServerResponse;
import net.gotev.uploadservice.UploadInfo;
import net.gotev.uploadservice.UploadServiceBroadcastReceiver;

/**
 * Created by mac10 on 8/30/17.
 */

public class SingleUploadBroadcastReceiver extends UploadServiceBroadcastReceiver {

    public static final String TAG = SingleUploadBroadcastReceiver.class.getSimpleName();
    public interface Delegate {
        void onProgress(int progress);
        void onError(Exception exception);
        void onCompleted(UploadInfo uploadInfo, ServerResponse serverResponse);
        void onCancelled();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG,"on Receiver here");
    }

    private String mUploadID;
    private Delegate mDelegate;

    public void setUploadID(String uploadID) {
        mUploadID = uploadID;
    }

    public void setDelegate(Delegate delegate) {
        mDelegate = delegate;
    }


    @Override
    public void onProgress(Context context, UploadInfo uploadInfo) {
        super.onProgress(context,uploadInfo);
        if (uploadInfo.getUploadId().equals(mUploadID) && mDelegate != null) {
            mDelegate.onProgress(uploadInfo.getProgressPercent());
        }
    }

    @Override
    public void onCompleted(Context context, UploadInfo uploadInfo, ServerResponse serverResponse) {
        super.onCompleted(context,uploadInfo,serverResponse);
        if (uploadInfo.getUploadId().equals(mUploadID) && mDelegate != null) {
            mDelegate.onCompleted(uploadInfo, serverResponse);
        }
    }

    @Override
    public void onError(Context context, UploadInfo uploadInfo, ServerResponse serverResponse, Exception exception) {
        super.onError(context,uploadInfo,serverResponse,exception);
        if (uploadInfo.getUploadId().equals(mUploadID) && mDelegate != null) {
            mDelegate.onError(exception);
        }
    }


    @Override
    public void onCancelled(Context context, UploadInfo uploadInfo) {
        super.onCancelled(context,uploadInfo);
        if (uploadInfo.getUploadId().equals(mUploadID) && mDelegate != null) {
            mDelegate.onCancelled();
        }
    }

}
