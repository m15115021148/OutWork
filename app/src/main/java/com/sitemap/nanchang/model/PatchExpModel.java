package com.sitemap.nanchang.model;

import java.io.File;
import java.io.Serializable;
import java.util.List;

/**
 * @desc 采集点 类别 实体类
 * Created by chenmeng on 2017/1/4.
 */
public class PatchExpModel implements Serializable{
    private String type;
    private List<PatchModel> patchModelList;

    private String typeNamePath;//类别路径
    private String isZip;//是否压缩  1为压缩
    private String zipPath;//需要压缩文件的路径
    private List<TaskPathModel> zipPathList;
    private String tmpZipPath;//压缩文件存放的路径
    private String isAdd;//是否能添加 1能
    private String isStatus;//是否展开 1 展开
    private File patchFile;//采集目录 gather

    public File getPatchFile() {
        return patchFile;
    }

    public void setPatchFile(File patchFile) {
        this.patchFile = patchFile;
    }

    public String getIsStatus() {
        return isStatus;
    }

    public void setIsStatus(String isStatus) {
        this.isStatus = isStatus;
    }

    public String getIsAdd() {
        return isAdd;
    }

    public void setIsAdd(String isAdd) {
        this.isAdd = isAdd;
    }

    public String getTmpZipPath() {
        return tmpZipPath;
    }

    public void setTmpZipPath(String tmpZipPath) {
        this.tmpZipPath = tmpZipPath;
    }

    public List<TaskPathModel> getZipPathList() {
        return zipPathList;
    }

    public void setZipPathList(List<TaskPathModel> zipPathList) {
        this.zipPathList = zipPathList;
    }

    public String getZipPath() {
        return zipPath;
    }

    public void setZipPath(String zipPath) {
        this.zipPath = zipPath;
    }

    public String getTypeNamePath() {
        return typeNamePath;
    }

    public void setTypeNamePath(String typeNamePath) {
        this.typeNamePath = typeNamePath;
    }

    public String getIsZip() {
        return isZip;
    }

    public void setIsZip(String isZip) {
        this.isZip = isZip;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<PatchModel> getPatchModelList() {
        return patchModelList;
    }

    public void setPatchModelList(List<PatchModel> patchModelList) {
        this.patchModelList = patchModelList;
    }
}
