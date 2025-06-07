package com.openelements.recordstore.server.internal.gson;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.openelements.data.runtime.data.DataReference;
import java.io.IOException;

public class DataReferenceTypeAdapter extends TypeAdapter<DataReference> {

    @Override
    public void write(JsonWriter out, DataReference value) throws IOException {
        if (value == null) {
            out.nullValue();
        } else {
            out.value(value.id().toString());
        }
    }

    @Override
    public DataReference read(JsonReader in) throws IOException {
        throw new UnsupportedOperationException("Reading datatype from JSON is not supported");
    }
}
