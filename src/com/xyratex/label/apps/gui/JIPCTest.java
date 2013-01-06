package com.xyratex.label.apps.gui;

import java.io.File;
import java.io.Serializable;

import org.apache.commons.io.FileUtils;
import org.garret.jipc.*;
import org.garret.jipc.client.JIPCClientFactory;

public class JIPCTest { 
    public static void main(String args[]) throws Exception { 
        String hostName = System.getProperty("host", "localhost");
	int    port = Integer.parseInt(System.getProperty("port", "6000"));

	
	
	
	      System.out.println("hostName =" + hostName);
	
        JIPCFactory factory = org.garret.jipc.client.JIPCClientFactory.getInstance();
        JIPCSession session = factory.create("",0);
        

 
        

        JIPCQueue queue = session.createQueue("test");
        
        /*
         * 
         \\ServerName\pipe\PipeName

where ServerName is either the name of a remote computer or a period, to specify the local computer. The pipe name string specified by PipeName can include any character other than a backslash, including numbers and special characters. The entire pipe name string can be up to 256 characters long. Pipe names are not case-sensitive.

The pipe server cannot create a pipe on another computer, so CreateNamedPipe must use a period for the server name, as shown in the following example.

\\.\pipe\PipeName
         * 
         */ 
        
        
        File file = new File("\\\\.\\pipe\\test");
        
        FileUtils.writeStringToFile(file, "hello");

        Serializable s = queue.get();
        
        System.out.println( s.toString() );
       
        
  /*
	JIPCMutex   mutex = session.createMutex("my-mutex", false);
	mutex.lock();
	// do something
	mutex.unlock();
   */
        
        

	session.close();
    }
}
