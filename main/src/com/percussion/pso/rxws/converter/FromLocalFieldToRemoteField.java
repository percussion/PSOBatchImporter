package com.percussion.pso.rxws.converter;

import com.percussion.pso.rxws.item.RxWsContext;
import com.percussion.webservices.content.PSField;

public interface FromLocalFieldToRemoteField {
	 public PSField convert(String fieldName, String contentType, Object fieldValue, PSField initField, RxWsContext context);
    
}