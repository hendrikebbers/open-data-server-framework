package com.openelements.data.provider;

import com.openelements.data.db.EntityRepositoryFactory;
import java.time.ZonedDateTime;
import java.util.concurrent.Executor;

public record DataProviderContext(EntityRepositoryFactory repositoryFactory, ZonedDateTime lastUpdate,
                                  Executor executor) {

}
