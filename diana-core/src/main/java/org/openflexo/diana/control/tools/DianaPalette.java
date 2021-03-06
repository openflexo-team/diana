/**
 * 
 * Copyright (c) 2014, Openflexo
 * 
 * This file is part of Diana-core, a component of the software infrastructure 
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

package org.openflexo.diana.control.tools;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.dnd.DragSource;
import java.beans.PropertyChangeEvent;
import java.util.logging.Logger;

import org.openflexo.diana.DianaModelFactory;
import org.openflexo.diana.DrawingGraphicalRepresentation;
import org.openflexo.diana.GRBinding.DrawingGRBinding;
import org.openflexo.diana.GRBinding.ShapeGRBinding;
import org.openflexo.diana.GRProvider.DrawingGRProvider;
import org.openflexo.diana.GRProvider.ShapeGRProvider;
import org.openflexo.diana.GRStructureVisitor;
import org.openflexo.diana.ShapeGraphicalRepresentation;
import org.openflexo.diana.animation.Animation;
import org.openflexo.diana.control.AbstractDianaEditor;
import org.openflexo.diana.control.DianaInteractiveEditor;
import org.openflexo.diana.control.PaletteElement;
import org.openflexo.diana.control.PaletteModel;
import org.openflexo.diana.impl.DrawingImpl;
import org.openflexo.diana.view.DianaViewFactory;
import org.openflexo.diana.view.DrawingView;
import org.openflexo.gina.utils.FIBIconLibrary;
import org.openflexo.toolbox.ToolBox;

/**
 * A DianaPaletteC represents the graphical tool representing a {@link PaletteModel} (the model)
 * 
 * @author sylvain
 * 
 */
public abstract class DianaPalette<C, F extends DianaViewFactory<F, ? super C>> extends DianaToolImpl<C, F> {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(DianaPalette.class.getPackage().getName());

	private static Image DROP_OK_IMAGE = FIBIconLibrary.DROP_OK_CURSOR.getImage();
	private static Image DROP_KO_IMAGE = FIBIconLibrary.DROP_KO_CURSOR.getImage();

	public static final Cursor dropOK = ToolBox.isMacOS()
			? Toolkit.getDefaultToolkit().createCustomCursor(DROP_OK_IMAGE, new Point(16, 16), "Drop OK")
			: DragSource.DefaultMoveDrop;

	public static final Cursor dropKO = ToolBox.isMacOS()
			? Toolkit.getDefaultToolkit().createCustomCursor(DROP_KO_IMAGE, new Point(16, 16), "Drop KO")
			: DragSource.DefaultMoveNoDrop;

	private PaletteModel palette = null;

	private PaletteDrawing paletteDrawing;
	// This controller is the local controller for displaying the palette, NOT the controller
	// Which this palette is associated to.
	private PaletteController<?, ?> paletteController;

	// private DragSourceContext dragSourceContext;

	public DianaPalette(PaletteModel palette) {
		super();
		setPalette(palette);
	}

	/**
	 * Return the technology-specific component representing the palette
	 * 
	 * @return
	 */
	public abstract C getComponent();

	@Override
	public void attachToEditor(AbstractDianaEditor<?, F, ?> editor) {
		super.attachToEditor(editor);
		if (editor instanceof DianaInteractiveEditor) {
			((DianaInteractiveEditor<?, F, ?>) editor).activatePalette(this);
		}
	}

	public PaletteModel getPalette() {
		return palette;
	}

	public void setPalette(PaletteModel palette) {
		if (palette != this.palette) {
			updatePalette(palette);
		}
	}

	protected void updatePalette(PaletteModel palette) {
		if (paletteController != null) {
			paletteController.delete();
		}
		if (paletteDrawing != null) {
			paletteDrawing.delete();
		}
		this.palette = palette;
		paletteDrawing = new PaletteDrawing(palette);
		paletteController = getDianaFactory().makePaletteController(this);
	}

	public void delete() {
		if (paletteController != null) {
			paletteController.delete();
		}
		if (paletteDrawing != null) {
			paletteDrawing.delete();
		}
	}

	public DrawingView<PaletteModel, ?> getPaletteView() {
		if (paletteController == null) {
			return null;
		}
		return paletteController.getDrawingView();
	}

	public PaletteDrawing getPaletteDrawing() {
		return paletteDrawing;
	}

	public static class PaletteDrawing extends DrawingImpl<PaletteModel> {

		private final DrawingGraphicalRepresentation gr;

		private PaletteDrawing(PaletteModel palette) {
			super(palette, PaletteModel.FACTORY, PersistenceMode.UniqueGraphicalRepresentations);
			gr = PaletteModel.FACTORY.makeDrawingGraphicalRepresentation(false);
			gr.setWidth(palette.getPaletteWidth());
			gr.setHeight(palette.getPaletteHeight());
			gr.setBackgroundColor(Color.WHITE);
			gr.setDrawWorkingArea(palette.getDrawWorkingArea());
			setEditable(true);
		}

		@Override
		public void init() {

			final DrawingGRBinding<PaletteModel> paletteBinding = bindDrawing(PaletteModel.class, "palette",
					new DrawingGRProvider<PaletteModel>() {
						@Override
						public DrawingGraphicalRepresentation provideGR(PaletteModel drawable, DianaModelFactory factory) {
							return gr;
						}
					});
			final ShapeGRBinding<PaletteElement> paletteElementBinding = bindShape(PaletteElement.class, "paletteElement",
					new ShapeGRProvider<PaletteElement>() {
						@Override
						public ShapeGraphicalRepresentation provideGR(PaletteElement drawable, DianaModelFactory factory) {
							return drawable.getGraphicalRepresentation();
						}
					});

			paletteBinding.addToWalkers(new GRStructureVisitor<PaletteModel>() {

				@Override
				public void visit(PaletteModel palette) {
					for (PaletteElement element : palette.getElements()) {
						drawShape(paletteElementBinding, element, palette);
					}
				}
			});

			// Do not display palette element label
			// paletteElementBinding.setDynamicPropertyValue(GraphicalRepresentation.TEXT, new DataBinding<String>("drawable.name"), false);

		}

		@Override
		public void startAnimation(Animation animation) {
		}

		@Override
		public void stopAnimation(Animation animation) {
		}
	}

	/**
	 * Return the DrawingView of the controller this palette is associated to
	 * 
	 * @return
	 */
	public DrawingView<?, ?> getDrawingView() {
		if (getEditor() != null) {
			return getEditor().getDrawingView();
		}
		return null;
	}

	public void updatePalette() {
		paletteController.rebuildDrawingView();
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
	}

}
