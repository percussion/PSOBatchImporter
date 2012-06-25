/**
 * 
 */
package com.percussion.pso.rxws.item.processor.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.percussion.pso.importer.manager.ItemManager;
import com.percussion.pso.importer.model.ImportBatch;
import com.percussion.pso.rxws.item.processor.RxWsItemProcessor;

public class LoadItemsProcessor implements RxWsItemProcessor {

    /**
     * The log instance to use for this class, never <code>null</code>.
     */
    private final Log log = LogFactory.getLog(LoadItemsProcessor.class);
    ItemManager manager;
    
    public ImportBatch  processItems(ImportBatch  items) 
    throws Exception {
    	    	
    	log.debug("Loading Items");
    	manager.load(items);
    	
    	return items;
    }

	public ItemManager getManager() {
		return manager;
	}

	public void setManager(ItemManager manager) {
		this.manager = manager;
	}

 

    
}