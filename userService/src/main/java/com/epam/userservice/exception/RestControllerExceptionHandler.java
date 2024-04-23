package com.epam.userservice.exception;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class RestControllerExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ExceptionResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, WebRequest req) {
		List<String> errors = new ArrayList<>();
		ex.getAllErrors().forEach(err -> errors.add(err.getDefaultMessage()));
		log.error(ex.getMessage());
		return new ExceptionResponse(new Date().toString(), HttpStatus.BAD_REQUEST.name(), errors.toString(),
				req.getDescription(false));
	}

	@ExceptionHandler(UserException.class)
	@ResponseStatus(HttpStatus.OK)
	public String handleBookException(UserException ex, WebRequest req) {
		return ex.getMessage();
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	@ResponseStatus(HttpStatus.OK)
	public ExceptionResponse handleDataIntegrityException(DataIntegrityViolationException ex, WebRequest req) {
		ExceptionResponse exceptionResponse = new ExceptionResponse(new Date().toString(),
				HttpStatus.INTERNAL_SERVER_ERROR.name(), ex.getMessage(), req.getDescription(false));
		log.error(ex.getMessage());
		return exceptionResponse;
	}
	
	@ExceptionHandler(RuntimeException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ExceptionResponse handleRuntimeException(RuntimeException ex, WebRequest req) {
		ExceptionResponse exceptionResponse = new ExceptionResponse(new Date().toString(),
				HttpStatus.INTERNAL_SERVER_ERROR.name(), ex.getMessage(), req.getDescription(false));
		log.error(ex.getMessage());
		return exceptionResponse;
	}
}