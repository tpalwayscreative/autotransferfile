package delfi.com.vn.autotransferfile.common.utils;
import android.content.Context;
import android.content.Intent;

import delfi.com.vn.autotransferfile.ui.autoupload.AutoUploadActivity;
import delfi.com.vn.autotransferfile.ui.home.HomeActivity;


/**
 * Created by PC on 8/3/2017.
 */

public class Navigator {

    public static void moveToHome(Context context){
        Intent intent = new Intent(context, AutoUploadActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

}
