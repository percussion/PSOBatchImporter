package com.percussion.pso.rxws.item.processor.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.percussion.pso.importer.model.ImportBatch;
import com.percussion.pso.importer.model.ImportItem;
import com.percussion.pso.importer.model.ImportRelationship;
import com.percussion.pso.rxws.item.processor.RxWsItemProcessor;

public class ExtractRelatedItemsProcessor implements RxWsItemProcessor {

    /**
     * The log instance to use for this class, never <code>null</code>.
     */
    @SuppressWarnings("unused")
    private final Log log = LogFactory.getLog(ExtractRelatedItemsProcessor.class);

    
    public ImportBatch  processItems(ImportBatch  items) 
    throws Exception {
    	//manager.createNav(items);
    	log.debug("Extracting related items to be imported");
    	List<ImportItem> addList = new ArrayList<ImportItem>();
    	for (ImportItem item : items) {
			if (item.getRelationships() != null) {
				for (ImportRelationship rel : item.getRelationships()) {
					addList.add(rel.getItem());
				}
			}
    	}
    	log.debug("Adding "+addList.size()+" related items");
    	 items.addAll(addList);
    	return items;
    }
}
