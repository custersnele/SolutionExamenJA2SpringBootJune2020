package be.pxl.ja2.examen.rest;

import be.pxl.ja2.examen.util.exception.BezoekersAppException;
import be.pxl.ja2.examen.util.exception.OngeldigTijdstipException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class BezoekersAppExceptionHandler {

	@ExceptionHandler(value = { BezoekersAppException.class, OngeldigTijdstipException.class})
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	public ErrorMessage resourceNotFoundException(RuntimeException ex) {
		return new ErrorMessage(ex.getMessage());
	}
}

