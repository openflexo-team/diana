package org.openflexo.server;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;

import org.openflexo.fge.BackgroundStyle;
import org.openflexo.fge.ColorBackgroundStyle;
import org.openflexo.fge.Drawing;
import org.openflexo.fge.Drawing.ContainerNode;
import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.Drawing.DrawingTreeNodeIdentifier;
import org.openflexo.fge.Drawing.RootNode;
import org.openflexo.fge.Drawing.ShapeNode;
import org.openflexo.fge.DrawingVisitor;

public class ChangeManager implements DrawingVisitor, PropertyChangeListener {
	private final Server server;
	private Drawing<?> drawing;
	private final Map<Integer, DrawingTreeNode<?, ?>> nodeMap;

	public ChangeManager(Server server) {
		this.server = server;
		nodeMap = new HashMap<>();
	}

	public Drawing<?> getDrawing() {
		return drawing;
	}

	public <O> void registerNode(DrawingTreeNode<O, ?> dtn) {
		DrawingTreeNodeIdentifier<O> id = new DrawingTreeNodeIdentifier<O>(dtn.getDrawable(), dtn.getGRBinding());
		nodeMap.put(id.hashCode(), dtn);
	}

	public DrawingTreeNode<?, ?> getDrawingTreeNodeById(int nodeId) {
		return nodeMap.get(nodeId);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getSource() instanceof DrawingTreeNode<?, ?>) {
			DrawingTreeNode<?, ?> dtn = (DrawingTreeNode<?, ?>) evt.getSource();
			UpdateChange<?> change = new UpdateChange<>(dtn.getId(), evt.getPropertyName(), evt.getNewValue());
			server.publishChange("server.changes", change);
		}
	}

	@Override
	public void visit(Drawing drawing) {
		this.drawing = drawing;
	}

	@Override
	public void visit(DrawingTreeNode<?, ?> dtn) {
		// dtn.getPropertyChangeSupport().addPropertyChangeListener(this);
		registerNode(dtn);
	}

	@Override
	public void visit(RootNode<?> dtn) {
		visit((DrawingTreeNode<?, ?>) dtn);
	}

	@Override
	public void visit(ContainerNode<?, ?> dtn) {
		visit((DrawingTreeNode<?, ?>) dtn);
	}

	@Override
	public void visit(ShapeNode<?> dtn) {
		visit((DrawingTreeNode<?, ?>) dtn);
	}

	@Override
	public void visit(BackgroundStyle bg, DrawingTreeNode<?, ?> dtn) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(ColorBackgroundStyle bg, DrawingTreeNode<?, ?> dtn) {
		// TODO Auto-generated method stub

	}

	public void apply(Change change) {
		change.apply(this);
	}

}
