package delfi.com.vn.autotransferfile.service.uploadservice;

import android.content.Context;

import java.util.HashMap;
import java.util.List;

import delfi.com.vn.autotransferfile.model.CAutoFileOffice;
import delfi.com.vn.autotransferfile.model.CFileDocument;
import dk.delfi.core.common.presenter.BaseView;

/**
 * Created by PC on 9/6/2017.
 */

public interface AutoServiceView<T> extends BaseView<CFileDocument>{
    void onUpload(List<T>list);
    void onDownLoadNow();
    void onUploadNow(Context context, HashMap<String,String>hashMap);
    void onDataOffice(List<CAutoFileOffice>list);
}
