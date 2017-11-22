package com.naren.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ApplicationExporterException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ApplicationExporterException(String message) {
		super("Application Exception :-" + message + "'.");
	}
}
