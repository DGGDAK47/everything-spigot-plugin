package org.ultragore.everything.modules.WorldEditExtender.exceptions;

public class InvalidWECommandSyntaxException extends RuntimeException{
	
	private int atArgumentIndex;
	
	public InvalidWECommandSyntaxException(int atArgumentIndex) {
		this.atArgumentIndex = atArgumentIndex;
	}
	
	public int getNearIndex() {
		return this.atArgumentIndex;
	}
}
