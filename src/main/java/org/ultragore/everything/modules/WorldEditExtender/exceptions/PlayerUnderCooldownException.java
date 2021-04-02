package org.ultragore.everything.modules.WorldEditExtender.exceptions;

public class PlayerUnderCooldownException extends RuntimeException {
	public int secondsLeft;
	
	public PlayerUnderCooldownException(int secondsLeft) {
		this.secondsLeft = secondsLeft;
	}
}