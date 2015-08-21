package org.openflexo.checkers;

import javax.swing.JDialog;

import org.openflexo.fge.FGEModelFactory;
import org.openflexo.fge.FGEModelFactoryImpl;
import org.openflexo.fge.swing.JDianaViewer;
import org.openflexo.fge.swing.control.SwingToolFactory;
import org.openflexo.model.exceptions.ModelDefinitionException;

public class CheckersUI extends JDialog {
	private final JDianaViewer<CheckersBoard> viewer;

	public CheckersUI(CheckersBoard board) {
		FGEModelFactory factory = null;
		try {
			factory = new FGEModelFactoryImpl();
		} catch (ModelDefinitionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		CheckersDrawing d = new CheckersDrawing(board, factory);
		viewer = new JDianaViewer<>(d, d.getFactory(), SwingToolFactory.DEFAULT);

		this.getContentPane().add(viewer.getDrawingView());

		this.setTitle("Checkers");
		this.setResizable(false);
		this.setSize(420, 420);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		this.setVisible(true);
	}
}
