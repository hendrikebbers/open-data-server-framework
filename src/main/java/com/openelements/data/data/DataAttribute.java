package com.openelements.data.data;

import java.util.function.Function;

public record DataAttribute<ENTITY, TYPE>(String name, AttributeType type, Function<ENTITY, TYPE> supplier) {
}
