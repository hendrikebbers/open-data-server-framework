package com.openelements.data.runtime.sql.types.impl;

import com.openelements.data.runtime.h2.H2Dialect;
import com.openelements.data.runtime.sql.types.AbstractMatchingSqlTypeSupport;
import java.time.LocalDate;
import java.util.Set;

public class LocalDateSupport extends AbstractMatchingSqlTypeSupport<LocalDate> {

    public LocalDateSupport() {
        super(LocalDate.class, "DATE");
    }

    @Override
    public Set<String> getSupportedJdbcDrivers() {
        return Set.of(H2Dialect.DRIVER_CLASS_NAME);
    }
}
