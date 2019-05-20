package com.bgyfw.erp.resources.honor.util;

import java.util.List;

public interface IValidator<T> {

    default void beforeValidate(){}

    default void afterValidate(List<T> success,List<T> error){}

}