//-------------------------------------------------------------------------
// (C) COPYRIGHT Xyratex Storage Systems Division 2011
// All Rights Reserved
//
// Filename    : XyLabelPopulator.java
// Author      : Rob Davis


// By          : Rob Davis
// File Type   : Java source code for Open Label Print
//-------------------------------------------------------------------------

package com.xyratex.label.population;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jaxen.JaxenException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.xyratex.label.barcode.XyBarcodeGeneratorFactory;
import com.xyratex.label.barcode.XyBarcodeGenerator;
import com.xyratex.label.population.exception.XyExceptionUnknownWidthScalingModeForBarcode;
import com.xyratex.label.tags.XyLabelTagRegistry;
import com.xyratex.svg.utils.XySVGInsert;
import com.xyratex.transaction.XyTransaction;
import com.xyratex.transaction.XyTransactionMember;
import com.xyratex.xml.utils.XyXMLDOMSearchUtils;

/**
 * <p>Populates a label with a set of variable field data, e.g. barcodes and human readable fields.</p>
 * 
 * <p>Controls the size of barcodes according to a mode selected by the client calling code.</p>
 * 
 * <p>Takes as input:</p>
 * <ol>
 *   <li>The label template SVG XML - 
 *     defines the size and layout of the label, with static symbols etc already defined. 
 *     Defines the position, size and type of the variable fields - the barcode and human readable fields.
 *   </li>
 *   
 *   <li>The label populator XML -
 *     defines the data to populate the label template for a given completed, label ready for printing.
 *   </li>
 *  </ol> 
 * 
 *  <p>Future functionality to consider:</p>
 *  <ul>
 *   <li>TODO: need a tolerance for barcodes that only slightly exceed their placeholders?</li>
 *   <li>TODO: this may become part of a facade/transaction design pattern so that the label
 *     creation and checking for validity logic is independent of user interface
 *   </li>
 *  </ul>
 */
final public class XyLabelPopulator
{
	/**
	 *  <p>R = Release L = Level G = date U = time</p>
	 *  <p>Values calculated by SCCS when file is checked out and compiled.</p>
	 */
	public static final String sccsid = "@(#)Xyratex  ISTP  XyLabelPopulator.java  %R%.%L%, %G% %U%";
	
	/**
	 * log object used for logging activity in the form of String/character messages for debug, diagnostic and analysis purposes.
	 * The output destination for the log could be console output and/or file(s) depending on the user-definable overall configuration
	 * of logs in the system.
	 */
	private static Log log = LogFactory.getLog(XyLabelPopulator.class);
	
	/**
	 * Used to create the barcode generator for each barcode on the label.
	 */
	private static XyBarcodeGeneratorFactory barcodeGeneratorFactory = new XyBarcodeGeneratorFactory();
	
	/**
	 * <p>Populates label, with barcode size determined by size of thinnest line as specified.</p>
	 * 
	 * @param labelTemplateDocToBePopulated (input/output) label template as W3C XML DOM Document that will be populated
	 * @param inputPopulatorDoc (input) populator as as W3C XML DOM Document
	 * @param moduleWidth (input) width of thinnest line in barcode as String, most flexible format for storing any precision
	 * @param dotsPerUnitLength (input) dots per unit of measurement (specified in value) as String, most flexible format for storing any precision
	 */
	public static void populateEntireLabelToModuleWidth(
			Document labelTemplateDocToBePopulated,
			final Document inputPopulatorDoc, 
			final String moduleWidth,
			final String dotsPerUnitLength
		  ) throws Exception
	{		  
	  int widthMode = XyBarcodeGenerator.WIDTH_USE_MODULE_WIDTH;
	   
	 	populateEntireLabel(
				 labelTemplateDocToBePopulated,
				 inputPopulatorDoc, 
				 widthMode, 
				 moduleWidth, dotsPerUnitLength );
	}
	
