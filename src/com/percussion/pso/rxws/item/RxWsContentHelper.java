package com.percussion.pso.rxws.item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.axis.client.Stub;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.percussion.pso.importer.manager.ItemLocatorManager;
import com.percussion.pso.importer.model.Guid;
import com.percussion.webservices.assembly.Assembly;
import com.percussion.webservices.assembly.LoadAssemblyTemplatesRequest;
import com.percussion.webservices.assembly.LoadSlotsRequest;
import com.percussion.webservices.assembly.data.PSAssemblyTemplate;
import com.percussion.webservices.assembly.data.PSTemplateSlot;
import com.percussion.webservices.common.Reference;
import com.percussion.webservices.content.AddContentRelationsRequest;
import com.percussion.webservices.content.AddFolderChildrenRequest;
import com.percussion.webservices.content.AddFolderTreeRequest;
import com.percussion.webservices.content.CheckinItemsRequest;
import com.percussion.webservices.content.Content;
import com.percussion.webservices.content.CreateChildEntriesRequest;
import com.percussion.webservices.content.CreateItemsRequest;
import com.percussion.webservices.content.DeleteChildEntriesRequest;
import com.percussion.webservices.content.DeleteFoldersRequest;
import com.percussion.webservices.content.FindChildItemsRequest;
import com.percussion.webservices.content.FindFolderChildrenRequest;
import com.percussion.webservices.content.FindFolderPathRequest;
import com.percussion.webservices.content.FindFolderPathResponse;
import com.percussion.webservices.content.FindItemsRequest;
import com.percussion.webservices.content.FindPathIdsRequest;
import com.percussion.webservices.content.FindPathIdsResponse;
import com.percussion.webservices.content.FolderRef;
import com.percussion.webservices.content.LoadChildEntriesRequest;
import com.percussion.webservices.content.LoadContentRelationsRequest;
import com.percussion.webservices.content.LoadContentTypesRequest;
import com.percussion.webservices.content.LoadFoldersRequest;
import com.percussion.webservices.content.LoadItemsRequest;
import com.percussion.webservices.content.NewTranslationsRequest;
import com.percussion.webservices.content.PSAaRelationship;
import com.percussion.webservices.content.PSAaRelationshipFilter;
import com.percussion.webservices.content.PSAaRelationshipFolder;
import com.percussion.webservices.content.PSChildEntry;
import com.percussion.webservices.content.PSContentTypeSummary;
import com.percussion.webservices.content.PSFolder;
import com.percussion.webservices.content.PSItem;
import com.percussion.webservices.content.PSItemStatus;
import com.percussion.webservices.content.PSItemSummary;
import com.percussion.webservices.content.PSSearch;
import com.percussion.webservices.content.PSSearchField;
import com.percussion.webservices.content.PSSearchParams;
import com.percussion.webservices.content.PSSearchParamsFolderFilter;
import com.percussion.webservices.content.PSSearchProperty;
import com.percussion.webservices.content.PSSearchResultField;
import com.percussion.webservices.content.PSSearchResults;
import com.percussion.webservices.content.ReleaseFromEditRequest;
import com.percussion.webservices.content.RemoveFolderChildrenRequest;
import com.percussion.webservices.content.SaveChildEntriesRequest;
import com.percussion.webservices.content.SaveItemsRequest;
import com.percussion.webservices.content.SaveItemsResponse;
import com.percussion.webservices.system.GetAllowedTransitionsResponse;
import com.percussion.webservices.system.LoadWorkflowsRequest;
import com.percussion.webservices.system.PSAuditTrail;
import com.percussion.webservices.system.PSWorkflow;
import com.percussion.webservices.system.SwitchCommunityRequest;
import com.percussion.webservices.system.System;
import com.percussion.webservices.system.TransitionItemsRequest;
import com.percussion.webservices.system.TransitionItemsResponse;




public class RxWsContentHelper {
	public RxWsContext context;
   
	private static final Log log = LogFactory.getLog(RxWsContentHelper.class);
	
