/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * Copyright (c) 2011-2012, AgileBirds
 * 
 * This file is part of Diana-api, a component of the software infrastructure 
 * developed at Openflexo.
 * 
 * 
 * Openflexo is dual-licensed under the European Union Public License (EUPL, either 
 * version 1.1 of the License, or any later version ), which is available at 
 * https://joinup.ec.europa.eu/software/page/eupl/licence-eupl
 * and the GNU General Public License (GPL, either version 3 of the License, or any 
 * later version), which is available at http://www.gnu.org/licenses/gpl.html .
 * 
 * You can redistribute it and/or modify under the terms of either of these licenses
 * 
 * If you choose to redistribute it and/or modify under the terms of the GNU GPL, you
 * must include the following additional permission.
 *
 *          Additional permission under GNU GPL version 3 section 7
 *
 *          If you modify this Program, or any covered work, by linking or 
 *          combining it with software containing parts covered by the terms 
 *          of EPL 1.0, the licensors of this Program grant you additional permission
 *          to convey the resulting work. * 
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE. 
 *
 * See http://www.openflexo.org/license.html for details.
 * 
 * 
 * Please contact Openflexo (openflexo-contacts@openflexo.org)
 * or visit www.openflexo.org if you need additional information.
 * 
 */

package org.openflexo.fge.shapes;

import java.util.List;

import org.openflexo.fge.GRProperty;
import org.openflexo.model.annotations.Adder;
import org.openflexo.model.annotations.CloningStrategy;
import org.openflexo.model.annotations.CloningStrategy.StrategyType;
import org.openflexo.model.annotations.Embedded;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.Getter.Cardinality;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Remover;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLElement;

/**
 * Represents an union of shapes (a grouped collection)
 * 
 * Note that this implementation is powered by PAMELA framework.
 * 
 * @author sylvain
 */
@ModelEntity
@XMLElement(xmlTag = "Union")
public interface ShapeUnion extends ShapeSpecification {

	@PropertyIdentifier(type = ShapeSpecification.class, cardinality = Cardinality.LIST)
	public static final String SHAPES_KEY = "shapes";

	public static GRProperty<List> SHAPES = GRProperty.getGRParameter(ShapeUnion.class, SHAPES_KEY, List.class);

	@Getter(value = SHAPES_KEY, cardinality = Cardinality.LIST)
	@XMLElement
	@CloningStrategy(StrategyType.CLONE)
	@Embedded
	public List<ShapeSpecification> getShapes();

	@Setter(SHAPES_KEY)
	public void setShapes(List<ShapeSpecification> shapes);

	@Adder(SHAPES_KEY)
	public void addToShapes(ShapeSpecification shape);

	@Remover(SHAPES_KEY)
	public void removeFromShapes(ShapeSpecification shape);

}
