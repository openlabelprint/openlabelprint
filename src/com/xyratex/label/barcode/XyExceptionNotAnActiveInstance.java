//-------------------------------------------------------------------------
// (C) COPYRIGHT Xyratex Storage Systems Division 2011
// All Rights Reserved
//
// Filename    : XyExceptionNotAnActiveInstance.java
// Author      : Rob Davis


// By          : Rob Davis
// File Type   : Java source code for Open Label Print
//-------------------------------------------------------------------------

package com.xyratex.label.barcode;

import org.apache.commons.lang.math.NumberUtils;

import com.xyratex.label.config.XyVersion;

/**
 * <p>This exception is raised when attempt to use a parent instance, as part of a Factory design pattern,
 * when what should happen is that this instance should be used to create a child instance which can
 * be used for the purpose it was intended.</p>
 * 
 * @author rdavis
 *
 */
public class XyExceptionNotAnActiveInstance extends Exception
{
	/**
	 *  R = Release L = Level G = date U = time
	 */
	public static final String sccsid = "@(#)Xyratex  ISTP  XyExceptionNotAnActiveInstance.java  %R%.%L%, %G% %U%";
	
	/**
	 * serialVersionUID required for this class.
	 * Its value is derived from SCCS Release and Level
	 * toLong() method won't throw an exception if non-numeric values are in the SCCS version data.
	 */
	public static final long serialVersionUID = NumberUtils.toLong("%R%") * XyVersion.sccsReleaseMultiplier 
	                                          + NumberUtils.toLong("%L%");
	
	/**
	 * String providing detail about the cause of the expection, used in creating exception.
	 * This is not mandatory; the code that creates and throws the exception can
	 * supply its own string, but having this string pre-defined saves time considering
	 * what to write as detail about the exception - and also encourages consistency
	 * if there are several situations where the exception is thrown.
	 */
	public static final String NOT_ACTIVE_INSTANCE = "This instance is not an active instance. It is a parent instance and should be used to create an active instance.";
	
	/**
	 * Constructor for exception.
	 * 
	 * @param detail - the detail string describing the cause of the exception.
	 */
  public XyExceptionNotAnActiveInstance( String detail )
  {
  	super ( detail + "\n" + sccsid );
  }
}
