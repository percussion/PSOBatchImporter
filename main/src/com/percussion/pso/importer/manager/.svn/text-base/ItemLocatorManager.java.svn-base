package com.percussion.pso.importer.manager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.percussion.pso.importer.model.Guid;
import com.percussion.pso.importer.model.ImportBatch;
import com.percussion.pso.importer.model.ImportItem;
import com.percussion.pso.rxws.item.RxWsContext;
import com.percussion.webservices.content.PSField;
import com.percussion.webservices.content.PSItem;
import com.percussion.webservices.content.PSSearchResults;
import com.percussion.webservices.content.PSSearchResultsFields;
import com.percussion.webservices.security.data.PSCommunity;

public class ItemLocatorManager {

	private ItemManager manager;


	private Map<Integer,String> availableCommunities;
	private ArrayList<ItemLocator> locators = new ArrayList<ItemLocator>();
	private static final Log log = LogFactory.getLog(ItemLocatorManager.class);
	private RxWsContext context;
	private String allowUpdateField;
	private String navTreeType="rffNavTree";
	private String navonType="rffNavon";

	public String getNavTreeType() {
		return navTreeType;
	}

	public void setNavTreeType(String navTreeType) {
		this.navTreeType = navTreeType;
	}

	public String getNavonType() {
		return navonType;
	}

	public void setNavonType(String navonType) {
		this.navonType = navonType;
	}

	public ItemLocatorManager() {
	}

	private Map<Integer,String> getAvailableCommunities() {
		if (availableCommunities == null) {
			availableCommunities = new HashMap<Integer,String>();
			for (PSCommunity community: context.getAvailableCommunities()) {
				availableCommunities.put(Guid.getId(community.getId()),community.getName());
				log.debug("Adding available community "+community.getName());
			}
		}
		return availableCommunities;
	}

	public void getLocatorsFromBatch(ImportBatch batch) {
		log.debug("Getting locators for "+batch.size()+" Items");
		List<ItemLocator> updateLocatorIdList = new ArrayList<ItemLocator>();
		for (ImportItem item : batch) {
			ItemLocator itemLocator = item.getLocator();
			int index = locators.indexOf(itemLocator);
			if (index >= 0) {
				if(itemLocator.isReadOnly()==false) locators.get(index).setReadOnly(false);
				item.setLocator(locators.get(index));
				
			} else {
				locators.add(itemLocator);
			}
			if (itemLocator.getRemoteId() <= 0 && itemLocator.getMatches() < 0) {
				updateLocatorIdList.add(itemLocator);
			}
		}
		getLocatorIds(updateLocatorIdList);
	
		log.debug("Locators size = " + locators.size());
	}

	
	public void logUnresolvedLocators(ImportBatch batch) {
		List<ItemLocator> updateLocatorIdList = new ArrayList<ItemLocator>();
		for (ImportItem item : batch) {
			ItemLocator itemLocator = item.getLocator();
			if (itemLocator.getRemoteId() <= 0) {
				log.error(item.getUpdateType()+" Item can not be found : "+itemLocator);
			}
		}
		
	}

	public void updateLocator(ImportItem item,int remoteId) {

		if (locators.contains(item)) {
			int index = locators.indexOf(item.getLocator());
			ItemLocator locator = locators.get(index);
			locator.setRemoteId(remoteId);
			locator.setMatches(1);
			if(item.getLocator().isReadOnly()==false) {
				locator.setReadOnly(false);
			}
			item.setLocator(locator);
		} else {
			item.getLocator().setRemoteId(remoteId);
			locators.add(item.getLocator());
		}
		item.setId(remoteId);
	}

	public Map<String,List<ItemLocator>> getEditableItems(ImportBatch batch) {
		getLocatorsFromBatch(batch);

		List<ItemLocator> returnList = new ArrayList<ItemLocator>();
		for (ImportItem item : batch) {
			log.debug("item update type="+ item.getUpdateType()+ " remoteId="+item.getLocator().getRemoteId()+" matches="+item.getLocator().getMatches() +" ro="+item.getLocator().isReadOnly()); 
			if (!item.getUpdateType().equals("ref")  && item.getLocator().getRemoteId() > 0 && !item.getLocator().isReadOnly()) {	
				if (!returnList.contains(item.getLocator())) returnList.add(item.getLocator());
			}
		}

		return getCommunityLocators(returnList);
	}

	

