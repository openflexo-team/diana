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
import java.awt.Point;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.logging.Logger;

import javax.swing.JComponent;

import org.openflexo.connie.DataBinding;
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
import org.openflexo.fge.control.AbstractDianaEditor;
import org.openflexo.fge.control.MouseControl.MouseButton;
import org.openflexo.fge.control.MouseControlContext;
import org.openflexo.fge.control.actions.MouseClickControlActionImpl;
import org.openflexo.fge.control.actions.MouseClickControlImpl;
import org.openflexo.fge.graph.FGEFunction.FGEGraphType;
import org.openflexo.fge.graph.FGEGraph;
import org.openflexo.fge.graph.FGENumericFunction;
import org.openflexo.fge.impl.DrawingImpl;
import org.openflexo.fge.shapes.ShapeSpecification.ShapeType;
import org.openflexo.fge.view.FGEView;
import org.openflexo.gina.controller.FIBController;
import org.openflexo.gina.model.graph.FIBContinuousSimpleFunctionGraph;
import org.openflexo.gina.model.graph.FIBGraph;
import org.openflexo.gina.model.graph.FIBGraphFunction;
import org.openflexo.gina.model.graph.FIBGraphFunction.GraphType;
import org.openflexo.gina.model.graph.FIBNumericFunction;
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

	private GraphDrawing<W, ?> graphDrawing;

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
	protected void componentBecomesVisible() {
		super.componentBecomesVisible();
		getGraphDrawing().updateGraph();
	}

	@Override
	protected void componentBecomesInvisible() {
		super.componentBecomesInvisible();
	}

	@Override
	protected void performUpdate() {
		super.performUpdate();
		updateGraph();
	}

	protected void updateGraph() {
		if (getTechnologyComponent() != null) {
			getTechnologyComponent().updateGraph();
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {

		super.propertyChange(evt);
	}

	@Override
	protected JFIBGraphPanel makeTechnologyComponent() {
		graphDrawing = makeGraphDrawing();
		return new JFIBGraphPanel(graphDrawing);
	}

	@Override
	public JComponent getJComponent() {
		return getRenderingAdapter().getJComponent(getTechnologyComponent());
	}

	@Override
	public JComponent getResultingJComponent() {
		return getRenderingAdapter().getResultingJComponent(this);
	}

	protected abstract <G extends FGEGraph> GraphDrawing<W, G> makeGraphDrawing();

	public <G extends FGEGraph> GraphDrawing<W, ? extends G> getGraphDrawing() {
		return (GraphDrawing<W, G>) graphDrawing;
	}

	/**
	 * Drawing used by the JFIBGraphWidget to display a graph using Diana framework
	 * 
	 * 
	 * @author sylvain
	 *
	 * @param <W>
	 *            type of widget beeing represented
	 * @param <G>
	 *            type of FGEGraph (Diana framework) used to represent the widget
	 */
	public static abstract class GraphDrawing<W extends FIBGraph, G extends FGEGraph> extends DrawingImpl<W>
			implements PropertyChangeListener {

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

		protected G graph;
		private JFIBGraphWidget<W> widget;

		protected DrawingGraphicalRepresentation drawingRepresentation;
		protected ShapeGraphicalRepresentation graphGR;

		public GraphDrawing(W fibGraph, JFIBGraphWidget<W> widget) {
			super(fibGraph, GRAPH_FACTORY, PersistenceMode.UniqueGraphicalRepresentations);
			fibGraph.getPropertyChangeSupport().addPropertyChangeListener(this);
			for (FIBGraphFunction f : fibGraph.getFunctions()) {
				f.getPropertyChangeSupport().addPropertyChangeListener(this);
			}
			this.widget = widget;
		}

		public G getGraph() {
			return graph;
		}

		@Override
		public void delete() {
			if (getModel() != null) {
				getModel().getPropertyChangeSupport().removePropertyChangeListener(this);
				for (FIBGraphFunction f : getModel().getFunctions()) {
					f.getPropertyChangeSupport().removePropertyChangeListener(this);
				}
			}
			super.delete();
		}

		protected abstract G makeGraph(W fibGraph);

		protected G appendFunctions(W fibGraph, G graph, FIBController controller) {

			for (FIBGraphFunction function : fibGraph.getFunctions()) {
				if (function instanceof FIBNumericFunction) {
					FIBNumericFunction fibNumericFunction = (FIBNumericFunction) function;
					FGENumericFunction numericFunction = graph.addNumericFunction(function.getName(), function.getType(),
							(DataBinding) function.getExpression(), getGraphType(function.getGraphType()));
					// numericFunction.setRange(0.0, 100.0);
					numericFunction.setForegroundStyle(getFactory().makeForegroundStyle(function.getForegroundColor(), 1.0f));
					switch (function.getBackgroundType()) {
						case COLORED:
							numericFunction.setBackgroundStyle(getFactory().makeColoredBackground(function.getBackgroundColor1()));
							break;
						case GRADIENT:
							numericFunction.setBackgroundStyle(getFactory().makeColorGradientBackground(function.getBackgroundColor1(),
									function.getBackgroundColor2(), ColorGradientDirection.SOUTH_EAST_NORTH_WEST));
							break;
						case NONE:
							numericFunction.setBackgroundStyle(getFactory().makeEmptyBackground());
							break;
					}

					numericFunction.setDisplayMajorTicks(fibNumericFunction.getDisplayMajorTicks());
					numericFunction.setDisplayMinorTicks(fibNumericFunction.getDisplayMinorTicks());
					numericFunction.setDisplayLabels(fibNumericFunction.getDisplayLabels());

					// Sets parameter range
					Number minValue = FIBNumericFunction.DEFAULT_MIN_VALUE;
					Number maxValue = FIBNumericFunction.DEFAULT_MAX_VALUE;
					if (fibNumericFunction.getMinValue() != null && fibNumericFunction.getMinValue().isSet()
							&& fibNumericFunction.getMinValue().isValid()) {
						try {
							minValue = fibNumericFunction.getMinValue().getBindingValue(widget);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					if (fibNumericFunction.getMaxValue() != null && fibNumericFunction.getMaxValue().isSet()
							&& fibNumericFunction.getMaxValue().isValid()) {
						try {
							maxValue = fibNumericFunction.getMaxValue().getBindingValue(widget);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					// System.out.println("minValue=" + minValue + " maxValue=" + maxValue);
					numericFunction.setRange(minValue, maxValue);

					// Sets step number
					int stepsNumber = FIBContinuousSimpleFunctionGraph.DEFAULT_STEPS_NUMBER;
					if (fibNumericFunction.getStepsNumber() != null && fibNumericFunction.getStepsNumber().isSet()
							&& fibNumericFunction.getStepsNumber().isValid()) {
						try {
							stepsNumber = fibNumericFunction.getStepsNumber().getBindingValue(widget);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					// System.out.println("stepsNumber=" + stepsNumber);
					numericFunction.setStepsNb(stepsNumber);

					// Sets major tick spacing
					Number majorTickSpacing = FIBContinuousSimpleFunctionGraph.DEFAULT_MAJOR_TICK_SPACING;
					if (fibNumericFunction.getMajorTickSpacing() != null && fibNumericFunction.getMajorTickSpacing().isSet()
							&& fibNumericFunction.getMajorTickSpacing().isValid()) {
						try {
							majorTickSpacing = fibNumericFunction.getMajorTickSpacing().getBindingValue(widget);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					// System.out.println("majorTickSpacing=" + majorTickSpacing);
					numericFunction.setMajorTickSpacing(majorTickSpacing);

					// Sets minor tick spacing
					Number minorTickSpacing = FIBContinuousSimpleFunctionGraph.DEFAULT_MINOR_TICK_SPACING;
					if (fibNumericFunction.getMinorTickSpacing() != null && fibNumericFunction.getMinorTickSpacing().isSet()
							&& fibNumericFunction.getMinorTickSpacing().isValid()) {
						try {
							minorTickSpacing = fibNumericFunction.getMinorTickSpacing().getBindingValue(widget);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					// System.out.println("minorTickSpacing=" + minorTickSpacing);
					numericFunction.setMinorTickSpacing(minorTickSpacing);

				}
			}

			return graph;
		}

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getPropertyName().equals(FIBGraph.FUNCTIONS_KEY)) {
				if (evt.getNewValue() instanceof FIBGraphFunction) {
					((FIBGraphFunction) evt.getNewValue()).getPropertyChangeSupport().addPropertyChangeListener(this);
				}
				if (evt.getOldValue() instanceof FIBGraphFunction) {
					((FIBGraphFunction) evt.getOldValue()).getPropertyChangeSupport().removePropertyChangeListener(this);
				}
				updateGraph();
			}
			if (evt.getPropertyName().equals(FIBGraph.BORDER_TOP_KEY) || evt.getPropertyName().equals(FIBGraph.BORDER_BOTTOM_KEY)
					|| evt.getPropertyName().equals(FIBGraph.BORDER_LEFT_KEY) || evt.getPropertyName().equals(FIBGraph.BORDER_RIGHT_KEY)) {
				updateBorders();
			}
			if (evt.getSource() instanceof FIBGraphFunction) {
				// System.out.println("On reconstruit la graphe a cause de la fonction qui change: " + evt.getPropertyName());
				updateGraph();
			}
		}

		protected void updateGraph() {

			performUpdateGraph();

			GraphNode<FGEGraph> graphNode = (GraphNode) getDrawingTreeNode(graph);
			if (graphNode != null) {
				graphNode.notifyGraphNeedsToBeRedrawn();
			}
			else {
				logger.warning("Inconsistent data: could not retrieve node for graph");
			}

			widget.getRenderingAdapter().revalidateAndRepaint(widget.getTechnologyComponent());
		}

		protected void performUpdateGraph() {
			graph.update();
		}

		public void resizeTo(Dimension newSize) {
			drawingRepresentation.setWidth(newSize.getWidth());
			drawingRepresentation.setHeight(newSize.getHeight());
			graphGR.setX(getModel().getBorderLeft());
			graphGR.setY(getModel().getBorderTop());
			graphGR.setWidth(newSize.getWidth() - getModel().getBorderRight() - getModel().getBorderLeft());
			graphGR.setHeight(newSize.getHeight() - getModel().getBorderTop() - getModel().getBorderBottom());
		}

		private void updateBorders() {
			// Sets borders
			graph.setBorderTop(getModel().getBorderTop());
			graph.setBorderBottom(getModel().getBorderBottom());
			graph.setBorderLeft(getModel().getBorderLeft());
			graph.setBorderRight(getModel().getBorderRight());
			graphGR.setX(getModel().getBorderLeft());
			graphGR.setY(getModel().getBorderTop());
			graphGR.setWidth(widget.getTechnologyComponent().getWidth() - getModel().getBorderRight() - getModel().getBorderLeft());
			graphGR.setHeight(widget.getTechnologyComponent().getHeight() - getModel().getBorderTop() - getModel().getBorderBottom());
		}

		protected FGEGraphType getGraphType(GraphType graphType) {
			if (graphType == null) {
				return FGEGraphType.CURVE;
			}
			switch (graphType) {
				case POINTS:
					return FGEGraphType.POINTS;
				case POLYLIN:
					return FGEGraphType.POLYLIN;
				case RECT_POLYLIN:
					return FGEGraphType.RECT_POLYLIN;
				case CURVE:
					return FGEGraphType.CURVE;
				case BAR_GRAPH:
					return FGEGraphType.BAR_GRAPH;
				case COLORED_STEPS:
					return FGEGraphType.COLORED_STEPS;
				case SECTORS:
					return FGEGraphType.SECTORS;
			}
			return FGEGraphType.CURVE;
		}

		/*public sclass ShowContextualMenuControl extends MouseClickControlImpl<DianaDrawingEditor> {
		
			public ShowContextualMenuControl(EditingContext editingContext) {
				super("Show contextual menu", MouseButton.RIGHT, 1, new MouseClickControlActionImpl<DianaDrawingEditor>() {
					@Override
					public boolean handleClick(DrawingTreeNode<?, ?> dtn, DianaDrawingEditor controller, MouseControlContext context) {
						FGEView<?, ?> view = controller.getDrawingView().viewForNode(dtn);
						Point newPoint = getPointInView(dtn, controller, context);
						controller.showContextualMenu(dtn, view, newPoint);
						return false;
					}
				}, false, false, false, false, editingContext);
			}
		}*/

		@Override
		public void init() {

			graph = makeGraph(getModel());

			drawingRepresentation = getFactory().makeDrawingGraphicalRepresentation();
			drawingRepresentation.setWidth(DEFAULT_WIDTH + getModel().getBorderRight() + getModel().getBorderLeft());
			drawingRepresentation.setHeight(DEFAULT_HEIGHT + getModel().getBorderTop() + getModel().getBorderBottom());
			drawingRepresentation.setDrawWorkingArea(false);
			drawingRepresentation.addToMouseClickControls(new MouseClickControlImpl<>("update", MouseButton.LEFT, 2,
					new MouseClickControlActionImpl<AbstractDianaEditor<?, ?, ?>>() {
						@Override
						public boolean handleClick(DrawingTreeNode<?, ?> dtn, AbstractDianaEditor<?, ?, ?> controller,
								MouseControlContext context) {
							FGEView<?, ?> view = controller.getDrawingView().viewForNode(dtn);
							Point newPoint = getPointInView(dtn, controller, context);
							// controller.showContextualMenu(dtn, view, newPoint);
							// System.out.println("OK on doit faire l'update du graphe");
							updateGraph();
							return false;
						}
					}, false, false, false, false, null));

			final DrawingGRBinding<W> drawingBinding = bindDrawing((Class<W>) getModel().getClass(), "drawing", new DrawingGRProvider<W>() {
				@Override
				public DrawingGraphicalRepresentation provideGR(W drawable, FGEModelFactory factory) {
					return drawingRepresentation;
				}
			});

			graphGR = getFactory().makeShapeGraphicalRepresentation(ShapeType.RECTANGLE);
			graphGR.setX(getModel().getBorderLeft());
			graphGR.setY(getModel().getBorderTop());
			graphGR.setWidth(DEFAULT_WIDTH);
			graphGR.setHeight(DEFAULT_HEIGHT);
			graphGR.setShadowStyle(getFactory().makeNoneShadowStyle());
			graphGR.setBackground(getFactory().makeColorGradientBackground(FGEConstants.DEFAULT_BACKGROUND_COLOR, Color.white,
					ColorGradientDirection.SOUTH_EAST_NORTH_WEST));
			graphGR.setForeground(getFactory().makeForegroundStyle(Color.ORANGE));
			graphGR.setIsFocusable(false);
			graphGR.setIsSelectable(false);

			final GraphGRBinding<FGEGraph> graphBinding = bindGraph(FGEGraph.class, "graph", new ShapeGRProvider<FGEGraph>() {
				@Override
				public ShapeGraphicalRepresentation provideGR(FGEGraph drawable, FGEModelFactory factory) {
					return graphGR;
				}
			});

			drawingBinding.addToWalkers(new GRStructureVisitor<W>() {

				@Override
				public void visit(W fibGraph) {
					drawGraph(graphBinding, graph);
				}
			});

		}
	}

}
