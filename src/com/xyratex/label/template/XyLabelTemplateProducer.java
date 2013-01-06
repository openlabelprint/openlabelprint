//-------------------------------------------------------------------------
// (C) COPYRIGHT Xyratex Storage Systems Division 2011
// All Rights Reserved
//
// Filename    : XyLabelTemplateProducer.java
// Author      : Rob Davis


// By          : Rob Davis
// File Type   : Java source code for Open Label Print
//-------------------------------------------------------------------------

package com.xyratex.label.template;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.svg.SVGDocument;
import org.w3c.dom.svg.SVGElement;

import org.apache.batik.dom.svg.SAXSVGDocumentFactory;
import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.apache.batik.util.XMLResourceDescriptor;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jaxen.JaxenException;

import com.xyratex.label.config.XyCharacterEncoding;
import com.xyratex.label.tags.XyLabelTagRegistry;
import com.xyratex.svg.batik.XyBatikWrapper;

import com.xyratex.svg.batik.util.XyCombinedTransformProducer;
import com.xyratex.svg.batik.util.XyShapeProducer;
import com.xyratex.svg.clip.XySVGClipper;
import com.xyratex.svg.deviation.XyCorelExportSVGCSSNegativeFontSize;
import com.xyratex.svg.utils.XySVGInsert;
import com.xyratex.xml.io.XyXMLDOMDocumentToStringService;
import com.xyratex.xml.utils.XyXMLDOMSearchUtils;

//TODO!!!! check placeholders not affected by transform

/**
 * <p>The component for creating a label template from a tagged label drawing supplied by the industrial designers.</p>
 * 
 * <p>The assumption is that the industrial designer (or otherwise) has tagged the outline of the label in the drawing
 * and also the rectangular place holders for the barcodes and also the dummy text for the human readable part</p>
 * 
 * <p>This is the first stage of three of the label production process. It is a one-off
 *    activity executed only once for each label design.
 *    Stages 2 and 3 are concerned with the production - i.e. for each real physical label printed
 *    Stage 2 is the population of a copy of the label template from stage 1 with barcodes and human readable fields
 *    using {@link com.xyratex.label.population.XyLabelPopulator}. 
 *    Stage 3 generates the bitmap of this label, using {@link com.xyratex.label.output.XyLabelBitmapProducer} for printing</p>
 * 
 * <p>This stage is for deployment on the server part</p>
 * 
 * <p>See also the batik javadoc documentation: <a href="http://xmlgraphics.apache.org/batik/javadoc/">http://xmlgraphics.apache.org/batik/javadoc/</a></p>
 *
 * @see com.xyratex.label.population.XyLabelPopulator XySVGLabelPopulator
 * @see com.xyratex.label.output.XyLabelBitmapProducer XyLabelBitmapProducer
 *  
 * @author Rob Davis
 */
final public class XyLabelTemplateProducer 
{
	/**
	 *  %R% = Release %L% = Level %G% = date %U% = time
	 */
	public static final String sccsid = "@(#)Xyratex  ISTP  XyLabelTemplateProducer.java  %R%.%L%, %G% %U%";
	
	/**
	 * <p>This tag is the id of a rectangle that specifies the outline edges of the label.
	 * The rectangle dimensions and position are derived from the outline rectangle as
	 * defined in the original label drawing.
	 * </p>
	 * 
	 * <p>The XyLabelTemplateProducer replaces the original outline rectangle of the original
	 * drawing with a new rectangle with id XYPRINTABLE_REGION for two reasons:
	 * </p>
	 * <ol>
	 *  <li>The position and dimensions of the original outline rectangle may be dependent
	 *  on a transform. The XYPRINTABLE_REGION rectangle position and
	 *  co-ordinates will be calculated from this to contain absolute values. 
	 *  This means that rasterizing and printing code is kept simple as simple as possible
	 *  in that it will therefore not require the certain batik facilities that handle
	 *  the transform and performance overhead will be minimised.</li>
	 *  
	 *  <li>As a new element, who's parent is the root svg element, the XYPRINTABLE_REGION rectangle
	 *  is not subject to any styling that might be applied if it was a child of a non-root element, 
	 *  so total control over the XYPRINTABLE_REGION rectangle appearance is possible - this is
	 *  require because we actually want it to be invisible in the template and when printed.
	 *  It serves to inform rasterizing, printing and other programs that use the template to
	 *  tell them the size of the label, and position.
	 *  </li>
	 * </ol>
	 * 
	 * 
	 * <p>The XyLabelTemplateProducer should know about this tag since its job is to 
	 * produce a template from a lable drawing.</p>
	 */
	public static final String XYPRINTABLE_REGION = "XYPRINTABLE_REGION";
	
