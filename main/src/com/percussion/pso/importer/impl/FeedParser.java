package com.percussion.pso.importer.impl;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.jexl.JexlContext;
import org.apache.commons.jexl.JexlHelper;
import org.apache.commons.jexl.Script;
import org.apache.commons.jexl.ScriptFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.tools.generic.DateTool;

import com.percussion.pso.importer.IImportItemRefConsumer;
import com.percussion.pso.importer.model.FieldMap;
import com.percussion.pso.importer.model.ImportBatch;
import com.percussion.pso.importer.model.ImportField;
import com.percussion.pso.importer.model.ImportItem;
import com.percussion.pso.importer.model.ImportRelationship;
import com.percussion.pso.importer.model.ImportSlot;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

/**
 * Given a String that represents an rss url this class will return an 
 * iterator of ImportBatch that can be used as an IImportItemProducer
 * For a single feed you can set the url property and use it directly
 * or you can link this into the ItemRefProcessor that will loop through the results
 * of an ItemRefProducer to provide the url.  In this way we can search for
 * the urls from percussion to dynamically handle multiple feeds.
 * 
 * @author stephenbolton
 *
 */
public class FeedParser implements IImportItemRefConsumer<String> {

	private String contentType = "rffEvent";
	private String path = "//Sites/EnterpriseInvestments/FeedEntries";
	private int maxItems = -1;
	private Map<String, String> fieldMap;

	private List<String> keyFields;
	private List<String> feedKeyFields;

	private boolean debug = false;
	private String url;
	private Date lastImportedDate;
	private boolean forceUpdate = false;
	private String community;
	private String feedType;
	private String feedItemSlotName;
	private String importRoot;
	private String feedItemTemplateName;

	private Map<String, String> feedFieldMap;

	/**
	 * The log instance to use for this class, never <code>null</code>.
	 */
	private static final Log log = LogFactory.getLog(FeedParser.class);

	// TODO UNIT TEST
	public Iterator<ImportBatch> produce() throws Exception {
		return produce(getUrl());
	}

