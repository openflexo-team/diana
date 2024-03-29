/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * Copyright (c) 2011-2012, AgileBirds
 * 
 * This file is part of Diana-swing, a component of the software infrastructure 
 * developed at Openflexo.
 * 
 * 
 * Openflexo is dual-licensed under the European Union Public License (EUPL, either 
 * version 1.1 of the License, or any later version ), which is available at 
 * https://joinup.ec.europa.eu/software/page/eupl/licence-eupl
 * and the GNU General Public License (GPL, either version 3 of the License, or any 
 * later version), which is available at http://www.gnu.org/licenses/gpl.html .
 * 
 * You can redistribute it and/or modify under the terms of either of these licenses
 * 
 * If you choose to redistribute it and/or modify under the terms of the GNU GPL, you
 * must include the following additional permission.
 *
 *          Additional permission under GNU GPL version 3 section 7
 *
 *          If you modify this Program, or any covered work, by linking or 
 *          combining it with software containing parts covered by the terms 
 *          of EPL 1.0, the licensors of this Program grant you additional permission
 *          to convey the resulting work. * 
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE. 
 *
 * See http://www.openflexo.org/license.html for details.
 * 
 * 
 * Please contact Openflexo (openflexo-contacts@openflexo.org)
 * or visit www.openflexo.org if you need additional information.
 * 
 */

package org.openflexo.diana.swing.paint;

import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Vector;
import java.util.WeakHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.RepaintManager;
import javax.swing.SwingUtilities;

import org.openflexo.diana.DianaConstants;
import org.openflexo.diana.DianaUtils;
import org.openflexo.diana.Drawing.ContainerNode;
import org.openflexo.diana.Drawing.DrawingTreeNode;
import org.openflexo.diana.Drawing.ShapeNode;
import org.openflexo.diana.control.AbstractDianaEditor;
import org.openflexo.diana.swing.view.JDianaView;
import org.openflexo.diana.swing.view.JDrawingView;
import org.openflexo.diana.swing.view.JLabelView;
import org.openflexo.diana.swing.view.JShapeView;
import org.openflexo.diana.view.DianaView;
import org.openflexo.diana.view.ShapeView;

public class DianaPaintManager {

	private static final boolean ENABLE_CACHE_BY_DEFAULT = true;

	private static final Logger logger = Logger.getLogger(DianaPaintManager.class.getPackage().getName());

	public static final Logger paintPrimitiveLogger = Logger.getLogger("PaintPrimitive");
	public static final Logger paintRequestLogger = Logger.getLogger("PaintRequest");
	public static final Logger paintStatsLogger = Logger.getLogger("PaintStats");

	private boolean _paintingCacheEnabled;

	// private static final int DEFAULT_IMAGE_TYPE = BufferedImage.TYPE_INT_RGB;

	private static DianaRepaintManager repaintManager;

	static {
		initDianaRepaintManager();
		/* Debug purposes
		paintPrimitiveLogger.setLevel(Level.FINE);
		paintRequestLogger.setLevel(Level.FINE);
		paintStatsLogger.setLevel(Level.FINE);
		 */
	}

	private final JDrawingView<?> _drawingView;

	private BufferedImage _paintBuffer;
	private final HashSet<DrawingTreeNode<?, ?>> _temporaryObjects;

	public DianaPaintManager(JDrawingView<?> drawingView) {
		super();
		_drawingView = drawingView;
		_paintBuffer = null;
		_temporaryObjects = new HashSet<DrawingTreeNode<?, ?>>();
		if (ENABLE_CACHE_BY_DEFAULT) {
			enablePaintingCache();
		}
		else {
			disablePaintingCache();
		}
	}

	public JDrawingView<?> getDrawingView() {
		return _drawingView;
	}

	public AbstractDianaEditor<?, ?, ?> getDrawingController() {
		return _drawingView.getController();
	}

