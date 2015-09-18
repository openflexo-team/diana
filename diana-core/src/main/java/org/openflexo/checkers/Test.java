package org.openflexo.checkers;

import java.util.LinkedList;
import java.util.List;

public class Test {

	public static void main(String[] args) {
		List<CheckersStep> list = new LinkedList<>();

		Cell cell1 = new Cell(0, 1);
		Cell cell10 = new Cell(0, 1);
		Cell cell2 = new Cell(0, 2);

		CheckersStep step1 = new CheckersStep(cell1, cell2);
		CheckersStep step2 = new CheckersStep(cell1, cell2);

		list.add(step1);
	}

}
