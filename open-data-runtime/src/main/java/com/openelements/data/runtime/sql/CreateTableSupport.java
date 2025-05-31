package com.openelements.data.runtime.sql;

import com.openelements.data.runtime.data.DataType;
import com.openelements.data.runtime.sql.repositories.TableRepository;
import com.openelements.data.runtime.sql.tables.SqlDataTable;

public class CreateTableSupport {

    public static <E extends Record> String createCreateTableStatement(Class<E> recordClass, SqlConnection connection) {
        DataType<E> dataType = DataType.of(recordClass);
        SqlDataTable sqlDataTable = TableRepository.createTable(dataType, connection);
        return connection.getSqlStatementFactory().createTableCreateStatement(sqlDataTable).getStatement();
    }

    public static <E extends Record> String createUniqueIndexStatement(Class<E> recordClass, SqlConnection connection) {
        DataType<E> dataType = DataType.of(recordClass);
        SqlDataTable sqlDataTable = TableRepository.createTable(dataType, connection);
        return connection.getSqlStatementFactory().createUniqueIndexStatement(sqlDataTable).getStatement();
    }
}
