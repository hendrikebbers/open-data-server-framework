package com.openelements.data.server.internal.gson;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

public class TemporalAccessorTypeAdapter extends TypeAdapter<TemporalAccessor> {

    @Override
    public void write(JsonWriter jsonWriter, TemporalAccessor temporalAccessor) throws IOException {
        if (temporalAccessor == null) {
            jsonWriter.nullValue();
        } else {
            if (temporalAccessor instanceof LocalDate localDate) {
                jsonWriter.value(DateTimeFormatter.ISO_LOCAL_DATE.format(localDate));
            } else if (temporalAccessor instanceof java.time.LocalDateTime localDateTime) {
                jsonWriter.value(DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(localDateTime));
            } else if (temporalAccessor instanceof java.time.ZonedDateTime zonedDateTime) {
                jsonWriter.value(DateTimeFormatter.ISO_ZONED_DATE_TIME.format(zonedDateTime));
            } else if (temporalAccessor instanceof java.time.OffsetDateTime offsetDateTime) {
                jsonWriter.value(DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(offsetDateTime));
            } else {
                throw new IOException("Unsupported TemporalAccessor type: " + temporalAccessor.getClass().getName());
            }
        }
    }

    @Override
    public TemporalAccessor read(JsonReader jsonReader) throws IOException {
        throw new UnsupportedOperationException("Reading TemporalAccessor from JSON is not supported");
    }
}
