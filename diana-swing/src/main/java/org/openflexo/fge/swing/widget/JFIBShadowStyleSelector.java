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
import org.openflexo.fge.GRBinding.DrawingGRBinding;
import org.openflexo.fge.GRBinding.ShapeGRBinding;
import org.openflexo.fge.GRProvider.DrawingGRProvider;
import org.openflexo.fge.GRProvider.ShapeGRProvider;
import org.openflexo.fge.GRStructureVisitor;
import org.openflexo.fge.ShadowStyle;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.control.AbstractDianaEditor;
import org.openflexo.fge.impl.DrawingImpl;
import org.openflexo.fge.shapes.ShapeSpecification.ShapeType;
import org.openflexo.fge.swing.JDianaViewer;
import org.openflexo.fge.swing.control.SwingToolFactory;
import org.openflexo.fge.view.widget.FIBShadowStyleSelector;
import org.openflexo.gina.controller.FIBController;
import org.openflexo.gina.model.FIBComponent;
import org.openflexo.gina.model.widget.FIBCustom;
import org.openflexo.gina.swing.view.JFIBView;
import org.openflexo.gina.swing.view.SwingViewFactory;
import org.openflexo.gina.view.GinaViewFactory;
import org.openflexo.swing.CustomPopup;

/**
 * Widget allowing to view and edit a ShadowStyle
 * 
 * @author sguerin
 * 
 */
@SuppressWarnings("serial")
public class JFIBShadowStyleSelector extends CustomPopup<ShadowStyle> implements FIBShadowStyleSelector {

	static final Logger logger = Logger.getLogger(JFIBShadowStyleSelector.class.getPackage().getName());

	private ShadowStyle _revertValue;

	protected ShadowStyleDetailsPanel _selectorPanel;

	private ShadowStylePreviewPanel shadowStylePreviewPanel;

	public JFIBShadowStyleSelector(ShadowStyle editedObject) {
		super(editedObject);
		setRevertValue(editedObject != null ? (ShadowStyle) editedObject.clone() : null);
		setFocusable(true);
	}

	@Override
	public void delete() {
		super.delete();
		shadowStylePreviewPanel.delete();
		if (_selectorPanel != null) {
			_selectorPanel.delete();
		}
	}

	@Override
	public void init(FIBCustom component, FIBController controller) {
	}

	/**
	 * Return a flag indicating if equals() method should be used to determine equality.<br>
	 * For the FIBForegroundStyleSelector implementation, we MUST return false, because we can otherwise switch between ForegroundStyle
	 * which are equals, and then start to share ShadowStyle between many GraphicalRepresentation
	 * 
	 * @return false
	 */
	@Override
	public boolean useEqualsLookup() {
		return false;
	}

