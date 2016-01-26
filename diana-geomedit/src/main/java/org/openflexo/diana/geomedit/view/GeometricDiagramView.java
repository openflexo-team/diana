package org.openflexo.diana.geomedit.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.logging.Logger;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;

import org.openflexo.diana.geomedit.GeomEditDrawingEditor;
import org.openflexo.diana.geomedit.GeometricDiagramDrawing;
import org.openflexo.diana.geomedit.edition.EditionInputMethod;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geomedit.GeometricDrawingView;
import org.openflexo.logging.FlexoLogger;

public class GeometricDiagramView extends JPanel {

	private static final Logger logger = FlexoLogger.getLogger(GeometricDrawingView.class.getPackage().getName());

	private FGEPoint lastMouseLocation;
	private GeomEditDrawingEditor controller;

	private JScrollPane scrollPane;
	private JSplitPane splitPane;

	public GeometricDiagramView(GeomEditDrawingEditor controller) {
		super(new BorderLayout());

		this.controller = controller;

		lastMouseLocation = new FGEPoint();
		addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				Point ptInView = SwingUtilities.convertPoint((Component) e.getSource(), e.getPoint(), getController().getDrawingView());
				lastMouseLocation.x = ptInView.x / getController().getScale();
				lastMouseLocation.y = ptInView.y / getController().getScale();
				getController().getPositionLabel().setText((int) lastMouseLocation.x + " x " + (int) lastMouseLocation.y);
				if (getController().getCurrentEdition() != null
						&& getController().getCurrentEdition().requireRepaint(lastMouseLocation.clone())) {
					// TODO !
					System.out.println("Il faut repeindre ici !!!");
					// getPaintManager().repaint(GeometricDrawingView.this);
				}
			}
		});

		setLayout(new BorderLayout());
		scrollPane = new JScrollPane(controller.getDrawingView());

		add(getControlPanel(), BorderLayout.NORTH);

		JScrollPane browser = new JScrollPane(new JLabel("browser"));// v.getController().getTree());
		// browser.setPreferredSize(new Dimension(200,200));
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, browser, scrollPane);
		splitPane.setDividerLocation(150);
		splitPane.setResizeWeight(0);

		add(splitPane, BorderLayout.CENTER);

		JPanel bottom = new JPanel(new BorderLayout());
		bottom.add(getEditionLabel(), BorderLayout.WEST);
		bottom.add(getPositionLabel(), BorderLayout.EAST);

		add(bottom, BorderLayout.SOUTH);

		validate();
	}

	public GeometricDiagramDrawing getDrawing() {
		return getController().getDrawing();
	}

	public JPanel getControlPanel() {
		return getController().getControlPanel();
	}

	public JLabel getEditionLabel() {
		return getController().getEditionLabel();
	}

	public JLabel getPositionLabel() {
		return getController().getEditionLabel();
	}

	public GeomEditDrawingEditor getController() {
		return controller;
	}

	public GeomEditDrawingView getDrawingView() {
		return controller.getDrawingView();
	}

	public void enableEditionInputMethod(EditionInputMethod anInputMethod) {
		getDrawingView().enableEditionInputMethod(anInputMethod);
	}

	public void disableEditionInputMethod() {
		getDrawingView().disableEditionInputMethod();
	}

	/*@Override
	public void update(Observable o, Object notification) {
		if (notification instanceof NodeAdded) {
			getController().notifiedObjectAdded();
		}
		else if (notification instanceof NodeAdded) {
			getController().notifiedObjectRemoved();
		}
		super.update(o, notification);
	}*/

}
