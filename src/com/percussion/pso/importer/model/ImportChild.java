package com.percussion.pso.importer.model;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

public class ImportChild {
	private String name;

	private List<ImportChildEntry> entries;

	@XmlAttribute
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<ImportChildEntry> getEntries() {
		return entries;
	}
	@XmlElement(name="entry")
	public void setEntries(List<ImportChildEntry> entries) {
		this.entries = entries;
	}
	
	
	
	
}
