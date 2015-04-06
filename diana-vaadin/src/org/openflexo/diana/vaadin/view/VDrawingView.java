package org.openflexo.diana.vaadin.view;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.fge.Drawing;
import org.openflexo.fge.DrawingGraphicalRepresentation;
import org.openflexo.fge.Drawing.ConnectorNode;
import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.Drawing.GeometricNode;
import org.openflexo.fge.Drawing.ShapeNode;
import org.openflexo.fge.control.AbstractDianaEditor;
import org.openflexo.fge.control.DianaInteractiveEditor;
import org.openflexo.fge.control.DianaInteractiveViewer;
import org.openflexo.fge.control.DianaInteractiveEditor.EditorTool;
import org.openflexo.fge.control.actions.RectangleSelectingAction;
import org.openflexo.fge.control.tools.DianaPalette;
import org.openflexo.fge.cp.ControlArea;
import org.openflexo.fge.graphics.FGEDrawingGraphics;
import org.openflexo.fge.graphics.FGEGraphics;
import org.openflexo.fge.impl.FGECachedModelFactory;
import org.openflexo.fge.view.ConnectorView;
import org.openflexo.fge.view.DrawingView;
import org.openflexo.fge.view.FGEContainerView;
import org.openflexo.fge.view.FGEView;
import org.openflexo.fge.view.ShapeView;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.diana.vaadin.graphics.VDianaDrawingGraphics;
import org.openflexo.diana.vaadin.graphics.VDianaGeometricGraphics;
import org.openflexo.diana.vaadin.graphics.VDianaGraphics;
import org.openflexo.diana.vaadin.graphics.formes.Line;
import org.openflexo.diana.vaadin.graphics.formes.Rectangle;
import org.openflexo.diana.vaadin.paint.VDianaPaintManager;
import org.openflexo.diana.vaadin.view.VaadinViewFactory;
import org.openflexo.diana.vaadin.view.widgetset.client.vdrawingview.VDrawingViewClientRpc;
import org.openflexo.diana.vaadin.view.widgetset.client.vdrawingview.VDrawingViewServerRpc;

import com.vaadin.server.Sizeable.Unit;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.ui.AbstractComponent;

/**
 * The VDrawingView is the VAADIN implementation of the root pane of a FGE graphical editor<br>
 * The managing of the VDrawingView is performed by the {@link AbstractDianaEditor}.
 * 
 * @author Peiqi SHI
 * 
 * @param <M>
 *            the type of represented model
 */

