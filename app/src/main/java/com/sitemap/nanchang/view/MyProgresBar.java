package com.sitemap.nanchang.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ProgressBar;


/**
 * Created by Administrator on 2016/12/12.
 */

public class MyProgresBar extends ProgressBar {

    public void setCallBack(StartCallBack callBack) {
        this.callBack = callBack;
    }

    private StartCallBack callBack;

    public interface StartCallBack{
        public void onStart();
    }

    public MyProgresBar(Context context) {
        super(context);
    }

    public MyProgresBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyProgresBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

}
