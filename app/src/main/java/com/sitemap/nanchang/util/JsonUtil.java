package com.sitemap.nanchang.util;

import com.alibaba.fastjson.JSONObject;
import com.sitemap.nanchang.model.TaskModel;

/**
 * @desc 将实体类构建成json数据
 * Created by chenmeng on 2016/12/1.
 */

public class JsonUtil {

    /**
     * 将数据构建成json形式
     * @param model
     * @return
     */
    public static String buildJsonData(TaskModel model){
        JSONObject object = new JSONObject();
        object.put("uuid",model.getUuid());
        object.put("taskName",model.getTaskName());
        object.put("taskNumber",model.getTaskNumber());
        object.put("address",model.getAddress());
        object.put("company",model.getCompany());
        object.put("uploadText",model.getUploadText());
        object.put("time",model.getTime());
        return object.toString();
    }

}
