package delfi.com.vn.autotransferfile.common.application;
import android.preference.PreferenceManager;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import delfi.com.vn.autotransferfile.BuildConfig;
import delfi.com.vn.autotransferfile.Constant;
import delfi.com.vn.autotransferfile.common.api.ServerAPI;
import delfi.com.vn.autotransferfile.model.CUser;
import dk.delfi.core.Dependencies;
import dk.delfi.core.common.controller.RealmController;
import io.realm.Realm;
import timber.log.Timber;

public class BaseApplication extends MultiDexApplication implements Dependencies.DependenciesListener {
    public static String BASE_IP ;
    public static final String TAG = BaseApplication.class.getSimpleName();
    protected static Dependencies dependencies;
    protected static RealmController realmController;
    public static ServerAPI serverAPI ;
    private static String author = null ;
    @Override
    public void onCreate() {
        super.onCreate();
        String ip= PreferenceManager.getDefaultSharedPreferences(this).getString(Constant.TAG_KEY_IP_SERVER, null);
        if (ip!=null && !ip.equals("")){
            BASE_IP = ip;
            Log.d(TAG,"ip here : "+ ip);
        }
        else{
            BASE_IP = delfi.com.vn.autotransferfile.BuildConfig.BASE_URL_API;
            Log.d(TAG,"ip here !!! : "+ BASE_IP);
        }
        Realm.init(getApplicationContext());
        realmController = RealmController.with();
        dependencies = Dependencies.getsInstance(getApplicationContext(),BASE_IP);
        dependencies.dependenciesListener(this);
        dependencies.init();
        serverAPI = (ServerAPI) Dependencies.serverAPI;

        if (BuildConfig.DEBUG){
            Timber.Tree tree = new Timber.DebugTree();
            Timber.plant(tree);
        }
    }

    public static void onUpdatedServerIP(String ip){
        if (ip!=null && !ip.equals("")){
            BASE_IP = ip;
            Log.d(TAG,"ip here : "+ ip);
        }
        else{
            BASE_IP = delfi.com.vn.autotransferfile.BuildConfig.BASE_URL_API;
            Log.d(TAG,"ip here !!! : "+ BASE_IP);
        }
        dependencies.changeApiBaseUrl(BASE_IP);
        serverAPI = (ServerAPI)Dependencies.serverAPI;
    }

    public static void onSetAuthor(String authors){
        author = authors;
    }

    @Override
    public String onAuthorToken() {
        CUser cUser =  (CUser) realmController.getFirstItem(CUser.class);
        if (cUser!=null){
            this.author = cUser.access_token;
        }
        return this.author;
    }

    @Override
    public Class onObject() {
        return ServerAPI.class;
    }
}
