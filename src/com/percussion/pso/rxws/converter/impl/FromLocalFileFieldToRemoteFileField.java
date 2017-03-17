package com.percussion.pso.rxws.converter.impl;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.activation.MimetypesFileTypeMap;
import javax.activation.URLDataSource;

import org.apache.axis.attachments.AttachmentPart;
import org.apache.axis.client.Stub;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.percussion.pso.importer.manager.ItemManager;
import com.percussion.pso.rxws.converter.FromLocalFieldToRemoteField;
import com.percussion.pso.rxws.item.RxWsContext;
import com.percussion.webservices.content.PSField;
import com.percussion.webservices.content.PSFieldValue;

public class FromLocalFileFieldToRemoteFileField implements FromLocalFieldToRemoteField {

	private static final Log log = LogFactory.getLog(FromLocalFileFieldToRemoteFileField.class);


    public PSField convert(String fieldName, String contentType, Object source, PSField initial, RxWsContext context) {
    	log.debug("Updating file field " + source );
        if ( ! (source instanceof File)) {
            throw new RuntimeException("The field is not a file");
        }
        File f = (File) source;
       /* URL url;
        try {
            url = f.toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
       
        }
        */
       
        log.debug("updating file "+ f.getAbsolutePath());
        PSFieldValue fieldValue = new PSFieldValue();
       
      
        String attachmentId = addAttachment(f, context);
        fieldValue.setAttachmentId(attachmentId);
   
        initial.setPSFieldValue(new PSFieldValue[] { fieldValue });
       
        return initial;
    }

    /**
     * Add the supplied attachment to the provided proxy and return the 
     * attachment id.
     * 
     * @param binding the stub to which to add the attachement, assumed not
     *    <code>null</code>.
     * @param attachment the file to attach, assumed not <code>null</code>.
     * @return the attachment id needed on the server to map this to the 
     *    appropriate field, never <code>null</code> or empty.
     */
    private String addAttachment(URL attachment, RxWsContext context)
    {
    	
       DataHandler handler = new DataHandler(new URLDataSource(attachment));
       
       AttachmentPart part = new AttachmentPart(handler);
       if (context.getContentService() instanceof Stub) {
           Stub stub = (Stub) context.getContentService();
         stub.addAttachment(part);
       }
       		part.detachAttachmentFile();
       return part.getContentId();
      
    }
    
    private String addAttachment(File attachment, RxWsContext context)
    {
    
    	DataHandler handler = new DataHandler(new FileDataSource(attachment));
    	AttachmentPart part = new AttachmentPart(handler);
       if (context.getContentService() instanceof Stub) {
           Stub stub = (Stub) context.getContentService();
       
           //stub.clearAttachments();
           stub.addAttachment(part);
          
       }

       return part.getContentId();
      
    }
}
