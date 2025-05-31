package com.openelements.data.runtime.types;

import com.openelements.data.api.data.Attribute;
import com.openelements.data.api.data.Data;
import com.openelements.data.api.data.Language;
import com.openelements.data.runtime.data.ApiData;
import com.openelements.data.runtime.data.DataRepository;
import com.openelements.data.runtime.data.DataType;
import com.openelements.data.runtime.sql.SqlConnection;
import com.openelements.data.runtime.sql.repositories.TableRepository;
import com.openelements.data.runtime.sql.statement.SqlStatement;
import com.openelements.data.runtime.sql.tables.ResultRow;
import com.openelements.data.runtime.sql.tables.SqlDataTable;
import com.openelements.data.runtime.sql.tables.TableColumn;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

@ApiData
@Data
public record I18nStringEntry(@Attribute(partOfIdentifier = true, required = true) UUID reference,
                              @Attribute(partOfIdentifier = true, required = true) Language language,
                              String content) {

    public static DataType<I18nStringEntry> getDataType() {
        return DataType.of(I18nStringEntry.class);
    }

    public static SqlDataTable getSqlDataTable(SqlConnection connection) {
        return TableRepository.createTable(getDataType(), connection);
    }

    public static DataRepository<I18nStringEntry> getDataRepository(SqlConnection sqlConnection) {
        return DataRepository.of(getDataType(), sqlConnection);
    }

    public static void deleteForReference(UUID reference, SqlConnection sqlConnection) throws SQLException {
        final SqlDataTable table = I18nStringEntry.getSqlDataTable(sqlConnection);
        final TableColumn<?, ?> referenceColumn = table.getColumnByName("reference").orElseThrow();
        final SqlStatement deleteStatement = sqlConnection.getSqlStatementFactory()
                .createDeleteStatement(table, List.of(referenceColumn));
        deleteStatement.set("reference", reference);
        deleteStatement.executeUpdate();
    }

    public static List<I18nStringEntry> findForReference(UUID reference, SqlConnection sqlConnection) {
        final DataType<I18nStringEntry> dataType = getDataType();
        final SqlDataTable table = getSqlDataTable(sqlConnection);
        final TableColumn<?, ?> referenceColumn = table.getColumnByName("reference")
                .orElseThrow();
        final SqlStatement selectStatement = sqlConnection.getSqlStatementFactory()
                .createSelectStatement(table, table.getDataColumns(), List.of(referenceColumn));
        selectStatement.set("reference", reference);
        try {
            final List<ResultRow> resultRows = selectStatement.executeQuery();
            return TableRepository.convertList(dataType, resultRows);
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving last translations for " + reference, e);
        }
    }
}
