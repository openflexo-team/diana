package org.openflexo.checkers;

import org.openflexo.fge.FGEModelFactory;
import org.openflexo.fge.FGEModelFactoryImpl;
import org.openflexo.fge.swing.JDianaInteractiveViewer;
import org.openflexo.fge.swing.control.SwingToolFactory;
import org.openflexo.model.exceptions.ModelDefinitionException;

public class Checkers {

	public static void main(String[] args) {
		CheckersBoard board = new CheckersBoard();
		CheckersController controller = new CheckersController(board);
		CheckersGame game = new CheckersGame(board, controller);

		FGEModelFactory factory = null;
		try {
			factory = new FGEModelFactoryImpl();
		} catch (ModelDefinitionException e) {
			e.printStackTrace();
		}

		CheckersDrawing d = new CheckersDrawing(game, factory);
		JDianaInteractiveViewer<CheckersGame> viewer = new JDianaInteractiveViewer<>(d, d.getFactory(), SwingToolFactory.DEFAULT);

		CheckersUI ui = new CheckersUI(viewer);
	}

}
