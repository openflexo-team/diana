package org.openflexo.diana.geomedit.controller;

import org.openflexo.diana.Drawing.DrawingTreeNode;
import org.openflexo.diana.control.MouseClickControlAction;
import org.openflexo.diana.control.MouseControlContext;
import org.openflexo.diana.control.actions.MouseClickControlActionImpl;
import org.openflexo.diana.control.actions.MouseClickControlImpl;
import org.openflexo.diana.geomedit.GeomEditDrawingController;
import org.openflexo.model.factory.EditingContext;

public class ShowContextualMenuControl extends MouseClickControlImpl<GeomEditDrawingController> {

	public ShowContextualMenuControl(EditingContext editingContext) {
		this(editingContext, false);
	}

	public ShowContextualMenuControl(EditingContext editingContext, boolean ctrlPressed) {

		super("Show contextual menu", MouseButton.RIGHT, 1, null, false, ctrlPressed, false, false, editingContext);

		MouseClickControlAction<GeomEditDrawingController> action = new MouseClickControlActionImpl<GeomEditDrawingController>() {

			@Override
			public boolean handleClick(DrawingTreeNode<?, ?> node, GeomEditDrawingController controller, MouseControlContext context) {

				controller.showContextualMenu(node, controller.getDrawingView(), context.getPoint());

				return true;
			}
		};

		setControlAction(action);

	}
}
