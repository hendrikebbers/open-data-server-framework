package com.openelements.data.runtime.sql.types.impl;

import com.openelements.data.runtime.h2.H2Dialect;
import com.openelements.data.runtime.sql.types.AbstractMatchingSqlTypeSupport;
import java.time.LocalDateTime;
import java.util.Set;

public class LocalDateTimeSupport extends AbstractMatchingSqlTypeSupport<LocalDateTime> {

    public LocalDateTimeSupport() {
        super(LocalDateTime.class, "TIMESTAMP");
    }

    @Override
    public Set<String> getSupportedJdbcDrivers() {
        return Set.of(H2Dialect.DRIVER_CLASS_NAME);
    }

}
