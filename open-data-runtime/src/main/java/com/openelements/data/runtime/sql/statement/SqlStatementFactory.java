package com.openelements.data.runtime.sql.statement;

import com.openelements.data.runtime.sql.tables.SqlDataTable;

public interface SqlStatementFactory {

    <E extends Record> String createSelectStatement(SqlDataTable<E> table);

    <E extends Record> String createSelectPageStatement(SqlDataTable<E> table, int pageNumber,
            int pageSize);

    <E extends Record> String createQueryCountStatement(SqlDataTable<E> table);

    <E extends Record> String createFindStatement(SqlDataTable<E> table);

    <E extends Record> String createTableCreateStatement(SqlDataTable<E> table);

    <E extends Record> String createUniqueIndexStatement(SqlDataTable<E> table);

    <E extends Record> String createUpdateStatement(SqlDataTable<E> table);

    <E extends Record> String createInsertStatement(SqlDataTable<E> table);
}