    public RxWsContentHelper(RxWsContext context) {
        this.context = context;
    }
    
    public List<PSItem> createItems(int number, String contentType)
    throws Exception
    {
    	
        CreateItemsRequest request = new CreateItemsRequest();
        request.setContentType(contentType);
        request.setCount(number);
        PSItem[] items = context.getContentService().createItems(request);
        
        return Arrays.asList(items);
    }
    
    public List<PSItemStatus> prepareForEdit(List<Integer> ids)
    throws Exception
    {
    
        PSItemStatus[] items = context.getContentService().prepareForEdit(convertIds(ids)); 
        return Arrays.asList(items);
    }
    
    public void releaseFromEdit(List<PSItemStatus> status, boolean checkInOnly)
    throws Exception
    {
    	ReleaseFromEditRequest request = new ReleaseFromEditRequest();
    	request.setPSItemStatus(status.toArray(new PSItemStatus[] {}));
    	request.setCheckInOnly(checkInOnly);
        context.getContentService().releaseFromEdit(request); 
    }
    
    public List<Integer> findItemsbyKeys(Map<String,String> fields, List<String> types) 
    throws Exception
    {
    	
    	List<Integer> combined = new ArrayList<Integer>();
    	if (types != null) {
    		for (String type : types ) {
    			combined.addAll(findItemsbyKeys(fields, type));
    		}
    	} else {
    		combined = findItemsbyKeys(fields);
    	}
    	return combined;
    }
    
    public List<Integer> findItemsbyKeys(Map<String,String> fields)
    throws Exception
    {
    	return findItemsbyKeys(fields,"");
    }
    
    public List<Integer> findItemsbyKeys(Map<String,String> fields, String type)
    throws Exception
    {
    	List<Integer> idList = new ArrayList<Integer>();
    	List<PSSearchResults> results = findItemsbyKeysFull(fields,  type);
    	for(PSSearchResults result : results) {
    		idList.add(Guid.getId(result.getId()));
    	}
        return idList;
    }
   
    public List<PSSearchResults> findItemsbyKeysFull(Map<String,String> fields, String type)
    throws Exception {
    	List<String> resultFields = new ArrayList<String>();
    	resultFields.add("sys_communityid");
    	return findItemsbyKeysFull(fields, resultFields, type);
    }
    	
    public List<PSSearchResults> findItemsbyKeysFull(Map<String,String> fields, List<String> resultFields, String type)
    throws Exception {
    	return findItemsbyKeysFull(fields, resultFields, type, null);
    }
  
