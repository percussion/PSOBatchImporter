package com.percussion.pso.importer;

import java.util.Iterator;

import com.percussion.pso.importer.model.ImportBatch;
import com.percussion.pso.rxws.item.RxWsContext;

/**
 * ImportItem consumers consume items. 
 * Usually used for importing items into a system.
 * @author adamgent
 */
public interface IImportItemConsumer {
    /**
     * Consumes the iterator of items.
     * 
     * @param importItems
     * @param importContext should not be <code>null</code>.
     * @throws Exception
     */
    public void consume(Iterator<ImportBatch> importItems) throws Exception;
}
