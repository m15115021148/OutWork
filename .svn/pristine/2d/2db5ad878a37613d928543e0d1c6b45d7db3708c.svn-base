package com.sitemap.nanchang.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sitemap.nanchang.R;
import com.sitemap.nanchang.model.PatchModel;
import com.sitemap.nanchang.model.TaskModel;
import com.sitemap.nanchang.model.TaskPathModel;
import com.sitemap.nanchang.util.HttpImageUtil;

import java.util.List;

/**
 * @dese 采集点适配器
 * Created by zf on 2016/12/2
 */

public class PatchListViewAdapter extends BaseAdapter {
    private Context mContext;
    private List<PatchModel> mList;
    private List<TaskPathModel> isZip;
    private Holder holder;

    public void setCallBack(OnClickPatchUpLoading callBack) {
        this.callBack = callBack;
    }

    private OnClickPatchUpLoading callBack;

    public interface OnClickPatchUpLoading{
        void onClickUpLoading(int pos,int type);//1为上传，2删除
    }


    public PatchListViewAdapter(Context context, List<PatchModel> list, List<TaskPathModel> isZip, OnClickPatchUpLoading callBack) {
        this.mContext = context;
        this.mList = list;
        this.isZip=isZip;
        this.callBack = callBack;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.patch_list_view_item, null);
            holder = new Holder();
            holder.task_image = (ImageView) convertView.findViewById(R.id.task_image);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.upText = (TextView) convertView.findViewById(R.id.up_loading_text);
            holder.address = (TextView) convertView.findViewById(R.id.address);
            holder.time = (TextView) convertView.findViewById(R.id.time);
            holder.uploadText= (TextView) convertView.findViewById(R.id.uploadText);
            holder.upLoading = (ImageView) convertView.findViewById(R.id.upLoading);
            holder.progressBar=(ProgressBar) convertView.findViewById(R.id.progressBar);
            holder.uploadLayout=(LinearLayout)convertView.findViewById(R.id.uploadLayout) ;
            holder.deleteLayout=(LinearLayout) convertView.findViewById(R.id.deleteLayout);
            convertView.setTag(holder);

        } else {
            holder = (Holder) convertView.getTag();
        }
         holder.name.setText(mList.get(position).getName());
        holder.upText.setText(mList.get(position).getUploadText());
        holder.address.setText(mList.get(position).getAddress());
        holder.time.setText(mList.get(position).getTime());
        HttpImageUtil.loadImage(holder.task_image,isZip.get(position).getJpgImgPath());

        if (isZip.get(position).getIsZip().equals("1")){
            holder.upLoading.setVisibility(View.VISIBLE);

            if (isZip.get(position).getUpload().equals("1")){
                holder.upLoading.setVisibility(View.GONE);
                holder.progressBar.setVisibility(View.VISIBLE);
            }else{
                holder.upLoading.setVisibility(View.VISIBLE);
                holder.progressBar.setVisibility(View.GONE);
            }
            holder.uploadText.setTextColor(Color.parseColor("#1D7DFE"));
        }else {
            holder.upLoading.setVisibility(View.GONE);
            holder.uploadText.setTextColor(Color.GRAY);
        }

        final int pos = position;
        holder.uploadLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callBack!=null){
                    callBack.onClickUpLoading(pos,1);
                }
            }
        });
        holder.deleteLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callBack!=null){
                    callBack.onClickUpLoading(pos,2);
                }
            }
        });

        return convertView;
    }

    private class Holder {
        ImageView task_image,upLoading;
        TextView name,upText,address,time,uploadText;
        LinearLayout uploadLayout,deleteLayout;
        ProgressBar progressBar;
    }
}