	/**
	 * <p>Populates label, with barcode size determined by each placeholder.</p>
	 * 
	 * @param labelTemplateDocToBePopulated (input/output) label template as W3C XML DOM Document that will be populated
	 * @param inputPopulatorDoc (input) populator as as W3C XML DOM Document
	 * @param dotsPerUnitLength (input) dots per unit of measurement (specified in value) as String, most flexible format for storing any precision
	 */
	public static void populateEntireLabelFitToPlaceholder(
			Document labelTemplateDocToBePopulated,
			final Document inputPopulatorDoc, 
			final String dotsPerUnitLength 
		  ) throws Exception // deliberately generic to allow implementations to use any existing Exception or define their own
	{
	  int widthMode = XyBarcodeGenerator.WIDTH_SCALEFACTOR_PLACEHOLDER;
		
	  String moduleWidth = ""; // we don't use this for fitting to a placeholder
	  
	 	populateEntireLabel(
				 labelTemplateDocToBePopulated,
				 inputPopulatorDoc, 
				 widthMode, 
				 moduleWidth, dotsPerUnitLength );
	}
	
	/**
	 * <p>Called when a set of barcodes is to be populated, 
	 * with the lengths being relative and in proportion 
	 * to the longest that is scaled to fit its placeholder.</p>
	 * 
	 * <p>This method follows the sequence of population:</p>
	 * <ol>
	 *  <li>All the barcodes will be rendered at the same module width</li>
	 *  <li>The longest barcode will be scaled to fit the size of its placeholder</li>
	 *  <li>All the other barcodes will be scaled by the same value, so
	 *  that the module width is the same for all barcodes on the label.
	 *  This gives aesthetic benefits.</li>
	 * </ol>
	 * 
	 * <p>Limitations to consider (TODO):</p>
	 * <ol>
	 *  <li>If the placeholder for the longest barcode is
	 * smaller than the other barcodes, then, due to the relative scaling, they
	 * will not benefit from the extra space they have.</li>
	 * <li>If the any of the placeholders of the other barcodes is smaller than
	 * the place holder for the longest barcode, then there is a possibility that
	 * the shorter barcodes may exceed their placeholder length when scaled in
	 * proportion to the longest barcode, particularly if they are near to the
	 * length of the longest barcode but their placeholder is much smaller.</li>
	 * <li>The assumption is that all placeholders for barcodes are the same size
	 * on the label.</li>
	 * </ol>
	 *   
	 * @param labelTemplateDocToBePopulated (input/output) parameter - the label template to be populated with the variable fields (barcodes, text etc.)
	 * @param inputPopulatorDoc (input) the xml containing the variable data to be populated
	 * @param dotsPerUnitLength (input) the dots per unit of measurement
	 */
	public static void populateEntireLabelToLongestWidth(
			Document labelTemplateDocToBePopulated,
			final Document inputPopulatorDoc, 
			final String dotsPerUnitLength 
		  ) throws Exception // deliberately generic to allow implementations to use any existing Exception or define their own
	{
		int widthMode = XyBarcodeGenerator.WIDTH_SCALEFACTOR_RELATIVE_TO_LONGEST;
		
	  String moduleWidth = "";
	  
	 	populateEntireLabel(
				 labelTemplateDocToBePopulated,
				 inputPopulatorDoc, 
				 widthMode, 
				 moduleWidth,
				 dotsPerUnitLength);
	}
	
