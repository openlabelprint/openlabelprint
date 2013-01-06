package com.xyratex.net;

import java.io.File;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;

import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;

import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.util.EntityUtils;

public class XyHttpClient
{
  
  public static String MIMETYPE_XML = "text/xml";
  
  public String fetchContentAtUrl( String urlAsString )
  {
   String responseAsString = "";
    
    HttpClient httpclient = new DefaultHttpClient();
    HttpGet httpget 
      = new HttpGet( urlAsString );
    HttpResponse response = null;
    try
    {
      response = httpclient.execute(httpget);
      
      if ( response != null )
      {  
        HttpEntity entity = response.getEntity();
        InputStream instream = entity.getContent();
        
        int val = 0;

        val = instream.read();
        while ( val != -1 )
        {
          responseAsString += (char)val;
          val = instream.read();
        }
      }
    }
    catch (ClientProtocolException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    catch (IOException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }


    //System.out.println( "RESPONSE: [" + responseAsString + "] RESPONSE END" );
    
    // When HttpClient instance is no longer needed, 
    // shut down the connection manager to ensure
    // immediate deallocation of all system resources
    httpclient.getConnectionManager().shutdown();
    
    return responseAsString;
    
  }
  
  
  /*
  public void formPostFileToServerUrl( File file, String url ) throws Exception 
  {
    HttpClient httpclient = new DefaultHttpClient();

    HttpPost httppost = new HttpPost( url );

    //InputStreamEntity reqEntity = new InputStreamEntity(
    //        new FileInputStream(file), -1);
    //reqEntity.setContentType("binary/octet-stream");
    //reqEntity.setChunked(true);
    
    
    
    // It may be more appropriate to use FileEntity class in this particular 
    // instance but we are using a more generic InputStreamEntity to demonstrate
    // the capability to stream out data from any arbitrary source
    // 
    // FileEntity entity = new FileEntity(file, "binary/octet-stream"); 
    
    FileEntity fileEntity = new FileEntity(file, "binary/octet-stream"); 
    
    fileEntity.setChunked(true);
  

    
    BasicHttpParams formInputs = new BasicHttpParams();
    
    
    formInputs.setParameter( "machineid", "robtest" );
    
    formInputs.setParameter( "filename", "afile.svg" );
    
    httppost.setParams(formInputs);
    
    httppost.setEntity(fileEntity);

  
    
    
    System.out.println("executing request " + httppost.getRequestLine());
    HttpResponse response = httpclient.execute(httppost);
    HttpEntity resEntity = response.getEntity();

    System.out.println("----------------------------------------");
    System.out.println(response.getStatusLine());
    if (resEntity != null) {
        System.out.println("Response content length: " + resEntity.getContentLength());
        System.out.println("Chunked?: " + resEntity.isChunked());
    }
    if (resEntity != null) {
        resEntity.consumeContent();
    }

    // When HttpClient instance is no longer needed, 
    // shut down the connection manager to ensure
    // immediate deallocation of all system resources
    httpclient.getConnectionManager().shutdown();        
  }
  */
  
  
  public void formPostFileToServerUrl( File file, String mimeType, String urlAsString ) throws Exception 
  {
    HttpClient httpclient = new DefaultHttpClient();
    httpclient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

    // httppost = new HttpPost("http://localhost:9001/upload.php");
    HttpPost httppost = new HttpPost( urlAsString );
    //File file = new File("c:/TRASH/zaba_1.jpg");

    MultipartEntity mpEntity = new MultipartEntity();
    ContentBody cbFile = new FileBody(file, mimeType );
    mpEntity.addPart("file", cbFile);

    httppost.setEntity(mpEntity);
    
    System.out.println("executing request " + httppost.getRequestLine());
    HttpResponse response = httpclient.execute(httppost);
    HttpEntity resEntity = response.getEntity();

    System.out.println(response.getStatusLine());
    if (resEntity != null) {
      System.out.println(EntityUtils.toString(resEntity));
    }
    if (resEntity != null) {
      resEntity.consumeContent();
    }

    httpclient.getConnectionManager().shutdown();
  }
  
  
  
}
