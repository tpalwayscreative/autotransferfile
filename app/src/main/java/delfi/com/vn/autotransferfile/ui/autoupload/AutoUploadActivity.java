package delfi.com.vn.autotransferfile.ui.autoupload;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
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
import delfi.com.vn.autotransferfile.common.utils.FileUtil;
import delfi.com.vn.autotransferfile.common.utils.Navigator;
import delfi.com.vn.autotransferfile.model.CAuToUpload;
import delfi.com.vn.autotransferfile.model.CFolder;
import delfi.com.vn.autotransferfile.service.AutoService;
import dk.delfi.core.ui.recycleview.DPRecyclerView;

public class AutoUploadActivity extends AutoUploadRemote {

    public static final String TAG = AutoUploadActivity.class.getSimpleName();
    private Intent intent ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_upload);
        adapter = DPRecyclerView.instance(this, recyclerView, R.layout.autoupload_items, this, LinearLayoutManager.VERTICAL).adapterRecycleView();
        recyclerView.setAdapter(adapter);
        presenter = new AutoUploadPresenter(this);
        presenter.bindView(this);
        presenter.getRealmController().getALLObject(CFolder.class,100);
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
                    presenter.getRealmController().clearAll(CFolder.class,0);
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
                Navigator.moveToUser(getContext());
                finish();
                return true;
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
    public void onShowList(List<CFolder> list) {
        super.onShowList(list);
        adapter.setDataSource(new ArrayList(list));
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
}
