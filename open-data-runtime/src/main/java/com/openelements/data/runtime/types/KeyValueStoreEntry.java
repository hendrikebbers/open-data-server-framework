package com.openelements.data.runtime.types;

import com.openelements.data.api.data.Attribute;
import com.openelements.data.api.data.Data;

@Data(name = "OE_KEY_VALUE_STORE")
public record KeyValueStoreEntry(
        @Attribute(required = true, partOfIdentifier = true) String storeName,
        @Attribute(required = true, partOfIdentifier = true, name = "entryKey") String key,
        @Attribute(name = "entryValue") String value
) {

}
