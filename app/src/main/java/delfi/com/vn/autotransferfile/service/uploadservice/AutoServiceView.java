package delfi.com.vn.autotransferfile.service.uploadservice;

import java.util.List;

import delfi.com.vn.autotransferfile.model.CFileDocument;
import dk.delfi.core.common.presenter.BaseView;

/**
 * Created by PC on 9/6/2017.
 */

public interface AutoServiceView<T> extends BaseView<CFileDocument>{
    void onUpload(List<T>list);
}
