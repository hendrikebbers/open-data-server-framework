package com.openelements.data.runtime.sql.tables;

import com.openelements.data.runtime.sql.types.ReferenceType;
import java.util.List;

public class I18nTable implements SqlTable {

    private final TableColumn<Long> idColumnn = new TableColumn<>("id", ReferenceType.INSTANCE);

    private final TableColumn<String> keyColumn = null;

    @Override
    public String getName() {
        return "I18nTable";
    }

    @Override
    public List<TableColumn<?>> getDataColumns() {
        return List.of(idColumnn, keyColumn);
    }

    @Override
    public List<TableColumn<?>> getKeyColumns() {
        return List.of(idColumnn);
    }
}
