package com.example.rqchallenge.service;

import com.example.rqchallenge.exception.EmployeeNotFoundException;
import com.example.rqchallenge.exception.GenericException;
import com.example.rqchallenge.model.*;
import com.example.rqchallenge.repository.EmployeeFeignClient;
import com.example.rqchallenge.util.Constants;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class EmployeeServiceTest {

    private final static List<Employee> employees = new ArrayList<>();
    @Mock
    private RestTemplate restTemplate;
    @InjectMocks
    private EmployeeService employeeService;
    @Mock
    public EmployeeFeignClient employeeFeignClient;

    @BeforeAll
    static void loadInitialData() {
        employees.add(Employee.builder().id(1).employee_name("Snehal").employee_age(32).employee_salary(25000).build());
        employees.add(Employee.builder().id(2).employee_name("Arun").employee_age(25).employee_salary(12000).build());
        employees.add(Employee.builder().id(3).employee_name("Sonal").employee_age(28).employee_salary(10000).build());
        employees.add(Employee.builder().id(4).employee_name("Ajay").employee_age(45).employee_salary(10005).build());
        employees.add(Employee.builder().id(5).employee_name("Madhukar").employee_age(35).employee_salary(100006).build());
        employees.add(Employee.builder().id(6).employee_name("Narayan").employee_age(50).employee_salary(10000).build());
        employees.add(Employee.builder().id(7).employee_name("Prakash").employee_age(39).employee_salary(10001).build());
        employees.add(Employee.builder().id(8).employee_name("Sanjay").employee_age(42).employee_salary(10002).build());
        employees.add(Employee.builder().id(9).employee_name("Radha").employee_age(65).employee_salary(10003).build());
        employees.add(Employee.builder().id(10).employee_name("Sarika").employee_age(41).employee_salary(10004).build());
        employees.add(Employee.builder().id(11).employee_name("Ajay Kumar").employee_age(45).employee_salary(200000).build());
    }

    @BeforeEach
    void setMockitoResults() {
        EmployeeList employeeList = new EmployeeList();
        employeeList.setData(employees);
        Mockito.when(employeeFeignClient.getAllEmployees()).thenReturn(employeeList);
        Mockito.when(employeeFeignClient.getEmployeeById("1")).thenReturn(employees.get(0));
        Mockito.when(employeeFeignClient.getEmployeeById("50")).thenReturn(null);
    }

    @Test
    public void testGetAllEmployees() throws EmployeeNotFoundException {

        List<Employee> allEmployeesList = employeeService.getAllEmployees();
        assertEquals(allEmployeesList.size(), employees.size());
        assertEquals(allEmployeesList, employees);
    }

    @Test
    public void testGetAllEmployees_NotFound() {
        Mockito.when(employeeFeignClient.getAllEmployees()).thenReturn(null);
        Exception exception = assertThrows(EmployeeNotFoundException.class, () -> {
            employeeService.getAllEmployees();
        });
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(Constants.STATUS_404));
    }

    @Test
    public void testGetEmployeesByNameSearch() throws EmployeeNotFoundException {
        List<Employee> allEmployeesList = employeeService.getEmployeesByNameSearch("Ajay");
        assertEquals(allEmployeesList.size(), 2);
        assertEquals(allEmployeesList.get(1).getEmployee_name(), "Ajay Kumar");
    }

    @Test
    public void testGetEmployeesByNameSearch_NotFound() {
        Exception exception = assertThrows(EmployeeNotFoundException.class, () -> {
            employeeService.getEmployeesByNameSearch("Ganesh");
        });
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(Constants.STATUS_404));
    }

    @Test
    public void testGetEmployeeById() throws EmployeeNotFoundException {
        Employee emp = employeeService.getEmployeeById("1");
        assertEquals(emp, employees.get(0));
    }

    @Test
    public void testGetEmployeeById_NotFound() {
        Exception exception = assertThrows(EmployeeNotFoundException.class, () -> {
            employeeService.getEmployeeById("50");
        });
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(Constants.STATUS_404));
    }

    @Test
    public void testGetHighestSalaryOfEmployees() throws EmployeeNotFoundException {
        Integer highestSalary = employeeService.getHighestSalaryOfEmployees();
        assertEquals(highestSalary, employees.get(10).getEmployee_salary());
    }

    @Test
    public void testGetHighestSalaryOfEmployees_NotFound() {
        List<Employee> emptyEmployeeList = new ArrayList<>();
        EmployeeList elist = new EmployeeList();
        elist.setData(emptyEmployeeList);
        Mockito.when(employeeFeignClient.getAllEmployees()).thenReturn(elist);
        Exception exception = assertThrows(EmployeeNotFoundException.class, () -> {
            employeeService.getHighestSalaryOfEmployees();
        });
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(Constants.STATUS_404));
    }

    @Test
    public void testGetTopTenHighestEarningEmployeeNames() throws EmployeeNotFoundException {
        List<String> highlyPaidEmployees = employeeService.getTopTenHighestEarningEmployeeNames();
        assertEquals(highlyPaidEmployees.size(), 10);
        assertEquals(highlyPaidEmployees, getHighlyPaidEmployeeForTest());
    }

    private static List<String> getHighlyPaidEmployeeForTest() {
        return employees.stream()
                .sorted(Collections.reverseOrder(Comparator.comparingInt(Employee::getEmployee_salary)))
                .map(Employee::getEmployee_name).limit(10).collect(Collectors.toList());
    }

    @Test
    public void testGetTopTenHighestEarningEmployeeNames_NotFound() {
        List<Employee> emptyEmployeeList = new ArrayList<>();
        EmployeeList elist = new EmployeeList();
        elist.setData(emptyEmployeeList);
        Mockito.when(employeeFeignClient.getAllEmployees()).thenReturn(elist);
        Exception exception = assertThrows(EmployeeNotFoundException.class, () -> {
            employeeService.getTopTenHighestEarningEmployeeNames();
        });
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(Constants.STATUS_404));
    }

    @Test
    public void testCreateEmployee() throws GenericException {
        EmployeeInput newInput = EmployeeInput.builder().name("Shinde").salary("970000").age("45").build();
        EmployeeInput newEmp = EmployeeInput.builder().id("12").name("Shinde").salary("970000").age("45").build();
        EmployeeResponse expectedResp = new EmployeeResponse();
        expectedResp.setData(newEmp);
        expectedResp.setStatus(Constants.SUCCESS);
        expectedResp.setMessage("Successfully! Record has been added.");

        Mockito.when(employeeFeignClient.createEmployee(newInput)).thenReturn(expectedResp);

        EmployeeResponse createResp = employeeService.createEmployee(newInput);
        assertEquals(createResp.getStatus(), expectedResp.getStatus());
        assertEquals(createResp.getData(), expectedResp.getData());
        assertEquals(createResp.getMessage(), expectedResp.getMessage());
    }

    @Test
    public void testCreateEmployee_Fail() {
        EmployeeInput newInput = EmployeeInput.builder().name("Shinde").salary("970000").age("45").build();
        EmployeeResponse expectedResp = new EmployeeResponse();
        expectedResp.setData(null);
        expectedResp.setStatus(Constants.FAIL);
        expectedResp.setMessage(Constants.MSG_UNKNOWN);

        Mockito.when(employeeFeignClient.createEmployee(newInput)).thenReturn(expectedResp);

        Exception exception = assertThrows(GenericException.class, () -> {
            employeeService.createEmployee(newInput);
        });
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(Constants.MSG_UNKNOWN));
    }

    @Test
    public void testCreateEmployee_Null() {
        EmployeeInput newInput = EmployeeInput.builder().name("Shinde").salary("970000").age("45").build();

        Mockito.when(employeeFeignClient.createEmployee(newInput)).thenReturn(null);

        Exception exception = assertThrows(GenericException.class, () -> {
            employeeService.createEmployee(newInput);
        });
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(Constants.MSG_UNKNOWN));
    }

    @Test
    public void testDeleteEmployee() throws GenericException {
        ResponseData expectedResp = new ResponseData(Constants.SUCCESS, "Successfully! Record has been deleted", "9");
        Mockito.when(employeeFeignClient.deleteEmployeeById("9")).thenReturn(expectedResp);
        String actualResp = employeeService.deleteEmployeeById("9");
        assertEquals(expectedResp.getMessage(), actualResp);
    }

    @Test
    public void testDeleteEmployee_Fail() {
        ResponseData expectedResp = new ResponseData(Constants.FAIL, Constants.MSG_UNKNOWN, null);
        Mockito.when(employeeFeignClient.deleteEmployeeById("9")).thenReturn(expectedResp);
        Exception exception = assertThrows(GenericException.class, () -> {
            employeeService.deleteEmployeeById("9");
        });
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(Constants.MSG_UNKNOWN));
    }

    @Test
    public void testDeleteEmployee_Null() {
        Mockito.when(employeeFeignClient.deleteEmployeeById("9")).thenReturn(null);
        Exception exception = assertThrows(GenericException.class, () -> {
            employeeService.deleteEmployeeById("9");
        });
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains("Null Response from Server"));
    }


}
