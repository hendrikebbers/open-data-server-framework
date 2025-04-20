package com.openelements.data.provider.db;

import com.openelements.data.db.EntityMapper;

public class UpdateRunEntityMapper implements EntityMapper<UpdateRunEntity> {

    @Override
    public UpdateRunEntity updateEntity(UpdateRunEntity updated, UpdateRunEntity toUpdate) {
        toUpdate.setType(updated.getType());
        toUpdate.setStartOfUpdate(updated.getStartOfUpdate());
        toUpdate.setDuration(updated.getDuration());
        toUpdate.setNumberOfEntities(updated.getNumberOfEntities());
        return toUpdate;
    }
}
