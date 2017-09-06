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
import delfi.com.vn.autotransferfile.service.AutoService;
import dk.delfi.core.ui.recycleview.DPRecyclerView;

public class AutoUploadActivity extends AutoUploadRemote {

    public static final String TAG = AutoUploadActivity.class.getSimpleName();
    private Intent intent ;
    @BindView(R.id.fab)
    FloatingActionButton floatingActionButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_upload);
        adapter = DPRecyclerView.instance(this, recyclerView, R.layout.autoupload_items, this, LinearLayoutManager.VERTICAL).adapterRecycleView();
        recyclerView.setAdapter(adapter);
        presenter = new AutoUploadPresenter(this);
        presenter.bindView(this);
        presenter.onShowList();
        PermissionUtils.checkAndRequestPermissions(this);
        intent = new Intent(this, AutoService.class);
        startService(intent);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigator.moveToUser(getContext());
                finish();
            }
        });

    }

    @Override
    public void onCheckedChangedCheckBox(int i, boolean b) {
        super.onCheckedChangedCheckBox(i, b);
        presenter.getList().get(i).isEnable = b;
    }

    @Override
    public void onShowPosition(int i) {
        super.onShowPosition(i);
        Navigator.moveToAutoDetail(this,presenter.getList().get(i));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_SettingScanner :
                if (FileUtil.mDeleteFile(this,Constant.LIST_FILE)){
                    if (FileUtil.mCreateAndSaveFile(this, Constant.LIST_FILE,new Gson().toJson(presenter.getList()))){
                        Toast.makeText(this,"Saved successfully",Toast.LENGTH_SHORT).show();
                        stopService(intent);
                        startService(intent);
                    }else{
                        Toast.makeText(this,"Saved failed",Toast.LENGTH_SHORT).show();
                    }
                }
               return true ;
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
    public void onShowList(List<CAuToUpload> list) {
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
