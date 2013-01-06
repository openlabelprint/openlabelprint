package com.xyratex.label.database;

import java.util.Vector;

import org.w3c.dom.Document;

public class XyLabelRepositoryManager implements XyLabelRepository
{
  
  private Vector serverRepositories = new Vector();
  
  /* (non-Javadoc)
   * @see com.xyratex.label.database.XyLabelRepository#getTemplate(java.lang.String)
   */
  public Document getTemplate( String search )
  {
    return null;
  }
  
  /* (non-Javadoc)
   * @see com.xyratex.label.database.XyLabelRepository#getPopulator(java.lang.String)
   */
  public Document getPopulator( String search )
  {
    return null;
  }
  
  /* (non-Javadoc)
   * @see com.xyratex.label.database.XyLabelRepository#getListOfTemplates()
   */
  public String getListOfTemplates()
  {
    return "";
  }
  
  /* (non-Javadoc)
   * @see com.xyratex.label.database.XyLabelRepository#getNumberOfTemplates()
   */
  public int getNumberOfTemplates()
  {
    return 0;
  }
  
  /* (non-Javadoc)
   * @see com.xyratex.label.database.XyLabelRepository#addLabelRepository(java.lang.String)
   */
  public void addLabelRepository( String url )
  {
    // format of store command
    // http://server/path
  }
  
  /* (non-Javadoc)
   * @see com.xyratex.label.database.XyLabelRepository#removeLabelRepository(java.lang.String)
   */
  public void removeLabelRepository( String url )
  {
    
  }
  
  /* (non-Javadoc)
   * @see com.xyratex.label.database.XyLabelRepository#recordPrintedLabel(org.w3c.dom.Document, java.lang.String)
   */
  public void recordPrintedLabel( Document labelPopulator, String id )
  {
    
  }
  
  /* (non-Javadoc)
   * @see com.xyratex.label.database.XyLabelRepository#recordTemplate(java.lang.String, org.w3c.dom.Document)
   */
  public void recordTemplate( String id, Document template )
  {
    
  }
  
  public boolean templateAlreadyExistsOnRepository( String id )
  {
    return false;
  }
  
}
