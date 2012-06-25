/**
 * 
 */
package com.percussion.pso.rxws.item.processor.impl;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import javax.activation.MimetypesFileTypeMap;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.MemoryCacheImageOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.percussion.pso.importer.model.FieldMap;
import com.percussion.pso.importer.model.ImportBatch;
import com.percussion.pso.importer.model.ImportItem;
import com.percussion.pso.rxws.item.processor.RxWsItemProcessor;

public class FileProcessor implements RxWsItemProcessor {

	/**
	 * The log instance to use for this class, never <code>null</code>.
	 */


	/**
	 * Controls the size of steps by which the image is rescaled. 
	 * This value should always be a power of 2 (2,4,8...). Larger values will
	 * increase performance, but add sampling noise to the compressed image. 
	 * Defaults to 2. 
	 */
	private int stepFactor = 2;

	/**
	 * Maximum image size where interpolation is used.  For images larger
	 * than this size, the image pixels are not interpolated: the "nearest 
	 * pixel" algorithm is used instead. Increasing the value may increase
	 * image quality for images that are smaller than the specified size. 
	 * It will also decrease performance, sometimes significantly. 
	 * Defaults to 1,000,000 pixels.  
	 */
	private int maxInterpolationSize = 1000000; 

	private String imageFormat = "jpeg";

	private float compression = 0.85f;



	

	private final Log log = LogFactory.getLog(FileProcessor.class);

	private Map<String,Map<String,String>> fields;
	private String tempdir;
	private boolean checksumRequired;
	

	private String fileBase;


	public String getFileBase() {
		return fileBase;
	}


	public void setFileBase(String fileBase) {
		this.fileBase = fileBase;
	}


	public ImportBatch  processItems(ImportBatch  items) 
	throws Exception {
		log.debug("Processing Files");
		for(ImportItem item : items) {
			if(!item.getUpdateType().equals("ref")) {
				try{
					String contentType = item.getType();
					FieldMap extraFields = new FieldMap();
					for(String field : item.getFields().keySet()) {
						log.debug("Checking field "+field);
						Map<String,String>  fieldProps = fields.get(field);
						if (fieldProps == null) {
							fieldProps = fields.get(contentType + "." + field);
						}
						if (fieldProps != null) {
							log.debug("Processing field "+contentType + "." + field);
							int height = 0;
							int width = 0; 
							int maxDimension = 0; 
							String type="";
							if (fieldProps.containsKey("type")) {
								type = fieldProps.get("type");
							} else {
								type="raw";
							}
							if (fieldProps.containsKey("maxDimension")) {
								maxDimension = Integer.parseInt(fieldProps.get("maxDimension"));
							}
							if (fieldProps.containsKey("height")) {
								height = Integer.parseInt(fieldProps.get("height"));
							}
							if (fieldProps.containsKey("width")) {
								width = Integer.parseInt(fieldProps.get("width"));
							}

							Object filenameObject= item.getFields().get(field).getValue();

							if (filenameObject instanceof String) {
								String filename = item.getFields().get(field).getStringValue();

								if (!filename.startsWith("http")) {
									filename = fileBase + "/" + filename;
								}

								log.debug("Found field value="+filename);
								try {
									if (filename.startsWith("http")) {
										filename = download(filename);
									}

									FieldMap fieldMap = item.getFields();
									File fieldFile = new File(filename);
									if (filename != null ) {
										log.debug("Getting file " +fieldFile.getAbsolutePath());
										fieldMap.put(field, fieldFile);

										Dimension imageSize = null;
										if(type.equals("image")) {
											BufferedImage inImage = ImageIO.read(fieldFile);
											imageSize = new Dimension(inImage.getWidth(), inImage.getHeight()); 

											if(maxDimension >0 || width > 0 || height > 0) {
												Dimension outSize = computeSize(maxDimension, width, height,imageSize);  
												fieldFile = createThumbnail(fieldFile,outSize);
												imageSize = outSize;
											}
											if (!item.getFields().containsKey(field + "_height")) {
												extraFields.put(field + "_height", imageSize.height);
											}
											if (!item.getFields().containsKey(field + "_width")) {
												extraFields.put(field + "_width", imageSize.width);
											}

										}
										log.debug("type="+type);
										if(type.equals("image") || type.equals("file"))  {


											if(checksumRequired){
												String checksum = calcChecksum(fieldFile);
												extraFields.put(field + "_sum", checksum);
											}
											
											MimetypesFileTypeMap mimemap = new MimetypesFileTypeMap();
											mimemap.addMimeTypes("application/pdf pdf PDF");

											String mimeType = mimemap.getContentType(fieldFile);
											extraFields.put(field + "_type", mimeType);
											extraFields.put(field + "_size", fieldFile.length());
											String origfilename = fieldFile.getName();
											extraFields.put(field + "_filename", origfilename);
											extraFields.put(field + "_ext", origfilename.substring(origfilename.lastIndexOf(".")));
											
										}


									}
								} catch (MalformedURLException e) {
									log.error("Malformed URL Exception " + filename);
									item.addError("Malformed URL Exception " + filename);
									item.setUpdateType("ref");
								} catch (IOException e) {
									log.error("Could not read file " + filename);
									item.addError("Could not read file " + filename);
									item.setUpdateType("ref");
								}
							}		




						} 

					}
					item.getFields().putAll(extraFields);

				} catch (Exception e) {
					item.addError("Error Processing file", e);
					log.error("Error Processing file", e);
				}
			}
		}
		return items;
	}


