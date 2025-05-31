package com.openelements.data.runtime.data;

public record DataAttributeReference(Class<? extends Record> toType,
                                     String toAttribute) {

}
