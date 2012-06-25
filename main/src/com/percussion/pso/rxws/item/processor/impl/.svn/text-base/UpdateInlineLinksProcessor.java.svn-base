/**
 * 
 */
package com.percussion.pso.rxws.item.processor.impl;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.DOMBuilder;
import org.jdom.output.DOMOutputter;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

import com.percussion.pso.importer.manager.ItemManager;
import com.percussion.pso.importer.model.ImportBatch;
import com.percussion.pso.importer.model.ImportField;
import com.percussion.pso.importer.model.ImportItem;
import com.percussion.pso.rxws.item.processor.RxWsItemProcessor;

public class UpdateInlineLinksProcessor implements RxWsItemProcessor {

	/**
	 * The log instance to use for this class, never <code>null</code>.
	 */

	private HashMap<String,String> inlineTypeMap = new HashMap<String,String>();

	private static String INLINE_LINK_TYPE="rxhyperlink";
	private static String INLINE_TEMPLATE_TYPE="rxvariant";
	private static String INLINE_IMAGE_TYPE="rximage";
	private static String INLINE_LINK_SLOT="103";
	private static String INLINE_TEMPLATE_SLOT="105";
	private static String INLINE_IMAGE_SLOT="104";

	private boolean updateItems = false;

	private final Log log = LogFactory.getLog(UpdateInlineLinksProcessor.class);
	ItemManager manager;
	//Linktype,destination type, template name
	private Map<String,Map<String,String>> inlineLinkTemplateTypeMap = new HashMap<String,Map<String,String>>();

	public UpdateInlineLinksProcessor() {
		inlineTypeMap.put(INLINE_LINK_TYPE, INLINE_LINK_SLOT);
		inlineTypeMap.put(INLINE_IMAGE_TYPE, INLINE_IMAGE_SLOT);
		inlineTypeMap.put(INLINE_TEMPLATE_TYPE,  INLINE_TEMPLATE_SLOT);
	}

