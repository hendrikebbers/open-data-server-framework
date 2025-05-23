package com.openelements.data.runtime.sql.support;

import com.openelements.data.api.types.I18nString;
import com.openelements.data.runtime.sql.PersistenceContext;
import com.openelements.data.runtime.sql.QueryContext;
import com.openelements.data.runtime.sql.repositories.I18nStringRepository;
import com.openelements.data.runtime.sql.types.ReferenceType;

public class I18NSupport extends AbstractDataAttributeTypeSupport<I18nString, Long> {

    public I18NSupport() {
        super("I18nString", I18nString.class, ReferenceType.INSTANCE);
    }

    @Override
    public I18nString convertValueFromSqlResult(Long sqlValue, QueryContext queryContext) {
        try {
            I18nStringRepository repository = new I18nStringRepository(queryContext.getConnection());
            return repository.load(sqlValue);
        } catch (Exception e) {
            throw new RuntimeException("Error loading I18nString with ID: " + sqlValue, e);
        }
    }

    @Override
    public Long convertValueForSqlPersit(I18nString newValue, Long currentValue,
            PersistenceContext persistenceContext) {
        try {
            if (currentValue == null) {
                if (newValue == null) {
                    return null;
                }
                I18nStringRepository repository = new I18nStringRepository(persistenceContext.getConnection());
                long id = repository.insert(newValue);
                return id;
            }
            I18nStringRepository repository = new I18nStringRepository(persistenceContext.getConnection());
            repository.update(currentValue, newValue);
            return currentValue;
        } catch (Exception e) {
            throw new RuntimeException("Error persisting I18nString", e);
        }
    }
}
