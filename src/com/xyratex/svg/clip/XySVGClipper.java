//-------------------------------------------------------------------------
// (C) COPYRIGHT Xyratex Storage Systems Division 2011
// All Rights Reserved
//
// Filename    : XySVGClipper.java
// Author      : Rob Davis


// By          : Rob Davis
// File Type   : Java source code for Open Label Print
//-------------------------------------------------------------------------

package com.xyratex.svg.clip;

import java.util.Iterator;
import java.util.Vector;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.DocumentTraversal;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.traversal.TreeWalker;
import org.w3c.dom.Document;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.batik.bridge.BridgeContext;

/**
 * <p>Part of the filter system used to determine if a SVG XML element is inside or outside a region in the SVG drawing.</p>
 * 
 * <p>Part of a generic Xyratex library for manipulating SVG.</p>
 * 
 * <p>An example application is in labelling
 * whereby a label drawing specification supplied from industrial designers can be taken and just the label
 * itself can be extracted and used for generating printed labels directly on the line. The industrial designer
 * can tag a region to denote the label outline and this filter can then use this to work out what is part of
 * the label (i.e. inside the label) and what is outside - i.e. ancillary notation and overall document headings,
 * designer names, document numbers etc.)</p>
 * 
 * <p>This class provides the W3C DOM traversal of the SVG document and uses the XySVGClipFilter as the
 * implementation of W3C DOM NodeFilter interface to select the required elements during the traversal.</p>
 * 
 * <p>NOTE: this XySVGClipper class and the XySVGClipFilter may be obsoleted as instantiation of the batik
 * already provides a document tree which could be traversed instead, rather than using the W3C DOM tree.
 * Since the filtering requires batik this makes sense. The savings will be CPU cycles at least and at best
 * that and memory footprint as fewer data structures are required.</p>
 * 
 * @see XySVGClipFilter
 * 
 * <p>See also the batik javadoc documentation: <a href="http://xmlgraphics.apache.org/batik/javadoc/">http://xmlgraphics.apache.org/batik/javadoc/</a></p>
 */
public class XySVGClipper 
{
	/**
	 *  <p>R = Release L = Level G = date U = time</p>
	 *  <p>Values calculated by SCCS when file is checked out and compiled.</p>
	 */
	public static final String sccsid = "@(#)Xyratex  ISTP  XySVGClipper.java  %R%.%L%, %G% %U%";
	
  public static final int CLIPPER_REMOVE_UNSET = 0;
  public static final int CLIPPER_REMOVE_INSIDE = 1;
  public static final int CLIPPER_REMOVE_OUTSIDE = 2;

  private static Log log = LogFactory.getLog(XySVGClipper.class);


 /**
  * <p>
  * buildList
  * </p>
  * 
  * <p>
  * A recursive helper function used by the clip function to traverse the W3C DOM
  * tree of the SVG document, building the list of filtered elements.
  * </p>
  * 
  * @param tw
  *          (input parameter) TreeWalker object that implements the W3C DOM
  *          interface, allowing traversal of the XML document - i.e. the SVG
  * @param list
  *          (input/output parameter) cumulative Vector list of elements
  *          collected during the traversal of the filtered tree. This parameter
  *          is modified - new elements are added to the list
  */
  private static void buildList(TreeWalker tw, Vector<Node> list)
	{
		Node n = tw.getCurrentNode();

		// do the remove here
		log.trace("remove:" + n.getNodeName());

		if (!n.getLocalName().equals("svg")) list.add(n);

		for (Node child = tw.firstChild(); child != null; child = tw.nextSibling())
		{
			buildList(tw, list);
		}

		tw.setCurrentNode(n);
	}


 /**
  *  <p>Perform the filter clip</p>
  *
  * @param  originalSvgDocument (input/output parameter) the original SVG W3C Document to apply the filter to. This object is modified following the filter to contain elements remaining resulting from the filtering 
  * @param  labelOutlineTag (input parameter) the elementid String of the element defining the outline filtering region
  * @param  preExcludedElements (input parameter) elements as a Vector list collection that we will automatically remove regardless of their position relative to the filtering region
  * @param  preIncludedElements (input parameter) elements as a Vector list that we will automatically keep regardless of their position relative to the filtering region
  * @param  clipperMode (input parameter) do we want to select elements inside (CLIPPER_REMOVE_INSIDE) the filtering region or outside (CLIPPER_REMOVE_OUTSIDE)
  * @param  ctx (input parameter) BridgeContext object - its purpose is for linking W3C DOM elements with their graphical W3C SVG/batik equivalents - vital for the region filtering system to work
  *
  */
  public static void clip( 
    Document originalSvgDocument,
    final String labelOutlineTag,
    final Vector<Element> preExcludedElements,
    final Vector<Element> preIncludedElements,
    final int clipperMode,
    final BridgeContext ctx  ) 
	{
  	log.trace(sccsid);
  	
		Vector<Node> listOfSvgElementsOutsideLabel = new Vector<Node>();

		// get the root of the XML document
		Node root = originalSvgDocument.getLastChild();

		// instantiate the filter object
		XySVGClipFilter xySVGClipFilter = new XySVGClipFilter(labelOutlineTag,
		    originalSvgDocument, clipperMode, ctx);

		xySVGClipFilter.addPreExcludedElements(preExcludedElements);
		xySVGClipFilter.addPreIncludedElements(preIncludedElements);

		// create an object of the TreeWalker implementation class
		DocumentTraversal docTraversal = (DocumentTraversal) originalSvgDocument;
		TreeWalker tw = docTraversal.createTreeWalker(root, NodeFilter.SHOW_ELEMENT, xySVGClipFilter, true);

		// print the elements of the TreeWalker implementation class
		buildList(tw, listOfSvgElementsOutsideLabel);

		Iterator<Node> elementsToRemoveIterator = listOfSvgElementsOutsideLabel.listIterator();

		while (elementsToRemoveIterator.hasNext())
		{
			org.w3c.dom.Element elementToRemove = (org.w3c.dom.Element) elementsToRemoveIterator.next();

			log.trace("elementToRemove = " + elementToRemove.getTagName());

			elementsToRemoveIterator.remove();

			Node parent = elementToRemove.getParentNode();
			parent.removeChild(elementToRemove);
		}
	}
}