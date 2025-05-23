package com.openelements.data.runtime.sql.support;

import com.openelements.data.runtime.sql.types.SqlDataType;

public abstract class AbstractDataAttributeTypeSupport<T, U> implements DataAttributeTypeSupport<T, U> {

    private final String uniqueName;

    private final Class<T> javaType;

    private final SqlDataType<U> sqlDataType;

    protected AbstractDataAttributeTypeSupport(String uniqueName, Class<T> javaType, SqlDataType<U> sqlDataType) {
        this.uniqueName = uniqueName;
        this.javaType = javaType;
        this.sqlDataType = sqlDataType;
    }

    @Override
    public String getUniqueName() {
        return uniqueName;
    }

    @Override
    public Class<T> getJavaType() {
        return javaType;
    }

    @Override
    public SqlDataType<U> getSqlDataType() {
        return sqlDataType;
    }
}
