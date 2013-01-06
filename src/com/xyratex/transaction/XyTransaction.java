//-------------------------------------------------------------------------
//(C) COPYRIGHT Xyratex Storage Systems Division 2011
//All Rights Reserved
//
//Filename    : XyTransaction.java
//Author      : Rob Davis
//Version     : %R%.%L%
//Last Update : %G% at %U%
//By          : Rob Davis
//File Type   : Java source code for Open Label Print
//-------------------------------------------------------------------------

package com.xyratex.transaction;

import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.xyratex.label.apps.cli.XyAbstractCli;
import com.xyratex.svg.batik.XyBatikWrapper;

/**
 * <p>This class provides a controlled means for separate software components, that together
 * produce output, to query each other's component output, before the final output is produced.
 * This enables validation and execution program logic required between the components to be
 * written once and re-used in many applications, rather than re-implementing each time.</p>
 * 
 * <p>A current specific example is the Open Label Print system, which uses this class to
 * store the barcodes for a label to be printed and apply scaling to them based on requirements of the
 * user. @see XyLabelSetProducer.</p>
 * 
 * <p>Future examples for the Open Label Print system: For example, this XyTransaction can provide a means to
 * check that there is at least one output destination for the generated bitmap (i.e. one or more
 * of: screen, printer or file) and if the same bitmap data should be reused among these destinations
 * then this is possible without re-rendering, giving performance savings.</p>
 * 
 * <p> Another example is where the barcodes in the label are to be generated with their lines based
 * on a multiple of the pixel width of the output device(s). With XyTransaction, the bitmap resolution from the 
 * bitmap producer can be queries to allow the controller of barcode generation to generate the 
 * barcodes to the correct size.</p>
 * 
 * @author rdavis
 *
 */
public class XyTransaction
{
	 /**
	  *  <p>R = Release L = Level G = date U = time</p>
	  * 
	  */
	 public static final String sccsid = "@(#)Xyratex  ISTP  XyTransaction.java  %R%.%L%, %G% %U%";
	
		/**
		 * log object used for logging activity in the form of String/character messages for debug, diagnostic and analysis purposes.
		 * The output destination for the log could be console output and/or file(s) depending on the user-definable overall configuration
		 * of logs in the system.
		 */
	 private Log log = LogFactory.getLog(XyTransaction.class);
	 
 	/**
	  * Represents output products of software components that will work together to produce a final overall output product.
	  */
	 private Vector<XyTransactionMember> transactionMembers = null;
	 
	 /**
	  * Constructor for initialising the transaction.
	  */
	 public XyTransaction()
	 {
	   log.trace( sccsid );
	  	
		 transactionMembers = new Vector<XyTransactionMember>();
	 }
	 
	 /**
	  * Add a software component or its output product to the transaction.
	  * 
	  * @param member
	  */
   public void addTransactionMember( final XyTransactionMember member )
   {
  	 transactionMembers.add(member);
   }
   
	 /**
	  * Get a software component or its output product to the transaction.
	  * 
	  * @param (input) index into the transaction collection
	  * @return member
	  */
   public XyTransactionMember getTransactionMember( final int i )
   {
  	 return (XyTransactionMember) transactionMembers.get(i);
   }
   
   /**
    * Returns number of transaction members (software components/output products)
    * @return number of transaction members
    */
   public int numberOfTransactionMembers()
   {
  	 return transactionMembers.size();
   }
}
