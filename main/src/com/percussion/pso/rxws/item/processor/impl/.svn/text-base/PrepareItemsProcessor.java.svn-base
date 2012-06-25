/**
 * 
 */
package com.percussion.pso.rxws.item.processor.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.percussion.pso.importer.manager.ItemManager;
import com.percussion.pso.importer.model.ImportBatch;
import com.percussion.pso.rxws.item.processor.RxWsItemProcessor;

public class PrepareItemsProcessor implements RxWsItemProcessor {

    /**
     * The log instance to use for this class, never <code>null</code>.
     */
    private final Log log = LogFactory.getLog(PrepareItemsProcessor.class);
    ItemManager manager;
    
    public ImportBatch  processItems(ImportBatch  items) 
    throws Exception {
    	//manager.createNav(items);
    	manager.resetLocators();
    	log.debug("preparing items");
    	manager.prepareForEdit(items);
    	return items;
    }

	public ItemManager getManager() {
		return manager;
	}

	public void setManager(ItemManager manager) {
		this.manager = manager;
	}

 

    
}