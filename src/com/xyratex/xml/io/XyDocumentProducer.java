//-------------------------------------------------------------------------
//(C) COPYRIGHT Xyratex Storage Systems Division 2011
//All Rights Reserved
//
//Filename    : XyDocumentProducer.java
//Author      : Rob Davis
//Version     : %R%.%L%
//Last Update : %G% at %U%
//By          : Rob Davis
//File Type   : Java source code for Open Label Print
//-------------------------------------------------------------------------

package com.xyratex.xml.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMErrorHandler;
import org.w3c.dom.Document;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSParser;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.xyratex.label.config.XyCharacterEncoding;

import net.socialchange.doctype.DoctypeChangerStream;
import net.socialchange.doctype.DoctypeGenerator;


/**
 * <p>A non-vendor-specific general toolkit for loading and parsing W3C DOM XML documents and saving W3C DOM documents as standard XML text files.
 * </p>
 * 
 * <p>The toolkit is dependent on a vendor but because this is abstracted behind standard W3C DOM interfaces then
 * the vendor can be changed without impacting the code.</p>
 * 
 * <p>The current vendor used is the respected Apache Xerces-J</p>
 * 
 * <p>See net.socialchange.doctype.Doctype and other DocTypeChanger documentation at: 
 *   <a href="http://doctypechanger.sourceforge.net/">http://doctypechanger.sourceforge.net/</a>
 * </p>
 * 
 * @author rdavis
 *
 */
public class XyDocumentProducer 
{
	/**
	 *  <p>R = Release L = Level G = date U = time</p>
	 * 
	 */
	public static final String sccsid = "@(#)Xyratex  ISTP  XyDocumentProducer.java  %R%.%L%, %G% %U%";
	
	private static Log log = LogFactory.getLog(XyDocumentProducer.class);
	
	public static Document getDocument( String rootElement, InputStream xmlAsInputStream  ) throws FileNotFoundException, IllegalAccessException, InstantiationException, ClassNotFoundException
	{
		XyLocalDocType aLocalDocType = null;
		DOMErrorHandler aDOMErrorHandler = null;
		Document doc = null;
		

    if ( rootElement.equals("svg") )
    {
    	aLocalDocType = new XyLocalDocType( "svg", "W3C//DTD SVG 1.1//EN", "/com/xyratex/svg/dtd/local/svg11.dtd" );
    	aDOMErrorHandler = new XyDOMErrorHandler();
    	boolean ignoreWhitespace = false;
    	doc = getDocument( xmlAsInputStream,
          new XyLocalDocTypeGenerator( aLocalDocType ),
          aDOMErrorHandler,
          ignoreWhitespace);
    }
    else
    {
      // we don't care about anything other than elements, attributes within them and child elements
      // so we ignore the whitespace, which means we can robustly check a updated populator
      // document with the last one to see if any of the above items of concern have changed.
    	if ( rootElement.equals("label") )
    	{
    		aLocalDocType = new XyLocalDocType( "label", "", "/com/xyratex/label/population/dtd/local/labelpopulator.dtd" );
    		aDOMErrorHandler = new XyDOMErrorHandler();
        boolean ignoreWhitespace = true;
    		doc = getDocument( xmlAsInputStream,
            new XyLocalDocTypeGenerator( aLocalDocType ),
            aDOMErrorHandler,
            ignoreWhitespace);
    	}
    }
    
    return doc;
	}  
   

  public static Document getDocument( String rootElement, File xmlAsFile ) throws FileNotFoundException, IllegalAccessException, InstantiationException, ClassNotFoundException
  {
	  FileInputStream xmlAsFileInputStream = new FileInputStream( xmlAsFile );
  	
		return getDocument( rootElement, xmlAsFileInputStream );
  }
  
  public static Document getDocument( String rootElement, String xmlAsString ) throws FileNotFoundException, IllegalAccessException, InstantiationException, ClassNotFoundException
  {
    InputStream xmlAsInputStream = IOUtils.toInputStream( xmlAsString );
  	
		return getDocument( rootElement, xmlAsInputStream );
  }
	
	private static Document getDocument( 
	  InputStream xmlAsInputStream,
	  DoctypeGenerator aLocalDocTypeGenerator,
	  DOMErrorHandler aDomErrorHandler,
	  boolean ignoreWhitespace) 
	  throws 
	    IllegalAccessException,
	    InstantiationException,
	    ClassNotFoundException    
	{	
  	log.trace( sccsid + "\ngetDocument" );
		
		DOMConfiguration config;
		
		DOMImplementationRegistry registry;
		DOMImplementationLS lsImpl;
		LSParser parser;
		
	   registry =
			  DOMImplementationRegistry.newInstance( );
		  
		   lsImpl =
			  (DOMImplementationLS)registry.getDOMImplementation("LS");
		   
		   parser =
			  lsImpl.createLSParser(DOMImplementationLS.MODE_SYNCHRONOUS,
			  null);
		
	  // Set options on the parser
	  config = parser.getDomConfig( );
	  config.setParameter("validate", Boolean.TRUE);
	 
	  config.setParameter("error-handler", aDomErrorHandler );
	  
	  if ( ignoreWhitespace )
	  {
	    config.setParameter("element-content-whitespace", Boolean.FALSE);
	  }

	  LSInput lsinput = lsImpl.createLSInput();
	  
	  lsinput.setEncoding(XyCharacterEncoding.ENCODING);
	  
	  DoctypeChangerStream doctypeChangerStream = new DoctypeChangerStream(xmlAsInputStream);
	  
	  doctypeChangerStream.setGenerator(aLocalDocTypeGenerator);
	  
	  lsinput.setByteStream(doctypeChangerStream);
	  
	  org.w3c.dom.Document doc = parser.parse(lsinput);

		return doc;
	}
}
