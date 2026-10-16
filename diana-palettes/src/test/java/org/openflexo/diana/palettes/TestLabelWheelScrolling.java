/**
 *
 * Copyright (c) 2026, Openflexo
 *
 * This file is part of Diana-palettes, a component of the software infrastructure
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

package org.openflexo.diana.palettes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.awt.Component;
import java.awt.Container;
import java.awt.Point;
import java.awt.event.MouseWheelEvent;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.openflexo.diana.palettes.RenderPalettes.PreviewPalette;
import org.openflexo.diana.swing.control.tools.JDianaPalette;
import org.openflexo.diana.swing.view.JLabelView;
import org.openflexo.test.UITest;

/**
 * Turning the mouse wheel over the label of a shape scrolls the enclosing scroll pane: a label view, itself a scroll pane, must not
 * swallow wheel events
 * 
 * @author sylvain
 */
public class TestLabelWheelScrolling {

	private static JLabelView<?> findLabelView(Component c) {
		if (c instanceof JLabelView && c.isShowing() && c.getWidth() > 0) {
			return (JLabelView<?>) c;
		}
		if (c instanceof Container) {
			for (Component child : ((Container) c).getComponents()) {
				JLabelView<?> found = findLabelView(child);
				if (found != null) {
					return found;
				}
			}
		}
		return null;
	}

	@Test
	@Category(UITest.class)
	public void testWheelOverLabelScrolls() throws Exception {
		// Basic palette holds labeled elements (single and multiple lines labels)
		JDianaPalette palette = new JDianaPalette(new PreviewPalette("Basic"));
		JScrollPane pane = new JScrollPane(palette.getPaletteView());
		JFrame frame = new JFrame();
		SwingUtilities.invokeAndWait(() -> {
			frame.getContentPane().add(pane);
			frame.setSize(300, 150);
			frame.setVisible(true);
		});
		Thread.sleep(1000);
		try {
			SwingUtilities.invokeAndWait(() -> {
				pane.getViewport().setViewPosition(new Point(0, 0));
				JLabelView<?> label = findLabelView(palette.getPaletteView());
				assertNotNull("No label shown", label);
				assertEquals("A label view listens to the mouse wheel", 0, label.getMouseWheelListeners().length);

				// Dispatch the wheel event to the deepest component under the label center, as Swing does
				Point inPane = SwingUtilities.convertPoint(label, new Point(label.getWidth() / 2, label.getHeight() / 2), pane);
				Component target = SwingUtilities.getDeepestComponentAt(pane, inPane.x, inPane.y);
				Point p = SwingUtilities.convertPoint(pane, inPane, target);
				target.dispatchEvent(new MouseWheelEvent(target, MouseWheelEvent.MOUSE_WHEEL, System.currentTimeMillis(), 0, p.x, p.y, 0,
						false, MouseWheelEvent.WHEEL_UNIT_SCROLL, 3, 3));
				assertTrue("The scroll pane did not scroll", pane.getViewport().getViewPosition().y > 0);
			});
		} finally {
			SwingUtilities.invokeAndWait(frame::dispose);
		}
	}
}
