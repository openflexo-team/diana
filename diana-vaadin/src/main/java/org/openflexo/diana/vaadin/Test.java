package org.openflexo.diana.vaadin;

import org.openflexo.checkers.CheckersBoard;
import org.openflexo.checkers.CheckersDrawing;
import org.openflexo.diana.vaadin.control.VaadinToolFactory;
import org.openflexo.fge.FGEModelFactory;
import org.openflexo.fge.FGEModelFactoryImpl;
import org.openflexo.model.exceptions.ModelDefinitionException;

public class Test {

	public static void main(String[] args) {
		FGEModelFactory factory = null;
		try {
			factory = new FGEModelFactoryImpl();
		} catch (ModelDefinitionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		CheckersBoard board = new CheckersBoard();
		CheckersDrawing d = new CheckersDrawing(board, factory);

		VDianaViewer<CheckersBoard> viewer = new VDianaViewer<CheckersBoard>(d, d.getFactory(), VaadinToolFactory.DEFAULT);

	}

}
