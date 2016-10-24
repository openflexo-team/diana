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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.logging.Logger;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.openflexo.fge.Drawing;
import org.openflexo.fge.graph.FGEGraph;
import org.openflexo.fge.swing.JDianaInteractiveEditor;
import org.openflexo.fge.swing.SwingViewFactory;
import org.openflexo.fge.swing.control.SwingToolFactory;
import org.openflexo.fge.swing.graph.JFIBGraphWidget.GraphDrawing;
import org.openflexo.gina.model.graph.FIBGraph;

/**
 * Default base implementation for a widget with a button<br>
 * 
 * @param <C>
 *            type of technology-specific component this view manage
 * 
 * @author sylvain
 */
@SuppressWarnings("serial")
public class JFIBGraphPanel<W extends FIBGraph, G extends FGEGraph> extends JPanel {

	private static final Logger logger = Logger.getLogger(JFIBGraphPanel.class.getPackage().getName());

	private GraphDrawing<W, G> drawing;

	public JFIBGraphPanel(GraphDrawing<W, G> aDrawing) {
		super(new BorderLayout());

		setMinimumSize(new Dimension(10, 10));

		this.drawing = aDrawing;

		final GraphDrawingController controller = new GraphDrawingController(drawing);
		add(new JScrollPane(controller.getDrawingView()), BorderLayout.CENTER);
		// add(controller.scaleSelector.getComponent(), BorderLayout.NORTH);

		addComponentListener(new ComponentAdapter() {

			@Override
			public void componentResized(ComponentEvent e) {
				// TODO: i used this small shifting to avoid scrollbars to appear
				// Check if it is relevant for other OS
				// Anyway, we should find a better solution
				drawing.resizeTo(new Dimension(getSize().width - 5, getSize().height - 5));
				// revalidate();
				// repaint();
			}

		});

		validate();
	}

	public void updateGraph() {
		System.out.println("updateGraph()");
		// drawing.resizeTo(new Dimension(getSize().width - 5, getSize().height - 5));
		drawing.updateGraph();
	}

	public static class GraphDrawingController<G extends FIBGraph> extends JDianaInteractiveEditor<G> {
		// protected final JDianaScaleSelector scaleSelector;

		public GraphDrawingController(Drawing<G> aDrawing) {
			super(aDrawing, aDrawing.getFactory(), SwingViewFactory.INSTANCE, SwingToolFactory.DEFAULT);
			// scaleSelector = (JDianaScaleSelector) getToolFactory().makeDianaScaleSelector(this);
		}
	}

}
