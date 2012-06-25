package com.percussion.pso.importer.config.manager;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.percussion.pso.importer.config.model.ContentType;
import com.percussion.pso.importer.config.model.Field;
import com.percussion.pso.rxws.item.RxWsContentHelper;
import com.percussion.pso.rxws.item.RxWsContext;
import com.percussion.webservices.content.PSContentTypeSummary;
import com.percussion.webservices.content.PSFieldDescription;
@XmlRootElement
public class RhythmyxConfigManager {
	@XmlTransient
	RxWsContext context;
	@XmlTransient
	RxWsContentHelper helper;
	private List<ContentType> contentTypes = new ArrayList<ContentType>();
	
	boolean allTypes = false;
	
	public RhythmyxConfigManager () {
		
	}
	
	public RhythmyxConfigManager (RxWsContext context) {
		setContext(context);
	}
	@XmlElement(name="contentType")
	public List<ContentType> getContentTypes() throws Exception { 
	
		if (contentTypes == null || !allTypes) {
		contentTypes = new ArrayList<ContentType>();
		List<PSContentTypeSummary> rxTypes = helper.loadContentTypes(null);
		for (PSContentTypeSummary type : rxTypes ) {
			contentTypes.add(convertType(type));
		}
		this.allTypes = true;
		}
		return contentTypes;
	}
	
	public ContentType getContentType(String name) throws Exception {
	
		ContentType newType = new ContentType();
		newType.setName(name);
		if (!contentTypes.contains(newType)) {
		List<PSContentTypeSummary> rxTypes = helper.loadContentTypes(name);
			if (rxTypes != null) {
				newType = convertType(rxTypes.get(0));
				contentTypes.add(newType);
			} else {
			 newType = null;
			}
		} else {
			newType = null;
		}
		return newType;
	}

	public void setContentTypes(List<ContentType> contentTypes) {
		this.contentTypes = contentTypes;
	}
	@XmlTransient
	public RxWsContext getContext() {
		return context;
	}

	public void setContext(RxWsContext context) {
		this.context = context;
		this.helper = context.getHelper();
	}

	

	public ContentType  convertType(PSContentTypeSummary type) {
		ContentType newType = new ContentType();
		newType.setId(type.getId());
		newType.setDescription(type.getDescription());
		newType.setName(type.getName());
		newType.setFields(convertFields(type.getFields()));
		return newType;
	}
	
	public List<Field> convertFields (PSFieldDescription[] rxFields) {
		List<Field> localFields = new ArrayList<Field>();
		for(PSFieldDescription rxField : rxFields) {
		
			localFields.add(convertField(rxField));
		}
		return localFields;
	}
	
	Field convertField(PSFieldDescription rxField) {
			Field newField = new Field();
			newField.setName(rxField.getName());
			newField.setType(rxField.getDataType().getValue());
			return newField;
	}
}