	@Override
	public void setRevertValue(ShadowStyle oldValue) {
		// WARNING: we need here to clone to keep track back of previous data !!!
		if (oldValue != null) {
			_revertValue = (ShadowStyle) oldValue.clone();
		}
		else {
			_revertValue = null;
		}
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Sets revert value to " + _revertValue);
		}
	}

	@Override
	public ShadowStyle getRevertValue() {
		return _revertValue;
	}

	@Override
	protected ResizablePanel createCustomPanel(ShadowStyle editedObject) {
		_selectorPanel = makeCustomPanel(editedObject);
		return _selectorPanel;
	}

	protected ShadowStyleDetailsPanel makeCustomPanel(ShadowStyle editedObject) {
		return new ShadowStyleDetailsPanel(editedObject);
	}

	@Override
	public void updateCustomPanel(ShadowStyle editedObject) {
		if (_selectorPanel != null) {
			_selectorPanel.update();
		}
		getFrontComponent().update();
	}

	public class ShadowStyleDetailsPanel extends ResizablePanel {
		private FIBComponent fibComponent;
		private JFIBView<?, ?> fibView;
		private CustomFIBController controller;

		protected ShadowStyleDetailsPanel(ShadowStyle shadowStyle) {
			super();

			fibComponent = AbstractDianaEditor.EDITOR_FIB_LIBRARY.retrieveFIBComponent(FIB_FILE, true);
			controller = new CustomFIBController(fibComponent, SwingViewFactory.INSTANCE);
			fibView = (JFIBView<?, ?>) controller.buildView(fibComponent, null, true);

			controller.setDataObject(shadowStyle);

			setLayout(new BorderLayout());
			add(fibView.getResultingJComponent(), BorderLayout.CENTER);

		}

		public void update() {
			controller.setDataObject(getEditedObject(), true);
		}

		@Override
		public Dimension getDefaultSize() {
			return new Dimension(fibComponent.getWidth(), fibComponent.getHeight());
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
				JFIBShadowStyleSelector.this.apply();
			}

			public void cancel() {
				JFIBShadowStyleSelector.this.cancel();
			}

			public void parameterChanged() {
				getFrontComponent().update();
			}

		}

	}

	@Override
	public void apply() {
		setRevertValue(getEditedObject() != null ? (ShadowStyle) getEditedObject().clone() : null);
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

	public ShadowStyleDetailsPanel getSelectorPanel() {
		return _selectorPanel;
	}

	@Override
	protected ShadowStylePreviewPanel buildFrontComponent() {
		return shadowStylePreviewPanel = new ShadowStylePreviewPanel();
	}

	@Override
	public ShadowStylePreviewPanel getFrontComponent() {
		return (ShadowStylePreviewPanel) super.getFrontComponent();
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

	protected class ShadowStylePreviewPanel extends JPanel {
		private Drawing<ShadowStylePreviewPanel> drawing;
		private DrawingGraphicalRepresentation drawingGR;
		private JDianaViewer<ShadowStylePreviewPanel> controller;
		private ShapeGraphicalRepresentation shapeGR;

		private FGEModelFactory factory;

		protected ShadowStylePreviewPanel() {
			super(new BorderLayout());
			setBorder(BorderFactory.createEtchedBorder(Color.GRAY, Color.LIGHT_GRAY));
			// setBorder(BorderFactory.createEtchedBorder());
			setPreferredSize(new Dimension(40, 19));
			// setBackground(Color.WHITE);

			factory = FGECoreUtils.TOOLS_FACTORY;

			drawing = new DrawingImpl<ShadowStylePreviewPanel>(this, factory, PersistenceMode.UniqueGraphicalRepresentations) {
				@Override
				public void init() {
					final DrawingGRBinding<ShadowStylePreviewPanel> previewPanelBinding = bindDrawing(ShadowStylePreviewPanel.class,
							"previewPanel", new DrawingGRProvider<ShadowStylePreviewPanel>() {
								@Override
								public DrawingGraphicalRepresentation provideGR(ShadowStylePreviewPanel drawable, FGEModelFactory factory) {
									return drawingGR;
								}
							});
					final ShapeGRBinding<ShadowStylePreviewPanel> shapeBinding = bindShape(ShadowStylePreviewPanel.class, "line",
							new ShapeGRProvider<ShadowStylePreviewPanel>() {
								@Override
								public ShapeGraphicalRepresentation provideGR(ShadowStylePreviewPanel drawable, FGEModelFactory factory) {
									return shapeGR;
								}
							});

					previewPanelBinding.addToWalkers(new GRStructureVisitor<ShadowStylePreviewPanel>() {

						@Override
						public void visit(ShadowStylePreviewPanel previewPanel) {
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
			shapeGR = factory.makeShapeGraphicalRepresentation(ShapeType.RECTANGLE);
			shapeGR.setWidth(130);
			shapeGR.setHeight(130);
			shapeGR.setAllowToLeaveBounds(true);
			shapeGR.setX(-130);
			shapeGR.setY(-143);
			shapeGR.setForeground(factory.makeForegroundStyle(Color.BLACK));
			shapeGR.setBackground(factory.makeColoredBackground(new Color(252, 242, 175)));

			shapeGR.setIsSelectable(false);
			shapeGR.setIsFocusable(false);
			shapeGR.setIsReadOnly(true);
			// shapeGR.setBorder(factory.makeShapeBorder(20, 20, 20, 20));

			update();

			controller = new JDianaViewer<ShadowStylePreviewPanel>(drawing, factory, SwingToolFactory.DEFAULT);
			add(controller.getDrawingView());

		}

		public void delete() {
			controller.delete();
			drawingGR.delete();
			shapeGR.delete();
			drawing = null;
		}

		protected void update() {
			if (getEditedObject() == null) {
				return;
			}
			shapeGR.setShadowStyle(getEditedObject());
		}

	}

	@Override
	public Class<ShadowStyle> getRepresentedType() {
		return ShadowStyle.class;
	}

}
