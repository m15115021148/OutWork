package com.sitemap.nanchang.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sitemap.nanchang.R;
import com.sitemap.nanchang.application.MyApplication;
import com.sitemap.nanchang.config.RequestCode;
import com.sitemap.nanchang.model.PersonModel;
import com.sitemap.nanchang.util.FileUtil;
import com.sitemap.nanchang.util.ParserUtil;
import com.sitemap.nanchang.util.ToastUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @desc 修改密码
 * @author chenmeng created by 2016/12/8
 */
public class ChangePswActivity extends BaseActivity implements View.OnClickListener{
    private ChangePswActivity mContext;//本类
    private LinearLayout mBack;//返回上一层
    private TextView mTitle;//标题
    private EditText oldPsw,newPsw,newRePsw;//原密码 新秘密 确认密码
    private TextView mSure;//确认修改
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_psw);
        mContext = this;
        preferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        initView();
    }

    /**
     *  初始化view
     */
    private void initView() {
        mBack = (LinearLayout) findViewById(R.id.include_title).findViewById(R.id.back);
        mBack.setOnClickListener(this);
        mTitle = (TextView) findViewById(R.id.include_title).findViewById(R.id.title);
        mTitle.setText("修改密码");
        oldPsw = (EditText) findViewById(R.id.change_psw_old);
        newPsw = (EditText) findViewById(R.id.change_psw_new);
        newRePsw = (EditText) findViewById(R.id.change_psw_re);
        mSure = (TextView) findViewById(R.id.change_psw_sure);
        mSure.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == mBack){
            mContext.finish();
        }
        if (v == mSure){
            if (TextUtils.isEmpty(oldPsw.getText().toString())){
                ToastUtil.showBottomShort(mContext,"原密码不能为空");
                return;
            }
            if (TextUtils.isEmpty(newPsw.getText().toString())){
                ToastUtil.showBottomShort(mContext,"新密码不能为空");
                return;
            }
            if (TextUtils.isEmpty(newRePsw.getText().toString())){
                ToastUtil.showBottomShort(mContext,"确认密码不能为空");
                return;
            }
//            String password = MyApplication.MD5(oldPsw.getText().toString().trim());
            String password = oldPsw.getText().toString();
            if (!password.equals(MyApplication.psw)){
                ToastUtil.showBottomShort(mContext,"原密码不正确");
                return;
            }
            if (!newPsw.getText().toString().equals(newRePsw.getText().toString())){
                ToastUtil.showBottomShort(mContext,"两次密码不一致");
                return;
            }
//            changePsw(MyApplication.MD5(newPsw.getText().toString().trim()));
            changePassWord(newPsw.getText().toString());
            ToastUtil.showBottomShort(mContext,"修改成功");
            finish();
        }
    }

    /**
     * 获取密码
     * @return
     */
    private String getPsw(){
        String userFile =  Environment.getExternalStorageDirectory().toString() + "/";
        String persons= FileUtil.readFilePath(userFile, RequestCode.PERSONS);
        if (persons != null && !persons.equals("")) {
            List<PersonModel> lPerson = ParserUtil.jsonToList(persons, PersonModel.class);
            if (lPerson.size()>0&&lPerson!=null){
                for (PersonModel model:lPerson){
                    if (model.getUsername().equals(MyApplication.userName)){
                        return model.getPassword();
                    }
                }
            }
        }
        return "";
    }


    /**
     * 获取密码
     * @return
     */
    private String getPassWord(){
        return preferences.getString("pwd","");
    }

    /**
     *
     * @param newPsw
     */
    private void changePsw(String newPsw){
        List<PersonModel> mList = new ArrayList<>();
        String userFile =  Environment.getExternalStorageDirectory().toString() + "/";
        String persons= FileUtil.readFilePath(userFile, RequestCode.PERSONS);
        if (persons != null && !persons.equals("")) {
            List<PersonModel> lPerson = ParserUtil.jsonToList(persons, PersonModel.class);
           if (lPerson.size()>0&&lPerson!=null){
               for (PersonModel model:lPerson){
                   if (model.getUsername().equals(MyApplication.userName)){
                        model.setPassword(newPsw);
                   }
                   mList.add(model);
               }
           }
        }
        FileUtil.saveFilePath(new File(userFile), RequestCode.PERSONS, ParserUtil.arrayObjectToJson(mList,true),false);
    }

    /**
     * 修改密码
     * @param newPsw
     */
    private void changePassWord(String newPsw){
        SharedPreferences.Editor edit = preferences.edit();
        edit.putString("pwd",newPsw);
        edit.commit();
    }
}
