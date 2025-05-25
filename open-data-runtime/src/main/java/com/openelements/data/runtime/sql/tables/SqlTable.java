package com.openelements.data.runtime.sql.tables;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public interface SqlTable {

    String getName();

    List<TableColumn<?, ?, ?>> getDataColumns();

    default List<TableColumn<?, ?, ?>> getColumns() {
        final List<TableColumn<?, ?, ?>> columns = new ArrayList<>();
        columns.addAll(getDataColumns());
        columns.addAll(getMetadataColumns());
        return Collections.unmodifiableList(columns);
    }

    /**
     * MUTS BE SUBSET OF getDataColumns
     *
     * @return
     */
    List<TableColumn<?, ?, ?>> getKeyColumns();

    // TODO: CREATION TIUME AND UUID
    default List<TableColumn<?, ?, ?>> getMetadataColumns() {
        return List.of();
    }
}
