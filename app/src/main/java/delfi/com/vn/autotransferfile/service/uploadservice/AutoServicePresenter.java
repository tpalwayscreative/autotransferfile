package delfi.com.vn.autotransferfile.service.uploadservice;
import android.content.Context;
import android.util.Log;
import com.google.gson.Gson;
import com.snatik.storage.Storage;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import delfi.com.vn.autotransferfile.BuildConfig;
import delfi.com.vn.autotransferfile.R;
import delfi.com.vn.autotransferfile.common.api.ServerAPI;
import delfi.com.vn.autotransferfile.common.utils.NetworkUtil;
import delfi.com.vn.autotransferfile.model.CFileDocument;
import delfi.com.vn.autotransferfile.model.CUser;
import delfi.com.vn.autotransferfile.ui.autodetail.AutoDetailPresenter;
import delfi.com.vn.autotransferfile.ui.user.UserView;
import dk.delfi.core.Dependencies;
import dk.delfi.core.common.controller.RealmController;
import dk.delfi.core.common.presenter.Presenter;
import okhttp3.ResponseBody;
import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by PC on 9/6/2017.
 */

public class AutoServicePresenter extends Presenter<AutoServiceView> implements RealmController.RealmControllerListener,Dependencies.DependenciesListener{

    private Context context ;
    private ServerAPI serverAPI ;
    private List<CFileDocument>fileDocumentList;
    private RealmController realmController ;
    private String folder_name;
    private String author = null ;
    private Storage storage ;
    public static final String TAG = AutoServicePresenter.class.getSimpleName();

    public AutoServicePresenter(Context context){
        this.context = context;
        Dependencies dependencies = Dependencies.getsInstance(context.getApplicationContext(), BuildConfig.BASE_URL_API);
        dependencies.dependenciesListener(this);
        dependencies.init();
        serverAPI = (ServerAPI) Dependencies.serverAPI;
        realmController = RealmController.with(this);
        realmController.getFirstItem(CUser.class);
        storage = new Storage(context);
        fileDocumentList = new ArrayList<>();
    }

    public void initRealm(){
        realmController.getFirstItem(CUser.class);
    }

    public void getAllFile(){
        AutoServiceView view = view();
        if (view == null) {
            return;
        }
        if (NetworkUtil.pingIpAddress(view.getContext())) {
            return;
        }

        subscriptions.add(serverAPI.getALLFile()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(() -> {
                    Log.d(TAG,"action 0");
                })
                .subscribe(onResponse -> {
                    Log.d(TAG,"json all list : "+ new Gson().toJson(onResponse));
                    fileDocumentList = onResponse.files;
                    realmController.clearAll(CFileDocument.class);
                    realmController.mInsertList(CFileDocument.class,fileDocumentList);
                    Log.d(TAG,"Get all file : " + new Gson().toJson(fileDocumentList));
                }, throwable -> {
                    if (throwable instanceof HttpException) {
                        ResponseBody body = ((HttpException) throwable).response().errorBody();
                        try {
                            JSONObject object = new JSONObject(body.string());

                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }else {
                        Log.d(TAG,"action 2");
                    }
                }));
    }

    @Override
    public void onShowRealmObject(Object cFileDocument) {
        if (cFileDocument instanceof CUser){
            cFileDocument = realmController.getRealm().copyFromRealm((CUser)cFileDocument);
            Log.d(TAG,"on Show Realm Object :" + new Gson().toJson(cFileDocument));
            this.device_id = ((CUser) cFileDocument).device_id;
            this.author =  ((CUser) cFileDocument).apiKey;
        }
    }

    @Override
    public void onShowRealmList(List list) {
        if (list!=null){
            fileDocumentList = realmController.getRealm().copyFromRealm(list);
            Log.d(TAG,"Must do : "+ new Gson().toJson(fileDocumentList));
        }
    }

    @Override
    public String onAuthorToken() {
        return this.author;
    }

    @Override
    public Class onObject() {
        return ServerAPI.class;
    }

    public Context getContext() {
        return context;
    }

    @Override
    public void onShowRealmCheck(boolean b) {

    }

    @Override
    public void onShowRealmQueryItem(Object o) {

    }

    @Override
    public void onRealmUpdated(Object o) {

    }

    @Override
    public void onRealmDeleted(boolean b) {

    }

    @Override
    public void onRealmInserted(Object o) {

    }

    @Override
    public void onRealmInsertedList(List list) {
        if (list != null){
            List<CFileDocument> list1= realmController.getRealm().copyFromRealm(list);
            Log.d(TAG,"Show inserted list : "+ new Gson().toJson(list1));
        }
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    private String device_id ;

    public String getFolder_name() {
        return folder_name;
    }

    public void setFolder_name(String folder_name) {
        this.folder_name = folder_name;
    }

    public List<CFileDocument> getFileDocumentList() {
        return fileDocumentList;
    }

    public void setFileDocumentList(List<CFileDocument> fileDocumentList) {
        this.fileDocumentList = fileDocumentList;
    }

    public RealmController getRealmController() {
        return realmController;
    }

    public void setRealmController(RealmController realmController) {
        this.realmController = realmController;
    }

    public Storage getStorage() {
        return storage;
    }

    public void setStorage(Storage storage) {
        this.storage = storage;
    }


}
