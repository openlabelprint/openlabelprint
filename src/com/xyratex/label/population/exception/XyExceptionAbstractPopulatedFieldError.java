//-------------------------------------------------------------------------
// (C) COPYRIGHT Xyratex Storage Systems Division 2011
// All Rights Reserved
//
// Filename    : XyExceptionAbstractPopulatedFieldError.java
// Author      : Rob Davis


// By          : Rob Davis
// File Type   : Java source code for Open Label Print
//-------------------------------------------------------------------------


package com.xyratex.label.population.exception;

/**
 * <p>Abstract Exception from which concrete Exceptions relating to label population can subclass off.</p>
 * 
 * <p>The reason for this abstract exception is that enables catching off all exceptions
 * subclassing off it. This can make catching code simpler and slightly more generic where needed.
 * It might the case that common action needs to be taken for all subclasses of this exception but
 * also actions specific to each particular exception. This abstract exception aids reuse and identification
 * of common handling code.</p>
 * 
 * @author rdavis
 *
 */
public abstract class XyExceptionAbstractPopulatedFieldError extends Exception
{
	/**
	 *  R = Release L = Level G = date U = time
	 */
	public static final String sccsid = "@(#)Xyratex  ISTP  XyExceptionAbstractPopulatedFieldError.java  %R%.%L%, %G% %U%";
	
	/**
	 * Constructor for exception.
	 * 
	 * @param detail - the detail string describing the cause of the exception.
	 */
  public XyExceptionAbstractPopulatedFieldError( String detail )
  {
  	super ( detail + "\n" + sccsid );
  }
}
