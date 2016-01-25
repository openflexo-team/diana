/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * Copyright (c) 2011-2012, AgileBirds
 * 
 * This file is part of Diana-geomedit, a component of the software infrastructure 
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

package org.openflexo.fge.geomedit;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.Observable;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;

import org.openflexo.diana.geomedit.edition.EditionInputMethod;
import org.openflexo.fge.control.AbstractDianaEditor;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.notifications.NodeAdded;
import org.openflexo.fge.swing.view.JDrawingView;
import org.openflexo.logging.FlexoLogger;

public class GeometricDrawingView extends JDrawingView<GeometricDrawing> {

	private static final Logger logger = FlexoLogger.getLogger(GeometricDrawingView.class.getPackage().getName());

	private FGEPoint lastMouseLocation;

	public GeometricDrawingView(GeometricDrawing drawing, AbstractDianaEditor<GeometricDrawing> controller) {
		super(drawing, controller);
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
					getPaintManager().repaint(GeometricDrawingView.this);
				}
			}
		});
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);

		if (getController().getCurrentEdition() != null) {
			Graphics2D g2 = (Graphics2D) g;
			graphics.createGraphics(g2, getController());
			getController().getCurrentEdition().paint(graphics, lastMouseLocation);
			graphics.releaseGraphics();
		}
	}

	@Override
	public GeomEditController getController() {
		return (GeomEditController) super.getController();
	}

	private EditionInputMethod inputMethod;

	public void enableEditionInputMethod(EditionInputMethod anInputMethod) {
		if (inputMethod != null) {
			removeMouseListener(inputMethod);
			removeMouseMotionListener(inputMethod);
		}
		inputMethod = anInputMethod;
		removeMouseListener(getMouseListener());
		removeMouseMotionListener(getMouseListener());
		addMouseListener(anInputMethod);
		addMouseMotionListener(anInputMethod);
	}

	public void disableEditionInputMethod() {
		if (inputMethod != null) {
			removeMouseListener(inputMethod);
			removeMouseMotionListener(inputMethod);
			addMouseListener(getMouseListener());
			addMouseMotionListener(getMouseListener());
			inputMethod = null;
		}
	}

	@Override
	public void update(Observable o, Object notification) {
		if (notification instanceof NodeAdded) {
			getController().notifiedObjectAdded();
		} else if (notification instanceof NodeAdded) {
			getController().notifiedObjectRemoved();
		}
		super.update(o, notification);
	}

}
