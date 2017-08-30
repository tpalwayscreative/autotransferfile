package delfi.com.vn.autotransferfile.common.utils;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;
import com.google.gson.Gson;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import delfi.com.vn.autotransferfile.BuildConfig;
import delfi.com.vn.autotransferfile.Constant;


/**
 * Created by PC on 7/20/2017.
 */

public class SharePreferencesFile {

    static private SharePreferencesFile instance ;
    private SharedPreferences sharedPreferences ;
    private SharedPreferences.Editor editor ;

    private Context context ;
    private void setContext(Context context){
        this.context = context;
    }

    public static SharePreferencesFile with(Context context){
        if (instance==null){
            instance = new SharePreferencesFile();
        }
        instance.setContext(context);
        return  instance ;
    }

    public void initSharePreferences(){
        if (context==null){
            return;
        }
        sharedPreferences = context.getSharedPreferences(BuildConfig.APPLICATION_ID,context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public SharedPreferences preferences(){
        return sharedPreferences;
    }

    public void onPutValue(String key,String value){
        editor.putString(key,value);
        editor.commit();
    }

    public String onGetValue(String key){
        String result = sharedPreferences.getString(key,null);
        return result ;
    }

    public void initDefaultValue(){
        try {
            Map<Integer,Boolean> hash = new HashMap<>();
            hash.put(0,false);
            hash.put(1,false);
            hash.put(2,false);
            editor.putString(Constant.LIST_FILE,new Gson().toJson(hash));
            editor.commit();
        }
        catch (Exception e){

        }
    }


}
