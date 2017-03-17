package com.percussion.pso.importer.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xml.resolver.tools.CatalogResolver;
import org.w3c.dom.Document;

import com.percussion.pso.importer.IImportItemFileParser;
import com.percussion.pso.importer.IImportItemProducer;
import com.percussion.pso.importer.model.ImportBatch;
import com.percussion.pso.importer.model.ImportItem;

public class FolderTransformProducer implements IImportItemProducer,
		Iterator<ImportBatch> {

	/**
	 * The log instance to use for this class, never <code>null</code>.
	 */
	private static final Log log = LogFactory
			.getLog(FolderTransformProducer.class);

	private IImportItemFileParser importItemFileParser;

	private List<String> files;
	private String folderName;
	private String stylesheetPath;
	private String transPath;
	private String encoding;
	private boolean removeItems = false;
	private Templates template = null;
	private TransformerFactory transformerFactory;
	private HashMap<String, String> xslParams;
	private DocumentBuilderFactory domFactory;
	
	public HashMap<String, String> getXslParams() {
		return xslParams;
	}

	public void setXslParams(HashMap<String, String> xslParams) {
		this.xslParams = xslParams;
	}

	private Iterator<File> fileIterator;

	
	public Iterator<ImportBatch> produce() throws Exception {
		 domFactory =
			    DocumentBuilderFactory.newInstance();
		log.debug("producing items");

		if (stylesheetPath != null) {
			log.debug("getting xsl");
			// TransformerFactory factory = TransformerFactory.newInstance();

			template = transformerFactory.newTemplates(new StreamSource(
					new File(stylesheetPath)));

		}

		File f = new File(folderName);

		File files[] = f.listFiles();

		if (files == null) {
			List<File> filesd = new ArrayList<File>();
			this.fileIterator = filesd.iterator();
			log.debug("Cannot find folder " + folderName);
			return this;
		}

		this.fileIterator = Arrays.asList(files).iterator();

		return this;

	}

	public IImportItemFileParser getItemParser() {
		return importItemFileParser;
	}

	public void setItemParser(IImportItemFileParser iImportItemFileParser) {
		this.importItemFileParser = iImportItemFileParser;
	}

	public List<String> getFiles() {
		return files;
	}

	public void setFiles(List<String> itemXmlFiles) {
		this.files = itemXmlFiles;
	}

	public String getFolderName() {
		return folderName;
	}

	public void setFolderName(String folderName) {
		this.folderName = folderName;
	}

	public String getStylesheetPath() {
		return stylesheetPath;
	}

	public void setStylesheetPath(String stylesheetPath) {
		this.stylesheetPath = stylesheetPath;
	}

	public TransformerFactory getTransformerFactory() {
		return transformerFactory;
	}

	public void setTransformerFactory(TransformerFactory transformerFactory) {
		this.transformerFactory = transformerFactory;
		transformerFactory.setAttribute(
				net.sf.saxon.FeatureKeys.SOURCE_PARSER_CLASS,
				"org.apache.xml.resolver.tools.ResolvingXMLReader");
		transformerFactory.setAttribute(
				net.sf.saxon.FeatureKeys.STYLE_PARSER_CLASS,
				"org.apache.xml.resolver.tools.ResolvingXMLReader");
		CatalogResolver resolver = new CatalogResolver();
		this.transformerFactory.setURIResolver(resolver);
	}

	public String getTransPath() {
		return transPath;
	}

	public void setTransPath(String transPath) {
		this.transPath = transPath;
	}

	public boolean hasNext() {
		return fileIterator.hasNext();
	}

	public ImportBatch next() {
		ImportBatch newBatch = new ImportBatch();

		File file = fileIterator.next();
		try {

			File transFolder = new File(transPath);

			if (file.getPath().endsWith(".xml")
					|| file.getPath().endsWith(".html")) {
				String filename = file.getName();

				if (template != null) {

					Transformer transformer = template.newTransformer();
				
					String outputName = transFolder.getPath() + File.separator
							+ filename;
					File transformedFile = new File(outputName);

					try {
						
						Document document = null;
						Source source = null;
						InputStreamReader r = null;
						FileInputStream fis;
						
						if (xslParams != null) {
							for (String xslParam : xslParams.keySet()) {
								transformer.setParameter(xslParam, xslParams
										.get(xslParam));
							}
						}					

						 try{
							 DocumentBuilder builder = domFactory.newDocumentBuilder();
							 document = builder.parse( file );
							 log.debug("Encoding is UTF-8 for file: " + file.getName());
						 }catch (Exception e){
							 fis = new FileInputStream(file.getAbsolutePath());
							 r = new InputStreamReader(fis, encoding);
							 log.debug("Encoding is ISO for file: " + file.getName());
						 }finally{
							 if(document != null){
								 source = new DOMSource(document);
							 }else if(r != null){
								 source = new StreamSource(r);
							 }
						 }
						 
						transformer.transform(source,
								new StreamResult(transformedFile));
						log.debug("Transforming the file");
						log.debug("Transformed file: " + transformedFile.getName());
						newBatch = this.getBatchFromXMLFile(transformedFile);
						
						log.debug("New batch file: " + newBatch.size());

					} catch (Exception e) {
						log.debug("Failed to transform file " + file.getName(),
								e);
						newBatch.addError("Failed to transform file "
								+ file.getName(), e);
					}
				}

			}

		} catch (Exception e) {

		}
		log.info("Transformed all files");
		if (removeItems) {
			log.debug("Removing source file");
			file.delete();
		}
		return newBatch;
	}

	public void remove() {
		// TODO Auto-generated method stub

	}

	private ImportBatch getBatchFromXMLFile(File f) {
		ImportBatch fileBatch = new ImportBatch();
		try {
			JAXBContext ctx = JAXBContext.newInstance(ImportBatch.class);

			log.debug("Getting items from  xml");
			Unmarshaller unmarshaller = ctx.createUnmarshaller();

			fileBatch = (ImportBatch) unmarshaller.unmarshal(f);
		} catch (JAXBException e) {
			log.debug("Cannot read xml file " + f.getAbsolutePath(), e);
		}

		if (fileBatch.size() == 0) {
			fileBatch.addError("No Items Parsed for xml file "
					+ f.getAbsolutePath());
		}

		for (ImportItem item : fileBatch) {
			item.getFields().put("importXMLFile", f.getName());
		}
		return fileBatch;
	}

	public boolean isRemoveItems() {
		return removeItems;
	}

	public void setRemoveItems(boolean removeItems) {
		this.removeItems = removeItems;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

}
