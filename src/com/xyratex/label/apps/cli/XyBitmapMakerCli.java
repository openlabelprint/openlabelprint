package com.xyratex.label.apps.cli;

import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import java.io.OutputStream;

import org.apache.batik.dom.svg.SAXSVGDocumentFactory;

import org.apache.batik.util.XMLResourceDescriptor;

import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGDocument;

import com.xyratex.label.output.XyLabelBitmapProducer;
import com.xyratex.svg.batik.XyBatikWrapper;
import com.xyratex.svg.batik.util.XyShapeProducer;
import com.xyratex.svg.rasterize.XySVGtoBitmapProducer;

import com.xyratex.xml.utils.XyXMLDOMSearchUtils;

public class XyBitmapMakerCli
{

  /**
   * @param args
   */
  public static void main(String[] args)
  {
    // TODO Auto-generated method stub

    try
    {
      
    //File svgFile = new File( "C:\\a011322 ibm 2u12 chassis combi label rd01.svg" );
      File svgFile = new File( "C:\\output3.svg" );
    

    


    
    
    
    
        ByteArrayOutputStream ostream = new ByteArrayOutputStream();
    
    float millimetresPerPixel = XySVGtoBitmapProducer.getMillimetresPerPixelFromDpi(300);
    
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
    
    byte[] bitmapAsByteArray = ostream.toByteArray();


/*
    FileOutputStream fileOutputStream = new FileOutputStream(
        new File( "C:\\bitmapmakeroutput.png" ) );
*/
    
    FileOutputStream fileOutputStream = new FileOutputStream(
        new File( "C:\\output3.png" ) );

    
    fileOutputStream.write(bitmapAsByteArray);
    
    fileOutputStream.flush();
    
    fileOutputStream.close();
    
    }
    catch( Exception e )
    {
      e.printStackTrace();
    }
    
    
    /*
    public static void generateBitmap( final Document doc,
        OutputStream ostream,
        final float millimetresPerPixel,
        final float mmx,
        final float mmy,
        final float mmwidth,
        final float mmheight ) throws IOException, TranscoderException
   */
    
    
  }

}
