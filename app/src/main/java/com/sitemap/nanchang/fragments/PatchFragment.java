package com.sitemap.nanchang.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sitemap.nanchang.R;
import com.sitemap.nanchang.activity.Location;
import com.sitemap.nanchang.activity.PatchDesActivity;
import com.sitemap.nanchang.adapter.PatchExpListViewAdapter;
import com.sitemap.nanchang.application.MyApplication;
import com.sitemap.nanchang.config.RequestCode;
import com.sitemap.nanchang.gps.GpsLocation;
import com.sitemap.nanchang.model.PatchExpModel;
import com.sitemap.nanchang.model.PatchModel;
import com.sitemap.nanchang.model.TaskPathModel;
import com.sitemap.nanchang.util.DialogUtil;
import com.sitemap.nanchang.util.FileNames;
import com.sitemap.nanchang.util.FileUtil;
import com.sitemap.nanchang.util.ParserUtil;
import com.sitemap.nanchang.util.ToastUtil;
import com.sitemap.nanchang.util.ZipUtil;
import com.sitemap.nanchang.view.MyProgressDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @desc 采集点 页面
 * Created by chenmeng on 2016/12/9.
 */
public class PatchFragment extends Fragment implements PatchExpListViewAdapter.OnCallBackUpdate {
    private View view;//视图
    private Context mContext;//本类
    private ExpandableListView mElv;//listview
    private List<PatchExpModel> mList = new ArrayList<>();//数据 总
    private PatchExpListViewAdapter mAdapter;//适配器
    private MyProgressDialog progressDialog;// 压缩显示的进度
    private  Handler handler=null;
    private int currPos = 0;//当前选择的位置
    private LinearLayout mLayout;//整个布局
//    private Location location;//定位
    private double lat = 0;//纬度
    private double lng = 0;//经度
    private GpsLocation location;//gps定位

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_patch, null);
        }
        mContext = inflater.getContext();
        mElv = (ExpandableListView) view.findViewById(R.id.patch_list);
        mLayout = (LinearLayout) view.findViewById(R.id.container);
