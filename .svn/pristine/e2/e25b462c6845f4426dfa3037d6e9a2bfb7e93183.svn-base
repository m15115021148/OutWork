package com.sitemap.nanchang.application;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.baidu.mapapi.SDKInitializer;
import com.sitemap.nanchang.config.RequestCode;
import com.sitemap.nanchang.util.UuidUtil;

import org.xutils.BuildConfig;
import org.xutils.x;

import java.io.File;
import java.security.MessageDigest;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @desc 启动 全局配置
 * Created by chenmeng on 2016/11/30.
 */

public class MyApplication extends Application{
//    public static String uuid = "";//uuid
    public static String[] numberSum = null;//所有任务的编号集合
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    public static String appSelPath = "";//用户选择的目录路径
    public static String appRootPath = "";//数据存放的根目录路径
    public static String userName = "";//用户名
    /**application对象*/
    private static MyApplication instance;
    public static int gatherNum = 0;//采集点的数据
    public static int gpsTime = 1000*30;//gps采集时间间隔
    public static String psw = "";//密码
    public static File appFile;//app根目录文件夹
    public static File taskFile;//专案目录文件夹
    public static File taskTempFile;//专案 压缩存放目录文件夹
    public static File gatherFile;//采集点目录文件夹
    public static File gatherTempFile;//采集点 压缩存放目录文件夹

    @Override
    public void onCreate() {
        super.onCreate();
        initXutils();
        appSelPath = Environment.getExternalStorageDirectory().toString();
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);

        if (!preferences.getString("path",appSelPath).equals(appSelPath)) {
            appSelPath=preferences.getString("path",appSelPath);
        }
        Log.i("TAG","appSelPath:"+appSelPath);
        appRootPath = appSelPath + "/"+ RequestCode.APPNAME;
//        uuid = UuidUtil.getUUID(getApplicationContext());
        SDKInitializer.initialize(getApplicationContext());
    }

    public static MyApplication instance() {
        if (instance != null) {
            return instance;
        } else {
            return new MyApplication();
        }
    }

    /**
     * 初始化xutils框架
     */
    private void initXutils() {
        x.Ext.init(this);
        x.Ext.setDebug(BuildConfig.DEBUG); // 是否输出debug日志, 开启debug会影响性能.

    }

    /**
     * 描述：MD5加密.
     *
     * @param str
     *            要加密的字符串
     * @return String 加密的字符串
     */
    public static String MD5(String str) {
        char hexDigits[] = { // 用来将字节转换成 16 进制表示的字符
                'g', 'h', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'a', 'b', 'c', 'd',
                'e', 'f' };
        try {
            byte[] strTemp = str.getBytes();
            MessageDigest mdTemp = MessageDigest.getInstance("MD5");
            mdTemp.update(strTemp);
            byte tmp[] = mdTemp.digest(); // MD5 的计算结果是一个 128 位的长整数，
            // 用字节表示就是 16 个字节
            char strs[] = new char[16 * 2]; // 每个字节用 16 进制表示的话，使用两个字符，
            // 所以表示成 16 进制需要 32 个字符
            int k = 0; // 表示转换结果中对应的字符位置
            for (int i = 0; i < 16; i++) { // 从第一个字节开始，对 MD5 的每一个字节
                // 转换成 16 进制字符的转换
                byte byte0 = tmp[i]; // 取第 i 个字节
                strs[k++] = hexDigits[byte0 >>> 4 & 0xf]; // 取字节中高 4 位的数字转换,
                // >>> 为逻辑右移，将符号位一起右移
                strs[k++] = hexDigits[byte0 & 0xf]; // 取字节中低 4 位的数字转换
            }
            return new String(strs).toUpperCase(); // 换后的结果转换为字符串
        } catch (Exception e) {
            return null;
        }
    }

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE);
        }
    }

    /**
     * 必须是数字和字母组合
     *
     * @param str
     * @return
     */
    public static boolean isNumberOrChars(String str) {
        Pattern p = Pattern
                .compile("^(?![0-9]+$)|(?![a-zA-Z]+$)|[0-9A-Za-z]+$");
        Matcher m = p.matcher(str);
        return m.matches();
    }

    /**
     * 得到随机的颜色
     * @return
     */
    public static int randomColor(){
        int a = getRandom(10, 200);
        int b = getRandom(10, 200);
        int c = getRandom(30, 200);
        int d = getRandom(20, 200);
        int argb = Color.argb(a,b,c,d);
        return argb;
    }

    /**
     * 得到随机int介于最小和最大值
     *
     * @param min
     * @param max
     * @return <ul>
     */
    private static int getRandom(int min, int max) {
        if (min > max) {
            return 0;
        }
        if (min == max) {
            return min;
        }
        return min + new Random().nextInt(max - min);
    }
}
