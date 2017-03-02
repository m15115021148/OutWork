package com.sitemap.nanchang.util;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipUtil {

    /**
     * 压缩文件,文件夹
     *
     * @param srcFilePath 要压缩的文件/文件夹名字
     * @param zipFilePath 指定压缩的目的和名字
     * @throws Exception
     */
    public String ZipFolder(String srcFilePath, String zipFilePath,String zipName) throws Exception {
        Log.e("result","压缩文件夹的路径:"+srcFilePath);
        ZipOutputStream outZip = null;
        try {
           //创建Zip包
            FileOutputStream os = new FileOutputStream(zipFilePath + "/" + zipName);
            Log.e("result","压缩文件夹存放的路径:"+zipFilePath + "/" + zipName);
            outZip = new ZipOutputStream(os);
           //打开要输出的文件
           File file = new File(srcFilePath);
           //压缩
           ZipFiles(file.getParent() + File.separator, file.getName(), outZip);
            return zipFilePath + "/" + zipName;
       }catch (Exception e){
           e.printStackTrace();
       }finally {
            // 关闭输出流
            if (outZip != null) {
                if (getCallBack()!=null){
                    callBack.onZipOver();
                }
                //完成,关闭
                outZip.finish();
                outZip.close();
            }
       }
        return null;
    }

    /**
     * 压缩文件
     *
     * @param folderString
     * @param fileString
     * @param zipOutputSteam
     * @throws Exception
     */
    private void ZipFiles(String folderString, String fileString, java.util.zip.ZipOutputStream zipOutputSteam) throws Exception {
        if (zipOutputSteam == null)
            return;
        File file = new File(folderString + fileString);

        //判断是不是文件
        if (file.isFile()) {
            ZipEntry zipEntry = new ZipEntry(fileString);
            FileInputStream inputStream = new FileInputStream(file);
            zipOutputSteam.putNextEntry(zipEntry);

            int len;
            byte[] buffer = new byte[4096];
            while ((len = inputStream.read(buffer)) != -1) {
                zipOutputSteam.write(buffer, 0, len);
            }
            zipOutputSteam.closeEntry();
        } else {

            //文件夹的方式,获取文件夹下的子文件
            String fileList[] = file.list();

            //如果没有子文件, 则添加进去即可
            if (fileList.length <= 0) {
                ZipEntry zipEntry = new ZipEntry(fileString + File.separator);
                zipOutputSteam.putNextEntry(zipEntry);
                zipOutputSteam.closeEntry();
            }

            //如果有子文件, 遍历子文件
            for (int i = 0; i < fileList.length; i++) {
                ZipFiles(folderString, fileString + File.separator + fileList[i], zipOutputSteam);
            }
        }
    }

    public ZipOverCallBack getCallBack() {
        return callBack;
    }

    public void setCallBack(ZipOverCallBack mCallBack) {
        this.callBack = mCallBack;
    }

    private ZipOverCallBack callBack;

    public interface ZipOverCallBack {
        void onZipOver();
    }

}