	public Map<String, Map<String, List<ItemLocator>>> getNewItems(ImportBatch batch) {
		getLocatorsFromBatch(batch);
		List<ItemLocator> createItems = new ArrayList<ItemLocator>();
		for(ImportItem item : batch) {
			if (!item.getUpdateType().equals("ref") && item.getLocator().getRemoteId() <= 0 ) {
				log.debug("Need to create item " + item.getLocator());
				log.debug("Setting read/write flag on new item");
				setReadWrite(item);
				if (!createItems.contains(item.getLocator())) createItems.add(item.getLocator());
			} else if(item.getUpdateType().equals("createonly")) {
				item.setUpdateType("ref");
			} 
		}
		return getCommunityTypeItems(createItems);

	}
	public void resetLocators() {
		log.debug("Resetting Locators search count");
		for (ItemLocator locator : locators) {
			if(locator.getRemoteId()==0) {
				locator.setMatches(-1);
				locator.setErrorMessage(null);
				locator.setHasError(false);
			}
		}
	}

	private void getLocatorIds(List<ItemLocator> updateLocatorIdList) {
//		for (ItemLocator itemLocator : locators) {
//			if (itemLocator.getRemoteId() <= 0 && itemLocator.getMatches() < 0) {
//				updateLocatorIdList.add(itemLocator);
//			}
//		}
		log.debug("Number of update locators: " + updateLocatorIdList.size());
		if (updateLocatorIdList.size() > 0 ) {
			Map<String,List<ItemLocator>> updateCommMap = getCommunityLocators(updateLocatorIdList);

			for (String community : updateCommMap.keySet()) {

				log.debug("switching community to "+community);

				try {
					context.getHelper().switchCommunity(community);
					log.debug("Getting ids for community " + community);

					List<ItemLocator> communityLocators = updateCommMap.get(community);
					for(ItemLocator locator : communityLocators ) {
						if (locator.getSearchFields() != null && locator.getSearchFields().size() > 0) {
						//	log.debug("Resolving locator with search");
							updateLocatorWithSearch(locator);
						} 
						//log.debug("Locator Paths = "+locator.getPaths());
						if ( locator.getRemoteId() <= 0 && locator.getPaths() != null && locator.getPaths().size()==1  &&  locator.getContentType() != null && ( locator.getContentType().equals("rffNavon") || locator.getContentType().equals("rffNavTree") )){
						//	log.debug("Resolving Navigation Locator");
							updateLocatorwithNavigation(locator);
						} 
						
						if (locator.getRemoteId() <=0) {
							log.debug("Unresolved locator "+locator);
						}
					}
				
				} catch (Exception e) {
					log.debug("error" ,e);
					for(ItemLocator locator : updateCommMap.get(community)) {
						locator.setErrorMessage("Cannot switch to community "+ community);
					}
				}
			}
		}
	}
	private void checkReadOnly(PSItem item, ItemLocator locator) {
		if (allowUpdateField == null || allowUpdateField.length() == 0) {
			log.debug("No Readonly field defined : allowing item to be updated");
			locator.setReadOnly(false);
		} else {
			for(PSField field : item.getFields()) {
				if (field!=null) {
					if (field.getName().equals(allowUpdateField) && field.getPSFieldValue() != null && field.getPSFieldValue().length > 0 && field.getPSFieldValue(0).getRawData()!=null && ( field.getPSFieldValue(0).getRawData().equals("1") ||  field.getPSFieldValue(0).getRawData().equals("true")) ) {
						 log.debug("Found update flag on item, setting locator to read/write");
					     locator.setReadOnly(false);
					}
				}
			}
		}
		if (locator.isReadOnly()) {
			log.debug("Item set as read only, will not update : " +locator.getRemoteId());
		}

	}
	
	
	public void setReadWrite(ImportItem item) {
		log.debug("Setting  ImportItem to read write");
		if (allowUpdateField != null && allowUpdateField.length() != 0) {
			item.getFields().put(allowUpdateField, 1);
		}
		item.getLocator().setReadOnly(false);
	}
	
