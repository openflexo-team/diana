package org.openflexo.diana.vaadin.view;

import javax.servlet.annotation.WebServlet;

import org.openflexo.diana.vaadin.control.VaadinToolFactory;
import org.openflexo.diana.vaadin.view.VDianaInteractiveEditor;
import org.openflexo.diana.vaadin.view.VaadinViewFactory;
import org.openflexo.diana.vaadin.tests.GraphDrawing1;
import org.openflexo.diana.vaadin.tests.TestGraph;
import org.openflexo.diana.vaadin.tests.TestGraphNode;
import org.openflexo.fge.FGEModelFactory;
import org.openflexo.fge.FGEModelFactoryImpl;
import org.openflexo.model.exceptions.ModelDefinitionException;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
@Theme("diana_vaadin")
public class Diana_vaadinUI extends UI {

	@WebServlet(value = "/*", asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = Diana_vaadinUI.class, widgetset = "org.openflexo.diana.vaadin.view.widgetset.Diana_vaadinWidgetset")
	public static class Servlet extends VaadinServlet {
	}

	@Override
	protected void init(VaadinRequest request) {
		final VerticalLayout layout = new VerticalLayout();
		final HorizontalLayout Menu = new HorizontalLayout();
		layout.setMargin(true);
		setContent(layout);

		final GraphDrawing1 d = makeDrawing();
		final TestDrawingController dc = new TestDrawingController(d);
		//dc.getDrawingView().GetRPC();
		//System.out.print("dc DrawingView" + dc.getDrawingView());
		((VDrawingView<?>)dc.getDrawingView()).paintComponent();
		
		Button button1 = new Button("CreateView");
		button1.addClickListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				if(dc.getDrawingView().getPaintManager().mode_Rectangle == true){
					dc.getDrawingView().getPaintManager().mode_Rectangle = false;
					dc.getDrawingView().getPaintManager().mode_Delete = false;
					dc.getDrawingView().getPaintManager().mode_Deplacement=false;
					dc.getDrawingView().getPaintManager().mode_Link=false;
				}
				else{
					dc.getDrawingView().getPaintManager().mode_Rectangle = true;
					dc.getDrawingView().getPaintManager().mode_Delete = false;
					dc.getDrawingView().getPaintManager().mode_Deplacement=false;
					dc.getDrawingView().getPaintManager().mode_Link=false;
				}
			}
		});
		
		Button button2 = new Button("DeleteView");
		Button button3 = new Button("RemoveView");
		Button button4 = new Button("ClearViews");
		Button button5 = new Button("LinkViews");

button3.addClickListener(new Button.ClickListener() {		
		/**
		 * 
		 */
		private static final long serialVersionUID = -2320988092773531233L;

		@Override
		public void buttonClick(ClickEvent event) {
			if(dc.getDrawingView().getPaintManager().mode_Deplacement == true){
				dc.getDrawingView().getPaintManager().mode_Link=false;
				dc.getDrawingView().getPaintManager().mode_Deplacement=false;
				dc.getDrawingView().getPaintManager().mode_Rectangle=false;
				dc.getDrawingView().getPaintManager().mode_Delete=false;
			}
			else{
				dc.getDrawingView().getPaintManager().mode_Link=false;
				dc.getDrawingView().getPaintManager().mode_Rectangle=false;
				dc.getDrawingView().getPaintManager().mode_Delete=false;
				dc.getDrawingView().getPaintManager().mode_Deplacement=true;
					
			}
			
		}
	});
	
	button2.addClickListener(new Button.ClickListener() {
		/**
		 * 
		 */
		private static final long serialVersionUID = -1486007131889061378L;

		/**
		 * 
		 */

		public void buttonClick(ClickEvent event) {
				if(dc.getDrawingView().getPaintManager().mode_Delete==true){
					dc.getDrawingView().getPaintManager().mode_Delete=false;
					dc.getDrawingView().getPaintManager().mode_Deplacement=false;
					dc.getDrawingView().getPaintManager().mode_Rectangle=false;
					dc.getDrawingView().getPaintManager().mode_Link=false;
				}
				else{
					dc.getDrawingView().getPaintManager().mode_Delete=true;
					dc.getDrawingView().getPaintManager().mode_Rectangle=false;
					dc.getDrawingView().getPaintManager().mode_Link=false;
					dc.getDrawingView().getPaintManager().mode_Deplacement=false;
				}
		}
	});
	
	button5.addClickListener(new Button.ClickListener() {

		public void buttonClick(ClickEvent event) {
				if(dc.getDrawingView().getPaintManager().mode_Link==true){
					dc.getDrawingView().getPaintManager().mode_Delete=false;
					dc.getDrawingView().getPaintManager().mode_Deplacement=false;
					dc.getDrawingView().getPaintManager().mode_Rectangle=false;
					dc.getDrawingView().getPaintManager().mode_Link=false;
				}
				else{
					dc.getDrawingView().getPaintManager().mode_Delete=false;
					dc.getDrawingView().getPaintManager().mode_Rectangle=false;
					dc.getDrawingView().getPaintManager().mode_Link=true;
					dc.getDrawingView().getPaintManager().mode_Deplacement=false;
				}
		}
	});
	
	button4.addClickListener(new Button.ClickListener() {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1696479532047276895L;

		@Override
		public void buttonClick(ClickEvent event) {
			dc.getDrawingView().getPaintManager()._drawingView.clear(); 
			dc.getDrawingView().getPaintManager()._drawingView.graphics.applyCurrentBackgroundStyle();
			dc.getDrawingView().getPaintManager().mode_Link=false;
			dc.getDrawingView().getPaintManager().mode_Rectangle=false;
			dc.getDrawingView().getPaintManager().mode_Delete=false;
			dc.getDrawingView().getPaintManager().mode_Deplacement=false;	
		}
	});
		
		
		Menu.addComponent(button1);
		Menu.addComponent(button2);
		Menu.addComponent(button3);
		Menu.addComponent(button4);
		Menu.addComponent(button5);
		layout.addComponent(Menu);
		layout.addComponent(dc.getDrawingView());
	}
	public static GraphDrawing1 makeDrawing() {
		FGEModelFactory factory = null;
		try {
			factory = new FGEModelFactoryImpl();
		} catch (ModelDefinitionException e) {
			e.printStackTrace();
		}
		TestGraph graph = new TestGraph(){};
		TestGraphNode node1 = new TestGraphNode("node1", graph);
		TestGraphNode node2 = new TestGraphNode("node2", graph);
		TestGraphNode node3 = new TestGraphNode("node3", graph);
		node1.connectTo(node2);
		node1.connectTo(node3);
		node3.connectTo(node2);
		GraphDrawing1 returned = new GraphDrawing1(graph, factory);
		System.out.println("returned is: " + returned);
		returned.printGraphicalObjectHierarchy();
		return returned;
	}
	
	public static class TestDrawingController extends VDianaInteractiveEditor<TestGraph>{

		public TestDrawingController(GraphDrawing1 aDrawing) {
			super(aDrawing, aDrawing.getFactory(), VaadinViewFactory.INSTANCE, VaadinToolFactory.DEFAULT);
			//System.out.println(VaadinViewFactory.INSTANCE);
		}
	}
}