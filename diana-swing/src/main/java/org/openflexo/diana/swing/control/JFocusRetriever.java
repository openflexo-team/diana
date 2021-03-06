/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * Copyright (c) 2011-2012, AgileBirds
 * 
 * This file is part of Diana-swing, a component of the software infrastructure 
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

package org.openflexo.diana.swing.control;

import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;

import org.openflexo.diana.DianaConstants;
import org.openflexo.diana.Drawing.ConnectorNode;
import org.openflexo.diana.Drawing.ContainerNode;
import org.openflexo.diana.Drawing.DrawingTreeNode;
import org.openflexo.diana.Drawing.GeometricNode;
import org.openflexo.diana.Drawing.RootNode;
import org.openflexo.diana.Drawing.ShapeNode;
import org.openflexo.diana.control.DianaInteractiveEditor;
import org.openflexo.diana.control.DianaInteractiveViewer;
import org.openflexo.diana.cp.ControlArea;
import org.openflexo.diana.geom.DianaGeometricObject;
import org.openflexo.diana.geom.DianaPoint;
import org.openflexo.diana.geom.DianaSegment;
import org.openflexo.diana.swing.view.JDianaView;
import org.openflexo.diana.swing.view.JDrawingView;
import org.openflexo.diana.swing.view.JLabelView;
import org.openflexo.diana.view.DianaView;

/**
 * Utility class used in a general context to retrieve the focus owner in a graphical context.<br>
 * 
 * The policy is to return the closest connector or geometrical object from the cursor, or the top-most shape where the cursor is located
 * in.<br>
 * Manage focusable properties, as well as layers.
 * 
 * @author sylvain
 * 
 */
public class JFocusRetriever {

	private static final Logger logger = Logger.getLogger(JFocusRetriever.class.getPackage().getName());

	private final JDrawingView<?> drawingView;

	public JFocusRetriever(JDrawingView<?> aDrawingView) {
		drawingView = aDrawingView;
	}

	private boolean cursorChanged = false;

	private Component cursoredComponent;

	private void resetCursorIfRequired() {
		if (cursorChanged) {
			cursoredComponent.setCursor(null);
			cursoredComponent = null;
			cursorChanged = false;
			getController()._setFocusedControlArea(null);
		}
	}

	public DianaInteractiveViewer<?, ?, ?> getController() {
		return (DianaInteractiveViewer<?, ?, ?>) drawingView.getController();
	}

	public void handleMouseMove(MouseEvent event) {
		DrawingTreeNode<?, ?> newFocusedObject = getFocusedObject(event);

		// System.out.println("handleMouseMove() newFocusedObject=" + newFocusedObject);

		if (newFocusedObject != null) {
			getController().setFocusedFloatingLabel(focusOnFloatingLabel(newFocusedObject, event) ? newFocusedObject : null);
			ControlArea<?> cp = getFocusedControlAreaForDrawable(newFocusedObject, event);
			// System.out.println("Focused CP=" + cp);
			if (cp != null) {
				if (cursoredComponent != null) {
					cursoredComponent.setCursor(null);
				}
				cursoredComponent = event.getComponent();
				cursoredComponent.setCursor(cp.getDraggingCursor());
				cursorChanged = true;
				getController()._setFocusedControlArea(cp);
			}
			else {
				resetCursorIfRequired();
			}
		}
		else {
			if (getController().getFocusedFloatingLabel() != null) {
				getController().setFocusedFloatingLabel(null);
			}
			resetCursorIfRequired();
		}

		// if (newFocusedObject !=
		// drawingView.getController().getFocusedObject()) {
		getController().setFocusedObject(newFocusedObject);
		// }

	}

	public boolean focusOnFloatingLabel(DrawingTreeNode<?, ?> node, MouseEvent event) {
		return focusOnFloatingLabel(node, (Component) event.getSource(), event.getPoint());
	}

