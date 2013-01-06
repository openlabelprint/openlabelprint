//-------------------------------------------------------------------------
//(C) COPYRIGHT Xyratex Storage Systems Division 2011
//All Rights Reserved
//
//Filename    : XySVGInsert.java
//Author      : Rob Davis
//Version     : %R%.%L%
//Last Update : %G% at %U%
//By          : Rob Davis
//File Type   : Java source code for Open Label Print
//-------------------------------------------------------------------------

package com.xyratex.svg.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * <p>
 * Singleton stateless class that provides a generic utility to insert, position and scale
 * an SVG document into another SVG document.
 * </p>
 * 
 * @author rdavis
 *
 */
public class XySVGInsert
{
	/**
	 *  R = Release L = Level G = date U = time
	 *  Values calculated by SCCS when file is checked out and compiled.
	 */
	public static final String sccsid = "@(#)Xyratex  ISTP  XySVGInsert.java  %R%.%L%, %G% %U%";
	
	/**
	 * log object used for logging activity in the form of String/character messages for debug, diagnostic and analysis purposes.
	 * The output destination for the log could be console output and/or file(s) depending on the user-definable overall configuration
	 * of logs in the system.
	 */
	private static Log log = LogFactory.getLog(XySVGInsert.class);
	
	/**
	 * <p>Insert and scale an SVG into another SVG at specified position.</p> 
	 * 
	 * @param hostSvgDocument (input/output parameter) the document that will accept the insertion (the host document)
	 * @param svgDocumentToInsert (input) the document to be inserted
	 * @param xScaleFactor (input) the x, horizontal, scale factor applied to the SVG document to be inserted
	 * @param yScaleFactor (input) the y, vertical, scale factor applied to the SVG document to be inserted
	 * @param placeholderElementInHostSvgDocument (input/output) the placeholder element in the host document, 
	 * this determines where in the co-ordinate space the inserted SVG will appear. It also
	 * marks the position in the host document SVG XML that the inserted SVG will go
	 * @param cssStyleForInsertedSvg (input) the Cascading Style Sheet applied to the inserted SVG
	 */
	public static void insert( 
		Document hostSvgDocument,
	  final Document svgDocumentToInsert,
	  String transform,
	  final Element placeholderElementInHostSvgDocument,
	  final String cssStyleForInsertedSvg )
	{		
		log.trace(sccsid // output version of class
		    + "\ninsert");


		String ns = hostSvgDocument.getDocumentElement().getNamespaceURI();
		
    String placeholderElementInHostSvgDocumentId = placeholderElementInHostSvgDocument.getAttribute("id");
    
    log.trace( "placeholderElementInHostSvgDocumentId = " + placeholderElementInHostSvgDocumentId );

    Node parentOfExisting = placeholderElementInHostSvgDocument.getParentNode();
    
		// create the <g>group tag to enclose the SVG to insert
		Element gPlaceholderNewContainerElement = hostSvgDocument.createElementNS(ns, "g");
		
		// make the group tag have the same id as the original rectangle place holder

		String gPlaceholderElementInHostSvgDocumentId = "CONTAINER_" + placeholderElementInHostSvgDocumentId;
		gPlaceholderNewContainerElement.setAttribute("id", gPlaceholderElementInHostSvgDocumentId );

		if ( cssStyleForInsertedSvg != null && cssStyleForInsertedSvg != "" )
		{	
		  gPlaceholderNewContainerElement.setAttribute( "style", cssStyleForInsertedSvg );
		}

		if ( transform != null && transform != "" )
		{
		  // the group tag enclosing the SVG to be inserted will have a transform that will be
		  // applied to the elements it contains - the SVG to be inserted itself so that it fits
      // the placeholder
		  gPlaceholderNewContainerElement.setAttribute("transform", transform);
		}

		String idOfplaceholderElementInHostSvgDocument = placeholderElementInHostSvgDocument.getAttribute("id");
		


		NamedNodeMap attributesOfExistingParent = parentOfExisting.getAttributes();
		
		// insert the group tag just before the original placeholder rectangle
		parentOfExisting.insertBefore(gPlaceholderNewContainerElement, placeholderElementInHostSvgDocument);

		// remove the original placeholder rectangle because we dont want this displayed
		parentOfExisting.removeChild(placeholderElementInHostSvgDocument);

		svgDocumentToInsert.setDocumentURI(ns);

		// insert the barcode xml into the <g> group so that the elements 
		// of the SVG to be inserted are children of the <g> tag
		//
		// first of all the elements of the SVG to be inserted have to be imported into the host document so
		// that they belong to the same document as the g group
		//
		// (true - deep copy - copy the entire svg document to be inserted element fragments across)
		Node svgToBeInsertedRootElement = svgDocumentToInsert.getDocumentElement();

		NodeList svgToBeInsertedElements = svgToBeInsertedRootElement.getChildNodes();

		final boolean deepCopy = true;
		for (int i = 0; i < svgToBeInsertedElements.getLength(); i++)
		{
			Node aSvgToBeInsertedElement = (Node) svgToBeInsertedElements.item(i);

			Node anImportedSvgToBeInsertedElement = hostSvgDocument.importNode(aSvgToBeInsertedElement, deepCopy);

			gPlaceholderNewContainerElement.appendChild(anImportedSvgToBeInsertedElement);
		}
	}
	
	public static void insert( 
			Document hostSvgDocument,
		  final Document svgDocumentToInsert,
		  final double xScaleFactor,
		  final double yScaleFactor,
		  final Element placeholderElementInHostSvgDocument,
		  final String cssStyleForInsertedSvg )
	{
		double svgDocumentToInsertTopLeftX 
	  = Double.valueOf( placeholderElementInHostSvgDocument.getAttribute("x") ).doubleValue();
	  double svgDocumentToInsertTopLeftY
	  = Double.valueOf( placeholderElementInHostSvgDocument.getAttribute("y") ).doubleValue();
		
	  // applied to svgDocumentToInsert to make it fit the rectangle bounds defined by placeholder placeholderElementInHostSvgDocument
		String transform =
		"translate(" 
		+ svgDocumentToInsertTopLeftX
		+ "," 
		+ svgDocumentToInsertTopLeftY
		+ ")"
		+ "scale("
		+ Double.toString(xScaleFactor)
		+ ","
		+ Double.toString(yScaleFactor)
		+ ")";
		
		insert( hostSvgDocument, svgDocumentToInsert, transform, placeholderElementInHostSvgDocument, cssStyleForInsertedSvg );
	}
}
