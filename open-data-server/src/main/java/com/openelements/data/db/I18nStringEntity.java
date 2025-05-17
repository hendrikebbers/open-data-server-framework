package com.openelements.data.db;

import com.openelements.data.data.I18nString;
import com.openelements.data.data.Language;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Transient;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
public class I18nStringEntity extends EntityWithId {

    @OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER, orphanRemoval = true)
    private Set<TranslationMessageEntity> translations = new HashSet<>();

    @Transient
    private String value;

    @Transient
    private boolean resolved = false;

    public I18nStringEntity() {

    }

    public I18nStringEntity(Language language, String value) {
        translations.add(new TranslationMessageEntity(language, value));
    }

    public I18nStringEntity(String value) {
        this(Language.EN, value);
    }

    public I18nStringEntity(I18nString i18nString) {
        Objects.requireNonNull(i18nString, "i18nString must be null");
        i18nString.translations().forEach((language, message) -> {
            translations.add(new TranslationMessageEntity(language, message));
        });
    }

    public String resolve(Language language) {
        if (!resolved) {
            if (translations == null) {
                this.value = null;
            } else {
                translations.stream().filter(t -> t.getLanguage().equals(language))
                        .findFirst()
                        .ifPresent(translation -> value = translation.getMessage());
            }
            resolved = true;
        }
        return value;
    }

    public Set<TranslationMessageEntity> getTranslations() {
        return translations;
    }

    public void setTranslations(Set<TranslationMessageEntity> translations) {
        this.translations = translations;
    }

    public String getValue() {
        return value;
    }

    public boolean isResolved() {
        return resolved;
    }
}
