package com.eidiko.portal.exception.biometric;

import com.eidiko.portal.helper.biometric.ConstantValues;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.sql.SQLException;
import java.time.DateTimeException;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@RestControllerAdvice
public class GlobalExceptionHandlerBiometric {
	
	@ExceptionHandler({MethodArgumentNotValidException.class})
    public ProblemDetail handleInvalidArgument(MethodArgumentNotValidException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        List<String> errorList = new ArrayList<>();
        errorList = ex.getBindingResult().getAllErrors().stream()
                .map(error -> error.getDefaultMessage())
                .collect(Collectors.toList());
        problemDetail.setProperty("message",String.join(", ",errorList));
        problemDetail.setStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setType(URI.create("/error"));

        return problemDetail;
    }
	
	 @ExceptionHandler({EmployeeNotFoundException.class})
	    public ProblemDetail handleUserNotFoundException(EmployeeNotFoundException ex) {
	        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
	        problemDetail.setProperty("message" , ConstantValues.EMPLOYEE_NOT_FOUND);
	        problemDetail.setStatus(HttpStatus.NOT_FOUND);
	       // problemDetail.setType(URI.create(ConstantValues.ERROR));

	        return problemDetail;
	    }
	
	@ExceptionHandler({FileDataAlreadyExistsException.class})
    public ProblemDetail handleCustomException(FileDataAlreadyExistsException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setProperty("message" , ConstantValues.REPORT);
        problemDetail.setStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setType(URI.create("/error"));
        return problemDetail;
    }
	
    @ExceptionHandler({NullPointerException.class})
    public ProblemDetail handleNullPointerException(NullPointerException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.NOT_FOUND.ordinal());
        problemDetail.setProperty("message" , ConstantValues.NO_CONTENT);
        problemDetail.setStatus(HttpStatus.NOT_FOUND);
        //problemDetail.setType(URI.create("/error"));
        return problemDetail;
    }
    
    @ExceptionHandler(SQLException.class)
    public ProblemDetail handleSqlException(NullPointerException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        problemDetail.setProperty("message" , ex.getMessage());
        problemDetail.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        //problemDetail.setType(URI.create("/error"));
        return problemDetail;
    }
    
    @ExceptionHandler(BadRequestException.class)
    public ProblemDetail handleBadRequestException(BadRequestException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setProperty("message" , ex.getMessage());
        problemDetail.setStatus(HttpStatus.BAD_REQUEST);
        //problemDetail.setType(URI.create("/error"));
        return problemDetail;
    }
    @ExceptionHandler(DateTimeParseException.class)
    public ProblemDetail handleDateTimeParseException(DateTimeParseException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setProperty("message" , ex.getMessage());
        problemDetail.setStatus(HttpStatus.BAD_REQUEST);
        //problemDetail.setType(URI.create("/error"));
        return problemDetail;
    }
    
    @ExceptionHandler(DateTimeException.class)
    public ProblemDetail handleDateTimeException(DateTimeException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setProperty("message" , ex.getMessage());
        problemDetail.setStatus(HttpStatus.CREATED);
        //problemDetail.setType(URI.create("/error"));
        return problemDetail;
    }
    
}
