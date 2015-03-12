package org.openflexo.diana.vaadin.view;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import org.openflexo.diana.vaadin.view.VFGEView;
import org.openflexo.fge.view.FGEContainerView;
import org.openflexo.fge.view.FGEView;

import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.CustomLayout;

// need to extends a class like JLayeredPane fonctionnality;
public abstract class VDianaLayeredView<O> extends AbsoluteLayout implements FGEContainerView<O, AbsoluteLayout>, VFGEView<O, AbsoluteLayout>  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final List<FGEView<?, ? extends AbstractComponent>> childViews;
	
	public VDianaLayeredView() {
		super();
		childViews = new ArrayList<FGEView<?, ? extends AbstractComponent>>();
	}
	
	@Override
	public abstract VDrawingView<?> getDrawingView();
	
	@Override
	public void addView(FGEView<?, ?> view) {
		if (view instanceof VShapeView) {
		addComponent((com.vaadin.ui.Component) ((VShapeView<?>) view).getLabelView());
	}

	}
}
