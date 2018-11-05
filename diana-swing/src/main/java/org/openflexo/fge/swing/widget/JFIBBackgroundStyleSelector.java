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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.openflexo.fge.BackgroundStyle;
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
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.control.AbstractDianaEditor;
import org.openflexo.fge.control.tools.BackgroundStyleFactory;
import org.openflexo.fge.impl.DrawingImpl;
import org.openflexo.fge.shapes.ShapeSpecification.ShapeType;
import org.openflexo.fge.swing.JDianaViewer;
import org.openflexo.fge.swing.control.SwingToolFactory;
import org.openflexo.fge.view.widget.FIBBackgroundStyleSelector;
import org.openflexo.gina.controller.FIBController;
import org.openflexo.gina.model.FIBComponent;
import org.openflexo.gina.model.widget.FIBCustom;
import org.openflexo.gina.swing.view.JFIBView;
import org.openflexo.gina.swing.view.SwingViewFactory;
import org.openflexo.gina.view.GinaViewFactory;
import org.openflexo.swing.CustomPopup;

/**
 * Widget allowing to view and edit a BackgroundStyle
 * 
 * @author sguerin
 * 
 */
@SuppressWarnings("serial")
public class JFIBBackgroundStyleSelector extends CustomPopup<BackgroundStyle> implements FIBBackgroundStyleSelector {

	static final Logger logger = Logger.getLogger(JFIBBackgroundStyleSelector.class.getPackage().getName());

	private static final Color DEFAULT_COLOR1 = Color.RED;
	// private static final Color DEFAULT_COLOR2 = Color.WHITE;

	private BackgroundStyleFactory factory;

	private BackgroundStyle _revertValue;

	protected BackgroundStyleDetailsPanel _selectorPanel;

	private BackgroundStylePreviewPanel backgroundStylePreviewPanel;

	public JFIBBackgroundStyleSelector(BackgroundStyleFactory factory) {
		super(factory != null ? factory.getBackgroundStyle() : null);
		this.factory = factory;
		/*if (factory != null) {
			setRevertValue(factory.getBackgroundStyle() != null ? (BackgroundStyle) factory.getBackgroundStyle().cloneObject() : null);
		}*/
		setFocusable(true);

	}

	@Override
	public BackgroundStyleFactory getFactory() {
		if (factory == null) {
			factory = new BackgroundStyleFactory(null);
		}
		return factory;
	}

