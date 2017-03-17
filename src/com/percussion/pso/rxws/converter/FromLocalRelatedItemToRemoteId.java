package com.percussion.pso.rxws.converter;

import com.percussion.pso.importer.converter.Converter;
import com.percussion.pso.importer.model.ImportRelationship;
import com.percussion.pso.rxws.item.RxWsContext;

/**
 * Converts related items into their remote ids.
 * 
 * When getting the {@link IImportItem} that corresponds to this {@link ImportRelationship} the
 * typical approach is to try the following fields in order and pick 
 * the first non null value.:
 * <ol>
 * <li>{@link #getItem()} - an ImportItem instance of the related item</li>
 * <li>{@link #getId()} - the unique id of the item which is then used to get an instance.</li>
 * <li>{@link #getFolderPath()} - the path of the item</li>
 * <li>{@link #getProperties()} custom - Using meta properties and a custom resolver</li>
 * </ol>
 * We need to change this to convert them to PSRelationship[]
 * 
 * @author adamgent
 *
 */
public interface FromLocalRelatedItemToRemoteId 
    extends Converter<ImportRelationship, Integer, Integer, RxWsContext> {

}
