package com.openelements.data.sample;

import com.openelements.data.runtime.data.DataContext;
import com.openelements.data.runtime.data.DataSource;
import com.openelements.data.runtime.data.DataTypeProvider;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MemorySupport implements DataTypeProvider, DataSource {

    private final static Logger log = LoggerFactory.getLogger(MemorySupport.class);

    @Override
    public Set<Class<? extends Record>> getDataTypes() {
        return Set.of(Memory.class);
    }

    @Override
    public void install(DataContext dataContext) {
        final Runnable runnable = () -> {
            try {
                final long freeMemory = Runtime.getRuntime().freeMemory();
                Memory memory = new Memory(ZonedDateTime.now(), freeMemory, Set.of());
                dataContext.store(Memory.class, List.of(memory));
            } catch (Exception e) {
                log.error("Error providing data", e);
            }
        };
        dataContext.getExecutor().scheduleAtFixedRate(runnable, 0, 1, java.util.concurrent.TimeUnit.SECONDS);
    }
}
