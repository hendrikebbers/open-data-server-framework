package com.openelements.data.sample;

import com.openelements.data.data.AttributeType;
import com.openelements.data.data.DataAttribute;
import com.openelements.data.data.DataProvider;
import com.openelements.data.data.DataType;
import com.openelements.data.db.DbHandler;
import com.openelements.data.provider.ProviderHandler;
import com.openelements.data.sample.employee.Employee;
import com.openelements.data.sample.employee.EmployeeEntityMapper;
import com.openelements.data.sample.employee.EmployeeProvider;
import com.openelements.data.server.DataEndpointMetadata;
import com.openelements.data.server.DataServer;
import java.util.List;
import java.util.Set;

public class Sample {

    public static void main(String[] args) {
        final DbHandler dbHandler = new DbHandler("my-unit");
        final ProviderHandler providerHandler = new ProviderHandler(dbHandler.createRepository());
        final EmployeeProvider employeeProvider = new EmployeeProvider();
        providerHandler.add(Employee.class, employeeProvider, new EmployeeEntityMapper());
        DataServer dataServer = new DataServer(8080, getEndpoints(dbHandler));
        dataServer.start();
    }

    private static Set<DataEndpointMetadata<?>> getEndpoints(DbHandler dbHandler) {
        final DataAttribute<Employee, String> firstNameAttribute = new DataAttribute<>("firstName",
                AttributeType.STRING,
                Employee::getFirstName);
        final DataAttribute<Employee, String> lastNameAttribute = new DataAttribute<>("lastName", AttributeType.STRING,
                Employee::getLastName);
        final DataType<Employee> employeeType = new DataType<>("employee", Employee.class,
                List.of(firstNameAttribute, lastNameAttribute));
        final DataProvider<Employee> dataProvider = dbHandler.createDataProvider(Employee.class);
        final DataEndpointMetadata<Employee> endpoint = new DataEndpointMetadata<>("employees", employeeType,
                dataProvider);
        return Set.of(endpoint);
    }
}
