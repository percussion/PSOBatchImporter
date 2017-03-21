/**
 * 
 */
package com.percussion.pso.rxws.item.processor.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.percussion.pso.importer.manager.ItemManager;
import com.percussion.pso.importer.model.ImportBatch;
import com.percussion.pso.rxws.item.processor.RxWsItemProcessor;

public class UpdateItemsProcessor implements RxWsItemProcessor {

    /**
     * The log instance to use for this class, never <code>null</code>.
     */
    private final Log log = LogFactory.getLog(UpdateItemsProcessor.class);
    ItemManager manager;
    private boolean reloadItem = true;
    
    public ImportBatch  processItems(ImportBatch  items) 
    throws Exception {
    	
    	
    	log.debug("updating items");
    	manager.updateRx(items);
    	log.debug("updating paths");
    	manager.updatePaths(items);
     	log.debug("updating relationships");
    	manager.updateRelationships(items);
    	log.debug("passing "+items.size()+ " items");
    	return items;
    }

	public ItemManager getManager() {
		return manager;
	}

	public void setManager(ItemManager manager) {
		this.manager = manager;
	}

 
	public void setReloadItem(boolean reloadItem) {
		this.reloadItem = reloadItem;
	}
	public boolean getReloadItem() {
		return this.reloadItem;
	}
    
}