	/**
	 * <p>The implementation of the label population.</p>
	 * 
	 * <p>look at each field element, obtained from the populator xml:</p>
	 * <ol>
	 * <li>get the value for the field to be populated on the label</li>
	 * <li>determine if there is more than one place for the value to go and in
	 *    what form e.g. barcode or human readable (by having a value defined once
	 *    in the populator xml for all the places it is used prevents duplication
	 *    mistakes)
	 * </li>
	 * <li>
	 * if there is a barcode to be used for the field then see if the
	 *    populator defines the symbology or if the label template defines it 
	 *    (the proper scenario)
	 *    Get the symbology and create the appropriate barcode generator
	 *    to generate the barcode, store the generator in a collection.
	 *    Keep track of the longest barcode
	 * </li>
	 * </ol>
	 *  
	 * <p>After all the fields in the populator xml have been looked at...</p>
	 * <ol>
	 * <li>Recall the collection of the barcode generators.</li>
	 * <li>If the horizontal width mode is relative to the longest barcode,
	 *    then set the scale factor for each one relative to the longest
	 *    barcode, using the longest barcode tracked earlier.</li>
	 * <li>In any case, populate the template with barcodes from the generators.</li>
	 * </ol>
	 * 
	 * @param labelTemplateDocToBePopulated (input/output)
	 * @param inputPopulatorDoc (input) contains the variable data that the label will be populated
	 * @param widthMode (input) determines how the barcode horizontal width is set
	 * @param moduleWidth (input) depending on the widthMode, this parameter will contain 
	 *   a value for the module width - the thinnest part of the barcode upon which 
	 *   all other bands/stripes are based on as multiples
	 * @param dotsPerUnitLength (input) the resolution measured in dots per unit of measurement
	 */
	private static void populateEntireLabel(
			Document labelTemplateDocToBePopulated,
			final Document inputPopulatorDoc, 
			final int widthMode, 
			final String moduleWidth,
			final String dotsPerUnitLength
		  ) throws Exception // deliberately generic to allow implementations to use any existing Exception or define their own
	{
		log.trace(sccsid); // output version of class
		
		/*
		final String gElementStr = "g"; 
		final Element xyTransformGElement = XyXMLDOMSearchUtils.getElementByPartialAttributeValueMatch( XyLabelTagRegistry.XY_TRANSFORM, "id", gElementStr, labelTemplateDocToBePopulated );
		if ( xyTransformGElement != null )
		{
			String transform = xyTransformGElement.getAttribute("transform");
			log.trace("transform: " + transform + "temporarily turned off");
			xyTransformGElement.removeAttribute("transform");
		}
		*/
		
		// get <field> tags from populator xml
		NodeList populatorFieldElements = inputPopulatorDoc.getElementsByTagName("field");		
		
		String populatorFieldValue = null;
		
		// to hold the barcode objects and is used for working out the horizontal
		// width of the barcodes relative to the longest one in the label for aesthetic purposes
		XyTransaction transaction = new XyTransaction();
		
		XyBarcodeGenerator currentLongestBarcode = null;
		
		for ( int i = 0; i < populatorFieldElements.getLength(); i++ )
		{
			Node populatorNode = populatorFieldElements.item(i);
			
      // check that field node is an element (probably not necessary, given that this should
			// just be a collection of field elements)
			if ( populatorNode instanceof Element )
			{
				Element populatorElement = (Element) populatorNode;

        log.trace( populatorElement.getAttribute("description") );
				
				// get the value of the value attribute, e.g. a serial number DHSS50A12345678
				populatorFieldValue = populatorElement.getAttribute("value");

				// the field value should have at least one child node
				// that determines the field type, e.g. a barcode
				// - so that the field value is expressed as a barcode
				// but there can be one or more, e.g. another child node
				// could be a human readable, so the value also gets
				// expressed as a human readable value
				if (populatorNode.hasChildNodes())
				{
					NodeList populatorFieldTypes = populatorElement.getChildNodes();

					log.trace( "number of children: " + populatorFieldTypes.getLength() );
					
					// go through the populator XML field types
					for (int j = 0; j < populatorFieldTypes.getLength(); j++)
					{
						log.trace("j=" + j);
						
						Node aFieldType = populatorFieldTypes.item(j);

						// check that the field type is an element
						// because we want to ignore other elements, e.g. comments
						if (aFieldType instanceof Element)
						{
							Element populatorFieldTypeElement = (Element) aFieldType;

							// get the tag id for the field
							// this id corresponds to the tagged place holder in the label
							// template doc
							String populatorPlaceHolderInLabelTemplateId = populatorFieldTypeElement
							    .getAttribute("id");
							
							
							log.trace( "populator xml:" + populatorPlaceHolderInLabelTemplateId);

						  if (populatorFieldTypeElement.getTagName().equals("barcode"))
							{
								// ...get the element in the label template doc that has this tag
								// id
						    /*
						     *  // disabled for now as returned multiple elements
								Element placeholderElementInTemplate = XyXMLDOMSearchUtils
							   .getElementByPartialAttributeValueMatch(
							      populatorPlaceHolderInLabelTemplateId, "id", "rect",
							      labelTemplateDocToBePopulated);
								*/
                Element placeholderElementInTemplate = labelTemplateDocToBePopulated.getElementById( populatorPlaceHolderInLabelTemplateId );
						    
							  if ( placeholderElementInTemplate != null )
								{
							    String fieldTypeId = populatorFieldTypeElement.getAttribute("id");
							    log.trace( "fieldTypeId - " + fieldTypeId );
							    
							    String placeholderElementInTemplateId = placeholderElementInTemplate.getAttribute("id");
							    log.trace( "placeholderElementInTemplateId - " + placeholderElementInTemplateId );
							    
								  XyBarcodeGenerator newCurrentLongestBarcode = addBarcodeToPopulatorList(
									    currentLongestBarcode, populatorFieldTypeElement,
									    populatorFieldValue, placeholderElementInTemplate,
									    labelTemplateDocToBePopulated, transaction, widthMode);

								  currentLongestBarcode = newCurrentLongestBarcode;
								}
								else
								{
									// this scenario is OK whereby the client code supplies all of
									// the possible populator XML values but the template only consumes the ones
									// it needs. We log the fact though, just in case we need to know this is
									// happening.
									log.trace("Advice: Barcode Field from Populator XML not used in template:"
								    + populatorPlaceHolderInLabelTemplateId);
								}
							}
							else // if not a barcode then perhaps it is a human readable field
							{
							  if (populatorFieldTypeElement.getTagName().equals("human-readable"))
								{

							  	Element textPlaceholderElementInTemplate = labelTemplateDocToBePopulated.getElementById(populatorPlaceHolderInLabelTemplateId);
							  	
							  	/*
							  	
							  	// we original did a partial match but actually at the moment we don't require
							  	// to be able to partial match text elements
							  	 
							    Element textPlaceholderElementInTemplate = XyXMLDOMSearchUtils
								    .getElementByPartialAttributeValueMatch(
								        populatorPlaceHolderInLabelTemplateId, "id", "text",
								        labelTemplateDocToBePopulated);
									*/
										
									if ( textPlaceholderElementInTemplate != null )
									{
										String labelElementTag = textPlaceholderElementInTemplate.getAttribute("id");
										
										if ( XyLabelTagRegistry.labelTemplateTagHasATruncationMode( labelElementTag ) )
										{
											textPlaceholderElementInTemplate.setTextContent( getTruncatedValue( labelTemplateDocToBePopulated, labelElementTag, populatorFieldValue ) );
										}
										else
										{
										  textPlaceholderElementInTemplate.setTextContent(populatorFieldValue);
										}
									}
									else
									{
										// this scenario is OK whereby the client code supplies all of
										// the possible populator XML values but the template only consumes the ones
										// it needs. We log the fact though, just in case we need to know this is
										// happening.
									  log.trace("Advice: Text Field from Populator XML not used in template:"
									    + populatorPlaceHolderInLabelTemplateId);
									}
								} // end check for human readable
							} // end check for barcode
						} // end check that child node within a field element is an element
					} // end for loop to iterate through children of field - i.e. the  field types - barcode, human readable field etc.
				} // end check field has children (e.g. barcode)
			} // end check that node in list of nodes of a populator xml file is a field element
		} // end for loop iteration of node in list of nodes of a populator xml file		

		insertBarcodesIntoTemplate( 
			transaction,
			currentLongestBarcode,
			labelTemplateDocToBePopulated,
			widthMode,
			moduleWidth,
			dotsPerUnitLength 	
		);
		
		addDate( labelTemplateDocToBePopulated );
		
		/*
		// turn transform back on
    if ( xyTransformGElement != null )
    {
    	// find the printable region to determine the orientation mode
  		final Element printableRegion = XyXMLDOMSearchUtils.getElementByPartialAttributeValueMatch( XyLabelTemplateProducer.XYPRINTABLE_REGION, "id", "rect", labelTemplateDocToBePopulated );
  		if ( printableRegion != null )
  		{
  			String transform = XyLabelTagRegistry.getTransformForOrientation( printableRegion.getAttribute("id") );
  		
  		  if ( transform != null )
  		  {
  		  	xyTransformGElement.setAttribute("transform", transform );
  		  	log.trace("transform restored:" + transform );
  		  }
  		}
    }
    */
	} // end method
	
