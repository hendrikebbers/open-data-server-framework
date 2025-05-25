package com.openelements.data.runtime.sql.types;

public class StringSupport extends AbstractMatchingSqlTypeSupport<String> {

    public StringSupport() {
        super("String", "VARCHAR", String.class);
    }

}
