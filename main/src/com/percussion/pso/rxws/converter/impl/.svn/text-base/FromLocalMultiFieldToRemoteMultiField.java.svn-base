package com.percussion.pso.rxws.converter.impl;

import java.util.Collection;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.percussion.pso.importer.model.ImportFieldValue;
import com.percussion.pso.rxws.converter.FromLocalFieldToRemoteField;
import com.percussion.pso.rxws.item.RxWsContext;
import com.percussion.webservices.content.PSField;
import com.percussion.webservices.content.PSFieldValue;

public class FromLocalMultiFieldToRemoteMultiField implements FromLocalFieldToRemoteField {

	  /**
     * The log instance to use for this class, never <code>null</code>.
     */
    private static final Log log = LogFactory.getLog(FromLocalMultiFieldToRemoteMultiField.class);

    
    @SuppressWarnings("unchecked")
	public PSField convert(String fieldName, String contentTypeName, Object source, PSField initial, RxWsContext context) {
        
    	log.debug("converting to remote with multi valued field");
    	if (! (source instanceof Collection)) {
    		   throw new RuntimeException("The field is not a collection");
        }
    		String str = "";
			List multiValues = (List)source;
			PSFieldValue[] multiField = new PSFieldValue[multiValues.size()];
			int i=0;
			for (Object value : multiValues) {
				ImportFieldValue fieldValue = (ImportFieldValue) value;
				// TODO: This currently will not work with values that are not strings;
				str= fieldValue.getStringValue();
				PSFieldValue value2 = new PSFieldValue();
				value2.setRawData(str);
				multiField[i++]=value2;

			}
			initial.setPSFieldValue(multiField);

			return initial;
		
    	
    }
}
