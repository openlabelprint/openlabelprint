//-------------------------------------------------------------------------
// (C) COPYRIGHT Xyratex Storage Systems Division 2011
// All Rights Reserved
//
// Filename    : XyXMLDOMDocumentToStringService.java
// Author      : Rob Davis


// By          : Rob Davis
// File Type   : Java source code for Open Label Print
//-------------------------------------------------------------------------

package com.xyratex.xml.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;

import com.xyratex.label.config.XyCharacterEncoding;

/**
 * <p>This stateless singleton class provides a service for outputting
 * a W3C DOM XML Document as a String.</p>
 * 
 * @author rdavis
 *
 */
final public class XyXMLDOMDocumentToStringService
{
	/**
	 *  R = Release L = Level G = date U = time
	 *  Values calculated by SCCS when file is checked out and compiled.
	 */
	public static final String sccsid = "@(#)Xyratex  ISTP  XyXMLDOMDocumentToStringService.java  %R%.%L%, %G% %U%";
	
	/**
	 * log object used for logging activity in the form of String/character messages for debug, diagnostic and analysis purposes.
	 * The output destination for the log could be console output and/or file(s) depending on the user-definable overall configuration
	 * of logs in the system.
	 */
	private static Log log = LogFactory.getLog(XyXMLDOMDocumentToStringService.class);
	
	/**
	 * Outputs a W3C DOM Document as text XML in a String.
	 * 
	 * @param doc
	 * @return the XML in a String
	 * @throws Exception
	 */
	public static String outputAsString( final Document doc )
	  throws IllegalAccessException,
	         ClassNotFoundException,
	         InstantiationException,
	         IOException
	  
	{
		// This method is implemented independent of underlying parser.
    // Based on code described in Java and XML 3rd Edition, Brett D. McLaughlin & Justin Edelson, published by O'Reilly
		// ISBN-10: 0-596-10149-x
		// ISBN-13: 978-0-596-10149-7
		
		log.trace(sccsid);
		
		// This will search for a parser library that supports the W3C DOM Level 3 Load and Save (LS)  functionality.
    DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance();
		DOMImplementationLS lsImpl = (DOMImplementationLS) registry.getDOMImplementation("LS");

		// Serialize the document 
		LSSerializer serializer = lsImpl.createLSSerializer();

		LSOutput output = lsImpl.createLSOutput();

		StringWriter stringWriter = new StringWriter();

		output.setCharacterStream(stringWriter);

    output.setEncoding(XyCharacterEncoding.ENCODING);
		
		serializer.write(doc, output);

		ByteArrayOutputStream bos = new ByteArrayOutputStream();

		OutputStreamWriter outputStreamWriter = new OutputStreamWriter(bos);

		outputStreamWriter.write(stringWriter.toString());

		outputStreamWriter.close();

		return bos.toString();
	}
}
