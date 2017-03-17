package com.percussion.pso.importer.manager;

import static com.percussion.pso.importer.converter.Converters.convertAll;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.percussion.pso.importer.model.Guid;
import com.percussion.pso.importer.model.ImportBatch;
import com.percussion.pso.importer.model.ImportItem;
import com.percussion.pso.importer.model.ImportRelationship;
import com.percussion.pso.importer.model.ImportSlot;
import com.percussion.pso.rxws.converter.FromLocalItemToRemoteItem;
import com.percussion.pso.rxws.converter.FromRemoteItemToLocalItem;
import com.percussion.pso.rxws.item.RxWsContentHelper;
import com.percussion.pso.rxws.item.RxWsContext;
import com.percussion.webservices.assembly.data.PSAssemblyTemplate;
import com.percussion.webservices.assembly.data.PSTemplateSlot;
import com.percussion.webservices.content.FolderRef;
import com.percussion.webservices.content.PSAaRelationship;
import com.percussion.webservices.content.PSAaRelationshipFilter;
import com.percussion.webservices.content.PSContentTypeSummary;
import com.percussion.webservices.content.PSItem;
import com.percussion.webservices.content.PSItemStatus;
import com.percussion.webservices.security.data.PSCommunity;

public class ItemManager {


	private static final Log log = LogFactory.getLog(ItemManager.class);


	private Map<Integer,PSItemStatus> statusMap = new HashMap<Integer,PSItemStatus>();
	private RxWsContext context;
	private RxWsContentHelper helper;
	private Map<Integer,String> availableCommunities = new HashMap<Integer,String>();
	private ItemLocatorManager locatorManager;

	private boolean initialized;
	private FromLocalItemToRemoteItem localItemConverter;
	private FromRemoteItemToLocalItem remoteItemConverter;

	Map<String,String> templateNameMap;
	Map<String,String> slotNameMap;
	Map<String, String> contentTypeMap;


	public ItemManager() {

	}

	public void initializeContext() {
		if(!context.isLoggedIn()) {
			log.debug("Not logged in logging in");
			try {
				context.login();
			} catch (Exception e) {
				log.debug("Cannot login ",e);
			}
		}
		if(context.isLoggedIn() && !isInitialized()) {
			//locatorManager = new ItemLocatorManager();
			//locatorManager.setManager(this);
			//locatorManager.setContext(context);
			//locatorManager.setAllowUpdateField(allowImportField);
			for (PSCommunity community: context.getAvailableCommunities()) {
				availableCommunities.put(Guid.getId(community.getId()),community.getName());
				log.debug("Adding available community "+community.getName());
			}

			try {
				List<PSTemplateSlot> slots = helper.loadSlots(null);
				List<PSAssemblyTemplate> templates = helper.loadAssemblyTemplates(null, null);
				List<PSContentTypeSummary> contentTypes = helper.loadContentTypes(null);

				this.templateNameMap = new HashMap<String,String>();
				this.slotNameMap = new HashMap<String,String>();
				this.contentTypeMap = new HashMap<String, String>();
				for(PSAssemblyTemplate template : templates) {

					String name = template.getName();
					long id = Guid.getId(template.getId());
					templateNameMap.put(name, String.valueOf(id));
					log.debug("Template map name="+name+" id="+id);
				}
				for(PSTemplateSlot slot : slots) {
					String name = slot.getName();
					long id = Guid.getId(slot.getId());
					slotNameMap.put(name, String.valueOf(id));
					log.debug("Slot map name="+name+" id="+id);
				}
				for(PSContentTypeSummary contentType : contentTypes){
					String name = contentType.getName();
					long id =  Guid.getId(contentType.getId());
					contentTypeMap.put(String.valueOf(id), name);
				}
			} catch (Exception e) {
				log.debug("Cannot get templates or slots", e);
			}
			setInitialized(true);
		}

	}



