package org.openflexo.diana.vaadin.control.tools;

import org.openflexo.diana.vaadin.VaadinViewFactory;
import org.openflexo.fge.control.AbstractDianaEditor;
import org.openflexo.fge.control.tools.DianaToolSelector;

import com.vaadin.ui.Panel;


public class VDianaToolSelector extends DianaToolSelector<Panel, VaadinViewFactory> {

	private Panel component;
	public VDianaToolSelector(
			AbstractDianaEditor<?, VaadinViewFactory, ?> editor) {
		super(editor);
		component = new Panel();
		component.setCaption("hello world");
	}

	@Override
	public VaadinViewFactory getDianaFactory() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Panel getComponent() {
		// TODO Auto-generated method stub
		return component;
	}

	@Override
	public void handleToolChanged() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleToolOptionChanged() {
		// TODO Auto-generated method stub
		
	}

}
