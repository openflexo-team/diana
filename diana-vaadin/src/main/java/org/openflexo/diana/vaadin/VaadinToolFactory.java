package org.openflexo.diana.vaadin;

import org.openflexo.fge.control.AbstractDianaEditor;
import org.openflexo.fge.control.DianaInteractiveEditor;
import org.openflexo.fge.control.DianaToolFactory;
import org.openflexo.fge.control.DrawingPalette;
import org.openflexo.fge.control.actions.DrawConnectorAction;
import org.openflexo.fge.control.actions.DrawShapeAction;
import org.openflexo.fge.control.tools.DianaInspectors;
import org.openflexo.fge.control.tools.DianaLayoutWidget;
import org.openflexo.fge.control.tools.DianaPalette;
import org.openflexo.fge.control.tools.DianaScaleSelector;
import org.openflexo.fge.control.tools.DianaStyles;
import org.openflexo.fge.control.tools.DianaToolSelector;
import org.openflexo.fge.control.tools.DrawComplexCurveToolController;
import org.openflexo.fge.control.tools.DrawConnectorToolController;
import org.openflexo.fge.control.tools.DrawPolygonToolController;
import org.openflexo.fge.control.tools.DrawShapeToolController;
import org.openflexo.fge.control.tools.DrawTextToolController;

import com.vaadin.ui.AbstractComponent;

public class VaadinToolFactory implements DianaToolFactory<AbstractComponent> {

	public static VaadinToolFactory DEFAULT = new VaadinToolFactory();

	@Override
	public DianaToolSelector<? extends AbstractComponent, ?> makeDianaToolSelector(AbstractDianaEditor<?, ?, ?> editor) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DianaScaleSelector<? extends AbstractComponent, ?> makeDianaScaleSelector(AbstractDianaEditor<?, ?, ?> editor) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DianaStyles<? extends AbstractComponent, ?> makeDianaStyles() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DianaInspectors<?, ?> makeDianaDialogInspectors() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DianaInspectors<?, ?> makeDianaInspectors() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DianaLayoutWidget<? extends AbstractComponent, ?> makeDianaLayoutWidget() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DianaPalette<? extends AbstractComponent, ?> makeDianaPalette(DrawingPalette palette) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DrawPolygonToolController<?> makeDrawPolygonToolController(DianaInteractiveEditor<?, ?, ?> controller, DrawShapeAction control) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DrawComplexCurveToolController<?> makeDrawClosedCurveToolController(DianaInteractiveEditor<?, ?, ?> controller,
			DrawShapeAction control, boolean isClosedCurve) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DrawShapeToolController<?> makeDrawShapeToolController(DianaInteractiveEditor<?, ?, ?> controller, DrawShapeAction control) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DrawConnectorToolController<?> makeDrawConnectorToolController(DianaInteractiveEditor<?, ?, ?> controller,
			DrawConnectorAction control) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DrawTextToolController<?> makeDrawTextToolController(DianaInteractiveEditor<?, ?, ?> controller, DrawShapeAction control) {
		// TODO Auto-generated method stub
		return null;
	}

}
