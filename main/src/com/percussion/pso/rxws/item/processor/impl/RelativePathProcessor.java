/**
 * 
 */
package com.percussion.pso.rxws.item.processor.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.percussion.pso.importer.model.ImportBatch;
import com.percussion.pso.importer.model.ImportItem;
import com.percussion.pso.importer.model.ImportRelationship;
import com.percussion.pso.rxws.item.processor.RxWsItemProcessor;

public class RelativePathProcessor implements RxWsItemProcessor {

    /**
     * The log instance to use for this class, never <code>null</code>.
     */
    @SuppressWarnings("unused")
    private final Log log = LogFactory.getLog(RelativePathProcessor.class);
     
    
    public ImportBatch  processItems(ImportBatch  items) 
    throws Exception {
    	
    	//  Figure out absolute paths based upon relationships  
    	ImportBatch extraItems = new ImportBatch();
    	for(ImportItem item : items) {
    		if(item.getRelationships().size()>0) {
    			Set<String> paths = item.getPaths();
    			List<ImportRelationship> rels = item.getRelationships();
    			Set<String> newPaths = new HashSet<String>();
    			for(ImportRelationship rel : rels) {
    				Set<String> relPaths = rel.getItem().getPaths();
    				for(String path : relPaths) {
    					if(!path.startsWith("/")) {
    						for(String parentpath : paths) {
    							newPaths.add(parentpath + "/" + path);
    						}
    					} else {
    						newPaths.add(path);
    					}
    				}
    				
    			}
    			if (newPaths.size()>0) {
    				item.setPaths(newPaths);
    			}
    			
    		
    		}
    	}
    	items.addAll(extraItems);
    	return items;
    }

 

    
}