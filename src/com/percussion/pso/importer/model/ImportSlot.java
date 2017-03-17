package com.percussion.pso.importer.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;


public class ImportSlot {
	private String template;
	
	private String slot;
	private boolean isRef;
	private List<ImportRelationship> relationship;
	@XmlAttribute
	public  String getTemplate() {
		return template;
	}
	public void setTemplate(String template) {
		this.template = template;
	}
	@XmlAttribute(name="slot")
	public String getSlot() {
		return slot;
	}
	public void setSlot(String slot) {
		this.slot = slot;
	}
	
	@XmlElement(name="relationship")
	public List<ImportRelationship> getRelationship() {
		if (this.relationship == null) this.relationship = new ArrayList<ImportRelationship>();
		if(isRef) {
			for(ImportRelationship rel : this.relationship) {
				rel.setRef(true);
			}
		}
		return relationship;
	}
	public void setRelationship(List<ImportRelationship> relationship) {
		this.relationship = relationship;
	}
	@XmlTransient
	public boolean isRef() {
		return isRef;
	}
	public void setRef(boolean isRef) {
		this.isRef = isRef;
	}
}