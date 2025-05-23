package com.openelements.data.runtime.sql.support;

import com.openelements.data.runtime.sql.types.VarCharType;

public class StringSupport extends AbstractMatchingDataAttributeTypeSupport<String> {

    public StringSupport() {
        super("String", VarCharType.INSTANCE);
    }

}
