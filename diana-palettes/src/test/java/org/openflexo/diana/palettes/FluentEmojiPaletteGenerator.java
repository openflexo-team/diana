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
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;

import org.openflexo.diana.BackgroundImageBackgroundStyle;
import org.openflexo.diana.BackgroundImageBackgroundStyle.ImageBackgroundType;
import org.openflexo.diana.DianaModelFactory;
import org.openflexo.diana.DianaModelFactoryImpl;
import org.openflexo.diana.PaletteElementSpecification;
import org.openflexo.diana.ShapeGraphicalRepresentation;
import org.openflexo.diana.ShapeGraphicalRepresentation.DimensionConstraints;
import org.openflexo.diana.ShapeGraphicalRepresentation.LocationConstraints;
import org.openflexo.diana.shapes.ShapeSpecification.ShapeType;
import org.openflexo.pamela.converter.RelativePathResourceConverter;
import org.openflexo.rm.FileSystemResourceLocatorImpl;

/**
 * Generates the Emoji palette from Microsoft Fluent Emoji (https://github.com/microsoft/fluentui-emoji, MIT license), in its 3D style.<br>
 * 
 * For each selected emoji, the 256x256 PNG is scaled down to {@value #IMAGE_SIZE} pixels into <code>Images/FluentEmoji</code>, and a
 * palette element (a rectangle without stroke nor shadow, filled with the image) is written into <code>Palettes/Emoji</code>. The license
 * of Fluent Emoji is copied next to the images, as it requires.<br>
 * 
 * Run it through <code>gradle :diana-palettes:generateEmojiPalette -PfluentEmojiDir=&lt;fluentui-emoji checkout&gt;</code>; generated files
 * are committed.
 * 
 * @author sylvain
 */
public class FluentEmojiPaletteGenerator {

	static final int IMAGE_SIZE = 128;
	static final String PALETTE_DIRECTORY = "Palettes/Emoji";
	static final String IMAGES_DIRECTORY = "Images/FluentEmoji";

	/**
	 * Selected emojis, by their name in Fluent Emoji (its asset directory), in palette order
	 */
	static final List<String> EMOJIS = Arrays.asList(
			"Person",
			"Man",
			"Woman",
			"Busts in silhouette",
			"Technologist",
			"Office worker",
			"Scientist",
			"Factory worker",
			"Construction worker",
			"Health worker",
			"Teacher",
			"Detective",
			"Robot",
			"Laptop",
			"Desktop computer",
			"Mobile phone",
			"Printer",
			"Keyboard",
			"Computer mouse",
			"Floppy disk",
			"Optical disk",
			"Battery",
			"Electric plug",
			"Satellite antenna",
			"Gear",
			"Wrench",
			"Hammer",
			"Toolbox",
			"Link",
			"Locked",
			"Unlocked",
			"Key",
			"Shield",
			"Magnifying glass tilted left",
			"Light bulb",
			"Envelope",
			"Incoming envelope",
			"Package",
			"File folder",
			"Open file folder",
			"Page facing up",
			"Memo",
			"Clipboard",
			"Spiral calendar",
			"Bar chart",
			"Chart increasing",
			"Chart decreasing",
			"Books",
			"Card index dividers",
			"File cabinet",
			"Wastebasket",
			"Money bag",
			"Credit card",
			"Shopping cart",
			"Bell",
			"Megaphone",
			"Hourglass done",
			"Alarm clock",
			"Test tube",
			"Microscope",
			"Office building",
			"Factory",
			"House",
			"Hospital",
			"Bank",
			"Classical building",
			"Delivery truck",
			"Airplane",
			"Rocket",
			"Globe showing europe-africa",
			"Cloud",
			"High voltage",
			"Fire",
			"Construction",
			"Check mark button",
			"Cross mark",
			"Warning",
			"No entry",
			"Red question mark",
			"Red exclamation mark",
			"Recycling symbol",
			"Star",
			"Red heart",
			"Green circle",
			"Yellow circle",
			"Red circle");

	private final File fluentEmojiDirectory;
	private final File resourcesDirectory;
	private final DianaModelFactory factory;
	private final FileSystemResourceLocatorImpl fileSystemLocator = new FileSystemResourceLocatorImpl();

	public FluentEmojiPaletteGenerator(File fluentEmojiDirectory, File resourcesDirectory) throws Exception {
		this.fluentEmojiDirectory = fluentEmojiDirectory;
		this.resourcesDirectory = resourcesDirectory;
		factory = new DianaModelFactoryImpl();
		// Image files are stored relatively to the resources directory
		factory.addConverter(new RelativePathResourceConverter(fileSystemLocator.retrieveResource(resourcesDirectory)));
	}

