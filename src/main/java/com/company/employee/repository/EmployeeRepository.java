package com.company.employee.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.company.employee.model.Employee;
import java.util.List;

public interface EmployeeRepository extends MongoRepository<Employee, String> {

    List<Employee> findByDepartment(String department);

    List<Employee> findByRole(String role);
}
