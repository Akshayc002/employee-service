package com.company.employee.service;

import com.company.employee.model.Employee;
import java.util.List;

public interface EmployeeService {

    List<Employee> getAllEmployees();

    Employee getEmployeeById(String id);
}