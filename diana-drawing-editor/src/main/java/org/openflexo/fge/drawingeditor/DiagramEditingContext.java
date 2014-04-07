package org.openflexo.fge.drawingeditor;

import org.openflexo.model.factory.EditingContextImpl;

public class DiagramEditingContext extends EditingContextImpl {

	public DiagramEditingContext() {
		createUndoManager();
	}
}
