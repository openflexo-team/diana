package org.openflexo.fge.control.tools.animations;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.Timer;

import org.openflexo.fge.control.AbstractDianaEditor;
import org.openflexo.model.undo.CompoundEdit;

public class Animation {
	private int currentStep = 0;
	private final int steps;
	private final List<TranslationTransition> transitions;
	private Timer timer;
	private final CompoundEdit edit;
	private final AbstractDianaEditor<?, ?, ?> editor;

	public static void performTransitions(final List<TranslationTransition> transitions) {
		new Animation(transitions, 10, null, null).performAnimation();
	}

	public static void performTransitions(final List<TranslationTransition> transitions, CompoundEdit edit,
			AbstractDianaEditor<?, ?, ?> editor) {
		new Animation(transitions, 10, edit, editor).performAnimation();
	}

	private Animation(List<TranslationTransition> transitions, int steps, CompoundEdit edit, AbstractDianaEditor<?, ?, ?> editor) {
		super();
		this.currentStep = 0;
		this.steps = steps;
		this.transitions = transitions;
		this.edit = edit;
		this.editor = editor;
	}

	private void performAnimation() {
		currentStep = 0;
		timer = new Timer(0, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				for (final TranslationTransition tt : transitions) {
					tt.performStep(currentStep, steps);
				}
				currentStep++;
				if (currentStep > steps) {
					timer.stop();
					stopRecordEdit(edit);
				}
			}
		});
		timer.start();
	}

	protected void stopRecordEdit(CompoundEdit edit) {
		if (edit != null && editor.getUndoManager() != null) {
			editor.getUndoManager().stopRecording(edit);
		}
	}

}