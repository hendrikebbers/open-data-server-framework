package com.openelements.data.runtime.sql.support;

import com.openelements.data.runtime.sql.types.BooleanType;

public class BooleanSupport extends AbstractMatchingDataAttributeTypeSupport<Boolean> {

    public BooleanSupport() {
        super("Boolean", BooleanType.INSTANCE);
    }

}
