//-------------------------------------------------------------------------
// (C) COPYRIGHT Xyratex Storage Systems Division 2011
// All Rights Reserved
//
// Filename    : XyBarcodeGeneratorFactory.java
// Author      : Rob Davis


// By          : Rob Davis
// File Type   : Java source code for Open Label Print
//-------------------------------------------------------------------------

package com.xyratex.label.barcode;

import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Element;

// specific barcode generator implementations here:
import com.xyratex.label.barcode.barcode4j.XyBarcode4JGenerator;

/**
 * <p>Creates an appropriate barcode generator, given a supported barcode symbology/type.</p>
 * 
 * <p>Hides the specifics of 3rd party barcode generator libraries.</p>
 * 
 * <p>The created barcode generator is created with its symbology and value already defined.
 * This forces creation of a generator for each barcode on a label.
 * This enables other data (position, size...) to be retained for each barcode
 * throughout the label creation process which enables logic rules to
 * be applied on all the barcodes of a label, for example scaling of size of each barcode
 * relative to the largest barcode.</p>
 */
public class XyBarcodeGeneratorFactory
{
	public static final String sccsid = "@(#)Xyratex  ISTP  XyBarcodeGeneratorFactory.java  %R%.%L%, %G% %U%";
	
	private Vector<XyBarcodeGenerator> barcodeGenerators = new Vector<XyBarcodeGenerator>();
	
	/**
	 * Registers the supported barcode generators.
	 */
	public XyBarcodeGeneratorFactory()
	{
		// register barcode generators
		//
		// add new ones here
		//
		// eventually we may make this automatic whereby the factory makes
		// use of the java classloader (or related class) to seek out 
		// classes implementing XyBarcodeGenerator
		
		// Each barcode generator registered here is a parent barcode generator
		// used to create generators of that type.
		// See XyBarcodeGenerator for how the parent generator is used to create a the actual
	  // generator of a symbology, and the reasoning behind this approach.
		
		XyBarcodeGenerator barcodeGenerator = new XyBarcode4JGenerator();
		barcodeGenerators.add( barcodeGenerator );
	}
	
	/**
	 * Create a barcode generator
	 * 
	 * @param barcodeNumber (input) the barcode number
	 * @param barcodeType (input) the barcode type/symbology
	 * @param aNamespacePrefixAsString 
	 *   (input) the namespace, this is can be supplied from the document 
	 *   that the barcode will be inserted into, e.g. using org.w3c.dom.Document getNamespaceURI()
	 * @param placeholderElement (input) defines the position and size of the barcode in the label
	 * @return a generator to be accessed via interface XyBarcodeGenerator
	 * @throws Exception (allows the generator implementation to raise exceptions of any type)
	 */
  public XyBarcodeGenerator createBarcodeGenerator( 
  		final String barcodeNumber,
      final String barcodeType,
      final String aNamespacePrefixAsString,
      final Element placeholderElement,
      final float orientation
     ) throws Exception
  {
  	final XyBarcodeGenerator barcodeGenerator = findGeneratorOfSymbology( barcodeType );
  	
  	XyBarcodeGenerator newGenerator = null;
  	if ( barcodeGenerator != null )
  	{
  		newGenerator = barcodeGenerator.createNewGenerator( barcodeNumber, barcodeType, aNamespacePrefixAsString, placeholderElement, orientation );
  	}
  	
 	  // this may return null: it is up to the client calling code to decide if
 	  // this is an error, or can be ignored
  	return newGenerator;
  }
  /**
   * <p>Given a barcode symbology/type, if supported, a parent for a particular generator
   * is returned.</p>
   * 
   * @param symbologyToFind
   * @return XyBarcodeGenerator
   * 
   * @see XyBarcodeGenerator for how the parent generator is used to create a the actual
   * generator of a symbology, and the reasoning behind this approach.
   */
  public XyBarcodeGenerator findGeneratorOfSymbology( String symbologyToFind )
  {
 	  String aSupportedSymbology = null;
 	  String foundSymbology = null;
 	  XyBarcodeGenerator foundBarcodeGenerator = null;
 	  XyBarcodeGenerator aBarcodeGenerator = null;
 	  
    int numberOfSymbologiesSeenSoFar = 0;
 	  
 	  //look through the list of barcode generators
 	  for (int j = 0; j < barcodeGenerators.size(); j++)
	  {
 	  	// for each barcode generator, look through its list of supported symbologies
 	  	// and see if the symbologyToFind is in this list
 	  	
 		  aBarcodeGenerator = ((XyBarcodeGenerator) barcodeGenerators.get(j));
	    Collection<String> supportedSymbologies 
	      = aBarcodeGenerator.getSupportedSymbologies();

	    int numberOfSupportedSymbologies = supportedSymbologies.size();

	    
	    Iterator<String> i = supportedSymbologies.iterator();
	    aSupportedSymbology = i.next();
	      
	    boolean lookedAtLastOne = false;
	    
	    do
	    {
	      if ( !(i.hasNext()) )
	      {
	        lookedAtLastOne = true;  
	      }
	      
				if (aSupportedSymbology != null)
				{
				  numberOfSymbologiesSeenSoFar++;
					if (StringUtils.containsIgnoreCase(aSupportedSymbology,
					    symbologyToFind))
					{
						foundSymbology = aSupportedSymbology;
						foundBarcodeGenerator = aBarcodeGenerator;
					}
				} // if (aSupportedSymbology != null)
				
				if ( !( lookedAtLastOne ) )
				{
				  aSupportedSymbology = i.next();
				}
			}
	    while(  (!lookedAtLastOne) && foundSymbology == null );
	    
	    // for loop to look through a generator's supported symbologies
		} // for loop to look through the list of barcode generators

 	  // this may return null: it is up to the client calling code to decide if
 	  // this is an error, or can be ignored
	  return foundBarcodeGenerator;
  }
}