	public static void main(String[] args) throws Exception {
		new FluentEmojiPaletteGenerator(new File(args[0]), new File(args[1])).generate();
	}

	void generate() throws Exception {
		File paletteDirectory = new File(resourcesDirectory, PALETTE_DIRECTORY);
		File imagesDirectory = new File(resourcesDirectory, IMAGES_DIRECTORY);
		paletteDirectory.mkdirs();
		imagesDirectory.mkdirs();
		Files.copy(new File(fluentEmojiDirectory, "LICENSE").toPath(), new File(imagesDirectory, "LICENSE").toPath(),
				StandardCopyOption.REPLACE_EXISTING);
		int index = 0;
		for (String emoji : EMOJIS) {
			String fileName = fileName(emoji);
			File imageFile = new File(imagesDirectory, fileName + ".png");
			ImageIO.write(scale(ImageIO.read(find3DImage(emoji))), "png", imageFile);
			PaletteElementSpecification spec = makePaletteElement(emoji, imageFile, index++);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			factory.serialize(spec, out);
			File pelFile = new File(paletteDirectory, fileName + ".pel");
			Files.write(pelFile.toPath(), DrawingMLPaletteGenerator.sortAttributes(out.toString("UTF-8")).getBytes(StandardCharsets.UTF_8));
			System.out.println("Generated " + pelFile);
		}
	}

	/**
	 * The 3D image of an emoji: in its <code>3D</code> directory, or in <code>Default/3D</code> for emojis with skin tones
	 */
	private File find3DImage(String emoji) throws FileNotFoundException {
		File assetDirectory = new File(new File(fluentEmojiDirectory, "assets"), emoji);
		for (File directory : new File[] { new File(assetDirectory, "3D"), new File(new File(assetDirectory, "Default"), "3D") }) {
			File[] images = directory.listFiles((dir, name) -> name.endsWith(".png"));
			if (images != null && images.length == 1) {
				return images[0];
			}
		}
		throw new FileNotFoundException("No 3D image for " + emoji + " in " + assetDirectory);
	}

	private static BufferedImage scale(BufferedImage image) {
		BufferedImage returned = new BufferedImage(IMAGE_SIZE, IMAGE_SIZE, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = returned.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.drawImage(image, 0, 0, IMAGE_SIZE, IMAGE_SIZE, null);
		g.dispose();
		return returned;
	}

	/**
	 * "Magnifying glass tilted left" becomes "MagnifyingGlassTiltedLeft"
	 */
	static String fileName(String emoji) {
		StringBuilder returned = new StringBuilder();
		for (String word : emoji.split("[^A-Za-z0-9]+")) {
			if (!word.isEmpty()) {
				returned.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1));
			}
		}
		return returned.toString();
	}

	private PaletteElementSpecification makePaletteElement(String emoji, File imageFile, int index) {
		PaletteElementSpecification spec = factory.newInstance(PaletteElementSpecification.class);
		spec.setName(Character.toUpperCase(emoji.charAt(0)) + emoji.substring(1));
		spec.setDescription("Fluent Emoji (MIT license): " + emoji);
		spec.setIndex(index);
		// The image is the element: do not apply the current styles of the editor
		spec.setApplyCurrentForeground(false);
		spec.setApplyCurrentBackground(false);
		spec.setApplyCurrentShadowStyle(false);

		BackgroundImageBackgroundStyle image = factory.makeImageBackground(fileSystemLocator.retrieveResource(imageFile));
		image.setFitToShape(true);
		image.setImageBackgroundType(ImageBackgroundType.TRANSPARENT);

		ShapeGraphicalRepresentation gr = factory.makeShapeGraphicalRepresentation(ShapeType.RECTANGLE);
		gr.setIdentifier("fluent-emoji-" + fileName(emoji));
		gr.setForeground(factory.makeNoneForegroundStyle());
		gr.setBackground(image);
		gr.setShadowStyle(factory.makeNoneShadowStyle());
		gr.setTextStyle(factory.makeDefaultTextStyle());
		gr.setIsFloatingLabel(false);
		gr.setRelativeTextX(0.5);
		gr.setRelativeTextY(0.5);
		gr.setLocationConstraints(LocationConstraints.FREELY_MOVABLE);
		gr.setDimensionConstraints(DimensionConstraints.FREELY_RESIZABLE);
		gr.setWidth(64);
		gr.setHeight(64);
		// The label would hide the image
		PaletteLabels.setFloatingLabelAbove(gr);
		spec.setGraphicalRepresentation(gr);
		return spec;
	}
}
