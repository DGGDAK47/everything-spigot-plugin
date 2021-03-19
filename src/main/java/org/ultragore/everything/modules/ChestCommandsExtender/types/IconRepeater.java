package org.ultragore.everything.modules.ChestCommandsExtender.types;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class IconRepeater {
	
	private NativeIcon iconSample;
	private byte xFrom;
	private byte xTo;
	private byte yFrom;
	private byte yTo;
	
	public IconRepeater(Map<String, Object> map) {
		this.iconSample = new NativeIcon(map);
		
		String[] rangeX = ((String) map.get("RANGE-X")).split("-");
		String[] rangeY = ((String) map.get("RANGE-Y")).split("-");
		
		xFrom = Byte.parseByte(rangeX[0]);
		xTo = Byte.parseByte(rangeX[1]);
		
		yFrom = Byte.parseByte(rangeY[0]);
		yTo = Byte.parseByte(rangeY[1]);
	}
	
	public List<NativeIcon> createIcons() {
		List<NativeIcon> toReturn = new ArrayList();
		
		NativeIcon icon;
		for(int x = xFrom; x <= xTo; x++) {
			for(int y = yFrom; y <= yTo; y++) {
				icon = iconSample.clone();
				icon.positionX = x;
				icon.positionY = y;
				
				toReturn.add(icon);
			}
		}
		
		return toReturn;
	}
}