	@Override
	public void setFactory(BackgroundStyleFactory factory) {
		this.factory = factory;
		setEditedObject(factory.getCurrentStyle());
		factory.getPropertyChangeSupport().addPropertyChangeListener(new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				setEditedObject(factory.getCurrentStyle());
			}
		});
	}

	@Override
	public void init(FIBCustom component, FIBController controller) {
	}

	@Override
	public void delete() {
		super.delete();
		backgroundStylePreviewPanel.delete();
		if (_selectorPanel != null) {
			_selectorPanel.delete();
		}
	}

	/**
	 * Return a flag indicating if equals() method should be used to determine equality.<br>
	 * For the FIBForegroundStyleSelector implementation, we MUST return false, because we can otherwise switch between ForegroundStyle
	 * which are equals, and then start to share BackgroundStyle between many GraphicalRepresentation
	 * 
	 * @return false
	 */
	@Override
	public boolean useEqualsLookup() {
		return false;
	}

	@Override
	public void setRevertValue(BackgroundStyle oldValue) {
		// WARNING: we need here to clone to keep track back of previous data !!!
		if (oldValue != null) {
			_revertValue = (BackgroundStyle) oldValue.clone();
		}
		else {
			_revertValue = null;
		}
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Sets revert value to " + _revertValue);
		}
	}

	@Override
	public BackgroundStyle getRevertValue() {
		return _revertValue;
	}

	@Override
	protected ResizablePanel createCustomPanel(BackgroundStyle editedObject) {
		_selectorPanel = makeCustomPanel(editedObject);
		return _selectorPanel;
	}

	protected BackgroundStyleDetailsPanel makeCustomPanel(BackgroundStyle editedObject) {
		return new BackgroundStyleDetailsPanel(editedObject);
	}

	@Override
	public void updateCustomPanel(BackgroundStyle editedObject) {
		if (_selectorPanel != null) {
			_selectorPanel.update();
		}
		getFrontComponent().update();
	}

	public class BackgroundStyleDetailsPanel extends ResizablePanel {
		private FIBComponent fibComponent;
		private JFIBView<?, ?> fibView;
		private CustomFIBController controller;

		// private BackgroundStyleFactory bsFactory;

		protected BackgroundStyleDetailsPanel(BackgroundStyle backgroundStyle) {
			super();

			fibComponent = AbstractDianaEditor.EDITOR_FIB_LIBRARY.retrieveFIBComponent(FIB_FILE, true);
			controller = new CustomFIBController(fibComponent, SwingViewFactory.INSTANCE);
			fibView = (JFIBView<?, ?>) controller.buildView(fibComponent, null, true);

			controller.setDataObject(getFactory());

			setLayout(new BorderLayout());
			add(fibView.getResultingJComponent(), BorderLayout.CENTER);
		}

		public void update() {
			// logger.info("Update with " + getEditedObject());
			// getFactory().setBackgroundStyle(getEditedObject());
			controller.setDataObject(getFactory(), true);
		}

		@Override
		public Dimension getDefaultSize() {
			return new Dimension(fibComponent.getWidth(), fibComponent.getHeight());
		}

		public void delete() {
			controller.delete();
			fibView.delete();
			getFactory().delete();
			fibComponent = null;
			controller = null;
			fibView = null;
			factory = null;
		}

		public class CustomFIBController extends FIBController {
			public CustomFIBController(FIBComponent component, GinaViewFactory<?> viewFactory) {
				super(component, viewFactory);
			}

			public void apply() {
				setEditedObject(getFactory().getBackgroundStyle());
				JFIBBackgroundStyleSelector.this.apply();
			}

			public void cancel() {
				JFIBBackgroundStyleSelector.this.cancel();
			}

			public void parameterChanged() {
				// System.out.println("parameterChanged() for "+bsFactory.getBackgroundStyle());
				setEditedObject(getFactory().getBackgroundStyle());
				getFrontComponent().update();
				// notifyApplyPerformed();
			}

			public void backgroundStyleChanged() {
				// System.out.println("backgroundStyleChanged() for "+bsFactory.getBackgroundStyle());
				setEditedObject(getFactory().getBackgroundStyle());
				getFrontComponent().update();
				notifyApplyPerformed();
			}

		}

	}

	/* @Override
	 public void setEditedObject(BackgroundStyle object)
	 {
	 	logger.info("setEditedObject with "+object);
	 	super.setEditedObject(object);
	 }*/

	@Override
	public void apply() {
		setRevertValue(getEditedObject() != null ? (BackgroundStyle) getEditedObject().clone() : null);
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

	/*protected void pointerLeavesPopup()
	{
	   cancel();
	}*/

	public BackgroundStyleDetailsPanel getSelectorPanel() {
		return _selectorPanel;
	}

	@Override
	protected BackgroundStylePreviewPanel buildFrontComponent() {
		return backgroundStylePreviewPanel = new BackgroundStylePreviewPanel();
	}

	@Override
	public BackgroundStylePreviewPanel getFrontComponent() {
		return (BackgroundStylePreviewPanel) super.getFrontComponent();
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

	protected class BackgroundStylePreviewPanel extends JPanel {
		private Drawing<BackgroundStylePreviewPanel> drawing;
		private DrawingGraphicalRepresentation drawingGR;
		private JDianaViewer<BackgroundStylePreviewPanel> controller;
		private ShapeGraphicalRepresentation rectGR;

		private FGEModelFactory factory;

		protected BackgroundStylePreviewPanel() {
			super(new BorderLayout());
			setBorder(BorderFactory.createEtchedBorder(Color.GRAY, Color.LIGHT_GRAY));
			// setBorder(BorderFactory.createEtchedBorder());
			setPreferredSize(new Dimension(40, 19));
			// setBackground(Color.WHITE);

			factory = FGECoreUtils.TOOLS_FACTORY;

			drawing = new DrawingImpl<BackgroundStylePreviewPanel>(this, factory, PersistenceMode.UniqueGraphicalRepresentations) {
				@Override
				public void init() {
					final DrawingGRBinding<BackgroundStylePreviewPanel> previewPanelBinding = bindDrawing(BackgroundStylePreviewPanel.class,
							"previewPanel", new DrawingGRProvider<BackgroundStylePreviewPanel>() {
								@Override
								public DrawingGraphicalRepresentation provideGR(BackgroundStylePreviewPanel drawable,
										FGEModelFactory factory) {
									return drawingGR;
								}
							});
					final ShapeGRBinding<BackgroundStylePreviewPanel> shapeBinding = bindShape(BackgroundStylePreviewPanel.class, "line",
							new ShapeGRProvider<BackgroundStylePreviewPanel>() {
								@Override
								public ShapeGraphicalRepresentation provideGR(BackgroundStylePreviewPanel drawable,
										FGEModelFactory factory) {
									return rectGR;
								}
							});

					previewPanelBinding.addToWalkers(new GRStructureVisitor<BackgroundStylePreviewPanel>() {

						@Override
						public void visit(BackgroundStylePreviewPanel previewPanel) {
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
			rectGR = factory.makeShapeGraphicalRepresentation(ShapeType.RECTANGLE);
			rectGR.setWidth(36);
			rectGR.setHeight(20);
			rectGR.setX(0);
			rectGR.setY(0);
			rectGR.setForeground(factory.makeNoneForegroundStyle());
			rectGR.setBackground(getEditedObject() != null ? getEditedObject() : factory.makeColoredBackground(DEFAULT_COLOR1));
			rectGR.setShadowStyle(factory.makeNoneShadowStyle());
			rectGR.setIsSelectable(false);
			rectGR.setIsFocusable(false);
			rectGR.setIsReadOnly(true);
			// rectGR.setBorder(factory.makeShapeBorder(0, 0, 0, 0));

			controller = new JDianaViewer<BackgroundStylePreviewPanel>(drawing, factory, SwingToolFactory.DEFAULT);
			add(controller.getDrawingView());
		}

		public void delete() {
			drawingGR.delete();
			rectGR.delete();
			controller.delete();
			drawing = null;
			controller = null;
			drawingGR = null;
			rectGR = null;
		}

		protected void update() {
			rectGR.setBackground(getEditedObject() != null ? getEditedObject() : factory.makeColoredBackground(DEFAULT_COLOR1));
			// We do it later because producer of texture may not has finished its job
			SwingUtilities.invokeLater(() -> {
				if (getEditedObject() == null) {
					return;
				}
				BackgroundStyle bg = getEditedObject();
				rectGR.setBackground(bg);
			});
		}
	}

	@Override
	public Class<BackgroundStyle> getRepresentedType() {
		return BackgroundStyle.class;
	}

}
