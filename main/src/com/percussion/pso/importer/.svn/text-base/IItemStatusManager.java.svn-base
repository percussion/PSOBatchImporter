package com.percussion.pso.importer;

import java.util.List;

import com.percussion.pso.rxws.item.RxWsContext;

public interface IItemStatusManager {

	public boolean isCheckInOnly();

	public void setCheckInOnly(boolean checkInOnly) ;

	public List<Long> prepareForEdit(List<Long> ids, RxWsContext context)  throws Exception;
	
	public void releaseFromEdit(List<Long> ids, RxWsContext context)  throws Exception ;
	
}
