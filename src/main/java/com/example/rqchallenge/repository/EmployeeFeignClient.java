package com.example.rqchallenge.repository;

import com.example.rqchallenge.model.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(value = "employee-client", url = "${employee.api.url}")
public interface EmployeeFeignClient {
    @GetMapping("/employees")
    EmployeeList getAllEmployees();

    @GetMapping("/employees/{id}")
    Employee getEmployeeById(@PathVariable("id") String id);

    @PostMapping("/create")
    EmployeeResponse createEmployee(@RequestBody EmployeeInput employeeInput);

    @DeleteMapping("/delete/{id}")
    ResponseData deleteEmployeeById(@PathVariable String id);
}
