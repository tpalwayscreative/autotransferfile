package delfi.com.vn.autotransferfile.ui.autodetail;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;

import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import delfi.com.vn.autotransferfile.Constant;
import delfi.com.vn.autotransferfile.R;
import delfi.com.vn.autotransferfile.model.CAuToUpload;
import delfi.com.vn.autotransferfile.model.CFolder;
import delfi.com.vn.autotransferfile.ui.autoupload.AutoUploadPresenter;
import delfi.com.vn.autotransferfile.ui.autoupload.AutoUploadRemote;
import dk.delfi.core.common.activity.BaseActivity;
import dk.delfi.core.ui.recycleview.DPRecyclerView;

public class AutoDetailActivity extends AutoDetailRemote {
    public static final String TAG = AutoDetailActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_detail);
        adapter = DPRecyclerView.instance(this, recyclerView, R.layout.autodetail_items, this, LinearLayoutManager.VERTICAL).adapterRecycleView();
        recyclerView.setAdapter(adapter);
        setDisplayHomeAsUpEnabled(true);
        presenter = new AutoDetailPresenter(this);
        presenter.bindView(this);
        Bundle bundle = getIntent().getExtras();
        CFolder auToUpload = (CFolder) bundle.get(Constant.TAG_AUTO_UPLOAD);
        if (auToUpload!=null){
            setTitle(auToUpload.folder_name);
            presenter.getListFile(auToUpload.path_folder_name);
        }

    }

    @Override
    public void onShowListObjects(List<File> list) {
        Log.d(TAG,"New json : "+ new Gson().toJson(list));
       adapter.setDataSource(new ArrayList(list));
    }

    @Override
    public Context getContext() {
        return getApplicationContext();
    }
}
