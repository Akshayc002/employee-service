package com.company.employee.controller;

import com.company.employee.exception.EmployeeNotFoundException;
import com.company.employee.exception.GlobalExceptionHandler;
import com.company.employee.model.Employee;
import com.company.employee.service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = EmployeeController.class)
@Import(GlobalExceptionHandler.class)
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeService employeeService;

    @Test
    void getAllEmployees_returnsList() throws Exception {
        Employee e1 = new Employee();
        e1.setId("1");
        e1.setName("Alice");
        e1.setEmail("alice@company.com");
        e1.setDepartment("Engineering");
        e1.setRole("Developer");

        Employee e2 = new Employee();
        e2.setId("2");
        e2.setName("Bob");
        e2.setEmail("bob@company.com");
        e2.setDepartment("HR");
        e2.setRole("Recruiter");

        when(employeeService.getAllEmployees()).thenReturn(List.of(e1, e2));

        mockMvc.perform(get("/api/employees").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[0].name").value("Alice"))
                .andExpect(jsonPath("$[1].id").value("2"))
                .andExpect(jsonPath("$[1].name").value("Bob"));
    }

    @Test
    void getEmployeeById_returnsEmployee() throws Exception {
        Employee e = new Employee();
        e.setId("123");
        e.setName("Charlie");
        e.setEmail("charlie@company.com");
        e.setDepartment("Finance");
        e.setRole("Analyst");

        when(employeeService.getEmployeeById("123")).thenReturn(e);

        mockMvc.perform(get("/api/employees/123").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("123"))
                .andExpect(jsonPath("$.name").value("Charlie"))
                .andExpect(jsonPath("$.email").value("charlie@company.com"))
                .andExpect(jsonPath("$.department").value("Finance"))
                .andExpect(jsonPath("$.role").value("Analyst"));
    }

    @Test
    void getEmployeeById_whenNotFound_returns404WithErrorBody() throws Exception {
        when(employeeService.getEmployeeById("missing"))
                .thenThrow(new EmployeeNotFoundException("Employee not found with id: missing"));

        mockMvc.perform(get("/api/employees/missing").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Employee Not Found"))
                .andExpect(jsonPath("$.message").value("Employee not found with id: missing"))
                .andExpect(jsonPath("$.timestamp").exists());
    }
}