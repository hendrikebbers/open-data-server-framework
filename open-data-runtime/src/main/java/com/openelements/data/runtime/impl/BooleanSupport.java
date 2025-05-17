package com.openelements.data.runtime.impl;

import com.openelements.data.runtime.DataAttributeTypeSupport;
import com.openelements.data.runtime.QueryContext;
import com.openelements.data.runtime.SqlDataType;
import com.openelements.data.runtime.sql.BooleanType;

public class BooleanSupport implements DataAttributeTypeSupport<Boolean, Boolean> {

    @Override
    public String getUniqueName() {
        return "Boolean";
    }

    @Override
    public Class<Boolean> getJavaType() {
        return Boolean.class;
    }

    @Override
    public SqlDataType getSqlDataType() {
        return new BooleanType();
    }

    @Override
    public Boolean convertValueFromSqlResult(Boolean sqlValue, QueryContext queryContext) {
        return sqlValue;
    }
}