	private boolean focusOnFloatingLabel(DrawingTreeNode<?, ?> node, Component eventSource, Point eventLocation) {
		// if (!graphicalRepresentation.hasText()) return false;

		if (node.isDeleted()) {
			return false;
		}

		if (node instanceof GeometricNode) {
			if (node.hasText()) {
				JLabelView<?> labelView = drawingView.getLabelView((GeometricNode<?>) node);
				if (labelView != null) {
					Point p = SwingUtilities.convertPoint(eventSource, eventLocation, drawingView);
					return labelView.getBounds().contains(p);
				}
			}
			return false;
		}

		JDianaView<?, ?> view = drawingView.viewForNode(node);
		if (view == null) {
			logger.warning(
					"Unexpected null view for node " + node + " AbstractDianaEditor=" + getController() + " JDrawingView=" + drawingView);
			/*Map<DrawingTreeNode<?, ?>, DianaView<?,?>> contents = getController().getContents();
			System.out.println("Pour node, j'ai:");
			DianaView v = contents.get(node);
			System.out.println("Prout");*/
		}
		DianaView<?, ?> parenttView = node == drawingView.getDrawing().getRoot() ? drawingView
				: drawingView.viewForNode(node.getParentNode());
		Point p = SwingUtilities.convertPoint(eventSource, eventLocation, (Component) parenttView);
		if (node.hasText() && view != null) {
			JLabelView<?> labelView = view.getLabelView();
			if (labelView != null) {
				return labelView.getBounds().contains(p);
			}
		}
		return false;

	}

	public ControlArea<?> getFocusedControlAreaForDrawable(DrawingTreeNode<?, ?> node, MouseEvent event) {
		return getFocusedControlAreaForDrawable(node, drawingView.getDrawing().getRoot(), event);
	}

	public ControlArea<?> getFocusedControlAreaForDrawable(DrawingTreeNode<?, ?> node, ContainerNode<?, ?> container, MouseEvent event) {
		ControlArea<?> returned = null;
		double selectionDistance = DianaConstants.SELECTION_DISTANCE; // Math.max(5.0,DianaConstants.SELECTION_DISTANCE*getScale());
		if (node instanceof GeometricNode) {
			GeometricNode<?> geometricNode = (GeometricNode<?>) node;
			Point viewPoint = SwingUtilities.convertPoint((Component) event.getSource(), event.getPoint(),
					(Component) drawingView.viewForNode(container));

			// Look if we are near a CP
			double distanceToNearestGeometricObject = Double.POSITIVE_INFINITY;
			for (ControlArea<?> cp : geometricNode.getControlAreas()) {
				double cpDistance = cp.getDistanceToArea(viewPoint, getScale());
				if (cpDistance < selectionDistance && cpDistance < distanceToNearestGeometricObject
						&& (returned == null || getController().preferredFocusedControlArea(returned, cp) == cp)) {
					distanceToNearestGeometricObject = cpDistance;
					returned = cp;
				}
			}
			return returned;
		}

		DianaView<?, ?> view = drawingView.viewForNode(container);
		Point p = SwingUtilities.convertPoint((Component) event.getSource(), event.getPoint(), (Component) view);
		DianaView<?, ?> v = drawingView.viewForNode(node);
		Point p2 = SwingUtilities.convertPoint((Component) view, p, (Component) v);

		if (node instanceof ShapeNode) {
			ShapeNode<?> shapeNode = (ShapeNode<?>) node;
			// Take the borders under account and translate point p2
			p2.x -= (int) (shapeNode.getBorderLeft() * getScale());
			p2.y -= (int) (shapeNode.getBorderTop() * getScale());
		}

		DianaPoint p3 = v.getNode().convertViewCoordinatesToNormalizedPoint(p2, getScale());

		if (node instanceof ShapeNode) {
			ShapeNode<?> shapeNode = (ShapeNode<?>) node;
			if (Double.isNaN(p3.getX()) && shapeNode.getWidth() == 0) {
				p3.x = 1;
			}
			if (Double.isNaN(p3.getY()) && shapeNode.getHeight() == 0) {
				p3.y = 1;
			}
			double smallestDistance = Double.POSITIVE_INFINITY;
			for (ControlArea<?> ca : shapeNode.getControlAreas()) {
				double cpDistance = ca.getDistanceToArea(p3, getScale());
				if (cpDistance < selectionDistance && cpDistance < smallestDistance
						&& (returned == null || getController().preferredFocusedControlArea(returned, ca) == ca)) {
					returned = ca;
					smallestDistance = cpDistance;
				}
			}
		}
		else if (node instanceof ConnectorNode) {
			ConnectorNode<?> connectorNode = (ConnectorNode<?>) node;
			double smallestDistance = Double.POSITIVE_INFINITY;
			for (ControlArea<?> ca : connectorNode.getControlAreas()) {
				double caDistance = ca.getDistanceToArea(p3, getScale());
				if (caDistance < selectionDistance && caDistance < smallestDistance
						&& (returned == null || getController().preferredFocusedControlArea(returned, ca) == ca)) {
					returned = ca;
					smallestDistance = caDistance;
				}
			}
		}
		else if (node instanceof RootNode) {
			RootNode<?> rootNode = (RootNode<?>) node;
			double smallestDistance = Double.POSITIVE_INFINITY;
			if (rootNode.getControlAreas() != null) {
				for (ControlArea<?> ca : rootNode.getControlAreas()) {
					if (ca != null) {
						double caDistance = ca.getDistanceToArea(p3, getScale());
						if (caDistance < selectionDistance && caDistance < smallestDistance
								&& (returned == null || getController().preferredFocusedControlArea(returned, ca) == ca)) {
							returned = ca;
							smallestDistance = caDistance;
						}
					}
				}
			}
		}

		return returned;
	}

