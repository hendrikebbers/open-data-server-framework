package com.openelements.data.runtime.data;

import com.openelements.data.api.data.Attribute;
import com.openelements.data.api.data.Data;
import com.openelements.data.runtime.DataTypeProvider;
import com.openelements.data.runtime.RecordStoreApiDataTypesProvider;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DataLoader {

    public static Set<DataType<?>> loadData() {
        final Set<DataType<?>> dataTypes = new HashSet<>();
        final Set<DataTypeProvider> instances = DataTypeProvider.getInstances();
        instances.stream().flatMap(provider -> provider.getDataTypes().stream())
                .forEach(dataType -> {
                    final DataType dataTypeInstance = load(dataType);
                    dataTypes.add(dataTypeInstance);
                });
        RecordStoreApiDataTypesProvider.getInstance().getDataTypes().stream()
                .map(DataLoader::load)
                .forEach(dataType -> dataTypes.add(dataType));
        return Collections.unmodifiableSet(dataTypes);
    }

    public static DataType load(Class<? extends Record> dataType) {
        final String dataTypeName;
        final boolean publiclyAvailable;
        final boolean virtual;
        if (dataType.isAnnotationPresent(Data.class)) {
            final Data data = dataType.getAnnotation(Data.class);
            if (data.name() != null && !data.name().isEmpty()) {
                dataTypeName = data.name();
            } else {
                dataTypeName = dataType.getSimpleName();
            }
            publiclyAvailable = data.publiclyAvailable();
            virtual = data.isVirtual();
        } else {
            dataTypeName = dataType.getSimpleName();
            publiclyAvailable = true;
            virtual = false;
        }
        final boolean isApi = dataType.isAnnotationPresent(ApiData.class);
        List<DataAttribute> attributes = loadAttributes(dataType);
        return new DataType(dataTypeName, isApi, publiclyAvailable, virtual, dataType, attributes);
    }

    public static List<DataAttribute> loadAttributes(Class<? extends Record> dataType) {
        final List<DataAttribute> attributes = new ArrayList<>();
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
                required = false;
            }
            final DataAttribute attribute = new DataAttribute(name, order, required, partOfIdentifier,
                    component.getGenericType());
            attributes.add(attribute);
        });
        return Collections.unmodifiableList(attributes);
    }

}
