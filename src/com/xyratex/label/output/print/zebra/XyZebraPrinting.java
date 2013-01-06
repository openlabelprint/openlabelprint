//-------------------------------------------------------------------------
// (C) COPYRIGHT Xyratex Storage Systems Division 2011
// All Rights Reserved
//
// Filename    : XyZebraPrinting.java
// Author      : Rob Davis


// By          : Rob Davis
// File Type   : Java source code for Open Label Print
//-------------------------------------------------------------------------

package com.xyratex.label.output.print.zebra;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.print.PrintException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.xyratex.label.output.print.XyBasicPrinterCommunication;
import com.xyratex.label.output.print.XyBasicPrinterCommunicationFactory;
import com.xyratex.label.output.print.XyCompositeLabel;
import com.xyratex.label.output.print.XyLabelPrintService;
import com.xyratex.label.output.print.XyPrinterResponseListener;

/**
 * <p>Printer driver for the Zebra printer family that uses the Zebra Printing Language command set.</p>
 * 
 * <p>Accepts a PNG bitmap and sends it to the Zebra printer as a sequence of ZPL commands.</p>
 * 
 * @author rdavis
 *
 */
public class XyZebraPrinting implements XyLabelPrintService
{
	/**
	 *  R = Release L = Level G = date U = time
	 */
	public static final String sccsid = "@(#)Xyratex  ISTP  XyZebraPrinting.java  %R%.%L%, %G% %U%";
	
	/**
	 * log object used for logging activity in the form of String/character messages for debug, diagnostic and analysis purposes.
	 * The output destination for the log could be console output and/or file(s) depending on the user-definable overall configuration
	 * of logs in the system.
	 */
	private final Log log = LogFactory.getLog(XyZebraPrinting.class);
	
	/**
	 * ZPL command sequence for deleting a bitmap image from the printer memory
	 */
	private static final String deleteGraphicFromPrinterCommandString = "XA^IDR:LABEL.GRF^FS^XZ";
	
	/**
	 * Darkness command absent by default. Set by client calling code if required.
	 */
	private String setDarknessCommand = "";
	
	/**
	 * Origin command absent by default. Set by client calling code if required.
	 */
	private String setOriginCommand = "";
	
	/**
	 * Initialised on construction.
	 */
	private XyBasicPrinterCommunication printerDriver = null;
	
	/**
	 * Top left x offset co-ordinate. 
	 * How far in pixels, horizontally, the image will be printed from the top left corner
	 */
	private int xCoordAsInt = 0;
	
	/**
	 * Top left y offset co-ordinate	 
	 * How far in pixels, vertically, the image will be printed from the top left corner 	
	 */
	private int yCoordAsInt = 0;
	
	/**
	 * <p>Constructs a XyZebraPrinting object that is used to send label graphics to the printer
	 * specified in printerName parameter.</p>
	 * 
	 * <p>This object should not be contructed directly by the client calling code,
	 * rather, the XyLabelPrintServiceFactory should be used.</p>
	 * 
	 * @param printerName (input) the id of the printer attached to the host PC. This can be a partial (but must be unique) match.
	 * @see com.xyratex.label.output.printXyLabelPrintServiceFactory
	 */
	public XyZebraPrinting( final String printerName )
	{
		log.trace( XyLabelPrintService.sccsid // the implemented interface version
				       + "\n" + sccsid ); // output the version of the class
		
		printerDriver = XyBasicPrinterCommunicationFactory.getPrinterCommunication(printerName);
	}
	
	/**
	 * Get the full name of the printer as a String.
	 */
	public String getPrinterName()
	{
		return printerDriver.getPrinterName();
	}
	
