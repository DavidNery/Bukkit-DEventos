package me.dery.deventos.exceptions;

public class EventoException extends Exception {

	private static final long serialVersionUID = 1L;
	
	private String error;

	public EventoException(String error) {
		this.error = error;
	}
	
	public String getError() {
		return error;
	}

}
