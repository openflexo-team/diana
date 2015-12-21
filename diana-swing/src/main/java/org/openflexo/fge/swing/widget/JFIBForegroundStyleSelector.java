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

package org.openflexo.fge.swing.widget;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import org.openflexo.fge.Drawing;
import org.openflexo.fge.Drawing.PersistenceMode;
import org.openflexo.fge.DrawingGraphicalRepresentation;
import org.openflexo.fge.FGECoreUtils;
import org.openflexo.fge.FGEModelFactory;
import org.openflexo.fge.ForegroundStyle;
import org.openflexo.fge.GRBinding.DrawingGRBinding;
import org.openflexo.fge.GRBinding.ShapeGRBinding;
import org.openflexo.fge.GRProvider.DrawingGRProvider;
import org.openflexo.fge.GRProvider.ShapeGRProvider;
import org.openflexo.fge.GRStructureVisitor;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.impl.DrawingImpl;
import org.openflexo.fge.shapes.ShapeSpecification.ShapeType;
import org.openflexo.fge.swing.JDianaViewer;
import org.openflexo.fge.swing.control.SwingToolFactory;
import org.openflexo.fge.view.widget.FIBForegroundStyleSelector;
import org.openflexo.gina.FIBLibrary;
import org.openflexo.gina.controller.FIBController;
import org.openflexo.gina.model.FIBComponent;
import org.openflexo.gina.model.widget.FIBCustom;
import org.openflexo.gina.swing.view.JFIBView;
import org.openflexo.gina.swing.view.SwingViewFactory;
import org.openflexo.gina.view.GinaViewFactory;
import org.openflexo.swing.CustomPopup;

/**
 * Widget allowing to view and edit a ForegroundStyle
 * 
 * @author sguerin
 * 
 */
@SuppressWarnings("serial")
public class JFIBForegroundStyleSelector extends CustomPopup<ForegroundStyle>implements FIBForegroundStyleSelector {

	static final Logger logger = Logger.getLogger(JFIBForegroundStyleSelector.class.getPackage().getName());

	private ForegroundStyle _revertValue;

	protected ForegroundStyleDetailsPanel _selectorPanel;

	private ForegroundStylePreviewPanel foregroundStylePreviewPanel;

	public JFIBForegroundStyleSelector(ForegroundStyle editedObject) {
		super(editedObject);
		setRevertValue(editedObject != null ? (ForegroundStyle) editedObject.clone() : null);
		setFocusable(true);
	}

	@Override
	public void delete() {
		super.delete();
		foregroundStylePreviewPanel.delete();
		if (_selectorPanel != null) {
			_selectorPanel.delete();
		}
	}

	@Override
	public void init(FIBCustom component, FIBController controller) {
	}

	@Override
	public Class<ForegroundStyle> getRepresentedType() {
		return ForegroundStyle.class;
	}

	/**
	 * Return a flag indicating if equals() method should be used to determine equality.<br>
	 * For the FIBForegroundStyleSelector implementation, we MUST return false, because we can otherwise switch between ForegroundStyle
	 * which are equals, and then start to share ForegroundStyle between many GraphicalRepresentation
	 * 
	 * @return false
	 */
	@Override
	public boolean useEqualsLookup() {
		return false;
	}

