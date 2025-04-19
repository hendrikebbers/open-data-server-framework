package com.openelements.data.data;

@FunctionalInterface
public interface I18nString {
    String resolve(Language language);
}
