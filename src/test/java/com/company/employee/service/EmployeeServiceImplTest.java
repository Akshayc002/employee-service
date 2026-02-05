package com.company.employee.service;

import com.company.employee.exception.EmployeeNotFoundException;
import com.company.employee.model.Employee;
import com.company.employee.repository.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    @Test
    void getAllEmployees_returnsFromRepository() {
        Employee e = new Employee();
        e.setId("1");
        e.setName("Alice");

        when(employeeRepository.findAll()).thenReturn(List.of(e));

        List<Employee> result = employeeService.getAllEmployees();

        assertEquals(1, result.size());
        assertEquals("1", result.get(0).getId());
        verify(employeeRepository, times(1)).findAll();
    }

    @Test
    void getEmployeeById_whenPresent_returnsEmployee() {
        Employee e = new Employee();
        e.setId("123");
        e.setName("Charlie");

        when(employeeRepository.findById("123")).thenReturn(Optional.of(e));

        Employee result = employeeService.getEmployeeById("123");

        assertEquals("123", result.getId());
        assertEquals("Charlie", result.getName());
        verify(employeeRepository, times(1)).findById("123");
    }

    @Test
    void getEmployeeById_whenMissing_throwsEmployeeNotFoundException() {
        when(employeeRepository.findById("missing")).thenReturn(Optional.empty());

        EmployeeNotFoundException ex = assertThrows(
                EmployeeNotFoundException.class,
                () -> employeeService.getEmployeeById("missing")
        );

        assertTrue(ex.getMessage().contains("missing"));
        verify(employeeRepository, times(1)).findById("missing");
    }
}