package delfi.com.vn.autotransferfile.common.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import delfi.com.vn.autotransferfile.model.CAuToUpload;
import delfi.com.vn.autotransferfile.model.CAutoFileOffice;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by hdadmin on 2/7/2017.
 */

public class FileUtil implements FileFilter {

    /**
     * Get a usable cache directory (external if available, internal otherwise).
     *
     * @param context The context to use
     * @param uniqueName A unique directory name to append to the cache dir
     * @return The cache dir
     */

    public  static  final String TAG = FileUtil.class.getSimpleName();
    private static final String[] okFileExtensions =  new String[] {"jpg", "png", "gif","jpeg"};
    public static final String USER_BEEPS_PATH = (Environment.getExternalStorageDirectory().getAbsolutePath() + "/ScannerBeep");
    private static FileUtil instance ;
    private Context context ;


    public static File getDiskCacheDir(Context context, String uniqueName) {
        // Check if media is mounted or storage is built-in, if so, try and use external cache dir
        // otherwise use internal cache dir
        final String cachePath =
                Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) ||
                        !isExternalStorageRemovable() ? getExternalCacheDir(context).getPath() :
                        context.getCacheDir().getPath();

        return new File(cachePath + File.separator + uniqueName);
    }

    @TargetApi(Build.VERSION_CODES.FROYO)
    public static File getExternalCacheDir(Context context) {
        if (hasFroyo()) {
            return context.getExternalCacheDir();
        }

        // Before Froyo we need to construct the external cache dir ourselves
        final String cacheDir = "/Android/data/" + context.getPackageName() + "/cache/";
        return new File(Environment.getExternalStorageDirectory().getPath() + cacheDir);
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public static boolean isExternalStorageRemovable() {
        if (hasGingerbread()) {
            return Environment.isExternalStorageRemovable();
        }
        return true;
    }

    public static boolean hasFroyo() {
        // Can use static final constants like FROYO, declared in later versions
        // of the OS since they are inlined at compile time. This is guaranteed behavior.
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;
    }

    public static boolean hasGingerbread() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;
    }

    public static boolean hasHoneycomb() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    }

    public static boolean hasHoneycombMR1() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1;
    }

    public static boolean hasJellyBean() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    }

    public static boolean hasKitKat() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }

    public static boolean mCreateAndSaveFile(Context context,String fileName,String responseJson) {
          try{
                File root = new File("/data/data/" + context.getPackageName() + "/" + fileName);
                if (!root.exists()){
                    root.createNewFile();
                }

                FileWriter file = new FileWriter(root,false);
                file.write(responseJson);
                file.flush();
                file.close();
                return true ;
            } catch (IOException e) {
                e.printStackTrace();
                return false ;
            }
    }

    public static List<CAuToUpload> mReadJsonDataSettingButton(Context context,String fileName) {
            try {
                File f = new File("/data/data/" + context.getPackageName() + "/" + fileName);
                FileInputStream is = new FileInputStream(f);
                int size = is.available();
                byte[] buffer = new byte[size];
                is.read(buffer);
                is.close();
                String mResponse = new String(buffer);
                Gson gson = new Gson();
                TypeToken<List<CAuToUpload>> token = new TypeToken<List<CAuToUpload>>() {};
                List<CAuToUpload> list = gson.fromJson(mResponse,token.getType());
                return list;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;

    }

    public static List<CAutoFileOffice> mReadJsonDataFileOffice(Context context, String fileName) {
        try {
            File f = new File("/data/data/" + context.getPackageName() + "/" + fileName);
            FileInputStream is = new FileInputStream(f);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String mResponse = new String(buffer);
            Gson gson = new Gson();
            TypeToken<List<CAutoFileOffice>> token = new TypeToken<List<CAutoFileOffice>>() {};
            List<CAutoFileOffice> list = gson.fromJson(mResponse,token.getType());
            return list;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();

    }

    public static boolean mCheckFileExisting(Context context,String fileName){
        File root = new File("/data/data/" + context.getPackageName() + "/" + fileName);
        if (root.exists()){
            return true;
        }
        return false;
    }

    public static boolean mDeleteFile(Context context,String fileName) {
        try {
            File root = new File("/data/data/" + context.getPackageName() + "/" + fileName);
            if (root.exists()){
                return root.delete();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean accept(File file) {
        return false;
    }


    public static boolean fileAccept(File file){
        for (String extension : okFileExtensions)
        {
            if (file.getName().toLowerCase().endsWith(extension))
            {
                return true;
            }
        }
        return false;
    }

    public static String getMacAddress() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;
                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }
                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:",b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
        }
        return "02:00:00:00:00:00";
    }

    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }


    private static String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }

}
