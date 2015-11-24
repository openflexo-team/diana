/// <reference path="MyNode.ts" />
// <reference path="Background.ts />"
/// <reference path="Deserializable.ts" />

class ShapeNode extends MyNode implements Deserializable<ShapeNode> {
    //public enum Shape {OVAL, RECTANGLE, TRIANGLE};
    
    private x: number;
    private y: number;
    private shape: string;
    private width: number;
    private height: number;
    private background: Background;

    public deserialize(input: any): ShapeNode {
        super.deserialize(input);
        this.x = input.x;
        this.y = input.y;
        this.shape = input.shape;
        this.width = input.width;
        this.height = input.height;
        this.background.deserialize(input.background);
        return this;
    }

    public constructor() {
        super();
        this.background = new Background();
    }
    
    public getX(): number{
        return this.x;
    }
    
    public getY(): number{
        return this.y;
    }
    
    public getWidth(): number{
        return this.width;
    }
    
    public getHeight(): number{
        return this.height;
    }
    
    public setX(x: number): void{
        this.x = x;
        this.getGraphic().setX(this.x);
    }
    
    public setY(y: number): void{
        this.y = y;
        this.getGraphic().setY(this.y);
    }
    
    public setWidth(width: number): void{
        this.width = width;
    }
    
    public setHeight(height: number): void{
        this.width = height;
    }
    
    public createSVG(s: Snap.Paper = null): void {
        if(this.shape === "triangle") {
            var path: string = "M" + (this.x + this.width /2) + "," + this.y;
            path += "L" + this.x + "," + (this.y + this.height);
            path += "L" + (this.x + this.width) + "," + (this.y + this.height);
            path += "L" + (this.x + this.width /2) + "," + this.y;
            this.graphic = new TriangleGraphicalElement(this, s.path(path));
        }
        else if(this.shape === "oval") {
            this.graphic = new OvalGraphicalElement(this, s.ellipse(0, 0, this.width/2, this.height/2));
        }
        else {
            this.graphic = new ShapeGraphicalElement(this, s.rect(0, 0, this.width, this.height));
        }
        this.getGraphic().setX(this.x);
        this.getGraphic().setY(this.y);
        this.graphic.native.attr({
            fill: this.background.toAttribute() // to replace with a correct Graphic.setBackground
        });
        
        super.createSVG();
    }
    
    public getGraphic(): ShapeGraphicalElement {
        return <ShapeGraphicalElement>super.getGraphic();
    }
    
    public print(depth: number = 0): void {
        var toPrint: string = this.getId().toString();
        toPrint = toPrint + ' : ' + this.shape + ':' + this.x;
        for (var i: number = 0; i < depth; i++) {
            toPrint = '   ' + toPrint;
        }
        console.log(toPrint);

        for (var child of this.getChildren()) {
            child.print(depth + 1);
        }
    }
}