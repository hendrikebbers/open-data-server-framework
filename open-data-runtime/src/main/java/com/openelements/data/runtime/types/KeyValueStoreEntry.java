package com.openelements.data.runtime.types;

import com.openelements.data.api.data.Attribute;
import com.openelements.data.api.data.Data;
import com.openelements.data.runtime.data.ApiData;

@ApiData
@Data
public record KeyValueStoreEntry(
        @Attribute(required = true, partOfIdentifier = true) String storeName,
        @Attribute(required = true, partOfIdentifier = true, name = "entryKey") String key,
        @Attribute(name = "entryValue") String value
) {

}
