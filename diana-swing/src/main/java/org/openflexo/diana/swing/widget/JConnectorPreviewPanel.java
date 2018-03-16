/**
 * 
 * Copyright (c) 2014, Openflexo
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

package org.openflexo.diana.swing.widget;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JPanel;

import org.openflexo.diana.BackgroundStyle;
import org.openflexo.diana.ConnectorGraphicalRepresentation;
import org.openflexo.diana.Drawing;
import org.openflexo.diana.DrawingGraphicalRepresentation;
import org.openflexo.diana.FGEConstants;
import org.openflexo.diana.FGECoreUtils;
import org.openflexo.diana.FGEModelFactory;
import org.openflexo.diana.ForegroundStyle;
import org.openflexo.diana.GRStructureVisitor;
import org.openflexo.diana.ShadowStyle;
import org.openflexo.diana.ShapeGraphicalRepresentation;
import org.openflexo.diana.Drawing.PersistenceMode;
import org.openflexo.diana.GRBinding.ConnectorGRBinding;
import org.openflexo.diana.GRBinding.DrawingGRBinding;
import org.openflexo.diana.GRBinding.ShapeGRBinding;
import org.openflexo.diana.GRProvider.ConnectorGRProvider;
import org.openflexo.diana.GRProvider.DrawingGRProvider;
import org.openflexo.diana.GRProvider.ShapeGRProvider;
import org.openflexo.diana.connectors.ConnectorSpecification;
import org.openflexo.diana.connectors.ConnectorSpecification.ConnectorType;
import org.openflexo.diana.impl.DrawingImpl;
import org.openflexo.diana.shapes.ShapeSpecification.ShapeType;
import org.openflexo.diana.swing.JDianaInteractiveViewer;
import org.openflexo.diana.swing.control.SwingToolFactory;
import org.openflexo.diana.view.widget.ConnectorPreviewPanel;
import org.openflexo.gina.controller.FIBController;
import org.openflexo.gina.model.widget.FIBCustom;
import org.openflexo.swing.CustomPopup.ApplyCancelListener;

@SuppressWarnings("serial")
public class JConnectorPreviewPanel extends JPanel implements ConnectorPreviewPanel {

	private Drawing<JConnectorPreviewPanel> drawing;
	private DrawingGraphicalRepresentation drawingGR;
	private JDianaInteractiveViewer<JConnectorPreviewPanel> controller;

	private ShapeGraphicalRepresentation startShapeGR;
	private ShapeGraphicalRepresentation endShapeGR;
	private ConnectorGraphicalRepresentation connectorGR;

	// FD : unused
	// private final int border = 10;
	private int width = 250;
	private int height = 80;
	private static final float RATIO = 0.6f;

	private FGEModelFactory factory;

	private ForegroundStyle foregroundStyle;

	private ForegroundStyle shapeForegroundStyle;
	private BackgroundStyle shapeBackgroundStyle;
	private ShadowStyle shapeShadowStyle;

	public JConnectorPreviewPanel(ConnectorSpecification aConnectorSpecification) {
		super(new BorderLayout());

		factory = FGECoreUtils.TOOLS_FACTORY;

		// representedDrawing = new RepresentedDrawing();
		// representedShape = new RepresentedShape();
		setPreferredSize(new Dimension(getPanelWidth(), getPanelHeight()));

		foregroundStyle = factory.makeForegroundStyle(Color.BLACK);
		shapeForegroundStyle = factory.makeForegroundStyle(Color.GRAY);
		shapeBackgroundStyle = factory.makeColoredBackground(FGEConstants.DEFAULT_BACKGROUND_COLOR);
		shapeShadowStyle = factory.makeNoneShadowStyle();

		// final Vector<RepresentedShape> singleton = new Vector<RepresentedShape>();
		// singleton.add(representedShape);

		drawing = new DrawingImpl<JConnectorPreviewPanel>(this, factory, PersistenceMode.SharedGraphicalRepresentations) {
			@Override
			public void init() {
				final DrawingGRBinding<JConnectorPreviewPanel> previewPanelBinding = bindDrawing(JConnectorPreviewPanel.class,
						"previewPanel", new DrawingGRProvider<JConnectorPreviewPanel>() {
							@Override
							public DrawingGraphicalRepresentation provideGR(JConnectorPreviewPanel drawable, FGEModelFactory factory) {
								return drawingGR;
							}
						});
				final ShapeGRBinding<JConnectorPreviewPanel> startShapeBinding = bindShape(JConnectorPreviewPanel.class, "startShape",
						new ShapeGRProvider<JConnectorPreviewPanel>() {
							@Override
							public ShapeGraphicalRepresentation provideGR(JConnectorPreviewPanel drawable, FGEModelFactory factory) {
								return startShapeGR;
							}
						});
				final ShapeGRBinding<JConnectorPreviewPanel> endShapeBinding = bindShape(JConnectorPreviewPanel.class, "endShape",
						new ShapeGRProvider<JConnectorPreviewPanel>() {
							@Override
							public ShapeGraphicalRepresentation provideGR(JConnectorPreviewPanel drawable, FGEModelFactory factory) {
								return endShapeGR;
							}
						});
				final ConnectorGRBinding<JConnectorPreviewPanel> connectorBinding = bindConnector(JConnectorPreviewPanel.class, "connector",
						new ConnectorGRProvider<JConnectorPreviewPanel>() {
							@Override
							public ConnectorGraphicalRepresentation provideGR(JConnectorPreviewPanel drawable, FGEModelFactory factory) {
								return connectorGR;
							}
						});

				previewPanelBinding.addToWalkers(new GRStructureVisitor<JConnectorPreviewPanel>() {

					@Override
					public void visit(JConnectorPreviewPanel previewPanel) {
						drawShape(startShapeBinding, previewPanel, previewPanelBinding, previewPanel);
						drawShape(endShapeBinding, previewPanel, previewPanelBinding, previewPanel);
						drawConnector(connectorBinding, previewPanel, startShapeBinding, previewPanel, endShapeBinding, previewPanel,
								previewPanelBinding, previewPanel);
					}
				});
			}
		};
		drawing.setEditable(true);

		drawingGR = factory.makeDrawingGraphicalRepresentation(false);
		drawingGR.setBackgroundColor(Color.WHITE);
		drawingGR.setWidth(getPanelWidth());
		drawingGR.setHeight(getPanelHeight());
		drawingGR.setDrawWorkingArea(false);

		startShapeGR = factory.makeShapeGraphicalRepresentation(ShapeType.RECTANGLE);
		startShapeGR.setX(10);
		startShapeGR.setY(10);
		startShapeGR.setWidth(20);
		startShapeGR.setHeight(20);
		startShapeGR.setForeground(shapeForegroundStyle);
		startShapeGR.setBackground(shapeBackgroundStyle);
		startShapeGR.setShadowStyle(shapeShadowStyle);
		startShapeGR.setShapeSpecification(factory.makeShape(ShapeType.RECTANGLE));
		startShapeGR.setIsSelectable(true);
		startShapeGR.setIsFocusable(true);
		startShapeGR.setIsReadOnly(false);
		// startShapeGR.setBorder(factory.makeShapeBorder(0, 0, 0, 0));

		endShapeGR = factory.makeShapeGraphicalRepresentation(ShapeType.RECTANGLE);
		endShapeGR.setX(getPanelWidth() - 30);
		endShapeGR.setY(getPanelHeight() - 30);
		endShapeGR.setWidth(20);
		endShapeGR.setHeight(20);
		endShapeGR.setForeground(shapeForegroundStyle);
		endShapeGR.setBackground(shapeBackgroundStyle);
		endShapeGR.setShadowStyle(shapeShadowStyle);
		endShapeGR.setShapeSpecification(factory.makeShape(ShapeType.RECTANGLE));
		endShapeGR.setIsSelectable(true);
		endShapeGR.setIsFocusable(true);
		endShapeGR.setIsReadOnly(false);
		// endShapeGR.setBorder(factory.makeShapeBorder(0, 0, 0, 0));

		connectorGR = factory.makeConnectorGraphicalRepresentation(ConnectorType.LINE);
		connectorGR.setForeground(foregroundStyle);
		connectorGR.setIsSelectable(true);
		connectorGR.setIsFocusable(true);
		connectorGR.setIsReadOnly(false);

		controller = new JDianaInteractiveViewer<JConnectorPreviewPanel>(drawing, factory, SwingToolFactory.DEFAULT);
		add(controller.getDrawingView());
	}

	@Override
	public void delete() {

	}

	public int getPanelWidth() {
		return width;
	}

	public void setPanelWidth(int width) {
		this.width = width;
		setPreferredSize(new Dimension(getPanelWidth(), getPanelHeight()));
		drawingGR.setWidth(getPanelWidth());
	}

	public int getPanelHeight() {
		return height;
	}

	public void setPanelHeight(int height) {
		this.height = height;
		setPreferredSize(new Dimension(getPanelWidth(), getPanelHeight()));
		drawingGR.setHeight(getPanelHeight());
	}

	protected void update() {

		// getShape().updateShape();

		connectorGR.setConnectorSpecification(
				getConnectorSpecification() != null ? getConnectorSpecification() : factory.makeConnector(ConnectorType.LINE));
		connectorGR.notifyConnectorModified();

		controller.getDelegate().repaintAll();

	}

	public ConnectorSpecification getConnectorSpecification() {
		return connectorGR.getConnectorSpecification();
	}

	public void setConnectorSpecification(ConnectorSpecification connectorSpecification) {
		if (connectorSpecification != null && (connectorSpecification != connectorGR.getConnectorSpecification()
				|| !connectorSpecification.equals(connectorGR.getConnectorSpecification()))) {
			connectorGR.setConnectorSpecification(/*(ConnectorSpecification)*/connectorSpecification/*.clone()*/);
			/*
			 * if (shape.getShapeType() == ShapeType.CUSTOM_POLYGON) {
			 * System.out.println("Go to edition mode");
			 * controller.setCurrentTool(EditorTool.DrawShapeTool);
			 * controller.getDrawShapeToolController().setShape(
			 * shape.getShape()); }
			 */
			update();
		}
	}

	@Override
	public ConnectorSpecification getEditedObject() {
		return getConnectorSpecification();
	}

	@Override
	public void setEditedObject(ConnectorSpecification object) {
		setConnectorSpecification(object);
	}

	@Override
	public ConnectorSpecification getRevertValue() {
		return null;
	}

	@Override
	public void setRevertValue(ConnectorSpecification object) {
	}

	@Override
	public void addApplyCancelListener(ApplyCancelListener l) {
	}

	@Override
	public void removeApplyCancelListener(ApplyCancelListener l) {
	}

	@Override
	public Class<ConnectorSpecification> getRepresentedType() {
		return ConnectorSpecification.class;
	}

	@Override
	public void init(FIBCustom component, FIBController controller) {
	}

	/*public class RepresentedDrawing {
	}
	
	public class RepresentedShape {
		public ShapeSpecification getRepresentedShape() {
			return getShape();
		}
	}*/

	public ForegroundStyle getForegroundStyle() {
		return foregroundStyle;
	}

	public void setForegroundStyle(ForegroundStyle foregroundStyle) {
		this.foregroundStyle = foregroundStyle;
		connectorGR.setForeground(foregroundStyle);
	}

}