	private static void insertBarcodesIntoTemplate( 
		XyTransaction populatorList,
    XyBarcodeGenerator currentLongestBarcode,
		Document labelTemplateDocToBePopulated,
		int widthMode,
		String moduleWidth,
		String dotsPerUnitLength ) throws Exception
	{
	   for (int i = 0; i < populatorList.numberOfTransactionMembers(); i++)
	    {
	      XyTransactionMember xtm = populatorList.getTransactionMember(i);

	      if (xtm instanceof XyBarcodeGenerator)
	      {
	        XyBarcodeGenerator bw = (XyBarcodeGenerator) xtm;
	        
	        String placeholderElementId = bw.getPlaceholderElement().getAttribute("id");
	        log.trace( "placeholderElementId " + placeholderElementId );
	      }
	    }
	  
		for (int i = 0; i < populatorList.numberOfTransactionMembers(); i++)
		{
			XyTransactionMember xtm = populatorList.getTransactionMember(i);

			if (xtm instanceof XyBarcodeGenerator)
			{
				XyBarcodeGenerator bw = (XyBarcodeGenerator) xtm;
				
				String placeholderElementId = bw.getPlaceholderElement().getAttribute("id");
				log.trace( "placeholderElementId " + placeholderElementId );

		    // now determine what barcode horizontal width that
			  // the client code wanted
			  switch ( widthMode )
			  {
				  // every barcode gets scaled (stretched or shrunk)
				  // to fit its placeholder, independent of others
				  case XyBarcodeGenerator.WIDTH_SCALEFACTOR_PLACEHOLDER:
				  {
				  	bw.setScaleFactorUsingPlaceholderWidth(Double.valueOf(
				  			getPlaceholderWidth(currentLongestBarcode.getPlaceholderElement())).doubleValue());
				  }
				  break;
				
				  // the longest barcode and its placeholder length in the label
				  // template is kept track of
				  // and all the other barcodes are scaled proportionately
				  // so that the module widths are all the same
				  case XyBarcodeGenerator.WIDTH_SCALEFACTOR_RELATIVE_TO_LONGEST:
				  {
					  double scaleFactor = bw.getBarcodeRawWidth() / currentLongestBarcode.getBarcodeRawWidth();

					  bw.setScaleFactorUsingPlaceholderWidth( Double.valueOf(getPlaceholderWidth(currentLongestBarcode.getPlaceholderElement())).doubleValue()  * scaleFactor);
				  }
				  break;
				
			    // the client code has define a module length
			    // for all the barcodes to be based on
			    // all other bars/stripes in a barcode are based on
			    // the size of this module
			    case XyBarcodeGenerator.WIDTH_USE_MODULE_WIDTH:
			    {
			    	bw.setScaleFactorUsingModuleWidth(moduleWidth, dotsPerUnitLength );
		  	  }
			    break;
			
			    default:
			    {
				    throw new XyExceptionUnknownWidthScalingModeForBarcode( "Unknown barcode width scaling mode:" + widthMode );
			    }
			  }

			  double xScaleFactor = 0.0F;
			  double yScaleFactor = 0.0F;
			  
		    // TODO: should work out orientation
			  boolean reOrientated = false;
		    final String anyElement = "*"; 
		    final Element labelOrientationElement = XyXMLDOMSearchUtils.getElementByPartialAttributeValueMatch( "PRINTABLEREGION", "id", anyElement, labelTemplateDocToBePopulated );
		    if ( labelOrientationElement != null )
		    {
		      if ( labelOrientationElement.getAttribute("id").contains(XyLabelTagRegistry.ORIENTATIONVERTICAL) )
		      {
		        reOrientated = true;
		      }
		    }

				if ( placeholderElementId.contains(XyLabelTagRegistry.ORIENTATIONVERTICAL) )
				{
				  reOrientated = true;
				}
				
				if ( reOrientated )
				{
          xScaleFactor =  (new Double( bw.getPlaceholderElement().getAttribute("width") ) ).doubleValue() / bw.getBarcodeRawHeight();
          yScaleFactor = bw.getScaleFactor();
				}
				else
				{
          xScaleFactor = bw.getScaleFactor();
          yScaleFactor = 1.0F;
				}
  
			  addBarcode( 
					labelTemplateDocToBePopulated,
					bw.getBarcodeDOM(),
					xScaleFactor,
					yScaleFactor,
					bw.getPlaceholderElement() );
			} // end if ( xtm instanceof XyBarcodeWrapper )
		} // end for iterate through XyTransactionMembers	
	}
	
