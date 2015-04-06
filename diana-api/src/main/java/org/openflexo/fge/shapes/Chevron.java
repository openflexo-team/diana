package org.openflexo.fge.shapes;

import org.openflexo.fge.GRProperty;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;

/**
 * Interface for a chevron. The sole property is the ratio of the arrow lengths. To have a correct shape, this ratio has to be strictly
 * between 0 and 0.5.
 * 
 * @author eloubout
 * 
 */
@ModelEntity
@XMLElement(xmlTag = "ChevronShape")
public interface Chevron extends ShapeSpecification {

	// Property Keys
	@PropertyIdentifier(type = Double.class)
	public static final String			ARROW_LENGTH_KEY	= "arrowLength";

	public static GRProperty<Double>	ARROW_LENGTH		= GRProperty.getGRParameter(Chevron.class, ARROW_LENGTH_KEY, Double.class);

	// *******************************************************************************
	// * Properties
	// *******************************************************************************
	@Getter(value = ARROW_LENGTH_KEY, defaultValue = "0.2")
	@XMLAttribute
	public double getArrowLength();

	@Setter(value = ARROW_LENGTH_KEY)
	public void setArrowLength(final double anArrowLength);
}
