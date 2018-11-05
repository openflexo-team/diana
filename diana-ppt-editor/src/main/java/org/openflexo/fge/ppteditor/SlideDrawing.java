/**
 * 
 * Copyright (c) 2014, Openflexo
 * 
 * This file is part of Diana-ppt-editor, a component of the software infrastructure 
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

package org.openflexo.fge.ppteditor;

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import org.apache.poi.hslf.usermodel.HSLFShape;
import org.apache.poi.sl.draw.geom.Context;
import org.apache.poi.sl.draw.geom.CustomGeometry;
import org.apache.poi.sl.draw.geom.Path;
import org.apache.poi.sl.usermodel.AutoShape;
import org.apache.poi.sl.usermodel.MasterSheet;
import org.apache.poi.sl.usermodel.PaintStyle;
import org.apache.poi.sl.usermodel.PaintStyle.SolidPaint;
import org.apache.poi.sl.usermodel.Shape;
import org.apache.poi.sl.usermodel.Slide;
import org.apache.poi.sl.usermodel.TextBox;
import org.apache.poi.sl.usermodel.TextParagraph;
import org.apache.poi.sl.usermodel.TextRun;
import org.apache.poi.sl.usermodel.TextShape;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.util.Units;
import org.openflexo.connie.DataBinding;
import org.openflexo.fge.DrawingGraphicalRepresentation;
import org.openflexo.fge.FGECoreUtils;
import org.openflexo.fge.FGEModelFactory;
import org.openflexo.fge.ForegroundStyle.DashStyle;
import org.openflexo.fge.GRBinding.DrawingGRBinding;
import org.openflexo.fge.GRBinding.ShapeGRBinding;
import org.openflexo.fge.GRProvider.DrawingGRProvider;
import org.openflexo.fge.GRProvider.ShapeGRProvider;
import org.openflexo.fge.GRStructureVisitor;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.GraphicalRepresentation.HorizontalTextAlignment;
import org.openflexo.fge.GraphicalRepresentation.ParagraphAlignment;
import org.openflexo.fge.GraphicalRepresentation.VerticalTextAlignment;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.TextStyle;
import org.openflexo.fge.impl.DrawingImpl;
import org.openflexo.fge.shapes.ShapeSpecification.ShapeType;

public class SlideDrawing extends DrawingImpl<Slide> {

	private static final Logger logger = Logger.getLogger(SlideDrawing.class.getPackage().getName());

	public SlideDrawing(Slide slide) {
		super(slide, FGECoreUtils.TOOLS_FACTORY, PersistenceMode.UniqueGraphicalRepresentations);
	}

	@Override
	public void init() {

		final DrawingGraphicalRepresentation drawingGR = getFactory().makeDrawingGraphicalRepresentation();
		drawingGR.setDrawWorkingArea(true);
		drawingGR.setWidth(getModel().getSlideShow().getPageSize().getWidth());
		drawingGR.setHeight(getModel().getSlideShow().getPageSize().getHeight());

		final DrawingGRBinding<Slide> slideBinding = bindDrawing(Slide.class, "slide", new DrawingGRProvider<Slide>() {
			@Override
			public DrawingGraphicalRepresentation provideGR(Slide drawable, FGEModelFactory factory) {
				return drawingGR;
			}
		});

		// slideBinding.setDynamicPropertyValue(DrawingGraphicalRepresentation.WIDTH, new DataBinding<Double>(
		// "drawable.slideShow.pageSize.width"), true);
		// slideBinding.setDynamicPropertyValue(DrawingGraphicalRepresentation.HEIGHT, new DataBinding<Double>(
		// "drawable.slideShow.pageSize.height"), true);

		final ShapeGRBinding<AutoShape> autoShapeBinding = bindShape(AutoShape.class, "autoShape", new ShapeGRProvider<AutoShape>() {
			@Override
			public ShapeGraphicalRepresentation provideGR(AutoShape drawable, FGEModelFactory factory) {
				return makeAutoShapeGraphicalRepresentation(drawable);
			}
		});
		autoShapeBinding.setDynamicPropertyValue(GraphicalRepresentation.TEXT, new DataBinding<String>("drawable.text"), true);

		final ShapeGRBinding<TextBox> textBoxBinding = bindShape(TextBox.class, "textBox", new ShapeGRProvider<TextBox>() {
			@Override
			public ShapeGraphicalRepresentation provideGR(TextBox drawable, FGEModelFactory factory) {
				return makeTextBoxGraphicalRepresentation(drawable);
			}
		});
		textBoxBinding.setDynamicPropertyValue(GraphicalRepresentation.TEXT, new DataBinding<String>("drawable.text"), true);

		final ShapeGRBinding<Picture> pictureBinding = bindShape(Picture.class, "picture", new ShapeGRProvider<Picture>() {
			@Override
			public ShapeGraphicalRepresentation provideGR(Picture drawable, FGEModelFactory factory) {
				return makePictureGraphicalRepresentation(drawable);
			}
		});

		slideBinding.addToWalkers(new GRStructureVisitor<Slide>() {

			@Override
			public void visit(Slide slide) {

				MasterSheet master = slide.getMasterSheet();

				System.out.println("******* Tiens, pour la slide " + slide);
				if (slide.getFollowMasterObjects()) {
					List<Shape> sh = master.getShapes();
					for (int i = sh.size() - 1; i >= 0; i--) {
						/*if (MasterSheet.isPlaceholder(sh.get(i))) {
							continue;
						}*/
						Shape<?, ?> shape = sh.get(i);

						if (shape instanceof HSLFShape) {
							if (((HSLFShape) shape).isPlaceholder()) {
								continue;
							}
						}

						System.out.println(" > " + shape.getClass());

						if (shape instanceof Picture) {
							System.out.println("Hop, une Picture");
							drawShape(pictureBinding, (Picture) shape, slide);
						}
						else if (shape instanceof AutoShape) {
							System.out.println("Hop, une AutoShape avec le texte " + ((AutoShape) shape).getText());
							drawShape(autoShapeBinding, (AutoShape) shape, slide);
						}
						else if (shape instanceof TextBox) {
							System.out.println("Hop, une TextBox");
							drawShape(textBoxBinding, (TextBox) shape, slide);
						}
					}
				}

				for (Shape shape : ((Slide<?, ?>) slide).getShapes()) {
					if (shape instanceof Picture) {
						drawShape(pictureBinding, (Picture) shape, slide);
					}
					else if (shape instanceof AutoShape) {
						drawShape(autoShapeBinding, (AutoShape) shape, slide);
					}
					else if (shape instanceof TextBox) {
						drawShape(textBoxBinding, (TextBox) shape, slide);
					}
				}
			}
		});

		/*shapeBinding.setDynamicPropertyValue(GraphicalRepresentation.TEXT, new DataBinding<String>("drawable.name"), true);
		connectorBinding.setDynamicPropertyValue(GraphicalRepresentation.TEXT, new DataBinding<String>("drawable.name"), true);
		 */
	}

	private ShapeGraphicalRepresentation makeAutoShapeGraphicalRepresentation(AutoShape autoShape) {

		System.out.println(">>>>>> Attention, on tombe sur " + autoShape.getText() + " ShapeType " + autoShape.getShapeType().nativeName);

		Rectangle2D anchor = autoShape.getAnchor();

		System.out.println("anchor=" + anchor);

		CustomGeometry geometry = autoShape.getGeometry();
		if (geometry != null) {
			for (Path p : geometry) {
				System.out.println(" > " + p + " of " + p.getClass());

				double w = p.getW(), h = p.getH(), scaleX = Units.toPoints(1), scaleY = scaleX;
				if (w == -1) {
					w = Units.toEMU(anchor.getWidth());
				}
				else {
					scaleX = anchor.getWidth() / w;
				}
				if (h == -1) {
					h = Units.toEMU(anchor.getHeight());
				}
				else {
					scaleY = anchor.getHeight() / h;
				}

				// the guides in the shape definitions are all defined relative to each other,
				// so we build the path starting from (0,0).
				final Rectangle2D pathAnchor = new Rectangle2D.Double(0, 0, w, h);

				Context ctx = new Context(geometry, pathAnchor, autoShape);

				java.awt.geom.Path2D.Double path = p.getPath(ctx);

				// java.awt.Shape gp = p.getPath(ctx);

				// translate the result to the canvas coordinates in points
				AffineTransform at = new AffineTransform();
				at.translate(anchor.getX(), anchor.getY());
				at.scale(scaleX, scaleY);

				PathIterator pathIterator = path.getPathIterator(at);

				final float[] coords = new float[6];

				int n = 0;
				for (; !pathIterator.isDone(); pathIterator.next()) {
					int type = pathIterator.currentSegment(coords);
					System.out.println(" - segment type= " + type + " with coords: " + Arrays.toString(coords));
					n++;
				}

				java.awt.Shape canvasShape = at.createTransformedShape(path);

				System.out.println("Et hop, on obtient la shape: " + canvasShape);

			}
		}
		else {
			System.out.println("cannot retrieve geometry for " + autoShape.getText());
		}

		/*
		 *         final SimpleShape<?,?> sh = getShape();
		
		List<Outline> lst = new ArrayList<Outline>();
		CustomGeometry geom = sh.getGeometry();
		if(geom == null) {
		    return lst;
		}
		
		Rectangle2D anchor = getAnchor(graphics, sh);
		for (Path p : geom) {
		
		    double w = p.getW(), h = p.getH(), scaleX = Units.toPoints(1), scaleY = scaleX;
		    if (w == -1) {
		        w = Units.toEMU(anchor.getWidth());
		    } else {
		        scaleX = anchor.getWidth() / w;
		    }
		    if (h == -1) {
		        h = Units.toEMU(anchor.getHeight());
		    } else {
		        scaleY = anchor.getHeight() / h;
		    }
		
		    // the guides in the shape definitions are all defined relative to each other,
		    // so we build the path starting from (0,0).
		    final Rectangle2D pathAnchor = new Rectangle2D.Double(0,0,w,h);
		
		    Context ctx = new Context(geom, pathAnchor, sh);
		
		    java.awt.Shape gp = p.getPath(ctx);
		
		    // translate the result to the canvas coordinates in points
		    AffineTransform at = new AffineTransform();
		    at.translate(anchor.getX(), anchor.getY());
		    at.scale(scaleX, scaleY);
		
		    java.awt.Shape canvasShape = at.createTransformedShape(gp);
		
		    lst.add(new Outline(canvasShape, p));
		}
		
		return lst;
		}
		
		 */

		ShapeGraphicalRepresentation returned = getFactory().makeShapeGraphicalRepresentation(ShapeType.RECTANGLE);
		returned.setX(autoShape.getAnchor().getX());
		returned.setY(autoShape.getAnchor().getY());
		returned.setWidth(autoShape.getAnchor().getWidth());
		returned.setHeight(autoShape.getAnchor().getHeight());
		// returned.setBorder(getFactory().makeShapeBorder(0, 0, 0, 0));

		returned.setShadowStyle(getFactory().makeDefaultShadowStyle());

		// System.out.println("autoShape.getStrokeStyle()=" + autoShape.getStrokeStyle());
		// System.out.println("autoShape.getStrokeStyle().getPaint()=" + autoShape.getStrokeStyle().getPaint());

		if (autoShape.getStrokeStyle().getPaint() == null) {
			returned.setForeground(getFactory().makeNoneForegroundStyle());
		}
		else if (autoShape.getStrokeStyle().getPaint() instanceof SolidPaint) {
			Color lineColor = ((SolidPaint) autoShape.getStrokeStyle().getPaint()).getSolidColor().getColor();
			returned.setForeground(getFactory().makeForegroundStyle(lineColor, (float) autoShape.getStrokeStyle().getLineWidth(),
					/*DashStyle.values()[autoShape.getStrokeStyle().getLineDashing()]*/DashStyle.PLAIN_STROKE));
		}

		if (autoShape.getFillColor() != null) {
			returned.setBackground(getFactory().makeColoredBackground(autoShape.getFillColor()));
		}
		else {
			returned.setBackground(getFactory().makeEmptyBackground());
			returned.setShadowStyle(getFactory().makeNoneShadowStyle());
		}

		setTextProperties(returned, autoShape);

		// System.out.println("autoshape text=" + autoShape.getText());
		// System.out.println("gr=" + getFactory().stringRepresentation(returned));
		return returned;
	}

	private void setTextProperties(ShapeGraphicalRepresentation returned, TextShape<?, ?> textShape) {

		for (TextParagraph<?, ?, ?> textParagraph : textShape.getTextParagraphs()) {
			for (TextRun textRun : textParagraph.getTextRuns()) {
				String fontName = textRun.getFontFamily();
				Double fontSize = textRun.getFontSize();
				if (fontSize == null) {
					fontSize = 12.0;
				}
				PaintStyle paintStyle = textRun.getFontColor();
				Color color = Color.BLACK;
				if (paintStyle instanceof SolidPaint) {
					color = ((SolidPaint) paintStyle).getSolidColor().getColor();
				}

				int fontStyle = Font.PLAIN | (textRun.isBold() ? Font.BOLD : Font.PLAIN) | (textRun.isItalic() ? Font.ITALIC : Font.PLAIN);
				Font f = new Font(fontName, fontStyle, fontSize.intValue());
				TextStyle textStyle = getFactory().makeTextStyle(color, f);
				returned.setTextStyle(textStyle);

			}
		}

		/*if (textShape.getTextRun() != null) {
			TextRun textRun = textShape.getTextRun();
			RichTextRun[] rt = textRun.getRichTextRuns();
		
			if (rt.length > 0) {
				RichTextRun rtr = rt[0];
				String fontName = rtr.getFontName();
				int fontSize = rtr.getFontSize();
				Color color = rtr.getFontColor();
				int fontStyle = Font.PLAIN | (rtr.isBold() ? Font.BOLD : Font.PLAIN) | (rtr.isItalic() ? Font.ITALIC : Font.PLAIN);
				Font f = new Font(fontName, fontStyle, fontSize);
				TextStyle textStyle = getFactory().makeTextStyle(color, f);
				returned.setTextStyle(textStyle);
			}
		}*/

		returned.setIsFloatingLabel(false);
		returned.setIsMultilineAllowed(true);

		// returned.setRelativeTextX(0.5);
		// returned.setRelativeTextY(0.5);

		switch (textShape.getVerticalAlignment()) {
			case TOP:
				returned.setVerticalTextAlignment(VerticalTextAlignment.TOP);
				break;
			case BOTTOM:
				returned.setVerticalTextAlignment(VerticalTextAlignment.BOTTOM);
				break;
			case MIDDLE:
				returned.setVerticalTextAlignment(VerticalTextAlignment.MIDDLE);
				break;
			default:
				returned.setVerticalTextAlignment(VerticalTextAlignment.MIDDLE);
				break;
		}

		/*switch(textShape.getHorizontalAlignment())
		{
			case TextShape.AlignLeft:
				returned.setHorizontalTextAlignment(HorizontalTextAlignment.RIGHT);
				returned.setParagraphAlignment(ParagraphAlignment.LEFT);
				break;
			case TextShape.AlignCenter:
				returned.setHorizontalTextAlignment(HorizontalTextAlignment.CENTER);
				returned.setParagraphAlignment(ParagraphAlignment.CENTER);
				break;
			case TextShape.AlignRight:
				returned.setHorizontalTextAlignment(HorizontalTextAlignment.LEFT);
				returned.setParagraphAlignment(ParagraphAlignment.RIGHT);
				break;
			case TextShape.AlignJustify:
				returned.setParagraphAlignment(ParagraphAlignment.RIGHT);
				returned.setParagraphAlignment(ParagraphAlignment.JUSTIFY);
				break;
		}*/

		// System.out.println("Je traite la shape " + textShape + " avec " + textShape.getText());
		// System.out.println("Centre: " + textShape.isHorizontalCentered());

		if (textShape.getTextParagraphs().size() > 0) {
			TextParagraph<?, ?, ?> firstTextParagraph = textShape.getTextParagraphs().get(0);
			/*System.out.println("Premier paragraphe: " + firstTextParagraph + " align=" + firstTextParagraph.getTextAlign());
			for (TextRun textRun : firstTextParagraph.getTextRuns()) {
				System.out.println(" > (run) " + textRun.getRawText());
			}*/
			if (firstTextParagraph.getTextAlign() != null) {
				switch (firstTextParagraph.getTextAlign()) {
					case CENTER:
						returned.setHorizontalTextAlignment(HorizontalTextAlignment.CENTER);
						returned.setParagraphAlignment(ParagraphAlignment.CENTER);
						break;
					case LEFT:
						returned.setHorizontalTextAlignment(HorizontalTextAlignment.LEFT);
						// returned.setParagraphAlignment(ParagraphAlignment.LEFT);
						break;
					case RIGHT:
						returned.setHorizontalTextAlignment(HorizontalTextAlignment.RIGHT);
						// returned.setParagraphAlignment(ParagraphAlignment.RIGHT);
						break;
					case JUSTIFY:
						returned.setParagraphAlignment(ParagraphAlignment.JUSTIFY);
						// returned.setParagraphAlignment(ParagraphAlignment.JUSTIFY);
						break;

					default:
						break;
				}
			}
		}

		/*if (textShape.isHorizontalCentered()) {
			returned.setHorizontalTextAlignment(HorizontalTextAlignment.CENTER);
			returned.setParagraphAlignment(ParagraphAlignment.CENTER);
		}
		else {
			returned.setHorizontalTextAlignment(HorizontalTextAlignment.RIGHT);
			returned.setParagraphAlignment(ParagraphAlignment.LEFT);
		}*/

		returned.setLineWrap(true);

	}

	private ShapeGraphicalRepresentation makeTextBoxGraphicalRepresentation(TextBox textBox) {
		ShapeGraphicalRepresentation returned = getFactory().makeShapeGraphicalRepresentation(ShapeType.RECTANGLE);
		returned.setX(textBox.getAnchor().getX());
		returned.setY(textBox.getAnchor().getY());
		returned.setWidth(textBox.getAnchor().getWidth());
		returned.setHeight(textBox.getAnchor().getHeight());
		// returned.setBorder(getFactory().makeShapeBorder(0, 0, 0, 0));

		returned.setForeground(getFactory().makeNoneForegroundStyle());

		returned.setBackground(getFactory().makeEmptyBackground());
		returned.setShadowStyle(getFactory().makeNoneShadowStyle());

		setTextProperties(returned, textBox);

		System.out.println("textbox text=" + textBox.getText() + " gr=" + getFactory().stringRepresentation(returned));
		return returned;
	}

	private ShapeGraphicalRepresentation makePictureGraphicalRepresentation(Picture pictureShape) {
		ShapeGraphicalRepresentation returned = getFactory().makeShapeGraphicalRepresentation(ShapeType.RECTANGLE);
		/*returned.setX(pictureShape.getAnchor2D().getX());
		returned.setY(pictureShape.getAnchor2D().getY());
		returned.setWidth(pictureShape.getAnchor2D().getWidth());
		returned.setHeight(pictureShape.getAnchor2D().getHeight());
		// returned.setBorder(getFactory().makeShapeBorder(0, 0, 0, 0));
		
		BufferedImage image = new BufferedImage((int) pictureShape.getAnchor2D().getWidth(), (int) pictureShape.getAnchor2D().getHeight(),
				BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics = image.createGraphics();
		graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
		graphics.translate(-pictureShape.getAnchor2D().getX(), -pictureShape.getAnchor2D().getY());
		graphics.clipRect((int) pictureShape.getAnchor2D().getX(), (int) pictureShape.getAnchor2D().getY(),
				(int) pictureShape.getAnchor2D().getWidth(), (int) pictureShape.getAnchor2D().getHeight());
		// graphics.transform(AffineTransform.getScaleInstance(WIDTH / d.width, WIDTH / d.width));
		
		graphics.setPaint(Color.WHITE);
		graphics.fillRect((int) pictureShape.getAnchor2D().getX(), (int) pictureShape.getAnchor2D().getY(),
				(int) pictureShape.getAnchor2D().getWidth(), (int) pictureShape.getAnchor2D().getHeight());
		
		pictureShape.getPictureData().draw(graphics, pictureShape);
		returned.setBackground(getFactory().makeImageBackground(image));
		returned.setForeground(getFactory().makeNoneForegroundStyle());*/

		System.out.println("picture gr = " + getFactory().stringRepresentation(returned));
		return returned;
	}
}
