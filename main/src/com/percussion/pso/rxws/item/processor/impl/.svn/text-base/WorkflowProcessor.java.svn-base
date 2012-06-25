/**
 * 
 */
package com.percussion.pso.rxws.item.processor.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.percussion.pso.importer.manager.ItemManager;
import com.percussion.pso.importer.model.ImportBatch;
import com.percussion.pso.importer.model.ImportItem;
import com.percussion.pso.rxws.item.RxWsContentHelper;
import com.percussion.pso.rxws.item.RxWsContext;
import com.percussion.pso.rxws.item.processor.RxWsItemProcessor;
import com.percussion.webservices.system.PSAudit;
import com.percussion.webservices.system.PSAuditTrail;
import com.percussion.webservices.system.PSWorkflow;

public class WorkflowProcessor implements RxWsItemProcessor {

	private Map<String,Map<String,String>> transitionMap;
	private Map<Long,String> workflowMap;
	/**
	 * The log instance to use for this class, never <code>null</code>.
	 */
	private final Log log = LogFactory.getLog(WorkflowProcessor.class);
	
	private ItemManager manager;
	
	public ImportBatch  processItems(ImportBatch  items) 
	throws Exception {
				
		ImportBatch updatableItems = new ImportBatch();
		for (ImportItem item : items) {
			if (!item.getUpdateType().equals("ref")  && item.getLocator().getRemoteId() > 0 && !item.getLocator().isReadOnly()) {	
				updatableItems.add(item);
			}
		}
		
		RxWsContext context = manager.getContext();
		RxWsContentHelper contentHelper = new RxWsContentHelper(context);
		log.debug("Transitioning Items: ");

		if (items == null ) {
			throw new IllegalArgumentException("List of items invalid");
		}
		int size = updatableItems.size();
		if (size > 0 ) {
		
			List<Integer> remoteIds = new ArrayList<Integer>();
			for( ImportItem item : updatableItems) {
				try {
					remoteIds.add(item.getId());
				} catch (NumberFormatException nfe) {
					log.debug("Content id is not set or is not a Long :" + nfe.getMessage());
				}

			}
			if (remoteIds.size() > 0) {



				List<PSAuditTrail> trails = contentHelper.loadAuditTrails(remoteIds);
				Map<Long, Map<String,List<Integer>>> stateMap = new HashMap<Long,Map<String,List<Integer>>>();
				int pos=0;
				Map<String,List<Integer>> wfStateMap = null;
				for(PSAuditTrail trail : trails ) {
					PSAudit[] audits = trail.getAudits();
					String state = "None";
					log.debug("Items size is " +pos);
					long workflowId =  Long.valueOf(updatableItems.get(pos).getFields().get("sys_workflowid").getStringValue());
					if (audits != null && audits.length > 0) {
						state = audits[audits.length - 1].getStateName();

						if (!stateMap.containsKey(workflowId)) {
							wfStateMap = new HashMap<String,List<Integer>>();
							stateMap.put(workflowId, wfStateMap);

						} else {
							wfStateMap = stateMap.get(workflowId);
						}

						if (!wfStateMap.containsKey(state)) wfStateMap.put(state, new ArrayList<Integer>());
						wfStateMap.get(state).add(remoteIds.get(pos++));
					}	
				}

				for (Long workflow : stateMap.keySet()) {
					
					//String workflowName = this.getWorkflowNamebyId(workflow, contentHelper);
					//  Need a better way to map workflow name to id
					String workflowName = workflow.toString();
					Map<String, String> wfTransitionMap = transitionMap.get(workflowName);
					wfStateMap = stateMap.get(workflow);
					while(wfStateMap.size() > 0 && wfTransitionMap != null) {
						log.debug("Transitioning items in workflow " +workflowName);
						
						Map<String,List<Integer>> newStateMap = new HashMap<String,List<Integer>>();
						for (String state : wfStateMap.keySet()) {
							log.debug("processing state "+state);
							if (wfTransitionMap.containsKey(state)) {
								String transition = wfTransitionMap.get(state);
								try {
								log.debug("need to transition items in state "+state+ " with transition "+ transition);
								List<String> afterStates = contentHelper.transitionItems(wfStateMap.get(state), transition);
							
								if (!afterStates.get(0).equals(state)) {
									log.debug("Now state is " + afterStates.get(0));
									newStateMap.put(afterStates.get(0), wfStateMap.get(state));
								}
								} catch (Exception e) {
									items.addError("Failed to transition items with transition name "+transition, e);
								}
							}

						}
						wfStateMap = newStateMap;
					}

				}

			}


		}

		return items;
	}
	private String getWorkflowNamebyId(Long workflowId, RxWsContentHelper helper) throws Exception {
		if (workflowMap == null || !workflowMap.containsKey(workflowId)) {
			buildWorkflowMap(helper);
		} 
	
		String workflowName = workflowMap.get(workflowId);

		return workflowName;
	}

	private void buildWorkflowMap(RxWsContentHelper helper)  throws Exception {
		workflowMap = new HashMap<Long, String>();
		List<PSWorkflow> workflows = helper.getWorkflows(null);
		for(PSWorkflow workflow : workflows) {
			workflowMap.put(workflow.getId(), workflow.getName());
		}
	}
	public Map<String,Map<String,String>>  getTransitionMap() {
		return transitionMap;
	}

	public void setTransitionMap(Map<String,Map<String,String>>  transitionMap) {
		this.transitionMap = transitionMap;
	}
	
	public ItemManager getManager() {
		return manager;
	}

	public void setManager(ItemManager manager) {
		this.manager = manager;
	}

 

}