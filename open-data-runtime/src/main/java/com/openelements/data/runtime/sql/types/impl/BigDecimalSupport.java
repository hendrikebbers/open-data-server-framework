package com.openelements.data.runtime.sql.types.impl;

import com.openelements.data.runtime.sql.h2.H2Dialect;
import com.openelements.data.runtime.sql.postgres.PostgresDialect;
import com.openelements.data.runtime.sql.types.AbstractMatchingSqlTypeSupport;
import java.math.BigDecimal;
import java.sql.Types;
import java.util.Set;
import org.jspecify.annotations.NonNull;

public class BigDecimalSupport extends AbstractMatchingSqlTypeSupport<BigDecimal> {

    public BigDecimalSupport() {
        super(BigDecimal.class, "NUMERIC");
    }

    @Override
    public int getJdbcTypeCode() {
        return Types.NUMERIC;
    }

    @NonNull
    @Override
    public Set<String> getSupportedJdbcDrivers() {
        return Set.of(H2Dialect.DRIVER_CLASS_NAME, PostgresDialect.DRIVER_CLASS_NAME);
    }

}
