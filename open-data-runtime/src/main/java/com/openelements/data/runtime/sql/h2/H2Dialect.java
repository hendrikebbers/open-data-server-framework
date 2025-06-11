package com.openelements.data.runtime.sql.h2;

import com.openelements.data.runtime.sql.api.SqlConnection;
import com.openelements.data.runtime.sql.api.SqlDialect;
import com.openelements.data.runtime.sql.implementation.DefaultSqlStatementFactory;
import com.openelements.data.runtime.sql.statement.SqlStatementFactory;
import org.jspecify.annotations.NonNull;

public class H2Dialect implements SqlDialect {

    public static final H2Dialect INSTANCE = new H2Dialect();

    public static final String DRIVER_CLASS_NAME = "org.h2.Driver";

    public String getName() {
        return "H2";
    }

    public String getDriverClassName() {
        return DRIVER_CLASS_NAME;
    }

    public SqlStatementFactory getSqlStatementFactory(@NonNull final SqlConnection sqlConnection) {
        return new DefaultSqlStatementFactory(sqlConnection);
    }

    public static H2Dialect getInstance() {
        return INSTANCE;
    }
}