public class VDrawingView<M> extends VDianaBasedView<M> implements DrawingView<M, AbstractComponent> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -590665961699071193L;
	private static final Logger logger = Logger.getLogger(VDrawingView.class.getPackage().getName());
	private final Drawing<M> drawing;	
	private final AbstractDianaEditor<M, VaadinViewFactory, AbstractComponent> controller;
	private RectangleSelectingAction _rectangleSelectingAction;
	private boolean isDeleted = false;
	private final VDianaPaintManager _paintManager;
	protected final VDianaDrawingGraphics graphics;
	//public VDrawingViewClientRpc drpc;
	protected final VShapeView _shapeview;
	private static FGECachedModelFactory PAINT_FACTORY = null;

	private long cumulatedRepaintTime = 0;
	private boolean isBuffering = false;
	private boolean bufferingHasBeenStartedAgain = false;

	static {
		try {
			PAINT_FACTORY = new FGECachedModelFactory();
		} catch (ModelDefinitionException e) {
			logger.severe(e.getMessage());
			e.printStackTrace();
		}
	}

	// corresponding to the Listener
	private final ArrayList<VDrawingViewClickListener> clickListeners = new ArrayList<VDrawingViewClickListener>();
	private final ArrayList<VDrawingViewPressListener> pressListeners = new ArrayList<VDrawingViewPressListener>();
	private LinkedList<Rectangle> rectangles;
	private LinkedList<Rectangle> Srectangles;
	private LinkedList<Line> links;
	private LinkedList<Line> Coplinks;
	private LinkedList<Rectangle> Coprectangles;
	private Integer idR;
	private Integer idR2;
	private Integer idL;

	//public VDrawingViewClientRpc rpc = getRpcProxy(VDrawingViewClientRpc.class);
	
	public VDrawingView(){
		//shapeview = null;
		controller = null;
		drawing = null;
		graphics = null;
		_paintManager = null;
		idR=0;
		idL=0;
		idR2=0;
		//drpc = rpc;
		links = new LinkedList<Line>();
		Coplinks = new LinkedList<Line>();
		rectangles = new LinkedList<Rectangle>();
		Srectangles = new LinkedList<Rectangle>();
		Coprectangles = new LinkedList<Rectangle>();
		_shapeview = null;
		registerRpc(new VDrawingViewServerRpc() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public void clicked(MouseEventDetails med) {
				fireClicked(med);
			}

			public void pressed(MouseEventDetails med) {
				firePressed(med);
			}
		});
		//System.out.println("in the VVVV the rpc is " + rpc);
	}

	public VDrawingView(AbstractDianaEditor<M, VaadinViewFactory, AbstractComponent> controller) {
		super();
		System.out.println("in the VDrawingView XXXX the rpc is " + rpc);
		this.controller = controller;
		drawing = controller.getDrawing();
		//shapeview = new VShapeView();
		drawing.getRoot().getGraphicalRepresentation().updateBindingModel();
		//staticRpc = rpc;
		graphics = new VDianaDrawingGraphics(drawing.getRoot(), this);
		setWidth(24, Unit.CM);
		setHeight(13, Unit.CM);
		//graphics.applyCurrentBackgroundStyle();
		//paintComponent();
		setRPC(rpc);
		drawing.getRoot().getPropertyChangeSupport().addPropertyChangeListener(this);
		_shapeview = new VShapeView();
		_paintManager = new VDianaPaintManager(this);
		_paintManager.setRPC(rpc);
		//_shapeview.setRPC(rpc);
		for (DrawingTreeNode<?, ?> dtn : drawing.getRoot().getChildNodes()) {
			if (dtn instanceof GeometricNode<?>) {
				((GeometricNode<?>) dtn).getPropertyChangeSupport().addPropertyChangeListener(this);
			}
		}
		idR=0;
		idR2=0;
		idL=0;
		links = new LinkedList<Line>();
		Coplinks = new LinkedList<Line>();
		rectangles = new LinkedList<Rectangle>();
		Srectangles = new LinkedList<Rectangle>();
		Coprectangles = new LinkedList<Rectangle>();
		registerRpc(new VDrawingViewServerRpc() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public void clicked(MouseEventDetails med) {
				fireClicked(med);
			}

			public void pressed(MouseEventDetails med) {
				firePressed(med);
			}
		});
	}

	/**
	 * Adds the CanvasClickListener.
	 * 
	 * @param listener
	 *            the listener
	 */
	public void addListener(VDrawingViewClickListener listener) {
		if (!clickListeners.contains(listener)) {
			clickListeners.add(listener);
		}
	}

	/**
	 * Removes a CanvasClickListener.
	 * 
	 * @param listener
	 *            the listener
	 */
	public void removeListener(VDrawingViewClickListener listener) {
		if (clickListeners.contains(listener)) {
			clickListeners.remove(listener);
		}
	}

	/**
	 * Adds the CanvasClickListener.
	 * 
	 * @param listener
	 *            the listener
	 */
	public void addListener(VDrawingViewPressListener listener) {
		if (!pressListeners.contains(listener)) {
			pressListeners.add(listener);
		}
	}

	public void removeListener(VDrawingViewPressListener listener) {
		if (!pressListeners.contains(listener)) {
			pressListeners.remove(listener);
		}
	}

	private void fireClicked(MouseEventDetails med) {
		for (VDrawingViewClickListener listener : clickListeners) {
			listener.clicked(med);
		}
	}

	private void firePressed(MouseEventDetails med) {
		for (VDrawingViewPressListener listener : pressListeners) {
			listener.pressed(med);
		}
	}

	/**
	 * The listener interface for receiving canvasClick events. The class that
	 * is interested in processing a canvasClick event implements this
	 * interface, and the object created with that class is registered with a
	 * component using the component's
	 * <code>addCanvasClickListener<code> method. When
	 * the canvasClick event occurs, that object's appropriate
	 * method is invoked.
	 * 
	 * @see CanvasClickEvent
	 */
	public interface VDrawingViewClickListener {

		/**
		 * A mouse was clicked in a canvas.
		 * 
		 * @param med
		 *            the mouse event details
		 */
		public void clicked(MouseEventDetails med);
	}

	public interface VDrawingViewPressListener {

		/**
		 * A mouse was clicked in a canvas.
		 * 
		 * @param med
		 *            the mouse event details
		 */
		public void pressed(MouseEventDetails med);
	}
	
	public void paintControlArea(ControlArea<?> ca, VDianaDrawingGraphics graphics) {
		//Rectangle invalidatedArea = ca.paint(graphics);
	}

	@Override
	public DrawingTreeNode<M, ?> getNode() {
		return drawing.getRoot();
	}

	@Override
	public VDrawingView<?> getDrawingView() {		
		return this;
	}

	public VaadinEditorDelegate getDelegate() {
		return (VaadinEditorDelegate) getController().getDelegate();
	}

	@Override
	public FGEContainerView<?, ?> getParentView() {

		return null;
	}

	@Override
	public double getScale() {
		return getController().getScale();
	}

	public DrawingGraphicalRepresentation getGraphicalRepresentation() {
		return drawing.getRoot().getGraphicalRepresentation();
	}

	@Override
	public void activatePalette(DianaPalette<?, ?> aPalette) {
		// TODO Auto-generated method stub

	}

	public LinkedList<Rectangle> getrectangles(){

		return rectangles;
	}

	public LinkedList<Line> getLinks(){

		return links;
	}

	@Override
	public boolean isDeleted() {
		return false;
	}

	// complicated to implement
	@Override
	public void propertyChange(final PropertyChangeEvent evt) {
		// TODO Auto-generated method stub

	}

	@Override
	public M getDrawable() {
		return getModel();
	}
	public M getModel() {
		return drawing.getModel();
	}

	@Override
	public AbstractDianaEditor<M, ?, ? super AbstractComponent> getController() {
		return controller;
	}
	@Override
	public boolean contains(FGEView<?, ?> view) {
		if (view == null) {
			return false;
		}
		if (view == this) {
			return true;
		}
		if (((AbstractComponent) view).getParent() != null && ((AbstractComponent) view).getParent() instanceof FGEView) {
			return contains((FGEView<?, ?>) ((AbstractComponent) view).getParent());
		}
		return false;
	}

	@Override
	public Drawing<M> getDrawing() {
		return drawing;
	}

	@Override
	public <O> FGEView<O, ?> viewForNode(DrawingTreeNode<O, ?> node) {
		return (VFGEView<O, ? extends AbstractComponent>) controller.viewForNode(node);
	}

	@Override
	public <O> ConnectorView<O, ?> connectorViewForNode(ConnectorNode<O> node) {
		return (VConnectorView<O>) viewForNode(node);
	}

	@Override
	public <O> ShapeView<O, ?> shapeViewForNode(ShapeNode<O> node) {
		return (VShapeView<O>) viewForNode(node);
	}

	@Override
	public void setRectangleSelectingAction(RectangleSelectingAction action) {
		_rectangleSelectingAction = action;

	}

	@Override
	public void resetRectangleSelectingAction() {
		_rectangleSelectingAction = null;
		//getPaintManager().repaint(this);
	}

	@Override
	public VDianaPaintManager getPaintManager() {
		return _paintManager;
	}

	@Override
	public FGEDrawingGraphics getFGEGraphics() {
		// TODO Auto-generated method stub
		return null;
	}

	// a must to implement
	public synchronized void paint() {
		if (isDeleted()) {
			return;
		}
		long startTime = System.currentTimeMillis();
		if (getPaintManager().isPaintingCacheEnabled()) {
			if (isBuffering) {
				// Buffering painting
				if (VDianaPaintManager.paintPrimitiveLogger.isLoggable(Level.FINE)) {
					VDianaPaintManager.paintPrimitiveLogger.fine("JDrawingView: Paint for image buffering area, clip=");
				}
				//super.paint(graphics, controller);
				//rpc.drawArc(2, 2, 3, 0, 360);
				if (bufferingHasBeenStartedAgain) {
					rpc.clear();
					//g.clearRect(0, 0, drawing.getRoot().getViewWidth(getController().getScale()),
					//	drawing.getRoot().getViewHeight(getController().getScale()));
					//super.paint(graphics,controller);
					//rpc.drawArc(2, 2, 3, 0, 360);
					bufferingHasBeenStartedAgain = false;
				}
			} else {
				if (getPaintManager().renderUsingBuffer(null, drawing.getRoot(), getScale())) {
					// Now, we still have to paint objects that are declared
					// to be temporary and continuougetPaintManagersly to be redrawn
					//forcePaintTemporaryObjects(drawing.getRoot(), g);
				} else {
					// Skip buffering and perform normal rendering
					//super.paint(g);
					//rpc.drawArc(2, 2, 3, 0, 360);
				}
				//paintCapturedNode(g);
				//rpc.drawArc(2, 2, 3, 0, 360);
			}
		} else {
			// Normal painting
			//super.paint(g);
			//rpc.drawArc(2, 2, 3, 0, 360);
		}

		paintGeometricObjects();
		//rpc.drawArc(2, 2, 3, 0, 360);

		if (!isBuffering) {

			rpc.createGraphics();
			graphics.createGraphics();

			if (getController() instanceof DianaInteractiveViewer) {
				// Don't paint those things in case of buffering
				for (DrawingTreeNode<?, ?> o : new ArrayList<DrawingTreeNode<?, ?>>(
						((DianaInteractiveViewer<?, ?, ?>) getController()).getFocusedObjects())) {
				}

				for (DrawingTreeNode<?, ?> o : new ArrayList<DrawingTreeNode<?, ?>>(
						((DianaInteractiveViewer<?, ?, ?>) getController()).getSelectedObjects())) {
					if (o.shouldBeDisplayed()) {
					}
				}
			}

			if (getController() instanceof DianaInteractiveEditor) {
				if (((DianaInteractiveEditor<?, ?, ?>) getController()).getCurrentTool() == EditorTool.DrawCustomShapeTool) {
					paintCurrentEditedShape(graphics);
				} else if (((DianaInteractiveEditor<?, ?, ?>) getController()).getCurrentTool() == EditorTool.DrawConnectorTool) {
					paintCurrentDrawnConnector(graphics);
				}
			}

			//graphics.releaseGraphics();

			if (_rectangleSelectingAction != null) {
				//_rectangleSelectingAction.paint(g, getController());
			}

		}

		// Do it once only !!!
		isBuffering = false;

		long endTime = System.currentTimeMillis();
		// System.out.println("END paint() in JDrawingView, this took "+(endTime-startTime)+" ms");

		cumulatedRepaintTime += endTime - startTime;

		if (VDianaPaintManager.paintStatsLogger.isLoggable(Level.FINE)) {
			VDianaPaintManager.paintStatsLogger.fine("PAINT " + " time=" + (endTime - startTime)
					+ "ms cumulatedRepaintTime=" + cumulatedRepaintTime + " ms");
		}
	}

	private void paintGeometricObjects() {

		rpc.createGraphics();

		List<GeometricNode<?>> geomList = new ArrayList<GeometricNode<?>>();
		for (Object n : drawing.getRoot().getChildNodes()) {
			if (n instanceof GeometricNode) {
				geomList.add((GeometricNode<?>) n);
			}
		}
		if (geomList.size() > 0) {
			Collections.sort(geomList, new Comparator<GeometricNode<?>>() {
				@Override
				public int compare(GeometricNode<?> o1, GeometricNode<?> o2) {
					return o1.getGraphicalRepresentation().getLayer() - o2.getGraphicalRepresentation().getLayer();
				}
			});
			for (GeometricNode<?> gn : geomList) {
				// TODO: use the same graphics, just change DrawingTreeNode
				VDianaGeometricGraphics geometricGraphics = new VDianaGeometricGraphics(gn, this);
				geometricGraphics.createGraphics(/*, controller*/);
				gn.paint(geometricGraphics);
				//geometricGraphics.releaseGraphics();
				geometricGraphics.delete();
			}
		}
	}

	@Override
	public void paintComponent() {
		//graphics.applyCurrentBackgroundStyle();
		//graphics.fillRect(100,100,40,30);
		//graphics.fillRect(300,300,40,50);
		//graphics.fillRect(600,200,50,40);
		getDrawing().getRoot().paint(graphics);
		for (DrawingTreeNode<?,?> child : getDrawing().getRoot().getChildNodes()){
			VFGEView view = (VFGEView)viewForNode(child);
			System.out.println("child="+child+" view="+view);
			//if (view != null) {
			//if (view instanceof VShapeView)
				view.setRPC(rpc);	
			    view.paintComponent();
			//}
		}
	}

	private void paintCurrentDrawnConnector(VDianaDrawingGraphics graphics) {

		if (!(getController() instanceof DianaInteractiveEditor)) {
			return;
		}
	}

	private void paintCurrentEditedShape(VDianaDrawingGraphics graphics) {
		if (!(getController() instanceof DianaInteractiveEditor)) {
			return;
		}	
	}
	public void fillCircle(double x, double y, double width,
			double height){
		graphics.drawArc(x, y, width, height, 0, 360);
	}
	public void fillRect(double startX, double startY, double width,
			double height) {
		graphics.fillRect(startX, startY, width, height);
		 //DrawingTreeNode<?, ?> newNode = null;//DrawingTreeNode<?, ?> ();
		 //super.handleNodeAdded(newNode);
		// replace by the handleDrawingTreeNode to create the drawing for the view 
		Rectangle rect =  new Rectangle(startX,startY,width,height);
		rect.setId(idR+1);
		idR++;
		rectangles.add(rect);
	}

	public void drawRect(double startX, double startY, double width,
			double height) {
		graphics.drawRect(startX, startY, width, height);
		Rectangle Srect =  new Rectangle(startX, startY,width,height);
		System.out.print("Srect is " + Srect);
		Srect.setId(idR+1);
		idR++;
		Srectangles.add(Srect);
	}

	public void drawArc(double x, double y, double width, double height,
			double angleStart, double arcAngle){
		graphics.drawArc(100.0, 90.0, 50.0,40.0, 0.0, Math.PI);
	}
	public void clear() {

		Srectangles.clear();
		rectangles.clear();
		links.clear();
		rpc.clear();

	}

	public void supprimerObjet(int id){
		Coplinks.clear();
		Coprectangles.clear();
		Iterator<Line> ite = links.iterator(); 
		while (ite.hasNext() ) {
			Line link = ite.next();
			if(link.getLine()[0]==id || link.getLine()[1]==id)
			{
				System.out.println("delete");
				ite.remove();
			}
		}
		Coplinks.addAll(links);

		Iterator<Rectangle> iterator = rectangles.iterator(); 
		while (iterator.hasNext() ) {
			Rectangle rect = iterator.next();
			if(rect.getId()==id)
			{
				iterator.remove();
			}
		}
		Coprectangles.addAll(rectangles);
		graphics.clearGraphics();
		graphics.applyCurrentBackgroundStyle();
		for(Rectangle rect: Coprectangles){
			rpc.fillRect(rect.coordinates()[0], rect.coordinates()[1], rect.coordinates()[2], rect.coordinates()[3]);
			rectangles.add(rect);
			rpc.setFillStyle("blue");
		}
		for(Line link:Coplinks){
			links.add(link);
			drawLink(link.getLine()[0], link.getLine()[1]);
		}
	}
	// used to draw the shapes in the contexts
	public void RmvRec(Integer Id_Rec, double startX, double startY) {
		Coprectangles.clear();
		Coplinks.clear();
		Iterator<Rectangle> iterator = rectangles.iterator(); 
		while (iterator.hasNext() ) {
			Rectangle rect = iterator.next();
			//for(rectangle rect:rectangles){
			if(rect.getId()==Id_Rec)
			{
				iterator.remove();
				Rectangle Rrect =  new Rectangle(startX,startY,20,20);
				Rrect.setId(Id_Rec);
				Coprectangles.add(Rrect);
			}
		}
		Iterator<Line> ite = links.iterator(); 
		while (ite.hasNext() ) {
			Line link = ite.next();
			if(link.getLine()[0]==Id_Rec || link.getLine()[1]==Id_Rec)
			{
				ite.remove();
			}
		}
		Coplinks.addAll(links);

		Coprectangles.addAll(rectangles);
		graphics.clearGraphics();
		graphics.applyCurrentBackgroundStyle();	
		for(Rectangle rect: Coprectangles){
			graphics.fillRect(rect.coordinates()[0], rect.coordinates()[1], rect.coordinates()[2], rect.coordinates()[3]);
			rectangles.add(rect);
			graphics.setFillStyle("blue");
		}
		for(Line link:Coplinks){
			links.add(link);
			drawLink(link.getLine()[0], link.getLine()[1]);
		}
	}

	public void drawLink(Integer id1, Integer id2){
		graphics.beginPath();
		moveTo(searchrectangleById(id1).getCentre()[0], searchrectangleById(id1).getCentre()[1]);
		lineTo(searchrectangleById(id2).getCentre()[0], searchrectangleById(id2).getCentre()[1]);
		System.out.print("ddfddddddddddddddddddddddddddd"+searchrectangleById(id1).getCentre()[0] + searchrectangleById(id1).getCentre()[1]);
		System.out.print("ddfddddddddddddddddddddddddddd"+searchrectangleById(id2).getCentre()[0] + searchrectangleById(id2).getCentre()[1]);
		Line link = new Line(id1,id2);
		link.setId(idL+1);
		idL++;
		links.add(link);
		stroke();
	}

	public void moveTo(double x, double y) {
		rpc.moveTo(x, y);
	}
	public void lineTo(double x, double y) {
		rpc.lineTo(x, y);
	}
	public void stroke() {
		rpc.stroke();
	}

	public Rectangle searchrectangleById(Integer id){

		for(Rectangle rect:rectangles){
			if(rect.getId()==id)

				return rect;
		}
		return null;

	}

	public Line searchLinkById(Integer id){
		Line result;
		result = null;
		for(Line link:links){
			if(link.getId()==id)
				result=link;
			return result;
		}
		return result;

	}

	public void printLinks(){
		for(Line link:links){
			link.getLine();
		}
	}
	@Override
	public <RC> List<FGEView<?, ? extends RC>> getChildViews() {
		// TODO Auto-generated method stub
		return null;
	}

	public void SetRPC(VDrawingViewClientRpc rpc){
		this.rpc = rpc;
	}
	
	public VDrawingViewClientRpc GetRPC(){
		return rpc;
	}

	@Override
	public void setRPC(VDrawingViewClientRpc rpc) {
		this.graphics.setRPC(rpc);		
	}

	@Override
	public VDrawingViewClientRpc getRPC() {
		// TODO Auto-generated method stub
		return rpc;
	}

}

