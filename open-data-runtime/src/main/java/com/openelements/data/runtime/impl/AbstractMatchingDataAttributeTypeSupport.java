package com.openelements.data.runtime.impl;

import com.openelements.data.runtime.SqlDataType;

public abstract class AbstractMatchingDataAttributeTypeSupport<D> implements MatchingDataAttributeTypeSupport<D> {

    private final String uniqueName;

    private final SqlDataType<D> sqlDataType;

    protected AbstractMatchingDataAttributeTypeSupport(String uniqueName, SqlDataType<D> sqlDataType) {
        this.uniqueName = uniqueName;
        this.sqlDataType = sqlDataType;
    }

    @Override
    public String getUniqueName() {
        return uniqueName;
    }

    @Override
    public SqlDataType<D> getSqlDataType() {
        return sqlDataType;
    }
}
