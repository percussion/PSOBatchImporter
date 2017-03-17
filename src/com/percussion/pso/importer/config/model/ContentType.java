package com.percussion.pso.importer.config.model;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import com.percussion.pso.importer.model.Guid;

public class ContentType implements Comparable<ContentType>{
	private Guid id;
	private String description;
	private String name;
	private List<Field> fields;
	private List<ChildGroup> childGroups;
	
	@XmlElement
	public Guid getId() {
		return id;
	}
	public void setId(Guid id) {
		this.id = id;
	}
	public void setId(long id) {
		this.id = new Guid(id);
	}
	@XmlElement
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	@XmlAttribute
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@XmlElementWrapper(name="fields")
	@XmlElement
	public List<Field> getFields() {
		return fields;
	}
	public void setFields(List<Field> fields) {
		this.fields = fields;
	}
	@XmlElementWrapper(name="childGroups")
	@XmlElement(name="childGroup")
	public List<ChildGroup> getChildGroups() {
		return childGroups;
	}
	public void setChildGroups(List<ChildGroup> childGroups) {
		this.childGroups = childGroups;
	}
	public int compareTo(ContentType type) {
		return this.getName().compareTo(type.getName());
	}
}
