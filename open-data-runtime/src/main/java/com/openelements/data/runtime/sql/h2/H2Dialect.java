package com.openelements.data.runtime.sql.h2;

import com.openelements.data.runtime.sql.SqlConnection;
import com.openelements.data.runtime.sql.connection.SqlConnectionImpl;
import com.openelements.data.runtime.sql.SqlDialect;
import com.openelements.data.runtime.sql.h2.impl.H2SqlStatementFactory;
import com.openelements.data.runtime.sql.statement.SqlStatementFactory;
import org.jspecify.annotations.NonNull;

public class H2Dialect implements SqlDialect {

    public static final String DRIVER_CLASS_NAME = "org.h2.Driver";

    public String getName() {
        return "H2";
    }

    public String getDriverClassName() {
        return DRIVER_CLASS_NAME;
    }

    public SqlStatementFactory getSqlStatementFactory(@NonNull final SqlConnection sqlConnection) {
        return new H2SqlStatementFactory(sqlConnection);
    }
}
