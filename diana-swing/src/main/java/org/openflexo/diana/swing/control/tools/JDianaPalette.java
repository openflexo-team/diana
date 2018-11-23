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

import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import org.openflexo.diana.control.PaletteElement;
import org.openflexo.diana.control.PaletteModel;
import org.openflexo.diana.control.tools.DianaPalette;
import org.openflexo.diana.swing.SwingViewFactory;
import org.openflexo.diana.swing.view.JDrawingView;

/**
 * A DianaPaletteC represents the graphical tool representing a {@link PaletteModel} (the model)
 * 
 * @author sylvain
 * 
 */
public class JDianaPalette extends DianaPalette<JComponent, SwingViewFactory> {

	static final Logger logger = Logger.getLogger(JDianaPalette.class.getPackage().getName());

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
	}

	@Override
	public JScrollPane getComponent() {
		if (component == null) {
			component = new JScrollPane(getPaletteView(), ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
					ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		}
		return component;
	}

	@Override
	@SuppressWarnings("unchecked")
	public JDrawingView<PaletteModel> getPaletteView() {
		return (JDrawingView<PaletteModel>) super.getPaletteView();
	}

	private JScrollPane scrollPane;

	public JScrollPane getPaletteViewInScrollPane() {
		if (scrollPane == null) {
			scrollPane = new JScrollPane(getPaletteView(), ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
					ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
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
	}

	@Override
	public JDrawingView<?> getDrawingView() {
		return (JDrawingView<?>) super.getDrawingView();
	}

	public static class PaletteElementTransferable implements Transferable {

		private static DataFlavor _defaultFlavor;

		private final TransferedPaletteElement _transferedData;

		public PaletteElementTransferable(PaletteElement element, Point dragOrigin) {
			_transferedData = new TransferedPaletteElement(element, dragOrigin);
		}

		@Override
		public DataFlavor[] getTransferDataFlavors() {
			return new DataFlavor[] { defaultFlavor() };
		}

		@Override
		public boolean isDataFlavorSupported(DataFlavor flavor) {
			return true;
		}

		@Override
		public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
			return _transferedData;
		}

		public static DataFlavor defaultFlavor() {
			if (_defaultFlavor == null) {
				_defaultFlavor = new DataFlavor(PaletteElementTransferable.class, "PaletteElement");
			}
			return _defaultFlavor;
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
