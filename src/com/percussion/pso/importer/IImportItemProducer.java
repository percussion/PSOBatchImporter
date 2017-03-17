package com.percussion.pso.importer;

import java.util.Iterator;

import com.percussion.pso.importer.model.ImportBatch;
import com.percussion.pso.rxws.item.RxWsContext;

/**
 * Produces items.
 * Hides the implementation of where the items come from.
 * 
 * @see IImportItemConsumer
 * @author adamgent
 *
 */
public interface IImportItemProducer {
    /**
     * Produces items for consumption.
     * @param importContext maybe <code>null</code>
     * @return an Iterator of items.
     * @throws Exception
     */ 
    public Iterator<ImportBatch> produce() throws Exception;
}
