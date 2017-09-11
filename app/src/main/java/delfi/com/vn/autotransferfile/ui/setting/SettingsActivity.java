package delfi.com.vn.autotransferfile.ui.setting;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.apache.commons.validator.routines.UrlValidator;

import delfi.com.vn.autotransferfile.R;
import delfi.com.vn.autotransferfile.common.application.BaseApplication;
import dk.delfi.core.common.activity.BaseActivity;

public class SettingsActivity extends BaseActivity {
    public static final String TAG = SettingsActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MainPreferenceFragment()).commit();
        setDisplayHomeAsUpEnabled(true);

    }

    public static class MainPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_main);
            // gallery EditText change listener
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pre_ip)));
        }
    }

    private static void bindPreferenceSummaryToValue(Preference preference) {
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */

    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
             String stringValue = newValue.toString();
             if (preference instanceof EditTextPreference) {
                if (preference.getKey().equals("key_ip_server")) {
                    UrlValidator urlValidator = new UrlValidator();
                    if(urlValidator.isValid(stringValue)) {
                        preference.setSummary(stringValue);
                        BaseApplication.onUpdatedServerIP(stringValue);
                    }
                    else{
                        Toast.makeText(preference.getContext(),"Please check IP or Domain name !!!",Toast.LENGTH_SHORT).show();
                    }
                    Log.d(TAG,"changed value : " + stringValue);
                }
            }
            return true;
        }
    };


}
