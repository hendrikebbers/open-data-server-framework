package com.openelements.data.runtime.sql.types;

public class VarCharType extends AbstractSqlDataType<String> {

    public static final VarCharType INSTANCE = new VarCharType();

    public VarCharType() {
        super("VARCHAR", String.class);
    }
}
