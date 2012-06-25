package com.percussion.pso.importer.impl;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.axis.utils.TeeOutputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xml.resolver.tools.CatalogResolver;
import org.w3c.dom.Document;
import org.w3c.tidy.Configuration;
import org.w3c.tidy.Tidy;
import org.xml.sax.XMLReader;
import org.xml.sax.InputSource;

import com.percussion.pso.importer.IImportItemFileParser;
import com.percussion.pso.importer.IImportItemRefConsumer;
import com.percussion.pso.importer.model.ImportBatch;
import com.percussion.pso.importer.model.ImportItem;
public class HttpTransformProducer implements IImportItemRefConsumer<String> {

	/**
	 * The log instance to use for this class, never <code>null</code>.
	 */
	private static final Log log = LogFactory.getLog(HttpTransformProducer.class);


	private IImportItemFileParser importItemFileParser;

	private List<String> files;
	private String folderName;
	private String stylesheetPath;
	private String transPath;
	private String tidyPath;
	private HashMap<String,String> xslParams;

	private boolean removeItems = false;
	private Templates template = null;
	private TransformerFactory transformerFactory;
	private String url;
	private boolean tidy=true;
	
	public String getUrl() {
		return url;
	}


	public void setUrl(String url) {
		this.url = url;
	}

	private String encoding="UTF-8";

	public Iterator<ImportBatch> produce()
	throws Exception {   
		
		log.debug("producing items");
		ImportBatch newBatch = new ImportBatch();
		List<ImportBatch> batchList = new ArrayList<ImportBatch>();
		
		if (stylesheetPath != null) {
			log.debug("getting xsl");
			template = transformerFactory.newTemplates(new StreamSource(new File(stylesheetPath)));

		}

		try {
			URL pageUrl = new URL(url);

			if (template!=null) {

				Transformer transformer = template.newTransformer();


				File transformedFile = createTempFilename(transPath,pageUrl);

				try {

					URLConnection uc = pageUrl.openConnection();
					int contentLength = uc.getContentLength();
					String contentType = uc.getContentType();
					String itemEncoding = uc.getContentEncoding();
					
					
					if (uc.getContentEncoding()!=null) {
						itemEncoding = uc.getContentEncoding();
					} else {
						itemEncoding = encoding;
					}
					log.debug("Request for "+url+ " regurned type="+contentType+" encoding="+itemEncoding+" length="+contentLength);
					InputStream raw2 = uc.getInputStream();


					InputStream raw = stripScript(convertStreamToString(raw2,itemEncoding),itemEncoding);
					
					transformer.setParameter("pageurl", url.toString());
					transformer.setParameter("isDefaultItem", transformedFile.getPath().contains("folderdefault.htm")?"yes":"no");
					if(xslParams!= null) {
						for(String xslParam : xslParams.keySet()) {
							transformer.setParameter(xslParam, xslParams.get(xslParam));
						}
					}
					
					if (tidy) {

						File tidyFile =  createTempFilename(tidyPath,pageUrl);
						Tidy tidy = new Tidy();
						tidy.setXmlOut(true);
						tidy.setCharEncoding(Configuration.UTF8);

						tidy.setFixComments(true);
						tidy.setMakeClean(true);
						tidy.setXHTML(true);

						ByteArrayOutputStream errorsOut = new ByteArrayOutputStream();
						ByteArrayOutputStream out = new ByteArrayOutputStream();
						FileOutputStream file = new FileOutputStream(tidyFile );

						OutputStream tee = new TeeOutputStream( out, file );


						Document doc = tidy.parseDOM(raw,errorsOut);
						
// 						XMLReader reader = new Parser();
//     					reader.setFeature(Parser.namespacesFeature, true);
//     					reader.setFeature(Parser.namespacePrefixesFeature, true);
//     					reader.setFeature(Parser.ignoreBogonsFeature, true);
//
//						Transformer tagSoupTransformer = TransformerFactory.newInstance().newTransformer();
//						StreamResult sr = new StreamResult(tee);
//						tagSoupTransformer.transform(new SAXSource(reader, new InputSource(raw)),sr);     					
//     					

						tidy.pprint(doc, tee);
						tee.flush();
						
						log.debug("Written to file"+ tidyFile.getPath());
						ByteArrayInputStream r =  new ByteArrayInputStream(out.toByteArray());
						transformer.transform(new StreamSource(r), new StreamResult(transformedFile));
					} else {
						InputStream in = new BufferedInputStream(raw);
						InputStreamReader r = new InputStreamReader(in, encoding);
						transformer.transform(new StreamSource(r), new StreamResult(transformedFile));
					}
					newBatch = this.getBatchFromXMLFile(transformedFile);

				} catch (Exception e) {
					log.debug("Failed  to transform url " + url, e);
					newBatch.addError("Failed to transform url " + url, e);
				}


			}	

		} catch (Exception e) {
			log.error("Failed to create import item from url : "+url,e);
		}
		log.info("Transformed all files");
		
		batchList.add(newBatch);
		return batchList.iterator();

		
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
		transformerFactory.setAttribute(net.sf.saxon.FeatureKeys.SOURCE_PARSER_CLASS,"org.apache.xml.resolver.tools.ResolvingXMLReader");
		transformerFactory.setAttribute(net.sf.saxon.FeatureKeys.STYLE_PARSER_CLASS, "org.apache.xml.resolver.tools.ResolvingXMLReader");
		CatalogResolver resolver = new CatalogResolver();
		this.transformerFactory.setURIResolver(resolver);
	}

