//-------------------------------------------------------------------------
// (C) COPYRIGHT Xyratex Storage Systems Division 2011
// All Rights Reserved
//
// Filename    : XyBarcode4JGenerator.java
// Author      : Rob Davis


// By          : Rob Davis
// File Type   : Java source code for Open Label Print
//-------------------------------------------------------------------------

package com.xyratex.label.barcode.barcode4j;

import java.lang.Float;

import java.util.Collection;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.apache.commons.logging.LogFactory;

import org.krysalis.barcode4j.BarcodeClassResolver;
import org.krysalis.barcode4j.BarcodeDimension;
import org.krysalis.barcode4j.DefaultBarcodeClassResolver;
import org.krysalis.barcode4j.HumanReadablePlacement;
import org.krysalis.barcode4j.impl.AbstractBarcodeBean;
import org.krysalis.barcode4j.output.BarcodeCanvasSetupException;
import org.krysalis.barcode4j.output.svg.SVGCanvasProvider;

import com.xyratex.label.barcode.XyAbstractBarcodeImpl;
import com.xyratex.label.barcode.XyBarcodeGenerator;
import com.xyratex.label.barcode.XyExceptionNotAnActiveInstance;
import com.xyratex.label.output.XyLabelBitmapProducer;
import com.xyratex.label.population.exception.XyExceptionBarcodeSymbologyUnsupported;

/**
 * <p>
 * Wrapper class for Barcode4J barcode generator.
 * </p>
 * 
 * <p>
 * Instances of this class should not be directly created by clients, they should
 * use the XyBarcodeGeneratorFactory - @see com.xyratex.label.barcode.XyBarcodeGeneratorFactory
 * </p>
 * 
 * <p>Descriptions of the methods here will be specific to this implementation. 
 * For a generic description of the XyBarcodeGenerator interface methods that this class implements,
 * @see com.xyratex.label.barcode.XyBarcodeGenerator
 * </p>
 * 
 * @author rdavis
 *
 */
final public class XyBarcode4JGenerator extends XyAbstractBarcodeImpl
{
	/**
	 *  R = Release L = Level G = date U = time
	 */
	public static final String sccsid = "@(#)Xyratex  ISTP  XyBarcode4JGenerator.java  %R%.%L%, %G% %U%";
	
	/**
	 * This is the object in the Barcode4J 3rd-party library that is used to select the 'bean' - the
	 * generator of the symbology and it also supplies information on the symbologies supported by Barcode4J.
	 */
  final private BarcodeClassResolver resolver = new DefaultBarcodeClassResolver();
	
  /**
   * This is the object in the Barcode4J 3rd-party library that does most of the work of generating the
   * barcode.
   */
  private AbstractBarcodeBean bean = null;
  
  /**
   * This is the object in the Barcode4J 3rd-party library that, together with the bean,
   * produces the barcode as an SVG XML Document.
   */
  private SVGCanvasProvider canvas = null;
  
  /**
   * This holds the namespace of the Document that the barcode SVG XML will be inserted into.
   * The Barcode4J 3rd-party library needs to know this when generating the barcode so that
   * it can be inserted into the Document and displayed correctly in the label.
   */
  private String namespacePrefixAsString = null;
  
  /**
   * The dimensions of the generated barcode before it has been scaled.
   * This is used to calculate how much scaling is required for the barcode to
   * fit a placeholder, or to be rendered based on a certain module (the thinnest line) width.
   */
  private BarcodeDimension dim = null;

  
  /**
   * Constructor for making the parent instance of the generator, which will then be used to 
   * generate child active generators. This is for use by the @see com.xyratex.label.barcode.XyBarcodeGeneratorFactory
   * only.
   */
  public XyBarcode4JGenerator()
  {
  	log = LogFactory.getLog(XyBarcode4JGenerator.class);
  	
  	log.trace( "\n" + XyBarcodeGenerator.sccsid // id of implemented interface
	           + "\n" + XyAbstractBarcodeImpl.sccsid // id of superclass
	           + "\n" + sccsid 
	           + "\n parent"
	     );
  }
             
  /**
   * @see com.xyratex.label.barcode.XyBarcodeGenerator
   */
  public XyBarcodeGenerator createNewGenerator( 
          		 final String barcodeNumber,
               final String barcodeType,
               final String aNamespacePrefixAsString,
               final Element thePlaceholderElement,
               final float anOrientation ) 
    throws BarcodeCanvasSetupException,
           XyExceptionBarcodeSymbologyUnsupported,
           InstantiationException,
           IllegalAccessException
  {
  	final boolean isABarcodeGeneratingInstance = true;
  	XyBarcodeGenerator generator = new XyBarcode4JGenerator( barcodeNumber, barcodeType, aNamespacePrefixAsString, thePlaceholderElement, anOrientation, isABarcodeGeneratingInstance );
  	
  	return generator;
  }
  
