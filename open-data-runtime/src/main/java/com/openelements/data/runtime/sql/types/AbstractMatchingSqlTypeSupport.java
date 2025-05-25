package com.openelements.data.runtime.sql.types;

public abstract class AbstractMatchingSqlTypeSupport<D> implements MatchingSqlTypeSupport<D> {

    private final String uniqueName;

    private final String sqlType;

    private final Class<D> javaClass;

    protected AbstractMatchingSqlTypeSupport(String uniqueName, String sqlType, Class<D> javaClass) {
        this.uniqueName = uniqueName;
        this.sqlType = sqlType;
        this.javaClass = javaClass;
    }

    @Override
    public String getUniqueName() {
        return uniqueName;
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
