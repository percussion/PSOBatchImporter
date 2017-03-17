/**
 * 
 */
package com.percussion.pso.rxws.item.processor.impl;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.percussion.pso.importer.manager.ItemManager;
import com.percussion.pso.importer.manager.SiteDefinition;
import com.percussion.pso.importer.model.FieldMap;
import com.percussion.pso.importer.model.ImportBatch;
import com.percussion.pso.importer.model.ImportItem;
import com.percussion.pso.rxws.item.processor.RxWsItemProcessor;

public class PrepareNavProcessor implements RxWsItemProcessor {

    /**
     * The log instance to use for this class, never <code>null</code>.
     */
    private final Log log = LogFactory.getLog(PrepareNavProcessor.class);
    ItemManager manager;
    private String navTreeType="rffNavTree";
    private String navonType="rffNavon";
    private Map<String,String> navTreeDefaultFields;
    public Map<String, String> getNavTreeDefaultFields() {
		return navTreeDefaultFields;
	}

	public void setNavTreeDefaultFields(Map<String, String> navTreeDefaultFields) {
		this.navTreeDefaultFields = navTreeDefaultFields;
	}

	private List<SiteDefinition> siteDefinitions;
    
    public ImportBatch  processItems(ImportBatch  items) 
    throws Exception {
    	
    	manager.getLocatorManager().setNavonType(navonType);
    	manager.getLocatorManager().setNavTreeType(navTreeType);
    	
    	log.debug("preparing navtree items");
    	for(ImportItem item: items) {
    		if(item.getType()!=null && (item.getType().equals(navTreeType)|| item.getType().equals(navonType))) {
    		// Should be one and only one path for navon or navtree.
    		
    			String itemPathString = item.getPaths().iterator().next();
    		
    			ImportItem newItem = new ImportItem();
	    		newItem.setType(navTreeType);
	    		newItem.setKeyFields(Collections.singletonList("navigation"));
	    	    FieldMap newFieldMap = new FieldMap();
	    	    for(String fieldName : navTreeDefaultFields.keySet()) {
	    	    	newFieldMap.put(fieldName, navTreeDefaultFields.get(fieldName));
	    	    }
	    	    log.debug("Path to create is "+itemPathString);
	    	    manager.createFolderTree(itemPathString);
	    	    newItem.setFields(newFieldMap);
    			for(SiteDefinition def : siteDefinitions) {
    				if (!def.isNavTreeExists() && itemPathString.startsWith(def.getPath())) {
    				
    				Set<String> paths = new HashSet<String>();
    				paths.add(def.getPath());
    				newItem.setCommunityName(def.getNavTreeCommunity());
    				newItem.setPaths(paths);
    		        newItem.getFields().put("sys_title", def.getNavTreeTitle());
    				if (manager.createNav(newItem, navTreeType)) def.setNavTreeExists(true);
    			}
    			}
    		}
    	}
    	
    	//manager.prepareForEdit(items);
    	return items;
    }

	public List<SiteDefinition> getSiteDefinitions() {
		return siteDefinitions;
	}

	public void setSiteDefinitions(List<SiteDefinition> siteDefinitions) {
		this.siteDefinitions = siteDefinitions;
	}

	public ItemManager getManager() {
		return manager;
	}

	public void setManager(ItemManager manager) {
		this.manager = manager;
	}

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

    
}