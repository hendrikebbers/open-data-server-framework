package com.openelements.data.data;

import com.openelements.data.db.AbstractEntity;
import java.util.List;

public record DataType<E extends AbstractEntity>(String name, String description, Class<E> entityClass,
                                                 List<DataAttribute<E, ?>> attributes) {
}