	public String getTransPath() {
		return transPath;
	}

	public void setTransPath(String transPath) {
		this.transPath = transPath;
	}

	public void remove() {
		// TODO Auto-generated method stub

	}

	private ImportBatch getBatchFromXMLFile(File f) {
		ImportBatch fileBatch = new ImportBatch();
		try {
			JAXBContext ctx  = JAXBContext.newInstance(ImportBatch.class);


			log.debug("Getting items from  xml");
			Unmarshaller unmarshaller = ctx.createUnmarshaller();
		    fileBatch = (ImportBatch)unmarshaller.unmarshal(f);
		} catch (JAXBException e) {
			log.debug("Cannot read xml file "+f.getAbsolutePath(), e);
		}

		if (fileBatch.size() == 0) {
			fileBatch.addError("No Items Parsed for xml file "+ f.getAbsolutePath());
		}


		for(ImportItem item : fileBatch) {
			item.getFields().put("importXMLFile",f.getName());
		}
		log.debug("Filebatch size is "+fileBatch.size());
		return fileBatch;
	}

	public boolean isRemoveItems() {
		return removeItems;
	}


	public void setRemoveItems(boolean removeItems) {
		this.removeItems = removeItems;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public String getEncoding() {
		return encoding;
	}
	public boolean isTidy() {
		return tidy;
	}

	public void setTidy(boolean tidy) {
		this.tidy = tidy;
	}


	public String getTidyPath() {
		return tidyPath;
	}


	public void setTidyPath(String tidyPath) {
		this.tidyPath = tidyPath;
	}


	private File createTempFilename(String root, URL url) {
		File transFolder = new File(root);
		String outputName = transFolder.getPath() + File.separator + url.getHost().replace('.', '_') + File.separator + url.getPath();
		File transformedFile = new File(outputName);
		if (transformedFile.getName().equals("")) {
			outputName+= "folderdefault.htm";
			transformedFile = new File(outputName);
		} else if (!transformedFile.getName().contains(".")) {
			outputName+= File.separator + "folderdefault.htm";
			transformedFile = new File(outputName);
		}
		File folder = transformedFile.getParentFile();
		if(!folder.exists()) folder.mkdirs();
		return transformedFile;
	}

	private InputStream stripScript(String docString,String itemEncoding) throws UnsupportedEncodingException {

		String scriptTagRegEx="<script.*?</script>";

		Pattern pattern = 
			Pattern.compile(scriptTagRegEx,Pattern.DOTALL|Pattern.MULTILINE|Pattern.CASE_INSENSITIVE|Pattern.UNICODE_CASE);

		Matcher matcher = pattern.matcher(docString);
		;

		ByteArrayInputStream stream = new ByteArrayInputStream(matcher.replaceAll("").getBytes(itemEncoding));
		return stream;
	}

	public String convertStreamToString(InputStream is,String itemEncoding) throws UnsupportedEncodingException {
		/*
		 * To convert the InputStream to String we use the BufferedReader.readLine()
		 * method. We iterate until the BufferedReader return null which means
		 * there's no more data to read. Each line will appended to a StringBuilder
		 * and returned as String.
		 */
		BufferedReader reader = new BufferedReader(new InputStreamReader(is,itemEncoding));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return sb.toString();
	}

	public void setItemRef(String itemRef) {
		setUrl(itemRef);
	}


	public void setXslParams(HashMap<String,String> xslParams) {
		this.xslParams = xslParams;
	}


	public HashMap<String,String> getXslParams() {
		return xslParams;
	}

}