	@SuppressWarnings("unchecked")
	public Iterator<ImportBatch> produce(String urlString) throws Exception {
		URL feedUrl = new URL(urlString);
		SyndFeedInput input = new SyndFeedInput();
		log.debug("Requesting feed from " + feedUrl);
		SyndFeed feed = input.build(new XmlReader(feedUrl));
		Date feedPublishDate = feed.getPublishedDate();
		log.debug("feed publish date is = " + feedPublishDate);

		List<SyndEntry> entries = feed.getEntries();
		ImportBatch items = new ImportBatch();
		log.debug("Found " + entries.size() + " items for feed " + url);
		int count = (entries.size() < maxItems || maxItems == -1) ? entries
				.size() : maxItems;
		ArrayList<ImportBatch> list = new ArrayList<ImportBatch>();

		JexlContext jc = JexlHelper.createContext();
		jc.getVars().put("$tools.date", new DateTool());
		jc.getVars().put("$feed", feed);
		jc.getVars().put("$feedLink", url);
		// used for demo to assign unique value for import. should change
		jc.getVars().put("$path", path);

		// Setup feed Item
		ImportItem feedItem;
		List<ImportRelationship> relationships = null;
		if (feedFieldMap != null) {
			feedItem = new ImportItem();
			feedItem.setKeyFields(feedKeyFields);
			feedItem.setType(feedType);
			feedItem.setPaths(new HashSet<String>(Arrays.asList(path)));
			feedItem.setFields(new FieldMap());
			feedItem.setImportRoot(importRoot);
			log.debug("set feed key fields to " + feedKeyFields.toString());

			for (String field : feedFieldMap.keySet()) {
				String jexl = feedFieldMap.get(field);
				Script s = ScriptFactory.createScript(jexl);

				// Create a context and add data

				log.debug("executing field " + field + " expression: " + jexl);
				Object o = s.execute(jc);
				if (o != null) {
					if (field.equals("sys_title")) {
						if (feedItem != null)
							feedItem.setName(o.toString());
						log.debug("Set title to " + o.toString());
					}

					ImportField importField = new ImportField();
					importField.setName(field);
					importField.setValue(o);

					
						feedItem.getFields().put(importField);
		
					log.debug("Setting feed field " + field + " to value "
							+ o.toString());

				} else {
					log.debug("Jexl Expression returned null");
				}

			}
			if (!debug) items.add(feedItem);
		
		

			
		ImportSlot slot = new ImportSlot();
		if (feedItemTemplateName != null && feedItemSlotName != null) {
			slot.setTemplate(feedItemTemplateName);
			slot.setSlot(feedItemSlotName);
			List<ImportSlot> slots = new ArrayList<ImportSlot>();
			relationships = new ArrayList<ImportRelationship>();
			slot.setRelationship(relationships);
			slots.add(slot);
			if (feedItem != null)
				feedItem.setSlots(slots);
		}
		}
		if (forceUpdate || feedPublishDate == null || lastImportedDate == null
				|| feedPublishDate.after(lastImportedDate)) {
			Date mostRecentArticle = null;
			for (int i = 0; i < count; i++) {

				ImportItem item = new ImportItem();
				item.setFieldsEnabled(true);
				item.setImportRoot(importRoot);
				
				SyndEntry entry = entries.get(i);

				Date entryPublishDate = entry.getPublishedDate();
				if (entry.getUpdatedDate() != null) {
					entryPublishDate = entry.getUpdatedDate();
				}
				log.debug("Entry publish date is = " + entryPublishDate);
				if (entryPublishDate != null
						&& (feedPublishDate == null || entryPublishDate
								.after(feedPublishDate))) {
					feedPublishDate = entryPublishDate;
				}
				// If we do not have a publish date for the feed then assume it
				// as the
				// date of the most recent article

				if (mostRecentArticle == null
						|| (entryPublishDate != null && entryPublishDate
								.after(mostRecentArticle))) {
					mostRecentArticle = entryPublishDate;
				}

				if (feedPublishDate == null || lastImportedDate == null
						|| entryPublishDate.after(lastImportedDate)) {

					if (debug) {
						log.debug("Feed output = " + entry);
					}

					item.setType(contentType);
					item.setCommunityName(community);
					item.setPaths(new HashSet<String>(Arrays.asList(path)));

					item.setKeyFields(keyFields);

					log.debug("set key fields to " + keyFields.toString());
					/*
					 * JexlContext jc = JexlHelper.createContext();
					 * jc.getVars().put("$tools.date",new DateTool());
					 * jc.getVars().put("$feed",feed);
					 * jc.getVars().put("$feedLink", url); //used for demo to
					 * assign unique value for import. should change
					 * jc.getVars().put("$path", path);
					 */
					for (String field : fieldMap.keySet()) {
						String jexl = fieldMap.get(field);
						Script s = ScriptFactory.createScript(jexl);

						// Create a context and add data

						jc.getVars().put("$item", entry);
						log.debug("executing field " + field + " expression: "
								+ jexl);
						Object o = s.execute(jc);
						if (o != null) {
							if (field.equals("sys_title")) {
								item.setName(o.toString());
								log.debug("Set title to " + o.toString());
							}

							ImportField importField = new ImportField();
							importField.setName(field);
							importField.setValue(o);

							item.getFields().put(importField);

							log.debug("Setting field " + field + " to value "
									+ o.toString());

						} else {
							log.debug("Jexl Expression returned null");
						}

					}

					if (!debug) {
						items.add(item);
						log.debug("Adding slot relationship");
						ImportRelationship rel = new ImportRelationship();
						rel.setSlot(feedItemSlotName);
						rel.setTemplate(feedItemTemplateName);
						rel.setItem(item);
						if (relationships != null) {
							relationships.add(rel);
						}
					}
				}
			}
			log.debug("Adding " + items.size() + "Feed items to import batch");
			list.add(items);

			log.debug("setting last imported date to " + feedPublishDate);
			setLastImportedDate(feedPublishDate);
		} else {
			log.debug("Feed not modified skipping");
		}

		return list.iterator();

	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public int getMaxItems() {
		return maxItems;
	}

	public void setMaxItems(int maxItems) {
		this.maxItems = maxItems;
	}

	public void setFieldMap(Map<String, String> fieldMap) {
		this.fieldMap = fieldMap;
	}

	public List<String> getKeyFields() {
		return keyFields;
	}

	public void setKeyFields(List<String> keyFields) {
		this.keyFields = keyFields;
	}

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public Date getLastImportedDate() {
		return lastImportedDate;
	}

	public void setLastImportedDate(Date lastImportedDate) {
		this.lastImportedDate = lastImportedDate;
	}

	public boolean isForceUpdate() {
		return forceUpdate;
	}

	public void setForceUpdate(boolean forceUpdate) {
		this.forceUpdate = forceUpdate;
	}

	public String getCommunity() {
		return community;
	}

	public void setCommunity(String community) {
		this.community = community;
	}

	public String getFeedType() {
		return feedType;
	}

	public void setFeedType(String feedType) {
		this.feedType = feedType;
	}

	public Map<String, String> getFeedFieldMap() {
		return feedFieldMap;
	}

	public void setFeedFieldMap(Map<String, String> feedFieldMap) {
		this.feedFieldMap = feedFieldMap;
	}

	public Map<String, String> getFieldMap() {
		return fieldMap;
	}

	public List<String> getFeedKeyFields() {
		return feedKeyFields;
	}

	public void setFeedKeyFields(List<String> feedKeyFields) {
		this.feedKeyFields = feedKeyFields;
	}

	public String getFeedItemSlotName() {
		return feedItemSlotName;
	}

	public void setFeedItemSlotName(String feedItemSlotName) {
		this.feedItemSlotName = feedItemSlotName;
	}

	public String getFeedItemTemplateName() {
		return feedItemTemplateName;
	}

	public void setFeedItemTemplateName(String feedItemTemplateName) {
		this.feedItemTemplateName = feedItemTemplateName;
	}

	public void setItemRef(String itemRef) {
		this.setUrl(itemRef);

	}

	public void setImportRoot(String importRoot) {
		this.importRoot = importRoot;
	}

	public String getImportRoot() {
		return importRoot;
	}
}
