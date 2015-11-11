/// <reference path="Deserializable.ts" />

abstract class MyNode implements Deserializable<MyNode> {
    
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
    private children: MyNode[];
    protected graphic: Snap.Element;

    deserialize(input:any) : MyNode {
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
    }
    
    public createSVG(s: Snap.Paper = null) : void {
        for(var child of this.getChildren()) {
            child.createSVG(s);
        }
    }
    
    public addChildNode (node: MyNode) {
        this.children.push(node);
    }
    
    public getId():number {
        return this.id;
    }
    
    public getChildren():Array<MyNode> {
        return this.children;
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