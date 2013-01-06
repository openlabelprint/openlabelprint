/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.xyratex.label.apps.gui.components;


import java.io.File;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.w3c.dom.Document;

import com.xyratex.label.population.XyLabelPopulator;
import com.xyratex.net.XyHttpClient;
import com.xyratex.xml.io.XyDocumentProducer;

/**
 *
 * @author rdavis
 */
public class XyOLPStoreLabelLogic 
{
  
  private XyLoadLabelTemplateLogic loadLabelLogic = null;
  
  public void setLoadLabelLogic( XyLoadLabelTemplateLogic aLoadLabelLogic )
  {
    loadLabelLogic = aLoadLabelLogic;
  }
  
  public XyOLPStoreLabelLogic()
  {
    
  }
  
  

  
  /**
   * log object used for logging activity in the form of String/character messages for debug, diagnostic and analysis purposes.
   * The output destination for the log could be console output and/or file(s) depending on the user-definable overall configuration
   * of logs in the system.
   */
  private Log log = LogFactory.getLog(XyLoadLabelTemplateLogic.class);
  
  private JFrame parentFrame = null;
        

  
  
  //offsetTypeChooser = buildOffsetTypeChooser();
  
  /*
  // Set the JSVGCanvas listeners.
  svgCanvas.addSVGDocumentLoaderListener(new SVGDocumentLoaderAdapter() {
      public void documentLoadingStarted(SVGDocumentLoaderEvent e) {
          label.setText("Document Loading...");
      }
      public void documentLoadingCompleted(SVGDocumentLoaderEvent e) {
          label.setText("Document Loaded.");
      }
  });

  svgCanvas.addGVTTreeBuilderListener(new GVTTreeBuilderAdapter() {
      public void gvtBuildStarted(GVTTreeBuilderEvent e) {
          label.setText("Build Started...");
      }
      public void gvtBuildCompleted(GVTTreeBuilderEvent e) {
          label.setText("Build Done.");
          frame.pack();
      }
  });

  svgCanvas.addGVTTreeRendererListener(new GVTTreeRendererAdapter() {
      public void gvtRenderingPrepare(GVTTreeRendererEvent e) {
          label.setText("Rendering Started...");
      }
      public void gvtRenderingCompleted(GVTTreeRendererEvent e) {
          label.setText("");
      }
  });
  */
  
  
  public void setFrame( JFrame frame )
  {
      parentFrame = frame;
  }
  

  
  

  public void storeLabel()
  {

      XyHttpClient xyHttpClient = new XyHttpClient();
    
      
      File file = loadLabelLogic.getFile();
      
      try
      {
        xyHttpClient.formPostFileToServerUrl( 
          file,
          XyHttpClient.MIMETYPE_XML,
          "http://rockstar/cgi-bin/nsr_store_label.cgi" );
      }
      catch (Exception e)
      {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
  }
  


  
}
