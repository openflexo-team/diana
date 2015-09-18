package org.openflexo.checkers;

import javax.swing.JDialog;

import org.openflexo.fge.swing.JDianaInteractiveViewer;

public class CheckersUI extends JDialog {

	private final JDianaInteractiveViewer<CheckersGame> viewer;

	public CheckersUI(JDianaInteractiveViewer<CheckersGame> viewer) {
		this.viewer = viewer;
		this.getContentPane().add(viewer.getDrawingView());

		this.setTitle("Checkers");
		this.setResizable(false);
		this.setSize(420, 420);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		this.setVisible(true);
	}
}
