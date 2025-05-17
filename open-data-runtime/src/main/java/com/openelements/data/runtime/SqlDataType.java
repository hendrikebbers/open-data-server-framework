package com.openelements.data.runtime;

public interface SqlDataType<T> {

    String getSqlType();

    Class<T> getJavaType();

}
