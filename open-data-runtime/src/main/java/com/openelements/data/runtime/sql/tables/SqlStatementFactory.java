package com.openelements.data.runtime.sql.tables;

public class SqlStatementFactory {

    public static <E extends Record> String createSelectStatement(SqlDataTable<E> table) {
        final StringBuilder sql = new StringBuilder("SELECT ");
        for (TableColumn<E, ?> column : table.getColumns()) {
            sql.append(column.getName()).append(", ");
        }
        sql.setLength(sql.length() - 2); // Remove the last comma and space
        sql.append(" FROM ").append(table.getName());
        return sql.toString();
    }

    public static <E extends Record> String createSelectPageStatement(SqlDataTable<E> table, int pageNumber,
            int pageSize) {
        final StringBuilder sql = new StringBuilder(createSelectStatement(table));
        sql.append(" LIMIT ").append(pageSize).append(" OFFSET ").append((pageNumber) * pageSize);
        return sql.toString();
    }

    public static <E extends Record> String createQueryCountStatement(SqlDataTable<E> table) {
        final StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM ");
        sql.append(table.getName());
        return sql.toString();
    }

    public static <E extends Record> String createFindStatement(SqlDataTable<E> table) {
        final StringBuilder sql = new StringBuilder(createSelectStatement(table));
        sql.append(" WHERE ");
        for (TableColumn<E, ?> column : table.getKeyColumns()) {
            sql.append(column.getName()).append(" = ? AND ");
        }
        sql.setLength(sql.length() - 5); // Remove the last " AND "
        return sql.toString();
    }

    public static <E extends Record> String createTableCreateStatement(SqlDataTable<E> table) {
        final StringBuilder sql = new StringBuilder("CREATE TABLE IF NOT EXISTS ");
        sql.append(table.getName());
        sql.append(" (");
        for (TableColumn<E, ?> column : table.getColumns()) {
            sql.append(column.getName()).append(" ").append(column.getType().getSqlType()).append(", ");
        }
        sql.setLength(sql.length() - 2); // Remove the last comma and space
        sql.append(")");
        return sql.toString();
    }

    public static <E extends Record> String createUpdateStatement(SqlDataTable<E> table) {
        final StringBuilder sql = new StringBuilder("UPDATE ");
        sql.append(table.getName()).append(" SET ");
        for (TableColumn<E, ?> column : table.getColumns()) {
            if (!table.getKeyColumns().contains(column)) {
                sql.append(column.getName()).append(" = ?, ");
            }
        }
        sql.setLength(sql.length() - 2); // Remove the last comma and space
        sql.append(" WHERE ");
        for (TableColumn<E, ?> column : table.getKeyColumns()) {
            sql.append(column.getName()).append(" = ? AND ");
        }
        sql.setLength(sql.length() - 5); // Remove the last " AND "
        return sql.toString();
    }

    public static <E extends Record> String createInsertStatement(SqlDataTable<E> table) {
        final StringBuilder sql = new StringBuilder("INSERT INTO ");
        sql.append(table.getName()).append(" (");
        for (TableColumn<E, ?> column : table.getColumns()) {
            sql.append(column.getName()).append(", ");
        }
        sql.setLength(sql.length() - 2); // Remove the last comma and space
        sql.append(") VALUES (");
        for (int i = 0; i < table.getColumns().size(); i++) {
            sql.append("?, ");
        }
        sql.setLength(sql.length() - 2); // Remove the last comma and space
        sql.append(")");
        return sql.toString();
    }
}
