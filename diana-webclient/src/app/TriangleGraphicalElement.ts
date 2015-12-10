/// <reference path="ShapeGraphicalElement.ts" />

class TriangleGraphicalElement extends ShapeGraphicalElement {
    public constructor(aDtn : MyNode, paper: Snap.Paper) {
        super(aDtn, paper);
    }
    
    public updatePath() {
        var path: string = "M" + (this.getNode().getX() + this.getNode().getWidth()/2) + "," + this.getNode().getY();
            path += "L" + this.getNode().getX() + "," + (this.getNode().getY() + this.getNode().getHeight());
            path += "L" + (this.getNode().getX() + this.getNode().getWidth()) + "," + (this.getNode().getY() + this.getNode().getHeight());
            path += "L" + (this.getNode().getX() + this.getNode().getWidth() /2) + "," + this.getNode().getY();
        this.native.attr({'d' : path});
    }
    
    public createSVG(): void {
        var path: string = "M" + (this.getNode().getX() + this.getNode().getWidth() /2) + "," + this.getNode().getY();
            path += "L" + this.getNode().getX() + "," + (this.getNode().getY() + this.getNode().getHeight());
            path += "L" + (this.getNode().getX() + this.getNode().getWidth()) + "," + (this.getNode().getY() + this.getNode().getHeight());
            path += "L" + (this.getNode().getX() + this.getNode().getWidth() /2) + "," + this.getNode().getY();
        this.native = this.paper.path(path);
        super.createSVG();
    }
    
    public createControlPoints(): void {
        var cPointsDescription = [{'relX': 0.5, 'relY': 0, 'type': 'NORTH'},
                                    {'relX': 0, 'relY': 1, 'type': 'SOUTHWEST'},
                                    {'relX': 1, 'relY': 1, 'type': 'SOUTHEAST'}];
        for(var cPoint of cPointsDescription) {
            this.getControlPoints().push(new ControlPoint(this.getNode(), this.getPaper(), cPoint.relX, cPoint.relY, cPoint.type));
        }
    }
    
    public setX(val: number): void {
        this.updatePath();
    }
    
    public setY(val: number): void {
        this.updatePath();
    }
    
    public setWidth(val: number): void {
        this.updatePath();
    }
    
    public setHeight(val: number): void {
        this.updatePath();
    }
    
    public getNode(): ShapeNode {
       return <ShapeNode>super.getNode();
    }
}