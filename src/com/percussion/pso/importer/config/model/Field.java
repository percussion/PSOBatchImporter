package com.percussion.pso.importer.config.model;

import javax.xml.bind.annotation.XmlAttribute;

public class Field implements Comparable<Field> {
	private String name;
	private String type;
	public String getName() {
		return name;
	}
	@XmlAttribute
	public void setName(String name) {
		this.name = name;
	}
	@XmlAttribute
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public int compareTo(Field o) {
		return this.getName().compareTo(o.getName());
	}
	
}
