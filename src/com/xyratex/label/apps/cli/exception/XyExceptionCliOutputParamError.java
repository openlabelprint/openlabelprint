//-------------------------------------------------------------------------
// (C) COPYRIGHT Xyratex Storage Systems Division 2011
// All Rights Reserved
//
// Filename    : XyExceptionCliOutputParamError.java
// Author      : Rob Davis


// By          : Rob Davis
// File Type   : Java source code for Open Label Print
//-------------------------------------------------------------------------

package com.xyratex.label.apps.cli.exception;

import org.apache.commons.lang.math.NumberUtils;

import com.xyratex.label.config.XyVersion;

/**
 * Thrown when no output device or file specified for applications that output to a file or printer.
 * 
 * @author rdavis
 *
 */
public class XyExceptionCliOutputParamError extends XyExceptionAbstractCliParamError
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
	 * String providing detail about the cause of the expection, used in creating exception.
	 * This is not mandatory; the code that creates and throws the exception can
	 * supply its own string, but having this string pre-defined saves time considering
	 * what to write as detail about the exception - and also encourages consistency
	 * if there are several situations where the exception is thrown.
	 */
	public static final String NO_OUTPUT_DEFINED = "No output device or file defined";
	
	/**
	 * String providing detail about the cause of the expection, used in creating exception.
	 * This is not mandatory; the code that creates and throws the exception can
	 * supply its own string, but having this string pre-defined saves time considering
	 * what to write as detail about the exception - and also encourages consistency
	 * if there are several situations where the exception is thrown.
	 */
	public static final String XML_OR_PNG_OUTPUT_FILE_NOT_DEFINED = "File to be created must either end with a .png or .xml which will define type of output.";
	
	/**
	 * Constructor for exception.
	 * 
	 * @param detail - the detail string describing the cause of the exception.
	 */
	public XyExceptionCliOutputParamError(String detail)
	{
		super(detail + "\n" + sccsid );
	}
}