	/**
	 * Set the top-left offset - how far from the top-left corner the image will be printed.
	 */
	public void setOrigin( final String originString )
	{
		final String[] coords = StringUtils.split(originString, "," );
		
		String xcoord = coords[0];
		String ycoord = coords[1];
		
		if (    xcoord.contains("+") || xcoord.contains("-") 
				 || ycoord.contains("+") || ycoord.contains("-") )
		{
			// relative offset
			setOriginCommand = "^FO";
		}
		else
		{
			// absolute origin, changing the printer settings
			setOriginCommand = "^LH";
		}
		
		// parseInt throws an exception on discovering a '+' so this needs to be removed
		// but its only explictly stating the sign of the value - i.e. positive so parseInt ought to be more tolerant
		xcoord = xcoord.replace("+", "");
		ycoord = ycoord.replace("+", "");
		
		xCoordAsInt = Integer.parseInt(xcoord);
		yCoordAsInt = Integer.parseInt(ycoord);
		
		setOriginCommand += xCoordAsInt + "," + yCoordAsInt;
	}
	
	/**
	 * <p>Print the bitmap from a .png File.
	 * A convenience method that means that the client calling code only has to know
	 * about File objects and the location of the .png file.</p>
	 * @param png (input parameter) the png file as represented by a File object
	 */
	public void printBitmap( final File png ) throws PrintException, IOException
	{	
		if ( png.getName().contains(".png") )
		{
		  BufferedImage pngAsBufferedImage = ImageIO.read(png);

			byte[] command = ArrayUtils.addAll(
			    getDownloadAndPrintGraphicCommand(pngAsBufferedImage),
			    deleteGraphicFromPrinterCommandString.getBytes());

			printerDriver.sendToPrinter(command);
		}
	}
	
	 public void printToFileBitmap( final File png, File output ) throws PrintException, IOException
	  { 
	    if ( png.getName().contains(".png") )
	    {
	      BufferedImage pngAsBufferedImage = ImageIO.read(png);

	      byte[] command = ArrayUtils.addAll(
	          getDownloadAndPrintGraphicCommand(pngAsBufferedImage),
	          deleteGraphicFromPrinterCommandString.getBytes());
	      
	      FileUtils.writeByteArrayToFile(output, command);
	    }
	  }
	
	/**
	 * <p>Print the bitmap from an InputStream.
	 * A convenience method where the client code will have a
	 * bitmap in the form of an InputStream - which is a flexible structure that
	 * can be used more than once for different output destinations: e.g. Files,
	 * screen and printers. This is beneficial in the case where the client wants
	 * to use the same bitmap more than once as performance and memory savings
	 * are achieved.</p>
	 * @param is (input parameter) the InputStream containing the .png bitmap */
	public void printBitmap( final InputStream is ) throws IOException, PrintException
	{
		final BufferedImage pngAsBufferedImage = ImageIO.read( is );
		
		final byte[] command = ArrayUtils.addAll( getDownloadAndPrintGraphicCommand( pngAsBufferedImage ),
				deleteGraphicFromPrinterCommandString.getBytes() );
		
		printerDriver.sendToPrinter(command);
	}
	
	
  /**
   * <p>Helper method that assembles header of ZPL commands that perform the printing of the bitmap.</p>
   * 
   * @param labelWidthInPixels (input) width of label in pixels
   * @return String containing the ZPL command header
   */
  private String getPrintBitmapCommandString( final int labelWidthInPixels )
  {
  	// the real label size told to the printer must account for any offset,
  	// otherwise the right hand edge gets increasingly clipped as the offset increases
  	final int labelWidthPlusXOffset = xCoordAsInt + labelWidthInPixels;
  	
  	final String cmdStr =   "^XA"
  	       + setDarknessCommand
  	       + "^PW" + labelWidthPlusXOffset 
  	       + setOriginCommand
  	       + "^XGR:LABEL.GRF,1,1^FS"
  	       + "^XZ";
  	
  	// reset setDarknessCommand and setOriginCommand for next time
  	// TODO: do we want to reset this? Perhaps this should be persistent
  	setDarknessCommand = "";
  	setOriginCommand = "";
  	
  	return cmdStr;
  }
  