	private String download(String url) throws IOException
	{
		String filename = null;
		URL u;

		u = new URL(url);

		URLConnection uc = u.openConnection();
		int contentLength = uc.getContentLength();
		if (contentLength == -1)
		{
			throw new IOException("This is an empty file.");
		}
		InputStream raw = uc.getInputStream();
		InputStream in = new BufferedInputStream(raw);
		byte[] data = new byte[contentLength];
		int bytesRead = 0;
		int offset = 0;
		while (offset < contentLength) {
			bytesRead = in.read(data, offset, data.length - offset);
			if (bytesRead == -1)
				break;
			offset += bytesRead;
		}
		in.close();

		if (offset != contentLength) {
			throw new IOException("Only read " + offset + " bytes; Expected " + contentLength + " bytes");
		}

		log.debug("url = " + u.getFile());
		filename = tempdir + "/" + u.getFile().substring(u.getFile().lastIndexOf("/") + 1);
		FileOutputStream out = new FileOutputStream(filename);
		out.write(data);
		out.flush();
		out.close();



		return filename;
	}


	/**
	 * Reads an image in a file and creates a thumbnail in another file. 
	 * Modified slightly to use ImageIO instead of Sun JPEG CODEC.  
	 * @param outstream the thumbnail image as a byte stream.  This image will
	 * always be coded as a JPEG. 
	 * @param instream the source image as a byte stream. 
	 * @param maxDim The width and height of the thumbnail must be maxDim pixels or less.
	 */
	protected File createThumbnail(File imageIn, Dimension outSize) throws IOException
	{
		try {
			// Get the image from a file.
			BufferedImage inImage = ImageIO.read(imageIn);
			// Determine the scale.

			while(inImage.getHeight() > outSize.height*stepFactor || inImage.getWidth() > outSize.width*stepFactor)
			{
				inImage = halfImage(inImage);
			}


			// Create an image buffer in which to paint on.
			int imageType = (inImage.getTransparency() == Transparency.OPAQUE) ? 
					BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
			BufferedImage outImage = new BufferedImage(outSize.width, outSize.height, imageType);

			// Paint image.
			Graphics2D g2d = outImage.createGraphics();
			g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
					RenderingHints.VALUE_INTERPOLATION_BICUBIC);
			g2d.drawImage(inImage, 0, 0, outSize.width, outSize.height, null);
			g2d.dispose();

			Iterator<ImageWriter> iter = ImageIO.getImageWritersByFormatName(imageFormat);
			ImageWriter iw = iter.next(); 
			ImageWriteParam iwp = iw.getDefaultWriteParam();
			iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);

			iwp.setCompressionQuality(compression); 

			String newFilePath = tempdir + "/" + outSize.width+"_"+outSize.height+"_"+ imageIn.getName();
			File outFile = new File(newFilePath);
			FileOutputStream outstream = new FileOutputStream(outFile);
			MemoryCacheImageOutputStream mcios = new MemoryCacheImageOutputStream(outstream);
			iw.setOutput(mcios); 

			iw.write(null, new IIOImage(outImage, new ArrayList<BufferedImage>(), null), iwp); 

