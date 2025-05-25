package com.openelements.data.runtime.sql.types;

public abstract class AbstractMatchingSqlTypeSupport<D> implements MatchingSqlTypeSupport<D> {

    private final String sqlType;

    private final Class<D> javaClass;

    protected AbstractMatchingSqlTypeSupport(Class<D> javaClass, String sqlType) {
        this.sqlType = sqlType;
        this.javaClass = javaClass;
    }

    @Override
    public String getSqlType() {
        return sqlType;
    }

    @Override
    public Class<D> getJavaType() {
        return javaClass;
    }
}
