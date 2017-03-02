package com.sitemap.nanchang.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sitemap.nanchang.R;
import com.sitemap.nanchang.config.RequestCode;

/**
 * 文字的内容 输入
 *
 * @author chenmeng created by 2016/11/30
 */
public class CharDetailActivity extends BaseActivity implements View.OnClickListener {
    private CharDetailActivity mContext;//本类
    private LinearLayout mBack;//返回
    private TextView mTitle;//标题
    private EditText mDetail;//文字内容

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_char_detail);
        mContext = this;
        mBack = (LinearLayout) findViewById(R.id.include_title).findViewById(R.id.back);
        mBack.setOnClickListener(this);
        mTitle = (TextView) findViewById(R.id.include_title).findViewById(R.id.title);
        mTitle.setText("文字");
        mDetail = (EditText) findViewById(R.id.char_detail);
        mDetail.setText(getIntent().getStringExtra("chars"));
    }

    @Override
    public void onClick(View v) {
        if (v == mBack){
            onBackPressed();
            mContext.finish();
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("chars",mDetail.getText().toString());
        setResult(RequestCode.CHARS,intent);
        super.onBackPressed();
    }
}
