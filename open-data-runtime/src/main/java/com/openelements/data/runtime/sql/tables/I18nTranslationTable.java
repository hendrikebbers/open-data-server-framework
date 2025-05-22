package com.openelements.data.runtime.sql.tables;

import com.openelements.data.runtime.sql.types.ReferenceType;

public class I18nTranslationTable {

    private final TableColumn<Long> idColumnn = new TableColumn<>("id", ReferenceType.INSTANCE);

    private final TableColumn<Long> i18nReferenceColumn = new TableColumn<>("i18n_id", ReferenceType.INSTANCE);

    private final TableColumn<String> languageKeyColumn = null;

    private final TableColumn<String> keyColumn = null;

    private final TableColumn<String> translationColumn = null;


}
