package delfi.com.vn.autotransferfile.ui.autoupload;

import android.content.Context;
import android.content.Intent;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import delfi.com.vn.autotransferfile.Constant;
import delfi.com.vn.autotransferfile.R;
import delfi.com.vn.autotransferfile.common.PermissionUtils;
import delfi.com.vn.autotransferfile.common.dialog.MaterialMultiSelectListPreference;
import delfi.com.vn.autotransferfile.common.utils.FileUtil;
import delfi.com.vn.autotransferfile.model.CAuToUpload;
import delfi.com.vn.autotransferfile.service.AutoService;
import dk.delfi.core.ui.recycleview.DPRecyclerView;

public class AutoUploadActivity extends AutoUploadRemote{

    public static final String TAG = AutoUploadActivity.class.getSimpleName();
    @BindView(R.id.rcAuto)
    RecyclerView recyclerView ;
    private Unbinder unbinder ;
    private Intent intent ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_upload);
        adapter = DPRecyclerView.instance(this,recyclerView,R.layout.autoupdate_items,this, LinearLayoutManager.VERTICAL).adapterRecycleView();
        recyclerView.setAdapter(adapter);
        presenter = new AutoUploadPresenter(this);
        presenter.bindView(this);
        presenter.onShowList();
        PermissionUtils.checkAndRequestPermissions(this);
        intent = new Intent(this, AutoService.class );
        startService(intent);

    }


//    @Override
//    public void onChecked(int position, boolean isEnable) {
//        Log.d(TAG,"Show :"+ isEnable);
//        presenter.getList().get(position).isEnable = isEnable;
//    }

    public static class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener,MaterialMultiSelectListPreference.BeepSettingPreferencesListener {
        MaterialMultiSelectListPreference preference ;
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
            preference = (MaterialMultiSelectListPreference)findPreference(getString(R.string.key_gallery_name));
            preference.setListener(this);
            preference.setOnPreferenceChangeListener(this);
        }

        @Override
        public void onClosed() {
            Log.d(TAG,"Save");
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object o) {
            if (preference instanceof MaterialMultiSelectListPreference){
                MaterialMultiSelectListPreference array = (MaterialMultiSelectListPreference) preference;
                Log.d(TAG,"Log here : "+ new Gson().toJson(array.getEntries()));
            }
            return true;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_SettingScanner :
                if (FileUtil.mDeleteFile(this,Constant.LIST_FILE)){
                    if (FileUtil.mCreateAndSaveFile(this, Constant.LIST_FILE,new Gson().toJson(presenter.getList()))){
                        Toast.makeText(this,"Synchronized successfully",Toast.LENGTH_SHORT).show();
                        stopService(intent);
                        startService(intent);
                    }else{
                        Toast.makeText(this,"Synchronized failed",Toast.LENGTH_SHORT).show();
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
        unbinder.unbind();
    }

    @Override
    public Context getContext() {
        return getApplicationContext();
    }


}
