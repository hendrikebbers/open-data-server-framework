package com.openelements.data.server.internal.handler;

import com.openelements.data.runtime.DataType;
import com.openelements.data.runtime.sql.DataRepository;
import com.openelements.data.runtime.sql.Page;
import java.util.List;
import java.util.Objects;

public class DataHandlerImpl<E extends Record, D extends DataType<E>> implements DataHandler<E, D> {

    private final D dataType;

    private final Class<E> dataClass;

    private final DataRepository<E> dataRepository;

    public DataHandlerImpl(final D dataType, final Class<E> dataClass, DataRepository<E> dataRepository) {
        this.dataType = Objects.requireNonNull(dataType, "dataType must not be null");
        this.dataClass = Objects.requireNonNull(dataClass, "dataClass must not be null");
        this.dataRepository = Objects.requireNonNull(dataRepository, "dataRepository must not be null");
    }

    @Override
    public List<E> getAll() throws Exception {
        return dataRepository.getAll();
    }

    @Override
    public Page<E> getPage(int pageNumber, int pageSize) throws Exception {
        return dataRepository.getPage(pageNumber, pageSize);
    }

    @Override
    public long getCount() throws Exception {
        return dataRepository.getCount();
    }

    @Override
    public D getDataType() {
        return dataType;
    }

    @Override
    public Class<E> getDataClass() {
        return dataClass;
    }

    @Override
    public boolean isPubliclyAvailable() {
        return dataType.publiclyAvailable();
    }
}
