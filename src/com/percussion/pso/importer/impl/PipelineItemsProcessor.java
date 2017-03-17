package com.percussion.pso.importer.impl;

import java.util.List;

import com.percussion.pso.importer.IImportItemProcessor;
import com.percussion.pso.importer.model.ImportBatch;

/**
 * An items processor that contains a list of items processors to be run against
 * the items in order.
 * 
 * For example if the itemsProcessor list contains two processors:
 * <code>createItemsProcessor</code> and <code>createFolderPathsProcessor</code>
 * in that order it will first process the items through the
 * <code>createItemsProcessor</code>, then
 * <code>createFolderPathsProcessor</code>
 * 
 * @author adamgent
 * 
 */
public class PipelineItemsProcessor implements IImportItemProcessor {

	private List<IImportItemProcessor> importItemProcessors;

	@SuppressWarnings("unchecked")
	public ImportBatch processItems(ImportBatch items) throws Exception {
		if (importItemProcessors == null || importItemProcessors.size() < 0) {
			throw new RuntimeException("You need atleast one items processor");
		}
		for (IImportItemProcessor processor : importItemProcessors) {
			// There is a chance you could get a class cast exception here
			if (items.size() > 0) {
				items = processor.processItems(items);
			}
		}
		return items;
	}

	public List<IImportItemProcessor> getItemProcessors() {
		return importItemProcessors;
	}

	public void setItemProcessors(
			List<IImportItemProcessor> importItemProcessors) {
		this.importItemProcessors = importItemProcessors;
	}

}
