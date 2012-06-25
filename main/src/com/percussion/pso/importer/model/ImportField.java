
package com.percussion.pso.importer.model;

import java.util.Collection;
import java.util.List;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.w3c.dom.Element;

@XmlRootElement
public class ImportField {
 
 private String name;
 private String stringValue;

 private boolean xml=false;
 private Object body;
 private List<ImportFieldValue> values;
	


public ImportField() {

  }
  
  public ImportField(String name, String value) {
	  setName(name);
	  setValue(value);
  }
 
  @XmlAttribute()
  public String getName() {
	return name;
}



public void setName(String name) {
	this.name = name;
}

@XmlTransient
public Object getValue() {
	if (values != null) {
		return values;
	} else {
		return body;
	}
}


@SuppressWarnings("unchecked")
public void setValue(Object value) {

	if(value instanceof String || value instanceof Number){
		setStringValue(value.toString());
	} else if (value instanceof List) {
		setValues((List<ImportFieldValue>)value);
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

@XmlElementWrapper(name="values")
@XmlElement(name="value")
public List<ImportFieldValue> getValues() {
return values;
}

public void setValues(List<ImportFieldValue> values) {
	this.values = values;
}

@XmlAttribute()
public String getType() {
	return (body==null)? "null" : body.getClass().getName();
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