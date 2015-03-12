package org.openflexo.diana.vaadin.paint;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.fge.FGEUtils;
import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.Drawing.ShapeNode;
import org.openflexo.fge.control.AbstractDianaEditor;
//import org.openflexo.fge.shapes.Rectangle;
import org.openflexo.fge.view.FGEView;
import org.openflexo.fge.view.ShapeView;
import org.openflexo.diana.vaadin.graphics.formes.Rectangle;
import org.openflexo.diana.vaadin.view.VDrawingView;
import org.openflexo.diana.vaadin.view.VFGEView;
import org.openflexo.diana.vaadin.view.widgetset.client.vdrawingview.VDrawingViewClientRpc;

import com.google.gwt.dom.client.ImageElement;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Button;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.Button.ClickEvent;

// how to implement a bufferDrawingView in Vaaind ???
public class VDianaPaintManager {
	
	private static final Logger logger = Logger.getLogger(VDianaPaintManager.class.getPackage().getName());
	private final VDrawingView<Object> Delegate = new VDrawingView<Object>();
	public static final Logger paintPrimitiveLogger = Logger.getLogger("PaintPrimitive");
	public static final Logger paintRequestLogger = Logger.getLogger("PaintRequest");
	public static final Logger paintStatsLogger = Logger.getLogger("PaintStats");

	private boolean _paintingCacheEnabled;
	private ImageElement _paintBuffer;
	// private static final int DEFAULT_IMAGE_TYPE = ImageElement.TYPE_INT_RGB;

	
	static {
		initFGERepaintManager();
		/* Debug purposes
		paintPrimitiveLogger.setLevel(Level.FINE);
		paintRequestLogger.setLevel(Level.FINE);
		paintStatsLogger.setLevel(Level.FINE);
		*/
	}
	private static final boolean ENABLE_CACHE_BY_DEFAULT = true;
	public final VDrawingView<?> _drawingView;
	private final HashSet<DrawingTreeNode<?, ?>> _temporaryObjects;
	public boolean mode_Rectangle;
	public boolean mode_Delete;
	public boolean mode_Link;
	public boolean mode_Deplacement;
	private Integer[] temp = new Integer[2];
	private int flag = 0;
	private int tmp = -1;
	private final Button buttonUpdate = new Button("Update");
	private final Button buttonDelete = new Button("Supprimer");
	Button buttonClear = new Button("CLEAR");
	Button buttonLink = new Button("Link");
	Button buttonDeplacement = new Button("Deplacement");
	
	public VDianaPaintManager(VDrawingView<?> drawingView) {
		super();
		_drawingView = drawingView;
		_temporaryObjects = new HashSet<DrawingTreeNode<?, ?>>();
		if (ENABLE_CACHE_BY_DEFAULT) {
			enablePaintingCache();
		} else {
			disablePaintingCache();
		}
		//System.out.print("VD _drawingView" + _drawingView);
		 _drawingView.addListener(new VDrawingView.VDrawingViewClickListener() {
				@Override
				public void clicked(MouseEventDetails med) {
					System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxx");
					LinkedList<Rectangle> rectangles = _drawingView.getrectangles();
					int idRect=-1;
					for(Rectangle rect: rectangles){
						if(med.getRelativeX()>rect.coordinates()[0] && med.getRelativeX()<rect.coordinates()[0]+rect.coordinates()[2]){
							if(med.getRelativeY()>rect.coordinates()[1] && med.getRelativeY()<rect.coordinates()[1]+rect.coordinates()[3]){
								idRect=rect.getId();
								//name.setValue(Integer.toString(idRect));
								//width.setValue(Double.toString(_drawingView.searchrectangleById(idRect).coordinates()[2]));
								//height.setValue(Double.toString(_drawingView.searchrectangleById(idRect).coordinates()[3]));

							}
						}
					}	
					if( mode_Rectangle==true && idRect==-1 && mode_Delete==false){
						//_drawingView.drawRect(med.getRelativeX(), med.getRelativeY(), 20, 20);
						//_drawingView.drawArc(med.getRelativeX(), med.getRelativeY(), 20, 20,20,20);
						System.out.println("qqqqqqqqqqqqqqqqqqqqqqqq");
						_drawingView.fillRect(med.getRelativeX(), med.getRelativeY(), 20, 20);
						//System.out.print("VD _drawingViewdddddddddd " + _drawingView);
					}
					
					/*if(idRect==-1 && mode_Delete==true){
						mode_Delete=false;
					}*/
					if(idRect!=-1 && mode_Delete==true){
						
						_drawingView.supprimerObjet(idRect);		
					}
					if(mode_Link==true && idRect!=-1 && temp[0] != 0 ){
						//System.out.print("VD _drawingViewddddddddddlinklinklinklink " + _drawingView);
						temp[1]=idRect;
						_drawingView.drawLink(temp[0], temp[1]);
						//_drawingView.fillRect(med.getRelativeX(), med.getRelativeY(), 20, 20);
						System.out.print("enterrrrrrrrrrrrrrrrrrrrrrrr \n" + temp[0] + temp[1] +"\n");
						temp[0]=0;
						temp[1]=0;
					}
					
					if(mode_Link==true & idRect!=-1 && temp[0]==0){
						temp[0]=0;
						temp[1]=0;
					}
				}	
	        });   
		 
		   _drawingView.addListener(new VDrawingView.VDrawingViewPressListener() {
			   @Override
				public void pressed(MouseEventDetails med) {
					LinkedList<Rectangle> rectangles = _drawingView.getrectangles();
					int idRect;
					idRect=-1;
					for(Rectangle rect: rectangles){
						if(med.getRelativeX()>rect.coordinates()[0] && med.getRelativeX()<rect.coordinates()[0]+rect.coordinates()[2]){
							if(med.getRelativeY()>rect.coordinates()[1] && med.getRelativeY()<rect.coordinates()[1]+rect.coordinates()[3]){
								idRect=rect.getId();
								tmp = idRect;
							}
						}		
					}
					if(idRect!=-1){
						if(mode_Link==true&&mode_Deplacement==false){
							temp[0]=idRect;
							flag = 0;
						}
						else{
							flag = 1;
							}	
					}
					else{
						if(flag == 1&& tmp!=-1){
						flag = 0;
						if(mode_Deplacement==true){
							_drawingView.RmvRec(tmp, med.getRelativeX(), med.getRelativeY());
							System.out.println("in");
							}	
						}
					}
				}
			});
	}
	
