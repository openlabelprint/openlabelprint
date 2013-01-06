//-------------------------------------------------------------------------
// (C) COPYRIGHT Xyratex Storage Systems Division 2011
// All Rights Reserved
//
// Filename    : XyLabelTagRegistry.java
// Author      : Rob Davis


// By          : Rob Davis
// File Type   : Java source code for Open Label Print
//-------------------------------------------------------------------------

package com.xyratex.label.tags;

import java.io.File;
import java.util.List;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.jaxen.JaxenException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.xyratex.xml.io.XyDocumentProducer;
import com.xyratex.xml.utils.XyXMLDOMSearchUtils;

/**
 * <p>Singleton class providing centralised knowledge about the tags used within a label drawing.</p>
 * 
 * <p>Provides the basis to add new tag definitions while the system is in operation.</p>
 * 
 * <p>Tag definitions can be added at run time, avoiding that would be required if the code 
 * had to be modified, tested and released.</p>
 * 
 * @author rdavis
 *
 */
final public class XyLabelTagRegistry
{
	/**
	 *  R = Release L = Level G = date U = time
	 *  Values calculated by SCCS when file is checked out and compiled.
	 */
	public static final String sccsid = "@(#)Xyratex  ISTP  XyLabelTagRegistry.java  %R%.%L%, %G% %U%";
	
	/**
	 * The tagid denoting the outline of the entire label.
	 * The should be only one of these in the entire label drawing.
	 */
	private static String labelOutline = null;

	/**
	 * <p>
	 * The tagid denoting a suboutline in a label. Occurs in labels that
	 * are multi-section - where they are physical precut so that sections
	 * can be peeled off individually. 
	 * </p>
	 * 
	 * <p>
	 * This is a prefix - each sub outline must be unique by appending
	 * a unique instance suffix to this
	 * for example if the suffix were XYLABEL_SUBOUTLINE
	 * then an actual outline element in the label would be
	 * XYLABEL_SUBOUTLINE1, another in the same
	 * label would be XYLABEL_SUBOUTLINE2 etc and so-on.
	 * </p>
	 */
	private static String labelSubOutlinePrefix = null;
	
	/**
	 * Holds the descriptions for each of the tags
	 */
	private static Vector<String> allLabelTagsDescription = new Vector<String>();
	
	/**
	 * Holds all the known tag ids
	 */
	private static Vector<String> allLabelTags = new Vector<String>();
	
	//TODO: use a Map data structure to connect these 2 lists as they should be, (associative array)
	
	/**
	 * The prefix identifying a tag as recognised by the Xyratex Open Label Print system
	 */
	public static final String XYRATEX_PREFIX = "XY";
	
	public static final String XY_BACKGROUND = "XY_BACKGROUND";

	
	/**
	 * The prefix in a tag denoting it refers to a barcode field
	 */
	public static final String BARCODE_PREFIX = XYRATEX_PREFIX + "BC";
	
	/**
	 * The prefix in a tag denoting it refers to a human readable field
	 */
	public static final String HUMAN_READABLE_PREFIX = XYRATEX_PREFIX + "HR";
	
	private static Log log = LogFactory.getLog(XyLabelTagRegistry.class);
	
	/**
	 * 
	 * date
	 * 
	 */
	public static final String DATE_SUFFIX = "DATE";
	public static final String DATE = HUMAN_READABLE_PREFIX + "_" + DATE_SUFFIX;
	
	/**
	 * class constructor
	 */
  static
  {
  	log.trace(sccsid + "\nthe class constructor" );
  	addKnownTags();
  }
  
  public static final String ORIENTATIONVERTICAL = "ORIENTATIONVERTICAL";
  
  public static final String ORIENTATIONVERTICALTOP = ORIENTATIONVERTICAL + "TOP";
  
  public static final String ORIENTATIONVERTICALBOTTOM = ORIENTATIONVERTICAL + "BOTTOM";
  
  public static final String ORIENTATIONHORIZONTAL = "ORIENTATION_HORIZONTAL";
  
  public static final String TRUNCATEMODE_TAIL = "TAIL";
  
