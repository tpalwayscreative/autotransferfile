package delfi.com.vn.autotransferfile.ui.autodetail;
import android.app.Activity;
import android.support.annotation.Nullable;
import android.util.Log;

import com.snatik.storage.Storage;
import com.snatik.storage.helpers.OrderType;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import delfi.com.vn.autotransferfile.ui.autoupload.AutoUploadRemote;
import dk.delfi.core.common.presenter.Presenter;

/**
 * Created by PC on 9/5/2017.
 */

public class AutoDetailPresenter extends Presenter<AutoDetailRemote> {


    private List<File> list ;
    private Activity activity ;
    private Storage storage;

    public AutoDetailPresenter(Activity activity){
        this.activity = activity;
        list = new ArrayList<>();
        storage = new Storage(activity);
    }

    public void getListFile(String path){
        List<File> files = storage.getFiles(path);
        AutoDetailRemote view = view();
        list.addAll(files);
        view.onShowListObjects(files);
    }

    public List<File> getList() {
        return list;
    }

    public void setList(List<File> list) {
        this.list = list;
    }




}
