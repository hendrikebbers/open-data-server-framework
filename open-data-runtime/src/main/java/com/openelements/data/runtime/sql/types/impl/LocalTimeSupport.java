package com.openelements.data.runtime.sql.types.impl;

import com.openelements.data.runtime.sql.h2.H2Dialect;
import com.openelements.data.runtime.sql.postgres.PostgresDialect;
import com.openelements.data.runtime.sql.types.AbstractMatchingSqlTypeSupport;
import java.sql.Types;
import java.time.LocalTime;
import java.util.Set;

public class LocalTimeSupport extends AbstractMatchingSqlTypeSupport<LocalTime> {

    public LocalTimeSupport() {
        super(LocalTime.class, "TIME WITHOUT TIME ZONE");
    }

    @Override
    public int getJdbcTypeCode() {
        return Types.TIME;
    }

    @Override
    public Set<String> getSupportedJdbcDrivers() {
        return Set.of(H2Dialect.DRIVER_CLASS_NAME, PostgresDialect.DRIVER_CLASS_NAME);
    }
}
