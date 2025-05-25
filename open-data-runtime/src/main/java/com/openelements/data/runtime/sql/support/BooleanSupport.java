package com.openelements.data.runtime.sql.support;

public class BooleanSupport extends AbstractMatchingDataAttributeTypeSupport<Boolean> {

    public BooleanSupport() {
        super("Boolean", "BOOLEAN", Boolean.class);
    }

}
