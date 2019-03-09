/**
 * 
 */
package org.openflexo.diana.geomedit.tikz;

import java.util.Enumeration;
import java.util.Hashtable;
import org.openflexo.diana.geomedit.GeomEditDrawingController;
import org.openflexo.diana.geomedit.model.GeometricConstruction;
import org.openflexo.diana.geomedit.model.GeometricConstructionFactory;
import org.openflexo.diana.geomedit.model.GeometricDiagram;
import org.openflexo.tikz.controller.TikzConnector;
import org.openflexo.tikz.model.TikzConstruction;

/**
 * @author Quentin
 *
 */
public class TikzConnectorExtended extends TikzConnector{
	private GeomEditDrawingController controller;
	private GeometricDiagram geomEditModel;
	private GeometricConstructionFactory geomEditFactory;
	
	private Hashtable<String, TikzLinkExtended> links;
	private TikzFactoryExtended tikzFactory;
	private TikzEditorFrame editorFrame;
	
	public TikzConnectorExtended(GeomEditDrawingController controller) {
		super();
		
		this.controller = controller;
		geomEditModel = controller.getDiagram();
		geomEditFactory = controller.getFactory();
		
		links = new Hashtable<String, TikzLinkExtended>(); 
		tikzFactory = new TikzFactoryExtended(geomEditFactory);
	}
	
	public TikzEditorFrame getTikzEditorFrame() {
		if(editorFrame == null) {
			editorFrame = new TikzEditorFrame(this);
		}
		return editorFrame;
	}
	
	public String toString() {
		String returned = "TikzConnectorExtended\n";
		
		returned += "links : {\n";
		Enumeration<String> en = links.keys();
		while(en.hasMoreElements()) {
			String k = (String) en.nextElement();
			returned += "\t" + k + " : " + links.get(k).toString() + "\n";
		}
		returned += "}\n";
		
		returned += "diagram.constructions : {\n";
		for(GeometricConstruction<?> gc : geomEditModel.getConstructions()) {
			returned += "\t" + gc.toString() + "\n";
		}
		returned += "}\n";
		
		returned += "tikzConstructions : {\n";
		for(TikzConstruction tc : tikzConstructions) {
			returned += "\t" + tc.toString() + "\n";
		}
		returned += "}\n";
		
		return returned;
	}
	
	
	public Boolean pushTikzToGeomEdit() {
		if(makeAstFromString(editorFrame.getText())) {
			if(makeTikzConstructionsFromAst()) {
				if(linkFromGeomEdit()) {
					if(linkFromTikz()) {
						if(updateGeomEditFromTikz()) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}
	
	public Boolean pushGeomEditToTikz() {
		if(makeAstFromString(editorFrame.getText())) {
			if(makeTikzConstructionsFromAst()) {
				if(linkFromTikz()) {
					if(linkFromGeomEdit()) {
						if(updateTikzFromGeomEdit()) {
							editorFrame.setText(makeCodeFromTikz());
							return true;
						}
					}
				}
			}
		}
		return false;
	}
	
	
	public Boolean linkFromGeomEdit() {
		for(GeometricConstruction<?> gc : geomEditModel.getConstructions()) {
			String gcKey = TikzLinkExtended.makeKeyFromGeometricConstruction(gc);
			TikzLinkExtended link = links.get(gcKey);
			if(link == null) {
				TikzLinkExtended newLink = new TikzLinkExtended();
				newLink.setGeomEditConstruction(gc);
				links.put(gcKey, newLink);
			}
			else {
				if(link.getGeomEditConstruction() == null) {
					link.setGeomEditConstruction(gc);
				}
				if(link.getGeomEditConstruction() != gc) {
					/* TODO : accept same name multiple times
					 * previous has name changed?
					 *   yes -> can overwrite
					 *   no -> previous still in constructions list ?
					 *     no -> can overwrite
					 *     yes -> iterate over version numbers
					 */
					link.setGeomEditConstruction(gc);
				}
			}
		}
		return true;
	}
	
	public Boolean updateTikzFromGeomEdit() {
		for(GeometricConstruction<?> gc : geomEditModel.getConstructions()) {
			TikzLinkExtended link = links.get(TikzLinkExtended.makeKeyFromGeometricConstruction(gc));
			if(link != null) {
				TikzConstruction tc = link.getTikzConstruction();
				if(tc == null) {
					tc = tikzFactory.makeTikzConstruction(gc);
					tikzConstructions.add(tc);
					link.setTikzConstruction(tc);
				}
				else {
					tikzFactory.updateTikzConstruction(tc, gc);
				}
			}
		}
		for(TikzLinkExtended link : links.values()) {
			if(link.getGeomEditConstruction() == null && link.getTikzConstruction() != null) {
				link.getTikzConstruction().setDeleted(true);
			}
		}
		return true;
	}
	
	
	public Boolean linkFromTikz() {
		for(TikzConstruction tc : tikzConstructions) {
			String tcKey = TikzLinkExtended.makeKeyFromTikzConstruction(tc);
			TikzLinkExtended link = links.get(tcKey);
			if(link == null) {
				TikzLinkExtended newLink = new TikzLinkExtended();
				newLink.setTikzConstruction(tc);
				links.put(tcKey, newLink);
			}
			else {
				if(link.getTikzConstruction() == null) {
					link.setTikzConstruction(tc);
				}
				if(link.getTikzConstruction() != tc) {
					/*
					 * TODO : accept same name multiple times
					 * -> iterate over version numbers
					 * OR
					 * -> give a context to each TikzConstruction? 
					 */
					link.setTikzConstruction(tc);
				}
			}
		}
		return true;
	}

	public Boolean updateGeomEditFromTikz() {
		boolean makeAnotherUpdate = true;
		while(makeAnotherUpdate) {
			makeAnotherUpdate = false;
			for(TikzConstruction tc : tikzConstructions) {
				TikzLinkExtended link = links.get(TikzLinkExtended.makeKeyFromTikzConstruction(tc));
				if(link != null) {
					GeometricConstruction<?> gc = link.getGeomEditConstruction();
					if(gc == null) {
						gc = tikzFactory.makeGeometricConstruction(tc, links);
						geomEditModel.addToConstructions(gc);
						link.setGeomEditConstruction(gc);
					}
					else {
						if(!tikzFactory.updateGeomEditConstruction(gc, tc, links)) {
							GeometricConstruction<?> newgc = tikzFactory.makeGeometricConstruction(tc, links);
							geomEditModel.addToConstructions(newgc);
							link.setGeomEditConstruction(newgc);
							makeAnotherUpdate = true;
							geomEditModel.removeFromConstructions(gc);
						}
					}
				}
			}
		}
		for(TikzLinkExtended link : links.values()) {
			if(!tikzConstructions.contains(link.getTikzConstruction()) && link.getGeomEditConstruction() != null) {
				System.out.println("unlinked construction : " + link.getGeomEditConstruction().getName());
				GeometricConstruction<?> gc = link.getGeomEditConstruction();
				geomEditModel.removeFromConstructions(gc);
			}
		}
		return true;
	}
	
}