	public Map<String,Map<String, String>> getInlineLinkTemplateTypeMap() {
		return inlineLinkTemplateTypeMap;
	}
	public void setInlineLinkTemplateTypeMap(
			Map<String,Map< String,String>> inlineLinkTemplateTypeMap) {
		this.inlineLinkTemplateTypeMap = inlineLinkTemplateTypeMap;
	}
	public ImportBatch  processItems(ImportBatch  items) 
	throws Exception {

		Map<String,ImportItem> objectIdMap = new HashMap<String,ImportItem>();
		for(ImportItem i : items.getItems()) {
			if(i.getId() != null && i.getObjectId() != null) {
				if (i.getLocator().getLocalId().contains(",")) {
					log.debug("found item with multiple local ids");
					for( String id : i.getLocator().getLocalId().split(",")) {

						objectIdMap.put(id, i);
					}
				} else {
					objectIdMap.put(i.getLocator().getLocalId(), i);
				}
			}
		}

		log.debug("Checking "+items.size()+" items in batch");

		Map<String,String> templateNameMap =manager.getTemplateNameMap();

		log.debug("Processing Inline Relationships");
		ImportBatch updateItems = new ImportBatch();
		for(ImportItem item : items) {

			try {
				boolean changed=false;
				for(String fieldName : item.getFields().keySet()) {
					ImportField field = item.getFields().get(fieldName);
					//log.debug("checking field"+fieldName);
					/*
					if (field.getValue() != null) {
						log.debug("field type is "+field.getValue().getClass());

					}	else {
						log.debug("field value is null");
					}*/
					if (field.getValue() != null && field.getValue() instanceof org.w3c.dom.Element) {
						//log.debug("field is an element "+ (field.getValue() instanceof org.w3c.dom.Element));

						org.w3c.dom.Element rootNode = (org.w3c.dom.Element)field.getBody();
						//log.debug("field is a body"+fieldName);
						DOMBuilder builder = new DOMBuilder();
						Element body = builder.build(rootNode);
						XPath path = XPath.newInstance("//*[@inlinetype]");
						List list = path.selectNodes(body);
						for (Iterator iter = list.iterator(); iter.hasNext();) {

							Element inlineElement = (Element) iter.next();
						
							try {

								if (inlineElement.getAttribute("inlinetype") != null) {
									log.debug("Found inline link to process");
									changed=true;

									String objectId= inlineElement.getAttributeValue("objectId");    			
									String variantName = inlineElement.getAttributeValue("templatename");
									String inlineType = inlineElement.getAttributeValue("inlinetype");
									String folderPath = inlineElement.getAttributeValue("folderPath");
							
									// TODO : add folderPath
									if (variantName!=null) {
									//	log.debug("Found variantName = "+variantName);
									}
									if (inlineType!=null) {
									//	log.debug("Found inlineType = "+inlineType);
									} else {
									//	log.debug("Inline type not specified");
									}
									
									int folderId = 0;
									String sysFolderId = null;
									
									if(folderPath != null && folderPath.equals("")){
									//	log.debug("Found folderPath = "+folderPath);
										try{
											folderId = manager.getContext().getHelper().findPathId(folderPath);
										}catch(Exception ex){
											log.error("Cannot find the folder Id for the folder path:" + folderPath);
										}
									}
									
									if(folderId != 0){
										sysFolderId = Integer.toString(folderId);
									}
									
									String templateId = null;
									String slotId = null;
									String dependentId = null;



									ImportItem relatedItem = objectIdMap.get(objectId);
									if (relatedItem != null) {
										String relatedType = relatedItem.getType();
										dependentId = String.valueOf(relatedItem.getId());
										log.debug("dependent id:" +objectId);
										
										if (variantName==null && relatedType != null && inlineLinkTemplateTypeMap.containsKey(inlineType)) {
											Map<String,String> templateMap = inlineLinkTemplateTypeMap.get(inlineType);
											variantName = templateMap.get(relatedType);
											log.debug("Setting variant name for inline element from map in spring config based upon item type" + variantName + " for type " + relatedType);
										} 

										templateId = templateNameMap.get(variantName);
										if (templateId == null ) {
											item.addError("UpdateInlineLinksProcessorCannot find template id for name" + variantName + "ObjectId:"+objectId + "relatedItem:" +relatedItem);
											log.error("UpdateInlineLinksProcessor Cannot find template id for name" + variantName);
										}
										
										slotId = inlineTypeMap.get(inlineType);

										if (slotId == null ){
											item.addError("UpdateInlineLinksProcessor Cannot find slot id");
											log.error("UpdateInlineLinksProcessor Cannot find slot id");
										}

										if (relatedItem.getId() <= 0 ) {										
											item.addError("UpdateInlineLinksProcessor Cannot find dependent id");
											log.error("UpdateInlineLinksProcessor Cannot find dependent item");
										}


									}
									if( templateId != null && slotId != null && relatedItem.getId() > 0 ) {


										inlineElement.setAttribute("sys_dependentvariantid",templateId);
										inlineElement.setAttribute("inlinetype",inlineType);
										inlineElement.setAttribute("rxinlineslot",slotId);
										inlineElement.setAttribute("sys_dependentid",dependentId);
										inlineElement.setAttribute("contenteditable","false");
										if(sysFolderId != null){
											inlineElement.setAttribute("sys_folderid", sysFolderId);
										}

									} else {
										inlineElement.removeAttribute("inlinetype");
										log.debug("Cannot find objectId "+objectId+ " in batch, ignoring");	
									}
									inlineElement.removeAttribute("objectId");
									//slotname obsolete left in to handle old systems
									inlineElement.removeAttribute("slotname");
									inlineElement.removeAttribute("templatename");
									inlineElement.removeAttribute("folderPath");

									DOMOutputter domOutputter = new DOMOutputter();
									body.detach();
									Document doc = new Document(body);

									org.w3c.dom.Document w3cdoc= domOutputter.output(doc);

									field.setBody(w3cdoc.getDocumentElement());

								}
							} catch(Exception e) {
								ByteArrayOutputStream output = new ByteArrayOutputStream();
								XMLOutputter outputter = new XMLOutputter();
								outputter.output(inlineElement, output);

								log.error("Error updating inline link fragment for item:" + output,e);
								item.addError("Error updating inline link fragment for item:"+output,e);
							}
						}

					}

				}
				if (changed==true) {
					updateItems.add(item);
				} 
			} catch(Exception e) {
				log.error("Error updating inline links for item",e);
				item.addError("Error updating inline links for item",e);
			}
		}

		log.debug("updating " +updateItems.size() +" Items");

		manager.updateRx(updateItems);
		log.debug("finished updating inline relationships");
		return items;
	}
	public ItemManager getManager() {
		return manager;
	}

	public void setManager(ItemManager manager) {
		this.manager = manager;
	}

	public boolean isUpdateItems() {
		return updateItems;
	}
	public void setUpdateItems(boolean updateItems) {
		this.updateItems = updateItems;
	}

}


