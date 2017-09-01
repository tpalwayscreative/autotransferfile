package delfi.com.vn.autotransferfile.ui.user;

import delfi.com.vn.autotransferfile.model.CUser;
import dk.delfi.core.common.presenter.BaseView;

/**
 * Created by PC on 9/1/2017.
 */

public interface UserView extends BaseView<CUser> {
    void onShowNameError(int resId);
    void onShowError(String alert);
    String onGetName();
}
