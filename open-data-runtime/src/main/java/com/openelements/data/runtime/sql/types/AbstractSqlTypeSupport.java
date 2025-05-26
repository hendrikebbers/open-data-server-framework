package com.openelements.data.runtime.sql.types;

public abstract class AbstractSqlTypeSupport<T, U> implements SqlTypeSupport<T, U> {

    private final Class<T> javaType;

    private final String sqlType;

    protected AbstractSqlTypeSupport(Class<T> javaType, String sqlType) {
        this.javaType = javaType;
        this.sqlType = sqlType;
    }

    @Override
    public Class<T> getJavaClass() {
        return javaType;
    }

    @Override
    public String getNativeSqlType() {
        return sqlType;
    }
}
