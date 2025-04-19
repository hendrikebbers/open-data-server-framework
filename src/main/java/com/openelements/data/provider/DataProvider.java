package com.openelements.data.provider;

import com.openelements.data.db.AbstractEntity;
import java.time.ZonedDateTime;
import java.util.Set;

public interface DataProvider<T extends AbstractEntity> {

    Set<T> loadUpdateData(ZonedDateTime lastUpdate);
}
