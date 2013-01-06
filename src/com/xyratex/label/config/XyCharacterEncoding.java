//-------------------------------------------------------------------------
// (C) COPYRIGHT Xyratex Storage Systems Division 2011
// All Rights Reserved
//
// Filename    : XyCharacterEncoding.java
// Author      : Rob Davis


// By          : Rob Davis
// File Type   : Java source code for Open Label Print
//-------------------------------------------------------------------------

package com.xyratex.label.config;

/**
 * <p>A centralised place to hold the character encoding across the whole system.
 * Applications, code, queries this to determine the character encoding in use.
 * The purpose is to avoid compatibility and mismatch problems that can occur
 * if different character sets are used in different parts of the system
 * that are working on the same character based data.</p>
 * 
 * <p>This is a singleton, stateless class that does not require instantiation</p>
 * 
 * @author rdavis
 *
 */
final public class XyCharacterEncoding
{
	/**
	 *  R = Release L = Level G = date U = time
	 *  Values calculated by SCCS when file is checked out and compiled.
	 */
 public static final String sccsid = "@(#)Xyratex  ISTP  XyCharacterEncoding.java  %R%.%L%, %G% %U%";
	
 /**
  * Efficiently provide public read only access to the character set in use.
  */
 public static final String ENCODING = "UTF-8";
}