	public void enablePaintingCache() {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Painting cache: ENABLED");
		}
		_paintingCacheEnabled = true;
	}
	
	public void disablePaintingCache() {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Painting cache: DISABLED");
		}
		_paintingCacheEnabled = false;
	}

	public VDrawingView<?> getDrawingView() {
		return _drawingView;
	}

	public AbstractDianaEditor<?, ?, ?> getDrawingController() {
		return _drawingView.getController();
	}
	
	public void repaint(FGEView<?, ?> view, Rectangle bounds) {
		if (!_drawingView.contains(view)) {
			return;
		}

		if (paintRequestLogger.isLoggable(Level.FINE)) {
			paintRequestLogger.fine("Called REPAINT for view " + view + " for " + bounds);
		}
		//((AbstractComponent) view).setWidth(bounds., null) ;
		// repaintManager.repaintTemporaryRepaintAreas((JComponent)view);
		//repaintManager.repaintTemporaryRepaintAreas(_drawingView);
	}
	
	public void repaint(DrawingTreeNode<?, ?> node) {
		if (paintRequestLogger.isLoggable(Level.FINE)) {
			paintRequestLogger.fine("Called REPAINT for graphical representation " + node);
		}
	}
	public boolean isPaintingCacheEnabled() {
		return _paintingCacheEnabled;
	}
	public void repaint(final VFGEView<?, ?> view) {
		if (view == null) {
			logger.warning("Cannot paint null view");
			return;
		}
		if (view instanceof ShapeView) {
			// What may happen here ?
			// Control points displayed focus or selection might changed, and to be refresh correctely
			// we must assume that a request to an extended area embedding those control points
			// must be performed (in case of border is not sufficient)
			ShapeNode<?> shapeNode = ((ShapeView<?, ?>) view).getNode();

			if (shapeNode.getGraphicalRepresentation() == null) {
				// Might happen during some required updating
				return;
			}
		}
	}
	private synchronized ImageElement bufferDrawingView() {
		if (paintRequestLogger.isLoggable(Level.FINE)) {
			paintRequestLogger.fine("Buffering whole JDrawingView. Is it really necessary ?");
		}
		AbstractComponent view = getDrawingView();
		ImageElement image = null;
		return image;
	}

	private synchronized ImageElement getPaintBuffer() {
		if (_paintBuffer == null) {
			_paintBuffer = bufferDrawingView();
		}
		/*try {
			File f = File.createTempFile("MyScreenshot", new SimpleDateFormat("HH-mm-ss SSS").format(new Date())+".png");
			ImageUtils.saveImageToFile(_paintBuffer, f, ImageType.PNG);
			if (logger.isLoggable(Level.INFO))
				logger.info("Saved buffer to "+f.getAbsolutePath());
			ToolBox.openFile(f);
		} catch (Exception e) {
			e.printStackTrace();
		}*/
		return _paintBuffer;
	}

//*******************************************************************************
	// * Repaint manager *
	// *******************************************************************************

	//static class FGERepaintManager extends RepaintManager {

		//public static final boolean MANAGE_DIRTY_REGIONS = true;
		
		//public FGERepaintManager() {
			
		//}		

	//}
	
	public static void initFGERepaintManager() {
		logger.info("@@@@@@@@@@@@@@@@ initFGERepaintManager()");
	}

	public void invalidate(DrawingTreeNode<?, ?> parentNode) {
		if (paintRequestLogger.isLoggable(Level.FINE)) {
			paintRequestLogger.fine("CALLED invalidate on FGEPaintManager");
		}
		_paintBuffer = null;
		
	}
	
	public void setRPC(VDrawingViewClientRpc rpc){
		this.Delegate.rpc = rpc;
	}

	/**
	 * must to implement in client side
	 * @param rpc 
	 * @param renderingBounds
	 * @param gr
	 * @return
	 */
	public boolean renderUsingBuffer(Rectangle renderingBounds, DrawingTreeNode<?, ?> node, double scale) {
		if (renderingBounds == null) {
			return false;
		}
		ImageElement buffer = getPaintBuffer();
//		Rectangle viewBoundsInDrawingView = FGEUtils.convertRectangle(node, renderingBounds, node.getDrawing().getRoot(), scale);
//		Point dp1 = renderingBounds.getLocation();
//		// Point dp2 = new Point(renderingBounds.x + renderingBounds.width, renderingBounds.y + renderingBounds.height);
//		Point sp1 = viewBoundsInDrawingView.getLocation();
//		Point sp2 = new Point(viewBoundsInDrawingView.x + viewBoundsInDrawingView.width, viewBoundsInDrawingView.y
//				+ viewBoundsInDrawingView.height);
		ImageElement partialImage = null;//
		//this.Delegate.rpc.drawImage2(partialImage, dp1.x, dp1.y);

		return true;
	}
	
}