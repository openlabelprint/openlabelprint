//-------------------------------------------------------------------------
// (C) COPYRIGHT Xyratex Storage Systems Division 2011
// All Rights Reserved
//
// Filename    : XyXMLDOMSearchUtils.java
// Author      : Rob Davis


// By          : Rob Davis
// File Type   : Java source code for Open Label Print
//-------------------------------------------------------------------------

package com.xyratex.xml.utils;

import java.util.HashMap;
import java.util.List;

import org.apache.batik.parser.AWTTransformProducer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jaxen.JaxenException;
import org.jaxen.SimpleNamespaceContext;
import org.jaxen.XPath;
import org.jaxen.dom.DOMXPath;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Singleton stateless class providing utilities for searching within W3C DOM XML Documents.
 * 
 * @author rdavis
 *
 */
public final class XyXMLDOMSearchUtils
{
	/**
	 *  R = Release L = Level G = date U = time
	 *  Values calculated by SCCS when file is checked out and compiled.
	 */
	public static final String sccsid = "@(#)Xyratex  ISTP  XyXMLDOMSearchUtils.java  %R%.%L%, %G% %U%";

	/**
	 * log object used for logging activity in the form of String/character messages for debug, diagnostic and analysis purposes.
	 * The output destination for the log could be console output and/or file(s) depending on the user-definable overall configuration
	 * of logs in the system.
	 */
	private static Log log = LogFactory.getLog(XyXMLDOMSearchUtils.class);
	
	/**
	 * <p>Find an element based on a partial match of an attribute value for a given attribute.</p>
	 * 
	 * <p>e.g.: To search for a svg rect element with attribute id partially matching XYBC in a document called theDocument
	 * the call would be getElementByPartialAttributeValueMatch( "XYBC", "id", "rect", theDocument )</p>
	 * 
	 * <p>
	 * This method is used where the caller expects to only find one element with a the specified attribute (param attribute)
	 * with a value that partially matches the specified search string (param partialAttributeValueMatch).
	 * </p>
	 * 
	 * @param partialAttributeValueMatch (input parameter) the partial value of the attribute to search in the document where attribute value is what is assigned to the attribute, as in attribute=attribute value
	 * @param attribute (input parameter) the attribute as in: attribute = attribute value
	 * @param elementTypeFilter (input parameter) define which elements to look for e.g. rect as in <rect attribute="attribute value"...
	 *        to find any element use *
	 * @param documentToSearchIn (input parameter) the W3C DOM XML Document to search within
	 * @return Element - if there is more than one element, then the first encountered will be selected
	 * @throws JaxenException if there is a problem with how the XPath has been constructed to perform the search.
	 */
  public static Element getElementByPartialAttributeValueMatch( final String partialAttributeValueMatch, final String attribute, final String elementTypeFilter, final Document documentToSearchIn ) throws JaxenException
  {
    XPath path = getXPathForPartialAttributeValueMatch( partialAttributeValueMatch, attribute, elementTypeFilter, documentToSearchIn );
    
    return (Element)path.selectSingleNode(documentToSearchIn);
  }
  
	/**
	 * <p>Return a list of elements based on a partial match of an attribute value for a given attribute.</p>
	 * 
	 * <p>e.g.: To search for a svg rect element with attribute id partially matching XYBC in a document called theDocument
	 * the call would be getElementByPartialAttributeValueMatch( "XYBC", "id", "rect", theDocument )</p>
	 * 
	 * <p>
	 * This method is used where the caller may expect to find <em>more than one</em> element with a the specified attribute (param attribute)
	 * with a value that partially matches the specified search string (param partialAttributeValueMatch).
	 * </p>
	 * 
	 * @param partialAttributeValueMatch (input parameter) the partial value of the attribute to search in the document where attribute value is what is assigned to the attribute, as in attribute=attribute value
	 * @param attribute (input parameter) the attribute as in: attribute = attribute value
	 * @param elementTypeFilter (input parameter) define which elements to look for e.g. rect as in <rect attribute="attribute value"...
	 *        to find any element use *
	 * @param documentToSearchIn (input parameter) the W3C DOM XML Document to search within
	 * @return List of Elements matching search criteria
	 * @throws JaxenException if there is a problem with how the XPath has been constructed to perform the search.
	 */
  public static List getElementsByPartialAttributeValueMatch( final String partialAttributeValueMatch, final String attribute, final String elementTypeFilter, final Document documentToSearchIn ) throws JaxenException
  {
    XPath path = getXPathForPartialAttributeValueMatch( partialAttributeValueMatch, attribute, elementTypeFilter, documentToSearchIn );
    
    return path.selectNodes(documentToSearchIn);
  }
  
  public static Node findAContainerNodeByTag( Node childNode, final String tagOfContainerToFind, final String rootNodeTag )
  {
    boolean  parentNodeWithIdReached = false;
    boolean reachedRootNodeTag = false;
    
    Node currentNode = childNode; // this is where we start our search from
    String containerNodeTag = "";
    Node containerNode = null; // initially - we haven't found it yet
    
    while ( (!parentNodeWithIdReached) && (!reachedRootNodeTag) )
    {
      //containerNode = (org.w3c.dom.Element) currentNode.getParentNode();
      containerNode = currentNode.getParentNode();
      currentNode = containerNode;

      log.trace("containerNode = " + containerNode.getNodeName());

      containerNodeTag = containerNode.getNodeName();
      if ( containerNodeTag == tagOfContainerToFind )
      {
        parentNodeWithIdReached = true;
      }
      
      if ( containerNodeTag.equals( rootNodeTag ))
      {
        reachedRootNodeTag = true;
      }
    }
    
    if ( ( reachedRootNodeTag ) && !(parentNodeWithIdReached) )
    {
      containerNode = null;
    }
    
    return containerNode;
  }
  
  /**
   * <p>Returns a JAXEN XPath object for use in performing partial matches of attribute values in elements
   * in a W3C DOM XML Document.</p>
   * 
   * @param partialAttributeValueMatch (input) the partial String to match against attribute values in the Document elements
   * @param attribute (input) the attribute to look at when performing the search
   * @param elementTypeFilter (input) option to restrict the search to a particular element name, or if * then all elements will be considered
   * @param documentToSearchIn (input) the W3C DOM XML Document to search in
   * @return the JAXEN XPath object
   * @throws JaxenException if there is a programmatical problem with creating the XPath object, i.e. it was supplied invalid values via
   * one or more of the above parameters
   */
  private static XPath getXPathForPartialAttributeValueMatch( final String partialAttributeValueMatch, final String attribute, final String elementTypeFilter, final Document documentToSearchIn ) throws JaxenException
  {
  	String documentRootElement = (documentToSearchIn.getDocumentElement()).getTagName();
  	
  	String namespace = documentToSearchIn.getDocumentElement().getNamespaceURI();
  	  	
    String xpathString = "//" + documentRootElement + ":" + elementTypeFilter + "[contains(@" + attribute + ", '" + partialAttributeValueMatch + "')]"; 
	
    log.trace( sccsid + "\ngetElementByPartialAttributeValueMatch\nxpathString " + xpathString );

    HashMap<String, String> map = new HashMap<String, String>();

    map.put( documentRootElement, namespace );
   
    XPath path = new DOMXPath(xpathString);

    path.setNamespaceContext( new SimpleNamespaceContext( map ) );
    
    return path;
  }
}