	/**
	 * <p>Just provides the outlines of multi-section labels - labels that have
	 * precut parts than can eahc be peeled off separately and applied to a different
	 * place on the product.</p>
	 * 
	 * <p>The XyLabelTemplateProducer replaces occurences of XYLABEL_SUBOUTLINEs
	 * with XYPRINTABLE_SUBREGIONs using the same method for XYPRINTABLE_REGION.
	 * At the current time,  XYPRINTABLE_SUBREGIONs serve no active purpose as
	 * the positioning of the symbols, text etc in the
	 * entire label image should determine that the graphics get printed
	 * correctly on the physical cut out sections. However it is worthwhile
	 * retaining the suboutline information in case it becomes useful in the
	 * future.</p>
	 */
	public static final String XYPRINTABLE_SUBREGION = "XYPRINTABLE_SUBREGION";
	
	/**
	 * log object used for logging activity in the form of String/character messages for debug, diagnostic and analysis purposes.
	 * The output destination for the log could be console output and/or file(s) depending on the user-definable overall configuration
	 * of logs in the system.
	 */
	private static Log log = LogFactory.getLog(XyLabelTemplateProducer.class);
	
  /**
   *  <p>Generates a label template from a tagged SVG drawing supplied by the industrial designers</p>
   * 
	 * @param origLabelDrawingInputFilename (input parameter) the input file - a tagged SVG drawing supplied by the industrial designers
	 * @param labelTemplateOutputFilename (input parameter) the output file - the resultant label template file
	 * 
	 * <p>Assumes input template label has been created from a SVG template created from a SVG drawing using {@link XyLabelTemplateProducer}
   * </p>
   * 
	 */
	public static void generateLabelTemplate(
	    String origLabelDrawingInputFilename, String labelTemplateOutputFilename)
  throws IOException,
         JaxenException,
         InstantiationException,
         IllegalAccessException,
         ClassNotFoundException
	{
		Document doc = convertDrawingToLabel(new File(
		    origLabelDrawingInputFilename));

		String svgAsString = XyXMLDOMDocumentToStringService.outputAsString(doc);

		FileUtils.writeStringToFile(  new File (labelTemplateOutputFilename), svgAsString, XyCharacterEncoding.ENCODING );
	}

 

