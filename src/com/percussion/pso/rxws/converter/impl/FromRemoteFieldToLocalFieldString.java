/**
 * 
 */
package com.percussion.pso.rxws.converter.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.percussion.pso.importer.model.ImportField;
import com.percussion.pso.rxws.converter.FromRemoteFieldToLocalField;
import com.percussion.pso.rxws.item.RxWsContext;
import com.percussion.webservices.content.PSField;
import com.percussion.webservices.content.PSFieldValue;


public class FromRemoteFieldToLocalFieldString implements FromRemoteFieldToLocalField {
    
	/**
	 * The log instance to use for this class, never <code>null</code>.
	 */
	private static final Log log = LogFactory.getLog(FromRemoteFieldToLocalFieldImpl.class);


	
    public ImportField convert(String fieldName, String contentTypeName, PSField source, ImportField initial, RxWsContext context) {
        PSFieldValue value = source.getPSFieldValue(0);
        initial.setName(fieldName);
        initial.setStringValue(value.getRawData());
        log.debug("field name="+fieldName+" got string value=" + value.getRawData());
        return initial;
    }
}