package com.opstree.microservice.salary.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table("employee_salary")
public class Employee implements Serializable {

    // Partition key
    @PrimaryKeyColumn(name = "id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private String id;

    // Clustering column
    @PrimaryKeyColumn(name = "process_date", ordinal = 1, type = PrimaryKeyType.CLUSTERED)
    private LocalDate processDate;    // maps to Cassandra `date`

    @Column("name")
    private String name;

    @Column("salary")
    private BigDecimal salary;        // maps to Cassandra `decimal`

    @Column("status")
    private String status;
}