//        getDate();
//        location = Location.instance();
//        location.start(mContext);
        location = new GpsLocation(mContext);
        location.start();

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (progressDialog!=null&&progressDialog.isShowing()){
                    progressDialog.dismiss();
                }
                switch(msg.what) {
                    case 101:
                        int pos = Integer.parseInt(msg.obj.toString());
                        mList.get(pos).setIsZip("1");
                        mList.get(pos).setIsAdd("2");
                        mList.get(pos).setIsStatus("1");
                        currPos = pos;
                        onResume();
                        ToastUtil.showBottomShort(mContext,"压缩成功");
                        break;
                }
            }
        };
        return view;
    }

    @Override
    public void onResume() {
        if (mAdapter!=null){
            mAdapter.refresh();
        }
        getDate();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        if(location!=null){
//            location.stop();
            location.close();
        }
        super.onDestroy();
    }

    /**
     * 遍历数据
     */
    private void getDate() {
        if (MyApplication.appFile.exists()) {
//            File upLoadFile = FileUtil.createOneFolder(MyApplication.appFile, RequestCode.GATHER);
//            File tempFile = FileUtil.createOneFolder(MyApplication.appFile, RequestCode.TEMP_GATHER);
            if (MyApplication.gatherFile.exists()) {
                mList.clear();
                for (int i = 0; i < MyApplication.gatherFile.listFiles().length; i++) {
                    if (!MyApplication.gatherFile.listFiles()[i].isFile()) {
                        String typeName = MyApplication.gatherFile.listFiles()[i].getName();
                        File typeNameFile = FileUtil.createOneFolder(MyApplication.gatherFile, typeName);
                        PatchExpModel patchExpModel = new PatchExpModel();
                        patchExpModel.setType(typeName);
                        patchExpModel.setTypeNamePath(typeNameFile.getPath());
                        patchExpModel.setZipPath(MyApplication.gatherFile.getPath());
                        patchExpModel.setIsZip("0");
                        patchExpModel.setIsAdd("1");
                        patchExpModel.setIsStatus("2");
                        patchExpModel.setPatchFile(MyApplication.gatherFile);
                        List<PatchModel> lTask = new ArrayList<PatchModel>();
                        List<TaskPathModel> lIsZip = new ArrayList<TaskPathModel>();
                        lIsZip.clear();
                        lTask.clear();
                        if (typeNameFile.exists()) {
                            if (typeNameFile.listFiles().length>0){
                                for (int j = 0; j < typeNameFile.listFiles().length; j++) {
                                    String number = typeNameFile.listFiles()[j].getName();
                                    File numberFile = FileUtil.createOneFolder(typeNameFile, number);
                                    File userTextFile = FileUtil.createOneFolder(numberFile, RequestCode.TEXT);
                                    TaskPathModel tpm = new TaskPathModel();
                                    PatchModel tm = new PatchModel();
                                    tpm.setUpload("0");
                                    tpm.setProjectName(number);
                                    tpm.setProjectPath(numberFile.getPath());
                                    if (userTextFile.exists()) {
                                        String text = FileUtil.readFilePath(userTextFile.getPath(), RequestCode.GATHERNAME);
                                        if (text != null) {
                                            tm = (PatchModel) ParserUtil.jsonToObject(text, PatchModel.class);
                                            tpm.setTextPath(userTextFile.getPath());
                                            tpm.setIsZip("0");
                                        }
                                    }
                                    File userImgFile = FileUtil.createOneFolder(numberFile, RequestCode.IMG);
                                    if (userImgFile.exists()) {
                                        if (userImgFile.listFiles().length > 0) {
                                            if (userImgFile.listFiles()[0].getName().contains(".jpg")) {
                                                tm.setImgPath(userImgFile.listFiles()[0].getPath());
                                                tpm.setJpgImgPath(userImgFile.listFiles()[0].getPath());
                                            }
                                        }
                                        tpm.setImgPath(userImgFile.getPath());
                                    }
//                                    File userVoiceFile = FileUtil.createOneFolder(numberFile, RequestCode.VOICE);
//                                    if (userVoiceFile.exists()) {
//                                        tpm.setVoicePath(userVoiceFile.getPath());
//                                    }
                                    File userVideoFile = FileUtil.createOneFolder(numberFile, RequestCode.VIDEO);
                                    if (userVideoFile.exists()) {
                                        tpm.setVideoPath(userVideoFile.getPath());
                                    }
                                    lTask.add(tm);
                                    lIsZip.add(tpm);
                                }
                                patchExpModel.setZipPathList(lIsZip);
                                patchExpModel.setPatchModelList(lTask);
                            }else{
                                patchExpModel.setPatchModelList(new ArrayList<PatchModel>());
                            }
                            mList.add(patchExpModel);
                        }
                    }
                }
                if (MyApplication.gatherTempFile.exists()) {
                    for (int i = 0; i < MyApplication.gatherTempFile.listFiles().length; i++) {
                        if (MyApplication.gatherTempFile.listFiles()[i].getName().contains(".zip")) {
                            for (int j = 0; j < mList.size(); j++) {
                                String typeName = MyApplication.gatherTempFile.listFiles()[i].getName().split("_")[1];
                                if (mList.get(j).getType().equals(typeName)) {
                                    mList.get(j).setIsZip("1");
                                    mList.get(j).setIsAdd("2");
                                    mList.get(j).setTmpZipPath(MyApplication.gatherTempFile.listFiles()[i].getPath());
                                }
                            }
                        }
                    }
                }
                initExpListView(mList,currPos);
            }
        }
    }

    /**
     * 更新数据
     *
     * @param typeName
     */
    public void addData(String typeName) {
        if (mList.size()>0){
            for (PatchExpModel model:mList){
                if (typeName.equals(model.getType())){
                    ToastUtil.showBottomShort(mContext,"类名已存在");
                    return;
                }
            }
        }
        if (MyApplication.gatherFile.exists()) {
            File typeNameFile = FileUtil.createOneFolder(MyApplication.gatherFile, typeName);
            PatchExpModel model = new PatchExpModel();
            model.setType(typeName);
            model.setPatchModelList(new ArrayList<PatchModel>());
            model.setIsAdd("1");
            model.setIsZip("0");
            model.setZipPathList(new ArrayList<TaskPathModel>());
            model.setZipPath("");
            model.setIsStatus("1");
            model.setPatchFile(MyApplication.gatherFile);
            model.setTypeNamePath(typeNameFile.getPath());
            mList.add(model);
            initExpListView(mList,mList.size()-1);
        }
    }

    /**
     * 初始数据
     *
     * @param list
     */
    private void initExpListView(List<PatchExpModel> list,int pos) {
        if (list.size() > 0) {
            mList.get(pos).setIsStatus("1");
            mAdapter = new PatchExpListViewAdapter(mContext, list, this);
            mElv.setAdapter(mAdapter);
            // 默认展开第一条
//            for (int i = 0; i < list.size(); i++) {
                mElv.expandGroup(pos);
//            }
            //点击事件
            mElv.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

                @Override
                public boolean onChildClick(ExpandableListView parent, View v,
                                            int groupPosition, int childPosition, long id) {
                        Intent intent = new Intent(mContext, PatchDesActivity.class);
                        intent.putExtra("type", 2);//you压缩按钮
                        Bundle b = new Bundle();
                        b.putString("typeName",mList.get(groupPosition).getType());
                        b.putString("isZip",mList.get(groupPosition).getIsZip());//是否压缩
                        b.putSerializable("PatchModel", mList.get(groupPosition).getPatchModelList().get(childPosition));
                        b.putSerializable("TaskPathModel", mList.get(groupPosition).getZipPathList().get(childPosition));
                        intent.putExtras(b);
                        currPos = groupPosition;
                        startActivity(intent);
                    return true;
                }
            });
        } else {
            TextView emptyView = new TextView(PatchFragment.this.getContext());
            emptyView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
            emptyView.setText("暂无采集信息！");
            emptyView.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
            emptyView.setVisibility(View.GONE);
            ((ViewGroup)mElv.getParent()).addView(emptyView);
            mElv.setEmptyView(emptyView);
        }

    }

    @Override
    public void onClickAddListener(final int groupPosition) {
        View viewDialog = DialogUtil.customPromptDialog(mContext, "确定", "取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        currPos = groupPosition;
//                        lat = location.getLocation()[1];
//                        lng = location.getLocation()[0];
                        lat = location.getLatLng()[0];
                        lng = location.getLatLng()[1];
                        Bundle b = new Bundle();
                        Intent intent = new Intent(mContext, PatchDesActivity.class);
                        b.putInt("type", 1);//增加采集点 无压缩按钮
                        b.putString("typeName", mList.get(groupPosition).getType());//类别名
                        b.putDouble("lat",lat);
                        b.putDouble("lng",lng);
                        b.putSerializable("PatchModel", mList.get(groupPosition));
                        intent.putExtras(b);
                        startActivity(intent);
                    }
                }, null);
        TextView tv = (TextView) viewDialog.findViewById(R.id.dialog_tv_txt);
        tv.setText("是否以当前的坐标为采集点吗?");

    }

    /**
     * 展开 与开闭
     * @param groupPosition
     */
    @Override
    public void onClickTypeNameListener(int groupPosition) {
        if(mElv.isGroupExpanded(groupPosition)){
            mElv.collapseGroup(groupPosition);
            mList.get(groupPosition).setIsStatus("2");
        }else{
            mElv.expandGroup(groupPosition);
            mList.get(groupPosition).setIsStatus("1");
        }
    }

    @Override
    public void onClickDeleteListener(final int groupPosition, int type) {
        if (type==1){
            View viewDialog = DialogUtil.customPromptDialog(mContext, "确定", "取消",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (!TextUtils.isEmpty(mList.get(groupPosition).getTypeNamePath())){
                                FileUtil.deleteFolder(new File(mList.get(groupPosition).getTypeNamePath()));
                                currPos = 0;
                                onResume();
                                ToastUtil.showBottomShort(mContext,"删除成功");
                            }
                        }
                    }, null);
            TextView tv = (TextView) viewDialog.findViewById(R.id.dialog_tv_txt);
            tv.setText("确定要删除类别吗？");
        }
    }

    /**
     * 压缩 跟删除压缩包
     * @param groupPosition
     * @param childPosition
     */
    @Override
    public void onClickUpLoadingListener(final int groupPosition, int childPosition) {
        if (mList.get(groupPosition).getIsZip().equals("1")){//已压缩
            final String delPath = mList.get(groupPosition).getTmpZipPath();
            View viewDialog = DialogUtil.customPromptDialog(mContext, "确定", "取消",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (!TextUtils.isEmpty(delPath)){
                                File file = new File(delPath);
                                file.delete();
                                ToastUtil.showBottomShort(mContext,"删除成功");
                                mList.get(groupPosition).setIsZip("0");
                                mList.get(groupPosition).setIsAdd("1");
                                mList.get(groupPosition).setIsStatus("1");
                                currPos = groupPosition;
                                onResume();
                            }
                        }
                    }, null);
            TextView tv = (TextView) viewDialog.findViewById(R.id.dialog_tv_txt);
            tv.setText("确定要删除压缩包吗？");
        }else{
            if (progressDialog==null){
                progressDialog = MyProgressDialog.createDialog(mContext);
            }
            final ZipUtil zipUtils = new ZipUtil();
//            File appFilePath = FileUtil.createFolder(MyApplication.appRootPath);
//            if (appFilePath.exists()) {
//                final File tempFile = FileUtil.createOneFolder(appFilePath, RequestCode.TEMP_GATHER);
                File zipFile = new File(mList.get(groupPosition).getZipPath());
                if (MyApplication.gatherTempFile.exists()) {
                    final FileNames names = new FileNames();
                    String zip = "";
                    File[] files = zipFile.listFiles();
                    for (File file : files) {
                        if (file.getName().equals(mList.get(groupPosition).getType())) {
                            zip = file.getPath();
                        }
                    }
                    if (TextUtils.isEmpty(zip)) {
                        ToastUtil.showBottomShort(mContext, "压缩文件不存在");
                        return;
                    }
                    progressDialog.setMessage("压缩中...");
                    progressDialog.show();
                    final String msg = zip;
                    // 将压缩放入线程中
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                zipUtils.setCallBack(new ZipUtil.ZipOverCallBack() {
                                    @Override
                                    public void onZipOver() {
                                        Message msg = handler.obtainMessage();
                                        msg.what = 101;
                                        msg.obj = String.valueOf(groupPosition);
                                        handler.sendMessage(msg);
                                    }
                                });
                                zipUtils.ZipFolder(msg, MyApplication.gatherTempFile.getPath(), names.getZipGatherName(mList.get(groupPosition).getType()));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    thread.start();
                }
//            }
        }

    }

    /**
     * 单个删除
     * @param groupPosition
     * @param childPosition
     */
    @Override
    public void onClickDeleteOneListener(int groupPosition, int childPosition) {
        final String delPath = mList.get(groupPosition).getZipPathList().get(childPosition).getProjectPath();
        View viewDialog = DialogUtil.customPromptDialog(mContext, "确定", "取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!TextUtils.isEmpty(delPath)){
                            FileUtil.deleteFolder(new File(delPath));
                            ToastUtil.showBottomShort(mContext,"删除成功");
                            onResume();
                        }
                    }
                }, null);
        TextView tv = (TextView) viewDialog.findViewById(R.id.dialog_tv_txt);
        tv.setText("确定要删除该采集信息吗？");
    }
}
