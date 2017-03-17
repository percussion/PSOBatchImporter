package com.percussion.pso.importer;

import java.util.List;

import com.percussion.pso.importer.model.ImportBatch;
import com.percussion.pso.rxws.item.RxWsContext;

/**
 * Generically item processors process a list of items.
 * This is typically used to perform some kind of action to the 
 * items using the context.
 * ImportItem processors can be chained since {@link #processItems(List, IImportContext)}
 * returns a list of items. Consequently you can setup a series of 
 * item processors to perform work one after the other. 
 * @author adamgent
 * 
 * @param <T>
 */
public interface IImportItemProcessor {
    /**
     * 
     * @param items
     * @param context
     * @return the list of processed items.
     * @throws Exception
     */
    public ImportBatch processItems(ImportBatch items) throws Exception;
}