  /**
   * <p>Converts a tagged label drawing into a label template. The primary purpose of XySVGLabelTemplateProducer</p>
   * 
   * <p>A tagged label drawing is created by the industrial designer, who also supplies tags. Each tag
   * marks an element of the drawing. A tag will specify the label outline, the rectangular placeholder of a barcode
   * or a text field for the human readable version of a barcode.</p>
   * 
   * <p>The tags are necessary for this function to work. Otherwise the template cannot be produced.</p>
   * 
   * <p>All of the drawing elements, shapes, text outside of the label (as defined by the LABEL_OUTLINE tag)
   * are removed. Any text or otherwise withing the barcode placeholder elements is removed.</p>
   * 
   * <p>SVG non-compliances are also handled - as the drawing program currently in use by 
   * the industrial designers is unpatched Corel Draw Graphics Suite 12, which exports SVG with
   * negative (i.e. illegal) font sizes defined in the document CSS.</p> 
   * 
   * @param file (input parameter) the input tagged label SVG drawing from the industrial designer
   * 
   * @return Document  the the label template as a W3C Document object
	 */
	public static Document convertDrawingToLabel( final File file ) 
	  throws IOException,
	         JaxenException
	{
		log.trace(sccsid);
		
		// parse in the SVG document as a file, using the batik SVG/XML parser
		final SAXSVGDocumentFactory docFactory = new SAXSVGDocumentFactory(XMLResourceDescriptor.getXMLParserClassName());
		final SVGDocument svgdoc = docFactory.createSVGDocument(file.toURI().toString());

		// gain access to the graphical shape and co-ordinate space view of the SVG document,
		// by instantiating the batik SVG DOM and CSS libraries
		final XyBatikWrapper batik = new XyBatikWrapper( svgdoc  );
		
		// find elements that Corel has erroneously CSS styled as having negative font size
		// - these elements need to be marked for removed, and they can be without affecting the resulting label template
		final XyCorelExportSVGCSSNegativeFontSize xyCSSDeviationNegativeFontSize = new XyCorelExportSVGCSSNegativeFontSize( svgdoc,  batik.getBridgeContext(), batik.getRootGraphicsNode() );		
		final Vector<Element> affectedNegativeFontSizeElements = xyCSSDeviationNegativeFontSize.getElements();
		
		// get the outline of the label
		final String labelOutline = XyLabelTagRegistry.getLabelOutline();
		final String anyElement = "*"; 
		final Element labelOutlineElement = XyXMLDOMSearchUtils.getElementByPartialAttributeValueMatch( labelOutline, "id", anyElement, svgdoc );
		
		// when we create the label template from the label drawing, we need to explicitly specify
		// that we want to keep the label outline and sub-outlines, because the clip removal system, when told to removed
		// everything inside the label outline co-ordinates, it will by default remove the label outline and sub-outlines as well!
		//
		// TODO: could get everything that starts with XY to be more generic, aids future maintenance
		// - no need for specifics
		//
		Vector drawingPreIncludedElements = new Vector();
		final String subOutlinePrefix = XyLabelTagRegistry.getLabelSubOutlinePrefix();
		final List subOutlineElementList = XyXMLDOMSearchUtils.getElementsByPartialAttributeValueMatch( subOutlinePrefix, "id", anyElement, svgdoc );
		
		drawingPreIncludedElements.add( labelOutlineElement );
		drawingPreIncludedElements.addAll( subOutlineElementList );
		
    NodeList elementsWithSymbolTagAsNodeList = svgdoc.getElementsByTagName("symbol");
    
    for ( int i = 1; i < elementsWithSymbolTagAsNodeList.getLength(); i++ )
    {
      drawingPreIncludedElements.add( elementsWithSymbolTagAsNodeList.item(i) );
    }
    
    // look for a printed background - i.e. a background that must be printed - e.g. black - for white text on black
    final Element printedBackgroundElement = svgdoc.getElementById( XyLabelTagRegistry.getLabelBackgroundPrefix() );
		if ( printedBackgroundElement != null )
		{
		  drawingPreIncludedElements.add( printedBackgroundElement );
		}

		// remove all label drawing elements outside of the label outline
		XySVGClipper.clip(svgdoc, labelOutline, affectedNegativeFontSizeElements, drawingPreIncludedElements, XySVGClipper.CLIPPER_REMOVE_OUTSIDE, batik.getBridgeContext() );
		
		// remove the innards of the barcode rectangles - e.g. things like "barcode to go here"
		// BUT keep the placeholder shape itself so that the barcode populator will know 
		// where to populate the barcode
		final Vector<String> tagsInDrawing = XyLabelTagRegistry.findTagsInDrawing(svgdoc);
		for (int i = 0; i < tagsInDrawing.size(); i++ )
		{
			String aTagInDrawing = (String)tagsInDrawing.get(i);
			
			if ( XyLabelTagRegistry.tagIsForBarcode(aTagInDrawing) )
			{
				Vector<Element> barcodePlaceholderPreIncluded = new Vector<Element>();
				
				Element taggedElement = svgdoc.getElementById((String)tagsInDrawing.get(i));
				
				// be tolerant about how the drawing office have created outlines for barcodes
				if ( taggedElement.getTagName() != "rect" )
				{
				  convertShapeElementToRectElement( svgdoc, taggedElement, batik );
				}
				
				barcodePlaceholderPreIncluded.add(taggedElement);
						
				final Vector<Element> noPreExcluded = null;
				XySVGClipper.clip( svgdoc,
						               aTagInDrawing,
						               noPreExcluded,
						               barcodePlaceholderPreIncluded,
						               XySVGClipper.CLIPPER_REMOVE_INSIDE,
						               batik.getBridgeContext()
						             );
			}	
		}
		
		SVGDocument svgDocWithOrientation = null; 
		
		String labelOutlineElementId = labelOutlineElement.getAttribute("id");
		
		String orientationSuffix = null;
		
		if ( labelOutlineElementId.contains(XyLabelTagRegistry.ORIENTATIONVERTICALTOP ) )
		{
			orientationSuffix = XyLabelTagRegistry.ORIENTATIONVERTICALTOP;
		}
		else
		{
			if ( labelOutlineElementId.contains(XyLabelTagRegistry.ORIENTATIONVERTICALBOTTOM ) )
		  {
			  orientationSuffix = XyLabelTagRegistry.ORIENTATIONVERTICALBOTTOM;
		  }
		}
		
		if ( orientationSuffix != null )
		{
		  svgDocWithOrientation = installOrientation( svgdoc, batik, labelOutlineElement, orientationSuffix );
		}
		
		SVGDocument processedSvgDoc = null;
		
		if ( svgDocWithOrientation != null )
		{
			processedSvgDoc = svgDocWithOrientation;
		}
		else
		{
			processedSvgDoc = svgdoc;
		}
		
		// define the overall printable region for the label
		definePrintableRegion( processedSvgDoc, XYPRINTABLE_REGION, labelOutlineElement, batik );
	  
		// define the printable regions for the sub outlines
		// - not currently used but worthwhile retaining the knowledge should
		// we require it later


		for (int j = 0; j < subOutlineElementList.size(); j++ )
		{
			Element subOutlineElement = (Element)subOutlineElementList.get(j);
			
			String subOutlineElementName = subOutlineElement.getAttribute("id");
			
			// make the XYPRINTABLE_SUBREGION unique by using the suffix following
			// the label outline sub element attribute id.
			// for example if the label sub outline was XYLABEL_SUBOUTLINE_1
			// then this would be replaced with a XYPRINTABLE_SUBREGION_1 etc.
			String subPrintableSuffix = subOutlineElementName.replace(subOutlinePrefix, "");
			
			definePrintableRegion( 
			  processedSvgDoc,
				XYPRINTABLE_SUBREGION + subPrintableSuffix,
				subOutlineElement,
				batik );
		}
		
		/*
		
	  // this crops the template to the outline
	  // it works but disabled for now because this tight cropping might hinder efforts to adjust label size - if required
		 
		Element printableRegion = processedSvgDoc.getElementById(XYPRINTABLE_REGION);
		
 		String x = printableRegion.getAttribute("x");
    String y = printableRegion.getAttribute("y");
    String width = printableRegion.getAttribute("width");
    String height = printableRegion.getAttribute("height");
		
		String viewBox = x + " " + y + " " +width + " " + height;
    		
    Element svgElement = processedSvgDoc.getDocumentElement();
    		
    svgElement.setAttribute("viewBox", viewBox );
    svgElement.setAttribute("width", width);
    svgElement.setAttribute("height", height);
    */

		return processedSvgDoc;
	}
	
