package com.openelements.data.sample;

import com.openelements.data.api.data.Attribute;

public record Employee(String firstname, String lastname, @Attribute(partOfIdentifier = true) String email) {

}
