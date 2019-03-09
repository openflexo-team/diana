/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * Copyright (c) 2011-2012, AgileBirds
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

package org.openflexo.diana.geomedit.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.openflexo.diana.geomedit.GeomEditDrawingController;
import org.openflexo.diana.geomedit.edition.CreateBandFromLines;
import org.openflexo.diana.geomedit.edition.CreateCircleWithCenterAndPoint;
import org.openflexo.diana.geomedit.edition.CreateCircleWithThreePoints;
import org.openflexo.diana.geomedit.edition.CreateConnectorNode;
import org.openflexo.diana.geomedit.edition.CreateCubicCurveFromFourPoints;
import org.openflexo.diana.geomedit.edition.CreateCurveWithNPoints;
import org.openflexo.diana.geomedit.edition.CreateHalfBandWithLines;
import org.openflexo.diana.geomedit.edition.CreateHalfLineFromPoints;
import org.openflexo.diana.geomedit.edition.CreateHalfPlaneWithLineAndPoint;
import org.openflexo.diana.geomedit.edition.CreateHorizontalLineWithPoint;
import org.openflexo.diana.geomedit.edition.CreateIntersection;
import org.openflexo.diana.geomedit.edition.CreateLineFromPoints;
import org.openflexo.diana.geomedit.edition.CreateNearestPointFromObject;
import org.openflexo.diana.geomedit.edition.CreateNodeAtRelativeLocation;
import org.openflexo.diana.geomedit.edition.CreateNodeFromCenterAndSize;
import org.openflexo.diana.geomedit.edition.CreateNodeFromPoints;
import org.openflexo.diana.geomedit.edition.CreateOrthogonalLineWithPoint;
import org.openflexo.diana.geomedit.edition.CreateParallelLineWithPoint;
import org.openflexo.diana.geomedit.edition.CreatePoint;
import org.openflexo.diana.geomedit.edition.CreatePointMiddleOfPoints;
import org.openflexo.diana.geomedit.edition.CreatePointSymetricOfLine;
import org.openflexo.diana.geomedit.edition.CreatePointSymetricOfPoint;
import org.openflexo.diana.geomedit.edition.CreatePolygonWithNPoints;
import org.openflexo.diana.geomedit.edition.CreatePolylinWithNPoints;
import org.openflexo.diana.geomedit.edition.CreateQuadCurveFromThreePoints;
import org.openflexo.diana.geomedit.edition.CreateRectPolylinWithStartAndEndArea;
import org.openflexo.diana.geomedit.edition.CreateRectangleFromPoints;
import org.openflexo.diana.geomedit.edition.CreateRotatedLineWithPoint;
import org.openflexo.diana.geomedit.edition.CreateRoundRectangleFromPoints;
import org.openflexo.diana.geomedit.edition.CreateSegmentFromPoints;
import org.openflexo.diana.geomedit.edition.CreateSubstraction;
import org.openflexo.diana.geomedit.edition.CreateTangentLineWithCircleAndPoint;
import org.openflexo.diana.geomedit.edition.CreateUnion;
import org.openflexo.diana.geomedit.edition.CreateVerticalLineWithPoint;
import org.openflexo.diana.geomedit.model.GeometricDiagram;
import org.openflexo.diana.geomedit.view.GeomEditIconLibrary;
import org.openflexo.exceptions.CopyException;
import org.openflexo.exceptions.CutException;
import org.openflexo.exceptions.PasteException;

/**
 * General contextual menu (applies to {@link GeometricDiagram}
 * 
 * @author sylvain
 *
 */
public class GeneralContextualMenu extends JPopupMenu {

	private static final Logger logger = Logger.getLogger(GeneralContextualMenu.class.getPackage().getName());

	private GeomEditDrawingController controller;

