/**
 *
 * Copyright (c) 2026, Openflexo
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

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import org.jdesktop.swingx.JXCollapsiblePane;
import org.openflexo.diana.control.AbstractDianaEditor;
import org.openflexo.diana.swing.SwingViewFactory;
import org.openflexo.diana.swing.view.JDrawingView;
import org.openflexo.swing.FlexoCollabsiblePanel;
import org.openflexo.swing.FlexoCollabsiblePanelGroup;

/**
 * Shows several palettes as collapsible panels, on a white background: panels are toggled independently, several of them may be
 * opened together (a palette becomes the active one of the edited drawing when a drag starts from it), and an opened panel is brought
 * into view.<br>
 * All palettes are attached to the editor supplied to {@link #attachToEditor(AbstractDianaEditor)}
 * 
 * @author sylvain
 */
public class JDianaPaletteGroup {

	private final FlexoCollabsiblePanelGroup component;
	private final List<JDianaPalette> palettes = new ArrayList<>();
	private final List<FlexoCollabsiblePanel> panels = new ArrayList<>();

	public JDianaPaletteGroup() {
		component = new FlexoCollabsiblePanelGroup();
		setWhiteBackground(component);
	}

	public FlexoCollabsiblePanelGroup getComponent() {
		return component;
	}

	public List<JDianaPalette> getPalettes() {
		return Collections.unmodifiableList(palettes);
	}

	/**
	 * Add supplied palette, in a panel of supplied title. The panel is opened if supplied flag is set, collapsed otherwise
	 */
	public void addPalette(String title, JDianaPalette palette, boolean opened) {
		FlexoCollabsiblePanel panel = new FlexoCollabsiblePanel(title, palette.getFittingComponent());
		palettes.add(palette);
		panels.add(panel);
		for (Component child : panel.getComponents()) {
			if (child instanceof FlexoCollabsiblePanel.FlexoCollabsiblePanelHeader) {
				((JComponent) child).setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
			}
			else if (child instanceof JXCollapsiblePane) {
				// Fired once expanded (after the animation, if any): bring the opened palette into view
				child.addPropertyChangeListener("collapsed", e -> {
					if (Boolean.FALSE.equals(e.getNewValue())) {
						SwingUtilities.invokeLater(() -> panel.scrollRectToVisible(new Rectangle(0, 0, panel.getWidth(), panel.getHeight())));
					}
				});
			}
		}
		setWhiteBackground(panel);
		// The group opens a panel when it is added (and collapses the other ones): panels are then toggled independently
		List<Boolean> wasOpened = new ArrayList<>();
		for (FlexoCollabsiblePanel p : panels) {
			wasOpened.add(!p.isCollapsed());
		}
		component.addContents(panel);
		for (int i = 0; i < panels.size() - 1; i++) {
			panels.get(i).setCollapsed(!wasOpened.get(i));
		}
		panel.setCollapsed(!opened);
	}

	/**
	 * Tells if the panel of the palette at supplied index is opened
	 */
	public boolean isOpened(int index) {
		return !panels.get(index).isCollapsed();
	}

	/**
	 * Attach all palettes to supplied editor
	 */
	public void attachToEditor(AbstractDianaEditor<?, SwingViewFactory, ?> editor) {
		for (JDianaPalette palette : palettes) {
			palette.attachToEditor(editor);
		}
	}

	/**
	 * Sets a white background to supplied component and its descendants, palette drawings excepted
	 */
	private static void setWhiteBackground(Component c) {
		if (c instanceof JDrawingView) {
			return;
		}
		c.setBackground(Color.WHITE);
		if (c instanceof Container) {
			for (Component child : ((Container) c).getComponents()) {
				setWhiteBackground(child);
			}
		}
	}
}
