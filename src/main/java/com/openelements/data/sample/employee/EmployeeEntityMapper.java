package com.openelements.data.sample.employee;

import com.openelements.data.db.EntityMapper;

public class EmployeeEntityMapper implements EntityMapper<Employee> {

    @Override
    public Employee updateEntity(Employee updated, Employee toUpdate) {
        toUpdate.setFirstName(updated.getFirstName());
        toUpdate.setLastName(updated.getLastName());
        toUpdate.setGitHubUsername(updated.getGitHubUsername());
        toUpdate.setRole(updated.getRole());
        return toUpdate;
    }
}
