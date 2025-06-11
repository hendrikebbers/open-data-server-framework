package com.openelements.recordstore.server.internal.openapi;

import io.swagger.v3.oas.models.media.Schema;
import java.util.Arrays;

public class EnumSchema extends Schema<String> {

    public EnumSchema(Class<? extends Enum> clazz) {
        this.type("string");
        this.format("enum");
        Arrays.asList(clazz.getEnumConstants()).forEach(enumConstant -> {
            addEnumItemObject(enumConstant.name());
        });
    }

    @Override
    public String getType() {
        return "string";
    }
}
