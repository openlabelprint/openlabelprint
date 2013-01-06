//-------------------------------------------------------------------------
// (C) COPYRIGHT Xyratex Storage Systems Division 2011
// All Rights Reserved
//
// Filename    : XyAbstractConditionalCSSElementListProducer.java
// Author      : Rob Davis


// By          : Rob Davis
// File Type   : Java source code for Open Label Print
//-------------------------------------------------------------------------

package com.xyratex.svg.css;

import java.util.List;
import java.util.ListIterator;
import java.util.Vector;

import org.w3c.css.sac.Selector;
import org.w3c.css.sac.SelectorList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.batik.bridge.BridgeContext;

import org.apache.batik.css.engine.CSSStyleSheetNode;
import org.apache.batik.css.engine.Rule;
import org.apache.batik.css.engine.SVGCSSEngine;
import org.apache.batik.css.engine.StyleDeclaration;
import org.apache.batik.css.engine.StyleRule;
import org.apache.batik.css.engine.StyleSheet;
import org.apache.batik.css.engine.sac.CSSConditionalSelector;
import org.apache.batik.css.engine.value.Value;

import org.apache.batik.gvt.CompositeGraphicsNode;
import org.apache.batik.gvt.GraphicsNode;


/**
 * <p>An abstract class providing generic tools to objects of a derived concrete class for selecting XML elements
 * that use a certain CSS style and with certain values in that style</p>
 * 
 * <p>This class provides the generic mechanism for selecting elements using a CSS style-value combination based on 
 * the criteria supplied by an inheriting concrete class</p>
 * 
 * <p>It can be used to return the styles themselves that use a certain value and also the elements that use that style</p>
 * 
 * <p>
 * An example of this is a SVG document exported from a drawing package that contains elements that use non-compliant CSS.
 * CorelDRAW Graphics Suite 12 (without patches) exports SVG with CSS that contains negative font size.
 * Together with a derived class, this class enables the affected elements to be selected and processed 
 * as needed.
 * </p>
 * 
 * @see com.xyratex.svg.deviation.XyCorelExportSVGCSSNegativeFontSize
 *
 * <p>See also the batik javadoc documentation: <a href="http://xmlgraphics.apache.org/batik/javadoc/">http://xmlgraphics.apache.org/batik/javadoc/</a></p>
 * 
 */
public abstract class XyAbstractConditionalCSSElementListProducer
{
	/**
	 *  <p>R = Release L = Level G = date U = time</p>
	 *  <p>Values calculated by SCCS when file is checked out and compiled.</p>
	 */
	public static final String sccsid = "@(#)Xyratex  ISTP  XyAbstractConditionalCSSElementListProducer.java  %R%.%L%, %G% %U%";
	
	protected SVGCSSEngine cssEngine = null;
	protected Document doc = null;
	protected BridgeContext ctx = null;
	protected GraphicsNode rootGN = null;

	private static Log log = LogFactory.getLog(XyAbstractConditionalCSSElementListProducer.class);

	/**
	 * Abstract generic constructor
	 * 
	* @param document (input parameter) - the original SVG W3C Document containing the non-compliant SVG CSS
	* @param bridgeContext (input parameter) the BridgeContext batik object linking W3C DOM elements with their graphical W3C SVG/batik equivalents - vital for the region filtering system to work
	* @param aGraphicsNode (input parameter) the GraphicsNode object to start with - only it and its children are considered to see if 
	* they have the CSS Style-value combination. usually the GraphicsNode supplied is the RootGraphicsNode - equivalent to the svg element so the entire document is considered. 
	* But asking for a graphics node enables restricting to parts of the document
	*/
	public XyAbstractConditionalCSSElementListProducer( final Document document, final BridgeContext bridgeContext, final GraphicsNode aGraphicsNode )
	{
		log.trace(sccsid);
		
	  doc = document;
		ctx = bridgeContext;
		 
    rootGN = aGraphicsNode;
		 
		cssEngine = (SVGCSSEngine) ctx.getCSSEngineForElement(doc.getDocumentElement());
	}
	
	/**
	 * <p>private recursive function to traverse the SVG (represented as a batik GraphicsNode tree) and find
	 * the elements affected by the CSS style-value combination</p>
	 * 
	* @param  cssConditionalSelector (input parameter) the CSS style-value combination 
	* @param  ctx (input parameter) the BridgeContext linking W3C DOM Element representation of SVG document with the batik/W3C SVG graphical representation
	* @param  graphicsNode (input parameter) the starting top-level GraphicsNode, usually the root of the document, though subtrees are allowed if only concerned with a section of the document
	* @param  iteratorOnGraphicsNode (input/output parameter) the ListIterator determining the current position in the batik/w3c svg representation of the svg document - this is modified by the function as the traversal proceeds
	* @param  elementList (input/output parameter) the elements as a Vector list collection - that is built up as we traverse the document
	* 
	*/
	private void updateElementsUsingCSSStyle(
	    CSSConditionalSelector cssConditionalSelector,
	    BridgeContext ctx,
	    GraphicsNode graphicsNode,
	    ListIterator iteratorOnGraphicsNode,
	    Vector<Element> elementList )
	{
		// For type safety we'd like to have a more strongly typed ListIterator and List.
		// But batik CompositeGraphicsNode just returns a list of Object(s) so there is no value
		// - we still get a compiler warning.
		
		if (graphicsNode instanceof CompositeGraphicsNode)
		{
			CompositeGraphicsNode compositeGraphicsNode = (CompositeGraphicsNode) graphicsNode;

			List rootGNchildren = compositeGraphicsNode.getChildren();

			for ( ListIterator gniterator = rootGNchildren.listIterator(); 
			      gniterator.hasNext();)
			{
				Object object = gniterator.next();

				updateElementsUsingCSSStyle(cssConditionalSelector, ctx,
				    (GraphicsNode)object, gniterator, elementList );

			}
		}
		else // its not a node that contains other nodes, so this is our recursion stopping/windback
		{
			Element element = ctx.getElement((GraphicsNode) graphicsNode);

			if (element != null)
			{
				String elementName = element.getTagName();

				String elementStyleClass = element.getAttribute("class");

				String textContent = element.getTextContent();

				// some of the things we are logging aren't actually already obtained for use
				// in the logic so we can save overhead if we don't have debugging enabled by
				// not trying to fetch these
				if ( log.isTraceEnabled() )
				{
					log.trace("element.getTagName() " + elementName);
					log.trace("element.getTextContent() " + textContent);
					log.trace("element.getAttribute(\"class\") "
							+ elementStyleClass);
				}
				
				boolean b = cssConditionalSelector.match(element, elementName);

				if (b)
				{
					if ( log.isTraceEnabled() )
					{
						log.trace("match");

						log.trace("cssConditionalSelector.toString() "
								+ cssConditionalSelector.toString());

						log.trace("element.getAttribute(\"class\") "
								+ elementStyleClass);

						log.trace(element.getTextContent());
					}
					
					elementList.add(element);
				}
			}
			
			return;
		}
	}

	

