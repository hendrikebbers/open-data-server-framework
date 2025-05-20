package com.openelements.data.runtime.sql;

public class BooleanType extends AbstractSqlDataType<Boolean> {

    public static final BooleanType INSTANCE = new BooleanType();

    public BooleanType() {
        super("BOOLEAN", Boolean.class);
    }
}
