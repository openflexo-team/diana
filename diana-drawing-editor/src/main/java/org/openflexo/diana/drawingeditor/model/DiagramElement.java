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

package org.openflexo.diana.drawingeditor.model;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.openflexo.diana.GraphicalRepresentation;
import org.openflexo.model.annotations.Adder;
import org.openflexo.model.annotations.CloningStrategy;
import org.openflexo.model.annotations.CloningStrategy.StrategyType;
import org.openflexo.model.annotations.Embedded;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.Getter.Cardinality;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PastingPoint;
import org.openflexo.model.annotations.Remover;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.model.factory.AccessibleProxyObject;
import org.openflexo.model.factory.CloneableProxyObject;
import org.openflexo.model.factory.DeletableProxyObject;

@ModelEntity
@ImplementationClass(DiagramElementImpl.class)
public interface DiagramElement<M extends DiagramElement<M, G>, G extends GraphicalRepresentation> extends Cloneable, Observer,
		AccessibleProxyObject, DeletableProxyObject, CloneableProxyObject {

	public static final String GRAPHICAL_REPRESENTATION = "graphicalRepresentation";
	public static final String DIAGRAM = "diagram";
	public static final String SHAPES = "shapes";
	public static final String CONNECTORS = "connectors";
	public static final String NAME = "name";

	@Getter(value = SHAPES, cardinality = Cardinality.LIST)
	@XMLElement(primary = true)
	@CloningStrategy(StrategyType.CLONE)
	@Embedded
	public List<Shape> getShapes();

	@Setter(SHAPES)
	public void setShapes(List<Shape> someShapes);

	@Adder(SHAPES)
	@PastingPoint
	public void addToShapes(Shape aShape);

	@Remover(SHAPES)
	public void removeFromShapes(Shape aShape);

	@Getter(value = CONNECTORS, cardinality = Cardinality.LIST)
	@XMLElement(primary = true)
	@CloningStrategy(StrategyType.CLONE)
	@Embedded
	public List<Connector> getConnectors();

	@Setter(CONNECTORS)
	public void setConnectors(List<Connector> someConnectors);

	@Adder(CONNECTORS)
	@PastingPoint
	public void addToConnectors(Connector aConnector);

	@Remover(CONNECTORS)
	public void removeFromConnectors(Connector aConnector);

	@Getter(value = NAME)
	@XMLAttribute
	public String getName();

	@Setter(value = NAME)
	public void setName(String aName);

	@Getter(value = DIAGRAM)
	public Diagram getDiagram();

	@Setter(value = DIAGRAM)
	public void setDiagram(Diagram diagram);

	@Getter(value = GRAPHICAL_REPRESENTATION)
	@CloningStrategy(StrategyType.CLONE)
	@Embedded
	@XMLElement
	public G getGraphicalRepresentation();

	@Setter(value = GRAPHICAL_REPRESENTATION)
	public void setGraphicalRepresentation(G graphicalRepresentation);

	// public void initializeDeserialization();

	// public void finalizeDeserialization();

	public DiagramElement<M, G> clone();

	@Override
	public void update(Observable o, Object arg);

	public void setChanged();

	public boolean hasChanged();
}
