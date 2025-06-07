package com.openelements.recordstore.server.internal.gson;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.openelements.data.runtime.api.types.Binary;
import com.openelements.data.runtime.data.BinaryReference;
import com.openelements.recordstore.server.internal.PathResolver;
import java.io.IOException;

public class BinaryTypeAdapter extends TypeAdapter<Binary> {

    private final PathResolver pathResolver;

    public BinaryTypeAdapter(PathResolver pathResolver) {
        this.pathResolver = pathResolver;
    }

    @Override
    public void write(JsonWriter jsonWriter, Binary value) throws IOException {
        if (value == null) {
            jsonWriter.nullValue();
        } else {
            if (value instanceof BinaryReference binaryReference) {
                jsonWriter.value(binaryReference.id().toString());
            }
        }
        throw new IOException("Unsupported Binary type: " + value.getClass().getName());

    }

    @Override
    public Binary read(JsonReader in) throws IOException {
        throw new UnsupportedOperationException("Reading datatype from JSON is not supported");
    }
}
