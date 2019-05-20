package com.bgyfw.erp.resources.honor.util;

import org.apache.ibatis.annotations.UpdateProvider;

import java.util.List;

@tk.mybatis.mapper.annotation.RegisterMapper
public interface UpdateListMapper<T>  {

    @UpdateProvider(type = UpdateListProvider.class, method = "dynamicSQL")
    int updateList(List<? extends T> recordList);

}
