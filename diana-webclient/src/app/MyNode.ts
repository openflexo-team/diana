/**
 * Client-side equivalent of the ContainerNode.<br />
 */
abstract class MyNode implements Deserializable<MyNode>, HasPropertyChangeSupport {
    
    public static deserialize (input:any) : MyNode {
        var node : MyNode;
        if (input.type === 'root') {
            node = new RootNode();
        }
        else if (input.type === 'shape') {
            node = new ShapeNode();
        }
        node.deserialize(input);
        return node;
    }

    private id: number;
    private drawing: Drawing;
    private parent: MyNode;
    private children: MyNode[];
    protected graphic: GraphicalElement;
    private propertyChangeSupport: PropertyChangeSupport;
    
    protected selected: boolean;

    public deserialize(input:any) : MyNode {
        this.id = input.id;
        if (input.children !== undefined) {
            for(var child of input.children) {
                this.addChildNode(MyNode.deserialize(child));
            }
        }
        return this;
    }

    public constructor() {
        this.id = 0;
        this.children = new Array<MyNode>();
        this.selected = false;
        this.propertyChangeSupport = new PropertyChangeSupport(this);
    }
    
    public createGraphic(parentGraphic?: GraphicalElement) : void {
        this.getGraphic().createSVG();
        for(var i = this.getChildren().length - 1; i >= 0;  i--) {
            var child = this.getChildren()[i];
            child.createGraphic(this.getGraphic());
        }
    }
    
    public addChildNode (node: MyNode): void {
        this.children.push(node);
        node.setParent(this);
        if(this.drawing !== undefined) {
            node.setDrawing(this.drawing);
        }
    }
    
    public getId():number {
        return this.id;
    }
    
    public getParent(): MyNode {
        return this.parent;
    }
    
    public getChildren():Array<MyNode> {
        return this.children;
    }
    
    public getGraphic(): GraphicalElement {
        return this.graphic;
    }
    
    public getDrawing(): Drawing {
        return this.drawing;
    }
    
    public getPropertyChangeSupport(): PropertyChangeSupport {
        return this.propertyChangeSupport;
    }
    
    public isSelected(): boolean {
        return this.selected;
    }
    
    public setParent(parent: MyNode): void {
        this.parent = parent;
    }
    
    public setDrawing(aDrawing: Drawing) {
        this.drawing = aDrawing;
        this.drawing.refNode(this);
        for (var child of this.getChildren()) {
            child.setDrawing(this.drawing);
        }
    }
    
    public setSelected(selected: boolean) {
        this.selected = selected;
        this.graphic.selectChange();
    }
    
    public print (depth: number = 0): void {
        var toPrint: string = this.getId().toString();
        for(var i: number = 0; i < depth; i++) {
            toPrint = '   ' + toPrint;
        }
        console.log(toPrint);

        for(var child of this.getChildren()) {
            child.print(depth + 1);
        }
    }
}