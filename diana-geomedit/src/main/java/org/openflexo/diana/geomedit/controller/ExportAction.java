/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * Copyright (c) 2011-2012, AgileBirds
 * 
 * This file is part of Gina-swing-editor, a component of the software infrastructure 
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

package org.openflexo.diana.geomedit.controller;

import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Logger;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

import org.openflexo.diana.PaletteElementSpecification;
import org.openflexo.diana.ShapeGraphicalRepresentation;
import org.openflexo.diana.geom.DianaComplexCurve;
import org.openflexo.diana.geom.DianaGeneralShape;
import org.openflexo.diana.geom.DianaPolygon;
import org.openflexo.diana.geom.DianaRectangle;
import org.openflexo.diana.geom.DianaShape;
import org.openflexo.diana.geom.DianaShapeUnion;
import org.openflexo.diana.geomedit.GeomEditDrawingController;
import org.openflexo.diana.geomedit.model.GeometricConstruction;
import org.openflexo.diana.geomedit.model.GeometricConstructionFactory;
import org.openflexo.diana.geomedit.view.GeomEditIconLibrary;
import org.openflexo.diana.shapes.ShapeSpecification.ShapeType;
import org.openflexo.pamela.exceptions.ModelDefinitionException;
import org.openflexo.rm.FileResourceImpl;
import org.openflexo.rm.Resource;
import org.openflexo.rm.ResourceLocator;
import org.openflexo.swing.FlexoFileChooser;

public class ExportAction extends AbstractEditorActionImpl {

	private static final Logger logger = Logger.getLogger(ExportAction.class.getPackage().getName());

	public ExportAction(GeomEditDrawingController anEditorController, JFrame frame) {
		super("export", GeomEditIconLibrary.EXPORT_ICON, anEditorController, frame);
	}

	@Override
	public boolean isEnabledFor(GeometricConstruction<?> object) {
		return object != null;
	}

	@Override
	public boolean isVisibleFor(GeometricConstruction<?> object) {
		return true;
	}

