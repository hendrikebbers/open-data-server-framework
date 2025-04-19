package com.openelements.data.data;

import java.util.List;

public record DataType<ENTITY>(String name, String description, Class<ENTITY> entityClass,
                               List<DataAttribute<ENTITY, ?>> attributes) {
}
