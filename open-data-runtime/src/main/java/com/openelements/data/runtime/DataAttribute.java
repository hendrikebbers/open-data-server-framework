package com.openelements.data.runtime;

public record DataAttribute(String name, int oder, boolean required,
                            boolean partOfIdentifier, DataAttributeTypeSupport dataTypeSupport) {

}