	private static void definePrintableRegion( Document svgdoc, final String tagIdForRegion, final Element labelOutlineElement, XyBatikWrapper batik )
	{
	  // define printable region - this is so that the label production components can
	  // rasterize the svg to a bitmap without needing to do much computing (e.g using batik to calculate transforms)
			
		final Shape clipOutline =  XyShapeProducer.createShape(labelOutlineElement, batik.getBridgeContext());
		
	  final Rectangle2D viewportRectangle = clipOutline.getBounds2D();
	  
	  Element svgElement = svgdoc.getDocumentElement();
	  
	  Element printableRegionRectangle = svgdoc.createElement("rect");

	  printableRegionRectangle.setAttribute("id", tagIdForRegion);
	  printableRegionRectangle.setAttribute("style", "fill:none;stroke:none"); // invisible
	 
	  printableRegionRectangle.setAttribute("x", Double.toString(viewportRectangle.getX()) );
	  printableRegionRectangle.setAttribute("y", Double.toString(viewportRectangle.getY()) );
	  printableRegionRectangle.setAttribute("width", Double.toString(viewportRectangle.getWidth()) );
	  printableRegionRectangle.setAttribute("height", Double.toString(viewportRectangle.getHeight()) );
	  
	  // insert the group tag just before the original placeholder rectangle
	  svgElement.appendChild(printableRegionRectangle);

	  // remove the original label outline because this is now not needed
	  // also the fact that it may be coloured and we only want full black or full white monochrome
	  // in our label printing
	  Node labelOutlineParentElement = labelOutlineElement.getParentNode();
	  labelOutlineParentElement.removeChild(labelOutlineElement);
	}
	
	private static void convertShapeElementToRectElement( Document svgDocContainingShapeElement,
                                                        Element shapeElement,
	                                                      XyBatikWrapper batik )
	{
	  String tagId = shapeElement.getAttribute("id");
	  
    final Shape clipOutline =  XyShapeProducer.createShape(shapeElement, batik.getBridgeContext());
    
    final Rectangle2D rectangle = clipOutline.getBounds2D();
    
    Element svgElement = svgDocContainingShapeElement.getDocumentElement();
    
    // get the namespace of the label drawing
    String ns = svgDocContainingShapeElement.getDocumentElement().getNamespaceURI();
    
    
    //Element convertedElementAsRectangleElement = svgDocContainingShapeElement.createElement("rect");
    Element convertedElementAsRectangleElement = svgDocContainingShapeElement.createElementNS( ns, "rect");
     
    
    convertedElementAsRectangleElement.setAttribute("id", tagId);
    convertedElementAsRectangleElement.setAttribute("style", "fill:none;stroke:none"); // invisible
   
    convertedElementAsRectangleElement.setAttribute("x", Double.toString(rectangle.getX()) );
    convertedElementAsRectangleElement.setAttribute("y", Double.toString(rectangle.getY()) );
    convertedElementAsRectangleElement.setAttribute("width", Double.toString(rectangle.getWidth()) );
    convertedElementAsRectangleElement.setAttribute("height", Double.toString(rectangle.getHeight()) );
    
    // insert the group tag just before the original placeholder rectangle
    svgElement.appendChild(convertedElementAsRectangleElement);

    // remove the original label outline because this is now not needed
    // also the fact that it may be coloured and we only want full black or full white monochrome
    // in our label printing
    Node labelOutlineParentElement = shapeElement.getParentNode();
    labelOutlineParentElement.removeChild(shapeElement);
	}
	
	

	
	
