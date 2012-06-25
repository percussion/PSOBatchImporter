package com.percussion.pso.importer;

import java.io.File;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.impl.StdScheduler;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;


/**
 * Entry point for batch importing.
 * @author adamgent
 *
 */
public class Main {
    
    
    static ApplicationContext applicationContext;
    
    /**
     * The log instance to use for this class, never <code>null</code>.
     */
  
    private static final Log log = LogFactory.getLog(Main.class);
            
    public static void main(String[] args) 
    
        throws Exception {
  
    	log.debug("Starting Main");
    	boolean loop=false;
    	String springConfig="config/spring.xml";
    	
    	for(int i=0; i< args.length; i++) {
    		log.debug("arg="+args[i]);
    		if (args[i].startsWith("-config="))  {
    			String argConfig=args[i].substring(8);
    			File config = new File(argConfig); 
    			if (config.exists()) {
    				log.debug("Found customer specific spring config "+ argConfig);
    				springConfig=argConfig;
    			} else {
    				log.debug("Config "+argConfig+" not found");
    			}
    		}
    		if (args[i].equals("-service")) loop = true;
    	}
    	
    	
        loadSpringConfig(springConfig);
       
        if (!loop) {
        	log.debug("Not running with -service flag,  will run defaultJobList once and exit");
        	ImportJobList defaultJobList = (ImportJobList) applicationContext.getBean("defaultJobList");
        	List<IImportJob> jobs = defaultJobList.getJobs();
        	for(IImportJob job : jobs) {
        		log.debug("Running job "+job.getName());
        		job.runJob();
        	}
        	log.debug("Finished job list Exiting");
        } else {
    	while (loop) {
    		    StdScheduler scheduler = (StdScheduler) applicationContext.getBean("scheduleFactory");
    	       	scheduler.start();
				log.info("Main Thread alive");
				Thread.sleep(900000);
		}
    	
        }
    }
    
    protected static void loadSpringConfig(String xmlFile) {
        applicationContext = new FileSystemXmlApplicationContext(xmlFile);
        
    }
}
