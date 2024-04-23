package com.epam.library.exceptions;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.context.request.WebRequest;

import feign.FeignException;

@RestControllerAdvice
public class RestControllerExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	public ExceptionResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, WebRequest req) {
		List<String> errors = new ArrayList<>();
		ex.getAllErrors().forEach(err -> errors.add(err.getDefaultMessage()));
		return new ExceptionResponse(new Date().toString(), HttpStatus.BAD_REQUEST.name(), errors.toString(),
				req.getDescription(false));
	}

	@ExceptionHandler(HttpClientErrorException.class)
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	public ExceptionResponse handleHttpClientErrorException(HttpClientErrorException ex, WebRequest req) {
		return new ExceptionResponse(new Date().toString(), HttpStatus.BAD_REQUEST.name(), ex.getMessage(),
				req.getDescription(false));
	}
	
	@ExceptionHandler(FeignException.class)
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	public ExceptionResponse handleHttpClientErrorException(FeignException ex, WebRequest req) {
		return new ExceptionResponse(new Date().toString(), HttpStatus.BAD_REQUEST.name(), ex.getMessage(),
				req.getDescription(false));
	}
	
	@ExceptionHandler(LibraryException.class)
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	public ExceptionResponse handleBookException(LibraryException ex, WebRequest req) {
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
