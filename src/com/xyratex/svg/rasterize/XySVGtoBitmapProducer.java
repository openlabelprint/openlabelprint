//-------------------------------------------------------------------------
//(C) COPYRIGHT Xyratex Storage Systems Division 2011
//All Rights Reserved
//
//Filename    : XySVGtoBitmapProducer.java
//Author      : Rob Davis
//Version     : %R%.%L%
//Last Update : %G% at %U%
//By          : Rob Davis
//File Type   : Java source code for Open Label Print
//-------------------------------------------------------------------------


package com.xyratex.svg.rasterize;

import java.io.IOException;
import java.io.OutputStream;

import java.awt.Color;

import java.awt.geom.Rectangle2D;

import org.w3c.dom.Document;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.ImageTranscoder;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>Stateless singleton class that produces a monochrome, 1-bit-per-pixel, 
 * Portable Network Graphics .png bitmap from a SVG document.</p>
 * 
 * @author rdavis
 *
 */
public class XySVGtoBitmapProducer
{
	/**
	 *  R = Release L = Level G = date U = time
	 *  Values calculated by SCCS when file is checked out and compiled.
	 */
	public static final String sccsid = "@(#)Xyratex  ISTP  XySVGtoBitmapProducer.java  %R%.%L%, %G% %U%";
	
	/**
	 * log object used for logging activity in the form of String/character messages for debug, diagnostic and analysis purposes.
	 * The output destination for the log could be console output and/or file(s) depending on the user-definable overall configuration
	 * of logs in the system.
	 */
	private static Log log = LogFactory.getLog(XySVGtoBitmapProducer.class);
	
  /**
   * <p>Generates a bitmap from a region of a SVG Document, given rectangular bounds defined</p>
   * @param doc (input parameter) the document as a W3C DOM Document
   * @param ostream (input/output parameter) the empty OutputStream, created by the calling code, that will be populated with the bitmap
   * @param millimetresPerPixel (input) the resolution in millimetres per pixel
   * @param mmx (input) the x co-ord of the top left corner in mm (millimetres) of the region in the doc to rasterize
   * @param mmy (input) the y co-ord of the top left corner in mm of the region in the doc to rasterize
   * @param mmwidth (input) the width in mm 
   * @param mmheight (input) the height in mm
   */
  public static void generateBitmap( final Document doc,
  		                               OutputStream ostream,
  		                               final float millimetresPerPixel,
  		                               final float mmx,
  		                               final float mmy,
  		                               final float mmwidth,
  		                               final float mmheight ) throws IOException, TranscoderException
  {
  	log.trace(sccsid);
  	
		Rectangle2D areaOfInterest 
	  = new Rectangle2D.Float
	  (
	  	mmx,
	  	mmy,
	  	mmwidth,
	  	mmheight
	  );
  	
    PNGTranscoder t = new PNGTranscoder();
    
		t.addTranscodingHint(ImageTranscoder.KEY_AOI, areaOfInterest);

    t.addTranscodingHint(ImageTranscoder.KEY_PIXEL_UNIT_TO_MILLIMETER, new Float(millimetresPerPixel));
    	
    // we want to remove millimetresInAnInch and dpi    
    //
    // width in millimeters, we want width in pixels
    // we know: how many millimetres there are per pixel
    // or in other words how long a pixel is on the printed surface
    //
    // so width in pixels is width in mm divided by the width of one pixel
    final float pixelWidthAsFloat = mmwidth / millimetresPerPixel;
    final float pixelHeightAsFloat = mmheight / millimetresPerPixel;
    
    t.addTranscodingHint(ImageTranscoder.KEY_WIDTH, new Float(pixelWidthAsFloat) );
    t.addTranscodingHint(ImageTranscoder.KEY_HEIGHT, new Float(pixelHeightAsFloat) );
    
    // label colour settings
    t.addTranscodingHint(ImageTranscoder.KEY_BACKGROUND_COLOR, Color.white);
   
    // bits per pixel
    final int BITS_PER_PIXEL = 1;
    t.addTranscodingHint( PNGTranscoder.KEY_INDEXED, new Integer(BITS_PER_PIXEL));
    
    log.trace("PNGTranscoder settings complete. Ready to begin rasterizing.");
    
    TranscoderInput input = new TranscoderInput(doc);
    // Create the transcoder output.
    
    log.trace("SVG input created");

    TranscoderOutput output = new TranscoderOutput(ostream);
    
    log.trace("Output stream created");

    // Save the image.
    
    log.trace("Start the rasterize");
    
    t.transcode(input, output);
    
    log.trace("Rasterize completed");

    // Flush and close the stream.
    ostream.flush();
    ostream.close();
    
    log.trace("Stream closed - should now contain bitmap - ready for use.");
  }

  /**
   * Conversion between imperial inches and metric/SI millimetres (millimeters - US)
   */
  public final static float millimetresInAnInch = 25.4f;

  /**
   * <p>Converts the value and units supplied by the calling client code
   * into the standardised millimetres per pixel that the batik rasterizer requires.</p>
   * 
   * @param dotsPerUnitLengthAsString 
   *   (input) the measurement value and its units of measurement. 
   *   e.g. 24dpmm (dots per millimetres), 300dpi (dots per inch).
   *   The value is supplied as a String as it is generic enough for any precision to be supported.
   * 
   * @return millimeters per pixel
   */
  public static float getMillimetresPerPixelFromDotsPerUnitLength( final String dotsPerUnitLengthAsString )
  {
        
            
  	float millimetresPerPixel = 0.0F;
  	
  	
  	
  	// dots per mm as used by zebra printers for example
  	if ( dotsPerUnitLengthAsString.contains("dpmm"))
  	{
            String dotsPerUnitLengthAsStringUnitsRemoved = dotsPerUnitLengthAsString.replace("dpmm", "");
            
                    int resAsInt = Integer.valueOf(dotsPerUnitLengthAsStringUnitsRemoved);
  		// dots per mm
  		millimetresPerPixel = getMillimetresPerPixelFromDpmm( resAsInt );
  	}
  	else // dpi
  	{
                        String dotsPerUnitLengthAsStringUnitsRemoved = dotsPerUnitLengthAsString.replace("dpi", "");
            int resAsInt = Integer.valueOf(dotsPerUnitLengthAsStringUnitsRemoved);
      millimetresPerPixel = getMillimetresPerPixelFromDpi( resAsInt );
  	}
  	
  	return millimetresPerPixel;
  }

  /**
   * <p>Calculates millimetres per pixel from a given dots per inch value:
   * i.e. millimetresInAnInch / dpi</p>
   * 
   * @param dpi (input) integer value for dots per inch
   * @return float value for millimetres per pixel
   */
  public static float getMillimetresPerPixelFromDpi( final int dpi )
  {
  	return millimetresInAnInch / dpi;
  }

  /**
   *  <p>Assuming a pixel is square, This method works out the length of the side of this square
   *  in millimetres from the number of such pixels that fit in a millimetre
   *  this is calculated by dividing 1.0 by the number of such pixels that fit in a millimetre.</p>
   */
  public static float getMillimetresPerPixelFromDpmm( final int dpmm )
  {
  	return 1.0F / dpmm;
  }
}
