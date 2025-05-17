package com.openelements.data.runtime;

public record DataAttribute(String name, Class<?> typeClass, String type, int oder, boolean required,
                            boolean partOfIdentifier) {

}