	public DrawingTreeNode<?, ?> getFocusedObject(MouseEvent event) {
		if (getController() instanceof DianaInteractiveEditor) {
			DianaInteractiveEditor<?, ?, ?> editor = (DianaInteractiveEditor<?, ?, ?>) getController();
			switch (editor.getCurrentTool()) {
				case SelectionTool:
					return getFocusedObject(drawingView.getDrawing().getRoot(), event);
				case DrawShapeTool:
				case DrawCustomShapeTool:
				case DrawConnectorTool:
				case DrawTextTool:
					DrawingTreeNode<?, ?> returned = getFocusedObject(drawingView.getDrawing().getRoot(), event);
					if (returned == null) {
						returned = drawingView.getDrawing().getRoot();
					}
					return returned;
				/*if (editor.getDrawCustomShapeToolController() != null) {
				if (editor.getDrawCustomShapeToolController().editionHasBeenStarted()
				&& editor.getDrawCustomShapeToolController().getCurrentEditedShape() != null) {
				return editor.getDrawCustomShapeToolController().getCurrentEditedShape();
				} else {
				DrawingTreeNode<?, ?> returned = getFocusedObject(drawingView.getDrawing().getRoot(), event);
				if (returned == null) {
				returned = drawingView.getDrawing().getRoot();
				}
				return returned;
				}
				}*/
				default:
					return getFocusedObject(drawingView.getDrawing().getRoot(), event);
			}
		}
		return getFocusedObject(drawingView.getDrawing().getRoot(), event);
	}

	public DrawingTreeNode<?, ?> getFocusedObject(DropTargetDragEvent event) {
		return getFocusedObject(drawingView.getDrawing().getRoot(), event);
	}

	public DrawingTreeNode<?, ?> getFocusedObject(DropTargetDropEvent event) {
		return getFocusedObject(drawingView.getDrawing().getRoot(), event);
	}

	public DrawingTreeNode<?, ?> getFocusedObject(ContainerNode<?, ?> node, MouseEvent event) {
		return getFocusedObject(node, (Component) event.getSource(), event.getPoint());
	}

	public DrawingTreeNode<?, ?> getFocusedObject(ContainerNode<?, ?> node, DropTargetDragEvent event) {
		return getFocusedObject(node, event.getDropTargetContext().getComponent(), event.getLocation());
	}

	public DrawingTreeNode<?, ?> getFocusedObject(ContainerNode<?, ?> node, DropTargetDropEvent event) {
		return getFocusedObject(node, event.getDropTargetContext().getComponent(), event.getLocation());
	}

