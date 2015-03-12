package org.openflexo.diana.vaadin.widgets;

import javax.swing.JComponent;

import org.openflexo.fge.BackgroundStyle;
import org.openflexo.fge.view.widget.FIBBackgroundStyleSelector;
import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBCustom;
import org.openflexo.swing.CustomPopup;

public class VFIBBackgroundStyleSelector extends CustomPopup<BackgroundStyle> implements
FIBBackgroundStyleSelector<VFIBBackgroundStyleSelector>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public VFIBBackgroundStyleSelector(BackgroundStyle editedObject) {
		super(editedObject);
		// TODO Auto-generated constructor stub
	}

	@Override
	public VFIBBackgroundStyleSelector getJComponent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Class<BackgroundStyle> getRepresentedType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BackgroundStyle getRevertValue() {
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
			BackgroundStyle arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateCustomPanel(BackgroundStyle arg0) {
		// TODO Auto-generated method stub
		
	}

}