	public boolean isPaintingCacheEnabled() {
		return _paintingCacheEnabled;
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

	public HashSet<DrawingTreeNode<?, ?>> getTemporaryObjects() {
		return _temporaryObjects;
	}

	public boolean containsTemporaryObject(DrawingTreeNode<?, ?> dtn) {
		if (dtn == null) {
			return false;
		}
		if (isTemporaryObject(dtn)) {
			return true;
		}
		if (dtn instanceof ContainerNode) {
			if (((ContainerNode<?, ?>) dtn).getChildNodes() == null) {
				return false;
			}
			for (DrawingTreeNode<?, ?> child : ((ContainerNode<?, ?>) dtn).getChildNodes()) {
				if (containsTemporaryObject(child)) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean isTemporaryObject(DrawingTreeNode<?, ?> dtn) {
		return _temporaryObjects.contains(dtn);
	}

	public boolean isTemporaryObjectOrParentIsTemporaryObject(DrawingTreeNode<?, ?> dtn) {
		if (isTemporaryObject(dtn)) {
			return true;
		}
		if (dtn.getParentNode() != null) {
			return isTemporaryObjectOrParentIsTemporaryObject(dtn.getParentNode());
		}
		return false;
	}

	public void addToTemporaryObjects(DrawingTreeNode<?, ?> dtn) {
		if (paintRequestLogger.isLoggable(Level.FINE)) {
			paintRequestLogger.fine("addToTemporaryObjects() " + dtn);
		}
		if (!_temporaryObjects.contains(dtn)) {
			_temporaryObjects.add(dtn);
		}
	}

	public void removeFromTemporaryObjects(DrawingTreeNode<?, ?> dtn) {
		_temporaryObjects.remove(dtn);
	}

	// CPU-expensive because it will ask to recreate the whole buffer
	public void invalidate(DrawingTreeNode<?, ?> dtn) {
		if (paintRequestLogger.isLoggable(Level.FINE)) {
			paintRequestLogger.fine("CALLED invalidate on DianaPaintManager");
		}
		_paintBuffer = null;
		// repaintManager.clearTemporaryRepaintArea();
	}

	public void clearPaintBuffer() {
		if (paintRequestLogger.isLoggable(Level.INFO)) {
			paintRequestLogger.info("CALLED clear paint buffer on DianaPaintManager");
		}
		_paintBuffer = null;

	}

	public void repaint(DianaView<?, ?> view, Rectangle bounds) {
		if (!_drawingView.contains(view)) {
			return;
		}

		if (paintRequestLogger.isLoggable(Level.FINE)) {
			paintRequestLogger.fine("Called REPAINT for view " + view + " for " + bounds);
		}
		((JComponent) view).repaint(bounds.x, bounds.y, bounds.width, bounds.height);
		// repaintManager.repaintTemporaryRepaintAreas((JComponent)view);
		repaintManager.repaintTemporaryRepaintAreas(_drawingView);
	}

	public void addTemporaryRepaintArea(Rectangle r, JComponent view) {
		repaintManager.addTemporaryRepaintArea(r, view);
	}

	public void repaint(final JDianaView<?, ?> view) {

		if (view == null) {
			// logger.warning("Cannot paint null view");
			return;
		}
		if (view.isDeleted()) {
			// This warning is not necessary, for example if an action leads the focused object to be deleted
			// In this case, the old focused object will be called to be repainted. So, just ignore and return
			// logger.warning("Cannot paint deleted view");
			return;
		}
		if (!SwingUtilities.isEventDispatchThread()) {
			SwingUtilities.invokeLater(() -> repaint(view));
			return;
		}
		if (!_drawingView.contains(view)) {
			return;
		}

		if (paintRequestLogger.isLoggable(Level.FINE)) {
			paintRequestLogger.fine("Called REPAINT for view " + view);
		}
		if (view == _drawingView) {
			// clearTemporaryRepaintArea();
			// paintRequestLogger.warning("Called repaint on whole JDrawingView. Is it really necessary ?");
		}
		repaintManager.repaintTemporaryRepaintAreas(_drawingView);
		((JComponent) view).repaint();
		if (view.getNode().hasFloatingLabel()) {
			JLabelView<?> label = view.getLabelView();
			if (label != null) {
				label.repaint();
			}
		}
		// repaintManager.repaintTemporaryRepaintAreas((JComponent)view);

		if (view instanceof ShapeView) {
			Container parent = ((Component) view).getParent();
			if (parent == null) {
				return;
			}
			// What may happen here ?
			// Control points displayed focus or selection might changed, and to be refresh correctely
			// we must assume that a request to an extended area embedding those control points
			// must be performed (in case of border is not sufficient)
			ShapeNode<?> shapeNode = ((ShapeView<?, ?>) view).getNode();

			if (shapeNode.getGraphicalRepresentation() == null) {
				// Might happen during some required updating
				return;
			}

			int requiredControlPointSpace = DianaConstants.CONTROL_POINT_SIZE;
			if (shapeNode.getBorderTop() * view.getScale() < requiredControlPointSpace) {
				Rectangle repaintAlsoThis = new Rectangle(-requiredControlPointSpace, -requiredControlPointSpace,
						((Component) view).getWidth() + requiredControlPointSpace * 2, requiredControlPointSpace * 2);
				repaintAlsoThis = SwingUtilities.convertRectangle((Component) view, repaintAlsoThis, parent);
				parent.repaint(repaintAlsoThis.x, repaintAlsoThis.y, repaintAlsoThis.width, repaintAlsoThis.height);
				// System.out.println("Repaint "+repaintAlsoThis+" for "+((Component)view).getParent());
			}
			if (shapeNode.getBorderBottom() * view.getScale() < requiredControlPointSpace) {
				Rectangle repaintAlsoThis = new Rectangle(-requiredControlPointSpace,
						((Component) view).getHeight() - requiredControlPointSpace,
						((Component) view).getWidth() + requiredControlPointSpace * 2, requiredControlPointSpace * 2);
				repaintAlsoThis = SwingUtilities.convertRectangle((Component) view, repaintAlsoThis, parent);
				parent.repaint(repaintAlsoThis.x, repaintAlsoThis.y, repaintAlsoThis.width, repaintAlsoThis.height);
				// System.out.println("Repaint "+repaintAlsoThis+" for "+((Component)view).getParent());
			}
			if (shapeNode.getBorderLeft() * view.getScale() < requiredControlPointSpace) {
				Rectangle repaintAlsoThis = new Rectangle(-requiredControlPointSpace, -requiredControlPointSpace,
						requiredControlPointSpace * 2, ((Component) view).getHeight() + requiredControlPointSpace * 2);
				repaintAlsoThis = SwingUtilities.convertRectangle((Component) view, repaintAlsoThis, parent);
				parent.repaint(repaintAlsoThis.x, repaintAlsoThis.y, repaintAlsoThis.width, repaintAlsoThis.height);
				// System.out.println("Repaint "+repaintAlsoThis+" for "+((Component)view).getParent());
			}
			if (shapeNode.getBorderRight() * view.getScale() < requiredControlPointSpace) {
				Rectangle repaintAlsoThis = new Rectangle(((Component) view).getWidth() - requiredControlPointSpace,
						-requiredControlPointSpace, requiredControlPointSpace * 2,
						((Component) view).getHeight() + requiredControlPointSpace * 2);
				repaintAlsoThis = SwingUtilities.convertRectangle((Component) view, repaintAlsoThis, parent);
				parent.repaint(repaintAlsoThis.x, repaintAlsoThis.y, repaintAlsoThis.width, repaintAlsoThis.height);
				// System.out.println("Repaint "+repaintAlsoThis+" for "+((Component)view).getParent());
			}
		}
	}

	public void repaint(DrawingTreeNode<?, ?> node) {
		if (paintRequestLogger.isLoggable(Level.FINE)) {
			paintRequestLogger.fine("Called REPAINT for graphical representation " + node);
		}
		JDianaView<?, ?> view = _drawingView.viewForNode(node);
		if (view != null) {
			repaint(view);
		}
	}

	public BufferedImage getScreenshot(DrawingTreeNode<?, ?> node) {
		/*Component view = getDrawingView();
		BufferedImage bufferedImage = new BufferedImage(view.getWidth(), view.getHeight(), DEFAULT_IMAGE_TYPE);
		Graphics2D g = bufferedImage.createGraphics();
		view.print(g);
		return bufferedImage;*/
		DianaView<?, ?> v = getDrawingView().viewForNode(node);
		Rectangle rect = new Rectangle(((JComponent) v).getX(), ((JComponent) v).getY(), ((JComponent) v).getWidth(),
				((JComponent) v).getHeight());
		if (v instanceof JShapeView) {
			if (((JShapeView<?>) v).getLabelView() != null) {
				rect = rect.union(((JShapeView<?>) v).getLabelView().getBounds());
			}
		}
		return getPaintBuffer().getSubimage(rect.x, rect.y, rect.width, rect.height);
	}

	private BufferedImage bufferDrawingView() {
		if (paintRequestLogger.isLoggable(Level.FINE)) {
			paintRequestLogger.fine("Buffering whole JDrawingView. Is it really necessary ?");
		}
		Component view = getDrawingView();
		GraphicsConfiguration gc = view.getGraphicsConfiguration();
		if (gc == null) {
			gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
		}
		BufferedImage image = gc.createCompatibleImage(view.getWidth(), view.getHeight(), Transparency.TRANSLUCENT);
		Graphics2D g = image.createGraphics();
		getDrawingView().prepareForBuffering(g);
		view.print(g);
		g.dispose();
		return image;
	}

	private BufferedImage getPaintBuffer() {
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

	// *******************************************************************************
	// * Repaint manager *
	// *******************************************************************************

	static class DianaRepaintManager extends RepaintManager {

		public static final boolean MANAGE_DIRTY_REGIONS = true;

		// For now temporary repaint areas are registered only for JDrawingView !!!!!!
		// Later, we might extend this scheme to the whole view hierarchy
		// Using for example Hashtable<JComponent,Vector<Rectangle>> structure
		private final WeakHashMap<JComponent, Vector<Rectangle>> temporaryRepaintAreas;

		public DianaRepaintManager() {
			temporaryRepaintAreas = new WeakHashMap<JComponent, Vector<Rectangle>>();
		}

		public void addTemporaryRepaintArea(Rectangle r, JComponent view) {
			if (MANAGE_DIRTY_REGIONS) {
				Vector<Rectangle> allRect = temporaryRepaintAreas.get(view);
				if (allRect == null) {
					allRect = new Vector<Rectangle>();
					temporaryRepaintAreas.put(view, allRect);
				}
				allRect.add(r);
				if (paintRequestLogger.isLoggable(Level.FINER)) {
					paintRequestLogger.finer("addTemporaryRepaintArea(" + r + ") for " + view.getClass().getSimpleName()
							+ " temporaryRepaintAreas size=" + allRect.size());
				}
			}
		}

		private void repaintTemporaryRepaintAreas(JComponent component) {
			if (MANAGE_DIRTY_REGIONS) {
				Vector<Rectangle> allRect = temporaryRepaintAreas.get(component);
				if (allRect != null) {
					for (Rectangle r : allRect) {
						component.repaint(r);
						if (paintRequestLogger.isLoggable(Level.FINER)) {
							paintRequestLogger.finer("repaint(" + r + ") for " + component.getClass().getSimpleName());
						}
					}
					allRect.clear();
				}
			}
		}

		@Override
		public void addDirtyRegion(JComponent c, int x, int y, int w, int h) {
			if (paintRequestLogger.isLoggable(Level.FINEST)) {
				paintRequestLogger.finest("adding DirtyRegion: " + c.getName() + ", " + x + "," + y + " " + w + "x" + h);
			}

			// paintRequestLogger.warning("adding DirtyRegion: "+c.getName()+", "+x+","+y+" "+w+"x"+h);
			super.addDirtyRegion(c, x, y, w, h);
			/*if (MANAGE_DIRTY_REGIONS) {
			Rectangle r2 = new Rectangle(x,y,w,h);
			Iterator<Rectangle> it = temporaryRepaintAreas.iterator();
			while(it.hasNext()) {
				Rectangle next = it.next();
				if (r2.contains(next)) {
					if (paintRequestLogger.isLoggable(Level.FINEST))
						paintRequestLogger.finer("Remove temporary repaint area "+next);
					it.remove();
				}
			}
			}*/
		}

		@Override
		public void paintDirtyRegions() {
			// Unfortunately most of the RepaintManager state is package
			// private and not accessible from the subclass at the moment,
			// so we can't print more info about what's being painted.
			if (paintRequestLogger.isLoggable(Level.FINEST)) {
				paintRequestLogger.finest("painting DirtyRegions");
			}
			try {
				super.paintDirtyRegions();
			} catch (NullPointerException e) {
				logger.warning("Unexpected NullPointerException during repaint(). Please investigate. See logs for full stacktrace");
				e.printStackTrace();
			}
		}

	}

	public static void initDianaRepaintManager() {
		logger.info("@@@@@@@@@@@@@@@@ initDianaRepaintManager()");
		repaintManager = new DianaRepaintManager();
		RepaintManager.setCurrentManager(repaintManager);
	}

	/**
	 * 
	 * @param g
	 * @param renderingBounds
	 * @param gr
	 * @return
	 */
	/*protected boolean renderUsingBuffer(Graphics g, Rectangle renderingBounds, GraphicalRepresentation gr)
	{
	    //	Use buffer
		Image buffer = getPaintBuffer();
		Point p1 = renderingBounds.getLocation();
		Point p2 = new Point(renderingBounds.x+renderingBounds.width,renderingBounds.y+renderingBounds.height);
		if ((p1.x < 0)
				|| (p1.x > buffer.getWidth(null))
				|| (p1.y < 0)
				|| (p1.y > buffer.getHeight(null))
				|| (p2.x < 0)
				|| (p2.x > buffer.getWidth(null))
				|| (p2.y < 0)
				|| (p2.y > buffer.getHeight(null))) {
			// We have here a request for render outside cached image
			// We cannot do that, so skip buffer use and do normal painting
			if (DianaPaintManager.paintPrimitiveLogger.isLoggable(Level.FINE))
				DianaPaintManager.paintPrimitiveLogger.fine("GraphicalRepresentation:"+gr+" / request to render outside image buffer, use normal rendering clip="+renderingBounds);
			invalidate(gr);
			return false;
		}
		else {
			// OK, we are in our bounds
			if (DianaPaintManager.paintPrimitiveLogger.isLoggable(Level.FINE))
				DianaPaintManager.paintPrimitiveLogger.fine("JDrawingView: use image buffer, copy area "+renderingBounds);
			g.drawImage(buffer,
					p1.x,p1.y,p2.x,p2.y,
					p1.x,p1.y,p2.x,p2.y,
					null);
			return true;
		}
	}*/

	/**
	 * 
	 * @param g
	 * @param renderingBounds
	 * @param gr
	 * @return
	 */
	public boolean renderUsingBuffer(Graphics2D g, Rectangle renderingBounds, DrawingTreeNode<?, ?> node, double scale) {
		if (renderingBounds == null) {
			return false;
		}
		// Use buffer
		BufferedImage buffer = getPaintBuffer();
		Rectangle viewBoundsInDrawingView = DianaUtils.convertRectangle(node, renderingBounds, node.getDrawing().getRoot(), scale);
		Point dp1 = renderingBounds.getLocation();
		// Point dp2 = new Point(renderingBounds.x + renderingBounds.width, renderingBounds.y + renderingBounds.height);
		Point sp1 = viewBoundsInDrawingView.getLocation();
		Point sp2 = new Point(viewBoundsInDrawingView.x + viewBoundsInDrawingView.width,
				viewBoundsInDrawingView.y + viewBoundsInDrawingView.height);

		if (sp1.x < 0 || sp1.x > buffer.getWidth() || sp1.y < 0 || sp1.y > buffer.getHeight() || sp2.x < 0 || sp2.x > buffer.getWidth()
				|| sp2.y < 0 || sp2.y > buffer.getHeight()) {
			// We have here a request for render outside cached image
			// We cannot do that, so skip buffer use and do normal painting
			if (DianaPaintManager.paintPrimitiveLogger.isLoggable(Level.FINE)) {
				DianaPaintManager.paintPrimitiveLogger
						.fine("Node:" + node + " / request to render outside image buffer, use normal rendering clip=" + renderingBounds);
			}
			// invalidate(gr);
			return false;
		}
		else {
			// OK, we are in our bounds
			if (DianaPaintManager.paintPrimitiveLogger.isLoggable(Level.FINE)) {
				DianaPaintManager.paintPrimitiveLogger.fine("JDrawingView: use image buffer, copy area " + renderingBounds);
			}

			// Below was the previous implementation, using i think a too complex drawing primitive
			// (image was resized and so on)
			/*g.drawImage(buffer,
					dp1.x,dp1.y,dp2.x,dp2.y,
					sp1.x,sp1.y,sp2.x,sp2.y,
					null);*/

			// Alternative implementation: improve performances (hope so)
			Graphics2D newGraphics = (Graphics2D) g.create();
			/** Unactivation of anti-aliasing */
			newGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
			newGraphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
			/** Fast rendering required here */
			newGraphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
			newGraphics.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_SPEED);
			newGraphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
			newGraphics.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_DISABLE);

			BufferedImage partialImage = buffer.getSubimage(sp1.x, sp1.y, viewBoundsInDrawingView.width, viewBoundsInDrawingView.height);
			newGraphics.drawImage(partialImage, dp1.x, dp1.y, null);
			newGraphics.dispose();

			return true;
		}
	}

}
