/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.xyratex.ui;

/**
 *
 * @author rdavis
 */
public interface XyProgressReporter {

    
      // can we use observable instead?
  public void setProgressListener( XyOperationProgressListener progressListener );
    
}
