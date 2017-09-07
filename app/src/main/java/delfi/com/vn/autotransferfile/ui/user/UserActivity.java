package delfi.com.vn.autotransferfile.ui.user;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import delfi.com.vn.autotransferfile.R;
import delfi.com.vn.autotransferfile.common.api.request.GroupRequest;
import delfi.com.vn.autotransferfile.common.dialog.DialogFactory;
import delfi.com.vn.autotransferfile.common.utils.FileUtil;
import delfi.com.vn.autotransferfile.common.utils.Navigator;
import delfi.com.vn.autotransferfile.model.CUser;
import delfi.com.vn.autotransferfile.service.downloadservice.DownloadService;
import dk.delfi.core.common.activity.BaseActivity;
import dk.delfi.core.common.utils.Utils;

public class UserActivity extends BaseActivity implements UserView{

    public static  final String TAG = UserActivity.class.getSimpleName();
    private UserPresenter presenter ;
    private ProgressDialog pDialog ;
    @BindView(R.id.edtName)
    EditText edtName ;

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
        GroupRequest groupRequest = new GroupRequest(onGetName(),FileUtil.getMacAddress(),FileUtil.getDeviceName());
        presenter.userLogin(groupRequest);
       // Navigator.moveToHome(this);
        //UserRequestOption userRequestOption = new UserRequestOption("abc","123","dcf123@gmail.com");
       // presenter.userRegister(userRequestOption);
       // Intent intent = new Intent(this,DownloadService.class);
       // startService(intent);
    }

    @Override
    public void onShowNameError(int resId) {
        edtName.setError(getString(resId));

    }

    @Override
    public void onLoginSuccessful() {
        Navigator.moveToHome(this);
        finish();
    }

    @Override
    public void onShowError(String alert) {
        Toast.makeText(this,alert,Toast.LENGTH_SHORT).show();
    }

    @Override
    public String onGetName() {
        return edtName.getText().toString();
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
