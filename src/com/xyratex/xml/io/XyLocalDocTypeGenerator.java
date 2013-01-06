//-------------------------------------------------------------------------
// (C) COPYRIGHT Xyratex Storage Systems Division 2011
// All Rights Reserved
//
// Filename    : XyLocalDocTypeGenerator.java
// Author      : Rob Davis


// By          : Rob Davis
// File Type   : Java source code for Open Label Print
//-------------------------------------------------------------------------

package com.xyratex.xml.io;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.socialchange.doctype.Doctype;
import net.socialchange.doctype.DoctypeGenerator;

/**
 * <p>Used in combination with XyLocalDocType and net.socialchange.doctype.DoctypeChangerStream 
 * to provide a Document Type for parsing SVG against local DTD files
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
 * @see XyLocalDocType
 * 
 * <p>See net.socialchange.doctype.Doctype and other DocTypeChanger documentation at: 
 *   <a href="http://doctypechanger.sourceforge.net/">http://doctypechanger.sourceforge.net/</a>
 * </p>
 */
public class XyLocalDocTypeGenerator implements DoctypeGenerator
{
  /**
   * 
   * Intercepts the original Document type and its external references e.g. DTD
   * A decision can be made as to what to provide instead as a replacement.
   * In the case of validating against a local SVG DTD we don't care what the
   * original DTD was and we provide a local version for the parser to use - this function
   * queries XyLocalSVGDocType to get the local DTD information required
   * 
	*/
	
	
	/**
	 *  R = Release L = Level G = date U = time
	 *  Values calculated by SCCS when file is checked out and compiled.
	 */
	public static final String sccsid = "@(#)Xyratex  ISTP  XyLocalDocTypeGenerator.java  %R%.%L%, %G% %U%";
	
	
	private Log log = LogFactory.getLog(XyLocalDocTypeGenerator.class);
	
	private Doctype doctype = null;
	
	public XyLocalDocTypeGenerator( Doctype aDocType )
	{
		log.trace(sccsid);
		
		doctype = aDocType;
	}
	
	public Doctype generate(Doctype oldDoctype)
	{
		if (oldDoctype != null)
		{
			if (log.isTraceEnabled())
			{
				log.trace("oldDoctype.getPublicId() " + oldDoctype.getPublicId());

				log.trace("oldDoctype.getRootElement() " + oldDoctype.getRootElement());

				log.trace("oldDoctype.getSystemId() " + oldDoctype.getSystemId());

				log.trace("oldDoctype.getInternalSubset() "
				    + oldDoctype.getInternalSubset());
			}
		}
		return doctype;
	}
}
