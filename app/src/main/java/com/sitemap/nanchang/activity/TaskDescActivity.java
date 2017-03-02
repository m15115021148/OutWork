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
import com.sitemap.nanchang.model.TaskModel;
import com.sitemap.nanchang.model.TaskPathModel;
import com.sitemap.nanchang.util.DateUtil;
import com.sitemap.nanchang.util.DialogUtil;
import com.sitemap.nanchang.util.FileNames;
import com.sitemap.nanchang.util.FileUtil;
import com.sitemap.nanchang.util.JsonUtil;
import com.sitemap.nanchang.util.ParserUtil;
import com.sitemap.nanchang.util.ToastUtil;
import com.sitemap.nanchang.util.ZipUtil;
import com.sitemap.nanchang.view.MyProgressDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author chenmeng created by 2016/11/30
 * @desc 任务详情
 */
public class TaskDescActivity extends BaseActivity implements View.OnClickListener, ZipUtil.ZipOverCallBack {
    private TaskDescActivity mContext;//本类
    private TextView mSubmit, reduce;//提交 压缩字体
    private LinearLayout mBack;//返回
    private RelativeLayout mEdit;//编辑
    private RelativeLayout mChar, mPicture, mVideo, mVoice;//文字布局 图片,视频
    private RelativeLayout mName, mNumber, mAddress, mCompany;//名称  编号 地点
    private View dailogView;//名称输入的viewdialog
    private View dailog;//提示框
    private TextView name, number, address, chars, company;//名称 编号 地点 文字
    private RelativeLayout mZip;//压缩 视图
    private TextView mZipDel;//删除压缩包
    private String zipPath = "";//压缩包 文件 路径
    private String zipFolderPath = "";//需要压缩的文件夹目录路径
    private int type = 1; //是否有压缩按钮
    private TaskModel taskModel;//实体类
    private TaskPathModel taskPathModel;//路径实体类
    private String numberPath = "";//创建的编号文件夹 路径
    private double lat, lng;
    private String gpsPath = "";
    private boolean isChangeNumber = false;//是否输入过编号 一旦输入不能更改
    private int numberCode = 0;//编号存在的数量
//    private Location location;
    private String fileName;//文件夹的名称 number_时间
    private final int UPDATE_ZIP=11;//压缩完成更新
    private MyProgressDialog progressDialog;// 压缩显示的进度
    private List<File> mListFile = new ArrayList<>();;//保存的文件集合
    private String txtPath = "";//文字保存路径
    private String imgPath = "";//图片保存路径
    private String videoPath = "";//视频保存路径
    private String voicePath = "";//音频保存路径
    private TextView picture,video,voice;//图片 视频 音频
    private int pictureNum = 0,videoNum = 0,voiceNum = 0;//图片 视频 音频 的数量
    private EditText numberStr;//编号
    private GpsLocation location;//gps定位
    private File taskFile;//任务文件

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_desc);
        mContext = this;
        location = new GpsLocation(this);
        location.start();
        initView();
        initData();
        //开始定位
