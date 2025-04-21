package com.openelements.data.db;

import com.openelements.data.data.Language;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Transient;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
public class I18nStringEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER, orphanRemoval = true)
    private Set<TranslationMessage> translations = new HashSet<>();

    @Transient
    private String value;

    @Transient
    private boolean resolved = false;

    public I18nStringEntity() {

    }

    public I18nStringEntity(Language language, String value) {
        translations.add(new TranslationMessage(language, value));
    }

    public I18nStringEntity(String value) {
        this(Language.EN, value);
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

    public Set<TranslationMessage> getTranslations() {
        return translations;
    }

    public void setTranslations(Set<TranslationMessage> translations) {
        this.translations = translations;
    }

    public String getValue() {
        return value;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public boolean isResolved() {
        return resolved;
    }
}
