package com.openelements.data.runtime.sql.types.impl;

import com.openelements.data.runtime.h2.H2Dialect;
import com.openelements.data.runtime.sql.types.AbstractMatchingSqlTypeSupport;
import java.util.Set;

public class BooleanSupport extends AbstractMatchingSqlTypeSupport<Boolean> {

    public BooleanSupport() {
        super(Boolean.class, "BOOLEAN");
    }

    @Override
    public Set<String> getSupportedJdbcDrivers() {
        return Set.of(H2Dialect.DRIVER_CLASS_NAME);
    }
}
