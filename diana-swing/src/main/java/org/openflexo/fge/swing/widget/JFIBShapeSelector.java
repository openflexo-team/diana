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
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComponent;

import org.openflexo.fge.control.AbstractDianaEditor;
import org.openflexo.fge.control.tools.ShapeSpecificationFactory;
import org.openflexo.fge.shapes.ShapeSpecification;
import org.openflexo.fge.view.widget.FIBShapeSelector;
import org.openflexo.fge.view.widget.ShapePreviewPanel;
import org.openflexo.gina.controller.FIBController;
import org.openflexo.gina.model.FIBComponent;
import org.openflexo.gina.model.widget.FIBCustom;
import org.openflexo.gina.swing.view.JFIBView;
import org.openflexo.gina.swing.view.SwingViewFactory;
import org.openflexo.gina.view.FIBView;
import org.openflexo.gina.view.GinaViewFactory;
import org.openflexo.gina.view.widget.FIBCustomWidget;
import org.openflexo.swing.CustomPopup;

/**
 * Widget allowing to view and edit a ShapeSpecification
 * 
 * @author sguerin
 * 
 */
@SuppressWarnings("serial")
public class JFIBShapeSelector extends CustomPopup<ShapeSpecification> implements FIBShapeSelector {

	static final Logger logger = Logger.getLogger(JFIBShapeSelector.class.getPackage().getName());

	private ShapeSpecification _revertValue;

	protected ShapeDetailsPanel _selectorPanel;
	private JShapePreviewPanel frontComponent;

	private ShapeSpecificationFactory factory;

	public JFIBShapeSelector(ShapeSpecificationFactory factory) {
		super(factory != null ? factory.getShapeSpecification() : null);
		this.factory = factory;
		// setRevertValue(factory.getShape() != null ? (ShapeSpecification) factory.getShape().clone() : null);
		setFocusable(true);
	}

	@Override
	public ShapeSpecificationFactory getFactory() {
		if (factory == null) {
			factory = new ShapeSpecificationFactory(null);
		}
		return factory;
	}

	@Override
	public void setFactory(ShapeSpecificationFactory factory) {
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
	public void delete() {
		super.delete();
		if (_selectorPanel != null) {
			_selectorPanel.delete();
		}
	}

	@Override
	public void init(FIBCustom component, FIBController controller) {
	}

	@Override
	public void setRevertValue(ShapeSpecification oldValue) {
		// WARNING: we need here to clone to keep track back of previous data
		// !!!
		if (oldValue != null) {
			_revertValue = (ShapeSpecification) oldValue.clone();
		}
		else {
			_revertValue = null;
		}
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Sets revert value to " + _revertValue);
		}
	}

	@Override
	public ShapeSpecification getRevertValue() {
		return _revertValue;
	}

	@Override
	protected ResizablePanel createCustomPanel(ShapeSpecification editedObject) {
		_selectorPanel = makeCustomPanel(editedObject);
		return _selectorPanel;
	}

	protected ShapeDetailsPanel makeCustomPanel(ShapeSpecification editedObject) {
		return new ShapeDetailsPanel(editedObject);
	}

	@Override
	public void updateCustomPanel(ShapeSpecification editedObject) {
		if (_selectorPanel != null) {
			_selectorPanel.update();
		}
		getFrontComponent().setShape(editedObject);
		// getFrontComponent().update();
	}

	public class ShapeDetailsPanel extends ResizablePanel {
		private FIBComponent fibComponent;
		private JFIBView<?, ?> fibView;
		private CustomFIBController controller;

		protected ShapeDetailsPanel(ShapeSpecification backgroundStyle) {
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
			controller.setDataObject(getFactory(), true);
		}

		@Override
		public Dimension getDefaultSize() {
			return new Dimension(fibComponent.getWidth(), fibComponent.getHeight());
		}

		public void delete() {
			controller.delete();
			fibView.delete();
			factory.delete();
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
				setEditedObject(getFactory().getShapeSpecification());
				JFIBShapeSelector.this.apply();
			}

			public void cancel() {
				JFIBShapeSelector.this.cancel();
			}

			public void shapeChanged() {

				getFrontComponent().setShape(getFactory().getShapeSpecification());
				// getFrontComponent().update();

				FIBView<?, ?> previewComponent = viewForComponent(fibComponent.getComponentNamed("PreviewPanel"));
				if (previewComponent instanceof FIBCustomWidget) {
					JComponent customComponent = (JComponent) ((FIBCustomWidget<?, ?, ?>) previewComponent).getTechnologyComponent();
					if (customComponent instanceof ShapePreviewPanel) {
						((JShapePreviewPanel) customComponent).setShape(getFactory().getShapeSpecification());
						// ((ShapePreviewPanel) customComponent).update();
					}
				}
				notifyApplyPerformed();
			}

		}

	}

	/*
	 * @Override public void setEditedObject(BackgroundStyle object) {
	 * logger.info("setEditedObject with "+object);
	 * super.setEditedObject(object); }
	 */

	@Override
	public void apply() {
		setRevertValue(getEditedObject() != null ? (ShapeSpecification) getEditedObject().clone() : null);
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

	/*
	 * protected void pointerLeavesPopup() { cancel(); }
	 */

	public ShapeDetailsPanel getSelectorPanel() {
		return _selectorPanel;
	}

	@Override
	protected JShapePreviewPanel buildFrontComponent() {
		frontComponent = new JShapePreviewPanel(getEditedObject());
		frontComponent.setBorderSize(1);
		frontComponent.setPanelWidth(40);
		frontComponent.setPanelHeight(19);
		return frontComponent;
	}

	@Override
	public JShapePreviewPanel getFrontComponent() {
		return (JShapePreviewPanel) super.getFrontComponent();
	}

	/*
	 * @Override protected Border getDownButtonBorder() { return
	 * BorderFactory.createCompoundBorder(
	 * BorderFactory.createEmptyBorder(1,1,1,1),
	 * BorderFactory.createRaisedBevelBorder()); //return
	 * BorderFactory.createRaisedBevelBorder(); //return
	 * BorderFactory.createLoweredBevelBorder() //return
	 * BorderFactory.createEtchedBorder(); //return
	 * BorderFactory.createBevelBorder(BevelBorder.LOWERED); //return
	 * BorderFactory.createBevelBorder(BevelBorder.LOWERED); }
	 */

	@Override
	public Class<ShapeSpecification> getRepresentedType() {
		return ShapeSpecification.class;
	}

}
