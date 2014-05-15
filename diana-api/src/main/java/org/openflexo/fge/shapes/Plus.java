package org.openflexo.fge.shapes;

import org.openflexo.fge.GRParameter;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;

/**
 * A filled plus shape. Specified by start angle and ratio between inner square and outer square.<br>
 * Referenced as Plus in POI API, and referenced as Cross in shape selector of libreoffice.
 * 
 * @author eloubout
 * 
 */
@ModelEntity
@XMLElement(xmlTag = "PlusShape")
public interface Plus extends ShapeSpecification {

	// Property Keys
	/**
	 * Cut ratio of rectangle.
	 */
	@PropertyIdentifier(type = Double.class)
	public static final String			RATIO_KEY	= "ratio";

	public static GRParameter<Double>	RATIO		= GRParameter.getGRParameter(Plus.class, RATIO_KEY, Double.class);

	// *******************************************************************************
	// * Properties
	// *******************************************************************************

	@Getter(value = RATIO_KEY, defaultValue = "0.2")
	@XMLAttribute
	public double getRatio();

	@Setter(value = RATIO_KEY)
	public void setRatio(final double aRatio);

}
