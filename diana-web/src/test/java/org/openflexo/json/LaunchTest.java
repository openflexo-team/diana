package org.openflexo.json;

import javax.swing.JDialog;

import org.openflexo.diagram.Diagram;
import org.openflexo.diagram.DiagramDrawing;
import org.openflexo.diagram.Shape;
import org.openflexo.diagram.Shape.ShapeType;
import org.openflexo.fge.FGEModelFactory;
import org.openflexo.fge.FGEModelFactoryImpl;
import org.openflexo.fge.swing.JDianaInteractiveViewer;
import org.openflexo.fge.swing.control.SwingToolFactory;
import org.openflexo.model.exceptions.ModelDefinitionException;

public class LaunchTest {

	public static void main(String[] args) {
		Diagram diagram = new Diagram();
		Shape rectangle = new Shape(ShapeType.RECTANGLE, 20, 40, 200, 200);
		rectangle.addChild(new Shape(ShapeType.RECTANGLE, 10, 10, 40, 30));
		diagram.add(rectangle);

		diagram.add(new Shape(ShapeType.TRIANGLE, 80, 250, 150, 100));
		// diagram.add(new Shape(ShapeType.TRIANGLE, 400, 320, 90, 200));
		// diagram.add(new Shape(ShapeType.OVAL, 50, 30, 90, 90));

		FGEModelFactory factory = null;
		try {
			factory = new FGEModelFactoryImpl();
		} catch (ModelDefinitionException e) {
			e.printStackTrace();
		}

		DiagramDrawing d = new DiagramDrawing(diagram, factory);
		JDianaInteractiveViewer<Diagram> viewer = new JDianaInteractiveViewer<>(d, d.getFactory(), SwingToolFactory.DEFAULT);

		JDialog dialog = new JDialog();
		dialog.setContentPane(viewer.getDrawingView());
		dialog.setSize(400, 500);
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialog.setVisible(true);

		JsonVisitor visitor = JsonFactory.INSTANCE.makeJsonVisitor();
		d.accept(visitor);

		visitor.printJson(System.out);

	}

}