	@Override
	public GeometricConstruction<?> performAction(GeometricConstruction<?> object) {

		FlexoFileChooser fileChooser = new FlexoFileChooser(getFrame());
		fileChooser.setFileFilterAsString("*.pel");
		Resource paletteDirResource = ResourceLocator.locateSourceCodeResource("Palettes/Basic");
		if (paletteDirResource instanceof FileResourceImpl) {
			fileChooser.setCurrentDirectory(((FileResourceImpl) paletteDirResource).getFile());
		}
		if (fileChooser.showSaveDialog(getFrame()) == JFileChooser.APPROVE_OPTION) {
			File exportFile = fileChooser.getSelectedFile();
			if (!exportFile.getName().endsWith(".pel")) {
				exportFile = new File(exportFile.getParentFile(), exportFile.getName() + ".pel");
			}
			String name = exportFile.getName().substring(0, exportFile.getName().length() - 4);
			logger.info("Exporting to " + exportFile + " element " + object.getData());
			PaletteElementSpecification elSpec = exportPaletteElement(object, name, exportFile.getParentFile().listFiles().length);
			FileOutputStream output;
			try {
				output = new FileOutputStream(exportFile);
				getEditorController().getFactory().serialize(elSpec, output);
				output.close();
				logger.info("Done.");
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ModelDefinitionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return object;
	}

	private PaletteElementSpecification exportPaletteElement(GeometricConstruction<?> object, String name, int index) {
		GeometricConstructionFactory factory = getEditorController().getFactory();
		PaletteElementSpecification elSpec = factory.newInstance(PaletteElementSpecification.class);
		elSpec.setName(name);
		elSpec.setIndex(index);
		ShapeGraphicalRepresentation gr = factory.makeShapeGraphicalRepresentation();
		elSpec.setGraphicalRepresentation(gr);
		gr.setForeground(object.getForeground());
		gr.setBackground(object.getBackground());
		gr.setTextStyle(factory.makeDefaultTextStyle());
		gr.setIsFloatingLabel(false);
		gr.setRelativeTextX(0.5);
		gr.setRelativeTextY(0.5);
		gr.setWidth(1);
		gr.setHeight(1);
		// TODO: factorize this code using DianaShape only
		if (object.getData() instanceof DianaPolygon) {
			gr.setShapeSpecification(factory.makePolygon(makeNormalizedPolygon((DianaPolygon) object.getData())));
		}
		else if (object.getData() instanceof DianaComplexCurve) {
			gr.setShapeSpecification(factory.makeComplexCurve(makeNormalizedComplexCurve((DianaComplexCurve) object.getData())));
		}
		else if (object.getData() instanceof DianaGeneralShape) {
			gr.setShapeSpecification(factory.makeGeneralShape(makeNormalizedGeneralShape((DianaGeneralShape<?>) object.getData())));
		}
		else if (object.getData() instanceof DianaShapeUnion) {
			gr.setShapeSpecification(factory.makeShapeUnion(makeNormalizedShapeUnion((DianaShapeUnion) object.getData())));
		}
		else if (object.getData() instanceof DianaShape) {
			gr.setShapeSpecification(factory.makeShapeSpecification(makeNormalizedShape((DianaShape) object.getData()), false));
		}
		else {
			gr.setShapeSpecification(factory.makeShape(ShapeType.RECTANGLE));
		}
		return elSpec;
	}

	private DianaShape<?> makeNormalizedShape(DianaShape<?> shape) {
		DianaRectangle boundingBox = shape.getBoundingBox();
		AffineTransform translateAT = AffineTransform.getTranslateInstance(-boundingBox.getX(), -boundingBox.getY());
		AffineTransform scaleAT = AffineTransform.getScaleInstance(1 / boundingBox.getWidth(), 1 / boundingBox.getHeight());
		DianaShape<?> normalizedShape = (DianaShape<?>) shape.transform(translateAT).transform(scaleAT);
		return normalizedShape;
	}

	private DianaPolygon makeNormalizedPolygon(DianaPolygon polygon) {
		DianaRectangle boundingBox = polygon.getBoundingBox();
		AffineTransform translateAT = AffineTransform.getTranslateInstance(-boundingBox.getX(), -boundingBox.getY());
		AffineTransform scaleAT = AffineTransform.getScaleInstance(1 / boundingBox.getWidth(), 1 / boundingBox.getHeight());
		DianaPolygon normalizedPolygon = polygon.transform(translateAT).transform(scaleAT);
		return normalizedPolygon;
	}

	private DianaComplexCurve makeNormalizedComplexCurve(DianaComplexCurve complexCurve) {
		DianaRectangle boundingBox = complexCurve.getBoundingBox();
		AffineTransform translateAT = AffineTransform.getTranslateInstance(-boundingBox.getX(), -boundingBox.getY());
		AffineTransform scaleAT = AffineTransform.getScaleInstance(1 / boundingBox.getWidth(), 1 / boundingBox.getHeight());
		DianaComplexCurve normalizedComplexCurve = complexCurve.transform(translateAT).transform(scaleAT);
		return normalizedComplexCurve;
	}

	private DianaGeneralShape<?> makeNormalizedGeneralShape(DianaGeneralShape<?> generalShape) {
		DianaRectangle boundingBox = generalShape.getBoundingBox();
		AffineTransform translateAT = AffineTransform.getTranslateInstance(-boundingBox.getX(), -boundingBox.getY());
		AffineTransform scaleAT = AffineTransform.getScaleInstance(1 / boundingBox.getWidth(), 1 / boundingBox.getHeight());
		DianaGeneralShape<?> normalizedGeneralShape = generalShape.transform(translateAT).transform(scaleAT);
		return normalizedGeneralShape;
	}

	private DianaShapeUnion makeNormalizedShapeUnion(DianaShapeUnion shapeUnion) {
		DianaRectangle boundingBox = shapeUnion.getBoundingBox();
		AffineTransform translateAT = AffineTransform.getTranslateInstance(-boundingBox.getX(), -boundingBox.getY());
		AffineTransform scaleAT = AffineTransform.getScaleInstance(1 / boundingBox.getWidth(), 1 / boundingBox.getHeight());
		DianaShapeUnion normalizedShapeUnion = shapeUnion.transform(translateAT).transform(scaleAT);
		return normalizedShapeUnion;
	}
}