	@Override
	public void setRevertValue(ForegroundStyle oldValue) {
		// WARNING: we need here to clone to keep track back of previous data !!!
		if (oldValue != null) {
			_revertValue = (ForegroundStyle) oldValue.clone();
		}
		else {
			_revertValue = null;
		}
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Sets revert value to " + _revertValue);
		}
	}

	@Override
	public ForegroundStyle getRevertValue() {
		return _revertValue;
	}

	@Override
	protected ResizablePanel createCustomPanel(ForegroundStyle editedObject) {
		_selectorPanel = makeCustomPanel(editedObject);
		return _selectorPanel;
	}

	protected ForegroundStyleDetailsPanel makeCustomPanel(ForegroundStyle editedObject) {
		return new ForegroundStyleDetailsPanel(editedObject);
	}

	@Override
	public void updateCustomPanel(ForegroundStyle editedObject) {
		if (_selectorPanel != null) {
			_selectorPanel.update();
		}
		getFrontComponent().update();
	}

	public class ForegroundStyleDetailsPanel extends ResizablePanel {
		private FIBComponent fibComponent;
		private JFIBView<?, ?> fibView;
		private CustomFIBController controller;

		protected ForegroundStyleDetailsPanel(ForegroundStyle fs) {
			super();

			fibComponent = FIBLibrary.instance().retrieveFIBComponent(FIB_FILE, true);
			controller = new CustomFIBController(fibComponent, SwingViewFactory.INSTANCE);
			fibView = (JFIBView<?, ?>) controller.buildView(fibComponent);

			controller.setDataObject(fs);

			setLayout(new BorderLayout());
			add(fibView.getResultingJComponent(), BorderLayout.CENTER);

		}

		public void update() {
			controller.setDataObject(getEditedObject(), true);
		}

		@Override
		public Dimension getDefaultSize() {
			if (fibComponent.getWidth() != null && fibComponent.getHeight() != null) {
				return new Dimension(fibComponent.getWidth(), fibComponent.getHeight());
			}
			return null;
		}

		public void delete() {
			controller.delete();
			fibView.delete();
			fibComponent = null;
			controller = null;
			fibView = null;
		}

		public class CustomFIBController extends FIBController {
			public CustomFIBController(FIBComponent component, GinaViewFactory<?> viewFactory) {
				super(component, viewFactory);
			}

			public void apply() {
				JFIBForegroundStyleSelector.this.apply();
			}

			public void cancel() {
				JFIBForegroundStyleSelector.this.cancel();
			}

			public void parameterChanged() {
				getFrontComponent().update();
			}
		}

	}

	@Override
	public void apply() {
		setRevertValue(getEditedObject() != null ? (ForegroundStyle) getEditedObject().clone() : null);
		closePopup();
		super.apply();
	}

	@Override
	public void cancel() {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("CANCEL: revert to " + getRevertValue());
		}
		setEditedObject(getRevertValue());
		closePopup();
		super.cancel();
	}

	@Override
	protected void deletePopup() {
		if (_selectorPanel != null) {
			_selectorPanel.delete();
		}
		_selectorPanel = null;
		super.deletePopup();
	}

	/* protected void pointerLeavesPopup()
	 {
	     cancel();
	 }*/

	public ForegroundStyleDetailsPanel getSelectorPanel() {
		return _selectorPanel;
	}

	@Override
	protected ForegroundStylePreviewPanel buildFrontComponent() {
		return foregroundStylePreviewPanel = new ForegroundStylePreviewPanel();
	}

	@Override
	public ForegroundStylePreviewPanel getFrontComponent() {
		return (ForegroundStylePreviewPanel) super.getFrontComponent();
	}

	/*@Override
	protected Border getDownButtonBorder()
	{
		return BorderFactory.createCompoundBorder(
				BorderFactory.createEmptyBorder(1,1,1,1),
				BorderFactory.createRaisedBevelBorder());
		//return BorderFactory.createRaisedBevelBorder();
		//return BorderFactory.createLoweredBevelBorder()
		//return BorderFactory.createEtchedBorder();
		//return BorderFactory.createBevelBorder(BevelBorder.LOWERED);
		//return BorderFactory.createBevelBorder(BevelBorder.LOWERED);
	}*/

	protected class ForegroundStylePreviewPanel extends JPanel {
		private Drawing<ForegroundStylePreviewPanel> drawing;
		private DrawingGraphicalRepresentation drawingGR;
		private JDianaViewer<ForegroundStylePreviewPanel> controller;
		private ShapeGraphicalRepresentation lineGR;

		private FGEModelFactory factory;

		protected ForegroundStylePreviewPanel() {
			super(new BorderLayout());
			setBorder(BorderFactory.createEtchedBorder(Color.GRAY, Color.LIGHT_GRAY));
			// setBorder(BorderFactory.createEtchedBorder());
			setPreferredSize(new Dimension(40, 19));
			// setBackground(Color.WHITE);

			factory = FGECoreUtils.TOOLS_FACTORY;

			drawing = new DrawingImpl<ForegroundStylePreviewPanel>(this, factory, PersistenceMode.UniqueGraphicalRepresentations) {
				@Override
				public void init() {
					final DrawingGRBinding<ForegroundStylePreviewPanel> previewPanelBinding = bindDrawing(ForegroundStylePreviewPanel.class,
							"previewPanel", new DrawingGRProvider<ForegroundStylePreviewPanel>() {
						@Override
						public DrawingGraphicalRepresentation provideGR(ForegroundStylePreviewPanel drawable, FGEModelFactory factory) {
							return drawingGR;
						}
					});
					final ShapeGRBinding<ForegroundStylePreviewPanel> shapeBinding = bindShape(ForegroundStylePreviewPanel.class, "line",
							new ShapeGRProvider<ForegroundStylePreviewPanel>() {
						@Override
						public ShapeGraphicalRepresentation provideGR(ForegroundStylePreviewPanel drawable, FGEModelFactory factory) {
							return lineGR;
						}
					});

					previewPanelBinding.addToWalkers(new GRStructureVisitor<ForegroundStylePreviewPanel>() {

						@Override
						public void visit(ForegroundStylePreviewPanel previewPanel) {
							drawShape(shapeBinding, previewPanel, previewPanel);
						}
					});
				}
			};
			drawing.setEditable(false);

			drawingGR = factory.makeDrawingGraphicalRepresentation(false);
			drawingGR.setBackgroundColor(new Color(255, 255, 255));
			drawingGR.setWidth(35);
			drawingGR.setHeight(19);
			drawingGR.setDrawWorkingArea(false);
			lineGR = factory.makeShapeGraphicalRepresentation(ShapeType.RECTANGLE);
			lineGR.setWidth(25);
			lineGR.setHeight(0);
			lineGR.setX(-5);
			lineGR.setY(-2);
			lineGR.setForeground(getEditedObject() != null ? getEditedObject() : factory.makeDefaultForegroundStyle());
			lineGR.setBackground(factory.makeEmptyBackground());
			lineGR.setShadowStyle(factory.makeNoneShadowStyle());
			lineGR.setIsSelectable(false);
			lineGR.setIsFocusable(false);
			lineGR.setIsReadOnly(true);
			lineGR.setBorder(factory.makeShapeBorder(10, 10, 10, 10));

			System.out.println("lineGR setForeground with " + getEditedObject());

			controller = new JDianaViewer<ForegroundStylePreviewPanel>(drawing, factory, SwingToolFactory.DEFAULT);
			add(controller.getDrawingView());
		}

		public void delete() {
			controller.delete();
			drawingGR.delete();
			lineGR.delete();
			drawing = null;
			lineGR = null;
			drawingGR = null;
			controller = null;
		}

		protected void update() {
			if (getEditedObject() == null) {
				return;
			}
			System.out.println("update() in PreviewPanel, lineGR setForeground with " + getEditedObject());
			System.out.println("lineGR=" + lineGR);
			lineGR.setForeground(getEditedObject());
		}

	}

}
