package com.sitemap.nanchang.util;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.sitemap.nanchang.R;

import org.xutils.image.ImageOptions;
import org.xutils.x;

/**
 * @desc 加载网络图片 使用xutil3加载
 * Created by chenmeng on 2016/10/9.
 */
public class HttpImageUtil {

    /**
     * 初始化图片配置属性
     * @return
     */
    public static ImageOptions getInstance(){
        ImageOptions imageOptions = new ImageOptions.Builder()
//                .setSize(-1, -1)//默认适应图片大小
//                .setRadius(DensityUtil.dip2px(5))
                        // 如果ImageView的大小不是定义为wrap_content, 不要crop.
//                .setCrop(true)
                        // 加载中或错误图片的ScaleType
                        //.setPlaceholderScaleType(ImageView.ScaleType.MATRIX)
                .setImageScaleType(ImageView.ScaleType.CENTER)
                        //设置图片的质量
                .setConfig(Bitmap.Config.RGB_565)
                        //设置加载过程中的图片
                .setLoadingDrawableId(R.drawable.img_failure)
                        //设置加载失败后的图片
                .setFailureDrawableId(R.drawable.img_failure)
                        //设置使用缓存
                .setUseMemCache(true)
                        //设置支持gif
                .setIgnoreGif(false)
                        //设置显示圆形图片
//                .setCircular(true)
                .build();
        return imageOptions;
    }

    /**
     * 加载图片
     * @param iv imageview
     * @param path 图片路径
     */
    public static void loadImage(ImageView iv, String path) {
        x.image().bind(iv,path,getInstance());
    }

    /**
     * 清除缓存
     */
    public static void clearCache(){
        x.image().clearMemCache();
    }
}
