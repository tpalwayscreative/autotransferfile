package delfi.com.vn.autotransferfile.common.dialog;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

/**
 * Created by PC on 8/30/2017.
 */

public class MaterialMultiSelectListPreference extends MultiSelectListPreference{
    private BeepSettingPreferencesListener listener;
    private Context context;
    public MaterialMultiSelectListPreference(Context context) {
        super(context);
        init(context, null);
    }

    public void setListener(BeepSettingPreferencesListener listener){
        this.listener= listener;
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);
        this.listener.onClosed();
    }

    public MaterialMultiSelectListPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MaterialMultiSelectListPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MaterialMultiSelectListPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    @Override
    public void setEntries(CharSequence[] entries) {
        super.setEntries(entries);
    }

    private void init(Context context, AttributeSet attrs) {
        this.context = context;
    }
    public interface BeepSettingPreferencesListener{
        void onClosed();
    }

    @Override
    protected void showDialog(Bundle state) {
        super.showDialog(state);
    }

    @Override
    protected void onPrepareDialogBuilder(AlertDialog.Builder builder) {
        super.onPrepareDialogBuilder(builder);
    }

}