	private static XyBarcodeGenerator addBarcodeToPopulatorList( 
		XyBarcodeGenerator currentLongestBarcode,
		Element populatorFieldTypeElement,
		String populatorFieldValue,
		Element placeholderElementInTemplate,
		Document labelTemplateDocToBePopulated,
		XyTransaction populatorList,
		int widthMode ) throws Exception
	{
		XyBarcodeGenerator newCurrentLongestBarcode = currentLongestBarcode;
		
	  String symbology;
	  XyBarcodeGenerator barcode = null;
	
	  float orientation = 0.0F;
	  
		if ( placeholderElementInTemplate.getAttribute("id").contains(XyLabelTagRegistry.ORIENTATIONVERTICAL) )
		{
			orientation = 90F;
		}
		
    // TODO: should work out orientation
    final String anyElement = "*"; 
    final Element labelOrientationElement = XyXMLDOMSearchUtils.getElementByPartialAttributeValueMatch( "PRINTABLEREGION", "id", anyElement, labelTemplateDocToBePopulated );
    if ( labelOrientationElement != null )
    {
      if ( labelOrientationElement.getAttribute("id").contains(XyLabelTagRegistry.ORIENTATIONVERTICAL) )
      {
        orientation = 90F;
      }
    }

	  
	  // is the symbology for the barcode defined in the populator XML?
	  if (populatorFieldTypeElement.hasAttribute("symbology"))
	  {
		  symbology = populatorFieldTypeElement.getAttribute("symbology");

		  barcode = barcodeGeneratorFactory.createBarcodeGenerator( 
	  	  	 populatorFieldValue,
	         symbology,
	         labelTemplateDocToBePopulated.getNamespaceURI(), placeholderElementInTemplate, orientation );
  
	  }
	  else // no symbology specified in populator xml
	  {
		  // there should therefore be a symbology in the label
		  // template itself

	    log.trace( "barcode " + placeholderElementInTemplate.getAttribute("id") );
	    
		  barcode = barcodeGeneratorFactory.createBarcodeGenerator( 
	  		 populatorFieldValue,
         XyLabelTagRegistry.getSymbologyInLabelTag( placeholderElementInTemplate.getAttribute("id") ),
	       labelTemplateDocToBePopulated.getNamespaceURI(),
	       placeholderElementInTemplate, orientation);
	  } // end check for symbology specified in populator
	
	  // if we were able to create a barcode generator (because we found a symbology
	  // either
	  // from the populator XML or the label template doc), then...
	  if ( barcode != null )
	  {	  	
	  	// store each barcode generator object
		  populatorList.addTransactionMember((XyTransactionMember)barcode);
		
		  if ( currentLongestBarcode != null )
		  {
			  if (barcode.getBarcodeRawWidth() > currentLongestBarcode.getBarcodeRawWidth())
				{
					newCurrentLongestBarcode = barcode;
				}
		  }
		  else // if currentLongestBarcode == null then we are at the first barcode encountered
		  {
		  	newCurrentLongestBarcode = barcode;
		  }
	  }
	  // if ( barcode != null ) - i.e. we can create a barcode from the specified symbology
	  
	  return newCurrentLongestBarcode;
	}
	
	
	public static void addNumber( 
			Document labelFileDocToBePopulated,
	    final String textValue, 
	    final String tag)
	{
		log.trace( sccsid  // output version of class
               + "\naddNumber tagId = '" + tag + "'" );
    
    Element textElement = labelFileDocToBePopulated.getElementById(tag);
		
		if ( textElement != null )
		{
			if ( textElement.getNodeName() == "text" )
			{
				textElement.setTextContent(textValue);
			}
		}
		else
		{
			// at the moment, we think we don't want to raise this as an error
			log.trace( "textElement = null" );
		}
	}
	
