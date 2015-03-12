package org.openflexo.diana.vaadin;

import javax.servlet.annotation.WebServlet;

import org.openflexo.fge.Drawing;
import org.openflexo.fge.FGEModelFactory;
import org.openflexo.fge.FGEModelFactoryImpl;
import org.openflexo.fge.control.DianaToolFactory;
import org.openflexo.diana.vaadin.VaadinViewFactory;
import org.openflexo.diana.vaadin.control.VaadinToolFactory;
import org.openflexo.diana.vaadin.control.tools.VDianaScaleSelector;
import org.openflexo.diana.vaadin.tests.GraphDrawing1;
import org.openflexo.diana.vaadin.tests.TestGraph;
import org.openflexo.diana.vaadin.tests.TestGraphNode;
import org.openflexo.model.exceptions.ModelDefinitionException;

import com.google.gwt.user.client.ui.LayoutPanel;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
@Theme("diana_vaadin")
public class Diana_vaadinUI extends UI {

	private VaadinViewFactory viewfactory;
	
	@WebServlet(value = "/*", asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = Diana_vaadinUI.class, widgetset = "org.openflexo.diana.vaadin.widgetset.Diana_vaadinWidgetset")
	public static class Servlet extends VaadinServlet {
	}

	@Override
	protected void init(VaadinRequest request) {
		final VerticalLayout main = new VerticalLayout();
		final VerticalLayout layout = new VerticalLayout();
		final HorizontalLayout Menu = new HorizontalLayout();
		layout.setMargin(true);
		setContent(main);

		Button Editbutton = new Button("Edit");
		Editbutton.addClickListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				
			}
		});
		Menu.addComponent(Editbutton);
		
		Button Addbutton = new Button("Add");
		Addbutton.addClickListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				
			}
		});
		Menu.addComponent(Addbutton);
		main.addComponent(Menu);
		main.addComponent(layout);
		
		final GraphDrawing1 d = makeDrawing();
		System.out.println("get the reference of GraphDrawing");
		
		final TestDrawingController dc = new TestDrawingController(d);
		//dc.getDrawingView().setName("[NO_CACHE]");
		layout.addComponent((AbstractComponent) dc.getDrawingView());
		
	}
	
	public static GraphDrawing1 makeDrawing() {
		FGEModelFactory factory = null;
		try {
			factory = new FGEModelFactoryImpl();
		} catch (ModelDefinitionException e) {
			e.printStackTrace();
		}
		/*TestGraph graph = new TestGraph(){};
		TestGraphNode node1 = new TestGraphNode("node1", graph);
		TestGraphNode node2 = new TestGraphNode("node2", graph);
		TestGraphNode node3 = new TestGraphNode("node3", graph);
		node1.connectTo(node2);
		node1.connectTo(node3);
		node3.connectTo(node2);
		GraphDrawing1 returned = new GraphDrawing1(graph, factory);
		returned.printGraphicalObjectHierarchy();
		return returned;*/
		return null;
	}
	
	public static class TestDrawingController extends VDianaInteractiveEditor<TestGraph>{

		//private final JPopupMenu contextualMenu;
		//private final VDianaScaleSelector scaleSelector;

		public TestDrawingController(GraphDrawing1 aDrawing) {
			
			super(aDrawing, aDrawing.getFactory(), VaadinViewFactory.INSTANCE, VaadinToolFactory.DEFAULT);
			System.out.println("Pb in the VDianaInteractiveEditor");
			System.out.println(VaadinViewFactory.INSTANCE);
			//scaleSelector = (VDianaScaleSelector) getToolFactory().makeDianaScaleSelector(this);
			//contextualMenu = new PopupMenu();
			//contextualMenu.add(new MenuItem("Item"));
		}
		
	}
	


}