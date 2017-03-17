package com.percussion.pso.importer.manager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
public class ItemLocator {

	private String localId;
	private int remoteId;
	private HashMap<String,String> searchFields;
	private String contentType;
	private String communityName;
	private String path;
	private Set<String> paths;
	private String importRoot;
	private String errorMessage;
	private boolean error;
	private boolean readOnly=false;
	private int matches = -1;
	private int hashValue = -1;
	
	public ItemLocator(String localId, Set<String> paths, String contentType, String communityName, HashMap<String,String> searchFields, String importRoot) {
		this.localId = localId;
		this.paths = paths;
		this.contentType = contentType;
		this.communityName = communityName;
		this.importRoot = importRoot;
		this.searchFields = deepClone(searchFields);
	}
	
	public String getLocalId() {
		return localId;
	}
	public void setLocalId(String localId) {
		this.localId = localId;
	}
	public HashMap<String, String> getSearchFields() {
		return searchFields;
	}
	public void setSearchFields(HashMap<String, String> searchFields) {
		this.searchFields = searchFields;
	}

	
	
	public String getContentType() {
		return contentType;
	}
	
	 @SuppressWarnings("unchecked")
	private HashMap<String,String> deepClone(HashMap<String,String> map) {
	        HashMap<String,String> newone = (HashMap<String,String>)map.clone();
	        Iterator<String> it = newone.keySet().iterator();
	        while (it.hasNext()) {
	           String newkey = it.next();
	            String deepobj = null, newobj = newone.get(newkey);	         
	                deepobj = new String(newobj);
	            newone.put(newkey, deepobj);
	        }
	        return newone;
	  }

	
	 @Override 
	 public boolean equals(Object otherO) {
		    //check for self-comparison
		 
		 
		    if ( this == otherO ) return true;
		    if ( !(otherO instanceof ItemLocator) ) return false;
		    ItemLocator other = (ItemLocator)otherO;
		 
		 //  if ( ! (this.path == null ? other.getPath() == null : this.path.equals(other.getPath()))) return false;
		   if ( ! (this.importRoot == null ? other.getImportRoot() == null : this.importRoot.equals(other.getImportRoot()))) return false;
			  
		   if ( ! (this.contentType == null ? other.getContentType() == null : this.contentType.equals(other.getContentType()))) return false;
		 	
		    if ( (this.getSearchFields() == null) != (other.getSearchFields() == null)) return false;
		    if ( this.getSearchFields() != null && other.getSearchFields() != null ) {
		    	  if (this.getSearchFields().size() != other.getSearchFields().size()) {
		    		  return false;
		    	  } else {
		    		  for(String searchfield : this.getSearchFields().keySet() )  { 
		    			  String result = other.getSearchFields().get(searchfield);
				    		if (other == null || result==null || !result.equals(this.getSearchFields().get(searchfield))) return false;
				    		
				    	}
		    	  }
		    }
		
		    return true;
		    
	 }
	 
	 public int hashCode() { 
		 int hash;
		 if (hashValue==-1) {
		 hash=1;
	
		 hash = hash * 32 + (contentType == null ? 0 : contentType.hashCode());
		// hash = hash * 32 + (path == null ? 0 : path.hashCode());
		 hash = hash * 32 + (importRoot == null ? 0 : importRoot.hashCode());
		 List<String> sortedKeys = new ArrayList<String>( searchFields.keySet());
		 Collections.sort(sortedKeys);
		 for (String key : sortedKeys) {
			 hash = hash * 32 + (key == null ? 0 : key.hashCode());
			 hash = hash * 32 + (searchFields.get(key) == null ? 0 : searchFields.get(key).hashCode());
			 hashValue=hash;
		 }
		 hashValue=hash;
		 } else {
			 hash=hashValue;
		 }
		 return hash;
	 }

	public int getRemoteId() {
		return remoteId;
	}

	public void setRemoteId(int remoteId) {
		this.remoteId = remoteId;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
		error = true;
	}

	public boolean isError() {
		return error;
	}

	public void setHasError(boolean error) {
		this.error = error;
	}

	public String getCommunityName() {
		return communityName;
	}

	public String toString() {
		return "ContentType="+contentType
		+",CommunityName=" +communityName
		+",localName="+localId
		+",remoteId="+remoteId
		+",path="+path
		+",importRoot="+importRoot
		+",errorMessage="+errorMessage
		+",searchFields="+searchFields
		+",readOnly="+readOnly;
		
	}



	public void setCommunityName(String communityName) {
		this.communityName = communityName;
	}
	
	public void setContentType(String contentType){
		this.contentType = contentType;
	}
	
	public int getMatches() {
		return matches;
	}

	public void setMatches(int matches) {
		this.matches = matches;
	}

	

	public boolean isAllowImport() {
		return readOnly;
	}


	public boolean isReadOnly() {
		return false;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	public void setImportRoot(String importRoot) {
		this.importRoot = importRoot;
	}

	public String getImportRoot() {
		return importRoot;
	}

	public void setPaths(Set<String> paths) {
		this.paths = paths;
	}

	public Set<String> getPaths() {
		return paths;
	}


}