	public void prepareForEdit(ImportBatch batch) {


		initializeContext();
		locatorManager.getLocatorsFromBatch(batch);
		getCreateItems(batch);
		locatorManager.logUnresolvedLocators(batch);
		//locatorManager.getNewItems(batch);
		Map<String,List<ItemLocator>> prepareCommMap  = locatorManager.getEditableItems(batch);

		for (String community : prepareCommMap.keySet()) {
			log.debug("switching community to "+community);
			try {
				helper.switchCommunity(community);
				List<Integer> ids = new ArrayList<Integer>();
				log.debug("Getting ids for community " + community);

				for(ItemLocator locator : prepareCommMap.get(community)) {

					if( !statusMap.containsKey(locator.getRemoteId()) && locator.getRemoteId() > 0) { 
						ids.add(locator.getRemoteId());
					} else {
						log.debug("Item already ready to edit" +locator.getRemoteId());
					}
				}
				if(ids.size() > 0) {
					log.debug("Forcing Checking in items now");
					log.debug("ids = "+ids);
					helper.checkinItems(ids, "Forced checkin by Importer");
					List<PSItemStatus> status = helper.prepareForEdit(ids);
					for(PSItemStatus statusitem : status) {
						statusMap.put(Guid.getId(statusitem.getId()), statusitem);
					}
				}
			} catch (Exception e) {
				log.debug("error" ,e);
				for(ItemLocator locator : prepareCommMap.get(community)) {
					locator.setErrorMessage("Cannot switch to community "+ community);
				}
			}
		}

	}


	public void releaseFromEdit(ImportBatch batch) {
		releaseFromEdit(batch,false);
	}

	public void releaseFromEdit(ImportBatch batch, boolean checkInOnly) {

		initializeContext();
		Map<String,List<ItemLocator>> releaseCommMap  = locatorManager.getEditableItems(batch);

		for (String community : releaseCommMap.keySet()) {
			log.debug("switching community to "+community);
			try {
				helper.switchCommunity(community);
				List<PSItemStatus> itemStatus = new ArrayList<PSItemStatus>();
				List<Integer> checkinList = new ArrayList<Integer>();
				log.debug("Getting release ids for community " + community);
				for(ItemLocator locator : releaseCommMap.get(community)) {

					if( statusMap.containsKey(locator.getRemoteId())) { 

						itemStatus.add(statusMap.get(locator.getRemoteId()));
						log.debug("Found status for item, releasing " +locator.getRemoteId());
						statusMap.remove(Integer.valueOf(locator.getRemoteId()));
					} else {
						if(locator.getRemoteId() > 0) {
							checkinList.add(locator.getRemoteId());
							log.debug("Item not Checked out by me adding to checkin list" +locator);
						}
					}
					// after an item is checked in make it read only, cannot make multiple updates
					//  This may have prevented inline links within pages to be updated.
					//locator.setReadOnly(true);
				}
				if (itemStatus.size() > 0) helper.releaseFromEdit(itemStatus,checkInOnly);
				if (checkinList.size() > 0) helper.checkinItems(checkinList, "Item checked in by importer");


			} catch (Exception e) {
				log.debug("error" ,e);
				for(ItemLocator locator : releaseCommMap.get(community)) {
					locator.setErrorMessage("Cannot switch to community "+ community);
				}
			}

		}

	}

	public boolean createNav(ImportItem item, String navTreeType) { 

		initializeContext();
		ImportBatch navBatch = new ImportBatch();
		navBatch.add(item);
		Set<String> folderPaths = item.getPaths();
		if (folderPaths.size() > 1) {
			log.debug("Nav Item can only specify one folder path");
		} else if (folderPaths.size()==0) {
			log.debug("Nav Item must specify folder path");
		} else {
			String folder = folderPaths.iterator().next();
			//TODO:  Set content type name as property or pass into this function.
			List<Integer> navtreeItems;
			try {
				navtreeItems = helper.findFolderItemByType(folder, navTreeType);
			} catch (Exception e) {
				log.debug("Error tring to find NavTree for folder "+folder,e);
				return false;
			}
			if (navtreeItems.size()==1) {
				return true;
			} else {
				
				getCreateItems(navBatch);
				updatePaths(navBatch);
				releaseFromEdit(navBatch);
				return true;
			}

		}
		return false;
	}