	private void updateLocatorWithSearch(ItemLocator locator) throws Exception {
		//TODO : Add Paths to search
		log.debug("Searching for " + locator);
		List<String> resultFields = new ArrayList<String>();
		resultFields.add("sys_communityid");
		resultFields.add("sys_contenttypeid");
		if(allowUpdateField != null && allowUpdateField.length() > 0) {
//			log.debug("Allow update field is "+allowUpdateField);
			resultFields.add(allowUpdateField);
		}
		//resultFields.add("sys_folderid");
		List<PSSearchResults> foundItems = context.getHelper().findItemsbyKeysFull(locator.getSearchFields(),resultFields,locator.getContentType(),locator.getImportRoot());
		log.debug("foundItems size : " + foundItems.size());
		if (foundItems.size() == 1) {
			//log.error("Found exactly one item, assigning to locator");
			PSSearchResults result = foundItems.get(0);
			String communityName = null;
			String contentTypeName = null;
			for  (PSSearchResultsFields field : result.getFields() ) {
				
				if (field.getName().equals("sys_communityid")) {
					//	log.debug("found communityid =" +field.get_value());
					communityName = getAvailableCommunities().get(Integer.valueOf(field.get_value()));
					//	log.debug("Found community name = "+communityName);
				}else if(field.getName().equals("sys_contenttypeid")){
					contentTypeName = manager.getContentTypeMap().get(field.get_value());
					//log.debug("sys_contenttypeid:"+ Integer.valueOf(field.get_value()) + ":content type name:" + contentTypeName);
				} else if(allowUpdateField != null && allowUpdateField.length() > 0 && field.getName().equals(allowUpdateField)) {
					if (field.get_value().equals("1") || field.get_value().equals("true")) {
					//	log.debug("found read/write field for item");
						locator.setReadOnly(false);
					}
				}
				locator.setReadOnly(false);
				if (field.getName().equals(allowUpdateField)) {
					//log.debug("readwrite field value is "+field.get_value());
				}
			}
			if (locator.getCommunityName() != null && !locator.getCommunityName().equals(communityName)) {
				log.debug("Item already exists but community in system "+communityName 
						+" is different than community set on item  "+ locator.getCommunityName() 
						+", will reset");
			}
			if(locator.getContentType() != null && !locator.getContentType().equals(contentTypeName)){
				log.debug("Item already exists but contentType in system "+contentTypeName 
						+" is different than contentType set on item  "+ locator.getContentType() 
						+", will reset");			
			}
			int remoteId=Guid.getId(result.getId());
			locator.setRemoteId(remoteId);
			locator.setCommunityName(communityName);
			locator.setContentType(contentTypeName);
			List<String> folders = context.getHelper().findFolderPaths(remoteId);
			locator.setPaths(new HashSet<String>(folders));
			log.debug("Updated locator : "+locator);
			//log.debug("Set "+folders.size()+ " paths for item");
		} else if (foundItems.size() > 0) {
			log.error("Locator cannot find unique item, please check following items ");
			for(PSSearchResults result : foundItems) {
				log.debug("Item id = "+Guid.getId(result.getId()));
			}
			locator.setErrorMessage("No unique item" + foundItems);

		}  else if (foundItems.size() == 0) {
			log.error("Locator finds no matches, maybe item created later" + locator);
		}
		locator.setMatches(foundItems.size());

	}

