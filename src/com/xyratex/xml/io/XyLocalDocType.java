//-------------------------------------------------------------------------
// (C) COPYRIGHT Xyratex Storage Systems Division 2011
// All Rights Reserved
//
// Filename    : XyLocalDocType.java
// Author      : Rob Davis


// By          : Rob Davis
// File Type   : Java source code for Open Label Print
//-------------------------------------------------------------------------

package com.xyratex.xml.io;

import java.net.URI;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.xyratex.xml.io.XyLocalDocTypeGenerator;

import net.socialchange.doctype.Doctype;


/**
 * <p>Used in combination with XyLocalDocTypeGenerator and net.socialchange.doctype.DoctypeChangerStream 
 * to provide a Document Type for parsing against local SVG DTD files
 *</p>
 * 
 * <p>This enables W3C DOM parsing of documents against a local DTD.</p>
 * 
 * <p>We need the DTD to be local because otherwise the parser will try to fetch the 
 * DTD from the external W3C site. We need our deployment to be standalone, internal
 * for convenience, flexibility and security.
 * </p>
 * 
 * <p>XyLocalDocTypeGenerator and XyLocalDocType are used with net.socialchange.doctype.DoctypeChangerStream
 * which behaves as a standard input stream which is fed to a W3C DOM parser.
 * When an external reference to a DTD is encountered in the SVG document to be parsered,
 * the DoctypeChangerStream can intercept this and replace it with the local DTD file/location
 * defined by XyLocalDocType via XyLocalDocTypeGenerator.
 * </p>
 * 
 * @see XyLocalDocTypeGenerator
 * 
 * <p>See net.socialchange.doctype.Doctype and other DocTypeChanger documentation at: 
 *   <a href="http://doctypechanger.sourceforge.net/">http://doctypechanger.sourceforge.net/</a>
 * </p>
 */
public class XyLocalDocType implements Doctype
{
	/**
	 *  R = Release L = Level G = date U = time
	 *  Values calculated by SCCS when file is checked out and compiled.
	 */
	public static final String sccsid = "@(#)Xyratex  ISTP  XyLocalDocType.java  %R%.%L%, %G% %U%";
	
	private Log log = LogFactory.getLog(XyLocalDocType.class);
	
	private String publicId = null; // e.g. "W3C//DTD SVG 1.1//EN";
	private String rootElement = null; // e.g. svg
	private String systemId = null; // this is the path to the dtd, e.g. in the jar: "/com/xyratex/svg/dtd/local/svg11.dtd"
	

	
	public XyLocalDocType( String aRootElement, String aPublicId, String aSystemId )
	{
		log.trace(sccsid);
		
		rootElement = aRootElement;
		publicId = aPublicId;
		systemId = aSystemId;
	}
	
  /**
   * not required - does nothing - we have to implement to satisfy the DocType interface however
   * 
	 */	
	public String getInternalSubset()
	{
		return null;
	}

  /**
   * returns the standard SVG Public ID - i.e. "W3C//DTD SVG 1.1//EN";
   * 
	 */	
	public String getPublicId()
	{
		return publicId;
	}

  /**
   * returns the root element of a svg document - i.e. "svg"
   * 
	 */	
	public String getRootElement()
	{
		return rootElement;
	}

  /**
   * <p>returns the System ID of the SVG document
   * - this is the location of the DTD which is locally supplied rather than on a network.</p>
   * 
   * <p>Ideally this is packaged within the file system within the java executable file.
   * This avoids missing the files when deploying a Java application that uses this class.
   * </p>
   * 
	 */	
	public String getSystemId()
	{
		try
		{
		URI uri =
      this.getClass().getResource(systemId).toURI();
		
		log.trace( uri.toString() );

		return uri.toString();
		}
		catch( Exception e )
		{
			log.error( "getSystemId() exception", e );
		}

		return "";
		
	}

}