	/**
	 * <p>Creates a ZPL format bitmap from the .png supplied as a BufferImage.</p>
	 *
	 * <p>The printer is monochrome, bilevel, only 2 values, 1 bit per pixel.</p>
	 * 
	 * <p>Pixels are grouped in groups of 8, so an 8 bit value (1 byte) represents 8 pixels.</p>
	 * 
	 * <p>The ZPL download graphic (DG) command represents the 8 pixel byte in its 2 character
	 * ascii hexadecimal form.</p>
	 * 
	 * <p>Examples:</p>
	 * <ol>
	 *  <li>A Completely black row of 8 pixels will mean each black pixel has a value of 0, zero.
	 *    Therefore 8 of them is 0,0,0,0,0,0,0,0 which is 00000000 binary which is 00 hex.
	 *  </li>
	 *  
	 *  <li>A completely white row of 8 pixels will mean each white pixel has a value of 1, one.
	 *		Therefore 8 of them is 1,1,1,1,1,1,1,1 which is 11111111 binary which is FF hex.
	 *	</li>
	 *
   *  <li>The system is big endian pixel organisation, e.g. 1,0,0,0,0,0,0,1 is one dot at either
   * end and is represented as 81 in hex
   *  </li>
   * </ol>
   * 
   * <p>So when we are reading the row of pixels from a bitmap file, we need to process them
	 * in chunks of 4 pixels in a row at a time, in order to output a hex value.</p>
	 * 
	 * <p>We need to be able to assemble the hex value and to do this we need to represent each pixel
	 *  in the hex value.</p>
	 *  
	 * @param pngAsBufferedImage
	 * @return the byte array of the bitmap in ZPL DG command format
	 * @throws IOException
	 */
	public byte[] getDownloadAndPrintGraphicCommand( BufferedImage pngAsBufferedImage ) throws IOException
	{
		int heightInPixels = pngAsBufferedImage.getHeight();
		log.trace( "height = " + heightInPixels );
		
		int widthInPixels = pngAsBufferedImage.getWidth();
		log.trace( "width = " + widthInPixels );
		
		// in zpl there are 8 pixels per byte and a byte is represented as 2 ascii chars
		// which are sent to the printer, therefore there are four pixels represented by each ascii char
		// that is sent to the printer
		final int zplHexCharIs4Pixels = 4;
		
		// this represents the threshold, upon crossing to a greater value, the pixel is considered white
		// the visible colour for this is mid-grey.
		// this simple approach to deciding on pixel being black (print a dot) or white (print nothing)
		// should be adequate enough for bi-level, 2 colour monochrome bitmaps whereby
		// the black is decidedly black and the value is well within that area (i.e. minimal value, furthest
		// away from the threshold and likewise the white is well within its area, i.e. maximum value
		// well away from the threshold also)
		final long c_thresholdWhite = 0x80;
				
		// work out the number of bytes per row DG command parameter value
		//
		// a byte is a pair of hexadecimal characters sent to the printer,
		// each char is an ascii hex char

    // some bitmaps horizontal dimension will not be aligned to be a multiple of the bytes per row
    // so some rounding up to alignment has to be done...
		//
		// define that there are 2 hex chars per byte
    final int zplByteIs2ZplHexChars = 2;
    //
    // ...first work out how many complete 4-pixel ascii hex chars are in the row of the bitmap
    int numComplete4PixelZplCharsInRow = widthInPixels / zplHexCharIs4Pixels;
    log.trace( "numComplete4PixelZplCharsInRow: " + numComplete4PixelZplCharsInRow );
    
    // now work out what the aligned, rounded up number of ascii hex chars will be
    //
    int alignedNum4PixelZplHexCharsPerRow = 0;
    int zplAlignedWidthInPixels = 0;
    //
    // the modulus is the remainder - the number of pixels that aren't enough
    // to make a complete zpl-4-pixel-hexchar
    //
    // for example for:
    // - a row of 101 pixels, the remainder is 1
    // - 102, remainder is 2
    // - 103, remainder is 3
    // - 104, remainder is 0 (there are 26 complete zpl-4-pixel-hexchars
    int remainingPixels = widthInPixels % zplHexCharIs4Pixels;
    log.trace( "remainingPixels: " + remainingPixels );
    
    // did we get a remainder?
    if ( remainingPixels == 0 )
    {
      // the number of pixels in the row of the bitmap exactly aligns to the 4-pixel multiple of
      // a ascii hex char to be sent to the printer
      // our number of 4 pixel hex chars per row exactly fits the pixel width of the bitmap
      alignedNum4PixelZplHexCharsPerRow = numComplete4PixelZplCharsInRow;
    }
    else
    {
      // the number of pixels in the row of the bitmap does not align to the 4-pixel multiple of
      // a ascii hex char to be sent to the printer
      // - we have a few remaining pixels at the end of the row that are less than 4
      // so for our number of 4 pixel hex chars per row, 
      // we first use the number of complete 4-pixel multiples in the row,
      // and for the remaining pixels we add 2 - to byte align the final value
      alignedNum4PixelZplHexCharsPerRow = ( numComplete4PixelZplCharsInRow ) + zplByteIs2ZplHexChars;
    }
    
    // do we have any odd hex chars - if so, we need to round up to align by byte (2-hex char pairs)
    int numOddZplHexChars = alignedNum4PixelZplHexCharsPerRow % zplByteIs2ZplHexChars;
    
    if ( numOddZplHexChars > 0 )
    {
      alignedNum4PixelZplHexCharsPerRow+=1;
    }
    
    zplAlignedWidthInPixels = alignedNum4PixelZplHexCharsPerRow * zplHexCharIs4Pixels;
    
    log.trace( "alignedNum4PixelZplHexCharsPerRow: " + alignedNum4PixelZplHexCharsPerRow );
    log.trace( "zplAlignedWidthInPixels: " + zplAlignedWidthInPixels );
      
    int alignedNumZplBytesPerRow = alignedNum4PixelZplHexCharsPerRow / zplByteIs2ZplHexChars;
    
    log.trace( "alignedNumZplBytesPerRow: " + alignedNumZplBytesPerRow );
    
		int zplSizeOfOutput = alignedNumZplBytesPerRow * heightInPixels;
		
		byte[] bitmapAsSequenceOfZplHexChars = new byte[ alignedNum4PixelZplHexCharsPerRow * heightInPixels ];
		
		int posInBitmapSequence = 0;
		
		for ( int yPixelPos = 0; yPixelPos < heightInPixels; yPixelPos++ )
		{
			for ( int xPixelPos = 0; xPixelPos < zplAlignedWidthInPixels; xPixelPos+= zplHexCharIs4Pixels )
			{
				// for each row, process 4 pixels in that row each time
				int zpl4PixelCharValue = 0; // assuming black = 0 // && (x+rowpos < width)
				for ( int posWithinZpl4PixelChar = 0; 
				      (posWithinZpl4PixelChar < zplHexCharIs4Pixels );
				      posWithinZpl4PixelChar++ )
				{
					// if the last set of pixels is less than 4 then we need to pad, instead of querying the png for pixel(s) that don't exist
					int rgb = 0;
					if ( (xPixelPos+posWithinZpl4PixelChar) >= widthInPixels )
					{
						rgb = 0;
					}
					else
					{
						rgb = pngAsBufferedImage.getRGB( xPixelPos+posWithinZpl4PixelChar, yPixelPos );
					}
					
					//String rgbAsHex = Integer.toHexString( rgb );
					//log.trace( "rgbAsHex = " + rgbAsHex );
					
					// use bitwise shifting to the right (big endian) to 
					// work out the decimal value for the bit position within the big endian 4-pixel char
					int bitPosAsDecimal = ( 1 << ((zplHexCharIs4Pixels - 1)  - posWithinZpl4PixelChar) );
					
					// 0x00rrggbb
					int rr = ( rgb >> 16 ) & 0xFF;
					int gg = ( rgb >> 8 ) & 0xFF;
					int bb = rgb & 0xFF;
					
					// a very basic method to get an intensity 
					// - take the average of the intensities for red, green and blue
					// 
					// this has to be good enough given that:
					// - any bitmap received is going to be monochrome anyway, so the red green and blue values will be the same
					// - this has to be a fast algorithm (we don't want to use some kind of spohisticated 
					// mathematical, compute intensive intensity algorithm) otherwise printing is delayed
					int averageIntensity = ( rr + bb + gg ) / 3;
					
					// if the average intensity is less than the white threshold,
					// then we consider it to be black
					// so we set the appropropriate bit in the 4-pixel value for the ZPL Ascii Hex Char
	        if ( averageIntensity < c_thresholdWhite )
          {
            zpl4PixelCharValue += bitPosAsDecimal;
          }
				} // for int rowpos...
				
				// convert value to int for conversion to hex routine but reset unused units to zero
				//int valueAsInt = 0xF & zpl4PixelCharValue;
				String zpl4PixelCharValueAsHex = Integer.toHexString( zpl4PixelCharValue );
	
				// the value returned by the to-hex routine will be a String
				// the routine does not pad the more significant bytes with zeros, so the 
				// first char in the string should be the value we want
				bitmapAsSequenceOfZplHexChars[posInBitmapSequence] = (byte)zpl4PixelCharValueAsHex.charAt(0);
				posInBitmapSequence++;
			} // for int xPixelPos...
		} // for int yPixelPos...

		byte[] header = getDownloadGraphicCommandheader( zplSizeOfOutput, alignedNumZplBytesPerRow ).getBytes();
		/* Just for information in CUPS code:
		 *  327 header->cupsHeight * header->cupsBytesPerLine,
     *  328 header->cupsBytesPerLine);
		 */
		
		return ArrayUtils.addAll( 
				ArrayUtils.addAll( header, bitmapAsSequenceOfZplHexChars ),
		  	getPrintBitmapCommandString( widthInPixels ).getBytes()
		  		 );
	}
  
