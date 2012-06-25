package com.percussion.pso.importer.model;

import java.util.HashMap;


public class FieldMap extends HashMap<String,ImportField>{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ImportField put(ImportField field) {
		return super.put(field.getName(), field);

	}
	public ImportField put(String fieldName, Object value) {
		ImportField field = new ImportField();
		field.setName(fieldName);
		field.setValue(value);
		return super.put(field.getName(), field);

	}
	
}
