package com.example.rqchallenge.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class EmployeeInput {
    @Nullable
    private String id;
    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "Name should be between 2 and 50 characters")
    private String name;
    @Pattern(regexp = "^(0|[1-9][0-9]*)$", message = "Salary must be numeric")
    @Size(min = 4, max = 15, message = "Salary should be minimum 4 digit")
    private String salary;
    @Pattern(regexp = "^([1-9][0-9]*)$", message = "Age must be numeric")
    @Size(min = 1, max = 3, message = "Age can't be more than 3 digits")
    private String age;
}
