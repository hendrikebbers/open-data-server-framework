package com.openelements.data.runtime.types;

import com.openelements.data.api.data.Attribute;
import com.openelements.data.api.data.Data;
import com.openelements.data.runtime.data.DataRepository;
import com.openelements.data.runtime.data.DataType;
import com.openelements.data.runtime.sql.SqlConnection;
import com.openelements.data.runtime.sql.repositories.TableRepository;
import com.openelements.data.runtime.sql.statement.SqlStatement;
import com.openelements.data.runtime.sql.tables.ResultRow;
import com.openelements.data.runtime.sql.tables.SqlDataTable;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Data(name = "OE_BINARY_DATA_ENTRY")
public record BinaryDataEntry(@Attribute(partOfIdentifier = true, required = true) UUID id,
                              String name,
                              ByteArray content) {

    public static DataType<BinaryDataEntry> getDataType() {
        return DataType.of(BinaryDataEntry.class);
    }

    public static SqlDataTable getSqlDataTable(SqlConnection sqlConnection) {
        return TableRepository.createTable(getDataType(), sqlConnection);
    }

    public static DataRepository<BinaryDataEntry> getDataRepository(SqlConnection sqlConnection) {
        return DataRepository.of(getDataType(), sqlConnection);
    }

    public static Optional<BinaryDataEntry> findForReference(UUID reference, SqlConnection sqlConnection) {
        final DataType<BinaryDataEntry> dataType = getDataType();
        final SqlDataTable table = getSqlDataTable(sqlConnection);
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

    public static void deleteForReference(UUID id, SqlConnection connection) throws SQLException {
        final SqlDataTable table = getSqlDataTable(connection);
        final SqlStatement deleteStatement = connection.getSqlStatementFactory()
                .createDeleteStatement(table, table.getKeyColumns());
        deleteStatement.set("id", id);
        deleteStatement.executeUpdate();
    }
}
