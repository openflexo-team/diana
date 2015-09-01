package org.openflexo.fge.vaadin.view;

import java.beans.PropertyChangeEvent;
import java.util.List;

import org.openflexo.fge.control.AbstractDianaEditor;
import org.openflexo.fge.control.tools.DianaPalette;
import org.openflexo.fge.graphics.FGEGraphics;
import org.openflexo.fge.view.FGEContainerView;
import org.openflexo.fge.view.FGEView;

import com.vaadin.ui.AbstractComponent;

// TODO : Decline vaadin Canvas into LayeredCanvas
public abstract class VDianaLayeredView<O> implements FGEContainerView<O, AbstractComponent>, VFGEView<O, AbstractComponent> {

	@Override
	public AbstractDianaEditor<?, ?, ? super AbstractComponent> getController() {
		return getDrawingView().getController();
	}

	@Override
	public abstract VDrawingView<?> getDrawingView();

	@Override
	public O getDrawable() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FGEGraphics getFGEGraphics() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double getScale() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void rescale() {
		// TODO Auto-generated method stub

	}

	@Override
	public void activatePalette(DianaPalette<?, ?> aPalette) {
		// TODO Auto-generated method stub

	}

	@Override
	public void delete() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isDeleted() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void stopLabelEdition() {
		// TODO Auto-generated method stub

	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addView(FGEView<?, ?> view) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeView(FGEView<?, ?> view) {
		// TODO Auto-generated method stub

	}

	@Override
	public <RC> List<FGEView<?, ? extends RC>> getChildViews() {
		// TODO Auto-generated method stub
		return null;
	}

}
