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

package org.openflexo.diana.connectors.rpc;

import java.awt.event.MouseEvent;
import java.util.logging.Logger;

import org.openflexo.diana.Drawing.DrawingTreeNode;
import org.openflexo.diana.connectors.impl.RectPolylinConnector;
import org.openflexo.diana.control.DianaEditor;
import org.openflexo.diana.geom.DianaGeometricObject.Filling;
import org.openflexo.diana.geom.DianaGeometricObject.SimplifiedCardinalDirection;
import org.openflexo.diana.geom.DianaPoint;
import org.openflexo.diana.geom.DianaRectangle;
import org.openflexo.diana.geom.DianaSegment;
import org.openflexo.diana.geom.area.DianaArea;
import org.openflexo.diana.geom.area.DianaEmptyArea;
import org.openflexo.diana.geom.area.DianaHalfBand;

public class AdjustableIntermediateSegment extends RectPolylinAdjustableSegment {
	static final Logger LOGGER = Logger.getLogger(AdjustableIntermediateSegment.class.getPackage().getName());

	private boolean consistentData = false;
	private int index;
	private DianaSegment currentSegment;
	private DianaSegment previousSegment;
	private DianaSegment nextSegment;
	private DianaSegment beforePreviousSegment;
	private DianaSegment afterNextSegment;
	private SimplifiedCardinalDirection currentOrientation;
	private SimplifiedCardinalDirection previousOrientation;
	private SimplifiedCardinalDirection nextOrientation;
	private DianaArea draggingAuthorizedArea;

	public AdjustableIntermediateSegment(DianaSegment segment, RectPolylinConnector connector) {
		super(segment, connector);
		retrieveInfos();
	}