    public List<PSSearchResults> findItemsbyKeysFull(Map<String,String> fields, List<String> resultFields, String type, String folder)
    throws Exception {
    	
    //  Could extend to pick out a version number. e.g. update if version higher
    	//  Would be faster if replaced with JEXL and/or done in batches
    	
    	PSSearch newSearch = new PSSearch();
    	
    	PSSearchParams newSearchParams = new PSSearchParams();
    	ArrayList<String> fieldNames = new ArrayList<String>(fields.keySet());
    	PSSearchField[] searchFields = new PSSearchField[fields.size()+1];
    	
    	PSSearchProperty[] searchProperties = new PSSearchProperty[1];
    	PSSearchProperty startIndex = new PSSearchProperty();
    	PSSearchProperty endIndex = new PSSearchProperty();
    	startIndex.setName("sys_maxsearchresults");
    	startIndex.set_value("100");
    	
    	searchProperties[0] = startIndex;
        
    	int j = 0;
    	for(String fieldName : fieldNames) {
    	PSSearchField newSearchField = new PSSearchField();
    	newSearchField.setName(fieldName);
    	newSearchField.setValue(fields.get(fieldName));
    	//log.debug("Field Name: " + fieldName);
    	//log.debug("Field Value : " + fields.get(fieldName));
    	searchFields[j++]=newSearchField;
    	
    	}
    	PSSearchField maxSearchField = new PSSearchField();
    	//This does not work
    	maxSearchField.setName("sys_maxsearchresults");
    	maxSearchField.setValue("100");
    	searchFields[j++]=maxSearchField;
    	if (folder != null && folder.startsWith("//")) {
    	PSSearchParamsFolderFilter folderFilter = new PSSearchParamsFolderFilter();
    		folderFilter.set_value(folder);
    		// TODO: make following configurable,  possibly using % at end of folder string
    		// the same as jcr query
    		folderFilter.setIncludeSubFolders(true);
    		newSearchParams.setFolderFilter(folderFilter);
    	}
    	
    	newSearchParams.setParameter(searchFields);
    	newSearchParams.setProperties(searchProperties);
    	if(type != null && type.length() > 0) {
    		newSearchParams.setContentType(type);
    	}
        FindItemsRequest request = new FindItemsRequest();
       
        request.setLoadOperations(false);
        newSearch.setPSSearchParams(newSearchParams);
        request.setPSSearch(newSearch);
        PSSearchResultField[] resultFieldsArray = new PSSearchResultField[resultFields.size()];
        
        for (int i=0; i <  resultFields.size(); i++) {
        	 resultFieldsArray[i]=new PSSearchResultField();
             resultFieldsArray[i].setName(resultFields.get(i));
        }
       
        newSearchParams.setSearchResults(resultFieldsArray);
        PSSearchResults[] results = context.getContentService().findItems(request);
      	List<PSSearchResults> returnList = new ArrayList<PSSearchResults>();
      	for (int i=0 ; i<results.length; i++) {
      		// Skip folders
      		if (!results[i].getContentType().getName().equals("Folder")) {
      			returnList.add(results[i]);
      		}
      	}
    	return returnList;
    }
    public long[] convertIds(List<Integer> ids) {
        long[] rvalue = new long[ids.size()];
        for (int i = 0; i < rvalue.length; i++) {
        	Guid guid = new Guid(ids.get(i));
            rvalue[i] = guid.getGuid();
        }
        return rvalue;
    }
    public long[] convertLongIds(List<Long> ids) {
        long[] rvalue = new long[ids.size()];
        for (int i = 0; i < rvalue.length; i++) {
            rvalue[i] = ids.get(i).longValue();
        }
        return rvalue;
    }
    
    public List<Integer> convertIds(long[] ids) {
        List<Integer> rvalue = new ArrayList<Integer>();
        for (long id : ids) {
        	Guid guid = new Guid();
        	guid.setGuid(id);
            rvalue.add(guid.getId());
        }
        return rvalue;
    }
 
    public void deleteItems (List<Integer> ids) throws Exception {
    	context.getContentService().deleteItems(convertIds(ids));
    }
    public void deleteFolders(List<String> folderPaths, boolean purgeItems) throws Exception{
    	List<Integer> folderids = new ArrayList<Integer>();
    	for (String folder : folderPaths) {
    		int id = findPathId(folder);
    		if(id != -1) {
    			folderids.add(id);
    		}
    	}
    	
    	if (folderids.size()>0) {
    		DeleteFoldersRequest req = new DeleteFoldersRequest();
    		req.setId(convertIds(folderids));
    		req.setPurgItems(purgeItems);
    		context.getContentService().deleteFolders(req);
    	}
 
    }
    
    public int  findPathId(String folderPath) throws Exception {
    	List<Integer> ids = findPathIds(folderPath);
    		int id = -1;
    	 if (ids.size()>0) {
    		id = ids.get(ids.size()-1);
    	 }
    	 return id;  
    }
    
    
    
    public List<Integer>  findPathIds(String folderPath) throws Exception {
    	 FindPathIdsRequest req = new FindPathIdsRequest();
    	   req.setPath(folderPath);
    	 
    	 FindPathIdsResponse resp = context.getContentService().findPathIds(req);
    	 return  convertIds(resp.getIds());
    	   
    }
    
    public List<PSItemSummary>  findFolderChildren(String folderPath, boolean loadOperation) throws Exception {
   	 FindFolderChildrenRequest req = new FindFolderChildrenRequest();
   	 
   	  FolderRef ref = new FolderRef();
   	  ref.setPath(folderPath);
   	  req.setFolder(ref);
   	 PSItemSummary[] itemSummaries = context.getContentService().findFolderChildren(req);
   	 
   	 return   Arrays.asList(itemSummaries);
   }
   
