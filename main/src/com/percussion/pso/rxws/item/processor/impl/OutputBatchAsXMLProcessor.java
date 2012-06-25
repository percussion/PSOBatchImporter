/**
 * 
 */
package com.percussion.pso.rxws.item.processor.impl;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.percussion.pso.importer.model.ImportBatch;
import com.percussion.pso.importer.model.ImportItem;
import com.percussion.pso.rxws.item.processor.RxWsItemProcessor;


public class OutputBatchAsXMLProcessor implements RxWsItemProcessor {

	private String outputFolder;
	private String errorFolder;
	
	/**
	 * The log instance to use for this class, never <code>null</code>.
	 */
	private final Log log = LogFactory.getLog(OutputBatchAsXMLProcessor.class);


	public ImportBatch  processItems(ImportBatch  items) 
	throws Exception {
		log.debug("Outputting as XML");

		boolean hasError = false;
		if (items.getErrors() != null && items.getErrors().size() > 0) {
			hasError = true;
		} else {
			for (ImportItem item : items) {
				if (item.getErrors() != null && item.getErrors().size()>0)  {
					hasError=true;
					break;
				}
			}
		}
		String path="";
		if (errorFolder != null) {
		 path = hasError ? errorFolder : outputFolder; 
		} else {
			path = outputFolder;
		}
		try {
			
				JAXBContext ctx  =  JAXBContext.newInstance(ImportBatch.class);
				Marshaller marshaller = ctx.createMarshaller();
				marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true); 
				Calendar cal = Calendar.getInstance();
			    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HHmmssZ");
			    
				File outputFile = new File(path + "/" +"importbatch_"+sdf.format(cal.getTime()) + ".xml");
			
				marshaller.marshal(items,outputFile); 
				
		} catch (JAXBException e) {
			items.addError("Cannot marshall batch" , e);
		}

		

		return items;
	}

	public String getOutputFolder() {
		return outputFolder;
	}


	public void setOutputFolder(String outputFolder) {
		this.outputFolder = outputFolder;
	}


	public String getErrorFolder() {
		return errorFolder;
	}


	public void setErrorFolder(String errorFolder) {
		this.errorFolder = errorFolder;
	}
}