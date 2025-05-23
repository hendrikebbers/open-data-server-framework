package com.openelements.data.runtime.sql;

public interface SqlDataType<T> {

    String getSqlType();

    Class<T> getJavaType();

}
