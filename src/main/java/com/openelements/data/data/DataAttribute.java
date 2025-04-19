package com.openelements.data.data;

import java.util.function.Function;

public record DataAttribute<ENTITY, TYPE>(String name, String description, AttributeType type,
                                          Function<ENTITY, TYPE> supplier) {
}
