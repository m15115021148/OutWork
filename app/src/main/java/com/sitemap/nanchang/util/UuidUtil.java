package com.sitemap.nanchang.util;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.UUID;

/**
 * @desc 获取uuid
 * Created by chenmeng on 2016/12/1.
 */

public class UuidUtil {
    /**UUID */
    private static String mUUID = null;
    /**标识符*/
    private static final String INSTALLATION = "INSTALLATION";

    /**
     * 获取手机唯一标识符uuid
     *
     * @return
     */
    public static String getUUID(Context context) {
        if (mUUID == null) {
            File installation = new File(context.getFilesDir(), INSTALLATION);
            try {
                if (!installation.exists())
                    writeInstallationFile(installation);
                mUUID = readInstallationFile(installation);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return mUUID;
    }

    /**
     * 读取保存的uuid
     * @param installation
     * @return
     * @throws IOException
     */
    private static String readInstallationFile(File installation)
            throws IOException {
        RandomAccessFile f = new RandomAccessFile(installation, "r");
        byte[] bytes = new byte[(int) f.length()];
        f.readFully(bytes);
        f.close();
        return new String(bytes);
    }

    /**
     * 写入文件中
     * @param installation
     * @throws IOException
     */
    private static void writeInstallationFile(File installation)
            throws IOException {
        FileOutputStream out = new FileOutputStream(installation);
        String id = UUID.randomUUID().toString();
        out.write(id.getBytes());
        out.close();
    }

}
