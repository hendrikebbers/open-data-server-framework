package com.openelements.data.provider.db;

import com.openelements.data.db.AbstractEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import java.time.ZonedDateTime;
import org.jspecify.annotations.NonNull;

@Entity
public class UpdateEntity extends AbstractEntity {

    @Column(nullable = false, unique = true)
    private String type;

    @Column(nullable = false)
    private ZonedDateTime lastUpdate;

    public String getType() {
        return type;
    }

    public void setType(final @NonNull String type) {
        this.type = type;
    }

    public ZonedDateTime getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(final @NonNull ZonedDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    @Override
    protected String calculateUUID() {
        return type;
    }
}
