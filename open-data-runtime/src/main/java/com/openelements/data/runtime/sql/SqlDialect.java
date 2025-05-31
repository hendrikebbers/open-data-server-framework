package com.openelements.data.runtime.sql;

import com.openelements.data.runtime.sql.h2.H2Dialect;
import com.openelements.data.runtime.sql.statement.SqlStatementFactory;
import com.openelements.data.runtime.sql.types.SqlTypeSupport;
import java.lang.reflect.Type;
import java.util.Objects;
import java.util.Optional;
import org.jspecify.annotations.NonNull;

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

    static SqlDialect forName(@NonNull final String dialectName) {
        Objects.requireNonNull(dialectName, "dialectName must not be null");
        if (Objects.equals(dialectName, "H2")) {
            return new H2Dialect();
        }
        throw new IllegalArgumentException("Unsupported SQL dialect: " + dialectName);
    }
}