	private void retrieveInfos() {
		currentSegment = getArea();
		index = getPolylin().getSegmentIndex(currentSegment);
		if (index <= 0 || index >= getPolylin().getSegmentNb() - 1) {
			LOGGER.warning("Inconsistent data while managing adjustable segment in RectPolylinConnectorSpecification "
					+ getNode().getGraphicalRepresentation().getText() + " index=" + index + " polylin.getSegmentNb()="
					+ getPolylin().getSegmentNb());
			return;
		}
		previousSegment = getPolylin().getSegmentAt(index - 1);
		nextSegment = getPolylin().getSegmentAt(index + 1);
		if (currentSegment.getApproximatedOrientation() == null || previousSegment.getApproximatedOrientation() == null
				|| nextSegment.getApproximatedOrientation() == null) {
			LOGGER.warning("Inconsistent data while managing adjustable segment in RectPolylinConnectorSpecification");
			return;
		}
		if (index > 1) {
			beforePreviousSegment = getPolylin().getSegmentAt(index - 2);
		}
		if (index + 2 < getPolylin().getSegmentNb()) {
			afterNextSegment = getPolylin().getSegmentAt(index + 2);
		}
		currentOrientation = currentSegment.getApproximatedOrientation();
		previousOrientation = previousSegment.getApproximatedOrientation();
		nextOrientation = nextSegment.getApproximatedOrientation();

		if (currentOrientation.isHorizontal()) {
			if (previousOrientation == SimplifiedCardinalDirection.NORTH) {
				if (nextOrientation == SimplifiedCardinalDirection.NORTH) {
					draggingAuthorizedArea = new DianaRectangle(previousSegment.getP1(), nextSegment.getP2(), Filling.FILLED);
				}
				else if (nextOrientation == SimplifiedCardinalDirection.SOUTH) {
					DianaSegment limit;
					if (previousSegment.getP1().y > nextSegment.getP2().y) {
						limit = new DianaSegment(new DianaPoint(currentSegment.getP1().x, nextSegment.getP2().y),
								new DianaPoint(currentSegment.getP2().x, nextSegment.getP2().y));
					}
					else {
						limit = new DianaSegment(new DianaPoint(currentSegment.getP1().x, previousSegment.getP1().y),
								new DianaPoint(currentSegment.getP2().x, previousSegment.getP1().y));
					}
					draggingAuthorizedArea = new DianaHalfBand(limit, SimplifiedCardinalDirection.NORTH);
				}
			}
			else if (previousOrientation == SimplifiedCardinalDirection.SOUTH) {
				if (nextOrientation == SimplifiedCardinalDirection.SOUTH) {
					draggingAuthorizedArea = new DianaRectangle(previousSegment.getP1(), nextSegment.getP2(), Filling.FILLED);
				}
				else if (nextOrientation == SimplifiedCardinalDirection.NORTH) {
					DianaSegment limit;
					if (previousSegment.getP1().y < nextSegment.getP2().y) {
						limit = new DianaSegment(new DianaPoint(currentSegment.getP1().x, nextSegment.getP2().y),
								new DianaPoint(currentSegment.getP2().x, nextSegment.getP2().y));
					}
					else {
						limit = new DianaSegment(new DianaPoint(currentSegment.getP1().x, previousSegment.getP1().y),
								new DianaPoint(currentSegment.getP2().x, previousSegment.getP1().y));
					}
					draggingAuthorizedArea = new DianaHalfBand(limit, SimplifiedCardinalDirection.SOUTH);
				}
			}
		}
		if (currentOrientation.isVertical()) {
			if (previousOrientation == SimplifiedCardinalDirection.EAST) {
				if (nextOrientation == SimplifiedCardinalDirection.EAST) {
					draggingAuthorizedArea = new DianaRectangle(previousSegment.getP1(), nextSegment.getP2(), Filling.FILLED);
				}
				else if (nextOrientation == SimplifiedCardinalDirection.WEST) {
					DianaSegment limit;
					if (previousSegment.getP1().x < nextSegment.getP2().x) {
						limit = new DianaSegment(new DianaPoint(nextSegment.getP2().x, currentSegment.getP1().y),
								new DianaPoint(nextSegment.getP2().x, currentSegment.getP2().y));
					}
					else {
						limit = new DianaSegment(new DianaPoint(previousSegment.getP1().x, currentSegment.getP1().y),
								new DianaPoint(previousSegment.getP1().x, currentSegment.getP2().y));
					}
					draggingAuthorizedArea = new DianaHalfBand(limit, SimplifiedCardinalDirection.EAST);
				}
			}
			else if (previousOrientation == SimplifiedCardinalDirection.WEST) {
				if (nextOrientation == SimplifiedCardinalDirection.WEST) {
					draggingAuthorizedArea = new DianaRectangle(previousSegment.getP1(), nextSegment.getP2(), Filling.FILLED);
				}
				else if (nextOrientation == SimplifiedCardinalDirection.EAST) {
					DianaSegment limit;
					if (previousSegment.getP1().x > nextSegment.getP2().x) {
						limit = new DianaSegment(new DianaPoint(nextSegment.getP2().x, currentSegment.getP1().y),
								new DianaPoint(nextSegment.getP2().x, currentSegment.getP2().y));
					}
					else {
						limit = new DianaSegment(new DianaPoint(previousSegment.getP1().x, currentSegment.getP1().y),
								new DianaPoint(previousSegment.getP1().x, currentSegment.getP2().y));
					}
					draggingAuthorizedArea = new DianaHalfBand(limit, SimplifiedCardinalDirection.WEST);
				}
			}
		}

		if (draggingAuthorizedArea == null) {
			LOGGER.warning("Inconsistent data while managing adjustable segment in RectPolylinConnectorSpecification");
			return;
		}

		consistentData = true;
	}

	@Override
	public void startDragging(DianaEditor<?> controller, DianaPoint startPoint) {
		super.startDragging(controller, startPoint);
		retrieveInfos();
	}

