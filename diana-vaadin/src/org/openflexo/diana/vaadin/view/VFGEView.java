package org.openflexo.diana.vaadin.view;

import org.openflexo.diana.vaadin.view.widgetset.client.vdrawingview.VDrawingViewClientRpc;
import org.openflexo.fge.view.FGEView;

import com.vaadin.ui.AbstractComponent;

	/**
	 * Implemented by all Vaadin views
	 * 
	 * @author peiqi
	 * 
	 * @param <O>
	 *            type of object represented by this view
	 * @param <C>
	 *            type of component this view is instanced (at least a AbstractComponent)
	 */
public interface VFGEView<O, C extends AbstractComponent> extends FGEView<O, C> {

		public VDrawingView<?> getDrawingView();

		public void paintComponent();
		public void setRPC(VDrawingViewClientRpc rpc);
		public VDrawingViewClientRpc getRPC();
	}


