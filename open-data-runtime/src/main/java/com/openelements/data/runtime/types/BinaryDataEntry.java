package com.openelements.data.runtime.types;

import com.openelements.data.api.data.Attribute;
import com.openelements.data.api.data.Data;
import com.openelements.data.runtime.data.DataType;
import com.openelements.data.runtime.sql.SqlConnection;
import com.openelements.data.runtime.sql.SqlDialect;
import com.openelements.data.runtime.sql.repositories.DataRepository;
import com.openelements.data.runtime.sql.statement.SqlStatement;
import com.openelements.data.runtime.sql.tables.SqlDataTable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

@Data(name = "OE_BINARY_DATA_ENTRY")
public record BinaryDataEntry(@Attribute(partOfIdentifier = true, required = true) UUID id,
                              String name,
                              ByteArray content) {

    public static DataType<BinaryDataEntry> getDataType() {
        return DataType.of(BinaryDataEntry.class);
    }

    public static SqlDataTable<BinaryDataEntry> getSqlDataTable(SqlDialect sqlDialect) {
        return SqlDataTable.of(getDataType(), sqlDialect);
    }

    public static DataRepository<BinaryDataEntry> getDataRepository(SqlConnection sqlConnection) {
        return DataRepository.of(getDataType(), sqlConnection);
    }

    public static Optional<BinaryDataEntry> findForReference(UUID reference, SqlConnection sqlConnection) {
        final SqlDataTable<BinaryDataEntry> table = getSqlDataTable(sqlConnection.getSqlDialect());
        final SqlStatement selectStatement = sqlConnection.getSqlStatementFactory()
                .createSelectStatement(table, table.getDataColumns(), table.getKeyColumns());
        selectStatement.set("id", reference);
        try {
            final PreparedStatement preparedStatement = selectStatement.toPreparedStatement(sqlConnection);
            final ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                final UUID id = resultSet.getObject("id", UUID.class);
                final String name = resultSet.getObject("name", String.class);
                final ByteArray content = resultSet.getObject("content", ByteArray.class);
                return Optional.of(new BinaryDataEntry(id, name, content));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving last translations for " + reference, e);
        }
    }

    public static void deleteForReference(UUID id, SqlConnection connection) throws SQLException {
        final SqlDataTable<BinaryDataEntry> table = getSqlDataTable(connection.getSqlDialect());
        final SqlStatement deleteStatement = connection.getSqlStatementFactory()
                .createDeleteStatement(table, table.getKeyColumns());
        deleteStatement.set("id", id);
        deleteStatement.toPreparedStatement(connection).executeUpdate();
    }
}
