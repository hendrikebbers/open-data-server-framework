package com.openelements.data.provider.db;

import com.openelements.data.db.AbstractEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Optional;

@Entity
public class UpdateRunEntity extends AbstractEntity {

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private ZonedDateTime startOfUpdate;

    @Transient
    private Duration duration;

    @Column(nullable = false)
    private Long durationInMillis;

    @Column(nullable = false)
    private int numberOfEntities;

    public void setType(String type) {
        this.type = type;
    }

    public void setStartOfUpdate(ZonedDateTime startOfUpdate) {
        this.startOfUpdate = startOfUpdate;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
        this.durationInMillis = Optional.ofNullable(duration)
                .map(Duration::toMillis)
                .orElse(null);
    }

    public Long getDurationInMillis() {
        return durationInMillis;
    }

    public void setDurationInMillis(Long durationInMillis) {
        this.durationInMillis = durationInMillis;
        this.duration = Optional.ofNullable(durationInMillis)
                .map(Duration::ofMillis)
                .orElse(null);
    }

    public String getType() {
        return type;
    }

    public ZonedDateTime getStartOfUpdate() {
        return startOfUpdate;
    }

    public Duration getDuration() {
        return duration;
    }

    public int getNumberOfEntities() {
        return numberOfEntities;
    }

    public void setNumberOfEntities(int numberOfEntities) {
        this.numberOfEntities = numberOfEntities;
    }

    @Override
    protected String calculateUUID() {
        return type + "-" + startOfUpdate.toInstant().toEpochMilli();
    }
}

