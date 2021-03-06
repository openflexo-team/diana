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

package org.openflexo.diana.swing.widget;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import org.openflexo.diana.Drawing;
import org.openflexo.diana.DrawingGraphicalRepresentation;
import org.openflexo.diana.DianaCoreUtils;
import org.openflexo.diana.DianaModelFactory;
import org.openflexo.diana.GRStructureVisitor;
import org.openflexo.diana.ShapeGraphicalRepresentation;
import org.openflexo.diana.TextStyle;
import org.openflexo.diana.Drawing.PersistenceMode;
import org.openflexo.diana.GRBinding.DrawingGRBinding;
import org.openflexo.diana.GRBinding.ShapeGRBinding;
import org.openflexo.diana.GRProvider.DrawingGRProvider;
import org.openflexo.diana.GRProvider.ShapeGRProvider;
import org.openflexo.diana.control.AbstractDianaEditor;
import org.openflexo.diana.impl.DrawingImpl;
import org.openflexo.diana.shapes.ShapeSpecification.ShapeType;
import org.openflexo.diana.swing.JDianaViewer;
import org.openflexo.diana.swing.control.SwingToolFactory;
import org.openflexo.diana.view.widget.FIBTextStyleSelector;
import org.openflexo.gina.controller.FIBController;
import org.openflexo.gina.model.FIBComponent;
import org.openflexo.gina.model.widget.FIBCustom;
import org.openflexo.gina.swing.view.JFIBView;
import org.openflexo.gina.swing.view.SwingViewFactory;
import org.openflexo.gina.view.GinaViewFactory;
import org.openflexo.swing.CustomPopup;
import org.openflexo.swing.JFontChooser;

/**
 * Widget allowing to view and edit a TextStyle
 * 
 * @author sguerin
 * 
 */
@SuppressWarnings("serial")
public class JFIBTextStyleSelector extends CustomPopup<TextStyle> implements FIBTextStyleSelector {

	static final Logger logger = Logger.getLogger(JFIBTextStyleSelector.class.getPackage().getName());

	private TextStyle _revertValue;

	protected TextStyleDetailsPanel _selectorPanel;

	private TextStylePreviewPanel textStylePreviewPanel;

	public JFIBTextStyleSelector(TextStyle editedObject) {
		super(editedObject);
		setRevertValue(editedObject != null ? (TextStyle) editedObject.clone() : null);
		setFocusable(true);
	}

	@Override
	public void delete() {
		super.delete();
		textStylePreviewPanel.delete();
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
	 * which are equals, and then start to share TextStyle between many GraphicalRepresentation
	 * 
	 * @return false
	 */
	@Override
	public boolean useEqualsLookup() {
		return false;
	}

	@Override
	public void setRevertValue(TextStyle oldValue) {
		// WARNING: we need here to clone to keep track back of previous data !!!
		if (oldValue != null) {
			_revertValue = (TextStyle) oldValue.clone();
		}
		else {
			_revertValue = null;
		}
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Sets revert value to " + _revertValue);
		}
	}

	@Override
	public TextStyle getRevertValue() {
		return _revertValue;
	}

	@Override
	protected ResizablePanel createCustomPanel(TextStyle editedObject) {
		_selectorPanel = makeCustomPanel(editedObject);
		return _selectorPanel;
	}

	protected TextStyleDetailsPanel makeCustomPanel(TextStyle editedObject) {
		return new TextStyleDetailsPanel(editedObject);
	}

	@Override
	public void updateCustomPanel(TextStyle editedObject) {
		if (_selectorPanel != null) {
			_selectorPanel.update();
		}
		getFrontComponent().update();
	}

	public class TextStyleDetailsPanel extends ResizablePanel {
		private FIBComponent fibComponent;
		private JFIBView<?, ?> fibView;
		private CustomFIBController controller;

		protected TextStyleDetailsPanel(TextStyle textStyle) {
			super();

			fibComponent = AbstractDianaEditor.EDITOR_FIB_LIBRARY.retrieveFIBComponent(FIB_FILE, true);
			controller = new CustomFIBController(fibComponent, SwingViewFactory.INSTANCE);
			fibView = (JFIBView<?, ?>) controller.buildView(fibComponent, null, true);

			controller.setDataObject(textStyle);

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
				JFIBTextStyleSelector.this.apply();
			}

			public void cancel() {
				JFIBTextStyleSelector.this.cancel();
			}

