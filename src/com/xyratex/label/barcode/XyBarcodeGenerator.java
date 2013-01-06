//-------------------------------------------------------------------------
// (C) COPYRIGHT Xyratex Storage Systems Division 2011
// All Rights Reserved
//
// Filename    : XyBarcodeGenerator.java
// Author      : Rob Davis


// By          : Rob Davis
// File Type   : Java source code for Open Label Print
//-------------------------------------------------------------------------

package com.xyratex.label.barcode;

import java.util.Collection;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * <p>Common Interface for all barcode generators.</p>
 * 
 * <p>This common interface means that any 3rd party barcode generator 
 * can be used in a standard way without having to write new code.</p>
 * 
 */
public interface XyBarcodeGenerator
{
	 /**
	  *  R = Release L = Level G = date U = time
	  */
	 public static final String sccsid = "@(#)Xyratex  ISTP  XyBarcodeGenerator.java  %R%.%L%, %G% %U%";
	
	 /**
	  * Scaling mode for barcode: unset
	  */
	 public static final int WIDTH_UNSET = 0;
	 
	 /**
	  * Scaling mode for barcode: scale barcode to fit placeholder
	  */
   public static final int WIDTH_SCALEFACTOR_PLACEHOLDER = 1;
   
	 /**
	  * <p>Scaling mode for barcode: scale barcode relative to longest barcode on the label.
	  * This is a way to ensure that all barcodes use the same width in their bars, for aesthetic and read reliability reasons.</p>
	  */
   public static final int WIDTH_SCALEFACTOR_RELATIVE_TO_LONGEST = 2;
   
	 /**
	  * <p>This is another way to ensure that all barcodes use the same width in their bars, for aesthetic and read reliability reasons.
	  * However the resultant barcode may exceed the size of its placeholder in the label. This therefore is a useful validation
	  * mode, whereby the industrial designer that produces the label drawing can test the placeholder sizes to see if they
	  * can house a barcode that can be reliably scanned. For example, applying the minimum module width for a barcode,
	  * as defined by its specification, and seeing if it fits the defined placeholder on the label.</p>
	  */
   public static final int WIDTH_USE_MODULE_WIDTH = 3;
  
   
   public static final int orientationAngleInDegrees_standardHorizontal = 0;
   
   public static final int orientationAngleInDegrees_verticalReadFromTop = 90;
   
   public static final int orientationAngleInDegrees_verticalReadFromBottom = -90;
   
   /**
    * <p>
    * Set the x-scale factor (and therefore the overall final width) of the barcode
    * using the module width.
    * </p>
    * 
    * <p>
    * The module is the thinnest line in the barcode, upon which the rest have thickness which is multiples of this.
    * </p>
    * 
    * <p>
    * This method accepts module width defined as pixels or as millimetres.
    * </p>
    * 
    * <p>
    * If module width is specified as mm, then the 2nd parameter may not be required by the implementation
    * This method may be modified in future to not require the 2nd parameter in this case.
    * </p>
    * 
    * @param moduleWidth (input parameter) in String format with the value and units (mm or if unit absent pixels is assumed).
    * @param dotsPerUnitLengthAsString
    * @throws Exception - enables the implementation to throw Exceptions if necessary, for example for invalid values or use of the method.
    */
   public void setScaleFactorUsingModuleWidth( final String moduleWidth, final String dotsPerUnitLengthAsString ) throws Exception;
   
   /**
    * <p>
    * Set the x-scale factor (and therefore the overall final width) of the barcode
    * using the module width.
    * </p>
    * 
    * <p>
    * The module is the thinnest line in the barcode, in mm, upon which the rest have thickness which is multiples of this.
    * </p>
    * 
    * @param moduleWidth (input parameter) in String format with the value and units (mm).
    * @throws Exception - enables the implementation to throw Exceptions if necessary, for example for invalid values or use of the method.
    */
   public void setScaleFactorUsingModuleWidthInMM( final String moduleWidth ) throws Exception;
   