	@Override
	public DianaArea getDraggingAuthorizedArea() {
		if (!consistentData) {
			return new DianaEmptyArea();
		}

		return draggingAuthorizedArea;
		/*
		 	if (currentOrientation.isHorizontal()) {
				if (previousOrientation == SimplifiedCardinalDirection.NORTH) {
					if (nextOrientation == SimplifiedCardinalDirection.NORTH) {
						return new DianaRectangle(previousSegment.getP1(),nextSegment.getP2(),Filling.FILLED);
					}
					else if (nextOrientation == SimplifiedCardinalDirection.SOUTH) {
						DianaSegment limit;						
						if (previousSegment.getP1().y>nextSegment.getP2().y) {
							limit = new DianaSegment(
									new DianaPoint(currentSegment.getP1().x,nextSegment.getP2().y),
									new DianaPoint(currentSegment.getP2().x,nextSegment.getP2().y));
						}
						else {
							limit = new DianaSegment(
									new DianaPoint(currentSegment.getP1().x,previousSegment.getP1().y),
									new DianaPoint(currentSegment.getP2().x,previousSegment.getP1().y));
						}
						return new DianaHalfBand(limit,SimplifiedCardinalDirection.NORTH);
					}
				}
				else if (previousOrientation == SimplifiedCardinalDirection.SOUTH) {
					if (nextOrientation == SimplifiedCardinalDirection.SOUTH) {
						return new DianaRectangle(previousSegment.getP1(),nextSegment.getP2(),Filling.FILLED);
					}
					else if (nextOrientation == SimplifiedCardinalDirection.NORTH) {
						DianaSegment limit;						
						if (previousSegment.getP1().y<nextSegment.getP2().y) {
							limit = new DianaSegment(
									new DianaPoint(currentSegment.getP1().x,nextSegment.getP2().y),
									new DianaPoint(currentSegment.getP2().x,nextSegment.getP2().y));
						}
						else {
							limit = new DianaSegment(
									new DianaPoint(currentSegment.getP1().x,previousSegment.getP1().y),
									new DianaPoint(currentSegment.getP2().x,previousSegment.getP1().y));
						}
						return new DianaHalfBand(limit,SimplifiedCardinalDirection.SOUTH);
					}
				}
			}
			if (currentOrientation.isVertical()) {
				if (previousOrientation == SimplifiedCardinalDirection.EAST) {
					if (nextOrientation == SimplifiedCardinalDirection.EAST) {
						return new DianaRectangle(previousSegment.getP1(),nextSegment.getP2(),Filling.FILLED);
					}
					else if (nextOrientation == SimplifiedCardinalDirection.WEST) {
						DianaSegment limit;						
						if (previousSegment.getP1().x<nextSegment.getP2().x) {
							limit = new DianaSegment(
									new DianaPoint(nextSegment.getP2().x,currentSegment.getP1().y),
									new DianaPoint(nextSegment.getP2().x,currentSegment.getP2().y));
						}
						else {
							limit = new DianaSegment(
									new DianaPoint(previousSegment.getP1().x,currentSegment.getP1().y),
									new DianaPoint(previousSegment.getP1().x,currentSegment.getP2().y));
						}
						return new DianaHalfBand(limit,SimplifiedCardinalDirection.EAST);
					}
				}
				else if (previousOrientation == SimplifiedCardinalDirection.WEST) {
					if (nextOrientation == SimplifiedCardinalDirection.WEST) {
						return new DianaRectangle(previousSegment.getP1(),nextSegment.getP2(),Filling.FILLED);
					}
					else if (nextOrientation == SimplifiedCardinalDirection.EAST) {
						DianaSegment limit;						
						if (previousSegment.getP1().x>nextSegment.getP2().x) {
							limit = new DianaSegment(
									new DianaPoint(nextSegment.getP2().x,currentSegment.getP1().y),
									new DianaPoint(nextSegment.getP2().x,currentSegment.getP2().y));
						}
						else {
							limit = new DianaSegment(
									new DianaPoint(previousSegment.getP1().x,currentSegment.getP1().y),
									new DianaPoint(previousSegment.getP1().x,currentSegment.getP2().y));
						}
						return new DianaHalfBand(limit,SimplifiedCardinalDirection.WEST);
					}
				}
			}
		*/

		// logger.warning("Inconsistent data while managing adjustable segment in RectPolylinConnectorSpecification");
		// return new DianaEmptyArea();
	}

