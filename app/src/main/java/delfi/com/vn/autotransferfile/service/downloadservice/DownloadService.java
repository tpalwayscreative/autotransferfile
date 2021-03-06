package delfi.com.vn.autotransferfile.service.downloadservice;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import delfi.com.vn.autotransferfile.BuildConfig;
import delfi.com.vn.autotransferfile.R;
import delfi.com.vn.autotransferfile.common.application.BaseApplication;
import delfi.com.vn.autotransferfile.service.AutoApplication;
import delfi.com.vn.autotransferfile.service.AutoService;
import delfi.com.vn.autotransferfile.service.broadcastreceiver.ConnectivityReceiver;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okio.BufferedSink;
import okio.Okio;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by PC on 9/1/2017.
 */

public class DownloadService  implements ProgressResponseBody.ProgressResponseBodyListener{

    private Context context;
    public static final String TAG = DownloadService.class.getSimpleName();
    public DownloadService(Context context) {
        this.context = context;
    }
    private NotificationCompat.Builder notificationBuilder;
    private NotificationManager notificationManager;
    private DownLoadServiceListener listener;

    public void intDownLoad(int idResponse,String fileName,String folderName,String path_save_to){
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_download)
                .setContentTitle("Download")
                .setContentText("Downloading File")
                .setAutoCancel(true);
        notificationManager.notify(idResponse, notificationBuilder.build());
        downloadZipFileRx(idResponse,fileName,folderName,path_save_to);
    }

    public void onProgressingDownload(DownLoadServiceListener downLoadServiceListener){
        this.listener  = downLoadServiceListener;
    }

    @Override
    public void onAttachmentDownloadUpdate(int percent,int idResponse) {
        Log.d(TAG,"Downloading : "+ percent);
        this.listener.onProgressingDownloading(percent);
        notificationBuilder.setContentInfo(percent+ "%")
                .setProgress(100, percent, false);
        notificationManager.notify(idResponse, notificationBuilder.build());
    }

    @Override
    public void onAttachmentDownloadedError() {

    }


    @Override
    public void onAttachmentDownloadedSuccess(int idResponse) {
        Log.d(TAG, "idResponse : "+ idResponse);
        listener.onDownLoadCompleted(idResponse);
        onDownloadComplete(idResponse);
    }

    public void downloadZipFileRx(int idResponse,String fileName,String folderName,String path_save_to) {
        // https://github.com/yourusername/awesomegames/archive/master.zip
        RetrofitInterface downloadService = createService(RetrofitInterface.class,BaseApplication.BASE_IP,idResponse);
        Log.d(TAG,"show path : "+ "/api/file/GetImage?file_name="+fileName+"&folder_name="+folderName);
        downloadService.downloadFileByUrlRx("/api/file/GetImage?file_name="+fileName+"&folder_name="+folderName)
                .flatMap(processResponse(idResponse,fileName,path_save_to))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(handleResult());
    }

    private Func1<Response<ResponseBody>, Observable<File>> processResponse(int idResponse,String fileName,String path_save_to) {
        return new Func1<Response<ResponseBody>, Observable<File>>() {
            @Override
            public Observable<File> call(Response<ResponseBody> responseBodyResponse) {
                return saveToDiskRx(responseBodyResponse,idResponse,fileName,path_save_to);
            }
        };
    }

    private Observable<File> saveToDiskRx(final Response<ResponseBody> response,int idResponse,String fileName,String path_save_to) {
        return Observable.create(new Observable.OnSubscribe<File>() {
            @Override
            public void call(Subscriber<? super File> subscriber) {
                try {
                    String header = response.headers().get("Content-Disposition");
                   // String filename = header.replace("attachment; filename=", "");
                    //new File("/data/data/" + activity.getPackageName() + "/games").mkdir();
                    //File destinationFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),fileName);
                    File destinationFile = new File(path_save_to,fileName);
                    //File destinationFile = new File("/data/data/" + activity.getPackageName() + "/games/" + filename);
                    BufferedSink bufferedSink = Okio.buffer(Okio.sink(destinationFile));
                    bufferedSink.writeAll(response.body().source());
                    bufferedSink.close();
                    subscriber.onNext(destinationFile);
                    subscriber.onCompleted();
                } catch (IOException e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                }
            }
        });
    }

    private Observer<File> handleResult() {
        return new Observer<File>() {
            @Override
            public void onCompleted() {
                //onDownloadComplete(idResponse);
                //onDownloadComplete();
            }
            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                Log.d(TAG, "Error " + e.getMessage());
            }
            @Override
            public void onNext(File file) {
                Log.d(TAG, "File downloaded to " + file.getAbsolutePath());
            }
        };
    }


    public <T> T createService(Class<T> serviceClass, String baseUrl,int idResponse) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(getOkHttpDownloadClientBuilder(this,idResponse))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create()).build();
        return retrofit.create(serviceClass);
    }

    public OkHttpClient getOkHttpDownloadClientBuilder(final ProgressResponseBody.ProgressResponseBodyListener progressListener,int idResponse) {
        OkHttpClient httpClientBuilder = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                if(progressListener == null) return chain.proceed(chain.request());

                okhttp3.Response originalResponse = chain.proceed(chain.request());
                return originalResponse.newBuilder()
                        .body(new ProgressResponseBody(originalResponse.body(), progressListener,idResponse))
                        .build();
            }
        }).build();
        // You might want to increase the timeout
        httpClientBuilder.newBuilder().connectTimeout(20, TimeUnit.MINUTES);
        httpClientBuilder.newBuilder().writeTimeout(20, TimeUnit.MINUTES);
        httpClientBuilder.newBuilder().readTimeout(20, TimeUnit.MINUTES);
        return httpClientBuilder;
    }

    private void onDownloadComplete(int idResponse){
        //notificationManager.cancel(0);
        notificationBuilder.setProgress(0,0,false);
        notificationBuilder.setContentText("File Downloaded");
        notificationManager.notify(idResponse, notificationBuilder.build());
    }

    public interface DownLoadServiceListener {
        void onDownLoadCompleted(int count);
        void onProgressingDownloading(int percent);
    }


//    @Override
//    public void onNetworkConnectionChanged(boolean isConnected) {
//        Log.d(TAG,"show changed network : ");
//        if (isConnected){
//            for (int i = 0 ; i < 1; i++){
//                int nextValue = Sequence.nextValue();
//                intDownLoad(nextValue);
//            }
//        }
//    }


}




