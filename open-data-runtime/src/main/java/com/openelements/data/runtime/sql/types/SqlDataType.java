package com.openelements.data.runtime.sql.types;

public interface SqlDataType<T> {

    String getSqlType();

    Class<T> getJavaType();

}
