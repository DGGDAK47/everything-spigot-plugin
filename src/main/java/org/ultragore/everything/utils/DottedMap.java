package org.ultragore.everything.utils;

import java.util.List;
import java.util.Map;

public class DottedMap {
	private Map<String, Object> map;
	
	public DottedMap(Map<String, Object> map) {
		this.map = map;
	}
	
	public Map<String, Object> getMap() {
		return this.map;
	}
	
	public void setMap(Map<String, Object> map) {
		this.map = map;
	}
	
	/**
	 * @param path - dotted path to field in map.
	 * 		  Example: "my.property.field"
	 * @return object from path
	 */
	public Object getObject(String path) {
		String[] splitedPath = path.split("\\.");
		
		if(splitedPath.length == 1) {
			return map.get(path);
		} else if (path.length() == 0) {
			return null;
		}
		
		Map<String, Object> extractedPart = this.map;
		for(int i = 0; i < splitedPath.length; i++) {
			if(extractedPart == null) {
				return null;
			}
			if(i < splitedPath.length - 1) {
				extractedPart = (Map<String, Object>) extractedPart.get(splitedPath[i]);
			} else {
				return extractedPart.get(splitedPath[i]);
			}
		}
		
		return null;
	}
	
	public String getString(String path) {
		return (String) getObject(path);
	}
	
	public Integer getInteger(String path) {
		return (Integer) getObject(path);
	}
	
	public Float getFloat(String path) {
		return (Float) getObject(path);
	}
	
	public Double getDouble(String path) {
		return (Double) getObject(path);
	}
	
	public Boolean getBoolean(String path) {
		return (Boolean) getObject(path);
	}
	
	public Map getMap(String path) {
		return (Map) getObject(path);
	}
	
	public List<Object> getList(String path) {
		return (List<Object>) getObject(path);
	}
	
	public boolean hasPath(String path) {
		return getObject(path) != null;
	}
}