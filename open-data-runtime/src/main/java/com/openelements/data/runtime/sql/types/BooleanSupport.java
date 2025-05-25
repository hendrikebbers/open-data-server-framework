package com.openelements.data.runtime.sql.types;

public class BooleanSupport extends AbstractMatchingSqlTypeSupport<Boolean> {

    public BooleanSupport() {
        super("Boolean", "BOOLEAN", Boolean.class);
    }

}
