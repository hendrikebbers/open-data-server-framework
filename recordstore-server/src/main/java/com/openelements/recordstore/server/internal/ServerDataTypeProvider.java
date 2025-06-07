package com.openelements.recordstore.server.internal;

import com.openelements.data.runtime.api.DataTypeProvider;
import com.openelements.recordstore.server.internal.path.PathEntity;
import java.util.Set;
import org.jspecify.annotations.NonNull;

public class ServerDataTypeProvider implements DataTypeProvider {

    @Override
    public @NonNull Set<Class<? extends Record>> getDataTypes() {
        return Set.of(PathEntity.class);
    }
}
