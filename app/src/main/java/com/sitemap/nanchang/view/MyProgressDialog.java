package com.sitemap.nanchang.view;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.sitemap.nanchang.R;

/**
 * @desc 加载圆形提示框
 * Created by chenmeng on 2016/11/28.
 */
public class MyProgressDialog extends Dialog{

    private static MyProgressDialog progressDialog = null;

    public void setCallBack(OnStartCallBack callBack) {
        this.callBack = callBack;
    }

    private OnStartCallBack callBack;

    public interface OnStartCallBack{
        void onStart();
    }

    public MyProgressDialog(Context context) {
        super(context);
    }

    public MyProgressDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    /**
     * 创建dialog
     * @param context
     * @return
     */
    public static MyProgressDialog createDialog(Context context){
        progressDialog =  new MyProgressDialog(context, R.style.progress_dialog);
        progressDialog.setContentView(R.layout.my_progress_dialog);
        progressDialog.getWindow().getAttributes().gravity = Gravity.CENTER;
        progressDialog.setCanceledOnTouchOutside(false);
        return progressDialog;
    }

    /**
     * 设置提示的信息
     * @param title
     */
    public void setMessage(String title) {
        TextView tvMsg = (TextView) progressDialog.findViewById(R.id.dialog_msg);
        if (title != null && title.length()>0) {
            tvMsg.setVisibility(View.VISIBLE);
            tvMsg.setText(title);
        }
    }

}
