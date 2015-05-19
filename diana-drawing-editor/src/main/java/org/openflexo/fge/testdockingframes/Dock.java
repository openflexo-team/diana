/**
 * 
 * Copyright (c) 2014, Openflexo
 * 
 * This file is part of Diana-drawing-editor, a component of the software infrastructure 
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

package org.openflexo.fge.testdockingframes;

import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;

import bibliothek.extension.gui.dock.theme.EclipseTheme;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.CGrid;
import bibliothek.gui.dock.common.CLocation;
import bibliothek.gui.dock.common.DefaultSingleCDockable;
import bibliothek.gui.dock.common.SingleCDockable;
import bibliothek.gui.dock.common.menu.SingleCDockableListMenuPiece;
import bibliothek.gui.dock.facile.menu.RootMenuPiece;

public class Dock {
	public static void main(String[] args) {
		JFrame frame = new JFrame("Demo");
		CControl control = new CControl(frame);
		control.getController().setTheme(new EclipseTheme());

		frame.add(control.getContentArea());

		CGrid grid = new CGrid(control);
		grid.add(0, 0, 1, 1, createDockable("Red", Color.RED));
		grid.add(0, 1, 1, 1, createDockable("Green", Color.GREEN));
		grid.add(1, 0, 1, 1, createDockable("Blue", Color.BLUE));
		grid.add(1, 1, 1, 1, createDockable("Yellow", Color.YELLOW));
		control.getContentArea().deploy(grid);

		SingleCDockable black = createDockable("Black", Color.BLACK);
		control.addDockable(black);
		black.setLocation(CLocation.base().minimalNorth());
		black.setVisible(true);

		RootMenuPiece menu = new RootMenuPiece("Colors", false);
		menu.add(new SingleCDockableListMenuPiece(control));
		JMenuBar menuBar = new JMenuBar();
		menuBar.add(menu.getMenu());
		frame.setJMenuBar(menuBar);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBounds(20, 20, 400, 400);
		frame.setVisible(true);
	}

	public static SingleCDockable createDockable(String title, Color color) {
		JPanel panel = new JPanel();
		panel.setOpaque(true);
		panel.setBackground(color);
		DefaultSingleCDockable dockable = new DefaultSingleCDockable(title, title, panel);
		dockable.setCloseable(true);
		return dockable;
	}
}
