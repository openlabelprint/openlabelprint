package com.xyratex.label.apps.cli;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.xyratex.label.output.print.XyLabelPrintService;
import com.xyratex.label.output.print.XyLabelPrintServiceFactory;
import com.xyratex.label.output.print.offset.XyPrinterOffsetManager;

public class XyBitmapPrinterCli
{
  private static final Log log = LogFactory.getLog(XyBitmapPrinterCli.class);
  
  /**
   * @param args
   */
  public static void main(String[] args)
  {
    /*
    * <ul> CREME_EGG
    *  <li>-i the input file - a copy of a SVG label template - i.e. a label with the barcode fields empty</li>
    *  <li>-o the output file - a SVG file populated with the barcode</li>
    *  <li>-t the tag id, identifying the barcode field on the label where the barcode should be placed, e.g. BC_SN - for barcode serial number</li>
    *  <li>-n the number used for the barcode - e.g. a serial number DHSS50A12345678</li>
    * </ul> -s ean-8
    */
    
    //print( new File( "C:\\Thunderbird_justlabel_template.png" ), "ZM400" );
    
    /*
    printToFile( new File( "C:\\Thunderbird_justlabel_template_2.png" ),
                 "ZM400",
                 new File( "C:\\Thunderbird_justlabel_template_2.zpl" ) );
    */
    

    /*
    printToFile( new File( "Y:\\My Documents\\new ibm thunderbird\\tagged\\0951866 EXN3600 MODEL NUMBER LABEL VERSION 2_template2.png" ),
                   "ZM400",
                   new File( "Y:\\My Documents\\new ibm thunderbird\\tagged\\0951866 EXN3600 MODEL NUMBER LABEL VERSION 2_template2.zpl" ) );
    */
    
    //print( new File( "C:\\circle.png" ), "ZM400" );
    
    
    //print( new File( "C:\\Thunderbird_justlabel_template_causes_diagonal.png" ), "ZM400" );
    
    
    print( new File( "C:\\output4.png" ), "170Xi4" );
    
    // 0950824 IBM CHASSIS AGENCY LABEL RG20100517 ORIENTATIONVERTICALTOP template3_112 pct manual edit without 600dpi anti-aliasing present arial redef.png
    
    //print( new File( "Y:\\My Documents\\new ibm thunderbird\\tagged 600dpi\\0950824 IBM CHASSIS AGENCY LABEL\\0950824 600dpi.png" ), "ZDesigner ZM400 600 dpi (ZPL)" );
    
    //print( new File( "Y:\\My Documents\\new ibm thunderbird\\tagged 600dpi\\0950824 IBM CHASSIS AGENCY LABEL\\0950824 600dpi korean.png" ), "ZDesigner ZM400 600 dpi (ZPL)" );
    
    //print( new File( "Y:\\My Documents\\new ibm thunderbird\\tagged 600dpi\\0950824 IBM CHASSIS AGENCY LABEL\\0950824 IBM CHASSIS AGENCY LABEL RG20100517 ORIENTATIONVERTICALTOP template3_112 pct manual edit style manual edits.png" ), "ZDesigner ZM400 600 dpi (ZPL)" );
    
    print( new File( "Y:\\My Documents\\new ibm thunderbird\\tagged 600dpi\\0952340 75X120 CHASSIS ARTWORK\\output4b.png" ), "ZDesigner ZM400 600 dpi (ZPL)" );
    
    
    
    
    /*
    printToFile( new File( "C:\\2pixel.png" ),
                 "ZM400",
                 new File( "C:\\2pixel.zpl" ) );
*/
    /*
    printToFile( new File( "C:\\circle.png" ),
        "ZM400",
        new File( "C:\\circle.zpl" ) );
    */
  }
  
  public static void print( File bitmapFile, String printer )
  {
    log.trace("Printer:" + printer);
    
    XyLabelPrintService printService = XyLabelPrintServiceFactory.getPrintService( printer );
     
    try
    {
      printService.printBitmap( bitmapFile );
    }
    catch ( Exception e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  
  public static void printToFile( File bitmapFile, String printer, File outputFile )
  {
    log.trace("Printer:" + printer);
    
    XyLabelPrintService printService = XyLabelPrintServiceFactory.getPrintService( printer );
     
    try
    {
      printService.printToFileBitmap(bitmapFile, outputFile );
    }
    catch ( Exception e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  
}
