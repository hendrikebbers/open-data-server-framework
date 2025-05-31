package com.openelements.data.runtime.sql.types.impl;

import com.openelements.data.runtime.sql.h2.H2Dialect;
import com.openelements.data.runtime.sql.types.AbstractMatchingSqlTypeSupport;
import java.util.Set;
import java.util.UUID;

public class UUIDSupport extends AbstractMatchingSqlTypeSupport<UUID> {

    public UUIDSupport() {
        super(UUID.class, "UUID");
    }

    @Override
    public Set<String> getSupportedJdbcDrivers() {
        return Set.of(H2Dialect.DRIVER_CLASS_NAME);
    }
}
