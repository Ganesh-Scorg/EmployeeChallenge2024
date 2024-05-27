package com.example.rqchallenge.controller;

import com.example.rqchallenge.exception.EmployeeNotFoundException;
import com.example.rqchallenge.exception.GenericException;
import com.example.rqchallenge.model.Employee;
import com.example.rqchallenge.model.EmployeeInput;
import com.example.rqchallenge.model.EmployeeResponse;
import com.example.rqchallenge.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@RestController
public class EmployeeController implements IEmployeeController {

    @Autowired
    public EmployeeService employeeService;

    @Override
    public ResponseEntity<List<Employee>> getAllEmployees() throws IOException, EmployeeNotFoundException {
        List<Employee> employeelist = employeeService.getAllEmployees();
        return new ResponseEntity<>(employeelist, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<Employee>> getEmployeesByNameSearch(String searchString) throws EmployeeNotFoundException {
        List<Employee> filteredList = employeeService.getEmployeesByNameSearch(searchString);
        return new ResponseEntity<>(filteredList, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Employee> getEmployeeById(String id) throws EmployeeNotFoundException {
        Employee employee = employeeService.getEmployeeById(id);
        return new ResponseEntity<>(employee, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Integer> getHighestSalaryOfEmployees() throws EmployeeNotFoundException {
        return new ResponseEntity<>(employeeService.getHighestSalaryOfEmployees(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames() throws EmployeeNotFoundException {
        List<String> highestEarningEmployees = employeeService.getTopTenHighestEarningEmployeeNames();
        return new ResponseEntity<>(highestEarningEmployees, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> createEmployee(@RequestBody @Valid EmployeeInput employeeInput) throws GenericException {
        EmployeeResponse newEmployee = employeeService.createEmployee(employeeInput);
        return new ResponseEntity<>(newEmployee.getMessage(), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<String> deleteEmployeeById(String id) throws GenericException {
        return new ResponseEntity<>(employeeService.deleteEmployeeById(id), HttpStatus.OK);
    }
}
