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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.openflexo.diana.geomedit.model.GeometricConstruction.GeometricConstructionImpl;
import org.openflexo.diana.geomedit.model.gr.GeometricObjectGraphicalRepresentation;
import org.openflexo.fge.BackgroundStyle;
import org.openflexo.fge.ForegroundStyle;
import org.openflexo.fge.cp.ControlPoint;
import org.openflexo.fge.geom.area.FGEArea;
import org.openflexo.fge.notifications.GeometryModified;
import org.openflexo.model.annotations.CloningStrategy;
import org.openflexo.model.annotations.CloningStrategy.StrategyType;
import org.openflexo.model.annotations.Embedded;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.Import;
import org.openflexo.model.annotations.Imports;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLElement;

@ModelEntity(isAbstract = true)
@ImplementationClass(GeometricConstructionImpl.class)
@Imports({ @Import(LineConstruction.class), @Import(PointConstruction.class), @Import(EllipsConstruction.class),
		@Import(RectangleConstruction.class), @Import(RoundRectangleConstruction.class), @Import(SegmentConstruction.class),
		@Import(HalfLineConstruction.class), @Import(CubicCurveConstruction.class), @Import(QuadCurveConstruction.class),
		@Import(ComplexCurveConstruction.class), @Import(BandConstruction.class), @Import(HalfBandConstruction.class),
		@Import(HalfPlaneConstruction.class), @Import(ObjectReference.class), @Import(PolygonConstruction.class),
		@Import(QuarterPlaneConstruction.class), @Import(PolylinConstruction.class), @Import(IntersectionConstruction.class),
		@Import(UnionConstruction.class), @Import(SubstractionConstruction.class) })
public interface GeometricConstruction<A extends FGEArea> extends GeometricElement {

	@PropertyIdentifier(type = GeometricObjectGraphicalRepresentation.class)
	public static final String GRAPHICAL_REPRESENTATION = "graphicalRepresentation";
	@PropertyIdentifier(type = GeometricDiagram.class)
	public static final String GEOMETRIC_DIAGRAM = "geometricDiagram";

	@PropertyIdentifier(type = ForegroundStyle.class)
	public static final String FOREGROUND_KEY = "foreground";
	@PropertyIdentifier(type = BackgroundStyle.class)
	public static final String BACKGROUND_KEY = "background";

	@Override
	@Getter(value = GEOMETRIC_DIAGRAM)
	public GeometricDiagram getGeometricDiagram();

	@Setter(value = GEOMETRIC_DIAGRAM)
	public void setGeometricDiagram(GeometricDiagram geometricDiagram);

	@Getter(value = GRAPHICAL_REPRESENTATION)
	@CloningStrategy(StrategyType.CLONE)
	@Embedded
	@XMLElement
	public GeometricObjectGraphicalRepresentation<A> getGraphicalRepresentation();

	@Setter(value = GRAPHICAL_REPRESENTATION)
	public void setGraphicalRepresentation(GeometricObjectGraphicalRepresentation<A> graphicalRepresentation);

	@Getter(value = FOREGROUND_KEY)
	@CloningStrategy(StrategyType.CLONE)
	@Embedded
	@XMLElement
	public ForegroundStyle getForeground();

	@Setter(value = FOREGROUND_KEY)
	public void setForeground(ForegroundStyle aForeground);

	@Getter(value = BACKGROUND_KEY)
	@CloningStrategy(StrategyType.CLONE)
	@Embedded
	@XMLElement
	public BackgroundStyle getBackground();

	@Setter(value = BACKGROUND_KEY)
	public void setBackground(BackgroundStyle aBackground);

	public void refresh();

	public GeometricConstruction<?>[] getDepends();

	public GeometricObjectGraphicalRepresentation<A> makeNewConstructionGR(GeometricConstructionFactory geometricConstructionFactory);

	public A getData();

	public List<ControlPoint> getControlPoints();

	public void setControlPoints(List<ControlPoint> controlPoints);

	public void notifyGeometryChanged();

	public static abstract class GeometricConstructionImpl<A extends FGEArea> extends GeometricElementImpl
			implements GeometricConstruction<A>, PropertyChangeListener {

		private A computedData;
		private List<GeometricConstruction<?>> listened = new ArrayList<GeometricConstruction<?>>();

		protected abstract A computeData();

		@Override
		public A getData() {
			// System.out.println("getData() for "+this.getClass().getSimpleName());

			ensureUpToDate();

			if (computedData == null) {
				computedData = computeData();
			}

			return computedData;
		}

		private void ensureUpToDate() {
			// Recursively called ensureUpToDate() on dependancies
			if (getDepends() != null) {
				for (GeometricConstruction<?> d : getDepends()) {
					if (!listened.contains(d)) {
						listened.add(d);
						d.getPropertyChangeSupport().addPropertyChangeListener(this);
					}
					((GeometricConstructionImpl<?>) d).ensureUpToDate();
				}
			}
		}

		@Override
		public boolean delete(Object... arg0) {
			for (GeometricConstruction<?> c : listened) {
				c.getPropertyChangeSupport().removePropertyChangeListener(this);
			}
			listened.clear();
			return true;
		}

		@Override
		public final void refresh() {
			// System.out.println("Refresh for "+this.getClass().getSimpleName());
			if (getDepends() != null) {
				for (GeometricConstruction c : getDepends()) {
					c.refresh();
				}
			}
			computedData = computeData();
		}

		@Override
		public abstract String toString();

		@Override
		public abstract GeometricConstruction<?>[] getDepends();

		@Override
		public String getName() {
			String returned = (String) performSuperGetter(NAME_KEY);
			if (returned == null) {
				return getDefaultName();
			}
			return returned;
		}

		protected String getDefaultName() {
			return getBaseName() + getIndex();
		}

		public String getBaseName() {
			return "Construction";
		}

		private static int totalIndex = 0;
		private int index = -1;

		public int getIndex() {
			if (index == -1) {
				index = totalIndex;
				totalIndex++;
			}
			return index;
		}

		private List<ControlPoint> controlPoints;

		@Override
		public List<ControlPoint> getControlPoints() {
			return controlPoints;
		}

		@Override
		public void setControlPoints(List<ControlPoint> controlPoints) {
			this.controlPoints = controlPoints;
			notifyControlPointsChanged();
		}

		@Override
		public void notifyGeometryChanged() {
			if (getGraphicalRepresentation() != null) {
				getGraphicalRepresentation().setGeometricObject(computedData);
			}
			getPropertyChangeSupport().firePropertyChange(GeometryModified.EVENT_NAME, false, true);
		}

		private void notifyControlPointsChanged() {
			getPropertyChangeSupport().firePropertyChange("ControlPoints", false, true);
		}

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			// TODO: this is really brutal, we can do better here
			refresh();
			notifyGeometryChanged();
		}

	}

}
