package org.openflexo.diana.vaadin.widgets;

import javax.swing.JComponent;

import org.openflexo.fge.TextStyle;
import org.openflexo.fge.view.widget.FIBTextStyleSelector;
import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBCustom;
import org.openflexo.swing.CustomPopup;

@SuppressWarnings("serial")
public class VFIBTextStyleSelector extends CustomPopup<TextStyle> implements FIBTextStyleSelector<VFIBTextStyleSelector> {

	public VFIBTextStyleSelector(TextStyle editedObject) {
		super(editedObject);
		// TODO Auto-generated constructor stub
	}

	@Override
	public VFIBTextStyleSelector getJComponent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Class<TextStyle> getRepresentedType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TextStyle getRevertValue() {
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
			TextStyle arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateCustomPanel(TextStyle arg0) {
		// TODO Auto-generated method stub
		
	}
	
}