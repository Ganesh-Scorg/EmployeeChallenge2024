package com.example.rqchallenge.service;

import com.example.rqchallenge.exception.EmployeeNotFoundException;
import com.example.rqchallenge.exception.GenericException;
import com.example.rqchallenge.model.*;
import com.example.rqchallenge.repository.EmployeeFeignClient;
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

        List<Employee> all_employee = fetchAllEmployees();

        if (all_employee.isEmpty()) {
            throw new EmployeeNotFoundException(Constants.STATUS_404);
        }
        return all_employee;
    }

    public List<Employee> fetchAllEmployees() {
        EmployeeList all_employee = employeeFeignClient.getAllEmployees();
        log.info("fetchAllEmployees = {}", all_employee);
        return all_employee != null? all_employee.getData() : new ArrayList<>();
    }


    public List<Employee> getEmployeesByNameSearch(String searchString) throws EmployeeNotFoundException {
        List<Employee> all_employee = fetchAllEmployees();
        List<Employee> filteredList = all_employee.stream().filter(e -> e.getEmployee_name().contains(searchString)).collect(Collectors.toList());
        log.info("filteredList = {}", filteredList);

        if (filteredList.isEmpty()) {
            //handle custom exception to return status code as 404
            throw new EmployeeNotFoundException(Constants.STATUS_404);
        }
        return filteredList;
    }


    public Employee getEmployeeById(String id) throws EmployeeNotFoundException {

        Employee employee = employeeFeignClient.getEmployeeById(id);

        if (employee == null) {
            //handle custom exception to return status code as 404
            throw new EmployeeNotFoundException(Constants.STATUS_404);
        }
        log.info("Employee found = {}", employee);
        return employee;
    }

    public Integer getHighestSalaryOfEmployees() throws EmployeeNotFoundException {
        List<Employee> all_employee = fetchAllEmployees();
        Optional<Employee> highestSalary = all_employee.stream().max(Comparator.comparingInt(Employee::getEmployee_salary));
        log.info("highestSalary = {}", highestSalary);

        if (highestSalary.isEmpty()) {
            //handle custom exception to return status code as 404
            throw new EmployeeNotFoundException(Constants.STATUS_404);
        }
        return highestSalary.get().getEmployee_salary();
    }

    public List<String> getTopTenHighestEarningEmployeeNames() throws EmployeeNotFoundException {
        List<Employee> all_employee = fetchAllEmployees();

        List<String> highestSalaryEmployeeNames = all_employee.stream()
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
        EmployeeResponse newemployee = employeeFeignClient.createEmployee(employeeInput);
        if (newemployee != null) {
            if (newemployee.getStatus().equals("success")) {
                log.info("Newly created employee = {}", newemployee.getData());
                return newemployee;
            } else {
                throw new GenericException(new ResponseData(newemployee.getStatus(), newemployee.getMessage(), null));
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