  /**
	 * <p>
	 * Internal implementation of populating a label template copy with a barcode
	 * in a specified field
	 * </p>
	 * 
	 * @param labelTemplate
	 *          (input/output parameter) a copy of the label template represented
	 *          as W3C Document - this is modified by this method because it
	 *          populates it with the barcode
	 *          
	 * @param barcodeDOM (input parameter) the barcode as a xml svg document 
	 * 
	 * @param scaleFactor (input parameter) the horizontal scale factor
	 * 
	 * @param existingElementWithTagId (input parameter) the placeholder element that defines where the barcode should go
	 *          
	 */
	public static void addBarcode( Document labelTemplate, final Document barcodeDOM, final double xScaleFactorValue, final double yScaleFactorValue, final Element existingElementWithTagId )
	{		
		XySVGInsert.insert( labelTemplate, barcodeDOM, xScaleFactorValue, yScaleFactorValue, existingElementWithTagId, "shape-rendering:crispEdges" );
	}
	
	public static void addDate( Document labelTemplateDocToBePopulated ) throws JaxenException
	{
		List listOfDateTextElementPlaceholders = 
       XyXMLDOMSearchUtils
        .getElementsByPartialAttributeValueMatch( XyLabelTagRegistry.getDateTag(), "id", "text", labelTemplateDocToBePopulated);
    
		for (int i = 0; i < listOfDateTextElementPlaceholders.size(); i++ )
		{
			Element textPlaceholderElementInTemplate = (Element)listOfDateTextElementPlaceholders.get(i);
		
      if ( textPlaceholderElementInTemplate != null )
      {
        String dateFormat = textPlaceholderElementInTemplate.getTextContent();
      
        log.trace( "dateFormat = " + dateFormat );
        
        String dateFormatLowerCase = dateFormat.toLowerCase();
        
        log.trace( "toLowerCase dateFormatLowerCase = " + dateFormatLowerCase );

        SimpleDateFormat dateFormatter  = new SimpleDateFormat(dateFormatLowerCase);
        Date today = new Date();
        String dateOut = dateFormatter.format(today);

        textPlaceholderElementInTemplate.setTextContent(dateOut);
      
        log.trace("dateOut" + dateOut);
      }
		}
	}
	
