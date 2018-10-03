package org.openflexo.diana.geomedit.controller;

import org.openflexo.diana.geomedit.GeomEditDrawingController;
import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.control.MouseClickControlAction;
import org.openflexo.fge.control.MouseControlContext;
import org.openflexo.fge.control.actions.MouseClickControlActionImpl;
import org.openflexo.fge.control.actions.MouseClickControlImpl;
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
