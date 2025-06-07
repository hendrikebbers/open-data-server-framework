package com.openelements.recordstore.server.internal.gson;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.time.LocalDate;

public class LocalDateTypeAdapter extends TypeAdapter<LocalDate> {

    @Override
    public void write(JsonWriter jsonWriter, LocalDate value) throws IOException {

    }

    @Override
    public LocalDate read(JsonReader in) throws IOException {
        throw new UnsupportedOperationException("Reading datatype from JSON is not supported");
    }
}
