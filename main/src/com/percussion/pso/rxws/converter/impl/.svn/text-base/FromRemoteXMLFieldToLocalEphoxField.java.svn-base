package com.percussion.pso.rxws.converter.impl;

import java.io.ByteArrayInputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xerces.dom.ElementNSImpl;

import com.percussion.pso.importer.model.ImportField;
import com.percussion.pso.rxws.converter.FromRemoteFieldToLocalField;
import com.percussion.pso.rxws.item.RxWsContext;
import com.percussion.webservices.content.PSField;
import com.percussion.webservices.content.PSFieldValue;

public class FromRemoteXMLFieldToLocalEphoxField implements FromRemoteFieldToLocalField {
	private final Log log = LogFactory.getLog(FromRemoteXMLFieldToLocalEphoxField.class);

    public ImportField convert(String fieldName, String contentTypeName, PSField source, ImportField initial, RxWsContext context) {
        PSFieldValue value = source.getPSFieldValue(0);
        ImportField field;
        try {
        JAXBContext ctx  = JAXBContext.newInstance(ElementNSImpl.class);
    	ByteArrayInputStream input = new ByteArrayInputStream(value.getRawData().getBytes());
    	
		Unmarshaller unmarshaller = ctx.createUnmarshaller();

		ElementNSImpl xml = (ElementNSImpl)unmarshaller.unmarshal(input);
			field = new ImportField();
			field.setName(fieldName);
			field.setBody(xml);
        } catch (Exception e) {
        	log.debug("Cannot parse field as xml",e);
        	field = new ImportField(fieldName,value.getRawData());
        }
        
        
        return field;
    }
}