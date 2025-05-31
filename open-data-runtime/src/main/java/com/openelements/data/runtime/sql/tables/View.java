package com.openelements.data.runtime.sql.tables;

import com.openelements.data.runtime.sql.SqlConnection;
import com.openelements.data.runtime.sql.statement.SqlStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class View {

    private final SqlDataTable tableA;

    private final List<TableColumn<?, ?>> selectColumnsA;

    private final SqlDataTable tableB;

    private final List<TableColumn<?, ?>> selectColumnsB;

    private final TableColumn<?, ?> whereColumnB;

    private final Function<ResultRow, ?> whereValueFunction;

    private final SqlConnection sqlConnection;

    public View(SqlDataTable tableA, List<TableColumn<?, ?>> selectColumnsA,
            SqlDataTable tableB, List<TableColumn<?, ?>> selectColumnsB,
            TableColumn<?, ?> whereColumnB, Function<ResultRow, ?> whereValueFunction,
            SqlConnection sqlConnection
    ) throws Exception {
        this.tableA = tableA;
        this.selectColumnsA = selectColumnsA;
        this.tableB = tableB;
        this.selectColumnsB = selectColumnsB;
        this.whereColumnB = whereColumnB;
        this.whereValueFunction = whereValueFunction;
        this.sqlConnection = sqlConnection;
    }

    public List<ResultRow> getAll() throws SQLException {
        final List<ResultRow> rows = new ArrayList<>();
        SqlStatement selectStatementA = sqlConnection.getSqlStatementFactory()
                .createSelectStatement(tableA, selectColumnsA, List.of());
        selectStatementA.executeQuery().forEach(resultRowA -> {
            SqlStatement selectStatementB = sqlConnection.getSqlStatementFactory()
                    .createSelectStatement(tableB, selectColumnsB, List.of(whereColumnB));
            selectStatementB.set(whereColumnB.getName(), whereValueFunction.apply(resultRowA));
            try {
                selectStatementB.executeQuery().forEach(resultRowB -> {
                    System.out.println("Result from table A: " + resultRowA);
                    System.out.println("Result from table B: " + resultRowB);
                    ResultRow combinedRow = new ViewResultRow(resultRowA, resultRowB);
                    rows.add(combinedRow);
                });
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        return rows;
    }

    public long getCount() throws SQLException {
        return getAll().size();
    }
}
