package com.example.rqchallenge.service;

import com.example.rqchallenge.exception.EmployeeNotFoundException;
import com.example.rqchallenge.exception.GenericException;
import com.example.rqchallenge.model.*;
import com.example.rqchallenge.feignclient.EmployeeFeignClient;
import com.example.rqchallenge.util.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class EmployeeService {

    @Autowired
    public EmployeeFeignClient employeeFeignClient;

    public List<Employee> getAllEmployees() throws EmployeeNotFoundException {
        List<Employee> allEmployee = fetchAllEmployees();
        log.info("getAllEmployees = {}", allEmployee);
        if (allEmployee.isEmpty()) {
            //handle custom exception to return status code as 404
            throw new EmployeeNotFoundException(Constants.STATUS_404);
        }
        return allEmployee;
    }

    public List<Employee> fetchAllEmployees() {
        EmployeeList allEmployee = employeeFeignClient.getAllEmployees();
        log.info("fetchAllEmployees = {}", allEmployee);
        return allEmployee != null ? allEmployee.getData() : new ArrayList<>();
    }


    public List<Employee> getEmployeesByNameSearch(String searchString) throws EmployeeNotFoundException {
        List<Employee> allEmployee = fetchAllEmployees();
        List<Employee> filteredList = allEmployee.stream().filter(e -> e.getEmployee_name().contains(searchString)).collect(Collectors.toList());
        log.info("filteredList = {}", filteredList);

        if (filteredList.isEmpty()) {
            //handle custom exception to return status code as 404
            throw new EmployeeNotFoundException(Constants.STATUS_404);
        }
        return filteredList;
    }


    public Employee getEmployeeById(String id) throws EmployeeNotFoundException {

        Employee employee = employeeFeignClient.getEmployeeById(id);
        log.info("Employee found = {}", employee);
        if (employee == null) {
            //handle custom exception to return status code as 404
            throw new EmployeeNotFoundException(Constants.STATUS_404);
        }
        return employee;
    }

    public Integer getHighestSalaryOfEmployees() throws EmployeeNotFoundException {
        List<Employee> allEmployee = fetchAllEmployees();
        Optional<Employee> highestSalary = allEmployee.stream().max(Comparator.comparingInt(Employee::getEmployee_salary));
        log.info("highestSalary = {}", highestSalary);

        if (highestSalary.isEmpty()) {
            //handle custom exception to return status code as 404
            throw new EmployeeNotFoundException(Constants.STATUS_404);
        }
        return highestSalary.get().getEmployee_salary();
    }

    public List<String> getTopTenHighestEarningEmployeeNames() throws EmployeeNotFoundException {
        List<Employee> allEmployee = fetchAllEmployees();

        List<String> highestSalaryEmployeeNames = allEmployee.stream()
                .sorted(Collections.reverseOrder(Comparator.comparingInt(Employee::getEmployee_salary)))
                .map(Employee::getEmployee_name).limit(10).collect(Collectors.toList());

        log.info("highestSalaryEmployeeNames = {}", highestSalaryEmployeeNames);

        if (highestSalaryEmployeeNames.isEmpty()) {
            //handle custom exception to return status code as 404
            throw new EmployeeNotFoundException(Constants.STATUS_404);
        }
        return highestSalaryEmployeeNames;
    }


    public EmployeeResponse createEmployee(EmployeeInput employeeInput) throws GenericException {
        EmployeeResponse newEmployee = employeeFeignClient.createEmployee(employeeInput);
        if (newEmployee != null) {
            if (newEmployee.getStatus().equals("success")) {
                log.info("Newly created employee = {}", newEmployee.getData());
                return newEmployee;
            } else {
                throw new GenericException(new ResponseData(newEmployee.getStatus(), newEmployee.getMessage(), null));
            }
        } else {
            throw new GenericException(new ResponseData(Constants.STATUS_UNKNOWN, Constants.MSG_UNKNOWN, null));
        }
    }

    public String deleteEmployeeById(String id) throws GenericException {
        ResponseData resp = employeeFeignClient.deleteEmployeeById(id);
        if (resp != null && resp.getStatus().equals(Constants.SUCCESS)) {
            return resp.getMessage();
        } else {
            throw new GenericException(resp);
        }
    }
}
