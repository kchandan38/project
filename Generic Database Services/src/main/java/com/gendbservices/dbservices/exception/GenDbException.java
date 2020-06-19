package com.gendbservices.dbservices.exception;

import jdk.nashorn.internal.objects.annotations.Setter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public class GenDbException extends Exception{

    HttpStatus httpStatusCode;
    public GenDbException(String message){
        super(message);
    }

    public GenDbException(String message, Throwable errorCode) {
        super(message, errorCode);
    }
}
