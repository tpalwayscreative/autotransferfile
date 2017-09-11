package delfi.com.vn.autotransferfile.ui.autoupload;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import delfi.com.vn.autotransferfile.Constant;
import delfi.com.vn.autotransferfile.R;
import delfi.com.vn.autotransferfile.common.PermissionUtils;
import delfi.com.vn.autotransferfile.common.api.request.FileDocumentRequest;
import delfi.com.vn.autotransferfile.common.utils.FileUtil;
import delfi.com.vn.autotransferfile.common.utils.Navigator;
import delfi.com.vn.autotransferfile.common.utils.NetworkUtil;
import delfi.com.vn.autotransferfile.model.CAuToUpload;
import delfi.com.vn.autotransferfile.model.CFileDocument;
import delfi.com.vn.autotransferfile.model.CFolder;
import delfi.com.vn.autotransferfile.model.CUser;
import delfi.com.vn.autotransferfile.service.AutoService;
import delfi.com.vn.autotransferfile.service.downloadservice.DownloadService;
import dk.delfi.core.ui.recycleview.DPRecyclerView;
import io.realm.Realm;

public class AutoUploadActivity extends AutoUploadRemote implements DownloadService.DownLoadServiceListener {

    public static final String TAG = AutoUploadActivity.class.getSimpleName();
    private Intent intent ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_upload);
        adapter = DPRecyclerView.instance(this, recyclerView, R.layout.autoupload_items, this, LinearLayoutManager.VERTICAL).adapterRecycleView();
        recyclerView.setAdapter(adapter);
        presenter = new AutoUploadPresenter(this);
        downloadService = new DownloadService(this);
        presenter.bindView(this);
        presenter.onShowData();
        PermissionUtils.checkAndRequestPermissions(this);
        intent = new Intent(this, AutoService.class);
        startService(intent);
    }

    @Override
    public void onCheckedChangedCheckBox(int i, boolean b) {
        super.onCheckedChangedCheckBox(i, b);
        presenter.getListFolder().get(i).isEnable = b;
    }

    @Override
    public void onShowPosition(int i) {
        super.onShowPosition(i);
        Navigator.moveToAutoDetail(this,presenter.getListFolder().get(i));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_SettingScanner : {
                    List<CFolder> list = presenter.getRealmController().mInsertList(CFolder.class,presenter.getListFolder());
                    if (list!=null){
                        presenter.setListFolder(list);
                    }
                    Toast.makeText(this, "Saved successfully", Toast.LENGTH_SHORT).show();
                    stopService(intent);
                    startService(intent);
                return true;
            }
            case R.id.action_Logout :{
                presenter.onLogout(presenter.getcUser());
                return true;
            }
            case R.id.action_syn_data :{
                if (presenter.isCheckingSynData()){
                    FileDocumentRequest fileDocumentRequest = new FileDocumentRequest(presenter.getFileDocument().file_document_id);
                    presenter.getLatestFile(fileDocumentRequest);
                }else{
                    Log.d(TAG,"Getting all file ");
                    presenter.getAllFile();
                }
                return true;
            }

            case R.id.action_created_folder :{
                Navigator.moveToCreatingFolder(this);
                return true;
            }
            case R.id.action_settings : {
                Navigator.moveToSettings(this);
                return true ;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDownLoadNow() {
        if (NetworkUtil.pingIpAddress(getContext())) {
            return;
        }
        for (int i = 0 ;i<presenter.getFileDocumentList().size();i++){
            downloadService.onProgressingDownload(this);
            downloadService.intDownLoad(i,presenter.getFileDocumentList().get(i).file_name,presenter.getFileDocumentList().get(i).parent_folder_name,presenter.getFileDocumentList().get(i).path_folder_name);
        }
    }

    @Override
    public void onShowList(List<CFolder> list) {
        super.onShowList(list);
        adapter.setDataSource(new ArrayList(list));
    }

    @Override
    public void onLogoutSuccessfully() {
        Navigator.moveToUser(this);
        finish();
    }

    @Override
    public Context getContext() {
        return getApplicationContext();
    }

    @Override
    protected void onResume() {
        super.onResume();
        DPRecyclerView.onRefresh(this,recyclerView,R.layout.autoupload_items,this, LinearLayoutManager.VERTICAL);
    }

    @Override
    public void onDownLoadCompleted(int count) {
        if (presenter.getFileDocumentList().size()==0){
            Log.d(TAG,"Array is null");
            return;
        }

        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                CFileDocument fileDocument =  realm.copyToRealmOrUpdate(presenter.getFileDocumentList().get(count));
                fileDocument = realm.copyFromRealm(fileDocument);
                Log.d(TAG,"Insert successful" + new Gson().toJson(fileDocument));
            }
        });
        if (count == presenter.getFileDocumentList().size()-1){
            Log.d(TAG,"onDownload Finish : " + presenter.getFileDocumentList().size());
            realm.close();
        }
        else{
            Log.d(TAG,"onDownload Completed");
        }
    }

    @Override
    public void onProgressingDownloading(int percent) {
        Log.d(TAG," Progressing : "+percent);
    }

}
