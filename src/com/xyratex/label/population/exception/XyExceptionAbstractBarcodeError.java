//-------------------------------------------------------------------------
// (C) COPYRIGHT Xyratex Storage Systems Division 2011
// All Rights Reserved
//
// Filename    : XyExceptionAbstractBarcodeError.java
// Author      : Rob Davis


// By          : Rob Davis
// File Type   : Java source code for Open Label Print
//-------------------------------------------------------------------------

package com.xyratex.label.population.exception;

public abstract class XyExceptionAbstractBarcodeError extends XyExceptionAbstractPopulatedFieldError
{
	/**
	 *  R = Release L = Level G = date U = time
	 */
	public static final String sccsid = "@(#)Xyratex  ISTP  XyExceptionAbstractBarcodeError.java  %R%.%L%, %G% %U%";
	
	/**
	 * Constructor for exception.
	 * 
	 * @param detail - the detail string describing the cause of the exception.
	 */
  public XyExceptionAbstractBarcodeError( String detail )
  {
  	super ( detail + "\n" + sccsid );
  }
}
