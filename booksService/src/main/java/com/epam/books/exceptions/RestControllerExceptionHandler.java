package com.epam.books.exceptions;

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
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;


@RestControllerAdvice
public class RestControllerExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	public ExceptionResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, WebRequest req) {
		List<String> errors = new ArrayList<>();
		ex.getAllErrors().forEach(err -> errors.add(err.getDefaultMessage()));
		return new ExceptionResponse(new Date().toString(), HttpStatus.NOT_FOUND.name(), errors.toString(),
				req.getDescription(false));
	}

	@ExceptionHandler(BookException.class)
	@ResponseStatus(value = HttpStatus.OK)
	public String handleBookException(BookException ex, WebRequest req) {
		return ex.getMessage();
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	public String handleDataIntegrityViolationException(DataIntegrityViolationException ex, WebRequest req) {
		return ex.getMessage();
	}
	
	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	public ExceptionResponse handleMethodArgumentMismatchException(MethodArgumentTypeMismatchException ex,
			WebRequest req) {
		return new ExceptionResponse(new Date().toString(), HttpStatus.BAD_REQUEST.name(), ex.getMessage(),
				req.getDescription(false));
	}

	@ExceptionHandler(RuntimeException.class)
	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
	public ExceptionResponse handleRuntimeException(RuntimeException ex, WebRequest req) {
		return new ExceptionResponse(new Date().toString(), HttpStatus.INTERNAL_SERVER_ERROR.name(), ex.getMessage(),
				req.getDescription(false));
	}
}
