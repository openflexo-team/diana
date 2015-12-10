/// <reference path="GraphicalElement.ts" />
/// <reference path="ShapeNode.ts" />

abstract class ShapeGraphicalElement extends GraphicalElement {
    //static enum Direction {WEST, NORTH, EAST, SOUTH, NORTHWEST, NORTHEAST, SOUTWEST, SOUTEAST};
    private controlPoints: Array<ControlPoint>;
    
    public constructor(aDtn : MyNode, paper: Snap.Paper) {
        super(aDtn, paper);
        this.controlPoints = new Array<ControlPoint>();
    }
    
    public createSVG(): void {
        this.setX(this.getNode().getX());
        this.setY(this.getNode().getY());
        this.native.attr({
            fill: this.getNode().getBackground().toAttribute(), // to replace with a correct Graphic.setBackground
            stroke: '#000'
        });
        
        this.createControlPoints();
        this.setControls();
    }
    
    public setControls(): void {
        var wrapper: ShapeGraphicalElement = this;
        var move = function(dx: number, dy: number) {
            wrapper.getNode().setX(this.data('startX') + dx);
            wrapper.getNode().setY(this.data('startY') + dy);
            //wrapper.adjustControlPoints();
            /*this.data('moveMatrix', Snap.matrix(1,0, 0, 1, dx, dy));
            this.attr({
                transform:  this.data('origTransform') + this.data('moveMatrix').toTransformString()
            }); //doesn't quite work if there is already a transform. Partial solution, store original transform
            */
        };
        var start = function() {
            this.data('startX', wrapper.getNode().getX());
            this.data('startY', wrapper.getNode().getY());
            /*this.data('origTransform', this.transform().local);
            this.data('moveMatrix', Snap.matrix(1, 0, 0, 1, 0, 0));
            this.data('origTransformMtx', this.transform().localMatrix);*/
        };
        var stop = function() {
            wrapper.getNode().publishEndMove(this.data('startX'), this.data('startY'));
        };

        this.native.drag(move, start, stop);
        
        this.native.mousedown(() => wrapper.select());
    }
    
    public propertyChange(event: PropertyChangeEvent): void {
        switch (event.getPropertyName()) {
            case 'x':
                this.setX(this.getNode().getX());
                break;
            case 'y':
                this.setY(this.getNode().getY());
                break;
            case 'width':
                this.setWidth(this.getNode().getWidth());
                break;
            case 'height':
                this.setHeight(this.getNode().getHeight());
                break;
        }
    }
    
    public select(): void {
        this.getNode().getDrawing().select(this.getNode());
    }

    public selectChange(): void {
        if(this.getNode().isSelected() === true) {
            this.displayControlPoints();
        }
        else {
            this.removeControlPoints();
        }
    }

    public displayControlPoints(): void {
        for(var cPoint of this.controlPoints) {
            (<ControlPoint>cPoint).createSVG();
        }
    }
    
    public updateControlPoints():void {
        for(var cPoint of this.controlPoints) {
            (<ControlPoint>cPoint).update();
        }
    }
    
    public removeControlPoints(): void {
        for(var cpoint of this.controlPoints) {
            (<ControlPoint>cpoint).deleteSVG();
        }
    }
    
    public abstract createControlPoints(): void;

    public getControlPoints(): Array<ControlPoint> {
        return this.controlPoints;
    }
    
    public controlPointHasMoved(dX: number, dY: number, point: ControlPoint) {
        if(point.getControlType() === "NORTH" || point.getControlType() === "NORTHWEST" || point.getControlType() === "NORTHEAST") {
            var startHeight = this.getNode().getHeight();
            this.getNode().setHeight(this.getNode().getHeight() - dY);
            var dHeight = this.getNode().getHeight() - startHeight;
            this.getNode().setY(this.getNode().getY() - dHeight);
            
        }
        else if(point.getControlType() === "SOUTH" || point.getControlType() === "SOUTHWEST" || point.getControlType() === "SOUTHEAST") {
            this.getNode().setHeight(this.getNode().getHeight() + dY);
        }
        if(point.getControlType() === "WEST" || point.getControlType() === "NORTHWEST" || point.getControlType() === "SOUTHWEST") {
            var startWidth = this.getNode().getWidth();
            this.getNode().setWidth(this.getNode().getWidth() - dX);
            var dWidth = this.getNode().getWidth() - startWidth;
            this.getNode().setX(this.getNode().getX() - dWidth);
        }
        else if(point.getControlType() === "EAST" || point.getControlType() === "NORTHEAST" || point.getControlType() === "SOUTHEAST") {
            this.getNode().setWidth(this.getNode().getWidth() + dX);
        }
    }
    
    /*public getX(): number {
        return parseInt(this.native.attr('x'), 10);
    }*/
    
    public setX(val: number): void {
        this.native.attr({'x': val});
    }
    
    /*public getY(): number {
        return parseInt(this.native.attr('y'), 10);
    }*/
    
    public setY(val: number): void {
        this.native.attr({'y': val});
    }
    
    /*public getWidth(): number {
        return parseInt(this.native.attr('width'), 10);
    }*/
    
    /*
    public getHeight(): number {
        return parseInt(this.native.attr('height'), 10);
    }*/
    
    public setWidth(val: number): void {
        this.native.attr({'width': val});
    }
    
    public setHeight(val: number): void {
        this.native.attr({'height': val});
    }
    
    public getNode(): ShapeNode {
	   return <ShapeNode>super.getNode();
    }
}