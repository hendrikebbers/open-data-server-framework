package com.openelements.data.runtime.sql.types.impl;

import com.openelements.data.runtime.h2.H2Dialect;
import com.openelements.data.runtime.sql.types.AbstractMatchingSqlTypeSupport;
import java.util.Set;

public class PrimitiveDoubleSupport extends AbstractMatchingSqlTypeSupport<Double> {

    public PrimitiveDoubleSupport() {
        super(Double.TYPE, "DOUBLE PRECISION");
    }

    @Override
    public Set<String> getSupportedJdbcDrivers() {
        return Set.of(H2Dialect.DRIVER_CLASS_NAME);
    }

}
