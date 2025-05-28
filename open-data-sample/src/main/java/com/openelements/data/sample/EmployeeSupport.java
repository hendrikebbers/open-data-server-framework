package com.openelements.data.sample;

import com.openelements.data.api.types.I18nString;
import com.openelements.data.runtime.data.DataContext;
import com.openelements.data.runtime.data.DataSource;
import com.openelements.data.runtime.data.DataTypeProvider;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmployeeSupport implements DataTypeProvider, DataSource {

    private final static Logger log = LoggerFactory.getLogger(EmployeeSupport.class);

    @Override
    public Set<Class<? extends Record>> getDataTypes() {
        return Set.of(Employee.class);
    }

    @Override
    public void install(DataContext dataContext) {
        final Runnable runnable = () -> {
            try {
                Employee employee = new Employee("John", "Doe", "john@doe.com", null, I18nString.of("Senior Developer"),
                        null, LocalDate.now());
                dataContext.store(Employee.class, List.of(employee));
            } catch (Exception e) {
                log.error("Error providing data", e);
            }
        };
        dataContext.getExecutor().scheduleAtFixedRate(runnable, 0, 2, java.util.concurrent.TimeUnit.SECONDS);
    }
}
