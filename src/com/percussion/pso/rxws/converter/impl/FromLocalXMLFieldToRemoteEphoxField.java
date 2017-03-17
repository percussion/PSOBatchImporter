package com.percussion.pso.rxws.converter.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xerces.dom.ElementNSImpl;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.tidy.Configuration;
import org.w3c.tidy.Tidy;

import com.percussion.pso.importer.model.ImportItem;
import com.percussion.pso.rxws.converter.FromLocalFieldToRemoteField;
import com.percussion.pso.rxws.item.RxWsContext;
import com.percussion.webservices.content.PSField;
import com.percussion.webservices.content.PSFieldValue;

public class FromLocalXMLFieldToRemoteEphoxField implements FromLocalFieldToRemoteField {
	/**
	 * The log instance to use for this class, never <code>null</code>.
	 */
	private static final Log log = LogFactory.getLog(FromLocalXMLFieldToRemoteEphoxField.class);

	private String encoding = "UTF-8";

	public PSField convert(String fieldName, String contentTypeName, Object source, PSField initial, RxWsContext context) {
		
		if (source == null) { 
			log.debug("source field data is null");
		}  else {
			log.debug("Converting ephox field "  +source.getClass());
		}
		if ( source instanceof String) {
			log.debug("processing xml from String");
			Tidy tidy = new Tidy();
			tidy.setXmlOut(true);
			tidy.setCharEncoding(Configuration.UTF8);
			tidy.setFixComments(true);
			tidy.setMakeClean(true);
			tidy.setXHTML(true);

			OutputStream out = new ByteArrayOutputStream();
			String value = (String) source;

			log.debug("value is " + value);
			// Find better way of removing these bad namespace elements
			value = value.replaceAll("x:num", "");
			try {
				InputStream is = new ByteArrayInputStream(value.getBytes(encoding));

				org.w3c.dom.Document doc = tidy.parseDOM(is,out);
				NodeList bodyList = doc.getElementsByTagName("body");
				Node body = null;

				Element div = doc.createElement("div");
				Attr cssClass = doc.createAttribute("class");
				cssClass.setValue("rxbodyfield");
				div.setAttributeNode(cssClass);

				if (bodyList.getLength() > 0)
					body = bodyList.item(0);
				Element bodyElement = (Element) body;
				if (bodyElement != null) {
					for (int i = 0; i < bodyElement.getChildNodes()
					.getLength(); i++) {
						Node bodyItem = bodyElement.getChildNodes()
						.item(i);
						div.appendChild(bodyItem);
					}

					log.debug("test" +div);
					List<Object> newel = new ArrayList<Object>();
					newel.add(div);

					source = div;

				}
			} catch (UnsupportedEncodingException e) {
				log.debug("UnsupportedEncoding",e);
			}
		}
		
		PSFieldValue fieldValue = new PSFieldValue();
		if (source instanceof Element) {
			log.debug("processing xml from Element");
			String resultString="";
			try {
				Source source2 = new DOMSource((Element)source);
				StringWriter stringWriter = new StringWriter();
				Result result = new StreamResult(stringWriter);
				TransformerFactory factory = TransformerFactory.newInstance();
				Transformer transformer = factory.newTransformer();
				transformer.transform(source2, result);
				resultString =  stringWriter.getBuffer().toString();
				//log.debug("Streamed xml string = "+resultString.substring(38));
				
			} catch (TransformerConfigurationException e) {
				log.debug(e);
			} catch (TransformerException e) {
				log.debug(e);
			}

			log.debug("Setting field Value");
			fieldValue.setRawData(resultString.substring(38));
			initial.setPSFieldValue(new PSFieldValue[] { fieldValue });
		} 
		


		log.debug("returning");
		return initial;
	}



}
