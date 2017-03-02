package com.sitemap.nanchang.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 创建图片、音频、视频的文件名称
 *
 * @author chenhao
 */
public class FileNames {
    private String imageName;
    private String voiceName;
    private String videoName;
    private SimpleDateFormat timeStampFormat;
    private String fileName;
    private String zipName;
    private String gatherName;
    private String zipGatherName;
    private String zipTaskName;


    public FileNames() {
        timeStampFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA);
    }

    public String getImageName() {
        fileName = timeStampFormat.format(new Date());
        imageName = "IMG_" + fileName + ".jpg";
        return imageName;
    }

    public String getVoiceName() {
        fileName = timeStampFormat.format(new Date());
        voiceName = "VOICE_" + fileName + ".amr";
        return voiceName;
    }

    public String getVideoName() {
        fileName = timeStampFormat.format(new Date());
        videoName = "VIDEO_" + fileName + ".mp4";
        return videoName;
    }

    public String getZipName() {
        zipName = timeStampFormat.format(new Date());
        zipName = "_" + zipName + ".zip";
        return zipName;
    }

    public String getGatherName() {
        gatherName = timeStampFormat.format(new Date());
        gatherName = "gather_" + gatherName;
        return gatherName;
    }

    public String getZipGatherName(String name) {
        zipGatherName = timeStampFormat.format(new Date());
        zipGatherName = "DATA_"+ name + "_"+zipGatherName+".zip";
        return zipGatherName;
    }

    public String getZipTaskName(String name) {
        zipTaskName = timeStampFormat.format(new Date());
        zipTaskName = "TASK_"+ name +"_"+zipTaskName+ ".zip";
        return zipTaskName;
    }

}
