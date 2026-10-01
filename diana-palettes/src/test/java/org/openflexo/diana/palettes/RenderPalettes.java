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

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.openflexo.diana.PaletteElementSpecification;
import org.openflexo.diana.ShapeGraphicalRepresentation;
import org.openflexo.diana.Drawing.DrawingTreeNode;
import org.openflexo.diana.control.PaletteElement;
import org.openflexo.diana.control.PaletteModel;
import org.openflexo.diana.geom.DianaPoint;
import org.openflexo.diana.swing.control.tools.JDianaPalette;
import org.openflexo.diana.swing.view.JDrawingView;
import org.openflexo.pamela.converter.RelativePathResourceConverter;
import org.openflexo.rm.ResourceLocator;

/**
 * Renders each palette of this module to a PNG image, through the real DIANA Swing rendering, for visual review.<br>
 * Needs a display. Usage: <code>RenderPalettes &lt;outputDirectory&gt; &lt;Palette&gt;...</code>
 * 
 * @author sylvain
 */
public class RenderPalettes {

	static class PreviewPalette extends PaletteModel {

		PreviewPalette(String paletteName) {
			super(paletteName, 840, 800, 120, 80, 20, 30);
			// Image files of palette elements are relative to the resources directory
			FACTORY.addConverter(new RelativePathResourceConverter(
					ResourceLocator.locateResource("Palettes/" + paletteName).getContainer().getContainer()));
			readFromDirectory(ResourceLocator.locateResource("Palettes/" + paletteName));
		}

		@Override
		protected PaletteElement buildPaletteElement(PaletteElementSpecification spec) {
			return new PaletteElement() {
				@Override
				public ShapeGraphicalRepresentation getGraphicalRepresentation() {
					return spec.getGraphicalRepresentation();
				}

				@Override
				public boolean acceptDragging(DrawingTreeNode<?, ?> target) {
					return false;
				}

				@Override
				public boolean elementDragged(DrawingTreeNode<?, ?> target, DianaPoint dropLocation) {
					return false;
				}

				@Override
				public void delete(Object... context) {
				}

				@Override
				public String getName() {
					return spec.getName();
				}
			};
		}
	}

	/**
	 * Renders a palette (elements in cells of 120x80, named by their label) through DIANA Swing rendering. Must not be called from the
	 * event dispatch thread.
	 */
	public static BufferedImage render(String paletteName) throws Exception {
		return render(new PreviewPalette(paletteName));
	}

	static BufferedImage render(PaletteModel model) throws Exception {
		String paletteName = model.getTitle();
		// Show the element name as label, to identify shapes on the image
		for (PaletteElement element : model.getElements()) {
			element.getGraphicalRepresentation().setText(element.getName());
		}
		JDianaPalette palette = new JDianaPalette(model);
		JFrame frame = new JFrame(paletteName);
		SwingUtilities.invokeAndWait(() -> {
			frame.getContentPane().add(palette.getPaletteView());
			frame.pack();
			frame.setVisible(true);
		});
		Thread.sleep(1000);
		BufferedImage[] image = new BufferedImage[1];
		SwingUtilities.invokeAndWait(() -> {
			JDrawingView<?> view = palette.getPaletteView();
			image[0] = new BufferedImage(view.getWidth(), view.getHeight(), BufferedImage.TYPE_INT_RGB);
			Graphics2D g = image[0].createGraphics();
			view.paint(g);
			g.dispose();
			frame.dispose();
		});
		return image[0];
	}

	public static void main(String[] args) throws Exception {
		File outputDirectory = new File(args[0]);
		outputDirectory.mkdirs();
		for (int i = 1; i < args.length; i++) {
			File png = new File(outputDirectory, args[i] + ".png");
			ImageIO.write(render(args[i]), "png", png);
			System.out.println("Rendered " + png);
		}
		System.exit(0);
	}
}
