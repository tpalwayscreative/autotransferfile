package delfi.com.vn.autotransferfile.ui.autoupload;
import java.util.List;
import delfi.com.vn.autotransferfile.model.CAuToUpload;
import delfi.com.vn.autotransferfile.model.CFolder;
import dk.delfi.core.common.presenter.BaseView;

/**
 * Created by PC on 8/30/2017.
 */

public interface  AutoUploadView extends BaseView<CFolder>{
    void onShowList(List<CFolder>list);
    void onShowError(String alert);
    void onDownLoadNow();
    void onLogoutSuccessfully();
}