  public static final String XY_TRANSFORM = "XY_TRANSFORM";
  
  
  public static String getTransformForOrientation( String orientation )
  {
  	String transform = null;
  	
		if ( orientation.contains(XyLabelTagRegistry.ORIENTATIONVERTICALTOP ) )
		{
			transform = "rotate(90)";

		}
		
		if ( orientation.contains(XyLabelTagRegistry.ORIENTATIONVERTICALBOTTOM ) )
		{
			transform = "rotate(-90)";
		}
		
	  return transform;
  }
  
  public static String getDateTag()
  {
  	return DATE;
  }
  
	 /**
   *  <p>List the tags in a given original drawing or label template</p>
   * 
	 * @param labelFilename (input parameter) the input file - a tagged SVG original label drawing or template
   * @return a list of the tags as a Vector of String(s)
	 */
	public static Vector<String> listTagsInLabel(String labelFilename) throws Exception
	{
		log.trace(sccsid + "\nlistTagsInLabel" );
		
		Document doc = XyDocumentProducer.getDocument("svg", new File(labelFilename) );

		return findTagsInDrawing(doc);
	}

  /**
   * <p>Add a new tag.</p>
   * 
   * <p>This enables the system to be told about new tags at runtime,
   * without the need add them to the source and the attendant rebuild delay.
   * Saves time in deployment, being more responsive to customer needs.
   * Obviously a UI would need to be deployed to enable entry of the tag, but this
   * method provides the basic interface that such a UI could access.</p>
   * 
	 * @param tag (input parameter)
   * @param description (input parameter) a description of the tag purpose
	 */
	public static void addLabelTag( final String tag, final String description )
	{
		log.trace(sccsid + "\naddLabelTag" );
		
		// don't add the tag if it has already been added
		for ( int i = 0; i < allLabelTags.size(); i++ )
		{
		  if ( allLabelTags.get(i).equals(tag) )
		  {
		  	return;
		  }
		}
		
		allLabelTags.add( tag );
		allLabelTagsDescription.add( description );
	}
	
  /**
   *  <p>Tells the system about a list of already known tags</p>
   * 
   * <p>Although we can add tags at runtime (via addLabelTag), addKnownTags provides
   * the built-in convenience of already knowing about most of the tags, enabling
   * quicker initial deployment.</p>
	 */
  private static void addKnownTags()
  {
		log.trace(sccsid + "\naddKnownTags" );
  	
  	addLabelTag( "XYBC_QTY", "Bar code quantity" );
  	addLabelTag( "XYBC_PACK_DATE", "Bar code pack date" );
  	addLabelTag( "XYBC_BATCH_NUMBER", "Bar code batch number"  );
  	addLabelTag( "XYBC_CARTON_NUMBER", "Bar carton number" );
  	addLabelTag( "XYBC_XPN", "Bar code Xyratex part number" );
  	addLabelTag( "XYBC_SN", "Bar code serial number" );
  	addLabelTag( "XYBC_CPN", "Bar code Customer part number" );
  	addLabelTag( "XYBC_VER", "Bar code customer version number" );
  	
  	addLabelTag( "XYHR_QTY", "quantity - human readable field" );
  	addLabelTag( "XYHR_PACK_DATE", "pack date - human readable field" );
  	addLabelTag( "XYHR_BATCH_NUMBER", "batch number - human readable field" );
  	addLabelTag( "XYHR_CARTON_NUMBER", "carton number - human readable field" );
  	addLabelTag( "XYHR_XPN", "part number - human readable field" );
  	addLabelTag( "XYHR_SN", "serial number - human readable field" );
  	addLabelTag( "XYHR_CPN", "customer part number - human readable field" );
  	addLabelTag( "XYHR_VER", "customer version number - human readable field" );
  	
  	addLabelTag( DATE, "date code" );
  	addLabelTag( "XYHR_WWN", "WWN" );
  	addLabelTag( "XYBC_WWN", "WWN" );

  	addLabelTag( "XYHR_COUNTRY", "COUNTRY" );
  	addLabelTag( "XYHR_PN", "PN" );
  	addLabelTag( "XYBC_PN", "PN" );
  	addLabelTag( "XYHR_FC", "FC" );
  	addLabelTag( "XYBC_FC", "FC" );
  	
  	addLabelTag( "XYHR_PN2", "PN" );
  	addLabelTag( DATE + "2", "date code 2" );
  	
  	labelOutline = "XYLABEL_OUTLINE";
  	labelSubOutlinePrefix = "XYLABEL_SUBOUTLINE";
  	
  	// TODO: XYLABEL_SUBOUTLINEn for multi-section labels
  }
  
