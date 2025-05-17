package com.openelements.data.server.internal;

import com.openelements.data.data.DataType;
import com.openelements.data.db.AbstractEntity;
import com.openelements.data.db.EntityRepository;

public record OpenDataDefinition<E extends AbstractEntity>(String pathName, DataType<E> dataType,
                                                           EntityRepository<E> dataProvider) {
}
