package com.percussion.pso.rxws.item;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.List;

import javax.xml.rpc.ServiceException;

import org.apache.axis.client.Call;
import org.apache.axis.client.Stub;

import com.percussion.pso.importer.IImportContext;
import com.percussion.pso.importer.IImportLog;
import com.percussion.pso.importer.model.ImportItem;
import com.percussion.pso.importer.model.ImportRelationship;
import com.percussion.webservices.assembly.Assembly;
import com.percussion.webservices.assembly.AssemblySOAPStub;
import com.percussion.webservices.content.Content;
import com.percussion.webservices.content.ContentSOAPStub;
import com.percussion.webservices.faults.PSContractViolationFault;
import com.percussion.webservices.faults.PSInvalidSessionFault;
import com.percussion.webservices.faults.PSUserNotMemberOfCommunityFault;
import com.percussion.webservices.rhythmyx.AssemblyLocator;
import com.percussion.webservices.rhythmyx.ContentLocator;
import com.percussion.webservices.rhythmyx.SecurityLocator;
import com.percussion.webservices.rhythmyx.SystemLocator;
import com.percussion.webservices.security.LoginRequest;
import com.percussion.webservices.security.LoginResponse;
import com.percussion.webservices.security.SecuritySOAPStub;
import com.percussion.webservices.security.data.PSCommunity;
import com.percussion.webservices.security.data.PSLogin;
import com.percussion.webservices.system.SwitchCommunityRequest;
import com.percussion.webservices.system.System;
import com.percussion.webservices.system.SystemSOAPStub;

public class RxWsContext implements IImportContext {
    private String password;
    private String sessionId;
    private String user;
    private String communityName;
    private String originalCommunityName;
    private Content contentService;
    private System systemService;
    private Assembly assemblyService;
    private String endPointAddress;
    private List<PSCommunity> availableCommunities;
    private RxWsContentHelper helper;
    boolean loggedIn;
    
    
  
	public RxWsContext() {
		  RxWsContentHelper helper = new RxWsContentHelper(this);
	        this.setHelper(helper);
    }
    public void login()  
    throws ServiceException, PSContractViolationFault, RemoteException {
    
    if (!loggedIn) {

    SecurityLocator locator = new SecurityLocator();
    String address = getServiceAddress(locator.getsecuritySOAPAddress());
    locator.setsecuritySOAPEndpointAddress(address);

    SecuritySOAPStub binding = (SecuritySOAPStub) locator.getsecuritySOAP();

    binding.setTimeout(180000);
    
    if (user == null || user.length() == 0)
        throw new IllegalArgumentException("user may not be null or empty.");
     if (password == null || password.length() == 0)
        throw new IllegalArgumentException("password may not be null or empty.");

     LoginRequest loginReq = new LoginRequest();
     loginReq.setUsername(user);
     loginReq.setPassword(password);
     loginReq.setCommunity(communityName);
     //loginReq.setLocaleCode(locale);


        LoginResponse loginResp = binding.login(loginReq);
        PSLogin loginObj = loginResp.getPSLogin();

        String rxSession = loginObj.getSessionId();

        // Setting to maintain the returned Rhythmyx session for all
        // subsequent requests from the service object.
        setRxSessionHeader(binding, rxSession);
        this.setUser(user);
        this.setPassword(password);
        this.setSessionId(rxSession);
        this.setContentService(getContentService(rxSession));
        this.setSystemService(getSystemService(rxSession));
        this.setAssemblyService(getAssemblyService(rxSession));
        this.setAvailableCommunities(Arrays.asList(loginObj.getCommunities()));
        this.setCommunityName(communityName);
      
        loggedIn = true;
    }
}
    
    
    
    public RxWsContentHelper getHelper() {
		return helper;
	}

	public void setHelper(RxWsContentHelper helper) {
		this.helper = helper;
	}

	public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getSessionId() {
        return sessionId;
    }
    
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
    
    public String getUser() {
        return user;
    }
    
    public void setUser(String user) {
        this.user = user;
    }

    public Content getContentService() {
        return contentService;
    }

    public void setContentService(Content contentService) {
        this.contentService = contentService;
    }

    public ImportItem createImportItem(String producerId) {
        return new ImportItem();
    }

    public ImportRelationship createImportRelationship(String producerId) {
        return new ImportRelationship();
    }

    public IImportLog getImportLog() {
        // TODO Auto-generated method stub
        //return null;
        throw new UnsupportedOperationException("getImportLog is not yet supported");
    }

	public System getSystemService() {
		return systemService;
	}

	public void setSystemService(System systemService) {
		this.systemService = systemService;
	}

	public Assembly getAssemblyService() {
		return assemblyService;
	}

	public void setAssemblyService(Assembly assemblyService) {
		this.assemblyService = assemblyService;
	}

	public String getCommunityName() {
		return communityName;
	}

