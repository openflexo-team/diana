/**
 * 
 */
package org.openflexo.diana.geomedit.tikz;

import org.openflexo.tikz.model.ParameterValue;
import org.openflexo.tikz.model.ParameterValuePosition;
import org.openflexo.tikz.model.TikzConstruction;
import org.openflexo.tikz.model.TikzConstructionDraw;
import org.openflexo.tikz.model.TikzFactory;
import org.openflexo.tikz.model.ValueConverter;
import org.openflexo.tikz.model.TikzConstructionNode;
import org.openflexo.tikz.model.TikzConstruction.ParameterType;

import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.util.Hashtable;

import org.openflexo.diana.geom.DianaPoint;
import org.openflexo.diana.geomedit.model.ConnectorConstruction;
import org.openflexo.diana.geomedit.model.GeometricConstruction;
import org.openflexo.diana.geomedit.model.GeometricConstructionFactory;
import org.openflexo.diana.geomedit.model.NodeConstruction;
import org.openflexo.diana.geomedit.model.NodeReference;
import org.openflexo.diana.geomedit.model.NodeWithCenterAndDimensionConstruction;
import org.openflexo.diana.geomedit.model.NodeWithRelativePositionConstruction;
import org.openflexo.diana.geomedit.model.NodeWithTwoPointsConstruction;
import org.openflexo.diana.geomedit.model.PointConstruction;
import org.openflexo.diana.shapes.ShapeSpecification.ShapeType;

/**
 * @author Quentin
 *
 */
public class TikzFactoryExtended extends TikzFactory {
	
	private GeometricConstructionFactory geomEditFactory;
	private ValueConverter<Color, String> colorStringConverter;
	private ValueConverter<ShapeType, String> shapeStringConverter;
	private AffineTransform transformFromGeomEditToTikz;
	private AffineTransform transformFromTikzToGeomEdit;
	