   /**
    * <p>
    * Set the x-scale factor (and therefore the overall final width) of the barcode
    * using the module width.
    * </p>
    * 
    * <p>
    * The module is the thinnest line in the barcode, upon which the rest have thickness which is multiples of this.
    * </p>
    * 
    * <p>This enables a barcode to be generated with module width to per pixel accuracy, so the dpi needs to be known
    * so that the size of the barcode SVG can be scaled accordingly.
    * </p>
    * 
    * <p>Per pixel accuracy does not mean more reliable barcodes, it just means that there is more control about
    * the size of the barcode, if needed.</p>
    * 
    * @param moduleWidth (input parameter) in String format with the value.
    * @param dotsPerUnitLengthAsString
    * @throws Exception - enables the implementation to throw Exceptions if necessary, for example for invalid values or use of the method.
    */
   public void setScaleFactorUsingModuleWidthInPixels( final String moduleWidth, final String dotsPerUnitLengthAsString ) throws Exception;

   /**
    * <p>Scale the barcode to fit the placeholder width.</p>
    * 
    * @param placeHolderWidth
    * @throws Exception - enables the implementation to throw Exceptions if necessary, for example for invalid values or use of the method.
    */
   public void setScaleFactorUsingPlaceholderWidth( final double placeHolderWidth ) throws Exception;
   
   /**
    * <p>Check to see if the symbology is supported by the barcode generator.</p>
    */
   public boolean isSymbologySupported( final String symbology );
   
   /**
    * <p>
    * Create the active barcode generator.
    * 
    * <p>When we say 'active' we mean the generator that will actually generate barcodes.</p>
    * 
    * <p>This method is invoked on an initial instance of the generator created using the default constructor with
    * no parameters. We call this initial instance the parent, which itself is not used to generate
    * barcode but simply to spawn children that do - active generators.
    * </p>
    * 
    * <p>Using a 'parent' instance to create active instances came about because the underlying 3rd party
    * barcode generating library, barcode4j, requires creation of an instance of its barcode generator bean before the
    * symbology can be selected. Also, to query what symbologies are supported by barcode4j requires instantiation of
    * the bean.</p>
    * 
    * <p>We could use a single instance of the bean to generate all the barcodes we require, but that assumes
    * a single thread environment. We want this to be thread safe so that we can create several barcodes on
    * the fly, on a multi-core multi-threaded platform, to maximise performance. Particularly if the barcode
    * generation is based on a central server, which may use a thread for each client print station's barcode
    * generating requirements.</p>
    * 
    * <p>This design will suit other 3rd party barcode generating implementations. The design is meant to
    * support more than one 3rd party library at once so that we can support the maximum number of symbologies
    * across all 3rd party libraries available.</p>
    * 
    * @param barcodeNumber
    * @param barcodeType
    * @param aNamespacePrefixAsString
    * @param thePlaceholderElement
    * @return XyBarcodeGenerator
    * @throws Exception
    */
   public XyBarcodeGenerator createNewGenerator( 
  		 final String barcodeNumber,
       final String barcodeType,
       final String aNamespacePrefixAsString,
       final Element thePlaceholderElement,
       float orientation ) throws Exception; // allow for any exception that the contained implementation can throw
  
  /**
   * <p>Get the raw width of the barcode before scaling.
   * This is used to work out the relative lengths a set of a barcodes in a label</p>
   */
  public double getBarcodeRawWidth() throws Exception;
  
  /**
   * <p>Get the raw width of the barcode before scaling.
   * This is used to work out the relative lengths a set of a barcodes in a label</p>
   */
  public double getBarcodeRawHeight() throws Exception;
  
  /**
   * <p>Get the scale factor of the barcode</p>
   */
  public double getScaleFactor() throws Exception;
  
  /**
   * <p>The barcode generator knows about the placeholder element 
   * for the necessary association between generator and placeholding during 
   * the multi-stage process whereby barcode scaling is calculated for all labels.</p>
   */
  public Element getPlaceholderElement() throws Exception;
  
  /**
   * Get the symbology (the barcode type)
   */
  public String getSymbology() throws Exception;

  /**
   * Get the generated barcode SVG XML
   * 
   * @return the generated barcode SVG XML in the form of a W3C DOM Document
   * @throws Exception
   */
  Document getBarcodeDOM() throws Exception;
  
  /**
   * Get the symbologies that an implementation of this interface supports.
   * @return a Collection of String(s) each string being a supported symbology
   */
  public Collection<String> getSupportedSymbologies();
  
  /**
   * Query that an instance of this interface is the actual active barcode
   * Generator and not a parent which is only used to generate active instances.
   */
  public boolean getIsInstanceFlag();
  
  public double getModuleWidth();
}

