package com.openelements.data.data;

import com.openelements.data.db.AbstractEntity;
import java.util.function.Function;

public record DataAttribute<E extends AbstractEntity, T>(String identifier, I18nString name, I18nString description,
                                                         AttributeType type,
                                                         Function<E, T> supplier) {
}