			outstream.flush();
			return outFile;
		}
		catch (IOException e) {
			log.error("Could not create thumbnail " + e.getMessage(), e);
		}
		return null;
	}

	/**
	 * Computes the size of the thumbnail image based on parameters specified. 
	 * If height and width are both specified, these values define the size directly. 
	 * If only one is specified, then the aspect ration of the original image is used to 
	 * find the other value. 
	 * If height and width are both 0, then the maxDim parameter is used with Aspect
	 * ratio of the original image to find the thumbnail dimensions. 
	 * @param maxDim maximum dimension 
	 * @param thumbWidth desired width
	 * @param thumbHeight desired height
	 * @param originalSize size of the original image. 
	 * @return the size of the thumbnail. 
	 */
	protected Dimension computeSize(int maxDim, int thumbWidth, int thumbHeight, Dimension originalSize)
	{
		int width = 0;
		int height = 0;
		String emsg;
		if(thumbWidth > 0 && thumbHeight > 0 )
		{
			log.debug("using specified size " + thumbWidth + " w " + thumbHeight + " h "); 
			return new Dimension(thumbWidth, thumbHeight); 
		}
		double aspect = originalSize.getWidth() / originalSize.getHeight(); 
		if(thumbWidth > 0)
		{
			width = thumbWidth; 
			height = Long.valueOf(Math.round(width / aspect)).intValue(); 
			return new Dimension(width, height); 
		}
		if(thumbHeight > 0)
		{
			height = thumbHeight; 
			width = Long.valueOf(Math.round(height * aspect)).intValue(); 
			return new Dimension(width, height); 
		}
		//if we get here, neither height nor width was specified. 
		if(maxDim == 0)
		{
			emsg = "at least one of height, width and maxdim must be specified"; 
			log.error(emsg);
			throw new IllegalArgumentException(emsg); 
		}
		if(aspect > 1.0)
		{ // the image is wider than it is high
			width = maxDim; 
			height = Long.valueOf(Math.round(width / aspect)).intValue();
		}
		else
		{
			height = maxDim; 
			width = Long.valueOf(Math.round(height * aspect)).intValue();
		}
		return new Dimension(width, height); 
	}

	/**
	 * Scales an image in half, both vertically and horizontally
	 * @param inImage the original image
	 * @return the scaled image. Never <code>null</code>
	 */
	protected BufferedImage halfImage(BufferedImage inImage)
	{
		long timer = System.currentTimeMillis(); 
		int height = inImage.getHeight() / stepFactor;
		int width = inImage.getWidth() / stepFactor; 
		log.debug("Scaling to image height " + height + " width " + width );
		int imageType = (inImage.getTransparency() == Transparency.OPAQUE) ? 
				BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
		BufferedImage halfImage = new BufferedImage(width, height, imageType);
		Graphics2D half = halfImage.createGraphics();
		if((height * width) < maxInterpolationSize)
		{
			log.debug("using bilinear interpolation");    
			half.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
					RenderingHints.VALUE_INTERPOLATION_BILINEAR);  
		}
		/*half.setRenderingHint(RenderingHints.KEY_RENDERING, 
             RenderingHints.VALUE_RENDER_QUALITY); */ 
		/* half.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, 
             RenderingHints.VALUE_COLOR_RENDER_QUALITY); */
		half.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
				RenderingHints.VALUE_ANTIALIAS_ON);
		half.setRenderingHint(RenderingHints.KEY_DITHERING, 
				RenderingHints.VALUE_DITHER_ENABLE); 

		half.drawImage(inImage, 0, 0, width, height, 0, 0, inImage.getWidth(), inImage.getHeight(), null);
		if(log.isDebugEnabled())
		{
			long timestop = System.currentTimeMillis();
			long elapsed = timestop - timer; 
			log.debug("Time elapsed is " + elapsed);
		}      
		half.dispose();
		return halfImage;
	}




	public String calcChecksum(File input) throws NoSuchAlgorithmException, Exception {

		MessageDigest digest = MessageDigest.getInstance("MD5");
		String ret = "";
		InputStream is = new FileInputStream(input);				
		byte[] buffer = new byte[8192];
		int read = 0;
		try {
			while( (read = is.read(buffer)) > 0) {
				digest.update(buffer, 0, read);
			}		
			byte[] md5sum = digest.digest();
			ret = returnHex(md5sum);
		}
		catch(IOException e) {
			log.debug("Unable to process file for MD5", e);
		}
		finally {
			try {
				is.close();
			}
			catch(IOException e) {
				log.debug("Unable to close input stream for MD5 calculation", e);
			}
		}	
		return ret;

	}



	static String returnHex(byte[] inBytes) throws Exception {
		String hexString = "";
		for (int i=0; i < inBytes.length; i++) {
			hexString +=
				Integer.toString( ( inBytes[i] & 0xff ) + 0x100, 16).substring( 1 );
		}                                  
		return hexString;
	}                       

	public Map<String, Map<String, String>> getFields() {
		return fields;
	}


	public void setFields(Map<String, Map<String, String>> fields) {
		this.fields = fields;
	}


	public String getTempdir() {
		return tempdir;
	}


	public void setTempdir(String tempdir) {
		this.tempdir = tempdir;
	}

	public boolean isChecksumRequired() {
		return checksumRequired;
	}


	public void setChecksumRequired(boolean checksumRequired) {
		this.checksumRequired = checksumRequired;
	}



}