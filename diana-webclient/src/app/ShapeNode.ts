/// <reference path="MyNode.ts" />
// <reference path="Background.ts />"
/// <reference path="Deserializable.ts" />
/// <reference path="GraphicalProperty.ts" />

class ShapeNode extends MyNode implements Deserializable<ShapeNode> {
    private x: number;
    private y: number;
    private shape: string;
    private width: number;
    private height: number;
    private background: Background;

    deserialize(input: any): ShapeNode {
        super.deserialize(input);
        this.x = input.x;
        this.y = input.y;
        this.shape = input.shape;
        this.width = GraphicalProperty.getDeserializationFunction()(input.width);
        this.height = input.height;
        this.background.deserialize(input.background);
        return this;
    }

    public constructor() {
        super();
        this.background = new Background();
    }

    public createSVG(s: Snap.Paper = null): void {
        this.graphic = s.rect(0, 0, this.width, this.height);
        this.graphic.attr({
            x: this.x,
            y: this.y,
            fill: this.background.toAttribute()
        });
        
        var move = function(dx: number, dy: number) {
            this.data('moveMatrix', Snap.matrix(1,0, 0, 1, dx, dy));
            this.attr({
                transform:  this.data('origTransform') + this.data('moveMatrix').toTransformString()
            }); //doesn't quite work if there is already a transform. Partial solution, store original transform
        };
        var start = function() {
            this.data('origTransform', this.transform().local);
            this.data('moveMatrix', Snap.matrix(1, 0, 0, 1, 0, 0));
            this.data('origTransformMtx', this.transform().localMatrix);
        };
        var stop = function() {
            this.attr({
                x: parseInt(this.attr('x'), 10) + parseInt(this.data('moveMatrix').e, 10),
                y:  parseInt(this.attr('y'), 10) + parseInt(this.data('moveMatrix').f, 10),
                transform: this.data('origTransform')
            });
        };

        this.graphic.drag(move, start, stop);
        super.createSVG();
    }

    public print(depth: number = 0): void {
        var toPrint: string = this.getId().toString();
        toPrint = toPrint + ' : ' + this.shape + ':' + this.x;
        for (var i: number = 0; i < depth; i++) {
            toPrint = '   ' + toPrint;
        }
        console.log(toPrint);
        this.background.print();

        for (var child of this.getChildren()) {
            child.print(depth + 1);
        }
    }
}