    public int findFolderNavon(String folderPath, List<String> navTypes) throws Exception {
    
    	List<PSItemSummary> summaries = findFolderChildren(folderPath, false);
    	int id=-1;
    	for (PSItemSummary summary : summaries) {
    		if (summary.getContentType()!= null) {
    			String typeName = summary.getContentType().getName();
    			//TODO: Configure nav content names
    			if(navTypes.contains(typeName)) {
    				id = Guid.getId(summary.getId());
    			}
    		}
    	}
    	return id;
    }
    
    public List<Integer> findFolderItemByType(String folderPath, String contentType) throws Exception {
        
    	List<PSItemSummary> summaries = findFolderChildren(folderPath, false);
    	List<Integer> ids = new ArrayList<Integer>();
    	for (PSItemSummary summary : summaries) {
    		if (summary.getContentType()!= null) {
    			String typeName = summary.getContentType().getName();
    			//TODO: Configure nav content names
    			if(typeName.equals(contentType)) {
    				ids.add(Guid.getId(summary.getId()));
    			}
    		}
    	}
    	return ids;
    }
    
    public List<PSTemplateSlot> loadSlots(String name) throws Exception {
    	LoadSlotsRequest req = new LoadSlotsRequest();
    	req.setName(name);
    	return Arrays.asList(context.getAssemblyService().loadSlots(req));
    }
    
    public List<PSAssemblyTemplate> loadAssemblyTemplates(String contentType, String name) throws Exception {
    	LoadAssemblyTemplatesRequest req = new LoadAssemblyTemplatesRequest();
    	req.setContentType(contentType);
    	req.setName(name);
    	return Arrays.asList(context.getAssemblyService().loadAssemblyTemplates(req));
    }
    public List<PSContentTypeSummary> loadContentTypes(String name) throws Exception {
    	LoadContentTypesRequest req = new LoadContentTypesRequest();
    	req.setName(name);
    
    	return Arrays.asList(context.getContentService().loadContentTypes(req));
    }
    
    
    public List<PSItem> loadItems(List<Integer> ids)
       throws Exception
     {
       LoadItemsRequest req = new LoadItemsRequest();
       req.setId(convertIds(ids));
       req.setIncludeBinary(false);
       req.setAttachBinaries(false);
       req.setIncludeChildren(true);
       PSItem[] items = context.getContentService().loadItems(req);
       return Arrays.asList(items);
    }
    
    public List<Integer> saveItems(List<PSItem> items)
    throws Exception
 {
    SaveItemsRequest req = new SaveItemsRequest();
    req.setPSItem(items.toArray(new PSItem[] {}));
    
    SaveItemsResponse response = context.getContentService().saveItems(req);
    Stub stub = (Stub) context.getContentService();
    
    stub.clearAttachments();
	
    return convertIds(response.getIds());
 }

 
 public void clearAttachements()
 throws Exception
{
	 Stub stub = (Stub) context.getContentService();
	 stub.clearAttachments();
}

    
    /**
     * Creates Folders for the specified Folder path.  Any Folders specified in 
     * the path that do not exist will be created; No action is taken on any 
     * existing Folders.
     *  
     * @param folderPath the Folder path to be updated; assumed not to be
     *    <code>null</code> or empty.
     * 
     * @return the created folder objects, never <code>null</code>, may be empty.
     * 
     * @throws Exception if an error occurs.
     */
    public  List<PSFolder> addFolderTree(String folderPath) throws Exception
    {
       AddFolderTreeRequest req = new AddFolderTreeRequest();
       req.setPath(folderPath);
       return Arrays.asList(context.getContentService().addFolderTree(req));
    }
    
    
    /**
     * Associates the specified Content Items with the specified Folder.
     * 
     * @param folderPath the path of the Folder to which you want to add the 
     *    child objects;, assumed not to be <code>null</code> or empty.
     * @param childIds the IDs of the objects to be associated with the Folder 
     *    specified in the folderPath parameter; assumed not <code>null</code> or 
     *    empty.
     *    
     * @throws Exception if an error occurs.
     */
    public void addFolderChildren(FolderRef folderRef, List<Integer> childIds) throws Exception
    {
       AddFolderChildrenRequest req = new AddFolderChildrenRequest();
       req.setChildIds(convertIds(childIds));
       req.setParent(folderRef);
       context.getContentService().addFolderChildren(req);
    }
    
  
    
    
    public List<PSChildEntry> createChildEntries(int contentID, String childSetName, int numEntries) 
    throws Exception {
       CreateChildEntriesRequest request = new CreateChildEntriesRequest();
       request.setId(contentID);
       request.setName(childSetName);
       request.setCount(numEntries);
       PSChildEntry[] psChildEntries = context.getContentService().createChildEntries(request);
       return Arrays.asList(psChildEntries);
    
    }
    
