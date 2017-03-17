package com.percussion.pso.importer;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ImportJobList {

	private List<IImportJob> jobs;
	
	/**
     * The log instance to use for this class, never <code>null</code>.
     */
   private static final Log log = LogFactory.getLog(ImportJobList.class);
   
	
	public List<IImportJob> getJobs() {
		return jobs;
	}

	public void setJobs(List<IImportJob> jobs) {
		this.jobs = jobs;
	}

	public void runJob() {
		if (jobs!=null) {
			for(IImportJob job : jobs) {
				log.info("Running list of jobs with "+jobs.size()+" Jobs");
				job.runJob();
			}
		}
		
	}
}
