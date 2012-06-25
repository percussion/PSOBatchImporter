package com.percussion.pso.importer;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.percussion.pso.importer.manager.ItemManager;

public class DeleteFoldersJob implements IImportJob {
	
	 private static final Log log = LogFactory.getLog(DeleteFoldersJob.class);
	 
	 ItemManager manager;
	 private List<String> folderPaths;
	 private String name;
	 
	 public ItemManager getManager() 
	 {
		 return manager;
	 }
		
	 public void setManager(ItemManager manager) 
	 {
		 this.manager = manager;
	 }
	 
	 public List<String> getFolderPaths()
	 {
		 return folderPaths;
	 }
	 
	 public void setFolderPaths(List<String> folderPaths)
	 {
		 this.folderPaths = folderPaths;
	 }
	 
	 public String getName()
	 {
		 return name;
	 }
	 
	 public void setName(String name)
	 {
		 this.name = name;
	 }
	 
	 public void runJob()
	 {
		 try
		 {
			log.debug("Deleting folders: "+ folderPaths.size());
			boolean purgeItems = true;
			manager.deleteFolders(folderPaths, purgeItems );
			log.debug("Deleted folders");
		 }catch(Exception e)
		 {
			 log.debug("Error running the delete folders job ", e);
		 }
	 }
}
