package com.openelements.data.runtime.sql;

import com.openelements.data.runtime.SqlDataType;

public class BooleanType implements SqlDataType<Boolean> {

    @Override
    public String getSqlType() {
        return "BOOLEAN";
    }

    @Override
    public Class<Boolean> getJavaType() {
        return Boolean.class;
    }
}
