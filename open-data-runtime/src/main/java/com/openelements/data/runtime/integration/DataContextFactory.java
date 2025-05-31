package com.openelements.data.runtime.integration;

import com.openelements.data.runtime.api.DataContext;
import com.openelements.data.runtime.sql.SqlConnection;
import com.openelements.data.runtime.sql.implementation.SqlDataContext;
import java.util.concurrent.ScheduledExecutorService;
import org.jspecify.annotations.NonNull;

public class DataContextFactory {

    public static DataContext createDataContext(@NonNull final ScheduledExecutorService executor,
            @NonNull final SqlConnection connection) {
        return new SqlDataContext(executor, connection);
    }
}