    public List<PSChildEntry> loadChildEntries(int contentID, String name, boolean includeBinaries, boolean attachBinaries) 
    throws Exception {
       LoadChildEntriesRequest request = new LoadChildEntriesRequest();
       request.setId(contentID);
       request.setName(name);
       request.setIncludeBinaries(includeBinaries);
       request.setAttachBinaries(attachBinaries);
       PSChildEntry[] psChildEntries = context.getContentService().loadChildEntries(request);
       return Arrays.asList(psChildEntries);
    }
    
    public void deleteChildEntries(int contentID, String name, List<Integer> ids) 
    throws Exception {
       DeleteChildEntriesRequest request = new DeleteChildEntriesRequest();
       request.setChildId(convertIds(ids));
       request.setName(name);
       request.setId(new Guid(contentID).getGuid());
       context.getContentService().deleteChildEntries(request);
       return;
    }
    
    public List<String> findFolderPaths(int id) 
    throws Exception {
       FindFolderPathRequest request = new FindFolderPathRequest();
       request.setId(id);
     

      FindFolderPathResponse response = context.getContentService().findFolderPath(request);
      return Arrays.asList(response.getPaths());
    }
    
    public List<PSFolder> loadFolders(List<String> paths) 
    throws Exception {
       LoadFoldersRequest request = new LoadFoldersRequest();
      request.setPath(paths.toArray(new String[] {}));
     
      PSFolder[] response = context.getContentService().loadFolders(request);
      return Arrays.asList(response);
    }
    
    public void removefolderChildren(FolderRef folderRef, List<Integer> childIds, boolean purgeItems) throws Exception
    {
    	RemoveFolderChildrenRequest request = new RemoveFolderChildrenRequest();
        request.setChildIds(convertIds(childIds));
        request.setParent(folderRef);
        request.setPurgeItems(purgeItems);
       context.getContentService().removeFolderChildren(request);
      return;
    }
    
    public List<PSItemSummary> findChildItems(int id, PSAaRelationshipFilter filter, boolean loadOperations) throws Exception
    {
    	FindChildItemsRequest request = new FindChildItemsRequest();
        request.setId(new Guid(id).getGuid());
        request.setPSAaRelationshipFilter(filter);
        request.setLoadOperations(loadOperations);
       PSItemSummary[] results = context.getContentService().findChildItems(request);
      return Arrays.asList(results);
    }
    public  List<PSAaRelationship>loadContentRelations(PSAaRelationshipFilter filter) throws Exception
    {
       LoadContentRelationsRequest request = new LoadContentRelationsRequest();
       request.setPSAaRelationshipFilter(filter);
       PSAaRelationship[] results = context.getContentService().loadContentRelations(request);
       return Arrays.asList(results);
    }
    
    public  void deleteContentRelations(List<Long> ids) throws Exception
    {
       context.getContentService().deleteContentRelations(convertLongIds(ids));
       return;
    }
    
