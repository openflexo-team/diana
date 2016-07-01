/**
 * 
 * Copyright (c) 2013-2016, Openflexo
 * Copyright (c) 2011-2012, AgileBirds
 * 
 * This file is part of Gina-core, a component of the software infrastructure 
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

package org.openflexo.fge.swing.graph;

import java.awt.Color;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.util.logging.Logger;

import javax.swing.JComponent;

import org.openflexo.fge.ColorGradientBackgroundStyle.ColorGradientDirection;
import org.openflexo.fge.DrawingGraphicalRepresentation;
import org.openflexo.fge.FGEConstants;
import org.openflexo.fge.FGEModelFactory;
import org.openflexo.fge.FGEModelFactoryImpl;
import org.openflexo.fge.GRBinding.DrawingGRBinding;
import org.openflexo.fge.GRBinding.GraphGRBinding;
import org.openflexo.fge.GRProvider.DrawingGRProvider;
import org.openflexo.fge.GRProvider.ShapeGRProvider;
import org.openflexo.fge.GRStructureVisitor;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.graph.FGEGraph;
import org.openflexo.fge.impl.DrawingImpl;
import org.openflexo.fge.shapes.ShapeSpecification.ShapeType;
import org.openflexo.gina.controller.FIBController;
import org.openflexo.gina.model.graph.FIBGraph;
import org.openflexo.gina.swing.view.JFIBView;
import org.openflexo.gina.swing.view.SwingRenderingAdapter;
import org.openflexo.gina.view.impl.FIBWidgetViewImpl;
import org.openflexo.model.exceptions.ModelDefinitionException;

/**
 * Default base implementation for a {@link FIBGraph} view<br>
 * 
 * @param <W>
 *            type of {@link FIBGraph} this view manage
 * 
 * @author sylvain
 */
public abstract class JFIBGraphWidget<W extends FIBGraph> extends FIBWidgetViewImpl<W, JFIBGraphPanel, Object>
		implements JFIBView<W, JFIBGraphPanel> {

	private static final Logger logger = Logger.getLogger(JFIBGraphWidget.class.getPackage().getName());

	/**
	 * A {@link RenderingAdapter} implementation dedicated for Swing JLabel<br>
	 * (based on generic SwingTextRenderingAdapter)
	 * 
	 * @author sylvain
	 * 
	 */
	public static class SwingGraphRenderingAdapter extends SwingRenderingAdapter<JFIBGraphPanel>
			implements RenderingAdapter<JFIBGraphPanel> {

		@Override
		public Color getDefaultBackgroundColor(JFIBGraphPanel component) {
			return Color.WHITE;
		}

		@Override
		public Color getDefaultForegroundColor(JFIBGraphPanel component) {
			return Color.BLACK;
		}

	}

	public static SwingGraphRenderingAdapter RENDERING_TECHNOLOGY_ADAPTER = new SwingGraphRenderingAdapter();

	public JFIBGraphWidget(W model, FIBController controller) {
		super(model, controller, RENDERING_TECHNOLOGY_ADAPTER);
	}

	@Override
	public SwingGraphRenderingAdapter getRenderingAdapter() {
		return (SwingGraphRenderingAdapter) super.getRenderingAdapter();
	}

	@Override
	protected void performUpdate() {
		super.performUpdate();
		updateGraph();
	}

	protected void updateGraph() {
		System.out.println("Ce serait bien de mettre a jour le graphe");
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {

		/*if (evt.getPropertyName().equals(FIBButton.LABEL_KEY)) {
			updateLabel();
		}
		if (evt.getPropertyName().equals(FIBButton.BUTTON_ICON_KEY)) {
			updateIcon();
		}*/

		super.propertyChange(evt);
	}

	@Override
	protected JFIBGraphPanel makeTechnologyComponent() {
		return new JFIBGraphPanel(makeGraphDrawing());
	}

	@Override
	public JComponent getJComponent() {
		return getRenderingAdapter().getJComponent(getTechnologyComponent());
	}

	@Override
	public JComponent getResultingJComponent() {
		return getRenderingAdapter().getResultingJComponent(this);
	}

	protected abstract <G extends FGEGraph> GraphDrawing<G> makeGraphDrawing();

	public static abstract class GraphDrawing<G extends FGEGraph> extends DrawingImpl<Object> {

		public static final int HORIZONTAL_BORDER = 10;
		public static final int VERTICAL_BORDER = 10;
		public static final int DEFAULT_WIDTH = 400;
		public static final int DEFAULT_HEIGHT = 300;

		protected static FGEModelFactory GRAPH_FACTORY = null;

		static {
			try {
				GRAPH_FACTORY = new FGEModelFactoryImpl();
			} catch (ModelDefinitionException e) {
				e.printStackTrace();
			}
		}

		private G graph;

		private DrawingGraphicalRepresentation drawingRepresentation;
		private ShapeGraphicalRepresentation graphGR;

		public GraphDrawing() {
			super(new Object(), GRAPH_FACTORY, PersistenceMode.UniqueGraphicalRepresentations);
		}

		protected abstract G makeGraph();

		public void resizeTo(Dimension newSize) {
			drawingRepresentation.setWidth(newSize.getWidth());
			drawingRepresentation.setHeight(newSize.getHeight());
			graphGR.setWidth(newSize.getWidth() - 2 * HORIZONTAL_BORDER);
			graphGR.setHeight(newSize.getHeight() - 2 * VERTICAL_BORDER);
		}

		@Override
		public void init() {

			graph = makeGraph();

			drawingRepresentation = getFactory().makeDrawingGraphicalRepresentation();
			drawingRepresentation.setWidth(DEFAULT_WIDTH + 2 * HORIZONTAL_BORDER);
			drawingRepresentation.setHeight(DEFAULT_HEIGHT + 2 * VERTICAL_BORDER);
			drawingRepresentation.setDrawWorkingArea(false);

			final DrawingGRBinding<Object> drawingBinding = bindDrawing(Object.class, "drawing", new DrawingGRProvider<Object>() {
				@Override
				public DrawingGraphicalRepresentation provideGR(Object drawable, FGEModelFactory factory) {
					return drawingRepresentation;
				}
			});

			graphGR = getFactory().makeShapeGraphicalRepresentation(ShapeType.RECTANGLE);
			graphGR.setX(HORIZONTAL_BORDER);
			graphGR.setY(VERTICAL_BORDER);
			graphGR.setWidth(DEFAULT_WIDTH);
			graphGR.setHeight(DEFAULT_HEIGHT);
			graphGR.setShadowStyle(getFactory().makeNoneShadowStyle());
			graphGR.setBackground(getFactory().makeColorGradientBackground(FGEConstants.DEFAULT_BACKGROUND_COLOR, Color.white,
					ColorGradientDirection.SOUTH_EAST_NORTH_WEST));
			graphGR.setForeground(getFactory().makeForegroundStyle(Color.ORANGE));
			graphGR.setIsFocusable(false);
			graphGR.setIsSelectable(false);

			final GraphGRBinding<G> graphBinding = bindGraph((Class<G>) graph.getClass(), "graph", new ShapeGRProvider<G>() {
				@Override
				public ShapeGraphicalRepresentation provideGR(G drawable, FGEModelFactory factory) {
					return graphGR;
				}
			});

			drawingBinding.addToWalkers(new GRStructureVisitor<Object>() {

				@Override
				public void visit(Object route) {
					drawGraph(graphBinding, graph);
				}
			});

		}
	}

}
