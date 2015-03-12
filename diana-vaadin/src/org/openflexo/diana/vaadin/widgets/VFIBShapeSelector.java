package org.openflexo.diana.vaadin.widgets;

import javax.swing.JComponent;

import org.openflexo.fge.shapes.ShapeSpecification;
import org.openflexo.fge.view.widget.FIBShapeSelector;
import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBCustom;
import org.openflexo.swing.CustomPopup;

@SuppressWarnings("serial")
public class VFIBShapeSelector extends CustomPopup<ShapeSpecification> implements FIBShapeSelector<VFIBShapeSelector> {

	public VFIBShapeSelector(ShapeSpecification editedObject) {
		super(editedObject);
		// TODO Auto-generated constructor stub
	}

	@Override
	public VFIBShapeSelector getJComponent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Class<ShapeSpecification> getRepresentedType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ShapeSpecification getRevertValue() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void init(FIBCustom arg0, FIBController arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected JComponent buildFrontComponent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected org.openflexo.swing.CustomPopup.ResizablePanel createCustomPanel(
			ShapeSpecification arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateCustomPanel(ShapeSpecification arg0) {
		// TODO Auto-generated method stub
		
	}

}
