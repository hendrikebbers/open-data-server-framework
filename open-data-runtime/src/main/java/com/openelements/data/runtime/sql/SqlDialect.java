package com.openelements.data.runtime.sql;

import com.openelements.data.runtime.sql.statement.SqlStatementFactory;
import com.openelements.data.runtime.sql.types.SqlTypeSupport;
import java.lang.reflect.Type;
import java.util.Optional;

public interface SqlDialect {

    String getName();

    String getDriverClassName();

    default <T, U> Optional<SqlTypeSupport<T, U>> getSqlTypeSupportForJavaType(Type type) {
        return SqlTypeSupport.getInstances().stream()
                .filter(support -> support.supportsJavaType(type))
                .filter(support -> support.getSupportedJdbcDrivers().contains(getDriverClassName()))
                .map(support -> (SqlTypeSupport<T, U>) support)
                .findFirst();
    }

    SqlStatementFactory getSqlStatementFactory(SqlConnection sqlConnection);
}
