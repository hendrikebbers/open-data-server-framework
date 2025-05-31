package com.openelements.data.runtime.data.impl;

import com.openelements.data.runtime.api.Attribute;
import com.openelements.data.runtime.api.Data;
import com.openelements.data.runtime.api.DataTypeProvider;
import com.openelements.data.runtime.api.RecordStoreApiDataTypesProvider;
import com.openelements.data.runtime.api.Reference;
import com.openelements.data.runtime.data.DataAttribute;
import com.openelements.data.runtime.data.DataAttributeReference;
import com.openelements.data.runtime.data.DataType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.jspecify.annotations.NonNull;

public class DataLoader {

    @NonNull
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

    @NonNull
    public static DataType load(@NonNull final Class<? extends Record> dataType) {
        Objects.requireNonNull(dataType, "dataType must not be null");
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
        final List<DataAttribute> attributes = loadAttributes(dataType);
        return new DataType(dataTypeName, isApi, publiclyAvailable, virtual, dataType, attributes);
    }

    @NonNull
    public static List<DataAttribute> loadAttributes(@NonNull final Class<? extends Record> dataType) {
        Objects.requireNonNull(dataType, "dataType must not be null");
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
            final Set<DataAttributeReference> references = new HashSet<>();
            if (component.isAnnotationPresent(Reference.class)) {
                final Reference reference = component.getAnnotation(Reference.class);
                references.add(new DataAttributeReference(reference.toType(), reference.toAttribute()));
            }
            final DataAttribute attribute = new DataAttribute(name, order, required, partOfIdentifier,
                    component.getGenericType(), Collections.unmodifiableSet(references));
            attributes.add(attribute);
        });
        return Collections.unmodifiableList(attributes);
    }

}
