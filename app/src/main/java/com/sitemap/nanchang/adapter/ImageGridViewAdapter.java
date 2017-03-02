package com.sitemap.nanchang.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.sitemap.nanchang.R;
import com.sitemap.nanchang.model.DataType;
import com.sitemap.nanchang.model.PathModel;
import com.sitemap.nanchang.util.HttpImageUtil;

import java.util.List;

/**
 * @dese 图片布局适配器
 * Created by chenmeng on 2016/11/30.
 */

public class ImageGridViewAdapter extends BaseAdapter {
    private Context mContext;
    private List<PathModel> mList;
    private Holder holder;
    private boolean isShow=false;//选择按钮是否显示
    private DataType type;//数据类型

    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean show) {
        isShow = show;
    }

    public ImageGridViewAdapter(Context context, List<PathModel> list,  DataType type) {
        this.mContext = context;
        this.mList = list;
        this.type=type;
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.image_grid_view_item, null);
            holder = new Holder();
            holder.img = (ImageView) convertView.findViewById(R.id.img);
            holder.sel = (ImageView) convertView.findViewById(R.id.sel);
            holder.mLayout = (RelativeLayout) convertView.findViewById(R.id.sel_check);
            convertView.setTag(holder);

        } else {
            holder = (Holder) convertView.getTag();
        }

        if (position==0){
//        摄像头
        }else{

            if(type==DataType.IMG){
                HttpImageUtil.loadImage(holder.img, mList.get(position).getPath());
            }
            if(type==DataType.VOICE){
//                MediaMetadataRetriever media = new MediaMetadataRetriever();
//                media.setDataSource(mList.get(position).getPath());
//
//                Bitmap bitmap = media.getFrameAtTime();
//                holder.img.setImageBitmap(bitmap);
                holder.img.setImageResource(R.drawable.voice);
            }
            if(type==DataType.VIDEO){
//                MediaMetadataRetriever media = new MediaMetadataRetriever();
//                media.setDataSource(mList.get(position).getPath());
//
//                Bitmap bitmap = media.getFrameAtTime();
//                holder.img.setImageResource(R.drawable.img_failure);
                Bitmap bmp = ThumbnailUtils.createVideoThumbnail(mList.get(position).getPath(),
                        MediaStore.Video.Thumbnails.MINI_KIND);
                holder.img.setImageBitmap(bmp);
            }

            boolean isShow=isShow();
            if (isShow) {
                holder.sel.setVisibility(View.VISIBLE);
            } else {
                holder.sel.setVisibility(View.GONE);
            }
            boolean isSelected=mList.get(position).isSelected();
            if (isSelected) {
                holder.sel.setSelected(true);
            } else {
                holder.sel.setSelected(false);
            }
        }
        return convertView;
    }

    private class Holder {
        ImageView img;
        ImageView sel;
        RelativeLayout mLayout;
    }
}