	@Override
	public boolean dragToPoint(DianaPoint newRelativePoint, DianaPoint pointRelativeToInitialConfiguration, DianaPoint newAbsolutePoint,
			DianaPoint initialPoint, MouseEvent event) {
		DianaPoint pt = getNearestPointOnAuthorizedArea(newRelativePoint);

		if (pt == null) {
			LOGGER.warning("Unexpected null point while dragging AdjustableIntermediateSegment " + getDraggingAuthorizedArea() + " pt="
					+ newRelativePoint);
			return false;
		}

		DianaPoint p1 = getPolylin().getPointAt(index);
		if (p1 == null) {
			LOGGER.warning("Inconsistent data while managing adjustable segment in RectPolylinConnectorSpecification");
			return false;
		}
		DianaPoint p2 = getPolylin().getPointAt(index + 1);
		if (p2 == null) {
			LOGGER.warning("Inconsistent data while managing adjustable segment in RectPolylinConnectorSpecification");
			return false;
		}

		if (currentOrientation.isHorizontal()) {
			p1.y = pt.y;
			p2.y = pt.y;
		}
		else if (currentOrientation.isVertical()) {
			p1.x = pt.x;
			p2.x = pt.x;
		}
		else {
			LOGGER.warning("Inconsistent data while managing adjustable segment in RectPolylinConnectorSpecification");
			return false;
		}

		getPolylin().updatePointAt(index, p1);
		getConnector().getControlPoints().get(index).setPoint(p1);

		getPolylin().updatePointAt(index + 1, p2);
		getConnector().getControlPoints().get(index + 1).setPoint(p2);

		getConnector()._connectorChanged(true);
		getNode().notifyConnectorModified();

		return true;

		/*System.out.println("Index = "+index+" pour "+getGraphicalRepresentation().getText());
		System.out.println("getDraggingAuthorizedArea() = "+getDraggingAuthorizedArea());
		System.out.println("pt = "+pt);
		System.out.println("polylin = "+polylin);
		System.out.println("polylin.getSegmentNb() = "+polylin.getSegmentNb());
		System.out.println("polylin.getPointAt(index) = "+polylin.getPointAt(index));*/
		/*if (currentOrientation.isHorizontal()) {
			DianaPoint p1 = getPolylin().getPointAt(index);
			if (p1 == null) {
				logger.warning("Inconsistent data while managing adjustable segment in RectPolylinConnectorSpecification");
				return false;
			}
			p1.y = pt.y;
			getPolylin().updatePointAt(index,p1);
			getConnector()._getControlPoints().elementAt(index).setPoint(p1);
			DianaPoint p2 = getPolylin().getPointAt(index+1);
			if (p2 == null) {
				logger.warning("Inconsistent data while managing adjustable segment in RectPolylinConnectorSpecification");
				return false;
			}
			p2.y = pt.y;
			getPolylin().updatePointAt(index+1,p2);
			getConnector()._getControlPoints().elementAt(index+1).setPoint(p2);
			getConnector().rememberLayout();
			getGraphicalRepresentation().notifyConnectorChanged();
			return true;
		}
		else if (currentOrientation.isVertical()) {
			DianaPoint p1 = getPolylin().getPointAt(index);
			if (p1 == null) {
				logger.warning("Inconsistent data while managing adjustable segment in RectPolylinConnectorSpecification");
				return false;
			}
			p1.x = pt.x;
			getPolylin().updatePointAt(index,p1);
			getConnector()._getControlPoints().elementAt(index).setPoint(p1);
			DianaPoint p2 = getPolylin().getPointAt(index+1);
			if (p2 == null) {
				logger.warning("Inconsistent data while managing adjustable segment in RectPolylinConnectorSpecification");
				return false;
			}
			p2.x = pt.x;
			getPolylin().updatePointAt(index+1,p2);
			getConnector()._getControlPoints().elementAt(index+1).setPoint(p2);
			getConnector().rememberLayout();
			getGraphicalRepresentation().notifyConnectorChanged();
			return true;
		}
		else {
			logger.warning("Inconsistent data while managing adjustable segment in RectPolylinConnectorSpecification");
			return false;
		}			*/
	}

	@Override
	public void stopDragging(DianaEditor<?> controller, DrawingTreeNode<?, ?> focused) {
		if (beforePreviousSegment != null && beforePreviousSegment.overlap(currentSegment)) {
			getConnector()._simplifyLayoutOfCurrentPolylinByDeletingTwoPoints(index - 1);
		}
		if (afterNextSegment != null && afterNextSegment.overlap(currentSegment)) {
			getConnector()._simplifyLayoutOfCurrentPolylinByDeletingTwoPoints(index + 1);
		}
		super.stopDragging(controller, focused);
	}
}
