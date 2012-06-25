package com.percussion.pso.rxws.converter.impl;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.percussion.pso.importer.model.ImportFieldValue;
import com.percussion.pso.rxws.converter.FromLocalFieldToRemoteField;
import com.percussion.pso.rxws.item.RxWsContext;
import com.percussion.webservices.content.PSField;
import com.percussion.webservices.content.PSFieldValue;

public class FromLocalFieldToRemoteFieldDispatch implements FromLocalFieldToRemoteField {

	
	  /**
     * The log instance to use for this class, never <code>null</code>.
     */
    private static final Log log = LogFactory.getLog(FromLocalFieldToRemoteFieldDispatch.class);

    
	private Map<String,FromLocalFieldToRemoteField> converterFieldNameMap;
    private Map<Class, FromLocalFieldToRemoteField> converterTypeMap;
    private FromLocalFieldToRemoteField defaultConverter;
    

    public FromLocalFieldToRemoteFieldDispatch() {
    	defaultConverter = new FromLocalFieldToRemoteField() {

    		public PSField convert(String fieldName, String ContentTypeName, Object source, PSField initial, RxWsContext context) {
    			String str;
    			log.debug("Processing field "+ initial.getName());

    				if (source instanceof String) {
    					str = (String) source;

    				}
    				else if (source instanceof Date) {
    					Date date = (Date) source;
    					SimpleDateFormat sdf = new SimpleDateFormat();
    					//2005-01-01 00:00:00.0
    					sdf.applyPattern("yyyy-MM-dd HH:mm:ss.S");
    					str = sdf.format(date); 
    				//	log.debug("Setting date string to "+str);
    				}
    			
    				else if (source == null){
    					str="";
    				}
    				else {
    					str = source.toString();
    				}
    				PSFieldValue value = new PSFieldValue();
    				value.setRawData(str);
    				initial.setPSFieldValue(new PSFieldValue[] { value });

    				return initial;
    		}


    	};
    }
    /* (non-Javadoc)
     * @see com.percussion.pso.rxws.converter.FromLocalFieldToRemoteField#convert(java.lang.Object, com.percussion.webservices.content.PSField)
     */
    public PSField convert(String fieldName, String contentTypeName, Object source, PSField initial, RxWsContext context) {
       
    	if (source != null && source instanceof Collection ) {
    		
    	}
    	FromLocalFieldToRemoteField converter = getConverter(fieldName,contentTypeName,source);
        return converter.convert(fieldName, contentTypeName, source, initial, context);

    }
    
    FromLocalFieldToRemoteField getConverter(String fieldName, String contentTypeName, Object obj) {
        FromLocalFieldToRemoteField converter;
       // log.debug("TypeMapSize="+converterTypeMap.size());
      //log.debug(converterTypeMap.keySet());
       //log.debug("key class=" +converterTypeMap.keySet().iterator().next()); 
  
       //log.debug(converterTypeMap.get("java.util.ArrayList"));
       //  log.debug("fieldName="+fieldName);
        if (converterFieldNameMap != null && converterFieldNameMap.containsKey(fieldName)) {
        	
        	converter = converterFieldNameMap.get(fieldName);
     //  	log.debug("Found field mapped converter"+converter.getClass());
        } else {
        
        if (obj == null || converterTypeMap == null || 
                ! converterTypeMap.containsKey(obj.getClass())) {
            converter = defaultConverter;
            if (obj==null)   log.debug("Object is null");
           /* log.debug("type keys =" +converterTypeMap.keySet());
            log.debug("Type is "+obj.getClass().getName());
            log.debug("type exists in map= " +  converterTypeMap.containsKey(obj.getClass()));
            log.debug("Using default convertor for class " + obj.getClass().getName());
        */
        }
        else {
        	  converter = converterTypeMap.get(obj.getClass());
        	  log.debug("using convertor "+converter.getClass());
              
        }
        }
        return converter;
    }
    
    public Map<Class, FromLocalFieldToRemoteField> getConverterTypeMap() {
        return converterTypeMap;
    }

    public void setConverterTypeMap(Map<Class, FromLocalFieldToRemoteField> converterTypeMap) {
        this.converterTypeMap = converterTypeMap;
    }

    public FromLocalFieldToRemoteField getDefaultConverter() {
        return defaultConverter;
    }

    public void setDefaultConverter(FromLocalFieldToRemoteField defaultConverter) {
        this.defaultConverter = defaultConverter;
    }
	public Map<String, FromLocalFieldToRemoteField> getConverterFieldNameMap() {
		return converterFieldNameMap;
	}
	public void setConverterFieldNameMap(
			Map<String, FromLocalFieldToRemoteField> converterFieldNameMap) {
		this.converterFieldNameMap = converterFieldNameMap;
	}
    
}
