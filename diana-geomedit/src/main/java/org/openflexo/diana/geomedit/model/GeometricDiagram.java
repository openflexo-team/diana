/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * Copyright (c) 2011-2012, AgileBirds
 * 
 * This file is part of Diana-drawing-editor, a component of the software infrastructure 
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

package org.openflexo.diana.geomedit.model;

import java.util.List;

import org.openflexo.diana.geomedit.model.GeometricDiagram.GeometricDiagramImpl;
import org.openflexo.fge.DrawingGraphicalRepresentation;
import org.openflexo.fge.geomedit.construction.GeometricConstruction;
import org.openflexo.model.annotations.Adder;
import org.openflexo.model.annotations.CloningStrategy;
import org.openflexo.model.annotations.CloningStrategy.StrategyType;
import org.openflexo.model.annotations.Embedded;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.Getter.Cardinality;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PastingPoint;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Remover;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLElement;

@ModelEntity
@ImplementationClass(GeometricDiagramImpl.class)
@XMLElement(xmlTag = "GeometricDiagram")
public interface GeometricDiagram extends GeometricElement {

	@PropertyIdentifier(type = DrawingGraphicalRepresentation.class)
	public static final String GRAPHICAL_REPRESENTATION = "graphicalRepresentation";
	@PropertyIdentifier(type = GeometricConstruction.class, cardinality = Cardinality.LIST)
	public static final String CONSTRUCTIONS_KEY = "constructions";

	@Getter(value = CONSTRUCTIONS_KEY, cardinality = Cardinality.LIST, inverse = GeometricConstruction.GEOMETRIC_DIAGRAM)
	@XMLElement(primary = true)
	@CloningStrategy(StrategyType.CLONE)
	@Embedded
	public List<GeometricConstruction<?>> getConstructions();

	@Setter(CONSTRUCTIONS_KEY)
	public void setConstructions(List<GeometricConstruction<?>> someConstructions);

	@Adder(CONSTRUCTIONS_KEY)
	@PastingPoint
	public void addToConstructions(GeometricConstruction<?> aConstruction);

	@Remover(CONSTRUCTIONS_KEY)
	public void removeFromConstructions(GeometricConstruction<?> aConstruction);

	@Getter(value = GRAPHICAL_REPRESENTATION)
	@CloningStrategy(StrategyType.CLONE)
	@Embedded
	@XMLElement
	public DrawingGraphicalRepresentation getGraphicalRepresentation();

	@Setter(value = GRAPHICAL_REPRESENTATION)
	public void setGraphicalRepresentation(DrawingGraphicalRepresentation graphicalRepresentation);

	public static abstract class GeometricDiagramImpl extends GeometricElementImpl implements GeometricDiagram {

		@Override
		public GeometricDiagram getGeometricDiagram() {
			return this;
		}
	}

}
