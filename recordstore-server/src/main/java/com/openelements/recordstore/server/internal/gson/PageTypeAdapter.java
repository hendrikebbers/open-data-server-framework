package com.openelements.recordstore.server.internal.gson;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.openelements.data.runtime.api.Page;
import com.openelements.recordstore.server.internal.PathResolver;
import java.io.IOException;

public class PageTypeAdapter<T extends Record> extends TypeAdapter<Page<T>> {

    private final PathResolver pathResolver;

    private final Gson gson;

    public PageTypeAdapter(final Gson gson, final PathResolver pathResolver) {
        this.pathResolver = pathResolver;
        this.gson = gson;
    }

    @Override
    public void write(JsonWriter jsonWriter, Page<T> value) throws IOException {
        if (value == null) {
            jsonWriter.nullValue();
        } else if (value.getContent().isEmpty()) {
            jsonWriter.beginObject();
            jsonWriter.name("content").beginArray();
            jsonWriter.endArray();
            jsonWriter.endObject();
        } else {
            jsonWriter.beginObject();
            jsonWriter.name("content").beginArray();
            for (Object item : value.getContent()) {
                gson.toJson(item, item.getClass(), jsonWriter);
            }
            jsonWriter.endArray();
            if (value.hasNext()) {
                int nextPage = value.getPageNumber() + 1;
                pathResolver.resolveGetAllWithPaginationPath(value.getContent().get(0).getClass(),
                        value.getPageNumber() + 1, value.getPageSize());
                jsonWriter.name("nextPage").value(nextPage);
            }
            jsonWriter.endObject();
        }
    }

    @Override
    public Page<T> read(JsonReader in) throws IOException {
        throw new UnsupportedOperationException("Reading datatype from JSON is not supported");
    }
}
