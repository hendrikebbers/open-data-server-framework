package com.openelements.data.runtime.sql.types.impl;

import com.openelements.data.runtime.sql.h2.H2Dialect;
import com.openelements.data.runtime.sql.postgres.PostgresDialect;
import com.openelements.data.runtime.sql.types.AbstractMatchingSqlTypeSupport;
import java.sql.Types;
import java.util.Set;
import java.util.UUID;

public class UUIDSupport extends AbstractMatchingSqlTypeSupport<UUID> {

    public UUIDSupport() {
        super(UUID.class, "UUID");
    }

    @Override
    public int getJdbcTypeCode() {
        return Types.OTHER;
    }

    @Override
    public Set<String> getSupportedJdbcDrivers() {
        return Set.of(H2Dialect.DRIVER_CLASS_NAME, PostgresDialect.DRIVER_CLASS_NAME);
    }
}