    public void saveChildEntries(List<PSChildEntry> childEntries, int contentID, String childSetName) 
    throws Exception {
       SaveChildEntriesRequest request = new SaveChildEntriesRequest();
       request.setId(contentID);
       PSChildEntry[] childArray = new PSChildEntry[childEntries.size()];
       request.setPSChildEntry(childEntries.toArray(childArray));
       request.setName(childSetName);

       context.getContentService().saveChildEntries(request);
     
    
    }
    public List<PSAaRelationship> addContentRelations(Integer contentId,
    		List<Integer> relatedIds,
            Integer folderId,
            Integer siteId,
            String slot,
            String template,
            Integer index)
            throws Exception {
    	boolean update=false;
    	List<PSAaRelationship> newRels = addContentRelations(contentId,relatedIds, slot, template, index);
    	if (folderId >0) {
    	PSAaRelationshipFolder folder = new PSAaRelationshipFolder();
    	Guid folderGuid = new Guid();
        folderGuid.setId(folderId);
        folder.setId(folderGuid.getGuid());
    	newRels.get(0).setFolder(folder);
    	update=true;
    	}
    	if (siteId >0) {
    		Reference ref = new Reference();
    		Guid siteGuid = new Guid();
            siteGuid.setId(siteId);
            ref.setId(siteGuid.getGuid());
    		newRels.get(0).setSite(ref);
    		update=true;
    	}
    	if (update) {
    	   context.getContentService().saveContentRelations(newRels.toArray(new PSAaRelationship[] {}));
    	}
    	return newRels;
    }
            
    public List<PSAaRelationship> addContentRelations(Integer contentID,List<Integer> relatedIds,
    		String slot, 
            String template, Integer index)
    throws Exception {
     
       AddContentRelationsRequest request = new AddContentRelationsRequest();
   
       Guid guid = new Guid();
       guid.setGuid(contentID);
     
       request.setId(guid.getGuid());
       request.setSlot(slot);
       request.setRelatedId(convertIds(relatedIds));
       request.setTemplate(template);
       request.setIndex(index);
       request.setRelationshipConfig("ActiveAssembly");
       PSAaRelationship[] relationships = context.getContentService().addContentRelations(request);
       return Arrays.asList(relationships);

    }
    
    public void checkinItems(List<Integer> ids, String comment) throws Exception {
        CheckinItemsRequest request = new CheckinItemsRequest();
        request.setId(convertIds(ids));
        request.setComment(comment);
        context.getContentService().checkinItems(request);
        
    }
    
    public List<PSAuditTrail> loadAuditTrails(List<Integer> ids) throws Exception {
    
    	PSAuditTrail[] auditTrail = context.getSystemService().loadAuditTrails(convertIds(ids));
    	return (Arrays.asList(auditTrail));
    }
    
 public List<String> getAllowedTransitions(List<Integer> ids) throws Exception {
    	
    	GetAllowedTransitionsResponse resp = context.getSystemService().getAllowedTransitions(convertIds(ids));
    	return (Arrays.asList(resp.getTransition()));
    }
 
 public List<String> transitionItems(List<Integer> ids, String transition) throws Exception {
 	
	 TransitionItemsRequest req = new TransitionItemsRequest();
	 req.setId(convertIds(ids));
	 req.setTransition(transition);
	
	 TransitionItemsResponse resp = context.getSystemService().transitionItems(req);
 	return (Arrays.asList(resp.getStates()));
 }
 
 public List<PSWorkflow> getWorkflows(String name) throws Exception {
	 	
	 LoadWorkflowsRequest req = new LoadWorkflowsRequest();
	 req.setName(name);
	
	 PSWorkflow[] resp = context.getSystemService().loadWorkflows(req);
 	return (Arrays.asList(resp));
 }
 
 public void switchCommunity(String name) throws Exception {
	 
	 if(!context.getCommunityName().equals(name)){
		 SwitchCommunityRequest req = new SwitchCommunityRequest();
		 req.setName(name);

		 context.setCommunityName(name);
		 context.getSystemService().switchCommunity(req);
	 }
}
 
 
 
 
}