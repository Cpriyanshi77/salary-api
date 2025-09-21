package com.opstree.microservice.salary.repository;

import java.util.List;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import com.opstree.microservice.salary.model.Employee;

@Repository
public interface EmployeeRepository extends CassandraRepository<Employee, String> {

    /**
     * Return all rows for the given partition key (id).
     * Method name intentionally does NOT clash with CrudRepository.findById(ID).
     */
    List<Employee> findByIdIs(String id);
}

