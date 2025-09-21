package com.opstree.microservice.salary.contollers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.opstree.microservice.salary.model.Employee;
import com.opstree.microservice.salary.repository.EmployeeRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller providing basic endpoints for the salary microservice.
 *
 * - GET  /api/v1/salary/search/all         -> list all employees
 * - GET  /api/v1/salary/search/{id}        -> list rows for an employee id
 * - POST /api/v1/salary/create             -> create a new employee salary record
 *
 * This controller performs safe conversions of String dates -> LocalDate and
 * numeric values -> BigDecimal so they match the Cassandra table mapping.
 */
@RestController
@RequestMapping("/api/v1/salary")
@CrossOrigin(origins = "*")
public class SpringDataController {

    private static final Logger logger = LoggerFactory.getLogger(SpringDataController.class);

    private final EmployeeRepository repository;

    public SpringDataController(EmployeeRepository repository) {
        this.repository = repository;
    }

    /**
     * Return all rows from employee_salary table.
     */
    @GetMapping("/search/all")
    public ResponseEntity<List<Employee>> getAll() {
        List<Employee> list = StreamSupport.stream(repository.findAll().spliterator(), false)
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    /**
     * Return all rows for a given partition key (employee id).
     */
    @GetMapping("/search/{id}")
    public ResponseEntity<List<Employee>> getById(@PathVariable("id") String id) {
        List<Employee> rows = repository.findByIdIs(id);
        return ResponseEntity.ok(rows);
    }

    /**
     * Create a new employee_salary record.
     *
     * Accepts JSON body with fields:
     * {
     *   "id": "EMP001",
     *   "processDate": "2025-09-21",   // ISO yyyy-MM-dd
     *   "name": "Alice Kumar",
     *   "salary": 75000.50,
     *   "status": "active"
     * }
     *
     * Note: we accept processDate as a String and parse it to LocalDate to avoid
     * compile/runtime type mismatches.
     */
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody CreateEmployeeRequest req) {
        if (req == null) {
            return ResponseEntity.badRequest().body("request body is missing");
        }
        if (req.getId() == null || req.getId().isBlank()) {
            return ResponseEntity.badRequest().body("id is required");
        }

        // parse processDate
        LocalDate pd = null;
        String pdStr = req.getProcessDate();
        if (pdStr != null && !pdStr.isBlank()) {
            try {
                pd = LocalDate.parse(pdStr); // expects yyyy-MM-dd
            } catch (DateTimeParseException ex) {
                logger.warn("Invalid processDate provided: {}", pdStr, ex);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("processDate must be in yyyy-MM-dd format");
            }
        }

        // normalize salary to BigDecimal
        BigDecimal salaryBd = null;
        if (req.getSalary() != null) {
            // Jackson may already map numeric JSON to BigDecimal; accept as-is
            salaryBd = req.getSalary();
        }

        // build entity (Employee has @Builder)
        Employee emp = Employee.builder()
                .id(req.getId())
                .processDate(pd)
                .name(req.getName())
                .salary(salaryBd)
                .status(req.getStatus())
                .build();

        try {
            Employee saved = repository.save(emp);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (Exception e) {
            logger.error("Failed to save employee record for id={}", req.getId(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to save record: " + e.getMessage());
        }
    }

    /**
     * Simple DTO for create endpoint. Keep as inner static class to avoid adding another file.
     * Jackson will bind JSON numeric values to BigDecimal if possible.
     */
    public static class CreateEmployeeRequest {
        private String id;
        private String processDate;
        private String name;
        private BigDecimal salary;
        private String status;

        // getters & setters (Jackson needs them)
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public String getProcessDate() { return processDate; }
        public void setProcessDate(String processDate) { this.processDate = processDate; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public BigDecimal getSalary() { return salary; }
        public void setSalary(BigDecimal salary) { this.salary = salary; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }
}