	private DrawingTreeNode<?, ?> getFocusedObject(ContainerNode<?, ?> node, Component eventSource, Point eventLocation) {

		// System.out.println("\n--------->>>>>>>>>>> called getFocusedObject eventSource=" + eventSource + " eventLocation=" +
		// eventLocation);
		// System.out.println("node=" + node);

		DianaView<?, ?> view = drawingView.viewForNode(node);
		Point p = SwingUtilities.convertPoint(eventSource, eventLocation, (Component) view);

		double distanceToNearestConnector = Double.POSITIVE_INFINITY;
		double smallestDistanceToCPOfNearestConnector = Double.POSITIVE_INFINITY;
		ConnectorNode<?> nearestConnector = null;
		List<ShapeNode<?>> enclosingShapes = new ArrayList<ShapeNode<?>>();

		double distanceToNearestGeometricObject = Double.POSITIVE_INFINITY;
		int layerOfNearestGeometricObject = 0;
		GeometricNode<?> nearestGeometricObject = null;
		List<GeometricNode<?>> enclosingGeometricObjects = new ArrayList<GeometricNode<?>>();

		// iterate on all contained objects

		ControlArea<?> focusedCP = null;

		for (DrawingTreeNode<?, ?> childNode : node.getChildNodes()) {

			if (childNode == null) {
				logger.warning("Null child node ");
				continue;
			}
			double selectionDistance = Math.max(5.0, DianaConstants.SELECTION_DISTANCE * getScale());
			// Work on object only if object is visible and focusable
			if (childNode.shouldBeDisplayed() /*&& childNode.getGraphicalRepresentation().getIsFocusable()*/) {

				if (childNode instanceof GeometricNode) {

					GeometricNode<?> geometricNode = (GeometricNode<?>) childNode;
					Point viewPoint = SwingUtilities.convertPoint(eventSource, eventLocation, (Component) drawingView.viewForNode(node));
					DianaPoint point = childNode.convertViewCoordinatesToNormalizedPoint(viewPoint, getScale());

					if (geometricNode.getGraphicalRepresentation().getGeometricObject() != null) {
						if (geometricNode.getGraphicalRepresentation().getGeometricObject().containsPoint(point)) {
							enclosingGeometricObjects.add(geometricNode);
						}
						else {
							DianaPoint nearestPoint = geometricNode.getGraphicalRepresentation().getGeometricObject()
									.getNearestPoint(point);
							if (nearestPoint != null) {
								double distance = DianaSegment.getLength(point, nearestPoint) * getScale();
								if (distance < selectionDistance && (distance < distanceToNearestGeometricObject
										&& Math.abs(distance - distanceToNearestGeometricObject) > DianaGeometricObject.EPSILON
										&& focusedCP == null
										|| geometricNode.getGraphicalRepresentation().getLayer() > layerOfNearestGeometricObject)) {
									distanceToNearestGeometricObject = distance;
									layerOfNearestGeometricObject = geometricNode.getGraphicalRepresentation().getLayer();
									nearestGeometricObject = geometricNode;
								}
							}
						}
					}

					// Look if we are near a CP
					for (ControlArea<?> cp : geometricNode.getControlAreas()) {
						double cpDistance = cp.getDistanceToArea(viewPoint, getScale());
						if (cpDistance <= selectionDistance
								&& (focusedCP == null || getController().preferredFocusedControlArea(focusedCP, cp) == cp)) {
							distanceToNearestGeometricObject = cpDistance;
							nearestGeometricObject = geometricNode;
							focusedCP = cp;
						}
					}

					if (focusOnFloatingLabel(geometricNode, eventSource, eventLocation)) {
						nearestGeometricObject = geometricNode;
					}

				}

				else {

					DianaView<?, ?> v = drawingView.viewForNode(childNode);
					Rectangle r = childNode.getViewBounds(getScale());

					// We duplicate p as point in node view
					Point pointInNodeView = new Point(p);

					// In case of child node, perform translation caused by borders
					if (node instanceof ShapeNode) {
						pointInNodeView.x -= (int) (((ShapeNode<?>) node).getBorderLeft() * getScale());
						pointInNodeView.y -= (int) (((ShapeNode<?>) node).getBorderTop() * getScale());
					}

					if (r.contains(pointInNodeView)) {
						// The point is located in the view built for object
						// Let's see if the point is located inside shape
						Point p2 = SwingUtilities.convertPoint((Component) view, p, (Component) v);
						if (childNode instanceof ShapeNode) {
							p2.x -= (int) (((ShapeNode<?>) childNode).getBorderLeft() * getScale());
							p2.y -= (int) (((ShapeNode<?>) childNode).getBorderTop() * getScale());
						}

						DianaPoint p3 = childNode.convertViewCoordinatesToNormalizedPoint(p2, getScale());
						if (childNode instanceof ShapeNode) {
							ShapeNode<?> shapeNode = (ShapeNode<?>) childNode;
							if (Double.isNaN(p3.getX()) && shapeNode.getWidth() == 0) {
								p3.x = 0;
							}
							if (Double.isNaN(p3.getY()) && shapeNode.getHeight() == 0) {
								p3.y = 0;
							}

							if (shapeNode.isPointInsideShape(p3)) {
								if (shapeNode.getIsFocusable()) {
									enclosingShapes.add(shapeNode);
								}
								else {
									DrawingTreeNode<?, ?> insideFocusedShape = getFocusedObject(shapeNode, eventSource, eventLocation);
									if (insideFocusedShape != null && insideFocusedShape instanceof ShapeNode) {
										enclosingShapes.add((ShapeNode<?>) insideFocusedShape);
									}
								}
							}
							else { // Look if we are near a CP
								for (ControlArea<?> ca : shapeNode.getControlAreas()) {
									double caDistance = ca.getDistanceToArea(p3, getScale());
									if (caDistance < selectionDistance) {
										// System.out.println("Detected control point");
										if (shapeNode.getIsFocusable()) {
											enclosingShapes.add(shapeNode);
										}
									}
								}
								if (focusOnFloatingLabel(shapeNode, eventSource, eventLocation)) {
									// System.out.println("Detected floating label");
									if (shapeNode.getIsFocusable()) {
										enclosingShapes.add(shapeNode);
									}
								}
								// Look if we are not contained in a child
								// shape outside current shape
								DrawingTreeNode<?, ?> insideFocusedShape = getFocusedObject(shapeNode, eventSource, eventLocation);
								if (insideFocusedShape != null && insideFocusedShape instanceof ShapeNode) {
									enclosingShapes.add((ShapeNode<?>) insideFocusedShape);
								}
							}

						}
						else if (childNode instanceof ConnectorNode) {
							ConnectorNode<?> connectorNode = (ConnectorNode<?>) childNode;
							double distance = connectorNode.distanceToConnector(p3, getScale());
							if (distance < selectionDistance) {
								// The current gr can be selected if either
								// it is closer than the other edge
								// or if its middle symbol is closer (within
								// selection range of course)
								if (distance < distanceToNearestConnector) {
									// If we are clearly nearer than another
									// connector, then this is the one the
									// user has selected
									distanceToNearestConnector = distance;
									nearestConnector = connectorNode;
									for (ControlArea<?> ca : connectorNode.getControlAreas()) {
										double cpDistance = ca.getDistanceToArea(p3, getScale());
										if (cpDistance < selectionDistance && cpDistance < distance) {
											// System.out.println("Detected control point");
											distanceToNearestConnector = cpDistance;
										}
									}
									smallestDistanceToCPOfNearestConnector = updateSmallestDistanceToCPForConnector(connectorNode, p2,
											distance);
								}
								else {
									// We try to find a control area that is
									// closer than the already selected
									// connector.
									for (ControlArea<?> ca : connectorNode.getControlAreas()) {
										double cpDistance = ca.getDistanceToArea(p3, getScale());
										// We have found a control area
										// which is closer than the previous
										// selected connector
										if (cpDistance < selectionDistance && cpDistance < distance) {
											// System.out.println("Detected control point");
											distanceToNearestConnector = cpDistance;
											nearestConnector = connectorNode;
											smallestDistanceToCPOfNearestConnector = updateSmallestDistanceToCPForConnector(connectorNode,
													p2, cpDistance);
										}
									}
									// We can also be closer to the CP than
									// the other one.
									if (connectorNode.getConnector() != null
											&& connectorNode.getConnector().getMiddleSymbolLocation() != null) {
										double cpDistance = connectorNode.convertNormalizedPointToViewCoordinates(
												connectorNode.getConnector().getMiddleSymbolLocation(), getScale()).distance(p2);
										if (cpDistance < selectionDistance && cpDistance < smallestDistanceToCPOfNearestConnector) {
											distanceToNearestConnector = cpDistance;
											smallestDistanceToCPOfNearestConnector = cpDistance;
											nearestConnector = connectorNode;
										}

									}
								}
							}
							else {
								// Look if we are near a CP
								for (ControlArea<?> cp : connectorNode.getControlAreas()) {
									double cpDistance = cp.getDistanceToArea(p3, getScale());
									if (cpDistance <= selectionDistance
											&& (focusedCP == null || getController().preferredFocusedControlArea(focusedCP, cp) == cp)) {
										distanceToNearestGeometricObject = cpDistance;
										nearestConnector = connectorNode;
										focusedCP = cp;
									}
								}
							}
							if (focusOnFloatingLabel(connectorNode, eventSource, eventLocation)) {
								// System.out.println("Detected floating label");
								nearestConnector = connectorNode;
							}

						}
					}
					else {
						// System.out.println("Not in " + childNode);
						Rectangle extendedRectangle = new Rectangle((int) (r.x - selectionDistance), (int) (r.y - selectionDistance),
								(int) (r.width + 2 * selectionDistance), (int) (r.height + 2 * selectionDistance));
						if (extendedRectangle.contains(p)) {
							// We are just outside the shape, may be we
							// focus on a CP ???
							Point p2 = SwingUtilities.convertPoint((Component) view, p, (Component) v);
							DianaPoint p3 = childNode.convertViewCoordinatesToNormalizedPoint(p2, getScale());
							if (childNode instanceof ShapeNode) {
								ShapeNode<?> shapeNode = (ShapeNode<?>) childNode;
								if (shapeNode.getControlAreas() != null) {
									for (ControlArea<?> ca : shapeNode.getControlAreas()) {
										double cpDistance = ca.getDistanceToArea(p3, getScale());
										if (cpDistance < selectionDistance) {
											// System.out.println("Detected control point");
											if (shapeNode.getIsFocusable()) {
												enclosingShapes.add(shapeNode);
											}
										}
									}
								}
							}
							else if (childNode instanceof ConnectorNode) {
								ConnectorNode<?> connectorNode = (ConnectorNode<?>) childNode;
								if (connectorNode.isValid() && connectorNode.getControlAreas() != null) {
									for (ControlArea<?> ca : connectorNode.getControlAreas()) {
										double cpDistance = ca.getDistanceToArea(p3, getScale());
										if (cpDistance < selectionDistance && cpDistance < distanceToNearestConnector) {
											// System.out.println("Detected control point2");
											distanceToNearestConnector = cpDistance;
											nearestConnector = connectorNode;
										}
									}
									if (connectorNode.getConnector() != null
											&& connectorNode.getConnector().getMiddleSymbolLocation() != null) {
										double cpDistance = connectorNode.convertNormalizedPointToViewCoordinates(
												connectorNode.getConnector().getMiddleSymbolLocation(), getScale()).distance(p2);
										if (cpDistance < selectionDistance && cpDistance < smallestDistanceToCPOfNearestConnector) {
											distanceToNearestConnector = cpDistance;
											nearestConnector = connectorNode;
											smallestDistanceToCPOfNearestConnector = cpDistance;
										}
									}
								}
							}
						}
						if (childNode.hasFloatingLabel() && focusOnFloatingLabel(childNode, eventSource, eventLocation)) {
							// System.out.println("Detected floating label");
							if (childNode instanceof ShapeNode) {
								if (childNode.getIsFocusable()) {
									enclosingShapes.add((ShapeNode<?>) childNode);
								}
							}
							else if (childNode instanceof ConnectorNode) {
								nearestConnector = (ConnectorNode<?>) childNode;
							}
						}
					}
				}
			}

		}

		if (nearestGeometricObject != null) {
			return nearestGeometricObject;
		}

		if (enclosingGeometricObjects.size() > 0) {

			Collections.sort(enclosingGeometricObjects, new Comparator<GeometricNode<?>>() {
				@Override
				public int compare(GeometricNode<?> o1, GeometricNode<?> o2) {
					return o2.getGraphicalRepresentation().getLayer() - o1.getGraphicalRepresentation().getLayer();
				}
			});

			return enclosingGeometricObjects.get(0);
		}

		DrawingTreeNode<?, ?> returned = nearestConnector;

		if (enclosingShapes.size() > 0) {

			Collections.sort(enclosingShapes, new Comparator<ShapeNode<?>>() {
				@Override
				public int compare(ShapeNode<?> o1, ShapeNode<?> o2) {
					if (o2.getIsSelected()) {
						return Integer.MAX_VALUE;
					}
					if (o1.getIsSelected()) {
						return Integer.MIN_VALUE;
					}
					return o2.getGraphicalRepresentation().getLayer() - o1.getGraphicalRepresentation().getLayer();
				}
			});

			ShapeNode<?> focusedShape = enclosingShapes.get(0);
			int layer = focusedShape.getGraphicalRepresentation().getLayer();
			if (focusedShape.getIsSelected()) {
				for (ShapeNode<?> s : enclosingShapes) {
					if (s.getIsSelected()) {
						continue;
					}
					else {
						layer = s.getGraphicalRepresentation().getLayer();
						break;
					}
				}
			}
			List<ShapeNode<?>> shapesInSameLayer = new ArrayList<ShapeNode<?>>();
			for (ShapeNode<?> s : enclosingShapes) {
				if (s.getGraphicalRepresentation().getLayer() == layer || s.getIsSelected()) {
					shapesInSameLayer.add(s);
				}
				else {
					break;
				}
			}
			if (shapesInSameLayer.size() > 1) {
				double distance = Double.MAX_VALUE;
				for (ShapeNode<?> gr : shapesInSameLayer) {
					DianaView<?, ?> v = drawingView.viewForNode(gr);
					Point p2 = SwingUtilities.convertPoint((Component) view, p, (Component) v);
					DianaPoint p3 = gr.convertViewCoordinatesToNormalizedPoint(p2, getScale());
					if (Double.isNaN(p3.getX()) && gr.getWidth() == 0) {
						p3.x = 0;
					}
					if (Double.isNaN(p3.getY()) && gr.getHeight() == 0) {
						p3.y = 0;
					}
					for (ControlArea<?> ca : gr.getControlAreas()) {
						double caDistance = ca.getDistanceToArea(p3, getScale());
						if (caDistance < distance) {
							// System.out.println("Detected control point");
							distance = caDistance;
							focusedShape = gr;
						}
					}
				}
			}
			DrawingTreeNode<?, ?> insideFocusedShape = getFocusedObject(focusedShape, eventSource, eventLocation);

			if (insideFocusedShape != null) {
				if (returned == null
						|| returned.getGraphicalRepresentation().getLayer() < insideFocusedShape.getGraphicalRepresentation().getLayer()
						|| insideFocusedShape.getIsSelected()) {
					if (logger.isLoggable(Level.FINE)) {
						logger.fine("Focused GR: " + insideFocusedShape);
					}
					returned = insideFocusedShape;
				}
			}
			else {
				if (returned == null
						|| returned.getGraphicalRepresentation().getLayer() < focusedShape.getGraphicalRepresentation().getLayer()
						|| focusedShape.getIsSelected()) {
					if (logger.isLoggable(Level.FINE)) {
						logger.fine("Focused GR: " + focusedShape);
					}
					returned = focusedShape;
				}
			}

		}

		if (returned == null && node instanceof RootNode) {

			if (node.getControlAreas() != null) {
				for (ControlArea<?> ca : node.getControlAreas()) {
					if (ca != null) {
						DianaPoint p3 = node.convertViewCoordinatesToNormalizedPoint(p, getScale());
						double caDistance = ca.getDistanceToArea(p3, getScale());
						if (caDistance < DianaConstants.SELECTION_DISTANCE) {
							returned = node;
						}
					}
				}
			}

		}

		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Focused GR: " + returned);
		}
		return returned;
	}

	/**
	 * @param gr
	 * @param smallestDistanceToCPOfNearestConnector
	 * @param p2
	 * @param distance
	 * @return
	 */
	private double updateSmallestDistanceToCPForConnector(ConnectorNode<?> connectorNode, Point p2, double distance) {
		if (connectorNode.getConnector() != null && connectorNode.getConnector().getMiddleSymbolLocation() != null) {
			return connectorNode.convertNormalizedPointToViewCoordinates(connectorNode.getConnector().getMiddleSymbolLocation(), getScale())
					.distance(p2);
		}
		else {
			return distance;
		}
	}

	public double getScale() {
		return drawingView.getController().getScale();
	}

}