	private static SVGDocument installOrientation( SVGDocument svgdoc, XyBatikWrapper batik, Element labelOutlineElement, String orientationSuffix )
	{
		String labelOutlineElementId = labelOutlineElement.getAttribute("id");

		SVGDocument containerdoc = null;

		if (labelOutlineElementId.contains(XyLabelTagRegistry.ORIENTATIONVERTICAL))
		{
		  // get the namespace of the label drawing
			String ns = svgdoc.getDocumentElement().getNamespaceURI();

			DOMImplementation impl = SVGDOMImplementation.getDOMImplementation();

			// create a container using the g element to contain the label so that
			// the
			// transform can be applied

			containerdoc = (SVGDocument) impl.createDocument(ns, "svg", null);

			// Get the root element (the 'svg' element) of the label drawing
			Element containerdocSvgRoot = containerdoc.getDocumentElement();

			Element gContainerNode = containerdoc.createElementNS(ns, "g");

			// gContainerNode.setAttribute("transform", transform);

			// log.trace("gContainerNode transform " +
			// gContainerNode.getAttribute("transform"));

			containerdocSvgRoot.appendChild(gContainerNode);

			gContainerNode.setAttribute("id", XyLabelTagRegistry.XY_TRANSFORM);

			// final String noTransform = null; // at this stage
			String css = null;

			// Get the orientation expressed as a SVG transform to apply to the
			// label
			String transform = XyLabelTagRegistry.getTransformForOrientation(orientationSuffix);

			XySVGInsert.insert(containerdoc, svgdoc, transform, gContainerNode, css);

			Element postTransformedPrintableRegionInContainerDoc = containerdoc.createElement("rect");
			// Element postTransformedPrintableRegionInContainerDoc =
			// containerdoc.getElementById(postTransformedPrintableRegionId);

			// was here
			String postTransformedPrintableRegionId = "XY_POSTTRANSFORMEDPRINTABLEREGION_" + orientationSuffix;
		  postTransformedPrintableRegionInContainerDoc.setAttribute("id", postTransformedPrintableRegionId);

		  Element containerSvgElement = containerdoc.getDocumentElement();

			containerSvgElement.appendChild(postTransformedPrintableRegionInContainerDoc);

      Element labelOutlineElementInContainerDoc = containerdoc.getElementById(labelOutlineElementId);

      Element gelement = gContainerNode;
      // pointless duplication?
			//Element gelement = containerdoc.getElementById(XyLabelTagRegistry.XY_TRANSFORM);

			log.trace("transform " + gelement.getAttributeNS(ns, "transform"));

			AffineTransform at = XyCombinedTransformProducer.getCurrentTransform(labelOutlineElementInContainerDoc);

			final Shape clipOutline = XyShapeProducer.createShape( labelOutlineElement, batik.getBridgeContext());
			final Rectangle2D viewportRectangle = clipOutline.getBounds2D();
			Shape transformedOutlineShape = XyShapeProducer.createTransformedShape( viewportRectangle, at);

			Rectangle2D transformedOutlineRectangle = transformedOutlineShape.getBounds2D();

			postTransformedPrintableRegionInContainerDoc.setAttribute("style", "fill:none;stroke:none"); // invisible

			postTransformedPrintableRegionInContainerDoc.setAttribute("x", Double.toString(transformedOutlineRectangle.getX()));
			postTransformedPrintableRegionInContainerDoc.setAttribute("y", Double.toString(transformedOutlineRectangle.getY()));
			postTransformedPrintableRegionInContainerDoc.setAttribute("width", Double.toString(transformedOutlineRectangle.getWidth()));
			postTransformedPrintableRegionInContainerDoc.setAttribute("height", Double.toString(transformedOutlineRectangle.getHeight()));

			gelement.removeAttribute("transform");
		}
		
		return containerdoc;
	}
}