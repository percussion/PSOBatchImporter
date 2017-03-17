/**
 * 
 */
package com.percussion.pso.rxws.converter.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.percussion.pso.importer.model.FieldMap;
import com.percussion.pso.importer.model.ImportItem;
import com.percussion.pso.rxws.converter.FromLocalFieldToRemoteField;
import com.percussion.pso.rxws.converter.FromLocalItemToRemoteItem;
import com.percussion.pso.rxws.item.RxWsContext;
import com.percussion.webservices.content.PSField;
import com.percussion.webservices.content.PSItem;

public class FromLocalItemToRemoteItemImpl implements FromLocalItemToRemoteItem {
	  /**
     * The log instance to use for this class, never <code>null</code>.
     */
    private static final Log log = LogFactory.getLog(FromLocalItemToRemoteItemImpl.class);

	
    FromLocalFieldToRemoteField fieldConverter;
    
    /* (non-Javadoc)
     * @see com.percussion.pso.rxws.item.converter.LocalItemConverter#convert(com.percussion.pso.importer.Item, com.percussion.webservices.content.PSItem)
     */
    public PSItem convert(ImportItem source, PSItem initial, RxWsContext context) {
        FieldMap localFields = source.getFields();
      
        for (PSField initField : initial.getFields()) {
            String initFieldName = initField.getName();
            if (initFieldName.equals("sys_title") 
                    && source.getName() != null) {
                initField = fieldConverter.convert("sys_title", source.getType(),source.getName(), initField, context);
            }
//            else if (initFieldName.equals("displaytitle") 
//                        && source.getLabel() != null) {
//                initField = fieldConverter.convert(source.getLabel(), initField, context);
//            }
           /* if (initFieldName.equals("sys_communityid") || initFieldName.equals("sys_workflowid") || initFieldName.equals("sys_workflowid") || initFieldName.equals("sys_hibernateVersion") || initFieldName.equals("placeholder")) {
            	
            	
            	log.debug("Skipping conversion of " +initFieldName);
            } */
            else if (localFields.get(initFieldName) != null) {
               
                Object sourceField = localFields.get(initFieldName).getValue();
                log.debug("field "+initFieldName +" is a "+sourceField.getClass());
                initField = fieldConverter.convert(initFieldName,source.getType(),sourceField, initField, context);
               
            }
           

        }
       
        
      

        return initial;
    }

    /* (non-Javadoc)
     * @see com.percussion.pso.rxws.item.converter.LocalItemConverter#getFieldConverter()
     */
    public FromLocalFieldToRemoteField getFieldConverter() {
        return fieldConverter;
    }

    /* (non-Javadoc)
     * @see com.percussion.pso.rxws.item.converter.LocalItemConverter#setFieldConverter(com.percussion.pso.rxws.item.converter.RxWsConverter.LocalFieldConverter)
     */
    public void setFieldConverter(FromLocalFieldToRemoteField fieldConverter) {
        this.fieldConverter = fieldConverter;
    }
}