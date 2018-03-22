/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * Copyright (c) 2011-2012, AgileBirds
 * 
 * This file is part of Diana-core, a component of the software infrastructure 
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

package org.openflexo.diana.control.tools;

import java.awt.geom.AffineTransform;
import java.util.logging.Logger;

import org.openflexo.diana.DianaConstants;
import org.openflexo.diana.DianaUtils;
import org.openflexo.diana.ShapeGraphicalRepresentation;
import org.openflexo.diana.control.DianaInteractiveEditor;
import org.openflexo.diana.control.DianaInteractiveEditor.EditorTool;
import org.openflexo.diana.control.actions.DrawShapeAction;
import org.openflexo.diana.geom.DianaGeometricObject.Filling;
import org.openflexo.diana.geom.DianaPoint;
import org.openflexo.diana.geom.DianaPolygon;
import org.openflexo.diana.geom.DianaRectangle;
import org.openflexo.diana.geom.DianaShape;
import org.openflexo.diana.shapes.ShapeSpecification.ShapeType;
import org.openflexo.diana.view.DrawingView;
import org.openflexo.model.undo.CompoundEdit;

/**
 * Abstract implementation for the controller of the Complex Curve drawing tool
 * 
 * @author sylvain
 * 
 * @param <ME>
 *            technology-specific controlling events type
 */
public abstract class DrawPolygonToolController<ME> extends DrawCustomShapeToolController<DianaPolygon, ME> {

	private static final Logger logger = Logger.getLogger(DrawPolygonToolController.class.getPackage().getName());

	private boolean isBuildingPoints;

	private CompoundEdit drawPolygonEdit;

	public DrawPolygonToolController(DianaInteractiveEditor<?, ?, ?> controller, DrawShapeAction control) {
		super(controller, control);
	}

	/**
	 * Return the DrawingView of the controller this tool is associated to
	 * 
	 * @return
	 */
	@Override
	public DrawingView<?, ?> getDrawingView() {
		if (getController() != null) {
			return getController().getDrawingView();
		}
		return null;
	}

	@Override
	public DianaPolygon makeDefaultShape(ME e) {
		DianaPoint newPoint = getPoint(e);
		return new DianaPolygon(Filling.FILLED, newPoint, new DianaPoint(newPoint));
	}

	public DianaPolygon getPolygon() {
		return getShape();
	}

	@Override
	protected void geometryChanged() {
		getShape().geometryChanged();
		super.geometryChanged();
	}

	@Override
	public void setShape(DianaShape<?> shape) {
		super.setShape(shape);
		stopMouseEdition();
	}

	@Override
	public boolean mouseClicked(ME e) {
		super.mouseClicked(e);
		logger.fine("Handle mouseClicked()");
		// System.out.println("Mouse clicked");
		if (!editionHasBeenStarted()) {
			startMouseEdition(e);
		}
		else {
			logger.info("Edition started");
			if (isBuildingPoints) {
				DianaPoint newPoint = getPoint(e);
				if (isFinalizationEvent(e)) {
					// System.out.println("Stopping point edition");
					getShape().getPoints().lastElement().setX(newPoint.x);
					getShape().getPoints().lastElement().setY(newPoint.y);
					stopMouseEdition();
					getController().setCurrentTool(EditorTool.SelectionTool);
				}
				else {
					// System.out.println("add point " + newPoint);
					getShape().addToPoints(newPoint);
				}
				getCurrentEditedShape().rebuildControlPoints();
				geometryChanged();
			}
			else {
				// System.out.println("Done edited shape");
				getController().setCurrentTool(EditorTool.SelectionTool);
			}
		}
		return true;
	}

	protected abstract boolean isFinalizationEvent(ME e);

	@Override
	protected void startMouseEdition(ME e) {
		drawPolygonEdit = startRecordEdit("Draw polygon");
		super.startMouseEdition(e);
		isBuildingPoints = true;
	}

	@Override
	protected void stopMouseEdition() {
		getShape().setIsFilled(true);
		isBuildingPoints = false;
		makeNewShape();
		super.stopMouseEdition();
		stopRecordEdit(drawPolygonEdit);
	}

	@Override
	public boolean mouseMoved(ME e) {
		super.mouseMoved(e);
		// System.out.println("ShapeSpecification=" + getShape());
		if (isBuildingPoints && getShape().getPointsNb() > 0) {
			DianaPoint newPoint = getPoint(e);
			// logger.info("move last point to " + newPoint);
			getShape().getPoints().lastElement().setX(newPoint.x);
			getShape().getPoints().lastElement().setY(newPoint.y);
			geometryChanged();
			return true;
		}
		return false;
	}

	@Override
	public ShapeGraphicalRepresentation buildShapeGraphicalRepresentation() {
		ShapeGraphicalRepresentation returned = getController().getFactory().makeShapeGraphicalRepresentation(ShapeType.CUSTOM_POLYGON);
		// returned.setBorder(getController().getFactory().makeShapeBorder(DianaConstants.DEFAULT_BORDER_SIZE,
		// DianaConstants.DEFAULT_BORDER_SIZE,
		// DianaConstants.DEFAULT_BORDER_SIZE, DianaConstants.DEFAULT_BORDER_SIZE));
		returned.setBackground(getController().getInspectedBackgroundStyle().cloneStyle());
		returned.setForeground(getController().getInspectedForegroundStyle().cloneStyle());
		returned.setTextStyle(getController().getInspectedTextStyle().cloneStyle());
		returned.setAllowToLeaveBounds(false);
		returned.setIsFloatingLabel(false);
		returned.setRelativeTextX(0.5);
		returned.setRelativeTextY(0.5);

		DianaRectangle boundingBox = getPolygon().getBoundingBox();
		returned.setWidth(boundingBox.getWidth());
		returned.setHeight(boundingBox.getHeight());
		AffineTransform translateAT = AffineTransform.getTranslateInstance(-boundingBox.getX(), -boundingBox.getY());

		AffineTransform scaleAT = AffineTransform.getScaleInstance(1 / boundingBox.getWidth(), 1 / boundingBox.getHeight());
		DianaPolygon normalizedPolygon = getPolygon().transform(translateAT).transform(scaleAT);
		if (parentNode instanceof ShapeGraphicalRepresentation) {
			DianaPoint pt = DianaUtils.convertNormalizedPoint(parentNode, new DianaPoint(0, 0), getController().getDrawing().getRoot());
			returned.setX(boundingBox.getX() - pt.x);
			returned.setY(boundingBox.getY() - pt.y);
		}
		else {
			returned.setX(boundingBox.getX() - DianaConstants.DEFAULT_BORDER_SIZE);
			returned.setY(boundingBox.getY() - DianaConstants.DEFAULT_BORDER_SIZE);
		}
		returned.setShapeSpecification(getController().getFactory().makePolygon(normalizedPolygon));
		return returned;
	}

}
