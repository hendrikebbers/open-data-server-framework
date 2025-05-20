package com.openelements.data.runtime.sql;

import com.openelements.data.runtime.SqlDataType;

public abstract class AbstractSqlDataType<T> implements SqlDataType<T> {

    private final String sqlType;

    private final Class<T> javaType;

    protected AbstractSqlDataType(String sqlType, Class<T> javaType) {
        this.sqlType = sqlType;
        this.javaType = javaType;
    }

    @Override
    public String getSqlType() {
        return sqlType;
    }

    @Override
    public Class<T> getJavaType() {
        return javaType;
    }
}
