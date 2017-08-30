package delfi.com.vn.autotransferfile.ui.autoupload;

import android.app.Activity;
import android.os.Environment;
import android.util.Log;

import com.google.gson.Gson;
import com.snatik.storage.Storage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import delfi.com.vn.autotransferfile.Constant;
import delfi.com.vn.autotransferfile.common.presenter.Presenter;
import delfi.com.vn.autotransferfile.common.utils.FileUtil;
import delfi.com.vn.autotransferfile.model.CAuToUpload;

/**
 * Created by PC on 8/30/2017.
 */

public class AutoUploadPresenter extends Presenter<AutoUploadView>  {


    private List<CAuToUpload>list ;
    private Activity activity ;
    private Storage storage;
    public static final String TAG = AutoUploadPresenter.class.getSimpleName();

    public AutoUploadPresenter(Activity activity){
        this.activity = activity;
        list = new ArrayList<>();
        storage = new Storage(activity);
    }

    public void onShowList(){
        AutoUploadView view = view();
        list = FileUtil.mReadJsonDataSettingButton(activity,Constant.LIST_FILE);
        view.onShowList(list);
    }

    public void setList(List<CAuToUpload> list) {
        this.list = list;
    }

    public Storage getStorage() {
        return storage;
    }

    public void setStorage(Storage storage) {
        this.storage = storage;
    }

    public List<CAuToUpload> getList() {
        return list;
    }

}
