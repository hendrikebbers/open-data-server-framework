package com.openelements.data.runtime.sql.support;

public abstract class AbstractMatchingDataAttributeTypeSupport<D> implements MatchingDataAttributeTypeSupport<D> {

    private final String uniqueName;

    private final String sqlType;

    private final Class<D> javaClass;

    protected AbstractMatchingDataAttributeTypeSupport(String uniqueName, String sqlType, Class<D> javaClass) {
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