  //truncate value to last n characters, where n is the length of the placeholder field
	// e.g. value = 12345678
	// placeholder field is SSSSS
	// length of placeholder field is 5
	// therefore trunctaed value is 45678 if truncate mode is tail 
	public static String getTruncatedValue( Document labelTemplateDocToBePopulated, String populatorFieldTag, String value ) throws JaxenException
	{
    String truncatedValue = null;
		
    /*
     * 
		Element textPlaceholderElementInTemplate 
    = XyXMLDOMSearchUtils
      .getElementByPartialAttributeValueMatch( populatorFieldTag, "id", "text", labelTemplateDocToBePopulated);
    */
    Element textPlaceholderElementInTemplate = labelTemplateDocToBePopulated.getElementById( populatorFieldTag );
  
    if ( textPlaceholderElementInTemplate != null )
    {
    	if ( populatorFieldTag.contains(XyLabelTagRegistry.TRUNCATEMODE_TAIL) )
    	{
    		// truncate value to last n characters, where n is the length of the placeholder field
    		truncatedValue = StringUtils.right( value, textPlaceholderElementInTemplate.getTextContent().length() );
    	}
    }
    
    return truncatedValue;
	}
	
	public static String getPlaceholderWidth( Element placeholder )
	{
		String width = null; 
		
		// placeholder.getAttribute("width"))
		if ( placeholder.getAttribute("id").contains(XyLabelTagRegistry.ORIENTATIONVERTICAL) )
		{
			width = placeholder.getAttribute("height");
		}
		else
		{
			width = placeholder.getAttribute("width");
		}
		
		return width;
	}
	
	public static String getTemplatePath( final Document inputPopulatorDoc )
	{
		Element docElement = inputPopulatorDoc.getDocumentElement();
		
		log.trace( "getTemplatePath: doc element: " + docElement.getTagName() );
		
		String labelId = docElement.getAttribute("id");
		
		return labelId;
	}
} // end class
