//-------------------------------------------------------------------------
// (C) COPYRIGHT Xyratex Storage Systems Division 2011
// All Rights Reserved
//
// Filename    : XyAbstractBarcodeImpl.java
// Author      : Rob Davis


// By          : Rob Davis
// File Type   : Java source code for Open Label Print
//-------------------------------------------------------------------------

package com.xyratex.label.barcode;

import java.util.Collection;
import java.util.Iterator;

import org.w3c.dom.Element;

import org.apache.commons.logging.Log;

import com.xyratex.label.output.XyLabelBitmapProducer;
import com.xyratex.svg.rasterize.XySVGtoBitmapProducer;
import com.xyratex.transaction.XyTransactionMember;

/**
 * Provides common wrapper functionality for all derived concrete generators.
 */
public abstract class XyAbstractBarcodeImpl implements XyBarcodeGenerator, XyTransactionMember
{ 
	/**
	 *  R = Release L = Level G = date U = time
	 *  Values calculated by SCCS when file is checked out and compiled.
	 */
	public static final String sccsid = "@(#)Xyratex  ISTP  XyAbstractBarcodeImpl.java  %R%.%L%, %G% %U%";
	
	/**
	 * log object used for logging activity in the form of String/character messages for debug, diagnostic and analysis purposes.
	 * The output destination for the log could be console output and/or file(s) depending on the user-definable overall configuration
	 * of logs in the system.
   *
   * Derived class to declare value for.
   */
	 protected Log log = null;
	
	 protected boolean isInstanceFlag = false;
	 
	  /**
    * Default value.
    */
   protected int widthMode = WIDTH_SCALEFACTOR_RELATIVE_TO_LONGEST;
    
   /**
    *  By default, if used for WIDTH_USE_MODULE_WIDTH mode.
    */
   protected int moduleWidth = 1; 
   
   /**
    *  By default, if used for WIDTH_SCALEFACTOR_PLACEHOLDER or WIDTH_SCALEFACTOR_RELATIVE_TO_LONGEST.
    */
   protected double scaleFactor = 1.0; 
   
   /**
    * Derived class to declare value for.
    */
   protected String symbology = ""; 
  
   /**
    * Derived class to declare value for.
    */
   protected Element placeholderElement = null;
   
   protected float orientation = orientationAngleInDegrees_standardHorizontal; // by default
   
   
   public double getScaleFactor()
   {
  	 return scaleFactor;
   }
   
   /**
    * <p>
    * Sets the horizontal scale factor to scale the barcode by, based on placeHolderWidth.
    * scaleFactor = placeHolderWidth / width of barcode (as supplied by specific barcode generator)
    * </p>
    * @param placeHolderWidth
    */
   public void setScaleFactorUsingPlaceholderWidth( final double placeHolderWidth ) throws Exception
   {
  	 scaleFactor = placeHolderWidth / getBarcodeRawWidth();
   }
   
   public String getSymbology()
   {
  	 return symbology;
   }

   public Element getPlaceholderElement()
   {
  	 return placeholderElement;
   }
   
   public boolean isSymbologySupported( final String symbology )
   {
   	boolean foundSymbology = false;
   	
   	final Collection symbologies = getSupportedSymbologies();
   	
     Iterator symbologiesIterator = symbologies.iterator();
     
     while ( symbologiesIterator.hasNext() )
     {
     	String aSymbology = (String) symbologiesIterator.next();
     	
     	if ( aSymbology.equals(symbology) )
     	{
     		foundSymbology = true;
     	}
     }
   	
   	return foundSymbology;
   }
   
   public final boolean getIsInstanceFlag()
   {
  	 return isInstanceFlag;
   }
   
   /**
    * @see com.xyratex.label.barcode.XyBarcodeGenerator
    */
   public void setScaleFactorUsingModuleWidth( final String moduleWidth, final String dotsPerUnitLengthAsString ) throws XyExceptionNotAnActiveInstance
   {
   	if ( !isInstanceFlag ) throw new XyExceptionNotAnActiveInstance( XyExceptionNotAnActiveInstance.NOT_ACTIVE_INSTANCE ); 
   	
   	if ( moduleWidth.contains("mm") )
   	{
   		setScaleFactorUsingModuleWidthInMM( moduleWidth );
   	}
   	else // assume pixels required
   	{
   		setScaleFactorUsingModuleWidthInPixels( moduleWidth, dotsPerUnitLengthAsString );
   	}
   }
   
   /**
    * @see com.xyratex.label.barcode.XyBarcodeGenerator
    */
   public void setScaleFactorUsingModuleWidthInMM( final String moduleWidth ) throws XyExceptionNotAnActiveInstance
   {
   	if ( !isInstanceFlag ) throw new XyExceptionNotAnActiveInstance( XyExceptionNotAnActiveInstance.NOT_ACTIVE_INSTANCE ); 
   	
   	double requiredModuleWidthInMM = 0.0;
   	
   	double originalModuleWidth = getModuleWidth();
   	
 		requiredModuleWidthInMM = Double.parseDouble( moduleWidth.replace("mm", "") );
 		
     //bean.setModuleWidth( moduleWidthInMM ); seems to be ignored so we can alter the size of the modules by this calculation
   	scaleFactor = requiredModuleWidthInMM / originalModuleWidth;
   }
   
   /**
    * @see com.xyratex.label.barcode.XyBarcodeGenerator
    */
   public void setScaleFactorUsingModuleWidthInPixels( final String moduleWidth, final String dotsPerUnitLengthAsString ) throws XyExceptionNotAnActiveInstance
   {
   	if ( !isInstanceFlag ) throw new XyExceptionNotAnActiveInstance( XyExceptionNotAnActiveInstance.NOT_ACTIVE_INSTANCE ); 
   	
   	double requiredModuleWidthInMM = 0.0;
   	
   	double originalModuleWidth = getModuleWidth();
   	
 		float millimetresPerPixel = XySVGtoBitmapProducer.getMillimetresPerPixelFromDotsPerUnitLength( dotsPerUnitLengthAsString );
 		
 		int moduleWidthInPixels = Integer.parseInt(moduleWidth);
 		
   	requiredModuleWidthInMM = moduleWidthInPixels * millimetresPerPixel;
   	
     //bean.setModuleWidth( moduleWidthInMM ); seems to be ignored so we can alter the size of the modules by this calculation
   	scaleFactor = requiredModuleWidthInMM / originalModuleWidth;
   }
}
