package com.openelements.data.runtime.sql.types;

public class ReferenceType extends AbstractSqlDataType<Long> {

    public static final ReferenceType INSTANCE = new ReferenceType();

    public ReferenceType() {
        super("LONG", Long.class);
    }

}
