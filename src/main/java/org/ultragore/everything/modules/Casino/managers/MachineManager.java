package org.ultragore.everything.modules.Casino.managers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.ultragore.everything.modules.Casino.types.Machine;

public class MachineManager {
	private List<Machine> machines = new <Machine>ArrayList();
	
	public MachineManager(Map<String, Map> machinesMap) {
		for(String key: machinesMap.keySet()) {
			machines.add(new Machine(machinesMap.get(key)));
		}
	}
	
	public Machine getMachineByLever(Location loc) {
		for(Machine m: machines) {
			if(m.leverLocation.equals(loc)) {
				return m;
			}
		}
		return null;
	}
}
