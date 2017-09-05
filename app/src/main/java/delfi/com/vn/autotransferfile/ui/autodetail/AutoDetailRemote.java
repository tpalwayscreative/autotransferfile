package delfi.com.vn.autotransferfile.ui.autodetail;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import delfi.com.vn.autotransferfile.R;
import delfi.com.vn.autotransferfile.model.CAuToUpload;
import delfi.com.vn.autotransferfile.ui.autoupload.AutoUploadPresenter;
import delfi.com.vn.autotransferfile.ui.autoupload.AutoUploadRemote;
import dk.delfi.core.common.activity.BaseActivity;
import dk.delfi.core.common.ui.FontTextView;
import dk.delfi.core.ui.recycleview.DPRecyclerView;
import dk.delfi.core.ui.recycleview.RecyclerViewAdapter;
import dk.delfi.core.ui.swipereveallayout.SwipeRevealLayout;

/**
 * Created by PC on 9/5/2017.
 */

public class AutoDetailRemote extends BaseActivity implements AutoDetailView, DPRecyclerView.RecycleViewListener {

    protected RecyclerViewAdapter adapter ;
    protected AutoDetailPresenter presenter ;
    protected ViewHolder viewHolder ;
    @BindView(R.id.recyclerAutoDetail)
    protected RecyclerView recyclerView;

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
    public void onShowListObjects(List<File> list) {

    }

    @Override
    public void onShowObjects(File file) {

    }

    @Override
    public List<File> onGetListObjects() {
        return null;
    }

    @Override
    public File onGetObjects() {
        return null;
    }

    @Override
    public void onShowPosition(int i) {

    }

    @Override
    public void onShowData(Object o, int i) {
        File file = (File)o;
        viewHolder.tvName.setText(file.getName());
        if (file.isFile()){
            Picasso.with(this).load(new File(file.getAbsolutePath())).resize(50,50).centerCrop().into(viewHolder.imageView);
        }
        else{
            viewHolder.imageView.setColorFilter(ContextCompat.getColor(this, android.R.color.holo_orange_light),
                    PorterDuff.Mode.MULTIPLY);
        }

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
        return null;
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
        return null;
    }

    @Override
    public void onLoadingMore() {

    }

    @Override
    public boolean onEnableLoadMore() {
        return false;
    }

    protected class ViewHolder{
        @BindView(R.id.llAutoDetail)
        LinearLayout llHome;
        @BindView(R.id.tvAutoDetail)
        FontTextView tvName;
        @BindView(R.id.imgAutoDetail)
        ImageView imageView;
        public ViewHolder(View view){
            ButterKnife.bind(this,view);
        }
    }




}
