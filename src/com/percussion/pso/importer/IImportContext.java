package com.percussion.pso.importer;

import com.percussion.pso.importer.model.ImportItem;
import com.percussion.pso.importer.model.ImportRelationship;

/**
 * Used to represent a user context.
 * This could be a database context, 
 * web service connection, JCR context, so on...
 * @author adamgent
 *
 */
public interface IImportContext {
	public String getUser();
	public String getPassword();
	public String getSessionId();
    public ImportItem createImportItem(String producerId);
    public ImportRelationship createImportRelationship(String producerId);
    public IImportLog getImportLog();
}
