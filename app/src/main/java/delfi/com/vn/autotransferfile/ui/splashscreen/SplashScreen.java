package delfi.com.vn.autotransferfile.ui.splashscreen;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import java.util.List;
import delfi.com.vn.autotransferfile.R;
import delfi.com.vn.autotransferfile.common.utils.Navigator;
import delfi.com.vn.autotransferfile.model.CUser;
import dk.delfi.core.common.activity.BaseActivity;
import dk.delfi.core.common.controller.RealmController;

public class SplashScreen extends BaseActivity implements RealmController.RealmControllerListener<CUser>{

    Handler handler ;
    private RealmController instance ;
    public static final String TAG = SplashScreen.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        handler = new Handler();
        instance = RealmController.with(this);
        instance.getFirstItem(CUser.class);
    }

    @Override
    public void onShowRealmObject(CUser cUser) {
        Log.d(TAG,"ShowRealmObject : "+cUser);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (cUser!=null){
                    Navigator.moveToHome(getApplicationContext());
                }
                else{
                    Navigator.moveToUser(getApplicationContext());
                }
                finish();
            }
        },2000);
    }

    @Override
    public void onShowRealmList(List<CUser> list) {

    }

    @Override
    public void onShowRealmCheck(boolean b) {

    }

    @Override
    public void onShowRealmQueryItem(CUser cUser) {

    }

    @Override
    public void onRealmInsertedList(List<CUser> list) {

    }

    @Override
    public void onRealmUpdated(CUser cUser) {

    }

    @Override
    public void onRealmDeleted(boolean b) {

    }

    @Override
    public void onRealmInserted(CUser cUser) {

    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
