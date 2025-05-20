package com.openelements.data.runtime;

import java.util.Set;

public record DataType(String name, boolean publiclyAvailable, Class<? extends Record> dataClass,
                       Set<DataAttribute> attributes) {

}
