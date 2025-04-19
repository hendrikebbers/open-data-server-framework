package com.openelements.data.server;

import com.openelements.data.data.DataProvider;
import com.openelements.data.data.DataType;

public record DataEndpointMetadata<ENTITY>(String path, DataType<ENTITY> dataType,
                                           DataProvider<ENTITY> dataProvider) {
}
