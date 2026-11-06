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
import org.openflexo.localization.LocalizedSwing;
import org.openflexo.diana.geomedit.GeomEditApplication;

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
		JMenuItem copyItem = LocalizedSwing.bind(new JMenuItem(GeomEditApplication.GEOMEDIT_LOCALIZATION.localizedForKey("copy"), GeomEditIconLibrary.COPY_ICON), GeomEditApplication.GEOMEDIT_LOCALIZATION, "copy");
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
		JMenuItem pasteItem = LocalizedSwing.bind(new JMenuItem(GeomEditApplication.GEOMEDIT_LOCALIZATION.localizedForKey("paste"), GeomEditIconLibrary.PASTE_ICON), GeomEditApplication.GEOMEDIT_LOCALIZATION, "paste");
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
		JMenuItem cutItem = LocalizedSwing.bind(new JMenuItem(GeomEditApplication.GEOMEDIT_LOCALIZATION.localizedForKey("cut"), GeomEditIconLibrary.CUT_ICON), GeomEditApplication.GEOMEDIT_LOCALIZATION, "cut");
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

		JMenuItem undoItem = LocalizedSwing.bind(new JMenuItem(GeomEditApplication.GEOMEDIT_LOCALIZATION.localizedForKey("undo")), GeomEditApplication.GEOMEDIT_LOCALIZATION, "undo");
		undoItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// undo();
			}
		});
		add(undoItem);
		JMenuItem redoItem = LocalizedSwing.bind(new JMenuItem(GeomEditApplication.GEOMEDIT_LOCALIZATION.localizedForKey("redo")), GeomEditApplication.GEOMEDIT_LOCALIZATION, "redo");
		redoItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// redo();
			}
		});
		add(redoItem);
		// initPalette();
	}

	private JMenu makeCreateNodeMenu() {
		JMenu createNodeItem = LocalizedSwing.bind(new JMenu(GeomEditApplication.GEOMEDIT_LOCALIZATION.localizedForKey("create_node")), GeomEditApplication.GEOMEDIT_LOCALIZATION, "create_node");

		JMenuItem createNodeFromPointsItem = LocalizedSwing.bind(new JMenuItem(GeomEditApplication.GEOMEDIT_LOCALIZATION.localizedForKey("from_points")), GeomEditApplication.GEOMEDIT_LOCALIZATION, "from_points");
		createNodeFromPointsItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.setCurrentEdition(new CreateNodeFromPoints(controller));
			}
		});
		createNodeItem.add(createNodeFromPointsItem);

		JMenuItem createNodeFromCenterAndSizeItem = LocalizedSwing.bind(new JMenuItem(GeomEditApplication.GEOMEDIT_LOCALIZATION.localizedForKey("from_center_and_size")), GeomEditApplication.GEOMEDIT_LOCALIZATION, "from_center_and_size");
		createNodeFromCenterAndSizeItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.setCurrentEdition(new CreateNodeFromCenterAndSize(controller));
			}
		});
		createNodeItem.add(createNodeFromCenterAndSizeItem);

		JMenuItem createNodeAtRelativeLocationItem = LocalizedSwing.bind(new JMenuItem(GeomEditApplication.GEOMEDIT_LOCALIZATION.localizedForKey("at_relative_location")), GeomEditApplication.GEOMEDIT_LOCALIZATION, "at_relative_location");
		createNodeAtRelativeLocationItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.setCurrentEdition(new CreateNodeAtRelativeLocation(controller));
			}
		});
		createNodeItem.add(createNodeAtRelativeLocationItem);

		createNodeItem.addSeparator();

		JMenuItem createNodeConnectorItem = LocalizedSwing.bind(new JMenuItem(GeomEditApplication.GEOMEDIT_LOCALIZATION.localizedForKey("connector_between_nodes")), GeomEditApplication.GEOMEDIT_LOCALIZATION, "connector_between_nodes");
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
		JMenu createPointItem = LocalizedSwing.bind(new JMenu(GeomEditApplication.GEOMEDIT_LOCALIZATION.localizedForKey("create_point")), GeomEditApplication.GEOMEDIT_LOCALIZATION, "create_point");

		JMenuItem createExplicitPoint = LocalizedSwing.bind(new JMenuItem(GeomEditApplication.GEOMEDIT_LOCALIZATION.localizedForKey("as_explicit_position")), GeomEditApplication.GEOMEDIT_LOCALIZATION, "as_explicit_position");
		createExplicitPoint.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.setCurrentEdition(new CreatePoint(controller));
			}
		});
		createPointItem.add(createExplicitPoint);

		JMenuItem createPointAsMiddleFromPointsItem = LocalizedSwing.bind(new JMenuItem(GeomEditApplication.GEOMEDIT_LOCALIZATION.localizedForKey("middle_of_two_other_points")), GeomEditApplication.GEOMEDIT_LOCALIZATION, "middle_of_two_other_points");
		createPointAsMiddleFromPointsItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.setCurrentEdition(new CreatePointMiddleOfPoints(controller));
			}
		});
		createPointItem.add(createPointAsMiddleFromPointsItem);

		JMenuItem createPointSymetricOfPointItem = LocalizedSwing.bind(new JMenuItem(GeomEditApplication.GEOMEDIT_LOCALIZATION.localizedForKey("symmetric_relatively_to_another_point")), GeomEditApplication.GEOMEDIT_LOCALIZATION, "symmetric_relatively_to_another_point");
		createPointSymetricOfPointItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.setCurrentEdition(new CreatePointSymetricOfPoint(controller));
			}
		});
		createPointItem.add(createPointSymetricOfPointItem);

		JMenuItem createPointSymetricOfLineItem = LocalizedSwing.bind(new JMenuItem(GeomEditApplication.GEOMEDIT_LOCALIZATION.localizedForKey("symmetric_relatively_to_a_line")), GeomEditApplication.GEOMEDIT_LOCALIZATION, "symmetric_relatively_to_a_line");
		createPointSymetricOfLineItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.setCurrentEdition(new CreatePointSymetricOfLine(controller));
			}
		});
		createPointItem.add(createPointSymetricOfLineItem);

		JMenuItem createNearestPointFromObjectItem = LocalizedSwing.bind(new JMenuItem(GeomEditApplication.GEOMEDIT_LOCALIZATION.localizedForKey("nearest_from_object")), GeomEditApplication.GEOMEDIT_LOCALIZATION, "nearest_from_object");
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
		JMenu createLineItem = LocalizedSwing.bind(new JMenu(GeomEditApplication.GEOMEDIT_LOCALIZATION.localizedForKey("create_line")), GeomEditApplication.GEOMEDIT_LOCALIZATION, "create_line");

		JMenuItem createLineFromPointsItem = LocalizedSwing.bind(new JMenuItem(GeomEditApplication.GEOMEDIT_LOCALIZATION.localizedForKey("from_points")), GeomEditApplication.GEOMEDIT_LOCALIZATION, "from_points");
		createLineFromPointsItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.setCurrentEdition(new CreateLineFromPoints(controller));
			}
		});
		createLineItem.add(createLineFromPointsItem);

		JMenuItem createHorizontalLineWithPointItem = LocalizedSwing.bind(new JMenuItem(GeomEditApplication.GEOMEDIT_LOCALIZATION.localizedForKey("horizontal_crossing_point")), GeomEditApplication.GEOMEDIT_LOCALIZATION, "horizontal_crossing_point");
		createHorizontalLineWithPointItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.setCurrentEdition(new CreateHorizontalLineWithPoint(controller));
			}
		});
		createLineItem.add(createHorizontalLineWithPointItem);

		JMenuItem createVerticalLineWithPointItem = LocalizedSwing.bind(new JMenuItem(GeomEditApplication.GEOMEDIT_LOCALIZATION.localizedForKey("vertical_crossing_point")), GeomEditApplication.GEOMEDIT_LOCALIZATION, "vertical_crossing_point");
		createVerticalLineWithPointItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.setCurrentEdition(new CreateVerticalLineWithPoint(controller));
			}
		});
		createLineItem.add(createVerticalLineWithPointItem);

		JMenuItem createParallelLineWithPointItem = LocalizedSwing.bind(new JMenuItem(GeomEditApplication.GEOMEDIT_LOCALIZATION.localizedForKey("parallel_crossing_point")), GeomEditApplication.GEOMEDIT_LOCALIZATION, "parallel_crossing_point");
		createParallelLineWithPointItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.setCurrentEdition(new CreateParallelLineWithPoint(controller));
			}
		});
		createLineItem.add(createParallelLineWithPointItem);

		JMenuItem createOrthogonalLineWithPointItem = LocalizedSwing.bind(new JMenuItem(GeomEditApplication.GEOMEDIT_LOCALIZATION.localizedForKey("orthogonal_crossing_point")), GeomEditApplication.GEOMEDIT_LOCALIZATION, "orthogonal_crossing_point");
		createOrthogonalLineWithPointItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.setCurrentEdition(new CreateOrthogonalLineWithPoint(controller));
			}
		});
		createLineItem.add(createOrthogonalLineWithPointItem);

		JMenuItem createRotatedLineWithPointItem = LocalizedSwing.bind(new JMenuItem(GeomEditApplication.GEOMEDIT_LOCALIZATION.localizedForKey("rotated_line_crossing_point")), GeomEditApplication.GEOMEDIT_LOCALIZATION, "rotated_line_crossing_point");
		createRotatedLineWithPointItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.setCurrentEdition(new CreateRotatedLineWithPoint(controller));
			}
		});
		createLineItem.add(createRotatedLineWithPointItem);

		JMenuItem createTangentLineWithCircleAndPointItem = LocalizedSwing.bind(new JMenuItem(GeomEditApplication.GEOMEDIT_LOCALIZATION.localizedForKey("tangent_to_a_circle_and_crossing_point")), GeomEditApplication.GEOMEDIT_LOCALIZATION, "tangent_to_a_circle_and_crossing_point");
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
		JMenu createHalfLineItem = LocalizedSwing.bind(new JMenu(GeomEditApplication.GEOMEDIT_LOCALIZATION.localizedForKey("create_half-line")), GeomEditApplication.GEOMEDIT_LOCALIZATION, "create_half-line");

		JMenuItem createHalfLineFromPointsItem = LocalizedSwing.bind(new JMenuItem(GeomEditApplication.GEOMEDIT_LOCALIZATION.localizedForKey("from_points")), GeomEditApplication.GEOMEDIT_LOCALIZATION, "from_points");
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
		JMenu createLineItem = LocalizedSwing.bind(new JMenu(GeomEditApplication.GEOMEDIT_LOCALIZATION.localizedForKey("create_segment")), GeomEditApplication.GEOMEDIT_LOCALIZATION, "create_segment");

		JMenuItem createSegmentFromPointsItem = LocalizedSwing.bind(new JMenuItem(GeomEditApplication.GEOMEDIT_LOCALIZATION.localizedForKey("from_points")), GeomEditApplication.GEOMEDIT_LOCALIZATION, "from_points");
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
		JMenu createPolylineItem = LocalizedSwing.bind(new JMenu(GeomEditApplication.GEOMEDIT_LOCALIZATION.localizedForKey("create_polyline")), GeomEditApplication.GEOMEDIT_LOCALIZATION, "create_polyline");

		JMenuItem createPolylinWithNPointsItem = LocalizedSwing.bind(new JMenuItem(GeomEditApplication.GEOMEDIT_LOCALIZATION.localizedForKey("with_n_points")), GeomEditApplication.GEOMEDIT_LOCALIZATION, "with_n_points");
		createPolylinWithNPointsItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.setCurrentEdition(new CreatePolylinWithNPoints(controller));
			}
		});
		createPolylineItem.add(createPolylinWithNPointsItem);

		JMenuItem createRectPolylinWithStartAndEndAreaItem = LocalizedSwing.bind(new JMenuItem(GeomEditApplication.GEOMEDIT_LOCALIZATION.localizedForKey("rect_polyline_with_start_and_end_area")), GeomEditApplication.GEOMEDIT_LOCALIZATION, "rect_polyline_with_start_and_end_area");
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
		JMenu createRectangleItem = LocalizedSwing.bind(new JMenu(GeomEditApplication.GEOMEDIT_LOCALIZATION.localizedForKey("create_rectangle")), GeomEditApplication.GEOMEDIT_LOCALIZATION, "create_rectangle");

		JMenuItem createRectangleFromPointsItem = LocalizedSwing.bind(new JMenuItem(GeomEditApplication.GEOMEDIT_LOCALIZATION.localizedForKey("from_points")), GeomEditApplication.GEOMEDIT_LOCALIZATION, "from_points");
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
		JMenu createRoundRectangleItem = LocalizedSwing.bind(new JMenu(GeomEditApplication.GEOMEDIT_LOCALIZATION.localizedForKey("create_round_rectangle")), GeomEditApplication.GEOMEDIT_LOCALIZATION, "create_round_rectangle");

		JMenuItem createRoundRectangleFromPointsItem = LocalizedSwing.bind(new JMenuItem(GeomEditApplication.GEOMEDIT_LOCALIZATION.localizedForKey("from_points")), GeomEditApplication.GEOMEDIT_LOCALIZATION, "from_points");
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
		JMenu createPolygonItem = LocalizedSwing.bind(new JMenu(GeomEditApplication.GEOMEDIT_LOCALIZATION.localizedForKey("create_polygon")), GeomEditApplication.GEOMEDIT_LOCALIZATION, "create_polygon");

		JMenuItem CreatePolygonWithNPointsItem = LocalizedSwing.bind(new JMenuItem(GeomEditApplication.GEOMEDIT_LOCALIZATION.localizedForKey("with_n_points")), GeomEditApplication.GEOMEDIT_LOCALIZATION, "with_n_points");
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
		JMenu createCircleItem = LocalizedSwing.bind(new JMenu(GeomEditApplication.GEOMEDIT_LOCALIZATION.localizedForKey("create_circle")), GeomEditApplication.GEOMEDIT_LOCALIZATION, "create_circle");

		JMenuItem createCircleWithCenterAndPointItem = LocalizedSwing.bind(new JMenuItem(GeomEditApplication.GEOMEDIT_LOCALIZATION.localizedForKey("with_center_and_point")), GeomEditApplication.GEOMEDIT_LOCALIZATION, "with_center_and_point");
		createCircleWithCenterAndPointItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.setCurrentEdition(new CreateCircleWithCenterAndPoint(controller));
			}
		});
		createCircleItem.add(createCircleWithCenterAndPointItem);

		JMenuItem createCircleWithThreePointsItem = LocalizedSwing.bind(new JMenuItem(GeomEditApplication.GEOMEDIT_LOCALIZATION.localizedForKey("with_three_points")), GeomEditApplication.GEOMEDIT_LOCALIZATION, "with_three_points");
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
		JMenu createBandItem = LocalizedSwing.bind(new JMenu(GeomEditApplication.GEOMEDIT_LOCALIZATION.localizedForKey("create_half-plane")), GeomEditApplication.GEOMEDIT_LOCALIZATION, "create_half-plane");

		JMenuItem createBandWithTwoLinesItem = LocalizedSwing.bind(new JMenuItem(GeomEditApplication.GEOMEDIT_LOCALIZATION.localizedForKey("with_line_and_point")), GeomEditApplication.GEOMEDIT_LOCALIZATION, "with_line_and_point");
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
		JMenu createBandItem = LocalizedSwing.bind(new JMenu(GeomEditApplication.GEOMEDIT_LOCALIZATION.localizedForKey("create_band")), GeomEditApplication.GEOMEDIT_LOCALIZATION, "create_band");

		JMenuItem createBandWithTwoLinesItem = LocalizedSwing.bind(new JMenuItem(GeomEditApplication.GEOMEDIT_LOCALIZATION.localizedForKey("with_two_lines")), GeomEditApplication.GEOMEDIT_LOCALIZATION, "with_two_lines");
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
		JMenu createHalfBandItem = LocalizedSwing.bind(new JMenu(GeomEditApplication.GEOMEDIT_LOCALIZATION.localizedForKey("create_half-band")), GeomEditApplication.GEOMEDIT_LOCALIZATION, "create_half-band");

		JMenuItem createHalfBandWithTwoLinesItem = LocalizedSwing.bind(new JMenuItem(GeomEditApplication.GEOMEDIT_LOCALIZATION.localizedForKey("with_lines")), GeomEditApplication.GEOMEDIT_LOCALIZATION, "with_lines");
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
		JMenu createCurveItem = LocalizedSwing.bind(new JMenu(GeomEditApplication.GEOMEDIT_LOCALIZATION.localizedForKey("create_curve")), GeomEditApplication.GEOMEDIT_LOCALIZATION, "create_curve");

		JMenuItem createQuadCurveFromThreePoints = LocalizedSwing.bind(new JMenuItem(GeomEditApplication.GEOMEDIT_LOCALIZATION.localizedForKey("quad-curve_from_three_points")), GeomEditApplication.GEOMEDIT_LOCALIZATION, "quad-curve_from_three_points");
		createQuadCurveFromThreePoints.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.setCurrentEdition(new CreateQuadCurveFromThreePoints(controller));
			}
		});
		createCurveItem.add(createQuadCurveFromThreePoints);

		JMenuItem createCubicCurveFromFourPoints = LocalizedSwing.bind(new JMenuItem(GeomEditApplication.GEOMEDIT_LOCALIZATION.localizedForKey("cubic-curve_from_four_points")), GeomEditApplication.GEOMEDIT_LOCALIZATION, "cubic-curve_from_four_points");
		createCubicCurveFromFourPoints.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.setCurrentEdition(new CreateCubicCurveFromFourPoints(controller));
			}
		});
		createCurveItem.add(createCubicCurveFromFourPoints);

		JMenuItem createComplexCurveWithNPointsItem = LocalizedSwing.bind(new JMenuItem(GeomEditApplication.GEOMEDIT_LOCALIZATION.localizedForKey("with_n_points")), GeomEditApplication.GEOMEDIT_LOCALIZATION, "with_n_points");
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
		JMenu createOperationItem = LocalizedSwing.bind(new JMenu(GeomEditApplication.GEOMEDIT_LOCALIZATION.localizedForKey("create_operation")), GeomEditApplication.GEOMEDIT_LOCALIZATION, "create_operation");

		JMenuItem createIntersectionItem = LocalizedSwing.bind(new JMenuItem(GeomEditApplication.GEOMEDIT_LOCALIZATION.localizedForKey("intersection")), GeomEditApplication.GEOMEDIT_LOCALIZATION, "intersection");
		createIntersectionItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.setCurrentEdition(new CreateIntersection(controller));
			}
		});
		createOperationItem.add(createIntersectionItem);

		JMenuItem createUnionItem = LocalizedSwing.bind(new JMenuItem(GeomEditApplication.GEOMEDIT_LOCALIZATION.localizedForKey("union")), GeomEditApplication.GEOMEDIT_LOCALIZATION, "union");
		createUnionItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.setCurrentEdition(new CreateUnion(controller));
			}
		});
		createOperationItem.add(createUnionItem);

		JMenuItem createSoustractionItem = LocalizedSwing.bind(new JMenuItem(GeomEditApplication.GEOMEDIT_LOCALIZATION.localizedForKey("subtraction")), GeomEditApplication.GEOMEDIT_LOCALIZATION, "subtraction");
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
