package com.openelements.data.runtime.sql.statement;

import com.openelements.data.runtime.sql.tables.SqlDataTable;
import com.openelements.data.runtime.sql.tables.TableColumn;
import java.util.List;
import java.util.Objects;
import org.jspecify.annotations.NonNull;

public interface SqlStatementFactory {

    @NonNull
    SqlStatement createTableCreateStatement(@NonNull SqlDataTable table, boolean ifNotExists);

    @NonNull
    SqlStatement createUniqueIndexStatement(@NonNull SqlDataTable table, boolean ifNotExists);

    @NonNull
    SqlStatement createSelectStatement(@NonNull SqlDataTable table,
            @NonNull List<TableColumn<?, ?>> selectColumns,
            @NonNull List<TableColumn<?, ?>> whereColumns);

    @NonNull
    SqlStatement createSelectPageStatement(@NonNull SqlDataTable table, int pageNumber,
            int pageSize, @NonNull List<TableColumn<?, ?>> selectColumns,
            @NonNull List<TableColumn<?, ?>> whereColumns);

    @NonNull
    SqlStatement createSelectCountStatement(@NonNull SqlDataTable table,
            @NonNull List<TableColumn<?, ?>> whereColumns);

    @NonNull
    SqlStatement createUpdateStatement(@NonNull SqlDataTable table,
            @NonNull List<TableColumn<?, ?>> toUpdateColumns,
            @NonNull List<TableColumn<?, ?>> whereColumns);

    @NonNull
    SqlStatement createDeleteStatement(@NonNull SqlDataTable table,
            @NonNull List<TableColumn<?, ?>> whereColumns);

    @NonNull
    SqlStatement createInsertStatement(@NonNull SqlDataTable table);

    @NonNull
    default SqlStatement createFindStatement(@NonNull final SqlDataTable table) {
        Objects.requireNonNull(table, "table must not be null");
        return createSelectStatement(table, table.getColumns(), table.getKeyColumns());
    }

    @NonNull
    default SqlStatement createSelectStatement(@NonNull final SqlDataTable table) {
        Objects.requireNonNull(table, "table must not be null");
        return createSelectStatement(table, table.getColumns(), List.of());
    }

    @NonNull
    default SqlStatement createSelectPageStatement(@NonNull final SqlDataTable table, final int pageNumber,
            final int pageSize) {
        Objects.requireNonNull(table, "table must not be null");
        return createSelectPageStatement(table, pageNumber, pageSize, table.getColumns(), List.of());
    }

    @NonNull
    default SqlStatement createSelectCountStatement(@NonNull final SqlDataTable table) {
        return createSelectCountStatement(table, List.of());
    }

    @NonNull
    default SqlStatement createUpdateStatement(@NonNull final SqlDataTable table) {
        Objects.requireNonNull(table, "table must not be null");
        return createUpdateStatement(table, table.getColumnsWithoutKeys(), table.getKeyColumns());
    }

}
