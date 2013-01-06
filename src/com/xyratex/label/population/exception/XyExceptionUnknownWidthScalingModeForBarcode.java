package com.xyratex.label.population.exception;

import org.apache.commons.lang.math.NumberUtils;

import com.xyratex.label.config.XyVersion;

public class XyExceptionUnknownWidthScalingModeForBarcode extends
    XyExceptionAbstractPopulatedFieldError
{
	/**
	 *  R = Release L = Level G = date U = time
	 */
	public static final String sccsid = "@(#)Xyratex  ISTP  XyExceptionUnknownFieldTypeForPopulating.java  %R%.%L%, %G% %U%";

	/**
	 * serialVersionUID required for this class because its necessary parent implements the Serializable interface.
	 * Its value is derived from SCCS Release and Level
	 * toLong() method won't throw an exception if non-numeric values are in the SCCS version data.
	 */
	public static final long serialVersionUID = NumberUtils.toLong("%R%") * XyVersion.sccsReleaseMultiplier 
	                                          + NumberUtils.toLong("%L%");

	public XyExceptionUnknownWidthScalingModeForBarcode(String detail)
	{
		super( detail + "\n" + sccsid );
	}
}
