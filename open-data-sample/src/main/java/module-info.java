import com.openelements.data.api.DataSource;
import com.openelements.data.api.DataTypeProvider;
import com.openelements.data.sample.EmployeeSupport;

open module com.openelements.data.sample {
    requires com.openelements.data.runtime;
    requires com.openelements.data.server;
    requires java.sql;
    requires com.openelements.data.api;
    requires org.slf4j;

    provides DataTypeProvider with EmployeeSupport;
    provides DataSource with EmployeeSupport;
}