package com.percussion.pso.importer.impl;

import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.percussion.pso.importer.IImportItemConsumer;
import com.percussion.pso.importer.IImportItemProcessor;
import com.percussion.pso.importer.model.ImportBatch;
import com.percussion.pso.importer.model.ImportItem;
import com.percussion.pso.importer.manager.ItemLocatorManager;
/**
 * An item consumer used by the batch importer that uses a single item processor
 * to do its work.
 * 
 * @see PipelineItemsProcessor
 * @see IImportItemProcessor
 * @see IImportItemConsumer
 * @author adamgent
 * 
 */
public class ItemConsumer implements IImportItemConsumer {

	private ItemLocatorManager locatorManager;
	private boolean clearLocatorsOnBatch;
	/**
	 * The log instance to use for this class, never <code>null</code>.
	 */
	private static final Log log = LogFactory.getLog(ItemConsumer.class);

	private IImportItemProcessor itemsProcessor;
	private int maxBatchSize = 100;

	@SuppressWarnings("unchecked")
	public void consume(Iterator<ImportBatch> importBatches) throws Exception {

		if (importBatches == null)
			throw new IllegalArgumentException("Cannot consume null items");

		for (; importBatches.hasNext();) {

			ImportBatch importBatch = importBatches.next();
			int noItems = 0;
			ImportBatch newImportBatch = new ImportBatch();
			
			for (ImportItem items : importBatch) {
				if (++noItems % maxBatchSize == 0) {
					processBatch(newImportBatch);
					newImportBatch = new ImportBatch();
					if (clearLocatorsOnBatch) locatorManager.clearLocators();
				}
				newImportBatch.add(items);
			}
			processBatch(newImportBatch);
			if (clearLocatorsOnBatch) locatorManager.clearLocators();
		}

	}

	private void processBatch(ImportBatch batch) {
		try {
			itemsProcessor.processItems(batch);
		} catch (Exception e) {
			log.debug("Batch failed", e);
		}
	}

	public IImportItemProcessor getItemsProcessor() {
		return itemsProcessor;
	}

	public void setItemsProcessor(IImportItemProcessor importItemProcessor) {
		this.itemsProcessor = importItemProcessor;
	}

	public int getMaxBatchSize() {
		return maxBatchSize;
	}

	public void setMaxBatchSize(int maxBatchSize) {
		this.maxBatchSize = maxBatchSize;
	}

	public void setLocatorManager(ItemLocatorManager locatorManager) {
		this.locatorManager = locatorManager;
	}

	public void setClearLocators(boolean clearLocatorsOnBatch) {
		this.clearLocatorsOnBatch = clearLocatorsOnBatch;
	}
	
}
