/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.xyratex.label.apps.gui.components;


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
public class XyOLPRecallPrintedLabelLogic 
{
  
  public XyOLPRecallPrintedLabelLogic()
  {
    
  }
  
  

  
  /**
   * log object used for logging activity in the form of String/character messages for debug, diagnostic and analysis purposes.
   * The output destination for the log could be console output and/or file(s) depending on the user-definable overall configuration
   * of logs in the system.
   */
  private Log log = LogFactory.getLog(XyLoadLabelTemplateLogic.class);
  
  private JFrame parentFrame = null;
        
        private XyLabelTemplateImageLogic xyLabelTemplateImageLogic = null;
  
  
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
  

  
  public void setLabelImageLogic(XyLabelTemplateImageLogic labelImageLogic )
  {
      xyLabelTemplateImageLogic = labelImageLogic;
  }
  

  public void recallLabel()
  {

      XyHttpClient xyHttpClient = new XyHttpClient();
    
      String labelTemplateSVGAsString 
        = xyHttpClient.fetchContentAtUrl( "http://rockstar/nsrdata/printedlabels/template.svg" );

      String labelPopulatorAsString 
      = xyHttpClient.fetchContentAtUrl( "http://rockstar/nsrdata/printedlabels/populator.xml" );
      
      

      
      
      
      
      try
      {
        Document labelTemplate = XyDocumentProducer.getDocument( "svg", labelTemplateSVGAsString);

        final boolean wholeDocument = true;
        //Document copyOfLabelTemplate = labelTemplate.cloneNode( wholeDocument );
        
        if (labelTemplate == null)
        {
          final String badTemplate = "The label template is null - which is likely caused by invalid .svg file or some other problem with loading it.";

          JOptionPane.showMessageDialog(parentFrame, badTemplate);

          log.trace(badTemplate);
        }
        else
        { 
          
          /*
          XyLabelPopulator.populateEntireLabelToModuleWidth(
              copyOfLabelTemplate, currentLabelPopulatorDoc,
              moduleWidthArg, labelDpiAsString); 
          */
          
          
          
                                    xyLabelTemplateImageLogic.templateChanged( labelTemplate );
        //  notifyObservers( labelTemplate );
        }

        //labelAlignmentDisplay.setLabelTemplate(labelTemplate);

        //frame.setSize(panel.getSize());

        parentFrame.update(parentFrame.getGraphics());
      }
      catch (Exception ex)
      {
        ex.printStackTrace();
      }


  }
  


  
}
