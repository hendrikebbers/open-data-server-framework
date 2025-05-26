package com.openelements.data.runtime.sql.statement;

import com.openelements.data.runtime.sql.tables.SqlDataTable;
import com.openelements.data.runtime.sql.tables.TableColumn;
import java.util.List;

public interface SqlStatementFactory {

    <E extends Record> SqlStatement createTableCreateStatement(SqlDataTable<E> table);

    <E extends Record> SqlStatement createUniqueIndexStatement(SqlDataTable<E> table);

    <E extends Record> SqlStatement createSelectStatement(SqlDataTable<E> table,
            List<TableColumn<E, ?, ?>> selectColumns,
            List<TableColumn<E, ?, ?>> whereColumns);

    <E extends Record> SqlStatement createSelectPageStatement(SqlDataTable<E> table, int pageNumber,
            int pageSize, List<TableColumn<E, ?, ?>> selectColumns,
            List<TableColumn<E, ?, ?>> whereColumns);

    <E extends Record> SqlStatement createSelectCountStatement(SqlDataTable<E> table,
            List<TableColumn<E, ?, ?>> whereColumns);

    <E extends Record> SqlStatement createUpdateStatement(SqlDataTable<E> table,
            List<TableColumn<E, ?, ?>> toUpdateColumns,
            List<TableColumn<E, ?, ?>> whereColumns);

    <E extends Record> SqlStatement createDeleteStatement(SqlDataTable<E> table,
            List<TableColumn<E, ?, ?>> whereColumns);

    <E extends Record> SqlStatement createInsertStatement(SqlDataTable<E> table);

    default <E extends Record> SqlStatement createFindStatement(SqlDataTable<E> table) {
        return createSelectStatement(table, table.getColumns(), table.getKeyColumns());
    }

    default <E extends Record> SqlStatement createSelectStatement(SqlDataTable<E> table) {
        return createSelectStatement(table, table.getColumns(), List.of());
    }

    default <E extends Record> SqlStatement createSelectPageStatement(SqlDataTable<E> table, int pageNumber,
            int pageSize) {
        return createSelectPageStatement(table, pageNumber, pageSize, table.getColumns(), List.of());
    }

    default <E extends Record> SqlStatement createSelectCountStatement(SqlDataTable<E> table) {
        return createSelectCountStatement(table, List.of());
    }

    default <E extends Record> SqlStatement createUpdateStatement(SqlDataTable<E> table) {
        return createUpdateStatement(table, table.getColumnsWithoutKeys(), table.getKeyColumns());
    }

}
