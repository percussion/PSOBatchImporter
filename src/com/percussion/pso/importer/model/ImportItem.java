package com.percussion.pso.importer.model;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.percussion.pso.importer.IImportItemConsumer;
import com.percussion.pso.importer.IImportItemProducer;
import com.percussion.pso.importer.manager.ItemLocator;
import com.sun.xml.bind.CycleRecoverable;



/**
 * The main data structure used for the item services.
 * It follows the canonical Java bean getters and setters.
 * Most of the getters that return collections or maps will 
 * return an empty one instead of null if they have not been set 
 * already (lazy).
 * 
 * @see IImportItemProducer
 * @see IImportItemConsumer
 * @see IItemService
 * @author adamgent
 *
 */

public class ImportItem  implements CycleRecoverable {
	

    private List<String> keyFields;
    private int id;
	private FieldMap fields;
	private String communityName;
	private List<ImportRelationship> relationships;
	private Set<String> paths;
    private String importRoot;
	private List<ImportSlot> slots;
	private List<ImportChild> child;
	private String name;
	private String objectId;
	private int revision;
	private String type;
	private List<ImportError> errors;
	private boolean fieldsEnabled;
		//  createOnly,  update, ref
	private String updateType = "update";
	private ItemLocator locator;
	
	private final Log log = LogFactory.getLog(ImportItem.class);

	

    //private Date lastModifiedDate;
    
    //private Map<String,IImportItemChild> children;
 
   
    
	@XmlAttribute(name="id")
    public Integer getId() {
		if (locator==null) return 0;
        return locator.getRemoteId();
    }
    /**
     * @see com.percussion.pso.importer.IImportItem#setId(java.lang.String)
     */

    public void setId(int id) {
    	this.id = id;
       
    }

	
  
	@XmlAttribute
	public String getCommunityName() {
		return communityName;
	}
	public void setCommunityName(String communityName) {
		this.communityName = communityName;
		
	}

