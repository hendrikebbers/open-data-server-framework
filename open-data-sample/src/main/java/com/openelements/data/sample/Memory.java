package com.openelements.data.sample;

import com.openelements.data.api.data.Attribute;
import java.time.ZonedDateTime;

public record Memory(@Attribute(partOfIdentifier = true, required = true) ZonedDateTime timestamp,
                     @Attribute(required = true) long memoryUsed) {

}