			public void parameterChanged() {
				getFrontComponent().update();
			}

		}

	}

	@Override
	public void apply() {
		setRevertValue(getEditedObject() != null ? (TextStyle) getEditedObject().clone() : null);
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

	public TextStyleDetailsPanel getSelectorPanel() {
		return _selectorPanel;
	}

	@Override
	protected TextStylePreviewPanel buildFrontComponent() {
		return textStylePreviewPanel = new TextStylePreviewPanel();
	}

	@Override
	public TextStylePreviewPanel getFrontComponent() {
		return (TextStylePreviewPanel) super.getFrontComponent();
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

	protected class TextStylePreviewPanel extends JPanel {
		private Drawing<TextStylePreviewPanel> drawing;
		private DrawingGraphicalRepresentation drawingGR;
		private JDianaViewer<TextStylePreviewPanel> controller;
		private ShapeGraphicalRepresentation textGR;
		private DianaModelFactory factory;

		protected TextStylePreviewPanel() {
			super(new BorderLayout());
			setBorder(BorderFactory.createEtchedBorder(Color.GRAY, Color.LIGHT_GRAY));
			// setBorder(BorderFactory.createEtchedBorder());
			// setPreferredSize(new Dimension(40,19));
			// setBackground(Color.WHITE);
			setMinimumSize(new Dimension(40, 19));

			factory = DianaCoreUtils.TOOLS_FACTORY;

			drawing = new DrawingImpl<TextStylePreviewPanel>(this, factory, PersistenceMode.UniqueGraphicalRepresentations) {
				@Override
				public void init() {
					final DrawingGRBinding<TextStylePreviewPanel> previewPanelBinding = bindDrawing(TextStylePreviewPanel.class,
							"previewPanel", new DrawingGRProvider<TextStylePreviewPanel>() {
								@Override
								public DrawingGraphicalRepresentation provideGR(TextStylePreviewPanel drawable, DianaModelFactory factory) {
									return drawingGR;
								}
							});
					final ShapeGRBinding<TextStylePreviewPanel> shapeBinding = bindShape(TextStylePreviewPanel.class, "line",
							new ShapeGRProvider<TextStylePreviewPanel>() {
								@Override
								public ShapeGraphicalRepresentation provideGR(TextStylePreviewPanel drawable, DianaModelFactory factory) {
									return textGR;
								}
							});

					previewPanelBinding.addToWalkers(new GRStructureVisitor<TextStylePreviewPanel>() {

						@Override
						public void visit(TextStylePreviewPanel previewPanel) {
							drawShape(shapeBinding, previewPanel, previewPanel);
						}
					});
				}
			};
			drawing.setEditable(false);

			drawingGR = factory.makeDrawingGraphicalRepresentation(false);
			drawingGR.setBackgroundColor(new Color(255, 255, 255));
			drawingGR.setWidth(199);
			drawingGR.setHeight(19);
			drawingGR.setDrawWorkingArea(false);
			textGR = factory.makeShapeGraphicalRepresentation(ShapeType.RECTANGLE);
			textGR.setWidth(200);
			textGR.setHeight(20);
			textGR.setX(0);
			textGR.setY(0);
			textGR.setText(DianaCoreUtils.DIANA_LOCALIZATION.localizedForKey("no_font_selected"));
			textGR.setIsFloatingLabel(false);
			textGR.setRelativeTextX(0.5);
			textGR.setRelativeTextY(0.35);
			textGR.setForeground(factory.makeNoneForegroundStyle());
			textGR.setBackground(factory.makeEmptyBackground());
			textGR.setTextStyle(getEditedObject());
			textGR.setShadowStyle(factory.makeNoneShadowStyle());
			textGR.setIsSelectable(false);
			textGR.setIsFocusable(false);
			textGR.setIsReadOnly(true);
			// textGR.setBorder(factory.makeShapeBorder(0, 0, 0, 0));

			controller = new JDianaViewer<TextStylePreviewPanel>(drawing, factory, SwingToolFactory.DEFAULT);
			add(controller.getDrawingView());

			update();
		}

		public void delete() {
			controller.delete();
			drawingGR.delete();
			textGR.delete();
			controller = null;
			drawingGR = null;
			textGR = null;
			drawing = null;
		}

		protected void update() {
			if (getEditedObject() == null) {
				return;
			}
			textGR.setTextStyle(getEditedObject());
			textGR.setText(JFontChooser.fontDescription(getEditedObject().getFont()));
		}

	}

	@Override
	public Class<TextStyle> getRepresentedType() {
		return TextStyle.class;
	}

}
