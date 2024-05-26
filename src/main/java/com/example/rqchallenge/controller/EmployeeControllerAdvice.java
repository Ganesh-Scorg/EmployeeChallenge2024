package com.example.rqchallenge.controller;

import com.example.rqchallenge.exception.EmployeeNotFoundException;
import com.example.rqchallenge.exception.ErrorResponse;
import com.example.rqchallenge.exception.GenericException;
import com.example.rqchallenge.model.ResponseData;
import com.example.rqchallenge.util.Constants;
import feign.FeignException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class EmployeeControllerAdvice {

    @ExceptionHandler(EmployeeNotFoundException.class)
    public ResponseEntity<ResponseData> handleEmployeeNotFound() {
        return new ResponseEntity<>(new ResponseData(Constants.STATUS_429, Constants.MSG_429, null), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<?> handleUpstreamSystemError(FeignException e, HttpServletResponse response) {
        System.out.println("Error Message = "+e.getMessage());

         if(e.getMessage().contains(Constants.STATUS_429))
            return new ResponseEntity<>(new ResponseData(Constants.STATUS_429, Constants.MSG_429, null), HttpStatus.TOO_MANY_REQUESTS);
        else if(e.getMessage().contains(Constants.STATUS_404))
            return new ResponseEntity<>(new ResponseData(Constants.STATUS_404, Constants.MSG_404, null), HttpStatus.NOT_FOUND);
        else if(e.getMessage().contains(Constants.STATUS_405))
            return new ResponseEntity<>(new ResponseData(Constants.STATUS_405, Constants.MSG_405, null), HttpStatus.METHOD_NOT_ALLOWED);
        else
            return new ResponseEntity<>(new ResponseData(Constants.STATUS_UNKNOWN, e.getMessage(), null),  HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(GenericException.class)
    public ResponseEntity<ResponseData> handleGenericException(GenericException e) {
        return new ResponseEntity<>(e.getResponseData(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        List<String> details = new ArrayList<>();
        for (ObjectError error : e.getBindingResult().getAllErrors()) {
            details.add(error.getDefaultMessage());
        }
        ErrorResponse error = new ErrorResponse("Validation Failed", details);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

}
