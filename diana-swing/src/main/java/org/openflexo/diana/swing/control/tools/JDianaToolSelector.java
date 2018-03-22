/**
 * 
 * Copyright (c) 2014, Openflexo
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

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JToggleButton;

import org.openflexo.diana.DianaIconLibrary;
import org.openflexo.diana.control.DianaInteractiveEditor.DrawConnectorToolOption;
import org.openflexo.diana.control.DianaInteractiveEditor.DrawCustomShapeToolOption;
import org.openflexo.diana.control.DianaInteractiveEditor.DrawShapeToolOption;
import org.openflexo.diana.control.DianaInteractiveEditor.EditorTool;
import org.openflexo.diana.control.DianaInteractiveEditor.EditorToolOption;
import org.openflexo.diana.control.tools.DianaToolSelector;
import org.openflexo.diana.swing.JDianaInteractiveEditor;
import org.openflexo.diana.swing.SwingViewFactory;

/**
 * SWING implementation of the {@link DianaToolSelector}
 * 
 * @author sylvain
 * 
 */
public class JDianaToolSelector extends DianaToolSelector<JPanel, SwingViewFactory> {

	private static final Logger logger = Logger.getLogger(JDianaToolSelector.class.getPackage().getName());

	private ToolButton selectionToolButton;
	private ToolButton drawShapeToolButton;
	private ToolButton drawCustomShapeToolButton;
	private ToolButton drawConnectorToolButton;
	private ToolButton drawTextToolButton;

	private JPanel component;

	private boolean isInitialized = false;

	public JDianaToolSelector(JDianaInteractiveEditor<?> editor) {
		super(editor);
		component = new JPanel();
		component.setLayout(new FlowLayout(FlowLayout.LEADING, 0, 0));

		selectionToolButton = new ToolButton(EditorTool.SelectionTool, null);
		drawShapeToolButton = new ToolButton(EditorTool.DrawShapeTool, editor != null ? editor.getDrawShapeToolOption() : null);
		drawCustomShapeToolButton = new ToolButton(EditorTool.DrawCustomShapeTool, editor != null ? editor.getDrawCustomShapeToolOption()
				: null);
		drawConnectorToolButton = new ToolButton(EditorTool.DrawConnectorTool, editor != null ? editor.getDrawConnectorToolOption() : null);
		drawTextToolButton = new ToolButton(EditorTool.DrawTextTool, null);
		component.add(new JLabel(DianaIconLibrary.TOOLBAR_LEFT_ICON));
		component.add(selectionToolButton);
		component.add(new JLabel(DianaIconLibrary.TOOLBAR_SPACER_ICON));
		component.add(drawShapeToolButton);
		component.add(new JLabel(DianaIconLibrary.TOOLBAR_SPACER_ICON));
		component.add(drawCustomShapeToolButton);
		component.add(new JLabel(DianaIconLibrary.TOOLBAR_SPACER_ICON));
		component.add(drawConnectorToolButton);
		component.add(new JLabel(DianaIconLibrary.TOOLBAR_SPACER_ICON));
		component.add(drawTextToolButton);
		component.add(new JLabel(DianaIconLibrary.TOOLBAR_RIGHT_ICON));
		isInitialized = true;
		updateButtons();
	}

	@Override
	public JPanel getComponent() {
		return component;
	}

	@Override
	public void handleToolChanged() {
		updateButtons();
	}

	@Override
	public void handleToolOptionChanged() {
		updateButtons();
	}

	private void updateButtons() {
		if (isInitialized) {
			selectionToolButton.update();
			drawShapeToolButton.update();
			drawCustomShapeToolButton.update();
			drawConnectorToolButton.update();
			drawTextToolButton.update();
		}
	}

	@SuppressWarnings("serial")
	public class ToolButton extends JToggleButton {
		private EditorTool representedTool;
		private JPopupMenu contextualMenu;

