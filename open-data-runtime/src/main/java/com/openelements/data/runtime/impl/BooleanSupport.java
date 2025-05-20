package com.openelements.data.runtime.impl;

import com.openelements.data.runtime.sql.BooleanType;

public class BooleanSupport extends AbstractMatchingDataAttributeTypeSupport<Boolean> {

    public BooleanSupport() {
        super("Boolean", BooleanType.INSTANCE);
    }

}
