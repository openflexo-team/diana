package org.openflexo.diana.vaadin.view;

import org.openflexo.diana.vaadin.VaadinViewFactory;
import org.openflexo.fge.Drawing.ShapeNode;
import org.openflexo.fge.control.AbstractDianaEditor;
import org.openflexo.fge.control.PaletteElement;

import com.vaadin.ui.AbstractComponent;

public class VPaletteElementView extends VShapeView<PaletteElement>{

	public VPaletteElementView(
			ShapeNode<PaletteElement> node,
			AbstractDianaEditor<?, VaadinViewFactory, AbstractComponent> controller) {
		super(node, controller);
		// TODO Auto-generated constructor stub
	}

}
