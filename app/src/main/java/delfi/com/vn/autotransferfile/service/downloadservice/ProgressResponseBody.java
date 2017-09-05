package delfi.com.vn.autotransferfile.service.downloadservice;

import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

/**
 * Created by PC on 9/1/2017.
 */

public class ProgressResponseBody extends ResponseBody {

    private final ResponseBody responseBody;
    private final ProgressResponseBodyListener progressListener;
    private BufferedSource bufferedSource;
    private int idResponse ;
    public static final String TAG = ProgressResponseBody.class.getSimpleName();

    public ProgressResponseBody(ResponseBody responseBody, ProgressResponseBodyListener progressListener,int idResponse) {
        this.responseBody = responseBody;
        this.progressListener = progressListener;
        this.idResponse = idResponse;
    }

    @Override
    public MediaType contentType() {
        return responseBody.contentType();
    }

    @Override
    public long contentLength() {
        return responseBody.contentLength();
    }

    @Override
    public BufferedSource source() {
        if (bufferedSource == null) {
            bufferedSource = Okio.buffer(source(responseBody.source()));
        }
        return bufferedSource;
    }

    private Source source(Source source) {
        return new ForwardingSource(source) {
            long totalBytesRead = 0L;
            @Override
            public long read(Buffer sink, long byteCount) throws IOException {
                long bytesRead = super.read(sink, byteCount);
                totalBytesRead += bytesRead != -1 ? bytesRead : 0;
                float percent = bytesRead == -1 ? 100f : (((float)totalBytesRead / (float) responseBody.contentLength()) * 100);
                if(progressListener != null){
                    if (percent>0){
                        if (percent>99){
                            progressListener.onAttachmentDownloadedSuccess(idResponse);
                            progressListener.onAttachmentDownloadUpdate((int)percent,idResponse);
                        }
                        else{
                            progressListener.onAttachmentDownloadUpdate((int)percent,idResponse);
                        }
                    }
                }
                return bytesRead;
            }
        };
    }

    public interface ProgressResponseBodyListener {
        void onAttachmentDownloadedSuccess(int idResponse);
        void onAttachmentDownloadedError();
        void onAttachmentDownloadUpdate(int percent,int idResponse);
    }

}
