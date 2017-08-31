package delfi.com.vn.autotransferfile.ui.autoupload;

import java.util.List;

import delfi.com.vn.autotransferfile.common.presenter.BaseView;
import delfi.com.vn.autotransferfile.model.CAuToUpload;

/**
 * Created by PC on 8/30/2017.
 */

public interface  AutoUploadView extends BaseView{
    void onShowList(List<CAuToUpload>list);
}
