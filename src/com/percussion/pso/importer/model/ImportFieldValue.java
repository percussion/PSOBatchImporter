package com.percussion.pso.importer.model;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.w3c.dom.Element;

public class ImportFieldValue {
	 private String stringValue;

	 private boolean xml=false;
	 private Object body;
	 

	 @XmlTransient
	 public Object getValue() {
	 	return body;
	 }


	 public void setValue(Object value) {
	 	if(value instanceof String || value instanceof Number){
	 		setStringValue(value.toString());
	 	} else {
	 		setBody(value);
	 	}
	 }

	 @XmlAttribute(name="value")
	 public String getStringValue() {
	 	if(xml==true) return null;
	 	return stringValue;
	 }

	 public void setStringValue(String stringValue) {
	 	xml=false;
	 	this.stringValue = stringValue;
	 	this.body = stringValue;
	 }
	 public static class ObjectFieldAdapter extends XmlAdapter<String, Object> {
	 	  
	 	@Override
	 	public String marshal(Object arg0) throws Exception {
	 		   if (arg0==null) return null;
	 			return arg0.toString();
	 	}

	 	@Override
	 	public Object unmarshal(String arg0) throws Exception {
	 		 return (Object)arg0;
	 	}
	 }

	 @XmlAnyElement(lax=true)
	 public Object getBody() {
	 	return (xml) ? body : null;
	 }

	 public void setBody(Object others) {
	 	if (others != null && others instanceof Element)	xml=true;
	 	this.body = others;
	 }

}
