package org.ultragore.everything.modules.WorldEditExtender.exceptions;

public class EditBlocksLimitException extends RuntimeException {
	public int blocksLimit;
	public int blocksAffected;
	
	public EditBlocksLimitException(int blocksLimit, int blocksAffected) {
		this.blocksLimit = blocksLimit;
		this.blocksAffected = blocksAffected;
	}
	
}