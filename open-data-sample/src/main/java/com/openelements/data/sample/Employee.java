package com.openelements.data.sample;

import com.openelements.data.api.data.Attribute;
import com.openelements.data.api.types.BinaryData;
import com.openelements.data.api.types.I18nString;
import java.time.LocalDate;

public record Employee(@Attribute(required = true) String firstname,
                       @Attribute(required = true) String lastname,
                       @Attribute(required = true, partOfIdentifier = true) String email,
                       BinaryData photo,
                       @Attribute(required = true) I18nString role,
                       String githubAccount,
                       @Attribute(required = true) LocalDate startDate) {
}
