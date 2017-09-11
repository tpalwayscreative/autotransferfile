package delfi.com.vn.autotransferfile.ui.autoupload;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import delfi.com.vn.autotransferfile.R;
import delfi.com.vn.autotransferfile.model.CAuToUpload;
import delfi.com.vn.autotransferfile.model.CFolder;
import delfi.com.vn.autotransferfile.service.downloadservice.DownloadService;
import dk.delfi.core.common.activity.BaseActivity;
import dk.delfi.core.ui.recycleview.DPRecyclerView;
import dk.delfi.core.ui.recycleview.RecyclerViewAdapter;
import dk.delfi.core.ui.swipereveallayout.SwipeRevealLayout;

/**
 * Created by PC on 8/31/2017.
 */

public abstract class AutoUploadRemote  extends BaseActivity implements DPRecyclerView.RecycleViewListener, AutoUploadView, CompoundButton.OnCheckedChangeListener{

    protected RecyclerViewAdapter adapter ;
    protected AutoUploadPresenter presenter ;
    protected ViewHolder viewHolder ;
    protected DownloadService downloadService ;
    @BindView(R.id.rcAuto)
    protected RecyclerView recyclerView;

    public AutoUploadRemote() {
        super();
    }

    @Override
    public void onShowPosition(int i) {

    }

    @Override
    public void onShowData(Object o, int i) {
        CFolder data = (CFolder) o;
        viewHolder.checkBox.setOnCheckedChangeListener(null);
        viewHolder.checkBox.setChecked(data.isEnable);
        viewHolder.checkBox.setOnCheckedChangeListener(this);
        viewHolder.tvName.setText(data.folder_name);
        viewHolder.tvFullPath.setText(data.path_folder_name);
        viewHolder.checkBox.setChecked(data.isEnable);
    }

    @Override
    public void onView(View view) {
        viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
    }

    @Override
    public View onCustomView() {
        return null;
    }

    @Override
    public void onChangedSwitch(int i, boolean b) {

    }

    @Override
    public void onChangedSeekBar(int i, int i1) {

    }

    @Override
    public RelativeLayout onRelativeLayout() {
        return viewHolder.llHome;
    }

    @Override
    public LinearLayout onLinearLayout() {
        return null;
    }

    @Override
    public SeekBar onSeekBar() {
        return null;
    }

    @Override
    public Switch onSwitch() {
        return null;
    }

    @Override
    public List<View> onListView() {
        return null;
    }

    @Override
    public void onShowMultipleView(int i, View view) {

    }

    @Override
    public SwipeRevealLayout onSwipeRevealLayout() {
        return null;
    }

    @Override
    public void onOpened(SwipeRevealLayout swipeRevealLayout, int i) {

    }

    @Override
    public void onSlide(SwipeRevealLayout swipeRevealLayout, float v, int i) {

    }

    @Override
    public void onClosed(SwipeRevealLayout swipeRevealLayout, int i) {

    }

    @Override
    public void onCheckedChangedCheckBox(int i, boolean b) {

    }

    @Override
    public CheckBox onCheckBox() {
        return viewHolder.checkBox;
    }

    @Override
    public void onLoadingMore() {

    }

    @Override
    public boolean onEnableLoadMore() {
        return false;
    }

    @Override
    public void onShowList(List<CFolder> list) {

    }

    @Override
    public void onShowError(String alert) {

    }

    @Override
    public void onShowListObjects(List<CFolder> list) {

    }

    @Override
    public void onShowObjects(CFolder cFolder) {

    }

    @Override
    public List<CFolder> onGetListObjects() {
        return null;
    }

    @Override
    public CFolder onGetObjects() {
        return null;
    }

    @Override
    public void onShowLoading() {

    }

    @Override
    public void onHideLoading() {

    }

    @Override
    public Context getContext() {
        return null;
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

    }

    protected class ViewHolder{
        @BindView(R.id.llHome)
        RelativeLayout llHome;
        @BindView(R.id.tvName)
        TextView tvName;
        @BindView(R.id.ckEnable)
        CheckBox checkBox;
        @BindView(R.id.tvDetail)
        TextView tvFullPath;
        public ViewHolder(View view){
            ButterKnife.bind(this,view);
        }
    }


}
