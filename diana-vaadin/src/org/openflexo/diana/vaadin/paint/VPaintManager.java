package org.openflexo.diana.vaadin.paint;

import java.util.HashSet;
import java.util.Vector;
import java.util.WeakHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;


import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.diana.vaadin.view.VDrawingView;

public class VPaintManager {
	
	private static final Logger logger = Logger.getLogger(VPaintManager.class.getPackage().getName());

	public static final Logger paintPrimitiveLogger = Logger.getLogger("PaintPrimitive");
	public static final Logger paintRequestLogger = Logger.getLogger("PaintRequest");
	public static final Logger paintStatsLogger = Logger.getLogger("PaintStats");

	private boolean _paintingCacheEnabled;

	// private static final int DEFAULT_IMAGE_TYPE = BufferedImage.TYPE_INT_RGB;

	
	static {
		initFGERepaintManager();
		/* Debug purposes
		paintPrimitiveLogger.setLevel(Level.FINE);
		paintRequestLogger.setLevel(Level.FINE);
		paintStatsLogger.setLevel(Level.FINE);
		*/
	}
	private static final boolean ENABLE_CACHE_BY_DEFAULT = true;
	private final VDrawingView<?> _drawingView;

	//private BufferedImage _paintBuffer;
	private final HashSet<DrawingTreeNode<?, ?>> _temporaryObjects;
	
	public VPaintManager(VDrawingView<?> drawingView) {
		super();
		_drawingView = drawingView;
		//_paintBuffer = null;
		_temporaryObjects = new HashSet<DrawingTreeNode<?, ?>>();
		if (ENABLE_CACHE_BY_DEFAULT) {
			enablePaintingCache();
		} else {
			disablePaintingCache();
		}
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
	
}
