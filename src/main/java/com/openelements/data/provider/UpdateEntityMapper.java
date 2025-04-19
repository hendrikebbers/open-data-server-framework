package com.openelements.data.provider;

import com.openelements.data.db.EntityMapper;

public class UpdateEntityMapper implements EntityMapper<UpdateEntity> {

    @Override
    public UpdateEntity updateEntity(UpdateEntity updated, UpdateEntity toUpdate) {
        toUpdate.setType(updated.getType());
        toUpdate.setLastUpdate(updated.getLastUpdate());
        return toUpdate;
    }
}
