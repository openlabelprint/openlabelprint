package com.xyratex.label.output;

import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.batik.dom.svg.SAXSVGDocumentFactory;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.util.XMLResourceDescriptor;
import org.jaxen.JaxenException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGDocument;

import com.xyratex.label.template.exception.XyExceptionElementNotFoundInTemplate;
import com.xyratex.svg.batik.XyBatikWrapper;
import com.xyratex.svg.batik.util.XyShapeProducer;
import com.xyratex.svg.rasterize.XySVGtoBitmapProducer;
import com.xyratex.xml.io.XyDocumentProducer;
import com.xyratex.xml.utils.XyXMLDOMSearchUtils;

public class XyBitmapProducer
{
  
  public static void generateBitmap(final File svgFile, final File bitmapOutput, final String resolution ) 
  throws IllegalAccessException,
         InstantiationException,
         FileNotFoundException,
         ClassNotFoundException,
         TranscoderException,
         IOException,
         XyExceptionElementNotFoundInTemplate,
         JaxenException
{
  Document doc = XyDocumentProducer.getDocument( "svg", svgFile );
  
  generateBitmap( doc, bitmapOutput, resolution );
}


/**
 * <p>Generates the bitmap from SVG.</p>
 * 
 * <p>
 * Convenience method that allows a client, which already has the SVG in W3C DOM Document form,
 * to pass this in. This therefore enables performance efficiency as the SVG does not have to be reparsed
 * from an input File or String, for example.
 * </p>
 * 
 * @param doc (input parameter) the input File - the SVG format, a .svg file
 * @param bitmapOutput (input parameter) the File as a bitmap, a .png file 
 * @param resolution (input parameter) the resolution to render at,
 *   e.g. dots per inch (dpi) of the the PNG, e.g. 203, 300, 600 etc. as a String
 *   or dots per millimetre (dpmm). The value is supplied as a string because all precisions 
 *   can be supported flexibly.
 */
public static void generateBitmap(final Document doc, final File bitmapOutput, final String resolution )
  throws FileNotFoundException,
         IOException,
         TranscoderException,
         XyExceptionElementNotFoundInTemplate,
         JaxenException
{
  //OutputStream ostream = new FileOutputStream(bitmapOutput);
  
  OutputStream fileOutputStream = new FileOutputStream(bitmapOutput);
  
  generateBitmap( doc,  fileOutputStream,  resolution );
  
  fileOutputStream.flush();
  
  fileOutputStream.close();
  
}

/**
 * <p>Generates the bitmap from SVG.</p>
 * 
 * <p>Convenience method that provides the generated bitmap as an OutputStream
 * which allows the client calling code to reuse the same bitmap in multiple ways without
 * the overhead of rasterizing each time, for example for printing, display and writing to a file.
 * </p>
 *
 * @param doc (input parameter) the label template document as a W3C DOM Document
 * @param ostream (input/output parameter) the empty OutputStream, created by the calling code, that will be populated with the bitmap
 * @param res (input) the resolution (e.g. dpi, dpmm)
 */
public static void generateBitmap(final Document doc, OutputStream ostream, final String res ) 
  throws TranscoderException,
         IOException,
         XyExceptionElementNotFoundInTemplate,
         JaxenException
{
  generateBitmap( doc, ostream, XySVGtoBitmapProducer.getMillimetresPerPixelFromDotsPerUnitLength(res) );
}

/**
 * <p>Generates the bitmap from SVG.</p>
 * 
 * <p>Generates a bitmap from a rectangular region of a label template Document where
 * the region is defined by the rect SVG element with id defined in XyLabelTemplateProducer.XYPRINTABLE_REGION.
 * This rect element is inserted by the XyLabelTemplateProducer label template generator.</p>
 * 
 * @param doc (input parameter) the label template document as a W3C DOM Document
 * @param ostream (input/output parameter) the empty OutputStream, created by the calling code, that will be populated with the bitmap
 * @param millimetresPerPixel (input) the resolution in millimetres per pixel
 * 
 * @throws XyExceptionElementNotFoundInTemplate if such <rect> is not defined.
 */
public static void generateBitmap( 
    final Document doc,
    OutputStream ostream,
    final float millimetresPerPixel ) 
  throws TranscoderException,
         IOException,
         XyExceptionElementNotFoundInTemplate,
         JaxenException
{

  try
  {
    
  //File svgFile = new File( "C:\\a011322 ibm 2u12 chassis combi label rd01.svg" );
    File svgFile = new File( "C:\\Thunderbird_justlabel_template.svg" );
  

  

  
  // parse in the SVG document as a file, using the batik SVG/XML parser
  final SAXSVGDocumentFactory docFactory = new SAXSVGDocumentFactory(XMLResourceDescriptor.getXMLParserClassName());
  final SVGDocument svgdoc = docFactory.createSVGDocument(svgFile.toURI().toString());

  // gain access to the graphical shape and co-ordinate space view of the SVG document,
  // by instantiating the batik SVG DOM and CSS libraries
  final XyBatikWrapper batik = new XyBatikWrapper( svgdoc  );
  
  String anyElement = "*";
  
  final Element labelOutlineElement = XyXMLDOMSearchUtils.getElementByPartialAttributeValueMatch( "XYLABEL_OUTLINE", "id", anyElement, svgdoc );
  
  final Shape clipOutline =  XyShapeProducer.createShape(labelOutlineElement, batik.getBridgeContext());
  
  String xAsString = labelOutlineElement.getAttribute("x");
  String yAsString = labelOutlineElement.getAttribute("y");
  String widthAsString = labelOutlineElement.getAttribute("width");
  String heightAsString = labelOutlineElement.getAttribute("height");
  
  System.out.println(  "x = \n" + xAsString +
                       "y = " + yAsString +
                       "width = " + widthAsString +
                       "height = " + heightAsString +
                       "\n" ); 
  
  
  
  final Rectangle2D viewportRectangle = clipOutline.getBounds2D();
  
  float mmx = new Float( viewportRectangle.getX() ).floatValue();
  float mmy = new Float( viewportRectangle.getY() ).floatValue();
  float mmwidth = new Float( viewportRectangle.getWidth() ).floatValue();
  float mmheight = new Float( viewportRectangle.getHeight() ).floatValue();
  
  XySVGtoBitmapProducer.generateBitmap(svgdoc, ostream, millimetresPerPixel, mmx, mmy, mmwidth, mmheight);
  
  }
  catch( Exception e )
  {
    e.printStackTrace();
  }
  

  
}
  
}
