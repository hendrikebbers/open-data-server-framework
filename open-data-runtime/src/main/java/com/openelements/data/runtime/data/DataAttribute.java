package com.openelements.data.runtime.data;

public record DataAttribute<D>(String name, int oder, boolean required,
                               boolean partOfIdentifier, Class<D> type) {

}
