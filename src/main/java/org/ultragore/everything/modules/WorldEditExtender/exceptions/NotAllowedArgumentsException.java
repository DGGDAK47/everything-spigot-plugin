package org.ultragore.everything.modules.WorldEditExtender.exceptions;

public class NotAllowedArgumentsException extends RuntimeException {
	public String arguments;
	
	public NotAllowedArgumentsException(String arguments) {
		this.arguments = arguments;
	}
}
