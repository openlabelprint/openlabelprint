//-------------------------------------------------------------------------
// (C) COPYRIGHT Xyratex Storage Systems Division 2011
// All Rights Reserved
//
// Filename    : XyVersion.java
// Author      : Rob Davis


// By          : Rob Davis
// File Type   : Java source code for Open Label Print
//-------------------------------------------------------------------------

package com.xyratex.label.config;

/**
 * <p>A centralised place to hold the version across the whole system.
 * Applications, code, queries this to determine the overall version.
 * If any single file changes, then this overall version must be upissued</p>
 * 
 * <p>This is a singleton, stateless class that does not require instantiation</p>
 * 
 * @author rdavis
 *
 */
final public class XyVersion
{
	/**
	 *  R = Release L = Level G = date U = time
	 *  Values calculated by SCCS when file is checked out and compiled.
	 */
  public static final String sccsid = "@(#)Xyratex  ISTP  XyVersion.java  %R%.%L%, %G% %U%";

	 /**
	  * Efficiently provide public read only access to the overall version.
	  */
	public static final String OLP_VERSION = "20080924"; 
	
	/**
	 * <p>The serialVersionUID is calculated by multiplying the SCCS
	 * release by this value and adding the level.</p>
	 */
	public static final long sccsReleaseMultiplier = 0x1000;
}
