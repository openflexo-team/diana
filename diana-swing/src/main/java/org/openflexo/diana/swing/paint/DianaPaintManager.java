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

import java.awt.AlphaComposite;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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

	// *** Performance diagnostic instrumentation (toggle with -Ddiana.paintdebug=true) ***
	// All prints below are no-ops unless the flag is set; no behavioural change.
	public static final boolean PAINT_DEBUG = Boolean.getBoolean("diana.paintdebug");

	private long bufferRebuildCount = 0; // # of full background-buffer rebuilds (hypothesis 2)
	private long dragBlitCount = 0;      // # of drag-cache blits (hypothesis 1: should be ~1/frame)
	private long liveRenderCount = 0;    // # of live subtree re-renders during a drag (hypothesis 1)
	private long regionRefreshCount = 0; // # of region-incremental buffer refreshes (Piste C)

	// *** Region-incremental background buffer (Piste C) ***
	// Instead of discarding the whole _paintBuffer whenever a single node's contribution to the
	// background changes (appearance change via invalidate(), or temporary-status change via
	// add/removeFromTemporaryObjects), re-render only that node's region into the existing buffer.
	// Cost drops from O(all shapes of the drawing) to O(shapes intersecting the node's region).
	// On by default; disable with -Ddiana.disableRegionCache=true to fall back to full rebuilds.
	public static final boolean REGION_CACHE = !Boolean.getBoolean("diana.disableRegionCache");

	// Last region (in drawing-view coordinates) each node contributed to the buffer. Used to clear
	// the union(previous, current) so a node that moved/shrank leaves no ghost. Reset on full rebuild.
	private final Map<DrawingTreeNode<?, ?>, Rectangle> _bufferedNodeBounds = new HashMap<DrawingTreeNode<?, ?>, Rectangle>();

	public long getBufferRebuildCount() {
		return bufferRebuildCount;
	}

	public long getDragBlitCount() {
		return dragBlitCount;
	}

	public long getLiveRenderCount() {
		return liveRenderCount;
	}

	public void notifyDragBlit() {
		if (PAINT_DEBUG) {
			dragBlitCount++;
		}
	}

	public void notifyLiveRender() {
		if (PAINT_DEBUG) {
			liveRenderCount++;
		}
	}

	/** Short hint of the caller chain (skips this class) to attribute a temporary-set mutation. */
	private static String dbgCaller() {
		StackTraceElement[] st = Thread.currentThread().getStackTrace();
		StringBuilder sb = new StringBuilder("  <- ");
		int printed = 0;
		for (int i = 2; i < st.length && printed < 4; i++) {
			String cn = st[i].getClassName();
			if (cn.endsWith("DianaPaintManager")) {
				continue;
			}
			sb.append(cn.substring(cn.lastIndexOf('.') + 1)).append('.').append(st[i].getMethodName())
					.append(':').append(st[i].getLineNumber()).append(' ');
			printed++;
		}
		return sb.toString();
	}

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

	/**
	 * Per-node drag buffers: a snapshot of a node's whole subtree, captured once at the
	 * start of a <em>move</em> and blitted at the node's current location on every drag
	 * frame, instead of re-rendering the entire subtree each frame (see
	 * {@link #captureNode(DrawingTreeNode)}). Keyed by the dragged node.
	 */
	private final Map<DrawingTreeNode<?, ?>, BufferedImage> _nodeDragBuffers;

	public DianaPaintManager(JDrawingView<?> drawingView) {
		super();
		_drawingView = drawingView;
		_paintBuffer = null;
		_temporaryObjects = new HashSet<DrawingTreeNode<?, ?>>();
		_nodeDragBuffers = new HashMap<DrawingTreeNode<?, ?>, BufferedImage>();
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
		if (PAINT_DEBUG && !_temporaryObjects.contains(dtn)) {
			System.err.println("[diana.temp]  + ADD    " + dtn + dbgCaller());
		}
		if (!_temporaryObjects.contains(dtn)) {
			_temporaryObjects.add(dtn);
			// The set of objects excluded from the background buffer changed: the newly-temporary
			// object must no longer be baked into the background. Piste C: refresh only its region
			// (re-rendered without it, so the background behind it shows); else rebuild the lot.
			if (!(REGION_CACHE && refreshBufferRegion(dtn))) {
				_paintBuffer = null;
			}
		}
	}

	public void removeFromTemporaryObjects(DrawingTreeNode<?, ?> dtn) {
		if (PAINT_DEBUG && _temporaryObjects.contains(dtn)) {
			System.err.println("[diana.temp]  - REMOVE " + dtn + dbgCaller());
		}
		if (_temporaryObjects.remove(dtn)) {
			// Excluded set changed: the object re-enters the background buffer at its final position.
			// Piste C: render it back into its region only; else rebuild the lot.
			if (!(REGION_CACHE && refreshBufferRegion(dtn))) {
				_paintBuffer = null;
			}
		}
	}

	// *******************************************************************************
	// * Node drag-cache *
	// *******************************************************************************
	//
	// During a pure MOVE, the rendered appearance of the dragged node's subtree is
	// invariant - only its position changes. Diana already caches the rest of the
	// drawing as a bitmap (_paintBuffer); without the node drag-cache below, the dragged
	// node's whole subtree (which can be arbitrarily large for nested shapes) is instead
	// re-rendered on every frame. We snapshot that subtree once at drag start and blit it
	// at the node's current location on each frame, making a move O(blit) regardless of
	// subtree size. This is valid ONLY while the appearance is invariant, i.e. for a move
	// and never a resize, so the buffer is captured on ObjectWillMove and discarded on
	// ObjectHasMoved / any resize / any GR change (see JShapeView).

	/**
	 * Captures the current rendered appearance of a node's subtree into an offscreen
	 * image, to be blitted while the node is dragged instead of re-rendering the whole
	 * subtree on every frame. The render is forced live (painting cache temporarily
	 * disabled) because the background buffer has just been invalidated and excludes this
	 * temporary object. Must be called only for a move (never a resize), and balanced by
	 * {@link #discardNodeBuffer(DrawingTreeNode)} at the end of the drag.
	 */
	public void captureNode(DrawingTreeNode<?, ?> node) {
		if (node == null) {
			return;
		}
		DianaView<?, ?> v = _drawingView.viewForNode(node);
		if (!(v instanceof JComponent)) {
			return;
		}
		JComponent comp = (JComponent) v;
		int w = comp.getWidth();
		int h = comp.getHeight();
		if (w <= 0 || h <= 0) {
			return;
		}
		GraphicsConfiguration gc = comp.getGraphicsConfiguration();
		if (gc == null) {
			gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
		}
		BufferedImage image = gc.createCompatibleImage(w, h, Transparency.TRANSLUCENT);
		Graphics2D g = image.createGraphics();
		boolean wasEnabled = _paintingCacheEnabled;
		// Force a real, live subtree render: with the cache enabled, JShapeView.paint would
		// try to blit from the (now invalidated, temporary-excluding) background buffer.
		disablePaintingCache();
		try {
			comp.print(g);
		} catch (RuntimeException e) {
			// Capture failed: fall back to live rendering during the drag (no buffer stored).
			logger.log(Level.WARNING, "Could not capture drag buffer for " + node, e);
			return;
		} finally {
			if (wasEnabled) {
				enablePaintingCache();
			}
			g.dispose();
		}
		_nodeDragBuffers.put(node, image);
		if (PAINT_DEBUG) {
			System.err.println("[diana.paint] CAPTURE drag-cache for " + node + " (" + w + "x" + h + ")");
		}
	}

	/** The drag buffer captured for {@code node}, or {@code null} if none (not being moved). */
	public BufferedImage getNodeBuffer(DrawingTreeNode<?, ?> node) {
		return node == null ? null : _nodeDragBuffers.get(node);
	}

	/** Discards (and flushes) the drag buffer of {@code node}, if any. */
	public void discardNodeBuffer(DrawingTreeNode<?, ?> node) {
		if (node != null) {
			BufferedImage img = _nodeDragBuffers.remove(node);
			if (img != null) {
				img.flush();
			}
		}
	}

	// CPU-expensive because it will ask to recreate the whole buffer
	public void invalidate(DrawingTreeNode<?, ?> dtn) {
		if (paintRequestLogger.isLoggable(Level.FINE)) {
			paintRequestLogger.fine("CALLED invalidate on DianaPaintManager");
		}
		// A change concerning an object that is (transitively) part of the current drag must
		// not rebuild the background buffer: that buffer excludes all temporary objects, so it
		// stays valid as long as only temporary objects change. The buffer is rebuilt only when
		// the excluded set itself changes (see add/removeFromTemporaryObjects). This neutralises
		// the per-frame invalidations fired by the dragged shape, its connectors and their
		// labels (position, focus, text, …) that otherwise re-buffer the whole drawing each frame.
		if (dtn != null && isTemporaryObjectOrParentIsTemporaryObject(dtn)) {
			return;
		}
		// Piste C: re-render only this node's region into the buffer instead of discarding it all.
		if (REGION_CACHE && dtn != null && _paintBuffer != null && refreshBufferRegion(dtn)) {
			return;
		}
		if (PAINT_DEBUG && _paintBuffer != null) {
			System.err.println("[diana.inval] buffer nulled by " + dtn + dbgCaller());
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

	// *******************************************************************************
	// * Region-incremental buffer refresh (Piste C) *
	// *******************************************************************************

	/**
	 * Bounds of {@code dtn}'s view, in the drawing-view coordinate system, padded for shadow /
	 * control points / anti-alias bleed and unioned with its floating label (if any). Returns
	 * {@code null} when the node cannot be localized (no view, not displayed, zero-sized): the
	 * caller then falls back to a full buffer invalidation.
	 */
	private Rectangle nodeViewBounds(DrawingTreeNode<?, ?> dtn) {
		if (dtn == null) {
			return null;
		}
		if (dtn.getParentNode() == null) {
			// Root node (the whole drawing): "invalidate everything" - cannot be localized to a
			// region; let the caller fall back to a full buffer rebuild.
			return null;
		}
		DianaView<?, ?> v = _drawingView.viewForNode(dtn);
		if (!(v instanceof JComponent)) {
			return null;
		}
		JComponent comp = (JComponent) v;
		if (comp.getParent() == null || comp.getWidth() <= 0 || comp.getHeight() <= 0) {
			return null;
		}
		Rectangle r = SwingUtilities.convertRectangle(comp.getParent(), comp.getBounds(), _drawingView);
		if (v instanceof JShapeView) {
			JLabelView<?> label = ((JShapeView<?>) v).getLabelView();
			if (label != null && label.getParent() != null && label.getWidth() > 0 && label.getHeight() > 0) {
				r = r.union(SwingUtilities.convertRectangle(label.getParent(), label.getBounds(), _drawingView));
			}
		}
		// Pad for the drop shadow, selection/focus control points and anti-alias bleed.
		int pad = DianaConstants.CONTROL_POINT_SIZE + 6;
		r.grow(pad, pad);
		return r;
	}

	/**
	 * Re-render only {@code dtn}'s region into the existing {@link #_paintBuffer}, instead of
	 * discarding the whole buffer. Clears the union of the node's previous and current regions
	 * (so a moved/shrunk node leaves no ghost) and re-prints the drawing clipped to that region
	 * (Java2D clipping protects the rest of the buffer; the buffering pass excludes temporary
	 * objects, so this also handles a node entering/leaving the temporary set).
	 *
	 * @return {@code true} if the buffer is left consistent (was already null, or region
	 *         successfully refreshed); {@code false} if the node could not be localized and the
	 *         caller must fall back to a full invalidation.
	 */
	private boolean refreshBufferRegion(DrawingTreeNode<?, ?> dtn) {
		if (_paintBuffer == null) {
			return true; // nothing to maintain; a full rebuild will happen lazily on next paint
		}
		Rectangle current = nodeViewBounds(dtn);
		Rectangle previous = _bufferedNodeBounds.get(dtn);
		Rectangle region = current;
		if (previous != null) {
			region = (region == null) ? new Rectangle(previous) : region.union(previous);
		}
		if (region == null) {
			return false; // cannot localize -> caller nulls the whole buffer
		}
		region = region.intersection(new Rectangle(0, 0, _paintBuffer.getWidth(), _paintBuffer.getHeight()));
		if (!region.isEmpty()) {
			Graphics2D g = _paintBuffer.createGraphics();
			try {
				g.setClip(region);
				// Clear the region to transparent, then re-render the drawing clipped to it. The
				// drawing background + every shape intersecting the region are repainted in z-order;
				// the JShapeView buffering branch skips temporary objects.
				Composite previousComposite = g.getComposite();
				g.setComposite(AlphaComposite.Clear);
				g.fillRect(region.x, region.y, region.width, region.height);
				g.setComposite(previousComposite);
				getDrawingView().prepareForBuffering(g);
				getDrawingView().print(g);
			} catch (RuntimeException e) {
				logger.log(Level.WARNING, "Region buffer refresh failed for " + dtn + "; falling back to full rebuild", e);
				g.dispose();
				return false;
			}
			g.dispose();
		}
		if (current != null) {
			_bufferedNodeBounds.put(dtn, current);
		}
		else {
			_bufferedNodeBounds.remove(dtn);
		}
		if (PAINT_DEBUG) {
			regionRefreshCount++;
			System.err.println("[diana.region] refresh #" + regionRefreshCount + " " + region + " for " + dtn);
		}
		return true;
	}

	public long getRegionRefreshCount() {
		return regionRefreshCount;
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
		if (PAINT_DEBUG) {
			bufferRebuildCount++;
			System.err.println("[diana.paint] FULL BUFFER REBUILD #" + bufferRebuildCount
					+ " (re-renders every non-temporary shape of the whole drawing)");
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
		// Fresh full render: every node is now baked in, so per-node region history is stale.
		_bufferedNodeBounds.clear();
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
