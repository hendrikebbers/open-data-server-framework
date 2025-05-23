package com.openelements.data.runtime.sql.tables;

import com.openelements.data.runtime.sql.types.ReferenceType;
import java.util.List;

public class I18nTranslationTable implements SqlTable {

    private final TableColumn<Long> i18nReferenceColumn = new TableColumn<>("i18n_id", ReferenceType.INSTANCE);

    private final TableColumn<String> languageKeyColumn = null;

    private final TableColumn<String> translationColumn = null;

    @Override
    public String getName() {
        return "I18nTranslationTable";
    }

    @Override
    public List<TableColumn<?>> getDataColumns() {
        return List.of(i18nReferenceColumn, languageKeyColumn, translationColumn);
    }

    @Override
    public List<TableColumn<?>> getKeyColumns() {
        return List.of(i18nReferenceColumn, languageKeyColumn);
    }
}