	public Map<ItemLocator,ImportItem> getItemLocatorMap(ImportBatch batch) {
		Map<ItemLocator,ImportItem> itemLocatorMap = new HashMap<ItemLocator,ImportItem>();
		List<ImportItem> duplicates = new ArrayList<ImportItem>();
		for(ImportItem item: batch.getItems()) {
			// Map should filter out duplicates,  assuming they are the same we are not merging duplicates.
			if (itemLocatorMap.containsKey(item.getLocator())) {
				ImportItem currentItem = itemLocatorMap.get(item.getLocator());
				log.debug("More than one item in batch with same locator, ignoring duplicates "+ item.getLocator());
				String currentLocalId = currentItem.getLocator().getLocalId();
				String extraLocalId = item.getObjectId();
				String newLocalId = "";
				log.debug("Existing locator = "+currentItem.getLocator());
				log.debug("Duplicate locator = "+item.getObjectId());
				if (currentLocalId != null && currentLocalId.length() > 0) {
					if (extraLocalId != null && extraLocalId.length() > 0) {
						newLocalId = currentLocalId + "," + extraLocalId;
					} else {
						newLocalId = currentLocalId;
					}
				} else {
					newLocalId = extraLocalId;
				}

				if (newLocalId.length() > 0) {
					currentItem.setObjectId(newLocalId);
					currentItem.getLocator().setLocalId(newLocalId);
				}
				duplicates.add(item);
			} else {
				item.getLocator().setLocalId(item.getObjectId());
				itemLocatorMap.put(item.getLocator(), item);
			}

		}
		batch.removeAll(duplicates);
		return itemLocatorMap;
	}

	public void getCreateItems(ImportBatch batch) {
		//Import items by community, by content type
		log.debug("in get create items");
		

		locatorManager.getLocatorsFromBatch(batch);
		// TODO : merge the following into getLocatorsFromBatch to filter duplicates in all cases.
		Map<ItemLocator,ImportItem> itemLocatorMap = getItemLocatorMap(batch);

		Map<String,Map<String,List<ItemLocator>>> communitytypeItems = locatorManager.getNewItems(batch);

		for (String community : communitytypeItems.keySet()) {
			log.debug("switching community to "+community);
			try {
				helper.switchCommunity(community);


				for(String type : communitytypeItems.get(community).keySet()) {
					List<Integer> ids = new ArrayList<Integer>();
					List<ImportItem> items = new ArrayList<ImportItem>();

					for(ItemLocator locator : communitytypeItems.get(community).get(type)) {
						if (!locator.isError() && locator.getRemoteId() == 0) {
							ids.add(locator.getRemoteId());
							items.add(itemLocatorMap.get(locator));
						}
					}

					if(items.size() > 0) {
						log.debug("Creating items with community="+ community +" type="+type);



						List<Integer> remoteIds= new ArrayList<Integer>();

						for(int i = 0; i < items.size(); i ++) {

							List<PSItem> newRemoteItem = 
								helper.createItems(1, type);
					
							List<ImportItem> item = Collections.singletonList(items.get(i));
							
							helper.clearAttachements();
							newRemoteItem = convertAll(localItemConverter, item, newRemoteItem, context);
							//log.debug("created new item");
							//log.debug("Type is "+newRemoteItem.get(0).getContentType());
							//log.debug("Fields" + newRemoteItem.get(0).getFields());
							/*for(int j=0 ; j<  newRemoteItem.get(0).getFields().length ; j++) {
									PSField field = newRemoteItem.get(0).getFields()[j];

									log.debug(field.getName());
									if (field.getPSFieldValue() != null  && field.getPSFieldValue().length>0) {
										log.debug( field.getPSFieldValue(0).getRawData());
									}
								}*/
							try{
								int remoteId = helper.saveItems(newRemoteItem).get(0);
								remoteIds.add(remoteId);

								log.debug("Getting locator for new Item : " +remoteId);
								locatorManager.updateLocator(item.get(0),remoteId);
								
							} catch (Exception ex) {
								ImportItem localItem = item.get(0);
								log.debug("Cannot save Item", ex);
								localItem.addError("Cannot save Item", ex);
							}
						}
					}



					//	newRemoteItems = helper.loadItems(remoteIds);
					//	batch.setItems(convertAll(remoteItemConverter, newRemoteItems, items, context));

				}
			} catch (Exception e) {
				log.debug("error" ,e);
				log.debug("Error creating items");

			}
		}

		return;
	}

