//-------------------------------------------------------------------------
// (C) COPYRIGHT Xyratex Storage Systems Division 2011
// All Rights Reserved
//
// Filename    : XyCombinedTransformProducer.java
// Author      : Rob Davis


// By          : Rob Davis
// File Type   : Java source code for Open Label Print
//-------------------------------------------------------------------------

package com.xyratex.svg.batik.util;

import java.awt.geom.AffineTransform;

import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGMatrix;

import org.apache.batik.parser.AWTTransformProducer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>This stateless singleton class produces a transform that is the
 * product of all the transforms effecting a SVG Element. The example below illustrates this.</p>
 * 
 * <p>Consider:</p>
 *
 * <pre>
 *  &lt;svg&gt;
 *   &lt;g transform="A"&gt;
 *	  &lt;g transform="B"&gt;
 *	    &lt;g transform="C"&gt;
 *	      &lt;g transform="D"&gt;
 *	        &lt;!-- graphics elements go here --&gt;
 *	      &lt;/g&gt;
 *	    &lt;/g&gt;
 *	  &lt;/g&gt;
 * 	&lt;/g&gt;
 * &lt;/svg&gt;
 * </pre>
 *
 * <p>This class will first look at the graphics element itself to see if it has a transform, lets call this D
 * then it will look at its parent, to see if that has a transform in this case it does, C therefore so far the resultant transform would be E x D
 * the parent transform is *pre*concatenated to give E x D i.e. NOT postconcatenated to give E x D
 * and so on e.g. next parent up, is B so the resultant transform is C x D x E
 * and so on until A x B x C x D x E here's an example:</p>
 * 
 * <pre>
 *	&lt;g transform="translate(-10,-20)"&gt;
 *	  &lt;g transform="scale(2)"&gt;
 *	    &lt;g transform="rotate(45)"&gt;
 *	      &lt;g transform="translate(5,10)"&gt;
 *	        &lt;!-- graphics elements go here --&gt;
 *	      &lt;/g&gt;
 *	    &lt;/g&gt;
 *	  &lt;/g&gt;
 *	&lt;/g&gt;
 * </pre>
 * 
 * <p>is equivalent to</p>
 *
 * <pre>
 *	&lt;g transform="translate(-10,-20) scale(2) rotate(45) translate(5,10)"&gt;
 *   &lt;!-- graphics elements go here --&gt;
 *  &lt;/g&gt;
 * </pre>
 *
 * See also: http://www.w3.org/TR/SVG/coords.html
 * @author rdavis
 *
 */
public class XyCombinedTransformProducer 
{
	private static final Log log = LogFactory.getLog(XyCombinedTransformProducer.class);
	
	/**
	 *  R = Release L = Level G = date U = time
	 *  Values calculated by SCCS when file is checked out and compiled.
	 */
	public static final String sccsid = "@(#)Xyratex  ISTP  XyCombinedTransformProducer.java  %R%.%L%, %G% %U%";
	
	/**
	 * Get the transform of the element (as explained above) 
	 * 
	 * @param element (input parameter)
	 * @return the transform
	 */
	public static AffineTransform getCurrentTransform(final Element element)
	{
		log.trace(sccsid + "\ngetCurrentTransform" + element.getTagName());
		
		// get parent
		// if parent is <g> get transform, continue until parent svg

		Element currentElement = element;
		Element parentElement;
		AffineTransform currentAt = null, preConcatenatedAt = null;
		boolean svgElementReached = false;

		String transformString = null;

		log.trace("element: " + currentElement.getTagName());

		if (currentElement.getTagName() == "svg")
		{
			svgElementReached = true;
		}
		else // lets start off then
		{
			transformString = currentElement.getAttribute("transform");

			log.trace("transformString: " + transformString);

			if ((transformString != "") && (transformString != null))
			{
				preConcatenatedAt = AWTTransformProducer
				    .createAffineTransform(transformString);
			}
			else
			{
				preConcatenatedAt = new AffineTransform();
			}
		}

		if (preConcatenatedAt != null)
		{
			while (!svgElementReached)
			{
				parentElement = (org.w3c.dom.Element) currentElement.getParentNode();
				currentElement = parentElement;

				log.trace("parentElement = " + parentElement.getTagName());

				if (currentElement.getTagName() == "svg")
				{
					svgElementReached = true;
				}
				else
				{
					transformString = currentElement.getAttribute("transform");

					log.trace("transformString = " + transformString);

					if ((transformString != "") && (transformString != null))
					{
						currentAt = AWTTransformProducer
						    .createAffineTransform(transformString);

						preConcatenatedAt.preConcatenate(currentAt);
					}
				}
			}
		}

		if (preConcatenatedAt == null) preConcatenatedAt = new AffineTransform();

		return preConcatenatedAt;
	}
	
