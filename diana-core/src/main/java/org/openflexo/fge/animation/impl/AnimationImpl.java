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
package org.openflexo.fge.animation.impl;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.Timer;

import org.openflexo.fge.animation.Animable;
import org.openflexo.fge.animation.Animation;
import org.openflexo.fge.animation.Transition;
import org.openflexo.fge.control.AbstractDianaEditor;
import org.openflexo.model.undo.CompoundEdit;

public class AnimationImpl implements Animation {
	private int currentStep = 0;
	private final int steps;
	private final List<? extends Transition> transitions;
	private Timer timer;
	private final CompoundEdit edit;
	private final AbstractDianaEditor<?, ?, ?> editor;

	public static void performTransitions(final List<? extends Transition> transitions, int steps, Animable animable) {
		new AnimationImpl(transitions, steps, null, null).performAnimation(animable);
	}

	public static void performTransitions(final List<? extends Transition> transitions, int steps, CompoundEdit edit,
			AbstractDianaEditor<?, ?, ?> editor, Animable animable) {
		new AnimationImpl(transitions, steps, edit, editor).performAnimation(animable);
	}

	private AnimationImpl(List<? extends Transition> transitions, int steps, CompoundEdit edit, AbstractDianaEditor<?, ?, ?> editor) {
		super();
		this.currentStep = 0;
		this.steps = steps;
		this.transitions = transitions;
		this.edit = edit;
		this.editor = editor;
	}

	@Override
	public int getCurrentStep() {
		return currentStep;
	}

	@Override
	public int getSteps() {
		return steps;
	}

	@Override
	public List<? extends Transition> getTransitions() {
		return transitions;
	}

	@Override
	public CompoundEdit getEdit() {
		return edit;
	}

	@Override
	public AbstractDianaEditor<?, ?, ?> getEditor() {
		return editor;
	}

	@Override
	public void performAnimation(final Animable animable) {
		animable.startAnimation(AnimationImpl.this);
		currentStep = 0;
		timer = new Timer(0, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				for (final Transition tt : transitions) {
					tt.performStep(currentStep, steps);
				}
				currentStep++;
				if (currentStep > steps) {
					timer.stop();
					stopRecordEdit(edit);
					animable.stopAnimation(AnimationImpl.this);
				}
			}
		});
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				timer.start();
			}
		});

		// timer.start();
	}

	protected void stopRecordEdit(CompoundEdit edit) {
		if (edit != null && editor.getUndoManager() != null) {
			editor.getUndoManager().stopRecording(edit);
		}
	}

}