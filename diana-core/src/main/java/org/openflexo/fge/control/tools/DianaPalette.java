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

package org.openflexo.fge.control.tools;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.dnd.DragSource;
import java.beans.PropertyChangeEvent;
import java.util.logging.Logger;

import org.openflexo.antar.binding.DataBinding;
import org.openflexo.fge.Drawing;
import org.openflexo.fge.DrawingGraphicalRepresentation;
import org.openflexo.fge.FGEModelFactory;
import org.openflexo.fge.GRBinding.DrawingGRBinding;
import org.openflexo.fge.GRBinding.ShapeGRBinding;
import org.openflexo.fge.GRProvider.DrawingGRProvider;
import org.openflexo.fge.GRProvider.ShapeGRProvider;
import org.openflexo.fge.GRStructureVisitor;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.control.AbstractDianaEditor;
import org.openflexo.fge.control.DianaInteractiveEditor;
import org.openflexo.fge.control.DrawingPalette;
import org.openflexo.fge.control.PaletteElement;
import org.openflexo.fge.impl.DrawingImpl;
import org.openflexo.fge.view.DianaViewFactory;
import org.openflexo.fge.view.DrawingView;
import org.openflexo.fib.utils.FIBIconLibrary;
import org.openflexo.toolbox.ToolBox;

/**
 * A DianaPaletteC represents the graphical tool representing a {@link DrawingPalette} (the model)
 * 
 * @author sylvain
 * 
 */
public abstract class DianaPalette<C, F extends DianaViewFactory<F, ? super C>> extends DianaToolImpl<C, F> {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(DianaPalette.class.getPackage().getName());

	private static Image DROP_OK_IMAGE = FIBIconLibrary.DROP_OK_CURSOR.getImage();
	private static Image DROP_KO_IMAGE = FIBIconLibrary.DROP_KO_CURSOR.getImage();

	public static final Cursor dropOK = ToolBox.getPLATFORM() == ToolBox.MACOS ? Toolkit.getDefaultToolkit().createCustomCursor(
			DROP_OK_IMAGE, new Point(16, 16), "Drop OK") : DragSource.DefaultMoveDrop;

	public static final Cursor dropKO = ToolBox.getPLATFORM() == ToolBox.MACOS ? Toolkit.getDefaultToolkit().createCustomCursor(
			DROP_KO_IMAGE, new Point(16, 16), "Drop KO") : DragSource.DefaultMoveNoDrop;

	private DrawingPalette palette = null;

	private PaletteDrawing paletteDrawing;
	// This controller is the local controller for displaying the palette, NOT the controller
	// Which this palette is associated to.
	private PaletteController<?, ?> paletteController;

	// private DragSourceContext dragSourceContext;

	public DianaPalette(DrawingPalette palette) {
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

	public DrawingPalette getPalette() {
		return palette;
	}

	public void setPalette(DrawingPalette palette) {
		if (palette != this.palette) {
			updatePalette(palette);
		}
	}

	protected void updatePalette(DrawingPalette palette) {
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

	public DrawingView<DrawingPalette, ?> getPaletteView() {
		if (paletteController == null) {
			return null;
		}
		return paletteController.getDrawingView();
	}

	public PaletteDrawing getPaletteDrawing() {
		return paletteDrawing;
	}

	public static class PaletteDrawing extends DrawingImpl<DrawingPalette> implements Drawing<DrawingPalette> {

		private final DrawingGraphicalRepresentation gr;

		private PaletteDrawing(DrawingPalette palette) {
			super(palette, DrawingPalette.FACTORY, PersistenceMode.UniqueGraphicalRepresentations);
			gr = DrawingPalette.FACTORY.makeDrawingGraphicalRepresentation(false);
			gr.setWidth(palette.getWidth());
			gr.setHeight(palette.getHeight());
			gr.setBackgroundColor(Color.WHITE);
			gr.setDrawWorkingArea(palette.getDrawWorkingArea());
			setEditable(true);
		}

		@Override
		public void init() {

			final DrawingGRBinding<DrawingPalette> paletteBinding = bindDrawing(DrawingPalette.class, "palette",
					new DrawingGRProvider<DrawingPalette>() {
						@Override
						public DrawingGraphicalRepresentation provideGR(DrawingPalette drawable, FGEModelFactory factory) {
							return gr;
						}
					});
			final ShapeGRBinding<PaletteElement> paletteElementBinding = bindShape(PaletteElement.class, "paletteElement",
					new ShapeGRProvider<PaletteElement>() {
						@Override
						public ShapeGraphicalRepresentation provideGR(PaletteElement drawable, FGEModelFactory factory) {
							return drawable.getGraphicalRepresentation();
						}
					});

			paletteBinding.addToWalkers(new GRStructureVisitor<DrawingPalette>() {

				@Override
				public void visit(DrawingPalette palette) {
					for (PaletteElement element : palette.getElements()) {
						drawShape(paletteElementBinding, element, palette);
					}
				}
			});

			paletteElementBinding.setDynamicPropertyValue(GraphicalRepresentation.TEXT, new DataBinding<String>("drawable.name"), false);

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
