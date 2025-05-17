package com.openelements.data.provider;

import com.openelements.data.db.AbstractEntity;
import java.util.Set;

public interface EntityUpdatesProvider<T extends AbstractEntity> {

    Set<T> loadUpdatedData(DataProviderContext context);

}
