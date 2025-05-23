package com.openelements.data.runtime.sql.tables;

public class SqlStatementFactory {

    public static String createSelectStatement(SqlTable table) {
        final StringBuilder sql = new StringBuilder("SELECT ");
        for (TableColumn<?> column : table.getColumns()) {
            sql.append(column.getName()).append(", ");
        }
        sql.setLength(sql.length() - 2); // Remove the last comma and space
        sql.append(" FROM ").append(table.getName());
        return sql.toString();
    }

    public static String createSelectPageStatement(SqlTable table, int pageNumber, int pageSize) {
        final StringBuilder sql = new StringBuilder(createSelectStatement(table));
        sql.append(" LIMIT ").append(pageSize).append(" OFFSET ").append((pageNumber - 1) * pageSize);
        return sql.toString();
    }

    public static String createQueryCountStatement(SqlTable table) {
        final StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM ");
        sql.append(table.getName());
        return sql.toString();
    }

    public static String createTableCreateStatement(SqlTable table) {
        final StringBuilder sql = new StringBuilder("CREATE TABLE ");
        sql.append(table.getName());
        sql.append(" (");
        for (TableColumn<?> column : table.getColumns()) {
            sql.append(column.getName()).append(" ").append(column.getType().getSqlType()).append(", ");
        }
        sql.setLength(sql.length() - 2); // Remove the last comma and space
        sql.append(")");
        return sql.toString();
    }
}
