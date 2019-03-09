/**
 * 
 */
package org.openflexo.tikz.model;

/**
 * @author Quentin
 *
 */
public abstract class TikzLink {
	protected TikzConstruction tikzConstruction;
	
	public TikzLink() {
		tikzConstruction = null;
	}
	
	public String toString() {
		return "TikzLink";
	}
	
	public TikzConstruction getTikzConstruction() {
		return tikzConstruction;
	}
	
	public void setTikzConstruction(TikzConstruction tc) {
		tikzConstruction = tc;
	}
	
	public static String makeKeyFromTikzConstruction(TikzConstruction tc) {
		if(tc instanceof TikzConstructionDraw) {
			TikzConstructionDraw tcd = (TikzConstructionDraw) tc;
			String nameFrom = "annonymous";
			String nameTo = "annonymous";
			if(tcd.has_from_node()) {
				nameFrom = tcd.get_from_node();
			}
			if(tcd.has_to_node()) {
				nameTo = tcd.get_to_node();
			}
			return "connector$" + nameFrom + "$" + nameTo;
		}
		else if(tc instanceof TikzConstructionNode){
			TikzConstructionNode tcn = (TikzConstructionNode) tc;
			// TODO : change when same name allowed
			return tcn.get_name();
		}
		return "";
	}

}
