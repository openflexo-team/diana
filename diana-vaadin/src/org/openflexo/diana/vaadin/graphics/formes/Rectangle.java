package org.openflexo.diana.vaadin.graphics.formes;



public class Rectangle {
	private Integer id;
	private double startX;
	private double startY;
	private double width;
	private double height;
	
	public Rectangle(double startX,double startY,double width, double height){
		this.startX=startX;
		this.startY=startY;
		this.height=height;
		this.width=width;
		id=null;
	}
	
	public Rectangle(Integer id,double startX,double startY,double width, double height){
		this.startX=startX;
		this.startY=startY;
		this.height=height;
		this.width=width;
		this.id=id;
	}
	
	public double[] coordinates(){
		double[] coordinates = new double[4];
		coordinates[0]=startX;
		coordinates[1]=startY;
		coordinates[2]=width;
		coordinates[3]=height;
		return coordinates;
	}
	
	public void moveRect(double newX,double newY){
		startX=newX;
		startY=newY;
	}
	
	/**
	 * 
	 * change la taille du rectangle.
	 * 
	 * 
	 * 
	 * @param widtha
	 *            the new width
	 * @param heighta
	 *            the new height
	 */
	public void changerTaille(double widtha,double heighta){
		height=heighta;
		width=widtha;
	}
	
	public double[] getCentre(){
		double[] centre = new double[2];
		centre[0]=(startX*2 +width)/2;
		centre[1]=(startY*2 +height)/2;
		return centre;
	}
	
	public Integer getId(){
		return id;
	}
	
	public void setId(Integer Id){
		id=Id;
	}
	
	
}
