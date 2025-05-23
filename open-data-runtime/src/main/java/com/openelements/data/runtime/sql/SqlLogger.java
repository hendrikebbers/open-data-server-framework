package com.openelements.data.runtime.sql;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SqlLogger {

    private final static Logger log = LoggerFactory.getLogger(SqlLogger.class);

    public static void log(PreparedStatement preparedStatement) throws SQLException {
        log.info(preparedStatement.toString());
    }
}
