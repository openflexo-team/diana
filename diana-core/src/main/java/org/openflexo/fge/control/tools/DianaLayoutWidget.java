/**
 * 
 * Copyright (c) 2014, Openflexo
 * 
 * This file is part of Diana-core, a component of the software infrastructure 
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

package org.openflexo.fge.control.tools;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;

import org.openflexo.fge.Drawing.ShapeNode;
import org.openflexo.fge.control.DianaInteractiveViewer;
import org.openflexo.fge.control.tools.animations.Animation;
import org.openflexo.fge.control.tools.animations.TranslationTransition;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.view.DianaViewFactory;

/**
 * Represent a widget allowing to edit a layout, associated with a {@link DianaInteractiveViewer}
 * 
 * @author sylvain
 * 
 * @param <C>
 * @param <F>
 * @param <ME>
 */
public abstract class DianaLayoutWidget<C, F extends DianaViewFactory<F, ? super C>> extends DianaToolImpl<C, F> {

	/**
	 * Return the technology-specific component representing the widget
	 * 
	 * @return
	 */
	public abstract C getComponent();

	public void alignLeft() {
		System.out.println("Align left with " + getSelectedShapes());
		double newX = Double.POSITIVE_INFINITY;
		for (ShapeNode<?> gr : getSelectedShapes()) {
			if (gr.getX() < newX) {
				newX = gr.getX();
			}
		}
		List<TranslationTransition> tts = new ArrayList<TranslationTransition>();
		for (ShapeNode<?> gr : getSelectedShapes()) {
			tts.add(new TranslationTransition(gr, gr.getLocation(), new FGEPoint(newX, gr.getY())));
		}
		performTransitions(tts, "Align left");
	}

	public void alignCenter() {
		System.out.println("Align center with " + getSelectedShapes());
		double totalX = 0;
		for (ShapeNode<?> gr : getSelectedShapes()) {
			totalX += gr.getX() + gr.getWidth() / 2;
		}
		double newX = totalX / getSelectedShapes().size();
		List<TranslationTransition> tts = new ArrayList<TranslationTransition>();
		for (ShapeNode<?> gr : getSelectedShapes()) {
			tts.add(new TranslationTransition(gr, gr.getLocation(), new FGEPoint(newX - gr.getWidth() / 2, gr.getY())));
		}
		performTransitions(tts, "Align center");
	}

	public void alignRight() {
		System.out.println("Align right with " + getSelectedShapes());
		double newX = 0;
		for (ShapeNode<?> gr : getSelectedShapes()) {
			if (gr.getX() + gr.getWidth() > newX) {
				newX = gr.getX() + gr.getWidth();
			}
		}
		List<TranslationTransition> tts = new ArrayList<TranslationTransition>();
		for (ShapeNode<?> gr : getSelectedShapes()) {
			tts.add(new TranslationTransition(gr, gr.getLocation(), new FGEPoint(newX - gr.getWidth(), gr.getY())));
		}
		performTransitions(tts, "Align right");
	}

	public void alignTop() {
		System.out.println("Align top with " + getSelectedShapes());
		double newY = Double.POSITIVE_INFINITY;
		for (ShapeNode<?> gr : getSelectedShapes()) {
			if (gr.getY() < newY) {
				newY = gr.getY();
			}
		}
		List<TranslationTransition> tts = new ArrayList<TranslationTransition>();
		for (ShapeNode<?> gr : getSelectedShapes()) {
			tts.add(new TranslationTransition(gr, gr.getLocation(), new FGEPoint(gr.getX(), newY)));
		}
		performTransitions(tts, "Align top");
	}

	public void alignMiddle() {
		System.out.println("Align middle with " + getSelectedShapes());
		double totalY = 0;
		for (ShapeNode<?> gr : getSelectedShapes()) {
			totalY += gr.getY() + gr.getHeight() / 2;
		}
		double newY = totalY / getSelectedShapes().size();
		List<TranslationTransition> tts = new ArrayList<TranslationTransition>();
		for (ShapeNode<?> gr : getSelectedShapes()) {
			tts.add(new TranslationTransition(gr, gr.getLocation(), new FGEPoint(gr.getX(), newY - gr.getHeight() / 2)));
		}
		performTransitions(tts, "Align middle");
	}

	public void alignBottom() {
		System.out.println("Align bottom with " + getSelectedShapes());
		double newY = 0;
		for (ShapeNode<?> gr : getSelectedShapes()) {
			if (gr.getY() + gr.getHeight() > newY) {
				newY = gr.getY() + gr.getHeight();
			}
		}
		List<TranslationTransition> tts = new ArrayList<TranslationTransition>();
		for (ShapeNode<?> gr : getSelectedShapes()) {
			tts.add(new TranslationTransition(gr, gr.getLocation(), new FGEPoint(gr.getX(), newY - gr.getHeight())));
		}
		performTransitions(tts, "Align bottom");
	}

	public void performTransitions(final List<TranslationTransition> transitions, String editName) {
		Animation.performTransitions(transitions, startRecordEdit(editName), getEditor());
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
	}

}
