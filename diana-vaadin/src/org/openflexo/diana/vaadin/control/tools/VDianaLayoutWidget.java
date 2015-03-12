package org.openflexo.diana.vaadin.control.tools;

import javax.swing.JToolBar;

import org.openflexo.diana.vaadin.VaadinViewFactory;
import org.openflexo.fge.control.tools.DianaLayoutWidget;

import com.google.gwt.user.client.ui.TabBar;
import com.vaadin.ui.MenuBar;

public class VDianaLayoutWidget extends DianaLayoutWidget<MenuBar, VaadinViewFactory>{

	MenuBar component;
	public VDianaLayoutWidget() {
		super();
		component = new MenuBar();
		component.setCaption("DianaLayoutWidget");;
	}
	@Override
	public VaadinViewFactory getDianaFactory() {
		// TODO Auto-generated method stub
		return VaadinViewFactory.INSTANCE;
	}

	@Override
	public MenuBar getComponent() {
		// TODO Auto-generated method stub
		return component;
	}

}
