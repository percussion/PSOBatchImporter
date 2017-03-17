package com.percussion.pso.rxws.converter;

import com.percussion.pso.importer.converter.Converter;
import com.percussion.pso.importer.model.ImportItem;
import com.percussion.pso.rxws.item.RxWsContext;
import com.percussion.webservices.content.PSItem;

public interface FromLocalItemToRemoteItem extends Converter<ImportItem, PSItem, PSItem, RxWsContext> {

}