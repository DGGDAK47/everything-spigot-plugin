package org.ultragore.everything.modules.WorldEditExtender.types;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.ultragore.everything.modules.WorldEditExtender.exceptions.InvalidWECommandSyntaxException;

public class WECommand {
	
	private String command;
	private List<String> arguments = new ArrayList<String>();
	private List<String> options = new ArrayList<String>();
	private Map<String, String> parameters = new Hashtable<String, String>();
	
	
	public WECommand(String message, List<String> parameterKeys) throws InvalidWECommandSyntaxException {
		String[] sMessage = message.split(" ");
		
		if(parameterKeys == null) {
			parameterKeys = new ArrayList<String>();
		}
		
		command = sMessage[0];
		
		if(sMessage.length > 1) {
			for(int i = 1; i < sMessage.length; i++) {
				
				if(i == 1 && sMessage[i].startsWith("-") && !parameterKeys.contains(sMessage[i].substring(1))) {
					if(sMessage[i].length() == 1) throw new InvalidWECommandSyntaxException();
					options.addAll(Arrays.asList(sMessage[i].substring(1).split("")));
					for(String option: options) {
						if(!Character.isLetter(option.toCharArray()[0])) {
							throw new InvalidWECommandSyntaxException();
						}
					}
					continue;
				}
				
				if(sMessage[i].startsWith("-") && parameterKeys.contains(sMessage[i].substring(1))) {
					if(sMessage.length-1 == i) {
						throw new InvalidWECommandSyntaxException();
					} else {
						parameters.put(sMessage[i].substring(1), sMessage[i+1]);
						continue;						
					}
				}
				
				arguments.add(sMessage[i]);
			}
		}
	}
	

	public int allArgumentsSummary() {
		return arguments.size() + options.size() + parameters.size();
	}
	
	public void setCommand(String command) {
		this.command = command;
	}
	
	public boolean hasOption(String option) {
		return options.contains(option);
	}
	public boolean hasOptions(List<String> options) {
		return this.options.containsAll(options);
	}
	public void addOption(String option) {
		if(!options.contains(option)) {
			options.add(option);
		}
	}
	public void removeOption(String option) {
		options.remove(option);
	}
	
	public int getArgumentsAmount() {
		return arguments.size();
	}
	public String getArgument(int index) {
		return arguments.get(index);
	}
	
	public String getParameterValue(String parameter) {
		return parameters.get(parameter);
	}
}
