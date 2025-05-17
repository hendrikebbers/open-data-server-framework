package com.openelements.data.runtime.sql;

import com.openelements.data.runtime.SqlDataType;

public class BooleanType implements SqlDataType<Boolean> {

    public static final BooleanType INSTANCE = new BooleanType();

    @Override
    public String getSqlType() {
        return "BOOLEAN";
    }

    @Override
    public Class<Boolean> getJavaType() {
        return Boolean.class;
    }
}