	public ImportBatch updateRx(ImportBatch batch) throws Exception {
		ImportBatch newBatch = new ImportBatch();
		newBatch = updateRx(batch,true);
		
		return newBatch;
	}

	public ImportBatch updateRx(ImportBatch batch, boolean reloadItems) throws Exception {
		log.debug("Starting Update");
		initializeContext();
		locatorManager.getLocatorsFromBatch(batch);

		Map<String,List<ItemLocator>> releaseCommMap  = locatorManager.getEditableItems(batch);

		Map<ItemLocator,ImportItem> itemLocatorMap = getItemLocatorMap(batch);
		ImportBatch newBatch = new ImportBatch();
		for (String community : releaseCommMap.keySet()) {
			log.debug("switching community to "+community);
			try {
				helper.switchCommunity(community);
				log.debug("Updating items for community " + community);
				List<Integer> ids = new ArrayList<Integer>();
				List<ImportItem> items = new ArrayList<ImportItem>();
				for(ItemLocator locator : releaseCommMap.get(community)) {
					ids.add(locator.getRemoteId());
					ImportItem item = itemLocatorMap.get(locator);
					items.add(item);
					log.debug("Adding locator "+locator +" to update list");
					if (item.getCommunityName() != null && !item.getLocator().getCommunityName().equals(item.getCommunityName())) {
						log.debug("Need to change community of item "+locator.getRemoteId() + " from "+ locator.getCommunityName() +" to "+item.getCommunityName() );	
						//finding community id to change to
						for (Entry<Integer,String> entry : availableCommunities.entrySet()) {
							if (entry.getValue().equals(item.getCommunityName())) {
								item.getFields().put("sys_communityid", entry.getKey());
								locator.setCommunityName(item.getCommunityName());
							}
						}

					}
				}

				List<PSItem> remoteItems = helper.loadItems(ids);

				// need to make sure what we pull back is correct.  convertor not working.
				//	items = convertAll(remoteItemConverter, remoteItems,items,context);
				if (remoteItems.size() != ids.size()) {
					log.debug("Returned items not the same as ids passed in");
				} else {
					//remoteItems = helper.loadItems(ids);


					// Save and convert each item individually

					List<Integer> remoteIds= new ArrayList<Integer>();

					for(int i = 0; i < remoteItems.size(); i ++) {
						List<PSItem> newRemoteItem = 
							Collections.singletonList(remoteItems.get(i));
						List<ImportItem> newItem = Collections.singletonList(items.get(i));
						helper.clearAttachements();
						newRemoteItem = convertAll(localItemConverter, newItem, newRemoteItem, context);
						try{

							remoteIds.add(helper.saveItems(newRemoteItem).get(0));

						} catch (Exception ex) {
							ImportItem localItem = items.get(0);
							localItem.addError("Cannot save Item", ex);
						}
					}


					List<Integer> batchIds = new ArrayList<Integer>();
					List<ImportItem> newItems = new ArrayList<ImportItem>();
					for(ImportItem batchItem : items) {
						int remoteId = batchItem.getLocator().getRemoteId();
						if (remoteId > 0) {
							newItems.add(batchItem);
							batchIds.add(batchItem.getLocator().getRemoteId());
						} else {
							log.debug("Unresolved remote ids in batch");
							newBatch.add(batchItem);
						}
					}
					if (reloadItems) {
					log.debug("Reloading saved items from server");
					remoteItems = helper.loadItems(batchIds);
					log.debug("Converting Remote Results to local: "+remoteItems.size());
					items = convertAll(remoteItemConverter, remoteItems, newItems, context);
					log.debug("Getting items back from server "+items.size());
					log.debug("Workflowid is "+items.get(0).getFields().get("sys_workflowid").getStringValue() );
					newBatch.addAll(items);
					}
					else {
						log.debug("Reload Items set to false");
						newBatch = batch;
					}
				}
			
			}

		
			catch (Exception e) {
				log.debug("error" ,e);
				for(ItemLocator locator : releaseCommMap.get(community)) {
					locator.setErrorMessage("Cannot switch to community "+ community);
				}
			}
		}
		//log.debug("Resetting locators");
		log.debug("NewBatch size is :"+newBatch.size());
		//locatorManager.resetLocators();
		return newBatch;
	}

	
	
	
	public void updateRelationships(ImportBatch batch) {


		initializeContext();

		locatorManager.getLocatorsFromBatch(batch);

		Map<String,List<ItemLocator>> releaseCommMap  = locatorManager.getEditableItems(batch);

		Map<ItemLocator,ImportItem> itemLocatorMap = getItemLocatorMap(batch);

		for (String community : releaseCommMap.keySet()) {
			log.debug("switching community to "+community);
			try {
				helper.switchCommunity(community);
				log.debug("Updating relationships for community " + community);
				List<Integer> ids = new ArrayList<Integer>();
				List<ImportItem> items = new ArrayList<ImportItem>();
				for(ItemLocator locator : releaseCommMap.get(community)) {
					ids.add(locator.getRemoteId());
					items.add(itemLocatorMap.get(locator));
					ImportItem item = itemLocatorMap.get(locator);
					if (item.getSlots()!=null) {
						for (ImportSlot  e : item.getSlots()) {
							List<ImportRelationship> related = e.getRelationship();
							log.debug("pushing relationships for item" +item.getId() + " slot "+e.getSlot());
							if (related != null) {
								log.debug("Found "+ related.size() +" items in slot");
								pushRelated(locator, e.getSlot(), related);
							} else {
								log.debug("Slot contents is null");
							}
						}
					}

				}

			} catch (Exception e) {
				log.debug("error" ,e);
				for(ItemLocator locator : releaseCommMap.get(community)) {
					locator.setErrorMessage("Cannot switch to community "+ community);
				}
			}
		}
		return;

	}