  /**
   * Private constructor called by createNewGenerator method to create the active generator instance
   */
  private XyBarcode4JGenerator( 
   		  final String barcodeNumber,
        final String barcodeType,
        final String aNamespacePrefixAsString,
        final Element thePlaceholderElement,
        final float anOrientation,
        final boolean isBarcodeGeneratingInstance ) 
        throws BarcodeCanvasSetupException,
               XyExceptionBarcodeSymbologyUnsupported,
               InstantiationException,
               IllegalAccessException
  {
  	isInstanceFlag = isBarcodeGeneratingInstance;
  	
  	log = LogFactory.getLog(XyBarcode4JGenerator.class);
  	
  	log.trace(   "\n" + XyBarcodeGenerator.sccsid 
  			       + "\n" + XyAbstractBarcodeImpl.sccsid 
  			       + "\n" + sccsid 
  			       + "\n" + barcodeType
  			     );
  	
  	orientation = anOrientation;
  	
  	placeholderElement = thePlaceholderElement;
  	
  	symbology = barcodeType;
  	
  	namespacePrefixAsString = aNamespacePrefixAsString;
  	
  	// we'd like to use a specific type with Class but barcode4j 
  	// is over generic so there is no benefit; we'll still get a compiler warning about not being 
  	// specific with our raw Class type.
    Class reflectionClass; 
    
    int orientationAsInt = (new Float(orientation)).intValue();
    
    canvas = new SVGCanvasProvider( namespacePrefixAsString, orientationAsInt );
    
    // get the barcode4j generator to generate the barcode given the value in symbology
    try
    {
      reflectionClass = resolver.resolveBean(symbology);
    }
    catch( ClassNotFoundException e )
    {
    	// catch the generic ClassNotFoundException and throw a more informative, specific
    	// exception: XyExceptionBarcodeSymbologyUnsupported
      throw( new XyExceptionBarcodeSymbologyUnsupported( XyExceptionBarcodeSymbologyUnsupported.UNSUPPORTED + ": " + barcodeType ) );
    }
    
    bean = (AbstractBarcodeBean)reflectionClass.newInstance();
    
    // our industrial designers - who make the label drawing - already layout the label to have space around it
    // so there is no need to have a quiet zone
    bean.doQuietZone(false); 

    bean.setMsgPosition(HumanReadablePlacement.HRP_NONE);

    // Set the height of the barcode-to-be-generated, based on the height of the placeholder element
    double barcodePlaceHolderHeight = Double.valueOf(thePlaceholderElement.getAttribute("height")).doubleValue();
    bean.setHeight(barcodePlaceHolderHeight);
       
    // generate the barcode
    bean.generateBarcode(canvas, barcodeNumber);
    
    // calculate the raw (before horizontal scaling) dimensions of the barcode
    dim = bean.calcDimensions(barcodeNumber);
  }
  
  /**
   * Get the width of the generated barcode, before it has been scaled.
   */
  public double getBarcodeRawWidth() throws XyExceptionNotAnActiveInstance
  {
  	if ( !isInstanceFlag ) throw new XyExceptionNotAnActiveInstance( XyExceptionNotAnActiveInstance.NOT_ACTIVE_INSTANCE ); 
  	
  	return dim.getWidth();
  }
  
  /**
   * <p>Get the raw heigth of the barcode before scaling.
   * This is used to work out the relative lengths a set of a barcodes in a label</p>
   */
  public double getBarcodeRawHeight() throws XyExceptionNotAnActiveInstance
  {
  	if ( !isInstanceFlag ) throw new XyExceptionNotAnActiveInstance( XyExceptionNotAnActiveInstance.NOT_ACTIVE_INSTANCE ); 
  	
  	return dim.getHeight();
  }
  
  /**
   * Get the barcode as an SVG XML W3C DOM Document
   */
  public Document getBarcodeDOM() throws XyExceptionNotAnActiveInstance
  {
  	if ( !isInstanceFlag ) throw new XyExceptionNotAnActiveInstance( XyExceptionNotAnActiveInstance.NOT_ACTIVE_INSTANCE ); 
  	
		return canvas.getDOM();
  }
  
  /**
   * @see com.xyratex.label.barcode.XyBarcodeGenerator
   */
  public Collection getSupportedSymbologies()
  {
  	// we'd like to use a specific type with Collection but barcode4j 
  	// over generic so there is no benefit; we'll still get a compiler warning
    return resolver.getBarcodeNames();
  }
  
  public double getModuleWidth()
  {
  	return bean.getModuleWidth();
  }
}
