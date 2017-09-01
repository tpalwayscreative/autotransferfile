package delfi.com.vn.autotransferfile.ui.autoupload;
import java.util.List;
import delfi.com.vn.autotransferfile.model.CAuToUpload;
import dk.delfi.core.common.presenter.BaseView;

/**
 * Created by PC on 8/30/2017.
 */

public interface  AutoUploadView extends BaseView<CAuToUpload>{
    void onShowList(List<CAuToUpload>list);
}
