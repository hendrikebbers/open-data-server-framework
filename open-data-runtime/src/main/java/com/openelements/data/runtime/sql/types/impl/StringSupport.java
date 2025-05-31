package com.openelements.data.runtime.sql.types.impl;

import com.openelements.data.runtime.sql.h2.H2Dialect;
import com.openelements.data.runtime.sql.types.AbstractMatchingSqlTypeSupport;
import java.util.Set;

public class StringSupport extends AbstractMatchingSqlTypeSupport<String> {

    public StringSupport() {
        super(String.class, "VARCHAR");
    }

    @Override
    public Set<String> getSupportedJdbcDrivers() {
        return Set.of(H2Dialect.DRIVER_CLASS_NAME);
    }
}
