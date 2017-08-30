package delfi.com.vn.autotransferfile.ui.autoupload;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import delfi.com.vn.autotransferfile.R;
import delfi.com.vn.autotransferfile.common.adapter.BaseAdapter;
import delfi.com.vn.autotransferfile.common.adapter.BaseHolder;
import delfi.com.vn.autotransferfile.model.CAuToUpload;

/**
 * Created by PC on 8/30/2017.
 */

public class AutoUploadAdapter extends BaseAdapter<CAuToUpload,BaseHolder> {

    private AutoUploadListener autoUploadListener ;
    private Activity activity ;

    public AutoUploadAdapter(LayoutInflater inflater, Activity activity, AutoUploadListener autoUploadListener) {
        super(inflater);
        this.activity = activity;
        this.autoUploadListener = autoUploadListener;
    }

    @Override
    public BaseHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new AutoUploadHolder(inflater.inflate(R.layout.autoupdate_items, parent, false));
    }

    public class AutoUploadHolder extends BaseHolder<CAuToUpload> {

        @BindView(R.id.llHome)
        RelativeLayout llHome;
        @BindView(R.id.tvName)
        TextView tvName;
        @BindView(R.id.ckEnable)
        CheckBox checkBox;
        @BindView(R.id.tvDetail)
        TextView tvFullPath;

        private int position;

        public AutoUploadHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void bind(CAuToUpload data, int position) {
            super.bind(data, position);
            this.position = position;
            tvName.setText(data.name);
            tvFullPath.setText(data.full_path);
            checkBox.setChecked(data.isEnable);

            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    checkBox.setOnCheckedChangeListener(null);
                    autoUploadListener.onChecked(position, b);
                    checkBox.setOnCheckedChangeListener(this);
                }
            });
        }

        @OnClick(R.id.llHome)
        public void onSelected() {
            autoUploadListener.onItemClicked(position);
        }
    }

    @Override
    public int getItemCount() {
        return dataSource.size();
    }

    public interface AutoUploadListener {
        void onItemClicked(int position);
        void onChecked(int position ,boolean isEnable);
    }

}
