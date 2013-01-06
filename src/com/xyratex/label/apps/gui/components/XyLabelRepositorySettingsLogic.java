package com.xyratex.label.apps.gui.components;

import java.io.File;

import com.xyratex.net.XyHttpClient;

public class XyLabelRepositorySettingsLogic
{
  
  public String labelRepository1BaseURL = "";
  public String labelRepository2BaseURL = "";
  
  public void setLabelRepository1BaseURL( String url )
  {
    labelRepository1BaseURL = url;
  }
  
  public void setLabelRepository2BaseURL( String url )
  {
    labelRepository2BaseURL = url;
  }
  
  public void storeLabel( File file ) throws Exception
  {
  
    XyHttpClient xyHttpClient = new XyHttpClient();
  
    xyHttpClient.formPostFileToServerUrl( 
      file, XyHttpClient.MIMETYPE_XML, labelRepository1BaseURL );
  
    xyHttpClient.formPostFileToServerUrl( 
      file, XyHttpClient.MIMETYPE_XML, labelRepository2BaseURL );
  }

}
