package com.openelements.data.runtime.sql.support;

public abstract class AbstractDataAttributeTypeSupport<T, U> implements DataAttributeTypeSupport<T, U> {

    private final String uniqueName;

    private final Class<T> javaType;

    private final String sqlType;

    protected AbstractDataAttributeTypeSupport(String uniqueName, Class<T> javaType, String sqlType) {
        this.uniqueName = uniqueName;
        this.javaType = javaType;
        this.sqlType = sqlType;
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
    public String getSqlType() {
        return "sqlType";
    }
}
