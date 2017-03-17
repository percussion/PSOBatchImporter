package com.percussion.pso.importer;

import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.percussion.pso.importer.model.ImportBatch;
public class ImportJob implements IImportJob {
    private IImportItemConsumer importItemConsumer;
    private IImportItemProducer importItemProducer;
	
 
	private String name;
   
	/**
     * The log instance to use for this class, never <code>null</code>.
     */
   private static final Log log = LogFactory.getLog(ImportJob.class);
   
    public String getName() 
    {
		return name;
	}



	public void setName(String name) 
	{
		this.name = name;
	}
	 
		

   public IImportItemConsumer getImportItemConsumer() {
		return importItemConsumer;
	}



	public void setImportItemConsumer(IImportItemConsumer importItemConsumer) {
		this.importItemConsumer = importItemConsumer;
	}



	public IImportItemProducer getImportItemProducer() {
		return importItemProducer;
	}



	public void setImportItemProducer(IImportItemProducer importItemProducer) {
		this.importItemProducer = importItemProducer;
	}


	public void runJob() {
		try {
		    log.info("Starting Job -->" +":"+getName() +":"+Thread.currentThread().getId() + ":"+this.hashCode());
			
			Iterator<ImportBatch> importItems = importItemProducer.produce();
	        	if(getImportItemConsumer() != null) {
		    		log.info("Consuming items");
		    		getImportItemConsumer().consume(importItems);
		            log.info("Consumed items");
	        	}
	        	else {
	        		log.warn("Consumer is not set");
	        		while(importItems.hasNext()) importItems.next();
	        	}
			} catch (Exception e) {
				log.info("Import Job failed ", e);
			}		
	}
}
