/**
 * 
 */
package com.percussion.pso.rxws.converter;

import com.percussion.pso.importer.model.ImportField;
import com.percussion.pso.rxws.item.RxWsContext;
import com.percussion.webservices.content.PSField;

public interface FromRemoteFieldToLocalField  {
	 ImportField convert(String fieldName, String type, PSField source, ImportField initial, RxWsContext context);
}