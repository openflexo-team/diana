/**
 * Client-side equivalent of the RootNode.
 */
class RootNode extends MyNode implements Deserializable<RootNode> {
    
    public constructor() {
        super();
        console.log("let's build a root node");
    }
    
    public deserialize(input:any) : RootNode {
        super.deserialize(input);
        return this;
    }
    
    public createGraphic(parentGraphic: GraphicalElement = null) : void {
        this.graphic = new RootGraphicalElement(this);
        super.createGraphic();
    }
    
    public getGraphic(): RootGraphicalElement {
        return <RootGraphicalElement>super.getGraphic();
    }
    
    public print(depth: number = 0): void {
        var toPrint: string = this.getId().toString();
        toPrint = toPrint + ' : I am Root';
        for(var i: number = 0; i < depth; i++) {
            toPrint = '   ' + toPrint;
        }
        console.log(toPrint);

        for(var child of this.getChildren()) {
            child.print(depth + 1);
        }
    }
}