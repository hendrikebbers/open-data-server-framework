package com.openelements.data.runtime.sql.types.impl;

import com.openelements.data.runtime.h2.H2Dialect;
import com.openelements.data.runtime.sql.types.AbstractMatchingSqlTypeSupport;
import java.time.LocalTime;
import java.util.Set;

public class LocalTimeSupport extends AbstractMatchingSqlTypeSupport<LocalTime> {

    public LocalTimeSupport() {
        super(LocalTime.class, "TIME");
    }

    @Override
    public Set<String> getSupportedJdbcDrivers() {
        return Set.of(H2Dialect.DRIVER_CLASS_NAME);
    }
}
