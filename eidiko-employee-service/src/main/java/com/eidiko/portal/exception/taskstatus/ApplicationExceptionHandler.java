package com.eidiko.portal.exception.taskstatus;

import com.eidiko.portal.helper.taskstatus.ConstantValues;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ApplicationExceptionHandler {


    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ProblemDetail handleInvalidArgument(MethodArgumentNotValidException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        List<String> errorList = new ArrayList<>();
        errorList = ex.getBindingResult().getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());
        problemDetail.setProperty(ConstantValues.MESSAGE, String.join(", ", errorList));
        problemDetail.setStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setType(URI.create(ConstantValues.ERROR));

        return problemDetail;
    }

    @ExceptionHandler({ConstraintViolationException.class})
    public ProblemDetail handleConstraintViolation(ConstraintViolationException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        List<String> errorList = new ArrayList<>();
        errorList = ex.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toList());
        problemDetail.setProperty(ConstantValues.MESSAGE, String.join(", ", errorList));
        problemDetail.setStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setType(URI.create(ConstantValues.ERROR));

        return problemDetail;
    }

    @ExceptionHandler({ReportAlreadyExistsException.class})
    public ProblemDetail handleReportAlreadyExistException(ReportAlreadyExistsException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setProperty(ConstantValues.MESSAGE, ex.getMessage());
        problemDetail.setStatus(HttpStatus.ALREADY_REPORTED);
        problemDetail.setType(URI.create(ConstantValues.ERROR));

        return problemDetail;
    }

    @ExceptionHandler({UserNotFoundException.class})
    public ProblemDetail handleUserNotFoundException(UserNotFoundException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        problemDetail.setProperty(ConstantValues.MESSAGE, ex.getMessage());
        problemDetail.setStatus(HttpStatus.NOT_FOUND);
        problemDetail.setType(URI.create(ConstantValues.ERROR));

        return problemDetail;
    }

    @ExceptionHandler({HttpMessageNotReadableException.class})
    public ProblemDetail handleMessageNotReadableException(HttpMessageNotReadableException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setProperty(ConstantValues.MESSAGE, ConstantValues.NOT_READABLE);
        problemDetail.setStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setType(URI.create(ConstantValues.ERROR));

        return problemDetail;
    }

    @ExceptionHandler({DataNotFoundException.class})
    public ProblemDetail handleDataNotFoundException(DataNotFoundException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        problemDetail.setProperty(ConstantValues.MESSAGE, ex.getMessage());
        problemDetail.setStatus(HttpStatus.NOT_FOUND);
        problemDetail.setType(URI.create(ConstantValues.ERROR));

        return problemDetail;
    }

    @ExceptionHandler({NotEligibleException.class})
    public ProblemDetail handleDataNotEligibleException(NotEligibleException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.OK);
        problemDetail.setProperty(ConstantValues.MESSAGE, ex.getMessage());
        problemDetail.setStatus(HttpStatus.OK);
        problemDetail.setType(URI.create(ConstantValues.STATUS_MESSAGE));
        return problemDetail;
    }

    @ExceptionHandler({NotNullException.class})
    public ProblemDetail nullPointerException(NotNullException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setProperty(ConstantValues.MESSAGE, ex.getMessage());
        problemDetail.setStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setType(URI.create(ConstantValues.ERROR));

        return problemDetail;
    }
}
