package com.openelements.data.runtime.types;

import com.openelements.data.api.data.Attribute;
import com.openelements.data.api.data.Data;
import com.openelements.data.api.types.I18nString;
import com.openelements.data.runtime.data.ApiData;
import com.openelements.data.runtime.data.DataAttribute;
import com.openelements.data.runtime.data.DataAttributeReference;
import com.openelements.data.runtime.data.DataType;
import java.util.ArrayList;
import java.util.List;

@ApiData
@Data
public record DataReferenceEntry(@Attribute(partOfIdentifier = true, required = true) String dataIdentifierA,
                                 @Attribute(partOfIdentifier = true, required = true) String attributeIdentifiersA,
                                 @Attribute(partOfIdentifier = true, required = true) String dataIdentifierB,
                                 @Attribute(partOfIdentifier = true, required = true) String attributeIdentifiersB,
                                 I18nString name,
                                 I18nString description) {
    public static <E extends Record> List<DataReferenceEntry> of(DataType<E> dataType) {
        final List<DataReferenceEntry> entries = new ArrayList<>();
        for (DataAttribute<?, ?> attribute : dataType.attributes()) {
            for (DataAttributeReference reference : attribute.references()) {
                DataReferenceEntry entry = new DataReferenceEntry(
                        dataType.name(),
                        attribute.name(),
                        reference.toType().getName(),
                        reference.toAttribute(),
                        null,
                        null
                );
                entries.add(entry);
            }
        }
        return entries;
    }
}
