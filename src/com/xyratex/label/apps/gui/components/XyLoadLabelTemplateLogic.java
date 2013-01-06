package com.xyratex.label.apps.gui.components;

import java.awt.FileDialog;

import java.io.File;

import java.util.Observable;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;


import org.w3c.dom.Document;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.xyratex.xml.io.XyDocumentProducer;


public class XyLoadLabelTemplateLogic // extends Observable - disabled for now due to thread issues
{
	/**
	 * log object used for logging activity in the form of String/character messages for debug, diagnostic and analysis purposes.
	 * The output destination for the log could be console output and/or file(s) depending on the user-definable overall configuration
	 * of logs in the system.
	 */
	private Log log = LogFactory.getLog(XyLoadLabelTemplateLogic.class);
	
	private JFrame parentFrame = null;
        
        private XyLabelTemplateImageLogic xyLabelTemplateImageLogic = null;
	
        
  private File file = null;
	
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
	
  protected JButton loadTemplateButton = null;
	
  
  public XyLoadLabelTemplateLogic()
  {
  }
  
  public void setFrame( JFrame frame )
  {
      parentFrame = frame;
  }
  

  
  public void setLabelImageLogic(XyLabelTemplateImageLogic labelImageLogic )
  {
      xyLabelTemplateImageLogic = labelImageLogic;
  }
  

  public File getFile()
  {
    return file;
  }
  

  public void loadTemplate()
  {
    FileDialog fc = new FileDialog(parentFrame, "Choose a file", FileDialog.LOAD);
    
		fc.setDirectory("C:\\");

		log.trace("open file browser dialog" );
		fc.setVisible(true); // stops here until dialog is closed?
		log.trace("close file browser dialog" );
		
		// at this point, the template dialog is closed
		// and the user might have made a file selection
		
		String fn = fc.getFile();
		String fd = fc.getDirectory();

		log.trace(fd + " " + fn);

		if (fn != null)
		{
			// if there is a file selected then attempt to load it
			
			file = new File(fd + fn);

			try
			{
				Document labelTemplate = XyDocumentProducer.getDocument("svg", file);

				if (labelTemplate == null)
				{
					final String badTemplate = "The label template is null - which is likely caused by invalid .svg file or some other problem with loading it.";

					JOptionPane.showMessageDialog(parentFrame, badTemplate);

					log.trace(badTemplate);
				}
				else
				{
                                    xyLabelTemplateImageLogic.templateChanged( labelTemplate );
				//	notifyObservers( labelTemplate );
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
  


}
