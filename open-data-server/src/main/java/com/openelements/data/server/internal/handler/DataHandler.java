package com.openelements.data.server.internal.handler;

import com.openelements.data.runtime.Page;
import com.openelements.data.runtime.data.DataType;
import java.util.List;

public interface DataHandler<E extends Record, D extends DataType<E>> {

    List<E> getAll() throws Exception;

    Page<E> getPage(int pageNumber, int pageSize) throws Exception;

    long getCount() throws Exception;

    Class<E> getDataClass();

    boolean isPubliclyAvailable();

}
