package com.openelements.data.provider;

import com.openelements.data.db.ReadOnlyRepository;
import java.time.ZonedDateTime;
import java.util.concurrent.Executor;

public record DataProviderContext(ReadOnlyRepository repository, ZonedDateTime lastUpdate, Executor executor) {
   
}
