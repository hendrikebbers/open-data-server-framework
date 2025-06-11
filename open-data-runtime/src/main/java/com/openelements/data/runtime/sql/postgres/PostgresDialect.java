package com.openelements.data.runtime.sql.postgres;

import com.openelements.data.runtime.sql.api.SqlConnection;
import com.openelements.data.runtime.sql.api.SqlDialect;
import com.openelements.data.runtime.sql.implementation.DefaultSqlStatementFactory;
import com.openelements.data.runtime.sql.statement.SqlStatementFactory;

public class PostgresDialect implements SqlDialect {

    public static final String DRIVER_CLASS_NAME = "org.postgresql.Driver";

    @Override
    public String getName() {
        return "POSTGRES";
    }

    @Override
    public String getDriverClassName() {
        return DRIVER_CLASS_NAME;
    }

    @Override
    public SqlStatementFactory getSqlStatementFactory(SqlConnection sqlConnection) {
        return new DefaultSqlStatementFactory(sqlConnection);
    }
}