	private void pushRelated(ItemLocator ownerLoc , String slot, List<ImportRelationship>  related) throws Exception {
		initializeContext();
		int ownerId = ownerLoc.getRemoteId();
		//TODO we should be adding relationships properties.
		//Our RelatedItemToRemoteId converter will have to change.
		log.debug("Updating relationships for slot="+slot + " owner="+ownerId);
		//related = validateRelated(related);

		//RxWsContentHelper helper = new RxWsContentHelper(context);
		PSAaRelationshipFilter filter = new PSAaRelationshipFilter();
		filter.setSlot(slot);
		filter.setOwner(new Guid(ownerId).getGuid());
		filter.setLimitToEditOrCurrentOwnerRevision(true);
		List<PSAaRelationship> rels = helper.loadContentRelations(filter);

		List<Long> relsToRemove = new ArrayList<Long>();

		for(PSAaRelationship rel : rels) {
			log.debug("Removing old id to " + Guid.getId(rel.getDependentId())+" :"+ rel.getId() );
			relsToRemove.add(rel.getId());
		}

		if(relsToRemove.size() > 0) {
			helper.deleteContentRelations(relsToRemove);
		}

		for(ImportRelationship rel : related) {
			//TODO:  better way to make sure that related item has populated locator
			ImportBatch relBatch = new ImportBatch();
			relBatch.add(rel.getItem());
			Integer remoteId = 0;
			if (rel.getItem()!= null) {
				locatorManager.getLocatorsFromBatch(relBatch);

				ItemLocator relLocator = rel.getItem().getLocator();
				remoteId = relLocator.getRemoteId();
				log.debug("Adding item with remote id "+remoteId);
			} else {
				log.error("no relationship item");
			}
			if (remoteId > 0) {
				String template = rel.getTemplate();
				log.debug("Adding relationship for template="+template+" and id"+  Collections.singletonList(remoteId));
				int siteId = -1;
				//TODO: Add site id if set 
				String folder = rel.getFolder();
				int folderId = -1;
				if (folder!=null) {
					if (!rel.getItem().getPaths().contains(folder)) {
						log.debug("Related item not in folder specified"+folder);
					} else {
						folderId = getFolderId(folder);
						log.debug("Folder id is "+folderId);
					}
				}
				
				List<PSAaRelationship> newRels = helper.addContentRelations(ownerId, Collections.singletonList(remoteId), folderId, siteId, slot, template, -1);
				
			} else {
				log.debug("relationship dependent locator not yet found ");
			}
		}      

	}

