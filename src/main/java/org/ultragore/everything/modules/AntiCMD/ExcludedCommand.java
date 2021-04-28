package org.ultragore.everything.modules.AntiCMD;

import java.util.Map;

public class ExcludedCommand {
	public String command;
	public String cancelMessage = null;
	public String issueInsteadCommand = null;
	
	public ExcludedCommand(Map<String, String> map) {
		command = map.get("cmd");
		
		if(map.containsKey("cancel_message")) {
			cancelMessage = map.get("cancel_message");
		}
		
		if(map.containsKey("issue_instead_command")) {
			issueInsteadCommand = map.get("issue_instead_command");
		}
	}
}
