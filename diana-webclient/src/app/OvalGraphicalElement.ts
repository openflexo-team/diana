/// <reference path="ShapeGraphicalElement.ts" />

class OvalGraphicalElement extends ShapeGraphicalElement {
    
    public constructor(aDtn : MyNode, s: Snap.Paper) {
        super(aDtn, s);
    }
    
    public createSVG() {
        this.native = this.paper.ellipse(0, 0, this.getNode().getWidth()/2, this.getNode().getHeight()/2);
        super.createSVG();
    }
    
    public createControlPoints(): void {
        var cPointsDescription = [{'relX': 0.5, 'relY': 0, 'type': 'NORTH'},
                                    {'relX': 0.5, 'relY': 1, 'type': 'SOUTH'},
                                    {'relX': 0, 'relY': 0.5, 'type': 'WEST'},
                                    {'relX': 1, 'relY': 0.5, 'type': 'EAST'}];
        for(var cPoint of cPointsDescription) {
            this.getControlPoints().push(new ControlPoint(this.getNode(), this.getPaper(), cPoint.relX, cPoint.relY, cPoint.type));
        }
    }
    
    /*public getX(): number {
        return parseInt(this.native.attr('cx'), 10) -  parseInt(this.native.attr('rx'), 10);
    }*/
    
    public setX(val: number): void {
        this.native.attr({'cx': val + parseInt(this.native.attr('rx'), 10)});
    }
    
    /*public getY(): number {
        return parseInt(this.native.attr('cy'), 10) -  parseInt(this.native.attr('ry'), 10);
    }*/
    
    public setY(val: number): void {
        this.native.attr({'cy': val + parseInt(this.native.attr('ry'), 10)});
    }
    
    public setWidth(val: number): void {
        this.native.attr({'rx': val/2});
    }
    
    public setHeight(val: number): void {
        this.native.attr({'ry': val/2});
    }
}