package org.ultragore.everything.modules.WorldEditExtender.exceptions;

public class EditBlocksLimitException extends RuntimeException {
	public int blocksLimit;
	public int blocksSelected;
	
	public EditBlocksLimitException(int blocksLimit, int blocksSelected) {
		this.blocksLimit = blocksLimit;
		this.blocksSelected = blocksSelected;
	}
	
}