package com.openelements.data.runtime;

public record DataAttribute<D>(String name, int oder, boolean required,
                               boolean partOfIdentifier, Class<D> type) {

}
