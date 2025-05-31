package com.openelements.data.runtime.types;

import com.openelements.data.runtime.api.Attribute;
import com.openelements.data.runtime.api.Data;
import com.openelements.data.runtime.data.DataType;
import com.openelements.data.runtime.data.impl.ApiData;
import com.openelements.data.runtime.integration.DataRepository;
import com.openelements.data.runtime.sql.api.SqlConnection;
import com.openelements.data.runtime.sql.implementation.TableRepository;
import com.openelements.data.runtime.sql.statement.SqlStatement;
import com.openelements.data.runtime.sql.tables.SqlDataTable;
import com.openelements.data.runtime.sql.tables.TableColumn;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.jspecify.annotations.NonNull;

@ApiData
@Data
public record DataUpdate<E extends Record>(@Attribute(required = true, partOfIdentifier = true) String dataIdentifier,
                                           @Attribute(required = true, partOfIdentifier = true) ZonedDateTime timestamp,
                                           @Attribute(required = true) int count) {

    @NonNull
    public static DataRepository<DataUpdate> getDataRepository(@NonNull final SqlConnection sqlConnection) {
        return DataRepository.of(DataType.of(DataUpdate.class), sqlConnection);
    }

    public static Optional<ZonedDateTime> findLastUpdateTime(String dataIdentifier, SqlConnection sqlConnection) {
        final DataType<DataUpdate> dataType = DataType.of(DataUpdate.class);
        final SqlDataTable table = TableRepository.createTable(dataType, sqlConnection);
        final TableColumn<?, ?> dataIdentifierColumn = table.getColumnByName("dataIdentifier")
                .orElseThrow();
        final TableColumn<?, ?> timestampColumn = table.getColumnByName("timestamp").orElseThrow();
        final SqlStatement selectStatement = sqlConnection.getSqlStatementFactory()
                .createSelectStatement(table, List.of(timestampColumn), List.of(dataIdentifierColumn));
        selectStatement.set("dataIdentifier", dataIdentifier);
        try {
            final PreparedStatement preparedStatement = selectStatement.toPreparedStatement();
            final ResultSet resultSet = preparedStatement.executeQuery();
            List<ZonedDateTime> timestamps = new ArrayList<>();
            while (resultSet.next()) {
                ZonedDateTime timestamp = resultSet.getObject(timestampColumn.getName(), ZonedDateTime.class);
                timestamps.add(timestamp);
            }
            return timestamps.stream().sorted().findFirst();
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving last update time for data type: " + dataIdentifier, e);
        }
    }
}
