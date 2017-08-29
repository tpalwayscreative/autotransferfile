package delfi.com.vn.autotransferfile.ui.autoupload;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import delfi.com.vn.autotransferfile.R;
import delfi.com.vn.autotransferfile.common.PermissionUtils;
import delfi.com.vn.autotransferfile.service.AutoService;

public class AutoUploadActivity extends AppCompatActivity {

    public static final String TAG = AutoUploadActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_upload);
        PermissionUtils.checkAndRequestPermissions(this);
        Intent intent = new Intent(this, AutoService.class );
        startService(intent);

        if (getFragmentManager().findFragmentById(android.R.id.content) == null) {
            getFragmentManager()
                    .beginTransaction()
                    .replace(android.R.id.content, new SettingsFragment())
                    .commit();
        }
    }

    public static class SettingsFragment extends PreferenceFragment implements DialogInterface.OnCancelListener {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
            bindPreferenceSummaryToValue(findPreference(getString(R.string.key_gallery_name)));


        }

        @Override
        public void onCancel(DialogInterface dialogInterface) {
            Log.d(TAG,"Cancel");
        }
    }

    private static void bindPreferenceSummaryToValue(Preference preference) {

    }

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */

    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            String stringValue = newValue.toString();
            if (preference instanceof MultiSelectListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                MultiSelectListPreference listPreference = (MultiSelectListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);
            }

            return true;
        }

    };



    static private DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    //Yes button clicked
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    //No button clicked
                    break;
            }
        }


    };


}