	/**
	 * <p>Gets the list of CSS styles based on a style-value combination specified by a derived concrete class 
	 * in its constructor and its implementation of the condition method
	 * </p> 
	 * 
	 * @return list of CSS styles
	 * 
	*/
	public Vector<CSSConditionalSelector> getCSSConditionalSelectorList()
	{
		Vector<CSSConditionalSelector> cssList = new Vector<CSSConditionalSelector>();

		// For type safety we'd like to have a more strongly typed
		// List of CSSStyleSheetNode(s) rather than Object(s).
		// But batik SVGCSSEngine just returns a list of Object(s) so there is no value
		// in defining a List of CSSStyleSheetNode(s) - we still get a compiler warning.
		List styleSheetsList = cssEngine.getStyleSheetNodes();

		// assuming one internal style sheet defined within the document
		CSSStyleSheetNode cssNode = (CSSStyleSheetNode) styleSheetsList.get(0);

		StyleSheet styleSheet = cssNode.getCSSStyleSheet();

		int numRules = styleSheet.getSize();

		for (int ruleIndex = 0; ruleIndex < numRules; ruleIndex++)
		{
			Rule rule = styleSheet.getRule(ruleIndex);

			if (rule instanceof StyleRule)
			{
				StyleRule sr = ((StyleRule) rule);

				StyleDeclaration sd = ((StyleRule) rule).getStyleDeclaration();

				if ( log.isTraceEnabled() )
				{
					log.trace("rule.getType() " + rule.getType());
					log.trace("sr.toString() " + sr.toString());
					log.trace("sd.toString() " + sd.toString());
				}
				
				int sdlen = sd.size();

				for (int sdindex = 0; sdindex < sdlen; sdindex++)
				{
					Value val = sd.getValue(sdindex);

					if ( log.isTraceEnabled() ) { log.trace("sd.getIndex(sdindex) " + sd.getIndex(sdindex)); };

					if (condition(sd, sdindex))
					{
						SelectorList sl = sr.getSelectorList();

						int sllen = sl.getLength();

						for (int slindex = 0; slindex < sllen; slindex++)
						{

							Selector selector = sl.item(slindex);

							if (selector instanceof CSSConditionalSelector)
							{
								CSSConditionalSelector cssConditionalSelector = (CSSConditionalSelector) selector;

								if ( log.isTraceEnabled() ) { log.trace("cssConditionalSelector.toString() "
								    + cssConditionalSelector.toString()); }

							  cssList.add(cssConditionalSelector);
							}
						} // end for slindex

						if ( log.isTraceEnabled() )
						{
							log.trace("val.getCssValueType() " + val.getCssValueType());
					    log.trace("val.getCssText() " + val.getCssText());
						}
					} // end style declaration iteration

					// cater for DOM Exception
				}

				log.trace("StyleRule");
			} // end instanceof style rule

			if ( log.isTraceEnabled() ) 
			{
				log.trace("rule.toString()" + rule.toString());
			}
		}

		if ( log.isTraceEnabled() ) 
		{ 
			log.trace("styleSheet.getSize() " + styleSheet.getSize()); 
			log.trace("styleSheet.toString() " + styleSheet.toString());
		}

		return cssList;

	}
	
	
	/**
	 * <p>get a list of elements affected by a CSS style-value combination
	 * </p> 
	 * 
	 * <p>The style value combination is specified by the derived class
	 * constructor and its implementation of the condition method
	 * </p>
	 * 
	 * @return elements
	 * 
	*/
	public Vector<Element> getElements()
	{
		Vector<CSSConditionalSelector> cssConditionalSelectorList = getCSSConditionalSelectorList();
		
		Vector<Element> elementList = new Vector<Element>();
		
		for ( int i = 0; i < cssConditionalSelectorList.size(); i++ )
		{
			updateElementsUsingCSSStyle( (CSSConditionalSelector)cssConditionalSelectorList.get(i), ctx,
					rootGN, null, elementList );

		}
		
		return elementList;

	}
	

	// this method is meant to be overriden
	protected abstract boolean condition( StyleDeclaration sd, int sdindex );

}