	private int getFolderId(String folder) throws Exception{
		return helper.findPathId(folder);
	}
	
	public void updatePaths(ImportBatch batch) {
		initializeContext();
		locatorManager.getLocatorsFromBatch(batch);

		Map<String,List<ItemLocator>> releaseCommMap  = locatorManager.getEditableItems(batch);

		Map<ItemLocator,ImportItem> itemLocatorMap = getItemLocatorMap(batch);

		for (String community : releaseCommMap.keySet()) {
			log.debug("switching community to "+community);
			try {
				helper.switchCommunity(community);
				log.debug("Updating paths for community " + community);
				ImportBatch commBatch = new ImportBatch();
				for(ItemLocator locator : releaseCommMap.get(community)) {
					commBatch.add(itemLocatorMap.get(locator));
				}
				updatePathsCommunity(commBatch);
				if (commBatch.getErrors().size()> 0) {
					batch.getErrors().addAll(commBatch.getErrors());
				}
			} catch (Exception e) {
				log.debug("error" ,e);
				for(ItemLocator locator : releaseCommMap.get(community)) {
					locator.setErrorMessage("Cannot switch to community "+ community);
				}
			}
		}
	}
	private void updatePathsCommunity(ImportBatch batch) {
		// assumes items already editable and have locators populated,  called from updatePaths

		Map<String, List<Integer>> existingFolderMap = new HashMap<String, List<Integer>>();
		for (ImportItem item : batch) {
			try {
				//Check if id is set
				Integer id = item.getId();
				List<String> existingFolders = helper.findFolderPaths(id);
				if (item.getPaths() == null) {
					log.debug("This item does not specify a folder to move to : " + id);
				} else {
					//log.debug("Item is alredy in " + item.getPaths());

					for (String path : existingFolders) {
						if (!existingFolderMap.containsKey(path)) {
							existingFolderMap.put(path, new ArrayList<Integer>());
						}
						//log.debug("Adding existing " + id + " to folder " + path);
						existingFolderMap.get(path).add(id);
					}
				}

			} catch (Exception e) {
				item.addError("Error with folder path", e);
			}
		}

		Map<String, ImportBatch> pathMap = separateByPaths(batch);
		for (Entry<String, ImportBatch> e : pathMap.entrySet()) {
			String localPath = e.getKey();
			List<Integer> existingItems = existingFolderMap.get(localPath);
			log.debug("Checking for folder " + localPath);
			ImportBatch folderItems = e.getValue();
			List<Integer> remoteIds = new ArrayList<Integer>();
			for(ImportItem item : folderItems.getItems()) {
				remoteIds.add(item.getLocator().getRemoteId());
			}

			FolderRef remoteFolderRef = new FolderRef(null,localPath);


			//TODO getting the path this way is bad because it might not be set.
			//Converter should probably return a string.
			String path = remoteFolderRef.getPath();

			List<Integer> addToFolder = new ArrayList<Integer>();
			for (Integer remoteId : remoteIds) {

				if (existingItems == null || !existingItems.contains(remoteId)) {
					addToFolder.add(remoteId);
					log.debug("Item not already in folder need to add"
							+ remoteId);
				} else {
					log.debug("Item already in folder" + remoteId);
				}
				if (existingItems != null) {
					log.debug("Taking item from remove folder list");
					existingItems.remove(remoteId);
				}

			}

			if (existingItems != null) {
				existingFolderMap.put(localPath, existingItems);
				log.debug("remove items size for " + localPath + "is "
						+ existingItems.size());
			}
			if (addToFolder.size() > 0) {
				try {
					helper.addFolderTree(path);
				} catch (Exception ex) {
					batch.addError("Cannot add folder tree :"+path);
				}
				for(Integer id : addToFolder) {
					try {
						helper.addFolderChildren(remoteFolderRef, Collections.singletonList(id));
					} catch (Exception ex) {
						batch.addError(" Cannot Add "+id +" to folder :"+path);
						log.error(" Cannot Add "+id +" to folder :"+path,ex);
					}
				}
				
			}
		}
		try {
			cleanupFolderItems(existingFolderMap, context);
		} catch (Exception ex) {
			batch.addError("Cannot cleanup existing folder items");
		}
	}

