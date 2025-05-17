package com.openelements.data.sample.employee;

import com.openelements.data.data.AttributeType;
import com.openelements.data.data.DataAttribute;
import com.openelements.data.data.DataType;
import com.openelements.data.data.I18nString;
import com.openelements.data.db.FileEntity;
import com.openelements.data.db.I18nStringEntity;
import java.util.List;

public class EmployeeDataTypeFactory {

    public static DataType<Employee> createDataType() {
        final DataAttribute<Employee, String> firstNameAttribute = new DataAttribute<>("firstName",
                I18nString.of("First name"),
                I18nString.of("The first name"),
                AttributeType.STRING,
                Employee::getFirstName);
        final DataAttribute<Employee, String> lastNameAttribute = new DataAttribute<>("lastName",
                I18nString.of("Last name"),
                I18nString.of("The last name"),
                AttributeType.STRING,
                Employee::getLastName);
        final DataAttribute<Employee, I18nStringEntity> roleAttribute = new DataAttribute<>("role",
                I18nString.of("Role"),
                I18nString.of("The role"),
                AttributeType.I18N_STRING,
                Employee::getRole);
        final DataAttribute<Employee, FileEntity> imageAttribute = new DataAttribute<>("image",
                I18nString.of("Image"),
                I18nString.of("The profile image of the employee"),
                AttributeType.FILE,
                Employee::getProfilePicture);
        return new DataType<>("employee", "An employee", Employee.class,
                List.of(firstNameAttribute, lastNameAttribute, roleAttribute, imageAttribute));
    }
}
