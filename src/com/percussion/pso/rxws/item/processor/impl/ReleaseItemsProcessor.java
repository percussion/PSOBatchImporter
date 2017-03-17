/**
 * 
 */
package com.percussion.pso.rxws.item.processor.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.percussion.pso.importer.manager.ItemManager;
import com.percussion.pso.importer.model.ImportBatch;
import com.percussion.pso.rxws.item.processor.RxWsItemProcessor;

public class ReleaseItemsProcessor implements RxWsItemProcessor {

    /**
     * The log instance to use for this class, never <code>null</code>.
     */
    private final Log log = LogFactory.getLog(ReleaseItemsProcessor.class);
    private ItemManager manager;
    private boolean checkinOnly = false;
   

	public ImportBatch  processItems(ImportBatch  items) 
    throws Exception {
    
    	log.debug("releasing items");
    	manager.releaseFromEdit(items, checkinOnly);
    	log.debug("finished");
    	return items;
    }

	public ItemManager getManager() {
		return manager;
	}

	public void setManager(ItemManager manager) {
		this.manager = manager;
	}

 
	public boolean isCheckinOnly() {
			return checkinOnly;
	}

	public void setCheckinOnly(boolean checkinOnly) {
		this.checkinOnly = checkinOnly;
	}
    
}