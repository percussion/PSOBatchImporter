/**
 * 
 */
package com.percussion.pso.rxws.converter.impl;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.percussion.pso.importer.model.ImportField;
import com.percussion.pso.rxws.converter.FromRemoteFieldToLocalField;
import com.percussion.pso.rxws.item.RxWsContext;
import com.percussion.webservices.content.PSField;

public class FromRemoteFieldToLocalFieldImpl implements FromRemoteFieldToLocalField {
    /**
     * The log instance to use for this class, never <code>null</code>.
     */
    private static final Log log = LogFactory.getLog(FromRemoteFieldToLocalFieldImpl.class);

    /**
     * Field name, converter
     * */ 
	private Map<String,FromRemoteFieldToLocalField> converterFieldNameMap;
    
   
    public Map<String, FromRemoteFieldToLocalField> getConverterFieldNameMap() {
		return converterFieldNameMap;
	}


	public void setConverterFieldNameMap(
			Map<String, FromRemoteFieldToLocalField> converterFieldNameMap) {
		this.converterFieldNameMap = converterFieldNameMap;
	}


	public ImportField convert(String fieldName, String contentType, PSField source, ImportField initial, RxWsContext context) {
        //TODO: Make this dispatch based on content type
       /* log.debug("Converting field = " + source.getName() 
                + "\n MIME Type = " +  source.getMimeType()
                + "\n Content Type = " + source.getContentType()
                + "\n Source Type = " + source.getSourceType());
    	*/
    	ImportField field;
    	if (converterFieldNameMap != null && converterFieldNameMap.containsKey(fieldName)) {
    		FromRemoteFieldToLocalField converter = converterFieldNameMap.get(fieldName);
    		field = converter.convert(fieldName, contentType, source, initial, context);
    	} else {
        FromRemoteFieldToLocalFieldString sf = new FromRemoteFieldToLocalFieldString();
        	field = sf.convert(fieldName,contentType, source, initial, context);
    	}
        return field;
    }
    
}