	public void createFolderTree(String path) throws Exception {
		helper.addFolderTree(path);
	}
	private Map<String, ImportBatch> separateByPaths(ImportBatch items) {
		Map<String, ImportBatch> pathMap = new HashMap<String, ImportBatch>();
		for (ImportItem item : items) {
			if (item.getPaths() != null) {
				for (String p : item.getPaths()) {
					ImportBatch bucket = pathMap.get(p);
					if (bucket == null) {
						bucket = new ImportBatch();
						pathMap.put(p, bucket);
					}
					bucket.add(item);
				}
			}
		}
		return pathMap;
	}

	private void cleanupFolderItems(Map<String, List<Integer>> cleanupmap,
			RxWsContext context) throws Exception {

		if (cleanupmap.keySet().size() > 0) {

			//List<PSFolder> folders = contentHelper.loadFolders(new ArrayList(cleanupmap.keySet()));

			for (String path : cleanupmap.keySet()) {
				FolderRef remoteFolderRef = new FolderRef(null,path);
				if (cleanupmap.get(path).size() > 0) {
					helper.removefolderChildren(remoteFolderRef,
							cleanupmap.get(path), false);
					log.debug("Removing items from folder " + path);
				}

				for (int id : cleanupmap.get(path)) {
					log.debug("removing id " + id);
				}
			}

		}

	}

	public void load(ImportBatch batch) {

		List<Integer> ids = new ArrayList<Integer>();
		List<ImportItem> items = new ArrayList<ImportItem>();
		List<ImportItem> remainingItems = new ArrayList<ImportItem>();
		for(ImportItem item : batch) {
			int remoteId = item.getLocator().getRemoteId();
			if (remoteId > 0) {
				items.add(item);
				ids.add(item.getLocator().getRemoteId());
			}
			else {
				remainingItems.add(item);
			}
		}

		if( items.size() > 0) {
			try {
				List<PSItem> newRemoteItems = helper.loadItems(ids);
				remainingItems.addAll(convertAll(remoteItemConverter, newRemoteItems, items, context));
			} catch (Exception e) {
				remainingItems.addAll(items);
			}
		}

		batch.setItems(remainingItems);
	}

	public void deleteFolders(List<String> folderPaths, boolean purgeItems){
		initializeContext();
		try 
		{
			helper.deleteFolders(folderPaths, purgeItems);
		} catch (Exception e) 
		{
			log.debug("Folder has already been deleted");
		}
	}

	ImportBatch updateLocal(ImportBatch batch) {
		return null;
	}

	public void resetLocators() {
		initializeContext();
		locatorManager.resetLocators();
	}
	public void updateLocators(ImportBatch batch) {
		initializeContext();
		locatorManager.getLocatorsFromBatch(batch);
	}

	public RxWsContext getContext() {
		initializeContext();
		return context;
	}

	public void setContext(RxWsContext context) {
		this.context = context;
		this.helper=context.getHelper();
	}

	public FromLocalItemToRemoteItem getLocalItemConverter() {
		return localItemConverter;
	}

	public void setLocalItemConverter(FromLocalItemToRemoteItem localItemConverter) {
		this.localItemConverter = localItemConverter;
	}

	public Map<String, String> getTemplateNameMap() {
		initializeContext();
		return templateNameMap;
	}

	public Map<String, String> getSlotNameMap() {
		initializeContext();
		return slotNameMap;
	}
	
	public Map<String, String> getContentTypeMap() {
		initializeContext();
		return contentTypeMap;
	}

	public FromRemoteItemToLocalItem getRemoteItemConverter() {
		return remoteItemConverter;
	}

	public void setRemoteItemConverter(FromRemoteItemToLocalItem remoteItemConverter) {
		this.remoteItemConverter = remoteItemConverter;
	}

	public ItemLocatorManager getLocatorManager() {
		initializeContext();
		return locatorManager;
	}

	public void setLocatorManager(ItemLocatorManager locatorManager) {
		this.locatorManager = locatorManager;
	}

	public void setInitialized(boolean initialized) {
		this.initialized = initialized;
	}

	public boolean isInitialized() {
		return initialized;
	}
} 
