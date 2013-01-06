package com.xyratex.label.database;

import org.w3c.dom.Document;

public interface XyLabelRepository
{

  public abstract Document getTemplate(String search);

  public abstract Document getPopulator(String search);

  public abstract String getListOfTemplates();

  public abstract int getNumberOfTemplates();

  public abstract void addLabelRepository(String url);

  public abstract void removeLabelRepository(String url);

  public abstract void recordPrintedLabel(Document labelPopulator, String id);

  public abstract void recordTemplate(String id, Document template);
  
  public boolean templateAlreadyExistsOnRepository( String id );

}