	@XmlAttribute(name="objectId")
    public String getObjectId() {
		if(objectId==null) objectId = Integer.toString(this.hashCode());
        return this.objectId;
    } 
	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}
    /**
     * @see com.percussion.pso.importer.IImportItem#getLabel()
     */
	/*public String getLabel() {
		return label;
	}*/
	/**
     * @see com.percussion.pso.importer.IImportItem#setLabel(java.lang.String)
     */
	/*public void setLabel(String label) {
		this.label = label;
	}*/
    /**
     * @see com.percussion.pso.importer.IImportItem#getName()
     */
	@XmlAttribute
	public String getName() {
		return name;
	}
	/**
     * @see com.percussion.pso.importer.IImportItem#setName(java.lang.String)
     */
	public void setName(String name) {
		this.name = name;
	}

    
	@XmlAttribute
	public String getUpdateType() {
		return updateType;
	}
	public void setUpdateType(String updateType) {
		this.updateType = updateType;
	}
	
	@XmlTransient
	public int getRevision() {
		return revision;
	}
	
	public void setRevision(int revision) {
		this.revision = revision;
	}

    /**
     * @see com.percussion.pso.importer.IImportItem#getType()
     */
	@XmlAttribute
	public String getType() {
		return type;
	}

	/**
     * @see com.percussion.pso.importer.IImportItem#setType(java.lang.String)
     */
	public void setType(String type) {
		this.type = type;
	}
    

	@XmlAttribute
	public String getImportRoot() {
		return importRoot;
	}
	
	public void setImportRoot(String importRoot) {
		this.importRoot = importRoot;
	}
	
	@XmlElementWrapper(name="paths")
	@XmlElement(name = "path") 
    public Set<String> getPaths() {
		if (locator != null && locator.getPaths()!= null) {
			log.debug("getting paths when locator paths are not null :"+ locator.getPaths());
			this.paths = locator.getPaths();
		}
		return this.paths;
    }
    /**
     * @see com.percussion.pso.importer.IImportItem#setPaths(java.util.List)
     */
    public void setPaths(Set<String> paths) {
  
    	if (locator==null) {
    		this.paths=paths;
    	}
    	if (locator!=null) {
    		if (locator.getPaths()==null) {
    			locator.setPaths(new HashSet<String>());
    		}
    		locator.getPaths().addAll(paths);
    	 	this.paths=locator.getPaths();
    	 	log.debug("Updating locator Paths");
    	} else {
    		this.paths=paths;
    	}
     
    }
    
    /**
     * @see com.percussion.pso.importer.IImportItem#getLastModifiedDate()
     
    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }*/
    /**
     * @see com.percussion.pso.importer.IImportItem#setLastModifiedDate(java.util.Date)
   
    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }
      */
    
    @XmlAttribute(name="keyField")
    @XmlJavaTypeAdapter(KeyFieldAdapter.class)
	public List<String> getKeyFields() {
		return keyFields;
	}
   //TODO:  it would be an error to reset the keyfields (Possible unless the id returned is the same)
	public void setKeyFields(List<String> keyFields) {
		this.keyFields = keyFields;
	}
  
	
	
	
	@XmlJavaTypeAdapter(FieldAdapter.class)
	public FieldMap getFields() {
		if (fields == null && fieldsEnabled) fields = new FieldMap();
		return fields;
	}
	public void setFields(FieldMap fields) {
		if(fields != null) fieldsEnabled=true;
		this.fields = fields;
		
	}
	
	public ImportRelationship onCycleDetected(ImportItem item) {
		ImportRelationship newItem = new ImportRelationship();
		newItem.setId(item.id);
		return newItem;
	}
	@XmlTransient
	public List<ImportRelationship> getRelationships() {
		List<ImportRelationship> rels = new ArrayList<ImportRelationship>();
		if(this.getSlots() != null) {
		for(ImportSlot slot : this.getSlots()) {
			rels.addAll(slot.getRelationship());
		}
		}
		return rels;
	}
	public void setRelationships(List<ImportRelationship> relationships) {
	
		Map<String,ImportSlot> slots = new HashMap<String,ImportSlot>();
		if (relationships != null) {
		for(ImportRelationship rel : relationships ) {
			String slotName = rel.getSlot();
			if (slots.containsKey(slotName)) {
				ImportSlot slot = slots.get(slotName);
				slot.getRelationship().add(rel);
			} else {
				ImportSlot slot = new ImportSlot();
				slot.setSlot(slotName);
				slot.getRelationship().add(rel);
				slots.put(slotName, slot);
			}
		}
		ArrayList<ImportSlot> retSlots = new ArrayList<ImportSlot>();
		for(Map.Entry<String,ImportSlot> entry : slots.entrySet()) {
			retSlots.add(entry.getValue());
		}
			this.slots = retSlots;
		}
			this.relationships = relationships;
		
	}

	public Object onCycleDetected(Context arg0) {
		ImportItem newItem = new ImportItem();
		//Just add any
		newItem.setId(this.id);
		for(String field : this.fields.keySet()) {
			if(getKeyFields().contains(field)) {
				newItem.getFields().put(field,this.fields.get(field));
			}
			
		}
		newItem.setKeyFields(getKeyFields());
		return newItem;
	}
	
	
	public static class KeyFieldAdapter extends XmlAdapter<String, List<String>> {
		  
		@Override
		
		public String marshal(List<String> arg0) throws Exception {
			
			String ret="";
			if (arg0==null) return null;
			for(String item : arg0) {
				if (ret.length()>0)ret+=",";
				ret+=item;
			}
			
			return ret;
		}

		@Override
		public List<String> unmarshal(String arg0) throws Exception {
			 ArrayList<String> ret = new ArrayList<String>();
			 ret.addAll(Arrays.asList(arg0.split(",")));
			 return ret;
		}
	}
	
	public static class FieldAdapter extends XmlAdapter<Fields, FieldMap> {
		  
		@Override
		public Fields marshal(FieldMap arg0) throws Exception {
		
			Fields fieldSet = null;
			if(arg0!=null) {
				fieldSet = new Fields();
				fieldSet.carray = arg0.values().toArray(new ImportField[arg0.size()]);
			}
			return fieldSet;
		}

		@Override
		public FieldMap unmarshal(Fields arg0)
				throws Exception {
			FieldMap fieldMap = null;
			
			if (arg0 != null) {
				fieldMap = new FieldMap();
				for (ImportField field : arg0.carray) {
					fieldMap.put(field.getName(), field);
				}
			}
			return fieldMap;
		}
	}
	
	@XmlElement(name="related")
	public List<ImportSlot> getSlots() {
		return this.slots;
	}
	public void setSlots(List<ImportSlot> slots) {
		for(ImportSlot slot : slots){
			String name=slot.getSlot();
			for(ImportRelationship rel : slot.getRelationship() ) {
				rel.setSlot(name);
			}
			
		}
		
		this.slots = slots;
		
	}
	
	public List<ImportChild> getChild() {
		return child;
	}
	public void setChild(List<ImportChild> child) {
		this.child = child;
	}
	@XmlTransient
	public boolean isFieldsEnabled() {
		return fieldsEnabled;
	}
	public void setFieldsEnabled(boolean fieldsEnabled) {
		this.fieldsEnabled = fieldsEnabled;
	}
	
	
	@XmlElementWrapper(name="errors")
	@XmlElement(name="error")
	public List<ImportError> getErrors() {
		return errors;
	}
	public void setErrors(List<ImportError> errors) {
		this.errors = errors;
	}
	
	
	public void addError(String error) {
		if(this.errors == null) this.errors = new ArrayList<ImportError>();
		this.errors.add(new ImportError(error));
	}
	public void addError(String error, Exception e) {
		if(this.errors == null) this.errors = new ArrayList<ImportError>();
		
		this.errors.add(new ImportError(error,e));
	}
	public void clearErrors(String error) {
		this.errors = null;
	}
	
	@Override
	public String toString() {
		
	String ret=super.toString();
		try {
		JAXBContext ctx  =  JAXBContext.newInstance(ImportItem.class);
		Marshaller marshaller = ctx.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true); 
		StringWriter sw = new StringWriter();
		marshaller.marshal(this, sw); 
		ret = sw.toString();
		} catch (Exception e) {
			ret="Error marshalling ImportItem in toString";
		}
		return ret;
	}
	@XmlTransient
	public ItemLocator getLocator() {
		if (locator == null) updateLocator();
		return locator;
	}
	public void setLocator(ItemLocator locator) {
		//this.objectId = locator.getLocalId();
		this.type = locator.getContentType();
		if (this.communityName==null) {
			this.communityName  = locator.getCommunityName();
		}
		if (this.paths!=null && locator.getPaths()!=null) {
			locator.getPaths().addAll(locator.getPaths());
		}
		this.setKeyFields(new ArrayList<String>(locator.getSearchFields().keySet()));
		for(String fieldName : locator.getSearchFields().keySet()) {
			fields.put(fieldName, locator.getSearchFields().get(fieldName));
		}
		this.locator = locator;
	}
	
	private void updateLocator() {
		HashMap<String,String> searchFields = new HashMap<String,String>();
		if (keyFields != null  && fields != null) {
		for (String keyfield : keyFields) {
			// TODO This is to handle old syntax remove navigation check
			if(!keyfield.equals("navigation")) {
				if (fields.get(keyfield) != null ){
				searchFields.put(keyfield, fields.get(keyfield).getStringValue() );
				} else {
					log.debug("No key field value set for item :"+this.objectId);
				}
			}
			
		}
		}
		
		
	
		this.locator = new ItemLocator(objectId, paths,type, communityName,searchFields,importRoot);
	}
	
}


