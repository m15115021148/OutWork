package com.sitemap.nanchang.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sitemap.nanchang.R;
import com.sitemap.nanchang.application.MyApplication;
import com.sitemap.nanchang.config.RequestCode;
import com.sitemap.nanchang.gps.GpsLocation;
import com.sitemap.nanchang.model.GpsModel;
import com.sitemap.nanchang.model.PatchExpModel;
import com.sitemap.nanchang.model.PatchModel;
import com.sitemap.nanchang.model.TaskPathModel;
import com.sitemap.nanchang.util.DateUtil;
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
 * @desc 采集点新增页面
 * @author chenmeng created by 2016/12/9
 */
public class PatchDesActivity extends BaseActivity implements View.OnClickListener, ZipUtil.ZipOverCallBack{
    private PatchDesActivity mContext;//本类
    private TextView mSubmit,reduce;//提交 压缩字体
    private LinearLayout mBack,mEdit;//返回 编辑
    private RelativeLayout mChar,mPicture,mVideo,mVoice;//文字布局 图片,视频
    private RelativeLayout mName,mNumber,mAddress,mCompany,mPhone,mPerson,mNote,mClasses;//名称  编号 地点 电话 联系人 备注
    private View dailogView;//名称输入的viewdialog
    private View dailog;//提示框
    private TextView name,number,address,chars,company,phone,person,note,classes;//名称 编号 地点 文字 电话 联系人 备注
    private RelativeLayout mZip;//压缩 视图
    private TextView mZipDel;//删除压缩包
    private String zipPath = "";//压缩包 文件 路径
    private String zipFolderPath = "";//需要压缩的文件夹目录路径
    private int type = 1 ; //是否有压缩按钮
    private PatchModel taskModel;//实体类
    private TaskPathModel taskPathModel;//路径实体类
    private double lat = 0,lng = 0;
    private String gpsPath = "";
    private String numberPath = "";
    private String gatherName = "";//采集点 数据编号名称
    private List<File> mFileList = new ArrayList<>();//创建文件路径的集合
//    private Location location;
    private String txtPath = "";//文字保存路径
    private String imgPath = "";//图片保存路径
    private String videoPath = "";//视频保存路径
//    private String voicePath = "";//音频保存路径
    private final int UPDATE_ZIP=11;//压缩完成更新
    private MyProgressDialog progressDialog;// 压缩显示的进度
    private TextView picture,video,voice;//图片 视频 音频
    private boolean isChangeNumber = false;//是否输入过类别 一旦输入不能更改
    private String typeName = "";//类别名
    private int pictureNum = 0,videoNum = 0,voiceNum = 0;//图片 视频 音频 的数量
    private GpsLocation location;//gps定位
    private File gatherFile;//gather文件

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patch_des);
        mContext = this;
        location = new GpsLocation(this);
        location.start();
        initView();
        initData();

    }

    @Override
    public void onDestroy() {
        if(location!=null){
//            location.stop();
            location.close();
        }
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    /**
     * 初始化数据
     */
    private void initData() {

        type = getIntent().getIntExtra("type",1);
        if (type==1){
            isChangeNumber = false;
            mEdit.setVisibility(View.GONE);
            typeName = getIntent().getStringExtra("typeName");
            lat = getIntent().getDoubleExtra("lat",0);
            lng = getIntent().getDoubleExtra("lng",0);
            PatchExpModel model = (PatchExpModel) getIntent().getSerializableExtra("PatchModel");
            gatherFile = model.getPatchFile();

            if (gatherFile.exists()){
                File typeNameFile = FileUtil.createOneFolder(gatherFile, typeName);
                if (typeNameFile.exists()){
                    FileNames names = new FileNames();
                    gatherName = names.getGatherName();
                    File taskFile = FileUtil.createOneFolder(typeNameFile, gatherName);
                    numberPath = taskFile.getPath();
                    if (taskFile.exists()) {
                        File gpsFile = FileUtil.createOneFolder(taskFile, RequestCode.GPS);
                        if (gpsFile.exists()){
                            gpsPath=gpsFile.getPath();
                            GpsModel gpsModel = new GpsModel();
                            gpsModel.setLat(String.valueOf(lat));
                            gpsModel.setLng(String.valueOf(lng));
                            gpsModel.setTime(DateUtil.getCurrentDate());
//                                if (!gpsPath.equals("") && lat != 4.9E-324 && lat != 0 & lng != 0) {
                            FileUtil.saveFilePath(gpsFile, RequestCode.GPSTXT, ParserUtil.objectToJson(gpsModel), true);
//                                }
                        }
                    }
                }
            }
        }else{
            isChangeNumber = true;//编号无法修改
//            mEdit.setVisibility(View.VISIBLE);
            mEdit.setVisibility(View.GONE);
            String isZip = getIntent().getStringExtra("isZip");//是否压缩 1 压缩
            taskModel = (PatchModel) getIntent().getSerializableExtra("PatchModel");
            taskPathModel = (TaskPathModel) getIntent().getSerializableExtra("TaskPathModel");
            typeName = getIntent().getStringExtra("typeName");
            if (taskModel!=null&&taskPathModel!=null){
                name.setText(taskModel.getName());
                address.setText(taskModel.getAddress());
                chars.setText(taskModel.getUploadText());
                phone.setText(taskModel.getLinkPhone());
                person.setText(taskModel.getLinkman());
                note.setText(taskModel.getNote());
                zipFolderPath = taskPathModel.getSrcZipPath();
                zipPath = taskPathModel.getZipPath();
                txtPath = taskPathModel.getTextPath();
                imgPath = taskPathModel.getImgPath();
                videoPath = taskPathModel.getVideoPath();
//                voicePath = taskPathModel.getVoicePath();
                if (isZip.equals("1")){//已压缩
                    mEdit.setFocusable(false);
                    mEdit.setClickable(false);
                    setLayout(false);
                    reduce.setText("已完成");
                    reduce.setTextColor(mContext.getResources().getColor(R.color.task_txt));
                    mZip.setVisibility(View.GONE);
                }else{
                    mZip.setVisibility(View.GONE);
                    setLayout(true);
                }
                //遍历图片 音频 视频
                List<String> list = FileUtil.getListFiles(new File(videoPath));
                if (list!=null){
                    for (String path : list) {
                        if (!TextUtils.isEmpty(path)) {
                            if (path.contains(".mp4")) {
                                videoNum = videoNum+1;
                            }
                        }
                    }
                }
                List<String> list2 = FileUtil.getListFiles(new File(imgPath));
                if (list2!=null){
                    for (String path : list2) {
                        if (!TextUtils.isEmpty(path)) {
                            if (path.contains(".jpg")) {
                                pictureNum = pictureNum+1;
                            }
                        }
                    }
                }
                picture.setText(String.valueOf(pictureNum));
                video.setText(String.valueOf(videoNum));

            }
        }
    }

    /**
     * 初始化view
     */
    private void initView() {
        mBack = (LinearLayout) findViewById(R.id.task_back);
        mBack.setOnClickListener(this);
        mEdit = (LinearLayout) findViewById(R.id.task_edit);
        mEdit.setOnClickListener(this);
        mSubmit = (TextView) findViewById(R.id.task_submit);
        mSubmit.setOnClickListener(this);
        mChar = (RelativeLayout) findViewById(R.id.re_char);
        mChar.setOnClickListener(this);
        mPicture = (RelativeLayout) findViewById(R.id.re_picture);
        mPicture.setOnClickListener(this);
        mVideo = (RelativeLayout) findViewById(R.id.re_video);
        mVideo.setOnClickListener(this);
        mVoice = (RelativeLayout) findViewById(R.id.re_voice);
        mVoice.setOnClickListener(this);
        mName = (RelativeLayout) findViewById(R.id.re_name);
        mName.setOnClickListener(this);
        mNumber = (RelativeLayout) findViewById(R.id.re_number);
        mNumber.setOnClickListener(this);
        mAddress = (RelativeLayout) findViewById(R.id.re_address);
        mAddress.setOnClickListener(this);
        mCompany = (RelativeLayout) findViewById(R.id.re_company);
        mCompany.setOnClickListener(this);
        reduce = (TextView) findViewById(R.id.reduce);
        name = (TextView) findViewById(R.id.task_name);
        number = (TextView) findViewById(R.id.task_number);
        address = (TextView) findViewById(R.id.task_address);
        chars = (TextView) findViewById(R.id.task_char);
        company = (TextView) findViewById(R.id.task_company);
        mZip = (RelativeLayout) findViewById(R.id.re_zip);
        mZipDel = (TextView) findViewById(R.id.zip_del);
        mZipDel.setOnClickListener(this);
        mPhone = (RelativeLayout) findViewById(R.id.re_phone);
        mPhone.setOnClickListener(this);
        mPerson = (RelativeLayout) findViewById(R.id.re_person);
        mPerson.setOnClickListener(this);
        mNote = (RelativeLayout) findViewById(R.id.re_note);
        mNote.setOnClickListener(this);
        phone = (TextView) findViewById(R.id.task_phone);
        person = (TextView) findViewById(R.id.task_person);
        note = (TextView) findViewById(R.id.task_note);
        mClasses = (RelativeLayout) findViewById(R.id.re_classes);
        mClasses.setOnClickListener(this);
        classes = (TextView) findViewById(R.id.task_classes);
        picture = (TextView) findViewById(R.id.task_picture);
        video = (TextView) findViewById(R.id.task_video);
        voice = (TextView) findViewById(R.id.task_voice);
    }

    @Override
    public void onClick(View v) {
        if (v == mBack){
            if (type==1){
                onBackPressed();
            }else{
                mContext.finish();
            }
        }
        if (v == mEdit){//编辑(压缩)
            if (progressDialog==null){
                progressDialog = MyProgressDialog.createDialog(mContext);
            }
            final ZipUtil zipUtils = new ZipUtil();
                if (MyApplication.gatherTempFile.exists()){
                    File zipFile = new File(zipFolderPath);
                    final FileNames names = new FileNames();
                    String zip = "";
                    File[] files = zipFile.listFiles();
                    for (File file:files) {
                        if (file.getName().equals(gatherName)){
                            zip =  file.getPath();
                        }
                    }
                    if (TextUtils.isEmpty(zip)){
                        ToastUtil.showBottomShort(mContext,"压缩文件不存在");
                        return;
                    }
                    reduce.setVisibility(View.GONE);
                    progressDialog.setMessage("压缩中...");
                    progressDialog.show();
                    final String msg=zip;
                    // 将压缩放入线程中
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                zipUtils.setCallBack(mContext);
                                String path = zipUtils.ZipFolder(msg,MyApplication.gatherTempFile.getPath(),names.getZipGatherName(gatherName));
                                zipPath = path;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    thread.start();
                }
        }
        if (v == mSubmit){//提交
            if (TextUtils.isEmpty(name.getText().toString())){
                ToastUtil.showBottomShort(mContext,"采集名称不能为空");
                return;
            }
            if (TextUtils.isEmpty(address.getText().toString())){
                ToastUtil.showBottomShort(mContext,"采集地点不能为空");
                return;
            }
            if (mFileList==null||mFileList.size()<=0){
                mFileList = createFolder();
            }
            if(mFileList.get(0).exists()){
                lat = location.getLatLng()[0];
                lng = location.getLatLng()[1];
                FileUtil.saveFilePath(mFileList.get(0), RequestCode.GATHERNAME, ParserUtil.objectToJson(buildData()),false);
                GpsModel gpsModel=new GpsModel();
                gpsModel.setLat(String.valueOf(lat));
                gpsModel.setLng(String.valueOf(lng));
                gpsModel.setTime(DateUtil.getCurrentDate());
                gpsModel.setDes("");
                FileUtil.saveFilePath(mFileList.get(0), RequestCode.TASKDES, ParserUtil.objectToJson(gpsModel),false);
            }
            finish();
        }
        if(v == mChar){//文字
            if (TextUtils.isEmpty(address.getText().toString().trim())||TextUtils.isEmpty(name.getText().toString().trim())
                    ){
                ToastUtil.showBottomShort(mContext,"请先输入基本信息");
                return;
            }
            Intent intent = new Intent(mContext,CharDetailActivity.class);
            intent.putExtra("chars",chars.getText().toString());
            startActivityForResult(intent,RequestCode.CHARS);
        }
        if (v == mPicture){//图片
            if (TextUtils.isEmpty(address.getText().toString().trim())||TextUtils.isEmpty(name.getText().toString().trim())
                    ){
                ToastUtil.showBottomShort(mContext,"请先输入基本信息");
                return;
            }
            if (mFileList==null||mFileList.size()<=0){
                mFileList = createFolder();
            }
            Intent intent = new Intent(mContext,ImageSelectActivity.class);
            intent.putExtra("imgPath",mFileList.get(1).getPath());
            startActivityForResult(intent, RequestCode.NUMBER_PICTURE);
        }
        if(v == mVoice){//音频
            if (TextUtils.isEmpty(address.getText().toString().trim())||TextUtils.isEmpty(name.getText().toString().trim())
                    ){
                ToastUtil.showBottomShort(mContext,"请先输入基本信息");
                return;
            }
            if (mFileList==null||mFileList.size()<=0){
                mFileList = createFolder();
            }
            Intent intent = new Intent(mContext,VoiceSelectActivity.class);
            intent.putExtra("voicePath",mFileList.get(3).getPath());
            startActivityForResult(intent, RequestCode.NUMBER_VOICE);
        }
        if (v == mVideo){//视频
            if (TextUtils.isEmpty(address.getText().toString().trim())||TextUtils.isEmpty(name.getText().toString().trim())
                    ){
                ToastUtil.showBottomShort(mContext,"请先输入基本信息");
                return;
            }
            if (mFileList==null||mFileList.size()<=0){
                mFileList = createFolder();
            }
            Intent intent = new Intent(mContext,VideoSelectActivity.class);
            intent.putExtra("videoPath",mFileList.get(2).getPath());
            startActivityForResult(intent, RequestCode.NUMBER_VIDEO);
        }
        if (v == mName){//名称
            dailogView = DialogUtil.customInputDialog(mContext, "名称","确定", "取消",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            EditText text = (EditText) dailogView.findViewById(R.id.dialog_et_txt);
                            name.setText(text.getText().toString());
                        }
                    },
                    null);
            EditText text = (EditText) dailogView.findViewById(R.id.dialog_et_txt);
            text.setText(name.getText().toString());
        }
        if (v == mAddress){//地点
            dailogView = DialogUtil.customInputDialog(mContext,  "地点","确定", "取消",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            EditText text = (EditText) dailogView.findViewById(R.id.dialog_et_txt);
                            address.setText(text.getText().toString());
                        }
                    },
                    null);
            EditText text = (EditText) dailogView.findViewById(R.id.dialog_et_txt);
            text.setText(address.getText().toString());
        }
        if (v == mCompany){
            dailogView = DialogUtil.customInputDialog(mContext,  "目标单位","确定", "取消",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            EditText text = (EditText) dailogView.findViewById(R.id.dialog_et_txt);
                            company.setText(text.getText().toString());
                        }
                    },
                    null);
        }
        if (v == mNote){
            dailogView = DialogUtil.customInputDialog(mContext,  "备注","确定", "取消",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            EditText text = (EditText) dailogView.findViewById(R.id.dialog_et_txt);
                            note.setText(text.getText().toString());
                        }
                    },
                    null);
            EditText text = (EditText) dailogView.findViewById(R.id.dialog_et_txt);
            text.setText(note.getText().toString());
        }
        if (v == mPhone){
            dailogView = DialogUtil.customInputDialog(mContext,  "联系人电话","确定", "取消",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            EditText text = (EditText) dailogView.findViewById(R.id.dialog_et_txt);
                            phone.setText(text.getText().toString());
                        }
                    },
                    null);
            EditText text = (EditText) dailogView.findViewById(R.id.dialog_et_txt);
            text.setText(phone.getText().toString());
        }
        if (v == mPerson){
            dailogView = DialogUtil.customInputDialog(mContext, "联系人", "确定", "取消",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            EditText text = (EditText) dailogView.findViewById(R.id.dialog_et_txt);
                            person.setText(text.getText().toString());
                        }
                    },
                    null);
            EditText text = (EditText) dailogView.findViewById(R.id.dialog_et_txt);
            text.setText(person.getText().toString());
        }
        if (v == mClasses){
            if (isChangeNumber || type == 2) {
                ToastUtil.showBottomShort(mContext, "编号已确定,无法再次修改");
                return;
            }
            dailogView = DialogUtil.customInputDialog(mContext, "类别","确定", "取消",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            EditText tv = (EditText) dailogView.findViewById(R.id.dialog_et_txt);
                            if (TextUtils.isEmpty(tv.getText().toString().trim())) {
                                ToastUtil.showBottomShort(mContext, "类别不能为空");
                                return;
                            }
                            final String str = tv.getText().toString().trim();
                            dailog = DialogUtil.customPromptDialog(mContext, "确定", "取消",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            classes.setText(str);
                                            isChangeNumber = true;
                                        }
                                    },
                                    null);
                            TextView text = (TextView) dailog.findViewById(R.id.dialog_tv_txt);
                            text.setText("类别(" + str + ")确定后,无法再次修改");
                        }
                    },
                    null);
            EditText text = (EditText) dailogView.findViewById(R.id.dialog_et_txt);
            text.setText(classes.getText().toString());
        }
        if (v == mZipDel){//删除压缩包
            View viewDialog = DialogUtil.customPromptDialog(mContext, "确定", "取消",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mZip.setVisibility(View.GONE);
                            Log.e("result","delete _zipPath:"+zipPath);
                            File file = new File(zipPath);
                            file.delete();
                            mEdit.setFocusable(true);
                            mEdit.setClickable(true);
                            reduce.setText("压缩");
                            reduce.setTextColor(mContext.getResources().getColor(R.color.title_back_color));
                            ToastUtil.showBottomShort(mContext,"删除成功");
                            mZip.setClickable(false);
                            mZip.setFocusable(false);
                            setLayout(true);

                        }
                    }, null);
            TextView tv = (TextView) viewDialog.findViewById(R.id.dialog_tv_txt);
            tv.setText("确定要删除吗？");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode){
            case RequestCode.CHARS:
                chars.setText(data.getStringExtra("chars"));
                break;
            case RequestCode.NUMBER_PICTURE:
                int size1 = data.getIntExtra("size",0);
                Log.e("result","size:"+size1);
                if (size1==0){
                    picture.setText("0");
                }else{
                    picture.setText(size1+"");
                }
                break;
            case RequestCode.NUMBER_VIDEO:
                int size2 = data.getIntExtra("size",0);
                if (size2==0){
                    video.setText("0");
                }else{
                    video.setText(size2+"");
                }
                break;
            case RequestCode.NUMBER_VOICE:
                int size3 = data.getIntExtra("size",0);
                if (size3==0){
                    voice.setText("0");
                }else{
                    voice.setText(size3+"");
                }
                break;
        }
    }

    /**
     * 0 文本
     * 1 图片
     * 2 视频
     * 创建文件目录
     */
    private List<File> createFolder(){
        if (type==1){
            if (MyApplication.appFile.exists()){
//                File upLoadFile = FileUtil.createOneFolder(MyApplication.appFile, RequestCode.GATHER);
                if (MyApplication.gatherFile.exists()){
                    File typeNameFile = FileUtil.createOneFolder(MyApplication.gatherFile, typeName);
//                        numberPath = typeNameFile.getPath();
                    if (typeNameFile.exists()){
//                            FileNames names = new FileNames();
                        File numberFile = FileUtil.createOneFolder(typeNameFile, gatherName);
                        numberPath = numberFile.getPath();
                        if (numberFile.exists()){
                            //创建text img video 目录
                            File textFile = FileUtil.createOneFolder(numberFile, RequestCode.TEXT);
                            File imgFile = FileUtil.createOneFolder(numberFile, RequestCode.IMG);
                            File videoFile = FileUtil.createOneFolder(numberFile, RequestCode.VIDEO);
//                              File voiceFile = FileUtil.createOneFolder(numberFile, RequestCode.VOICE);
                            txtPath = textFile.getPath();
                            imgPath = imgFile.getPath();
                            videoPath = videoFile.getPath();
//                              voicePath = voiceFile.getPath();
                            mFileList.add(textFile);
                            mFileList.add(imgFile);
                            mFileList.add(videoFile);
//                              mFileList.add(voiceFile);
                        }
                    }
                }
            }
        }else{
            mFileList.add(FileUtil.createOneFolder(txtPath));
            mFileList.add(FileUtil.createOneFolder(imgPath));
            mFileList.add(FileUtil.createOneFolder(videoPath));
//            mFileList.add(FileUtil.createOneFolder(voicePath));
        }
        return mFileList;
    }

    /**
     * 实体类赋值
     * @return
     */
    private PatchModel buildData(){
        PatchModel model = new PatchModel();
//        model.setUuid(MyApplication.uuid);
        model.setName(name.getText().toString().trim());
        model.setAddress(address.getText().toString().trim());
        model.setLinkPhone(phone.getText().toString().trim());
        model.setLinkman(person.getText().toString().trim());
        model.setNote(note.getText().toString());
//        model.setUploadText(chars.getText().toString());
        model.setLat(String.valueOf(lat));
        model.setLng(String.valueOf(lng));
        model.setTime(DateUtil.getCurrentDate());
        return model;
    }

    @Override
    public void onBackPressed() {
        if (type==1){
            View viewDialog = DialogUtil.customPromptDialog(mContext, "放弃", "不放弃",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (!TextUtils.isEmpty(numberPath)){
                                FileUtil.deleteFolder(new File(numberPath));
                            }
                            mContext.finish();
                        }
                    }, null);
            TextView tv = (TextView) viewDialog.findViewById(R.id.dialog_tv_txt);
            tv.setText("是否放弃新建采集点吗?");
        }else{
            super.onBackPressed();
        }
    }

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (progressDialog!=null&&progressDialog.isShowing()){
                progressDialog.dismiss();
            }
            if(msg.what==UPDATE_ZIP){
                reduce.setVisibility(View.VISIBLE);
                mZip.setVisibility(View.VISIBLE);
                mEdit.setFocusable(false);
                mEdit.setClickable(false);
                reduce.setText("已完成");
                reduce.setTextColor(mContext.getResources().getColor(R.color.task_txt));
                ToastUtil.showBottomShort(mContext, "压缩成功");
                mEdit.setClickable(false);
                mEdit.setFocusable(false);
                setLayout(false);
                //删除img video voice gps 文件
                if (!TextUtils.isEmpty(gpsPath)){
                    FileUtil.deleteFolder(new File(gpsPath));
                }
                if (!TextUtils.isEmpty(imgPath)){
                    FileUtil.deleteFolder(new File(imgPath));
                }
                if (!TextUtils.isEmpty(videoPath)){
                    FileUtil.deleteFolder(new File(videoPath));
                }
//                if (!TextUtils.isEmpty(voicePath)){
//                    FileUtil.deleteFolder(new File(voicePath));
//                }
//                mPicture.setVisibility(View.GONE);
//                mVideo.setVisibility(View.GONE);
//                mVoice.setVisibility(View.GONE);
            }

        }
    };

    @Override
    public void onZipOver() {
        handler.sendEmptyMessage(UPDATE_ZIP);
    }

    /**
     * 设置布局是否能点击
     */
    private void setLayout(boolean isClick){
        mName.setClickable(isClick);
        mName.setFocusable(isClick);
        mAddress.setClickable(isClick);
        mAddress.setFocusable(isClick);
        mPhone.setClickable(isClick);
        mPhone.setFocusable(isClick);
        mPerson.setClickable(isClick);
        mPerson.setFocusable(isClick);
        mNote.setClickable(isClick);
        mNote.setFocusable(isClick);
        mChar.setClickable(isClick);
        mChar.setFocusable(isClick);
        mPicture.setClickable(isClick);
        mPicture.setFocusable(isClick);
        mVoice.setClickable(isClick);
        mVoice.setFocusable(isClick);
        mVideo.setClickable(isClick);
        mVideo.setFocusable(isClick);
        mSubmit.setClickable(isClick);
        mSubmit.setFocusable(isClick);
    }
}
