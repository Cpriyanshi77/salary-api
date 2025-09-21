package com.opstree.microservice.salary.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;

class EmployeeTest {

    @Test
    void testEmployeeGettersAndSetters() {
        // Create a sample Employee object
        Employee employee = new Employee();
        String id = "123";
        String name = "John Doe";
        BigDecimal salary = new BigDecimal("5000.00");
        LocalDate processDate = LocalDate.parse("2023-07-17");
        String status = "Active";

        // Set values using setters
        employee.setId(id);
        employee.setName(name);
        employee.setSalary(salary);
        employee.setProcessDate(processDate);
        employee.setStatus(status);

        // Check if the values are correctly set using getters
        assertEquals(id, employee.getId());
        assertEquals(name, employee.getName());
        assertEquals(salary, employee.getSalary());
        assertEquals(processDate, employee.getProcessDate());
        assertEquals(status, employee.getStatus());
    }
}