  /**
   * <p>Get the outline id of the label.</p>
   * 
   * @return the outline id as a String
   */
  public static String getLabelOutline()
  {
		log.trace(sccsid + "\ngetLabelOutline");
  	return labelOutline;
  }
  
  /**
   * <p>Get the sub outline id of the label.</p>
   * 
   * @return the sub outline id as a String
   */
  public static String getLabelSubOutlinePrefix()
  {
		log.trace(sccsid + "\ngetLabelSubOutlinePrefix");
  	return labelSubOutlinePrefix;
  }
  
  
  /**
   * <p>Get superset master list of tags that the XySVGLabelTemplateProducer knows about</p>
   * 
   * <p>This returns as a String a superset master list of all the tags that the XySVGLabelTemplateProducer
   * knows about. That is, not within a particular label drawing but overall.</p>
   * 
   * @return String  the tags
	 */
  public static String getMasterListOfTags()
  {
		log.trace(sccsid + "\ngetMasterListOfTags");
  	
  	String list = new String();
  	
		for (int i = 0; i < allLabelTags.size() && i < allLabelTagsDescription.size(); i++ )
		{
		  list += (String) allLabelTags.get(i)
					 + " - "
					 + (String) allLabelTagsDescription.get(i) 
					 + "\n";
		}
		
		return list;
  }
  
  /**
   * <p>Get a list of tags already known about that occur within a 
   * label drawing</p>
   * 
   * <p>This finds the tags that XySVGLabelTemplateProducer knows about within a label drawing.
   * It uses the master superset list of tags and sees whether each one occurs within the drawing.
   * If a tag in the drawing is not in the master list then it won't be recognised and won't be returned.
   * </p>
   * 
   * @param doc (input parameter) the label drawing in W3C Document form
   * 
   * @return Vector  - the list of tags
	 */
	public static Vector<String> findTagsInDrawing( final Document doc ) throws JaxenException
	{
		log.trace(sccsid + "\nfindTagsInDrawing");
		
		Vector<String> tags = new Vector<String>();
		
    final String anyElement = "*"; 
		
    /*
     * 
    // we don't want to hard code the tags we know about anymore
     * 
		for (int i = 0; i < allLabelTags.size(); i++ )
		{

		  Element element = XyXMLDOMSearchUtils.getElementByPartialAttributeValueMatch( (String)allLabelTags.get(i), "id", anyElement, doc );
			
			if ( element != null )
			{
				//tags.add( allLabelTags.get(i) );
				tags.add( element.getAttribute("id"));
			}
		}
		*/
		
		List humanreadables = XyXMLDOMSearchUtils.getElementsByPartialAttributeValueMatch( "XYHR", "id", anyElement, doc );
		List barcodes = XyXMLDOMSearchUtils.getElementsByPartialAttributeValueMatch( "XYBC", "id", anyElement, doc );
		
		for ( int i = 1; i < humanreadables.size(); i++ )
		{
		  Element e = (Element) humanreadables.get( i );
		  tags.add( e.getAttribute("id") );
		}
		
    for ( int i = 1; i < barcodes.size(); i++ )
    {
      Element e = (Element) barcodes.get( i );
      tags.add( e.getAttribute("id") );
    }
		
		return tags;
	}

	public static boolean labelTemplateTagHasATruncationMode( String tag )
	{
		boolean truncation = false;
		
		String[] tagFields = getFieldsFromTag( tag );
		
		final int fieldTypePositionInArray = 1;
		
		if ( tagFields[fieldTypePositionInArray].contains(TRUNCATEMODE_TAIL) )
		{
			truncation = true;
		}
			
		return truncation;
	}

