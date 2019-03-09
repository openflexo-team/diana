/**
 * 
 */
package org.openflexo.diana.geomedit.tikz;

import org.openflexo.diana.geomedit.model.ConnectorConstruction;
import org.openflexo.diana.geomedit.model.GeometricConstruction;
import org.openflexo.diana.geomedit.model.NodeConstruction;
import org.openflexo.diana.geomedit.model.NodeReference;
import org.openflexo.tikz.model.TikzLink;

/**
 * @author Quentin
 *
 */
public class TikzLinkExtended extends TikzLink{
	private GeometricConstruction<?> geomEditConstruction;
	
	public TikzLinkExtended() {
		super();
		geomEditConstruction = null;
	}
	
	public String toString() {
		String returned = "TikzLinkExtended { ";
		
		if(geomEditConstruction == null) {
			returned += "null, ";
		}
		else {
			returned += geomEditConstruction.getName() + "; ";
		}

		if(tikzConstruction == null) {
			returned += "null }";
		}
		else {
			returned += tikzConstruction.toString() + " }";
		}
		return returned;
	}
	
	public GeometricConstruction<?> getGeomEditConstruction(){
		return geomEditConstruction;
	}
	
	public void setGeomEditConstruction(GeometricConstruction<?> gc) {
		geomEditConstruction = gc;
	}
	
	public static String makeKeyFromGeometricConstruction(GeometricConstruction<?> gc) {
		if(gc == null) {
			return "annonymous";
		}
		else if(gc instanceof ConnectorConstruction) {
			ConnectorConstruction connector = (ConnectorConstruction) gc;
			return "connector$" + makeKeyFromGeometricConstruction(connector.getStartNode()) + "$" + makeKeyFromGeometricConstruction(connector.getEndNode());
		}
		else if(gc instanceof NodeConstruction) {
			NodeConstruction nc = (NodeConstruction) gc;
			if(nc instanceof NodeReference) {
				NodeReference nr = (NodeReference) nc;
				return makeKeyFromGeometricConstruction(nr.getReference());
			}
			return gc.getName();
		}
		return "annonymous";
	}
	
}
