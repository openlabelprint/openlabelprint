//-------------------------------------------------------------------------
// (C) COPYRIGHT Xyratex Storage Systems Division 2011
// All Rights Reserved
//
// Filename    : XyExceptionAbstractCliParamError.java
// Author      : Rob Davis


// By          : Rob Davis
// File Type   : Java source code for Open Label Print
//-------------------------------------------------------------------------

package com.xyratex.label.apps.cli.exception;

import org.apache.commons.lang.math.NumberUtils;

import com.xyratex.label.config.XyVersion;

/**
 * Abtract Exception class for incorrect parameters at command line.
 * Purpose: enables all subclasses to be caught and handled in the same manner, if that is what is required.
 * 
 * @author rdavis
 *
 */
public class XyExceptionAbstractCliParamError extends Exception
{
	/**
	 *  <p>R = Release L = Level G = date U = time</p>
	 * 
	 */
	public static final String sccsid = "@(#)Xyratex  ISTP  XyExceptionAbstractCliParamError.java  %R%.%L%, %G% %U%";
	
	/**
	 * serialVersionUID required for this class because its necessary parent implements the Serializable interface.
	 * Its value is derived from SCCS Release and Level
	 * toLong() method won't throw an exception if non-numeric values are in the SCCS version data.
	 */
	public static final long serialVersionUID = NumberUtils.toLong("%R%") * XyVersion.sccsReleaseMultiplier 
	                                          + NumberUtils.toLong("%L%");
	
	/**
	 * Constructor for exception.
	 * 
	 * @param detail - the detail string describing the cause of the exception.
	 */
	public XyExceptionAbstractCliParamError(String detail)
	{
		super(detail + "\n" + sccsid );
	}
}
