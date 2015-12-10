/**
 * Client-side equivalent of the ShapeNode.
 */
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
        if(input.background !== undefined) {
            this.background.deserialize(input.background);
        }
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
    
    public getBackground(): Background {
        return this.background;
    }
    
    public setX(x: number): void{
        var oldValue = this.x;
        this.x = x;
        this.getPropertyChangeSupport().firePropertyChange('x', oldValue, this.x);
        //this.getGraphic().setX(this.x);
        if(this.selected){
            this.getGraphic().updateControlPoints();
        }
    }
    
    public setY(y: number): void{
        var oldValue = this.y;
        this.y = y;
        this.getPropertyChangeSupport().firePropertyChange('y', oldValue, this.y);
        //this.getGraphic().setY(this.y);
        if(this.selected){
            this.getGraphic().updateControlPoints();
        }
    }
    
    public setWidth(width: number): void {
        var oldValue = this.width;
        this.width = (width > 0) ? width : 0;
        this.getPropertyChangeSupport().firePropertyChange('width', oldValue, this.width);
        //this.getGraphic().setWidth(this.width);
        if(this.selected){
            this.getGraphic().updateControlPoints();
        }
    }
    
    public setHeight(height: number): void{
        var oldValue = this.height;
        this.height = (height > 0) ? height : 0;
        this.getPropertyChangeSupport().firePropertyChange('height', oldValue, this.height);
        //this.getGraphic().setHeight(this.height);
        if(this.selected) {
            this.getGraphic().updateControlPoints();
        }
    }
    
    public createGraphic(parentGraphic: GraphicalElement): void {
        if(this.shape === "triangle") {
            this.graphic = new TriangleGraphicalElement(this, parentGraphic.getPaper());
        }
        else if(this.shape === "oval") {
            this.graphic = new OvalGraphicalElement(this, parentGraphic.getPaper());
        }
        else {
            this.graphic = new RectangleGraphicalElement(this, parentGraphic.getPaper());
        }
        super.createGraphic(parentGraphic);
    }
    
    public getGraphic(): ShapeGraphicalElement {
        return <ShapeGraphicalElement>super.getGraphic();
    }
    
    public publishEndMove(startX: number, startY: number): void {
        if(startX !== this.getX()) {
            var Xchange = new UpdateChange(this.getId(), "x", this.getX());
            this.getDrawing().publishChange(Xchange);
        }
        if(startY !== this.getY()) {
            var Ychange = new UpdateChange(this.getId(), "y", this.getY());
            this.getDrawing().publishChange(Ychange);
        }
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