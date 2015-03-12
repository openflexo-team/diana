package org.openflexo.diana.vaadin.graphics.formes;

public class Line {
	private Integer idRect1;
	private Integer idRect2;
	private Integer id;
	
	public Line(Integer id1,Integer id2){
		idRect1=id1;
		idRect2=id2;
		id= null;
	}
	
	public void SetLine(Integer id1,Integer id2){
		idRect1=id1;
		idRect2=id2;
	}
	
	public Integer[] getLine(){
		Integer[] link = new Integer[2];
		link[0] = idRect1;
		link[1] = idRect2;
		return link;
	}
	
	public Integer getId(){
		return id;
	}
	
	public void setId(Integer Id){
		id=Id;
	}
}
