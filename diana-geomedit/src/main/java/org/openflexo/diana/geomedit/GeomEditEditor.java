/**
 * 
 * Copyright (c) 2014, Openflexo
 * 
 * This file is part of Diana-geomedit, a component of the software infrastructure 
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

package org.openflexo.diana.geomedit;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Logger;

import org.jdom2.JDOMException;
import org.openflexo.diana.geomedit.model.GeometricConstructionFactory;
import org.openflexo.diana.geomedit.model.GeometricDiagram;
import org.openflexo.diana.geomedit.view.GeomEditDrawingView;
import org.openflexo.diana.geomedit.view.GeometricDiagramView;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.model.exceptions.InvalidDataException;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.model.undo.CompoundEdit;

/**
 * A complete editor allowing to view and edit a {@link GeometricDiagram}.<br>
 * Gives access to a {@link GeometricDiagramView} containing a {@link GeomEditDrawingView}
 * 
 * @author sylvain
 *
 */
public class GeomEditEditor {

	private static final Logger logger = FlexoLogger.getLogger(GeomEditEditor.class.getPackage().getName());

	private GeometricDiagram diagram;
	private GeometricDiagramDrawing drawing;
	private GeomEditDrawingEditor controller;

	private GeometricDiagramView diagramView;

	private int index;
	private File file = null;
	private final GeometricConstructionFactory factory;
	private final GeomEditApplication application;

	public static GeomEditEditor newDiagramEditor(GeometricConstructionFactory factory, GeomEditApplication application) {

		GeomEditEditor returned = new GeomEditEditor(factory, application);
		returned.diagram = factory.makeNewGeometricDiagram();
		return returned;

	}

	public static GeomEditEditor loadDiagramEditor(File file, GeometricConstructionFactory factory, GeomEditApplication application) {
		logger.info("Loading " + file);

		GeomEditEditor returned = new GeomEditEditor(factory, application);

		try {
			returned.diagram = (GeometricDiagram) factory.deserialize(new FileInputStream(file));
			returned.file = file;
			System.out.println("Loaded " + factory.stringRepresentation(returned.diagram));
			return returned;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (ModelDefinitionException e) {
			e.printStackTrace();
		} catch (InvalidDataException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
			logger.warning("Unhandled Exception");
		}
		return null;

		/*XMLDecoder decoder = new XMLDecoder(mapping, new DrawingBuilder());
		
		try {
			DiagramImpl drawing = (DiagramImpl) decoder.decodeObject(new FileInputStream(file));
			drawing.file = file;
			drawing.editedDrawing.init(factory);
			logger.info("Succeeded to load: " + file);
			return drawing;
		} catch (Exception e) {
			logger.warning("Failed to load: " + file + " unexpected exception: " + e.getMessage());
			e.printStackTrace();
			return null;
		}*/
	}

	private GeomEditEditor(GeometricConstructionFactory factory, GeomEditApplication application) {
		this.factory = factory;
		this.application = application;
	}

	public GeometricDiagram getDiagram() {
		return diagram;
	}

	public GeometricDiagramDrawing getDrawing() {
		if (drawing == null) {
			drawing = new GeometricDiagramDrawing(getDiagram(), factory);
		}
		return drawing;
	}

	public GeomEditDrawingEditor getController() {
		if (controller == null) {
			CompoundEdit edit = factory.getUndoManager().startRecording("Initialize diagram");
			controller = new GeomEditDrawingEditor(getDrawing(), factory, application.getToolFactory());
			factory.getUndoManager().stopRecording(edit);
		}
		return controller;
	}

	public GeometricDiagramView getDiagramView() {
		if (diagramView == null) {
			diagramView = new GeometricDiagramView(getController());
		}
		return diagramView;
	}

	public String getTitle() {
		if (file != null) {
			return file.getName();
		}
		else {
			return FlexoLocalization.localizedForKey(GeomEditApplication.LOCALIZATION, "untitled") + "-" + index;
		}
	}

	public boolean save() {
		System.out.println("Saving " + file);

		try {
			factory.serialize(diagram, new FileOutputStream(file));
			System.out.println("Saved " + file.getAbsolutePath());
			System.out.println(factory.stringRepresentation(diagram));
			return true;
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;

		/*XMLCoder coder = new XMLCoder(mapping);
		
		try {
			coder.encodeObject(this, new FileOutputStream(file));
			clearChanged();
			logger.info("Succeeded to save: " + file);
			System.out.println("> " + new XMLCoder(mapping).encodeObject(this));
			System.out.println("Et j'ai ca aussi: " + getFactory().getSerializer().serializeAsString(this));
			return true;
		} catch (Exception e) {
			logger.warning("Failed to save: " + file + " unexpected exception: " + e.getMessage());
			e.printStackTrace();
		}
				return false;
		 */

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
		return "DiagramEditor:" + getTitle();
	}

}
