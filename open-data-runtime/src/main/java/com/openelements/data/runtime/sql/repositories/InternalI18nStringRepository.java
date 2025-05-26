package com.openelements.data.runtime.sql.repositories;

import com.openelements.data.api.data.Language;
import com.openelements.data.api.types.I18nString;
import com.openelements.data.runtime.data.DataAttribute;
import com.openelements.data.runtime.data.DataType;
import com.openelements.data.runtime.sql.SqlConnection;
import com.openelements.data.runtime.sql.statement.SqlStatement;
import com.openelements.data.runtime.sql.tables.SqlDataTable;
import com.openelements.data.runtime.sql.tables.TableColumn;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class InternalI18nStringRepository {

    private final SqlDataTable<I18nString> referenceTable;

    private final SqlDataTable<I18nString> translationTable;

    private final SqlConnection connection;

    public InternalI18nStringRepository(SqlConnection connection) {
        this.connection = connection;
        List<DataAttribute> refAttributes = List.of(new DataAttribute("id", -1, true, true, UUID.class));
        DataType refDataType = new DataType<>("I18N", false, I18nString.class, refAttributes);
        this.referenceTable = new SqlDataTable<>(connection.getSqlDialect(), refDataType);

        List<DataAttribute> translationAttributes = List.of(new DataAttribute("i18n_id", -1, true, true, UUID.class),
                new DataAttribute("language_code", -1, true, true, String.class),
                new DataAttribute("translation", -1, true, false, String.class));
        DataType translationDataType = new DataType<>("I18N_TRANSLATIONS", false, I18nString.class,
                translationAttributes);
        translationTable = new SqlDataTable<>(connection.getSqlDialect(), translationDataType);
    }

    public void createTables(SqlConnection connection) throws SQLException {
        connection.runInTransaction(() -> {
            connection.getSqlDialect().getSqlStatementFactory()
                    .createTableCreateStatement(referenceTable).toPreparedStatement(connection).execute();
            connection.getSqlDialect().getSqlStatementFactory()
                    .createUniqueIndexStatement(referenceTable).toPreparedStatement(connection).execute();

            connection.getSqlDialect().getSqlStatementFactory()
                    .createTableCreateStatement(translationTable).toPreparedStatement(connection).execute();
            connection.getSqlDialect().getSqlStatementFactory()
                    .createUniqueIndexStatement(translationTable).toPreparedStatement(connection).execute();
        });
    }

    public I18nString load(UUID id) throws SQLException {
        Map<Language, String> translations = new HashMap<>();
        TableColumn<I18nString, ?, ?> idColumn = translationTable.getColumnByName("i18n_id").orElseThrow();
        TableColumn<I18nString, ?, ?> langColumn = translationTable.getColumnByName("language_code").orElseThrow();
        TableColumn<I18nString, ?, ?> translationColumn = translationTable.getColumnByName("translation").orElseThrow();
        final SqlStatement selectStatement = connection.getSqlDialect().getSqlStatementFactory()
                .createSelectStatement(translationTable, List.of(langColumn, translationColumn),
                        List.of(idColumn));
        selectStatement.set("i18n_id", id);
        ResultSet resultSet = selectStatement.toPreparedStatement(connection).executeQuery();
        while (resultSet.next()) {
            String languageCode = resultSet.getString("language_code");
            String translation = resultSet.getString("translation");
            Language language = Language.valueOf(languageCode.toUpperCase());
            translations.put(language, translation);
        }
        return new I18nString(translations);
    }

    public UUID insert(I18nString value) throws SQLException {
        if (value == null) {
            return null;
        }
        final SqlStatement insertRefStatement = connection.getSqlDialect().getSqlStatementFactory()
                .createInsertStatement(referenceTable);
        UUID i18nId = UUID.randomUUID();
        insertRefStatement.set("id", i18nId);
        insertRefStatement.toPreparedStatement(connection).execute();

        for (Map.Entry<Language, String> entry : value.translations().entrySet()) {
            Language language = entry.getKey();
            String translation = entry.getValue();
            final SqlStatement insertTranslationStatement = connection.getSqlDialect().getSqlStatementFactory()
                    .createInsertStatement(translationTable);
            insertTranslationStatement.set("i18n_id", i18nId);
            insertTranslationStatement.set("language_code", language.name());
            insertTranslationStatement.set("translation", translation);
            insertTranslationStatement.toPreparedStatement(connection).execute();
        }
        return i18nId;
    }

    public UUID update(UUID currentValue, I18nString value) throws SQLException {
        if (currentValue != null) {
            PreparedStatement deleteStatement = connection.prepareStatement(
                    "DELETE FROM I18N_TRANSLATIONS WHERE i18n_id = ?");
            deleteStatement.setObject(1, currentValue);
            deleteStatement.executeUpdate();
            PreparedStatement i18nDeleteStatement = connection.prepareStatement(
                    "DELETE FROM I18N WHERE id = ?");
            i18nDeleteStatement.setObject(1, currentValue);
            i18nDeleteStatement.executeUpdate();
        }
        return insert(value);
    }
}
