package org.openflexo.diana.vaadin.control.tools;

import com.vaadin.ui.AbstractComponentContainer;

import org.openflexo.diana.vaadin.VaadinViewFactory;
import org.openflexo.fge.control.AbstractDianaEditor;
import org.openflexo.fge.control.tools.DianaScaleSelector;

public class VDianaScaleSelector extends DianaScaleSelector<AbstractComponentContainer, VaadinViewFactory> {

	public VDianaScaleSelector(
			AbstractDianaEditor<?, VaadinViewFactory, ?> editor) {
		super(editor);
	}

	@Override
	public VaadinViewFactory getDianaFactory() {
		return VaadinViewFactory.INSTANCE;
	}

	@Override
	public AbstractComponentContainer getComponent() {
		return null;
	}

	@Override
	public void handleScaleChanged() {
		
	}

}