  /**
   * <p>Get a list of tags, as a String already known about that occur within a 
   * label drawing</p>
   * 
   * <p>This finds the tags that XySVGLabelTemplateProducer knows about within a label drawing.
   * It uses the master superset list of tags and sees whether each one occurs within the drawing.
   * If a tag in the drawing is not in the master list then it won't be recognised and won't be returned.
   * </p>
   * 
   * <p>Useful for command line UIs</p>
   * 
   * @param doc (input parameter) the label drawing in W3C Document form
   * 
   * @return String  the list of tags as a String
	 */
	public static String listTagsInDrawingAsString( final Document doc ) throws JaxenException
	{
		String tags = new String();
		
		Vector<String> tagsInDrawing = findTagsInDrawing( doc );
		
		for (int i = 0; i < tagsInDrawing.size(); i++ )
		{
			tags += ((String)tagsInDrawing.get(i) + "\n");
		}
		
		return tags;
	}
	
	/**
	 * <p>Indicates if tag is for a barcode.</p>
	 * 
	 * @param tagString (input) the tagid
	 * @return boolean - true if tag is for a barcode, otherwise false
	 */
	public static boolean tagIsForBarcode( final String tagString )
	{
		return tagString.contains(BARCODE_PREFIX);
	}

	/**
	 * <p>Indicates if tag is for a a human readable field.</p>
	 * 
	 * @param tagString (input) the tagid
	 * @return boolean - true if tag is for a human readable field, otherwise false
	 */
	public static boolean tagIsForHumanReadable( final String tagString )
	{
		return tagString.contains(HUMAN_READABLE_PREFIX);
	}
	
	/**
	 * <p>Gets the placeholder element as a W3C DOM Element, given the id of the element as a String.</p>
	 * 
	 * @param tagString (input) the tagid, can partially match with the full id of the Element
	 * @return the element as a W3C DOM Element
	 */
	public static Element getPlaceholderElement( final String tagId, final Document labelTemplate ) throws JaxenException
	{
		return XyXMLDOMSearchUtils.getElementByPartialAttributeValueMatch( tagId, "id", "rect", labelTemplate );
	}
	

	

	public static String getSymbologyInLabelTag( String tag )
	{
		log.trace(sccsid + "\ngetSymbologyInLabelTag" );
		
		// a robust way of parsing the tags? design decision:
		//  - does it contain XYBC? 
		//    - if yes, then it is a barcode field
		//    - check for symbology in string
		//      - either:
		//         1) assume that the symbology is in a certain position in the string
		//           - advantage: efficient, no need to know about supported symbologies - decoupled from barcode producers
		//           - disadvantage: not robust against changes to the format of the string, e.g. if other things were added later
		//             (but how likely is this?)
		//
		//         2) look for the symbology to be contained in the tag string
		//           - advantage: free format, symbology can be in any position
		//           - disadvantage: less efficient, a search on supported symbologies has to be performed for each tagged barcode element in the label
		//             and the actual barcode number might contain a pattern that is the same as the symbology
		//
		// Decided on option 1)
	  //
	  // 0      1         2  
	  // PREFIX_FIELDTYPE_SYMBOLOGY
		//
	  // where 
	  // 0 - PREFIX always is XYBC for barcode fields in a label
	  // 1 - FIELDTYPE is the type of field, examples: serial number, part number
	  // 2 - SYMBOLOGY is the barcode symbology
	  //
		// example tags in label drawing
		// XYBC_XPN_CODE128
	  // XYBC_SN_CODE128
		
	  final int tagFields = 3;  
	  final int symbologyPositionInArray = 2; // third item in tag since counting from 0, 1, 2
	  final String separator = "_";
	  
		String symbology = null;

		String[] tagIdAndSymbologyArray = StringUtils.split(tag, separator ); // tagIdAndSymbologyArraySize);

		if (tagIdAndSymbologyArray.length >= tagFields)
		{
			symbology = tagIdAndSymbologyArray[symbologyPositionInArray];
		}

		return symbology;
	}
	
	private static String[] getFieldsFromTag( String tag )
	{
	  final String separator = "_";
	  
	  String[] tagFields = StringUtils.split(tag, separator );
	  
	  return tagFields;
	}

  public static String getLabelBackgroundPrefix()
  {
    return XY_BACKGROUND;
  }
}
