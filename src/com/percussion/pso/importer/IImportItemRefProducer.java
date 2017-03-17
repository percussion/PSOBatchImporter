package com.percussion.pso.importer;

import java.util.Iterator;

/**
 * Produces items.
 * Hides the implementation of where the items come from.
 * 
 * @see IImportItemConsumer
 * @author adamgent
 *
 */
public interface IImportItemRefProducer<T> {
    /**
     * Produces references to items for consumption.
     * @param importContext maybe <code>null</code>
     * @return an Iterator of items.
     * @throws Exception
     */ 
    public Iterator<T> produce() throws Exception;
}
