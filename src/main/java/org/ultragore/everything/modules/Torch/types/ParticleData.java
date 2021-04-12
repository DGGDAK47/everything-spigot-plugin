package org.ultragore.everything.modules.Torch.types;

import org.bukkit.Particle;

public class ParticleData {
	public Particle particle;
	public double offsetX;
	public double offsetY;
	public double offsetZ;
	public int count;
	
	public ParticleData(String data) {
		// data -> "particle,offsetX,offsetY,offsetZ,count"
		String[] sData = data.split(",");
		particle = Particle.valueOf(sData[0]);
		offsetX = Double.parseDouble(sData[1]);
		offsetY = Double.parseDouble(sData[2]);
		offsetZ = Double.parseDouble(sData[3]);
		count = Integer.parseInt(sData[4]);
	}
}