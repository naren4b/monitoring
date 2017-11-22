package com.nokia.ndac.bean;

public class SystemParameter {
	String name;
	String description;
	String units;
	Object value;

	public SystemParameter(String name, String description, String units) {
		super(); 
		this.name = name;
		this.description = description;
		this.units = units;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getUnits() {
		return units;
	}

	public void setUnits(String units) {
		this.units = units;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

}
