package com.openelements.data.runtime.sql.types.impl;

import com.openelements.data.runtime.h2.H2Dialect;
import com.openelements.data.runtime.sql.types.AbstractMatchingSqlTypeSupport;
import java.util.Set;

public class PrimitiveBooleanSupport extends AbstractMatchingSqlTypeSupport<Boolean> {

    public PrimitiveBooleanSupport() {
        super(Boolean.TYPE, "BOOLEAN");
    }

    @Override
    public Set<String> getSupportedJdbcDrivers() {
        return Set.of(H2Dialect.DRIVER_CLASS_NAME);
    }

}
