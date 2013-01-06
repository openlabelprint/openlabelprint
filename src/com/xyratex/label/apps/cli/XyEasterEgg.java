package com.xyratex.label.apps.cli;


public class XyEasterEgg
{

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
    
    XyLabelSinglePopulatorCli.main( args );

  
  }

}
