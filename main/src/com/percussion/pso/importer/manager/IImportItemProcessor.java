package com.percussion.pso.importer.manager;

import com.percussion.pso.importer.model.ImportItem;
import com.percussion.webservices.content.PSItem;

public interface IImportItemProcessor {
	 enum Direction {
	        LEFT, RIGHT
	 }

	public void process(ImportItem localItem , PSItem remoteItem, Direction direction) throws Exception;
}
