package com.openelements.data.runtime.sql.statement;

import com.openelements.data.runtime.sql.tables.SqlDataTable;
import com.openelements.data.runtime.sql.tables.TableColumn;
import java.util.List;

public interface SqlStatementFactory {

    SqlStatement createTableCreateStatement(SqlDataTable table);

    SqlStatement createUniqueIndexStatement(SqlDataTable table);

    SqlStatement createSelectStatement(SqlDataTable table,
            List<TableColumn<?, ?>> selectColumns,
            List<TableColumn<?, ?>> whereColumns);

    SqlStatement createSelectPageStatement(SqlDataTable table, int pageNumber,
            int pageSize, List<TableColumn<?, ?>> selectColumns,
            List<TableColumn<?, ?>> whereColumns);

    SqlStatement createSelectCountStatement(SqlDataTable table,
            List<TableColumn<?, ?>> whereColumns);

    SqlStatement createUpdateStatement(SqlDataTable table,
            List<TableColumn<?, ?>> toUpdateColumns,
            List<TableColumn<?, ?>> whereColumns);

    SqlStatement createDeleteStatement(SqlDataTable table,
            List<TableColumn<?, ?>> whereColumns);

    SqlStatement createInsertStatement(SqlDataTable table);

    default SqlStatement createFindStatement(SqlDataTable table) {
        return createSelectStatement(table, table.getColumns(), table.getKeyColumns());
    }

    default SqlStatement createSelectStatement(SqlDataTable table) {
        return createSelectStatement(table, table.getColumns(), List.of());
    }

    default SqlStatement createSelectPageStatement(SqlDataTable table, int pageNumber,
            int pageSize) {
        return createSelectPageStatement(table, pageNumber, pageSize, table.getColumns(), List.of());
    }

    default SqlStatement createSelectCountStatement(SqlDataTable table) {
        return createSelectCountStatement(table, List.of());
    }

    default SqlStatement createUpdateStatement(SqlDataTable table) {
        return createUpdateStatement(table, table.getColumnsWithoutKeys(), table.getKeyColumns());
    }

}
