/// <reference path="ShapeGraphicalElement.ts" />

class OvalGraphicalElement extends ShapeGraphicalElement {
    
    public constructor(aDtn : MyNode, sElement: Snap.Element) {
        super(aDtn, sElement);
    }
    
    public getX(): number {
        return parseInt(this.native.attr('cx'), 10);
    }
    
    public setX(val: number): void {
        this.native.attr({'cx': val});
    }
    
    public getY(): number {
        return parseInt(this.native.attr('cy'), 10);
    }
    
    public setY(val: number): void {
        this.native.attr({'cy': val});
    }
}