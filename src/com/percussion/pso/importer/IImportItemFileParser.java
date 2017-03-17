package com.percussion.pso.importer;

import com.percussion.pso.importer.model.ImportBatch;
import com.percussion.pso.rxws.item.RxWsContext;


public interface IImportItemFileParser {
    public ImportBatch getItems(String url) throws Exception;
}