  /**
   * Helper method providing header for the download graphic command
   */
  private static String getDownloadGraphicCommandheader( final int totalNumberBytesInGraphic, final int numberOfBytesPerRow )
  {
  	return "~DGR:LABEL.GRF," + totalNumberBytesInGraphic + "," + numberOfBytesPerRow + ",";
  }
	
  /**
   * <p>Not implemented yet. This will provide the ability to print the label bitmap together with
   * Variable fields such as barcodes generated by the printer itself. It is an alternative
   * option for generating labels if the host computer is not fast enough to generate barcodes in
   * the label image or if the printer supports the barcode but the host generating software does not.</p>
   */
	public void printComposite( XyCompositeLabel composite )
	{
		
	}
	
	/**
	 * <p>Add a listener to the response from the printer.
	 * Not implemented yet as we require any response from the printer at current implementation.
	 * Though it might be useful in future for detecting status, e.g. out-off-labels-roll, 
	 * print ribbon used up for example.
	 * Any handshaking is provided by the printer driver installed on the host computer
	 * operating system.
	 * </p>
	 */
	public void addListener( final XyPrinterResponseListener listener )
	{
		
	}
		
	/**
	 * <p>Set the darkness of the image printed. Very dark images tend to bleed and thin
	 * gaps disappear but the benefit is smoother curved edges on logos and symbols.</p>
	 */
	public void setDarkness( final String aDarkness )
	{	
		String darkness = aDarkness;
		
		if ( darkness.contains("+") || darkness.contains("-") )
		{
			// parseInt throws an exception on discovering a '+' so this needs to be removed
			// but its only explictly stating the sign of the value - i.e. positive so parseInt ought to be more tolerant
			darkness = darkness.replace("+", "");
			
			// clean up string to be pure numerical and use this
			float darknessAsFloat = Float.parseFloat(darkness);
			
			/* from the manual:
			 * The ^MD command adjusts the darkness relative to the current darkness setting.
			 */
			setDarknessCommand = "^MD" + darknessAsFloat;
		}
		else
		{
			// clean up string to be pure numerical and use this
			float darknessAsFloat = Float.parseFloat(darkness);
			
			/* from the manual:
			Description The ~SD command allows you to set the darkness of printing. ~SD is the
			equivalent of the darkness setting parameter on the control panel display.
			Format ~SD##
			*/
			setDarknessCommand = "~SD" + darknessAsFloat;
		}
		
		/* Notes from the manual
		Parameters Details
		## = desired
		darkness setting
		(two-digit
		number)

		Accepted Values: 00 to 30
		Default Value: last permanently saved value
		Example  These are examples of the XiIIIPlus Darkness Setting:
		^MD8.3
		~SD8.3
				 */
	}
}
