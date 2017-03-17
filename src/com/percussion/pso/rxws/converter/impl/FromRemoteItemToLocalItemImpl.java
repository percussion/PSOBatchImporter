/**
 * 
 */
package com.percussion.pso.rxws.converter.impl;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.percussion.pso.importer.model.FieldMap;
import com.percussion.pso.importer.model.Guid;
import com.percussion.pso.importer.model.ImportField;
import com.percussion.pso.importer.model.ImportItem;
import com.percussion.pso.rxws.converter.FromRemoteFieldToLocalField;
import com.percussion.pso.rxws.converter.FromRemoteItemToLocalItem;
import com.percussion.pso.rxws.item.RxWsContext;
import com.percussion.webservices.content.PSField;
import com.percussion.webservices.content.PSItem;

public class FromRemoteItemToLocalItemImpl implements FromRemoteItemToLocalItem {

    private FromRemoteFieldToLocalField fieldConverter;
    private List<FromRemoteItemToLocalItem> itemConverters;
    
    /**
     * The log instance to use for this class, never <code>null</code>.
     */
    private static final Log log = LogFactory.getLog(FromRemoteItemToLocalItemImpl.class);
            
    /* (non-Javadoc)
     * @see com.percussion.pso.rxws.item.converter.RemoteItemConverter#convert(com.percussion.webservices.content.PSItem, com.percussion.pso.importer.Item)
     */
    public ImportItem convert(PSItem source, ImportItem initial, RxWsContext context) {
        
        if (initial == null) {
        	log.debug("Import item is null creating new");
            initial = context.createImportItem(null);
        }
        initial.setType(source.getContentType());
        // We will do the remote id conversion here
        Guid guid = new Guid(source.getId());
        guid.setRevision(-1);
        initial.setId(Guid.getId(source.getId()));
        //TODO:  get communityname
        if (fieldConverter != null) {
        	
            FieldMap localFields = initial.getFields();
            if (localFields == null) {
                log.error("local fields are null.");
            }
           
            for (PSField field : source.getFields()) {
         
                if (field.getPSFieldValue() != null
                        && field.getPSFieldValue().length > 0 
                        && field.getName() != null && !localFields.containsKey(field.getName())) {
                    ImportField convertedField = fieldConverter.convert(field.getName(), source.getContentType(), field,
                            new ImportField(), context);
                    if ("sys_title".equals(field.getName())) {
                    	if (convertedField.getValue() instanceof String) {
                        initial.setName((String)convertedField.getValue());
                    	}
                    }
//                    if ("displaytitle".equals(field.getName())) {
//                        initial.setLabel((String) convertedField);
//                    }
                    if (convertedField == null) {
                        log.warn("converted field is null");
                    }
                    else {
                    
                    	if (!localFields.containsKey(convertedField.getName())) {
                    		log.debug("putting field "+convertedField.getName());
                    		localFields.put(convertedField);
                        }
                    }
                }
            }
        }
        
       
      
        if (itemConverters != null) {
            for (FromRemoteItemToLocalItem converter : itemConverters) {
                initial = converter.convert(source, initial, context);
            }
        }
        return initial;
    }

    /* (non-Javadoc)
     * @see com.percussion.pso.rxws.item.converter.RemoteItemConverter#getFieldConverter()
     */
    public FromRemoteFieldToLocalField getFieldConverter() {
        return fieldConverter;
    }

    /* (non-Javadoc)
     * @see com.percussion.pso.rxws.item.converter.RemoteItemConverter#setFieldConverter(com.percussion.pso.rxws.item.converter.PSFieldConverter)
     */
    public void setFieldConverter(FromRemoteFieldToLocalField fieldConverter) {
        this.fieldConverter = fieldConverter;
    }

    public List<FromRemoteItemToLocalItem> getItemConverters() {
        return itemConverters;
    }

    public void setItemConverters(List<FromRemoteItemToLocalItem> itemConverters) {
        this.itemConverters = itemConverters;
    }
}