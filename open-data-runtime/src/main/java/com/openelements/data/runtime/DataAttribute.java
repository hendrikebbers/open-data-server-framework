package com.openelements.data.runtime;

import com.openelements.data.runtime.sql.DataAttributeTypeSupport;

public record DataAttribute(String name, int oder, boolean required,
                            boolean partOfIdentifier, DataAttributeTypeSupport<?, ?> dataTypeSupport) {

}