  public static void matrixAsString( SVGMatrix svgMatrix )
  {
      log.trace(
              "A = " + svgMatrix.getA()
           +  " B = " + svgMatrix.getB()
           +  " C = " + svgMatrix.getC()
           +  " D = " + svgMatrix.getD()
           +  " E = " + svgMatrix.getE()
           +  " F = " + svgMatrix.getF()
           );
  }
  
  /*
   * It is thought that this class could be replaced with the use of
   * (SVGMatrix)svgRootAsLocatable.getTransformToElement(svgElement) batik implementation.
   * to obtain the combined transformation as illustrated in the example below.
   * The code below has been tried and seems to work quite well, however
   * some text elements have been removed when they shouldn't have been.
   * So this alternative might have potential to replace this class.
   * Which means less new code to maintain, plus batik draws upon the
   * general expertise of 100s in the industry.
   * 
   * A concern is comparing the actual transformation values
   * that this class produces with this proposed replacement, for example:
   * 
   * DEBUG XyCombinedTransformProducer - the XyCombinedTransformProducer way
   * DEBUG XyCombinedTransformProducer - A = 0.999987 B = 0.0 C = 0.0 D = 1.0 E = -56.6287 F = -37.158
   * DEBUG XyCombinedTransformProducer - the getTransformToElement way
   * DEBUG XyCombinedTransformProducer - A = 1.000013 B = 0.0 C = 0.0 D = 1.0 E = 56.629436 F = 37.158
   *
   * DEBUG XyCombinedTransformProducer - the XyCombinedTransformProducer way
   * DEBUG XyCombinedTransformProducer - A = 1.00102 B = 0.0 C = 0.0 D = 1.0 E = -76.387 F = -20.2448
   * DEBUG XyCombinedTransformProducer - the getTransformToElement way
   * DEBUG XyCombinedTransformProducer - A = 0.99898106 B = 0.0 C = 0.0 D = 1.0 E = 76.309166 F = 20.2448
   *
   * 2 points to make about this:
   *  1. Some values differ by about +/- 0.01. This might be due to the precision of calculation 
   *  and may not actually have an adverse effect. In other words perhaps one of the methods is using
   *  superfluously high precision.
   *  
   *  2. Values E and F produced by this class are negative and the proposed replacement method
   *  has them as positive. This also might not be a problem if the values are scalar.
   *
   * import org.w3c.dom.svg.SVGElement;
   * import org.w3c.dom.svg.SVGLocatable;
   * import org.apache.batik.dom.svg.SVGOMMatrix;
   * 
	log.trace("the XyCombinedTransformProducer way");
	SVGMatrix xsm = new SVGOMMatrix(preConcatenatedAt);
	matrixAsString( xsm );
	
	log.trace("the getTransformToElement way");
	SVGLocatable svgRootAsLocatable = (SVGLocatable) (element.getOwnerDocument().getDocumentElement());	
	SVGElement svgElement = (SVGElement)element;	
	SVGMatrix gttesm = (SVGMatrix)svgRootAsLocatable.getTransformToElement(svgElement);
	
	return new AffineTransform( gttesm.getA(),
			                        gttesm.getB(),
			                        gttesm.getC(),
			                        gttesm.getD(),
			                        gttesm.getE(),
			                        gttesm.getF()
                        );
	
	if ( gttesm != null )
	{
	matrixAsString( gttesm );
	} 
  */
}
