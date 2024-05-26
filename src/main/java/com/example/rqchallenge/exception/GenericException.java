package com.example.rqchallenge.exception;


import com.example.rqchallenge.model.ResponseData;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class GenericException extends Exception {
    private ResponseData responseData;

    public GenericException(ResponseData responseData) {
        super(responseData != null ? responseData.getMessage() : "Null Response from Server");
        this.responseData = responseData;
    }
}