	public TikzFactoryExtended(GeometricConstructionFactory geomEditFactory) {
		this.geomEditFactory = geomEditFactory;
		
		colorStringConverter = new ValueConverter<Color,String>(Color.white, "white");
		colorStringConverter.addValueCorrespondance(Color.black, "black");
		colorStringConverter.addValueCorrespondance(Color.blue, "blue");
		colorStringConverter.addValueCorrespondance(new Color(191, 128, 64), "brown");
		colorStringConverter.addValueCorrespondance(Color.cyan, "cyan");
		colorStringConverter.addValueCorrespondance(Color.darkGray, "darkgray");
		colorStringConverter.addValueCorrespondance(Color.gray, "gray");
		colorStringConverter.addValueCorrespondance(Color.green, "green");
		colorStringConverter.addValueCorrespondance(Color.lightGray, "lightgray");
		colorStringConverter.addValueCorrespondance(new Color(191, 255, 0), "lime");
		colorStringConverter.addValueCorrespondance(Color.magenta, "magenta");
		colorStringConverter.addValueCorrespondance(new Color(159, 140, 24), "olive");
		colorStringConverter.addValueCorrespondance(Color.orange, "orange");
		colorStringConverter.addValueCorrespondance(Color.pink, "pink");
		colorStringConverter.addValueCorrespondance(new Color(191, 0, 64), "purple");
		colorStringConverter.addValueCorrespondance(Color.red, "red");
		colorStringConverter.addValueCorrespondance(new Color(0, 128, 128), "teal");
		colorStringConverter.addValueCorrespondance(new Color(128, 0, 128), "violet");
		colorStringConverter.addValueCorrespondance(Color.white, "white");
		colorStringConverter.addValueCorrespondance(Color.yellow, "yellow");
		
		shapeStringConverter = new ValueConverter<ShapeType, String>(ShapeType.RECTANGLE, "rectangle");
		shapeStringConverter.addValueCorrespondance(ShapeType.RECTANGLE, "rectangle");
		shapeStringConverter.addValueCorrespondance(ShapeType.CIRCLE, "circle");
		
		transformFromGeomEditToTikz = new AffineTransform(0.01, 0, 0, -0.01, 0, 10);
		transformFromTikzToGeomEdit = new AffineTransform(100, 0, 0, -100, 0, 1000);
		
		
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////
	// From GeomEdit //////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////
	
	public TikzConstruction makeTikzConstruction(GeometricConstruction<?> gc) {
		if(gc instanceof NodeConstruction) {
			return makeTikzConstructionNode((NodeConstruction) gc);
		}
		else if(gc instanceof ConnectorConstruction) {
			return makeTikzConstructionDraw((ConnectorConstruction) gc);
		}
		return null;
	}
	
	public TikzConstructionNode makeTikzConstructionNode(NodeConstruction nc) {
		TikzConstructionNode tcn = new TikzConstructionNode();
		
		// TODO : change when same name allowed
		tcn.set_name(nc.getName());
		
		updateTikzConstructionNode(tcn, nc);
		
		return tcn;
	}
	
	public static TikzConstructionDraw makeTikzConstructionDraw(ConnectorConstruction cc) {
		TikzConstructionDraw tcd = new TikzConstructionDraw();
		
		tcd.set_from_node(((NodeReference) cc.getStartNode()).getReference().getName());
		tcd.set_to_node(((NodeReference) cc.getEndNode()).getReference().getName());
		
		return tcd;
	}
	
	// Update /////////////////////////////////////////////////////////////////////////////////////
	
	public void updateTikzConstruction(TikzConstruction tc, GeometricConstruction<?> gc) {
		if(tc instanceof TikzConstructionNode && gc instanceof NodeConstruction) {
			updateTikzConstructionNode((TikzConstructionNode) tc, (NodeConstruction) gc);
		}
		else if(tc instanceof TikzConstructionDraw && gc instanceof ConnectorConstruction) {
			updateTikzConstructionDraw((TikzConstructionDraw) tc, (ConnectorConstruction) gc);
		}
	}
	
	public void updateTikzConstructionNode(TikzConstructionNode tcn, NodeConstruction nc) {
		// at_node & at_absolute & relative_position & node_distance
		if(nc instanceof NodeWithCenterAndDimensionConstruction) {
			NodeWithCenterAndDimensionConstruction ncdc = (NodeWithCenterAndDimensionConstruction) nc;
			DianaPoint geomEditPoint = ncdc.getCenterConstruction().getData();
			DianaPoint tikzPoint = geomEditPoint.transform(transformFromGeomEditToTikz);
			tcn.set_at_absolute(new ParameterValuePosition(tikzPoint.getX(), tikzPoint.getY()));
		}
		else if(nc instanceof NodeWithRelativePositionConstruction) {
			NodeWithRelativePositionConstruction nrpc = (NodeWithRelativePositionConstruction) nc;
			if(nrpc.getTX() == 0 && nrpc.getTY() == 0) {
				tcn.set_at_node(((NodeReference) nrpc.getReference()).getReference().getName()); 
			}
			else {
				// TODO : change when parameters "node distance", "below", "above left"... are created
				tcn.set_at_node(((NodeReference) nrpc.getReference()).getReference().getName()); 
			}
		}
		else if(nc instanceof NodeWithTwoPointsConstruction) {
			NodeWithTwoPointsConstruction ntpc = (NodeWithTwoPointsConstruction) nc;
			DianaPoint geomEditPoint = DianaPoint.getMiddlePoint(ntpc.getPointConstruction1().getData(), ntpc.getPointConstruction2().getData());
			DianaPoint tikzPoint = geomEditPoint.transform(transformFromGeomEditToTikz);
			tcn.set_at_absolute(new ParameterValuePosition(tikzPoint.getX(), tikzPoint.getY()));
		}
		
		// shape
		tcn.set_shape(makeStringFromShapeType(nc.getShapeType()));
		
		// color
		tcn.set_color(makeStringFromColor(nc.getGraphicalRepresentation().getTextStyle().getColor()));
		
		// draw
		tcn.set_draw(makeStringFromColor(nc.getForeground().getColor()));
	}
	
	public static void updateTikzConstructionDraw(TikzConstructionDraw tcd, ConnectorConstruction cc) {
		
	}
	
	// Parameter assignments //////////////////////////////////////////////////////////////////////
	
	public static void setConstructionParameterWithString(TikzConstruction tc, ParameterType pt, String s){
		ParameterValue<?> pv = tc.getParameterValue(pt);
		pv.setValueAsObject(s);
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////
	// From Tikz //////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////
	
	public GeometricConstruction<?> makeGeometricConstruction(TikzConstruction tc, Hashtable<String, TikzLinkExtended> links){
		if(tc instanceof TikzConstructionNode) {
			NodeConstruction nc = makeNodeConstruction((TikzConstructionNode) tc, links);
			nc.setLabel(((TikzConstructionNode) tc).get_name());
			return nc;
		}
		else if(tc instanceof TikzConstructionDraw) {
			return makeConnectorConstruction((TikzConstructionDraw) tc, links);
		}
		return null;
	}
	
	public NodeConstruction makeNodeConstruction(TikzConstructionNode tcn, Hashtable<String, TikzLinkExtended> links) {
		// TODO : add when tcn.has_relative_position() && tcn.has_node_distance()
		if(tcn.has_at_node()) {
			NodeWithRelativePositionConstruction nrpc = geomEditFactory.makeNodeWithRelativePositionConstruction(null, 0, 0);
			updateGeomEditNodeConstruction(nrpc, tcn, links);
			return nrpc;
		}
		else if(tcn.has_at_absolute()) {
			NodeWithCenterAndDimensionConstruction ncdc = geomEditFactory.makeNodeWithCenterAndDimensionConstruction(null);
			updateGeomEditNodeConstruction(ncdc, tcn, links);
			return ncdc;
		}
		return null;
	}
	
	public ConnectorConstruction makeConnectorConstruction(TikzConstructionDraw tcd, Hashtable<String, TikzLinkExtended> links) {
		ConnectorConstruction cc = geomEditFactory.makeConnector(null, null);
		
		updateGeomEditConnectorConstruction(cc, tcd, links);
		
		return cc;
	}
	
	// Update /////////////////////////////////////////////////////////////////////////////////////
	
	public Boolean updateGeomEditConstruction(GeometricConstruction<?> gc, TikzConstruction tc, Hashtable<String, TikzLinkExtended> links) {
		if(gc instanceof NodeConstruction && tc instanceof TikzConstructionNode) {
			return updateGeomEditNodeConstruction((NodeConstruction) gc, (TikzConstructionNode) tc, links);
		}
		else if(gc instanceof ConnectorConstruction && tc instanceof TikzConstructionDraw) {
			return updateGeomEditConnectorConstruction((ConnectorConstruction) gc, (TikzConstructionDraw) tc, links);
		}
		return false;
	}
	
	public Boolean updateGeomEditNodeConstruction(NodeConstruction nc, TikzConstructionNode tcn, Hashtable<String, TikzLinkExtended> links) {
		// at_node & at_absolute & relative_position & node_distance
		if(tcn.has_at_node()) {
			String newPositionKey = tcn.get_at_node();
			GeometricConstruction<?> newPosition = (links.get(newPositionKey)).getGeomEditConstruction();
			
			// TODO : verify if newPosition was updated before or force it
			
			if(newPosition instanceof NodeConstruction) {
				NodeConstruction newPositionNC = (NodeConstruction) newPosition;
				// nc has to be a NodeWithRelativePositionConstruction
				if(nc instanceof NodeWithRelativePositionConstruction) {
					NodeWithRelativePositionConstruction nrpc = (NodeWithRelativePositionConstruction) nc;
					nrpc.setReference(this.geomEditFactory.makeNodeReference(newPositionNC));
					nrpc.refresh();
					nrpc.notifyGeometryChanged();
				}
				else {
					// the node must be rebuilt from scratch
					return false;
				}
			}
			// TODO : shape=coordinate => background = none, no stroke
		}
		else if(tcn.has_at_absolute()) {
			ParameterValuePosition valuePosition = tcn.get_at_absolute();
			DianaPoint tikzPoint = new DianaPoint(valuePosition.getX(), valuePosition.getY());
			DianaPoint geomEditPoint = tikzPoint.transform(transformFromTikzToGeomEdit);
			if(nc instanceof NodeWithCenterAndDimensionConstruction) {
				NodeWithCenterAndDimensionConstruction ncdc = (NodeWithCenterAndDimensionConstruction) nc;
				PointConstruction pc = geomEditFactory.makeExplicitPointConstruction(geomEditPoint);
				ncdc.setCenterConstruction(pc);
				ncdc.refresh();
				ncdc.notifyGeometryChanged();
			}
			else if(nc instanceof NodeWithTwoPointsConstruction) {
				NodeWithTwoPointsConstruction ntpc = (NodeWithTwoPointsConstruction) nc;
				double w = nc.getWidth(), h = nc.getHeight();
				
				DianaPoint geomEditPoint1 = new DianaPoint(geomEditPoint.x - w / 2, geomEditPoint.y - h / 2);
				PointConstruction pc1 = geomEditFactory.makeExplicitPointConstruction(geomEditPoint1);
				ntpc.setPointConstruction1(pc1);
				DianaPoint geomEditPoint2 = new DianaPoint(geomEditPoint.x + w / 2, geomEditPoint.y + h / 2);
				PointConstruction pc2 = geomEditFactory.makeExplicitPointConstruction(geomEditPoint2);
				
				ntpc.setPointConstruction2(pc2);
				ntpc.refresh();
				ntpc.notifyGeometryChanged();
			}
			else {
				// The node has to be rebuilt from scratch
				return false;
			}
		}
		// TODO : else if(tcn.has_relative_position() && tcn.has_node_distance()){ nc has to be a NodeWithRelativePositionConstruction }
		
		// shape
		if(tcn.has_shape()) {
			ShapeType newShape = makeShapeTypeFromString(tcn.get_shape());
			if(nc.getShapeType() != newShape) {
				nc.setShapeType(newShape);
			}
		}
		
		if(nc.getForeground() == null) {
			nc.setForeground(geomEditFactory.makeForegroundStyle(Color.white));
		}
		if(nc.getGraphicalRepresentation() == null) {
			nc.setGraphicalRepresentation(geomEditFactory.makeNewConstructionGR(nc));
		}
		
		// color
		if(tcn.has_color()) {
			Color newColor = makeColorFromString(tcn.get_color());
			if(nc.getGraphicalRepresentation().getTextStyle().getColor() != newColor) {
				nc.getGraphicalRepresentation().getTextStyle().setColor(newColor);
			}
		}

		// draw
		if(tcn.has_draw()) {
			Color newColor = makeColorFromString(tcn.get_draw());
			if(nc.getForeground().getColor() != newColor) {
				nc.getForeground().setColor(newColor);
			}
		}
		
		// radius
		// TODO : radius is used in polar position
		
		return true;
	}
	
	public Boolean updateGeomEditConnectorConstruction(ConnectorConstruction cc, TikzConstructionDraw tcd, Hashtable<String, TikzLinkExtended> links) {
		// from_absolute & from_node
		if(tcd.has_from_node()) {
			String newPositionKey = tcd.get_from_node();
			GeometricConstruction<?> newPosition = (links.get(newPositionKey)).getGeomEditConstruction();
			
			if(newPosition instanceof NodeConstruction) {
				NodeConstruction newPositionNC = (NodeConstruction) newPosition;
				cc.setStartNode(this.geomEditFactory.makeNodeReference(newPositionNC));
				cc.refresh();
				cc.notifyGeometryChanged();
			}
		}
		else if(tcd.has_from_absolute()) {
			// TODO : add an invisible node?
			System.out.println("warning : not yet implemented");
		}
		
		// to_absolute & to_node
		if(tcd.has_to_node()) {
			String newPositionKey = tcd.get_to_node();
			GeometricConstruction<?> newPosition = (links.get(newPositionKey)).getGeomEditConstruction();
			
			if(newPosition instanceof NodeConstruction) {
				NodeConstruction newPositionNC = (NodeConstruction) newPosition;
				
				cc.setEndNode(this.geomEditFactory.makeNodeReference(newPositionNC));
				cc.refresh();
				cc.notifyGeometryChanged();
			}
		}
		else if(tcd.has_to_absolute()) {
			// TODO : add an invisible node?
			System.out.println("warning : not yet implemented");
		}
		return true;
	}

	// Type conversions ///////////////////////////////////////////////////////////////////////////
	
	public String makeStringFromShapeType(ShapeType sp) {
		return shapeStringConverter.convert(sp);
	}
	
	public ShapeType makeShapeTypeFromString(String s) {
		return shapeStringConverter.convertBack(s);
	}
	
	public String makeStringFromColor(Color c) {
		// TODO : make a generalized conversion algorithm for non-particular values
		return colorStringConverter.convert(c);
	}
	
	public Color makeColorFromString(String s) {
		// TODO : develop with other color formats ("red!50!blue"...)
		return colorStringConverter.convertBack(s);
	}
	
}
