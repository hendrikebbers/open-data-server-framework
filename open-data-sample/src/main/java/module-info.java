import com.openelements.data.runtime.DataSource;
import com.openelements.data.runtime.DataTypeProvider;
import com.openelements.data.sample.EmployeeSupport;
import com.openelements.data.sample.MemorySupport;

open module com.openelements.data.sample {
    requires com.openelements.data.runtime;
    requires com.openelements.data.server;
    requires java.sql;
    requires com.openelements.data.api;
    requires org.slf4j;

    provides DataTypeProvider with EmployeeSupport, MemorySupport;
    provides DataSource with EmployeeSupport, MemorySupport;
}