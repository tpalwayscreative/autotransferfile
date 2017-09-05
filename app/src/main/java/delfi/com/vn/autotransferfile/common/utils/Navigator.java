package delfi.com.vn.autotransferfile.common.utils;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import java.io.Serializable;

import delfi.com.vn.autotransferfile.Constant;
import delfi.com.vn.autotransferfile.model.CAuToUpload;
import delfi.com.vn.autotransferfile.ui.autodetail.AutoDetailActivity;
import delfi.com.vn.autotransferfile.ui.autoupload.AutoUploadActivity;
import delfi.com.vn.autotransferfile.ui.home.HomeActivity;
import delfi.com.vn.autotransferfile.ui.user.UserActivity;


/**
 * Created by PC on 8/3/2017.
 */

public class Navigator {

    public static void moveToHome(Context context){
        Intent intent = new Intent(context, AutoUploadActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void moveToUser(Context context){
        Intent intent = new Intent(context, UserActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }


    public static void moveToAutoDetail(Context context, CAuToUpload auto){
        Intent intent = new Intent(context, AutoDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constant.TAG_AUTO_UPLOAD,(Serializable)auto);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }



}
