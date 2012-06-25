package com.percussion.pso.importer.model;

import java.util.Properties;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
@XmlRootElement(name="relationship")
public class ImportRelationship {
	private String template;
    private String slot;
    private String folder;
    private String site;
	private Properties properties;
    

	private ImportItem importItem;

    private Integer id;
  
    private boolean isRef=false;

	
  

    /**
     * @see com.percussion.pso.importer.IImportRelationship#getItem()
     */
  
    public ImportItem getItem() {
    	if(isRef) {
    		ImportItem newItem = new ImportItem();
    		//Just add any
    		newItem.setId(this.id);
    		for(String fieldName : importItem.getFields().keySet()) {
    			if(importItem.getKeyFields()!= null && importItem.getKeyFields().contains(fieldName)) {
    				newItem.setFieldsEnabled(true);
    				newItem.getFields().put(fieldName,importItem.getFields().get(fieldName));	
    			}

    		}
    		newItem.setKeyFields(importItem.getKeyFields());
    		importItem = newItem;
    	}
    	return importItem;
    }

    /**
     * @see com.percussion.pso.importer.IImportRelationship#setItem(com.percussion.pso.importer.impl.ImportItem)
     */
    public void setItem(ImportItem importItem) {
        this.importItem = importItem;
    }

    /**
     * @see com.percussion.pso.importer.IImportRelationship#getTemplate()
     */
    @XmlAttribute
    public String getTemplate() {
		return template;
	}
    @XmlAttribute
	public String getFolder() {
		return folder;
	}
    @XmlAttribute
	public String getSite() {
		return site;
	}
	/**
     * @see com.percussion.pso.importer.IImportRelationship#setTemplate(java.lang.String)
     */
	public void setTemplate(String view) {
		this.template = view;
	}
	

	public void setFolder(String folder) {
		this.folder = folder;
	}
	

	public void setSite(String site) {
		this.site = site;
	}
    /**
     * @see com.percussion.pso.importer.IImportRelationship#getProperties()
     */
    public Properties getProperties() {
        return properties;
    }

    /**
     * @see com.percussion.pso.importer.IImportRelationship#setProperties(java.util.Properties)
     */
    public void setProperties(Properties reference) {
        this.properties = reference;
    }

    /**
     * @see com.percussion.pso.importer.IImportRelationship#getId()
     */
    @XmlAttribute
    public Integer getId() {
        return id;
    }

    /**
     * @see com.percussion.pso.importer.IImportRelationship#setId(java.lang.String)
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @see com.percussion.pso.importer.IImportRelationship#getFolderPath()
     */
 
    /**
     * @see com.percussion.pso.importer.IImportRelationship#getSlot()
     */
    @XmlTransient
    public String getSlot() {
        return slot;
    }

    /**
     * @see com.percussion.pso.importer.IImportRelationship#setSlot(java.lang.String)
     */
    public void setSlot(String slot) {
        this.slot = slot;
    }
    @XmlTransient
	public boolean isRef() {
		return isRef;
	}

	public void setRef(boolean isRef) {
		this.isRef = isRef;
	}
}
