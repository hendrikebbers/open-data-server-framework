package com.openelements.data.runtime.sql.support;

public class StringSupport extends AbstractMatchingDataAttributeTypeSupport<String> {

    public StringSupport() {
        super("String", "VARCHAR", String.class);
    }

}