	public GeneralContextualMenu(GeomEditDrawingController controller) {
		super();
		this.controller = controller;
		add(makeCreateNodeMenu());
		add(makeCreatePointMenu());
		add(makeCreateLineMenu());
		add(makeCreateHalfLineMenu());
		add(makeCreateSegmentMenu());
		add(makePolylineMenu());
		add(makeCreateRectangleMenu());
		add(makeCreateRoundRectangleMenu());
		add(makeCreatePolygonMenu());
		add(makeCreateCircleMenu());
		add(makeCreateHalfPlaneMenu());
		add(makeCreateBandMenu());
		add(makeCreateHalfBandMenu());
		add(makeCreateCurveMenu());
		add(makeCreateOperationMenu());

		addSeparator();
		JMenuItem copyItem = new JMenuItem("Copy", GeomEditIconLibrary.COPY_ICON);
		copyItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					controller.copy();
				} catch (CopyException e1) {
					e1.printStackTrace();
				}
			}
		});
		add(copyItem);
		JMenuItem pasteItem = new JMenuItem("Paste", GeomEditIconLibrary.PASTE_ICON);
		pasteItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					controller.paste();
				} catch (PasteException e1) {
					e1.printStackTrace();
				}
			}
		});
		add(pasteItem);
		JMenuItem cutItem = new JMenuItem("Cut", GeomEditIconLibrary.CUT_ICON);
		cutItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					controller.cut();
				} catch (CutException e1) {
					e1.printStackTrace();
				}
			}
		});
		add(cutItem);

		addSeparator();

		JMenuItem undoItem = new JMenuItem("Undo");
		undoItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// undo();
			}
		});
		add(undoItem);
		JMenuItem redoItem = new JMenuItem("Redo");
		redoItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// redo();
			}
		});
		add(redoItem);

		addSeparator();

		JMenuItem tikzEditorItem = new JMenuItem("Open Tikz Editor");
		tikzEditorItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.getEditor().getTikzConnector().getTikzEditorFrame().setVisible(true);
			}
		});
		add(tikzEditorItem);
		
		// initPalette();
	}

	private JMenu makeCreateNodeMenu() {
		JMenu createNodeItem = new JMenu("Create node");

		JMenuItem createNodeFromPointsItem = new JMenuItem("From points");
		createNodeFromPointsItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.setCurrentEdition(new CreateNodeFromPoints(controller));
			}
		});
		createNodeItem.add(createNodeFromPointsItem);

		JMenuItem createNodeFromCenterAndSizeItem = new JMenuItem("From center and size");
		createNodeFromCenterAndSizeItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.setCurrentEdition(new CreateNodeFromCenterAndSize(controller));
			}
		});
		createNodeItem.add(createNodeFromCenterAndSizeItem);

		JMenuItem createNodeAtRelativeLocationItem = new JMenuItem("At relative location");
		createNodeAtRelativeLocationItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.setCurrentEdition(new CreateNodeAtRelativeLocation(controller));
			}
		});
		createNodeItem.add(createNodeAtRelativeLocationItem);

		createNodeItem.addSeparator();

		JMenuItem createNodeConnectorItem = new JMenuItem("Connector between nodes");
		createNodeConnectorItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.setCurrentEdition(new CreateConnectorNode(controller));
			}
		});
		createNodeItem.add(createNodeConnectorItem);

		return createNodeItem;
	}

	private JMenu makeCreatePointMenu() {
		JMenu createPointItem = new JMenu("Create point");

		JMenuItem createExplicitPoint = new JMenuItem("As explicit position");
		createExplicitPoint.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.setCurrentEdition(new CreatePoint(controller));
			}
		});
		createPointItem.add(createExplicitPoint);

		JMenuItem createPointAsMiddleFromPointsItem = new JMenuItem("Middle of two other points");
		createPointAsMiddleFromPointsItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.setCurrentEdition(new CreatePointMiddleOfPoints(controller));
			}
		});
		createPointItem.add(createPointAsMiddleFromPointsItem);

		JMenuItem createPointSymetricOfPointItem = new JMenuItem("Symetric relatively to an other point");
		createPointSymetricOfPointItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.setCurrentEdition(new CreatePointSymetricOfPoint(controller));
			}
		});
		createPointItem.add(createPointSymetricOfPointItem);

		JMenuItem createPointSymetricOfLineItem = new JMenuItem("Symetric relatively to a line");
		createPointSymetricOfLineItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.setCurrentEdition(new CreatePointSymetricOfLine(controller));
			}
		});
		createPointItem.add(createPointSymetricOfLineItem);

		JMenuItem createNearestPointFromObjectItem = new JMenuItem("Nearest from object");
		createNearestPointFromObjectItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.setCurrentEdition(new CreateNearestPointFromObject(controller));
			}
		});
		createPointItem.add(createNearestPointFromObjectItem);

		return createPointItem;
	}

	private JMenu makeCreateLineMenu() {
		JMenu createLineItem = new JMenu("Create line");

		JMenuItem createLineFromPointsItem = new JMenuItem("From points");
		createLineFromPointsItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.setCurrentEdition(new CreateLineFromPoints(controller));
			}
		});
		createLineItem.add(createLineFromPointsItem);

		JMenuItem createHorizontalLineWithPointItem = new JMenuItem("Horizontal crossing point");
		createHorizontalLineWithPointItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.setCurrentEdition(new CreateHorizontalLineWithPoint(controller));
			}
		});
		createLineItem.add(createHorizontalLineWithPointItem);

		JMenuItem createVerticalLineWithPointItem = new JMenuItem("Vertical crossing point");
		createVerticalLineWithPointItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.setCurrentEdition(new CreateVerticalLineWithPoint(controller));
			}
		});
		createLineItem.add(createVerticalLineWithPointItem);

		JMenuItem createParallelLineWithPointItem = new JMenuItem("Parallel crossing point");
		createParallelLineWithPointItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.setCurrentEdition(new CreateParallelLineWithPoint(controller));
			}
		});
		createLineItem.add(createParallelLineWithPointItem);

		JMenuItem createOrthogonalLineWithPointItem = new JMenuItem("Orthogonal crossing point");
		createOrthogonalLineWithPointItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.setCurrentEdition(new CreateOrthogonalLineWithPoint(controller));
			}
		});
		createLineItem.add(createOrthogonalLineWithPointItem);

		JMenuItem createRotatedLineWithPointItem = new JMenuItem("Rotated line crossing point");
		createRotatedLineWithPointItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.setCurrentEdition(new CreateRotatedLineWithPoint(controller));
			}
		});
		createLineItem.add(createRotatedLineWithPointItem);

		JMenuItem createTangentLineWithCircleAndPointItem = new JMenuItem("Tangent to a circle and crossing point");
		createTangentLineWithCircleAndPointItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.setCurrentEdition(new CreateTangentLineWithCircleAndPoint(controller));
			}
		});
		createLineItem.add(createTangentLineWithCircleAndPointItem);

		return createLineItem;
	}

	private JMenu makeCreateHalfLineMenu() {
		JMenu createHalfLineItem = new JMenu("Create half-line");

		JMenuItem createHalfLineFromPointsItem = new JMenuItem("From points");
		createHalfLineFromPointsItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.setCurrentEdition(new CreateHalfLineFromPoints(controller));
			}
		});
		createHalfLineItem.add(createHalfLineFromPointsItem);

		return createHalfLineItem;
	}

	private JMenu makeCreateSegmentMenu() {
		JMenu createLineItem = new JMenu("Create segment");

		JMenuItem createSegmentFromPointsItem = new JMenuItem("From points");
		createSegmentFromPointsItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.setCurrentEdition(new CreateSegmentFromPoints(controller));
			}
		});
		createLineItem.add(createSegmentFromPointsItem);

		return createLineItem;
	}

	private JMenu makePolylineMenu() {
		JMenu createPolylineItem = new JMenu("Create polylin");

		JMenuItem createPolylinWithNPointsItem = new JMenuItem("With n points");
		createPolylinWithNPointsItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.setCurrentEdition(new CreatePolylinWithNPoints(controller));
			}
		});
		createPolylineItem.add(createPolylinWithNPointsItem);

		JMenuItem createRectPolylinWithStartAndEndAreaItem = new JMenuItem("Rect polylin with start and end area");
		createRectPolylinWithStartAndEndAreaItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.setCurrentEdition(new CreateRectPolylinWithStartAndEndArea(controller));
			}
		});
		createPolylineItem.add(createRectPolylinWithStartAndEndAreaItem);

		return createPolylineItem;
	}

	private JMenu makeCreateRectangleMenu() {
		JMenu createRectangleItem = new JMenu("Create rectangle");

		JMenuItem createRectangleFromPointsItem = new JMenuItem("From points");
		createRectangleFromPointsItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.setCurrentEdition(new CreateRectangleFromPoints(controller));
			}
		});
		createRectangleItem.add(createRectangleFromPointsItem);

		return createRectangleItem;
	}

	private JMenu makeCreateRoundRectangleMenu() {
		JMenu createRoundRectangleItem = new JMenu("Create round rectangle");

		JMenuItem createRoundRectangleFromPointsItem = new JMenuItem("From points");
		createRoundRectangleFromPointsItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.setCurrentEdition(new CreateRoundRectangleFromPoints(controller));
			}
		});
		createRoundRectangleItem.add(createRoundRectangleFromPointsItem);

		return createRoundRectangleItem;
	}

	private JMenu makeCreatePolygonMenu() {
		JMenu createPolygonItem = new JMenu("Create polygon");

		JMenuItem CreatePolygonWithNPointsItem = new JMenuItem("With n points");
		CreatePolygonWithNPointsItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.setCurrentEdition(new CreatePolygonWithNPoints(controller));
			}
		});
		createPolygonItem.add(CreatePolygonWithNPointsItem);

		return createPolygonItem;
	}

	private JMenu makeCreateCircleMenu() {
		JMenu createCircleItem = new JMenu("Create circle");

		JMenuItem createCircleWithCenterAndPointItem = new JMenuItem("With center and point");
		createCircleWithCenterAndPointItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.setCurrentEdition(new CreateCircleWithCenterAndPoint(controller));
			}
		});
		createCircleItem.add(createCircleWithCenterAndPointItem);

		JMenuItem createCircleWithThreePointsItem = new JMenuItem("With three points");
		createCircleWithThreePointsItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.setCurrentEdition(new CreateCircleWithThreePoints(controller));
			}
		});
		createCircleItem.add(createCircleWithThreePointsItem);

		return createCircleItem;
	}

	private JMenu makeCreateHalfPlaneMenu() {
		JMenu createBandItem = new JMenu("Create half-plane");

		JMenuItem createBandWithTwoLinesItem = new JMenuItem("With line and point");
		createBandWithTwoLinesItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.setCurrentEdition(new CreateHalfPlaneWithLineAndPoint(controller));
			}
		});
		createBandItem.add(createBandWithTwoLinesItem);

		return createBandItem;
	}

	private JMenu makeCreateBandMenu() {
		JMenu createBandItem = new JMenu("Create band");

		JMenuItem createBandWithTwoLinesItem = new JMenuItem("With two lines");
		createBandWithTwoLinesItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.setCurrentEdition(new CreateBandFromLines(controller));
			}
		});
		createBandItem.add(createBandWithTwoLinesItem);

		return createBandItem;
	}

	private JMenu makeCreateHalfBandMenu() {
		JMenu createHalfBandItem = new JMenu("Create half-band");

		JMenuItem createHalfBandWithTwoLinesItem = new JMenuItem("With lines");
		createHalfBandWithTwoLinesItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.setCurrentEdition(new CreateHalfBandWithLines(controller));
			}
		});
		createHalfBandItem.add(createHalfBandWithTwoLinesItem);

		return createHalfBandItem;
	}

	private JMenu makeCreateCurveMenu() {
		JMenu createCurveItem = new JMenu("Create curve");

		JMenuItem createQuadCurveFromThreePoints = new JMenuItem("Quad-curve from three points");
		createQuadCurveFromThreePoints.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.setCurrentEdition(new CreateQuadCurveFromThreePoints(controller));
			}
		});
		createCurveItem.add(createQuadCurveFromThreePoints);

		JMenuItem createCubicCurveFromFourPoints = new JMenuItem("Cubic-curve from four points");
		createCubicCurveFromFourPoints.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.setCurrentEdition(new CreateCubicCurveFromFourPoints(controller));
			}
		});
		createCurveItem.add(createCubicCurveFromFourPoints);

		JMenuItem createComplexCurveWithNPointsItem = new JMenuItem("With n points");
		createComplexCurveWithNPointsItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.setCurrentEdition(new CreateCurveWithNPoints(controller));
			}
		});
		createCurveItem.add(createComplexCurveWithNPointsItem);

		return createCurveItem;
	}

	private JMenu makeCreateOperationMenu() {
		JMenu createOperationItem = new JMenu("Create operation");

		JMenuItem createIntersectionItem = new JMenuItem("Intersection");
		createIntersectionItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.setCurrentEdition(new CreateIntersection(controller));
			}
		});
		createOperationItem.add(createIntersectionItem);

		JMenuItem createUnionItem = new JMenuItem("Union");
		createUnionItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.setCurrentEdition(new CreateUnion(controller));
			}
		});
		createOperationItem.add(createUnionItem);

		JMenuItem createSoustractionItem = new JMenuItem("Soustraction");
		createSoustractionItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.setCurrentEdition(new CreateSubstraction(controller));
			}
		});
		createOperationItem.add(createSoustractionItem);

		return createOperationItem;
	}

}
