package com.percussion.pso.importer.impl;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.percussion.pso.importer.IImportItemFileParser;
import com.percussion.pso.importer.IImportItemProducer;
import com.percussion.pso.importer.model.ImportBatch;
import com.percussion.pso.rxws.item.RxWsContext;

public class ItemProducer implements IImportItemProducer, Iterator<ImportBatch> {
	  /**
     * The log instance to use for this class, never <code>null</code>.
     */
    private static final Log log = LogFactory.getLog(ItemProducer.class);

    private IImportItemFileParser importItemFileParser;
    
    private List<String> files;
    private Iterator<String> fileIterator;
    private RxWsContext importContext;
    public RxWsContext getImportContext() {
		return importContext;
	}

	public void setImportContext(RxWsContext importContext) {
		this.importContext = importContext;
	}

	public Iterator<ImportBatch> produce()   throws Exception {
      
       fileIterator = files.iterator();
        return this;
        
    }

    public IImportItemFileParser getItemParser() {
        return importItemFileParser;
    }

    public void setItemParser(IImportItemFileParser iImportItemFileParser) {
        this.importItemFileParser = iImportItemFileParser;
    }

    public List<String> getFiles() {
        return files;
    }

    public void setFiles(List<String> itemXmlFiles) {
        this.files = itemXmlFiles;
    }

	public boolean hasNext() {
		return fileIterator.hasNext();
	}

	public ImportBatch next() {
		String fname = fileIterator.next();
		ImportBatch importItems = new ImportBatch();
		
		try {
			importItems = importItemFileParser.getItems(fname);
		} catch (Exception e) {
			importItems.addError("Cannot get Import Batch for "+fname);
			log.debug("Cannot get Import Batch for "+fname);
		}
        return importItems;
	}

	public void remove() {
		// TODO Auto-generated method stub
		
	}

}
