//-------------------------------------------------------------------------
// (C) COPYRIGHT Xyratex Storage Systems Division 2011
// All Rights Reserved
//
// Filename    : XyAbstractCli.java
// Author      : Rob Davis


// By          : Rob Davis
// File Type   : Java source code for Open Label Print
//-------------------------------------------------------------------------

package com.xyratex.label.apps.cli;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;

import com.xyratex.debug.XyDebug;
import com.xyratex.label.config.XyVersion;

/**
 * Abstract class providing common code for Command Line Applications.
 * An application in the form of a concrete derived subclass will inherit from this class to acquire the common code.
 */
public abstract class XyAbstractCli
{
	/**
	 *  <p>R = Release L = Level G = date U = time</p>
	 * 
	 */
	public static final String sccsid = "@(#)Xyratex  ISTP  XyAbstractCli.java  %R%.%L%, %G% %U%";
	
	/**
	 * (e)rror mode command line switch. 
	 */
	protected static final String errorsOnOptionAsText = "e";
	
	/**
	 * Error mode option definition.
	 * Mutually exclusive from (w)arning mode.
	 */
	protected static final Option errorsEnabledOption = new Option(errorsOnOptionAsText,
  "errors enabled - program will halt if error encountered");
	
	/**
	 * (w)arning mode command line switch. 
	 */
	protected static final String warningsOnOptionAsText = "w";
	
	/**
	 * Warning mode option definition.
	 * Mutually exclusive from (e)rror mode.
	 */
	protected static final Option warningOutputEnabledOption = new Option(
	    warningsOnOptionAsText,
	    "warnings enabled - if errors not enabled, program will just report errors and try to continue");

	/**
	 * Verbose command line switch
	 */
	protected static final String verboseOptionAsText = "v";
	
	/**
	 * Verbose option definition. Enables all log output.
	 */
	protected static final Option verboseOption = new Option( verboseOptionAsText, "verbose enabled" );
	
	/**
	 * Software version command line switch.
	 */
	protected static final String versionOptionAsText = "V";
	
	/**
	 * Software version option definition. Displays software version.
	 */
	protected static final Option versionOption = new Option( versionOptionAsText, "verbose enabled" );
	
	/**
	 * Holds possible options specifiable at command line.
	 */
	protected static Options options = new Options();
	
	/**
	 * 
	 * ensures that warning mode cannot be selected at the same time as error mode
	 * and vice-versa
	 */
	protected static OptionGroup errorModeOptionGroup = new OptionGroup(); 
	
	/**
	 * Run time initialisation. 
	 * Set up possible command line options.
	 * Static constructor.
	 */
	static 
	{
		// - add error and warning options to the recognised options list
		// - make both options have an optional argument - path/file to output their logs to
		// - add error and warning options to errorModeOptionGroup which ensure
    errorsEnabledOption.setOptionalArg(true);
    errorsEnabledOption.setArgs(1);
		options.addOption(errorsEnabledOption);
		errorModeOptionGroup.addOption(errorsEnabledOption);

		warningOutputEnabledOption.setOptionalArg(true);
		warningOutputEnabledOption.setArgs(1);
		options.addOption(warningOutputEnabledOption);
		errorModeOptionGroup.addOption(warningOutputEnabledOption);
		
		verboseOption.setOptionalArg(true);
		verboseOption.setArgs(1);
		options.addOption(verboseOption);
	}
	
	/**
	 * common routine to process error, warning and verbose debug options used for debugging
	 */
	protected static void processDebugOptions( final CommandLine line )
	{		
		if (line.hasOption(errorsOnOptionAsText))
		{
			XyDebug.setErrorMode(XyDebug.ERRORMODE_STRICT);

			Object errorModeOption = line
			    .getOptionObject(errorsOnOptionAsText);

			if ( errorModeOption instanceof Option )
			{
			  if ( ((Option)errorModeOption).hasArg())
			  {
				  //String errorOutputFile = line.getOptionValue(errorsOnOptionAsText);
				  // TODO outputting to a file is not currently implemented
			  }
		  }
		}
		else // if not error, then check for warnings
		{
			if (line.hasOption(warningsOnOptionAsText))
			{
				XyDebug.setWarningMode(XyDebug.WARNING_ON);

				Object warningModeOption = line
				    .getOptionObject(warningsOnOptionAsText);

				if ( warningModeOption instanceof Option )
				{
				  if (((Option) warningModeOption).hasArg())
					{
						//String warningOutputFile = line.getOptionValue(warningsOnOptionAsText);
						
					  // TODO outputting to a file is not currently implemented
					}
				}
			} // end check warning selected
		} // end check error selected
		
		// if there is no verbose option then all the debug output will not be outputted
		if ( !line.hasOption(verboseOptionAsText) )
		{
			XyDebug.disableLogging();
		}
		
		if ( line.hasOption(versionOptionAsText) )
		{
			System.out.println(XyVersion.OLP_VERSION);
		}
	}
}
