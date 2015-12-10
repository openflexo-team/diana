///<reference path="ShapeGraphicalElement.ts" />
class ControlPoint extends ShapeGraphicalElement {    
    private relX: number;
    private relY: number;
    private controlType: string;
    
    public constructor(node: ShapeNode, paper: Snap.Paper, relX: number, relY: number, controlType: string) {
        super(node, paper);
        this.relX = relX;
        this.relY = relY;
        this.controlType = controlType;
    }
    
    public createSVG(): void {
        this.native = this.paper.rect(this.getNode().getX() + this.getNode().getWidth() * this.relX - 2,
                                        this.getNode().getY() + this.getNode().getHeight() * this.relY - 2,
                                        4, 4);
        this.setControls();
    }
    
    public update(): void {
        this.native.attr({'x': this.getNode().getX() + this.getNode().getWidth() * this.relX - 2,
                    'y': this.getNode().getY() + this.getNode().getHeight() * this.relY - 2});
    }
    
    public createControlPoints() : void {
        return;
    }
    
    public setControls() {
        var that: ControlPoint = this;
        var move = function(dx: number, dy: number) {
            var ddx = dx - this.data('lastDX');
            var ddy = dy - this.data('lastDY');
            that.getNode().getGraphic().controlPointHasMoved(ddx, ddy, that);
            that.update();
            this.data('lastDX', dx);
            this.data('lastDY', dy);
            //that.getNode().setY(this.data('startY') + dy);
            //wrapper.adjustControlPoints();
            /*this.data('moveMatrix', Snap.matrix(1,0, 0, 1, dx, dy));
            this.attr({
                transform:  this.data('origTransform') + this.data('moveMatrix').toTransformString()
            }); //doesn't quite work if there is already a transform. Partial solution, store original transform
            */
        };
        var start = function() {
            this.data('lastDX', 0);
            this.data('lastDY', 0);
            //this.data('startY', that.getNode().getY());
            /*this.data('origTransform', this.transform().local);
            this.data('moveMatrix', Snap.matrix(1, 0, 0, 1, 0, 0));
            this.data('origTransformMtx', this.transform().localMatrix);*/
        };
        var stop = function() {
           // that.getNode().publishEndMove(this.data('startX'), this.data('startY'));
        };
        this.native.drag(move, start, stop);
    }
    
    public propertyChange(event: PropertyChangeEvent): void {
        /*switch (event.getPropertyName()) {
            case 'x':
                this.setX(<number>event.getNewValue());
                break;
            case 'y':
                this.setY(<number>event.getNewValue());
                break;
            case 'width':
                this.setWidth(<number>event.getNewValue());
                break;
            case 'height':
                this.setHeight(<number>event.getNewValue());
                break;
        }*/
        this.update();
    }
    
    public getControlType(): string {
        return this.controlType;
    }
}