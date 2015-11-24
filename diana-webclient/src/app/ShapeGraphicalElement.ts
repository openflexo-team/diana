/// <reference path="GraphicalElement.ts" />
/// <reference path="ShapeNode.ts" />

class ShapeGraphicalElement extends GraphicalElement {
    
    public constructor(aDtn : MyNode, sElement: Snap.Element) {
        super(aDtn, sElement);
        
        //this.native.click(function(){console.log(this.type);});
        
        var wrapper: ShapeGraphicalElement = this;
        var move = function(dx: number, dy: number) {
            wrapper.getNode().setX(this.data('startX') + dx);
            wrapper.getNode().setY(this.data('startY') + dy);
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
            /*wrapper.setX(wrapper.getX() + parseInt(this.data('moveMatrix').e, 10));
            wrapper.setY(wrapper.getY() + parseInt(this.data('moveMatrix').f, 10));
            this.attr({
                transform: this.data('origTransform')
            });
            wrapper.getNode().setX(wrapper.getX());
            wrapper.getNode().setY(wrapper.getY());
            */            
        };

        this.native.drag(move, start, stop);
    }
    
    public getX(): number {
        return parseInt(this.native.attr('x'), 10);
    }
    
    public setX(val: number): void {
        this.native.attr({'x': val});
    }
    
    public getY(): number {
        return parseInt(this.native.attr('y'), 10);
    }
    
    public setY(val: number): void {
        this.native.attr({'y': val});
    }
    
    public getNode(): ShapeNode {
	   return <ShapeNode>super.getNode();
    }
}