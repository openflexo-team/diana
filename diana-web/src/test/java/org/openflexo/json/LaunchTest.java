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
		diagram.add(new Shape(ShapeType.RECTANGLE, 20, 40, 120, 80));
		diagram.add(new Shape(ShapeType.TRIANGLE, 80, 20, 150, 100));
		diagram.add(new Shape(ShapeType.OVAL, 50, 30, 90, 90));

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

		JsonVisitor visitor = new JsonVisitor();
		d.accept(visitor);

		visitor.printJson(System.out);

	}

}