//        location = Location.instance();
//        location.setGpsPath(gpsPath);
//        location.start(this);
    }

    @Override
    public void onDestroy() {
        if (location != null) {
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
        type = getIntent().getIntExtra("type", 1);
        if (type == 1) {
            isChangeNumber = false;
            mEdit.setVisibility(View.GONE);
        } else {
            isChangeNumber = true;//编号无法修改
            mEdit.setVisibility(View.VISIBLE);
            taskModel = (TaskModel) getIntent().getSerializableExtra("TaskModel");
            taskPathModel = (TaskPathModel) getIntent().getSerializableExtra("TaskPathModel");
            if (taskModel != null && taskPathModel != null) {
                name.setText(taskModel.getTaskName());
                number.setText(taskModel.getTaskNumber());
                address.setText(taskModel.getAddress());
                company.setText(taskModel.getCompany());
                chars.setText(taskModel.getUploadText());
                zipFolderPath = taskPathModel.getSrcZipPath();
                zipPath = taskPathModel.getZipPath();
                fileName = taskPathModel.getProjectName();
                txtPath = taskPathModel.getTextPath();
                imgPath = taskPathModel.getImgPath();
                videoPath = taskPathModel.getVideoPath();
                voicePath = taskPathModel.getVoicePath();
                taskFile = taskPathModel.getTaskFile();
                if (taskPathModel.getIsZip().equals("1")) {//已压缩
                    mEdit.setFocusable(false);
                    mEdit.setClickable(false);
                    reduce.setText("已完成");
                    reduce.setTextColor(mContext.getResources().getColor(R.color.task_txt));
                    mZip.setVisibility(View.VISIBLE);
                    setLayout(false);
                } else {
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
                List<String> list1 = FileUtil.getListFiles(new File(voicePath));
                if (list1!=null){
                    for (String path : list1) {
                        if (!TextUtils.isEmpty(path)) {
                            if (path.contains(".amr")) {
                                voiceNum = voiceNum+1;
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
                voice.setText(String.valueOf(voiceNum));

                if (taskFile.exists()) {
                    File tastFile = FileUtil.createOneFolder(taskFile, taskModel.getTaskNumber());
                    if (tastFile.exists()) {
                        File gpsFile = FileUtil.createOneFolder(tastFile, RequestCode.GPS);
                        if (gpsFile.exists()) {
                            lat = location.getLatLng()[0];
                            lng = location.getLatLng()[1];
                            GpsModel gpsModel = new GpsModel();
                            gpsModel.setLat(String.valueOf(lat));
                            gpsModel.setLng(String.valueOf(lng));
                            gpsModel.setTime(DateUtil.getCurrentDate());
                            FileUtil.saveFilePath(gpsFile, RequestCode.GPSTXT, ParserUtil.objectToJson(gpsModel) + "\r\n", true);
                            gpsPath = gpsFile.getPath();
                            location.setGpsPath(gpsPath);
                        }
                    }
                }
            }
        }
    }

    /**
     * 初始化view
     */
    private void initView() {
        mBack = (LinearLayout) findViewById(R.id.task_back);
        mBack.setOnClickListener(this);
        mEdit = (RelativeLayout) findViewById(R.id.task_edit);
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
        picture = (TextView) findViewById(R.id.task_picture);
        video = (TextView) findViewById(R.id.task_video);
        voice = (TextView) findViewById(R.id.task_voice);
    }

    @Override
    public void onClick(View v) {
        if (v == mBack) {
            if (type == 1) {
                onBackPressed();
            } else {
                mContext.finish();
            }
        }
        if (v == mEdit) {//(压缩)
            if (progressDialog==null){
                progressDialog = MyProgressDialog.createDialog(mContext);
            }
            final ZipUtil zipUtils = new ZipUtil();
                if (MyApplication.taskTempFile.exists()) {
                    File zipFile = new File(zipFolderPath);
                    String zip = "";
                    final FileNames names = new FileNames();
                    File[] files = zipFile.listFiles();
                    for (File file : files) {
                        if (file.getName().equals(taskModel.getTaskNumber())) {
                            zip = file.getPath();
                        }
                    }
                    if (TextUtils.isEmpty(zip)) {
                        ToastUtil.showBottomShort(mContext, "压缩文件不存在");
                        return;
                    }
                    reduce.setVisibility(View.GONE);
//                        pb.setVisibility(View.VISIBLE);
                    progressDialog.setMessage("压缩中...");
                    progressDialog.show();
                    final String msg=zip;
//                        将压缩放入线程中
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                zipUtils.setCallBack(mContext);
                                String path = zipUtils.ZipFolder(msg, MyApplication.taskTempFile.getPath(), names.getZipTaskName(fileName));
                                zipPath = path;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    }) ;
                    thread.start();
                }
        }
        if (v == mSubmit) {//提交
            if (TextUtils.isEmpty(name.getText().toString())) {
                ToastUtil.showBottomShort(mContext, "任务名称不能为空");
                return;
            }
            if (TextUtils.isEmpty(number.getText().toString())) {
                ToastUtil.showBottomShort(mContext, "任务编号不能为空");
                return;
            }
            if (TextUtils.isEmpty(chars.getText().toString())) {
                ToastUtil.showBottomShort(mContext, "文字输入不能为空");
                return;
            }
            String str = number.getText().toString().trim();
            if (mListFile==null||mListFile.size()<=0){
                mListFile = createFolder(str);
            }
            if (mListFile.get(0).exists()) {
                FileUtil.saveFilePath(mListFile.get(0), RequestCode.TASKNAME, JsonUtil.buildJsonData(buildData()), false);
                lat = location.getLatLng()[0];
                lng = location.getLatLng()[1];
                GpsModel gpsModel = new GpsModel();
                gpsModel.setLat(String.valueOf(lat));
                gpsModel.setLng(String.valueOf(lng));
                gpsModel.setTime(DateUtil.getCurrentDate());
                gpsModel.setDes("");
                FileUtil.saveFilePath(mListFile.get(0), RequestCode.TASKDES, ParserUtil.objectToJson(gpsModel), false);
            }
            finish();
        }
        if (v == mChar) {//文字
            if (TextUtils.isEmpty(number.getText().toString().trim()) || TextUtils.isEmpty(name.getText().toString().trim())
                    ) {
                ToastUtil.showBottomShort(mContext, "请先输入基本信息");
                return;
            }
            Intent intent = new Intent(mContext, CharDetailActivity.class);
            intent.putExtra("chars", chars.getText().toString());
            startActivityForResult(intent, RequestCode.CHARS);
        }
        if (v == mPicture) {//图片
            if (TextUtils.isEmpty(number.getText().toString().trim()) || TextUtils.isEmpty(name.getText().toString().trim())
                    ) {
                ToastUtil.showBottomShort(mContext, "请先输入基本信息");
                return;
            }
            String str = number.getText().toString().trim();
            if (mListFile==null||mListFile.size()<=0){
                mListFile = createFolder(str);
            }
            Intent intent = new Intent(mContext, ImageSelectActivity.class);
            intent.putExtra("imgPath", mListFile.get(1).getPath());
            startActivityForResult(intent, RequestCode.NUMBER_PICTURE);
        }
        if (v == mVoice) {//音频
            if (TextUtils.isEmpty(number.getText().toString().trim()) || TextUtils.isEmpty(name.getText().toString().trim())
                    ) {
                ToastUtil.showBottomShort(mContext, "请先输入基本信息");
                return;
            }
            String str = number.getText().toString().trim();
            if (mListFile==null||mListFile.size()<=0){
                mListFile = createFolder(str);
            }
            Intent intent = new Intent(mContext, VoiceSelectActivity.class);
            intent.putExtra("voicePath", mListFile.get(3).getPath());
            startActivityForResult(intent, RequestCode.NUMBER_VOICE);
        }
        if (v == mVideo) {//视频
            if (TextUtils.isEmpty(number.getText().toString().trim()) || TextUtils.isEmpty(name.getText().toString().trim())
                    ) {
                ToastUtil.showBottomShort(mContext, "请先输入基本信息");
                return;
            }
            String str = number.getText().toString().trim();
            if (mListFile==null||mListFile.size()<=0){
                mListFile = createFolder(str);
            }
            Intent intent = new Intent(mContext, VideoSelectActivity.class);
            intent.putExtra("videoPath", mListFile.get(2).getPath());
            startActivityForResult(intent, RequestCode.NUMBER_VIDEO);
        }
        if (v == mName) {//名称
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
        if (v == mNumber) {//编号
            if (isChangeNumber || type == 2) {
                ToastUtil.showBottomShort(mContext, "编号已确定,无法再次修改");
                return;
            }
            dailogView = DialogUtil.customInputDialog(mContext,  "编号","确定", "取消",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (TextUtils.isEmpty(numberStr.getText().toString().trim())) {
                                ToastUtil.showBottomShort(mContext, "任务编号不能为空");
                                return;
                            }
                            if(!MyApplication.isNumberOrChars(numberStr.getText().toString().trim())){
                                ToastUtil.showBottomShort(mContext, "任务编号必须是数字或字母");
                                return;
                            }
                            final String str = numberStr.getText().toString().trim().toUpperCase();//转大写
                            dailog = DialogUtil.customPromptDialog(mContext, "确定", "取消",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (MyApplication.numberSum != null && MyApplication.numberSum.length > 0) {
                                                for (String string : MyApplication.numberSum) {
                                                    if (str.equals(string)) {
                                                        numberCode += 1;
                                                    }
                                                }
                                            }
                                            if (numberCode > 0) {
                                                ToastUtil.showBottomShort(mContext, "编号已存在");
                                                numberCode = 0;
                                            } else {
                                                number.setText(str);
                                                isChangeNumber = true;
                                            }

                                        }
                                    },
                                    null);
                            TextView text = (TextView) dailog.findViewById(R.id.dialog_tv_txt);
                            text.setText("编号(" + str + ")确定后,无法再次修改");
                        }
                    },
                    null);
            numberStr = (EditText) dailogView.findViewById(R.id.dialog_et_txt);
            numberStr.setText("JXZA");
        }
        if (v == mAddress) {//地点
            dailogView = DialogUtil.customInputDialog(mContext, "地点", "确定", "取消",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            EditText text = (EditText) dailogView.findViewById(R.id.dialog_et_txt);
                            address.setText(text.getText().toString());
                        }
                    },
                    null);
        }
        if (v == mCompany) {
            dailogView = DialogUtil.customInputDialog(mContext, "目标单位", "确定", "取消",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            EditText text = (EditText) dailogView.findViewById(R.id.dialog_et_txt);
                            company.setText(text.getText().toString());
                        }
                    },
                    null);
        }
        if (v == mZipDel) {//删除压缩包
            View viewDialog = DialogUtil.customPromptDialog(mContext, "确定", "取消",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mZip.setVisibility(View.GONE);
                            Log.e("result", "delete _zipPath:" + zipPath);
                            File file = new File(zipPath);
                            file.delete();
                            mEdit.setFocusable(true);
                            mEdit.setClickable(true);
                            reduce.setText("压缩");
                            reduce.setTextColor(mContext.getResources().getColor(R.color.title_back_color));
                            ToastUtil.showBottomShort(mContext, "删除成功");
                            mZip.setClickable(false);
                            mZip.setFocusable(false);
                            setLayout(true);
                            //显示 img video voice
                            mPicture.setVisibility(View.VISIBLE);
                            mVideo.setVisibility(View.VISIBLE);
                            mVoice.setVisibility(View.VISIBLE);
                        }
                    }, null);
            TextView tv = (TextView) viewDialog.findViewById(R.id.dialog_tv_txt);
            tv.setText("确定要删除吗？");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case RequestCode.CHARS:
                chars.setText(data.getStringExtra("chars"));
                break;
            case RequestCode.NUMBER_PICTURE:
                int size1 = data.getIntExtra("size",0);
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
    private List<File> createFolder(String numberName) {
        //保存文字 图片 视频 目录 集合
        if (type==1) {
            if (MyApplication.appFile.exists()) {
//                File upLoadFile = FileUtil.createOneFolder(MyApplication.appFile, RequestCode.TASK);
                if (MyApplication.taskFile.exists()) {
                    File numberFile = FileUtil.createOneFolder(MyApplication.taskFile, numberName);
                    numberPath = numberFile.getPath();
                    if (numberFile.exists()) {
                        //创建text img video 目录
                        File textFile = FileUtil.createOneFolder(numberFile, RequestCode.TEXT);
                        File imgFile = FileUtil.createOneFolder(numberFile, RequestCode.IMG);
                        File videoFile = FileUtil.createOneFolder(numberFile, RequestCode.VIDEO);
                        File voiceFile = FileUtil.createOneFolder(numberFile, RequestCode.VOICE);
                        txtPath = textFile.getPath();
                        imgPath = imgFile.getPath();
                        videoPath = videoFile.getPath();
                        voicePath = voiceFile.getPath();
                        mListFile.add(textFile);
                        mListFile.add(imgFile);
                        mListFile.add(videoFile);
                        mListFile.add(voiceFile);
                    }
                }
            }
        }else{
            mListFile.add(FileUtil.createOneFolder(txtPath));
            mListFile.add(FileUtil.createOneFolder(imgPath));
            mListFile.add(FileUtil.createOneFolder(videoPath));
            mListFile.add(FileUtil.createOneFolder(voicePath));
        }
        return mListFile;
    }

    /**
     * 实体类赋值
     *
     * @return
     */
    private TaskModel buildData() {
        TaskModel model = new TaskModel();
//        lat = location.getLocation()[1];
//        lng = location.getLocation()[0];
//        lat = location.getPositionByGps().getLat();
//        lng = location.getPositionByGps().getLng();
//        model.setUuid(MyApplication.uuid);
        model.setTaskName(name.getText().toString());
        model.setTaskNumber(number.getText().toString());
        model.setAddress(address.getText().toString());
        model.setCompany(company.getText().toString());
        model.setUploadText(chars.getText().toString());
        model.setTime(DateUtil.getCurrentDate());
        return model;
    }

    @Override
    public void onBackPressed() {
        if (type == 1) {
            View viewDialog = DialogUtil.customPromptDialog(mContext, "确定", "取消",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (!TextUtils.isEmpty(numberPath)) {
                                FileUtil.deleteFolder(new File(numberPath));
                            }
                            mContext.finish();
                        }
                    }, null);
            TextView tv = (TextView) viewDialog.findViewById(R.id.dialog_tv_txt);
            tv.setText("是否放弃新建任务吗?");
        } else {
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
//                if (!TextUtils.isEmpty(gpsPath)){
//                    FileUtil.deleteFolder(new File(gpsPath));
//                }
//                if (!TextUtils.isEmpty(imgPath)){
//                    FileUtil.deleteFolder(new File(imgPath));
//                }
//                if (!TextUtils.isEmpty(videoPath)){
//                    FileUtil.deleteFolder(new File(videoPath));
//                }
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
        mNumber.setClickable(isClick);
        mNumber.setFocusable(isClick);
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
