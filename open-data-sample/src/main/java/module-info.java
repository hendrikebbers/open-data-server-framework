import com.openelements.data.api.DataSource;
import com.openelements.data.api.DataTypeProvider;
import com.openelements.data.sample.EmployeeSupport;

module com.openelements.data.sample {
    requires com.openelements.data.runtime;
    requires com.openelements.data.server;
    requires java.sql;
    requires com.openelements.data.api;

    provides DataTypeProvider with EmployeeSupport;
    provides DataSource with EmployeeSupport;
}