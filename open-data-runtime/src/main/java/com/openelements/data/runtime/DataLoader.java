package com.openelements.data.runtime;

import com.openelements.data.api.DataTypesProvider;
import com.openelements.data.api.context.DataContext;
import com.openelements.data.api.data.Attribute;
import com.openelements.data.api.data.Data;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class DataLoader {

    public Set<DataType> loadData(DataContext dataContext) {
        final Set<DataType> dataTypes = new HashSet<>();
        final Set<DataTypesProvider> instances = DataTypesProvider.getInstances();
        instances.stream().flatMap(provider -> provider.getDataTypes(dataContext).stream())
                .forEach(dataType -> {
                    final String dataTypeName;
                    if (dataType.isAnnotationPresent(Data.class)) {
                        final Data data = dataType.getAnnotation(Data.class);
                        if (data.name() != null && !data.name().isEmpty()) {
                            dataTypeName = data.name();
                        } else {
                            dataTypeName = dataType.getSimpleName();
                        }
                    } else {
                        dataTypeName = dataType.getSimpleName();
                    }
                    Set<DataAttribute> attributes = loadAttributes(dataType);
                    final DataType dataTypeInstance = new DataType(dataTypeName, dataType, attributes);
                    dataTypes.add(dataTypeInstance);
                });
        return Collections.unmodifiableSet(dataTypes);
    }

    private Set<DataAttribute> loadAttributes(Class<? extends Record> dataType) {
        final Set<DataAttribute> attributes = new HashSet<>();
        Arrays.asList(dataType.getRecordComponents()).forEach(component -> {
            final String name;
            if (component.isAnnotationPresent(Attribute.class)) {
                final Attribute attribute = component.getAnnotation(Attribute.class);
                if (attribute.name() != null && !attribute.name().isEmpty()) {
                    name = attribute.name();
                } else {
                    name = component.getName();
                }
            } else {
                name = component.getName();
            }
            final int order;
            if (component.isAnnotationPresent(Attribute.class)) {
                final Attribute attribute = component.getAnnotation(Attribute.class);
                order = attribute.order();
            } else {
                order = -1;
            }
            final boolean partOfIdentifier;
            if (component.isAnnotationPresent(Attribute.class)) {
                final Attribute attribute = component.getAnnotation(Attribute.class);
                partOfIdentifier = attribute.partOfIdentifier();
            } else {
                partOfIdentifier = false;
            }
            final boolean required;
            if (component.isAnnotationPresent(Attribute.class)) {
                final Attribute attribute = component.getAnnotation(Attribute.class);
                required = attribute.required();
            } else {
                required = true;
            }

            final DataAttributeTypeSupport typeSupport = DataAttributeTypeSupport.getInstances().stream()
                    .filter(support -> support.getJavaType().isAssignableFrom(component.getType()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Unsupported data type " + component.getType()));

            final DataAttribute attribute = new DataAttribute(name, component.getType(), typeSupport.getUniqueName(),
                    order, required,
                    partOfIdentifier);
            attributes.add(attribute);
        });
        return Collections.unmodifiableSet(attributes);
    }

}
