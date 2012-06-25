package com.percussion.pso.importer.impl;

import net.sf.saxon.om.NamespaceConstant;
import java.util.*;
import javax.xml.xpath.*;
import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.percussion.pso.importer.IImportItemRefProducer;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class UrlIteratorProducer implements
IImportItemRefProducer<String> {

	private static final Log log = LogFactory.getLog(UrlIteratorProducer.class);
	
	private String folderName;	
	private DocumentBuilderFactory domFactory;
	private Iterator<File> fileIterator;
	
	public String getFolderName() {
		return folderName;
	}

	public void setFolderName(String folderName) {
		this.folderName = folderName;
	}
	
	public Iterator<String> produce() throws Exception {
		
		 List<String> urlList = new ArrayList<String>();
		 
		 domFactory = DocumentBuilderFactory.newInstance();		 
		 domFactory.setNamespaceAware(true);
		 DocumentBuilder builder = domFactory.newDocumentBuilder();
		 
		 File f = new File(folderName);
		 File files[] = f.listFiles();
		 
		 if (files != null) {
			this.fileIterator = Arrays.asList(files).iterator();
		 }		

		 while(fileIterator.hasNext()){
			 File file = fileIterator.next();
			 log.debug("XML file: " + file.toString());
			 Document doc = builder.parse(file);
			
			 System.setProperty("javax.xml.xpath.XPathFactory:"+NamespaceConstant.OBJECT_MODEL_SAXON, "net.sf.saxon.xpath.XPathFactoryImpl");
			 XPathFactory factory = XPathFactory.newInstance(NamespaceConstant.OBJECT_MODEL_SAXON);
			 
			//XPathFactory factory = new net.sf.saxon.xpath.XPathFactoryImpl();
			 XPath xpath = factory.newXPath();
			 
			 XPathExpression expr = xpath.compile("//row/OriginalUrl/text()");			 
			 Object result = expr.evaluate(doc, XPathConstants.NODESET);
			 
			 NodeList nodes = (NodeList) result;
			 for (int i = 0; i < nodes.getLength(); i++) {
				 String url = nodes.item(i).getNodeValue();
				 urlList.add(url);
			 }
		 }
		return urlList.iterator();
	}

}
