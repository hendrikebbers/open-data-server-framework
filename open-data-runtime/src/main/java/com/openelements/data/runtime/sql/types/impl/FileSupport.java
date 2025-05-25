package com.openelements.data.runtime.sql.types.impl;

import com.openelements.data.api.types.File;
import com.openelements.data.runtime.h2.H2Dialect;
import com.openelements.data.runtime.sql.SqlConnection;
import com.openelements.data.runtime.sql.types.AbstractSqlTypeSupport;
import java.util.Set;

public class FileSupport extends AbstractSqlTypeSupport<File, Long> {

    public FileSupport() {
        super(File.class, "LONG");
    }

    @Override
    public Set<String> getSupportedJdbcDrivers() {
        return Set.of(H2Dialect.DRIVER_CLASS_NAME);
    }

    @Override
    public File convertValueFromSqlResult(Long sqlValue, SqlConnection connection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Long convertValueForSqlPersit(File newValue, Long currentValue,
            SqlConnection connection) {
        throw new UnsupportedOperationException();
    }
}
