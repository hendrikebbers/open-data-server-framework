package com.openelements.data.server.internal;

import com.google.gson.JsonObject;
import com.openelements.data.data.AttributeType;
import com.openelements.data.data.DataAttribute;
import com.openelements.data.data.DataType;
import com.openelements.data.data.Language;
import com.openelements.data.db.AbstractEntity;
import com.openelements.data.db.I18nStringEntity;
import io.helidon.common.http.HttpRequest;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

public class JsonFactory {

    public <E extends AbstractEntity> JsonObject createJsonObject(HttpRequest request, Language language, E entity,
            DataType<E> dataType) {
        final JsonObject jsonObject = new JsonObject();
        for (final DataAttribute<E, ?> attribute : dataType.attributes()) {
            final AttributeType attributeType = attribute.type();
            switch (attributeType) {
                case STRING ->
                        jsonObject.addProperty(attribute.identifier(), (String) attribute.supplier().apply(entity));
                case NUMBER ->
                        jsonObject.addProperty(attribute.identifier(), (Number) attribute.supplier().apply(entity));
                case BOOLEAN ->
                        jsonObject.addProperty(attribute.identifier(), (Boolean) attribute.supplier().apply(entity));
                case DATE_TIME -> jsonObject.addProperty(attribute.identifier(),
                        toJson((TemporalAccessor) attribute.supplier().apply(entity)));
                case I18N_STRING -> {
                    final I18nStringEntity i18nString = (I18nStringEntity) attribute.supplier().apply(entity);
                    if (i18nString != null) {
                        jsonObject.addProperty(attribute.identifier(), i18nString.resolve(language));
                    } else {
                        jsonObject.addProperty(attribute.identifier(), (String) null);
                    }
                }
                default -> throw new IllegalArgumentException("Unsupported attribute type: " + attributeType);
            }
        }
        return jsonObject;
    }

    private String toJson(TemporalAccessor temporalAccessor) {
        return DateTimeFormatter.ISO_DATE_TIME.format(temporalAccessor);
    }
}
