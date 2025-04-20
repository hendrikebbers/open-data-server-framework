package com.openelements.data.server.internal;

import com.openelements.data.data.DataType;
import com.openelements.data.db.AbstractEntity;
import com.openelements.data.db.DbBasedDataProvider;

public record OpenDataDefinition<E extends AbstractEntity>(String pathName, DataType<E> dataType,
                                                           DbBasedDataProvider<E> dataProvider) {
}
