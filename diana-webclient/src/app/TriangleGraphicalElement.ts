/// <reference path="ShapeGraphicalElement.ts" />

class TriangleGraphicalElement extends ShapeGraphicalElement {
    public constructor(aDtn : MyNode, sElement: Snap.Element) {
        super(aDtn, sElement);
    }
    
    public updatePath() {
        var path: string = "M" + (this.getNode().getX() + this.getNode().getWidth()/2) + "," + this.getNode().getY();
            path += "L" + this.getNode().getX() + "," + (this.getNode().getY() + this.getNode().getHeight());
            path += "L" + (this.getNode().getX() + this.getNode().getWidth()) + "," + (this.getNode().getY() + this.getNode().getHeight());
            path += "L" + (this.getNode().getX() + this.getNode().getWidth() /2) + "," + this.getNode().getY();
        this.native.attr({'d' : path});
    }
    
//    public getX(): number {
//        return this.native.data('x');
//    }
    
    public setX(val: number): void {
        this.updatePath();
    }
    
    public setY(val: number): void {
        this.updatePath();
    }
    
    public getNode(): ShapeNode {
       return <ShapeNode>super.getNode();
    }
}