	private void updateLocatorwithNavigation(ItemLocator locator) throws Exception {
		if (locator.getPaths().size()!= 1) {
			log.error("Navigation items require one and only one path");
			return;
		}
		String path = locator.getPaths().iterator().next();
		context.getHelper().addFolderTree(path);
		List<String> navTypes = new ArrayList<String>();
		navTypes.add(navTreeType);
		navTypes.add(navonType);
		int navonId = context.getHelper().findFolderNavon(path, navTypes);

		if (navonId== -1) {
			log.debug("Need to create Navtree to generate navon");
		} else {
			log.debug("Found Navon setting locator Id");
			List<Integer> idList = new ArrayList<Integer>();
			idList.add(navonId);
			List<PSItem> remoteItems = context.getHelper().loadItems(idList);
		
			if (remoteItems.size() > 1) {
				log.error("Got "+remoteItems.size()+ " items for id "+idList);
			} else if (remoteItems.size() == 0){
				log.error("Cannot load navon item");
			}
			// the locator community stores the current item community, the item can have
			// a different community if it is we change the items community on update and change the
			// locator to reflect the change
			if (remoteItems.size() == 1) {
				log.debug("Got Navon Item");
				String currentCommunityName = locator.getCommunityName();
				for (PSField field : remoteItems.get(0).getFields() ) {
					if (field.getName().equals("sys_communityid")) {
					//	log.debug("Found community id for navon "+field.getPSFieldValue(0).getRawData());
						currentCommunityName = getAvailableCommunities().get(Integer.valueOf(field.getPSFieldValue(0).getRawData()));
					//	log.debug("item Community Name is "+currentCommunityName);
					}
				
				} 
				checkReadOnly(remoteItems.get(0), locator);
				locator.setCommunityName(currentCommunityName);
				locator.setMatches(1);
			}
			
			locator.setRemoteId(navonId);
		}

	}
	
	public  Map<String,List<ItemLocator>> getCommunityLocators(List<ItemLocator> locators) {
		Map<String,List<ItemLocator>> commLocatorMap = new HashMap<String,List<ItemLocator>>();
		for (ItemLocator itemLocator : locators) {
			String communityName = itemLocator.getCommunityName();
			if (communityName == null) {

				communityName = context.getCommunity();
			} 
			if (!getAvailableCommunities().containsValue(communityName) && !communityName.equals("Unknown") ){

				log.debug("User is not allowed to change to community "+communityName + " Available = " + getAvailableCommunities());
				itemLocator.setErrorMessage("Cannot change to Community "+communityName);

			} else {
				List<ItemLocator> commLocatorList = commLocatorMap.get(communityName);
				if (commLocatorList == null) {
					commLocatorList = new ArrayList<ItemLocator>();
					commLocatorMap.put(communityName, commLocatorList);
				} 
				//log.debug("test="+itemLocator.getRemoteId()+":"+itemLocator);
				commLocatorList.add(itemLocator);
			}
		}
		return commLocatorMap;
	}


	public  Map<String,Map<String,List<ItemLocator>>> getCommunityTypeItems(List<ItemLocator> items) {
		Map<String,Map<String,List<ItemLocator>>> commLocatorMap = new HashMap<String,Map<String,List<ItemLocator>>>();
		for (ItemLocator itemLocator : items) {
			String communityName = itemLocator.getCommunityName();
			if (communityName == null) {
				communityName = context.getCommunity();
			} 
			log.debug("communityName");
			if (!getAvailableCommunities().containsValue(communityName) && !communityName.equals("Unknown") ){

				log.debug("User is not allowed to change to community "+communityName + " Available = " + getAvailableCommunities());
				itemLocator.setErrorMessage("Cannot change to Community "+communityName);

			} else {
				Map<String,List<ItemLocator>> commType =  commLocatorMap.get(communityName);

				if (commType == null) {
					commType = new HashMap<String,List<ItemLocator>>();
					commLocatorMap.put(communityName, commType);
				} 
				String contentType = itemLocator.getContentType();
				if (contentType != null) {
					List<ItemLocator> commTypeItems = commType.get(itemLocator.getContentType());
					if (commTypeItems == null) {
						commTypeItems = new ArrayList<ItemLocator>(); 
						commType.put(itemLocator.getContentType(), commTypeItems);
					} 
					commTypeItems.add(itemLocator);
				} else {
					log.debug("Cannot create an item without a content type" + itemLocator);
					itemLocator.setErrorMessage("Cannot create item without a content type");
				}
			}
		}
		return commLocatorMap;
	}
	public void clearLocators() {
		locators = new ArrayList<ItemLocator>();
	}

	public RxWsContext getContext() {
		return context;
	}

	public void setContext(RxWsContext context) {
		this.context = context;
	}

	public String getAllowUpdateField() {
		return allowUpdateField;
	}

	public void setAllowUpdateField(String allowUpdateField) {
		this.allowUpdateField = allowUpdateField;
	}

	public ItemManager getManager() {
		return manager;
	}

	public void setManager(ItemManager manager) {
		this.manager = manager;
	}

}
