package com.openelements.data.runtime.types;

import com.openelements.data.runtime.api.Attribute;
import com.openelements.data.runtime.api.Data;
import com.openelements.data.runtime.api.types.Binary;
import com.openelements.data.runtime.data.ApiData;
import com.openelements.data.runtime.data.DataType;
import com.openelements.data.runtime.integration.DataRepository;
import com.openelements.data.runtime.sql.api.SqlConnection;
import com.openelements.data.runtime.sql.implementation.TableRepository;
import com.openelements.data.runtime.sql.statement.SqlStatement;
import com.openelements.data.runtime.sql.tables.ResultRow;
import com.openelements.data.runtime.sql.tables.SqlDataTable;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.jspecify.annotations.NonNull;

@ApiData
@Data
public record BinaryDataEntry(@Attribute(partOfIdentifier = true, required = true) UUID id,
                              String name,
                              ByteArray data) implements Binary {

    @Override
    public @NonNull byte[] content() {
        return data.value();
    }

    public static DataRepository<BinaryDataEntry> getDataRepository(@NonNull final SqlConnection sqlConnection) {
        return DataRepository.of(DataType.of(BinaryDataEntry.class), sqlConnection);
    }

    public static Optional<BinaryDataEntry> findForReference(@NonNull final UUID reference,
            @NonNull final SqlConnection sqlConnection) {
        Objects.requireNonNull(reference, "reference must not be null");
        Objects.requireNonNull(sqlConnection, "sqlConnection must not be null");
        final DataType<BinaryDataEntry> dataType = DataType.of(BinaryDataEntry.class);
        final SqlDataTable table = TableRepository.createTable(dataType, sqlConnection);
        final SqlStatement selectStatement = sqlConnection.getSqlStatementFactory()
                .createSelectStatement(table, table.getDataColumns(), table.getKeyColumns());
        selectStatement.set("id", reference);
        try {
            final List<ResultRow> resultRows = selectStatement.executeQuery();
            List<BinaryDataEntry> result = TableRepository.convertList(dataType, resultRows);
            if (result.isEmpty()) {
                return Optional.empty();
            }
            if (result.size() > 1) {
                throw new IllegalStateException("Multiple entries found for reference: " + reference);
            }
            return Optional.of(result.get(0));
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving last translations for " + reference, e);
        }
    }

    public static void deleteForReference(@NonNull final UUID id, @NonNull final SqlConnection connection)
            throws SQLException {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(connection, "connection must not be null");
        final DataType<BinaryDataEntry> dataType = DataType.of(BinaryDataEntry.class);
        final SqlDataTable table = TableRepository.createTable(dataType, connection);
        final SqlStatement deleteStatement = connection.getSqlStatementFactory()
                .createDeleteStatement(table, table.getKeyColumns());
        deleteStatement.set("id", id);
        deleteStatement.executeUpdate();
    }
}
