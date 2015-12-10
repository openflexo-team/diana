package org.openflexo.server;

import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.Drawing.ShapeNode;
import org.openflexo.fge.GRProperty;

public class UpdateChange<T> extends Change {
	private final int updatedNodeId;

	// private DrawingTreeNode<?, ?> updatedNode;
	private final String changedProperty;

	// private final int oldValue;
	private final T newValue;

	public UpdateChange(int updatedNodeId, String changedProperty, T newValue) {
		this.updatedNodeId = updatedNodeId;
		this.changedProperty = changedProperty;
		// this.oldValue = oldValue;
		this.newValue = newValue;
	}

	public int getUpdatedNodeId() {
		return updatedNodeId;
	}

	public String getChangedProperty() {
		return changedProperty;
	}

	public T getNewValue() {
		return newValue;
	}

	@Override
	public void apply(ChangeManager manager) {
		DrawingTreeNode<?, ?> dtn = manager.getDrawingTreeNodeById(updatedNodeId);
		GRProperty<T> property = (GRProperty<T>) GRProperty.getGRParameter(dtn.getGraphicalRepresentation().getClass(), changedProperty);
		// T value = dtn.getPropertyValue(property);

		if (dtn instanceof ShapeNode) {
			/*if ((property.getName().equals("x") || p.getName().equals("y"))) {
				FGEPoint point = new FGEPoint(dtn.getPropertyValue(ShapeGraphicalRepresentation.X),
						dtn.getPropertyValue(ShapeGraphicalRepresentation.Y));*/
			if (property.getName().equals("x"))
				((ShapeNode) dtn).setX((double) newValue);
			else if (property.getName().equals("y"))
				((ShapeNode) dtn).setY((double) newValue);
			// ((ShapeNode) dtn).setLocation(point);
		}
		System.out.println("changed applied");
	}
}
