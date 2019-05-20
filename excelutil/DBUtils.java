package com.bgyfw.erp.resources.honor.util;

import lombok.extern.slf4j.Slf4j;
import tk.mybatis.mapper.additional.insert.InsertListMapper;
import tk.mybatis.mapper.common.MySqlMapper;

import java.util.List;

@Slf4j
public class DBUtils {

    public static void insertListExcute(List dataList, InsertListMapper mapper, int pointsDataLimit) {
        if (null != dataList && dataList.size() > 0) {
            //限制条数,【修改这里】
            Integer size = dataList.size();
            //判断是否有必要分批
            if (pointsDataLimit < size) {
                //分批数
                int part = size / pointsDataLimit;
                log.debug("共有 ： " + size + "条，！" + " 分为 ：" + part + "批");
                for (int i = 0; i < part; i++) {
                    List<String> listPage = dataList.subList(0, pointsDataLimit);
                    log.debug("第" + (i + 1) + "次,执行处理:" + listPage);
                    mapper.insertList(listPage);
                    dataList.subList(0, pointsDataLimit).clear();
                }
                if (!dataList.isEmpty()) {
                    //表示最后剩下的数据
                    mapper.insertList(dataList);
                    log.debug("最后一批数据,执行处理:" + dataList);
                }
            } else {
                mapper.insertList(dataList);
                log.debug("不需要分批,执行处理:" + dataList);
            }
        } else {
            log.debug("没有数据!!!");
        }
    }


    public static void updateListExcute(List dataList, UpdateListMapper mapper, int pointsDataLimit) {
        if (null != dataList && dataList.size() > 0) {
            //限制条数,【修改这里】
            Integer size = dataList.size();
            //判断是否有必要分批
            if (pointsDataLimit < size) {
                //分批数
                int part = size / pointsDataLimit;
                log.debug("共有 ： " + size + "条，！" + " 分为 ：" + part + "批");
                for (int i = 0; i < part; i++) {
                    List<String> listPage = dataList.subList(0, pointsDataLimit);
                    log.debug("第" + (i + 1) + "次,执行处理:" + listPage);
                    mapper.updateList(listPage);
                    dataList.subList(0, pointsDataLimit).clear();
                }
                if (!dataList.isEmpty()) {
                    //表示最后剩下的数据
                    mapper.updateList(dataList);
                    log.debug("最后一批数据,执行处理:" + dataList);
                }
            } else {
                mapper.updateList(dataList);
                log.debug("不需要分批,执行处理:" + dataList);
            }
        } else {
            log.debug("没有数据!!!");
        }
    }

}
