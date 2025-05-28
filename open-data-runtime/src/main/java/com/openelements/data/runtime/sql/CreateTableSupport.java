package com.openelements.data.runtime.sql;

import com.openelements.data.runtime.data.DataType;
import com.openelements.data.runtime.sql.tables.SqlDataTable;

public class CreateTableSupport {

    public static <E extends Record> String createCreateTableStatement(Class<E> recordClass, SqlDialect sqlDialect) {
        DataType<E> dataType = DataType.of(recordClass);
        SqlDataTable<E> sqlDataTable = new SqlDataTable<>(sqlDialect, dataType);
        return sqlDialect.getSqlStatementFactory().createTableCreateStatement(sqlDataTable).getStatement();
    }

    public static <E extends Record> String createUniqueIndexStatement(Class<E> recordClass, SqlDialect sqlDialect) {
        DataType<E> dataType = DataType.of(recordClass);
        SqlDataTable<E> sqlDataTable = new SqlDataTable<>(sqlDialect, dataType);
        return sqlDialect.getSqlStatementFactory().createUniqueIndexStatement(sqlDataTable).getStatement();
    }
}
