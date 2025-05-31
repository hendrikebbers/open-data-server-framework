package com.openelements.data.runtime.types;

import com.openelements.data.runtime.api.Attribute;
import com.openelements.data.runtime.api.Data;
import com.openelements.data.runtime.api.Language;
import com.openelements.data.runtime.data.DataType;
import com.openelements.data.runtime.data.impl.ApiData;
import com.openelements.data.runtime.integration.DataRepository;
import com.openelements.data.runtime.sql.api.SqlConnection;
import com.openelements.data.runtime.sql.implementation.TableRepository;
import com.openelements.data.runtime.sql.statement.SqlStatement;
import com.openelements.data.runtime.sql.tables.ResultRow;
import com.openelements.data.runtime.sql.tables.SqlDataTable;
import com.openelements.data.runtime.sql.tables.TableColumn;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.jspecify.annotations.NonNull;

@ApiData
@Data
public record I18nStringEntry(@Attribute(partOfIdentifier = true, required = true) UUID reference,
                              @Attribute(partOfIdentifier = true, required = true) Language language,
                              String content) {

    @NonNull
    public static DataRepository<I18nStringEntry> getDataRepository(@NonNull final SqlConnection sqlConnection) {
        return DataRepository.of(DataType.of(I18nStringEntry.class), sqlConnection);
    }

    public static void deleteForReference(@NonNull final UUID reference, @NonNull final SqlConnection sqlConnection)
            throws SQLException {
        Objects.requireNonNull(reference, "reference must not be null");
        Objects.requireNonNull(sqlConnection, "sqlConnection must not be null");
        final SqlDataTable table = TableRepository.createTable(DataType.of(I18nStringEntry.class), sqlConnection);
        final TableColumn<?, ?> referenceColumn = table.getColumnByName("reference").orElseThrow();
        final SqlStatement deleteStatement = sqlConnection.getSqlStatementFactory()
                .createDeleteStatement(table, List.of(referenceColumn));
        deleteStatement.set("reference", reference);
        deleteStatement.executeUpdate();
    }

    @NonNull
    public static List<I18nStringEntry> findForReference(@NonNull final UUID reference,
            @NonNull final SqlConnection sqlConnection) {
        Objects.requireNonNull(reference, "reference must not be null");
        Objects.requireNonNull(sqlConnection, "sqlConnection must not be null");
        final DataType<I18nStringEntry> dataType = DataType.of(I18nStringEntry.class);
        final SqlDataTable table = TableRepository.createTable(DataType.of(I18nStringEntry.class), sqlConnection);
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
