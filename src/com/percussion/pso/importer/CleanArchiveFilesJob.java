package com.percussion.pso.importer;

import java.io.File;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CleanArchiveFilesJob
{
	private List<String> archiveFolders;
	private String expiredDays = "30"; 

	private final Log log = LogFactory.getLog(CleanArchiveFilesJob.class);

	public List<String> getArchiveFolders() {
		return archiveFolders;
	}

	public void setArchiveFolders(List<String> archiveFolders) {
		this.archiveFolders = archiveFolders;
	}

	public String getExpiredDays() 
	{
		return expiredDays;
	}

	public void setExpiredDays(String expiredDays) 
	{
		this.expiredDays = expiredDays;
	}
	
	public void runJob() 
	{
		try
		{
			log.debug("Deleting older files in the archive folders");
			deleteExpiredFiles(expiredDays, archiveFolders);
		}catch(Exception e)
		{
			log.debug("Error cleaning up old files in the Errors and Completed folders: ", e);
		}
	}
	
	protected void deleteExpiredFiles(String expiredDays, List<String> archivePath)
	{		
		try
		{
			for(int i = 0; archivePath.size() > 0; i++)				
			{
				String archiveFolder = archivePath.get(i);
				int daysOld = Integer.parseInt(expiredDays);
				File archiveDir = new File(archiveFolder);
				
				if(archiveDir.exists())
				{
					File [] listFiles = archiveDir.listFiles();			
					long purgeTime = System.currentTimeMillis() - (daysOld * 24 * 60 * 60 * 1000);
					for(File listFile : listFiles) {  
					   if(listFile.lastModified() < purgeTime) 
					   {  
					      if(!listFile.delete()) 
					      {  
					    	  log.debug("Unable to delete file: " + listFile);  
					      }  
					   }  
					} 
				}					
			}		
		}catch(Exception e)
		{
			log.debug("Error deleting the files from the archive directory "+ archivePath);			
		}
	}	

}
