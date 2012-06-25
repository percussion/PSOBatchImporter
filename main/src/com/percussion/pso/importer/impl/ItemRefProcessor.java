package com.percussion.pso.importer.impl;

import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.percussion.pso.importer.IImportItemProducer;
import com.percussion.pso.importer.IImportItemRefConsumer;
import com.percussion.pso.importer.IImportItemRefProducer;
import com.percussion.pso.importer.model.ImportBatch;

public class ItemRefProcessor implements IImportItemProducer,
		Iterator<ImportBatch> {

	/**
	 * The log instance to use for this class, never <code>null</code>.
	 */
	private static final Log log = LogFactory.getLog(ItemRefProcessor.class);

	private IImportItemRefProducer<Object> refProducer;
	private IImportItemRefConsumer<Object> refConsumer;

	public IImportItemRefConsumer<Object> getRefConsumer() {
		return refConsumer;
	}

	public void setRefConsumer(IImportItemRefConsumer<Object> refConsumer) {
		this.refConsumer = refConsumer;
	}

	Iterator<Object> sourceIterator = null;
	Iterator<ImportBatch> currentIterator = null;

	public Iterator<ImportBatch> produce() throws Exception {
		log.debug("In ItemRefProcessor:producer");
		sourceIterator = refProducer.produce();
		currentIterator = null;
		return this;
	}

	public IImportItemRefProducer<Object> getRefProducer() {
		return refProducer;
	}

	public void setRefProducer(IImportItemRefProducer<Object> refProducer) {
		this.refProducer = refProducer;
	}

	public boolean hasNext() {
		if (currentIterator == null) {
			if (sourceIterator.hasNext()) {
				refConsumer.setItemRef(sourceIterator.next());
				try {
					currentIterator = refConsumer.produce();
				} catch (Exception e) {
					log.debug("Error producing items ", e);
					return false;
				}
			} else {
				return false;
			}
		}

		while (!currentIterator.hasNext() && sourceIterator.hasNext()) {

			refConsumer.setItemRef(sourceIterator.next());
			try {
				currentIterator = refConsumer.produce();
			} catch (Exception e) {
				log.debug("Error producing items ", e);
				return false;
			}

		}

		return currentIterator.hasNext();
	}

	public ImportBatch next() {
		this.hasNext();
		return currentIterator.next();
	}

	public void remove() {

	}

}
