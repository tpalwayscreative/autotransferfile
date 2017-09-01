package delfi.com.vn.autotransferfile.ui.user;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import java.util.List;
import butterknife.OnClick;
import delfi.com.vn.autotransferfile.R;
import delfi.com.vn.autotransferfile.common.api.request.UserRequestOption;
import delfi.com.vn.autotransferfile.common.dialog.DialogFactory;
import delfi.com.vn.autotransferfile.model.CUser;
import dk.delfi.core.common.activity.BaseActivity;

public class UserActivity extends BaseActivity implements UserView{

    public static  final String TAG = UserActivity.class.getSimpleName();
    private UserPresenter presenter ;
    private ProgressDialog pDialog ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        presenter = new UserPresenter(this);
        presenter.bindView(this);
        pDialog = DialogFactory.simpleLoadingDialog(this,"Please wait...");
    }

    @OnClick(R.id.btnLogin)
    public void onLogin(){
        UserRequestOption userRequestOption = new UserRequestOption("abc","123","dcf123@gmail.com");
        presenter.userRegister(userRequestOption);
    }

    @Override
    public void onShowNameError(int resId) {

    }

    @Override
    public void onShowError(String alert) {
        Toast.makeText(this,alert,Toast.LENGTH_SHORT).show();
    }

    @Override
    public String onGetName() {
        return null;
    }

    @Override
    public void onShowLoading() {
        pDialog.show();
    }

    @Override
    public void onHideLoading() {
        pDialog.dismiss();
    }

    @Override
    public Context getContext() {
        return getApplicationContext();
    }

    @Override
    public void onShowListObjects(List<CUser> list) {

    }

    @Override
    public void onShowObjects(CUser cUser) {

    }

    @Override
    public List<CUser> onGetListObjects() {
        return null;
    }

    @Override
    public CUser onGetObjects() {
        return null;
    }
}
