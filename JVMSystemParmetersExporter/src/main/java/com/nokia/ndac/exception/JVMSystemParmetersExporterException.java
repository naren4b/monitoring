package com.nokia.ndac.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class JVMSystemParmetersExporterException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public JVMSystemParmetersExporterException(String message) {
        super("JVMSystemParmetersExporter Exception :-" + message + "'.");
    }
}
