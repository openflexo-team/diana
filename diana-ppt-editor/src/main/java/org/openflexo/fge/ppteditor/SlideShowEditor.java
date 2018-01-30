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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.apache.poi.hslf.model.Shape;
import org.apache.poi.hslf.model.Slide;
import org.apache.poi.hslf.usermodel.SlideShow;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.rm.ResourceLocator;
import org.openflexo.swing.VerticalLayout;

public class SlideShowEditor extends JPanel {

	private static final Logger logger = FlexoLogger.getLogger(SlideShowEditor.class.getPackage().getName());

	private SlideShow slideShow;
	private int index;
	private File file = null;
	private PPTEditorApplication application;

	public static SlideShowEditor loadSlideShowEditor(File file, PPTEditorApplication application) {
		logger.info("Loading " + file);

		SlideShowEditor returned = new SlideShowEditor(application);

		try (FileInputStream fis = new FileInputStream(file)) {
			returned.slideShow = new SlideShow(fis);
			returned.file = file;
			System.out.println("Loaded " + file.getAbsolutePath());
			returned.init();
			return returned;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;

	}

	private SlideShowEditor(PPTEditorApplication application) {
		this.application = application;
		setLayout(new BorderLayout());
	}

	private void init() {
		JPanel miniatures = new JPanel();
		miniatures.setLayout(new VerticalLayout(10, 30, 10));
		for (Slide s : slideShow.getSlides()) {
			miniatures.add(getMiniature(s));
		}
		add(new JScrollPane(miniatures, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER),
				BorderLayout.WEST);
		select(slideShow.getSlides()[0]);
	}

	public String getTitle() {
		if (file != null) {
			return file.getName();
		}
		return PPTEditorApplication.PPT_EDITOR_LOCALIZATION.localizedForKey("untitled") + "-" + index;
	}

	public boolean save() {
		System.out.println("Saving " + file);

		try (FileOutputStream fos = new FileOutputStream(file)) {
			slideShow.write(fos);
			System.out.println("Saved " + file.getAbsolutePath());
			return true;
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;

	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	@Override
	public String toString() {
		return "SlideShowEditor:" + getTitle();
	}

	public SlideEditor getController() {
		/*if (controller == null) {
			CompoundEdit edit = factory.getUndoManager().startRecording("Initialize diagram");
			controller = new SlideEditor(getDrawing(), factory, application.getToolFactory());
			factory.getUndoManager().stopRecording(edit);
		}
		return controller;*/
		return null;
	}

	public SlideShow getSlideShow() {
		return slideShow;
	}

	public static void main(String[] args) {
		try (InputStream fis = (ResourceLocator.locateResource("TestPPT2.ppt")).openInputStream()) {
			SlideShow ssOpenned = new SlideShow(fis);
			System.out.println("Yes, j'ai ouvert le truc");
			System.out.println("Slides:" + ssOpenned.getSlides().length);
			Slide slide = ssOpenned.getSlides()[0];
			System.out.println("shapes=" + slide.getShapes().length);
			for (Shape s : slide.getShapes()) {
				System.out.println("Shape: " + s);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private Slide currentSlide = null;

	public void select(Slide slide) {
		if (currentSlide != null) {
			remove(getEditor(currentSlide).getDrawingView());
		}
		System.out.println("selecting " + slide);
		currentSlide = slide;
		for (Slide s : slideShow.getSlides()) {
			getMiniature(s).setBorder(s == slide ? BorderFactory.createLineBorder(Color.BLUE, 2) : BorderFactory.createEmptyBorder());
		}
		add(getEditor(slide).getDrawingView(), BorderLayout.CENTER);
		revalidate();
		application.slideSwitched(getEditor(slide));
	}

	public Slide getCurrentSlide() {
		return currentSlide;
	}

	/*@Override
	protected void paintComponent(Graphics g) {
		slideShow.getSlides()[0].draw((Graphics2D) g);
	}*/

	private Map<Slide, MiniatureSlidePanel> miniatures = new HashMap<>();

	protected MiniatureSlidePanel getMiniature(Slide s) {
		MiniatureSlidePanel returned = miniatures.get(s);
		if (returned == null) {
			returned = new MiniatureSlidePanel(s);
			miniatures.put(s, returned);
		}
		return returned;
	}

	private Map<Slide, SlideEditor> editors = new HashMap<>();

	protected SlideEditor getEditor(Slide s) {
		SlideEditor returned = editors.get(s);
		if (returned == null) {
			SlideDrawing sDrawing = new SlideDrawing(s);
			// sDrawing.updateGraphicalObjectsHierarchy();
			returned = new SlideEditor(sDrawing);
			// sDrawing.updateGraphicalObjectsHierarchy();
			sDrawing.printGraphicalObjectHierarchy();
			editors.put(s, returned);
		}
		return returned;
	}

	public class MiniatureSlidePanel extends JLabel {

		private double WIDTH = 200;

		private MiniatureSlidePanel(final Slide s) {
			Dimension d = s.getSlideShow().getPageSize();
			// System.out.println("Slide in " + s.getSlideShow().getPageSize());

			BufferedImage i = new BufferedImage((int) WIDTH, (int) (WIDTH * d.height / d.width), BufferedImage.TYPE_INT_RGB);
			Graphics2D graphics = i.createGraphics();
			graphics.transform(AffineTransform.getScaleInstance(WIDTH / d.width, WIDTH / d.width));
			s.draw(graphics);
			setIcon(new ImageIcon(i));
			addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					super.mouseClicked(e);
					select(s);
				}
			});
		}

	}

}
