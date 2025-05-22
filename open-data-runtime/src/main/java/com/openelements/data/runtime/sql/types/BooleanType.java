package com.openelements.data.runtime.sql.types;

public class BooleanType extends AbstractSqlDataType<Boolean> {

    public static final BooleanType INSTANCE = new BooleanType();

    public BooleanType() {
        super("BOOLEAN", Boolean.class);
    }
}
