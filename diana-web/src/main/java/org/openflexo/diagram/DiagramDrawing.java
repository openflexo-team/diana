package org.openflexo.diagram;

import java.awt.Color;

import org.openflexo.connie.DataBinding;
import org.openflexo.fge.DrawingGraphicalRepresentation;
import org.openflexo.fge.FGEModelFactory;
import org.openflexo.fge.GRBinding.DrawingGRBinding;
import org.openflexo.fge.GRBinding.ShapeGRBinding;
import org.openflexo.fge.GRProvider.DrawingGRProvider;
import org.openflexo.fge.GRProvider.ShapeGRProvider;
import org.openflexo.fge.GRStructureVisitor;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.impl.DrawingImpl;
import org.openflexo.fge.shapes.ShapeSpecification.ShapeType;

public class DiagramDrawing extends DrawingImpl<Diagram> {

	DrawingGraphicalRepresentation diagramRepresentation;
	ShapeGraphicalRepresentation rectangleRepresentation, circleRepresentation, triangleRepresentation;

	public DiagramDrawing(Diagram model, FGEModelFactory factory) {
		super(model, factory, PersistenceMode.SharedGraphicalRepresentations);
	}

	@Override
	public void init() {
		diagramRepresentation = getFactory().makeDrawingGraphicalRepresentation();

		circleRepresentation = getFactory().makeShapeGraphicalRepresentation(ShapeType.OVAL);
		circleRepresentation.setBackground(getFactory().makeColoredBackground(Color.BLUE));

		triangleRepresentation = getFactory().makeShapeGraphicalRepresentation(ShapeType.TRIANGLE);
		triangleRepresentation.setBackground(getFactory().makeColoredBackground(Color.GREEN));

		rectangleRepresentation = getFactory().makeShapeGraphicalRepresentation(ShapeType.RECTANGLE);
		rectangleRepresentation.setBackground(getFactory().makeColoredBackground(Color.RED));
		// rectangleRepresentation.setBackground(getFactory().makeEmptyBackground());

		final DrawingGRBinding<Diagram> diagramBinding = bindDrawing(Diagram.class, "diagram", new DrawingGRProvider<Diagram>() {
			@Override
			public DrawingGraphicalRepresentation provideGR(Diagram drawable, FGEModelFactory factory) {
				return diagramRepresentation;
			}
		});

		final ShapeGRBinding<Shape> shapeBinding = bindShape(Shape.class, "shape", new ShapeGRProvider<Shape>() {
			@Override
			public ShapeGraphicalRepresentation provideGR(Shape drawable, FGEModelFactory factory) {
				switch (drawable.getType()) {
					case OVAL:
						return circleRepresentation;
					case RECTANGLE:
						return rectangleRepresentation;
					case TRIANGLE:
						return triangleRepresentation;
					default:
						return rectangleRepresentation;
				}
			}
		});

		diagramBinding.addToWalkers(new GRStructureVisitor<Diagram>() {

			@Override
			public void visit(Diagram drawable) {
				for (Shape shape : drawable.getShapes()) {
					drawShape(shapeBinding, shape);
					System.out.println(shape.getChildren());
					for (Shape child : shape.getChildren()) {
						drawShape(shapeBinding, child);
						/*System.out.println("drawing child : " + child.getX() + " " + child.getY() + " " + child.getWidth() + " "
								+ child.getHeight() + "" + child.getType());
						drawShape(shapeBinding, child, shapeBinding, shape);*/
					}
				}
			}
		});

		shapeBinding.setDynamicPropertyValue(ShapeGraphicalRepresentation.X, new DataBinding<Double>("drawable.x"), true);
		shapeBinding.setDynamicPropertyValue(ShapeGraphicalRepresentation.Y, new DataBinding<Double>("drawable.y"), true);
		shapeBinding.setDynamicPropertyValue(ShapeGraphicalRepresentation.WIDTH, new DataBinding<Double>("drawable.width"), true);
		shapeBinding.setDynamicPropertyValue(ShapeGraphicalRepresentation.HEIGHT, new DataBinding<Double>("drawable.height"), true);
	}
}