		public ToolButton(EditorTool aRepresentedTool, final EditorToolOption toolOption) {
			super();
			this.representedTool = aRepresentedTool;
			setBorder(BorderFactory.createEmptyBorder());
			addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if (e.getPoint().x > getWidth() - 8 && e.getPoint().y > getHeight() - 8 && representedTool.getOptions() != null
							&& isEnabled()) {
						if (getContextualMenu().isVisible()) {
							System.out.println("Try to hide popup menu");
							getContextualMenu().setVisible(false);
						} else {
							getContextualMenu().show((Component) e.getSource(), e.getPoint().x, e.getPoint().y);
						}
					}
					if (isSelected()) {
						selectTool(representedTool);
					} else {
						selectTool(EditorTool.SelectionTool);
					}
				}
			});

			update();

		}

		public JPopupMenu getContextualMenu() {
			if (contextualMenu == null) {
				contextualMenu = new JPopupMenu();
				for (final EditorToolOption option : representedTool.getOptions()) {
					JMenuItem menuItem = new JMenuItem(option.name(), getMenuItemIconFor(option));
					menuItem.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							selectTool(representedTool);
							setOption(option);
						}

					});
					contextualMenu.add(menuItem);
				}
			}
			return contextualMenu;
		}

		public void update() {
			setSelected(getSelectedTool() == representedTool);
			if (getOption() != null) {
				setIcon(getIconFor(getOption()));
				setPressedIcon(getSelectedIconFor(getOption()));
				setSelectedIcon(getSelectedIconFor(getOption()));
			} else {
				setIcon(getIconFor(representedTool));
				setPressedIcon(getSelectedIconFor(representedTool));
				setSelectedIcon(getSelectedIconFor(representedTool));
			}
			if (getEditor() == null) {
				setEnabled(false);
			} else {
				switch (representedTool) {
				case SelectionTool:
					setEnabled(true);
					break;
				case DrawShapeTool:
					setEnabled(getEditor().getDrawShapeAction() != null);
					break;
				case DrawCustomShapeTool:
					setEnabled(getEditor().getDrawCustomShapeAction() != null);
					break;
				case DrawConnectorTool:
					setEnabled(getEditor().getDrawConnectorAction() != null);
					break;
				case DrawTextTool:
					setEnabled(getEditor().getDrawTextAction() != null);
					break;
				default:
					setEnabled(false);
				}
			}

		}

		@Override
		public void setSelected(boolean b) {
			if (isSelected() != b) {
				super.setSelected(b);
			}
		}

		public EditorToolOption getOption() {
			if (getEditor() != null) {
				switch (representedTool) {
				case SelectionTool:
					return null;
				case DrawShapeTool:
					return getEditor().getDrawShapeToolOption();
				case DrawCustomShapeTool:
					return getEditor().getDrawCustomShapeToolOption();
				case DrawConnectorTool:
					return getEditor().getDrawConnectorToolOption();
				case DrawTextTool:
					return null;
				default:
					logger.warning("Unexpected tool: " + representedTool);
				}
			}
			return null;
		}

		public void setOption(EditorToolOption option) {
			if (getEditor() != null) {
				System.out.println("Sets option with " + option);

				if (option instanceof DrawShapeToolOption) {
					getEditor().setDrawShapeToolOption((DrawShapeToolOption) option);
				} else if (option instanceof DrawCustomShapeToolOption) {
					getEditor().setDrawCustomShapeToolOption((DrawCustomShapeToolOption) option);
				} else if (option instanceof DrawConnectorToolOption) {
					getEditor().setDrawConnectorToolOption((DrawConnectorToolOption) option);
				}
			}
		}

	}

	@Override
	public SwingViewFactory getDianaFactory() {
		return SwingViewFactory.INSTANCE;
	}

	public Icon getIconFor(EditorTool tool) {
		switch (tool) {
		case SelectionTool:
			return DianaIconLibrary.SELECTION_TOOL_ICON;
		case DrawShapeTool:
			return DianaIconLibrary.DRAW_RECTANGLE_TOOL_ICON;
		case DrawCustomShapeTool:
			return DianaIconLibrary.DRAW_CUSTOM_POLYGON_TOOL_ICON;
		case DrawConnectorTool:
			return DianaIconLibrary.DRAW_LINE_TOOL_ICON;
		case DrawTextTool:
			return DianaIconLibrary.DRAW_TEXT_TOOL_ICON;
		default:
			logger.warning("Unexpected tool: " + tool);
			return null;
		}

	}

	public Icon getSelectedIconFor(EditorTool tool) {
		switch (tool) {
		case SelectionTool:
			return DianaIconLibrary.SELECTION_TOOL_SELECTED_ICON;
		case DrawShapeTool:
			return DianaIconLibrary.DRAW_RECTANGLE_TOOL_SELECTED_ICON;
		case DrawCustomShapeTool:
			return DianaIconLibrary.DRAW_CUSTOM_POLYGON_TOOL_SELECTED_ICON;
		case DrawConnectorTool:
			return DianaIconLibrary.DRAW_LINE_TOOL_SELECTED_ICON;
		case DrawTextTool:
			return DianaIconLibrary.DRAW_TEXT_TOOL_SELECTED_ICON;
		default:
			logger.warning("Unexpected tool: " + tool);
			return null;
		}

	}

	public Icon getIconFor(EditorToolOption option) {
		if (option instanceof DrawShapeToolOption) {
			switch ((DrawShapeToolOption) option) {
			case DrawRectangle:
				return DianaIconLibrary.DRAW_RECTANGLE_TOOL_ICON;
			case DrawOval:
				return DianaIconLibrary.DRAW_OVAL_TOOL_ICON;
			default:
				logger.warning("Unexpected option: " + option);
				return DianaIconLibrary.DRAW_RECTANGLE_TOOL_ICON;
			}
		} else if (option instanceof DrawCustomShapeToolOption) {
			switch ((DrawCustomShapeToolOption) option) {
			case DrawPolygon:
				return DianaIconLibrary.DRAW_CUSTOM_POLYGON_TOOL_ICON;
			case DrawClosedCurve:
				return DianaIconLibrary.DRAW_CLOSED_CURVE_TOOL_ICON;
			case DrawOpenedCurve:
				return DianaIconLibrary.DRAW_OPENED_CURVE_TOOL_ICON;
			case DrawComplexShape:
				return DianaIconLibrary.DRAW_CLOSED_CURVE_TOOL_ICON;
			default:
				logger.warning("Unexpected option: " + option);
				return DianaIconLibrary.DRAW_CLOSED_CURVE_TOOL_ICON;
			}
		} else if (option instanceof DrawConnectorToolOption) {
			switch ((DrawConnectorToolOption) option) {
			case DrawLine:
				return DianaIconLibrary.DRAW_LINE_TOOL_ICON;
			case DrawCurve:
				return DianaIconLibrary.DRAW_CURVE_TOOL_ICON;
			case DrawRectPolylin:
				return DianaIconLibrary.DRAW_RECT_POLYLIN_TOOL_ICON;
			case DrawCurvedPolylin:
				return DianaIconLibrary.DRAW_CURVED_POLYLIN_TOOL_ICON;
			default:
				logger.warning("Unexpected option: " + option);
				return DianaIconLibrary.DRAW_LINE_TOOL_ICON;
			}
		}
		logger.warning("Unexpected option: " + option);
		return null;
	}

	public Icon getSelectedIconFor(EditorToolOption option) {
		if (option instanceof DrawShapeToolOption) {
			switch ((DrawShapeToolOption) option) {
			case DrawRectangle:
				return DianaIconLibrary.DRAW_RECTANGLE_TOOL_SELECTED_ICON;
			case DrawOval:
				return DianaIconLibrary.DRAW_OVAL_TOOL_SELECTED_ICON;
			default:
				logger.warning("Unexpected option: " + option);
				return DianaIconLibrary.DRAW_RECTANGLE_TOOL_SELECTED_ICON;
			}
		} else if (option instanceof DrawCustomShapeToolOption) {
			switch ((DrawCustomShapeToolOption) option) {
			case DrawPolygon:
				return DianaIconLibrary.DRAW_CUSTOM_POLYGON_TOOL_SELECTED_ICON;
			case DrawClosedCurve:
				return DianaIconLibrary.DRAW_CLOSED_CURVE_TOOL_SELECTED_ICON;
			case DrawOpenedCurve:
				return DianaIconLibrary.DRAW_OPENED_CURVE_TOOL_SELECTED_ICON;
			case DrawComplexShape:
				return DianaIconLibrary.DRAW_CLOSED_CURVE_TOOL_SELECTED_ICON;
			default:
				logger.warning("Unexpected option: " + option);
				return DianaIconLibrary.DRAW_CLOSED_CURVE_TOOL_SELECTED_ICON;
			}
		} else if (option instanceof DrawConnectorToolOption) {
			switch ((DrawConnectorToolOption) option) {
			case DrawLine:
				return DianaIconLibrary.DRAW_LINE_TOOL_SELECTED_ICON;
			case DrawCurve:
				return DianaIconLibrary.DRAW_CURVE_TOOL_SELECTED_ICON;
			case DrawRectPolylin:
				return DianaIconLibrary.DRAW_RECT_POLYLIN_TOOL_SELECTED_ICON;
			case DrawCurvedPolylin:
				return DianaIconLibrary.DRAW_CURVED_POLYLIN_TOOL_SELECTED_ICON;
			default:
				logger.warning("Unexpected option: " + option);
				return DianaIconLibrary.DRAW_LINE_TOOL_SELECTED_ICON;
			}
		}
		logger.warning("Unexpected option: " + option);
		return null;
	}

	public Icon getMenuItemIconFor(EditorToolOption option) {
		if (option instanceof DrawShapeToolOption) {
			switch ((DrawShapeToolOption) option) {
			case DrawRectangle:
				return DianaIconLibrary.RECTANGLE_ICON;
			case DrawOval:
				return DianaIconLibrary.OVAL_ICON;
			default:
				logger.warning("Unexpected option: " + option);
				return null;
			}
		} else if (option instanceof DrawCustomShapeToolOption) {
			switch ((DrawCustomShapeToolOption) option) {
			case DrawPolygon:
				return DianaIconLibrary.CUSTOM_POLYGON_ICON;
			case DrawClosedCurve:
				return DianaIconLibrary.CLOSE_CURVE_ICON;
			case DrawOpenedCurve:
				return DianaIconLibrary.OPENED_CURVE_ICON;
			case DrawComplexShape:
				return DianaIconLibrary.COMPLEX_SHAPE_ICON;
			default:
				logger.warning("Unexpected option: " + option);
				return null;
			}
		} else if (option instanceof DrawConnectorToolOption) {
			switch ((DrawConnectorToolOption) option) {
			case DrawLine:
				return DianaIconLibrary.LINE_CONNECTOR_ICON;
			case DrawCurve:
				return DianaIconLibrary.CURVE_CONNECTOR_ICON;
			case DrawRectPolylin:
				return DianaIconLibrary.RECT_POLYLIN_CONNECTOR_ICON;
			case DrawCurvedPolylin:
				return DianaIconLibrary.CURVED_POLYLIN_CONNECTOR_ICON;
			default:
				logger.warning("Unexpected option: " + option);
				return null;
			}
		}
		logger.warning("Unexpected option: " + option);
		return null;
	}

}
