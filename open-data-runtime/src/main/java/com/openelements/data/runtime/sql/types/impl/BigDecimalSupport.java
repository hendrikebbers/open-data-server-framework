package com.openelements.data.runtime.sql.types.impl;

import com.openelements.data.runtime.h2.H2Dialect;
import com.openelements.data.runtime.sql.types.AbstractMatchingSqlTypeSupport;
import java.math.BigDecimal;
import java.util.Set;

public class BigDecimalSupport extends AbstractMatchingSqlTypeSupport<BigDecimal> {

    public BigDecimalSupport() {
        super(BigDecimal.class, "NUMERIC");
    }

    @Override
    public Set<String> getSupportedJdbcDrivers() {
        return Set.of(H2Dialect.DRIVER_CLASS_NAME);
    }

}
