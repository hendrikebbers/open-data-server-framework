package com.openelements.data.runtime.sql.tables;

import com.openelements.data.runtime.sql.types.VarCharType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public interface SqlTable {

    TableColumn<String> CREATED_AT_COLUMN = new TableColumn<>("created_at", VarCharType.INSTANCE);

    TableColumn<String> UUID_COLUMN = new TableColumn<>("uuid", VarCharType.INSTANCE);

    String getName();

    List<TableColumn<?>> getDataColumns();

    default List<TableColumn<?>> getColumns() {
        final List<TableColumn<?>> columns = new ArrayList<>();
        columns.addAll(getDataColumns());
        columns.addAll(getMetadataColumns());
        return Collections.unmodifiableList(columns);
    }

    /**
     * MUTS BE SUBSET OF getDataColumns
     *
     * @return
     */
    List<TableColumn<?>> getKeyColumns();

    default List<TableColumn<?>> getMetadataColumns() {
        return List.of(CREATED_AT_COLUMN, UUID_COLUMN);
    }
}
