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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
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
 * A palette may be loaded lazily: its panel then shows a "Loading..." label, and the palette is loaded once the panel is opened for
 * the first time (at the end of its expansion, so that the label is shown meanwhile).<br>
 * Loaded palettes are attached to the editor supplied to {@link #attachToEditor(AbstractDianaEditor)}
 * 
 * @author sylvain
 */
public class JDianaPaletteGroup {

	/**
	 * A palette of the group, loaded or not yet
	 */
	private class PaletteEntry {
		private final String title;
		private final Supplier<JDianaPalette> loader;
		/** Holds the "Loading..." label, then the palette */
		private final JPanel container = new JPanel(new BorderLayout());
		private FlexoCollabsiblePanel panel;
		private JDianaPalette palette;

		private PaletteEntry(String title, Supplier<JDianaPalette> loader) {
			this.title = title;
			this.loader = loader;
		}
	}

	private final FlexoCollabsiblePanelGroup component;
	private final List<PaletteEntry> entries = new ArrayList<>();
	private AbstractDianaEditor<?, SwingViewFactory, ?> editor;

	public JDianaPaletteGroup() {
		component = new FlexoCollabsiblePanelGroup();
		setWhiteBackground(component);
	}

	public FlexoCollabsiblePanelGroup getComponent() {
		return component;
	}

	/**
	 * Return the palettes loaded so far, in the order of their panels
	 */
	public List<JDianaPalette> getPalettes() {
		List<JDianaPalette> returned = new ArrayList<>();
		for (PaletteEntry entry : entries) {
			if (entry.palette != null) {
				returned.add(entry.palette);
			}
		}
		return Collections.unmodifiableList(returned);
	}

	public int getPaletteCount() {
		return entries.size();
	}

	public String getTitle(int index) {
		return entries.get(index).title;
	}

	/**
	 * Return the palette at supplied index, or null if it is not loaded yet
	 */
	public JDianaPalette getPalette(int index) {
		return entries.get(index).palette;
	}

	public boolean isLoaded(int index) {
		return entries.get(index).palette != null;
	}

	/**
	 * Add supplied palette, in a panel of supplied title. The panel is opened if supplied flag is set, collapsed otherwise
	 */
	public void addPalette(String title, JDianaPalette palette, boolean opened) {
		addPalette(title, () -> palette, opened);
	}

	/**
	 * Add the palette built by supplied loader, in a panel of supplied title. The panel is opened (and the palette loaded) if supplied
	 * flag is set; otherwise it is collapsed, and the palette is loaded the first time the panel is opened
	 */
	public void addPalette(String title, Supplier<JDianaPalette> loader, boolean opened) {
		PaletteEntry entry = new PaletteEntry(title, loader);
		JLabel loadingLabel = new JLabel("Loading...");
		loadingLabel.setFont(loadingLabel.getFont().deriveFont(Font.ITALIC));
		loadingLabel.setForeground(Color.GRAY);
		loadingLabel.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
		entry.container.add(loadingLabel, BorderLayout.CENTER);
		entry.panel = new FlexoCollabsiblePanel(title, entry.container);
		for (Component child : entry.panel.getComponents()) {
			if (child instanceof FlexoCollabsiblePanel.FlexoCollabsiblePanelHeader) {
				((JComponent) child).setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
			}
			else if (child instanceof JXCollapsiblePane) {
				// Fired once expanded (after the animation, if any): load the palette the first time, bring it into view
				child.addPropertyChangeListener("collapsed", e -> {
					if (Boolean.FALSE.equals(e.getNewValue()) && !entry.panel.isCollapsed()) {
						// Let the expanded panel (and its label) be painted first
						SwingUtilities.invokeLater(() -> {
							if (entry.palette == null) {
								load(entry);
							}
							bringIntoView(entry);
						});
					}
				});
			}
		}
		setWhiteBackground(entry.panel);
		entries.add(entry);
		if (opened) {
			load(entry);
		}
		// The group opens a panel when it is added (and collapses the other ones): panels are then toggled independently
		List<Boolean> wasOpened = new ArrayList<>();
		for (PaletteEntry e : entries) {
			wasOpened.add(!e.panel.isCollapsed());
		}
		component.addContents(entry.panel);
		for (int i = 0; i < entries.size() - 1; i++) {
			entries.get(i).panel.setCollapsed(!wasOpened.get(i));
		}
		entry.panel.setCollapsed(!opened);
	}

	/**
	 * Replace the "Loading..." label of supplied entry with its palette, laid out for the available width
	 */
	private void load(PaletteEntry entry) {
		entry.palette = entry.loader.get();
		int width = entry.container.getWidth() > 0 ? entry.container.getWidth() : component.getViewport().getWidth();
		if (width > 0) {
			entry.palette.fitToWidth(width);
		}
		entry.container.removeAll();
		entry.container.add(entry.palette.getFittingComponent(), BorderLayout.CENTER);
		setWhiteBackground(entry.palette.getFittingComponent());
		if (editor != null) {
			entry.palette.attachToEditor(editor);
		}
		entry.container.revalidate();
		entry.container.repaint();
	}

	private static void bringIntoView(PaletteEntry entry) {
		FlexoCollabsiblePanel panel = entry.panel;
		SwingUtilities.invokeLater(() -> panel.scrollRectToVisible(new Rectangle(0, 0, panel.getWidth(), panel.getHeight())));
	}

	/**
	 * Tells if the panel of the palette at supplied index is opened
	 */
	public boolean isOpened(int index) {
		return !entries.get(index).panel.isCollapsed();
	}

	/**
	 * Open or collapse the panel of the palette at supplied index (opening it loads its palette, if not done yet)
	 */
	public void setOpened(int index, boolean opened) {
		entries.get(index).panel.setCollapsed(!opened);
	}

	/**
	 * Attach loaded palettes to supplied editor, and palettes loaded later
	 */
	public void attachToEditor(AbstractDianaEditor<?, SwingViewFactory, ?> editor) {
		this.editor = editor;
		for (JDianaPalette palette : getPalettes()) {
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
