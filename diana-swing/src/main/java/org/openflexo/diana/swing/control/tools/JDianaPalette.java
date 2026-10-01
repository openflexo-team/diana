/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * Copyright (c) 2011-2012, AgileBirds
 * 
 * This file is part of Diana-swing, a component of the software infrastructure 
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

package org.openflexo.diana.swing.control.tools;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.IOException;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import org.openflexo.diana.DianaLayoutManager;
import org.openflexo.diana.Drawing.RootNode;
import org.openflexo.diana.control.PaletteElement;
import org.openflexo.diana.geom.DianaDimension;
import org.openflexo.diana.control.PaletteModel;
import org.openflexo.diana.control.tools.DianaPalette;
import org.openflexo.diana.swing.SwingViewFactory;
import org.openflexo.diana.swing.view.JDrawingView;

/**
 * A {@link JDianaPalette} is the swing implementation of the graphical tool representing a {@link PaletteModel} (the model)
 * 
 * @author sylvain
 * 
 */
public class JDianaPalette extends DianaPalette<JComponent, SwingViewFactory> {

	static final Logger logger = Logger.getLogger(JDianaPalette.class.getPackage().getName());

	final public static DataFlavor PALETTE_ELEMENT_FLAVOR = new DataFlavor(PaletteElementTransferable.class, "PaletteElement");

	private JScrollPane component;

	public JDianaPalette(PaletteModel palette) {
		super(palette);
	}

	@Override
	protected void updatePalette(PaletteModel palette) {
		super.updatePalette(palette);
		if (component != null) {
			component.setViewportView(getPaletteView());
		}
		updateFittingComponent();
	}

	@Override
	public JScrollPane getComponent() {
		if (component == null) {
			component = new JScrollPane(getPaletteView(), ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
					ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			trackViewportWidth(component);
		}
		return component;
	}

	@Override
	@SuppressWarnings("unchecked")
	public JDrawingView<PaletteModel> getPaletteView() {
		return (JDrawingView<PaletteModel>) super.getPaletteView();
	}

	private JScrollPane scrollPane;

	/**
	 * Palette elements wrap according to the available width: make the palette drawing width follow the viewport width
	 */
	private void trackViewportWidth(final JScrollPane pane) {
		pane.getViewport().addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				updatePaletteSize(pane.getViewport().getWidth(), pane.getViewport().getHeight(), pane);
			}
		});
	}

	private JPanel fittingComponent;

	/**
	 * Return a component showing the palette without scrolling: its width follows the width it is given, its height is the one of the
	 * wrapped lines of elements. To be used in a container providing the scrolling, such as a
	 * {@link org.openflexo.swing.FlexoCollabsiblePanelGroup}
	 */
	public JComponent getFittingComponent() {
		if (fittingComponent == null) {
			fittingComponent = new JPanel(new BorderLayout());
			fittingComponent.setOpaque(false);
			fittingComponent.addComponentListener(new ComponentAdapter() {
				@Override
				public void componentResized(ComponentEvent e) {
					updatePaletteSize(fittingComponent.getWidth(), 0, fittingComponent);
				}
			});
			updateFittingComponent();
		}
		return fittingComponent;
	}

	private void updateFittingComponent() {
		if (fittingComponent != null) {
			fittingComponent.removeAll();
			fittingComponent.add(getPaletteView(), BorderLayout.CENTER);
			fittingComponent.revalidate();
		}
	}

	/**
	 * Resize the palette drawing to the available size, enlarged if required by the wrap-flow layout (width of the widest element, height
	 * of the wrapped lines), then revalidate supplied container.<br>
	 * This is computed here since the root node does not enforce the minimal size requested by its layout managers.
	 */
	private void updatePaletteSize(double availableWidth, double availableHeight, JComponent container) {
		if (getPaletteDrawing() == null || getPaletteDrawing().getRoot() == null) {
			return;
		}
		RootNode<PaletteModel> root = getPaletteDrawing().getRoot();
		double width = availableWidth;
		double height = availableHeight;
		if (width <= 0) {
			return;
		}
		double minHeight = 0;
		for (DianaLayoutManager<?, PaletteModel> lm : root.getLayoutManagers()) {
			width = Math.max(width, lm.getMinimumWidth());
		}
		for (DianaLayoutManager<?, PaletteModel> lm : root.getLayoutManagers()) {
			minHeight = Math.max(minHeight, lm.getMinimumHeightForWidth(width));
		}
		DianaDimension newSize = new DianaDimension(width, Math.max(height, minHeight));
		if (newSize.equals(root.getSize())) {
			return;
		}
		// Single update (one relayout, one repaint): nodes may not leave the container bounds, so width and height must be
		// applied together, the container being tall enough when the relayout is triggered
		root.setSize(newSize);
		container.revalidate();
	}

	public JScrollPane getPaletteViewInScrollPane() {
		if (scrollPane == null) {
			scrollPane = new JScrollPane(getPaletteView(), ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
					ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			trackViewportWidth(scrollPane);
		}
		return scrollPane;
	}

	@Override
	public SwingViewFactory getDianaFactory() {
		return SwingViewFactory.INSTANCE;
	}

	/*public DianaViewDropListener buildDropListener(JDianaView<?, ?> dropContainer, AbstractDianaEditor<?, ?, ?> controller) {
		return new DianaViewDropListener(dropContainer, controller);
	}*/

	@Override
	public void updatePalette() {
		super.updatePalette();
		if (component != null) {
			component.setViewportView(getPaletteView());
		}
		updateFittingComponent();
	}

	@Override
	public JDrawingView<?> getDrawingView() {
		return (JDrawingView<?>) super.getDrawingView();
	}

	public static class PaletteElementTransferable implements Transferable {

		private final TransferedPaletteElement _transferedData;

		public PaletteElementTransferable(PaletteElement element, Point dragOrigin) {
			_transferedData = new TransferedPaletteElement(element, dragOrigin);
		}

		@Override
		public DataFlavor[] getTransferDataFlavors() {
			return new DataFlavor[] { PALETTE_ELEMENT_FLAVOR };
		}

		@Override
		public boolean isDataFlavorSupported(DataFlavor flavor) {
			return true;
		}

		@Override
		public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
			return _transferedData;
		}
	}

	public static class TransferedPaletteElement {
		private final Point _offset;

		private final PaletteElement _transfered;

		public TransferedPaletteElement(PaletteElement element, Point dragOffset) {
			super();
			_transfered = element;
			_offset = dragOffset;
		}

		public Point getOffset() {
			return _offset;
		}

		public PaletteElement getPaletteElement() {
			return _transfered;
		}

	}

}
