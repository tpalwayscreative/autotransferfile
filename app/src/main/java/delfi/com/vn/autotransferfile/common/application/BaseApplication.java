package delfi.com.vn.autotransferfile.common.application;
import android.support.multidex.MultiDexApplication;
import delfi.com.vn.autotransferfile.BuildConfig;
import delfi.com.vn.autotransferfile.common.api.ServerAPI;
import dk.delfi.core.Dependencies;
import io.realm.Realm;
import timber.log.Timber;

public class BaseApplication extends MultiDexApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(getApplicationContext());
        Dependencies dependencies = Dependencies.getsInstance(getApplicationContext(), BuildConfig.BASE_URL_API);
        dependencies.dependenciesListener(new Dependencies.DependenciesListener() {
            @Override
            public Class onObject() {
                return ServerAPI.class;
            }

            @Override
            public String onAuthorToken() {
                return null;
            }
        });
        dependencies.init();
        if (BuildConfig.DEBUG){
            Timber.Tree tree = new Timber.DebugTree();
            Timber.plant(tree);
        }
    }
}
