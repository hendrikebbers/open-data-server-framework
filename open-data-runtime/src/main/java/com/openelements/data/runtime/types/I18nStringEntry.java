package com.openelements.data.runtime.types;

import com.openelements.data.api.data.Attribute;
import com.openelements.data.api.data.Data;
import com.openelements.data.api.data.Language;
import com.openelements.data.runtime.data.DataType;
import com.openelements.data.runtime.sql.SqlConnection;
import com.openelements.data.runtime.sql.SqlDialect;
import com.openelements.data.runtime.sql.repositories.DataRepository;
import com.openelements.data.runtime.sql.statement.SqlStatement;
import com.openelements.data.runtime.sql.tables.SqlDataTable;
import com.openelements.data.runtime.sql.tables.TableColumn;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Data(name = "OE_I18N_STRING_ENTRY")
public record I18nStringEntry(@Attribute(partOfIdentifier = true, required = true) UUID reference,
                              @Attribute(partOfIdentifier = true, required = true) Language language,
                              String content) {

    public static DataType<I18nStringEntry> getDataType() {
        return DataType.of(I18nStringEntry.class);
    }

    public static SqlDataTable<I18nStringEntry> getSqlDataTable(SqlDialect sqlDialect) {
        return SqlDataTable.of(getDataType(), sqlDialect);
    }

    public static DataRepository<I18nStringEntry> getDataRepository(SqlConnection sqlConnection) {
        return DataRepository.of(getDataType(), sqlConnection);
    }

    public static void deleteForReference(UUID reference, SqlConnection sqlConnection) throws SQLException {
        final SqlDataTable<I18nStringEntry> table = I18nStringEntry.getSqlDataTable(sqlConnection.getSqlDialect());
        final TableColumn<I18nStringEntry, ?, ?> referenceColumn = table.getColumnByName("reference").orElseThrow();
        final SqlStatement deleteStatement = sqlConnection.getSqlStatementFactory()
                .createDeleteStatement(table, List.of(referenceColumn));
        deleteStatement.set("reference", reference);
        deleteStatement.toPreparedStatement(sqlConnection).executeUpdate();
    }

    public static List<I18nStringEntry> findForReference(UUID reference, SqlConnection sqlConnection) {
        final SqlDataTable<I18nStringEntry> table = getSqlDataTable(sqlConnection.getSqlDialect());
        final TableColumn<I18nStringEntry, ?, ?> referenceColumn = table.getColumnByName("reference")
                .orElseThrow();
        final SqlStatement selectStatement = sqlConnection.getSqlStatementFactory()
                .createSelectStatement(table, table.getDataColumns(), List.of(referenceColumn));
        selectStatement.set("reference", reference);
        try {
            final PreparedStatement preparedStatement = selectStatement.toPreparedStatement(sqlConnection);
            final ResultSet resultSet = preparedStatement.executeQuery();
            final List<I18nStringEntry> results = new ArrayList<>();
            while (resultSet.next()) {
                final UUID timestamp = resultSet.getObject("reference", UUID.class);
                final Language language = resultSet.getObject("language", Language.class);
                final String content = resultSet.getObject("content", String.class);
                final I18nStringEntry entry = new I18nStringEntry(timestamp, language, content);
                results.add(entry);
            }
            return Collections.unmodifiableList(results);
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving last translations for " + reference, e);
        }
    }

}
