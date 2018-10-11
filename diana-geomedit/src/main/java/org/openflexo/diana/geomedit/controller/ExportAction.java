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

import org.openflexo.diana.geomedit.GeomEditDrawingController;
import org.openflexo.diana.geomedit.model.GeometricConstruction;
import org.openflexo.diana.geomedit.model.GeometricConstructionFactory;
import org.openflexo.diana.geomedit.view.GeomEditIconLibrary;
import org.openflexo.fge.PaletteElementSpecification;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.geom.FGEComplexCurve;
import org.openflexo.fge.geom.FGEGeneralShape;
import org.openflexo.fge.geom.FGEPolygon;
import org.openflexo.fge.geom.FGERectangle;
import org.openflexo.fge.geom.FGEShape;
import org.openflexo.fge.geom.FGEShapeUnion;
import org.openflexo.fge.shapes.ShapeSpecification.ShapeType;
import org.openflexo.model.exceptions.ModelDefinitionException;
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
		// TODO: factorize this code using FGEShape only
		if (object.getData() instanceof FGEPolygon) {
			gr.setShapeSpecification(factory.makePolygon(makeNormalizedPolygon((FGEPolygon) object.getData())));
		}
		else if (object.getData() instanceof FGEComplexCurve) {
			gr.setShapeSpecification(factory.makeComplexCurve(makeNormalizedComplexCurve((FGEComplexCurve) object.getData())));
		}
		else if (object.getData() instanceof FGEGeneralShape) {
			gr.setShapeSpecification(factory.makeGeneralShape(makeNormalizedGeneralShape((FGEGeneralShape<?>) object.getData())));
		}
		else if (object.getData() instanceof FGEShapeUnion) {
			gr.setShapeSpecification(factory.makeShapeUnion(makeNormalizedShapeUnion((FGEShapeUnion) object.getData())));
		}
		else if (object.getData() instanceof FGEShape) {
			gr.setShapeSpecification(factory.makeShapeSpecification(makeNormalizedShape((FGEShape) object.getData()), false));
		}
		else {
			gr.setShapeSpecification(factory.makeShape(ShapeType.RECTANGLE));
		}
		return elSpec;
	}

	private FGEShape<?> makeNormalizedShape(FGEShape<?> shape) {
		FGERectangle boundingBox = shape.getBoundingBox();
		AffineTransform translateAT = AffineTransform.getTranslateInstance(-boundingBox.getX(), -boundingBox.getY());
		AffineTransform scaleAT = AffineTransform.getScaleInstance(1 / boundingBox.getWidth(), 1 / boundingBox.getHeight());
		FGEShape<?> normalizedShape = (FGEShape<?>) shape.transform(translateAT).transform(scaleAT);
		return normalizedShape;
	}

	private FGEPolygon makeNormalizedPolygon(FGEPolygon polygon) {
		FGERectangle boundingBox = polygon.getBoundingBox();
		AffineTransform translateAT = AffineTransform.getTranslateInstance(-boundingBox.getX(), -boundingBox.getY());
		AffineTransform scaleAT = AffineTransform.getScaleInstance(1 / boundingBox.getWidth(), 1 / boundingBox.getHeight());
		FGEPolygon normalizedPolygon = polygon.transform(translateAT).transform(scaleAT);
		return normalizedPolygon;
	}

	private FGEComplexCurve makeNormalizedComplexCurve(FGEComplexCurve complexCurve) {
		FGERectangle boundingBox = complexCurve.getBoundingBox();
		AffineTransform translateAT = AffineTransform.getTranslateInstance(-boundingBox.getX(), -boundingBox.getY());
		AffineTransform scaleAT = AffineTransform.getScaleInstance(1 / boundingBox.getWidth(), 1 / boundingBox.getHeight());
		FGEComplexCurve normalizedComplexCurve = complexCurve.transform(translateAT).transform(scaleAT);
		return normalizedComplexCurve;
	}

	private FGEGeneralShape<?> makeNormalizedGeneralShape(FGEGeneralShape<?> generalShape) {
		FGERectangle boundingBox = generalShape.getBoundingBox();
		AffineTransform translateAT = AffineTransform.getTranslateInstance(-boundingBox.getX(), -boundingBox.getY());
		AffineTransform scaleAT = AffineTransform.getScaleInstance(1 / boundingBox.getWidth(), 1 / boundingBox.getHeight());
		FGEGeneralShape<?> normalizedGeneralShape = generalShape.transform(translateAT).transform(scaleAT);
		return normalizedGeneralShape;
	}

	private FGEShapeUnion makeNormalizedShapeUnion(FGEShapeUnion shapeUnion) {
		FGERectangle boundingBox = shapeUnion.getBoundingBox();
		AffineTransform translateAT = AffineTransform.getTranslateInstance(-boundingBox.getX(), -boundingBox.getY());
		AffineTransform scaleAT = AffineTransform.getScaleInstance(1 / boundingBox.getWidth(), 1 / boundingBox.getHeight());
		FGEShapeUnion normalizedShapeUnion = shapeUnion.transform(translateAT).transform(scaleAT);
		return normalizedShapeUnion;
	}
}