	public void setCommunityName(String communityName) throws PSInvalidSessionFault, PSContractViolationFault, PSUserNotMemberOfCommunityFault, RemoteException {
		if (this.communityName == null) {
			this.communityName = communityName;
			this.originalCommunityName = communityName;
		} else if (communityName==null) {
			this.communityName = this.originalCommunityName;
		} else 
		if (! communityName.equals(this.communityName)) {
			SwitchCommunityRequest req = new SwitchCommunityRequest();
			 req.setName(communityName);
			 systemService.switchCommunity(req);
			 this.communityName = communityName;
		}
		
	}

	public List<PSCommunity> getAvailableCommunities() {
		return availableCommunities;
	}

	public void setAvailableCommunities(List<PSCommunity> availableCommunities) {
		this.availableCommunities = availableCommunities;
	}

	
	

 protected Content getContentService(String rxSession) throws ServiceException {
     ContentLocator locator = new ContentLocator();
     String address = getServiceAddress(locator.getcontentSOAPAddress());
     locator.setcontentSOAPEndpointAddress(address);

     ContentSOAPStub binding = (ContentSOAPStub) locator.getcontentSOAP();
     setAttachmentFormat(binding);

     // Setting to maintain one JBoss session (JSESSION) for all requests
     binding.setMaintainSession(true);

     // Setting to maintain the given Rhythmyx session for all requests
     setRxSessionHeader(binding, rxSession);

     return binding;
 }
 protected System getSystemService(String rxSession) throws ServiceException {
     SystemLocator locator = new SystemLocator();
     String address = getServiceAddress(locator.getsystemSOAPAddress());
     locator.setsystemSOAPEndpointAddress(address);

     SystemSOAPStub binding = (SystemSOAPStub) locator.getsystemSOAP();
     setAttachmentFormat(binding);

     // Setting to maintain one JBoss session (JSESSION) for all requests
     binding.setMaintainSession(true);

     // Setting to maintain the given Rhythmyx session for all requests
     setRxSessionHeader(binding, rxSession);

     return binding;
 }
 
 protected Assembly getAssemblyService(String rxSession) throws ServiceException {
     AssemblyLocator locator = new AssemblyLocator();
     String address = getServiceAddress(locator.getassemblySOAPAddress());
     locator.setassemblySOAPEndpointAddress(address);

     AssemblySOAPStub binding = (AssemblySOAPStub) locator.getassemblySOAP();
     setAttachmentFormat(binding);

     // Setting to maintain one JBoss session (JSESSION) for all requests
     binding.setMaintainSession(true);

     // Setting to maintain the given Rhythmyx session for all requests
     setRxSessionHeader(binding, rxSession);

     return binding;
 }
 private static void setRxSessionHeader(Stub binding, String rxSession)
 {
    binding.clearHeaders();
    binding.setHeader("urn:www.percussion.com/6.0.0/common", "session",
       rxSession);
 }
 
 /**
  * Sets the MIME format of the attachment of the specified stub.
  *
  * @param binding the proxy for which to set MIME format; assumed not to be
  *   <code>null</code>.
  *
  * @throws ServiceException if the method fails to set the attachment format.
  */
 private static void setAttachmentFormat(org.apache.axis.client.Stub binding)
    throws ServiceException
 {
    Call call = binding._getCall();
    if (call == null)
       call = binding._createCall();
    call.setProperty(Call.ATTACHMENT_ENCAPSULATION_FORMAT,
       Call.ATTACHMENT_ENCAPSULATION_FORMAT_MIME);
 }
 
 /**
  * Creates a new address from the specified source address.
  *
  * @param srcAddress the source address; assumed not to be <code>null</code>
  *    or empty.
  *
  * @return The same address as the specified source address but with the 
  *    the connection information (protocol, host and port) of this class
  *    replacing the original ones.
  */
 private String getServiceAddress(String srcAddress)
 {
    try
    {
       URL url = new URL(srcAddress);
       
       if(endPointAddress!=null && endPointAddress.trim()!= ""){
    	   if(endPointAddress.endsWith("/")){
    		   endPointAddress = endPointAddress.substring(0, endPointAddress.length()-1);
    	   }
       }
       
       if(url.getPath().startsWith("/")){
    	   return endPointAddress=endPointAddress+url.getPath();
       }else{
    	   return endPointAddress + "/" +url.getPath();
       }
        
    }
    catch (MalformedURLException e)
    {
       // this is not possible
       e.printStackTrace();
       throw new RuntimeException(e);
    }
 }
 
 public String getEndPointAddress() {
     return endPointAddress;
 }

 public void setEndPointAddress(String endPointAddress) {
     this.endPointAddress = endPointAddress;
 }

 public String getCommunity() {
     return communityName;
 }

 public void setCommunity(String communityName) {
     this.communityName = communityName;
 } 
 public boolean isLoggedIn() {
		return loggedIn;
	}
	public void setLoggedIn(boolean loggedIn) {
		this.loggedIn = loggedIn;
	}

}
