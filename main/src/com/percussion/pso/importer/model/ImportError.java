package com.percussion.pso.importer.model;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;
@XmlRootElement(name="error")
public class ImportError {
	private String message;
	
	public ImportError() {
		
	}
	public ImportError(String message) {
		setMessage(message);
	}
	public ImportError(String message, Exception e) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		PrintWriter pw = new PrintWriter(stream, true);
		pw.println(message);
		pw.println(e.getMessage());
		e.printStackTrace(pw);
		setMessage(stream.toString());
	}
	@XmlValue
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	
}
