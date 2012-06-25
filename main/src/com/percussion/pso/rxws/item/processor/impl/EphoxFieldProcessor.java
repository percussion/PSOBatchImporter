/**
 * 
 */
package com.percussion.pso.rxws.item.processor.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.tidy.Configuration;
import org.w3c.tidy.Tidy;

import com.percussion.pso.importer.model.ImportBatch;
import com.percussion.pso.importer.model.ImportItem;
import com.percussion.pso.rxws.item.processor.RxWsItemProcessor;

public class EphoxFieldProcessor implements RxWsItemProcessor {

	private List<String> fields;
	private String encoding = "UTF-8";
	/**
	 * The log instance to use for this class, never <code>null</code>.
	 */

	private final Log log = LogFactory.getLog(EphoxFieldProcessor.class);

	public ImportBatch processItems(ImportBatch items)
	throws Exception {

		Tidy tidy = new Tidy();
		tidy.setXmlOut(true);
		tidy.setCharEncoding(Configuration.UTF8);
		
		tidy.setFixComments(true);
		tidy.setMakeClean(true);
		tidy.setXHTML(true);
		for (ImportItem item : items) {
			try {

				for (String field : fields) {
					if (item.getFields() != null
							&& item.getFields().containsKey(field) && item.getFields().get(field).getStringValue()!=null) {
						Object objValue = item.getFields().get(field).getStringValue();

						log.debug("type is " + objValue.getClass());

						OutputStream out = new ByteArrayOutputStream();
						String value = null;
						if (objValue instanceof String) {
							log.debug("Is a String");
							value = (String) objValue;

							log.debug("value is " + value);
							// Find better way of removing these bad namespace elements
							value = value.replaceAll("x:num", "");
							InputStream is = new ByteArrayInputStream(value.getBytes(encoding));

							org.w3c.dom.Document doc = tidy.parseDOM(is,out);

							Element div =null;

							NodeList divList = doc.getElementsByTagName("div");
							for(int i=0; i<divList.getLength(); i++) {
								Element currentDiv = (Element)divList.item(i);
								String className=currentDiv.getAttribute("class");
								if (className.equals("rxbodyfield")) {
									div = currentDiv;
									break;
								}

							}

							if (div == null) {
								NodeList bodyList = doc.getElementsByTagName("body");
								Node body = null;
								if (bodyList.getLength() > 0)
									body = bodyList.item(0);
								Element bodyElement = (Element) body;

								div = doc.createElement("div");
								Attr cssClass = doc.createAttribute("class");
								cssClass.setValue("rxbodyfield");

								div.setAttributeNode(cssClass);

								if (bodyList.getLength() > 0)
									body = bodyList.item(0);

								if (bodyElement != null) {
									for (int i = 0; i < bodyElement.getChildNodes()
									.getLength(); i++) {
										Node bodyItem = bodyElement.getChildNodes()
										.item(i);
										div.appendChild(bodyItem);
									}
								}
							}

							log.debug("test" +div);
							List<Object> newel = new ArrayList<Object>();
							newel.add(div);
							
							item.getFields().get(field).setBody(div);



						} else {
							log.debug("body element is null");
							//item.getFields().put(field, "body");
						}



					}

				}
			} catch (Exception e) {
				item.addError("Error processing ephox Field ", e);
			}
		}
		return items;
	}

	public List<String> getFields() {
		return fields;
	}

	public void setFields(List<String> fields) {
		this.fields = fields;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

}