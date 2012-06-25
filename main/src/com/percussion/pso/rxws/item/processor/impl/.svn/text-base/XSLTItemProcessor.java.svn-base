/**
 * 
 */
package com.percussion.pso.rxws.item.processor.impl;


import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;

import com.percussion.pso.importer.manager.ItemManager;
import com.percussion.pso.importer.model.ImportBatch;
import com.percussion.pso.rxws.item.processor.RxWsItemProcessor;

public class XSLTItemProcessor implements RxWsItemProcessor {

	private String stylesheetPath = "content/identity.xsl";
	
	ItemManager manager;
	private boolean updateLocator;
	    
	/**
	 * The log instance to use for this class, never <code>null</code>.
	 */
	private final Log log = LogFactory.getLog(XSLTItemProcessor.class);

	public ImportBatch processItems(ImportBatch items)
			throws Exception {
		log.debug("Transforming batch with stylesheet " + stylesheetPath);
		
		
		JAXBContext ctx = JAXBContext.newInstance(ImportBatch.class);

		Marshaller marshaller = null;

		marshaller = ctx.createMarshaller();

    	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(false);
		Document doc = dbf.newDocumentBuilder().newDocument();
		
		marshaller.marshal(items, doc);

		Source xsl = new StreamSource(stylesheetPath);

		Source source2 = new DOMSource(doc);
		DOMResult result = new DOMResult();
		TransformerFactory factory = TransformerFactory.newInstance();


		Transformer transformer = factory.newTransformer(xsl);
		
		transformer.transform(source2, result);

		Unmarshaller unmarshaller = ctx.createUnmarshaller();

		items = (ImportBatch) unmarshaller.unmarshal(result.getNode());

		// Need to re populate remote ids for new batch
		if(updateLocator == true){
			manager.updateLocators(items);
		}	

		return items;

	}
	
	public ItemManager getManager() {
		return manager;
	}

	public void setManager(ItemManager manager) {
		this.manager = manager;
	}

	public String getStylesheetPath() {
		return stylesheetPath;
	}

	public void setStylesheetPath(String stylesheetPath) {
		this.stylesheetPath = stylesheetPath;
	}
	

	public boolean isUpdateLocator() {
		return updateLocator;
	}

	public void setUpdateLocator(boolean updateLocator) {
		this.updateLocator = updateLocator;
	}


}