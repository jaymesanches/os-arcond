package br.com.js.base.exception;

import lombok.Getter;

public class BusinessException extends RuntimeException{

	private static final long serialVersionUID = 1L;
	
	@Getter
	private String message;

	public BusinessException(String message) {
		this.message = message;
	}
}
