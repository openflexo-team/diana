package org.openflexo.diana.vaadin;

import org.openflexo.diana.vaadin.control.tools.VDianaPalette;
import org.openflexo.diana.vaadin.view.VPaletteElementView;
import org.openflexo.diana.vaadin.view.VShapeView;
import org.openflexo.fge.Drawing.ShapeNode;
import org.openflexo.fge.control.AbstractDianaEditor;
import org.openflexo.fge.control.PaletteElement;
import org.openflexo.fge.control.tools.DianaPalette;
import org.openflexo.fge.control.tools.PaletteController;

import com.vaadin.ui.AbstractComponent;

public class VPaletteController extends PaletteController<VaadinViewFactory, AbstractComponent> {
	
	
	public static VaadinViewFactory PALETTE_VIEW_FACTORY = new VaadinViewFactory() {
		public <O> VShapeView<O> makeShapeView(ShapeNode<O> shapeNode, AbstractDianaEditor<?, VaadinViewFactory, AbstractComponent> controller) {
			if (shapeNode.getDrawable() instanceof PaletteElement) {
				return (VShapeView<O>) (new VPaletteElementView((ShapeNode<PaletteElement>) shapeNode, (VPaletteController) controller));
			}
			return super.makeShapeView(shapeNode, controller);
		}
	};
	public VPaletteController(
			DianaPalette<AbstractComponent, VaadinViewFactory> palette,
			VaadinViewFactory dianaFactory) {
		super(palette, dianaFactory);
		// TODO Auto-generated constructor stub
	}

	public VPaletteController(VDianaPalette palette) {
		super(palette, PALETTE_VIEW_FACTORY);
	}

}
