//-------------------------------------------------------------------------
// (C) COPYRIGHT Xyratex Storage Systems Division 2011
// All Rights Reserved
//
// Filename    : XyExceptionElementNotFoundInTemplate.java
// Author      : Rob Davis


// By          : Rob Davis
// File Type   : Java source code for Open Label Print
//-------------------------------------------------------------------------

package com.xyratex.label.template.exception;

import org.apache.commons.lang.math.NumberUtils;

import com.xyratex.label.config.XyVersion;

/**
 * Thrown when element does not exist in label template
 * 
 * @author rdavis
 *
 */
public class XyExceptionElementNotFoundInTemplate extends Exception
{
	/**
	 *  <p>R = Release L = Level G = date U = time</p>
	 * 
	 */
	public static final String sccsid = "@(#)Xyratex  ISTP  XyExceptionElementNotFoundInTemplate.java  %R%.%L%, %G% %U%";
	
	/**
	 * serialVersionUID required for this class because its necessary parent implements the Serializable interface.
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
	public static final String NOT_FOUND = "Element with tagid not found in template";
	
	/**
	 * Constructor for exception.
	 * 
	 * @param detail - the detail string describing the cause of the exception.
	 */
  public XyExceptionElementNotFoundInTemplate( String detail )
  {
  	super ( detail + "\n" + sccsid );
  }
}
