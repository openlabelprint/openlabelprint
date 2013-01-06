package com.xyratex.utils;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;


public class XyUtils
{
  /**
   * 
   * <p>Returns the line of a text file containing a String specified by the parameter keyword.
   * For example, consider the file:</p>
   * 
   * <pre>
   * line1: hello
   * line2: how are you?
   * line3: goodbye
   * </pre>
   * 
   * <p>If the String <b>are</b> is specified as the keyword, then line 2 is returned: <b>how are you?</b></p>
   * 
   * <p>This function reads the file line by line, rather than read the whole file into memory first.
   * Which means that any size of file can be read, regardless of the machine's memory size</p>
   * 
   * @param target_file
   * @param keyword
   * @return
   * @throws IOException
   */
  public static String getLineWithKeyword(String target_file, String keyword) throws IOException
  {
   
    /* credit:   
     * http://www.roseindia.net/java/beginners/java-read-file-line-by-line.shtml
     * read a file line-by-line
     */
    
    FileInputStream target_file_inputStream = new FileInputStream(target_file);
    // Get the object of DataInputStream
    DataInputStream in = new DataInputStream(target_file_inputStream);
    BufferedReader br = new BufferedReader(new InputStreamReader(in));
    String strLine;
    //Read File Line By Line
    boolean foundKeyword = false;
    while ( ((strLine = br.readLine()) != null) && foundKeyword == false )
    {
      if ( strLine.contains(keyword))
      {
        foundKeyword = true;
      }
    }
    //Close the input stream
    in.close();
    
    String returnVal = null;
    
    if ( foundKeyword )
    {
      returnVal = strLine;
    }
    else
    {
      returnVal = null;
    }
    
    return returnVal;
      
    /*
     alternatively http://www.java-tips.org/java-se-tips/java.io/how-to-read-a-string-line-by-line.html
     String str;
      
BufferedReader reader = new BufferedReader(
new StringReader(contentTextArea.getText()));
      
try {
while ((str = reader.readLine()) != null) {
              
        if (str.length() > 0) System.out.println(str.charAt(0));
              
      }

} catch(IOException e) {
e.printStackTrace();
} 
     */
  }
}
