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

package org.openflexo.diana.swing.graph;

import java.awt.Color;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import org.apache.commons.lang3.StringUtils;
import org.openflexo.connie.DataBinding;
import org.openflexo.diana.DrawingGraphicalRepresentation;
import org.openflexo.diana.DianaConstants;
import org.openflexo.diana.DianaModelFactory;
import org.openflexo.diana.DianaModelFactoryImpl;
import org.openflexo.diana.GRStructureVisitor;
import org.openflexo.diana.ShapeGraphicalRepresentation;
import org.openflexo.diana.ColorGradientBackgroundStyle.ColorGradientDirection;
import org.openflexo.diana.GRBinding.DrawingGRBinding;
import org.openflexo.diana.GRBinding.GraphGRBinding;
import org.openflexo.diana.GRProvider.DrawingGRProvider;
import org.openflexo.diana.GRProvider.ShapeGRProvider;
import org.openflexo.diana.control.AbstractDianaEditor;
import org.openflexo.diana.control.MouseControlContext;
import org.openflexo.diana.control.MouseControl.MouseButton;
import org.openflexo.diana.control.actions.MouseClickControlActionImpl;
import org.openflexo.diana.control.actions.MouseClickControlImpl;
import org.openflexo.diana.graph.DianaFunction;
import org.openflexo.diana.graph.DianaGraph;
import org.openflexo.diana.graph.DianaNumericFunction;
import org.openflexo.diana.graph.DianaFunction.DianaGraphType;
import org.openflexo.diana.impl.DrawingImpl;
import org.openflexo.diana.shapes.ShapeSpecification.ShapeType;
import org.openflexo.gina.controller.FIBController;
import org.openflexo.gina.model.graph.FIBContinuousSimpleFunctionGraph;
import org.openflexo.gina.model.graph.FIBGraph;
import org.openflexo.gina.model.graph.FIBGraphFunction;
import org.openflexo.gina.model.graph.FIBGraphFunction.GraphType;
import org.openflexo.gina.model.graph.FIBNumericFunction;
import org.openflexo.gina.swing.view.JFIBView;
import org.openflexo.gina.swing.view.SwingRenderingAdapter;
import org.openflexo.gina.view.impl.FIBWidgetViewImpl;
import org.openflexo.pamela.exceptions.ModelDefinitionException;

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

	// TODO: remove this method
	@Override
	protected final void performUpdate() {
		// System.out.println("performUpdate() dans " + this);
		super.performUpdate();
		// updateGraph();
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
		return new JFIBGraphPanel<>(graphDrawing);
	}

	@Override
	public JComponent getJComponent() {
		return getRenderingAdapter().getJComponent(getTechnologyComponent());
	}

	@Override
	public JComponent getResultingJComponent() {
		return getRenderingAdapter().getResultingJComponent(this);
	}

	protected abstract <G extends DianaGraph> GraphDrawing<W, G> makeGraphDrawing();

	public <G extends DianaGraph> GraphDrawing<W, ? extends G> getGraphDrawing() {
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
	 *            type of DianaGraph (Diana framework) used to represent the widget
	 */
	public static abstract class GraphDrawing<W extends FIBGraph, G extends DianaGraph> extends DrawingImpl<W>
			implements PropertyChangeListener {

		public static final int DEFAULT_WIDTH = 400;
		public static final int DEFAULT_HEIGHT = 300;

		protected static DianaModelFactory GRAPH_FACTORY = null;

		static {
			try {
				GRAPH_FACTORY = new DianaModelFactoryImpl();
			} catch (ModelDefinitionException e) {
				e.printStackTrace();
			}
		}

		protected G graph;
		private final JFIBGraphWidget<W> widget;

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

		private <N extends Number> void updateMinAndMax(FIBNumericFunction fibNumericFunction, DianaNumericFunction<N> dianaFunction) {
			N minValue = (N) FIBNumericFunction.DEFAULT_MIN_VALUE;
			N maxValue = (N) FIBNumericFunction.DEFAULT_MAX_VALUE;
			if (fibNumericFunction.getMinValue() != null && fibNumericFunction.getMinValue().isSet()
					&& fibNumericFunction.getMinValue().isValid()) {
				try {
					minValue = (N) fibNumericFunction.getMinValue().getBindingValue(widget);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (fibNumericFunction.getMaxValue() != null && fibNumericFunction.getMaxValue().isSet()
					&& fibNumericFunction.getMaxValue().isValid()) {
				try {
					maxValue = (N) fibNumericFunction.getMaxValue().getBindingValue(widget);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			// System.out.println("minValue=" + minValue + " maxValue=" + maxValue);
			dianaFunction.setRange(minValue, maxValue);
		}

		private <N extends Number> void updateStepsNumber(FIBNumericFunction fibNumericFunction, DianaNumericFunction<N> dianaFunction) {
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
			dianaFunction.setStepsNb(stepsNumber);

		}

		private <N extends Number> void updateMajorTickSpacing(FIBNumericFunction fibNumericFunction, DianaNumericFunction<N> dianaFunction) {
			// Sets major tick spacing
			N majorTickSpacing = (N) FIBContinuousSimpleFunctionGraph.DEFAULT_MAJOR_TICK_SPACING;
			if (fibNumericFunction.getMajorTickSpacing() != null && fibNumericFunction.getMajorTickSpacing().isSet()
					&& fibNumericFunction.getMajorTickSpacing().isValid()) {
				try {
					majorTickSpacing = (N) fibNumericFunction.getMajorTickSpacing().getBindingValue(widget);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			// System.out.println("majorTickSpacing=" + majorTickSpacing);
			dianaFunction.setMajorTickSpacing(majorTickSpacing);

		}

		private <N extends Number> void updateMinorTickSpacing(FIBNumericFunction fibNumericFunction, DianaNumericFunction<N> dianaFunction) {
			// Sets minor tick spacing
			N minorTickSpacing = (N) FIBContinuousSimpleFunctionGraph.DEFAULT_MINOR_TICK_SPACING;
			if (fibNumericFunction.getMinorTickSpacing() != null && fibNumericFunction.getMinorTickSpacing().isSet()
					&& fibNumericFunction.getMinorTickSpacing().isValid()) {
				try {
					minorTickSpacing = (N) fibNumericFunction.getMinorTickSpacing().getBindingValue(widget);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			// System.out.println("minorTickSpacing=" + minorTickSpacing);
			dianaFunction.setMinorTickSpacing(minorTickSpacing);

		}

		protected G appendFunctions(W fibGraph, G graph, FIBController controller) {

			for (FIBGraphFunction function : fibGraph.getFunctions()) {

				if (StringUtils.isEmpty(function.getName())) {
					function.setName("function" + fibGraph.getFunctions().indexOf(function));
				}

				if (function instanceof FIBNumericFunction) {
					FIBNumericFunction fibNumericFunction = (FIBNumericFunction) function;
					DianaNumericFunction numericFunction = graph.addNumericFunction(function.getName(), function.getType(),
							(DataBinding<Number>) function.getExpression(), getGraphType(function.getGraphType()));

					if (fibNumericFunction.getGraphType() == GraphType.COLORED_STEPS) {
						numericFunction.setAngleSpacing(fibNumericFunction.getAngleSpacing());
						numericFunction.setStepsSpacing(fibNumericFunction.getStepsSpacing());
					}

					// function.getPropertyChangeSupport().addPropertyChangeListener(this);

					// numericFunction.setRange(0.0, 100.0);
					numericFunction.setForegroundStyle(getFactory().makeForegroundStyle(function.getForegroundColor(), 1.0f));
					switch (function.getBackgroundType()) {
						case COLORED:
							numericFunction.setBackgroundStyle(getFactory().makeColoredBackground(function.getBackgroundColor1()));
							break;
						case GRADIENT:
							numericFunction.setBackgroundStyle(getFactory().makeColorGradientBackground(function.getBackgroundColor1(),
									function.getBackgroundColor2(), ColorGradientDirection.NORTH_WEST_SOUTH_EAST));
							break;
						case NONE:
							numericFunction.setBackgroundStyle(getFactory().makeEmptyBackground());
							break;
					}

					numericFunction.setDisplayMajorTicks(fibNumericFunction.getDisplayMajorTicks());
					numericFunction.setDisplayMinorTicks(fibNumericFunction.getDisplayMinorTicks());
					numericFunction.setDisplayLabels(fibNumericFunction.getDisplayLabels());

					// Sets parameters
					updateMinAndMax(fibNumericFunction, numericFunction);
					updateStepsNumber(fibNumericFunction, numericFunction);
					updateMajorTickSpacing(fibNumericFunction, numericFunction);
					updateMinorTickSpacing(fibNumericFunction, numericFunction);

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
				FIBGraphFunction fibFunction = (FIBGraphFunction) evt.getSource();
				DianaFunction<?> dianaFunction = graph.getFunction(fibFunction.getName());
				if (dianaFunction != null) {
					if (evt.getPropertyName().equals(FIBGraphFunction.EXPRESSION_KEY)) {
						// System.out.println("On reconstruit la graphe a cause de la fonction qui change: " + evt.getPropertyName());
						if (dianaFunction != null) {
							dianaFunction.setFunctionExpression((DataBinding) fibFunction.getExpression());
						}
						else {
							logger.warning("Inconsistent data : could not find DianaFunction matching " + fibFunction);
						}
						updateGraph();
					}
					else if (evt.getPropertyName().equals(FIBGraphFunction.GRAPH_TYPE_KEY)) {
						dianaFunction.setGraphType(getGraphType(fibFunction.getGraphType()));
						if (fibFunction.getGraphType() == GraphType.COLORED_STEPS) {
							dianaFunction.setAngleSpacing(fibFunction.getAngleSpacing());
							dianaFunction.setStepsSpacing(fibFunction.getStepsSpacing());
						}
						updateGraph();
					}
					else if (evt.getPropertyName().equals(FIBGraphFunction.ANGLE_SPACING_KEY)) {
						dianaFunction.setAngleSpacing(fibFunction.getAngleSpacing());
						updateGraph();
					}
					else if (evt.getPropertyName().equals(FIBGraphFunction.STEPS_SPACING_KEY)) {
						dianaFunction.setStepsSpacing(fibFunction.getStepsSpacing());
						updateGraph();
					}
				}
			}
			if (evt.getSource() instanceof FIBNumericFunction) {
				FIBNumericFunction fibNumericFunction = (FIBNumericFunction) evt.getSource();
				DianaNumericFunction dianaFunction = (DianaNumericFunction<?>) graph.getFunction(fibNumericFunction.getName());
				if (dianaFunction != null) {
					if (evt.getPropertyName().equals(FIBNumericFunction.MAX_VALUE_KEY)
							|| evt.getPropertyName().equals(FIBNumericFunction.MIN_VALUE_KEY)) {
						updateMinAndMax(fibNumericFunction, dianaFunction);
						updateGraph();
					}
					if (evt.getPropertyName().equals(FIBNumericFunction.MAJOR_TICK_SPACING_KEY)) {
						updateMajorTickSpacing(fibNumericFunction, dianaFunction);
						updateGraph();
					}
					if (evt.getPropertyName().equals(FIBNumericFunction.MINOR_TICK_SPACING_KEY)) {
						updateMinorTickSpacing(fibNumericFunction, dianaFunction);
						updateGraph();
					}
					if (evt.getPropertyName().equals(FIBNumericFunction.STEPS_NUMBER_KEY)) {
						updateStepsNumber(fibNumericFunction, dianaFunction);
						updateGraph();
					}
					if (evt.getPropertyName().equals(FIBNumericFunction.DISPLAY_LABELS_KEY)) {
						dianaFunction.setDisplayLabels(fibNumericFunction.getDisplayLabels());
						updateGraph();
					}
				}
			}
		}

		private boolean updateHasBeenRequested = false;

		protected void updateGraph() {

			// System.out
			// .println("updateGraph(), widget visible = " + widget.getRenderingAdapter().isVisible(widget.getTechnologyComponent()));

			if (!widget.getRenderingAdapter().isVisible(widget.getTechnologyComponent())) {
				// TODO: invoke later !
				if (!updateHasBeenRequested) {
					updateHasBeenRequested = true;
					SwingUtilities.invokeLater(() -> updateGraphNow());
				}
				return;
			}

			updateGraphNow();
		}

		protected void updateGraphNow() {

			performUpdateGraph();

			GraphNode<?> graphNode = (GraphNode) getDrawingTreeNode(graph);
			if (graphNode != null) {
				graphNode.notifyGraphNeedsToBeRedrawn();
			}
			else {
				logger.warning("Inconsistent data: could not retrieve node for graph");
			}

			widget.getRenderingAdapter().revalidateAndRepaint(widget.getTechnologyComponent());

			updateHasBeenRequested = false;
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

		protected DianaGraphType getGraphType(GraphType graphType) {
			if (graphType == null) {
				return DianaGraphType.CURVE;
			}
			switch (graphType) {
				case POINTS:
					return DianaGraphType.POINTS;
				case POLYLIN:
					return DianaGraphType.POLYLIN;
				case RECT_POLYLIN:
					return DianaGraphType.RECT_POLYLIN;
				case CURVE:
					return DianaGraphType.CURVE;
				case BAR_GRAPH:
					return DianaGraphType.BAR_GRAPH;
				case COLORED_STEPS:
					return DianaGraphType.COLORED_STEPS;
				case SECTORS:
					return DianaGraphType.SECTORS;
			}
			return DianaGraphType.CURVE;
		}

		/*public sclass ShowContextualMenuControl extends MouseClickControlImpl<DianaDrawingEditor> {
		
			public ShowContextualMenuControl(EditingContext editingContext) {
				super("Show contextual menu", MouseButton.RIGHT, 1, new MouseClickControlActionImpl<DianaDrawingEditor>() {
					@Override
					public boolean handleClick(DrawingTreeNode<?, ?> dtn, DianaDrawingEditor controller, MouseControlContext context) {
						DianaView<?, ?> view = controller.getDrawingView().viewForNode(dtn);
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
							// Unused DianaView<?, ?> view =
							controller.getDrawingView().viewForNode(dtn);
							// Unused Point newPoint =
							getPointInView(dtn, controller, context);
							// controller.showContextualMenu(dtn, view, newPoint);
							// System.out.println("OK on doit faire l'update du graphe");
							updateGraph();
							return false;
						}
					}, false, false, false, false, null));

			final DrawingGRBinding<W> drawingBinding = bindDrawing((Class<W>) getModel().getClass(), "drawing", new DrawingGRProvider<W>() {
				@Override
				public DrawingGraphicalRepresentation provideGR(W drawable, DianaModelFactory factory) {
					return drawingRepresentation;
				}
			});

			graphGR = getFactory().makeShapeGraphicalRepresentation(ShapeType.RECTANGLE);
			graphGR.setX(getModel().getBorderLeft());
			graphGR.setY(getModel().getBorderTop());
			graphGR.setWidth(DEFAULT_WIDTH);
			graphGR.setHeight(DEFAULT_HEIGHT);
			graphGR.setShadowStyle(getFactory().makeNoneShadowStyle());
			graphGR.setBackground(getFactory().makeColorGradientBackground(DianaConstants.DEFAULT_BACKGROUND_COLOR, Color.white,
					ColorGradientDirection.NORTH_WEST_SOUTH_EAST));
			graphGR.setForeground(getFactory().makeForegroundStyle(Color.ORANGE));
			graphGR.setIsFocusable(false);
			graphGR.setIsSelectable(false);

			final GraphGRBinding<DianaGraph> graphBinding = bindGraph(DianaGraph.class, "graph", new ShapeGRProvider<DianaGraph>() {
				@Override
				public ShapeGraphicalRepresentation provideGR(DianaGraph drawable, DianaModelFactory factory) {
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
