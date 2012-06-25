package com.percussion.pso.importer.impl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.percussion.pso.importer.IImportItemRefProducer;
import com.percussion.pso.rxws.item.RxWsContext;
import com.percussion.webservices.content.PSSearchResults;
import com.percussion.pso.importer.FeedInfo;

public class PercSearchItemRefProducer implements
		IImportItemRefProducer<FeedInfo> {

	/**
	 * The log instance to use for this class, never <code>null</code>.
	 */
	private static final Log log = LogFactory
			.getLog(PercSearchItemRefProducer.class);

	private RxWsContext context;
	private String urlField;
	private List<String> urlList;
	private Map<String, String> searchFields;

	private String importRoot = null;
	
	private String feedFolderPath;

	public String getFeedFolderPath() {
		return feedFolderPath;
	}

	public void setFeedFolderPath(String feedFolderPath) {
		this.feedFolderPath = feedFolderPath;
	}

	public String getImportRoot() {
		return importRoot;
	}

	public void setimportRoot(String importRoot) {
		this.importRoot = importRoot;
	}

	public Map<String, String> getSearchFields() {
		if (searchFields == null)
			searchFields = new HashMap<String, String>();
		return searchFields;
	}

	public void setSearchFields(Map<String, String> searchFields) {
		this.searchFields = searchFields;
	}

	public List<String> getUrlList() {
		return urlList;
	}

	public void setUrlList(List<String> urlList) {
		this.urlList = urlList;
	}

	public String getUrlField() {
		return urlField;
	}

	public void setUrlField(String urlField) {
		this.urlField = urlField;
	}

	public RxWsContext getContext() {
		return context;
	}

	public void setContext(RxWsContext context) {
		this.context = context;
	}

	public Iterator<FeedInfo> produce() throws Exception {
		initializeContext();
		List<String> searchUrlList = null;
		List<FeedInfo> feedInfoList = null;
		if (urlList == null) {
			searchUrlList = new ArrayList<String>();
			feedInfoList = new ArrayList<FeedInfo>();
			List<String> resultFields = new ArrayList<String>();
			log.debug("feed url from the soln feed: " + urlField);
			log.debug("feed folder path from the soln feed: " + feedFolderPath);
			resultFields.add(urlField);
			resultFields.add(feedFolderPath);

			List<PSSearchResults> results = context.getHelper()
					.findItemsbyKeysFull(getSearchFields(), resultFields, type,
							importRoot);
			log.debug("Search returned " + results.size() + " Items");
			for (PSSearchResults result : results) {
				FeedInfo feedInfo = new FeedInfo();
				for (int i = 0; i < result.getFields().length; i++) {
					String fieldName = result.getFields(i).getName();
					String fieldValue = result.getFields(i).get_value();
					log.debug("Field name found: " + fieldName);
					if (fieldName.equals(urlField)) {
						try {
							URL url = new URL(fieldValue);
							feedInfo.setFeedUrl(url.toString());
							searchUrlList.add(url.toString());
						} catch (MalformedURLException e) {
							log.debug("Field containd bad url " + fieldValue);
						}
					}else if (fieldName.equals(feedFolderPath)) {
						feedInfo.setFolderPath(fieldValue);
					}
					
				}
				feedInfoList.add(feedInfo);
			}
		} else {
			FeedInfo feedInfo = new FeedInfo();
			feedInfoList = new ArrayList<FeedInfo>();
			for(int j = 0; j < urlList.size(); j++){
				feedInfo.setFeedUrl(urlList.get(j));
			}
			feedInfoList.add(feedInfo);	
		}
		// TODO: If there are lots of results make this object iteratable and
		// handle getting batches of search results.
		//return searchUrlList.iterator();
		return feedInfoList.iterator();
	}

	public void initializeContext() {
		if (!context.isLoggedIn()) {
			log.debug("Not logged in logging in");
			try {
				context.login();
			} catch (Exception e) {
				log.debug("Cannot login ", e);
			}
		}
	}

	String type;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
