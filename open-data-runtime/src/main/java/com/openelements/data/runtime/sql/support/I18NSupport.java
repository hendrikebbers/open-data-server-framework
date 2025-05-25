package com.openelements.data.runtime.sql.support;

import com.openelements.data.api.types.I18nString;
import com.openelements.data.runtime.sql.SqlConnection;
import com.openelements.data.runtime.sql.repositories.I18nStringRepository;

public class I18NSupport extends AbstractDataAttributeTypeSupport<I18nString, Long> {

    public I18NSupport() {
        super("I18nString", I18nString.class, "LONG");
    }

    @Override
    public I18nString convertValueFromSqlResult(Long sqlValue, SqlConnection connection) {
        try {
            I18nStringRepository repository = new I18nStringRepository(connection);
            return repository.load(sqlValue);
        } catch (Exception e) {
            throw new RuntimeException("Error loading I18nString with ID: " + sqlValue, e);
        }
    }

    @Override
    public Long convertValueForSqlPersit(I18nString newValue, Long currentValue,
            SqlConnection connection) {
        try {
            if (currentValue == null) {
                if (newValue == null) {
                    return null;
                }
                I18nStringRepository repository = new I18nStringRepository(connection);
                long id = repository.insert(newValue);
                return id;
            }
            I18nStringRepository repository = new I18nStringRepository(connection);
            repository.update(currentValue, newValue);
            return currentValue;
        } catch (Exception e) {
            throw new RuntimeException("Error persisting I18nString", e);
        }
    }
}
