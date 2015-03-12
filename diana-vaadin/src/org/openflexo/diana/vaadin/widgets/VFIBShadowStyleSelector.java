package org.openflexo.diana.vaadin.widgets;

import javax.swing.JComponent;

import org.openflexo.fge.ShadowStyle;
import org.openflexo.fge.view.widget.FIBShadowStyleSelector;
import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBCustom;
import org.openflexo.swing.CustomPopup;

@SuppressWarnings("serial")
public class VFIBShadowStyleSelector extends CustomPopup<ShadowStyle> implements FIBShadowStyleSelector<VFIBShadowStyleSelector> {

	public VFIBShadowStyleSelector(ShadowStyle editedObject) {
		super(editedObject);
		// TODO Auto-generated constructor stub
	}

	@Override
	public VFIBShadowStyleSelector getJComponent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Class<ShadowStyle> getRepresentedType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ShadowStyle getRevertValue() {
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
			ShadowStyle arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateCustomPanel(ShadowStyle arg0) {
		// TODO Auto-generated method stub
		
	}

}
